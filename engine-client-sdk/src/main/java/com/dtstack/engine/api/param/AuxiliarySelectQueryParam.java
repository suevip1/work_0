package com.dtstack.engine.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
/**
 * 组件附属配置 select 可选项查询参数类
 *
 * @author ：wangchuan
 * date：Created in 上午10:46 2022/5/12
 * company: www.dtstack.com
 */
public class AuxiliarySelectQueryParam {

    /**
     * 集群id
     */
    @NotNull(message = "集群 ID 不能为空")
    @ApiModelProperty(value = "集群 ID", required = true, example = "1")
    private Long clusterId;

    /**
     * 组件类型
     */
    @NotNull(message = "组件类型不能为空")
    @ApiModelProperty(value = "组件类型", required = true, example = "1")
    private Integer componentTypeCode;

    /**
     * 附属配置类型
     */
    @NotNull(message = "附属配置类型不能为空")
    @ApiModelProperty(value = "附属配置类型", example = "LOG_TRACE")
    private String type;

    /**
     * select Type
     */
    @NotNull(message = "附属配置 select 的 key 不能为空")
    @ApiModelProperty(value = "附属配置 select 选择器的 key", example = "traceType")
    private String selectKey;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelectKey() {
        return selectKey;
    }

    public void setSelectKey(String selectKey) {
        this.selectKey = selectKey;
    }

    @Override
    public String toString() {
        return "AuxiliarySelectQueryParam{" +
                "clusterId=" + clusterId +
                ", componentTypeCode=" + componentTypeCode +
                ", type='" + type + '\'' +
                ", selectKey='" + selectKey + '\'' +
                '}';
    }
}
