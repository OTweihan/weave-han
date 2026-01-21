package com.han.common.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 线程池 配置属性
 */
@Data
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {

    /**
     * 是否开启线程池
     */
    private boolean enabled = true;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 队列最大长度
     */
    private int queueCapacity;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private int keepAliveSeconds;

    /**
     * 停机等待时间（秒）
     */
    private int shutdownAwaitSeconds = 120;
}
