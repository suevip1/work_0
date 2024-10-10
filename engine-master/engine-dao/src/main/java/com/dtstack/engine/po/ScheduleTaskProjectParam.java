package com.dtstack.engine.po;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-06 13:38
 */
public class ScheduleTaskProjectParam {
    private Long id;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
     */
    private Integer appType;

    /**
     * 项目参数 id
     */
    private Long projectParamId;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 新增时间
     */
    private Timestamp gmtCreate;

    /**
     * 修改时间
     */
    private Timestamp gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getProjectParamId() {
        return projectParamId;
    }

    public void setProjectParamId(Long projectParamId) {
        this.projectParamId = projectParamId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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