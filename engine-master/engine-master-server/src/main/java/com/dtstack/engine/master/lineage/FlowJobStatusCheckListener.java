package com.dtstack.engine.master.lineage;


import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.event.ScheduleJobBatchEvent;
import com.dtstack.engine.master.event.ScheduleJobEventListener;
import com.dtstack.engine.master.event.ScheduleJobEventPublisher;
import com.dtstack.engine.master.executor.AbstractJobExecutor;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2023/3/2 4:05 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class FlowJobStatusCheckListener implements ScheduleJobEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobExecutor.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    protected EnvironmentContext environmentContext;


    @Override
    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        if(!environmentContext.getOpenFastCheckStatus()){
            return;
        }
        List<String> jobIds = event.getJobIds();

        if (RdosTaskStatus.FINISHED.getStatus().equals(event.getStatus()) ) {
            LOGGER.info("FlowJobStatusCheckListener jobs: {} status finished", jobIds);
            List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(jobIds);

            for (ScheduleJob scheduleJob : scheduleJobs) {
                if (EScheduleJobType.WORK_FLOW.getType().equals(scheduleJob.getTaskType())) {
                    // 工作流任务直接continue
                    continue;
                }

                String flowJobId = scheduleJob.getFlowJobId();
                if (StringUtils.isBlank(flowJobId)) {
                    // 只处理工作流子任务
                    continue;
                }

                ScheduleJob flowJob = scheduleJobService.getByJobId(flowJobId, IsDeletedEnum.NOT_DELETE.getType());
                if (TaskRuleEnum.STRONG_RULE.getCode().equals(flowJob.getTaskRule())) {
                    // 不处理强规则任务这种
                    continue;
                }

                List<ScheduleJob> subJobsFlow = scheduleJobService.getSubJobsAndStatusByFlowId(flowJobId);
                List<ScheduleJob> jobs = subJobsFlow.stream()
                        .filter(job -> !job.getStatus().equals(RdosTaskStatus.FINISHED.getStatus()))
                        .collect(Collectors.toList());

                if (CollectionUtils.isEmpty(jobs)) {
                    // 说明任务已经全部成功了，直接更新成成功的状态
                    if (!scheduleJobService.hasTaskRule(flowJob)) {
                        LOGGER.info("FlowJobStatusCheckListener job: {} start update", scheduleJob.getJobId());
                        scheduleJobService.updateStatusByJobId(scheduleJob.getFlowJobId(), RdosTaskStatus.FINISHED.getStatus(),null);
                    }
                }
            }
        }
    }

    @PostConstruct
    public void registerEvent(){
        ScheduleJobEventPublisher.getInstance().register(this);
    }
}
