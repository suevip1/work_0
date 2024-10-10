package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.master.scheduler.JobParamReplace;

import java.util.List;

/**
 * @author leon
 * @date 2022-10-31 16:26
 **/
public class JobStartTriggerBaseMock extends HadoopJobStartTriggerMock {


    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime,
                               Integer scheduleType) {
       return "";
    }
}
