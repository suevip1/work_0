package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 * @date 2022-09-19 17:18
 **/
@ApiModel("租户的集群信息")
public class TenantClusterInfoVO {

    @ApiModelProperty(value = "集群id")
    private Long clusterId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "租户的集群资源信息")
    private TenantClusterResourceInfoVO tenantClusterResourceInfoVO;

    @ApiModelProperty(value = "租户的集群资源限制信息")
    private List<TenantClusterResourceLimitInfoVO> tenantClusterResourceLimitInfoVOS;

    @ApiModelProperty(value = "租户组件配置信息")
    private List<ConsoleTenantComponentVO> consoleTenantComponentVOS;

    @ApiModelProperty(value = "租户的集群标签资源信息")
    private TenantClusterLabelResourceInfoVO tenantClusterLabelResourceInfoVO;

    @ApiModel("租户的集群资源信息")
    public static class TenantClusterResourceInfoVO {

        @ApiModelProperty(value = "资源id")
        private Long defaultResourceId;

        @ApiModelProperty(value = "默认资源组")
        private String defaultResourceName;

        @ApiModelProperty(value = "最大容量")
        private String maxCapacity;

        @ApiModelProperty(value = "最小容量")
        private String minCapacity;

        @ApiModelProperty(value = "队列id, k8s")
        private Long queueId;

        @ApiModelProperty(value = "队列，k8s namespace")
        private String queue;

        public String getDefaultResourceName() {
            return defaultResourceName;
        }

        public void setDefaultResourceName(String defaultResourceName) {
            this.defaultResourceName = defaultResourceName;
        }

        public String getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(String maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public String getMinCapacity() {
            return minCapacity;
        }

        public void setMinCapacity(String minCapacity) {
            this.minCapacity = minCapacity;
        }

        public Long getDefaultResourceId() {
            return defaultResourceId;
        }

        public void setDefaultResourceId(Long defaultResourceId) {
            this.defaultResourceId = defaultResourceId;
        }

        public Long getQueueId() {
            return queueId;
        }

        public void setQueueId(Long queueId) {
            this.queueId = queueId;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }
    }

    @ApiModel("租户的集群标签资源信息")
    public static class TenantClusterLabelResourceInfoVO {

        @ApiModelProperty(value = "资源id")
        private Long defaultLabelResourceId;

        @ApiModelProperty(value = "默认节点标签")
        private String defaultLabel;

        @ApiModelProperty(value = "默认用户名")
        private String defaultUserName;

        public Long getDefaultLabelResourceId() {
            return defaultLabelResourceId;
        }

        public void setDefaultLabelResourceId(Long defaultLabelResourceId) {
            this.defaultLabelResourceId = defaultLabelResourceId;
        }

        public String getDefaultLabel() {
            return defaultLabel;
        }

        public void setDefaultLabel(String defaultLabel) {
            this.defaultLabel = defaultLabel;
        }

        public String getDefaultUserName() {
            return defaultUserName;
        }

        public void setDefaultUserName(String defaultUserName) {
            this.defaultUserName = defaultUserName;
        }
    }

    @ApiModel("租户的集群资源限制信息")
    public static class TenantClusterResourceLimitInfoVO {

        @ApiModelProperty(value = "任务类型")
        private Integer taskType;

        @ApiModelProperty(value = "任务名称")
        private String taskName;

        @ApiModelProperty(value = "资源限制")
        private Map<String,Object> resourceLimit;

        public Integer getTaskType() {
            return taskType;
        }

        public void setTaskType(Integer taskType) {
            this.taskType = taskType;
        }

        public Map<String, Object> getResourceLimit() {
            return resourceLimit;
        }

        public void setResourceLimit(Map<String, Object> resourceLimit) {
            this.resourceLimit = resourceLimit;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }
    }

    @ApiModel("租户组件配置信息")
    public static class ConsoleTenantComponentVO {

        @ApiModelProperty(value = "组件名称")
        private String componentName;

        @ApiModelProperty(value = "组件code")
        private Integer componentTypeCode;

        @ApiModelProperty(value = "组件配置")
        private String componentConfig;

        public String getComponentName() {
            return componentName;
        }

        public void setComponentName(String componentName) {
            this.componentName = componentName;
        }

        public Integer getComponentTypeCode() {
            return componentTypeCode;
        }

        public void setComponentTypeCode(Integer componentTypeCode) {
            this.componentTypeCode = componentTypeCode;
        }

        public String getComponentConfig() {
            return componentConfig;
        }

        public void setComponentConfig(String componentConfig) {
            this.componentConfig = componentConfig;
        }
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public TenantClusterResourceInfoVO getTenantClusterResourceInfo() {
        return tenantClusterResourceInfoVO;
    }

    public void setTenantClusterResourceInfo(TenantClusterResourceInfoVO tenantClusterResourceInfoVO) {
        this.tenantClusterResourceInfoVO = tenantClusterResourceInfoVO;
    }

    public List<TenantClusterResourceLimitInfoVO> getTenantClusterResourceLimitInfos() {
        return tenantClusterResourceLimitInfoVOS;
    }

    public void setTenantClusterResourceLimitInfos(List<TenantClusterResourceLimitInfoVO> tenantClusterResourceLimitInfoVOS) {
        this.tenantClusterResourceLimitInfoVOS = tenantClusterResourceLimitInfoVOS;
    }

    public List<ConsoleTenantComponentVO> getConsoleTenantComponentVOS() {
        return consoleTenantComponentVOS;
    }

    public void setConsoleTenantComponentVOS(List<ConsoleTenantComponentVO> consoleTenantComponentVOS) {
        this.consoleTenantComponentVOS = consoleTenantComponentVOS;
    }

    public TenantClusterLabelResourceInfoVO getTenantClusterLabelResourceInfoVO() {
        return tenantClusterLabelResourceInfoVO;
    }

    public void setTenantClusterLabelResourceInfoVO(TenantClusterLabelResourceInfoVO tenantClusterLabelResourceInfoVO) {
        this.tenantClusterLabelResourceInfoVO = tenantClusterLabelResourceInfoVO;
    }

    public static final class TenantClusterInfoVOBuilder {
        private Long clusterId;
        private Long tenantId;
        private TenantClusterResourceInfoVO tenantClusterResourceInfoVO;
        private List<TenantClusterResourceLimitInfoVO> tenantClusterResourceLimitInfoVOS;
        private List<ConsoleTenantComponentVO> consoleTenantComponentVOS;
        private TenantClusterLabelResourceInfoVO tenantClusterLabelResourceInfoVO;

        private TenantClusterInfoVOBuilder() {
        }

        public static TenantClusterInfoVOBuilder aTenantClusterInfoVO() {
            return new TenantClusterInfoVOBuilder();
        }

        public TenantClusterInfoVOBuilder clusterId(Long clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public TenantClusterInfoVOBuilder tenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public TenantClusterInfoVOBuilder tenantClusterResourceInfoVO(TenantClusterResourceInfoVO tenantClusterResourceInfoVO) {
            this.tenantClusterResourceInfoVO = tenantClusterResourceInfoVO;
            return this;
        }

        public TenantClusterInfoVOBuilder tenantClusterResourceLimitInfoVOS(List<TenantClusterResourceLimitInfoVO> tenantClusterResourceLimitInfoVOS) {
            this.tenantClusterResourceLimitInfoVOS = tenantClusterResourceLimitInfoVOS;
            return this;
        }

        public TenantClusterInfoVOBuilder consoleTenantComponentVOS(List<ConsoleTenantComponentVO> consoleTenantComponentVOS) {
            this.consoleTenantComponentVOS = consoleTenantComponentVOS;
            return this;
        }

        public TenantClusterInfoVOBuilder tenantClusterLabelResourceInfoVO(TenantClusterLabelResourceInfoVO tenantClusterLabelResourceInfoVO) {
            this.tenantClusterLabelResourceInfoVO = tenantClusterLabelResourceInfoVO;
            return this;
        }

        public TenantClusterInfoVO build() {
            TenantClusterInfoVO tenantClusterInfoVO = new TenantClusterInfoVO();
            tenantClusterInfoVO.setClusterId(clusterId);
            tenantClusterInfoVO.setTenantId(tenantId);
            tenantClusterInfoVO.setConsoleTenantComponentVOS(consoleTenantComponentVOS);
            tenantClusterInfoVO.tenantClusterResourceInfoVO = this.tenantClusterResourceInfoVO;
            tenantClusterInfoVO.tenantClusterResourceLimitInfoVOS = this.tenantClusterResourceLimitInfoVOS;
            tenantClusterInfoVO.tenantClusterLabelResourceInfoVO = this.tenantClusterLabelResourceInfoVO;
            return tenantClusterInfoVO;
        }
    }
}
