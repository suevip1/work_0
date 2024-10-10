package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:03 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineJob {

    private Long id;

    private String name;

    private Long projectId;

    private Long tenantId;

    private Long baselineTaskId;

    private Integer appType;

    /**
     * 责任人id
     */
    private Long ownerUserId;

    /**
     * 基线实例状态: 0 安全,1 预警,2 破线,3 定时未完成,4 其他
     */
    private Integer baselineStatus;

    /**
     * 基线完成状态: 0 未完成 1 已完成
     */
    private Integer finishStatus;

    /**
     * 业务时间: yyyyMMdd
     */
    private Timestamp businessDate;

    /**
     * 预计完成时间
     */
    private Timestamp expectFinishTime;

    private Timestamp finishTime;

    /**
     * 1 单批次 2 多批次
     */
    private Integer batchType;

    /**
     * 计划时间: yyyy-MM-dd hh:mm:ss
     * 对于多批次的实例，此计划时间就是生成的每个批次的实例计划时间；
     * 对于单批次的实例，此计划时间是生成基线实例里所有任务实例中计划时间最早的实例的计划时间
     */
    private String cycTime;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBaselineTaskId() {
        return baselineTaskId;
    }

    public void setBaselineTaskId(Long baselineTaskId) {
        this.baselineTaskId = baselineTaskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getBaselineStatus() {
        return baselineStatus;
    }

    public void setBaselineStatus(Integer baselineStatus) {
        this.baselineStatus = baselineStatus;
    }

    public Integer getFinishStatus() {
        return finishStatus;
    }

    public void setFinishStatus(Integer finishStatus) {
        this.finishStatus = finishStatus;
    }

    public Timestamp getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Timestamp businessDate) {
        this.businessDate = businessDate;
    }

    public Timestamp getExpectFinishTime() {
        return expectFinishTime;
    }

    public void setExpectFinishTime(Timestamp expectFinishTime) {
        this.expectFinishTime = expectFinishTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
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
}
