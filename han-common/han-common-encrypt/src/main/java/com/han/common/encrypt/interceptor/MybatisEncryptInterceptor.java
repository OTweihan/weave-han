package com.han.common.encrypt.interceptor;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import com.han.common.encrypt.core.EncryptorManager;

import java.sql.PreparedStatement;
import java.util.*;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 入参加密拦截器
 */
@Slf4j
@AllArgsConstructor
@Intercepts({@Signature(
    type = ParameterHandler.class,
    method = "setParameters",
    args = {PreparedStatement.class})
})
public class MybatisEncryptInterceptor implements Interceptor {

    private final EncryptorManager encryptorManager;

    @Override
    public Object intercept(Invocation invocation) {
        return invocation;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler parameterHandler) {
            // 进行加密操作
            Object parameterObject = parameterHandler.getParameterObject();
            if (ObjectUtil.isNotNull(parameterObject) && !(parameterObject instanceof String)) {
                this.encryptorManager.encryptObject(parameterObject);
            }
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
