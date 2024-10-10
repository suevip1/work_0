package com.dtstack.engine.api.vo.job;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/11/23 1:59 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobRunCountVO {

    /**
     *
     */
    private String id;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 第几次运行
     */
    private Integer runNum;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时运时间
     */
    private Timestamp execStartTime;

    /**
     * 结束运行时间
     */
    private Timestamp execEndTime;

    /**
     * 运行时长
     */
    private Integer execTime;

    /**
     * 独立运行的任务需要记录额外的id
     */
    private String applicationId;

    /**
     * 离线任务计算引擎id
     */
    private String engineJobId;

    /**
     * 是否启用日志监控, 1 表示启用, 0 表示未启用
     */
    private Integer enableJobMonitor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getRunNum() {
        return runNum;
    }

    public void setRunNum(Integer runNum) {
        this.runNum = runNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getExecTime() {
        return execTime;
    }

    public void setExecTime(Integer execTime) {
        this.execTime = execTime;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public Integer getEnableJobMonitor() {
        return enableJobMonitor;
    }

    public void setEnableJobMonitor(Integer enableJobMonitor) {
        this.enableJobMonitor = enableJobMonitor;
    }
}
