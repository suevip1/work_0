package com.dtstack.engine.master.workflow.temporary.model;

import java.util.List;

/**
 * @author leon
 * @date 2023-03-09 15:37
 **/
public class Graph {

    List<Node> nodes;

    List<Edge> edges;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
