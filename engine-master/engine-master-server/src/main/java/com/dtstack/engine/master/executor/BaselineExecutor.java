package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.BaselineStatusEnum;
import com.dtstack.engine.api.enums.FinishStatus;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.BaselineBlockJobRecordDao;
import com.dtstack.engine.dao.BaselineJobJobDao;
import com.dtstack.engine.dao.BaselineTaskDao;
import com.dtstack.engine.master.dto.BaselineBlockDTO;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.master.handler.AbstractMasterHandler;
import com.dtstack.engine.master.impl.BaselineJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.listener.AlterEvent;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.BaselineBlockJobRecord;
import com.dtstack.engine.po.BaselineJob;
import com.dtstack.engine.po.BaselineJobJob;
import com.dtstack.engine.po.BaselineTask;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/16 10:32 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class BaselineExecutor extends AbstractMasterHandler implements InitializingBean, ApplicationListener<ApplicationReadyEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(BaselineExecutor.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private BaselineTaskDao baselineTaskDao;

    @Autowired
    private BaselineJobService baselineJobService;

    @Autowired
    private BaselineJobJobDao baselineJobJobDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private BaselineBlockJobRecordDao baselineBlockJobRecordDao;

    @Autowired
    private AlterEvent alterEvent;

    private ScheduledExecutorService scheduledService;

    private final List<Integer> statusEnums = Lists.newArrayList(BaselineStatusEnum.SAFETY.getCode(),
            BaselineStatusEnum.WARNING.getCode(),
            BaselineStatusEnum.BREAKING_THE_LINE.getCode(),
            BaselineStatusEnum.TIMING_NOT_COMPLETED.getCode(),
            BaselineStatusEnum.OTHERS.getCode());

    @Override
    public void handle() {
        try {
            if (!isMaster()) {
                LOGGER.info("BaselineExecutor node is not master");
                return;
            }

            if (!environmentContext.getBaselineOpen()) {
                LOGGER.info("scanningBaseline node is close baseline scanning");
                return;
            }

            scanningBaseline();
        } catch (Throwable e) {
            LOGGER.error("error happen during handle scanningBaseline:{}", e.getMessage(), e);
        }
    }

    private void scanningBaseline() {
        LOGGER.info("scanning baseline task start");
        try {
            Long startId = null;
            Timestamp businessDate = getCurrentBusinessDate();
            List<BaselineJob> baselineJobs = baselineJobService.scanningBaselineJobById(startId,businessDate,
                    statusEnums, FinishStatus.NO_FINISH.getCode(),environmentContext.getAlertTriggerRecordReceiveLimit());
            

            while (CollectionUtils.isNotEmpty(baselineJobs)) {

                List<Long> baselineTaskIds = baselineJobs.stream().map(BaselineJob::getBaselineTaskId).collect(Collectors.toList());
                List<BaselineTask> baselineTasks = baselineTaskDao.selectByIds(baselineTaskIds);
                Map<Long, BaselineTask> taskMap = baselineTasks.stream().collect(Collectors.toMap(BaselineTask::getId, g -> (g)));
                for (BaselineJob baselineJob : baselineJobs) {
                    try {
                        LOGGER.info("scanning baseline task: {}", baselineJob.getId());
                        BaselineTask baselineTask = taskMap.get(baselineJob.getBaselineTaskId());

                        if (baselineTask == null) {
                            LOGGER.warn("baseline task not find:baselineTaskId:{} baselineJobId:{}", baselineJob.getBaselineTaskId()
                                    ,baselineJob.getId());
                            baselineJobService.updateFinishStatus(baselineJob.getId(),FinishStatus.FINISH.getCode(),new Timestamp(System.currentTimeMillis()),BaselineStatusEnum.OTHERS.getCode());
                            continue;
                        }

                        calculateBaselineJobStatus(baselineJob,baselineTask);

                        if (startId == null || startId < baselineJob.getId()) {
                            startId = baselineJob.getId();
                        }
                    } catch (Exception e) {
                        LOGGER.error("error happen during handle calculateBaselineJobStatus:{}, baselineJobId:{}", e.getMessage(), baselineJob.getId(), e);
                    }
                }

                baselineJobs = baselineJobService.scanningBaselineJobById(startId,businessDate,
                        statusEnums, FinishStatus.NO_FINISH.getCode(),environmentContext.getAlertTriggerRecordReceiveLimit());
            }
        } catch (Throwable e) {
            LOGGER.error("error happen during handle scanningBaseline:{}", e.getMessage(), e);
        }
    }

    private Timestamp getCurrentBusinessDate() {
        return new Timestamp(DateUtil.calTodayMills());
    }

    private void calculateBaselineJobStatus(BaselineJob baselineJob,BaselineTask baselineTask) {
        // 分批获取实例
        Long startId = null;

        List<BaselineJobJob> baselineJobJobs =  baselineJobJobDao.scanningBaselineJobJobByBaselineJobId(startId,baselineJob.getId(),
                environmentContext.getAlertTriggerRecordReceiveLimit());

        List<BaselineBlockDTO> baselineStatues = Lists.newArrayList();
        Integer finishStatus = FinishStatus.FINISH.getCode();
        while (CollectionUtils.isNotEmpty(baselineJobJobs)) {
            List<String> jobIds = baselineJobJobs.stream().map(BaselineJobJob::getJobId).collect(Collectors.toList());
            List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(jobIds);
            Map<String, List<ScheduleJob>> scheduleJobMap = scheduleJobs.stream().collect(Collectors.groupingBy(ScheduleJob::getJobId));

            for (BaselineJobJob jobJob : baselineJobJobs) {
                ScheduleJob scheduleJob = getOne(scheduleJobMap, jobJob);

                Integer isFinishStatus = calculateBaselineStatus(baselineJob,jobJob, scheduleJob, baselineStatues);

                if (FinishStatus.NO_FINISH.getCode().equals(isFinishStatus)) {
                    finishStatus = isFinishStatus;
                }

                if (startId == null || startId < jobJob.getId()) {
                    startId = jobJob.getId();
                }
            }

            baselineJobJobs =  baselineJobJobDao.scanningBaselineJobJobByBaselineJobId(startId,baselineJob.getId(),
                    environmentContext.getAlertTriggerRecordReceiveLimit());
        }

        BaselineBlockJobRecord maxBaselineBlockJobRecord = null;
        BaselineBlockDTO baselineBlockDTO = new BaselineBlockDTO();
        Integer maxBaselineStatus = null;
        for (BaselineBlockDTO baselineStatue : baselineStatues) {
            if (maxBaselineBlockJobRecord == null || baselineStatue.getBaselineStatus() < maxBaselineStatus) {
                maxBaselineBlockJobRecord = baselineStatue.getBaselineBlockJobRecord();
                maxBaselineStatus = baselineStatue.getBaselineStatus();
                baselineBlockDTO = baselineStatue;
            }
        }

        if (maxBaselineBlockJobRecord == null || maxBaselineStatus == null) {
            return;
        }

        insertBaselineBlockJobRecord(maxBaselineStatus,maxBaselineBlockJobRecord);

        if (FinishStatus.FINISH.getCode().equals(finishStatus)) {
            AlterEventContext context = getFinishContext(baselineJob,baselineBlockDTO,baselineTask);
            context.setFinishStatus(finishStatus);
            alterEvent.event(context);
        }

        if (maxBaselineStatus > baselineJob.getBaselineStatus()) {
            AlterEventContext context = getFinishContext(baselineJob,baselineBlockDTO,baselineTask);
            context.setBaselineStatus(maxBaselineStatus);
            alterEvent.event(context);
        }

        Timestamp finishTime = null;
        if (FinishStatus.FINISH.getCode().equals(finishStatus)) {
            finishTime = new Timestamp(System.currentTimeMillis());
        }

        baselineJobService.updateFinishStatus(baselineJob.getId(),finishStatus,finishTime,maxBaselineStatus);
    }

    private void insertBaselineBlockJobRecord(Integer maxBaselineStatus,BaselineBlockJobRecord maxBaselineBlockJobRecord) {
        baselineBlockJobRecordDao.deleteByBaselineJobId(maxBaselineBlockJobRecord.getBaselineJobId());

        if (maxBaselineStatus == 0) {
            return;
        }
        Long taskId = maxBaselineBlockJobRecord.getTaskId();
        Integer appType = maxBaselineBlockJobRecord.getAppType();
        maxBaselineBlockJobRecord.setTaskName("");
        if (taskId != null && appType != null) {
            ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(taskId, appType);
            if (scheduleTaskShade != null) {
                maxBaselineBlockJobRecord.setTaskName(scheduleTaskShade.getName());
            }
        }
        maxBaselineBlockJobRecord.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        baselineBlockJobRecordDao.insert(maxBaselineBlockJobRecord);
    }

    private AlterEventContext getFinishContext(BaselineJob baselineJob,BaselineBlockDTO baselineBlockDTO,BaselineTask baselineTask) {
        AlterEventContext context = new AlterEventContext();
        context.setKey(AlterKey.baseline);
        context.setBaselineJob(baselineJob);
        context.setAppType(baselineJob.getAppType());
        context.setBaselineBlockDTO(baselineBlockDTO);
        context.setOwnerUserId(baselineTask.getOwnerUserId());
        return context;
    }

    private Integer calculateBaselineStatus(BaselineJob baselineJob,
                                            BaselineJobJob jobJob,
                                            ScheduleJob scheduleJob,
                                            List<BaselineBlockDTO> baselineBlockJobRecords) {
        Integer finishStatus = FinishStatus.FINISH.getCode();

        if (scheduleJob == null) {
            baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob
                    ,null,BaselineStatusEnum.OTHERS.getCode(),String.format(ConstBaselineBlockingReason.JOB_NO_FIND,jobJob.getJobId())));
            return finishStatus;
        }

        Integer status = scheduleJob.getStatus();

        if (!RdosTaskStatus.FINISHED.getStatus().equals(status)) {
            // 任务没有运行完，执行更新成未完成
            finishStatus = 0;
        }

        Integer estimatedExecTime = jobJob.getEstimatedExecTime();
        Timestamp earlyWarningStartTimestamp = jobJob.getEarlyWarningStartTime();
        Timestamp earlyWarningEndTimestamp = jobJob.getEarlyWarningEndTime();
        Timestamp brokenLineEndTimestamp = jobJob.getBrokenLineEndTime();
        Timestamp brokenLineStartTimestamp = jobJob.getBrokenLineStartTime();
        Timestamp expectFinishTime = baselineJob.getExpectFinishTime();
        if (estimatedExecTime == null ||
                earlyWarningStartTimestamp == null ||
                earlyWarningEndTimestamp == null ||
                brokenLineEndTimestamp == null ||
                brokenLineStartTimestamp == null ||
                expectFinishTime == null) {
            baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob
                    , scheduleJob, BaselineStatusEnum.OTHERS.getCode(), String.format(ConstBaselineBlockingReason.UNABLE_TO_CALCULATE, jobJob.getJobId())));
            return finishStatus;
        }

        long currentTime = System.currentTimeMillis();
        Timestamp execEndTime = scheduleJob.getExecEndTime();
        if (RdosTaskStatus.FINISH_STATUS.contains(status)) {
            // 任务运行完成，不做判断
            if (execEndTime == null || execEndTime.getTime() > expectFinishTime.getTime()) {
                baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob, scheduleJob, BaselineStatusEnum.TIMING_NOT_COMPLETED.getCode(),
                        String.format(ConstBaselineBlockingReason.TIMING_NOT_COMPLETED, baselineJob.getName(), jobJob.getJobId())));
                return finishStatus;
            }
            baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob,scheduleJob,BaselineStatusEnum.SAFETY.getCode(),""));
            return finishStatus;
        }

        // 其他状态判断
        if (RdosTaskStatus.STOP_STATUS.contains(status)
                || RdosTaskStatus.EXPIRE_STATUS.contains(status)
                || RdosTaskStatus.FROZEN_STATUS.contains(status) ) {
            baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob,scheduleJob,BaselineStatusEnum.OTHERS.getCode(),
                    String.format(ConstBaselineBlockingReason.JOB_STATUS_OTHER,jobJob.getJobId(),RdosTaskStatus.getShowStatus(status))));
            return finishStatus;
        }

        // 时间轴
        long earlyWarningStartTime = earlyWarningStartTimestamp.getTime();
        long earlyWarningEndTime = earlyWarningEndTimestamp.getTime();
        long brokenLineStartTime = brokenLineStartTimestamp.getTime();

        // 定时未完成
        if (currentTime > expectFinishTime.getTime()) {
            baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob,scheduleJob,BaselineStatusEnum.TIMING_NOT_COMPLETED.getCode(),
                    String.format(ConstBaselineBlockingReason.TIMING_NOT_COMPLETED,baselineJob.getName(),jobJob.getJobId())));
            return finishStatus;
        }
        // 当前实例是未提交状态
        if (RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
            if (currentTime > earlyWarningStartTime && currentTime < brokenLineStartTime) {
                baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob,scheduleJob,BaselineStatusEnum.WARNING.getCode(),
                        String.format(ConstBaselineBlockingReason.EARLY_WARNING,jobJob.getJobId())));
                return finishStatus;
            }
        }

        // 当前时间是运行状态
        if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
            if (currentTime > earlyWarningEndTime && currentTime < brokenLineStartTime) {
                baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob,scheduleJob,BaselineStatusEnum.WARNING.getCode(),
                        String.format(ConstBaselineBlockingReason.EARLY_WARNING,jobJob.getJobId())));
                return finishStatus;
            }
        }

        if (currentTime > brokenLineStartTime && !RdosTaskStatus.FINISH_STATUS.contains(status)) {
            baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob, scheduleJob, BaselineStatusEnum.BREAKING_THE_LINE.getCode(),
                    String.format(ConstBaselineBlockingReason.BREAKING_THE_LINE, jobJob.getJobId())));
            return finishStatus;
        }

        baselineBlockJobRecords.add(buildBaselineBlockJobRecord(baselineJob,scheduleJob,BaselineStatusEnum.SAFETY.getCode(),""));
        return finishStatus;
    }

    private BaselineBlockDTO buildBaselineBlockJobRecord(BaselineJob baselineJob, ScheduleJob scheduleJob, Integer code, String blockingReason) {
        BaselineBlockJobRecord baselineBlockJobRecord = new BaselineBlockJobRecord();

        baselineBlockJobRecord.setBaselineJobId(baselineJob.getId());
        baselineBlockJobRecord.setBaselineTaskId(baselineJob.getBaselineTaskId());
        baselineBlockJobRecord.setAppType(baselineJob.getAppType());
        baselineBlockJobRecord.setProjectId(baselineJob.getProjectId());
        baselineBlockJobRecord.setTenantId(baselineJob.getTenantId());
        baselineBlockJobRecord.setOwnerUserId(baselineJob.getOwnerUserId());
        baselineBlockJobRecord.setBlockingReason(blockingReason);
        if (scheduleJob != null) {
            baselineBlockJobRecord.setJobStatus(RdosTaskStatus.getShowStatus(scheduleJob.getStatus()));
            baselineBlockJobRecord.setTaskId(scheduleJob.getTaskId());
        }

        BaselineBlockDTO baselineBlockDTO = new BaselineBlockDTO();
        baselineBlockDTO.setBaselineStatus(code);
        baselineBlockDTO.setBaselineBlockJobRecord(baselineBlockJobRecord);
        return baselineBlockDTO;
    }

    private ScheduleJob getOne(Map<String, List<ScheduleJob>> scheduleJobMap, BaselineJobJob jobJob) {
        List<ScheduleJob> jobs = scheduleJobMap.get(jobJob.getJobId());

        if (CollectionUtils.isNotEmpty(jobs)) {
            return jobs.get(0);
        }

        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(getClass().getName() + "_BaselineJob"));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        scheduledService.scheduleWithFixedDelay(
                this::handle,
                0,
                environmentContext.getBaseJobInterval(),
                TimeUnit.SECONDS);
    }

    public interface ConstBaselineBlockingReason {

        String JOB_NO_FIND = "周期实例未生成:jobId:%s";

        String UNABLE_TO_CALCULATE = "无法计算时间:jobId:%s";

        String TIMING_NOT_COMPLETED = "完成时间超过基线任务预计完成时间 基线任务:%s 实例任务:%s";

        String JOB_STATUS_OTHER = "实例任务:%s 实例状态:%s 导致基线任务状态变更成其他";

        String EARLY_WARNING = "实例任务:%s,到达预警时间未执行";
        String BREAKING_THE_LINE = "实例任务:%s,到达破线时间未执行";

    }
}
