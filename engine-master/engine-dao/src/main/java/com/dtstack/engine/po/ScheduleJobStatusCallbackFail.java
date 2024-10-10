package com.dtstack.engine.po;

import java.util.Date;

/**
 * 
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-16 19:52
 */
public class ScheduleJobStatusCallbackFail {
    private Integer id;

    /**
    * 工作任务id
    */
    private String jobId;

    /**
    * 重试次数 默认0
    */
    private Integer retryNum;

    /**
    * 最后一次失败的原因
    */
    private String lastRetryFailReason;

    /**
    * 新增时间
    */
    private Date gmtCreate;

    /**
    * 修改时间
    */
    private Date gmtModified;

    /**
    * 0正常 1逻辑删除
    */
    private Boolean isDeleted;

    /**
    * 监控记录id
    */
    private Integer monitorId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getLastRetryFailReason() {
        return lastRetryFailReason;
    }

    public void setLastRetryFailReason(String lastRetryFailReason) {
        this.lastRetryFailReason = lastRetryFailReason;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Integer monitorId) {
        this.monitorId = monitorId;
    }
}