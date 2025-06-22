package com.holidayproject.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncExecutorConfig {

    public static final int CORE_POOL_SIZE = 8;
    public static final int MAX_POOL_SIZE = 16;
    public static final int QUEUE_CAPACITY = 100;
    public static final int KEEP_ALIVE_SECONDS = 30;
    public static final String THREAD_NAME = "holiday-task-";

    @Bean(name = "holidayExecutor")
    public ThreadPoolTaskExecutor holidayExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.initialize();
        return executor;
    }
}
