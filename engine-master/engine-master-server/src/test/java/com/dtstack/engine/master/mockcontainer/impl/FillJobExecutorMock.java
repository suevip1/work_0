package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.impl.ScheduleFillDataJobService;
import com.dtstack.engine.po.EngineJobCache;

/**
 * @Auther: dazhi
 * @Date: 2022/6/29 11:18 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillJobExecutorMock extends AbstractJobExecutorMock {

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobService.class)
    public Integer incrementParallelNum(Long id) {
        return 1;
    }
}
