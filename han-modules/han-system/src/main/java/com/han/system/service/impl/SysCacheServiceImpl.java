package com.han.system.service.impl;

import com.han.common.core.utils.StringUtils;
import com.han.system.domain.vo.CacheListInfoVo;
import com.han.system.service.ISysCacheService;
import lombok.RequiredArgsConstructor;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 缓存监控 服务层实现
 */
@RequiredArgsConstructor
@Service
public class SysCacheServiceImpl implements ISysCacheService {

    private final RedissonConnectionFactory connectionFactory;

    @Override
    public CacheListInfoVo getCacheInfo() throws Exception {
        RedisConnection connection = connectionFactory.getConnection();
        try {
            Properties commandStats = connection.commands().info("commandstats");
            List<Map<String, String>> pieList = new ArrayList<>();
            if (commandStats != null) {
                commandStats.stringPropertyNames().forEach(key -> {
                    Map<String, String> data = new HashMap<>(2);
                    String property = commandStats.getProperty(key);
                    data.put("name", StringUtils.removeStart(key, "cmdstat_"));
                    data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
                    pieList.add(data);
                });
            }
            return new CacheListInfoVo(connection.commands().info(), connection.commands().dbSize(), pieList);
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }
}
