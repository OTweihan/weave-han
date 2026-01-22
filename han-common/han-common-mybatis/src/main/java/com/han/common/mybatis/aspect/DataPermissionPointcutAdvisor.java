package com.han.common.mybatis.aspect;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

/**
 * @Author: 秋辞未寒
 * @CreateTime: 2026-01-22
 * @Description: 数据权限注解切面定义
 */
@SuppressWarnings("all")
public class DataPermissionPointcutAdvisor extends AbstractPointcutAdvisor {

    private final Advice advice;
    private final Pointcut pointcut;

    public DataPermissionPointcutAdvisor() {
        this.advice = new DataPermissionAdvice();
        this.pointcut =  new DataPermissionPointcut();
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
