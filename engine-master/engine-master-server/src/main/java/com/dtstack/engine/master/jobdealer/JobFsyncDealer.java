package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONArray;
import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.bo.JobFsyncInfo;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.dto.FsyncSql;
import com.dtstack.engine.master.manager.FsyncManager;
import com.dtstack.engine.master.utils.FsyncUtil;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.master.worker.TaskOperator;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行结果落盘
 *
 * @author ：wangchuan
 * date：Created in 19:04 2023/2/9
 * company: www.dtstack.com
 */
@Component
public class JobFsyncDealer implements InitializingBean, Runnable, ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobFsyncDealer.class);

    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private FsyncManager fsyncManager;

    @Value("${job.fsync.add.queue.block.time:15000}")
    private Long addQueueBlockTimeInMillSecond;

    private final BlockingQueue<JobFsyncInfo> queue = new ArrayBlockingQueue<>(1000);

    /**
     * 已处理的 job，防止重复处理
     */
    private Set<String> dealtJobIds = Sets.newConcurrentHashSet();

    private final ExecutorService fsyncExecutePool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()), new ThreadPoolExecutor.CallerRunsPolicy());

    private ExecutorService fsyncPool;


    @Override
    public void run() {
        while (true) {
            String jobId = StringUtils.EMPTY;
            try {
                JobFsyncInfo fsyncInfo = queue.take();
                jobId = fsyncInfo.getJobId();
                fsyncPool.submit(() -> jobResultFsync(fsyncInfo));
            } catch (Exception e) {
                LOGGER.error("{} submit jobResultFsync error", jobId, e);
            }
        }
    }

    /**
     * 任务查询结果落盘
     *
     * @param fsyncInfo 任务相关信息
     */
    private void jobResultFsync(JobFsyncInfo fsyncInfo) {
        String jobId = fsyncInfo.getJobId();
        // 打印日志，防止后期排查问题困难
        LOGGER.info("jobResultFsync start, jobId: {}", jobId);
        JobIdentifier jobIdentifier = fsyncInfo.getJobIdentifier();
        TaskOperator dataSourceXOperator = operatorDistributor.getOperator(fsyncInfo.getClientType(), jobIdentifier.getEngineType());
        try {
            JobExecInfo jobExecInfo = dataSourceXOperator.getJobExecInfo(jobIdentifier, false, false);
            if (Objects.isNull(jobExecInfo) || MapUtils.isEmpty(jobExecInfo.getExecResult())) {
                LOGGER.warn("get job exec result is empty, jobId: {}", jobId);
                return;
            }
            // 将要落盘的文件目录
            String fsyncFileDir = fsyncManager.generateFsyncDir(fsyncInfo.getCycTime(), fsyncInfo.getGmtCreateTime(), fsyncInfo.getJobId());
            Map<String, List<List<String>>> execResult = jobExecInfo.getExecResult();
            for (Map.Entry<String, List<List<String>>> entry : execResult.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue())) {
                    LOGGER.warn("jobId:{}, sqlId: {}, result is empty", jobId, entry.getKey());
                    continue;
                }
                String fsyncFilePath = fsyncManager.concatFilePath(fsyncFileDir, entry.getKey());
                // 落盘
                fsyncManager.writeToFile(entry.getValue(), fsyncFilePath);
            }
        } catch (Exception e) {
            LOGGER.error("job {} exec result fsync error", jobId, e);
        } finally {
            try {
                // 落盘完成再去清理缓存的结果
                dataSourceXOperator.getJobExecInfo(jobIdentifier, true, false);
            } catch (Exception e) {
                LOGGER.error("job {} clean exec result fsync error", jobId, e);
            }
            dealtJobIds.remove(jobId);
        }
    }

    /**
     * 添加需要获取日志的任务到 queue 中
     *
     * @param jobFsyncInfo 任务相关信息
     */
    public void addJobInfo(JobFsyncInfo jobFsyncInfo) {
        String jobId = jobFsyncInfo.getJobId();
        if (!dealtJobIds.add(jobId)) {
            // 已经处理过了就不再处理
            return;
        }
        // 只有等待落盘的 rdb 任务会走到这里，所以加上日志，避免后续无法追踪问题
        LOGGER.info("jobId:{} status:FINISHED_WAIT_FLUSH", jobId);
        try {
            // 非 datasourceX 任务直接返回
            if (!ClientTypeEnum.DATASOURCEX_NO_STATUS.equals(jobFsyncInfo.getClientType())) {
                return;
            }
            JobIdentifier jobIdentifier = jobFsyncInfo.getJobIdentifier();
            Integer dataSourceType = rdosWrapper.getDataSourceType(jobIdentifier.getPluginInfo(), jobIdentifier.getEngineType(), null, jobIdentifier.getTenantId());
            // 判断是否满足落盘条件
            if (!jobNeedFsync(jobFsyncInfo.getScheduleType(), dataSourceType, jobFsyncInfo.getFsyncSql())) {
                // 打印日志，防止后期排查问题困难
                LOGGER.info("jobNeedFsync false, jobId: {}, so skip", jobId);
                return;
            }
            if (queue.contains(jobFsyncInfo)) {
                LOGGER.info("fsync queue contains this job, jobId: {}, so skip", jobId);
                return;
            }
            LOGGER.info("add job {} into result fsync dealer", jobId);
            queue.offer(jobFsyncInfo, addQueueBlockTimeInMillSecond, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("{} interrupted", jobId, e);
        } catch (Exception e) {
            LOGGER.error("{} addJobInfo error", jobId, e);
        }
    }

    /**
     * 判断是否需要将查询的结果落盘：临时运行/手动任务 + 满足配置项 + 包含 select 语句
     *
     * @param scheduleType   调度类型
     * @param dataSourceType 数据源类型
     * @param fsyncSql       需要执行的 sql 信息
     * @return 是否需要将执行结果落盘
     */
    private boolean jobNeedFsync(Integer scheduleType, Integer dataSourceType, String fsyncSql) {
        // 必须是临时运行或手动任务
        if (!EScheduleType.TEMP_JOB.getType().equals(scheduleType)
                && !EScheduleType.MANUAL.getType().equals(scheduleType)) {
            return false;
        }

        Set<Integer> fsyncSupportDataSourceType = environmentContext.getFsyncSupportDataSourceType();
        // 满足配置项
        if (!fsyncSupportDataSourceType.contains(dataSourceType)) {
            return false;
        }

        List<FsyncSql> rdosSqlList = JSONArray.parseArray(fsyncSql, FsyncSql.class);
        if (CollectionUtils.isEmpty(rdosSqlList)) {
            return false;
        }
        // 是否包含 select 语句
        boolean containSelect = false;
        for (FsyncSql singleFsyncSql : rdosSqlList) {
            if (FsyncUtil.containSelect(singleFsyncSql)) {
                containSelect = true;
                break;
            }
        }
        return containSelect;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fsyncPool = new ThreadPoolExecutor(3, environmentContext.getFsyncPoolSize(), 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new CustomThreadFactory(this.getClass().getSimpleName()));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        fsyncExecutePool.execute(this);
    }
}
