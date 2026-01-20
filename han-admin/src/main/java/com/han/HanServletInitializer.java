package com.han;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: Web 容器中进行部署
 */
public class HanServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HanApplication.class);
    }
}
