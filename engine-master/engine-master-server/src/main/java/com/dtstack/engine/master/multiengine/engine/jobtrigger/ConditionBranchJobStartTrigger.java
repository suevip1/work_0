package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.WorkflowDao;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.multiengine.engine.condition.ConditionBranchDTO;
import com.dtstack.engine.master.multiengine.engine.condition.ConditionResult;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.workflow.temporary.GraphParser;
import com.dtstack.engine.master.workflow.temporary.model.Edge;
import com.dtstack.engine.master.workflow.temporary.model.Graph;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.dtstack.engine.po.WorkflowTmpRunInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 条件任务处理
 *
 * @author ：wangchuan
 * date：Created in 下午8:28 2022/6/28
 * company: www.dtstack.com
 */
@Component
public class ConditionBranchJobStartTrigger extends JobStartTriggerBase {

    @Resource
    private JobParamReplace jobParamReplace;

    @Resource
    private JobChainParamHandler jobChainParamHandler;

    @Resource
    private ScheduleJobService scheduleJobService;

    @Resource
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Resource
    private WorkflowDao workflowDao;

    /**
     * log
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionBranchJobStartTrigger.class);

    /**
     * EL parser
     */
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    /**
     * evaluation context
     */
    private static final StandardEvaluationContext CONTEXT = new StandardEvaluationContext();

    /**
     * 分支条件输出日志格式
     */
    private static final String INFO_FORMAT = "分支\"%s\": %s ";

    /**
     * 错误信息格式
     */
    private static final String ERROR_FORMAT = "msg: %s";

