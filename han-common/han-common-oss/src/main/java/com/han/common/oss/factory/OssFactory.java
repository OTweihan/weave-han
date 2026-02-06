package com.han.common.oss.factory;

import com.han.common.oss.core.db.DbOssClient;
import com.han.common.oss.core.ftp.FtpOssClient;
import com.han.common.oss.core.local.LocalOssClient;
import com.han.common.oss.core.s3.S3OssClient;
import com.han.common.oss.core.sftp.SftpOssClient;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.CacheNames;
import com.han.common.core.utils.StringUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.oss.constant.OssConstant;
import com.han.common.oss.core.*;
import com.han.common.oss.exception.OssException;
import com.han.common.oss.properties.OssProperties;
import com.han.common.redis.utils.CacheUtils;
import com.han.common.redis.utils.RedisUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 文件上传 Factory
 */
@Slf4j
public class OssFactory {

    private static final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取默认实例
     */
    public static OssClient instance() {
        // 获取redis 默认类型
        String configKey = RedisUtils.getCacheObject(OssConstant.DEFAULT_CONFIG_KEY);
        if (StringUtils.isEmpty(configKey)) {
            throw new OssException("文件存储服务类型无法找到!");
        }
        return instance(configKey);
    }

    /**
     * 根据类型获取实例
     */
    public static OssClient instance(String configKey) {
        String json = CacheUtils.get(CacheNames.SYS_OSS_CONFIG, configKey);
        if (json == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        OssProperties properties = JsonUtils.parseObject(json, OssProperties.class);
        OssClient client = CLIENT_CACHE.get(configKey);
        // 客户端不存在或配置不相同则重新构建
        if (client == null || !client.checkPropertiesSame(properties)) {
            LOCK.lock();
            try {
                client = CLIENT_CACHE.get(configKey);
                if (client == null || !client.checkPropertiesSame(properties)) {
                    OssClient newClient = createOssClient(configKey, properties);
                    OssClient oldClient = CLIENT_CACHE.put(configKey, newClient);
                    if (oldClient != null) {
                        // 关闭旧客户端
                        oldClient.close();
                    }
                    log.info("创建OSS实例 key => {}", configKey);
                    return newClient;
                }
            } finally {
                LOCK.unlock();
            }
        }
        return client;
    }

    private static OssClient createOssClient(String configKey, OssProperties properties) {
        String storageType = properties.getStorageType();
        if (StringUtils.isBlank(storageType)) {
            // 默认 S3
            return new S3OssClient(configKey, properties);
        }
        return switch (storageType.toUpperCase()) {
            case "LOCAL", "2" -> new LocalOssClient(configKey, properties);
            case "FTP", "3" -> new FtpOssClient(configKey, properties);
            case "SFTP", "4" -> new SftpOssClient(configKey, properties);
            case "DB", "1" -> new DbOssClient(configKey, properties);
            default -> new S3OssClient(configKey, properties);
        };
    }
}
