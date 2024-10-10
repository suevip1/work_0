package com.dtstack.engine.po;

import java.sql.Timestamp;

public class EnvironmentParamTemplate {

    private Long id;

    private String params;

    private Integer taskType;

    private String taskVersion;

    private String taskName;

    private Timestamp gmtModified;

    private Timestamp gmtCreate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }


    public String getTaskVersion() {
        return taskVersion;
    }

    public void setTaskVersion(String taskVersion) {
        this.taskVersion = taskVersion;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
