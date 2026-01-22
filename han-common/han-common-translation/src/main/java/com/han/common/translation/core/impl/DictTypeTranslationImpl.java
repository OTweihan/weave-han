package com.han.common.translation.core.impl;

import lombok.AllArgsConstructor;
import com.han.common.core.service.DictService;
import com.han.common.core.utils.StringUtils;
import com.han.common.translation.annotation.TranslationType;
import com.han.common.translation.constant.TransConstant;
import com.han.common.translation.core.TranslationInterface;
import org.springframework.stereotype.Component;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 字典翻译实现
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.DICT_TYPE_TO_LABEL)
public class DictTypeTranslationImpl implements TranslationInterface<String> {

    private final DictService dictService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof String dictValue && StringUtils.isNotBlank(other)) {
            return dictService.getDictLabel(other, dictValue);
        }
        return null;
    }
}
