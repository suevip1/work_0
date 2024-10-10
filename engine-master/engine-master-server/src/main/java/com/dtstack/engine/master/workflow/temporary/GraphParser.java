package com.dtstack.engine.master.workflow.temporary;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.workflow.temporary.model.Edge;
import com.dtstack.engine.master.workflow.temporary.model.Graph;
import com.dtstack.engine.master.workflow.temporary.model.Node;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author leon
 * @date 2023-03-09 15:37
 **/
public class GraphParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(GraphParser.class);


    public static Graph parse(String graphJson) {
        try {
            Graph graph = doParse(graphJson);
            checkCycle(graph);
            return graph;
        } catch (Throwable e) {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("parse workflow graph error: {}, graphJson: {}",
                    errorMessage,
                    graphJson,
                    e);
            throw new RdosDefineException("parse workflow graph error: %s, graphJson: %s", errorMessage, graphJson, e);
        }
    }

    private static Graph doParse(String graphJson) {
        return JSONObject.parseObject(graphJson, Graph.class);
    }

    public static void checkCycle(Graph graph) {
        List<Node> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();
        if (CollectionUtils.isEmpty(nodes) || CollectionUtils.isEmpty(edges)) {
            return;
        }

        Set<Long> visited = new HashSet<>();
        Set<Long> recStack = new HashSet<>();

        for (Node node : nodes) {
            if (!visited.contains(node.getTaskId())) {
                if (hasCycle(node.getTaskId(), visited, recStack, edges)) {
                    throw new RdosDefineException("parse workflow graph error, has cycle, graphJson: %s",
                            JSONObject.toJSON(graph));
                }
            }
        }
    }

    private static boolean hasCycle(Long taskId, Set<Long> visited, Set<Long> recStack, List<Edge> edges) {
        visited.add(taskId);
        recStack.add(taskId);

        for (Edge edge : edges) {
            if (edge.getFrom().equals(taskId)) {
                if (!visited.contains(edge.getTo())) {
                    if (hasCycle(edge.getTo(), visited, recStack, edges)) {
                        return true;
                    }
                } else if (recStack.contains(edge.getTo())) {
                    return true;
                }
            }
        }

        recStack.remove(taskId);
        return false;
    }
}
