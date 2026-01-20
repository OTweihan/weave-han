package com.han.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.han.common.core.domain.R;
import com.han.common.core.domain.model.RegisterBody;
import com.han.common.core.domain.model.SocialLoginBody;
import com.han.common.encrypt.annotation.ApiEncrypt;
import com.han.system.service.ISysConfigService;
import com.han.web.domain.vo.LoginVo;
import com.han.web.service.SysLoginService;
import com.han.web.service.SysRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-16
 * @Description: 认证 Controller
 */
@Slf4j
@SaIgnore
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SysLoginService loginService;
    private final SysRegisterService registerService;
    private final ISysConfigService configService;

    /**
     * 登录方法
     *
     * @param body 登录信息
     * @return 结果
     */
    @ApiEncrypt
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody String body) {
        LoginVo loginVo = loginService.login(body);
        return R.ok(loginVo);
    }

    /**
     * 获取跳转URL
     *
     * @param source 登录来源
     * @return 结果
     */
    @GetMapping("/binding/{source}")
    public R<String> authBinding(@PathVariable("source") String source, @RequestParam String domain) {
        String authorizeUrl = loginService.authBinding(source, domain);
        return R.ok("操作成功", authorizeUrl);
    }

    /**
     * 前端回调绑定授权(需要token)
     *
     * @param loginBody 请求体
     * @return 结果
     */
    @PostMapping("/social/callback")
    public R<Void> socialCallback(@RequestBody SocialLoginBody loginBody) {
        // 校验token
        StpUtil.checkLogin();
        loginService.socialCallback(loginBody);
        return R.ok();
    }

    /**
     * 取消授权(需要token)
     *
     * @param socialId socialId
     */
    @DeleteMapping(value = "/unlock/{socialId}")
    public R<Void> unlockSocial(@PathVariable Long socialId) {
        // 校验token
        StpUtil.checkLogin();
        loginService.unlockSocial(socialId);
        return R.ok();
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功");
    }

    /**
     * 用户注册
     */
    @ApiEncrypt
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        if (!configService.selectRegisterEnabled()) {
            return R.fail("当前系统没有开启注册功能！");
        }
        registerService.register(user);
        return R.ok();
    }
}
