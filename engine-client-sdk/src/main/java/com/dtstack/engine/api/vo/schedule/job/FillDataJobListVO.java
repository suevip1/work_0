package com.dtstack.engine.api.vo.schedule.job;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/17 10:39 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataJobListVO {

    /**
     * 补数据id
     */
    @NotNull(message = "fillId is not null")
    private Long fillId;

    /**
     * uic租户id
     */
    @NotNull(message = "dtuicTenantId is not null")
    private Long dtuicTenantId;

    /**
     * 应用类型
     */
    @NotNull(message = "appType is not null")
    private Integer appType;

    /**
     * 项目id
     */
    @NotNull(message = "projectId is not null")
    private Long projectId;

    /**
     * 当前页
     */
    private Integer currentPage = 1;

    /**
     * 每页个数
     */
    private Integer pageSize = 20;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 业务日志
     */
    private Long bizStartDay;

    /**
     * 业务日志
     */
    private Long bizEndDay;

    /**
     * 工作流任务id
     */
    private List<String> flowJobIdList;

    /**
     * 用户ID 责任人
     */
    private Long dutyUserId;

    /**
     * 搜索类型
     */
    private String searchType;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 状态
     */
    private String jobStatuses;

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

    /**
     *  增加失败状态细分标志
     */
    private Boolean splitFiledFlag;

    private Long resourceId;

    private List<Long> resourceIds;

    private List<Long> tagIds;

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }


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

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public String getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
    }

    public Boolean getSplitFiledFlag() {
        return splitFiledFlag;
    }

    public void setSplitFiledFlag(Boolean splitFiledFlag) {
        this.splitFiledFlag = splitFiledFlag;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
