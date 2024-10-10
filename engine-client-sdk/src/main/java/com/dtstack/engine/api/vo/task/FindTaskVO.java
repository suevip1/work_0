package com.dtstack.engine.api.vo.task;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/10/29 10:28 上午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class FindTaskVO {

    /**
     * task 名称
     */
    private String name;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 住户
     */
    private Long uicTenantId;

    private List<Long> userIds;

    /**
     * 租户名称
     */
    private String ownerUserName;

    /**
     * 功能id
     */
    private List<Long> projectIds;

    /**
     * 当前项目
     */
    private Long currentProjectId;


    /**
     * 最大取多少条任务
     */
    private Integer limit;

    /**
     * 任务调度状态 参考枚举: EProjectScheduleStatus
     */
    private Integer projectScheduleStatus;

    /**
     * 租户ID
     */
    private Long tenantId;

    private Long projectId;

    private Long ownerId;

    private List<Long> ownerIds;

    private Long startTime;

    private Long endTime;

    private Integer scheduleStatus;

    private String taskTypeList;

    private String periodTypeList;

    private Integer currentPage;

    private Integer pageSize;

    private String searchType;

    private List<Long> periodTypes;

    private List<Long> resourceIds;

    private List<Long> calenderIds;

    private Integer taskGroup;

    public Integer getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(Integer taskGroup) {
        this.taskGroup = taskGroup;
    }

    public List<Long> getCalenderIds() {
        return calenderIds;
    }

    public void setCalenderIds(List<Long> calenderIds) {
        this.calenderIds = calenderIds;
    }

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public List<Long> getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(List<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }


    public List<Long> getPeriodTypes() {
        return periodTypes;
    }

    public void setPeriodTypes(List<Long> periodTypes) {
        this.periodTypes = periodTypes;
    }

    public Long getUicTenantId() {
        return uicTenantId;
    }

    public void setUicTenantId(Long uicTenantId) {
        this.uicTenantId = uicTenantId;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getProjectScheduleStatus() {
        return projectScheduleStatus;
    }

    public void setProjectScheduleStatus(Integer projectScheduleStatus) {
        this.projectScheduleStatus = projectScheduleStatus;
    }

    public Long getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProjectId(Long currentProjectId) {
        this.currentProjectId = currentProjectId;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
