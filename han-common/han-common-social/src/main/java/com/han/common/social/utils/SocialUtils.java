package com.han.common.social.utils;

import cn.hutool.core.util.ObjectUtil;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import com.han.common.core.utils.SpringUtils;
import com.han.common.social.config.properties.SocialLoginConfigProperties;
import com.han.common.social.config.properties.SocialProperties;

/**
 * @Author: thiszhc
 * @CreateTime: 2026-01-26
 * @Description: 认证授权工具类
 */
public class SocialUtils  {

    private static final AuthRedisStateCache STATE_CACHE = SpringUtils.getBean(AuthRedisStateCache.class);

    public static AuthResponse<AuthUser> loginAuth(String source, String code, String state, SocialProperties socialProperties) throws AuthException {
        AuthRequest authRequest = getAuthRequest(source, socialProperties);
        AuthCallback callback = new AuthCallback();
        callback.setCode(code);
        callback.setState(state);
        return authRequest.login(callback);
    }

    public static AuthRequest getAuthRequest(String source, SocialProperties socialProperties) throws AuthException {
        SocialLoginConfigProperties obj = socialProperties.getType().get(source);
         if (ObjectUtil.isNull(obj)) {
            throw new AuthException("不支持的第三方登录类型");
        }
        AuthConfig.AuthConfigBuilder builder = AuthConfig.builder()
            .clientId(obj.getClientId())
            .clientSecret(obj.getClientSecret())
            .redirectUri(obj.getRedirectUri())
            .scopes(obj.getScopes());
        return switch (source.toLowerCase()) {
            case "qq" -> new AuthQqRequest(builder.build(), STATE_CACHE);
            case "wechat_open" -> new AuthWeChatOpenRequest(builder.build(), STATE_CACHE);
            default -> throw new AuthException("未获取到有效的 Auth 配置");
        };
    }
}
