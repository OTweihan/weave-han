package com.han.common.oss.exception;

import java.io.Serial;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: OSS 异常类
 */
public class OssException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }
}
