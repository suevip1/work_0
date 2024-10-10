package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.enums.OperateTypeEnum;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;

import com.dtstack.engine.master.utils.JobKeyUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author yuebai
 * @date 2021-02-01
 * 查询出当前任务的所有下游任务（同一调度日期内）
 */
public class ForkJoinJobTask extends RecursiveTask<Map<String,String>> {

    private final static Logger logger = LoggerFactory.getLogger(ForkJoinJobTask.class);
    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());
    private String jobId;
    private ConcurrentHashMap<String,String> results;
    private ScheduleJobDao scheduleJobDao;
    private ScheduleJobJobDao scheduleJobJobDao;
    private ScheduleJobOperateService scheduleJobOperateService;
    private boolean isOnlyNextChild;

    public ForkJoinJobTask(String jobId, ConcurrentHashMap<String,String> results,
                           ScheduleJobDao scheduleJobDao,
                           ScheduleJobJobDao scheduleJobJobDao,
                           boolean isOnlyNextChild,
                           ScheduleJobOperateService scheduleJobOperateService) {
        this.jobId = jobId;
        this.results = results;
        this.scheduleJobDao = scheduleJobDao;
        this.scheduleJobJobDao = scheduleJobJobDao;
        this.isOnlyNextChild = isOnlyNextChild;
        this.scheduleJobOperateService = scheduleJobOperateService;
    }

    @Override
    protected ConcurrentHashMap<String,String> compute() {
        ScheduleJob job = scheduleJobDao.getRdosJobByJobId(jobId);
        if (null == job) {
            return null;
        }

        String jobKey = job.getJobKey();
        //从jobKey获取父任务的触发时间
        String parentJobDayStr = JobKeyUtils.getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)) {
            return null;
        }

        //查询子工作任务
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(jobKey, RelyTypeEnum.NORMAL.getType());
        if (CollectionUtils.isEmpty(scheduleJobJobs)) {
            return null;
        }

        List<ScheduleJob> subJobsAndStatusByFlowId = null;
        //如果工作流 和 实验任务 把子节点全部添加进来
        if (SPECIAL_TASK_TYPES.contains(job.getTaskType())) {
            subJobsAndStatusByFlowId = scheduleJobDao.getSubJobsAndStatusByFlowId(job.getJobId());
        }

        Set<String> jobKeyList = this.filterJobKeyList(job, scheduleJobJobs, subJobsAndStatusByFlowId);

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return null;
        }
        List<ScheduleJob> listJobs = scheduleJobDao.listJobByJobKeys(jobKeyList);
        List<ForkJoinJobTask> tasks = new ArrayList<>();
        for (ScheduleJob childScheduleJob : listJobs) {
            if (results.containsKey(childScheduleJob.getJobId())) {
                continue;
            }

            Integer status = childScheduleJob.getStatus();

            if (!RdosTaskStatus.canReset(status)) {
                logger.error("job {} status {}  can not restart ", job.getJobId(), job.getStatus());
                scheduleJobOperateService.addScheduleJobOperate(childScheduleJob.getJobId(),
                        OperateTypeEnum.RESTART_CANCELED.getCode(), String.format(OperateTypeEnum.RESTART_CANCELED.getMsg(),
                                childScheduleJob.getJobName()), childScheduleJob.getCreateUserId());
                continue;
            }

            results.put(childScheduleJob.getJobId(),childScheduleJob.getCycTime());
            if (isOnlyNextChild) {
                continue;
            }
            ForkJoinJobTask subTask = new ForkJoinJobTask(childScheduleJob.getJobId(), results, scheduleJobDao, scheduleJobJobDao, isOnlyNextChild,scheduleJobOperateService);
            logger.info("forkJoinJobTask subTask jobId {} result {} isOnlyNextChild {} ", childScheduleJob.getJobId(), results.size(), isOnlyNextChild);
            tasks.add(subTask);
        }

        Collection<ForkJoinJobTask> forkJoinJobTasks = ForkJoinTask.invokeAll(tasks);
        for (ForkJoinJobTask forkJoinJobTask : forkJoinJobTasks) {
            Map<String,String> scheduleJobs = forkJoinJobTask.join();
            if (MapUtils.isNotEmpty(scheduleJobs)) {
                results.putAll(scheduleJobs);
            }
        }
        return results;
    }


    private Set<String> filterJobKeyList(ScheduleJob scheduleJob, List<ScheduleJobJob> scheduleJobJobList, List<ScheduleJob> subJobsAndStatusByFlowId) {
        Long jobTaskShadeId = JobKeyUtils.getTaskShadeIdFromJobKey(scheduleJob.getJobKey());
        Set<String> jobKeyList = new HashSet<>();
        if (null == jobTaskShadeId) {
            return jobKeyList;
        }

        if (CollectionUtils.isNotEmpty(subJobsAndStatusByFlowId)) {
            for (ScheduleJob job : subJobsAndStatusByFlowId) {
                if (!RdosTaskStatus.canReset(job.getStatus())) {
                    logger.error("job {} status {}  can not restart ", job.getJobId(), job.getStatus());
                    scheduleJobOperateService.addScheduleJobOperate(scheduleJob.getJobId(),
                            OperateTypeEnum.RESTART_CANCELED.getCode(),String.format(OperateTypeEnum.RESTART_CANCELED.getMsg(),
                                    job.getJobName()), scheduleJob.getCreateUserId());
                    return new HashSet<>();
                }
                jobKeyList.add(job.getJobKey());
            }
        }

        for (ScheduleJobJob scheduleJobJob : scheduleJobJobList) {
            String childJobKey = scheduleJobJob.getJobKey();
            Long childJobShadeId = JobKeyUtils.getTaskShadeIdFromJobKey(childJobKey);
            //排除自依赖
            if (null != childJobShadeId && childJobShadeId.equals(jobTaskShadeId)) {
                continue;
            }
            //添加除工作流内部子任务之外的下游任务依赖
            if (CollectionUtils.isNotEmpty(subJobsAndStatusByFlowId) && subJobsAndStatusByFlowId.stream().anyMatch(s -> s.getJobKey().equalsIgnoreCase(childJobKey))) {
                continue;
            }
            jobKeyList.add(scheduleJobJob.getJobKey());
        }
        return jobKeyList;
    }
}
