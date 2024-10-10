package com.dtstack.engine.dto;

/**
 * @author leon
 * @date 2022-09-09 14:07
 **/
public class GroupOverviewDTO {

    private String jobResource;

    private Integer stage;

    private Integer jobSize;

    private long generateTime;

    private String waitTime;

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Integer getJobSize() {
        return jobSize;
    }

    public void setJobSize(Integer jobSize) {
        this.jobSize = jobSize;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }
}
