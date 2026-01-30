package com.han.web.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.han.common.core.utils.*;
import com.han.system.domain.vo.*;
import com.han.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import com.han.common.core.constant.CacheConstants;
import com.han.common.core.constant.Constants;
import com.han.common.core.domain.dto.RoleDTO;
import com.han.common.core.domain.model.LoginUser;
import com.han.common.core.enums.LoginType;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.exception.user.UserException;
import com.han.common.log.event.LogininforEvent;
import com.han.common.mybatis.helper.DataPermissionHelper;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.satoken.utils.LoginHelper;
import com.han.system.domain.SysUser;
import com.han.system.domain.bo.SysSocialBo;
import com.han.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import cn.hutool.core.codec.Base64;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.domain.model.LoginBody;
import com.han.common.core.domain.model.SocialLoginBody;
import com.han.common.json.utils.JsonUtils;
import com.han.common.social.config.properties.SocialLoginConfigProperties;
import com.han.common.social.config.properties.SocialProperties;
import com.han.common.social.utils.SocialUtils;
import com.han.common.sse.dto.SseMessageDto;
import com.han.common.sse.utils.SseMessageUtils;
import com.han.web.domain.vo.LoginVo;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-16
 * @Description: 登录校验与认证核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginService {

    @Value("${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Value("${user.password.lockTime}")
    private Integer lockTime;

    private final ISysPermissionService permissionService;
    private final ISysSocialService sysSocialService;
    private final ISysRoleService roleService;
    private final SysUserMapper userMapper;
    private final ISysClientService clientService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final SocialProperties socialProperties;

    /**
     * 统一登录入口
     * <p>根据 grantType 动态选择认证策略完成登录流程</p>
     *
     * @param body 前端传递的完整登录请求体（JSON字符串）
     * @return 登录成功后的令牌信息
     * @throws ServiceException 客户端配置异常、授权类型不支持等
     */
    public LoginVo login(String body) {
        // 解析通用登录参数
        LoginBody loginBody = JsonUtils.parseObject(body, LoginBody.class);
        if (ObjectUtil.isNull(loginBody)) {
            throw new ServiceException(MessageUtils.message("auth.data.error"));
        }

        ValidatorUtils.validate(loginBody);

        String clientId = loginBody.getClientId();
        String grantType = loginBody.getGrantType();

        // 获取并校验客户端配置
        SysClientVo client = clientService.queryByClientId(clientId);
        if (ObjectUtil.isNull(client)) {
            throw new ServiceException(MessageUtils.message("auth.client.not.found"));
        }
        if (!StringUtils.contains(client.getGrantType(), grantType)) {
            log.info("客户端[{}]不支持授权类型：{}", clientId, grantType);
            throw new ServiceException(MessageUtils.message("auth.grant.type.error"));
        }
        if (!SystemConstants.NORMAL.equals(client.getStatus())) {
            throw new ServiceException(MessageUtils.message("auth.grant.type.blocked"));
        }

        // 通过策略模式执行具体登录逻辑
        LoginVo loginVo = IAuthStrategy.login(body, client, grantType);

        // 异步发送欢迎消息（延迟3秒）
        Long userId = LoginHelper.getUserId();
        scheduledExecutorService.schedule(() -> {
            SseMessageDto dto = new SseMessageDto();
            dto.setMessage("欢迎登录Weave-Han后台管理系统");
            dto.setUserIds(List.of(userId));
            SseMessageUtils.publishMessage(dto);
        }, 3, TimeUnit.SECONDS);

        return loginVo;
    }

    /**
     * 执行具体登录逻辑（构建上下文、生成Token）
     *
     * @param user   系统用户
     * @param client 客户端信息
     * @return 登录结果 LoginVo
     */
    public LoginVo processLogin(SysUserVo user, SysClientVo client) {
        // 构建登录用户对象（可根据实际需求扩展 LoginUser 字段）
        LoginUser loginUser = buildLoginUser(user);

        // 设置客户端相关信息
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());

        return login(loginUser, client);
    }

    /**
     * 执行登录并构建返回结果
     *
     * @param loginUser 登录用户对象
     * @param client    客户端信息
     * @return 登录结果 LoginVo
     */
    public LoginVo login(LoginUser loginUser, SysClientVo client) {
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
     * 根据指定字段加载用户并校验状态
     *
     * @param field 查询字段（如 SysUser::getUserAccount）
     * @param value 字段值
     * @return 用户信息
     */
    public SysUserVo loadUserByField(SFunction<SysUser, String> field, String value) {
        SysUserVo user = userMapper.selectVoOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(field, value)
        );

        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在。", value);
            throw new UserException("user.not.exists", value);
        }

        if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用。", value);
            throw new UserException("user.blocked", value);
        }

        return user;
    }

    /**
     * 获取第三方平台授权跳转URL
     *
     * @param source 第三方平台标识（gitee/github/wechat等）
     * @param domain 回调域名（用于state中携带）
     * @return 完整的授权跳转地址
     * @throws ServiceException 不支持的平台
     */
    public String authBinding(String source, String domain) {
        SocialLoginConfigProperties config = socialProperties.getType().get(source);
        if (ObjectUtil.isNull(config)) {
            throw new ServiceException(source + "平台账号暂不支持");
        }

        AuthRequest authRequest = SocialUtils.getAuthRequest(source, socialProperties);

        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("domain", domain);
        stateMap.put("state", AuthStateUtils.createState());

        String state = Base64.encode(JsonUtils.toJsonString(stateMap), StandardCharsets.UTF_8);
        return authRequest.authorize(state);
    }

    /**
     * 处理第三方登录回调（登录或绑定）
     *
     * @param loginBody 回调请求参数
     * @throws ServiceException 授权失败或绑定冲突
     */
    public void socialCallback(SocialLoginBody loginBody) {
        AuthResponse<AuthUser> response = SocialUtils.loginAuth(
            loginBody.getSource(),
            loginBody.getSocialCode(),
            loginBody.getSocialState(),
            socialProperties);

        if (!response.ok()) {
            throw new ServiceException(response.getMsg());
        }

        socialRegister(response.getData());
    }

    /**
     * 解除第三方账号绑定
     *
     * @param socialId 社交关系主键
     * @throws ServiceException 解绑失败
     */
    public void unlockSocial(Long socialId) {
        sysSocialService.deleteWithValidById(socialId);
    }

    /**
     * 第三方账号绑定/注册逻辑（加分布式锁防止并发）
     *
     * @param authUserData 第三方平台返回的用户信息
     * @throws ServiceException 该账号已被他人绑定 或 当前用户该平台已绑定
     */
    @Lock4j
    public void socialRegister(AuthUser authUserData) {
        String authId = authUserData.getSource() + authUserData.getUuid();

        SysSocialBo bo = BeanUtil.toBean(authUserData, SysSocialBo.class);
        BeanUtil.copyProperties(authUserData.getToken(), bo);

        Long currentUserId = LoginHelper.getUserId();
        bo.setUserId(currentUserId);
        bo.setAuthId(authId);
        bo.setOpenId(authUserData.getUuid());
        bo.setUserName(authUserData.getUsername());
        bo.setNickName(authUserData.getNickname());

        // 检查该第三方账号是否已被其他用户绑定
        if (CollUtil.isNotEmpty(sysSocialService.selectByAuthId(authId))) {
            throw new ServiceException("此三方账号已经被其他用户绑定");
        }

        // 查询当前用户是否已绑定该平台
        SysSocialBo query = new SysSocialBo();
        query.setUserId(currentUserId);
        query.setSource(bo.getSource());
        List<SysSocialVo> exists = sysSocialService.queryList(query);

        if (CollUtil.isEmpty(exists)) {
            // 新增绑定
            sysSocialService.insertByBo(bo);
        } else {
            // 更新已有绑定信息（通常不应走到这里，抛出提示）
            bo.setId(exists.getFirst().getId());
            sysSocialService.updateByBo(bo);
            throw new ServiceException("此平台账号已经被绑定");
        }
    }

    /**
     * 当前用户主动退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNull(loginUser)) {
                return;
            }
            recordLogininfor(loginUser.getUserAccount(), Constants.LOGOUT,
                MessageUtils.message("user.logout.success"));
        } catch (NotLoginException ignored) {
            // 未登录状态直接忽略
        } finally {
            try {
                StpUtil.logout();
            } catch (NotLoginException ignored) {
            }
        }
    }

    /**
     * 记录登录/登出/失败等日志（通过事件发布异步处理）
     *
     * @param username 用户名/手机号/邮箱等标识
     * @param status   状态（LOGIN_SUCCESS / LOGIN_FAIL / LOGOUT）
     * @param message  具体提示信息
     */
    public void recordLogininfor(String username, String status, String message) {
        LogininforEvent event = new LogininforEvent();
        event.setUsername(username);
        event.setStatus(status);
        event.setMessage(message);
        event.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(event);
    }

    /**
     * 根据系统用户信息构建完整的登录上下文对象
     *
     * @param user 系统用户视图对象
     * @return 包含权限、角色等完整信息的登录用户对象
     */
    public LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        Long userId = user.getUserId();

        loginUser.setUserId(userId);
        loginUser.setUserAccount(user.getUserAccount());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());

        // 加载权限与角色
        loginUser.setMenuPermission(permissionService.getMenuPermission(userId));
        loginUser.setRolePermission(permissionService.getRolePermission(userId));
        List<SysRoleVo> roles = roleService.selectRolesByUserId(userId);
        loginUser.setRoles(BeanUtil.copyToList(roles, RoleDTO.class));

        return loginUser;
    }

    /**
     * 更新用户最后登录时间与IP（忽略数据权限）
     *
     * @param userId 用户ID
     * @param ip     登录IP地址
     */
    public void recordLoginInfo(Long userId, String ip) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ip);
        sysUser.setLoginDate(DateUtils.getNowDate());
        sysUser.setUpdateBy(userId);

        DataPermissionHelper.ignore(() -> userMapper.updateById(sysUser));
    }

    /**
     * 登录密码/验证码等前置校验（包含错误次数限制与账号锁定逻辑）
     *
     * @param loginType 登录方式（PASSWORD / EMAIL / SMS / SOCIAL / XC）
     * @param username  用于缓存错误次数的标识（通常为用户名/手机号/邮箱）
     * @param supplier  实际校验逻辑（返回 true 表示校验失败）
     * @throws UserException 密码错误次数超限或账号被锁定
     */
    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);
        errorNumber = ObjectUtil.defaultIfNull(errorNumber, 0);

        // 已达最大错误次数，账号被锁定
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(username, loginFail,
                MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        // 执行具体校验（如密码比对、验证码比对等）
        if (supplier.get()) {
            errorNumber++;
            RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));

            if (errorNumber >= maxRetryCount) {
                recordLogininfor(username, loginFail,
                    MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                recordLogininfor(username, loginFail,
                    MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }

        // 校验通过，清空错误计数
        RedisUtils.deleteObject(errorKey);
    }
}
