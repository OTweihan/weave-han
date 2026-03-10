package com.han.common.storage.factory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.han.common.storage.core.AbstractStorageClient;
import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.enums.StorageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-09
 * @Description: StorageClientFactory 实现类
 */
@Slf4j
@Component
public class StorageClientFactoryImpl implements StorageClientFactory {

    /**
     * 文件客户端 Map
     * key：配置编号
     */
    private final ConcurrentMap<Long, AbstractStorageClient<?>> clients = new ConcurrentHashMap<>();

    @Override
    public StorageClient getFileClient(Long configId) {
        AbstractStorageClient<?> client = clients.get(configId);
        if (client == null) {
            log.error("配置编号 {} 找不到客户端", configId);
        }
        return client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Config extends StorageClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config) {
        AbstractStorageClient<Config> client = (AbstractStorageClient<Config>) clients.get(configId);
        if (client == null) {
            client = this.createFileClient(configId, storage, config);
            client.init();
            clients.put(client.getOssConfigId(), client);
        } else {
            client.refresh(config);
        }
    }

    @SuppressWarnings("unchecked")
    private <Config extends StorageClientConfig> AbstractStorageClient<Config> createFileClient(
        Long configId, Integer storage, Config config) {
        StorageTypeEnum storageEnum = StorageTypeEnum.getByStorageType(storage);
        Assert.notNull(storageEnum, String.format("文件配置(%s) 为空", storageEnum));
        // 创建客户端
        return (AbstractStorageClient<Config>) ReflectUtil.newInstance(storageEnum.getClientClass(), configId, config);
    }
}
