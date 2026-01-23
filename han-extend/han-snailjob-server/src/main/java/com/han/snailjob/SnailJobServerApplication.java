package com.han.snailjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: opensnail
 * @CreateTime: 2024-05-17
 * @Description: SnailJob Server 启动程序
 */
@SpringBootApplication
public class SnailJobServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.aizuda.snailjob.server.SnailJobServerApplication.class, args);
    }
}
