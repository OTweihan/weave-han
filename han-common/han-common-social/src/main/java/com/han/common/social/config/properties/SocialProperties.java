package com.han.common.social.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @Author: thiszhc
 * @CreateTime: 2026-01-22
 * @Description: Social 配置属性
 */
@Data
@ConfigurationProperties(prefix = "justauth")
public class SocialProperties {

    /**
     * 授权类型
     */
    private Map<String, SocialLoginConfigProperties> type;
}
