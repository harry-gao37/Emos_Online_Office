package com.yifu.emos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @auther YIFU GAO
 * @date 2023/01/07/22:04
 * File Info:
 */
@Configuration
public class ThreadPoolConfig {
    @Bean("AsyncTaskExecutor")
    public AsyncTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(32);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;

    }
}
