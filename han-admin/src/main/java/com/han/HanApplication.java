package com.han;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 启动程序
 */
@SpringBootApplication
public class HanApplication {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        SpringApplication.run(HanApplication.class, args);

        long costMillis = System.currentTimeMillis() - startTime;
        double costSeconds = costMillis / 1000.0;

        System.out.printf("\n========================== Weave-Han 启动完成！耗时 %.2f 秒 ==========================\n\n", costSeconds);
    }
}
