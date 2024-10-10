package com.dtstack.engine.api.vo;

/**
 * @Auther: dazhi
 * @Date: 2022/8/16 10:03 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SchedulePeriodVO {

    private String jobId;

    private String cycTime;

    private Integer status;

    private Long taskId;

    private Integer version;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
