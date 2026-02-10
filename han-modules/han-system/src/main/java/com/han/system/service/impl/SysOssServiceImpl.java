package com.han.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.domain.dto.OssDTO;
import com.han.common.core.service.OssService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.file.FileTypeUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.oss.core.OssClient;
import com.han.system.domain.SysOss;
import com.han.system.domain.SysUser;
import com.han.system.domain.bo.SysOssBo;
import com.han.system.domain.bo.SysOssCreateBo;
import com.han.system.domain.bo.SysOssPresignedUrlBo;
import com.han.system.domain.bo.SysUserBo;
import com.han.system.domain.vo.SysOssVo;
import com.han.system.domain.vo.SysUserVo;
import com.han.system.mapper.SysOssConfigMapper;
import com.han.system.mapper.SysOssMapper;
import com.han.system.service.ISysOssConfigService;
import com.han.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import cn.hutool.core.io.IoUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 服务层实现
 */
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl implements ISysOssService, OssService {

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

    private final SysOssMapper ossMapper;
    private final SysOssConfigMapper ossConfigMapper;
    private final ISysOssConfigService ossConfigService;

    @Override
    @SneakyThrows
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
        OssClient client = ossConfigService.getMasterOssClient();
        Assert.notNull(client, "客户端主配置不能为空");
        String url = client.upload(content, path, type);
        SysOss oss = new SysOss().setConfigId(client.getOssConfigId())
            .setFileName(name).setUrl(url).setType(type).setSize((long) content.length);
        ossMapper.insert(oss);
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
        OssClient ossClient = ossConfigService.getMasterOssClient();
        String uploadUrl = ossClient.presignPutUrl(path);
        String visitUrl = ossClient.presignGetUrl(path, null);
        return new SysOssPresignedUrlBo().setConfigId(ossClient.getOssConfigId())
            .setPath(path).setUploadUrl(uploadUrl).setUrl(visitUrl);
    }

    @Override
    public Long createFile(SysOssCreateBo ossCreateBo) {
        ossCreateBo.setUrl(StrUtil.subBefore(ossCreateBo.getUrl(), "?", false));
        SysOss sysOss = MapstructUtils.convert(ossCreateBo, SysOss.class);
        Assert.notNull(sysOss, "文件创建失败");
        ossMapper.insert(sysOss);
        return sysOss.getOssId();
    }

    @Override
    public SysOss getOssFile(Long id) {
        return validateOssExists(id);
    }

    private SysOss validateOssExists(Long id) {
        SysOss sysOss = ossMapper.selectById(id);
        if (sysOss == null) {
            throw new ServiceException("文件不存在");
        }
        return sysOss;
    }

    @Override
    @SneakyThrows
    public void deleteFile(List<Long> ids) {
        // 删除文件
        List<SysOss> files = ossMapper.selectByIds(ids);
        for (SysOss sysOss : files) {
            // 获取客户端
            OssClient client = ossConfigService.getOssClient(sysOss.getConfigId());
            Assert.notNull(client, "客户端({}) 不能为空", sysOss.getFilePath());
            // 删除文件
            client.delete(sysOss.getFilePath());
        }

        // 删除记录
        ossMapper.deleteByIds(ids);
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        OssClient client = ossConfigService.getOssClient(configId);
        Assert.notNull(client, "客户端 {} 不能为空", configId);
        return client.getContent(path);
    }

    @Override
    public TableDataInfo<SysOssVo> selectPageOssList(SysOssBo ossBo, PageQuery pageQuery) {
        Page<SysOssVo> page = ossMapper.selectPageOssList(pageQuery.build(), this.buildQueryWrapper(ossBo));
        return TableDataInfo.build(page);
    }

    private Wrapper<SysOss> buildQueryWrapper(SysOssBo ossBo) {
        Map<String, Object> params = ossBo.getParams();
        LambdaQueryWrapper<SysOss> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(ossBo.getFileName()), SysOss::getFileName, ossBo.getFileName())
            .eq(StringUtils.isNotBlank(ossBo.getFileSuffix()), SysOss::getType, ossBo.getFileSuffix())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                SysOss::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(SysOss::getCreateTime);
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
        List<SysOss> list = ossMapper.selectBatchIds(idList);
        return list.stream().map(SysOss::getUrl).collect(Collectors.joining(","));
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
        List<SysOss> list = ossMapper.selectBatchIds(idList);
        return list.stream().map(sysOss -> {
            OssDTO dto = new OssDTO();
            dto.setOssId(sysOss.getOssId());
            dto.setUrl(sysOss.getUrl());
            dto.setFileName(sysOss.getFileName());
            return dto;
        }).collect(Collectors.toList());
    }
}
