package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

import javax.validation.constraints.NotNull;

/**
 * @author leon
 * @date 2023-03-07 17:00
 **/
public class WorkflowTmpRunInfo extends BaseEntity {

    @NotNull
    private Long  flowId;

    @NotNull
    private String flowJobId;

    @NotNull
    private Integer appType;

    @NotNull
    private Integer status;

    private String graph;

    private String errorMsg;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
