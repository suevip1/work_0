package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.po.ResourceQueueUsed;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResourceQueueUsedCronTest {

    ResourceQueueUsedCron cron = new ResourceQueueUsedCron();

    @Test
    public void test() {
        AtomicBoolean isMaster = new AtomicBoolean(true);
        cron.handle();
    }

    public static class Mock {
        @MockInvoke(
                targetClass = ClusterDao.class,
                targetMethod = "listAll"
        )
        public List<Cluster> listAll() {
            Cluster cluster = new Cluster();
            cluster.setClusterName("default");
            return Lists.newArrayList(cluster);
        }

        @MockInvoke(
                targetClass = EnvironmentContext.class,
                targetMethod = "getClusterResourcePoolSize"
        )
        public int getClusterResourcePoolSize() {
            return 1;
        }

        @MockInvoke(
                targetClass = ConsoleService.class,
                targetMethod = "clusterResources"
        )
        public ClusterResource clusterResources(String clusterName,
                                                Map<Integer, String> componentVersionMap, Long dtuicTenantId) {
            ClusterResource clusterResource = new ClusterResource();
            JSONObject queue = new JSONObject();
            queue.put("usedCapacity", 1);
            queue.put("queueName", "default");
            clusterResource.setQueues(Lists.newArrayList(queue));

            return clusterResource;
        }

        @MockInvoke(
                targetClass = ResourceGroupService.class,
                targetMethod = "batchInsert"
        )
        public void batchInsert(List<ResourceQueueUsed> useds) {
        }

        @MockInvoke(
                targetClass = ResourceGroupService.class,
                targetMethod = "clearByTimeInterval"
        )
        public void clearByTimeInterval(Integer timeInterVal) {
        }

        @MockInvoke(
                targetClass = EnvironmentContext.class,
                targetMethod = "getClusterUseSaveInterval"
        )
        public Integer getClusterUseSaveInterval() {
            return 1;
        }

    }
}
