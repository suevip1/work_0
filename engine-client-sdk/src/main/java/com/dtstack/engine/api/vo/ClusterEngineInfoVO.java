package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2022-09-19 19:39
 **/
@ApiModel("集群引擎信息")
public class ClusterEngineInfoVO {

    @ApiModelProperty(value = "引擎名称")
    private String engineName;

    @ApiModelProperty(value = "引擎类别")
    private Integer engineType;

    @ApiModelProperty(value = "是否可以绑定账号")
    private boolean canBindAccount;

    @ApiModelProperty(value = "资源组件类型，on yarn/on k8s")
    private String resourceType;

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public boolean getCanBindAccount() {
        return canBindAccount;
    }

    public void setCanBindAccount(boolean canBindAccount) {
        this.canBindAccount = canBindAccount;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
