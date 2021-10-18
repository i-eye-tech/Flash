package com.flash.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
//@EnableAsync
public class ThreadConfig implements AsyncConfigurer {

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
        executor.setThreadNamePrefix("flash_task_executor");
        executor.initialize();
        return executor;
    }


}
