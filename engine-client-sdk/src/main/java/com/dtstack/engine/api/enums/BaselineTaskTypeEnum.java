package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/5/12 4:46 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum BaselineTaskTypeEnum {
    CHOOSE_TASK(0,"选择基线任务"), CHAIN_TASK(1, "链路任务"),;

    private final Integer code;

    private final String msg;

    BaselineTaskTypeEnum(Integer code, String msg) {
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
