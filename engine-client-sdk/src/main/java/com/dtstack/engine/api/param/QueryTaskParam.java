package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;
import java.util.List;

public class QueryTaskParam {
    /**
     * uic租户id
     */
    @NotNull(message = "tenantId not be null")
    public Long dtuicTenantId;
    /**
     * 项目id
     */
    public Long projectId;
    /**
     * 任务名称
     */
    public String name;
    /**
     * 责任人
     */
    public Long ownerId;
    /**
     * 开始时间
     */
    public Long startTime;
    /**
     * 结束时间
     */
    public Long endTime;
    /**
     * 调度状态
     */
    public Integer scheduleStatus;
    /**
     * 任务类型
     */
    public String taskTypeList;
    /**
     * 调度周期类型
     */
    public String periodTypeList;
    public Integer currentPage;
    public Integer pageSize;
    public String searchType;
    @NotNull(message = "appType not be null")
    public Integer appType;
    /**
     * 所属资源组
     */
    public List<Long> resourceIds;


    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(String taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public String getPeriodTypeList() {
        return periodTypeList;
    }

    public void setPeriodTypeList(String periodTypeList) {
        this.periodTypeList = periodTypeList;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
