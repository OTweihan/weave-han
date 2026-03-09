package com.han.common.oss.enums;

import com.han.common.oss.core.OssClient;
import com.han.common.oss.core.OssClientConfig;
import com.han.common.oss.core.db.DbOssClient;
import com.han.common.oss.core.db.DbOssClientConfig;
import com.han.common.oss.core.ftp.FtpOssClient;
import com.han.common.oss.core.ftp.FtpOssClientConfig;
import com.han.common.oss.core.local.LocalOssClient;
import com.han.common.oss.core.local.LocalOssClientConfig;
import com.han.common.oss.core.s3.S3OssClient;
import com.han.common.oss.core.s3.S3OssClientConfig;
import com.han.common.oss.core.sftp.SftpOssClient;
import com.han.common.oss.core.sftp.SftpOssClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author WeiHan
 * @CreateTime: 2026-02-10
 * @Description: 文件存储器枚举
 */
@AllArgsConstructor
@Getter
public enum OssStorageTypeEnum {

    /**
     * 数据库
     */
    DB(1, "数据库", DbOssClientConfig.class, DbOssClient.class),

    /**
     * 本地存储
     */
    LOCAL(10, "本地存储", LocalOssClientConfig.class, LocalOssClient.class),

    /**
     * FTP
     */
    FTP(11, "FTP", FtpOssClientConfig.class, FtpOssClient.class),

    /**
     * SFTP
     */
    SFTP(12, "SFTP", SftpOssClientConfig.class, SftpOssClient.class),

    /**
     * S3
     */
    S3(20, "S3", S3OssClientConfig.class, S3OssClient.class);

    /**
     * 存储类型
     */
    private final Integer storageType;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 配置类
     */
    private final Class<? extends OssClientConfig> configClass;

    /**
     * 客户端类
     */
    private final Class<? extends OssClient> clientClass;

    /**
     * 根据存储类型获取枚举
     *
     * @param storageType 存储类型
     * @return 枚举
     */
    public static OssStorageTypeEnum getByStorageType(Integer storageType) {
        if (storageType == null) {
            return null;
        }
        for (OssStorageTypeEnum value : values()) {
            if (value.getStorageType().equals(storageType)) {
                return value;
            }
        }
        return null;
    }
}
