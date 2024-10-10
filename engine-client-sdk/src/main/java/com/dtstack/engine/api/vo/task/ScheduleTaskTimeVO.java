package com.dtstack.engine.api.vo.task;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

public class ScheduleTaskTimeVO extends BaseEntity {

    private Long taskId;

    private Integer appType;

    @ApiModelProperty(notes = "任务设置调度时间")
    private String scheduleTime;

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

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
}
