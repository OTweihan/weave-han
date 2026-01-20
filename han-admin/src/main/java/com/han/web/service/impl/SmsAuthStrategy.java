package com.han.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.domain.model.SmsLoginBody;
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
 * @Description: 短信认证策略
 */
@Slf4j
@RequiredArgsConstructor
@Service("sms" + IAuthStrategy.BASE_NAME)
public class SmsAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;

    /**
     * 执行手机号 + 短信验证码登录流程
     *
     * @param body   前端传递的 JSON 字符串（包含手机号与短信验证码）
     * @param client 客户端配置信息（包含 clientId、超时时间、设备类型等）
     * @return 登录成功后的令牌信息（LoginVo）
     */
    @Override
    public LoginVo login(String body, SysClientVo client) {
        // 解析并校验登录请求参数
        SmsLoginBody loginBody = JsonUtils.parseObject(body, SmsLoginBody.class);
        if (loginBody == null) {
            throw new IllegalArgumentException("登录请求参数不能为空");
        }
        ValidatorUtils.validate(loginBody);

        String phonenumber = loginBody.getPhonenumber();
        String smsCode = loginBody.getSmsCode();

        // 建议在此增加基础非空校验（防御性编程）
        if (StringUtils.isAnyBlank(phonenumber, smsCode)) {
            throw new IllegalArgumentException("手机号或验证码不能为空");
        }

        // 根据手机号加载用户信息并进行状态检查
        SysUserVo user = loginService.loadUserByField(SysUser::getPhonenumber, phonenumber);

        // 校验短信验证码是否正确，同时执行登录风控检查
        loginService.checkLogin(LoginType.SMS, user.getUserName(),
            () -> !validateSmsCode(phonenumber, smsCode));

        return loginService.processLogin(user, client);
    }

    /**
     * 校验短信验证码是否正确
     *
     * @param phonenumber 手机号（同时作为缓存 key 的一部分）
     * @param smsCode     前端提交的短信验证码
     * @return 验证码是否匹配
     * @throws CaptchaExpireException 当验证码已过期或不存在时抛出
     */
    private boolean validateSmsCode(String phonenumber, String smsCode) {
        // 从 Redis 获取已发送的短信验证码
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber);

        if (StringUtils.isBlank(code)) {
            // 记录登录失败日志：验证码过期
            loginService.recordLogininfor(phonenumber, Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }

        return code.equals(smsCode);
    }
}
