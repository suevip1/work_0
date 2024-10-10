package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Component extends BaseEntity {

    private String componentName;

    private Integer componentTypeCode;

    private Long clusterId;

    /**
     * 组件版本值
     */
    private String hadoopVersion;

    /**
     * 组件版本名称
     */
    private String versionName;

    /**
     * 上传配置文件名称
     */
    @ApiModelProperty(notes = "上传配置文件名称")
    private String uploadFileName;

    /**
     * kerberos文件名称
     */
    @ApiModelProperty(notes = "kerberos文件名称")
    private String kerberosFileName;

    @ApiModelProperty(notes = "ssl文件名称")
    private String sslFileName;

    /**
     * 存储组件名称
     */
    @ApiModelProperty(notes = "存储组件名称")
    private Integer storeType;

    /**
     * 当前组件是否为默认版本
     */
    private Boolean isDefault;



    @ApiModelProperty(notes = "是否为元数据")
    private Integer isMetadata;

    private Integer deployType;

    public Integer getDeployType() {
        return deployType;
    }

    public void setDeployType(Integer deployType) {
        this.deployType = deployType;
    }

    public Integer getIsMetadata() {
        return isMetadata;
    }

    public void setIsMetadata(Integer isMetadata) {
        this.isMetadata = isMetadata;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public String getKerberosFileName() {
        return kerberosFileName;
    }

    public void setKerberosFileName(String kerberosFileName) {
        this.kerberosFileName = kerberosFileName;
    }

    public String getSslFileName() {
        return sslFileName;
    }

    public void setSslFileName(String sslFileName) {
        this.sslFileName = sslFileName;
    }

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

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

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
