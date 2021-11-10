package com.ieye.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {

    @Value("${executor.corePoolSize}")
    private int corePoolSize;

    @Value("${executor.maxThreadPoolSize}")
    private int maxThreadPoolSize;

    @Value("${executor.queueSize}")
    private int queueSize;

    @Value("${executor.keepAliveTimeInSeconds}")
    private int keepAliveTimeInSeconds;

    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueSize);
        executor.setMaxPoolSize(maxThreadPoolSize);
        executor.setKeepAliveSeconds(keepAliveTimeInSeconds);
        executor.initialize();
        return executor;
    }

}
