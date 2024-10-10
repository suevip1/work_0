package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

import java.util.Date;

/**
 * @author yuebai
 * @date 2022/8/22
 */
public class ScheduleJobGanttChart extends BaseEntity {
    private String jobId;

    private Date cycTime;
    private Date statusTime;
    private Date parentDependTime;
    private Date tenantResourceTime;
    private Date jobSubmitTime;
    private Date resourceMatchTime;
    private Date runJobTime;
    private Date validJobTime;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getCycTime() {
        return cycTime;
    }

    public void setCycTime(Date cycTime) {
        this.cycTime = cycTime;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public Date getParentDependTime() {
        return parentDependTime;
    }

    public void setParentDependTime(Date parentDependTime) {
        this.parentDependTime = parentDependTime;
    }

    public Date getTenantResourceTime() {
        return tenantResourceTime;
    }

    public void setTenantResourceTime(Date tenantResourceTime) {
        this.tenantResourceTime = tenantResourceTime;
    }

    public Date getJobSubmitTime() {
        return jobSubmitTime;
    }

    public void setJobSubmitTime(Date jobSubmitTime) {
        this.jobSubmitTime = jobSubmitTime;
    }

    public Date getResourceMatchTime() {
        return resourceMatchTime;
    }

    public void setResourceMatchTime(Date resourceMatchTime) {
        this.resourceMatchTime = resourceMatchTime;
    }

    public Date getRunJobTime() {
        return runJobTime;
    }

    public void setRunJobTime(Date runJobTime) {
        this.runJobTime = runJobTime;
    }

    public Date getValidJobTime() {
        return validJobTime;
    }

    public void setValidJobTime(Date validJobTime) {
        this.validJobTime = validJobTime;
    }

}
