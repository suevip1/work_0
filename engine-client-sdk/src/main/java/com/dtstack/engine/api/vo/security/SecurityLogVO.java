package com.dtstack.engine.api.vo.security;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/6/4 22:02
 * @Description:
 */
public class SecurityLogVO {

    /**
     * 子应用标识
     */
    private String appTag;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 用户id
     */
    private Long operatorId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 执行的动作
     */
    private String action;

    private Timestamp createTime;

    /**
     *操作Action枚举值
     */
    private String operation;

    /**
     * 操作信息
     */
    private String operationObject;

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
     * 项目信息
     */
    private String projectInfo;

    /**
     * 详细信息
     */
    private Map<String,Object> detailInfo;

    /**
     * 工作值
     */
    private Integer operationVal;

    public Integer getOperationVal() {
        return operationVal;
    }

    public void setOperationVal(Integer operationVal) {
        this.operationVal = operationVal;
    }

    public Map<String, Object> getDetailInfo() {
        return detailInfo;
    }

    public String getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    public void setDetailInfo(Map<String, Object> detailInfo) {
        this.detailInfo = detailInfo;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
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
