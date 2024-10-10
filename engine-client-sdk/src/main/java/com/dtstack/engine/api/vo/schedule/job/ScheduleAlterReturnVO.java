package com.dtstack.engine.api.vo.schedule.job;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2023-09-13 15:23
 * @Email: dazhi@dtstack.com
 * @Description: ScheduleAlterReturnVO
 */
public class ScheduleAlterReturnVO {


    private String jobId;

    private Integer type;

    private Long taskId;

    private Long projectId;

    private Integer appType;

    private Integer status;

    private Long createUserId;

    private String cycTime;

    private Integer isRestart;

    private Timestamp execStartTime;

    private Timestamp execEndTime;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }
}
