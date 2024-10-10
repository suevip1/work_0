package com.dtstack.engine.api.vo.job;

import java.util.Date;

/**
 * @author yuebai
 * @date 2022/11/9
 */
public class JobStopProcessVO {
    private Date stopTime;
    private Integer maxRetry;
    private Integer hasRetry;
    private Integer status;
    private String jobId;
    /**
     * 是否还会持续停止
     */
    private boolean continueStop;

    public boolean isContinueStop() {
        return continueStop;
    }

    public void setContinueStop(boolean continueStop) {
        this.continueStop = continueStop;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(Integer maxRetry) {
        this.maxRetry = maxRetry;
    }

    public Integer getHasRetry() {
        return hasRetry;
    }

    public void setHasRetry(Integer hasRetry) {
        this.hasRetry = hasRetry;
    }
}
