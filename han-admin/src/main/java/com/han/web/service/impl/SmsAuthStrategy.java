package com.han.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.domain.model.LoginUser;
import com.han.common.core.domain.model.SmsLoginBody;
import com.han.common.core.enums.LoginType;
import com.han.common.core.exception.user.CaptchaExpireException;
import com.han.common.core.exception.user.UserException;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.satoken.utils.LoginHelper;
import com.han.system.domain.SysUser;
import com.han.system.domain.vo.SysClientVo;
import com.han.system.domain.vo.SysUserVo;
import com.han.system.mapper.SysUserMapper;
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
    private final SysUserMapper userMapper;

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
        SysUserVo user = loadUserByPhonenumber(phonenumber);

        // 校验短信验证码是否正确，同时执行登录风控检查
        loginService.checkLogin(LoginType.SMS, user.getUserName(),
            () -> !validateSmsCode(phonenumber, smsCode));

        // 构建登录用户信息对象（可根据业务需求扩展字段）
        LoginUser loginUser = loginService.buildLoginUser(user);

        // 设置客户端相关标识
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());

        // 构造 Sa-Token 登录参数，支持不同客户端不同超时策略
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(client.getDeviceType());
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());

        // 执行登录，生成 token
        LoginHelper.login(loginUser, model);

        // 组装返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());

        return loginVo;
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

    /**
     * 根据手机号查询用户信息，并进行存在性及状态检查
     *
     * @param phonenumber 登录使用的手机号
     * @return 用户视图对象 SysUserVo
     * @throws UserException 当用户不存在或已被禁用时抛出
     */
    private SysUserVo loadUserByPhonenumber(String phonenumber) {
        SysUserVo user = userMapper.selectVoOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhonenumber, phonenumber)
        );

        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在。", phonenumber);
            throw new UserException("user.not.exists", phonenumber);
        }

        if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用。", phonenumber);
            throw new UserException("user.blocked", phonenumber);
        }

        return user;
    }
}
