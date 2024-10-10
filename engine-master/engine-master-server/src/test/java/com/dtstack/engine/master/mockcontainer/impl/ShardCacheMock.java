package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.jobdealer.JobStatusDealer;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.EngineJobCache;
import org.springframework.context.ApplicationContext;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 8:25 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ShardCacheMock extends BaseMock {

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobResource("123456789");
        engineJobCache.setJobId(jobId);
        return engineJobCache;
    }

    @MockInvoke(targetClass = JobStatusDealer.class)
    public void setApplicationContext(ApplicationContext applicationContext) {
    }

    @MockInvoke(targetClass = JobStatusDealer.class)
    public void start() {

    }

}
