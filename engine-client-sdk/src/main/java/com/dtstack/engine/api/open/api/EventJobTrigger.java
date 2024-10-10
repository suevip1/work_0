package com.dtstack.engine.api.open.api;

public class EventJobTrigger {

    /**
     * 外部任务名称
     */
    private String triggerJob;

    /**
     * 外部任务计划时间
     */
    private String triggerJobTime;

    /**
     * 手动任务操作名称
     */
    private String manuallyTriggerBatchName;


    /**
     * 外部任务状态
     */
    private Integer triggerJobStatus;

    /**
     * 触发数栈任务id
     */
    private String eventJobId;

    /**
     * 触发的任务应用
     */
    private Integer appType;

    public String getManuallyTriggerBatchName() {
        return manuallyTriggerBatchName;
    }

    public void setManuallyTriggerBatchName(String manuallyTriggerBatchName) {
        this.manuallyTriggerBatchName = manuallyTriggerBatchName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getTriggerJob() {
        return triggerJob;
    }

    public void setTriggerJob(String triggerJob) {
        this.triggerJob = triggerJob;
    }

    public String getTriggerJobTime() {
        return triggerJobTime;
    }

    public void setTriggerJobTime(String triggerJobTime) {
        this.triggerJobTime = triggerJobTime;
    }

    public Integer getTriggerJobStatus() {
        return triggerJobStatus;
    }

    public void setTriggerJobStatus(Integer triggerJobStatus) {
        this.triggerJobStatus = triggerJobStatus;
    }

    public String getEventJobId() {
        return eventJobId;
    }

    public void setEventJobId(String eventJobId) {
        this.eventJobId = eventJobId;
    }
}
