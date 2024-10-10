package com.dtstack.engine.master.vo;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ScheduleTaskTimeVO {

    @NotNull(message = "time is not null")
    private List<String> time;
    @NotNull(message = "taskId is not null")
    private Long taskId;
    @NotNull(message = "appType is not null")
    private Integer appType;

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

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
}
