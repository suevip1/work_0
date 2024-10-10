package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2022-09-26 10:04
 **/
@ApiModel
public class StopJobResult {

    @ApiModelProperty(value = "未杀死的任务数量")
    private Integer notYetKilledJobCount;

    public Integer getNotYetKilledJobCount() {
        return notYetKilledJobCount;
    }

    public void setNotYetKilledJobCount(Integer notYetKilledJobCount) {
        this.notYetKilledJobCount = notYetKilledJobCount;
    }
}
