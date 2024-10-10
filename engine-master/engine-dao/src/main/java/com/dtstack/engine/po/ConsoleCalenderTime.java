package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

public class ConsoleCalenderTime extends BaseEntity {

    @ApiModelProperty(notes = "调度日历id")
    private Long calenderId;
    @ApiModelProperty(notes = "调度时间")
    private String calenderTime;
    @ApiModelProperty(notes = "附加信息，例如:基于计划时间的全局参数对应的参数值")
    private String extraInfo;

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public String getCalenderTime() {
        return calenderTime;
    }

    public void setCalenderTime(String calenderTime) {
        this.calenderTime = calenderTime;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
