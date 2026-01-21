package com.han.common.core.config;

import com.han.common.core.config.properties.ThreadPoolProperties;
import com.han.common.core.utils.SpringUtils;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.util.concurrent.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 全局线程池配置（主要提供定时任务专用线程池）
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(ThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "thread-pool", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ThreadPoolConfig {

    private final ThreadPoolProperties threadPoolProperties;
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * 创建定时任务专用线程池（ScheduledThreadPoolExecutor）
     * <p>主要用于 @Scheduled 定时任务、延迟任务、周期性清理、报表生成等场景</p>
     *
     * @return 配置完成的 ScheduledExecutorService Bean
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {
        int corePoolSize = threadPoolProperties.getCorePoolSize();
        // 如果未配置或配置不合法，则使用默认公式：CPU核心数 + 1
        if (corePoolSize <= 0) {
            corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
        }

        BasicThreadFactory.Builder builder = new BasicThreadFactory.Builder()
            // 设置为守护线程，应用退出时自动结束
            .daemon(true);

        // 根据当前环境是否支持虚拟线程（Java 21+）选择线程工厂
        if (SpringUtils.isVirtual()) {
            builder.namingPattern("virtual-schedule-pool-%d")
                .wrappedFactory(new VirtualThreadTaskExecutor().getVirtualThreadFactory());
        } else {
            builder.namingPattern("schedule-pool-%d");
        }

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            corePoolSize,
            builder.build(),
            // 拒绝策略：调用者运行，防止任务丢失
            new ThreadPoolExecutor.CallerRunsPolicy()
        ) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                // 统一捕获并记录任务执行中的异常
                printException(r, t);
            }
        };

        this.scheduledExecutorService = executor;
        return executor;
    }

    /**
     * 应用关闭时优雅销毁线程池
     * <p>关闭流程：</p>
     * <ol>
     *     <li>调用 shutdown()：停止接受新任务，尝试完成已提交任务</li>
     *     <li>等待配置的时间（默认120秒），若未完成则调用 shutdownNow() 强制中断</li>
     *     <li>再次等待配置的时间，若仍未终止则记录警告日志</li>
     *     <li>处理中断异常，恢复线程中断状态</li>
     * </ol>
     */
    @PreDestroy
    public void destroy() {
        try {
            log.info("==== 开始关闭定时任务线程池 ====");

            if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
                return;
            }

            scheduledExecutorService.shutdown();

            int awaitSeconds = threadPoolProperties.getShutdownAwaitSeconds();
            if (!scheduledExecutorService.awaitTermination(awaitSeconds, TimeUnit.SECONDS)) {
                log.warn("定时任务线程池 {} 秒内未正常关闭，尝试强制终止", awaitSeconds);
                scheduledExecutorService.shutdownNow();

                if (!scheduledExecutorService.awaitTermination(awaitSeconds, TimeUnit.SECONDS)) {
                    log.error("定时任务线程池强制终止后仍未结束，可能存在未完成任务");
                }
            }

            log.info("==== 定时任务线程池已关闭 ====");
        } catch (InterruptedException e) {
            // 恢复中断状态
            Thread.currentThread().interrupt();
            log.error("关闭定时任务线程池时被中断", e);
            // 尝试强制关闭
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (Exception e) {
            log.error("关闭定时任务线程池发生异常", e);
        }
    }

    /**
     * 统一处理线程池任务执行中的异常
     * <p>特别处理 Future 任务中被包装的异常（如 ExecutionException）</p>
     *
     * @param r 已执行的 Runnable 任务
     * @param t 执行过程中抛出的异常（可能为 null）
     */
    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?> future) {
            try {
                if (future.isDone()) {
                    // 主动获取结果以触发异常
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        if (t != null) {
            log.error("定时任务执行异常：{}", t.getMessage(), t);
        }
    }
}
