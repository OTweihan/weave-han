package com.han.common.encrypt.core.encryptor;

import com.han.common.encrypt.core.EncryptContext;
import com.han.common.encrypt.enumd.AlgorithmType;
import com.han.common.encrypt.enumd.EncodeType;
import com.han.common.encrypt.utils.EncryptUtils;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: sm4 算法实现
 */
public class Sm4Encryptor extends AbstractEncryptor {

    private final EncryptContext context;

    public Sm4Encryptor(EncryptContext context) {
        super(context);
        this.context = context;
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.SM4;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (encodeType == EncodeType.HEX) {
            return EncryptUtils.encryptBySm4Hex(value, context.getPassword());
        } else {
            return EncryptUtils.encryptBySm4(value, context.getPassword());
        }
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptBySm4(value, context.getPassword());
    }
}
