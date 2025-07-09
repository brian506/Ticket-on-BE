package com.ticketon.ticketon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);     // 동시에 실행될 최소 쓰레드 수
        executor.setMaxPoolSize(10);     // 최대 쓰레드 수
        executor.setQueueCapacity(100);  // 대기 큐
        executor.setThreadNamePrefix("AsyncFlush-");
        executor.initialize();
        return executor;
    }
}