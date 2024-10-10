package com.dtstack.engine.master.dto;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 7:46 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskTaskShadeDTO {

    private String taskKey;

    private Integer scheduleStatus;

    private String parentTaskKey;

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getParentTaskKey() {
        return parentTaskKey;
    }

    public void setParentTaskKey(String parentTaskKey) {
        this.parentTaskKey = parentTaskKey;
    }
}
