package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.impl.DataSourceService;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.master.worker.TaskOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/3 8:17 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobDealerMock extends FailoverStrategyMock {

    @MockInvoke(targetClass = ScheduleJobGanttTimeService.class)
    public void ganttChartTime(String jobId, JobGanttChartEnum jobGanttChartEnum, Integer scheduleType, Integer computeType) {

    }

    @MockInvoke(targetClass = ShardCache.class)
    public boolean updateLocalMemTaskStatus(String jobId, Integer status) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateJobStatus( String jobId,  int status){

    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer updateEngineLog( String jobId, String engineLog) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public String getJobExtraInfoOfValue(String jobId, String key){
        return "";
    }

    @MockInvoke(targetClass = OperatorDistributor.class)
    public TaskOperator getOperator(ClientTypeEnum clientType, String engineType) {
        return new EnginePluginsOperator();
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public String getEngineLog(JobIdentifier jobIdentifier) {
        return "logInfo";
    }


    @MockInvoke(targetClass = TaskParamsService.class)
    public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType, String engineType, Long tenantId) {
        return EDeployMode.PERJOB;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<String> getAllNodeAddress(){
        return Lists.newArrayList("127.0.0.1:8090","127.0.0.1:8091");
    }

     @MockInvoke(targetClass = EngineJobCacheDao.class)
     EngineJobCache getOne(String jobId) {
         EngineJobCache engineJobCache = new EngineJobCache();
         engineJobCache.setEngineType("spark");
         engineJobCache.setJobInfo("{}");
         return engineJobCache;
     }


    @MockInvoke(targetClass = EngineJobCacheDao.class)
    int updateStage(String jobId,  Integer stage, String nodeAddress,  Long jobPriority,String waitReason){
        return 0;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    int updateStageBatch(List<String> jobIds, Integer stage, String nodeAddress) {
        return 0;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return 2;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    Long minPriorityByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return 10L;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    int insert(String jobId, String engineType, Integer computeType, int stage,
               String jobInfo, String nodeAddress, String jobName,  Long jobPriority,  String jobResource, Long tenantId
    ){
        return 0;
    }

    @MockInvoke(targetClass = GroupPriorityQueue.class)
    public GroupPriorityQueue build() {
        return GroupPriorityQueue.builder();
    }


    @MockInvoke(targetClass = GroupPriorityQueue.class)
    public boolean add(JobClient jobClient, boolean judgeBlock, boolean insert) {
        return Boolean.FALSE;
    }

    @MockInvoke(targetClass = JobComputeResourcePlain.class)
    public String getJobResource(JobClient jobClient) {
        return "123456";
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public String loadJdbcInfo(JobClient jobClient, String pluginInfo) {
        return pluginInfo;
    }

}
