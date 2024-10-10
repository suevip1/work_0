package com.dtstack.engine.master.model.cluster.system.config;

class InnerException extends RuntimeException {
    public InnerException(String message) {
        super(message);
    }

    public InnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
