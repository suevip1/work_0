package com.dtstack.engine.master.mockcontainer.sync.fill;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mockcontainer.impl.WhiteAbstractFillDataTaskMock;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:54 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConditionEnhanceFillDataTaskMock extends WhiteAbstractFillDataTaskMock {

    private Integer count = 0;


    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShadeDTO> findTaskKeyByProjectId(Long projectId, Integer appType,List<Integer> taskTypes,Integer taskGroup) {
        List<ScheduleTaskShadeDTO> shadeDTOS = Lists.newArrayList();

        if (count == 1) {
            ScheduleTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO.setTaskId(1L);
            scheduleTaskTaskShadeDTO.setId(233344L);
            scheduleTaskTaskShadeDTO.setAppType(1);
            shadeDTOS.add(scheduleTaskTaskShadeDTO);
            count++;
        }

        return shadeDTOS;
    }
    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public Long findMinIdByTaskType(List<Integer> taskTypes) {
        return 1000L;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShadeDTO> findTaskByTaskType(Long minId, List<Integer> taskTypes, Integer fillDataLimitSize, Integer taskGroup) {
        List<ScheduleTaskShadeDTO> shadeDTOS = Lists.newArrayList();

        if (count == 0) {
            ScheduleTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO.setTaskId(1L);
            scheduleTaskTaskShadeDTO.setId(233344L);
            scheduleTaskTaskShadeDTO.setAppType(1);
            shadeDTOS.add(scheduleTaskTaskShadeDTO);
            count++;
        }

        return shadeDTOS;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getFillDataLimitSize() {
        return 200;
    }

}
