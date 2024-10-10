package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-11-01
 */
public class StreamJobQueryParam {

    /**
     * 任务状态
     */
    private List<Integer> status;

    /**
     * 任务版本 如1.8 1.10
     */
    private List<String> versions;

    /**
     * 扩展字段
     */
    private String businessType;

    /**
     * 任务名称
     */
    private String taskName;

    @NotNull(message = "appType can not be null")
    private Integer appType;

    @NotNull(message = "project can not be null")
    private Long projectId;

    @NotNull(message = "tenantId can not be null")
    private Long dtuicTenantId;

    private Integer currentPage;

    private Integer pageSize;

    /**
     *
     */
    private String modifyOrderSort;

    public String getModifyOrderSort() {
        return modifyOrderSort;
    }

    public void setModifyOrderSort(String modifyOrderSort) {
        this.modifyOrderSort = modifyOrderSort;
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

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
}
