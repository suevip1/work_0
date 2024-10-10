package com.dtstack.engine.common.enums;

/**
 * @author yuebai
 * @date 2021-07-06
 */
public enum OperatorType {
    STOP(0),
    RESTART(1),
    FILL_DATA(2),
    EVENT(3),
    MANUAL(4),
    TIMEOUT_STOP(5),
    // 从控制台杀的任务
    CONSOLE_STOP(-1)
    ;


    OperatorType(Integer type) {
        this.type = type;
    }

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
