package com.dtstack.engine.master.scheduler.interceptor;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EProjectScheduleStatus;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobErrorContext;
import com.dtstack.engine.master.scheduler.event.SummitCheckEventType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 11:00 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class TaskInfoJobStatusSummitCheckInterceptor extends AbstractJobStatusSummitCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskInfoJobStatusSummitCheckInterceptor.class);

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Override
    public JobCheckRunInfo checkJobStatus(JobStatusSummitCheckContext checkContext) {
        ScheduleTaskShade batchTaskShade = checkContext.getScheduleTaskShade();
        JobCheckRunInfo jobCheckRunInfo = checkContext.getJobCheckRunInfo();
        JobErrorContext jobErrorContext = jobCheckRunInfo.getJobErrorContext();
        ScheduleBatchJob scheduleBatchJob = checkContext.getScheduleBatchJob();
        Integer status = checkContext.getStatus();

        if (batchTaskShade == null || batchTaskShade.getIsDeleted().equals(Deleted.DELETED.getStatus())) {
            jobCheckRunInfo.setStatus(JobCheckStatus.TASK_DELETE);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            return jobCheckRunInfo;
        }

        if(ComputeType.BATCH.getType().equals(scheduleBatchJob.getScheduleJob().getComputeType())){
            List<String> errorMessage = checkTaskResourceLimit(scheduleBatchJob, batchTaskShade);
            if(CollectionUtils.isNotEmpty(errorMessage)) {
                jobCheckRunInfo.setStatus(JobCheckStatus.RESOURCE_OVER_LIMIT);
                jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
                jobErrorContext.taskResourceErrMsg(errorMessage.toString());
                jobCheckRunInfo.setExtInfo(JobCheckStatus.RESOURCE_OVER_LIMIT.getMsg()+errorMessage);
                return jobCheckRunInfo;
            }
//            scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.TENANT_RESOURCE_TIME);
        }

        if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
            jobCheckRunInfo.setStatus(JobCheckStatus.NOT_UNSUBMIT);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus());
            return jobCheckRunInfo;
        }

        Integer scheduleType = scheduleBatchJob.getScheduleType();
        //正常调度---判断当前任务是不是处于暂停状态--暂停状态直接返回冻结
        if (EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleType)
                && (EScheduleStatus.PAUSE.getVal().equals(batchTaskShade.getScheduleStatus()) ||
                EProjectScheduleStatus.PAUSE.getStatus().equals(batchTaskShade.getProjectScheduleStatus()))) {
            //查询缓存
            jobCheckRunInfo.setStatus(JobCheckStatus.TASK_PAUSE);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus())
                    .frozenReason("任务调度状态为暂停(状态码:2)或项目未开启调度(状态码:1)");
            return jobCheckRunInfo;
        }

        // 质量任务补数据支持冻结
        if (EScheduleType.manualOperatorType.contains(scheduleType)
                && (AppType.DQ.getType().equals(scheduleBatchJob.getAppType()) || AppType.DATAASSETS.getType().equals(scheduleBatchJob.getAppType()))
                && (EScheduleStatus.PAUSE.getVal().equals(batchTaskShade.getScheduleStatus()) ||
                EProjectScheduleStatus.PAUSE.getStatus().equals(batchTaskShade.getProjectScheduleStatus()))) {
            // 直接返回冻结
            jobCheckRunInfo.setStatus(JobCheckStatus.TASK_PAUSE);
            jobErrorContext.jobCheckStatus(jobCheckRunInfo.getStatus())
                    .frozenReason("任务调度状态为暂停(状态码:2)或项目未开启调度(状态码:1)");
            return jobCheckRunInfo;
        }

        return jobCheckRunInfo;
    }


    /**
     * @author zyd
     * @Param [batchTaskShade, tenantResource]
     * @Description 校验当前任务的参数是否超出资源限制
     **/
    private List<String> checkTaskResourceLimit(ScheduleBatchJob scheduleBatchJob ,
                                                ScheduleTaskShade batchTaskShade)  {
        //离线任务才需要校验资源
        //获取租户id
        Long dtuicTenantId = scheduleBatchJob.getScheduleJob().getDtuicTenantId();
        Integer taskType = scheduleBatchJob.getScheduleJob().getTaskType();
        String taskParams = batchTaskShade.getTaskParams();
        return scheduleTaskShadeService.checkResourceLimit
                (dtuicTenantId, taskType, taskParams, batchTaskShade.getTaskId());
    }

    @Override
    protected SummitCheckEventType getEventType() {
        return SummitCheckEventType.task_info_job_status_summit_check_interceptor_end_event;
    }
}
