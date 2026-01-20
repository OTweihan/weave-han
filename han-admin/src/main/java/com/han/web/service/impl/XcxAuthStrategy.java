package com.han.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWechatMiniProgramRequest;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.domain.model.XcxLoginBody;
import com.han.common.core.domain.model.XcxLoginUser;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.satoken.utils.LoginHelper;
import com.han.system.domain.vo.SysClientVo;
import com.han.system.domain.vo.SysUserVo;
import com.han.web.domain.vo.LoginVo;
import com.han.web.service.IAuthStrategy;
import com.han.web.service.SysLoginService;
import org.springframework.stereotype.Service;

/**
 * @Author: Michelle.Chung
 * @CreateTime: 2026-01-20
 * @Description: 小程序认证策略（微信小程序登录）
 */
@Slf4j
@RequiredArgsConstructor
@Service("xcx" + IAuthStrategy.BASE_NAME)
public class XcxAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;

    /**
     * 执行微信小程序登录流程
     *
     * @param body   前端传递的 JSON 字符串（包含小程序 code 和 appid）
     * @param client 客户端配置信息（包含 clientId、超时时间、设备类型等）
     * @return 登录成功后的令牌信息（LoginVo）
     * @throws ServiceException 当微信登录凭证校验失败时抛出
     */
    @Override
    public LoginVo login(String body, SysClientVo client) {
        // 解析并校验小程序登录请求参数
        XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
        if (loginBody == null) {
            throw new IllegalArgumentException("小程序登录请求参数不能为空");
        }
        ValidatorUtils.validate(loginBody);

        // 小程序 wx.login 获取的 code
        String xcxCode = loginBody.getXcxCode();
        // 小程序 appid，用于区分多小程序场景
        String appid = loginBody.getAppid();

        // 构造微信小程序授权请求配置
        AuthRequest authRequest = new AuthWechatMiniProgramRequest(
            AuthConfig.builder()
                .clientId(appid)
                // 注意：clientSecret（小程序密钥）应从配置中心、安全配置或数据库动态获取
                // 此处仅为示例，实际项目中切勿硬编码
                .clientSecret("自行填写密钥 可根据不同appid填入不同密钥")
                .ignoreCheckRedirectUri(true)
                .ignoreCheckState(true)
                .build()
        );

        // 构造回调参数（微信小程序登录使用 code 模式）
        AuthCallback authCallback = new AuthCallback();
        authCallback.setCode(xcxCode);

        // 调用微信接口校验 code 并换取 openid / session_key / unionid
        AuthResponse<AuthUser> resp = authRequest.login(authCallback);

        String openid, unionId;
        if (resp.ok()) {
            AuthToken token = resp.getData().getToken();
            openid = token.getOpenId();
            // unionId 只有小程序绑定到微信开放平台后才会返回，否则为 null
            unionId = token.getUnionId();
        } else {
            throw new ServiceException("微信登录凭证校验失败：" + resp.getMsg());
        }

        // 根据 openid 查询系统用户（实际项目中应替换为真实查询逻辑）
        SysUserVo user = loadUserByOpenid(openid);

        // 小程序专用登录用户对象（继承或扩展 LoginUser，根据业务需要添加字段）
        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        loginUser.setOpenid(openid);
        // 可选：loginUser.setUnionId(unionId);  // 如需使用 unionId 可在此保存

        // 构造 Sa-Token 登录参数，支持不同客户端不同超时策略
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(client.getDeviceType());
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());

        // 执行登录，生成 token
        LoginHelper.login(loginUser, model);

        // 组装返回结果（小程序端通常需要额外返回 openid）
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        loginVo.setOpenid(openid);

        return loginVo;
    }

    /**
     * 根据 openid 查询绑定的系统用户信息
     * <p>
     * 注意：当前为占位实现，实际项目中应替换为真实查询逻辑
     * </p>
     *
     * @param openid 微信小程序用户唯一标识
     * @return 用户视图对象 SysUserVo
     */
    private SysUserVo loadUserByOpenid(String openid) {
        // todo 实际项目中应替换为真实查询逻辑，例如：
        // SysUserVo user = userService.selectUserByOpenid(openid);

        // 当前为演示用空对象，实际必须实现查询 + 未绑定处理逻辑
        SysUserVo user = new SysUserVo();

        if (ObjectUtil.isNull(user)) {
            log.info("微信小程序 openid：{} 未绑定系统用户", openid);
            // todo 未绑定用户时的业务处理，例如：
            // 1. 自动注册新用户
            // 2. 抛出异常要求前端引导绑定
            // 3. 返回临时访客身份等
            // 示例：throw new ServiceException("该微信尚未绑定系统账号，请先绑定");
        } else if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("微信小程序 openid：{} 绑定的用户已被停用", openid);
            // todo 用户被禁用时的处理逻辑
            // throw new UserException("user.blocked");
        }

        return user;
    }
}
