package com.dtstack.engine.common;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockCallerTimeoutPolicy extends BlockCallerPolicy {
    /**
     * 超时时间，毫秒数
     */
    private long timeout;

    public BlockCallerTimeoutPolicy(long timeout) {
        this.timeout = timeout;
    }

    /**
     * block policy handler
     *
     * @param r
     * @param executor
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().offer(r, this.timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException var4) {
            throw new RejectedExecutionException("Unexpected InterruptedException", var4);
        }
    }
}