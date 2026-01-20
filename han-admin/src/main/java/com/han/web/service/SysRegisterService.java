package com.han.web.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.domain.model.RegisterBody;
import com.han.common.core.enums.UserType;
import com.han.common.core.exception.user.CaptchaException;
import com.han.common.core.exception.user.CaptchaExpireException;
import com.han.common.core.exception.user.UserException;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.ServletUtils;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.log.event.LogininforEvent;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.web.config.properties.CaptchaProperties;
import com.han.system.domain.SysUser;
import com.han.system.domain.bo.SysUserBo;
import com.han.system.mapper.SysUserMapper;
import com.han.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 用户注册校验与处理核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRegisterService {

    private final ISysUserService userService;
    private final SysUserMapper userMapper;
    private final CaptchaProperties captchaProperties;

    /**
     * 执行用户注册流程（支持图形验证码校验）
     *
     * @param registerBody 注册请求参数（包含用户名、密码、验证码、用户类型等）
     * @throws UserException      用户名已存在或注册失败
     * @throws CaptchaException   验证码错误
     * @throws CaptchaExpireException 验证码过期
     */
    public void register(RegisterBody registerBody) {
        // 参数判空与校验
        if (ObjectUtil.isNull(registerBody)) {
            throw new IllegalArgumentException("注册请求参数不能为空");
        }
        ValidatorUtils.validate(registerBody);

        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        // 判断是否开启图形验证码校验
        boolean captchaEnabled = captchaProperties.getEnable();
        if (captchaEnabled) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        // 构建用户注册数据（密码使用 BCrypt 加密存储）
        SysUserBo sysUser = new SysUserBo();
        sysUser.setUserName(username);
        // 默认昵称与用户名一致
        sysUser.setNickName(username);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setUserType(userType);

        // 检查用户名是否已存在（防止重复注册）
        boolean exist = userMapper.exists(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, sysUser.getUserName())
        );
        if (exist) {
            log.warn("注册用户：{} 已存在", username);
            throw new UserException("user.register.save.error", username);
        }

        // 执行注册（由 ISysUserService 处理完整注册流程，包括角色分配等）
        boolean regFlag = userService.registerUser(sysUser);
        if (!regFlag) {
            log.error("注册用户：{} 执行失败", username);
            throw new UserException("user.register.error");
        }

        // 记录注册成功日志
        recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    /**
     * 校验图形验证码是否正确（使用后立即删除，防止重复使用）
     *
     * @param username 用户名（用于失败日志记录）
     * @param code     用户输入的验证码
     * @param uuid     验证码唯一标识（前端生成）
     * @throws CaptchaExpireException 验证码已过期或不存在
     * @throws CaptchaException       验证码不正确
     */
    public void validateCaptcha(String username, String code, String uuid) {
        // 构建 Redis 缓存 key
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");

        // 获取并立即删除验证码（单次使用）
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);

        if (StringUtils.isBlank(captcha)) {
            recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }

        if (!StringUtils.equalsIgnoreCase(code, captcha)) {
            recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 记录注册/登录相关操作日志（通过事件发布异步处理）
     *
     * @param username 用户标识（用户名/手机号等）
     * @param status   操作状态（REGISTER / LOGIN_FAIL 等）
     * @param message  操作描述信息
     */
    private void recordLogininfor(String username, String status, String message) {
        LogininforEvent event = new LogininforEvent();
        event.setUsername(username);
        event.setStatus(status);
        event.setMessage(message);
        event.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(event);
    }
}
