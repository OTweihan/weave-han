package com.han.common.excel.annotation;

import com.han.common.core.utils.StringUtils;

import java.lang.annotation.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 字典格式化
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDictFormat {

    /**
     * 如果是字典类型，请设置字典的type值 (如: sys_user_sex)
     */
    String dictType() default "";

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    String readConverterExp() default "";

    /**
     * 分隔符，读取字符串组内容
     */
    String separator() default StringUtils.SEPARATOR;
}
