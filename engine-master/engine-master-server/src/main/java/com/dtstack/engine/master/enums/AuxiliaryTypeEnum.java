package com.dtstack.engine.master.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-13 20:56
 */
public enum AuxiliaryTypeEnum {

    /**
     * knox
     */
    KNOX(1) {
        @Override
        public String[] getConfigParams() {
            return PROXY_CONFIG_PARAM;
        }
    },

    /**
     * log trace
     */
    LOG_TRACE(2) {
        @Override
        public String[] getConfigParams() {
            return LOG_TRACE_CONFIG_PARAM;
        }
    };

    private final int type;

    public abstract String[] getConfigParams();

    private static final String[] PROXY_CONFIG_PARAM = {
            "url",
            "user",
            "password"
    };

    private static final String[] LOG_TRACE_CONFIG_PARAM = {
            "traceType",
            "path",
            "server"
    };

    AuxiliaryTypeEnum(int status){
        this.type = status;
    }

    public int getType() {
        return type;
    }

    public static AuxiliaryTypeEnum valueOfIgnoreCase(String value) {
        for (AuxiliaryTypeEnum auxiliaryTypeEnum : values()) {
            if (StringUtils.equalsIgnoreCase(auxiliaryTypeEnum.name(), value)) {
                return auxiliaryTypeEnum;
            }
        }
        return null;
    }
}
