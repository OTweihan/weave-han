package com.han.common.encrypt.core.encryptor;

import com.han.common.encrypt.core.EncryptContext;
import com.han.common.encrypt.core.IEncryptor;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 所有加密执行者的基类
 */
public abstract class AbstractEncryptor implements IEncryptor {

    public AbstractEncryptor(EncryptContext context) {
        // 用户配置校验与配置注入
    }
}
