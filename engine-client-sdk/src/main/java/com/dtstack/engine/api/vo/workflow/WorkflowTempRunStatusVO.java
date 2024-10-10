package com.dtstack.engine.api.vo.workflow;

import com.dtstack.engine.api.vo.action.ActionJobEntityVO;

import java.util.List;

/**
 * @author leon
 * @date 2023-03-07 15:41
 **/
public class WorkflowTempRunStatusVO {

    /**
     * 工作流临时运行 jobId
     */
    private String flowJobId;

    /**
     * 工作流整体临时运行状态
     */
    private Integer flowJobStatus;

    /**
     * 工作流子任务临时运行状态
     */
    private List<ActionJobEntityVO> subJobStatuses;

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public Integer getFlowJobStatus() {
        return flowJobStatus;
    }

    public void setFlowJobStatus(Integer flowJobStatus) {
        this.flowJobStatus = flowJobStatus;
    }

    public List<ActionJobEntityVO> getSubJobStatuses() {
        return subJobStatuses;
    }

    public void setSubJobStatuses(List<ActionJobEntityVO> subJobStatuses) {
        this.subJobStatuses = subJobStatuses;
    }


    public static final class Builder {
        private String flowJobId;
        private Integer flowJobStatus;
        private List<ActionJobEntityVO> subJobStatuses;

        private Builder() {
        }

        public static Builder aWorkflowTempRunStatusVO() {
            return new Builder();
        }

        public Builder flowJobId(String flowJobId) {
            this.flowJobId = flowJobId;
            return this;
        }

        public Builder flowJobStatus(Integer flowJobStatus) {
            this.flowJobStatus = flowJobStatus;
            return this;
        }

        public Builder subJobStatuses(List<ActionJobEntityVO> subJobStatuses) {
            this.subJobStatuses = subJobStatuses;
            return this;
        }

        public WorkflowTempRunStatusVO build() {
            WorkflowTempRunStatusVO workflowTempRunStatusVO = new WorkflowTempRunStatusVO();
            workflowTempRunStatusVO.setFlowJobId(flowJobId);
            workflowTempRunStatusVO.setFlowJobStatus(flowJobStatus);
            workflowTempRunStatusVO.setSubJobStatuses(subJobStatuses);
            return workflowTempRunStatusVO;
        }
    }
}
