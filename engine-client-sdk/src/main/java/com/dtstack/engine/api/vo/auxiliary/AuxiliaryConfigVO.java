package com.dtstack.engine.api.vo.auxiliary;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 附属配置 vo
 *
 * @author ：wangchuan
 * date：Created in 上午10:15 2022/5/12
 * company: www.dtstack.com
 */
public class AuxiliaryConfigVO {

    /**
     * 集群id
     */
    @ApiModelProperty(value = "集群 ID", example = "1")
    private Long clusterId;

    /**
     * 组件类型
     */
    @ApiModelProperty(value = "组件类型", example = "1")
    private Integer componentTypeCode;

    /**
     * 配置类型, 如 LOG_TRACE
     */
    @ApiModelProperty(value = "附属配置类型", example = "LOG_TRACE")
    private String type;

    /**
     * 配置信息详情
     */
    @ApiModelProperty(value = "单条配置信息")
    private List<AuxiliaryVO> auxiliaries;

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

    public List<AuxiliaryVO> getAuxiliaries() {
        return auxiliaries;
    }

    public void setAuxiliaries(List<AuxiliaryVO> auxiliaries) {
        this.auxiliaries = auxiliaries;
    }
}


