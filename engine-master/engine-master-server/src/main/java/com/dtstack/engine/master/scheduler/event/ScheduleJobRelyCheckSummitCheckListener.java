package com.dtstack.engine.master.scheduler.event;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.executor.AbstractJobExecutor;
import com.dtstack.engine.master.impl.ScheduleJobRelyCheckService;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.interceptor.JobStatusSummitCheckContext;
import com.dtstack.engine.po.ScheduleJobRelyCheck;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 12:00 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class ScheduleJobRelyCheckSummitCheckListener implements SummitCheckListener, InitializingBean, DisposableBean, Runnable, ApplicationListener<ApplicationStartedEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobRelyCheckSummitCheckListener.class);

    @Autowired
    private SummitCheckEvent summitCheckEvent;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobRelyCheckService scheduleJobRelyCheckService;

    private ExecutorService executorService;

    private LinkedBlockingQueue<CheckRelyContext> scheduleJobQueue;

    protected final AtomicBoolean RUNNING = new AtomicBoolean(true);

    @Override
    public void event(JobStatusSummitCheckContext checkContext,
                      SummitCheckEventType summitCheckEventType,
                      JobCheckRunInfo jobCheckRunInfo) {
        try {
            if (!environmentContext.openCheckRelyCheck()) {
                return;
            }

            CheckRelyContext checkRelyContext = new CheckRelyContext(checkContext, summitCheckEventType, jobCheckRunInfo);


            if (scheduleJobQueue.contains(checkRelyContext) || scheduleJobQueue.size() >= environmentContext.getQueueSize()) {
                LOGGER.warn("queue full not rely check:{}",checkContext.getScheduleBatchJob().getJobId());
                return;
            }

            try {
                scheduleJobQueue.put(checkRelyContext);
            } catch (InterruptedException e) {
                LOGGER.warn("queue full not rely check:{}",checkContext.getScheduleBatchJob().getJobId());
            }
        } catch (Exception e) {
            LOGGER.error("",e);
        }

    }

    private void buildRelyCheckSummit(JobStatusSummitCheckContext checkContext, SummitCheckEventType summitCheckEventType, JobCheckRunInfo jobCheckRunInfo) {
        ScheduleBatchJob scheduleBatchJob = checkContext.getScheduleBatchJob();
        if (SummitCheckEventType.job_job_status_summit_check_interceptor_end_evnet.equals(summitCheckEventType)) {
            List<JobCheckRunInfo> blockStatusJobCheckRunInfoList = checkContext.getBlockStatusJobCheckRunInfoList();

            List<CheckInfo> addParentJob = Lists.newArrayList();
            List<CheckInfo> judgeParentJob = Lists.newArrayList();

            List<ScheduleJobRelyCheck> scheduleJobRelyChecks = scheduleJobRelyCheckService.findByJobId(scheduleBatchJob.getJobId());
            List<String> jobIds = scheduleJobRelyChecks.stream().map(ScheduleJobRelyCheck::getParentJobId).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(blockStatusJobCheckRunInfoList)) {
                return;
            }

            for (JobCheckRunInfo checkRunInfo : blockStatusJobCheckRunInfoList) {
                JobCheckStatus status = checkRunInfo.getStatus();

                if (JobCheckStatus.FATHER_NO_CREATED.equals(jobCheckRunInfo.getStatus())) {
                    // 父任务为生成
                    addParentJob.add(new CheckInfo(scheduleBatchJob.getJobId(), status.getStatus()));
                    continue;
                }

                ScheduleJob parentJob = checkRunInfo.getJobErrorContext().getParentJob();

                Integer jobStatus = parentJob.getStatus();
                if (RdosTaskStatus.FAILED_STATUS.contains(jobStatus)) {
                    // 说明上游任务已经直接上游已经异常 需要直接插入的
                    if (!jobIds.contains(parentJob.getJobId())) {
                        addParentJob.add(new CheckInfo(parentJob.getJobId(), status.getStatus()));
                    }
                } else {
                    // 上游不是失败的，直接从 check里面查询是否有记录，没有记录的话，就直接 return
                    judgeParentJob.add(new CheckInfo(parentJob.getJobId(), status.getStatus()));
                }

            }

            List<ScheduleJobRelyCheck> scheduleJobRelyCheckList = Lists.newArrayList();
            for (CheckInfo checkInfo : addParentJob) {
                ScheduleJobRelyCheck scheduleJobRelyCheck = new ScheduleJobRelyCheck();
                scheduleJobRelyCheck.setJobId(scheduleBatchJob.getJobId());
                scheduleJobRelyCheck.setDirectParentJobId(checkInfo.jobId);
                scheduleJobRelyCheck.setParentJobId(checkInfo.jobId);
                scheduleJobRelyCheck.setParentJobCheckStatus(checkInfo.checkStatus);
                scheduleJobRelyCheck.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
                scheduleJobRelyCheckList.add(scheduleJobRelyCheck);
            }

            List<String> judgeJobIds = judgeParentJob.stream().map(CheckInfo::getJobId).collect(Collectors.toList());
            List<ScheduleJobRelyCheck> isExistParentCheck = scheduleJobRelyCheckService.findByJobIds(judgeJobIds);

            Map<String, List<ScheduleJobRelyCheck>> parentChecks = isExistParentCheck.stream().collect(Collectors.groupingBy(ScheduleJobRelyCheck::getJobId));

            for (String judgeJobId : judgeJobIds) {
                List<ScheduleJobRelyCheck> parentScheduleJobRelyChecks = parentChecks.get(judgeJobId);

                if (CollectionUtils.isEmpty(parentScheduleJobRelyChecks)) {
                    // 说明上游没有检查到直接返回
                    continue;
                }

                Map<String, List<ScheduleJobRelyCheck>> parentIds = parentScheduleJobRelyChecks.stream().collect(Collectors.groupingBy(ScheduleJobRelyCheck::getParentJobId));

                for (Map.Entry<String, List<ScheduleJobRelyCheck>> entry : parentIds.entrySet()) {
                    String key = entry.getKey();

                    if (jobIds.contains(key)) {
                        continue;
                    }

                    List<ScheduleJobRelyCheck> jobRelyChecks = entry.getValue();

                    Set<Integer> statues = jobRelyChecks.stream().map(ScheduleJobRelyCheck::getParentJobCheckStatus).collect(Collectors.toSet());

                    for (Integer status : statues) {
                        ScheduleJobRelyCheck check = new ScheduleJobRelyCheck();
                        check.setJobId(scheduleBatchJob.getJobId());
                        check.setDirectParentJobId("0");
                        check.setParentJobId(key);
                        check.setParentJobCheckStatus(status);
                        check.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
                        scheduleJobRelyCheckList.add(check);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(scheduleJobRelyCheckList)) {
                scheduleJobRelyCheckService.insertBatch(scheduleJobRelyCheckList);
            }

        } else if (SummitCheckEventType.summit_check_pass_event.equals(summitCheckEventType)) {
            // 删除check
            scheduleJobRelyCheckService.deleteByJobId(scheduleBatchJob.getJobId());
        }
    }

    @Override
    public void run() {
        try {
            while (RUNNING.get()) {
                CheckRelyContext take = scheduleJobQueue.take();
                buildRelyCheckSummit(take.checkContext, take.summitCheckEventType, take.jobCheckRunInfo);
            }
        } catch (Throwable e) {
            LOGGER.error("",e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!environmentContext.openCheckRelyCheck()) {
            return;
        }

        summitCheckEvent.registerEventListener(this);
        scheduleJobQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (!environmentContext.openCheckRelyCheck()) {
            return;
        }

        executorService = new ThreadPoolExecutor(1, 1, environmentContext.getQueueSize(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(environmentContext.getJobExecutorPoolQueueSize()),
                new CustomThreadFactory(getClass().getName()), new ThreadPoolExecutor.DiscardPolicy());
        executorService.submit(this);
    }

    @Override
    public void destroy() throws Exception {
        RUNNING.compareAndSet(true, false);
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    static class CheckInfo {
        private String jobId;

        private Integer checkStatus;

        public CheckInfo(String jobId, Integer checkStatus) {
            this.jobId = jobId;
            this.checkStatus = checkStatus;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public Integer getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(Integer checkStatus) {
            this.checkStatus = checkStatus;
        }
    }

    static class CheckRelyContext {

        private JobStatusSummitCheckContext checkContext;

        private SummitCheckEventType summitCheckEventType;

        private JobCheckRunInfo jobCheckRunInfo;

        public CheckRelyContext(JobStatusSummitCheckContext checkContext, SummitCheckEventType summitCheckEventType, JobCheckRunInfo jobCheckRunInfo) {
            this.checkContext = checkContext;
            this.summitCheckEventType = summitCheckEventType;
            this.jobCheckRunInfo = jobCheckRunInfo;
        }

        public JobStatusSummitCheckContext getCheckContext() {
            return checkContext;
        }

        public void setCheckContext(JobStatusSummitCheckContext checkContext) {
            this.checkContext = checkContext;
        }

        public SummitCheckEventType getSummitCheckEventType() {
            return summitCheckEventType;
        }

        public void setSummitCheckEventType(SummitCheckEventType summitCheckEventType) {
            this.summitCheckEventType = summitCheckEventType;
        }

        public JobCheckRunInfo getJobCheckRunInfo() {
            return jobCheckRunInfo;
        }

        public void setJobCheckRunInfo(JobCheckRunInfo jobCheckRunInfo) {
            this.jobCheckRunInfo = jobCheckRunInfo;
        }
    }


}
