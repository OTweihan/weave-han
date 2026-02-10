package com.han.common.oss.factory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.han.common.oss.core.AbstractOssClient;
import com.han.common.oss.core.OssClient;
import com.han.common.oss.core.OssClientConfig;
import com.han.common.oss.enums.OssStorageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-09
 * @Description: OssClientFactory 实现类
 */
@Slf4j
@Component
public class OssClientFactoryImpl implements OssClientFactory{

    /**
     * 文件客户端 Map
     * key：配置编号
     */
    private final ConcurrentMap<Long, AbstractOssClient<?>> clients = new ConcurrentHashMap<>();

    @Override
    public OssClient getFileClient(Long configId) {
        AbstractOssClient<?> client = clients.get(configId);
        if (client == null) {
            log.error("配置编号 {} 找不到客户端", configId);
        }
        return client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Config extends OssClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config) {
        AbstractOssClient<Config> client = (AbstractOssClient<Config>) clients.get(configId);
        if (client == null) {
            client = this.createFileClient(configId, storage, config);
            client.init();
            clients.put(client.getOssConfigId(), client);
        } else {
            client.refresh(config);
        }
    }

    @SuppressWarnings("unchecked")
    private <Config extends OssClientConfig> AbstractOssClient<Config> createFileClient(
        Long configId, Integer storage, Config config) {
        OssStorageTypeEnum storageEnum = OssStorageTypeEnum.getByStorageType(storage);
        Assert.notNull(storageEnum, String.format("文件配置(%s) 为空", storageEnum));
        // 创建客户端
        return (AbstractOssClient<Config>) ReflectUtil.newInstance(storageEnum.getClientClass(), configId, config);
    }
}
