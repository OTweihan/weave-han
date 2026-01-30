package com.han.common.oss.core;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.oss.domain.SysOssContent;
import com.han.common.oss.entity.UploadResult;
import com.han.common.oss.enums.AccessPolicyType;
import com.han.common.oss.exception.OssException;
import com.han.common.oss.mapper.SysOssContentMapper;
import com.han.common.oss.properties.OssProperties;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: DB 存储客户端
 */
public class DbOssClient extends AbstractOssClient {

    private final SysOssContentMapper ossContentMapper;

    public DbOssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
        this.ossContentMapper = SpringUtils.getBean(SysOssContentMapper.class);
    }

    @Override
    public UploadResult uploadSuffix(byte[] data, String suffix, String contentType) {
        return upload(IoUtil.toStream(data), getPath(properties.getPrefix(), suffix));
    }

    @Override
    public UploadResult uploadSuffix(InputStream inputStream, String suffix, Long length, String contentType) {
        return upload(inputStream, getPath(properties.getPrefix(), suffix));
    }

    @Override
    public UploadResult uploadSuffix(File file, String suffix) {
        return upload(IoUtil.toStream(file), getPath(properties.getPrefix(), suffix));
    }

    private UploadResult upload(InputStream inputStream, String path) {
        try {
            byte[] data = IoUtil.readBytes(inputStream);
            SysOssContent ossContent = new SysOssContent();
            ossContent.setContentId(IdUtil.getSnowflakeNextId());
            ossContent.setPath(path);
            ossContent.setContent(data);
            // 初始为0，关联关系由业务层处理
            ossContent.setOssId(0L);
            ossContentMapper.insert(ossContent);

            return UploadResult.builder()
                .url(getUrl() + StringUtils.SLASH + path)
                .filename(path)
                .build();
        } catch (Exception e) {
            throw new OssException("上传文件到DB失败: " + e.getMessage());
        }
    }

    @Override
    public void download(String key, OutputStream out, Consumer<Long> consumer) {
        try {
            LambdaQueryWrapper<SysOssContent> lqw = Wrappers.lambdaQuery();
            lqw.select(SysOssContent::getContent).eq(SysOssContent::getPath, key);
            SysOssContent ossContent = ossContentMapper.selectOne(lqw);
            if (ossContent != null && ossContent.getContent() != null) {
                IoUtil.write(out, true, ossContent.getContent());
            }
        } catch (Exception e) {
            throw new OssException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String path) {
        try {
            String key = path.replace(getUrl() + StringUtils.SLASH, "");
            LambdaQueryWrapper<SysOssContent> lqw = Wrappers.lambdaQuery();
            lqw.eq(SysOssContent::getPath, key);
            ossContentMapper.delete(lqw);
        } catch (Exception e) {
            throw new OssException("删除文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getPrivateUrl(String objectKey, Duration expiredTime) {
        return getUrl() + StringUtils.SLASH + objectKey;
    }

    @Override
    public AccessPolicyType getAccessPolicy() {
        return AccessPolicyType.getByType(properties.getAccessPolicy());
    }

    @Override
    public void close() {
        // DB连接由Spring管理，无需手动关闭
    }

    private String getUrl() {
        String domain = properties.getDomain();
        if (StringUtils.isNotEmpty(domain)) {
            return domain;
        }
        return "/resource/oss/downloadByConfig/" + configKey;
    }
}
