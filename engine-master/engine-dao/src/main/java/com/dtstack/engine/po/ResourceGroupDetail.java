package com.dtstack.engine.po;

public class ResourceGroupDetail {

    private Long resourceId;
    private String resourceName;

    private String capacity;
    private String maxCapacity;

    private Long clusterId;
    private String queueName;
    private String queuePath;

    private String description;

    /**
     * 是否项目级默认，0否1是
     */
    private Integer isProjectDefault;

    /**
     * 授权 id
     */
    private Long resourceGrantId;

    /**
     * 项目 id
     */
    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getResourceGrantId() {
        return resourceGrantId;
    }

    public void setResourceGrantId(Long resourceGrantId) {
        this.resourceGrantId = resourceGrantId;
    }

    public Integer getIsProjectDefault() {
        return isProjectDefault;
    }

    public void setIsProjectDefault(Integer isProjectDefault) {
        this.isProjectDefault = isProjectDefault;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
