package com.han.system.service;

import com.han.system.domain.vo.CacheListInfoVo;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 缓存监控 服务层
 */
public interface ISysCacheService {

    /**
     * 获取缓存监控列表
     *
     * @return 缓存监控列表
     * @throws Exception 异常
     */
    CacheListInfoVo getCacheInfo() throws Exception;
}
