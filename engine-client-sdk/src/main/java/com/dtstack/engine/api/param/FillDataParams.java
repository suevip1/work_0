package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;
import java.util.List;

public class FillDataParams {
    /**
     * 任务名称
     */
    public String taskName;
    /**
     * 业务日志日期开始时间
     */
    public Long bizStartDay;
    /**
     * 业务日志日期结束时间
     */
    public Long bizEndDay;

    public List<String> flowJobIdList;
    /**
     * 补数据名称
     */
    public String fillJobName;
    /**
     * 责任人
     */
    public Long dutyUserId;
    public String searchType;
    @NotNull(message =  "app type can not null")
    public Integer appType;
    /**
     * 项目id
     */
    public Long projectId;
    /**
     * uic租户id
     */
    @NotNull(message =  "tenantId can not null")
    public Long dtuicTenantId;
    public String execTimeSort;
    public String execStartSort;
    public String execEndSort;
    public String cycSort;
    public String businessDateSort;
    public String retryNumSort;
    /**
     * 任务类型
     */
    public String taskType;
    /**
     * 状态 ,分割
     */
    public String jobStatuses;
    public Integer currentPage;
    public Integer pageSize;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getBizStartDay() {
        return bizStartDay;
    }

    public void setBizStartDay(Long bizStartDay) {
        this.bizStartDay = bizStartDay;
    }

    public Long getBizEndDay() {
        return bizEndDay;
    }

    public void setBizEndDay(Long bizEndDay) {
        this.bizEndDay = bizEndDay;
    }

    public List<String> getFlowJobIdList() {
        return flowJobIdList;
    }

    public void setFlowJobIdList(List<String> flowJobIdList) {
        this.flowJobIdList = flowJobIdList;
    }

    public String getFillJobName() {
        return fillJobName;
    }

    public void setFillJobName(String fillJobName) {
        this.fillJobName = fillJobName;
    }

    public Long getDutyUserId() {
        return dutyUserId;
    }

    public void setDutyUserId(Long dutyUserId) {
        this.dutyUserId = dutyUserId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
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

    public String getExecEndSort() {
        return execEndSort;
    }

    public void setExecEndSort(String execEndSort) {
        this.execEndSort = execEndSort;
    }

    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
    }

    public String getBusinessDateSort() {
        return businessDateSort;
    }

    public void setBusinessDateSort(String businessDateSort) {
        this.businessDateSort = businessDateSort;
    }

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
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
}
