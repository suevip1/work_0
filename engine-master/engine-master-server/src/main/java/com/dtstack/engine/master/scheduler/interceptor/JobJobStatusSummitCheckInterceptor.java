package com.dtstack.engine.master.scheduler.interceptor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.ScheduleConfUtils;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.TimeService;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobErrorContext;
import com.dtstack.engine.master.scheduler.JobErrorInfo;
import com.dtstack.engine.master.scheduler.event.SummitCheckEventType;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.utils.JobGraphUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.enums.RdosTaskStatus.FINISHED;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 2:44 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobJobStatusSummitCheckInterceptor extends AbstractJobStatusSummitCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobJobStatusSummitCheckInterceptor.class);

    @Autowired
    private TimeService timeService;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleTaskRefShadeService scheduleTaskRefShadeService;

    /**
     * 阻塞任务提交的集合，这些状态会导致下游等待提交
     */
    private final List<JobCheckStatus> blockTaskStatusList = Lists.newArrayList();

    public JobJobStatusSummitCheckInterceptor() {
        blockTaskStatusList.add(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
        blockTaskStatusList.add(JobCheckStatus.CHILD_PRE_NOT_FINISHED);
        blockTaskStatusList.add(JobCheckStatus.CHILD_PRE_NOT_SUCCESS);

        blockTaskStatusList.add(JobCheckStatus.FATHER_JOB_EXCEPTION);
        blockTaskStatusList.add(JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION);
    }

    @Override
    public JobCheckRunInfo checkJobStatus(JobStatusSummitCheckContext checkContext) {
        ScheduleBatchJob scheduleBatchJob = checkContext.getScheduleBatchJob();
        JobCheckRunInfo jobCheckRunInfo = checkContext.getJobCheckRunInfo();
        ScheduleTaskShade scheduleTaskShade = checkContext.getScheduleTaskShade();
        List<ScheduleJobJob> jobJobList = scheduleBatchJob.getBatchJobJobList();

        if (CollectionUtils.isEmpty(jobJobList)) {
            return jobCheckRunInfo;
        }

        // jobjob 保存了上游的两种依赖 1 上游任务 2 自依赖上个周期任务
        // 所以需要校验这两种情况
        List<JobCheckRunInfo> blockStatusJobCheckRunInfoList = Lists.newArrayList();

        boolean hasFatherJobNotFinish = false;
        for (ScheduleJobJob scheduleJobJob : jobJobList) {
            // 这里需要校验每一个jobjob对应的实例状态
            jobCheckRunInfo = checkJobJob(scheduleBatchJob, scheduleJobJob);

            //多个任务依赖的时候 如果有依赖任务还未运行完 需要check其他的依赖任务 以最后为准
            if (JobCheckStatus.FATHER_JOB_NOT_FINISHED.equals(jobCheckRunInfo.getStatus())) {
                hasFatherJobNotFinish = true;
                blockStatusJobCheckRunInfoList.add(jobCheckRunInfo);
                continue;
            }
            if (!JobCheckStatus.CAN_EXE.equals(jobCheckRunInfo.getStatus())) {
                jobCheckRunInfo.getJobErrorContext().jobCheckStatus(jobCheckRunInfo.getStatus());
                blockStatusJobCheckRunInfoList.add(jobCheckRunInfo);
                checkContext.setBlockStatusJobCheckRunInfoList(blockStatusJobCheckRunInfoList);
                return jobCheckRunInfo;
            }
        }


        if (hasFatherJobNotFinish) {
            jobCheckRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
            jobCheckRunInfo.getJobErrorContext().jobCheckStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
            checkContext.setBlockStatusJobCheckRunInfoList(blockStatusJobCheckRunInfoList);
            return jobCheckRunInfo;
        }

        Integer dependencyType = scheduleBatchJob.getScheduleJob().getDependencyType();
        boolean dependencyChildPrePeriod = DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(dependencyType)
                || DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(dependencyType);

        // 校验任务依赖下游的上一周期条件
        if (JobCheckStatus.CAN_EXE.equals(jobCheckRunInfo.getStatus()) && dependencyChildPrePeriod) {
            //检测下游任务的上一个周期是否结束
            jobCheckRunInfo = checkChildTaskShadeStatus(scheduleBatchJob, scheduleTaskShade, dependencyType);
        }

        // 校验 python 和 shell 任务 引用链上的任务是否都成功
        if (JobCheckStatus.CAN_EXE.equals(jobCheckRunInfo.getStatus()) && scheduleTaskRefShadeService.isSupportRefJob(scheduleTaskShade.getTaskType())) {
            return checkRefTask(scheduleBatchJob);
        }

        if (hasBlocked(jobCheckRunInfo)) {
            blockStatusJobCheckRunInfoList.add(jobCheckRunInfo);
        }

        checkContext.setBlockStatusJobCheckRunInfoList(blockStatusJobCheckRunInfoList);
        return jobCheckRunInfo;
    }

    private boolean hasBlocked(JobCheckRunInfo jobCheckRunInfo) {
        return blockTaskStatusList.contains(jobCheckRunInfo.getStatus());
    }

    private JobCheckRunInfo checkRefTask(ScheduleBatchJob scheduleBatchJob) {
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();
        checkRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        checkRunInfo.setJobErrorContext(JobErrorContext.newInstance().jobCheckStatus(checkRunInfo.getStatus()));
        ScheduleTaskRefShadeService.CheckJobRefResult result = scheduleTaskRefShadeService.travelJobRef(scheduleBatchJob.getScheduleJob(),true);
        if (result.getJobRefBreak()) {
            checkRunInfo.setStatus(JobCheckStatus.REF_TASK_NOT_SUCCESS);
            checkRunInfo.setExtInfo(GlobalConst.SEMICOLON + result.getBreakReason());
        }

        return checkRunInfo;
    }

    private JobCheckRunInfo checkJobJob(ScheduleBatchJob scheduleBatchJob, ScheduleJobJob scheduleJobJob) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        jobCheckRunInfo.setJobErrorContext(JobErrorContext.newInstance().jobCheckStatus(jobCheckRunInfo.getStatus()));

        ScheduleJob dependencyJob = scheduleJobService.getJobByJobKeyAndType(scheduleJobJob.getParentJobKey(), scheduleBatchJob.getScheduleType());
        // 依赖的上游不存在，这里要分两种情况 1 手动任务和补数据任务 2 周期任务
        if (checkDependencyJob(scheduleBatchJob, jobCheckRunInfo, scheduleJobJob, dependencyJob)) {
            return jobCheckRunInfo;
        }

        // 父任务存在,获得父任务的状态进行判断
        Integer dependencyJobStatus = scheduleJobService.getJobStatus(dependencyJob.getJobId());
        String FLOW_JOB_ID = "0";
        if (!FLOW_JOB_ID.equals(scheduleBatchJob.getScheduleJob().getFlowJobId())
                && dependencyJob.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal())
                && RdosTaskStatus.RUNNING.getStatus().equals(dependencyJobStatus)) {
            return jobCheckRunInfo;
        }

        ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(dependencyJob.getTaskId(), dependencyJob.getAppType());

        JobErrorContext jobErrorContext = jobCheckRunInfo.getJobErrorContext();
        jobErrorContext.parentTaskShade(scheduleTaskShade).parentJob(dependencyJob);
        // 上游任务如果下线，下游任务取消
        if (scheduleTaskShade == null) {
            jobCheckRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_CANCELED);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            jobCheckRunInfo.setExtInfo("(父任务名称为:" + getTaskNameFromJobName(dependencyJob.getJobName(), dependencyJob.getType()) + ")");
            LOGGER.error("job:{} dependency_job_canceled job:{} ", scheduleJobJob.getParentJobKey(), scheduleJobJob.getJobKey());
            return jobCheckRunInfo;
        }

        //自依赖还需要判断二种情况
        //如果是依赖父任务成功 要判断父任务状态 走自依赖上一个周期异常
        //如果是依赖父任务结束 只要是满足结束条件的 这一周期可以执行
        Boolean isSelfDependency = scheduleBatchJob.getTaskId().equals(scheduleTaskShade.getTaskId())
                && scheduleBatchJob.getAppType().equals(scheduleTaskShade.getAppType());
        Integer dependencyType = scheduleBatchJob.getScheduleJob().getDependencyType();
        if (checkDependEndStatus(dependencyType, isSelfDependency)) {
            if (!isEndStatus(dependencyJobStatus)) {
                jobCheckRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
                jobCheckRunInfo.getJobErrorContext().jobCheckStatus(jobCheckRunInfo.getStatus())
                        .parentTaskShade(scheduleTaskShade)
                        .parentJob(dependencyJob);
            }
            return jobCheckRunInfo;
        }

        if (RdosTaskStatus.FAILED.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.SUBMITFAILD.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.PARENTFAILED.getStatus().equals(dependencyJobStatus)) {
            jobCheckRunInfo.setStatus(JobCheckStatus.FATHER_JOB_EXCEPTION);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            if (isSelfDependency) {
                jobCheckRunInfo.setStatus(JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION);
                jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
                jobCheckRunInfo.setExtInfo("(父任务名称为:" + scheduleJobJob.getParentJobKey() + ")");
                LOGGER.error("job:{} self-dependent exception job:{} self_pre_period_exception", scheduleJobJob.getJobKey(), scheduleJobJob.getParentJobKey());
            } else {//记录失败的父任务的名称
                JobErrorInfo errorInfo = createErrJobCacheInfo(dependencyJob, scheduleTaskShade);
                jobCheckRunInfo.setExtInfo("(父任务名称为:" + errorInfo.getTaskName() + ")");
                LOGGER.error("job:{} self-dependent exception taskName:{} error cache father_job_exception", dependencyJob.getJobKey(), errorInfo.getTaskName());
            }
        } else if (RdosTaskStatus.FROZEN.getStatus().equals(dependencyJobStatus)) {
            if (!isSelfDependency) {
                jobCheckRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_FROZEN);
                jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            }
            //自依赖的上游任务冻结不会影响当前任务的执行
        } else if (RdosTaskStatus.CANCELED.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.KILLED.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.AUTOCANCELED.getStatus().equals(dependencyJobStatus)) {
            jobCheckRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_CANCELED);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            jobCheckRunInfo.setExtInfo("(父任务名称为:" + getTaskNameFromJobName(dependencyJob.getJobName(), dependencyJob.getType()) + ")");
            LOGGER.error("job:{} dependency_job_canceled job:{} ", scheduleJobJob.getParentJobKey(), scheduleJobJob.getJobKey());
        } else if (RdosTaskStatus.EXPIRE.getStatus().equals(dependencyJobStatus)) {
            jobCheckRunInfo.setExtInfo("(父任务名称为:" + getTaskNameFromJobName(dependencyJob.getJobName(), dependencyJob.getType()) + ")");
            jobCheckRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_EXPIRE);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
        } else if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(dependencyJobStatus)) {
            // 父节点已经执行完了，判断当前任务是否是规则任务
            if (TaskRuleEnum.NO_RULE.getCode().equals(scheduleBatchJob.getScheduleJob().getTaskRule())) {
                jobCheckRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
                jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            }
        } else if (!FINISHED.getStatus().equals(dependencyJobStatus) &&
                !RdosTaskStatus.MANUALSUCCESS.getStatus().equals(dependencyJobStatus)) {
            //系统设置完成或者手动设置为完成
            jobCheckRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
        }

        return jobCheckRunInfo;
    }

    private boolean checkDependencyJob(ScheduleBatchJob scheduleBatchJob, JobCheckRunInfo jobCheckRunInfo, ScheduleJobJob scheduleJobJob, ScheduleJob dependencyJob) {
        if (dependencyJob == null) {
            if (!EScheduleType.manualOperatorType.contains(scheduleBatchJob.getScheduleType())) {
                // 记录当前异常信息上下文，方便排障
                String parentJobKey = scheduleJobJob.getParentJobKey();
                String parentTaskName = scheduleTaskShadeService.getTaskNameByJobKey(parentJobKey, scheduleBatchJob.getAppType());
                jobCheckRunInfo.setStatus(JobCheckStatus.FATHER_NO_CREATED);
                jobCheckRunInfo.getJobErrorContext().jobCheckStatus(jobCheckRunInfo.getStatus()).parentTaskName(parentTaskName);
                jobCheckRunInfo.setExtInfo("(父任务名称为:" + parentTaskName + ")");
            }
            return true;
        }
        return false;
    }

    /**
     * 只要是结束状态 都可以运行
     */
    private boolean checkDependEndStatus(Integer dependencyType, Boolean isSelfDependency) {
        if (isSelfDependency && (DependencyType.SELF_DEPENDENCY_END.getType().equals(dependencyType))) {
            return true;
        }
        return isSelfDependency && DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(dependencyType);
    }

    private boolean isEndStatus(Integer jobStatus) {
        for (Integer status : RdosTaskStatus.getStoppedStatus()) {
            if (jobStatus.equals(status)) {
                return true;
            }
        }

        return false;
    }

    public JobErrorInfo createErrJobCacheInfo(ScheduleJob scheduleJob,ScheduleTaskShade batchTaskShade) {
        JobErrorInfo errorJobCacheInfo = new JobErrorInfo();
        errorJobCacheInfo.setJobKey(scheduleJob.getJobKey());

        if (batchTaskShade == null) {
            errorJobCacheInfo.setTaskName("找不到对应的任务(id:" + scheduleJob.getTaskId() + ")");
        } else {
            errorJobCacheInfo.setTaskName(batchTaskShade.getName());
        }

        return errorJobCacheInfo;
    }

    public String getTaskNameFromJobName(String jobName, Integer scheduleType) {

        if (scheduleType == 1) {
            String[] arr = jobName.split("-");
            if (arr.length != 3) {
                return jobName;
            }

            return arr[1];
        } else {
            if (!jobName.contains("_")) {
                return jobName;
            }
            return jobName.substring(jobName.indexOf("_") + 1, jobName.lastIndexOf("_"));
        }

    }

    /**
     * 如果任务配置了依赖下游任务的上一周期
     * 去根据依赖关系 查询 是否满足执行条件
     *
     * @param scheduleBatchJob 当前实例 job
     * @param batchTaskShade   实例对应的 task
     * @param dependencyType   依赖类型
     * @return 实例运行检查结果
     */
    private JobCheckRunInfo checkChildTaskShadeStatus(ScheduleBatchJob scheduleBatchJob, ScheduleTaskShade batchTaskShade, Integer dependencyType) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        jobCheckRunInfo.setExtInfo("");
        jobCheckRunInfo.setJobErrorContext(JobErrorContext.newInstance().jobCheckStatus(jobCheckRunInfo.getStatus()));
        JobErrorContext jobErrorContext = jobCheckRunInfo.getJobErrorContext();

        List<ScheduleJob> childPrePeriodList = scheduleBatchJob.getDependencyChildPrePeriodList();
        String jobKey = scheduleBatchJob.getScheduleJob().getJobKey();
        if (childPrePeriodList == null) {//获取子任务的上一个周期
            List<ScheduleJobJob> childJobJobList = scheduleJobJobDao.listByParentJobKey(jobKey,RelyTypeEnum.NORMAL.getType());
            childPrePeriodList = getFirstChildPrePeriodBatchJobJob(childJobJobList);
            scheduleBatchJob.setDependencyChildPrePeriodList(childPrePeriodList);
        }
        String cycTime = JobGraphUtils.parseCycTimeFromJobKey(jobKey);
        //如果没有下游任务 需要往前找到有下游任务周期
        if (CollectionUtils.isEmpty(childPrePeriodList)) {
            ScheduleCron scheduleCron = null;
            try {
                String scheduleConf = batchTaskShade.getScheduleConf();
                if (ETaskGroupEnum.MANUAL.getType().equals(batchTaskShade.getTaskGroup())) {
                    scheduleConf = ScheduleConfUtils.buildManualTaskScheduleConf(batchTaskShade.getScheduleConf());
                }
                scheduleCron = ScheduleFactory.parseFromJson(scheduleConf,timeService,batchTaskShade.getTaskId(),
                        batchTaskShade.getAppType());
            } catch (IOException e) {
                LOGGER.error("get {} parent pre pre error", scheduleBatchJob.getTaskId(), e);
            }

            List<ScheduleJob> parentPrePreJob = this.getParentPrePreJob(jobKey, scheduleCron, cycTime);
            if (CollectionUtils.isNotEmpty(parentPrePreJob)) {
                if (null == scheduleBatchJob.getDependencyChildPrePeriodList()) {
                    scheduleBatchJob.setDependencyChildPrePeriodList(new ArrayList<>());
                }
                scheduleBatchJob.getDependencyChildPrePeriodList().addAll(parentPrePreJob);
            }
        }
        for (ScheduleJob childJobPreJob : childPrePeriodList) {
            //子实例和 当前任务一致 直接运行
            if (jobKey.equals(childJobPreJob.getJobKey())) {
                continue;
            }
            Integer childJobStatus = scheduleJobService.getJobStatus(childJobPreJob.getJobId());
            boolean check;
            if (DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(dependencyType)) {
                check = isSuccessStatus(childJobStatus);
                if (!check) {
                    //下游任务的上一周期已经结束(未成功状态 如手动取消 过期 kill等) 但是 配置条件的是依赖任务成功 需要跳出check 否则任务会一直是等待运行
                    if (isEndStatus(childJobStatus)) {
                        jobCheckRunInfo.setStatus(JobCheckStatus.CHILD_PRE_NOT_SUCCESS);
                        ScheduleTaskShade childPreTask = scheduleTaskShadeService.getBatchTaskById(childJobPreJob.getTaskId(),childJobPreJob.getAppType());
                        jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus()).parentTaskShade(childPreTask).parentJob(childJobPreJob);
                        jobCheckRunInfo.setExtInfo(String.format("(依赖下游任务的上一周期(%s)",null != childPreTask ? childPreTask.getName() : ""));
                        LOGGER.info("get JobKey {} child job {} prePeriod status is {}  but not success", jobKey, childJobPreJob.getJobId(), childJobStatus);
                        return jobCheckRunInfo;
                    }
                }
            } else {
                check = isEndStatus(childJobStatus);
            }

            if (!check) {
                jobCheckRunInfo.setStatus(JobCheckStatus.CHILD_PRE_NOT_FINISHED);
                ScheduleTaskShade childPreTask = scheduleTaskShadeService.getBatchTaskById(childJobPreJob.getTaskId(),childJobPreJob.getAppType());
                jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus()).parentTaskShade(childPreTask).parentJob(childJobPreJob);
                return jobCheckRunInfo;
            }
        }
        return jobCheckRunInfo;
    }

    private boolean isSuccessStatus(Integer jobStatus) {
        for (Integer status : RdosTaskStatus.getFinishStatus()) {
            if (jobStatus.equals(status)) {
                return true;
            }
        }

        return false;
    }

    private List<ScheduleJob> getFirstChildPrePeriodBatchJobJob(List<ScheduleJobJob> jobJobList) {

        if (CollectionUtils.isEmpty(jobJobList)) {
            return Lists.newArrayList();
        }

        Map<Long, ScheduleJobJob> taskRefFirstJobMap = Maps.newHashMap();
        for (ScheduleJobJob scheduleJobJob : jobJobList) {
            String jobKey = scheduleJobJob.getJobKey();
            Long taskId = getJobTaskIdFromJobKey(jobKey);
            if (taskId == null) {
                continue;
            }

            ScheduleJobJob preJobJob = taskRefFirstJobMap.get(taskId);
            if (preJobJob == null) {
                taskRefFirstJobMap.put(taskId, scheduleJobJob);
            } else {
                String preJobTimeStr = getJobTriggerTimeFromJobKey(preJobJob.getJobKey());
                String currJobTimeStr = getJobTriggerTimeFromJobKey(scheduleJobJob.getJobKey());

                Long preJobTime = MathUtil.getLongVal(preJobTimeStr);
                Long currJobTime = MathUtil.getLongVal(currJobTimeStr);

                if (currJobTime != null &&
                        preJobTime != null &&
                        currJobTime < preJobTime) {
                    taskRefFirstJobMap.put(taskId, scheduleJobJob);
                }
            }
        }

        List<ScheduleJob> resultList = Lists.newArrayList();
        //计算上一个周期的key,并判断是否存在-->存在则添加依赖关系
        for (Map.Entry<Long, ScheduleJobJob> entry : taskRefFirstJobMap.entrySet()) {
            ScheduleJobJob scheduleJobJob = entry.getValue();
            Long taskId = entry.getKey();
            ScheduleTaskShade batchTaskShade = scheduleTaskShadeService.getBatchTaskById(taskId, scheduleJobJob.getAppType());
            if (batchTaskShade == null) {
                LOGGER.error("can't find task by id:{}.", taskId);
                continue;
            }

            String jobKey = scheduleJobJob.getJobKey();
            String cycTime = JobGraphUtils.parseCycTimeFromJobKey(jobKey);
            String scheduleConf = batchTaskShade.getScheduleConf();
            try {
                if (ETaskGroupEnum.MANUAL.getType().equals(batchTaskShade.getTaskGroup())) {
                    scheduleConf = ScheduleConfUtils.buildManualTaskScheduleConf(scheduleConf);
                }
                ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleConf,timeService,batchTaskShade.getTaskId(),batchTaskShade.getAppType());
                String prePeriodJobTriggerDateStr = JobGraphUtils.getPrePeriodJobTriggerDateStr(cycTime, scheduleCron);
                String prePeriodJobKey = jobKey.substring(0, jobKey.lastIndexOf("_") + 1) + prePeriodJobTriggerDateStr;
                EScheduleType scheduleType = JobGraphUtils.parseScheduleTypeFromJobKey(jobKey);
                ScheduleJob dbScheduleJob = scheduleJobService.getJobByJobKeyAndType(prePeriodJobKey, scheduleType.getType());
                if (dbScheduleJob != null) {
                    resultList.add(dbScheduleJob);
                }

            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }

        return resultList;

    }

    private Long getJobTaskIdFromJobKey(String jobKey) {
        String[] fields = jobKey.split("_");
        if (fields.length < 3) {
            return null;
        }
        Long taskShadeId = MathUtil.getLongVal(fields[fields.length - 2]);
        ScheduleTaskShade batchTaskShade = scheduleTaskShadeService.getById(taskShadeId);
        return null == batchTaskShade ? null : batchTaskShade.getTaskId();
    }

    private String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] fields = jobKey.split("_");
        if (fields.length < 3) {
            return null;
        }

        return fields[fields.length - 1];
    }

    /**
     * 如果任务没有下游任务 需要找到往前 一直找到有下游任务的实例
     */
    private List<ScheduleJob> getParentPrePreJob(String jobKey, ScheduleCron scheduleCron, String cycTime) {
        if (StringUtils.isNotBlank(jobKey) && null != scheduleCron && StringUtils.isNotBlank(cycTime)) {
            String prePeriodJobTriggerDateStr = JobGraphUtils.getPrePeriodJobTriggerDateStr(cycTime, scheduleCron);
            String prePeriodJobKey = jobKey.substring(0, jobKey.lastIndexOf("_") + 1) + prePeriodJobTriggerDateStr;
            EScheduleType scheduleType = JobGraphUtils.parseScheduleTypeFromJobKey(jobKey);
            ScheduleJob dbBatchJob = scheduleJobService.getJobByJobKeyAndType(prePeriodJobKey, scheduleType.getType());
            //上一个周期任务为空 直接返回
            if (null == dbBatchJob) {
                return null;
            }
            List<ScheduleJobJob> batchJobJobs = scheduleJobJobDao.listByParentJobKey(dbBatchJob.getJobKey(),RelyTypeEnum.NORMAL.getType());
            if (!CollectionUtils.isEmpty(batchJobJobs)) {
                //上一轮周期任务的下游任务不为空 判断下游任务的状态
                return scheduleJobService.listJobByJobKeys(batchJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList()));
            }
            cycTime = JobGraphUtils.parseCycTimeFromJobKey(prePeriodJobKey);
            //如果上一轮周期也没下游任务 继续找
            return this.getParentPrePreJob(prePeriodJobKey, scheduleCron, cycTime);
        }
        return null;

    }

    @Override
    protected SummitCheckEventType getEventType() {
        return SummitCheckEventType.job_job_status_summit_check_interceptor_end_evnet;
    }
}
