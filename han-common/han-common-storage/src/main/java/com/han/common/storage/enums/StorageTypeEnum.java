package com.han.common.storage.enums;

import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.core.db.DbStorageClient;
import com.han.common.storage.core.db.DbStorageClientConfig;
import com.han.common.storage.core.ftp.FtpStorageClient;
import com.han.common.storage.core.ftp.FtpStorageClientConfig;
import com.han.common.storage.core.local.LocalStorageClient;
import com.han.common.storage.core.local.LocalStorageClientConfig;
import com.han.common.storage.core.s3.S3StorageClient;
import com.han.common.storage.core.s3.S3StorageClientConfig;
import com.han.common.storage.core.sftp.SftpStorageClient;
import com.han.common.storage.core.sftp.SftpStorageClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author WeiHan
 * @CreateTime: 2026-02-10
 * @Description: 文件存储器枚举
 */
@AllArgsConstructor
@Getter
public enum StorageTypeEnum {

    /**
     * 数据库
     */
    DB(1, "数据库", DbStorageClientConfig.class, DbStorageClient.class),

    /**
     * 本地存储
     */
    LOCAL(10, "本地存储", LocalStorageClientConfig.class, LocalStorageClient.class),

    /**
     * FTP
     */
    FTP(11, "FTP", FtpStorageClientConfig.class, FtpStorageClient.class),

    /**
     * SFTP
     */
    SFTP(12, "SFTP", SftpStorageClientConfig.class, SftpStorageClient.class),

    /**
     * S3
     */
    S3(20, "S3", S3StorageClientConfig.class, S3StorageClient.class);

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
    private final Class<? extends StorageClientConfig> configClass;

    /**
     * 客户端类
     */
    private final Class<? extends StorageClient> clientClass;

    /**
     * 根据存储类型获取枚举
     *
     * @param storageType 存储类型
     * @return 枚举
     */
    public static StorageTypeEnum getByStorageType(Integer storageType) {
        if (storageType == null) {
            return null;
        }
        for (StorageTypeEnum value : values()) {
            if (value.getStorageType().equals(storageType)) {
                return value;
            }
        }
        return null;
    }
}
