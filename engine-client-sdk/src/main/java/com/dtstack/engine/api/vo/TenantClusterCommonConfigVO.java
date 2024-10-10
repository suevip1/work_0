package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2023-06-13 05:56
 **/
@ApiModel("租户的集群通用配置")
public class TenantClusterCommonConfigVO {

    @ApiModelProperty(value = "是否允许下载select查询结果")
    private Boolean enableDownloadResult;

    @ApiModelProperty(value = "select下载条数上限")
    private Long downloadLimit;

    @ApiModelProperty(value = "select查询条数上限")
    private Long selectLimit;

    public Boolean getEnableDownloadResult() {
        return enableDownloadResult;
    }

    public void setEnableDownloadResult(Boolean enableDownloadResult) {
        this.enableDownloadResult = enableDownloadResult;
    }

    public Long getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(Long selectLimit) {
        this.selectLimit = selectLimit;
    }

    public Long getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(Long downloadLimit) {
        this.downloadLimit = downloadLimit;
    }
}
