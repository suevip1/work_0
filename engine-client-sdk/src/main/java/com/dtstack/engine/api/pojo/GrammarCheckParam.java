package com.dtstack.engine.api.pojo;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-19 18:04
 */
public class GrammarCheckParam {
    private Integer taskType;

    private String engineType;

    private Integer computeType;

    private String sqlText;

    private String taskParams;

    private String componentVersion;

    private Long tenantId;

    private Long projectId;

    private Integer appType;

    private String taskId;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrammarCheckParam{");
        sb.append("taskType=").append(taskType);
        sb.append(", engineType='").append(engineType).append('\'');
        sb.append(", computeType=").append(computeType);
        sb.append(", sqlText='").append(sqlText).append('\'');
        sb.append(", taskParams='").append(taskParams).append('\'');
        sb.append(", componentVersion='").append(componentVersion).append('\'');
        sb.append(", tenantId=").append(tenantId);
        sb.append(", projectId=").append(projectId);
        sb.append(", appType=").append(appType);
        sb.append(", taskId='").append(taskId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
