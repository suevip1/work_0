package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.dao.ScheduleTaskTaskShadeDao;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mapstruct.ScheduleTaskTaskShadeStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScheduleTaskTaskShadeServiceTestMock extends BaseMock {


    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    Integer insert(ScheduleTaskTaskShade scheduleTaskTaskShade) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    Integer deleteByTaskId(long taskId, Integer appType) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    List<ScheduleTaskTaskShade> listParentTaskKeys(List<String> taskKeys) {
        if (CollectionUtils.isEmpty(taskKeys)) {
            return new ArrayList<>();
        }
        if (taskKeys.get(0).equals("1-1")) {
            ScheduleTaskTaskShade scheduleTaskTaskShade = new ScheduleTaskTaskShade();
            scheduleTaskTaskShade.setTaskId(200L);
            scheduleTaskTaskShade.setAppType(1);
            scheduleTaskTaskShade.setParentTaskId(300L);
            scheduleTaskTaskShade.setParentAppType(1);
            return Lists.newArrayList(scheduleTaskTaskShade);
        }
        return new ArrayList<>();
    }


    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    List<ScheduleTaskTaskShade> getTaskOtherPlatformByProjectId(Long projectId, Integer appType, Integer listChildTaskLimit) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    Integer deleteByParentTaskKeys(Collection<String> jobKeys) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    Integer deleteByTaskKeys(Collection<String> jobKeys) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    List<ScheduleTaskTaskShade> listChildTask(long parentTaskId, Integer appType) {
        ScheduleTaskTaskShade scheduleTaskTaskShade = new ScheduleTaskTaskShade();
        scheduleTaskTaskShade.setTaskId(2L);
        scheduleTaskTaskShade.setAppType(1);
        scheduleTaskTaskShade.setParentTaskId(1L);
        scheduleTaskTaskShade.setParentAppType(1);
        return Lists.newArrayList(scheduleTaskTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    List<ScheduleTaskTaskShade> listParentTask(long childTaskId, Integer appType) {
        ScheduleTaskTaskShade scheduleTaskTaskShade = new ScheduleTaskTaskShade();
        scheduleTaskTaskShade.setTaskId(1L);
        scheduleTaskTaskShade.setAppType(1);
        scheduleTaskTaskShade.setParentTaskId(100L);
        scheduleTaskTaskShade.setParentAppType(1);
        return Lists.newArrayList(scheduleTaskTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeDao.class)
    List<ScheduleTaskTaskShade> listTaskKeys(List<String> taskKeys) {
        ScheduleTaskTaskShade scheduleTaskTaskShade = new ScheduleTaskTaskShade();
        scheduleTaskTaskShade.setTaskId(1L);
        scheduleTaskTaskShade.setAppType(1);
        scheduleTaskTaskShade.setParentTaskId(100L);
        scheduleTaskTaskShade.setParentAppType(1);
        return Lists.newArrayList(scheduleTaskTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> findChildTaskRuleByTaskId(Long taskId, Integer appType) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTasksByFlowId(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId + 1);
        scheduleTaskShade.setAppType(appType);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShade getWorkFlowTopNode(Long taskId, Integer appType) {

        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setFlowId(0L);
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskShade.setProjectId(1L);
        return scheduleTaskShade;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setFlowId(0L);
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskShade.setProjectId(1L);
        if (20L == taskId) {
            scheduleTaskShade.setTaskType(EScheduleJobType.GROUP.getType());
        }
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeStruct.class)
    List<ScheduleTaskTaskShadeDTO> toScheduleTaskTaskShadeDTO(List<ScheduleTaskTaskShade> taskTaskShades) {
        List<ScheduleTaskTaskShadeDTO> dto = new ArrayList<>();
        for (ScheduleTaskTaskShade taskTaskShade : taskTaskShades) {
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setTaskKey(taskTaskShade.getTaskKey());
            scheduleTaskTaskShadeDTO.setParentTaskKey(taskTaskShade.getParentTaskKey());
            dto.add(scheduleTaskTaskShadeDTO);
        }
        return dto;
    }
}
