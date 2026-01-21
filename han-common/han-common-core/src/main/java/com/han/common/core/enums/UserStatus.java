package com.han.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * 缓存枚举值的 Map，用于根据 code 快速查找
     */
    private static final Map<String, UserStatus> CODE_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(UserStatus::getCode, Function.identity()));

    /**
     * 根据 code 查找匹配的枚举值
     *
     * @param code 状态码
     * @return 匹配到的 UserStatus 的 Optional
     */
    public static Optional<UserStatus> getByCode(String code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    /**
     * 返回枚举的描述性字符串
     *
     * @return 状态信息
     */
    @Override
    public String toString() {
        return info;
    }
}
