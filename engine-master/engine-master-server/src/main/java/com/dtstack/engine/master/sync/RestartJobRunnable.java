package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobOperate;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.OperateTypeEnum;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.sync.restart.*;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 上午10:01
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class RestartJobRunnable extends AbstractRestart implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(RestartJobRunnable.class);

    private final List<String> jobIds;
    private final RestartType restartType;
    private final ScheduleTaskShadeDao scheduleTaskShadeDao;
    private final String redisKey;
    private final StringRedisTemplate redisTemplate;
    private final AbstractRestartJob abstractRestartJob;
    private final ScheduleJobOperateService scheduleJobOperateService;

    private Long operateId;

    public RestartJobRunnable(List<String> jobIds,
                              RestartType restartType,
                              ScheduleJobDao scheduleJobDao,
                              ScheduleTaskShadeDao scheduleTaskShadeDao,
                              ScheduleJobJobDao scheduleJobJobDao,
                              EnvironmentContext environmentContext,
                              String redisKey,
                              StringRedisTemplate redisTemplate, ScheduleJobService scheduleJobService, ScheduleJobOperateService scheduleJobOperateService, Long operateId) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
        this.jobIds = jobIds;
        this.restartType = restartType;
        this.abstractRestartJob = getAbstractRestartJob(operateId);
        this.scheduleTaskShadeDao = scheduleTaskShadeDao;
        this.redisKey = redisKey;
        this.redisTemplate = redisTemplate;
        this.operateId = operateId;
        this.scheduleJobOperateService = scheduleJobOperateService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("reset start jobIds:{},restartType:{}", jobIds, restartType);
            List<ScheduleJob> jobs = scheduleJobDao.getSimpleInfoByJobIds(jobIds);

            if (CollectionUtils.isEmpty(jobs)) {
                LOGGER.error("cat not find job by id:{} ", jobIds.toString());
                return;
            }

            // 需要恢复调度的任务实例
            Map<String, String> resumeBatchJobs = Maps.newHashMap();

            Map<Integer, Set<Long>> appTaskListMapping = jobs.stream()
                    .collect(Collectors.groupingBy(ScheduleJob::getAppType, Collectors.mapping(ScheduleJob::getTaskId, Collectors.toSet())));

            Table<Long, Integer, ScheduleTaskShade> taskShadeMap = HashBasedTable.create();
            ScheduleTaskShade oimTaskShade = null;
            for (Integer appType : appTaskListMapping.keySet()) {
                Set<Long> taskIds = appTaskListMapping.get(appType);
                List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listSimpleByTaskIds(taskIds, null, appType);

                Map<Long, List<ScheduleTaskShade>> taskIdGroup = scheduleTaskShades.stream().collect(Collectors.groupingBy(ScheduleTaskShade::getTaskId));

                for (Map.Entry<Long, List<ScheduleTaskShade>> entry : taskIdGroup.entrySet()) {
                    Long taskId = entry.getKey();

                    List<ScheduleTaskShade> value = entry.getValue();
                    for (ScheduleTaskShade scheduleTaskShade : value) {
                        if (IsDeletedEnum.NOT_DELETE.getType().equals(scheduleTaskShade.getIsDeleted())) {
                            taskShadeMap.put(scheduleTaskShade.getTaskId(), scheduleTaskShade.getAppType(), scheduleTaskShade);
                            oimTaskShade = scheduleTaskShade;
                        }
                    }
                    ScheduleTaskShade scheduleTaskShade = taskShadeMap.get(taskId, appType);

                    if (scheduleTaskShade == null) {
                        // 说明查询出来的一个任务已经被下线，不能执行重跑逻辑
                        LOGGER.error("has task offline id:{} task:{}", jobIds.toString(), taskId);
                        return;
                    }
                }
            }

            // 查询出能重跑的任务
            jobs = findCanResetJob(jobs, taskShadeMap,resumeBatchJobs,restartType);

            if (CollectionUtils.isNotEmpty(jobs)) {
                Map<String, String> computeResumeBatchJobs = abstractRestartJob.computeResumeBatchJobs(jobs);
                if (MapUtils.isNotEmpty(computeResumeBatchJobs)) {
                    resumeBatchJobs.putAll(computeResumeBatchJobs);
                }

                // 分批次插入操作记录
                List<List<String>> partition = Lists.partition(Lists.newArrayList(resumeBatchJobs.keySet()), environmentContext.getRestartOperatorRecordMaxSize());
                long oId = (operateId == null ? -1 : operateId);
                for (List<String> jobIds : partition) {
                    List<ScheduleJobOperate> scheduleJobOperates = buildBatchOperate(jobIds, oId, OperateTypeEnum.RESTART.getCode(), oimTaskShade);
                    scheduleJobOperateService.addScheduleJobOperates(scheduleJobOperates);
                }

                // 判断是否是手动置成功
                if (RestartType.SET_SUCCESSFULLY_AND_RESUME_SCHEDULING.equals(restartType)
                    || RestartType.SET_SUCCESSFULLY.equals(restartType)) {
                    List<ScheduleJobOperate> scheduleJobOperates = buildBatchOperate(jobIds, oId, OperateTypeEnum.RESTART.getCode(), oimTaskShade);
                    if (CollectionUtils.isNotEmpty(scheduleJobOperates)) {
                        scheduleJobOperateService.addScheduleJobOperates(scheduleJobOperates);
                    }
                }

                String key = "syncRestartJob_" + environmentContext.getRestartKey();
                int count = 0;
                LOGGER.info("release job {} redis key {} start get lock", jobIds, redisKey);
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
                                LOGGER.info("release job {} redis key {} get lock", jobIds, redisKey);
                                scheduleJobService.batchRestartScheduleJob(resumeBatchJobs);
                                break;
                            } finally {
                                redisTemplate.delete(key);
                                LOGGER.info("release job {} redis key {} finally , return lock", jobIds, redisKey);
                            }
                        }

                    }

                    if (count <= environmentContext.getRestartCount()) {
                        count++;
                        Thread.sleep(10L);
                    } else {
                        LOGGER.error("key :{} freed wait timeout", key);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("restart job {} error", jobIds, e);
        } finally {
            redisTemplate.delete(redisKey);
            LOGGER.info("release job {} redis key {} ", jobIds, redisKey);
        }
    }


    /**
     * @param jobs
     * @param taskShadeMap
     * @param resumeBatchJobs
     * @return
     */
    private List<ScheduleJob> findCanResetJob(List<ScheduleJob> jobs, Table<Long,Integer,ScheduleTaskShade> taskShadeMap,Map<String, String> resumeBatchJobs,RestartType restartType) {
        List<ScheduleJob> canResetList = Lists.newArrayList();
        for (ScheduleJob job : jobs) {
            Integer jobStatus = job.getStatus();

            if ((RestartType.RESTART_CURRENT_NODE.equals(restartType)
                    || RestartType.RESTART_CURRENT_AND_DOWNSTREAM_NODE.equals(restartType)
                    ||  RestartType.RESTART_CURRENT_AND_RESUME_SCHEDULING.equals(restartType))
                    && !RdosTaskStatus.canReset(jobStatus)) {
                LOGGER.error("job {} status {}  can not restart ", job.getJobId(), job.getStatus());
                continue;
            }

            ScheduleTaskShade scheduleTaskShade = taskShadeMap.get(job.getTaskId(), job.getAppType());
            if (scheduleTaskShade == null || Deleted.DELETED.getStatus().equals(scheduleTaskShade.getIsDeleted())) {
                LOGGER.error("cat not find taskShade by taskId:{} appType {}", job.getTaskId(), job.getAppType());
                continue;
            }

            canResetList.add(job);

            // 判断这个任务是否是工作流子任务，如果是需要带上工作流任务
            if (!StringUtils.equals("0", job.getFlowJobId())) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
                if (flowJob != null) {
                    resumeBatchJobs.put(flowJob.getJobId(), flowJob.getCycTime());
                }
            }
        }
        return canResetList;
    }

    public AbstractRestartJob getAbstractRestartJob(Long operateId) {
        switch (restartType) {
            case SET_SUCCESSFULLY_AND_RESUME_SCHEDULING:
                return new SetSuccessAndResumeSchedulingRestartJob(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
            case RESTART_CURRENT_NODE:
                return new RestartCurrentNodeRestartJob(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
            case RESTART_CURRENT_AND_DOWNSTREAM_NODE:
            case RESTART_CURRENT_AND_RESUME_SCHEDULING:
                return new RestartCurrentAndDownStreamNodeRestartJob(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService,operateId);
            case SET_SUCCESSFULLY:
                return new SetSuccessFully(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
            default:
                return null;
        }
    }

    private String getContent(String jobId, ScheduleTaskShade oimTaskShade) {
        switch (restartType) {
            case SET_SUCCESSFULLY_AND_RESUME_SCHEDULING:
                return String.format("重跑实例(由%s的置成功并恢复调度触发)", oimTaskShade.getName());
            case RESTART_CURRENT_NODE:
                return "重跑实例";
            case RESTART_CURRENT_AND_DOWNSTREAM_NODE:
                return jobIds.contains(jobId) ? "重跑当前实例及其下游" : String.format("重跑实例(由%s的重跑当前实例及其下游触发)", oimTaskShade.getName());
            case RESTART_CURRENT_AND_RESUME_SCHEDULING:
                return jobIds.contains(jobId) ? "重跑并恢复调度" : String.format("重跑实例(由%s的重跑并恢复调度触发)", oimTaskShade.getName());
            case SET_SUCCESSFULLY:
                return "置成功不恢复调度";
            default:
                return "";
        }
    }

    private List<ScheduleJobOperate> buildBatchOperate(final List<String> jobIds,
                                                       Long operateId,
                                                       Integer operateType,
                                                       final ScheduleTaskShade oimTaskShade) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return Collections.emptyList();
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<ScheduleJobOperate> scheduleJobOperates = new ArrayList<>(jobIds.size());
        for (String jobId : jobIds) {
            ScheduleJobOperate scheduleJobOperator = new ScheduleJobOperate();
            scheduleJobOperator.setJobId(jobId);
            scheduleJobOperator.setOperateId(operateId);
            scheduleJobOperator.setOperateType(operateType);
            scheduleJobOperator.setOperateContent(getContent(jobId, oimTaskShade));
            scheduleJobOperator.setGmtModified(now);
            scheduleJobOperator.setGmtCreate(now);
            scheduleJobOperator.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            scheduleJobOperates.add(scheduleJobOperator);
        }
        return scheduleJobOperates;
    }

}
