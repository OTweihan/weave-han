package com.han;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 启动程序
 *
 * @author Lion Li
 */
@SpringBootApplication
public class HanApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HanApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);

        System.out.println("================================== Weave-Han 启动成功！==================================");
    }
}
