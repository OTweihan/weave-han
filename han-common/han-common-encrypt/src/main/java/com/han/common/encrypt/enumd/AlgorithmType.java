package com.han.common.encrypt.enumd;

import com.han.common.encrypt.core.encryptor.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 算法名称
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

    /**
     * 默认走yml配置
     */
    DEFAULT(null),

    /**
     * base64
     */
    BASE64(Base64Encryptor.class),

    /**
     * aes
     */
    AES(AesEncryptor.class),

    /**
     * rsa
     */
    RSA(RsaEncryptor.class),

    /**
     * sm2
     */
    SM2(Sm2Encryptor.class),

    /**
     * sm4
     */
    SM4(Sm4Encryptor.class);

    private final Class<? extends AbstractEncryptor> clazz;
}
