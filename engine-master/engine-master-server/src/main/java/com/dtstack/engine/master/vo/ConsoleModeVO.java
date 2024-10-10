package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2022-06-10 10:48
 **/
@ApiModel(value = "控制台模式")
public class ConsoleModeVO {

    @ApiModelProperty(value = "模式类型，1：升级维护模式")
    private Integer type;

    @ApiModelProperty(value = "是否开启，0：关闭，1：开启")
    private Integer active;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
