package com.han.common.oss.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-04
 * @Description: 文件客户端的配置
 * 不同实现的客户端，需要不同的配置，通过子类来定义
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface OssClientConfig {
}
