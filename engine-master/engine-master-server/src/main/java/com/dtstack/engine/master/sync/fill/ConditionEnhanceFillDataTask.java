package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataConditionInfoVO;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.common.enums.ETaskGroupEnum;
import com.dtstack.engine.master.enums.CloseRetryEnum;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.impl.ScheduleTaskTagService;
import com.dtstack.engine.master.utils.ListUtil;
import com.dtstack.engine.po.ScheduleTaskTag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/10/28 2:00 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class ConditionEnhanceFillDataTask extends WhiteAbstractFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(BatchEnhanceFillDataTask.class);

    private final FillDataConditionInfoVO fillDataConditionInfoVO;

    private final ScheduleTaskTagService scheduleTaskTagService;

    public ConditionEnhanceFillDataTask(ApplicationContext applicationContext, FillDataConditionInfoVO fillDataConditionInfoVO,List<TaskKeyVO> whitelist) {
        super(applicationContext,whitelist);
        this.fillDataConditionInfoVO = fillDataConditionInfoVO;
        scheduleTaskTagService = applicationContext.getBean(ScheduleTaskTagService.class);
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.CONDITION_PROJECT_TASK;
    }

    @Override
    public Set<String> getRunList() {
        Set<String> runList = Sets.newHashSet();
        List<FillDataChooseProjectVO> projects = fillDataConditionInfoVO.getProjects();
        List<Integer> taskTypes = fillDataConditionInfoVO.getTaskTypes();
        List<Long> tagIds = fillDataConditionInfoVO.getTagIds();
        List<Long> taskIds = null;
        if (CollectionUtils.isNotEmpty(tagIds)) {
            taskIds = getTaskByTags(tagIds);
        }


        if (CollectionUtils.isNotEmpty(projects)) {
            for (FillDataChooseProjectVO project : projects) {

                List<ScheduleTaskShadeDTO> shadeDTOS = scheduleTaskShadeService.findTaskKeyByProjectId(project.getProjectId(), project.getAppType(), taskTypes, taskIds, ETaskGroupEnum.NORMAL_SCHEDULE.getType());
                shadeDTOS.stream().filter(dto -> {
                            if (CloseRetryEnum.OPEN.getType().equals(fillDataConditionInfoVO.getFilterFrozen())) {
                                return !EScheduleStatus.PAUSE.getVal().equals(dto.getScheduleStatus());
                            } else {
                                return true;
                            }
                        })
                        .collect(Collectors.toList())
                        .forEach(task -> runList.add(task.getTaskId() + FillDataConst.KEY_DELIMITER + task.getAppType()));
            }
        } else if (CollectionUtils.isNotEmpty(taskTypes) || CollectionUtils.isNotEmpty(taskIds) ) {
            // 帅选任务类型
            Long minId = scheduleTaskShadeService.findMinIdByTaskType(taskTypes,
                    ETaskGroupEnum.NORMAL_SCHEDULE.getType(),
                    taskIds);

            if (minId != null) {
                List<ScheduleTaskShadeDTO> shadeDTOS = scheduleTaskShadeService.findTaskByTaskType(minId,
                        taskTypes,
                        ETaskGroupEnum.NORMAL_SCHEDULE.getType(),
                        taskIds,
                        environmentContext.getFillDataLimitSize(),
                        Boolean.TRUE);

                while (CollectionUtils.isNotEmpty(shadeDTOS)) {
                    Long LastMinId = minId;
                    for (ScheduleTaskShadeDTO task : shadeDTOS) {
                        Long id = task.getId();
                        if (id > minId) {
                            minId = id;
                        }

                        if (CloseRetryEnum.OPEN.getType().equals(fillDataConditionInfoVO.getFilterFrozen())
                                && EScheduleStatus.PAUSE.getVal().equals(task.getScheduleStatus())) {
                            continue;
                        }

                        runList.add(task.getTaskId() + FillDataConst.KEY_DELIMITER + task.getAppType());
                    }

                    if (LastMinId >= minId) {
                        LOGGER.warn("birth and death cycle");
                        break;
                    }

                    shadeDTOS = scheduleTaskShadeService.findTaskByTaskType(minId,
                            taskTypes,
                            ETaskGroupEnum.NORMAL_SCHEDULE.getType(),
                            taskIds,
                            environmentContext.getFillDataLimitSize(),
                            Boolean.FALSE);
                }
            }
        }
        
        runList.addAll(addWhite(fillDataConditionInfoVO.getFilterFrozen()));
        return runList;
    }

    private List<Long> getTaskByTags(List<Long> tagIds) {
        List<ScheduleTaskTag> tagsByTagIds = scheduleTaskTagService.findTagsByTagIds(tagIds,null);
        List<Long> taskIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(tagsByTagIds)) {
            Map<Long, List<ScheduleTaskTag>> tagMap = tagsByTagIds.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId));

            for (Long task : tagMap.keySet()) {
                List<ScheduleTaskTag> scheduleTaskTags = tagMap.get(task);
                List<Long> taskTagIds = scheduleTaskTags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());

                // tagIds 是否是taskTagIds的子集
                if (ListUtil.isSubList(taskTagIds, tagIds)) {
                    taskIds.add(task);
                }
            }
        }

        return taskIds;
    }
}
