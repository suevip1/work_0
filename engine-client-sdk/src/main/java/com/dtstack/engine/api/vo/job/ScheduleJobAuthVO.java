package com.dtstack.engine.api.vo.job;

import java.io.Serializable;

public class ScheduleJobAuthVO implements Serializable {
    private static final long serialVersionUID = 7725117838330877198L;
    private Long id;

    /**
     * uic用户id
     */
    private Long uicUserId;

    /**
     * uic租户id
     */
    private Long dtuicTenantId;

    /**
     * 认证类型:KERBEROS、LDAP、TBDS、PROXY
     */
    private String authType;

    /**
     * 认证业务名
     */
    private String authBizName;

    /**
     * 任务提交用户名
     */
    private String submitUserName;

    /**
     * 认证信息
     */
    private String authInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUicUserId() {
        return uicUserId;
    }

    public void setUicUserId(Long uicUserId) {
        this.uicUserId = uicUserId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthBizName() {
        return authBizName;
    }

    public void setAuthBizName(String authBizName) {
        this.authBizName = authBizName;
    }

    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }
}