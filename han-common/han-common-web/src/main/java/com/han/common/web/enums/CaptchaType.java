package com.han.common.web.enums;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 验证码类型
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {

    /**
     * 数字
     */
    MATH(MathGenerator.class),

    /**
     * 字符
     */
    CHAR(RandomGenerator.class);

    /**
     * 验证码生成器
     */
    private final Class<? extends CodeGenerator> clazz;
}