    public static final String CONDITION_AUTO_CANCEL_KEY = "依赖链路中存在条件分支任务未满足, 任务自动取消";

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) {
        String sql = (String) actionParam.getOrDefault("sqlText", "");
        if (StringUtils.isBlank(sql)) {
            return;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        // 替换非上下游参数
        String replace = jobParamReplace.spElReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), taskShade.getProjectId());
        // 处理任务上下游参数
        JobChainParamHandleResult jobChainParamHandleResult = jobChainParamHandler.handle(replace, taskShade, taskParamsToReplace, scheduleJob);
        String resultSql = jobChainParamHandleResult.getSql();
        LOGGER.info("{}, sqlTextFromActionParam, jobId:{}, after replace, sql:「{}」, taskParams:「{}」", this.getClass().getName(), scheduleJob.getJobId(), resultSql, jobChainParamHandleResult.getTaskParams());
        actionParam.put("sqlText", resultSql);
    }

    /**
     * 处理条件分支任务
     *
     * @param conditionJson 条件分支任务 json
     * @param jobId         jobId
     */
    public void handleConditionTask(String conditionJson, String jobId, String flowJobId) {
        if (StringUtils.isBlank(conditionJson)) {
            updateStatusAndLog(jobId, RdosTaskStatus.FAILED.getStatus(), "afferent condition json is empty");
            return;
        }
        try {
            List<ConditionBranchDTO> conditionList = JSON.parseArray(conditionJson, ConditionBranchDTO.class);
            Set<Long> canRunChildTaskIds = Sets.newHashSet();
            StringBuilder logInfo = new StringBuilder();
            for (ConditionBranchDTO conditionBranchDTO : conditionList) {
                ConditionResult conditionResult = handleCondition(conditionBranchDTO);
                logInfo.append(String.format(INFO_FORMAT, conditionBranchDTO.getName(), conditionResult.isResult()));
                if (conditionResult.isResult()) {
                    // 符合条件分支的任务
                    canRunChildTaskIds.addAll(conditionBranchDTO.getChildTaskIdList());
                } else {
                    logInfo.append(String.format(ERROR_FORMAT, conditionResult.getMsg()));
                }
                logInfo.append("\n");
            }
            handleSubJob(jobId, flowJobId, canRunChildTaskIds, logInfo);
        } catch (Throwable e) {
            // 推送错误日志并更新任务状态
            updateStatusAndLog(jobId, RdosTaskStatus.FAILED.getStatus(), ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * 1. 如果当前条件分支任务同一周期内无下游任务, 则当前任务状态变更为已完成
     * 2. 如果当前条件分支任务同一周期内有下游任务但全部未命中, 则当前任务和下游所有任务状态变更为自动取消
     * 3. 只要有一个分支条件符合, 则该分支挂载的下游任务就会执行, 不符合的下游任务状态变更为自动取消
     *
     * @param jobId              jobId
     * @param flowJobId          flowJobId
     * @param canRunChildTaskIds 可以运行的子任务 taskId 集合
     * @param logInfo            日志收集器
     */
    private void handleSubJob(String jobId,String flowJobId, Set<Long> canRunChildTaskIds, StringBuilder logInfo) {
        // 更新可以运行的下游任务列表, 供离线工作流临时运行使用
        if (CollectionUtils.isNotEmpty(canRunChildTaskIds)) {
            List<String> taskIds = canRunChildTaskIds.stream().map(String::valueOf).collect(Collectors.toList());
            scheduleJobExpandDao.updateRunSubTaskIdByJobId(jobId, String.join(",", taskIds));
        }
        // 如果当前 job 是工作流临时任务, 将条件分支未命中的子任务信息 is_skip 置为1
        if (isWorkflowTemporaryJob(flowJobId)) {
            processWorkflowTemporaryJobSubJobInfo(flowJobId, jobId, canRunChildTaskIds);
            updateStatusAndLog(jobId, RdosTaskStatus.FINISHED.getStatus(), logInfo.toString());
            return;
        }
        // 获取同一周期当前 job 下游的第一层 job
        Map<String, String> nextJobMap = scheduleJobService.getAllChildJobWithSameDayByForkJoin(jobId, true);
        if (MapUtils.isEmpty(nextJobMap)) {
            // 更新状态为已完成, 原因为没有下游任务
            updateStatusAndLog(jobId, RdosTaskStatus.FINISHED.getStatus(), logInfo.toString());
            return;
        }
        Map<Long, String> taskIdJobIdMap = scheduleJobService.getTaskIdJobIdMap(Lists.newArrayList(nextJobMap.keySet()), Deleted.NORMAL.getStatus());
        // 所有子任务 taskId 集合, 记录可以运行的子任务 taskId
        Set<Long> allChildTaskIds = taskIdJobIdMap.keySet();
        // 计算不符合分支条件的任务列表, 去除所有可运行的任务, 不可运行的任务状态设置为自动取消并记录取消原因
        Set<Long> cancelChildTasks = Sets.newHashSet(allChildTaskIds);
        cancelChildTasks.removeAll(canRunChildTaskIds);
        List<String> cancelJobIds = cancelChildTasks.stream().map(taskIdJobIdMap::get).collect(Collectors.toList());
        cancelChildTask(cancelJobIds, jobId);
        // 如果全部都不符合则当前任务状态设置为自动取消, 判断条件：总子任务数量和不可运行的任务数量相等
        if (allChildTaskIds.size() == cancelChildTasks.size()) {
            logInfo.append("分支条件全部未命中, 任务变更为自动取消状态");
            updateStatusAndLog(jobId, RdosTaskStatus.AUTOCANCELED.getStatus(), logInfo.toString());
        } else {
            updateStatusAndLog(jobId, RdosTaskStatus.FINISHED.getStatus(), logInfo.toString());
        }
    }

    private void processWorkflowTemporaryJobSubJobInfo(String flowJobId, String jobId, Set<Long> canRunChildTaskIds) {
        WorkflowTmpRunInfo workflowTmpRunInfo = workflowDao.getWorkflowTmpRunInfo(flowJobId);
        WorkflowSubTmpRunInfo conditionSubTmpRunInfo = workflowDao.getWorkflowSubTmpRunInfoByFlowJobIdAndJobId(flowJobId, jobId);

        if (workflowTmpRunInfo == null || conditionSubTmpRunInfo == null) {
            return;
        }

        String graphJson = workflowTmpRunInfo.getGraph();
        if (StringUtils.isBlank(graphJson)) {
            return;
        }

        Graph graph = GraphParser.parse(graphJson);
        List<Edge> edges = graph.getEdges();

        Map<Long, List<Edge>> graphEdges = edges.stream().collect(Collectors.groupingBy(Edge::getFrom));

        // 拿到当前条件分支任务的下游任务
        List<Edge> conditionEdges = graphEdges.get(conditionSubTmpRunInfo.getTaskId());
        if (CollectionUtils.isEmpty(conditionEdges)) {
            return;
        }
        for (Edge conditionEdge : conditionEdges) {
            Long subTaskId = conditionEdge.getTo();
            if (canRunChildTaskIds.contains(subTaskId)) {
                continue;
            }
            // 将不符合条件的子任务 is_skip 置为1
            workflowDao.updateSubJobSkip(flowJobId, workflowTmpRunInfo.getFlowId(), subTaskId);
        }
    }

    private boolean isWorkflowTemporaryJob(String flowJobId) {
        return Objects.nonNull(workflowDao.getWorkflowTmpRunInfo(flowJobId));
    }

    /**
     * 取消子任务及下游所有子任务
     *
     * @param cancelChildJobIds 待取消的任务 jobId 集合
     * @param jobId             条件分支任务实例 id
     */
    private void cancelChildTask(List<String> cancelChildJobIds, String jobId) {
        if (CollectionUtils.isEmpty(cancelChildJobIds)) {
            LOGGER.info("The sub jobs are all comply, condition branch jobId: {}", jobId);
            return;
        }
        // 所有待取消的任务 jobId
        Set<String> allCancelJobIds = Sets.newHashSet();
        allCancelJobIds.addAll(cancelChildJobIds);
        cancelChildJobIds.forEach(job -> {
            Map<String, String> jobIdMap = scheduleJobService.getAllChildJobWithSameDayByForkJoin(job, false);
            if (MapUtils.isNotEmpty(jobIdMap)) {
                allCancelJobIds.addAll(jobIdMap.keySet());
            }
        });
        LOGGER.info("The {} jobs are cancelled because the conditional branch is not satisfied, condition branch jobId: {}", String.join(",", allCancelJobIds), jobId);
        // 更新实例状态为自动取消
        scheduleJobService.updateStatusAndLogInfoByIds(Lists.newArrayList(allCancelJobIds), RdosTaskStatus.AUTOCANCELED.getStatus(), String.format(CONDITION_AUTO_CANCEL_KEY +", jobId：[%s]", jobId));
    }

    /**
     * 处理单个条件分支, 替换逻辑在 LocalJobStartTrigger 中完成, 此处不再进行参数处理
     *
     * @param conditionBranchDTO 分支条件
     */
    private ConditionResult handleCondition(ConditionBranchDTO conditionBranchDTO) {
        if (StringUtils.isEmpty(conditionBranchDTO.getCondition())) {
            return ConditionResult.createFailedResult("condition is empty");
        }
        try {
            Boolean value = EXPRESSION_PARSER.parseExpression(conditionBranchDTO.getCondition()).getValue(CONTEXT, Boolean.class);
            if (BooleanUtils.isTrue(value)) {
                return ConditionResult.createSuccessResult();
            }
            return ConditionResult.createFailedResult(String.format("conditions not met, real execute condition: %s", conditionBranchDTO.getCondition()));
        } catch (Throwable e) {
            return ConditionResult.createFailedResult(String.format("real execute condition el: %s,", conditionBranchDTO.getCondition()) + ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * 更新任务状态和日志
     *
     * @param jobId  jobId
     * @param status 任务状态
     * @param log    日志信息
     */
    private void updateStatusAndLog(String jobId, Integer status, String log) {
        scheduleJobService.updateStatusAndLogInfoById(jobId, status, log);
    }
}
