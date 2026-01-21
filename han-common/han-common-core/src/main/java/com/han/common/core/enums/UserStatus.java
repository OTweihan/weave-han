package com.han.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author ruoyi
 * @CreateTime: 2026-01-21
 * @Description: 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
    /**
     * 正常
     */
    OK("0", "正常"),
    /**
     * 停用
     */
    DISABLE("1", "停用"),
    /**
     * 删除
     */
    DELETED("2", "删除");

    private final String code;
    private final String info;
}
