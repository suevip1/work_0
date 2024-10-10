package com.dtstack.engine.master.scheduler.interceptor;

import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.master.enums.JobRestrictStatusEnum;
import com.dtstack.engine.master.impl.ConsoleJobRestrictService;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobErrorContext;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.scheduler.event.SummitCheckEventType;
import com.dtstack.engine.po.ConsoleJobRestrict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 调度规则限制
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-04 10:00
 */
@Component
public class JobRestrictSubmitCheckInterceptor extends AbstractJobStatusSummitCheck{
    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ConsoleJobRestrictService consoleJobRestrictService;

    @Override
    public JobCheckRunInfo checkJobStatus(JobStatusSummitCheckContext checkContext) {
        JobCheckRunInfo jobCheckRunInfo = checkContext.getJobCheckRunInfo();
        Boolean isSelectWaitReason = Optional.ofNullable(checkContext.getExtraInfo())
                .map(t -> t.getBooleanValue(JobStatusSummitCheckContext.isSelectWaitReason))
                .orElse(Boolean.TRUE);

        if (isSelectWaitReason) {
            return showJobRestrict(jobCheckRunInfo);
        } else {
            return jobRichOperator.checkJobRestrict(jobCheckRunInfo);
        }
    }

    @Override
    public void afterCheckJobStatus(JobStatusSummitCheckContext checkContext, JobCheckRunInfo jobCheckRunInfo) {
        // just do nothing
    }

    @Override
    protected SummitCheckEventType getEventType() {
        return SummitCheckEventType.job_restrict_submit_check_interceptor_end_event;
    }

    private JobCheckRunInfo showJobRestrict(JobCheckRunInfo jobCheckRunInfo) {
        JobErrorContext jobErrorContext = jobCheckRunInfo.getJobErrorContext();

        // 找到最近的一条起作用的规则
        ConsoleJobRestrict jobRestrict = consoleJobRestrictService.findLatestWaitAndRunRestrict();
        if (Objects.isNull(jobRestrict)) {
            return jobCheckRunInfo;
        }

        // 确定该规则被实际命中
        if (JobRestrictStatusEnum.WAIT.getStatus().equals(jobRestrict.getStatus())
            && Objects.isNull(jobRestrict.getEffectiveTime())) {
            return jobCheckRunInfo;
        }

        String restrictTip = ConsoleJobRestrictService.generateRestrictTip(jobRestrict);
        jobCheckRunInfo.setStatus(JobCheckStatus.CONSOLE_JOB_RESTRICT);
        jobCheckRunInfo.setExtInfo(restrictTip);
        jobErrorContext.jobCheckStatus(JobCheckStatus.CONSOLE_JOB_RESTRICT);
        jobErrorContext.extraMsg(restrictTip);
        return jobCheckRunInfo;
    }
}
