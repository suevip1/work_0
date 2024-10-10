package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 10:30 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class TaskViewElementVO {

    private Long taskId;

    private Integer appType;

    private String taskName;

    private Integer taskType;

    private String taskKey;

    private Integer taskRule;

    private Boolean existsOnRule;

    private Integer scheduleStatus;

    private Long projectId;

    private Long flowId;

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

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }

    public Boolean getExistsOnRule() {
        return existsOnRule;
    }

    public void setExistsOnRule(Boolean existsOnRule) {
        this.existsOnRule = existsOnRule;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
}
