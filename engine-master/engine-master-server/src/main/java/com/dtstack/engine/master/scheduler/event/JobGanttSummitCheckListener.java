package com.dtstack.engine.master.scheduler.event;

import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.interceptor.JobStatusSummitCheckContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 11:51 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobGanttSummitCheckListener implements SummitCheckListener, InitializingBean {

    @Autowired
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    @Autowired
    private SummitCheckEvent summitCheckEvent;

    @Override
    public void event(JobStatusSummitCheckContext checkContext, SummitCheckEventType summitCheckEventType, JobCheckRunInfo jobCheckRunInfo) {
        ScheduleBatchJob scheduleBatchJob = checkContext.getScheduleBatchJob();
        if (SummitCheckEventType.job_time_job_status_summit_check_interceptor_end_event.equals(summitCheckEventType)) {
            scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.STATUS_TIME);
        }

        if (JobCheckStatus.RESOURCE_OVER_LIMIT.equals(jobCheckRunInfo.getStatus())) {
            scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.TENANT_RESOURCE_TIME);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        summitCheckEvent.registerEventListener(this);
    }
}
