package com.dtstack.engine.po;

import dt.insight.plat.lang.base.Times;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2023/2/23 9:58 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskTag {

    private Long id;

    private Integer appType;

    private Long taskId;

    private Long tagId;

    private Timestamp gmtCreate;

    private Times gmtModified;

    private Integer isDeleted;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Times getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Times gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
