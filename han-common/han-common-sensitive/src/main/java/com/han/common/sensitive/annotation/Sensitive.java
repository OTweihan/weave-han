package com.han.common.sensitive.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.han.common.sensitive.core.SensitiveStrategy;
import com.han.common.sensitive.handler.SensitiveHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zhujie
 * @CreateTime: 2026-01-22
 * @Description: 数据脱敏注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveHandler.class)
public @interface Sensitive {

    /**
     * 脱敏策略
     */
    SensitiveStrategy strategy();

    /**
     * 角色标识符 多个角色满足一个即可
     */
    String[] roleKey() default {};

    /**
     * 权限标识符 多个权限满足一个即可
     */
    String[] perms() default {};
}
