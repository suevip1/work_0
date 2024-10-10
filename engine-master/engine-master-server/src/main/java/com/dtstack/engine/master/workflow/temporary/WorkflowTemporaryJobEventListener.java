package com.dtstack.engine.master.workflow.temporary;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.event.ScheduleJobBatchEvent;
import com.dtstack.engine.master.event.ScheduleJobEventListener;
import com.dtstack.engine.master.event.ScheduleJobEventPublisher;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.workflow.temporary.event.WorkflowTemporaryStatusEvent;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * 工作流临时运行状态监听器，主要做两件事：
 * <ul>
 * <li>(1) 解除阻塞的 WorkflowTemporaryPerRunner 线程</li>
 * <li>(2) 更新工作流整体的运行状态</li>
 * </ul>
 *
 * @author leon
 * @date 2023-03-15 16:09
 * @see this#unParkRunner
 * @see this#introspectWorkflowStatus
 **/
@Component
public class WorkflowTemporaryJobEventListener
        implements ScheduleJobEventListener, ApplicationListener<WorkflowTemporaryStatusEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(WorkflowTemporaryJobEventListener.class);

    private static final ConcurrentHashSet<String> ACTIVE_TEMPORARY_WORKFLOW = new ConcurrentHashSet<>();

    private final ConcurrentHashMap<String, Thread> BLOCKED_WORKFLOW_PER_RUNNERS = new ConcurrentHashMap<>();

    private final ScheduleJobService scheduleJobService;

    private final WorkflowDBOperator workflowDBOperator;

    public WorkflowTemporaryJobEventListener(ScheduleJobService scheduleJobService,
                                             WorkflowDBOperator workflowDBOperator) {
        this.scheduleJobService = scheduleJobService;
        this.workflowDBOperator = workflowDBOperator;
    }

    @PostConstruct
    public void registerEvent() {
        ScheduleJobEventPublisher.getInstance().register(this);
    }

    @Override
    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        List<String> jobIds = event.getJobIds();
        Integer status = event.getStatus();

        // 这里只关心停止状态
        List<Integer> canStopStatus = RdosTaskStatus.getCanStopStatus();
        if (canStopStatus.contains(status)) {
            return;
        }

        List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(jobIds);

        for (ScheduleJob scheduleJob : scheduleJobs) {
            String flowJobId = scheduleJob.getFlowJobId();
            String jobId = scheduleJob.getJobId();

            unParkRunner(flowJobId, jobId, status);

            if (!ACTIVE_TEMPORARY_WORKFLOW.contains(flowJobId)) {
                return;
            }

            LOGGER.info("flowJobId:{},jobId:{},introspect workflow status, status: {}",
                    flowJobId,
                    jobId,
                    status);

            introspectWorkflowStatus(flowJobId);
        }

    }

    private void unParkRunner(String flowJobId, String jobId, Integer status) {
        Thread runner = BLOCKED_WORKFLOW_PER_RUNNERS.get(jobId);
        if (Objects.isNull(runner)) {
            return;
        }
        if (RdosTaskStatus.STOPPED_STATUS.contains(status)) {
            LockSupport.unpark(runner);
            BLOCKED_WORKFLOW_PER_RUNNERS.remove(jobId);
            LOGGER.info("flowJobId:{},jobId:{},unPark workflow job and remove from [BLOCKED_WORKFLOW_PER_RUNNERS], unPark runner threadId:{},status:{},",
                    flowJobId,
                    jobId,
                    runner.getId(),
                    status);
        }
    }

    private void introspectWorkflowStatus(String flowJobId) {
        RdosTaskStatus currentWorkflowStatus = workflowDBOperator.getWorkflowStatus(flowJobId);
        removeActiveTemporaryWorkflowInCondition(flowJobId, currentWorkflowStatus);

        if (!ACTIVE_TEMPORARY_WORKFLOW.contains(flowJobId)) {
            return;
        }

        List<WorkflowSubTmpRunInfo> subTmpRunInfos = workflowDBOperator.getSubTmpRunInfos(flowJobId);
        List<Integer> subJobStatus = getSubJobStatus(subTmpRunInfos);
        // 根据工作流中子任务的状态判断工作流整体的状态
        judgeWorkStatus(flowJobId, currentWorkflowStatus, subTmpRunInfos, subJobStatus);
    }


    public RdosTaskStatus judgeWorkStatus(String flowJobId,
                                 RdosTaskStatus currentWorkflowStatus,
                                 List<WorkflowSubTmpRunInfo> subTmpRunInfos,
                                 List<Integer> subJobStatus) {
        // 从子任务状态计算工作流状态
        RdosTaskStatus judgeStatus = WorkflowStatusJudge.getJudgeStatus(subJobStatus, subTmpRunInfos);

        if (Objects.isNull(judgeStatus) || judgeStatus.equals(currentWorkflowStatus)) {
            return currentWorkflowStatus;
        }
        workflowDBOperator.updateWorkflowStatus(flowJobId, judgeStatus);
        removeActiveTemporaryWorkflowInCondition(flowJobId, currentWorkflowStatus);
        return judgeStatus;
    }

    private List<Integer> getSubJobStatus(List<WorkflowSubTmpRunInfo> subTmpRunInfos) {
        List<String> subJobIds = subTmpRunInfos.stream()
                .map(WorkflowSubTmpRunInfo::getJobId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(subJobIds)) {
            return Lists.newArrayList();
        }
        List<ScheduleJob> subJobs = scheduleJobService.listByJobIds(subJobIds);
        return subJobs.stream()
                .map(ScheduleJob::getStatus).collect(Collectors.toList());
    }

    private void removeActiveTemporaryWorkflowInCondition(String flowJobId, RdosTaskStatus currentWorkflowStatus) {
        if (RdosTaskStatus.STOPPED_STATUS.contains(currentWorkflowStatus.getStatus())
                || RdosTaskStatus.NOTFOUND.getStatus().equals(currentWorkflowStatus.getStatus())) {
            ACTIVE_TEMPORARY_WORKFLOW.remove(flowJobId);

            List<String> subJobIds = workflowDBOperator.getSubJobIds(flowJobId);
            subJobIds.forEach(BLOCKED_WORKFLOW_PER_RUNNERS::remove);
        }
    }


    @Override
    public void onApplicationEvent(WorkflowTemporaryStatusEvent event) {
        WorkflowTemporaryStatusEvent.WorkflowTemporaryStatusEventWrapper wrapper =
                (WorkflowTemporaryStatusEvent.WorkflowTemporaryStatusEventWrapper) event.getSource();
        if (Objects.isNull(wrapper)) {
            return;
        }
        RdosTaskStatus status = wrapper.getStatus();
        String flowJobId = wrapper.getFlowJobId();

        if (RdosTaskStatus.STOPPED_STATUS.contains(status.getStatus())) {
            ACTIVE_TEMPORARY_WORKFLOW.remove(flowJobId);
            return;
        }

        ACTIVE_TEMPORARY_WORKFLOW.add(flowJobId);
    }


    public void registerRunner(String jobId, Thread runner) {
        LOGGER.info("jobId:{}, register workflow per runner,threadId:{}, already exist:{}",
                jobId,
                runner.getId(),
                Objects.nonNull(this.BLOCKED_WORKFLOW_PER_RUNNERS.get(jobId)));
        if (BLOCKED_WORKFLOW_PER_RUNNERS.containsKey(jobId)) {
            return;
        }
        this.BLOCKED_WORKFLOW_PER_RUNNERS.put(jobId, runner);
    }

}
