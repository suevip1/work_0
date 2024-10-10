package com.dtstack.engine.api.open.api;

public class EventJobResponse {


    /**
     * 触发任务描述 失败原因记录在此
     */
    private String eventJobStatusDesc;

    /**
     * 触发任务状态
     */
    private Integer eventJobStatus;

    /**
     * 触发数栈任务实例JobId
     */
    private String eventJobId;

    public String getEventJobStatusDesc() {
        return eventJobStatusDesc;
    }

    public void setEventJobStatusDesc(String eventJobStatusDesc) {
        this.eventJobStatusDesc = eventJobStatusDesc;
    }

    public Integer getEventJobStatus() {
        return eventJobStatus;
    }

    public void setEventJobStatus(Integer eventJobStatus) {
        this.eventJobStatus = eventJobStatus;
    }

    public String getEventJobId() {
        return eventJobId;
    }

    public void setEventJobId(String eventJobId) {
        this.eventJobId = eventJobId;
    }
}
