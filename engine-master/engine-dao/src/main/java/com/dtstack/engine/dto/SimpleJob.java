package com.dtstack.engine.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
@ApiModel
public class SimpleJob {

    private Long id;

    private String jobKey;

    private Integer status;

    /***
     * 任务调度时间 yyyymmddhhmmss
     */
    @ApiModelProperty(notes = "任务调度时间 yyyymmddhhmmss")
    private String cycTime;

    private Long taskId;

    private Integer taskType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        if(cycTime.length() != 14){
            this.cycTime = cycTime;
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(cycTime.substring(0, 4))
                .append("-")
                .append(cycTime.substring(4, 6))
                .append("-")
                .append(cycTime.substring(6, 8))
                .append(" ")
                .append(cycTime.substring(8, 10))
                .append(":")
                .append(cycTime.substring(10, 12))
                .append(":")
                .append(cycTime.substring(12, 14));
        this.cycTime = sb.toString();

    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
