package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.scheduler.event.SummitCheckEvent;
import com.dtstack.engine.master.scheduler.event.SummitCheckEventType;
import com.dtstack.engine.master.scheduler.interceptor.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 10:35 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobStatusSummitCheckOperator {

    @Autowired
    private JobStatusSummitCheckChainFactory jobStatusSummitCheckChainFactory;

    @Autowired
    private SummitCheckEvent summitCheckEvent;

    /**
     * 校验实例状态是否满足提交状态
     *
     * @param status 实例状态
     * @param scheduleTaskShade 任务信息
     * @param scheduleBatchJob 实例信息
     * @param isSelectWaitReason 是否是查询等待原因 {@link com.dtstack.engine.master.impl.ScheduleJobService#selectWaitReason}
     * @return 校验结果
     */
    public JobCheckRunInfo checkJobStatusMeetSubmissionConditions(Integer status,
                                                                  ScheduleTaskShade scheduleTaskShade,
                                                                  ScheduleBatchJob scheduleBatchJob,
                                                                  boolean isSelectWaitReason) {
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();
        checkRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        checkRunInfo.setJobErrorContext(JobErrorContext.newInstance().jobCheckStatus(checkRunInfo.getStatus()));

        JobStatusSummitCheckChain jobStatusSummitCheckChain = jobStatusSummitCheckChainFactory.getJobStatusSummitCheckChain();

        JobStatusSummitCheckContext jobStatusSummitCheckContext = null;
        while (true) {
            jobStatusSummitCheckContext = getJobStatusSummitCheckContext(status, scheduleTaskShade, scheduleBatchJob, checkRunInfo, isSelectWaitReason);

            // 前置处理
            jobStatusSummitCheckChain.beforeCheckJobStatus(jobStatusSummitCheckContext);

            // 判断是否到达提交条件
            JobCheckRunInfo jobCheckRunInfo = jobStatusSummitCheckChain.checkJobStatus(jobStatusSummitCheckContext);

            // 后置处理
            jobStatusSummitCheckChain.afterCheckJobStatus(jobStatusSummitCheckContext, jobCheckRunInfo);

            if (!jobCheckRunInfo.getStatus().equals(JobCheckStatus.CAN_EXE)) {
                return jobCheckRunInfo;
            }

            checkRunInfo = jobCheckRunInfo;

            if (jobStatusSummitCheckChain.hasNext()) {
                jobStatusSummitCheckChain = jobStatusSummitCheckChain.next();
            } else {
                break;
            }
        }

        if (!isSelectWaitReason) {
            summitCheckEvent.event(jobStatusSummitCheckContext, SummitCheckEventType.summit_check_pass_event,checkRunInfo);
        }
        return checkRunInfo;
    }

    @NotNull
    private JobStatusSummitCheckContext getJobStatusSummitCheckContext(Integer status, ScheduleTaskShade scheduleTaskShade,
                                                                       ScheduleBatchJob scheduleBatchJob, JobCheckRunInfo checkRunInfo,
                                                                       boolean isSelectWaitReason) {
        JobStatusSummitCheckContext jobStatusSummitCheckContext = new JobStatusSummitCheckContext();
        jobStatusSummitCheckContext.setStatus(status);
        jobStatusSummitCheckContext.setJobCheckRunInfo(checkRunInfo);
        jobStatusSummitCheckContext.setScheduleBatchJob(scheduleBatchJob);
        jobStatusSummitCheckContext.setScheduleTaskShade(scheduleTaskShade);

        JSONObject extraInfo = new JSONObject();
        extraInfo.put(JobStatusSummitCheckContext.isSelectWaitReason, isSelectWaitReason);
        jobStatusSummitCheckContext.setExtraInfo(extraInfo);
        return jobStatusSummitCheckContext;
    }

}
