package com.dtstack.engine.dto;

import java.sql.Timestamp;
import java.util.List;

/**
 * schedule job query
 *
 * @author ：wangchuan
 * date：Created in 10:00 2022/8/23
 * company: www.dtstack.com
 */
public class ScheduleJobBuildRecordQuery {

    /**
     * 实例构建方式 {@link com.dtstack.engine.api.enums.JobBuildType}
     */
    private List<Integer> jobBuildTypeList;

    /**
     * 实例构建状态 {@link com.dtstack.engine.api.enums.JobBuildStatus}
     */
    private List<Integer> jobBuildStatusList;

    /**
     * 任务调度类型
     */
    private List<Integer> scheduleTypeList;

    /**
     * 任务 id
     */
    private List<Long> taskIdList;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 创建时间大于指定时间
     */
    private Timestamp greaterTime;

    public List<Integer> getJobBuildTypeList() {
        return jobBuildTypeList;
    }

    public void setJobBuildTypeList(List<Integer> jobBuildTypeList) {
        this.jobBuildTypeList = jobBuildTypeList;
    }

    public List<Integer> getJobBuildStatusList() {
        return jobBuildStatusList;
    }

    public void setJobBuildStatusList(List<Integer> jobBuildStatusList) {
        this.jobBuildStatusList = jobBuildStatusList;
    }

    public List<Integer> getScheduleTypeList() {
        return scheduleTypeList;
    }

    public void setScheduleTypeList(List<Integer> scheduleTypeList) {
        this.scheduleTypeList = scheduleTypeList;
    }

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Timestamp getGreaterTime() {
        return greaterTime;
    }

    public void setGreaterTime(Timestamp greaterTime) {
        this.greaterTime = greaterTime;
    }
}
