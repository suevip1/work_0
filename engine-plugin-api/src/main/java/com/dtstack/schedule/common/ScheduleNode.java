package com.dtstack.schedule.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduleNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleNode.class);

    private static final ScheduleNode INSTANCE = new ScheduleNode();

    public static ScheduleNode getInstance() {
        return INSTANCE;
    }

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private LockService lockService;

    private ScheduleNode() {
    }

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    public void finishInit() {
        this.initialized.compareAndSet(false, true);
        LOGGER.info("ScheduleNode finishInit , initialized:{}",initialized.get());
    }

    /**
     *
     * @param lockName lockName
     * @param time timeout
     * @param timeUnit the unit of timeout
     * @return true for success, otherwise false
     * @throws LockServiceException Lock service error
     */
    public boolean tryLock(String lockName, int time, TimeUnit timeUnit) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        boolean tryLock = lockService.tryLock(lockName, time, timeUnit);
        if (tryLock) {
            LOGGER.info("{} get lock time:{}",lockName,System.currentTimeMillis());
        }
        return tryLock;
    }

    /**
     *
     * @param lockName lockName
     */
    public void release(String lockName) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        lockService.release(lockName);
        LOGGER.info("{} release lock time:{}",lockName,System.currentTimeMillis());
    }

    /**
     *
     * @param lockName lockName
     * @param runnable run when locked
     * @throws LockServiceException Lock service error
     * @throws LockTimeoutException Failed to get lock
     */
    public void execWithLock(String lockName, Runnable runnable) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        LOGGER.info("{} execWithLock lock time:{}",lockName,System.currentTimeMillis());
        lockService.execWithLock(lockName, runnable);
    }

}
