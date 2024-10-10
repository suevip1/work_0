package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 5:00 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskKeyVO {

    private Long taskId;

    private Integer appType;

    private Integer deleted;

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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
