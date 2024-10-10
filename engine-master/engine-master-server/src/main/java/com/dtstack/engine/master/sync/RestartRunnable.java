package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.dtcenter.common.enums.Deleted;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestartRunnable extends AbstractRestart implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(RestartRunnable.class);

    private Long id;
    private boolean justRunChild;
    private boolean setSuccess;
    private List<Long> subJobIds;
    private ScheduleTaskShadeDao scheduleTaskShadeDao;
    private String redisKey;
    private StringRedisTemplate redisTemplate;
    private ScheduleJobExpandDao scheduleJobExpandDao;

    public RestartRunnable(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds,
                           ScheduleJobDao scheduleJobDao, ScheduleTaskShadeDao scheduleTaskShadeDao,
                           ScheduleJobJobDao scheduleJobJobDao, EnvironmentContext environmentContext,
                           String redisKey, StringRedisTemplate redisTemplate,ScheduleJobService scheduleJobService,ScheduleJobExpandDao scheduleJobExpandDao) {
        super(scheduleJobDao,environmentContext,scheduleJobJobDao,scheduleJobService);
        this.id = id;
        this.justRunChild = BooleanUtils.toBoolean(justRunChild);
        this.setSuccess = BooleanUtils.toBoolean(setSuccess);
        this.subJobIds = subJobIds;
        this.scheduleTaskShadeDao = scheduleTaskShadeDao;
        this.redisKey = redisKey;
        this.redisTemplate =  redisTemplate;
        this.scheduleJobExpandDao = scheduleJobExpandDao;
    }

    @Override
    public void run() {
        try {
            ScheduleJob batchJob = scheduleJobDao.getOne(id);
            if (batchJob == null) {
                logger.error("cat not find job by id:{} ", id);
                return;
            }

            ScheduleTaskShade task = scheduleTaskShadeDao.getOne(batchJob.getTaskId(), batchJob.getAppType());
            if (task == null || Deleted.DELETED.getStatus().equals(task.getIsDeleted())) {
                logger.error("cat not find taskShade by taskId:{} appType {}", batchJob.getTaskId(), batchJob.getAppType());
                return;
            }

            Integer jobStatus = batchJob.getStatus();
            if (!RdosTaskStatus.canReset(jobStatus)) {
                logger.error("job {} status {}  can not restart ", batchJob.getJobId(), batchJob.getStatus());
                return;
            }
            Map<String,String> resumeBatchJobs = new HashMap<>();
            //置成功并恢复调度
            if (setSuccess && justRunChild) {
                List<String> jobIds = getSubFlowJob(batchJob);
                // 设置强规则任务
                List<String> ruleJobs = getRuleTask(batchJob);
                jobIds.add(batchJob.getJobId());
                jobIds.addAll(ruleJobs);
                scheduleJobDao.updateJobStatusByIds(RdosTaskStatus.MANUALSUCCESS.getStatus(), jobIds);
                logger.info("ids  {} manual success", jobIds);
                return;
            }

            //重跑并恢复调度
            if (!justRunChild) {
                resumeBatchJobs.put(batchJob.getJobId(),batchJob.getCycTime());
            }

            //重跑工作流中的子任务时，加入工作流任务，用于更新状态
            if (!StringUtils.equals("0", batchJob.getFlowJobId())) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(batchJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                if (flowJob != null) {
                    resumeBatchJobs.put(flowJob.getJobId(),flowJob.getCycTime());
                }
            }

            // 子任务不为空 重跑当前任务和自身
            if (CollectionUtils.isNotEmpty(subJobIds)) {
                List<ScheduleJob> jobs = scheduleJobDao.listByJobIds(subJobIds);
                resumeBatchJobs.putAll(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));

                // 判断该节点是否被强弱规则任务所依赖
                List<ScheduleJob> taskRuleSonJob = scheduleJobService.getTaskRuleSonJob(batchJob);
                if (CollectionUtils.isNotEmpty(taskRuleSonJob)) {
                    resumeBatchJobs.putAll(taskRuleSonJob.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));

                    // 判断所依赖的任务是否是工作流任务
                    for (ScheduleJob scheduleJob : taskRuleSonJob) {
                        setSubFlowJob(scheduleJob, resumeBatchJobs);
                    }
                }

                // 如果是工作流根节点 添加子节点
                setSubFlowJob(batchJob, resumeBatchJobs);

            } else {
                Map<String,String> allChildJobWithSameDayByForkJoin = scheduleJobService.getAllChildJobWithSameDayByForkJoin(batchJob.getJobId(), false);
                if (MapUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                    resumeBatchJobs.putAll(allChildJobWithSameDayByForkJoin);
                }
            }

            String key = "syncRestartJob_" + environmentContext.getRestartKey();
            int count = 0;
            logger.info("release redis key {} start get lock", redisKey);
            while (true) {
                if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
                    String execute = redisTemplate.execute((RedisCallback<String>) connection -> {
                        JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                        SetParams setParams = SetParams.setParams();
                        setParams.nx().ex(environmentContext.getRestartKeyTimeOut());
                        return commands.set(key, "engine_restart:" + Thread.currentThread().getName() + " IP:" + environmentContext.getLocalAddress(), setParams);
                    });

                    if (StringUtils.isNotBlank(execute)) {
                        // 说明抢到锁了，开始执行
                        try {
                            logger.info("release job {} redis key {} get lock", subJobIds, redisKey);
                            scheduleJobService.batchRestartScheduleJob(resumeBatchJobs);
                            break;
                        } finally {
                            redisTemplate.delete(key);
                            logger.info("release job {} redis key {} finally , return lock", subJobIds, redisKey);
                        }
                    }
                }

                if (count <= environmentContext.getRestartCount()) {
                    count++;
                    Thread.sleep(10L);
                } else {
                    logger.error("key :{} freed wait timeout", key);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("restart job {} error", id, e);
        } finally {
            redisTemplate.delete(redisKey);
            logger.info("release job {} redis key {} ", id, redisKey);
        }
    }
}