package com.han.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.domain.model.LoginUser;
import com.han.common.core.domain.model.SocialLoginBody;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.exception.user.UserException;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.satoken.utils.LoginHelper;
import com.han.common.social.config.properties.SocialProperties;
import com.han.common.social.utils.SocialUtils;
import com.han.system.domain.vo.SysClientVo;
import com.han.system.domain.vo.SysSocialVo;
import com.han.system.domain.vo.SysUserVo;
import com.han.system.mapper.SysUserMapper;
import com.han.system.service.ISysSocialService;
import com.han.web.domain.vo.LoginVo;
import com.han.web.service.IAuthStrategy;
import com.han.web.service.SysLoginService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: thiszhc is 三三
 * @CreateTime: 2026-01-20
 * @Description: 第三方授权策略
 */
@Slf4j
@RequiredArgsConstructor
@Service("social" + IAuthStrategy.BASE_NAME)
public class SocialAuthStrategy implements IAuthStrategy {

    private final SocialProperties socialProperties;
    private final ISysSocialService sysSocialService;
    private final SysUserMapper userMapper;
    private final SysLoginService loginService;

    /**
     * 执行第三方平台授权登录流程
     *
     * @param body   前端传递的 JSON 字符串（包含第三方登录必要参数：source、code、state 等）
     * @param client 客户端配置信息（包含 clientId、超时时间、设备类型等）
     * @return 登录成功后的令牌信息（LoginVo）
     * @throws ServiceException 当第三方授权失败或用户未绑定账号时抛出
     * @throws UserException    当关联的用户不存在或被禁用时抛出
     */
    @Override
    public LoginVo login(String body, SysClientVo client) {
        // 解析并校验第三方登录请求参数
        SocialLoginBody loginBody = JsonUtils.parseObject(body, SocialLoginBody.class);
        if (loginBody == null) {
            throw new IllegalArgumentException("第三方登录请求参数不能为空");
        }
        ValidatorUtils.validate(loginBody);

        // 调用第三方授权工具完成登录授权流程
        AuthResponse<AuthUser> response = SocialUtils.loginAuth(
            loginBody.getSource(),
            loginBody.getSocialCode(),
            loginBody.getSocialState(),
            socialProperties
        );

        // 判断第三方授权是否成功
        if (!response.ok()) {
            throw new ServiceException(response.getMsg());
        }

        AuthUser authUserData = response.getData();

        // 根据第三方平台标识 + 用户唯一ID 查询已绑定的系统账号
        List<SysSocialVo> list = sysSocialService.selectByAuthId(
            authUserData.getSource() + authUserData.getUuid()
        );

        // 未找到绑定记录，提示用户先绑定
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException("你还没有绑定第三方账号，绑定后才可以登录！");
        }

        // 取第一条绑定记录（通常设计上一个第三方账号只绑定一个系统用户）
        SysSocialVo social = list.getFirst();

        // 加载绑定的系统用户信息并进行状态检查
        SysUserVo user = loadUser(social.getUserId());

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
     * 根据用户ID加载用户信息，并进行存在性及状态检查
     *
     * @param userId 系统用户主键ID
     * @return 用户视图对象 SysUserVo
     * @throws UserException 当用户不存在或已被禁用时抛出
     */
    private SysUserVo loadUser(Long userId) {
        SysUserVo user = userMapper.selectVoById(userId);

        if (ObjectUtil.isNull(user)) {
            log.info("第三方登录关联的用户ID：{} 不存在", userId);
            throw new UserException("user.not.exists");
        }

        if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("第三方登录关联的用户ID：{} 已被停用", userId);
            throw new UserException("user.blocked");
        }

        return user;
    }
}
