package com.dtstack.engine.common.enums;

/**
 * @author yuebai
 * @date 2022/8/30
 */
public enum ETaskGroupEnum {

    NORMAL_SCHEDULE(0, "调度任务"), MANUAL(1, "手动任务");

    private Integer type;

    private String desc;

    ETaskGroupEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
