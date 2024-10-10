package com.dtstack.engine.master.scheduler.interceptor;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 10:15 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface JobStatusSummitCheckChain extends JobStatusSummitCheckInterceptor {

    /**
     * 设置链的下一个
     * @param jobStatusSummitCheckChain 链元素
     * @return 下一个责任链
     */
    JobStatusSummitCheckChain setNext(JobStatusSummitCheckChain jobStatusSummitCheckChain);

    /**
     * 是否存在下一个
     * @return true 存在 false 不存在
     */
    Boolean hasNext();

    /**
     * 链上的下一个元素
     * @return 链元素
     */
    JobStatusSummitCheckChain next();


}
