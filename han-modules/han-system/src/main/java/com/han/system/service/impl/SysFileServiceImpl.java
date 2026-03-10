package com.han.system.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.domain.dto.OssDTO;
import com.han.common.core.service.OssService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.file.FileTypeUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.db.DbStorageClient;
import com.han.common.storage.enums.StorageTypeEnum;
import com.han.system.domain.SysFile;
import com.han.system.domain.SysStorageConfig;
import com.han.system.domain.bo.SysOssBo;
import com.han.system.domain.bo.SysOssCreateBo;
import com.han.system.domain.bo.SysOssPresignedUrlBo;
import com.han.system.domain.vo.SysOssVo;
import com.han.system.mapper.SysStorageConfigMapper;
import com.han.system.mapper.SysFileMapper;
import com.han.system.service.ISysStorageConfigService;
import com.han.system.service.ISysFileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 服务层实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysFileServiceImpl implements ISysFileService, OssService {

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
    private final SysStorageConfigMapper sysStorageConfigMapper;
    private final ISysStorageConfigService storageConfigService;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public SysOssVo createFile(byte[] content, String name, String directory, String type) {
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
        String storageType = resolveStorageType(client.getOssConfigId(), client);
        SysFile oss = new SysFile().setConfigId(client.getOssConfigId())
            .setStorageType(storageType)
            .setFileName(name)
            .setFilePath(path)
            .setMimeType(type)
            .setFileSize((long) content.length);
        try {
            if (client instanceof DbStorageClient) {
                fileMapper.insert(oss);
                String url = client.upload(content, path, type, oss.getOssId());
                oss.setUrl(url);
                fileMapper.updateById(oss);
            } else {
                String url = client.upload(content, path, type);
                oss.setUrl(url);
                fileMapper.insert(oss);
            }
        } catch (Exception e) {
            try {
                if (StrUtil.isNotEmpty(oss.getFilePath())) {
                    client.delete(oss.getFilePath());
                }
            } catch (Exception ex) {
                log.error("上传失败清理文件失败，文件路径: {}", path, ex);
            }
            throw new ServiceException("文件上传失败");
        }
        return MapstructUtils.convert(oss, SysOssVo.class);
    }

    @Override
    public SysOssVo upload(MultipartFile file) {
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
    public SysOssPresignedUrlBo presignPutUrl(String name, String directory) {
        // 1. 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory);

        // 2. 获取文件预签名地址
        StorageClient storageClient = storageConfigService.getStorageConfigMaster();
        String uploadUrl = storageClient.presignPutUrl(path);
        String visitUrl = storageClient.presignGetUrl(path, null);
        return new SysOssPresignedUrlBo().setConfigId(storageClient.getOssConfigId())
            .setPath(path).setUploadUrl(uploadUrl).setUrl(visitUrl);
    }

    @Override
    public Long createFile(SysOssCreateBo ossCreateBo) {
        ossCreateBo.setUrl(StrUtil.subBefore(ossCreateBo.getUrl(), "?", false));
        SysFile sysFile = MapstructUtils.convert(ossCreateBo, SysFile.class);
        Assert.notNull(sysFile, "文件创建失败");
        if (StrUtil.isEmpty(sysFile.getFilePath())) {
            sysFile.setFilePath(ossCreateBo.getPath());
        }
        if (StrUtil.isEmpty(sysFile.getStorageType())) {
            SysStorageConfig config = sysStorageConfigMapper.selectById(ossCreateBo.getConfigId());
            if (config != null) {
                StorageTypeEnum storageTypeEnum = StorageTypeEnum.getByStorageType(config.getStorageType());
                if (storageTypeEnum != null) {
                    sysFile.setStorageType(storageTypeEnum.name());
                }
            }
        }
        fileMapper.insert(sysFile);
        return sysFile.getOssId();
    }

    @Override
    public SysFile getOssFile(Long id) {
        return validateOssExists(id);
    }

    private SysFile validateOssExists(Long id) {
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
    public TableDataInfo<SysOssVo> selectPageOssList(SysOssBo ossBo, PageQuery pageQuery) {
        Page<SysOssVo> page = fileMapper.selectPageOssList(pageQuery.build(), this.buildQueryWrapper(ossBo));
        return TableDataInfo.build(page);
    }

    private Wrapper<SysFile> buildQueryWrapper(SysOssBo ossBo) {
        Map<String, Object> params = ossBo.getParams();
        LambdaQueryWrapper<SysFile> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(ossBo.getFileName()), SysFile::getFileName, ossBo.getFileName())
            .eq(StringUtils.isNotBlank(ossBo.getFileSuffix()), SysFile::getMimeType, ossBo.getFileSuffix())
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
    public List<OssDTO> selectByIds(String ossIds) {
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
        return list.stream().map(sysOss -> {
            OssDTO dto = new OssDTO();
            dto.setOssId(sysOss.getOssId());
            dto.setUrl(sysOss.getUrl());
            dto.setFileName(sysOss.getFileName());
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
