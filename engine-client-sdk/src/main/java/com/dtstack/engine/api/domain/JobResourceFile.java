package com.dtstack.engine.api.domain;

import com.dtstack.engine.api.enums.JobResourceFileTypeEnum;

/**
 * @author leon
 * @date 2022-08-04 15:40
 **/
public class JobResourceFile {

    private String path;

    private String fileName;

    private JobResourceFileTypeEnum type;

    private String taskName;

    private Long subTaskId;

    private String content;

    private Integer taskType;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public JobResourceFileTypeEnum getType() {
        return type;
    }

    public void setType(JobResourceFileTypeEnum type) {
        this.type = type;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Long subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}


