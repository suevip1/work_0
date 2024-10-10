package com.dtstack.engine.master.workflow.temporary;

import com.dtstack.engine.master.tx.CustomizedTransactionTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author leon
 * @date 2023-03-09 16:36
 **/
@Component
public class WorkflowLock {

    private static final String WORKFLOW_LOCK_KEY_PREFIX = "WORK_FLOW_LOCK_";

    private final WorkflowConfig workflowConfig;

    private final RedissonClient redissonClient;

    private final CustomizedTransactionTemplate customizedTransactionTemplate;

    public WorkflowLock(WorkflowConfig workflowConfig,
                        RedissonClient redissonClient,
                        CustomizedTransactionTemplate customizedTransactionTemplate) {
        this.workflowConfig = workflowConfig;
        this.redissonClient = redissonClient;
        this.customizedTransactionTemplate = customizedTransactionTemplate;
    }

    public void execWithLock(String flowJobId, Runnable runnable, Consumer<Throwable> exceptionConsumer) {
        execWithLock(flowJobId, runnable, exceptionConsumer, false);
    }

    public void execWithLock(String flowJobId, Runnable runnable, Consumer<Throwable> exceptionConsumer, boolean needTx) {
        final String lockKey = WORKFLOW_LOCK_KEY_PREFIX + flowJobId;
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean lock = rLock.tryLock(
                    workflowConfig.getWorkflowLockWaitTime(),
                    workflowConfig.getWorkflowLockLeaseTime(),
                    TimeUnit.SECONDS);
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

    public <T, R> R execWithLock(String flowJobId,
                                              T param,
                                              Function<T, R> function,
                                              Consumer<Throwable> exceptionConsumer,
                                              Supplier<R> defaultSupplier,
                                              boolean needTx) {
        final String lockKey = WORKFLOW_LOCK_KEY_PREFIX + flowJobId;
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean lock = rLock.tryLock(
                    workflowConfig.getWorkflowLockWaitTime(),
                    workflowConfig.getWorkflowLockLeaseTime(),
                    TimeUnit.SECONDS);

            if (lock) {
                if (needTx) {
                    return customizedTransactionTemplate.execute(function, param);
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
