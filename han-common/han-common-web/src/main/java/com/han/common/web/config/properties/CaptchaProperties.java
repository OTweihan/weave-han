package com.han.common.web.config.properties;

import lombok.Data;
import com.han.common.web.enums.CaptchaCategory;
import com.han.common.web.enums.CaptchaType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 验证码 配置属性
 */
@Data
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 是否开启验证码
     */
    private Boolean enable;

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证码类别
     */
    private CaptchaCategory category;

    /**
     * 数字验证码位数
     */
    private Integer numberLength;

    /**
     * 字符验证码长度
     */
    private Integer charLength;
}
