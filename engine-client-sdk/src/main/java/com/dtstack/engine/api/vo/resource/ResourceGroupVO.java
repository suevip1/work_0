package com.dtstack.engine.api.vo.resource;

public class ResourceGroupVO {

    //资源组名称(支持中文、英文、数字、下划线，1-64字符。同一集群唯一)
    private String name;
    //资源组描述
    private String description;
    //队列路径
    private String queuePath;
    //集群ID
    private Long clusterId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }
}
