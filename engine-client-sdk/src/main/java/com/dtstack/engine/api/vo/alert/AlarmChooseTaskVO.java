package com.dtstack.engine.api.vo.alert;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlarmChooseTaskVO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 应用类型
     */
    private Integer appType;

    private Long ownerUserId;

    private String taskName;

    private Integer taskType;

    private Integer periodType;

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

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }
}
