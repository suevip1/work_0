package com.dtstack.engine.master.scheduler.event;

import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.interceptor.JobJobStatusSummitCheckInterceptor;
import com.dtstack.engine.master.scheduler.interceptor.JobStatusSummitCheckContext;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 11:24 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class SummitCheckEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SummitCheckEvent.class);

    private final List<SummitCheckListener> listeners;

    public SummitCheckEvent() {
        this.listeners = Lists.newArrayList();
    }

    /**
     * 注册监听器
     */
    public void registerEventListener(SummitCheckListener listener) {
        listeners.add(listener);
    }

    public void event(final JobStatusSummitCheckContext checkContext,
                      final SummitCheckEventType summitCheckEventType,
                      final JobCheckRunInfo jobCheckRunInfo) {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }

        ScheduleBatchJob scheduleBatchJob = checkContext.getScheduleBatchJob();
        String jobId = scheduleBatchJob.getJobId();

        for (SummitCheckListener listener : listeners) {
            try {
                LOGGER.info("jobId {} summitCheckEventType :{} jobCheckRunInfo:{}", jobId, summitCheckEventType,jobCheckRunInfo.getStatus());
                listener.event(checkContext,summitCheckEventType,jobCheckRunInfo);
            } catch (Throwable e) {
                LOGGER.error("jobId {} event error: ", jobId, e);
            }
        }
    }
}
