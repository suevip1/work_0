package com.dtstack.engine.master.thread;

import cn.hutool.core.lang.Assert;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yuebai
 * @date 2023/4/20
 */
public class DelegateThreadPoolExecutor extends AbstractExecutorService {
    protected final ThreadPoolExecutor runExecutorService;


    DelegateThreadPoolExecutor(ThreadPoolExecutor executor) {
        Assert.notNull(executor, "executor must be not null !");
        runExecutorService = executor;
    }

    @Override
    public void execute(Runnable command) {
        runExecutorService.execute(command);
    }

    @Override
    public void shutdown() {
        runExecutorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return runExecutorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return runExecutorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return runExecutorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return runExecutorService.awaitTermination(timeout, unit);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return runExecutorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return runExecutorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return runExecutorService.submit(task, result);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return runExecutorService.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return runExecutorService.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return runExecutorService.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return runExecutorService.invokeAny(tasks, timeout, unit);
    }
}
