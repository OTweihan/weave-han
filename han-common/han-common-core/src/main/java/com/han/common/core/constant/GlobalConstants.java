package com.han.common.core.constant;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 全局的 key 常量 (业务无关的key)
 */
public final class GlobalConstants {

    private GlobalConstants() {
        // 防止实例化
    }

    /**
     * 全局 redis key (业务无关的key)
     */
    public static final String GLOBAL_REDIS_KEY = "global:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = GLOBAL_REDIS_KEY + "captcha_codes:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = GLOBAL_REDIS_KEY + "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = GLOBAL_REDIS_KEY + "rate_limit:";

    /**
     * 三方认证 redis key
     */
    public static final String SOCIAL_AUTH_CODE_KEY = GLOBAL_REDIS_KEY + "social_auth_codes:";
}
