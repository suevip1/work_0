package com.dtstack.engine.master.mockcontainer.sync.fill.strategy;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 2:12 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AllSetStrategyFactoryMock extends BaseMock {

    private Integer count = 1;
    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getFillDataLimitSize() {
        return 200;
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public List<ScheduleTaskTaskShadeDTO> listChildByTaskKeys(List<String> taskKeys) {
        List<ScheduleTaskTaskShadeDTO> scheduleTaskTaskShadeDTOS = Lists.newArrayList();
        if (count == 1) {
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setTaskKey("1");
            scheduleTaskTaskShadeDTO.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO.setParentTaskKey("3");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO);
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO2 = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO2.setTaskKey("2");
            scheduleTaskTaskShadeDTO2.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO2.setParentTaskKey("3");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO2);
            count++;
            return scheduleTaskTaskShadeDTOS;
        }

        if (count == 2) {
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setTaskKey("3");
            scheduleTaskTaskShadeDTO.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO.setParentTaskKey("4");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO);
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO2 = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO2.setTaskKey("3");
            scheduleTaskTaskShadeDTO2.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO2.setParentTaskKey("5");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO2);
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO3 = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO3.setTaskKey("3");
            scheduleTaskTaskShadeDTO3.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO3.setParentTaskKey("6");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO3);
            count++;
            return scheduleTaskTaskShadeDTOS;
        }

        if (count == 3) {
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setTaskKey("4");
            scheduleTaskTaskShadeDTO.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO.setParentTaskKey("7");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO);
            count++;
            return scheduleTaskTaskShadeDTOS;
        }

        if (count == 4) {
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setTaskKey("7");
            scheduleTaskTaskShadeDTO.setScheduleStatus(1);
            scheduleTaskTaskShadeDTO.setParentTaskKey("8");
            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO);
            count++;
            return scheduleTaskTaskShadeDTOS;
        }


        return scheduleTaskTaskShadeDTOS;
    }

}
