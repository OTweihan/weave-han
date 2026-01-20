package com.han.web.service.impl;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.domain.model.EmailLoginBody;
import com.han.common.core.enums.LoginType;
import com.han.common.core.exception.user.CaptchaExpireException;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.redis.utils.RedisUtils;
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
 * @Description: 邮件认证策略
 */
@Slf4j
@RequiredArgsConstructor
@Service("email" + IAuthStrategy.BASE_NAME)
public class EmailAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;

    /**
     * 执行邮箱验证码登录流程
     *
     * @param body   前端传递的 JSON 字符串（包含 email 与 emailCode）
     * @param client 客户端配置信息（包含 clientId、超时时间等）
     * @return 登录成功后的令牌信息（LoginVo）
     */
    @Override
    public LoginVo login(String body, SysClientVo client) {
        // 解析并验证登录请求参数
        EmailLoginBody loginBody = JsonUtils.parseObject(body, EmailLoginBody.class);

        // 显式判空，防止后续 NPE
        if (ObjectUtil.isNull(loginBody)) {
            throw new IllegalArgumentException("登录请求参数不能为空");
        }

        ValidatorUtils.validate(loginBody);

        String email = loginBody.getEmail();
        String emailCode = loginBody.getEmailCode();

        // 根据邮箱查询用户信息（包含状态检查）
        SysUserVo user = loginService.loadUserByField(SysUser::getEmail, email);

        // 校验登录失败次数、账号状态，并验证邮箱验证码是否正确
        loginService.checkLogin(LoginType.EMAIL, user.getUserName(), () -> !validateEmailCode(email, emailCode));

        return loginService.processLogin(user, client);
    }

    /**
     * 验证邮箱收到的验证码是否正确
     *
     * @param email     用户邮箱地址（同时作为缓存 key 的一部分）
     * @param emailCode 前端提交的验证码
     * @return 验证码是否匹配
     * @throws CaptchaExpireException 当缓存中验证码已过期或不存在时抛出
     */
    private boolean validateEmailCode(String email, String emailCode) {
        // 从 Redis 获取已发送的验证码
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + email);

        if (StringUtils.isBlank(code)) {
            // 记录登录失败日志：验证码过期
            loginService.recordLogininfor(email, Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }

        return code.equals(emailCode);
    }
}
