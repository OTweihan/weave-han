package com.han.common.encrypt.config;

import jakarta.servlet.DispatcherType;
import com.han.common.encrypt.filter.CryptoFilter;
import com.han.common.encrypt.properties.ApiDecryptProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @Author: wdhcr
 * @CreateTime: 2026-01-22
 * @Description: api 解密自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiDecryptProperties.class)
@ConditionalOnProperty(value = "api-decrypt.enabled", havingValue = "true")
public class ApiDecryptAutoConfiguration {

    @Bean
    @FilterRegistration(
        name = "cryptoFilter",
        urlPatterns = "/*",
        order = FilterRegistrationBean.HIGHEST_PRECEDENCE,
        dispatcherTypes = DispatcherType.REQUEST
    )
    public CryptoFilter cryptoFilter(ApiDecryptProperties properties) {
        return new CryptoFilter(properties);
    }
}
