package com.dtstack.engine.api.vo.action;

import java.util.Date;

/**
 * @author yuebai
 * @date 2021-11-05
 */
public class ApplicationVO {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务applicationId
     */
    private String applicationId;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date finishTime;

    /**
     * jobId
     */
    private String jobId;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
