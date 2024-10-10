package com.dtstack.engine.master.component;

import com.dtstack.engine.master.tx.CustomizedTransactionTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分布式锁，参考 {@link com.dtstack.engine.master.workflow.temporary.WorkflowLock}
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-03 00:20
 */
@Component
public class DtRedissonLock {

    private final RedissonClient redissonClient;

    private final CustomizedTransactionTemplate customizedTransactionTemplate;

    public DtRedissonLock(RedissonClient redissonClient,
                          CustomizedTransactionTemplate customizedTransactionTemplate) {
        this.redissonClient = redissonClient;
        this.customizedTransactionTemplate = customizedTransactionTemplate;
    }

    public void execWithLock(final String lockKey, long lockWaitTime, long lockLeaseTime, TimeUnit unit,
                             Runnable runnable, Consumer<Throwable> exceptionConsumer, boolean needTx) {
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean lock = rLock.tryLock(
                    lockWaitTime,
                    lockLeaseTime,
                    unit);
            if (lock) {
                if (needTx) {
                    customizedTransactionTemplate.execute(runnable);
                } else {
                    runnable.run();
                }
            }
        } catch (Throwable e) {
            exceptionConsumer.accept(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    public <T, R> R execWithLock(final String lockKey, long lockWaitTime, long lockLeaseTime, TimeUnit unit,
                                 T param,
                                 Function<T, R> function,
                                 Consumer<Throwable> exceptionConsumer,
                                 Supplier<R> defaultSupplier,
                                 boolean needTx) {
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean lock = rLock.tryLock(
                    lockWaitTime,
                    lockLeaseTime,
                    unit);

            if (lock) {
                if (needTx) {
                    return customizedTransactionTemplate.executeWithThrow(function, param);
                } else {
                    return function.apply(param);
                }
            }
        } catch (Throwable e) {
            exceptionConsumer.accept(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        return defaultSupplier.get();
    }
}
