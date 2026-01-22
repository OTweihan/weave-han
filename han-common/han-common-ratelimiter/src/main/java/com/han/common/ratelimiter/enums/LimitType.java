package com.han.common.ratelimiter.enums;

/**
 * @Author: ruoyi
 * @CreateTime: 2026-01-22
 * @Description: 限流类型
 */
public enum LimitType {

    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP,

    /**
     * 实例限流(集群多后端实例)
     */
    CLUSTER
}
