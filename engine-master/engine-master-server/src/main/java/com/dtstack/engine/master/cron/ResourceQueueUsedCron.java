package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.po.ResourceQueueUsed;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.handler.AbstractMasterHandler;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.impl.ResourceGroupService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ResourceQueueUsedCron {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceQueueUsedCron.class);

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private EnvironmentContext environmentContext;

    @EngineCron
    @Scheduled(cron = "0 0/30 * * * ?")
    public void handle() {
        LOGGER.info("{} do handle", this.getClass().getSimpleName());
        collectResource();
    }

    private void collectResource() {
        LOGGER.info("Start collect queue used resources.");
        Timestamp now = Timestamp.from(Instant.now());
        List<Cluster> clusters = clusterDao.listAll();
        if (CollectionUtils.isEmpty(clusters)) {
            return;
        }
        int corePoolSize = Math.min(clusters.size(), environmentContext.getClusterResourcePoolSize());
        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, environmentContext.getClusterResourcePoolSize(), 1000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(clusters.size()), new CustomThreadFactory(this.getClass().getName()));

        try {
            List<CompletableFuture<List<ResourceQueueUsed>>> totalFutures = clusters.stream().map(c -> CompletableFuture.supplyAsync(() -> {
                ClusterResource resource = consoleService.clusterResources(c.getClusterName(), null, null);
                if (resource == null || CollectionUtils.isEmpty(resource.getQueues())) {
                    LOGGER.warn("get empty when get resources from cluster {}", c.getClusterName());
                    return null;
                }
                List<JSONObject> queues = resource.getQueues();
                return queues.stream().map(q -> {
                    ResourceQueueUsed used = new ResourceQueueUsed();
                    used.setUsed(q.getString("usedCapacity"));
                    used.setQueueName(q.getString("queueName"));
                    used.setClusterId(c.getId());
                    used.setGmtCreate(now);
                    return used;
                }).collect(Collectors.toList());
            }, executorService)).collect(Collectors.toList());

            List<ResourceQueueUsed> queueUsedList = totalFutures.stream().flatMap(s -> {
                try {
                    List<ResourceQueueUsed> resourceQueueUseds = s.get(1, TimeUnit.MINUTES);
                    if (CollectionUtils.isNotEmpty(resourceQueueUseds)) {
                        return resourceQueueUseds.stream();
                    }
                } catch (Exception e) {
                    LOGGER.error("Error when get resources from cluster: ", e);
                }
                return Stream.empty();
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(queueUsedList)) {
                resourceGroupService.batchInsert(queueUsedList);
            }

            resourceGroupService.clearByTimeInterval(environmentContext.getClusterUseSaveInterval());
            LOGGER.info("clear cluster queue use data {}", environmentContext.getClusterUseSaveInterval());
        } catch (Exception e) {
            LOGGER.info("collectResource error:{}", e.getMessage(), e);
        } finally {
            executorService.shutdownNow();
        }
        LOGGER.info("End collect queue used resources.");
    }
}
