package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.workflow.RunWorkflowParamVO;
import com.dtstack.engine.api.vo.workflow.RunWorkflowResultVO;
import com.dtstack.engine.api.vo.workflow.SubTaskActionParamVO;
import com.dtstack.engine.api.vo.workflow.WorkflowSubTaskActionParamVO;
import com.dtstack.engine.api.vo.workflow.WorkflowTempRunStatusVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.WorkflowDao;
import com.dtstack.engine.master.mapstruct.WorkflowStruct;
import com.dtstack.engine.master.workflow.temporary.GraphParser;
import com.dtstack.engine.master.workflow.temporary.WorkflowLock;
import com.dtstack.engine.master.workflow.temporary.WorkflowTemporaryJobEventListener;
import com.dtstack.engine.master.workflow.temporary.event.WorkflowTemporaryExecuteEvent;
import com.dtstack.engine.master.workflow.temporary.model.Graph;
import com.dtstack.engine.master.workflow.temporary.model.Workflow;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.dtstack.engine.po.WorkflowTmpRunInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author leon
 * @date 2023-03-08 14:01
 **/
@Service
public class WorkflowService implements ApplicationEventPublisherAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(WorkflowService.class);

    @Autowired
    private WorkflowLock workflowLock;

    @Autowired
    private WorkflowDao workflowDao;

    @Autowired
    private WorkflowStruct workflowStruct;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private WorkflowTemporaryJobEventListener workflowTemporaryJobEventListener;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void saveWorkflowSubTaskActionParam(WorkflowSubTaskActionParamVO vo) {
        String flowJobId = vo.getFlowJobId();
        List<SubTaskActionParamVO> subTaskActionParams = vo.getSubTaskActionParams();
        workflowLock.execWithLock(flowJobId, () -> {
            // 校验工作流临时运行实例是否已经存在
            checkWorkflowExist(flowJobId);
            // 保存子任务信息
            saveSubTaskActionParam(flowJobId, subTaskActionParams);
        }, e -> {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("floJobId: {} save workflow sub task action param error: {}",
                    flowJobId,
                    errorMessage, e);
            throw new RdosDefineException("flowJobId: %s save workflow sub task action param error: %s",
                    flowJobId,
                    errorMessage, e);
        }, true);
    }

    public RunWorkflowResultVO runWorkFlow(RunWorkflowParamVO param) {
        String flowJobId = param.getFlowJobId();
        return workflowLock.execWithLock(flowJobId, param, runWorkflowParamVO -> {
            // 校验工作流临时运行实例是否已经存在
            checkWorkflowExist(flowJobId);
            // 保存工作流信息
            Workflow workflow = saveWorkFlow(param);
            // 触发工作流执行
            triggerWorkflowExecute(workflow);

            List<RunWorkflowResultVO.SubJobInfo> subJobInfos = getSubJobInfos(flowJobId);

            return RunWorkflowResultVO.Builder.aRunWorkflowResultVO()
                    .flowId(param.getFlowId())
                    .flowJobId(param.getFlowJobId())
                    .subJobInfos(subJobInfos)
                    .build();
        }, e -> {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("flowJobId: {}, workflow temporary running error: {}", flowJobId, errorMessage, e);
            throw new RdosDefineException("flowJobId: %s, workflow temporary running error: %s", flowJobId, errorMessage);
        }, () -> {
            throw new RdosDefineException("flowJobId: %s, workflow temporary running error, lock not acquired", flowJobId);
        }, true);
    }


    public WorkflowTempRunStatusVO getStatus(String flowJobId) throws Exception {

        List<WorkflowSubTmpRunInfo> simpleWorkflowSubTmpRunInfos =
                workflowDao.getSimpleWorkflowSubTmpRunInfosByFlowJobId(flowJobId);

        List<String> jobIds = getSubJobIds(simpleWorkflowSubTmpRunInfos);

        WorkflowTmpRunInfo workflowTmpRunInfo = workflowDao.getWorkflowTmpRunInfo(flowJobId);
        List<ActionJobEntityVO> jobStatus = actionService.entitys(jobIds);

        if (CollectionUtils.isEmpty(jobStatus)) {
            return WorkflowTempRunStatusVO.Builder.aWorkflowTempRunStatusVO().build();
        }

        // 轮训时触发下工作流状态校验
        RdosTaskStatus workflowStatus = verifyWorkflowStatus(workflowTmpRunInfo, simpleWorkflowSubTmpRunInfos, jobStatus);

        return WorkflowTempRunStatusVO.Builder.aWorkflowTempRunStatusVO()
                .flowJobStatus(workflowStatus.getStatus())
                .subJobStatuses(jobStatus)
                .flowJobId(flowJobId).build();
    }

    private RdosTaskStatus verifyWorkflowStatus(WorkflowTmpRunInfo workflowTmpRunInfo,
                                                List<WorkflowSubTmpRunInfo> simpleWorkflowSubTmpRunInfos,
                                                List<ActionJobEntityVO> actionJobEntityVOS) {
        RdosTaskStatus currentWorkflowStatus = RdosTaskStatus.getTaskStatus(workflowTmpRunInfo.getStatus());
        String flowJobId = workflowTmpRunInfo.getFlowJobId();

        if (currentWorkflowStatus != RdosTaskStatus.RUNNING) {
            return currentWorkflowStatus;
        }

        List<Integer> jobStatus = actionJobEntityVOS.stream()
                .map(ActionJobEntityVO::getStatus)
                .collect(Collectors.toList());

        return workflowTemporaryJobEventListener.judgeWorkStatus(flowJobId, currentWorkflowStatus, simpleWorkflowSubTmpRunInfos, jobStatus);
    }

    public void stop(String flowJobId, Long userId) {
        List<String> subJobIds = getSubJobIds(flowJobId);
        List<String> needStopJobIds = filterOutStoppedJobs(subJobIds);
        if (CollectionUtils.isEmpty(needStopJobIds)) {
            return;
        }
        actionService.stop(needStopJobIds, userId);
    }

    private List<String> filterOutStoppedJobs(List<String> subJobIds) {
        List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(subJobIds);
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return Lists.newArrayList();
        }
        return scheduleJobs.stream()
                .filter(job -> !RdosTaskStatus.STOPPED_STATUS.contains(job.getStatus()))
                .map(ScheduleJob::getJobId)
                .collect(Collectors.toList());
    }


    private Workflow saveWorkFlow(RunWorkflowParamVO param) {
        Long flowId = param.getFlowId();
        String flowJobId = param.getFlowJobId();
        String graphJson = param.getGraph();

        // 解析工作流运行拓扑图
        Graph graph = GraphParser.parse(graphJson);

        WorkflowTmpRunInfo workflowTmpRunInfo = workflowStruct.toEntity(param);
        workflowTmpRunInfo.setStatus(RdosTaskStatus.SUBMITTING.getStatus());
        workflowDao.insertWorkflowTmpRunInfo(workflowTmpRunInfo);

        return Workflow.Builder.aWorkflow()
                .flowId(flowId)
                .flowJobId(flowJobId)
                .graph(graph).build();
    }


    private void saveSubTaskActionParam(String flowJobId,
                                        List<SubTaskActionParamVO> subTaskActionParams) {
        List<Long> taskList =
                subTaskActionParams.stream()
                        .map(SubTaskActionParamVO::getTaskId)
                        .collect(Collectors.toList());
        // 先删后插
        deleteExist(flowJobId, taskList);
        batchInsert(flowJobId, subTaskActionParams);
    }

    private void deleteExist(String flowJobId, List<Long> taskList) {
        workflowDao.deleteWorkflowSubRunInfoByFlowJobIdAndTaskIds(flowJobId, taskList);
    }


    private void batchInsert(String flowJobId,
                             List<SubTaskActionParamVO> subTaskActionParams) {

        List<WorkflowSubTmpRunInfo> workflowSubTmpRunInfos = workflowStruct.toEntities(subTaskActionParams);
        workflowSubTmpRunInfos
                .forEach(e -> {
                    e.setFlowJobId(flowJobId);
                    e.setJobId(actionService.generateUniqueSign());
                });

        workflowDao.batchInsertWorkflowSubRunInfo(workflowSubTmpRunInfos);
    }

    public void triggerWorkflowExecute(Workflow workflow) {
        LOGGER.info("trigger workflow execute, flowJobId:{}, graph:{}",
                workflow.getFlowJobId(),
                JSONObject.toJSONString(workflow.getGraph()));
        applicationEventPublisher.publishEvent(new WorkflowTemporaryExecuteEvent(workflow));
    }

    @NotNull
    private List<String> getSubJobIds(String flowJobId) {
        List<WorkflowSubTmpRunInfo> simpleWorkflowSubTmpRunInfos =
                workflowDao.getSimpleWorkflowSubTmpRunInfosByFlowJobId(flowJobId);
        return getSubJobIds(simpleWorkflowSubTmpRunInfos);
    }

    private List<String> getSubJobIds(List<WorkflowSubTmpRunInfo> simpleWorkflowSubTmpRunInfos) {
        return simpleWorkflowSubTmpRunInfos.stream().map(WorkflowSubTmpRunInfo::getJobId)
                .collect(Collectors.toList());
    }

    private List<RunWorkflowResultVO.SubJobInfo> getSubJobInfos(String flowJobId) {
        List<WorkflowSubTmpRunInfo> simpleSubTmpRunInfo =
                workflowDao.getSimpleWorkflowSubTmpRunInfosByFlowJobId(flowJobId);
        return workflowStruct.toSubJobInfos(simpleSubTmpRunInfo);
    }

    private void checkWorkflowExist(String flowJobId) {
        WorkflowTmpRunInfo workflowTmpRunInfo = workflowDao.getWorkflowTmpRunInfo(flowJobId);
        if (Objects.nonNull(workflowTmpRunInfo)) {
            throw new RdosDefineException("flowJobId already exist %s", flowJobId);
        }
    }
}
