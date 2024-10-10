package com.dtstack.engine.master.model.cluster.system.config;

import com.dtstack.engine.common.util.StringUtils;

/**
 * System config mapper exception.
 */
public class SystemConfigMapperException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Invalid system config: '{}'. {}";

    private final String configName;
    private final String message;

    public SystemConfigMapperException(String configName, String message) {
        this.configName = configName;
        this.message = message;
    }

    public SystemConfigMapperException(String configName, String message, Throwable cause) {
        this(configName, message);
        this.initCause(cause);
    }

    @Override
    public String getMessage() {
        return StringUtils.format(MESSAGE_TEMPLATE, this.configName, this.message);
    }

}
