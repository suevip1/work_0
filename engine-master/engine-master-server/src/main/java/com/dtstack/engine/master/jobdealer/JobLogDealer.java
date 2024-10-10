package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EJobLogType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.bo.JobLogInfo;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取任务运行结束日志 需要延迟
 * 获取任务重试日志  无需延迟
 * 获取任务超时日志 需要延迟
 */
@Component
public class JobLogDealer implements InitializingBean, Runnable, ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobLogDealer.class);
    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    private final DelayBlockingQueue<JobLogInfo> delayBlockingQueue = new DelayBlockingQueue<>(1000);
    private final ExecutorService logExecutePool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()));

    private ExecutorService logGetPool;


    @Override
    public void run() {
        while (true) {
            try {
                JobLogInfo taskInfo = delayBlockingQueue.take();
                EJobLogType logType = taskInfo.getLogType();
                switch (logType) {
                    case FINISH_LOG:
                        logGetPool.execute(() -> updateJobEngineLog(taskInfo));
                    case EXPIRE_LOG:
                        logGetPool.execute(() -> updateExpireLog(taskInfo));
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * 更新事件任务取消日志, 如果超时未触发则更新任务状态未失败并记录日志, 如果未超时则重新放入 delayBlockingQueue 中等待下次判断
     *
     * @param jobLogInfo 任务相关信息
     */
    private void updateExpireLog(JobLogInfo jobLogInfo) {
        String jobId = jobLogInfo.getJobId();
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
            if (scheduleJob == null || !EScheduleJobType.EVENT.getType().equals(scheduleJob.getTaskType())) {
                return;
            }
            if (RdosTaskStatus.isStopped(scheduleJob.getStatus())) {
                LOGGER.info("can not update {} expire log ,job is finish status {}", scheduleJob.getJobId(), scheduleJob.getStatus());
                return;
            }
            if (RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
                JSONObject sqlText = scheduleJobService.getSqlText(EScheduleType.TEMP_JOB.getType().equals(scheduleJob.getType()),
                        scheduleJob.getTaskId(), scheduleJob.getAppType(), scheduleJob.getJobId());
                Integer timeOut = 0;
                if (null != sqlText) {
                    timeOut = sqlText.getInteger(GlobalConst.TIMEOUT);
                }
                //判断是否超时
                long timeoutParam = timeOut * 60 * 1000;
                Timestamp execStartTime = scheduleJob.getExecStartTime();
                long time = execStartTime.getTime();
                long currentTimeMillis = System.currentTimeMillis();

                if ((currentTimeMillis - time) >= timeoutParam) {
                    // 已经超时
                    String expireLogTime = String.format(GlobalConst.EVENT_FAIL, timeOut);
                    scheduleJobExpandDao.updateEngineLog(jobId, expireLogTime);
                    scheduleJobDao.jobFinish(jobId, RdosTaskStatus.FAILED.getStatus());
                } else {
                    JobLogInfo refreshDelayJobInfo = new JobLogInfo(jobId, jobLogInfo.getJobIdentifier(), jobLogInfo.getEngineType(), jobLogInfo.getComputeType(),
                            currentTimeMillis - time - timeoutParam, EJobLogType.EXPIRE_LOG, jobLogInfo.getClientType());
                    addJobInfo(refreshDelayJobInfo);
                }
            }
        } catch (Exception e) {
            LOGGER.error("update job {} expire log error", jobId, e);
        }
    }

    /**
     * 添加需要获取日志的任务到 delayBlockingQueue 中
     *
     * @param jobLogInfo 任务相关信息
     */
    public void addJobInfo(JobLogInfo jobLogInfo) {
        try {
            LOGGER.info("add job {} into log dealer", jobLogInfo.getJobId());
            delayBlockingQueue.put(jobLogInfo);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * 使用 logGetPool 线程池异步执行任务
     *
     * @param runnable 需要执行的操作
     */
    public void executeLogRunnable(Runnable runnable) {
        logGetPool.execute(runnable);
    }

    /**
     * 更新任务引擎日志
     *
     * @param completedInfo 日志获取相关参数
     */
    private void updateJobEngineLog(JobLogInfo completedInfo) {
        String jobId = completedInfo.getJobId();
        JobIdentifier jobIdentifier = completedInfo.getJobIdentifier();
        String customLog = completedInfo.getCustomLog();
        try {
            String jobLog;
            if (StringUtils.isNotEmpty(customLog)) {
                jobLog = customLog;
            } else {
                jobLog = operatorDistributor.getOperator(completedInfo.getClientType(), jobIdentifier.getEngineType()).getEngineLog(jobIdentifier);
            }
            if (jobLog != null) {
                scheduleJobExpandDao.updateEngineLog(jobId, jobLog);
            }
        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            scheduleJobExpandDao.updateEngineLog(jobId, errorLog);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        logGetPool = new ThreadPoolExecutor(3, environmentContext.getLogPoolSize(), 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new CustomThreadFactory(this.getClass().getSimpleName()));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logExecutePool.execute(this);
    }

    /**
     * 更新事件任务超时取消日志
     *
     * @param sqlText     任务信息
     * @param jobId       任务 id
     * @param computeType 计算类型
     * @param engineType  引擎类型
     * @param clientType  client 类型
     */
    public void addExpireLogJob(String sqlText, String jobId, Integer computeType, String engineType, ClientTypeEnum clientType) {
        JSONObject sql = JSONObject.parseObject(sqlText);
        Integer timeout = sql.getInteger(GlobalConst.TIMEOUT);
        JobLogInfo refreshDelayJobInfo = new JobLogInfo(jobId, null, engineType, computeType, timeout * 60 * 1000, EJobLogType.EXPIRE_LOG, clientType);
        addJobInfo(refreshDelayJobInfo);
    }
}
