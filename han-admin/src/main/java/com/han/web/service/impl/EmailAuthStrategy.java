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
import com.han.common.core.domain.model.EmailLoginBody;
import com.han.common.core.domain.model.LoginUser;
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
 * @Description: 邮件认证策略
 */
@Slf4j
@RequiredArgsConstructor
@Service("email" + IAuthStrategy.BASE_NAME)
public class EmailAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;
    private final SysUserMapper userMapper;

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
        SysUserVo user = loadUserByEmail(email);

        // 校验登录失败次数、账号状态，并验证邮箱验证码是否正确
        loginService.checkLogin(LoginType.EMAIL, user.getUserName(), () -> !validateEmailCode(email, emailCode));

        // 构建登录用户对象（可根据实际需求扩展 LoginUser 字段）
        LoginUser loginUser = loginService.buildLoginUser(user);

        // 设置客户端相关信息
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());

        // 构造 Sa-Token 登录参数
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(client.getDeviceType());

        // 支持不同客户端配置不同的 token 超时时间
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());

        // 执行登录并生成 token
        LoginHelper.login(loginUser, model);

        // 组装返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());

        return loginVo;
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

    /**
     * 根据邮箱地址查询用户信息，并进行基本可用性检查
     *
     * @param email 登录使用的邮箱地址
     * @return 用户视图对象 SysUserVo
     * @throws UserException 当用户不存在或已被禁用时抛出
     */
    private SysUserVo loadUserByEmail(String email) {
        // 使用 MyBatis-Plus Lambda 方式精确查询
        SysUserVo user = userMapper.selectVoOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email)
        );

        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在。", email);
            throw new UserException("user.not.exists", email);
        }

        if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用。", email);
            throw new UserException("user.blocked", email);
        }

        return user;
    }
}
