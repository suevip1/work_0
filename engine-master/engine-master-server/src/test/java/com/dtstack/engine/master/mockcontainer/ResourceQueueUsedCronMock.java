package com.dtstack.engine.master.mockcontainer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.po.ResourceQueueUsed;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 * @date 2022-06-27 19:07
 **/
public class ResourceQueueUsedCronMock {


    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getClusterResourcePoolSize() {
        return 10;
    }


    @MockInvoke(targetClass = ClusterDao.class)
    public List<Cluster> listAll() {
        Cluster cluster = new Cluster();
        cluster.setClusterName("leon");
        return Lists.newArrayList(cluster);
    }

    @MockInvoke(targetClass = ConsoleService.class)
    public ClusterResource clusterResources(String clusterName, Map<Integer, String> componentVersionMap, Long dtuicTenantId) {
        ClusterResource clusterResource = new ClusterResource();
        List<JSONObject> queue = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("usedCapacity","10");
        jsonObject.put("queueName","default");
        queue.add(jsonObject);
        clusterResource.setQueues(queue);
        return clusterResource;
    }


    @MockInvoke(targetClass = ResourceGroupService.class)
    public void batchInsert(List<ResourceQueueUsed> useds) {}

    @MockInvoke(targetClass = ResourceGroupService.class)
    public void clearByTimeInterval(Integer timeInterVal) {}
}

