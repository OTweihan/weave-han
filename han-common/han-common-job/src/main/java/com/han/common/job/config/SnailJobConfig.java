package com.han.common.job.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.aizuda.snailjob.client.common.appender.SnailLogbackAppender;
import com.aizuda.snailjob.client.common.event.SnailClientStartingEvent;
import com.aizuda.snailjob.client.starter.EnableSnailJob;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: opensnail
 * @CreateTime: 2024-05-17
 * @Description: 启动定时任务
 */
@EnableSnailJob
@EnableScheduling
@AutoConfiguration
@ConditionalOnProperty(prefix = "snail-job", name = "enabled", havingValue = "true")
public class SnailJobConfig {

    @EventListener(SnailClientStartingEvent.class)
    public void onStarting(SnailClientStartingEvent event) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (!(loggerFactory instanceof LoggerContext lc)) {
            return;
        }
        Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);

        // 防止重复添加
        if (rootLogger.getAppender("snail_log_appender") != null) {
            return;
        }

        SnailLogbackAppender<ILoggingEvent> ca = new SnailLogbackAppender<>();
        ca.setName("snail_log_appender");
        ca.setContext(lc);
        ca.start();
        rootLogger.addAppender(ca);
    }
}
