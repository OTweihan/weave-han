package com.han.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-21
 * @Description: 登录类型枚举
 */
@Getter
@AllArgsConstructor
public enum LoginType {

    /**
     * 密码登录
     */
    PASSWORD("user.password.retry.limit.exceed", "user.password.retry.limit.count"),

    /**
     * 短信登录
     */
    SMS("sms.code.retry.limit.exceed", "sms.code.retry.limit.count"),

    /**
     * 邮箱登录
     */
    EMAIL("email.code.retry.limit.exceed", "email.code.retry.limit.count"),

    /**
     * 小程序登录
     */
    APPLET("", "");

    /**
     * 登录重试超出限制提示
     */
    final String retryLimitExceed;

    /**
     * 登录重试限制计数提示
     */
    final String retryLimitCount;
}
