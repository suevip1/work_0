package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.config.ExecutorProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-10 20:16
 */
public class ExecutorUtil {

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(ExecutorProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(properties.getThreadName());
        String rejectPolicy = StringUtils.isEmpty(properties.getRejectedExecutionHandler()) ? StringUtils.EMPTY : properties.getRejectedExecutionHandler();
        switch (rejectPolicy) {
            case ExecutorProperties.ABORT_POLICY:
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
                break;
            case ExecutorProperties.CALLER_RUNS_POLICY:
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                break;
            case ExecutorProperties.DISCARD_OLDEST_POLICY:
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
                break;
            case ExecutorProperties.DISCARD_POLICY:
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
                break;
            case ExecutorProperties.BLOCK_CALLER_POLICY:
                executor.setRejectedExecutionHandler(new BlockCallerPolicy());
            default:
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
                break;
        }

        String threadFactory = StringUtils.isEmpty(properties.getThreadFactory()) ? StringUtils.EMPTY : properties.getThreadFactory();
        switch (threadFactory) {
            case ExecutorProperties.CUSTOM_THREAD_FACTORY:
                executor.setThreadFactory(new CustomThreadFactory(properties.getThreadName()));
                break;
            default:
                executor.setThreadFactory(new CustomThreadFactory(properties.getThreadName()));
                break;
        }
        // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的 Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

}