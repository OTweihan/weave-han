package com.han.common.oss.core;

import java.io.IOException;

/**
 * @Author: 秋辞未寒
 * @CreateTime: 2026-01-22
 * @Description: 写出订阅器
 */
@FunctionalInterface
public interface WriteOutSubscriber<T> {

    void writeTo(T out) throws IOException;
}
