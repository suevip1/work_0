package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2022-09-13 14:18
 **/
@ApiModel
public class QueryNotYetKilledJobCountVO {

    @ApiModelProperty(value = "计算类型",required = true)
    private String jobResource;

    @ApiModelProperty(value = "节点")
    private String nodeAddress;

    @ApiModelProperty(value = "状态",example = "1：已存储，2：队列中，3：等待重试，4：等待资源，5：运行中")

    private Integer stage;

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }
}
