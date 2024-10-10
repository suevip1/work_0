package com.dtstack.engine.master.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dao.ScheduleTaskTaskShadeDao;
import com.dtstack.engine.master.sync.ForkJoinTaskShadeTask;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 质量任务关联了上游又关联了下游，会造成死循环。
 * 这里用来校验质量规则任务是否关联了多个存在上游关系的任务
 * <a href="http://zenpms.dtstack.cn/zentao/story-view-10522.html">...</a>
 *
 * @author leon
 * @date 2023-02-24 16:37
 **/
@Component
public class DQStrongRuleTaskChecker {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private  ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private  ScheduleTaskTaskShadeDao scheduleTaskTaskShadeDao;

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

    private final static String ERROR_MSG = "存在质量强规则任务关联了两个及以上具有上下游关系的任务";

    private final static String CHECK_FAIL_TEMPLATE = "检测质量任务循环失败: %s";

    public SaveTaskTaskVO checkDQRuleTaskIllegal(List<ScheduleTaskTaskShade> taskTaskList) {
        Map<Long, List<ScheduleTaskTaskShade>> dqRuleTaskGroup =
                taskTaskList.stream()
                        .filter(this::isDqStrongRuleTask)
                        .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getTaskId));

        if (MapUtils.isEmpty(dqRuleTaskGroup)) {
            return SaveTaskTaskVO.save();
        }

        for (Map.Entry<Long, List<ScheduleTaskTaskShade>> ruleTaskTaskEntry : dqRuleTaskGroup.entrySet()) {

            List<ScheduleTaskTaskShade> ruleTaskTasks = ruleTaskTaskEntry.getValue();
            Long taskId = ruleTaskTaskEntry.getKey();

            SaveTaskTaskVO saveTaskTaskVO = doCheckDQRuleTaskIllegal(taskId, ruleTaskTasks);
            if (!saveTaskTaskVO.getSave()) {
                return saveTaskTaskVO;
            }
        }

        return SaveTaskTaskVO.save();
    }


    private SaveTaskTaskVO doCheckDQRuleTaskIllegal(Long taskId, List<ScheduleTaskTaskShade> ruleTaskTasks) {
        List<ScheduleTaskTaskShade> allDBTaskTask =
                scheduleTaskTaskShadeService.getAllParentTaskExcludeParentAppTypes(taskId,
                        Lists.newArrayList(AppType.DQ.getType(), AppType.DATAASSETS.getType()));

        List<ScheduleTaskTaskShade> allTaskTaskForCheck = mergeAllTaskTask(ruleTaskTasks, allDBTaskTask);

        return doCheck(allTaskTaskForCheck);
    }


    private SaveTaskTaskVO doCheck(List<ScheduleTaskTaskShade> allTaskTaskForCheck) {
        if (CollectionUtils.isEmpty(allTaskTaskForCheck) && allTaskTaskForCheck.size() == 1) {
            return SaveTaskTaskVO.save();
        }
        Set<String> uniqueTaskKeys = new HashSet<>();
        for (ScheduleTaskTaskShade taskTask: allTaskTaskForCheck) {
            Set<String> travelResult = travelTaskKey(taskTask);
            if (CollectionUtils.isEmpty(travelResult)) {
                continue;
            }
            if (hasIntersection(uniqueTaskKeys, travelResult)) {
                return SaveTaskTaskVO.noSave(ERROR_MSG);
            }
            uniqueTaskKeys.addAll(travelResult);
        }
        return SaveTaskTaskVO.save();
    }

    private Set<String> travelTaskKey(ScheduleTaskTaskShade taskTask) {
        Long taskId = taskTask.getParentTaskId();
        Integer parentAppType = taskTask.getParentAppType();
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        ConcurrentHashSet<String> results = new ConcurrentHashSet<>();
        ForkJoinTaskShadeTask forkJoinTaskShadeTask = new ForkJoinTaskShadeTask(taskId,
                parentAppType,
                scheduleTaskShadeDao,
                scheduleTaskTaskShadeDao,
                results);
        ForkJoinTask<Set<String>> submit = forkJoinPool.submit(forkJoinTaskShadeTask);
        try {
            return submit.get(environmentContext.getForkJoinResultTimeOut(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("{} travelTaskKey error: {} ", taskTask, ExceptionUtil.getErrorMessage(e), e);
            throw new RdosDefineException(String.format(CHECK_FAIL_TEMPLATE, ExceptionUtil.getErrorMessage(e)));
        }
    }

    private List<ScheduleTaskTaskShade> mergeAllTaskTask(List<ScheduleTaskTaskShade> ruleTaskTasks, List<ScheduleTaskTaskShade> allDBTaskTask) {
        List<ScheduleTaskTaskShade> allTaskTaskForCheck = new ArrayList<>(allDBTaskTask);

        Set<Long> dbParentIds = allDBTaskTask.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toSet());
        for (ScheduleTaskTaskShade ruleTaskTask : ruleTaskTasks) {
            if (dbParentIds.contains(ruleTaskTask.getParentTaskId())) {
                continue;
            }
            allTaskTaskForCheck.add(ruleTaskTask);
        }

        return allTaskTaskForCheck;
    }

    private boolean isDqStrongRuleTask(ScheduleTaskTaskShade scheduleTaskTaskShade) {
        Long taskId = scheduleTaskTaskShade.getTaskId();
        Integer appType = scheduleTaskTaskShade.getAppType();
        Integer parentAppType = scheduleTaskTaskShade.getParentAppType();

        ScheduleTaskShade shade = scheduleTaskShadeService.getSimpleByTaskIdAndAppType(taskId, appType);
        if (Objects.isNull(shade)) {
            return false;
        }
        Integer taskRule = shade.getTaskRule();

        // 是资产任务且且是强规则任务，并且上游是非资产任务
        return (AppType.DATAASSETS.getType().equals(appType))
                && TaskRuleEnum.STRONG_RULE.getCode().equals(taskRule)
                && !AppType.DATAASSETS.getType().equals(parentAppType);
    }

    public static boolean hasIntersection(Set<?>... sets) {
        if (sets.length == 0) {
            return false;
        }
        Set<Object> mergedSet = new HashSet<>();
        int totalSize = 0;
        for (Set<?> set : sets) {
            mergedSet.addAll(set);
            totalSize += set.size();
        }
        return mergedSet.size() < totalSize;
    }

}
