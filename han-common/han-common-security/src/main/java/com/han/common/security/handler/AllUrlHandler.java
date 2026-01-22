package com.han.common.security.handler;

import cn.hutool.core.util.ReUtil;
import lombok.Data;
import com.han.common.core.utils.SpringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 获取所有 Url 配置
 */
@Data
public class AllUrlHandler implements InitializingBean {

    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private List<String> urls = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        Set<String> set = new HashSet<>();
        RequestMappingHandlerMapping mapping = SpringUtils.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        map.keySet().forEach(info -> {
            // 获取注解上边的 path 替代 path variable 为 *
            if (info.getPathPatternsCondition() != null) {
                Objects.requireNonNull(info.getPathPatternsCondition().getPatterns())
                        .forEach(url -> set.add(ReUtil.replaceAll(url.getPatternString(), PATTERN, "*")));
            }
        });
        urls.addAll(set);
    }
}
