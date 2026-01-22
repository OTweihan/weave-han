package com.han.common.social.config;

import me.zhyd.oauth.cache.AuthStateCache;
import com.han.common.social.config.properties.SocialProperties;
import com.han.common.social.utils.AuthRedisStateCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @Author: thiszhc
 * @CreateTime: 2026-01-22
 * @Description: Social 配置属性
 */
@AutoConfiguration
@EnableConfigurationProperties(SocialProperties.class)
public class SocialAutoConfiguration {

    @Bean
    public AuthStateCache authStateCache() {
        return new AuthRedisStateCache();
    }
}
