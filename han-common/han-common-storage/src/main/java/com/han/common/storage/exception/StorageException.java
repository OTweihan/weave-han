package com.han.common.storage.exception;

import java.io.Serial;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 存储异常类
 */
public class StorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public StorageException(String msg) {
        super(msg);
    }
}
