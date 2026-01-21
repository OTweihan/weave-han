package com.han.common.core.service;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-21
 * @Description: 通用参数配置服务
 */
public interface ConfigService {

    /**
     * 根据参数 key 获取参数值
     *
     * @param configKey 参数 key
     * @return 参数值
     */
    String getConfigValue(String configKey);
}
