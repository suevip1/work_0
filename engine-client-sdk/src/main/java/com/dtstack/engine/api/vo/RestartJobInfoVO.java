package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/11/22 下午4:08
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RestartJobInfoVO {

    private Long id;

    private String jobId;

    private String jobKey;

    private Integer jobStatus;

    /***
     * 任务调度时间 yyyymmddhhmmss
     */
    @ApiModelProperty(notes = "任务调度时间 yyyymmddhhmmss")
    private String cycTime;

    private Long taskId;

    private String taskName;

    private Integer taskType;

    private Integer scheduleStatus;

    private List<RestartJobInfoVO> childs;

    public List<RestartJobInfoVO> getChilds() {
        return childs;
    }

    public void setChilds(List<RestartJobInfoVO> childs) {
        this.childs = childs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
