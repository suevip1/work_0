package com.dtstack.engine.po;

import java.util.Date;

/**
 * 
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-15 22:08
 */
public class ScheduleJobStatusMonitor {
    private Integer id;

    /**
    * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
    */
    private String appTypeKey;

    /**
    * 回调地址，域名形式
    */
    private String callbackUrl;

    /**
    * 监听策略, {"listenerStatus":[], "listenerAppType":[], "listenerJobType":[]}
    */
    private String listenerPolicy;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppTypeKey() {
        return appTypeKey;
    }

    public void setAppTypeKey(String appTypeKey) {
        this.appTypeKey = appTypeKey;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getListenerPolicy() {
        return listenerPolicy;
    }

    public void setListenerPolicy(String listenerPolicy) {
        this.listenerPolicy = listenerPolicy;
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
}