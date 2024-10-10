package com.dtstack.schedule.common;

/**
 * @author leon
 * @date 2023-05-04 10:22
 **/
public final class LockObjectProvider {

    private static final LockObjectProvider INSTANCE = new LockObjectProvider();

    private final Object KRB5_CONFIG_LOCK = new Object();

    private LockObjectProvider() {}

    public static LockObjectProvider getInstance() {
        return INSTANCE;
    }

    public static Object getKrb5ConfigLock() {
        return INSTANCE.KRB5_CONFIG_LOCK;
    }
}
