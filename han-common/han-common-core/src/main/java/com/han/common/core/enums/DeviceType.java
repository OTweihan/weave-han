package com.han.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-21
 * @Description: 设备类型枚举
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * pc端
     */
    PC("pc"),

    /**
     * app端
     */
    APP("app"),

    /**
     * 小程序端
     */
    APPLET("applet"),

    /**
     * 第三方社交登录平台
     */
    SOCIAL("social");

    /**
     * 设备标识
     */
    private final String device;

    /**
     * 返回枚举的描述性字符串
     *
     * @return 设备标识
     */
    @Override
    public String toString() {
        return device;
    }
}
