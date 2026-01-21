package com.han.common.core.exception.user;

import com.han.common.core.exception.base.BaseException;

import java.io.Serial;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 用户信息异常类
 */
public class UserException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object... args) {
        super("user", code, args, null);
    }
}
