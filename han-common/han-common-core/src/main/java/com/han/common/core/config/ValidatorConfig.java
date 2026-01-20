package com.han.common.core.config;

import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Properties;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: Bean Validation 校验框架配置（支持国际化 + 快速失败模式）
 */
@AutoConfiguration(before = ValidationAutoConfiguration.class)
public class ValidatorConfig {

    /**
     * 配置 Hibernate Validator 为快速失败模式（Fail-Fast）
     * <p>核心特性：</p>
     * <ul>
     *     <li>国际化支持：通过 MessageSource 加载多语言校验消息</li>
     *     <li>快速失败：校验过程中遇到首个错误即停止，不继续校验后续字段</li>
     *     <li>优先级高于 Spring Boot 默认配置，确保自定义行为生效</li>
     * </ul>
     * <p>使用场景：适用于登录/注册/表单提交等高频校验，提高响应速度</p>
     *
     * @param messageSource Spring 国际化消息源（i18n）
     * @return 配置完成的 Validator 实例
     */
    @Bean
    public Validator validator(MessageSource messageSource) {
        try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
            // 配置国际化消息源，支持多语言校验提示
            factoryBean.setValidationMessageSource(messageSource);

            // 指定使用 Hibernate Validator 作为校验提供者（Spring Boot 默认）
            factoryBean.setProviderClass(HibernateValidator.class);

            // 配置 Validator 特性参数
            Properties properties = new Properties();
            // 启用快速失败模式：校验失败时立即返回，不校验剩余字段
            // 提升性能，减少不必要的校验开销，适合 REST API 场景
            properties.setProperty("hibernate.validator.fail_fast", "true");
            factoryBean.setValidationProperties(properties);

            // 初始化校验器工厂（加载配置并构建 Validator）
            factoryBean.afterPropertiesSet();

            return factoryBean.getValidator();
        }
    }
}
