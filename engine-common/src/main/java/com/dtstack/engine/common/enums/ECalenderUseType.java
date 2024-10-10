package com.dtstack.engine.common.enums;

import com.dtstack.schedule.common.enums.EParamType;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-14 14:45
 * 自定义调度日历用处
 */
public enum ECalenderUseType {


    /**
     * 基于计划时间的全局参数 {@link EParamType#GLOBAL_PARAM_BASE_CYCTIME}
     */
    GLOBAL_PARAM_BASE_CYCTIME(0),

    /**
     * 基于时间的全局参数 {@link EParamType#GLOBAL_PARAM_BASE_TIME}
     */
    GLOBAL_PARAM_BASE_TIME(1),

    /**
     * 自定义调度日期
     */
    CALENDER(2);

    private Integer type;

    public Integer getType() {
        return type;
    }

    ECalenderUseType(Integer type) {
        this.type = type;
    }
}
