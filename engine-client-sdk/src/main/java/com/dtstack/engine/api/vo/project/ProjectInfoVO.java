package com.dtstack.engine.api.vo.project;


public class ProjectInfoVO {

    /**
     * 工程id
     * 必填
     */
    private Long projectId;

    /**
     * 应用类型
     * 必填
     */
    private Integer appType;

    /**
     * 告警总数
     */
    private int alarmCount;

    /**
     * 实例总数
     */
    private int totalJob;

    /**
     * 失败总数
     */
    private int failJob;

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

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public int getTotalJob() {
        return totalJob;
    }

    public void setTotalJob(int totalJob) {
        this.totalJob = totalJob;
    }

    public int getFailJob() {
        return failJob;
    }

    public void setFailJob(int failJob) {
        this.failJob = failJob;
    }
}
