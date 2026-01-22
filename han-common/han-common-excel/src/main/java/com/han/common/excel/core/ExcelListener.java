package com.han.common.excel.core;

import cn.idev.excel.read.listener.ReadListener;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: Excel 导入监听
 */
public interface ExcelListener<T> extends ReadListener<T> {

    ExcelResult<T> getExcelResult();
}
