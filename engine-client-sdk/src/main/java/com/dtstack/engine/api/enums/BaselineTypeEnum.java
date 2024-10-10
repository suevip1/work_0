package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2023/1/31 10:27 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum BaselineTypeEnum {
    SINGLE_BATCH(1,"单批次"),
    MANY_BATCH(2,"多批次"),
    ;

    private final Integer code;

    private final String msg;

    BaselineTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
