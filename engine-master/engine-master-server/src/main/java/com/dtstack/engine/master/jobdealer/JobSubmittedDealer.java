package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.JobGraphUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class  JobSubmittedDealer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSubmittedDealer.class);

    private final LinkedBlockingQueue<JobClient> queue;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private JobRestartDealer jobRestartDealer;

    @Autowired
    private ScheduleJobHistoryService scheduleJobHistoryService;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EnvironmentContext environmentContext;

    public JobSubmittedDealer() {
        queue = JobSubmitDealer.getSubmittedQueue();
    }

    @Override
    public void run() {
        while (true) {
            JobClient jobClient = null;
            try {
                jobClient = queue.take();

                if (jobRestartDealer.checkAndRestartForSubmitResult(jobClient)) {
                    LOGGER.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());
                    continue;
                }

                LOGGER.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());

                // 存储执行日志
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId())) {
                    JobResult jobResult = jobClient.getJobResult();
                    String appId = jobResult.getData(JobResult.EXT_ID_KEY);
                    JSONObject jobExtraInfo = savaExtraInfo(jobClient, jobResult, appId);
                    Integer appType = jobClient.getAppType();
                    String sql = "";
                    if (environmentContext.getOpenSaveRunSqlText()) {
                        sql = jobClient.getSql();
                    }
                    scheduleJobExpandDao.updateExtraInfoAndLog(jobClient.getTaskId(), jobExtraInfo.toJSONString(), appendMsg(jobClient), null, sql);
                    //热更新 状态不变
                    if (!jobClient.isHotReloading()) {
                        jobDealer.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
                        jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
                        JobClient finalJobClient = jobClient;
                        shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITTED.getStatus(), (jobId) -> {
                            LOGGER.warn("success submit job to Engine, jobId:{} jobResult:{} but shareManager is not found ...", jobId, finalJobClient.getJobResult());
                            finalJobClient.doStatusCallBack(RdosTaskStatus.CANCELED.getStatus());
                            scheduleJobHistoryService.updateScheduleJobHistoryTime(appId, appType);
                        });
                    } else {
                        removeHotReloadingInBlackList(jobClient.getTaskId());
                    }
                    // 实时计算任务添加到运行历史表中
                    scheduleJobHistoryService.insertScheduleJobHistory(jobClient,appId);
                } else {
                    checkJobClientAndDoFail(jobClient,null);
                }
            } catch (Throwable e) {
                LOGGER.error("jobId submitted {} jobStatus dealer run error", null == jobClient ? "" : jobClient.getTaskId(), e);
                if (null != jobClient) {
                    checkJobClientAndDoFail(jobClient,e);
                }
            }
        }
    }

    public void checkJobClientAndDoFail(JobClient jobClient, Throwable e) {
        if (jobClient.isHotReloading()) {
            removeHotReloadingInBlackList(jobClient.getTaskId());
        } else if (e != null) {
            jobClientFail(jobClient.getTaskId(), JobResult.createErrorResult(e).getJsonStr());
        } else {
            jobClientFail(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
        }
    }

    private void removeHotReloadingInBlackList(String jobId) {
        redisTemplate.delete(GlobalConst.STATUS_BLACK_LIST + jobId);
    }

    @NotNull
    private JSONObject savaExtraInfo(JobClient jobClient, JobResult jobResult, String appId) {
        String jobExtraInfoString = scheduleJobExpandDao.getJobExtraInfo(jobClient.getTaskId());
        JSONObject jobExtraInfo;
        if (StringUtils.isBlank(jobExtraInfoString)) {
            jobExtraInfo = jobResult.getExtraInfoJson();
        } else {
            jobExtraInfo = JSONObject.parseObject(jobExtraInfoString);
            jobExtraInfo.putAll(jobResult.getExtraInfoJson());
        }
        jobExtraInfo.put(JobResultConstant.JOB_GRAPH, JobGraphUtil.formatJSON(jobClient.getEngineTaskId(), jobExtraInfo.getString(JobResultConstant.JOB_GRAPH), jobClient.getComputeType()));
        jobExtraInfo.put("componentVersion", jobClient.getComponentVersion());
        if (jobClient.isHotReloading()) {
            scheduleJobDao.updateJobAppId(jobClient.getTaskId(), jobClient.getEngineTaskId(), appId);
        } else {
            scheduleJobDao.updateJobSubmitSuccess(jobClient.getTaskId(), jobClient.getEngineTaskId(), appId);
        }
        scheduleJobExpandDao.updateJobSubmitSuccess(jobClient.getTaskId(),jobClient.getEngineTaskId(), appId);
        return jobExtraInfo;
    }

    private void jobClientFail(String taskId, String info) {
        try {
            scheduleJobExpandDao.updateLogInfoAndTimeByJobId(taskId,info,RdosTaskStatus.FAILED.getStatus());
            scheduleJobDao.jobFinish(taskId, RdosTaskStatus.FAILED.getStatus());
            LOGGER.info("jobId:{} update job status:{}, job is finished.", taskId, RdosTaskStatus.FAILED.getStatus());
            engineJobCacheDao.delete(taskId);
        } catch (Exception e) {
            LOGGER.error("jobId:{} update job fail {}  error", taskId, info, e);
        }
    }

    private String appendMsg(JobClient jobClient) {
        JSONObject resultStr = jobClient.getJobResult().getJson();
        if (jobClient.isHotReloading()) {
            resultStr.put(JobResult.MSG_INFO, "【hot reloading】" + resultStr.getString(JobResult.MSG_INFO));
        }
        return resultStr.toJSONString();
    }

}
