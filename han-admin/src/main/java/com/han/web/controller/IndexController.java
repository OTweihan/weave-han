package com.han.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-16
 * @Description: 首页
 */
@SaIgnore
@RestController
@RequiredArgsConstructor
public class IndexController {

    /**
     * 访问首页，提示语
     */
    @GetMapping("/")
    public String index() {
        return StringUtils.format("欢迎使用{}后台管理框架，请通过前端地址访问。", SpringUtils.getApplicationName());
    }
}
