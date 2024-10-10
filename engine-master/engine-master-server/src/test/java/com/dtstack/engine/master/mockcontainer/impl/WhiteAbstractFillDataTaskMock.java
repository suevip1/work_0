package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:31 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class WhiteAbstractFillDataTaskMock extends AbstractFillDataTaskMock {

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        return Lists.newArrayList();
    }


}
