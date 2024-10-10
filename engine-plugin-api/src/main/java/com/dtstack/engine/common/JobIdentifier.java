package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.EDeployMode;

import java.io.Serializable;

/**
 * Company: www.dtstack.com
 * @author yuebai
 */

public class JobIdentifier implements Serializable {

    private String engineJobId;

    private String applicationId;

    private String taskId;

    private Long tenantId;

    private String engineType;

    /**
     * 默认 perjob
     */
    private Integer deployMode = EDeployMode.PERJOB.getType();

    private Long userId;

    private String pluginInfo;

    private Long timeout;

    private Boolean forceCancel = Boolean.FALSE;

    private String componentVersion;

    private String archiveFsDir;

    private Long resourceId;

    private Integer taskType;

    private Long projectId;

    private Integer appType;

    private String classArgs;

    private String taskParams;

    private JobIdentifier() {

    }

    public JobIdentifier(String engineJobId, String applicationId, String taskId, Long tenantId, String engineType, Integer deployMode, Long userId, Long resourceId, String pluginInfo,String componentVersion) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
        this.tenantId = tenantId;
        this.engineType = engineType;
        this.deployMode = deployMode;
        this.userId = userId;
        this.pluginInfo = pluginInfo;
        this.componentVersion = componentVersion;
        this.resourceId = resourceId;
    }

    public JobIdentifier(String engineJobId, String applicationId, String taskId, Boolean forceCancel){
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
        this.forceCancel = forceCancel;
    }

    public JobIdentifier(String engineJobId, String applicationId, String taskId){
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
    }

    public static JobIdentifier createInstance(String engineJobId, String applicationId, String taskId, Boolean forceCancel) {
        return new JobIdentifier(engineJobId, applicationId, taskId, forceCancel);
    }

    public static JobIdentifier createInstance(String engineJobId, String applicationId, String taskId) {
        return new JobIdentifier(engineJobId, applicationId, taskId);
    }

    public Boolean isForceCancel() {
        return forceCancel;
    }

    public void setForceCancel(Boolean forceCancel) {
        this.forceCancel = forceCancel;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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

    @Override
    public String toString() {
        return "JobIdentifier{" +
                "engineJobId='" + engineJobId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", tenantId=" + tenantId +
                ", engineType=" + engineType +
                ", deployMode=" + deployMode +
                ", userId=" + userId +
                ", timeout=" + timeout +
                '}';
    }

    public String getArchiveFsDir() {
        return archiveFsDir;
    }

    public void setArchiveFsDir(String archiveFsDir) {
        this.archiveFsDir = archiveFsDir;
    }

    public String getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(String classArgs) {
        this.classArgs = classArgs;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }
}
