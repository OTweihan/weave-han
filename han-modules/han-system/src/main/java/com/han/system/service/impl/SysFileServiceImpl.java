package com.han.system.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.core.domain.dto.FileDTO;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.service.FileService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.file.FileTypeUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.db.DbStorageClient;
import com.han.common.storage.enums.StorageTypeEnum;
import com.han.system.domain.SysFile;
import com.han.system.domain.SysFileContent;
import com.han.system.domain.SysStorageConfig;
import com.han.system.domain.bo.SysFileBo;
import com.han.system.domain.bo.SysFileCreateBo;
import com.han.system.domain.bo.SysFilePresignedUrlBo;
import com.han.system.domain.vo.SysFileVo;
import com.han.system.mapper.SysFileContentMapper;
import com.han.system.mapper.SysFileMapper;
import com.han.system.mapper.SysStorageConfigMapper;
import com.han.system.service.ISysFileService;
import com.han.system.service.ISysStorageConfigService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件上传 服务层实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysFileServiceImpl implements ISysFileService, FileService {

    /**
     * 上传文件的前缀，是否包含日期（yyyyMMdd）
     * 目的：按照日期，进行分目录
     */
    static boolean PATH_PREFIX_DATE_ENABLE = true;

    /**
     * 上传文件的后缀，是否包含时间戳
     * 目的：保证文件的唯一性，避免覆盖
     * 定制：可按需调整成 UUID、或者其他方式
     */
    static boolean PATH_SUFFIX_TIMESTAMP_ENABLE = true;

    private final SysFileMapper fileMapper;
    private final SysFileContentMapper fileContentMapper;
    private final SysStorageConfigMapper sysStorageConfigMapper;
    private final ISysStorageConfigService storageConfigService;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo createFile(byte[] content, String name, String directory, String type) {
        if (StrUtil.isEmpty(type)) {
            type = FileTypeUtils.getMineType(content, name);
        }
        if (StrUtil.isEmpty(name)) {
            name = DigestUtil.sha256Hex(content);
        }
        if (StrUtil.isEmpty(FileUtil.extName(name))) {
            String extension = FileTypeUtils.getExtension(type);
            if (StrUtil.isNotEmpty(extension)) {
                name = name + extension;
            }
        }
        String path = generateUploadPath(name, directory);
        StorageClient client = storageConfigService.getStorageConfigMaster();
        Assert.notNull(client, "客户端主配置不能为空");
        String storageType = resolveStorageType(client.getStorageConfigId(), client);

        SysFile sysFile = new SysFile();
        sysFile.setConfigId(client.getStorageConfigId());
        sysFile.setStorageType(storageType);
        sysFile.setFileName(name);
        sysFile.setOriginalName(name);
        sysFile.setFilePath(path);
        sysFile.setMimeType(type);
        sysFile.setFileSize((long) content.length);
        sysFile.setFileSuffix(FileUtil.extName(name));
        sysFile.setHash(DigestUtil.sha256Hex(content));

        try {
            if (client instanceof DbStorageClient) {
                fileMapper.insert(sysFile);
                // 数据库存储：先保存文件信息获取ID，再保存内容
                SysFileContent fileContent = new SysFileContent();
                fileContent.setFileId(sysFile.getId());
                fileContent.setContent(content);
                fileContentMapper.insert(fileContent);

                String url = client.upload(content, path, type, sysFile.getId());
                sysFile.setUrl(url);
                fileMapper.updateById(sysFile);
            } else {
                String url = client.upload(content, path, type);
                sysFile.setUrl(url);
                fileMapper.insert(sysFile);
            }
        } catch (Exception e) {
            try {
                if (StrUtil.isNotEmpty(sysFile.getFilePath())) {
                    client.delete(sysFile.getFilePath());
                }
            } catch (Exception ex) {
                log.error("上传失败清理文件失败，文件路径: {}", path, ex);
            }
            throw new ServiceException("文件上传失败");
        }
        return MapstructUtils.convert(sysFile, SysFileVo.class);
    }

    @Override
    public SysFileVo upload(MultipartFile file) {
        try {
            byte[] content = file.getBytes();
            return createFile(content, file.getOriginalFilename(), null, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException("读取文件失败");
        }
    }

    String generateUploadPath(String name, String directory) {
        String prefix = null;
        if (PATH_PREFIX_DATE_ENABLE) {
            prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATE_PATTERN);
        }
        String suffix = null;
        if (PATH_SUFFIX_TIMESTAMP_ENABLE) {
            suffix = String.valueOf(System.currentTimeMillis());
        }
        if (StrUtil.isNotEmpty(suffix)) {
            String ext = FileUtil.extName(name);
            if (StrUtil.isNotEmpty(ext)) {
                name = FileUtil.mainName(name) + StrUtil.C_UNDERLINE + suffix + StrUtil.DOT + ext;
            } else {
                name = name + StrUtil.C_UNDERLINE + suffix;
            }
        }
        if (StrUtil.isNotEmpty(prefix)) {
            name = prefix + StrUtil.SLASH + name;
        }
        if (StrUtil.isNotEmpty(directory)) {
            name = directory + StrUtil.SLASH + name;
        }
        return name;
    }

    @Override
    @SneakyThrows
    public SysFilePresignedUrlBo presignPutUrl(String name, String directory) {
        // 1. 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory);

        // 2. 获取文件预签名地址
        StorageClient storageClient = storageConfigService.getStorageConfigMaster();
        String uploadUrl = storageClient.presignPutUrl(path);
        String visitUrl = storageClient.presignGetUrl(path, null);
        return new SysFilePresignedUrlBo().setConfigId(storageClient.getStorageConfigId())
            .setPath(path).setUploadUrl(uploadUrl).setUrl(visitUrl);
    }

    @Override
    public Long createFile(SysFileCreateBo createVo) {
        createVo.setUrl(StrUtil.subBefore(createVo.getUrl(), "?", false));
        SysFile sysFile = MapstructUtils.convert(createVo, SysFile.class);
        Assert.notNull(sysFile, "文件创建失败");
        if (StrUtil.isEmpty(sysFile.getFilePath())) {
            sysFile.setFilePath(createVo.getFilePath());
        }
        sysFile.setOriginalName(createVo.getOriginalName());
        sysFile.setFileName(createVo.getOriginalName());
        sysFile.setFileSuffix(FileUtil.extName(createVo.getOriginalName()));
        sysFile.setFileSize(createVo.getFileSize());
        sysFile.setMimeType(createVo.getMimeType());

        if (StrUtil.isEmpty(sysFile.getStorageType())) {
            SysStorageConfig config = sysStorageConfigMapper.selectById(createVo.getConfigId());
            if (config != null) {
                StorageTypeEnum storageTypeEnum = StorageTypeEnum.getByStorageType(config.getStorageType());
                if (storageTypeEnum != null) {
                    sysFile.setStorageType(storageTypeEnum.name());
                }
            }
        }
        fileMapper.insert(sysFile);
        return sysFile.getId();
    }

    @Override
    public SysFile getFile(Long id) {
        return validateFileExists(id);
    }

    private SysFile validateFileExists(Long id) {
        SysFile sysFile = fileMapper.selectById(id);
        if (sysFile == null) {
            throw new ServiceException("文件不存在");
        }
        return sysFile;
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(List<Long> ids) {
        // 删除文件
        List<SysFile> files = fileMapper.selectByIds(ids);
        for (SysFile sysFile : files) {
            // 获取客户端
            StorageClient client = storageConfigService.getStorageConfigClient(sysFile.getConfigId());
            Assert.notNull(client, "客户端({}) 不能为空", sysFile.getFilePath());
            // 删除文件
            client.delete(sysFile.getFilePath());
            // 如果是DB存储，还需要删除文件内容
            if (client instanceof DbStorageClient) {
                fileContentMapper.delete(new LambdaQueryWrapper<SysFileContent>()
                    .eq(SysFileContent::getFileId, sysFile.getId()));
            }
        }

        // 删除记录
        fileMapper.deleteByIds(ids);
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        StorageClient client = storageConfigService.getStorageConfigClient(configId);
        Assert.notNull(client, "客户端 {} 不能为空", configId);
        // 如果是DB存储，尝试从数据库获取内容
        if (client instanceof DbStorageClient) {
             // 这里逻辑稍微复杂，因为DbStorageClient.getContent通常需要文件ID或者路径
             // 假设DbStorageClient内部实现了通过路径查询，或者我们需要先查文件ID
             // 这里的path参数如果是文件路径，我们可以先查sys_file表
             SysFile sysFile = fileMapper.selectOne(new LambdaQueryWrapper<SysFile>()
                 .eq(SysFile::getConfigId, configId)
                 .eq(SysFile::getFilePath, path));
             if (sysFile != null) {
                 SysFileContent content = fileContentMapper.selectOne(new LambdaQueryWrapper<SysFileContent>()
                     .eq(SysFileContent::getFileId, sysFile.getId()));
                 if (content != null) {
                     return content.getContent();
                 }
             }
        }
        return client.getContent(path);
    }

    @Override
    public TableDataInfo<SysFileVo> selectPageFileList(SysFileBo fileBo, PageQuery pageQuery) {
        Page<SysFileVo> page = fileMapper.selectVoPage(pageQuery.build(), this.buildQueryWrapper(fileBo));
        return TableDataInfo.build(page);
    }

    private Wrapper<SysFile> buildQueryWrapper(SysFileBo fileBo) {
        Map<String, Object> params = fileBo.getParams();
        LambdaQueryWrapper<SysFile> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(fileBo.getFileName()), SysFile::getFileName, fileBo.getFileName())
            .eq(StringUtils.isNotBlank(fileBo.getFileSuffix()), SysFile::getFileSuffix, fileBo.getFileSuffix())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                SysFile::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(SysFile::getCreateTime);
        return wrapper;
    }

    @Override
    public String selectUrlByIds(String ossIds) {
        if (StringUtils.isBlank(ossIds)) {
            return StringUtils.EMPTY;
        }
        String[] ossIdArray = ossIds.split(",");
        List<Long> idList = new ArrayList<>();
        for (String ossId : ossIdArray) {
            if (StringUtils.isNotBlank(ossId)) {
                idList.add(Long.valueOf(ossId.trim()));
            }
        }
        if (idList.isEmpty()) {
            return StringUtils.EMPTY;
        }
        List<SysFile> list = fileMapper.selectBatchIds(idList);
        return list.stream().map(SysFile::getUrl).collect(Collectors.joining(","));
    }

    @Override
    public List<FileDTO> selectByIds(String ossIds) {
        if (StringUtils.isBlank(ossIds)) {
            return new ArrayList<>();
        }
        String[] ossIdArray = ossIds.split(",");
        List<Long> idList = new ArrayList<>();
        for (String ossId : ossIdArray) {
            if (StringUtils.isNotBlank(ossId)) {
                idList.add(Long.valueOf(ossId.trim()));
            }
        }
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysFile> list = fileMapper.selectBatchIds(idList);
        return list.stream().map(sysFile -> {
            FileDTO dto = new FileDTO();
            dto.setId(sysFile.getId());
            dto.setUrl(sysFile.getUrl());
            dto.setFileName(sysFile.getFileName());
            dto.setFileSuffix(sysFile.getFileSuffix());
            dto.setOriginalName(sysFile.getOriginalName());
            return dto;
        }).collect(Collectors.toList());
    }

    private String resolveStorageType(Long configId, StorageClient client) {
        SysStorageConfig config = sysStorageConfigMapper.selectById(configId);
        if (config != null) {
            StorageTypeEnum storageTypeEnum = StorageTypeEnum.getByStorageType(config.getStorageType());
            if (storageTypeEnum != null) {
                return storageTypeEnum.name();
            }
        }
        return client.getClass().getSimpleName();
    }
}
