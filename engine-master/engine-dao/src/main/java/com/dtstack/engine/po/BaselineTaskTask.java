package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 10:53 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskTask {

    private Long id;

    private Long baselineTaskId;

    private Long taskId;

    private Integer taskAppType;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaselineTaskId() {
        return baselineTaskId;
    }

    public void setBaselineTaskId(Long baselineTaskId) {
        this.baselineTaskId = baselineTaskId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskAppType() {
        return taskAppType;
    }

    public void setTaskAppType(Integer taskAppType) {
        this.taskAppType = taskAppType;
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
