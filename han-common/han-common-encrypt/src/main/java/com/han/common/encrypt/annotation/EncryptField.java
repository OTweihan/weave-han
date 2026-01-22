package com.han.common.encrypt.annotation;

import com.han.common.encrypt.enumd.AlgorithmType;
import com.han.common.encrypt.enumd.EncodeType;

import java.lang.annotation.*;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 字段加密注解
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    /**
     * 加密算法
     */
    AlgorithmType algorithm() default AlgorithmType.DEFAULT;

    /**
     * 秘钥。AES、SM4需要
     */
    String password() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String publicKey() default "";

    /**
     * 私钥。RSA、SM2需要
     */
    String privateKey() default "";

    /**
     * 编码方式。对加密算法为BASE64的不起作用
     */
    EncodeType encode() default EncodeType.DEFAULT;
}
