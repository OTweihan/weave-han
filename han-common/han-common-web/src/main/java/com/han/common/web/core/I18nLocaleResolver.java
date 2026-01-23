package com.han.common.web.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 获取请求头国际化信息
 */
public class I18nLocaleResolver implements LocaleResolver {

    @Override
    public @NonNull Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String language = httpServletRequest.getHeader("content-language");
        Locale locale = Locale.getDefault();
        if (language != null && !language.isEmpty()) {
            String[] split = language.split("_");
            if (split.length > 1) {
                locale = Locale.of(split[0], split[1]);
            } else if (split.length == 1) {
                locale = Locale.of(split[0]);
            }
        }
        return locale;
    }

    @Override
    public void setLocale(@NonNull HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
    }
}
