package com.han.common.translation.core;

import com.han.common.translation.annotation.TranslationType;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 翻译接口 (实现类需标注 {@link TranslationType} 注解标明翻译类型)
 */
public interface TranslationInterface<T> {

    /**
     * 翻译
     *
     * @param key   需要被翻译的键(不为空)
     * @param other 其他参数
     * @return 返回键对应的值
     */
    T translation(Object key, String other);
}
