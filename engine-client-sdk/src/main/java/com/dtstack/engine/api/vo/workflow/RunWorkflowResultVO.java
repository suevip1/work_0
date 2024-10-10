package com.dtstack.engine.api.vo.workflow;

import java.util.List;

/**
 * @author leon
 * @date 2023-03-07 15:34
 **/
public class RunWorkflowResultVO {

    /**
     * 工作流 taskId
     */
    private Long flowId;

    /**
     * 工作流临时运行 jobId
     */
    private String flowJobId;

    /**
     * 子任务信息
     */
    private List<SubJobInfo> subJobInfos;

    public static class SubJobInfo {
        private Long taskId;
        private String jobId;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
    }

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

    public List<SubJobInfo> getSubJobInfos() {
        return subJobInfos;
    }

    public void setSubJobInfos(List<SubJobInfo> subJobInfos) {
        this.subJobInfos = subJobInfos;
    }


    public static final class Builder {
        private Long flowId;
        private String flowJobId;
        private List<SubJobInfo> subJobInfos;

        private Builder() {
        }

        public static Builder aRunWorkflowResultVO() {
            return new Builder();
        }

        public Builder flowId(Long flowId) {
            this.flowId = flowId;
            return this;
        }

        public Builder flowJobId(String flowJobId) {
            this.flowJobId = flowJobId;
            return this;
        }

        public Builder subJobInfos(List<SubJobInfo> subJobInfos) {
            this.subJobInfos = subJobInfos;
            return this;
        }

        public RunWorkflowResultVO build() {
            RunWorkflowResultVO runWorkflowResultVO = new RunWorkflowResultVO();
            runWorkflowResultVO.setFlowId(flowId);
            runWorkflowResultVO.setFlowJobId(flowJobId);
            runWorkflowResultVO.setSubJobInfos(subJobInfos);
            return runWorkflowResultVO;
        }
    }
}
