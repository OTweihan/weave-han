package com.han.common.core.exception.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.StringUtils;

import java.io.Serial;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 基础异常类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误码对应的参数
     */
    private Object[] args;

    /**
     * 错误消息
     */
    private String defaultMessage;

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this(null, code, args, null);
    }

    public BaseException(String defaultMessage) {
        this(null, null, null, defaultMessage);
    }

    @Override
    public String getMessage() {
        String message = null;
        if (!StringUtils.isEmpty(code)) {
            message = MessageUtils.message(code, args);
        }
        if (message == null) {
            message = defaultMessage;
        }
        return message;
    }
}
