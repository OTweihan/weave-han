package com.han.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.han.common.core.domain.R;
import com.han.web.domain.vo.CaptchaVo;
import com.han.web.service.CaptchaService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-16
 * @Description: 验证码操作处理
 */
@Slf4j
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @GetMapping("/resource/sms/code")
    public R<Void> smsCode(@NotBlank(message = "{user.phonenumber.not.blank}") String phonenumber) {
        captchaService.sendSmsCode(phonenumber);
        return R.ok();
    }

    /**
     * 邮箱验证码
     *
     * @param email 邮箱
     */
    @GetMapping("/resource/email/code")
    public R<Void> emailCode(@NotBlank(message = "{user.email.not.blank}") String email) {
        captchaService.sendEmailCode(email);
        return R.ok();
    }

    /**
     * 生成验证码
     */
    @GetMapping("/auth/code")
    public R<CaptchaVo> getCode() {
        return R.ok(captchaService.createCaptcha());
    }
}
