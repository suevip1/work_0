package com.dtstack.engine.api.vo.job;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 10:30 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobViewElementVO {

    private Long id;

    private Long projectId;

    private Long taskId;

    private Integer appType;

    private String taskName;

    private Integer scheduleStatus;

    private Integer taskType;

    private Integer taskRule;

    private Boolean existsOnRule;

    private String jobId;

    private String jobKey;

    private Integer status;

    private String flowJobId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }
}
