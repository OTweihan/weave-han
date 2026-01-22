package com.han.common.ratelimiter.config;

import com.han.common.ratelimiter.aspectj.RateLimiterAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * @Author: guangxin
 * @CreateTime: 2023-01-18
 * @Description: 限流配置
 */

@AutoConfiguration(after = RedisConfiguration.class)
public class RateLimiterConfig {

    @Bean
    public RateLimiterAspect rateLimiterAspect() {
        return new RateLimiterAspect();
    }
}
