package com.han.web.service;

import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.SpringUtils;
import com.han.system.domain.vo.SysClientVo;
import com.han.web.domain.vo.LoginVo;

/**
 * @Author: Michelle.Chung
 * @CreateTime: 2026-01-20
 * @Description: 授权策略
 */
public interface IAuthStrategy {

    /**
     * 策略 Bean 名称后缀
     * <p>各具体策略实现类应使用 "类型 + BASE_NAME" 作为 Spring Bean 名称，例如：</p>
     * <ul>
     *     <li>passwordAuthStrategy</li>
     *     <li>emailAuthStrategy</li>
     *     <li>smsAuthStrategy</li>
     *     <li>socialAuthStrategy</li>
     *     <li>xcxAuthStrategy</li>
     * </ul>
     */
    String BASE_NAME = "AuthStrategy";

    /**
     * 根据授权类型动态获取对应的策略实现并执行登录
     * <p>此方法为静态工厂方法，用于统一入口调用不同登录策略。</p>
     *
     * @param body      前端传递的登录请求体（JSON 格式字符串，具体结构由 grantType 决定）
     * @param client    当前客户端配置信息（包含 clientId、超时时间、设备类型等）
     * @param grantType 授权类型（password / email / sms / social / xcx 等）
     * @return 登录成功后的令牌信息
     * @throws ServiceException 当授权类型不存在或对应的策略 Bean 未注册时抛出
     */
    static LoginVo login(String body, SysClientVo client, String grantType) {
        // 构造 Spring Bean 名称：授权类型 + 固定后缀
        String beanName = grantType + BASE_NAME;

        // 检查该类型的策略是否已注册为 Spring Bean
        if (!SpringUtils.containsBean(beanName)) {
            throw new ServiceException("授权类型不正确！");
        }

        // 获取对应的策略实现实例并执行登录
        IAuthStrategy instance = SpringUtils.getBean(beanName);
        return instance.login(body, client);
    }

    /**
     * 执行具体授权类型的登录逻辑
     * <p>各实现类根据不同的认证方式（密码、邮箱验证码、短信验证码、第三方登录、小程序登录等）
     * 完成用户身份验证、令牌生成等操作。</p>
     *
     * @param body   前端传递的登录请求体（JSON 格式字符串，结构由具体策略决定）
     * @param client 当前客户端配置信息（包含 clientId、超时时间、设备类型等）
     * @return 登录成功后的令牌信息（包含 accessToken、过期时间等）
     */
    LoginVo login(String body, SysClientVo client);
}
