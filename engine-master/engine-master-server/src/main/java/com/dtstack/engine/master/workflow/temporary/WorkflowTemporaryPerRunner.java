package com.dtstack.engine.master.workflow.temporary;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.LockSupport;

/**
 * @author leon
 * @date 2023-03-15 22:49
 **/
public class WorkflowTemporaryPerRunner extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(WorkflowTemporaryPerRunner.class);

    private final CompletableFuture<RdosTaskStatus> future;

    private final String jobId;

    private final WorkflowTemporaryJobEventListener listener;

    private final ScheduleJobService scheduleJobService;

    public WorkflowTemporaryPerRunner(String jobId,
                                      Runnable target,
                                      CompletableFuture<RdosTaskStatus> future,
                                      WorkflowTemporaryJobEventListener listener,
                                      ScheduleJobService scheduleJobService) {
        super(target);
        this.jobId = jobId;
        this.future = future;
        this.listener = listener;
        this.scheduleJobService = scheduleJobService;
    }


    @Override
    public void run() {
        try {
            LOGGER.info("jobId:{},workflow temporary per runner start, threadId:{}",
                    jobId,
                    Thread.currentThread().getId());
            super.run();
        } catch (Throwable e) {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("jobId:{},workflow temporary per runner run failed:{}, threadId:{}",
                    jobId,
                    errorMessage,
                    Thread.currentThread().getId());
            future.complete(RdosTaskStatus.FAILED);
            return;
        }

        // block here until job stopped => WorkflowTemporaryJobEventListener.unParkRunner
        LOGGER.info("jobId:{},workflow temporary per runner park here until job finished, threadId:{}",
                jobId,
                Thread.currentThread().getId());

        try {
            registerRunnerAndPark();
        } catch (InterruptedException e) {
            LOGGER.info("jobId:{}，workflow temporary per runner register and park failed:{}, threadId:{}",
                    ExceptionUtil.getErrorMessage(e),
                    jobId,
                    Thread.currentThread().getId());
            // 这里作为失败情况返回
            future.complete(RdosTaskStatus.FAILED);
            return;
        }

        LOGGER.info("jobId:{}，workflow temporary per runner unPark back here, threadId:{}",
                jobId,
                Thread.currentThread().getId());

        ScheduleJob targetJob = scheduleJobService.getByJobId(jobId, null);

        // 获取 job 当前状态
        RdosTaskStatus status = Optional.ofNullable(targetJob)
                .map(ScheduleJob::getStatus)
                .map(RdosTaskStatus::getTaskStatus)
                .orElse(RdosTaskStatus.NOTFOUND);

        LOGGER.info("jobId:{}, workflow temporary per runner finished, status: {}",
                jobId, status);

        future.complete(status);
    }

    private void registerRunnerAndPark() throws InterruptedException {
        // 这里判断下任务是否已经执行完成了，如果已经执行完成，就不需要阻塞了
        if (isJobAlreadyStopped(jobId)) {
            return;
        }
        listener.registerRunner(jobId, Thread.currentThread());

        while (!isJobAlreadyStopped(jobId)) {
            listener.registerRunner(jobId, Thread.currentThread());
            LockSupport.park(this);
        }
    }

    private boolean isJobAlreadyStopped(String jobId) {
        ScheduleJob targetJob = scheduleJobService.getByJobId(jobId, null);
        return Objects.nonNull(targetJob) && RdosTaskStatus.STOPPED_STATUS.contains(targetJob.getStatus());
    }
}
