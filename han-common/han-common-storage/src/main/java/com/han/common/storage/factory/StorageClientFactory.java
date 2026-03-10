package com.han.common.storage.factory;

import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.enums.StorageTypeEnum;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 文件上传 Factory
 */
public interface StorageClientFactory {

    /**
     * 获得文件客户端
     *
     * @param configId 配置编号
     * @return 文件客户端
     */
    StorageClient getFileClient(Long configId);

    /**
     * 创建文件客户端
     *
     * @param configId 配置编号
     * @param storage  存储器的枚举 {@link StorageTypeEnum}
     * @param config   文件配置
     */
    <Config extends StorageClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config);
}
