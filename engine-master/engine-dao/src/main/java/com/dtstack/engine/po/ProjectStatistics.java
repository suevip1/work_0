package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * @author yuebai
 * @date 2022/8/11
 */
public class ProjectStatistics extends BaseEntity {

    private Integer totalJob;

    private Integer unSubmitJob;

    private Integer runJob;

    private Integer failJob;

    private Integer alarmTotal;

    private Long userId;

    private Long projectId;

    private Integer appType;

    private Long tenantId;

    public Integer getTotalJob() {
        return totalJob;
    }

    public void setTotalJob(Integer totalJob) {
        this.totalJob = totalJob;
    }

    public Integer getUnSubmitJob() {
        return unSubmitJob;
    }

    public void setUnSubmitJob(Integer unSubmitJob) {
        this.unSubmitJob = unSubmitJob;
    }

    public Integer getRunJob() {
        return runJob;
    }

    public void setRunJob(Integer runJob) {
        this.runJob = runJob;
    }

    public Integer getFailJob() {
        return failJob;
    }

    public void setFailJob(Integer failJob) {
        this.failJob = failJob;
    }

    public Integer getAlarmTotal() {
        return alarmTotal;
    }

    public void setAlarmTotal(Integer alarmTotal) {
        this.alarmTotal = alarmTotal;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
