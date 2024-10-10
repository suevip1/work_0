package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.EngineJobRetry;
import com.dtstack.engine.po.ScheduleJobExpand;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 10:42 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobRestartDealerMock {

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setRetryNum(3);
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateRetryNum(String jobId, Integer retryNum) {
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateJobStatusByIds(Integer status, List<String> jobIds) {
        return 2;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateJobStatus(String jobId, int status) {
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobInfo("{}");
        return engineJobCache;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public boolean addRestartJob(JobClient jobClient) {
        return true;
    }

    @MockInvoke(targetClass = ShardCache.class)
    public boolean updateLocalMemTaskStatus(String jobId, Integer status) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    ScheduleJobExpand getLogByJobId(String jobId) {
        return new ScheduleJobExpand();
    }

    @MockInvoke(targetClass = EngineJobRetryDao.class)
    void insert(EngineJobRetry engineJobRetry) {
    }







}
