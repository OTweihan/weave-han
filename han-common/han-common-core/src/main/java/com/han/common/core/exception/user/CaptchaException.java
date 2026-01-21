package com.han.common.core.exception.user;

import java.io.Serial;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 验证码错误异常类
 */
public class CaptchaException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("user.jcaptcha.error");
    }
}
