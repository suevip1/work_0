package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * 任务实例生成记录
 *
 * @author ：wangchuan
 * date：Created in 10:00 2022/8/23
 * company: www.dtstack.com
 */
public class ScheduleJobBuildRecord extends BaseEntity {

    /**
     * 实例构建方式
     */
    private Integer jobBuildType;

    /**
     * 实例构建状态
     */
    private Integer jobBuildStatus;

    /**
     * 实例构建日志
     */
    private String jobBuildLog;

    /**
     * 任务调度类型
     */
    private Integer scheduleType;

    /**
     * 任务 id
     */
    private Long taskId;

    /**
     * 应用类型
     */
    private Integer appType;

    public Integer getJobBuildType() {
        return jobBuildType;
    }

    public void setJobBuildType(Integer jobBuildType) {
        this.jobBuildType = jobBuildType;
    }

    public Integer getJobBuildStatus() {
        return jobBuildStatus;
    }

    public void setJobBuildStatus(Integer jobBuildStatus) {
        this.jobBuildStatus = jobBuildStatus;
    }

    public String getJobBuildLog() {
        return jobBuildLog;
    }

    public void setJobBuildLog(String jobBuildLog) {
        this.jobBuildLog = jobBuildLog;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
