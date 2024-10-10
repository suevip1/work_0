package com.dtstack.engine.api.vo;

/**
 * @auther: shuxing
 * @date: 2022/3/15 13:44 周二
 * @email: shuxing@dtstack.com
 * @description:
 */
public class DependencyTaskVo {

    private Long id;
    private Long taskId;
    private String name;
    private Long createUserId;
    private Long taskType;

    public Long getTaskType() {
        return taskType;
    }

    public void setTaskType(Long taskType) {
        this.taskType = taskType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @Override
    public String toString() {
        return "DependencyTaskVo{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", name='" + name + '\'' +
                ", createUserId=" + createUserId +
                ", taskType=" + taskType +
                '}';
    }
}
