package com.dtstack.engine.master.jobdealer.cache;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.utils.SpringUtil;
import com.dtstack.engine.po.EngineJobCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * data 数据分片及空闲检测
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ShardManager implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShardManager.class);

    private static final long DATA_CLEAN_INTERVAL = 1000;
    private ScheduledExecutorService scheduledService = null;
    private Map<String, Integer> shard;
    private String jobResource;

    private EnvironmentContext environmentContext;
    private EngineJobCacheDao engineJobCacheDao;
    private ScheduleJobDao scheduleJobDao;
    private long FETCH_CACHE_INTERVAL;

    public ShardManager(String jobResource) {
        this.jobResource = jobResource;
        this.shard = new ConcurrentHashMap<>();
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(jobResource + this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                DATA_CLEAN_INTERVAL,
                TimeUnit.MILLISECONDS);

        environmentContext = SpringUtil.getBean(EnvironmentContext.class);
        engineJobCacheDao = SpringUtil.getBean(EngineJobCacheDao.class);
        scheduleJobDao = SpringUtil.getBean(ScheduleJobDao.class);
        FETCH_CACHE_INTERVAL = Math.max(GlobalConst.DEFAULT_FETCH_CACHE_INTERVAL, environmentContext.getFetchJobCacheIntoShardInterval());
        scheduledService.scheduleWithFixedDelay(
                () -> fetchJobCacheIntoShard(),
                0,
                FETCH_CACHE_INTERVAL,
                TimeUnit.MINUTES
        );
    }

    public Integer putJob(String jobId, Integer status) {
        return shard.put(jobId, status);
    }

    public Integer removeJob(String jobId) {
        return shard.remove(jobId);
    }

    public Map<String, Integer> getShard() {
        return shard;
    }

    public String getJobResource() {
        return jobResource;
    }

    @Override
    public void run() {
        shard.entrySet().removeIf(jobWithStatus -> RdosTaskStatus.needClean(jobWithStatus.getValue()));
    }

    /**
     * 定期拉取 cache 表的记录到内存 shard，用于规避 shard 内存丢失记录的场景
     * ref: http://zenpms.dtstack.cn/zentao/bug-view-82177.html
     *
     * @return
     */
    private void fetchJobCacheIntoShard() {
        // 当前机器 ip
        String localAddress = environmentContext.getLocalAddress();
        // 用于遍历的游标
        long startId = 0L;

        try {
            while (true) {
                // cache 记录
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, EJobCacheStage.SUBMITTED.getStage(), null, this.jobResource);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }

                // job 记录
                List<String> jobIds = jobCaches.stream().map(EngineJobCache::getJobId).collect(Collectors.toList());
                Map<String, Integer> jobId2Status = scheduleJobDao.getRdosJobByJobIds(jobIds).stream()
                        .filter(v -> RdosTaskStatus.WAIT_STATUS.contains(v.getStatus())
                                || RdosTaskStatus.RUNNING_STATUS.contains(v.getStatus()))
                        .collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getStatus, (v1, v2) -> v2));
                if (MapUtils.isEmpty(jobId2Status)) {
                    continue;
                }

                // 将 cache 表的记录放到内存
                for (EngineJobCache jobCache : jobCaches) {
                    startId = jobCache.getId();
                    Integer status = jobId2Status.get(jobCache.getJobId());
                    if (status == null) {
                        continue;
                    }
                    Integer previous = shard.putIfAbsent(jobCache.getJobId(), status);
                    if (previous == null) {
                        // 放入成功，打印日志，方便排查问题
                        LOGGER.info("jobId:{} maybe lost, status:{}, so fetch into shard", jobCache.getJobId(), status);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("fetchJobCacheIntoShard error, localAddress:{}", localAddress, e);
        }
    }
}