package com.dtstack.engine.master.listener;

import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 9:53 AM
 * @Email: dazhi@dtstack.com
 * @Description: JobGraphEvent用于发布JobGraph生成事件
 */
@Component
public class JobGraphEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphEvent.class);

    private final List<JobGraphListener> listeners;

    private ExecutorService executorService;

    @Autowired
    private EnvironmentContext env;

    public JobGraphEvent() {
        this.listeners = Lists.newArrayList();
    }

    /** 注册监听器 */
    public void registerEventListener(JobGraphListener listener) {
        listeners.add(listener);
        String threadName = this.getClass().getSimpleName() + "_"  + "startJobBuildProcessor";
        executorService = new ThreadPoolExecutor(env.getJobExecutorPoolCorePoolSize(), env.getJobExecutorPoolMaximumPoolSize(), env.getJobExecutorPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(env.getJobExecutorPoolQueueSize()),
                new CustomThreadFactory(threadName), new BlockCallerPolicy());
    }

    public void successEvent(final String triggerDay) {
        for (JobGraphListener listener : listeners) {
            executorService.submit(()->{
                try {
                    listener.successEvent(triggerDay);
                } catch (Throwable e) {
                    LOGGER.error("event error: ",e);
                }
            });
        }
    }

}
