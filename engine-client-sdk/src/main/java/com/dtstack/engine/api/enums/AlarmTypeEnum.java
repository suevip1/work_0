package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/5/19 2:34 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlarmTypeEnum {
    TASK(0, "周期任务"), BASELINE(1, "基线"), MANUAL_TASK(2, "手动任务");

    private final String name;

    private final Integer code;

    AlarmTypeEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public static boolean isTask(int code) {
        return TASK.getCode() == code || MANUAL_TASK.getCode() == code;
    }
}
