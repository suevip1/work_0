package com.dtstack.engine.master.executor;

import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.enums.TaskRunOrderEnum;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 补数据任务的执行器
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
@Component
public class FillJobExecutor extends AbstractJobExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(FillJobExecutor.class);

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    private Long operatorRecordStartId = 0L;

    @Override
    public EScheduleType getScheduleType() {
        return EScheduleType.FILL_DATA;
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        LOGGER.info("---stop FillJobExecutor----");
    }

    @Override
    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, Boolean isEq) {
        //query fill job from operator
        Integer fillJobExecutorJobLimit = environmentContext.getFillJobExecutorJobLimit();

        if (executorConfigDTO != null && executorConfigDTO.getScanNum() != null) {
            fillJobExecutorJobLimit = executorConfigDTO.getScanNum();
        }
        List<ScheduleJobOperatorRecord> records = scheduleJobOperatorRecordDao.listJobs(operatorRecordStartId, nodeAddress, Lists.newArrayList(OperatorType.FILL_DATA.getType(),
                OperatorType.MANUAL.getType()), fillJobExecutorJobLimit);
        if (CollectionUtils.isEmpty(records)) {
            operatorRecordStartId = 0L;
            return new ArrayList<>();
        }
        Optional<ScheduleJobOperatorRecord> max = records.stream().max(Comparator.comparing(ScheduleJobOperatorRecord::getId));
        max.ifPresent(scheduleJobOperatorRecord -> operatorRecordStartId = scheduleJobOperatorRecord.getId());

        records = removeOperatorRecord(records,OperatorType.FILL_DATA.getType());

        Set<String> jobIds = records.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toSet());

        List<ScheduleFillDataJob> fills = scheduleFillDataJobService.getFillIdByJobIds(jobIds);
        Map<Long, ScheduleFillDataJob> jobMap = fills.stream().collect(Collectors.toMap(ScheduleFillDataJob::getId, g -> g));
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByJobIds(nodeAddress, JobPhaseStatus.CREATE.getCode(), null, jobIds);
        LOGGER.info("getFillDataJob nodeAddress {} start scanning since when startId:{}  queryJobSize {} ", nodeAddress, startId, scheduleJobs.size());
        if (jobIds.size() > scheduleJobs.size()) {
            //check lost operator records can remove
            Set<String> needSubmit = scheduleJobs.stream().map(ScheduleJob::getJobId).collect(Collectors.toSet());
            jobIds.removeAll(needSubmit);
            removeFillOperatorRecord(jobIds, records,jobMap);
        }
        return getScheduleBatchJobList(scheduleJobs,jobMap);
    }

    public void removeFillOperatorRecord(Collection<String> jobIds, Collection<ScheduleJobOperatorRecord> records,Map<Long, ScheduleFillDataJob> jobMap) {
        Map<String, ScheduleJobOperatorRecord> recordMap = records.stream().collect(Collectors.toMap(ScheduleJobOperatorRecord::getJobId, k -> k));
        List<EngineJobCache> caches = engineJobCacheDao.getByJobIds(Lists.newArrayList(jobIds));
        Map<String, EngineJobCache> jobCacheMap = caches.stream().collect(Collectors.toMap(EngineJobCache::getJobId, k -> k));
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listByJobIdList(jobIds, null);
        Map<String, ScheduleJob> scheduleJobMap = scheduleJobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId, k -> k));

        List<String> needDeletedRecord = Lists.newArrayList();
        for (String jobId : jobIds) {
            ScheduleJobOperatorRecord record = recordMap.get(jobId);
            if (null == record) {
                continue;
            }

            EngineJobCache cache = jobCacheMap.get(jobId);
            if (cache == null) {
                ScheduleJob scheduleJob = scheduleJobMap.get(jobId);
                if (scheduleJob != null && RdosTaskStatus.STOPPED_STATUS.contains(scheduleJob.getStatus())) {
                    needDeletedRecord.add(jobId);
                    LOGGER.info("remove schedule:[{}] operator record:[{}] type:[{}]", record.getJobId(), record.getId(), record.getOperatorType());
                    //has submit to cache
                    if (FillJobTypeEnum.RUN_JOB.getType().equals(scheduleJob.getFillType())) {
                        ScheduleFillDataJob fillDataJob = jobMap.get(scheduleJob.getFillId());
                        if (fillDataJob != null && fillDataJob.getMaxParallelNumAndIncrement() > 0) {
                            scheduleFillDataJobService.decrementParallelNum(fillDataJob.getId());
                            LOGGER.info("jobId:{} fillId:{} count-- success jobStatus:{}", jobId, fillDataJob.getId(), scheduleJob.getStatus());
                        }
                    }
                }

                if (scheduleJob == null) {
                    needDeletedRecord.add(jobId);
                    LOGGER.info("remove schedule:[{}] operator record:[{}] type:[{}]", record.getJobId(), record.getId(), record.getOperatorType());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(needDeletedRecord)) {
            try {
                scheduleJobOperatorRecordDao.deleteByJobIdsAndType(needDeletedRecord,OperatorType.FILL_DATA.getType());
                LOGGER.info("remove schedule:[{}] type:[{}]", needDeletedRecord, OperatorType.FILL_DATA.getType());
            } catch (Exception e) {
                LOGGER.error("delete error :",e);
            }
        }
    }

    public List<ScheduleBatchJob> getScheduleBatchJobList(List<ScheduleJob> scheduleJobs, Map<Long, ScheduleFillDataJob> jobMap) {
        List<ScheduleBatchJob> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return resultList;
        }

        List<String> jobKeys = scheduleJobs.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());

        List<ScheduleJobJob> jobJobs = scheduleJobJobDao.listByJobKeys(jobKeys, RelyTypeEnum.NORMAL.getType());
        Map<String, List<ScheduleJobJob>> jobjobMaps = jobJobs.stream().collect(Collectors.groupingBy(ScheduleJobJob::getJobKey));
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleFillDataJob scheduleFillDataJob = jobMap.get(scheduleJob.getFillId());
            if (scheduleFillDataJob != null) {
                Integer fillGeneratStatus = scheduleFillDataJob.getFillGeneratStatus();
                if (FillGeneratStatusEnum.DEFAULT_VALUE.getType().equals(fillGeneratStatus) || FillGeneratStatusEnum.FILL_FINISH.getType().equals(fillGeneratStatus)) {
                    ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
                    List<ScheduleJobJob> scheduleJobJobs = jobjobMaps.get(scheduleJob.getJobKey());
                    if (CollectionUtils.isEmpty(scheduleJobJobs)) {
                        scheduleJobJobs = Lists.newArrayList();
                    }
                    scheduleBatchJob.setJobJobList(scheduleJobJobs);
                    resultList.add(scheduleBatchJob);
                }
            }
        }

        return resultList;
    }

    @Override
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        return 0L;
    }

    @Override
    protected boolean isPutQueue(JobCheckRunInfo checkRunInfo, ScheduleBatchJob scheduleBatchJob){
        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobService.getFillById(scheduleBatchJob.getScheduleJob().getFillId());
        if (scheduleFillDataJob != null) {
            if (TaskRunOrderEnum.AES.getType().equals(scheduleFillDataJob.getTaskRunOrder())) {
                // 设置按照业务日志排序,查询前一天是否有补数据实例不是停止状态
                if (!checkTaskRunOrder(scheduleBatchJob, scheduleFillDataJob)) {
                    return Boolean.FALSE;
                }
            }

            if (FillJobTypeEnum.RUN_JOB.getType().equals(scheduleBatchJob.getScheduleJob().getFillType())
                    && scheduleBatchJob.getScheduleJob().getFlowJobId().equals("0")) {
                Integer maxParallelNum = scheduleFillDataJob.getMaxParallelNumAndIncrement();
                if (maxParallelNum > 0) {
                    return fillDataIsPutQueue(checkRunInfo, scheduleBatchJob, scheduleFillDataJob);
                }
            }
        }
        return super.isPutQueue(checkRunInfo, scheduleBatchJob);
    }

    private boolean checkTaskRunOrder(ScheduleBatchJob scheduleBatchJob, ScheduleFillDataJob scheduleFillDataJob) {
        ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
        String cycTime = scheduleJob.getCycTime();
        DateTime dateTime = new DateTime(DateUtil.parseDate(cycTime, DateUtil.UN_STANDARD_DATETIME_FORMAT));
        DateTime startDateTime = dateTime.minusDays(1).withTimeAtStartOfDay();
        DateTime endDateTIme = startDateTime.plusDays(1);
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listByFillAndRangCycTime(scheduleFillDataJob.getId(),
                startDateTime.toString(DateUtil.UN_STANDARD_DATETIME_FORMAT),
                endDateTIme.toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));

        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return Boolean.TRUE;
        }

        for (ScheduleJob job : scheduleJobs) {
            if (!RdosTaskStatus.STOPPED_STATUS.contains(job.getStatus())) {
                // 说明上一个周期没有运行完成
                LOGGER.info("jobId:{} taskRunOrderEnum {} no run finish job:{} ", scheduleBatchJob.getJobId(),scheduleFillDataJob.getTaskRunOrder(),job.getJobId());
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean incrementParallelNum(ScheduleBatchJob scheduleBatchJob, ScheduleFillDataJob scheduleFillDataJob) {
        Integer count = scheduleFillDataJobService.incrementParallelNum(scheduleFillDataJob.getId());
        // 没有更新成功，说明正在运行的实例已经到达maxParallelNum
        if (count == 0) {
            return Boolean.FALSE;
        } else {
            LOGGER.info("jobId:{} fillId:{} count++ success count:{}", scheduleBatchJob.getJobId(), scheduleFillDataJob.getId(), count);
            return Boolean.TRUE;
        }
    }

    protected boolean fillDataIsPutQueue(JobCheckRunInfo checkRunInfo, ScheduleBatchJob scheduleBatchJob, ScheduleFillDataJob scheduleFillDataJob) {
        Integer status;
        String errMsg = checkRunInfo.getErrMsg();
        if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
            LOGGER.info("jobId:{} checkRunInfo.status:{} put success queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return incrementParallelNum(scheduleBatchJob, scheduleFillDataJob);
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH
                || checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_NOT_FINISHED
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_FINISHED) {
            LOGGER.info("jobId:{} checkRunInfo.status:{} unable put to queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION
                || checkRunInfo.getStatus() == JobCheckStatus.TASK_DELETE
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_NO_CREATED
                || checkRunInfo.getStatus() == JobCheckStatus.RESOURCE_OVER_LIMIT) {
            status = RdosTaskStatus.FAILED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION) {
            //上游任务失败
            status = RdosTaskStatus.PARENTFAILED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_CANCELED) {
            status = RdosTaskStatus.KILLED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TASK_PAUSE
                || checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_FROZEN) {
            status = RdosTaskStatus.FROZEN.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_OVER_EXPIRE
                || JobCheckStatus.DEPENDENCY_JOB_EXPIRE.equals(checkRunInfo.getStatus())) {
            //更新为自动取消
            status = RdosTaskStatus.EXPIRE.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_SUCCESS) {
            status = RdosTaskStatus.FAILED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.NO_TASK) {
            status = RdosTaskStatus.AUTOCANCELED.getStatus();
        } else {
            LOGGER.error("appear unknown jobId:{} checkRunInfo.status:{} ", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        }

        if (incrementParallelNum(scheduleBatchJob, scheduleFillDataJob)) {
            LOGGER.info("jobId:{} checkRunInfo.status:{} errMsg:{} status:{} update status.", scheduleBatchJob.getJobId(), checkRunInfo.getStatus(), errMsg, status);
            batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), status, errMsg);
            batchFlowWorkJobService.batchUpdateFlowSubJobStatus(scheduleBatchJob.getScheduleJob(), status);
        }
        return Boolean.FALSE;
    }
}
