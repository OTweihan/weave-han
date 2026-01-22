package com.han.common.json.validate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: AprilWind
 * @CreateTime: 2026-01-22
 * @Description: JSON 类型枚举
 */
@Getter
@AllArgsConstructor
public enum JsonType {

    /**
     * JSON 对象，例如 {"a":1}
     */
    OBJECT,

    /**
     * JSON 数组，例如 [1,2,3]
     */
    ARRAY,

    /**
     * 任意 JSON 类型，对象或数组都可以
     */
    ANY
}
