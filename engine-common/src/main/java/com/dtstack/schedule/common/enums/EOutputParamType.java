package com.dtstack.schedule.common.enums;

/**
 * 任务输出参数类型枚举
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-14 20:16
 */
public enum EOutputParamType {
    CUSTOMIZE(1, "自定义参数"),
    CONS(2, "常量"),
    PROCESSED(3, "计算结果"),
    ;

    private Integer type;
    private String desc;

    EOutputParamType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static EOutputParamType getByType(Integer type) {
        if (type == null) {
            return null;
        }
        for (EOutputParamType oneEnum : EOutputParamType.values()) {
            if (oneEnum.getType().equals(type)) {
                return oneEnum;
            }
        }
        return null;
    }
}
