package com.han.common.translation.core.impl;

import lombok.AllArgsConstructor;
import com.han.common.core.service.FileService;
import com.han.common.translation.annotation.TranslationType;
import com.han.common.translation.constant.TransConstant;
import com.han.common.translation.core.TranslationInterface;
import org.springframework.stereotype.Component;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 文件 翻译实现
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
public class OssUrlTranslationImpl implements TranslationInterface<String> {

    private final FileService fileService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof String ids) {
            return fileService.selectUrlByIds(ids);
        } else if (key instanceof Long id) {
            return fileService.selectUrlByIds(id.toString());
        }
        return null;
    }
}
