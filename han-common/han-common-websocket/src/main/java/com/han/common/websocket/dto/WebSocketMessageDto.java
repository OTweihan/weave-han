package com.han.common.websocket.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: zendwang
 * @CreateTime: 2026-01-23
 * @Description: 消息的 DTO
 */
@Data
public class WebSocketMessageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 需要推送到的session key 列表
     */
    private List<Long> sessionKeys;

    /**
     * 需要发送的消息
     */
    private String message;
}
