package com.dtstack.engine.api.vo.schedule.job;

/**
 * @Auther: dazhi
 * @Date: 2021/9/17 11:13 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataListVO {

    /**
     * 补数据名称
     */
    private String jobName;

    /**
     * 补数据运行时间
     */
    private Long runDay;

    /**
     * 补数据生成实例的开始时间
     */
    private Long bizStartDay;

    /**
     * 补数据生成实例的结束时间
     */
    private Long bizEndDay;

    /**
     * uic用户id
     */
    private Long dutyUserId;

    /**
     * 工程id
     */
    private Long projectId;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 当前页码
     */
    private Integer currentPage;

    /**
     * 当前页数
     */
    private Integer pageSize;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * uic租户id
     */
    private Long dtuicTenantId;

    /**
     * 补数据类型
     */
    private Integer fillDataType;

    public Integer getFillDataType() {
        return fillDataType;
    }

    public void setFillDataType(Integer fillDataType) {
        this.fillDataType = fillDataType;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getRunDay() {
        return runDay;
    }

    public void setRunDay(Long runDay) {
        this.runDay = runDay;
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

    public Long getDutyUserId() {
        return dutyUserId;
    }

    public void setDutyUserId(Long dutyUserId) {
        this.dutyUserId = dutyUserId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }
}
