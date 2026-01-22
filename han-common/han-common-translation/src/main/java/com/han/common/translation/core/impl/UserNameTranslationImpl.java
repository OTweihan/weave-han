package com.han.common.translation.core.impl;

import lombok.AllArgsConstructor;
import com.han.common.core.service.UserService;
import com.han.common.translation.annotation.TranslationType;
import com.han.common.translation.constant.TransConstant;
import com.han.common.translation.core.TranslationInterface;
import org.springframework.stereotype.Component;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 用户名翻译实现
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    private final UserService userService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof Long id) {
            return userService.selectUserNameById(id);
        }
        return null;
    }
}
