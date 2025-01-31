package com.dtstack.engine.master.queue;

import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.queue.comparator.JobClientComparator;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobSubmitDealer;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.EngineJobCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class GroupPriorityQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupPriorityQueue.class);

    private static final int WAIT_INTERVAL = 5000;

    private AtomicBoolean blocked = new AtomicBoolean(false);

    private String jobResource;
    private int queueSizeLimited;

    private ApplicationContext applicationContext;
    private EnvironmentContext environmentContext;
    private EngineJobCacheDao engineJobCacheDao;
    private ScheduleJobDao scheduleJobDao;
    private JobDealer jobDealer;
    private JobPartitioner jobPartitioner;
    private EnginePluginsOperator enginePluginsOperator;

    private PriorityBlockingQueue<JobClient> queue = null;
    public JobSubmitDealer jobSubmitDealer = null;

    public JobSubmitDealer getJobSubmitDealer() {
        return jobSubmitDealer;
    }

    private GroupPriorityQueue() {
    }

    public boolean add(JobClient jobClient, boolean judgeBlock, boolean insert) {
        if (judgeBlock) {
            if (isBlocked()) {
                LOGGER.info("jobId:{} unable add to queue, because queue is blocked.", jobClient.getTaskId());
                return false;
            }
            return addInner(jobClient, insert);
        } else {
            return addRedirect(jobClient, insert);
        }
    }

    private boolean addInner(JobClient jobClient, boolean insert) {
        if (this.priorityQueueSize() >= getQueueSizeLimited()) {
            blocked.set(true);
            LOGGER.info("jobId:{} unable add to queue, because over QueueSizeLimited.", jobClient.getTaskId());
            return false;
        }
        return addRedirect(jobClient, insert);
    }

    private boolean addRedirect(JobClient jobClient, boolean insert) {
        if (queue.contains(jobClient)) {
            LOGGER.info("jobId:{} unable add to queue, because jobId already exist.", jobClient.getTaskId());
            return true;
        }

        jobDealer.saveCache(jobClient, jobResource, EJobCacheStage.PRIORITY.getStage(), insert);

        queue.put(jobClient);
        LOGGER.info("jobId:{} redirect add job to queue.", jobClient.getTaskId());
        return true;
    }

    public boolean addRestartJob(JobClient jobClient) {
        return jobSubmitDealer.tryPutRestartJob(jobClient);
    }

    public PriorityBlockingQueue<JobClient> getQueue() {
        return queue;
    }

    public boolean remove(JobClient jobClient) {
        if (queue.remove(jobClient)) {
            return true;
        }
        return false;
    }

    private boolean isBlocked() {
        return blocked.get();
    }

    private long priorityQueueSize() {
        return queue.size() + (long)jobSubmitDealer.getDelayJobQueueSize();
    }

    public String getJobResource() {
        return jobResource;
    }

    public int getQueueSizeLimited() {
        return queueSizeLimited;
    }

    /**
     * @return false: blocked | true: unblocked
     */
    private boolean emitJob2PriorityQueue() {
        boolean empty = false;
        String localAddress = "";
        try {
            if (priorityQueueSize() >= getQueueSizeLimited()) {
                return false;
            }
            localAddress = environmentContext.getLocalAddress();
            long startId = 0L;
            outLoop:
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, EJobCacheStage.DB.getStage(), null, jobResource);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    empty = true;
                    break;
                }
                for (EngineJobCache jobCache : jobCaches) {
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
                        jobClient.setCallBack((jobStatus) -> {
                            jobDealer.updateJobStatus(jobClient.getTaskId(), jobStatus);
                        });

                        boolean addInner = this.addInner(jobClient, false);
                        LOGGER.info("jobId:{} load from db, {} emit job to queue.", jobClient.getTaskId(), addInner ? "success" : "failed");
                        if (!addInner) {
                            empty = false;
                            break outLoop;
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        //数据转换异常--打日志
                        jobDealer.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("emitJob2PriorityQueue localAddress:{} error:", localAddress, e);
        }
        if (empty) {
            blocked.set(false);
        }
        return empty;
    }

    private class AcquireGroupQueueJob implements Runnable {

        /**
         * blocked=true，已存储的任务数据超出队列limited上限
         * <p>
         * 如果队列中的任务数量小于
         *
         * @see com.dtstack.engine.master.queue.GroupPriorityQueue#queueSizeLimited ,
         * 并且没有查询到新的数据，则停止调度
         * @see com.dtstack.engine.master.queue.GroupPriorityQueue#blocked
         */
        @Override
        public void run() {
            try {
                if (Boolean.FALSE == blocked.get()) {
                    int jobSize = engineJobCacheDao.countByStage(jobResource, EJobCacheStage.unSubmitted(), environmentContext.getLocalAddress());
                    if (jobSize == 0) {
                        return;
                    }
                }

                emitJob2PriorityQueue();
            } catch (Exception e) {
                LOGGER.error("AcquireGroupQueueJob localAddress:{} error:",  environmentContext.getLocalAddress(), e);
            }
        }
    }

    public GroupPriorityQueue setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public GroupPriorityQueue setJobResource(String jobResource) {
        this.jobResource = jobResource;
        return this;
    }

    public GroupPriorityQueue setJobDealer(JobDealer jobDealer) {
        this.jobDealer = jobDealer;
        return this;
    }

    public static GroupPriorityQueue builder() {
        return new GroupPriorityQueue();
    }

    private void checkParams() {
        if (StringUtils.isBlank(jobResource)) {
            throw new RuntimeException("jobResource is null.");
        }
        if (queueSizeLimited <= 0) {
            throw new RuntimeException("queueSizeLimited less than 0.");
        }
        if (null == environmentContext) {
            throw new RuntimeException("environmentContext is null.");
        }
        if (null == engineJobCacheDao) {
            throw new RuntimeException("engineJobCacheDao is null.");
        }
        if (null == scheduleJobDao) {
            throw new RuntimeException("scheduleJobDao is null.");
        }
        if (null == jobDealer) {
            throw new RuntimeException("workNode is null.");
        }
        if (null == jobPartitioner) {
            throw new RuntimeException("jobPartitioner is null.");
        }
        if (null == enginePluginsOperator) {
            throw new RuntimeException("workerOperator is null.");
        }
    }

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     */
    public GroupPriorityQueue build() {
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.jobPartitioner = applicationContext.getBean(JobPartitioner.class);
        this.enginePluginsOperator = applicationContext.getBean(EnginePluginsOperator.class);

        this.queueSizeLimited = environmentContext.getQueueSize();

        checkParams();

        this.queue = new PriorityBlockingQueue<>(queueSizeLimited * 2, new JobClientComparator());
        this.jobSubmitDealer = new JobSubmitDealer(environmentContext.getLocalAddress(), this.jobResource,this.queue, applicationContext);

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(
                new AcquireGroupQueueJob(),
                WAIT_INTERVAL * 10L,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);

        ExecutorService jobSubmitService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_JobSubmit"));
        jobSubmitService.submit(jobSubmitDealer);
        return this;
    }
}
