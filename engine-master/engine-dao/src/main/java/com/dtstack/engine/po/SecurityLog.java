package com.dtstack.engine.po;


import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/6/4 21:52
 * @Description:
 */
public class SecurityLog implements Serializable {

    private Long id;

    private Long tenantId;

    private String operator;

    private Long operatorId;

    private String appTag;

    private String action;

    private Timestamp gmtCreate;

    private String operation;

    private String operationObject;

    /**
     * 是否删除
     */
    @ApiModelProperty(notes = "是否删除")
    private Integer isDeleted = 0;

    /**
     * 来源ip
     */
    private String nodeAddress;

    /**
     * 操作解雇
     */
    private String operatorResult;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 详细信息
     */
    private String detailInfo;

    /**
     * 项目信息
     */
    private String projectInfo;

    public String getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperationObject() {
        return operationObject;
    }

    public void setOperationObject(String operationObject) {
        this.operationObject = operationObject;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getOperatorResult() {
        return operatorResult;
    }

    public void setOperatorResult(String operatorResult) {
        this.operatorResult = operatorResult;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
