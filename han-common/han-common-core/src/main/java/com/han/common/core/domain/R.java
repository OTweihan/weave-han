package com.han.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.han.common.core.constant.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 统一响应结果封装（RESTful API 返回标准结构）
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 操作成功状态码
     */
    public static final int SUCCESS = 200;

    /**
     * 操作失败状态码（业务异常默认）
     */
    public static final int FAIL = 500;

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    // 成功响应

    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> R<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    // 失败响应

    public static <T> R<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, "操作失败");
    }

    public static <T> R<T> fail(String msg, T data) {
        return restResult(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    // 警告响应

    /**
     * 返回警告消息（通常用于非致命提示，例如部分成功、需要用户确认等）
     *
     * @param msg 返回内容
     * @return 警告响应对象
     */
    public static <T> R<T> warn(String msg) {
        return restResult(null, HttpStatus.WARN, msg);
    }

    /**
     * 返回警告消息并携带数据
     *
     * @param msg  返回内容
     * @param data 附加数据
     * @return 警告响应对象
     */
    public static <T> R<T> warn(String msg, T data) {
        return restResult(data, HttpStatus.WARN, msg);
    }

    // 内部构建方法

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 状态判断工具方法

    /**
     * 判断响应是否为错误状态
     *
     * @param ret 响应对象
     * @return true 表示失败，false 表示成功
     */
    public static <T> Boolean isError(R<T> ret) {
        return !isSuccess(ret);
    }

    /**
     * 判断响应是否为成功状态
     *
     * @param ret 响应对象
     * @return true 表示成功，false 表示失败
     */
    public static <T> Boolean isSuccess(R<T> ret) {
        return ret != null && R.SUCCESS == ret.getCode();
    }
}
