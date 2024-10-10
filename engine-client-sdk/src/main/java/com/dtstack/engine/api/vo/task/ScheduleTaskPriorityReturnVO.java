package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2023-07-04 14:18
 * @Email: dazhi@dtstack.com
 * @Description: ScheduleTaskPriorityReturnVO
 */
public class ScheduleTaskPriorityReturnVO {

    private Long taskId;

    private Integer appType;

    private Integer priority;

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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
