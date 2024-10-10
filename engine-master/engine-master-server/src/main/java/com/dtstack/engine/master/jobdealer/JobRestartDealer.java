package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ERetryType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.bo.EngineJobRetry;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleJobExpand;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * 注意如果是由于资源不足导致的任务失败应该减慢发送速度
 * Date: 2018/3/22
 * Company: www.dtstack.com
 * @author xuchao
 */
@Component
public class JobRestartDealer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRestartDealer.class);

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

    @Autowired
    private JobCheckpointDealer jobCheckpointDealer;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    /**
     * 对提交结果判定是否重试
     *
     * @param jobClient 任务相关信息
     * @return 是否在重试
     */
    public boolean checkAndRestartForSubmitResult(JobClient jobClient){
        if (jobClient.isHotReloading()) {
            return false;
        }
        if(!checkSubmitResult(jobClient)){
            return false;
        }

        int alreadyRetryNum = getAlreadyRetryNum(jobClient.getTaskId());
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOGGER.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getTaskId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        // 还存在重试次数则进行重试
        boolean retry = restartJob(jobClient,null);
        LOGGER.info("【retry={}】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", retry, jobClient.getTaskId(), alreadyRetryNum);
        return retry;
    }

    /**
     * 校验任务提交结果
     * 1. jobResult 为空则重试, 表示还未进行提交
     * 2. 提交引擎返回 jobResult 中 checkRetry 为 false 字段表示提交成功, 不需要进行重试
     * 3. 任务本身没有配置重试策略, 则不进行重试
     * 4. 提交引擎返回 jobResult 中 checkRetry 字段为 true 且任务配置了重试策略则进行重试
     *
     * @param jobClient 任务相关参数
     * @return 是否需要进行重试
     */
    private boolean checkSubmitResult(JobClient jobClient){
        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        }

        // 校验是否需要重新提交, checkRetry 为 false 则不需要重新提交
        if(!jobClient.getJobResult().getCheckRetry()){
            LOGGER.info("[retry=false] jobId:{} jobResult.checkRetry:{} jobResult.msgInfo:{} check retry is false.", jobClient.getTaskId(), jobClient.getJobResult().getCheckRetry(), jobClient.getJobResult().getMsgInfo());
            return false;
        }

        // 校验任务是否配置重试策略, maxRetryNum > 0 说明配置重试
        if(!jobClient.getIsFailRetry()){
            LOGGER.info("[retry=false] jobId:{} isFailRetry:{} isFailRetry is false.", jobClient.getTaskId(), jobClient.getIsFailRetry());
            return false;
        }

        return true;
    }

    /**
     * 对任务状态判断是否需要重试, 只有任务状态未 failed 或者提交失败且已经重试的次数小于最大重试次数
     *
     * @param status            任务状态
     * @param scheduleJob       任务实例信息
     * @param jobCache          任务缓存
     * @param saveRetryFunction 重试日志保存函数
     * @return 是否进行重试
     */
    public boolean checkAndRestart(Integer status, ScheduleJob scheduleJob,EngineJobCache jobCache,BiConsumer<ScheduleJob,JobClient> saveRetryFunction){
        try {
            Pair<Boolean, JobClient> checkResult = checkJobInfo(scheduleJob.getJobId(), jobCache, status);
            if(!checkResult.getKey()){
                return false;
            }

            JobClient jobClient = checkResult.getValue();
            // 是否需要重新提交
            int alreadyRetryNum = getAlreadyRetryNum(scheduleJob.getJobId());
            // 超过最大重试次数则返回 false
            if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
                LOGGER.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getTaskId(), alreadyRetryNum, jobClient.getMaxRetryNum());
                return false;
            }

            // 通过engineJobId或appId获取日志
            jobClient.setEngineTaskId(scheduleJob.getEngineJobId());
            jobClient.setApplicationId(scheduleJob.getApplicationId());

            jobClient.setCallBack((jobStatus)-> updateJobStatus(scheduleJob.getJobId(), jobStatus));

            if(EngineType.Kylin.name().equalsIgnoreCase(jobClient.getEngineType())){
                setRetryTag(jobClient);
            }

            // checkpoint的路径
            if (EngineType.isFlink(jobCache.getEngineType())) {
                setCheckpointPath(jobClient);
            }

            boolean retry = restartJob(jobClient, saveRetryFunction);
            LOGGER.info("【retry={}】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", retry, jobClient.getTaskId(), alreadyRetryNum);
            return retry;
        } catch (Exception e) {
            LOGGER.error("checkAndRestart fail:{}", e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    private void setRetryTag(JobClient jobClient){
        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), Map.class);
            pluginInfoMap.put("retry", true);
            jobClient.setPluginInfo(PublicUtil.objToString(pluginInfoMap));
        } catch (IOException e) {
            LOGGER.error("Set retry tag error:", e);
        }
    }

    /**
     * 设置这次实例重试的 checkpoint path 为上次任务实例生成的最后一个checkpoint path
     *
     * @param jobClient client
     */
    private void setCheckpointPath(JobClient jobClient){

        String checkpointOpenExecution = jobClient.getConfProperties().getProperty("execution.checkpointing.interval");
        String checkpointOpenFlink = jobClient.getConfProperties().getProperty("flink.checkpoint.interval");
        String checkpointOpenSql = jobClient.getConfProperties().getProperty("sql.checkpoint.interval");
        boolean openCheckpoint = StringUtils.isNotBlank(checkpointOpenExecution)
                || StringUtils.isNotBlank(checkpointOpenFlink)
                || StringUtils.isNotBlank(checkpointOpenSql);

        if (!openCheckpoint){
            return;
        }

        if(StringUtils.isEmpty(jobClient.getTaskId())){
            return;
        }

        // 默认续跑, 可选择重跑
        if (ERetryType.CONTINUE.equals(ERetryType.getRetryType(jobClient.getRetryType(), ERetryType.CONTINUE))) {
            String retryCheckPointPath = jobCheckpointDealer.getRetryCheckPointPath(jobClient);
            if (StringUtils.isNotBlank(retryCheckPointPath)) {
                jobClient.setExternalPath(retryCheckPointPath);
            }
        }
        LOGGER.info("jobId:{} set checkpoint path:{}", jobClient.getTaskId(), jobClient.getExternalPath());
    }

    /**
     * 校验任务是否可以进行重试并构建 JobClient 信息, 任务状态部位 failed 或者 submit failed 则返回 false
     *
     * @param jobId    任务 id
     * @param jobCache 任务缓存
     * @param status   任务状态
     * @return 是否可以进行重试和对应的 JobClient 信息
     */
    private Pair<Boolean, JobClient> checkJobInfo(String jobId, EngineJobCache jobCache, Integer status) {
        Pair<Boolean, JobClient> check = new Pair<>(false, null);

        if(!RdosTaskStatus.FAILED.getStatus().equals(status) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(status)){
            return check;
        }

        try {
            String jobInfo = jobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
            if(!jobClient.getIsFailRetry()){
                LOGGER.info("[retry=false] jobId:{} isFailRetry:{} isFailRetry is false.", jobClient.getTaskId(), jobClient.getIsFailRetry());
                return check;
            }
            // Reset jobClient 属性
            // 重试时初始化 generateTime
            jobClient.setGenerateTime(System.currentTimeMillis());
            jobClient.setCoreJarInfo(null);
            return new Pair<>(true, jobClient);
        } catch (Exception e){
            // 解析任务的jobInfo反序列到ParamAction失败，任务不进行重试.
            LOGGER.error("[retry=false] jobId:{} default not retry, because getIsFailRetry happens error:.", jobId, e);
            return check;
        }
    }

    /**
     * 重新提交任务
     *
     * @param jobClient         任务执行相关信息
     * @param saveRetryFunction 重试日志保存 func
     * @return 是否重试成功
     */
    private boolean restartJob(JobClient jobClient, BiConsumer<ScheduleJob,JobClient> saveRetryFunction){
        EngineJobCache jobCache = engineJobCacheDao.getOne(jobClient.getTaskId());
        if (jobCache == null) {
            LOGGER.info("jobId:{} restart but jobCache is null.", jobClient.getTaskId());
            return false;
        }

        // 插件会修改jobClient的sql，所以需求从cache中重新获取sqlText
        String jobInfo = jobCache.getJobInfo();
        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            jobClient.setSql(paramAction.getSqlText());
        } catch (IOException e) {
            LOGGER.error("jobId:{} restart but convert paramAction error: ", jobClient.getTaskId(), e);
            return false;
        }

        // 添加到重试队列中
        boolean isAdd = jobDealer.addRestartJob(jobClient);
        if (isAdd) {
            String jobId = jobClient.getTaskId();
            // 更新缓存任务状态
            shardCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.RESTARTING.getStatus());

            //重试的任务不置为失败，waitengine
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobClient.getTaskId());
            if (saveRetryFunction != null) {
                saveRetryFunction.accept(scheduleJob, jobClient);
            } else {
                jobRetryRecord(scheduleJob, jobClient, null);
            }

            scheduleJobDao.updateJobStatus(jobId,RdosTaskStatus.RESTARTING.getStatus());
            LOGGER.info("jobId:{} update job status:{}.", jobId, RdosTaskStatus.RESTARTING.getStatus());

            // 更新重试次数
            increaseJobRetryNum(jobClient.getTaskId());
        }
        return isAdd;
    }

    /**
     * 保存任务重试记录
     *
     * @param scheduleJob 任务实例信息
     * @param jobClient   任务实例信息
     * @param engineLog   任务 engineLog
     */
    public void jobRetryRecord(ScheduleJob scheduleJob, JobClient jobClient,String engineLog) {
        try {
            ScheduleJobExpand expand = scheduleJobExpandDao.getLogByJobId(jobClient.getTaskId());
            if (expand == null) {
                expand = new ScheduleJobExpand();
            }
            EngineJobRetry batchJobRetry = EngineJobRetry.toEntity(scheduleJob,jobClient,expand,engineLog);
            batchJobRetry.setStatus(RdosTaskStatus.RESTARTING.getStatus());
            // 记录重试日志
            engineJobRetryDao.insert(batchJobRetry);
        } catch (Throwable e) {
            LOGGER.error("job {} add retry record error", scheduleJob.getJobId(), e);
        }
    }

    private void updateJobStatus(String jobId, Integer status) {
        scheduleJobDao.updateJobStatus(jobId, status);
        LOGGER.info("jobId:{} update job status:{}.", jobId, status);
    }

    /**
     * 获取任务已经重试的次数
     */
    private Integer getAlreadyRetryNum(String jobId){
        ScheduleJob rdosEngineBatchJob = scheduleJobDao.getRdosJobByJobId(jobId);
        return rdosEngineBatchJob == null || rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
    }

    private void increaseJobRetryNum(String jobId){
        ScheduleJob rdosEngineBatchJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (rdosEngineBatchJob == null) {
            return;
        }
        Integer retryNum = rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
        retryNum++;
        scheduleJobDao.updateRetryNum(jobId, retryNum);
    }
}
