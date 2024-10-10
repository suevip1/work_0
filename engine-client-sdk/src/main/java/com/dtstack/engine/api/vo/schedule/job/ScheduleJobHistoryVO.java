package com.dtstack.engine.api.vo.schedule.job;

import java.util.Date;

/**
 * @author yuebai
 * @date 2021-09-10
 */
public class ScheduleJobHistoryVO {
    /**
     * 平台类型
     */
    private Integer appType;

    /**
     * jobId
     */
    private String jobId;

    /**
     * 执行开始时间
     */
    private Date execStartTime;

    /**
     * 执行结束时间
     */
    private Date execEndTime;

    /**
     * yarn applicationId
     */
    private String applicationId;


    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Date execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Date getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Date execEndTime) {
        this.execEndTime = execEndTime;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
