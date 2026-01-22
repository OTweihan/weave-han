package com.han.common.encrypt.interceptor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import com.han.common.encrypt.core.EncryptorManager;

import java.sql.Statement;
import java.util.*;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 出参解密拦截器
 */
@Slf4j
@AllArgsConstructor
@Intercepts({@Signature(
    type = ResultSetHandler.class,
    method = "handleResultSets",
    args = {Statement.class})
})
public class MybatisDecryptInterceptor implements Interceptor {

    private final EncryptorManager encryptorManager;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取执行mysql执行结果
        Object result = invocation.proceed();
        if (result == null) {
            return null;
        }
        this.encryptorManager.decryptObject(result);
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
