package com.dtstack.engine.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther: dazhi
 * @Date: 2024-01-22 17:41
 * @Email: dazhi@dtstack.com
 * @Description: AlterDiscardPolicy
 */
public class AlterDiscardPolicy implements RejectedExecutionHandler {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AlterDiscardPolicy.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread pool is EXHAUSTED!" +
                        " Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)!",
                e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        LOGGER.error(msg);
    }
}
