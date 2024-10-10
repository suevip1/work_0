package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

public class ScheduleTaskCalender extends BaseEntity {

    private Long taskId;

    private Integer appType;

    private Long calenderId;

    private String expandTime;

    private Integer candlerBatchType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public String getExpandTime() {
        return expandTime;
    }

    public void setExpandTime(String expandTime) {
        this.expandTime = expandTime;
    }


    public Integer getCandlerBatchType() {
        return candlerBatchType;
    }

    public void setCandlerBatchType(Integer candlerBatchType) {
        this.candlerBatchType = candlerBatchType;
    }
}
