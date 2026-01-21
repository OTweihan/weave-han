package com.han.common.core.exception.file;

import java.io.Serial;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 文件名大小限制异常类
 */
public class FileSizeLimitExceededException extends FileException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super("upload.exceed.maxSize", new Object[]{defaultMaxSize});
    }
}
