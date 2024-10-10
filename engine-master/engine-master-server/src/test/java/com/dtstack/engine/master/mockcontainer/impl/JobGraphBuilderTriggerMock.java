package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 10:47 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobGraphBuilderTriggerMock extends BaseMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getJobGraphBuildCron() {
        return "22:00:00";
    }

    @MockInvoke(targetClass = JobGraphBuilder.class)
    public void buildTaskJobGraph(String triggerDay){
    }


}
