package com.han.web.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.domain.model.PasswordLoginBody;
import com.han.common.core.enums.LoginType;
import com.han.common.core.exception.user.CaptchaException;
import com.han.common.core.exception.user.CaptchaExpireException;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.web.config.properties.CaptchaProperties;
import com.han.system.domain.SysUser;
import com.han.system.domain.vo.SysClientVo;
import com.han.system.domain.vo.SysUserVo;
import com.han.web.domain.vo.LoginVo;
import com.han.web.service.IAuthStrategy;
import com.han.web.service.SysLoginService;
import org.springframework.stereotype.Service;

/**
 * @Author: Michelle.Chung
 * @CreateTime: 2026-01-20
 * @Description: 密码认证策略
 */
@Slf4j
@RequiredArgsConstructor
@Service("password" + IAuthStrategy.BASE_NAME)
public class PasswordAuthStrategy implements IAuthStrategy {

    private final CaptchaProperties captchaProperties;
    private final SysLoginService loginService;

    /**
     * 执行用户名+密码登录流程（支持图形验证码校验）
     *
     * @param body   前端传递的 JSON 字符串（包含用户名、密码、验证码及 uuid 等）
     * @param client 客户端配置信息（包含 clientId、超时时间、设备类型等）
     * @return 登录成功后的令牌信息（LoginVo）
     */
    @Override
    public LoginVo login(String body, SysClientVo client) {
        // 解析并校验登录请求参数
        PasswordLoginBody loginBody = JsonUtils.parseObject(body, PasswordLoginBody.class);
        if (loginBody == null) {
            throw new IllegalArgumentException("登录请求参数不能为空");
        }
        ValidatorUtils.validate(loginBody);

        String userAccount = loginBody.getUserAccount();
        String password = loginBody.getPassword();
        String code = loginBody.getCode();
        String uuid = loginBody.getUuid();

        // 判断是否开启图形验证码校验
        boolean captchaEnabled = captchaProperties.getEnable();
        if (captchaEnabled) {
            validateCaptcha(userAccount, code, uuid);
        }

        // 根据用户名加载用户信息并进行状态检查
        SysUserVo user = loginService.loadUserByField(SysUser::getUserAccount, userAccount);

        // 校验密码是否正确，同时记录登录失败次数等风控逻辑
        loginService.checkLogin(LoginType.PASSWORD, userAccount,
            () -> !BCrypt.checkpw(password, user.getPassword()));

        return loginService.processLogin(user, client);
    }

    /**
     * 校验图形验证码是否正确
     *
     * @param userAccount 用户名（用于登录失败日志记录）
     * @param code     用户输入的验证码
     * @param uuid     验证码对应的唯一标识（通常为前端生成的随机串）
     * @throws CaptchaExpireException 验证码已过期或不存在
     * @throws CaptchaException       验证码不正确
     */
    private void validateCaptcha(String userAccount, String code, String uuid) {
        // 构建验证码在 Redis 中的 key
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");

        // 获取验证码
        String captcha = RedisUtils.getCacheObject(verifyKey);
        // 使用后立即删除（防止重复使用）
        RedisUtils.deleteObject(verifyKey);

        if (captcha == null) {
            loginService.recordLogininfor(userAccount, Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }

        if (!StringUtils.equalsIgnoreCase(code, captcha)) {
            loginService.recordLogininfor(userAccount, Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }
}
