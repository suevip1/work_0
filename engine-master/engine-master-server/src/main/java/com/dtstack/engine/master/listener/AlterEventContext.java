package com.dtstack.engine.master.listener;

import com.dtstack.engine.master.dto.BaselineBlockDTO;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.po.BaselineJob;

/**
 * @Auther: dazhi
 * @Date: 2022/5/20 3:38 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlterEventContext {

    private AlterKey key;

    private Long taskId;

    private Integer appType;

    private String taskName;

    private Integer taskType;

    private String jobId;

    private Integer status;

    private Long ownerUserId;

    private BaselineJob baselineJob;

    private BaselineBlockDTO baselineBlockDTO;

    private Integer finishStatus;

    private Integer baselineStatus;

    private String resourceUsage;

    private Long alertAlarmId;

    private Integer scheduleType;

    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public AlterKey getKey() {
        return key;
    }

    public void setKey(AlterKey key) {
        this.key = key;
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

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BaselineJob getBaselineJob() {
        return baselineJob;
    }

    public void setBaselineJob(BaselineJob baselineJob) {
        this.baselineJob = baselineJob;
    }

    public BaselineBlockDTO getBaselineBlockDTO() {
        return baselineBlockDTO;
    }

    public void setBaselineBlockDTO(BaselineBlockDTO baselineBlockDTO) {
        this.baselineBlockDTO = baselineBlockDTO;
    }

    public Integer getFinishStatus() {
        return finishStatus;
    }

    public void setFinishStatus(Integer finishStatus) {
        this.finishStatus = finishStatus;
    }

    public Integer getBaselineStatus() {
        return baselineStatus;
    }

    public void setBaselineStatus(Integer baselineStatus) {
        this.baselineStatus = baselineStatus;
    }

    public String getResourceUsage() {
        return resourceUsage;
    }

    public void setResourceUsage(String resourceUsage) {
        this.resourceUsage = resourceUsage;
    }

    public Long getAlertAlarmId() {
        return alertAlarmId;
    }

    public void setAlertAlarmId(Long alertAlarmId) {
        this.alertAlarmId = alertAlarmId;
    }
}
