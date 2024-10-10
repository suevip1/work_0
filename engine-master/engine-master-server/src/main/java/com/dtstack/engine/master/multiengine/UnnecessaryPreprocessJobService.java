package com.dtstack.engine.master.multiengine;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskBlack;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.FillConstant;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleTaskBlackDao;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.impl.TaskParamsService;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnnecessaryPreprocessJobService {

    public static ScheduleJobDao scheduleJobDao;
    public static ScheduleJobExpandDao scheduleJobExpandDao;

    public static TaskParamsService taskParamsService;
    public static EnvironmentContext environmentContext;

    public static ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    public static ScheduleTaskBlackDao scheduleTaskBlackDao;

    public static final String CYCLE_RUN_OPEN = "cycle_run_open";


    public static void init(ScheduleJobDao scheduleJobDao,
                            ScheduleJobExpandDao scheduleJobExpandDao,
                            ScheduleJobGanttTimeService scheduleJobGanttTimeService,
                            TaskParamsService taskParamsService,
                            EnvironmentContext environmentContext,
                            ScheduleTaskBlackDao scheduleTaskBlackDao) {
        UnnecessaryPreprocessJobService.scheduleJobDao = scheduleJobDao;
        UnnecessaryPreprocessJobService.scheduleJobExpandDao = scheduleJobExpandDao;
        UnnecessaryPreprocessJobService.scheduleJobGanttTimeService = scheduleJobGanttTimeService;
        UnnecessaryPreprocessJobService.taskParamsService = taskParamsService;
        UnnecessaryPreprocessJobService.environmentContext = environmentContext;
        UnnecessaryPreprocessJobService.scheduleTaskBlackDao = scheduleTaskBlackDao;
    }


    public static boolean preprocess(ScheduleJob job, ScheduleTaskShade batchTask) {
        if (FillJobTypeEnum.MIDDLE_JOB.getType().equals(job.getFillType())
                || FillJobTypeEnum.BLACK_JOB.getType().equals(job.getFillType())) {
            //补数据中间任务 不执行
            return buildToUpdate.andThen(s -> {
                s.setExecTime(0L);
                s.setExecEndTime(new Timestamp(System.currentTimeMillis()));
                s.setStatus(RdosTaskStatus.FINISHED.getStatus());
                scheduleJobDao.updateExecTimeAndStatusLogInfo(s);
                scheduleJobExpandDao.updateLogInfoByJobId(job.getJobId(), FillConstant.MIDDLE_AND_BLACK_LOG,RdosTaskStatus.FINISHED.getStatus());
                return true;
            }).apply(job);
        }

        if (EScheduleJobType.WORK_FLOW.getVal().equals(job.getTaskType()) || EScheduleJobType.ALGORITHM_LAB.getVal().equals(job.getTaskType())) {
            return buildToUpdate.andThen(s -> {
                s.setStatus(RdosTaskStatus.SUBMITTING.getStatus());
                scheduleJobDao.updateStatusWithExecTime(s);
                scheduleJobExpandDao.updateJobExpandByJobId(job.getJobId(),"",RdosTaskStatus.SUBMITTING.getStatus(),new Date(),new Date());
                scheduleJobGanttTimeService.ganttChartTimeRunImmediately(job.getJobId());
                return true;
            }).apply(job);
        }

        if (EScheduleJobType.VIRTUAL.getType().equals(job.getTaskType())) {
            //虚节点写入开始时间和结束时间
            return buildToUpdate.andThen(s -> {
                s.setExecTime(0L);
                s.setExecEndTime(new Timestamp(System.currentTimeMillis()));
                s.setStatus(RdosTaskStatus.FINISHED.getStatus());
                scheduleJobGanttTimeService.ganttChartTimeRunImmediately(job.getJobId());
                scheduleJobDao.updateStatusWithExecTime(s);
                scheduleJobExpandDao.updateJobExpandByJobId(job.getJobId(),FillConstant.VIRTUAL,RdosTaskStatus.FINISHED.getStatus(),new Date(),new Date());
                return true;
            }).apply(job);
        }


        if (EScheduleJobType.EVENT.getType().equals(job.getTaskType())) {
            //事件任务开始 等待回调
            return buildToUpdate.andThen(s -> {
                s.setStatus(RdosTaskStatus.RUNNING.getStatus());
                scheduleJobGanttTimeService.ganttChartTimeRunImmediately(job.getJobId());
                scheduleJobDao.updateStatusWithExecTime(s);
                scheduleJobExpandDao.updateJobExpandByJobId(job.getJobId(),"",RdosTaskStatus.RUNNING.getStatus(),new Date(),new Date());
                return true;
            }).apply(job);
        }

        if (EScheduleType.NORMAL_SCHEDULE.getType().equals(job.getType()) && environmentContext.getOpenCycleParam()) {
            try {
                Map<String, Object> paramInfo = taskParamsService.convertPropertiesToMap(batchTask.getTaskParams());
                Object open = paramInfo.get(CYCLE_RUN_OPEN);
                if (open != null && Boolean.parseBoolean(open.toString())) {
                    return buildToUpdate.andThen(s->{
                        s.setExecTime(0L);
                        s.setExecEndTime(new Timestamp(System.currentTimeMillis()));
                        s.setStatus(RdosTaskStatus.FINISHED.getStatus());
                        scheduleJobDao.updateStatusWithExecTime(s);
                        scheduleJobExpandDao.updateJobExpandByJobId(job.getJobId(),FillConstant.BLACK_LOG,RdosTaskStatus.FINISHED.getStatus(),new Date(),new Date());
                        return true;
                    }).apply(job);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 是否设置黑名单
        Long dtuicTenantId = batchTask.getDtuicTenantId();
        Long projectId = batchTask.getProjectId();
        Integer appType = batchTask.getAppType();
        List<ScheduleTaskBlack> scheduleTaskBlack = scheduleTaskBlackDao.findByTenantAndProjectAndAppType(dtuicTenantId, projectId, appType);

        if (CollectionUtils.isNotEmpty(scheduleTaskBlack)) {
            // 说明这个项目下被加入黑名单，判断该任务类型是否被加入白名单
            List<Integer> taskTypes = scheduleTaskBlack.stream().map(ScheduleTaskBlack::getTaskType).collect(Collectors.toList());
            Integer taskType = batchTask.getTaskType();
            if (taskTypes.contains(taskType) || taskTypes.contains(-2)) {
                // 说明该任务需要被置成功
                return buildToUpdate.andThen(s->{
                    s.setExecTime(0L);
                    s.setExecEndTime(new Timestamp(System.currentTimeMillis()));
                    s.setStatus(RdosTaskStatus.FINISHED.getStatus());
                    scheduleJobExpandDao.updateJobExpandByJobId(job.getJobId(),FillConstant.BLACK_LOG,RdosTaskStatus.FINISHED.getStatus(),new Date(),new Date());
                    scheduleJobDao.updateStatusWithExecTime(s);
                    return true;
                }).apply(job);
            }
        }

        return false;
    }

    private static final Function<ScheduleJob, ScheduleJob> buildToUpdate = s -> {
        ScheduleJob updateJob = new ScheduleJob();
        updateJob.setJobId(s.getJobId());
        updateJob.setAppType(s.getAppType());
        updateJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        return updateJob;
    };


}
