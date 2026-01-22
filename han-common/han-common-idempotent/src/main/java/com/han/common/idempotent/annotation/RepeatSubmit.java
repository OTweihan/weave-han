package com.han.common.idempotent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 自定义注解防止表单重复提交
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    int interval() default 5000;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 提示消息 支持国际化 格式为 {code}
     */
    String message() default "{repeat.submit.message}";

    /**
     * 业务Key (支持Spring SpEL表达式)
     * 例如: #user.id
     */
    String key() default "";
}
