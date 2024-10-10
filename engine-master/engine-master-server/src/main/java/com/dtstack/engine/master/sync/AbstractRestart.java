package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午5:40
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractRestart {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRestart.class);

    protected final ScheduleJobDao scheduleJobDao;

    protected final ScheduleJobJobDao scheduleJobJobDao;

    protected final EnvironmentContext environmentContext;

    protected final ScheduleJobService scheduleJobService;


    public AbstractRestart(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        this.scheduleJobDao = scheduleJobDao;
        this.environmentContext = environmentContext;
        this.scheduleJobJobDao = scheduleJobJobDao;
        this.scheduleJobService = scheduleJobService;
    }

    /**
     * 获得工作流任务
     *
     * @param batchJob
     * @param resumeBatchJobs
     */
    protected void setSubFlowJob(ScheduleJob batchJob, Map<String,String> resumeBatchJobs) {
        List<String> subFlowJob = getSubFlowJob(batchJob);
        if (CollectionUtils.isNotEmpty(subFlowJob)) {
            List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(subFlowJob);
            if(CollectionUtils.isNotEmpty(jobs)){
                resumeBatchJobs.putAll(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));
            }
        }
    }

    /**
     * 查询出当前任务的子节点
     *
     * @param batchJob
     * @return
     */
    protected List<String> getSubFlowJob(ScheduleJob batchJob) {
        List<String> subJobIds = new ArrayList<>();
        if (EScheduleJobType.WORK_FLOW.getType().equals(batchJob.getTaskType()) || EScheduleJobType.ALGORITHM_LAB.getType().equals(batchJob.getTaskType())) {
            //如果任务为工作流类型 需要补充自己的子节点
            List<ScheduleJob> subJobsByFlowIds = scheduleJobDao.getSubJobsByFlowIds(Collections.singletonList(batchJob.getJobId()));
            if (CollectionUtils.isNotEmpty(subJobsByFlowIds)) {
                subJobIds.addAll(subJobsByFlowIds.stream()
                        .map(ScheduleJob::getJobId)
                        .collect(Collectors.toSet()));
            }
        }
        return subJobIds;
    }

    /**
     * 获得规则任务
     *
     * @param batchJob
     * @return
     */
    protected List<String> getRuleTask(ScheduleJob batchJob) {
        List<String> ruleJobs = Lists.newArrayList();
        List<ScheduleJob> taskRuleSonJob = scheduleJobService.getTaskRuleSonJob(batchJob);

        for (ScheduleJob scheduleJob : taskRuleSonJob) {
            List<String> subFlowJob = getSubFlowJob(scheduleJob);
            ruleJobs.add(scheduleJob.getJobId());
            ruleJobs.addAll(subFlowJob);
        }

        return ruleJobs;
    }

    /**
     * 置成功
     *
     * @param job
     * @param jobMap
     */
    protected void setSuccess(ScheduleJob job, Map<String, String> jobMap,String log) {
        List<String> jobIds = getSubFlowJob(job);
        // 设置强规则任务
        List<String> ruleJobs = getRuleTask(job);
        jobIds.add(job.getJobId());
        jobIds.addAll(ruleJobs);

        scheduleJobService.updateStatusAndLogInfoByIds(jobIds, RdosTaskStatus.MANUALSUCCESS.getStatus(), log);
        scheduleJobService.deleteCurrentCheck(jobIds);
        LOGGER.info("ids  {} manual success", jobIds);

        // 置成功并恢复调度,要把当前置成功任务去除掉
        if (MapUtils.isNotEmpty(jobMap)) {
            jobIds.forEach(jobMap::remove);
        }
    }
}
