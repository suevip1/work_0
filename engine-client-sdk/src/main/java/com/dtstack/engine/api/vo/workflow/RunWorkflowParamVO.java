package com.dtstack.engine.api.vo.workflow;

/**
 * @author leon
 * @date 2023-03-07 15:32
 **/
public class RunWorkflowParamVO {

    /**
     * 工作流 taskId
     */
    private Long flowId;

    /**
     * 工作流临时运行 jobId
     */
    private String flowJobId;

    /**
     * 需要执行的工作流节点关系图
     * {
     *         "nodes": [
     *             { "taskId": "1"},
     *             { "taskId": "2"},
     *             { "taskId": "3"},
     *             { "taskId": "4"},
     *             { "taskId": "5"},
     *             { "taskId": "6"}
     *         ],
     *         "edges": [
     *             { "from": "1", "to": "2" },
     *             { "from": "1", "to": "3" },
     *             { "from": "2", "to": "4" },
     *             { "from": "3", "to": "4" },
     *             { "from": "4", "to": "5" },
     *             { "from": "4", "to": "6" },
     *             { "from": "5", "to": "6" }
     *         ]
     * }
     *
     */
    private String graph;

    private Integer appType;

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

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
