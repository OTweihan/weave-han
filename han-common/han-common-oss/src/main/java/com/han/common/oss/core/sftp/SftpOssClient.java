package com.han.common.oss.core.sftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ssh.Sftp;
import com.han.common.core.utils.StringUtils;
import com.han.common.oss.core.AbstractOssClient;
import com.han.common.oss.entity.UploadResult;
import com.han.common.oss.enums.AccessPolicyType;
import com.han.common.oss.exception.OssException;
import com.han.common.oss.properties.OssProperties;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: SFTP 存储客户端
 */
public class SftpOssClient extends AbstractOssClient {

    private final Sftp sftp;

    public SftpOssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
        try {
            FtpConfig ftpConfig = new FtpConfig();
            ftpConfig.setHost(properties.getEndpoint());
            ftpConfig.setPort(StringUtils.isNotBlank(properties.getRegion()) ? Integer.parseInt(properties.getRegion()) : 22);
            ftpConfig.setUser(properties.getAccessKey());
            ftpConfig.setPassword(properties.getSecretKey());
            ftpConfig.setCharset(CharsetUtil.CHARSET_UTF_8);
            this.sftp = new Sftp(ftpConfig);
        } catch (Exception e) {
            throw new OssException("SFTP客户端初始化失败: " + e.getMessage());
        }
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

    private UploadResult upload(InputStream inputStream, String key) {
        try {
            String dir = properties.getBucketName();
            String fileName = FileUtil.getName(key);
            String path = StrUtil.removeSuffix(key, fileName);
            String finalDir = dir + StringUtils.SLASH + path;

            sftp.reconnectIfTimeout();
            sftp.mkDirs(finalDir);
            sftp.upload(finalDir, fileName, inputStream);

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
        try {
            String dir = properties.getBucketName();
            String fileName = FileUtil.getName(key);
            String path = StrUtil.removeSuffix(key, fileName);
            String finalDir = dir + StringUtils.SLASH + path;

            sftp.reconnectIfTimeout();
            sftp.download(finalDir + StringUtils.SLASH + fileName, out);
        } catch (Exception e) {
            throw new OssException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public synchronized void delete(String path) {
        try {
            String key = path.replace(getUrl() + StringUtils.SLASH, "");
            String dir = properties.getBucketName();
            sftp.reconnectIfTimeout();
            sftp.delFile(dir + StringUtils.SLASH + key);
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
        IoUtil.close(sftp);
    }

    private String getUrl() {
        String domain = properties.getDomain();
        if (StringUtils.isNotEmpty(domain)) {
            return domain;
        }
        return "/resource/oss/downloadByConfig/" + configKey;
    }
}
