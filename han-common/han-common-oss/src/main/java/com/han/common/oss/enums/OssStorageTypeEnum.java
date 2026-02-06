package com.han.common.oss.enums;

import cn.hutool.core.util.ArrayUtil;
import com.han.common.oss.core.ftp.FtpOssClient;
import com.han.common.oss.core.ftp.FtpOssClientConfig;
import com.han.common.oss.core.local.LocalOssClient;
import com.han.common.oss.core.OssClientConfig;
import com.han.common.oss.core.OssClient;
import com.han.common.oss.core.db.DbOssClient;
import com.han.common.oss.core.db.DbOssClientConfig;
import com.han.common.oss.core.local.LocalOssClientConfig;
import com.han.common.oss.core.s3.S3OssClient;
import com.han.common.oss.core.s3.S3OssClientConfig;
import com.han.common.oss.core.sftp.SftpOssClient;
import com.han.common.oss.core.sftp.SftpOssClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储器枚举
 *
 * @author Qoder
 */
@AllArgsConstructor
@Getter
public enum OssStorageTypeEnum {

    DB(1, DbOssClientConfig.class, DbOssClient.class),
    LOCAL(10, LocalOssClientConfig.class, LocalOssClient.class),
    FTP(11, FtpOssClientConfig.class, FtpOssClient.class),
    SFTP(12, SftpOssClientConfig.class, SftpOssClient.class),
    S3(20, S3OssClientConfig.class, S3OssClient.class),
    ;

    /**
     * 存储类型
     */
    private final Integer storageType;
    /**
     * 配置类
     */
    private final Class<? extends OssClientConfig> configClass;

    /**
     * 客户端类
     */
    private final Class<? extends OssClient> clientClass;

    public static OssStorageTypeEnum getByStorageType(Integer storage) {
        return ArrayUtil.firstMatch(o -> o.getStorageType().equals(storage), values());
    }
}
