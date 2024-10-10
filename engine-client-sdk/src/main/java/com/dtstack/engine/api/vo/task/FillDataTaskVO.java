package com.dtstack.engine.api.vo.task;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName FillDataTaskVO
 * @date 2022/7/25 11:19 AM
 */
public class FillDataTaskVO {

    private Long taskId;

    private Integer appType;

    private String name;

    private Integer taskType;

    private String projectAlias;

    private String tenantName;

    private List<FillDataTaskVO> fillDataTaskVOList;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getProjectAlias() {
        return projectAlias;
    }

    public void setProjectAlias(String projectAlias) {
        this.projectAlias = projectAlias;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<FillDataTaskVO> getFillDataTaskVOList() {
        return fillDataTaskVOList;
    }

    public void setFillDataTaskVOList(List<FillDataTaskVO> fillDataTaskVOList) {
        this.fillDataTaskVOList = fillDataTaskVOList;
    }
}
