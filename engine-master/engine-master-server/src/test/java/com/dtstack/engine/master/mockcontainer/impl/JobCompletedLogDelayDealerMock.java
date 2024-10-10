package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.worker.EnginePluginsOperator;

/**
 * @Auther: dazhi
 * @Date: 2022/7/2 1:43 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobCompletedLogDelayDealerMock extends BaseMock {

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public String getEngineLog(JobIdentifier jobIdentifier) {
        return "logInfo";
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer updateEngineLog(String jobId,  String engineLog) {
        return 0;
    }
}
