package com.han.common.encrypt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: wdhcr
 * @CreateTime: 2026-01-22
 * @Description: api 解密属性配置类
 */
@Data
@ConfigurationProperties(prefix = "api-decrypt")
public class ApiDecryptProperties {

    /**
     * 加密开关
     */
    private Boolean enabled;

    /**
     * 头部标识
     */
    private String headerFlag;

    /**
     * 响应加密公钥
     */
    private String publicKey;

    /**
     * 请求解密私钥
     */
    private String privateKey;

    /**
     * 排除路径
     */
    private java.util.List<String> excludePath;
}
