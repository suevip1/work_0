package com.dtstack.engine.api.vo.components;

/**
 * @author yuebai
 * @date 2021-09-09
 */
public class ClusterComponentVO {

    private Long id;

    /**
     * 组件code
     */
    private Integer componentTypeCode;

    private Long clusterId;

    private String hadoopVersion;

    /**
     * 存储组件名称
     */
    private Integer storeType;

    /**
     * 当前组件是否为默认版本
     */
    private Boolean isDefault;

    /**
     * 是否为元数据
     */
    private Integer isMetadata;

    /**
     * 0: standalone 1: yarn
     */
    private Integer deployType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Integer getIsMetadata() {
        return isMetadata;
    }

    public void setIsMetadata(Integer isMetadata) {
        this.isMetadata = isMetadata;
    }

    public Integer getDeployType() {
        return deployType;
    }

    public void setDeployType(Integer deployType) {
        this.deployType = deployType;
    }
}
