package com.han.common.core.exception.user;

import java.io.Serial;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 验证码失效异常类
 */
public class CaptchaExpireException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire");
    }
}
