package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.po.EngineJobCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2023/6/14 3:45 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobPriorityDealer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobPriorityDealer.class);

    private String jobResource;

    private JobSubmitDealer jobSubmitDealer;

    private final Cache<String,List<String>> aliveNodeAddressCache = CacheBuilder.newBuilder()
            .maximumSize(5) // 设置缓存的最大容量
            .expireAfterWrite(2, TimeUnit.MINUTES) // 设置缓存在写入一分钟后失效
            .build();

    private ApplicationContext applicationContext;

    private EngineJobCacheDao engineJobCacheDao;

    private EnvironmentContext environmentContext;

    private ZkService zkService;

    private JobDealer jobDealer;

    private final String NODE_ADDRESS = "NODE_ADDRESS";

    public JobPriorityDealer() {
    }


    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            LOGGER.info("start jobResource:{} time:{}",jobResource,startTime);
            List<String> aliveNode = getAliveNode();

            Long maxCurrentPriority = engineJobCacheDao.getMaxCurrentPriority();

            if (maxCurrentPriority == null || maxCurrentPriority == 0) {
                // 说明当前没有需要提交的任务
                LOGGER.info("start jobResource:{} no cache",jobResource);
                return;
            }
            String maxPriority = maxCurrentPriority.toString();
            int priority = Integer.parseInt(maxPriority.substring(0, 1));

            LOGGER.info("jobResource:{} priority: {}",jobResource,priority);
            if (priority == 0) {
                return;
            }

            Pair<Long, Long> priorityRanger =   calculatePriorityRanger(priority);
            if (priorityRanger == null) {
                return;
            }

            List<EngineJobCache> caches = engineJobCacheDao.getAllCache(EJobCacheStage.DB.getStage(), jobResource, aliveNode,priorityRanger.getLeft(),priorityRanger.getRight());
            if (CollectionUtils.isEmpty(caches)) {
                return;
            }
            
            List<EngineJobCache> engineJobCaches = caches.stream().
                    filter(cache -> environmentContext.getLocalAddress().
                            equalsIgnoreCase(cache.getNodeAddress())).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(engineJobCaches)) {
                LOGGER.info("jobResource:{} priority: {} cache is empty", jobResource, priority);
                return;
            }

            LOGGER.info("start submit:{} costTime:{}",jobResource,System.currentTimeMillis() - startTime);
            for (EngineJobCache engineJobCache : engineJobCaches) {
                try {
                    EngineJobCache cache = engineJobCacheDao.getOne(engineJobCache.getJobId());
                    String jobInfo = cache.getJobInfo();
                    ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
                    JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
                    jobClient.setCallBack((jobStatus) -> {
                        jobDealer.updateJobStatus(jobClient.getTaskId(), jobStatus);
                    });

                    jobDealer.updateCache(jobClient, EJobCacheStage.PRIORITY.getStage());
                    jobSubmitDealer.submit(jobClient);
                } catch (Exception e) {
                    LOGGER.error("jobId : {} jobResource: {} e: ",engineJobCache.getJobId(),jobResource,e);
                    //数据转换异常--打日志
                    jobDealer.dealSubmitFailJob(engineJobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                }
            }
            LOGGER.info("end:{} costTime:{}",jobResource,System.currentTimeMillis() - startTime);
        } catch (Throwable e) {
            LOGGER.error("",e);
        }
    }

    private Pair<Long, Long> calculatePriorityRanger(Integer maxCurrentPriority) {
        if (maxCurrentPriority == null || maxCurrentPriority <= 0) {
            return null;
        }

        long min = Long.parseLong(maxCurrentPriority + "0000000000000");
        long max = Long.parseLong(maxCurrentPriority + "9999999999999");
        return new ImmutablePair<>(min,max);
    }

    public List<String> getAliveNode() throws ExecutionException {
        return aliveNodeAddressCache.get(NODE_ADDRESS, zkService::getAliveBrokersChildren);
    }


    public boolean addRestartJob(JobClient jobClient) {
        return jobSubmitDealer.tryPutRestartJob(jobClient);
    }

    public static JobPriorityDealer builder() {
        return new JobPriorityDealer();
    }

    public JobPriorityDealer setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public JobPriorityDealer setJobResource(String jobResource) {
        this.jobResource = jobResource;
        return this;
    }

    public JobPriorityDealer setJobDealer(JobDealer jobDealer) {
        this.jobDealer = jobDealer;
        return this;
    }

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     */
    public JobPriorityDealer build() {
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.zkService = applicationContext.getBean(ZkService.class);

        this.jobSubmitDealer = new JobSubmitDealer(environmentContext.getLocalAddress(), jobResource,null, applicationContext);

        int waitInterval = environmentContext.getWaitInterval();

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(
                this,
                waitInterval * 10L,
                waitInterval,
                TimeUnit.MILLISECONDS);
        return this;
    }
}
