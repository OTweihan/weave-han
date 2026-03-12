package com.han.system.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
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
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.file.FileTypeUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.db.DbStorageClient;
import com.han.system.domain.SysFile;
import com.han.system.domain.bo.SysFileBo;
import com.han.system.domain.bo.SysFileCreateBo;
import com.han.system.domain.bo.SysFilePresignedUrlBo;
import com.han.system.domain.vo.SysFileVo;
import com.han.system.mapper.SysFileMapper;
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
     * 上传文件的前缀，是否包含日期（yyyy-MM-dd）
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
    private final ISysStorageConfigService storageConfigService;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo createFile(byte[] content, String name, String directory, String type) {
        String hash = DigestUtil.sha256Hex(content);
        String originalName = StringUtils.defaultIfBlank(name, hash);
        if (StrUtil.isEmpty(type)) {
            type = FileTypeUtils.getMineType(content, originalName);
        }
        if (StrUtil.isEmpty(FileUtil.extName(originalName))) {
            String extension = FileTypeUtils.getExtension(type);
            if (StrUtil.isNotEmpty(extension)) {
                originalName = originalName + extension;
            }
        }
        StorageClient client = storageConfigService.getStorageConfigMaster();
        Assert.notNull(client, "客户端主配置不能为空");
        SysFile oldFile = fileMapper.selectOneByConfigIdAndHash(client.getStorageConfigId(), hash);
        if (oldFile != null && StrUtil.isNotEmpty(oldFile.getUrl())) {
            return MapstructUtils.convert(oldFile, SysFileVo.class);
        }
        String path = generateUploadPath(originalName, directory);
        String storedName = FileUtil.getName(path);

        SysFile sysFile = new SysFile();
        sysFile.setConfigId(client.getStorageConfigId());
        sysFile.setFileName(storedName);
        sysFile.setOriginalName(originalName);
        sysFile.setFilePath(path);
        sysFile.setMimeType(type);
        sysFile.setFileSize((long) content.length);
        sysFile.setFileSuffix(FileUtil.extName(storedName));
        sysFile.setHash(hash);

        try {
            if (client instanceof DbStorageClient) {
                fileMapper.insert(sysFile);
                String url = client.upload(content, path, type, sysFile.getId());
                sysFile.setUrl(url);
                fileMapper.updateById(sysFile);
            } else {
                String url = client.upload(content, path, type);
                sysFile.setUrl(url);
                fileMapper.insert(sysFile);
            }
        } catch (Exception e) {
            log.error("文件上传失败，路径: {}, 原始文件名: {}", path, originalName, e);
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
            // 通过 AOP 代理调用，避免同类内直接调用导致 @Transactional 失效
            return SpringUtils.getAopProxy(this).createFile(content, file.getOriginalFilename(), null, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException("读取文件失败");
        }
    }

    String generateUploadPath(String name, String directory) {
        String fileName = IdUtil.fastSimpleUUID();
        String ext = FileUtil.extName(name);
        if (PATH_SUFFIX_TIMESTAMP_ENABLE) {
            fileName = fileName + StrUtil.C_UNDERLINE + System.currentTimeMillis();
        }
        if (StrUtil.isNotEmpty(ext)) {
            fileName = fileName + StrUtil.DOT + ext;
        }

        String prefix = null;
        if (PATH_PREFIX_DATE_ENABLE) {
            prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATE_PATTERN);
        }
        if (StrUtil.isNotEmpty(prefix)) {
            fileName = prefix + StrUtil.SLASH + fileName;
        }
        if (StrUtil.isNotEmpty(directory)) {
            fileName = directory + StrUtil.SLASH + fileName;
        }
        return fileName;
    }

    @Override
    @SneakyThrows
    public SysFilePresignedUrlBo presignPutUrl(String name, String directory) {
        // 1. 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory);

        // 2. 获取文件预签名地址
        StorageClient storageClient = storageConfigService.getStorageConfigMaster();
        Assert.notNull(storageClient, "客户端主配置不能为空");
        String uploadUrl = storageClient.presignPutUrl(path);
        String visitUrl = storageClient.presignGetUrl(path, null);
        return new SysFilePresignedUrlBo().setConfigId(storageClient.getStorageConfigId())
            .setPath(path).setUploadUrl(uploadUrl).setUrl(visitUrl);
    }

    @Override
    public Long createFile(SysFileCreateBo createVo) {
        Assert.notNull(createVo, "文件创建参数不能为空");
        createVo.setUrl(StrUtil.subBefore(createVo.getUrl(), "?", false));
        SysFile sysFile = MapstructUtils.convert(createVo, SysFile.class);
        if (sysFile == null) {
            throw new ServiceException("文件创建失败");
        }
        // 优先使用转换后的 filePath；若为空则回退到请求参数中的 filePath
        String filePath = StringUtils.defaultIfBlank(sysFile.getFilePath(), createVo.getFilePath());
        sysFile.setFilePath(filePath);
        String storedName = StringUtils.EMPTY;
        if (StrUtil.isNotEmpty(filePath)) {
            storedName = FileUtil.getName(filePath);
        }
        if (StrUtil.isEmpty(storedName)) {
            storedName = createVo.getOriginalName();
        }
        String originalName = StringUtils.defaultIfBlank(createVo.getOriginalName(), storedName);
        String fileSuffix = FileUtil.extName(storedName);
        if (StrUtil.isEmpty(fileSuffix)) {
            fileSuffix = FileUtil.extName(originalName);
        }
        sysFile.setOriginalName(originalName);
        sysFile.setFileName(storedName);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setFileSize(createVo.getFileSize());
        sysFile.setMimeType(createVo.getMimeType());
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
        }

        // 删除记录
        fileMapper.deleteByIds(ids);
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        StorageClient client = storageConfigService.getStorageConfigClient(configId);
        Assert.notNull(client, "客户端 {} 不能为空", configId);
        return client.getContent(path);
    }

    @Override
    public TableDataInfo<SysFileVo> selectPageFileList(SysFileBo fileBo, PageQuery pageQuery) {
        Page<SysFileVo> page = fileMapper.selectVoPage(pageQuery.build(), this.buildQueryWrapper(fileBo));
        return TableDataInfo.build(page);
    }

    private Wrapper<SysFile> buildQueryWrapper(SysFileBo fileBo) {
        Map<String, Object> params = fileBo.getParams();
        Object beginTime = params.get("beginCreateTime") != null ? params.get("beginCreateTime") : params.get("beginTime");
        Object endTime = params.get("endCreateTime") != null ? params.get("endCreateTime") : params.get("endTime");
        LambdaQueryWrapper<SysFile> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(fileBo.getConfigId() != null, SysFile::getConfigId, fileBo.getConfigId())
            .like(StringUtils.isNotBlank(fileBo.getFileName()), SysFile::getFileName, fileBo.getFileName())
            .like(StringUtils.isNotBlank(fileBo.getOriginalName()), SysFile::getOriginalName, fileBo.getOriginalName())
            .eq(StringUtils.isNotBlank(fileBo.getFileSuffix()), SysFile::getFileSuffix, fileBo.getFileSuffix())
            .like(StringUtils.isNotBlank(fileBo.getMimeType()), SysFile::getMimeType, fileBo.getMimeType())
            .like(StringUtils.isNotBlank(fileBo.getUrl()), SysFile::getUrl, fileBo.getUrl())
            .between(beginTime != null && endTime != null, SysFile::getCreateTime, beginTime, endTime)
            .orderByDesc(SysFile::getCreateTime);
        return wrapper;
    }

    @Override
    public String selectUrlByIds(String fileIds) {
        List<Long> idList = parseFileIdList(fileIds);
        if (idList.isEmpty()) {
            return StringUtils.EMPTY;
        }
        List<SysFile> list = fileMapper.selectByIds(idList);
        return list.stream()
            .map(SysFile::getUrl)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(","));
    }

    @Override
    public List<FileDTO> selectByIds(String fileIds) {
        List<Long> idList = parseFileIdList(fileIds);
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysFile> list = fileMapper.selectByIds(idList);
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

    /**
     * 解析逗号分隔的文件编号串。
     * 说明：统一解析逻辑，避免各方法重复处理空值、空白字符与格式校验。
     */
    private List<Long> parseFileIdList(String fileIds) {
        if (StringUtils.isBlank(fileIds)) {
            return new ArrayList<>();
        }
        String[] fileIdArray = fileIds.split(",");
        List<Long> idList = new ArrayList<>(fileIdArray.length);
        for (String fileId : fileIdArray) {
            if (StringUtils.isBlank(fileId)) {
                continue;
            }
            String trimmedId = fileId.trim();
            try {
                idList.add(Long.valueOf(trimmedId));
            } catch (NumberFormatException ex) {
                throw new ServiceException("文件编号格式错误: " + trimmedId);
            }
        }
        return idList;
    }
}
