package com.dtstack.engine.api.vo.workflow;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author leon
 * @date 2023-03-07 15:24
 **/
@ApiModel
public class WorkflowSubTaskActionParamVO {

    /**
     * 工作流临时运行 jobId, 作为工作流临时运行动作的唯一标识
     */
    private String flowJobId;

    /**
     * 工作流临时运行子任务执行参数
     */
    private List<SubTaskActionParamVO> subTaskActionParamVOS;


    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public List<SubTaskActionParamVO> getSubTaskActionParams() {
        return subTaskActionParamVOS;
    }

    public void setSubTaskActionParams(List<SubTaskActionParamVO> subTaskActionParamVOS) {
        this.subTaskActionParamVOS = subTaskActionParamVOS;
    }

}
