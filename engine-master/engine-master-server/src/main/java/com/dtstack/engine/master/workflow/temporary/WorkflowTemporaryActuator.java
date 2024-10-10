package com.dtstack.engine.master.workflow.temporary;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.workflow.temporary.event.WorkflowTemporaryStatusEvent;
import com.dtstack.engine.master.workflow.temporary.model.Edge;
import com.dtstack.engine.master.workflow.temporary.model.Graph;
import com.dtstack.engine.master.workflow.temporary.model.Node;
import com.dtstack.engine.master.workflow.temporary.model.Workflow;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 工作流临时运行执行器
 * 由 WorkflowTemporaryAcceptor#run() 触发 this#execute 执行
 *
 * @author leon
 * @date 2023-03-09 15:44
 * @see WorkflowTemporaryAcceptor#run()
 **/
@Component
public class WorkflowTemporaryActuator implements ApplicationEventPublisherAware {

    private final Logger LOGGER = LoggerFactory.getLogger(WorkflowTemporaryActuator.class);

    private final ThreadPoolTaskExecutor workflowTemporaryExecutor;

    private final ActionService actionService;

    private final WorkflowDBOperator workflowDBOperator;

    private final WorkflowLock workflowLock;

    private ApplicationEventPublisher applicationEventPublisher;

    private final WorkflowTemporaryJobEventListener workflowTemporaryJobEventListener;

    private final ScheduleJobService scheduleJobService;

    private final WorkflowConfig workflowConfig;

    public WorkflowTemporaryActuator(ThreadPoolTaskExecutor workflowTemporaryExecutor,
                                     ActionService actionService,
                                     WorkflowDBOperator workflowDBOperator,
                                     WorkflowLock workflowLock,
                                     WorkflowTemporaryJobEventListener workflowTemporaryJobEventListener,
                                     ScheduleJobService scheduleJobService,
                                     WorkflowConfig workflowConfig) {
        this.workflowTemporaryExecutor = workflowTemporaryExecutor;
        this.actionService = actionService;
        this.workflowDBOperator = workflowDBOperator;
        this.workflowLock = workflowLock;
        this.workflowTemporaryJobEventListener = workflowTemporaryJobEventListener;
        this.scheduleJobService = scheduleJobService;
        this.workflowConfig = workflowConfig;
    }

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    public void execute(Workflow workflow) throws InterruptedException {
        if (Objects.isNull(workflow) || Objects.isNull(workflow.getGraph())) {
            return;
        }
        LOGGER.info("flowJobId:{},workflow starts executing",
                workflow.getFlowJobId());
        // check
        checkWorkflow(workflow);
        // invoke
        invokeWithLock(workflow);
    }


    private void invokeWithLock(Workflow workflow) {
        String flowJobId = workflow.getFlowJobId();
        workflowLock.execWithLock(workflow.getFlowJobId(), () -> {
            RdosTaskStatus workflowStatus = workflowDBOperator.getWorkflowStatus(flowJobId);
            if (RdosTaskStatus.RUNNING.equals(workflowStatus)) {
                LOGGER.warn("flowJobId:{},workflow execution failed because state is already running!",
                        flowJobId);
                return;
            }
            workflowDBOperator.updateWorkflowStatus(workflow, RdosTaskStatus.RUNNING, "");
            triggerWorkflowStatusIntrospect(workflow, RdosTaskStatus.RUNNING);
            invoke(workflow);
        }, throwable -> {
            String errorMessage = ExceptionUtil.getErrorMessage(throwable);
            LOGGER.error("flowJobId:{},workflow execution failed {}", flowJobId, errorMessage);
            throw new RdosDefineException("workflow execution failed %s",
                    errorMessage);
        });
    }

    public void triggerWorkflowStatusIntrospect(Workflow workflow, RdosTaskStatus status) {
        String flowJobId = workflow.getFlowJobId();
        LOGGER.info("flowJobId:{},trigger workflow status introspect, graph:{}",
                workflow.getFlowJobId(),
                JSONObject.toJSONString(workflow.getGraph()));

        applicationEventPublisher.publishEvent(
                new WorkflowTemporaryStatusEvent(
                        new WorkflowTemporaryStatusEvent.WorkflowTemporaryStatusEventWrapper(flowJobId, status)));
    }

    private void invoke(Workflow workflow) {
        String flowJobId = workflow.getFlowJobId();
        Long flowId = workflow.getFlowId();
        Graph graph = workflow.getGraph();

        List<Node> nodes = Optional.ofNullable(graph.getNodes()).orElse(Lists.newArrayList());
        List<Edge> edges = Optional.ofNullable(graph.getEdges()).orElse(Lists.newArrayList());

        // taskId -> Node
        Map<Long, Node> allTasks = nodes.stream().collect(Collectors.toMap(Node::getTaskId, a -> a, (k1, k2) -> k1));
        // to -> parentTasks
        Map<Long, List<Edge>> parentTasks = edges.stream().collect(Collectors.groupingBy(Edge::getTo));

        Map<Long, CompletableFuture<RdosTaskStatus>> futureMap = new HashMap<>();

        for (Long taskId : allTasks.keySet()) {
            doInvoke(flowJobId, flowId, allTasks, parentTasks, futureMap, taskId);
        }
    }

