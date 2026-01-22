package com.han.common.excel.annotation;

import com.han.common.excel.core.CellMergeStrategy;

import java.lang.annotation.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: excel 列单元格合并(合并列相同项)
 * 需搭配 {@link CellMergeStrategy} 策略使用
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CellMerge {

    /**
     * col index
     */
    int index() default -1;

    /**
     * 合并需要依赖的其他字段名称
     */
    String[] mergeBy() default {};
}
