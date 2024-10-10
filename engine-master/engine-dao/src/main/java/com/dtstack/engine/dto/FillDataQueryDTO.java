package com.dtstack.engine.dto;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/17 3:07 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataQueryDTO {

    /**
     * uic租户id
     */
    private Long dtuicTenantId;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页个数
     */
    private Integer pageSize;

    /**
     * sql开始查询数
     */
    private Integer start;

    /**
     * 任务ids
     */
    private List<Long> taskIds;

    /**
     * 补数据id
     */
    private Long fillId;

    /**
     * 计划开始时间
     */
    private String cycStart;

    /**
     * 计划结束时间
     */
    private String cycEnd;

    /**
     * 用户ID 责任人
     */
    private Long dutyUserId;

    /**
     * 补数据类型
     */
    private List<Integer> fillTypes;

    /**
     * 任务类型
     */
    private List<String> taskTypeList;

    /**
     * 状态帅选
     */
    private List<Integer> jobStatusesList;

    /**
     * 按业务日期排序
     */
    private String businessDateSort;

    /**
     * 按计划时间排序
     */
    private String cycSort;

    /**
     * 按运行时长排序
     */
    private String execTimeSort;

    /**
     * 按开始时间排序
     */
    private String execStartSort;

    /**
     * 按重试次数排序
     */
    private String retryNumSort;

    private Long resourceId;

    private List<Long> resourceIds;

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public String getCycStart() {
        return cycStart;
    }

    public void setCycStart(String cycStart) {
        this.cycStart = cycStart;
    }

    public String getCycEnd() {
        return cycEnd;
    }

    public void setCycEnd(String cycEnd) {
        this.cycEnd = cycEnd;
    }

    public Long getDutyUserId() {
        return dutyUserId;
    }

    public void setDutyUserId(Long dutyUserId) {
        this.dutyUserId = dutyUserId;
    }

    public List<Integer> getFillTypes() {
        return fillTypes;
    }

    public void setFillTypes(List<Integer> fillTypes) {
        this.fillTypes = fillTypes;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getBusinessDateSort() {
        return businessDateSort;
    }

    public void setBusinessDateSort(String businessDateSort) {
        this.businessDateSort = businessDateSort;
    }

    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
    }

    public String getExecTimeSort() {
        return execTimeSort;
    }

    public void setExecTimeSort(String execTimeSort) {
        this.execTimeSort = execTimeSort;
    }

    public String getExecStartSort() {
        return execStartSort;
    }

    public void setExecStartSort(String execStartSort) {
        this.execStartSort = execStartSort;
    }

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }

    public int getStart() {
        start = (this.currentPage - 1) * this.pageSize;
        return start;
    }

    public List<String> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<String> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Integer> getJobStatusesList() {
        return jobStatusesList;
    }

    public void setJobStatusesList(List<Integer> jobStatusesList) {
        this.jobStatusesList = jobStatusesList;
    }
}
