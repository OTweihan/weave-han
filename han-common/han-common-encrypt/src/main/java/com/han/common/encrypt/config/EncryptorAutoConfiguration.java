package com.han.common.encrypt.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.han.common.encrypt.core.EncryptorManager;
import com.han.common.encrypt.interceptor.MybatisDecryptInterceptor;
import com.han.common.encrypt.interceptor.MybatisEncryptInterceptor;
import com.han.common.encrypt.properties.EncryptorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 加解密配置
 */
@Slf4j
@EnableConfigurationProperties(EncryptorProperties.class)
@AutoConfiguration(after = MybatisPlusAutoConfiguration.class)
@ConditionalOnProperty(value = "mybatis-encryptor.enable", havingValue = "true")
public class EncryptorAutoConfiguration {

    @Resource
    private EncryptorProperties properties;

    @Bean
    public EncryptorManager encryptorManager(MybatisPlusProperties mybatisPlusProperties) {
        return new EncryptorManager(mybatisPlusProperties.getTypeAliasesPackage(), properties);
    }

    @Bean
    public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager) {
        return new MybatisEncryptInterceptor(encryptorManager);
    }

    @Bean
    public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager) {
        return new MybatisDecryptInterceptor(encryptorManager);
    }
}
