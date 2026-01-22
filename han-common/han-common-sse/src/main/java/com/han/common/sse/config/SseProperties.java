package com.han.common.sse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: SSE 配置项
 */
@Data
@ConfigurationProperties("sse")
public class SseProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 路径
     */
    private String path;

    /**
     * 消息超时时间 (默认 1 天)
     */
    private Long timeout = 86400000L;

    /**
     * 心跳间隔 (默认 60 秒)
     */
    private Long heartbeat = 60000L;
}
