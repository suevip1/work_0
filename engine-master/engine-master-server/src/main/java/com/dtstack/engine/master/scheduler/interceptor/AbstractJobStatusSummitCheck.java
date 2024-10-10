package com.dtstack.engine.master.scheduler.interceptor;

import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobErrorContext;
import com.dtstack.engine.master.scheduler.event.SummitCheckEvent;
import com.dtstack.engine.master.scheduler.event.SummitCheckEventType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 11:02 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractJobStatusSummitCheck implements JobStatusSummitCheckChain {

    private JobStatusSummitCheckChain jobStatusSummitCheckChain;

    @Autowired
    private SummitCheckEvent summitCheckEvent;

    @Override
    public void beforeCheckJobStatus(JobStatusSummitCheckContext checkContext) {
        JobCheckRunInfo jobCheckRunInfo = checkContext.getJobCheckRunInfo();

        if (jobCheckRunInfo == null) {
            jobCheckRunInfo = new JobCheckRunInfo();
            jobCheckRunInfo.setStatus(JobCheckStatus.CAN_EXE);
            jobCheckRunInfo.setJobErrorContext(JobErrorContext.newInstance().jobCheckStatus(jobCheckRunInfo.getStatus()));
            checkContext.setJobCheckRunInfo(jobCheckRunInfo);
        }
    }

    @Override
    public void afterCheckJobStatus(JobStatusSummitCheckContext checkContext, JobCheckRunInfo jobCheckRunInfo) {
        summitCheckEvent.event(checkContext,getEventType(),jobCheckRunInfo);
    }

    @Override
    public JobStatusSummitCheckChain setNext(JobStatusSummitCheckChain jobStatusSummitCheckChain) {
        this.jobStatusSummitCheckChain = jobStatusSummitCheckChain;
        return this.jobStatusSummitCheckChain;
    }

    @Override
    public Boolean hasNext() {
        return this.jobStatusSummitCheckChain!=null;
    }

    @Override
    public JobStatusSummitCheckChain next() {
        return this.jobStatusSummitCheckChain;
    }

    protected abstract SummitCheckEventType getEventType();

}
