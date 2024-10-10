package com.dtstack.engine.master.mockcontainer.sync.fill;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.mockcontainer.impl.WhiteAbstractFillDataTaskMock;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:41 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BatchFillDataTaskMock extends WhiteAbstractFillDataTaskMock {

    private Integer count = 0;

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public List<ScheduleTaskTaskShadeDTO> listChildByTaskKeys(List<String> taskKeys) {
        if (count == 0) {
            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
            scheduleTaskTaskShadeDTO.setTaskKey("1-1");
            count++;
            return Lists.newArrayList(scheduleTaskTaskShadeDTO);
        }
        return Lists.newArrayList();
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getFillDataRootTaskMaxLevel() {
        return 1000;
    }
}
