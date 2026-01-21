package com.han.common.core.exception.file;

import java.io.Serial;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 文件名称超长限制异常类
 */
public class FileNameLengthLimitExceededException extends FileException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super("upload.filename.exceed.length", new Object[]{defaultFileNameLength});
    }
}
