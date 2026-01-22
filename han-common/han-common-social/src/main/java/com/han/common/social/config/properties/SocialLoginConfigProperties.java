package com.han.common.social.config.properties;

import lombok.Data;

import java.util.List;

/**
 * @Author: thiszhc
 * @CreateTime: 2026-01-22
 * @Description: 社交登录配置
 */
@Data
public class SocialLoginConfigProperties {

    /**
     * 应用 ID
     */
    private String clientId;

    /**
     * 应用密钥
     */
    private String clientSecret;

    /**
     * 回调地址
     */
    private String redirectUri;

    /**
     * 是否获取unionId
     */
    private boolean unionId;

    /**
     * 请求范围
     */
    private List<String> scopes;
}