    private void doInvoke(String flowJobId,
                          Long flowId, Map<Long, Node> allTasks,
                          Map<Long, List<Edge>> parentTasks,
                          Map<Long, CompletableFuture<RdosTaskStatus>> futureMap,
                          Long taskId) {
        // 已经执行过就不执行了
        if (Objects.nonNull(futureMap.get(taskId))) {
            return;
        }
        // 先创建一个 cf
        CompletableFuture<RdosTaskStatus> future = futureMap.computeIfAbsent(taskId, k -> new CompletableFuture<>());
        Node node = allTasks.get(taskId);

        if (parentTasks.containsKey(taskId)) {
            List<Edge> dependentNodes = parentTasks.get(taskId);
            List<CompletableFuture<RdosTaskStatus>> dependentFutures = new ArrayList<>();

            for (Edge dependentNode : dependentNodes) {
                if (Objects.nonNull(dependentNode)) {
                    Long from = dependentNode.getFrom();
                    if (Objects.nonNull(from) && Objects.isNull(futureMap.get(from))) {
                        // 先执行上游
                        doInvoke(flowJobId, flowId, allTasks, parentTasks, futureMap, from);
                    }
                    dependentFutures.add(futureMap.get(from));
                }
            }
            // 阻塞获取所有上游 cf 的结果
            Set<RdosTaskStatus> dependentResults = getDependentResults(dependentFutures, flowJobId, taskId);

            // 上游并非全部成功，当前 cf 置为失败，让下游不执行
            if (!upstreamAreSuccessful(dependentResults)) {
                // 上游有因为条件分支取消的任务，下游也置为取消
                if (dependentResults.contains(RdosTaskStatus.AUTOCANCELED)) {
                    future.complete(RdosTaskStatus.AUTOCANCELED);
                    futureMap.put(node.getTaskId(), future);
                    workflowDBOperator.updateSubJobSkip(flowJobId, flowId,node.getTaskId());
                } else {
                    future.complete(RdosTaskStatus.FAILED);
                    futureMap.put(node.getTaskId(), future);
                    workflowDBOperator.updateSubJobParentFail(flowJobId, flowId,node.getTaskId());
                }
                LOGGER.error("flowJobId:{},workflow sub job terminated because the upstream execution failed, terminate taskId:{}",
                        flowJobId,
                        taskId);
                return;
            }
            // 开始执行当前节点
            runTask(flowJobId, flowId, future, node);
        } else {
            runTask(flowJobId, flowId, future, node);
        }
    }

    private void runTask(String flowJobId,
                         Long flowId,
                         CompletableFuture<RdosTaskStatus> future,
                         Node node) {
        Runnable runnable = buildJobRunnable(node, flowId, flowJobId);
        String jobId = getJobId(node, flowJobId, flowId);

        WorkflowSubTmpRunInfo subTmpRunInfo = workflowDBOperator.getSubTmpRunInfo(flowJobId, flowId, node.getTaskId());

        // 条件分支取消的任务
        if (subTmpRunInfo.getIsSkip() == 1) {
            future.complete(RdosTaskStatus.AUTOCANCELED);
            LOGGER.info("flowJobId:{},workflow child node skip bu condition, child node taskId:{}, jobId:{}",
                    flowJobId,
                    node.getTaskId(),
                    jobId);
            return;
        }

        WorkflowTemporaryPerRunner runner = new WorkflowTemporaryPerRunner(
                jobId,
                runnable,
                future,
                workflowTemporaryJobEventListener, scheduleJobService);

        LOGGER.info("flowJobId:{},workflow starts to execute child node, child node taskId:{}, jobId:{}",
                flowJobId,
                node.getTaskId(),
                jobId);

        CompletableFuture.runAsync(runner, workflowTemporaryExecutor);
    }


    private String getJobId(Node node, String flowJobId, Long flowId) {
        Long taskId = Optional.ofNullable(node).map(Node::getTaskId).orElse(null);
        WorkflowSubTmpRunInfo workflowSubTmpRunInfo = workflowDBOperator.getSubTmpRunInfo(flowJobId, flowId, taskId);
        return Optional.ofNullable(workflowSubTmpRunInfo).map(WorkflowSubTmpRunInfo::getJobId).orElse(null);
    }

    private boolean upstreamAreSuccessful(Set<RdosTaskStatus> dependentResults) {
        return Objects.nonNull(dependentResults) && dependentResults.size() == 1 && dependentResults.contains(RdosTaskStatus.FINISHED);
    }

    @NotNull
    private Set<RdosTaskStatus> getDependentResults(List<CompletableFuture<RdosTaskStatus>> dependentFutures, String flowJobId, Long taskId) {
        return dependentFutures.stream().map(d -> {
            // 默认三小时超时时间
            long timeOut = Optional.ofNullable(workflowConfig.getWorkflowTemporaryPerTaskExeTimeMaxLimitHour()).orElse(3L);
            try {
                return d.get(timeOut, TimeUnit.HOURS);
            } catch (Throwable e) {
                LOGGER.error("flowJobId:{},workflow execution, task taskId: {} block to get upstream execution result timed out:{}, timeout limit: {}",
                        flowJobId,
                        taskId,
                        ExceptionUtil.getErrorMessage(e),
                        timeOut, e);
                return RdosTaskStatus.FAILED;
            }
        }).collect(Collectors.toSet());
    }

    private void checkWorkflow(Workflow workflow) {
        if (Objects.nonNull(workflow) &&
                (Objects.isNull(workflow.getFlowId()) || StringUtils.isBlank(workflow.getFlowJobId()))) {
            throw new RdosDefineException("workflow execution check status failed, flowId or flowJobId cannot be null");
        }
    }


    private Runnable buildJobRunnable(Node node, Long flowId, String flowJobId) {
        Long taskId = node.getTaskId();
        ParamTaskAction paramTaskAction = workflowDBOperator.getParamTaskAction(flowJobId, flowId, taskId);
        return () -> actionService.startJobForWorkflowTemporary(paramTaskAction);
    }

}
