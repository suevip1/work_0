package com.dtstack.engine.api.vo.calender;

import io.swagger.annotations.ApiModelProperty;

public class TaskCalenderVO {
    @ApiModelProperty(notes = "日历名称")
    private String calenderName;
    @ApiModelProperty(notes = "日历id")
    private Long calenderId;
    @ApiModelProperty(notes = "任务id")
    private Long taskId;
    private Long appType;

    public String getCalenderName() {
        return calenderName;
    }

    public void setCalenderName(String calenderName) {
        this.calenderName = calenderName;
    }

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getAppType() {
        return appType;
    }

    public void setAppType(Long appType) {
        this.appType = appType;
    }
}
