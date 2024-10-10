package com.dtstack.engine.master.workflow.temporary;

import com.dtstack.engine.master.workflow.temporary.model.Workflow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 工作流临时运行的配置参数
 *
 * @author leon
 * @date 2023-03-10 10:31
 **/
@Configuration
public class WorkflowConfig {

    @Value("${workflow.lock.wait.time:30}")
    private Long workflowLockWaitTime;

    @Value("${workflow.lock.lease.time:10}")
    private Long workflowLockLeaseTime;

    @Value("${workflow.temporary.acceptor.queue.size:100}")
    private Integer workflowTemporaryAcceptorQueueSize;

    // 工作流单个子任务运行时长最大限制
    @Value("${workflow.temporary.per.task.exe.time.max.limit.hour:3}")
    private Long workflowTemporaryPerTaskExeTimeMaxLimitHour;

    @Bean
    public LinkedBlockingQueue<Workflow> workflowTemporaryAcceptorQueue() {
        return new LinkedBlockingQueue<>(workflowTemporaryAcceptorQueueSize);
    }

    public Long getWorkflowLockWaitTime() {
        return workflowLockWaitTime;
    }

    public void setWorkflowLockWaitTime(Long workflowLockWaitTime) {
        this.workflowLockWaitTime = workflowLockWaitTime;
    }

    public Long getWorkflowLockLeaseTime() {
        return workflowLockLeaseTime;
    }

    public void setWorkflowLockLeaseTime(Long workflowLockLeaseTime) {
        this.workflowLockLeaseTime = workflowLockLeaseTime;
    }

    public Integer getWorkflowTemporaryAcceptorQueueSize() {
        return workflowTemporaryAcceptorQueueSize;
    }

    public void setWorkflowTemporaryAcceptorQueueSize(Integer workflowTemporaryAcceptorQueueSize) {
        this.workflowTemporaryAcceptorQueueSize = workflowTemporaryAcceptorQueueSize;
    }

    public Long getWorkflowTemporaryPerTaskExeTimeMaxLimitHour() {
        return workflowTemporaryPerTaskExeTimeMaxLimitHour;
    }

    public void setWorkflowTemporaryPerTaskExeTimeMaxLimitHour(Long workflowTemporaryPerTaskExeTimeMaxLimitHour) {
        this.workflowTemporaryPerTaskExeTimeMaxLimitHour = workflowTemporaryPerTaskExeTimeMaxLimitHour;
    }
}
