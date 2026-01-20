package com.han.web.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.CacheConstants;
import com.han.common.core.constant.Constants;
import com.han.common.core.domain.dto.UserOnlineDTO;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.ServletUtils;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.ip.AddressUtils;
import com.han.common.log.event.LogininforEvent;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.satoken.utils.LoginHelper;
import com.han.web.service.SysLoginService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-16
 * @Description: 用户行为 侦听器的实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionListener implements SaTokenListener {

    private final SysLoginService loginService;

    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
        // 解析 User-Agent 信息
        UserAgent userAgent = UserAgentUtil.parse(Objects.requireNonNull(ServletUtils.getRequest()).getHeader("User-Agent"));
        String ip = ServletUtils.getClientIP();
        
        // 构建在线用户信息
        UserOnlineDTO dto = new UserOnlineDTO();
        dto.setIpaddr(ip);
        dto.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        dto.setBrowser(userAgent.getBrowser().getName());
        dto.setOs(userAgent.getOs().getName());
        dto.setLoginTime(System.currentTimeMillis());
        dto.setTokenId(tokenValue);
        
        // 从登录参数中获取额外信息
        String username = (String) loginParameter.getExtra(LoginHelper.USER_NAME_KEY);
        dto.setUserName(username);
        dto.setClientKey((String) loginParameter.getExtra(LoginHelper.CLIENT_KEY));
        dto.setDeviceType(loginParameter.getDeviceType());
        
        // 缓存用户在线状态
        if(loginParameter.getTimeout() == -1) {
            RedisUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto);
        } else {
            RedisUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto, Duration.ofSeconds(loginParameter.getTimeout()));
        }
        
        // 发布登录成功事件，记录日志
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(Constants.LOGIN_SUCCESS);
        logininforEvent.setMessage(MessageUtils.message("user.login.success"));
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
        
        // 更新用户最后登录信息
        loginService.recordLoginInfo((Long) loginParameter.getExtra(LoginHelper.USER_KEY), ip);
        log.info("user doLogin, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        // 清除在线用户缓存
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doLogout, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        // 清除在线用户缓存
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doKickout, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        // 清除在线用户缓存
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doReplaced, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
    }

    /**
     * 每次打开二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
    }

    /**
     * 每次Token续期时触发
     */
    @Override
    public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {
    }
}
