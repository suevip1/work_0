package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.SparkThriftServerDTO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.DataSourceService;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.datasource.EditConsoleParam;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2023/1/13
 */
@Component
public class SparkThriftServerMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkThriftServerMonitor.class);

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EnginePluginsOperator workerOperator;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private ComponentService componentService;


    /**
     * 监控spark.thrift.server 启动在不同的节点情况
     * jdbc url 为zookeeper链接 zk变动 url才会变更
     * spark client init才拉起spark thrift server
     */
    @EngineCron
    @Scheduled(cron = "${spark.thrift.server.monitor.interval: 0 0 0/1 * * ? } ")
    public void sparkThriftServerInfoMonitor() {
        LOGGER.info("spark thrift server monitor start");
        Thread.currentThread().setName("SparkThriftServerMonitorThread");
        List<Cluster> clusters = clusterDao.listAll();
        if (CollectionUtils.isEmpty(clusters)) {
            return;
        }
        for (Cluster cluster : clusters) {
            try {
                monitorTrigger(cluster);
            } catch (Exception e) {
                LOGGER.error("get cluster {} spark thrift server info error", cluster.getClusterName(), e);
            }
        }
        LOGGER.info("spark thrift server monitor end");
    }

    public void monitorTrigger(Cluster cluster) throws Exception {
        List<ClusterTenant> clusterTenants = clusterTenantDao.listByClusterId(cluster.getId());
        if (CollectionUtils.isEmpty(clusterTenants)) {
            return;
        }
        //当前集群没有配置Spark ThriftServer
        com.dtstack.engine.api.domain.Component sparkThriftComponent = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SPARK_THRIFT.getTypeCode(), null);
        if (null != sparkThriftComponent) {
            return;
        }
        com.dtstack.engine.api.domain.Component sparkComponent = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SPARK.getTypeCode(), null);
        if (null == sparkComponent) {
            return;
        }
        //获取spark 在线信息
        JobIdentifier jobIdentifier = new JobIdentifier(null, null, null);
        //同一cluster 只获取一次
        JSONObject pluginInfo = pluginInfoManager.buildTaskPluginInfo(null, null, EScheduleJobType.SPARK_SQL.getType(), clusterTenants.get(0).getDtUicTenantId(),
                ScheduleEngineType.Spark.getEngineName(), null, EDeployMode.PERJOB.getType(), null, null);
        jobIdentifier.setPluginInfo(pluginInfo.toJSONString());
        jobIdentifier.setEngineType(EngineType.Spark.name());
        SparkThriftServerDTO thriftServerUrl = workerOperator.getThriftServerUrl(jobIdentifier);
        if (StringUtils.isBlank(thriftServerUrl.getJdbcUrl())) {
            LOGGER.error("get cluster {} spark thrift server info empty", cluster.getClusterName());
        }
        String lastMonitorUrl = redisTemplate.opsForValue().get(GlobalConst.SPARK_THRIFT_SERVER_MONITOR_KEY + cluster.getClusterName());
        if (StringUtils.equals(lastMonitorUrl, thriftServerUrl.getJdbcUrl())) {
            return;
        }
        Set<Long> tenantIds = clusterTenants.stream()
                .map(ClusterTenant::getDtUicTenantId)
                .collect(Collectors.toSet());
        com.dtstack.engine.api.domain.Component component = new com.dtstack.engine.api.domain.Component();
        component.setComponentTypeCode(EComponentType.SPARK_THRIFT.getTypeCode());
        component.setHadoopVersion("2.x");
        Map<String, Object> thriftServer = new HashMap<>();
        thriftServer.put(GlobalConst.JDBC_URL, thriftServerUrl.getJdbcUrl());
        EditConsoleParam editConsoleParam = dataSourceService.getEditConsoleParam(cluster.getId(), EComponentType.SPARK_THRIFT.getTypeCode(), tenantIds, component, thriftServer);
        editConsoleParam.setClusterName(cluster.getClusterName());
        editConsoleParam.setType(DataSourceType.SPARKTHRIFT2_1.getVal());
        LOGGER.info("get cluster {} spark thrift server url {}", cluster.getClusterName(), thriftServerUrl.getJdbcUrl());
        dataSourceAPIClient.editConsoleDs(editConsoleParam);
        redisTemplate.opsForValue().set(GlobalConst.SPARK_THRIFT_SERVER_MONITOR_KEY + cluster.getClusterName(), thriftServerUrl.getJdbcUrl());
    }


}
