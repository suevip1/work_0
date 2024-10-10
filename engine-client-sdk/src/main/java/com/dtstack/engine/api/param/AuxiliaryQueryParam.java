package com.dtstack.engine.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 组件附属配置查询参数类
 *
 * @author ：wangchuan
 * date：Created in 上午10:46 2022/5/12
 * company: www.dtstack.com
 */
public class AuxiliaryQueryParam {

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
    @ApiModelProperty(value = "附属配置类型集合", example = "[LOG_TRACE]")
    private List<String> types;

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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "AuxiliaryQueryParam{" +
                "clusterId=" + clusterId +
                ", componentTypeCode=" + componentTypeCode +
                ", types=" + types +
                '}';
    }
}
