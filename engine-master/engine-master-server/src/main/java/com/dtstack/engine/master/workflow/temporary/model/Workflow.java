package com.dtstack.engine.master.workflow.temporary.model;

/**
 * @author leon
 * @date 2023-03-09 15:40
 **/
public class Workflow {

    private Graph graph;

    private String flowJobId;

    private Long flowId;

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }


    public static final class Builder {
        private Graph graph;
        private String flowJobId;
        private Long flowId;

        private Builder() {
        }

        public static Builder aWorkflow() {
            return new Builder();
        }

        public Builder graph(Graph graph) {
            this.graph = graph;
            return this;
        }

        public Builder flowJobId(String flowJobId) {
            this.flowJobId = flowJobId;
            return this;
        }

        public Builder flowId(Long flowId) {
            this.flowId = flowId;
            return this;
        }

        public Workflow build() {
            Workflow workflow = new Workflow();
            workflow.setGraph(graph);
            workflow.setFlowJobId(flowJobId);
            workflow.setFlowId(flowId);
            return workflow;
        }
    }
}
