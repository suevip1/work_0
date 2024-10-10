package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2023/5/29 8:25 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskInfoVO {

    private Long taskId;

    private Integer appType;

    private Integer deleted;

    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
