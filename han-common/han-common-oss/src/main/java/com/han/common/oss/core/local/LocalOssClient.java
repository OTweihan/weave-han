package com.han.common.oss.core.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.han.common.core.utils.StringUtils;
import com.han.common.oss.core.AbstractOssClient;
import com.han.common.oss.entity.UploadResult;
import com.han.common.oss.enums.AccessPolicyType;
import com.han.common.oss.exception.OssException;
import com.han.common.oss.properties.OssProperties;

import java.io.*;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: 本地存储客户端
 */
public class LocalOssClient extends AbstractOssClient {

    public LocalOssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
    }

    @Override
    public UploadResult uploadSuffix(byte[] data, String suffix, String contentType) {
        return upload(new ByteArrayInputStream(data), getPath(properties.getPrefix(), suffix), contentType);
    }

    @Override
    public UploadResult uploadSuffix(InputStream inputStream, String suffix, Long length, String contentType) {
        return upload(inputStream, getPath(properties.getPrefix(), suffix), contentType);
    }

    @Override
    public UploadResult uploadSuffix(File file, String suffix) {
        try {
            return upload(new FileInputStream(file), getPath(properties.getPrefix(), suffix), null);
        } catch (FileNotFoundException e) {
            throw new OssException("文件不存在: " + e.getMessage());
        }
    }

    private UploadResult upload(InputStream inputStream, String key, String contentType) {
        String filePath = getFilePath(key);
        try {
            FileUtil.writeFromStream(inputStream, filePath);
            return UploadResult.builder()
                .url(getUrl() + StringUtils.SLASH + key)
                .filename(key)
                .build();
        } catch (Exception e) {
            throw new OssException("上传文件失败: " + e.getMessage());
        }
    }

    @Override
    public void download(String key, OutputStream out, Consumer<Long> consumer) {
        String filePath = getFilePath(key);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new OssException("文件不存在");
        }
        if (consumer != null) {
            consumer.accept(file.length());
        }
        try (InputStream in = new FileInputStream(file)) {
            IoUtil.copy(in, out);
        } catch (IOException e) {
            throw new OssException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String path) {
        String key = path.replace(getUrl() + StringUtils.SLASH, "");
        FileUtil.del(getFilePath(key));
    }

    @Override
    public String getPrivateUrl(String objectKey, Duration expiredTime) {
        // 本地存储不支持预签名URL，直接返回普通URL
        return getUrl() + StringUtils.SLASH + objectKey;
    }

    @Override
    public AccessPolicyType getAccessPolicy() {
        return AccessPolicyType.getByType(properties.getAccessPolicy());
    }

    @Override
    public void close() {
        // 本地存储不需要关闭
    }

    private String getFilePath(String key) {
        return properties.getEndpoint() + File.separator + key;
    }

    private String getUrl() {
        String domain = properties.getDomain();
        if (StringUtils.isNotEmpty(domain)) {
            return domain;
        }
        // 如果没有配置域名，默认指向本系统的代理下载接口
        // 注意：这里的端口和上下文路径需要根据实际情况调整，或者由前端拼接
        return "/resource/oss/downloadByConfig/" + configKey;
    }
}
