package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName ScheduleJobGraph
 * @date 2022/8/2 7:23 PM
 */
public class ScheduleJobGraph {

    /**
     *
     */
    private Long id;

    private Integer appType;

    /**
     *
     */
    private Long uicTenantId;

    /**
     *
     */
    private Long projectId;

    /**
     *
     */
    private String date;

    /**
     *
     */
    private Integer hour;

    /**
     *
     */
    private Integer count;

    /**
     *
     */
    private Timestamp gmtCreate;

    /**
     *
     */
    private Timestamp gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getUicTenantId() {
        return uicTenantId;
    }

    public void setUicTenantId(Long uicTenantId) {
        this.uicTenantId = uicTenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
}
