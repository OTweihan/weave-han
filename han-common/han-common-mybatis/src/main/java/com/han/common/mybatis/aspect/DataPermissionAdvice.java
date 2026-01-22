package com.han.common.mybatis.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.han.common.mybatis.annotation.DataPermission;
import com.han.common.mybatis.helper.DataPermissionHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author: 秋辞未寒
 * @CreateTime: 2026-01-22
 * @Description: 数据权限注解 Advice
 */
@Slf4j
public class DataPermissionAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        // 设置权限注解
        DataPermissionHelper.setPermission(getDataPermissionAnnotation(target, method));
        try {
            // 执行代理方法
            return invocation.proceed();
        } finally {
            // 清除权限注解
            DataPermissionHelper.removePermission();
        }
    }

    /**
     * 获取数据权限注解
     */
    private DataPermission getDataPermissionAnnotation(Object target, Method method) {
        DataPermission dataPermission = method.getAnnotation(DataPermission.class);
        // 优先获取方法上的注解
        if (dataPermission != null) {
            return dataPermission;
        }
        // 方法上没有注解，则获取类上的注解
        Class<?> targetClass = target.getClass();
        // 如果是 JDK 动态代理，则获取真实的Class实例
        if (Proxy.isProxyClass(targetClass)) {
            targetClass = targetClass.getInterfaces()[0];
        }
        dataPermission = targetClass.getAnnotation(DataPermission.class);
        return dataPermission;
    }
}
