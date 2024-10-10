package com.dtstack.engine.master.scheduler.event;


import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.interceptor.JobStatusSummitCheckContext;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 11:16 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface SummitCheckListener {

    /**
     * 告警时间触发
     */
    void event(JobStatusSummitCheckContext checkContext,
               SummitCheckEventType summitCheckEventType,
               JobCheckRunInfo jobCheckRunInfo);
}
