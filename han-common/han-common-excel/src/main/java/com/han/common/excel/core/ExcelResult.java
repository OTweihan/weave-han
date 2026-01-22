package com.han.common.excel.core;

import java.util.List;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: excel返回对象
 */
public interface ExcelResult<T> {

    /**
     * 对象列表
     */
    List<T> getList();

    /**
     * 错误列表
     */
    List<String> getErrorList();

    /**
     * 导入回执
     */
    String getAnalysis();
}
