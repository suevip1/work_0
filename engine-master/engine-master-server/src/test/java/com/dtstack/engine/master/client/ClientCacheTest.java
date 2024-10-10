package com.dtstack.engine.master.client;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.util.PublicUtil;
import org.junit.Test;

/**
 * @author yuebai
 * @date 2023/8/7
 */
public class ClientCacheTest {

    @Test
    public void getJobResource() {
        String s1 = "{\n" +
                "  \"securityConf\":{\n" +
                "  },\n" +
                "  \"cluster\":\"default\",\n" +
                "  \"hadoopConf\":{\n" +
                "    \"dfs.ha.automatic-failover.enabled\":\"true\"\n" +
                "  },\n" +
                "  \"otherConf\":{\n" +
                "    \"cluster\":\"default\",\n" +
                "    \"componentType\":\"0\",\n" +
                "    \"deployMode\":1,\n" +
                "    \"md5sum\":\"5ba7b1e7cbab5f0c0538f53e8b9c0988_3eda3de2e58aff04daa435c9da0c6384\",\n" +
                "    \"namespace\":\"default\",\n" +
                "    \"md5zip\":\"5ba7b1e7cbab5f0c0538f53e8b9c0988\",\n" +
                "    \"componentVersion\":\"112\",\n" +
                "    \"queue\":\"default\"\n" +
                "  },\n" +
                "  \"typeName\":\"yarn2-hdfs2-flink112\",\n" +
                "  \"commonConf\":{\n" +
                "    \"hadoop.proxy.enable\":\"false\",\n" +
                "    \"hadoopUserName\":\"root\",\n" +
                "    \"engine.zookeeper.url\":\"172.16.84.247:2181\"\n" +
                "  },\n" +
                "  \"flinkConf\":{\n" +
                "    \"state.checkpoints.num-retained\":\"11\",\n" +
                "    \"prometheusHost\":\"172.16.21.107\",\n" +
                "    \"metrics.reporter.promgateway.port\":\"9091\",\n" +
                "    \"yarnAccepterTaskNumber\":\"100\",\n" +
                "    \"restart-strategy.failure-rate.max-failures-per-interval\":\"2\",\n" +
                "    \"env.java.opts\":\"-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8\",\n" +
                "    \"flinkLibDir\":\"/opt/dtstack/DTFlinkX1.12/flink_lib\",\n" +
                "    \"metrics.reporter.promgateway.jobName\":\"112job\",\n" +
                "    \"flinkxDistDir\":\"/opt/dtstack/DTFlinkX1.12/syncplugin\",\n" +
                "    \"monitorAcceptedApp\":\"false\",\n" +
                "    \"state.savepoints.dir\":\"hdfs://ns1/dtInsight/tmp/savepoints\",\n" +
                "    \"high-availability.zookeeper.path.root\":\"/flink112\",\n" +
                "    \"checkpoint.retain.time\":\"604800\",\n" +
                "    \"clusterMode\":\"perjob\",\n" +
                "    \"metrics.reporter.promgateway.class\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\n" +
                "    \"high-availability.storageDir\":\"hdfs://ns1/dtInsight/tmp/ha\",\n" +
                "    \"remoteFlinkLibDir\":\"/opt/dtstack/DTFlinkX1.12/flink_lib\",\n" +
                "    \"metrics.reporter.promgateway.randomJobNameSuffix\":\"true\",\n" +
                "    \"yarn.application-attempts\":\"3\",\n" +
                "    \"pluginsDistDir\":\"/opt/dtstack/DTSchedulex/Engine/pluginLibs\",\n" +
                "    \"taskmanager.numberOfTaskSlots\":\"1\",\n" +
                "    \"restart-strategy.failure-rate.failure-rate-interval\":\"1 min\",\n" +
                "    \"akka.ask.timeout\":\"60 s\",\n" +
                "    \"pluginLoadMode\":\"shipfile\",\n" +
                "    \"taskmanager.memory.process.size\":\"2048M\",\n" +
                "    \"jobmanager.archive.fs.dir\":\"hdfs://ns1/dtInsight/tmp/completed-jobs\",\n" +
                "    \"restart-strategy.failure-rate.delay\":\"1s\",\n" +
                "    \"classloader.resolve-order\":\"child-first\",\n" +
                "    \"deployMode\":1,\n" +
                "    \"state.backend.incremental\":\"true\",\n" +
                "    \"jobmanager.memory.process.size\":\"1600m\",\n" +
                "    \"classloader.dtstack-cache\":\"true\",\n" +
                "    \"hadoopUserName\":\"root\",\n" +
                "    \"metrics.reporter.promgateway.deleteOnShutdown\":\"true\",\n" +
                "    \"high-availability.zookeeper.quorum\":\"172.16.21.107:2181,172.16.22.103:2181,172.16.23.238:2181\",\n" +
                "    \"yarn.application-attempt-failures-validity-interval\":\"3600000\",\n" +
                "    \"high-availability\":\"ZOOKEEPER\",\n" +
                "    \"restart-strategy\":\"none\",\n" +
                "    \"state.backend\":\"RocksDB\",\n" +
                "    \"akka.tcp.timeout\":\"60 s\",\n" +
                "    \"remoteFlinkxDistDir\":\"/opt/dtstack/DTFlinkX1.12/syncplugin\",\n" +
                "    \"prometheusPort\":\"9090\",\n" +
                "    \"metrics.reporter.promgateway.host\":\"172.16.21.107\",\n" +
                "    \"state.checkpoints.dir\":\"hdfs://ns1/dtInsight/tmp/checkpoints\"\n" +
                "  }\n" +
                "}\n";
        String s2 = "{\n" +
                "  \"securityConf\":{\n" +
                "  },\n" +
                "  \"cluster\":\"default\",\n" +
                "  \"hadoopConf\":{\n" +
                "    \"dfs.ha.automatic-failover.enabled\":\"true\"\n" +
                "  },\n" +
                "  \"otherConf\":{\n" +
                "    \"cluster\":\"default\",\n" +
                "    \"componentType\":\"0\",\n" +
                "    \"deployMode\":1,\n" +
                "    \"md5sum\":\"5ba7b1e7cbab5f0c0538f53e8b9c0988_3sadda3de2e58aff04daa435c9da0c6384\",\n" +
                "    \"namespace\":\"default\",\n" +
                "    \"md5zip\":\"5ba7b1e7cbab5f0c0538f53e8b9c0988\",\n" +
                "    \"componentVersion\":\"112\",\n" +
                "    \"queue\":\"default\"\n" +
                "  },\n" +
                "  \"typeName\":\"yarn2-hdfs2-flink112\",\n" +
                "  \"commonConf\":{\n" +
                "    \"hadoop.proxy.enable\":\"false\",\n" +
                "    \"hadoopUserName\":\"root\",\n" +
                "    \"engine.zookeeper.url\":\"172.16.84.247:2181\"\n" +
                "  },\n" +
                "  \"flinkConf\":{\n" +
                "    \"state.checkpoints.num-retained\":\"11\",\n" +
                "    \"prometheusHost\":\"172.16.21.107\",\n" +
                "    \"metrics.reporter.promgateway.port\":\"9091\",\n" +
                "    \"yarnAccepterTaskNumber\":\"100\",\n" +
                "    \"restart-strategy.failure-rate.max-failures-per-interval\":\"2\",\n" +
                "    \"env.java.opts\":\"-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8\",\n" +
                "    \"flinkLibDir\":\"/opt/dtstack/DTFlinkX1.12/flink_lib\",\n" +
                "    \"metrics.reporter.promgateway.jobName\":\"112job\",\n" +
                "    \"flinkxDistDir\":\"/opt/dtstack/DTFlinkX1.12/syncplugin\",\n" +
                "    \"monitorAcceptedApp\":\"false\",\n" +
                "    \"state.savepoints.dir\":\"hdfs://ns1/dtInsight/tmp/savepoints\",\n" +
                "    \"high-availability.zookeeper.path.root\":\"/flink112\",\n" +
                "    \"checkpoint.retain.time\":\"604800\",\n" +
                "    \"clusterMode\":\"perjob\",\n" +
                "    \"metrics.reporter.promgateway.class\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\n" +
                "    \"high-availability.storageDir\":\"hdfs://ns1/dtInsight/tmp/ha\",\n" +
                "    \"remoteFlinkLibDir\":\"/opt/dtstack/DTFlinkX1.12/flink_lib\",\n" +
                "    \"metrics.reporter.promgateway.randomJobNameSuffix\":\"true\",\n" +
                "    \"yarn.application-attempts\":\"3\",\n" +
                "    \"pluginsDistDir\":\"/opt/dtstack/DTSchedulex/Engine/pluginLibs\",\n" +
                "    \"taskmanager.numberOfTaskSlots\":\"1\",\n" +
                "    \"restart-strategy.failure-rate.failure-rate-interval\":\"1 min\",\n" +
                "    \"akka.ask.timeout\":\"60 s\",\n" +
                "    \"pluginLoadMode\":\"shipfile\",\n" +
                "    \"taskmanager.memory.process.size\":\"2048M\",\n" +
                "    \"jobmanager.archive.fs.dir\":\"hdfs://ns1/dtInsight/tmp/completed-jobs\",\n" +
                "    \"restart-strategy.failure-rate.delay\":\"1s\",\n" +
                "    \"classloader.resolve-order\":\"child-first\",\n" +
                "    \"deployMode\":1,\n" +
                "    \"state.backend.incremental\":\"true\",\n" +
                "    \"jobmanager.memory.process.size\":\"1600m\",\n" +
                "    \"classloader.dtstack-cache\":\"true\",\n" +
                "    \"hadoopUserName\":\"root\",\n" +
                "    \"metrics.reporter.promgateway.deleteOnShutdown\":\"true\",\n" +
                "    \"high-availability.zookeeper.quorum\":\"172.16.21.107:2181,172.16.22.103:2181,172.16.23.238:2181\",\n" +
                "    \"yarn.application-attempt-failures-validity-interval\":\"3600000\",\n" +
                "    \"high-availability\":\"ZOOKEEPER\",\n" +
                "    \"execution.checkpointing.externalized-checkpoint-retention\":\"RETAIN_ON_CANCELLATION\",\n" +
                "    \"restart-strategy\":\"none\",\n" +
                "    \"state.backend\":\"RocksDB\",\n" +
                "    \"akka.tcp.timeout\":\"60 s\",\n" +
                "    \"remoteFlinkxDistDir\":\"/opt/dtstack/DTFlinkX1.12/syncplugin\",\n" +
                "    \"prometheusPort\":\"9090\",\n" +
                "    \"metrics.reporter.promgateway.host\":\"172.16.21.107\",\n" +
                "    \"state.checkpoints.dir\":\"hdfs://ns1/dtInsight/tmp/checkpoints\"\n" +
                "  }\n" +
                "}\n";
        JSONObject json1 = JSONObject.parseObject(s1);
        JSONObject jsonT1 = JSONObject.parseObject(s1);
        JSONObject jsonT2 = JSONObject.parseObject(s2);
        JSONObject json2 = JSONObject.parseObject(s2);
        PublicUtil.removeRepeatJSON(json1, json2);
        System.out.println(json1.toJSONString());
        System.out.println(json2.toJSONString());
        System.out.println(jsonT1.toJSONString());
        System.out.println(jsonT2.toJSONString());
    }
}
