package com.dtstack.engine.master.component;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 基于 redisson 的延迟队列
 *
 * @param <T> 延时消息
 * @param <R> 执行完延迟消息的业务后的返回数据
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:17
 */
public abstract class BaseDelayQueue<T, R> {
    protected static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    private volatile boolean flag = true;

    /**
     * 最大重试次数
     */
    private static final int maxRetryCount = 30;

    /**
     * 设置最大延迟（毫秒）
     */
    private static final long maxDelay = 60000;

    /**
     * 退避因子
     */
    private static final double backoffFactor = 2.0;

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private ThreadPoolTaskExecutor mqConsumeExecutor;

    /**
     * 队列名称
     */
    protected abstract String queueName();

    /**
     * 延迟消息的业务逻辑
     */
    protected abstract R onMessage(T t) throws Exception;

    /**
     * 添加延迟对象
     */
    public void add(T t, long delay, TimeUnit timeUnit) {
        Assert.isTrue(delay >= 0, "延时时间必须大于等于0");
        Assert.isTrue(Objects.nonNull(timeUnit), "延时时间单位不能为空");
        // 实际监听的消费队列
        RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueName());
        // 中介队列
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.offer(t, delay, timeUnit);
    }

    /**
     * 添加延迟对象
     */
    public void add(T t, Duration duration) {
        long delay = duration.getSeconds();
        add(t, delay, TimeUnit.SECONDS);
    }

    /**
     * 移除延迟对象
     */
    public void remove(T t) {
        RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueName());
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.remove(t);
    }

    /**
     * 是否包含延迟对象
     *
     * @param t
     * @return
     */
    public boolean contains(T t) {
        RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueName());
        // 中介队列
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        return delayedQueue.contains(t);
    }

    /**
     * 此方法应当在重启服务时，主动调用一次，可以使服务重启后立即消费重启前未消费的过期消息
     */
    private void addNull2DelayedQueue() {
        this.add(null, Duration.ofSeconds(1L));
    }

    /**
     * 触发延迟队列的消费
     */
    public void triggerDelayedQueueConsumer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> flag = false));
        this.addNull2DelayedQueue();
        new Thread(() -> {
            int retryCount = 0;
            // 设置基本延迟（毫秒）
            long baseDelay = 1000;

            while (flag) {
                try {
                    RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueName());
                    T t = blockingQueue.take();
                    if (Objects.isNull(t)) {
                        continue;
                    }
                    log.info("receive delayed msg, queue: {}，msg: {}", queueName(), JSON.toJSONString(t));
                    CompletableFuture.supplyAsync(() -> {
                        R r = null;
                        try {
                            r = onMessage(t);
                        } catch (Exception e) {
                            log.error("consume error，queue: {}，msg: {}", queueName(), JSON.toJSONString(t), e);
                        }
                        return r;
                    }, mqConsumeExecutor);

                    // 如果操作成功，则重置重试计数
                    retryCount = 0;
                } catch (RedisException e) {
                    // 处理 Redis 异常
                    log.error("delayed queue consumer redis exception", e);

                    retryCount++;
                    if (retryCount > maxRetryCount) {
                        // 如果超过最大重试次数，则跳出循环
                        break;
                    }

                    long retryDelay = (long) (baseDelay * Math.pow(backoffFactor, retryCount - 1));
                    retryDelay = Math.min(retryDelay, maxDelay);
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ex) {
                        log.error("delayed queue consumer interrupted redis exception", ex);
                        Thread.currentThread().interrupt();
                        // 如果线程被中断，则跳出循环
                        break;
                    }
                } catch (Exception e) {
                    log.error("delayed queue consumer exception", e);
                    retryCount++;
                    if (retryCount > maxRetryCount) {
                        break; // 如果超过最大重试次数，则跳出循环
                    }

                    long retryDelay = (long) (baseDelay * Math.pow(backoffFactor, retryCount - 1));
                    retryDelay = Math.min(retryDelay, maxDelay);
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ex) {
                        log.error("delayed queue consumer interrupted exception", ex);
                        Thread.currentThread().interrupt();
                        // 如果线程被中断，则跳出循环
                        break;
                    }
                }
            }
        }, "delay-queue-receiver").start();
    }
}