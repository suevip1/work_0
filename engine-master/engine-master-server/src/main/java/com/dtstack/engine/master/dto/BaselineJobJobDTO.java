package com.dtstack.engine.master.dto;

import java.sql.Timestamp;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/5/30 3:39 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineJobJobDTO {
    /**
     * 基线实例
     */
    private Long baselineJobId;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 应用类型
     */
    private Integer taskAppType;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 实例key
     */
    private String jobKey;

    /**
     * 父任务实例key
     */
    private Set<String> sonJobKey;

    /**
     * 预计执行时长
     */
    private Integer estimatedExecTime;

    /**
     * 预计开始时间
     */
    private Timestamp expectStartTime;

    /**
     * 预计结束时间
     */
    private Timestamp expectEndTime;

    /**
     * 预警开始时间
     */
    private Timestamp earlyWarningStartTime;

    /**
     * 预警结束时间
     */
    private Timestamp earlyWarningEndTime;

    /**
     * 破线开始时间
     */
    private Timestamp brokenLineStartTime;

    /**
     * 破线结束时间
     */
    private Timestamp brokenLineEndTime;

    /**
     * 基线任务类型 0 选择的基线任务，1 基线链路任务
     */
    private Integer baselineTaskType;

    public Long getBaselineJobId() {
        return baselineJobId;
    }

    public void setBaselineJobId(Long baselineJobId) {
        this.baselineJobId = baselineJobId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskAppType() {
        return taskAppType;
    }

    public void setTaskAppType(Integer taskAppType) {
        this.taskAppType = taskAppType;
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

    public Set<String> getSonJobKey() {
        return sonJobKey;
    }

    public void setSonJobKey(Set<String> sonJobKey) {
        this.sonJobKey = sonJobKey;
    }

    public Integer getEstimatedExecTime() {
        return estimatedExecTime;
    }

    public void setEstimatedExecTime(Integer estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
    }

    public Timestamp getExpectStartTime() {
        return expectStartTime;
    }

    public void setExpectStartTime(Timestamp expectStartTime) {
        this.expectStartTime = expectStartTime;
    }

    public Timestamp getExpectEndTime() {
        return expectEndTime;
    }

    public void setExpectEndTime(Timestamp expectEndTime) {
        this.expectEndTime = expectEndTime;
    }

    public Timestamp getEarlyWarningStartTime() {
        return earlyWarningStartTime;
    }

    public void setEarlyWarningStartTime(Timestamp earlyWarningStartTime) {
        this.earlyWarningStartTime = earlyWarningStartTime;
    }

    public Timestamp getEarlyWarningEndTime() {
        return earlyWarningEndTime;
    }

    public void setEarlyWarningEndTime(Timestamp earlyWarningEndTime) {
        this.earlyWarningEndTime = earlyWarningEndTime;
    }

    public Timestamp getBrokenLineStartTime() {
        return brokenLineStartTime;
    }

    public void setBrokenLineStartTime(Timestamp brokenLineStartTime) {
        this.brokenLineStartTime = brokenLineStartTime;
    }

    public Timestamp getBrokenLineEndTime() {
        return brokenLineEndTime;
    }

    public void setBrokenLineEndTime(Timestamp brokenLineEndTime) {
        this.brokenLineEndTime = brokenLineEndTime;
    }

    public Integer getBaselineTaskType() {
        return baselineTaskType;
    }

    public void setBaselineTaskType(Integer baselineTaskType) {
        this.baselineTaskType = baselineTaskType;
    }
}
