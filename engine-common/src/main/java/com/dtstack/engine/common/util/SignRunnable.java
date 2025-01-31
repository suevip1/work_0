package com.dtstack.engine.common.util;

public abstract class SignRunnable<T> implements Runnable {
    private T sign;

    public T getSign() {
        return sign;
    }

    public SignRunnable(T sign) {
        this.sign = sign;
    }

}