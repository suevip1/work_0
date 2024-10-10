package com.dtstack.engine.master.mockcontainer.sync.fill;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.mockcontainer.impl.WhiteAbstractFillDataTaskMock;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 1:52 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ProjectFillDataTaskMock extends WhiteAbstractFillDataTaskMock {

    private Integer count = 0;

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShadeDTO> findTaskKeyByProjectId(Long projectId, Integer appType, List<Integer> taskTypes, Integer taskGroup) {
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


}
