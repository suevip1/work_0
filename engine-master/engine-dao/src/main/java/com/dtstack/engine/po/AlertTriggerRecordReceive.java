package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 2:47 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertTriggerRecordReceive {
    private Long id;

    private Long alertTriggerRecordId;

    private Long receiveUserId;

    private String receiveUserName;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted;

    private Long projectId;

    private Integer appType;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlertTriggerRecordId() {
        return alertTriggerRecordId;
    }

    public void setAlertTriggerRecordId(Long alertTriggerRecordId) {
        this.alertTriggerRecordId = alertTriggerRecordId;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
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
