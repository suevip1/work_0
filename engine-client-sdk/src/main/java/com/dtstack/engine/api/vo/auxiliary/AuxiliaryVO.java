package com.dtstack.engine.api.vo.auxiliary;

import io.swagger.annotations.ApiModelProperty;

/**
 * 单条附属信息配置
 *
 * @author ：wangchuan
 * date：Created in 上午10:20 2022/5/12
 * company: www.dtstack.com
 */
public class AuxiliaryVO {

    /**
     * 配置 key
     */
    @ApiModelProperty(value = "附属配置的 key", example = "type")
    private String key;

    /**
     * 配置 value
     */
    @ApiModelProperty(value = "附属配置的 value", example = "value")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AuxiliaryVO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
