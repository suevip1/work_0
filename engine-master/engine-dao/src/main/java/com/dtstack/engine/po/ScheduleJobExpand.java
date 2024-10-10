package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.ResourceFile;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/8/26 2:54 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobExpand {
    private Long id;
    private String jobId;
    private String retryTaskParams;
    private String jobExtraInfo;
    private String engineLog;
    private String logInfo;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
    private Integer isDeleted;
    private Integer runNum;
    private Integer status;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private Integer execTime;
    private String applicationId;
    private String engineJobId;
    private Integer enableJobMonitor;
    private Long authId;

    private List<ResourceFile> resourceFiles;

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

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }

    public String getJobExtraInfo() {
        return jobExtraInfo;
    }

    public void setJobExtraInfo(String jobExtraInfo) {
        this.jobExtraInfo = jobExtraInfo;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<ResourceFile> getResourceFiles() {
        return resourceFiles;
    }

    public void setResourceFiles(List<ResourceFile> resourceFiles) {
        this.resourceFiles = resourceFiles;
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

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }
}
