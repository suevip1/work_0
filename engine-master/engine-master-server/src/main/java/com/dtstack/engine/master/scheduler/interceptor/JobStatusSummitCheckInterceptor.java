package com.dtstack.engine.master.scheduler.interceptor;

import com.dtstack.engine.master.scheduler.JobCheckRunInfo;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 10:44 AM
 * @Email: dazhi@dtstack.com
 * @Description: 实例状态判断拦截器
 */
public interface JobStatusSummitCheckInterceptor {

    /**
     * 校验前置处理
     * @param checkContext 上下文对象
     */
    void beforeCheckJobStatus(JobStatusSummitCheckContext checkContext);

    /**
     * 校验实例状态
     * @param checkContext 上下文对象
     * @return 校验结果
     */
    JobCheckRunInfo checkJobStatus(JobStatusSummitCheckContext checkContext);

    /**
     * 校验结果处理
     * @param checkContext 上下文对象
     * @param jobCheckRunInfo 校验结果
     */
    void afterCheckJobStatus(JobStatusSummitCheckContext checkContext,JobCheckRunInfo jobCheckRunInfo);

}
