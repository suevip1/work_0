package com.dtstack.engine.po;

import java.util.Date;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-07-25 20:40
 */
public class ScheduleJobAuth {
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

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 0正常 1逻辑删除
     */
    private Integer isDeleted;

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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}