package com.han.web.domain.vo;

import lombok.Data;

/**
 * @Author: Michelle.Chung
 * @CreateTime: 2026-01-16
 * @Description: 验证码信息
 */
@Data
public class CaptchaVo {

    /**
     * 是否开启验证码
     */
    private Boolean captchaEnabled = true;

    /**
     * 验证码uuid
     */
    private String uuid;

    /**
     * 验证码图片
     */
    private String img;
}
