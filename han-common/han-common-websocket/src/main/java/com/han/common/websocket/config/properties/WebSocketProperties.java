package com.han.common.websocket.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: zendwang
 * @CreateTime: 2026-01-23
 * @Description: WebSocket 配置项
 */
@Data
@ConfigurationProperties("websocket")
public class WebSocketProperties {

    /**
     * 是否启用 WebSocket
     */
    private Boolean enabled;

    /**
     * 路径
     */
    private String path;

    /**
     *  设置访问源地址
     */
    private String allowedOrigins;
}
