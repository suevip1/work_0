package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.ApplicationInfo;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.mapstruct.ScheduleJobStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.JobStartTriggerBase;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.MultiEngineFactory;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.EngineJobRetry;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.po.ScheduleJobParam;
import org.assertj.core.util.Lists;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ActionServiceMock extends BaseMock {

    @MockInvoke(targetClass = ParamStruct.class)
    List<ConsoleParamBO> toBOs(List<ConsoleParam> consoleParams) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ParamStruct.class)
    List<ScheduleTaskParamShade> BOtoTaskParams(List<ConsoleParamBO> consoleParamBOS) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ParamService.class)
    public List<ScheduleJobParam> selectParamByJobId(String jobId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ParamStruct.class)
    public List<ScheduleTaskParamShade> jobParamToTaskParams(List<ScheduleJobParam> scheduleJobParams) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = MultiEngineFactory.class)
    public JobStartTriggerBase getJobTriggerService(Integer multiEngineType, Integer taskType, Integer taskEngineType) {
        return new JobStartTriggerBase();
    }

        @MockInvoke(targetClass = JobDealer.class)
    public void saveCache(JobClient jobClient, String jobResource, int stage, boolean insert) {
        return;
    }

    @MockInvoke(targetClass = JobComputeResourcePlain.class)
    public String getJobResource(JobClient jobClient) {
        return "";
    }

    @MockInvoke(targetClass = ScheduleJobStruct.class)
    List<ActionJobEntityVO> toActionJobEntityVO(List<ScheduleJob> job) {
        return job.stream().map(j -> {
            ActionJobEntityVO actionJobEntityVO = new ActionJobEntityVO();
            actionJobEntityVO.setJobId(j.getJobId());
            return actionJobEntityVO;
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public ApplicationInfo retrieveJob(JobClient jobClient) throws ClientAccessException {
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationId("te");
        applicationInfo.setEngineJobId("12");
        return applicationInfo;
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public List<String> containerInfos(JobClient jobClient) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public JobResult submitJob(JobClient jobClient) throws Exception {
        return new JobResult();
    }

    @MockInvoke(targetClass = EngineJobRetryDao.class)
    EngineJobRetry getJobRetryByJobId(String jobId, int retryNum) {
        EngineJobRetry engineJobRetry = new EngineJobRetry();
        engineJobRetry.setEngineJobId("ts");
        engineJobRetry.setEngineLog("test");
        engineJobRetry.setRetryTaskParams("test");
        engineJobRetry.setLogInfo("test");
        return engineJobRetry;
    }

    @MockInvoke(targetClass = EngineJobRetryDao.class)
    List<EngineJobRetry> listJobRetryByJobId(String jobId, int maxRetryLogSize) {
        EngineJobRetry engineJobRetry = new EngineJobRetry();
        engineJobRetry.setEngineJobId("ts");
        engineJobRetry.setEngineLog("test");
        return Lists.newArrayList(engineJobRetry);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOne(long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = JobStopDealer.class)
    public int addStopJobs(List<ScheduleJob> jobs, Integer isForce, Long operateId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateJobSubmitSuccess(String jobId, String engineId, String appId) {
        return;
    }

    @MockInvoke(targetClass = ShardCache.class)
    public boolean updateLocalMemTaskStatus(String jobId, Integer status) {
        return false;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listJobStatus(Timestamp timeStamp, Integer computeType, Integer appType) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateJobStatusAndPhaseStatus(List<String> jobIds, Integer status, Integer phaseStatus, Integer isRestart, String nodeAddress) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer updateLogByJobId(String jobId, String logInfo, String engineLog, String retryTaskParam) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        if ("test".equals(jobId)) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setStatus(0);
            scheduleJob.setTaskId(1L);
            scheduleJob.setAppType(1);
            scheduleJob.setRetryNum(1);
            scheduleJob.setJobId(jobId);
            scheduleJob.setCycTime("20220707000000");
            return scheduleJob;
        }
        return null;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        if ("test".equals(jobId) || "4j66umhct4q1".equals(jobId)) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setStatus(0);
            scheduleJob.setTaskId(1L);
            scheduleJob.setAppType(1);
            scheduleJob.setRetryNum(1);
            scheduleJob.setJobId(jobId);
            scheduleJob.setCycTime("20220707000000");
            if ("4j66umhct4q1".equals(jobId)) {
                scheduleJob.setStatus(5);
            }
            return scheduleJob;
        }
        return null;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> getRdosJobByJobIds(List<String> jobIds) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(0);
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    Integer deleteByTaskId(String taskId) {
        return 1;
    }

    @MockInvoke(targetClass = MultiEngineFactory.class)
    public JobStartTriggerBase getJobTriggerService(Integer type) {
        return new TestJobBase();
    }


    @MockInvoke(targetClass = ParamStruct.class)
    List<ScheduleTaskParamShade> toTaskParams(List<ConsoleParam> consoleParam) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ParamService.class)
    public List<ConsoleParam> selectByTaskId(Long taskId, int appType) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ParamService.class)
    public List<ConsoleParam> selectSysParam() {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = ParamService.class)
    public void convertGlobalToParamType(String info, BiConsumer<List<ConsoleParam>, List<ScheduleTaskParamShade>> convertConsumer) {
        return;
    }

    @MockInvoke(targetClass = JobRichOperator.class)
    public String getCycTime(Integer beforeDay) {
        return "";
    }

    @MockInvoke(targetClass = Context.class)
    public ClientTypeEnum getClientType(String engineType) {
        return ClientTypeEnum.WORKER_STATUS;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    String getLogInfoByJobId(String jobId) {
        JSONObject j = new JSONObject();
        j.put("test", "1");
        return j.toJSONString();
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    ScheduleJobExpand getLogByJobId(String jobId) {
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setEngineLog("test");
        return scheduleJobExpand;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    List<ScheduleJobExpand> getLogByJobIds(List<String> jobIds) {
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setEngineLog("test");
        return Lists.newArrayList(scheduleJobExpand);
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer insert(ScheduleJobExpand scheduleJobExpand) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer insert(ScheduleJob scheduleJob) {
        return 1;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public void addSubmitJob(JobClient jobClient) {
        return;
    }

    class TestJobBase extends JobStartTriggerBase {
    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        return;
    }
    }
}
