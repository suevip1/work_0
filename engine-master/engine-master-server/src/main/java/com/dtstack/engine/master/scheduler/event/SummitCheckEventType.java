package com.dtstack.engine.master.scheduler.event;

/**
 * @Auther: dazhi
 * @Date: 2023/2/28 11:17 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public enum SummitCheckEventType {

    // 过滤器结束事件类型
    job_restrict_submit_check_interceptor_end_event,
    task_info_job_status_summit_check_interceptor_end_event,
    job_time_job_status_summit_check_interceptor_end_event,
    job_job_status_summit_check_interceptor_end_evnet,

    // 校验通过事件
    summit_check_pass_event,
    ;

}
