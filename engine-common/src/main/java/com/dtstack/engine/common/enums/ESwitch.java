package com.dtstack.engine.common.enums;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-16 00:31
 */
public enum ESwitch {
    OFF(0),
    ON(1);
    private final Integer code;

    public Integer getCode() {
        return code;
    }

    ESwitch(Integer code) {
        this.code = code;
    }
}
