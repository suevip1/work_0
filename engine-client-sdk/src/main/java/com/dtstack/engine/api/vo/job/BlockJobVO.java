package com.dtstack.engine.api.vo.job;

/**
 * @Auther: dazhi
 * @Date: 2023/3/2 10:27 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BlockJobVO {

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 所属产品
     */
    private String appTypeName;

    /**
     * 产品类型
     */
    private Integer appType;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 责任人名称
     */
    private String ownerUserName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 校验状态
     */
    private Integer parentJobCheckStatus;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getAppTypeName() {
        return appTypeName;
    }

    public void setAppTypeName(String appTypeName) {
        this.appTypeName = appTypeName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getParentJobCheckStatus() {
        return parentJobCheckStatus;
    }

    public void setParentJobCheckStatus(Integer parentJobCheckStatus) {
        this.parentJobCheckStatus = parentJobCheckStatus;
    }
}
