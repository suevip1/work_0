package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.dto.JobChainParamStatusResult;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.jobdealer.JobCheckpointDealer;
import com.dtstack.engine.master.jobdealer.JobRestartDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.master.worker.TaskOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 11:40 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobStatusDealerMock extends BaseMock {


    @MockInvoke(targetClass = ShardManager.class)
    public Map<String, Integer> getShard() {
        HashMap<String, Integer> stringIntegerHashMap = Maps.newHashMap();
        stringIntegerHashMap.put("12345",4);
        stringIntegerHashMap.put("54698",4);
        return stringIntegerHashMap;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setEngineJobId("12345");
        scheduleJob.setStatus(4);

        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateJobStatusAndExecTime(String jobId, int status){

    }


    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        if ("12345".equals(jobId)) {
            EngineJobCache engineJobCache = new EngineJobCache();
            engineJobCache.setJobInfo("{\"taskType\":1,\"computeType\":1,\"maxRetryNum\":10,\"engineType\":\"kylin\",\"pluginInfo\":{}}");
            engineJobCache.setEngineType("kylin");
            engineJobCache.setComputeType(1);
            return engineJobCache;
        }

        return null;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    int delete(String jobId){
        return 1;
    }

    @MockInvoke(targetClass = ShardCache.class)
    public boolean updateLocalMemTaskStatus(String jobId, Integer status) {
        return true;
    }

    @MockInvoke(targetClass = ShardCache.class)
    public boolean removeIfPresent(String jobId) {
        return true;
    }


    @MockInvoke(targetClass = TaskParamsService.class)
    public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType, String engineType, Long tenantId) {
        return EDeployMode.PERJOB;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public String getJobExtraInfoOfValue(String jobId, String key){
        return "";
    }

    @MockInvoke(targetClass = OperatorDistributor.class)
    public TaskOperator getOperator(ClientTypeEnum clientType, String engineType) {
        return new DataSourceXOperator();
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        return RdosTaskStatus.RUNNING;
    }

    @MockInvoke(targetClass = JobRestartDealer.class)
    public boolean checkAndRestart(Integer status, ScheduleJob scheduleJob, EngineJobCache jobCache, BiConsumer<ScheduleJob, JobClient> saveRetryFunction){
       return false;
    }

    @MockInvoke(targetClass = JobChainParamHandler.class)
    public JobChainParamStatusResult checkOutputParamIfNeed(RdosTaskStatus rdosTaskStatus, ScheduleJob scheduleJob) throws Exception {
        JobChainParamStatusResult jobChainParamStatusResult = new JobChainParamStatusResult();

        jobChainParamStatusResult.setRdosTaskStatus(RdosTaskStatus.FAILED);
        return jobChainParamStatusResult;
    }

    @MockInvoke(targetClass = JobCheckpointDealer.class)
    public void updateCheckpointImmediately(JobCheckpointInfo taskInfo, String engineJobId, Integer status) {
    }

    @MockInvoke(targetClass = ScheduleJobHistoryService.class)
    public void updateScheduleJobHistoryTime(String applicationId, Integer appType) {
    }

}
