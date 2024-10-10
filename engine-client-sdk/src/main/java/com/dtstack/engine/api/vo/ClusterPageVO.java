package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author leon
 * @date 2022-09-19 10:47
 **/
@ApiModel("集群分页信息")
public class ClusterPageVO  {

    @ApiModelProperty("租户id")
    private Long clusterId;

    @ApiModelProperty("集群名称")
    private String clusterName;

    @ApiModelProperty(value = "集群绑定的租户")
    private List<TenantNameVO> bindingTenants;

    @ApiModelProperty(value = "集群是否包含 Hadoop 引擎,不包括 k8s")
    private Boolean hasHadoopEngine;

    @ApiModelProperty(notes = "修改时间")
    private Timestamp gmtModified;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<TenantNameVO> getBindingTenants() {
        return bindingTenants;
    }

    public void setBindingTenants(List<TenantNameVO> bindingTenants) {
        this.bindingTenants = bindingTenants;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Boolean getHasHadoopEngine() {
        return hasHadoopEngine;
    }

    public void setHasHadoopEngine(Boolean hasHadoopEngine) {
        this.hasHadoopEngine = hasHadoopEngine;
    }
}
