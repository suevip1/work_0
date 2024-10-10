package com.dtstack.engine.master.scheduler.interceptor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 10:39 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobStatusSummitCheckChainFactory implements InitializingBean {

    @Autowired
    private JobJobStatusSummitCheckInterceptor jobJobStatusSummitCheckInterceptor;

    @Autowired
    private JobTimeJobStatusSummitCheckInterceptor jobStatusSummitCheckInterceptor;

    @Autowired
    private TaskInfoJobStatusSummitCheckInterceptor taskInfoJobStatusSummitCheckInterceptor;

    @Autowired
    private JobRestrictSubmitCheckInterceptor jobRestrictSubmitCheckInterceptor;

    public JobStatusSummitCheckChain getJobStatusSummitCheckChain () {
        return jobRestrictSubmitCheckInterceptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jobRestrictSubmitCheckInterceptor
                .setNext(taskInfoJobStatusSummitCheckInterceptor)
                .setNext(jobStatusSummitCheckInterceptor)
                .setNext(jobJobStatusSummitCheckInterceptor);
    }
}
