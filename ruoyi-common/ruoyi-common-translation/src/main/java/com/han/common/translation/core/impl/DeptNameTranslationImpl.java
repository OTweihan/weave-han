package com.han.common.translation.core.impl;

import lombok.AllArgsConstructor;
import com.han.common.core.service.DeptService;
import com.han.common.translation.annotation.TranslationType;
import com.han.common.translation.constant.TransConstant;
import com.han.common.translation.core.TranslationInterface;

/**
 * 部门翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.DEPT_ID_TO_NAME)
public class DeptNameTranslationImpl implements TranslationInterface<String> {

    private final DeptService deptService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof String ids) {
            return deptService.selectDeptNameByIds(ids);
        } else if (key instanceof Long id) {
            return deptService.selectDeptNameByIds(id.toString());
        }
        return null;
    }
}
