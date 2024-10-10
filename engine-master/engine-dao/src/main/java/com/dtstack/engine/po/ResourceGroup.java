package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

@ApiModel
public class ResourceGroup extends BaseEntity {

    //资源组名称(支持中文、英文、数字、下划线，1-64字符。同一集群唯一)
    private String name;
    //资源组描述
    private String description;
    //队列路径
    private String queuePath;
    //集群ID
    private Long clusterId;

    private Integer componentTypeCode;

    /**资源组类型，0 yarn，1 trino，默认0**/
    private Integer resourceType = 0;

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

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

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }
}
