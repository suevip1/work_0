package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2022-09-13 14:16
 **/
@ApiModel
public class NotYetKilledJobCountVO {

    @ApiModelProperty(value = "计算类型")
    private String jobResource;

    @ApiModelProperty(value = "未杀死的任务数量")
    private Integer notYetKilledJobCount;

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public Integer getNotYetKilledJobCount() {
        return notYetKilledJobCount;
    }

    public void setNotYetKilledJobCount(Integer notYetKilledJobCount) {
        this.notYetKilledJobCount = notYetKilledJobCount;
    }
}
