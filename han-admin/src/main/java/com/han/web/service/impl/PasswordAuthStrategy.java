package com.han.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.domain.model.LoginUser;
import com.han.common.core.domain.model.PasswordLoginBody;
import com.han.common.core.enums.LoginType;
import com.han.common.core.exception.user.CaptchaException;
import com.han.common.core.exception.user.CaptchaExpireException;
import com.han.common.core.exception.user.UserException;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.satoken.utils.LoginHelper;
import com.han.common.web.config.properties.CaptchaProperties;
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
 * @Description: 密码认证策略
 */
@Slf4j
@RequiredArgsConstructor
@Service("password" + IAuthStrategy.BASE_NAME)
public class PasswordAuthStrategy implements IAuthStrategy {

    private final CaptchaProperties captchaProperties;
    private final SysLoginService loginService;
    private final SysUserMapper userMapper;

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

        String username = loginBody.getUsername();
        String password = loginBody.getPassword();
        String code = loginBody.getCode();
        String uuid = loginBody.getUuid();

        // 判断是否开启图形验证码校验
        boolean captchaEnabled = captchaProperties.getEnable();
        if (captchaEnabled) {
            validateCaptcha(username, code, uuid);
        }

        // 根据用户名加载用户信息并进行状态检查
        SysUserVo user = loadUserByUsername(username);

        // 校验密码是否正确，同时记录登录失败次数等风控逻辑
        loginService.checkLogin(LoginType.PASSWORD, username,
            () -> !BCrypt.checkpw(password, user.getPassword()));

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
     * 校验图形验证码是否正确
     *
     * @param username 用户名（用于登录失败日志记录）
     * @param code     用户输入的验证码
     * @param uuid     验证码对应的唯一标识（通常为前端生成的随机串）
     * @throws CaptchaExpireException 验证码已过期或不存在
     * @throws CaptchaException       验证码不正确
     */
    private void validateCaptcha(String username, String code, String uuid) {
        // 构建验证码在 Redis 中的 key
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");

        // 获取验证码
        String captcha = RedisUtils.getCacheObject(verifyKey);
        // 使用后立即删除（防止重复使用）
        RedisUtils.deleteObject(verifyKey);

        if (captcha == null) {
            loginService.recordLogininfor(username, Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }

        if (!StringUtils.equalsIgnoreCase(code, captcha)) {
            loginService.recordLogininfor(username, Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 根据用户名查询用户信息，并进行存在性及状态检查
     *
     * @param username 登录使用的用户名
     * @return 用户视图对象 SysUserVo
     * @throws UserException 当用户不存在或已被禁用时抛出
     */
    private SysUserVo loadUserByUsername(String username) {
        SysUserVo user = userMapper.selectVoOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, username)
        );

        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在。", username);
            throw new UserException("user.not.exists", username);
        }

        if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用。", username);
            throw new UserException("user.blocked", username);
        }

        return user;
    }
}
