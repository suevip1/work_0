package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.enums.JobBuildStatus;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleJobBuildRecordService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/22
 */
@Component
public class JobGraphBuilderTrigger implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphBuilderTrigger.class);

    private static final long CHECK_JOB_BUILD_INTERVAL = 60 * 10 * 1000L;

    /**
     * 校验立即生成实例任务的间隔, 30s
     */
    private static final long CHECK_JOB_BUILD_IMMEDIATELY_INTERVAL = 30 * 1000L;

    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    private ScheduledExecutorService scheduledService;

    @Autowired
    private ScheduleJobBuildRecordService scheduleJobBuildRecordService;

    public JobGraphBuilderTrigger() {
        scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory("JobGraphTrigger"));
    }

    public void dealMaster(boolean isMaster) {
        try {
            if (isMaster) {
                startJobGraph();
            } else {
                stopJobGraph();
            }
        } catch (Throwable e) {
            LOGGER.error("JobGraphBuilderTrigger.dealMaster error:", e);
        }
    }

    private void startJobGraph() {
        if (RUNNING.get()) {
            return;
        }
        if (scheduledService.isShutdown()) {
            scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory("JobGraphTrigger"));
        }

        scheduledService.scheduleAtFixedRate(
                this,
                100,
                CHECK_JOB_BUILD_INTERVAL,
                TimeUnit.MILLISECONDS);

        // 刷新立即创建实例状态为 create
        scheduleJobBuildRecordService.refreshRunningStatus(JobBuildStatus.CREATE.getStatus());

        // (立即生成实例) 检查, 30s 一次
        scheduledService.scheduleAtFixedRate(
                this::buildJobGraphImmediately,
                200,
                CHECK_JOB_BUILD_IMMEDIATELY_INTERVAL,
                TimeUnit.MILLISECONDS);

        RUNNING.compareAndSet(false, true);
        LOGGER.info("start job graph trigger...");
    }

    private void stopJobGraph() {
        if (scheduledService != null) {
            scheduledService.shutdownNow();
        }
        RUNNING.compareAndSet(true, false);
        LOGGER.info("stop job graph trigger...");
    }

    private String getTriggerDay(String time) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        SimpleDateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
        Date triggerDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
        if (triggerDate.after(new Date())) {
            //校验当天运行的
            return new DateTime().toString("yyyy-MM-dd");
        }
        return new DateTime().plusDays(1).toString("yyyy-MM-dd");
    }

    @Override
    public void run() {
        try {
            if (RUNNING.get()) {
                try {
                    String triggerDay = getTriggerDay(environmentContext.getJobGraphBuildCron());
                    LOGGER.warn("---check jobGraph build day:{} job graph start!--", triggerDay);
                    jobGraphBuilder.buildTaskJobGraph(triggerDay);
                    LOGGER.warn("---check jobGraph build day:{} job graph end!--", triggerDay);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            } else {
                LOGGER.warn("---triggering, but Running is false---");
            }
        } catch (Exception e) {
            LOGGER.error("---trigger job graph error---", e);
        }
    }

    /**
     * 立即生成实例
     */
    public void buildJobGraphImmediately() {
        try {
            if (RUNNING.get()) {
                try {
                    LOGGER.warn("---check jobGraph build immediately start!--");
                    jobGraphBuilder.buildTaskJobGraphImmediately();
                    LOGGER.warn("---check jobGraph build immediately end!--");
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            } else {
                LOGGER.warn("---triggering build job graph immediately, but Running is false---");
            }
        } catch (Exception e) {
            LOGGER.error("---trigger build job graph immediately error---", e);
        }
    }
}
