package com.han.common.core.xss;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-21
 * @Description: 自定义xss校验注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {XssValidator.class})
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
public @interface Xss {

    String message() default "不允许任何脚本运行";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
