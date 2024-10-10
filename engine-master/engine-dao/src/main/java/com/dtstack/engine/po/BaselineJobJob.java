package com.dtstack.engine.po;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:17 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineJobJob {

    private Long id;

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

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaselineJobJob that = (BaselineJobJob) o;
        return Objects.equals(id, that.id) && Objects.equals(baselineJobId, that.baselineJobId) && Objects.equals(taskId, that.taskId) && Objects.equals(taskAppType, that.taskAppType) && Objects.equals(jobId, that.jobId) && Objects.equals(estimatedExecTime, that.estimatedExecTime) && Objects.equals(expectStartTime, that.expectStartTime) && Objects.equals(expectEndTime, that.expectEndTime) && Objects.equals(earlyWarningStartTime, that.earlyWarningStartTime) && Objects.equals(earlyWarningEndTime, that.earlyWarningEndTime) && Objects.equals(brokenLineStartTime, that.brokenLineStartTime) && Objects.equals(brokenLineEndTime, that.brokenLineEndTime) && Objects.equals(baselineTaskType, that.baselineTaskType) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baselineJobId, taskId, taskAppType, jobId, estimatedExecTime, expectStartTime, expectEndTime, earlyWarningStartTime, earlyWarningEndTime, brokenLineStartTime, brokenLineEndTime, baselineTaskType, gmtCreate, gmtModified, isDeleted);
    }
}
