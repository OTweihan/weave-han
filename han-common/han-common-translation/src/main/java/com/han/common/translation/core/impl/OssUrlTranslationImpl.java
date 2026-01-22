package com.han.common.translation.core.impl;

import lombok.AllArgsConstructor;
import com.han.common.core.service.OssService;
import com.han.common.translation.annotation.TranslationType;
import com.han.common.translation.constant.TransConstant;
import com.han.common.translation.core.TranslationInterface;
import org.springframework.stereotype.Component;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: OSS 翻译实现
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
public class OssUrlTranslationImpl implements TranslationInterface<String> {

    private final OssService ossService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof String ids) {
            return ossService.selectUrlByIds(ids);
        } else if (key instanceof Long id) {
            return ossService.selectUrlByIds(id.toString());
        }
        return null;
    }
}
