package com.han.common.oss.factory;

import com.han.common.oss.core.OssClient;
import com.han.common.oss.core.OssClientConfig;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 文件上传 Factory
 */
public interface OssClientFactory {

    /**
     * 获得文件客户端
     *
     * @param configId 配置编号
     * @return 文件客户端
     */
    OssClient getFileClient(Long configId);

    /**
     * 创建文件客户端
     *
     * @param configId 配置编号
     * @param storage  存储器的枚举 {@link com.han.common.oss.enums.OssStorageTypeEnum}
     * @param config   文件配置
     */
    <Config extends OssClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config);
}
