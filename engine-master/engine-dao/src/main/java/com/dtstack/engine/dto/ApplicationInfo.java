package com.dtstack.engine.dto;

import java.util.Date;

/**
 * @author yuebai
 * @date 2021-11-05
 */
public class ApplicationInfo {

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
    private  Date startTime;

    /**
     * 任务结束时间
     */
    private  Date finishTime;

    /**
     * flink或其它类型的任务ID
     */
    private String engineJobId;


    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
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
