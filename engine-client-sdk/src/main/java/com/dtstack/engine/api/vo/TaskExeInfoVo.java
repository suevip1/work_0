package com.dtstack.engine.api.vo;

/**
 * @auther: shuxing
 * @date: 2022/3/15 11:23 周二
 * @email: shuxing@dtstack.com
 * @description:
 */
public class TaskExeInfoVo {

    private String jobId;
    private int type;
    private int status;
    private String execTime;
    private String execStartTime;
    private String execEndTime;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public String getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(String execStartTime) {
        this.execStartTime = execStartTime;
    }

    public String getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(String execEndTime) {
        this.execEndTime = execEndTime;
    }

    @Override
    public String toString() {
        return "TaskExeInfoVo{" +
                "jobId='" + jobId + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", execTime='" + execTime + '\'' +
                ", execStartTime='" + execStartTime + '\'' +
                ", execEndTime='" + execEndTime + '\'' +
                '}';
    }
}
