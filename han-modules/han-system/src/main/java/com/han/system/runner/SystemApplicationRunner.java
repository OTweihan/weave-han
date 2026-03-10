package com.han.system.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.system.service.ISysStorageConfigService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 初始化 system 模块对应业务数据
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SystemApplicationRunner implements ApplicationRunner {

    private final ISysStorageConfigService storageConfigService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        storageConfigService.init();
        log.info("初始化存储配置成功");
    }
}
