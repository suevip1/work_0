package com.dtstack.engine.master.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Strings;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-25 20:32
 */
public class PreCheckSyncDTOTest extends TestCase {
    @Test
    public void testPreCheckJson2Obj() {
        Map<String, Object> actionParam = new HashMap<>();
        actionParam.put("preCheck", "{\"reader\":{\"sqlText\":\"select * from test_reader\"},\"writer\":{\"sqlText\":\"select * from test_writer\"}}");
        String preCheckStr = (String) actionParam.get("preCheck");
        PreCheckSyncDTO preCheckSyncDTO = JSONObject.parseObject(preCheckStr, PreCheckSyncDTO.class);
        System.out.println(preCheckSyncDTO);
    }

    @Test
    public void testJobJson2Obj() {
        Map<String, Object> actionParam = new HashMap<>();
        actionParam.put("job", "{\"job\":{\"content\":[{\"reader\":{\"parameter\":{\"password\":\"Password!.\",\"customSql\":\"\",\"startLocation\":\"\",\"dtCenterSourceId\":24805,\"increColumn\":\"\",\"column\":[{\"name\":\"id\",\"isPart\":false,\"type\":\"INT\",\"key\":\"id\"}],\"connection\":[{\"sourceId\":6975,\"schema\":\"batch_502\",\"password\":\"Password!.\",\"jdbcUrl\":[\"jdbc:mysql://172.16.23.194:3306/batch_502\"],\"type\":1,\"table\":[\"test\"],\"username\":\"root\"}],\"sourceIds\":[6975],\"username\":\"root\"},\"name\":\"mysqlreader\"},\"writer\":{\"parameter\":{\"postSql\":[],\"password\":\"Password!.\",\"dtCenterSourceId\":24805,\"session\":[],\"column\":[{\"name\":\"id\",\"isPart\":false,\"type\":\"INT\",\"key\":\"id\"}],\"connection\":[{\"schema\":\"batch_502\",\"jdbcUrl\":\"jdbc:mysql://172.16.23.194:3306/batch_502\",\"table\":[\"you\"]}],\"writeMode\":\"insert\",\"sourceIds\":[6975],\"username\":\"root\",\"preSql\":[]},\"name\":\"mysqlwriter\"}}],\"setting\":{\"restore\":{\"maxRowNumForCheckpoint\":0,\"isRestore\":false,\"restoreColumnName\":\"\",\"restoreColumnIndex\":0},\"errorLimit\":{\"record\":100},\"speed\":{\"bytes\":0,\"channel\":1}}}}");
        String jobJson = (String) actionParam.get("job");
        JSONObject jsonObject = JSONObject.parseObject(jobJson);
        System.out.println(jsonObject);
    }

    @Test
    public void testEvalJson() {
        String jsonStr = "{\n" +
                "  \"job\": {\n" +
                "    \"content\": [\n" +
                "      {\n" +
                "        \"reader\": {\n" +
                "          \"parameter\": {\n" +
                "            \"path\": \"hdfs://ns1/dtInsight/hive/warehouse/xingran.db/mutong\",\n" +
                "            \"column\": [\n" +
                "              {\n" +
                "                \"name\": \"id\",\n" +
                "                \"index\": 0,\n" +
                "                \"isPart\": false,\n" +
                "                \"type\": \"int\",\n" +
                "                \"key\": \"id\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"defaultFS\": \"hdfs://ns1\",\n" +
                "            \"fieldDelimiter\": \"\\u0001\",\n" +
                "            \"encoding\": \"utf-8\",\n" +
                "            \"fileType\": \"text\"\n" +
                "          },\n" +
                "          \"name\": \"hdfsreader\"\n" +
                "        },\n" +
                "        \"writer\": {\n" +
                "          \"parameter\": {\n" +
                "            \"fileName\": \"\",\n" +
                "            \"dtCenterSourceId\": 24329,\n" +
                "            \"column\": [\n" +
                "              {\n" +
                "                \"name\": \"id\",\n" +
                "                \"index\": 0,\n" +
                "                \"isPart\": false,\n" +
                "                \"type\": \"string\",\n" +
                "                \"key\": \"id\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"writeMode\": \"overwrite\",\n" +
                "            \"encoding\": \"utf-8\",\n" +
                "            \"fullColumnName\": [\n" +
                "              \"id\",\n" +
                "              \"name\"\n" +
                "            ],\n" +
                "            \"path\": \"hdfs://ns1/dtInsight/hive/warehouse/anmo_test.db/dsddd\",\n" +
                "            \"hadoopConfig\": {\n" +
                "              \"hive.exec.reducers.bytes.per.reducer\": \"64000000\",\n" +
                "              \"yarn.resourcemanager.zk-address\": \"172.16.85.215:2181,172.16.85.248:2181,172.16.85.253:2181\",\n" +
                "              \"dfs.replication\": \"3\",\n" +
                "              \"yarn.admin.acl\": \"*\",\n" +
                "              \"dfs.ha.fencing.ssh.private-key-files\": \"~/.ssh/id_rsa\",\n" +
                "              \"yarn.app.mapreduce.am.job.committer.cancel-timeout\": \"60000\",\n" +
                "              \"hive.exec.max.dynamic.partitions.pernode\": \"100\",\n" +
                "              \"dfs.ha.namenodes.ns1\": \"nn1,nn2\",\n" +
                "              \"yarn.resourcemanager.address.rm1\": \"172.16.85.215:8032\",\n" +
                "              \"dfs.namenode.avoid.read.stale.datanode\": \"FALSE\",\n" +
                "              \"dfs.journalnode.rpc-address\": \"0.0.0.0:8485\",\n" +
                "              \"yarn.resourcemanager.address.rm2\": \"172.16.85.248:8032\",\n" +
                "              \"yarn.scheduler.maximum-allocation-vcores\": \"4\",\n" +
                "              \"dfs.namenode.rpc-address.ns1.nn2\": \"172.16.85.248:9000\",\n" +
                "              \"hive.map.aggr\": \"true\",\n" +
                "              \"ipc.client.connection.maxidletime\": \"10000\",\n" +
                "              \"dfs.namenode.rpc-address.ns1.nn1\": \"172.16.85.215:9000\",\n" +
                "              \"yarn.resourcemanager.cluster-id\": \"yarn-rm-cluster\",\n" +
                "              \"hive.merge.mapfile\": \"true\",\n" +
                "              \"ha.health-monitor.sleep-after-disconnect.ms\": \"1000\",\n" +
                "              \"yarn.nodemanager.aux-services\": \"mapreduce_shuffle\",\n" +
                "              \"hadoop.util.hash.type\": \"murmur\",\n" +
                "              \"yarn.webapp.api-service.enable\": \"false\",\n" +
                "              \"dfs.hosts.exclude\": \"null\",\n" +
                "              \"dfs.namenode.replication.min\": \"1\",\n" +
                "              \"hive.server2.support.dynamic.service.discovery\": \"true\",\n" +
                "              \"yarn.nodemanager.container-manager.thread-count\": \"20\",\n" +
                "              \"dfs.datanode.directoryscan.threads\": \"1\",\n" +
                "              \"version\": \"2.7.6\",\n" +
                "              \"dfs.namenode.fs-limits.min-block-size\": \"1048576\",\n" +
                "              \"dfs.datanode.directoryscan.interval\": \"21600\",\n" +
                "              \"net.topology.script.number.args\": \"100\",\n" +
                "              \"hadoop.http.authentication.token.validity\": \"36000\",\n" +
                "              \"ha.failover-controller.graceful-fence.rpc-timeout.ms\": \"5000\",\n" +
                "              \"yarn.resourcemanager.nm.liveness-monitor.interval-ms\": \"1000\",\n" +
                "              \"dfs.namenode.datanode.registration.ip-hostname-check\": \"false\",\n" +
                "              \"hive.zookeeper.quorum\": \"172.16.85.215:2181,172.16.85.248:2181,172.16.85.253:2181\",\n" +
                "              \"yarn.nodemanager.localizer.cache.cleanup.interval-ms\": \"3600000\",\n" +
                "              \"dfs.client.file-block-storage-locations.num-threads\": \"10\",\n" +
                "              \"yarn.resourcemanager.scheduler.address.rm1\": \"172.16.85.215:8030\",\n" +
                "              \"yarn.resourcemanager.scheduler.address.rm2\": \"172.16.85.248:8030\",\n" +
                "              \"yarn.nodemanager.delete.debug-delay-sec\": \"600\",\n" +
                "              \"dfs.webhdfs.enabled\": \"true\",\n" +
                "              \"yarn.log-aggregation.retain-seconds\": \"604800\",\n" +
                "              \"dfs.namenode.avoid.write.stale.datanode\": \"FALSE\",\n" +
                "              \"fs.trash.interval\": \"4320\",\n" +
                "              \"yarn.scheduler.minimum-allocation-vcores\": \"1\",\n" +
                "              \"dfs.namenode.num.extra.edits.retained\": \"1000000\",\n" +
                "              \"ipc.client.connect.max.retries.on.timeouts\": \"45\",\n" +
                "              \"dfs.datanode.data.dir\": \"file:/data/hadoop/hdfs/data\",\n" +
                "              \"spark.executor.memory\": \"456340275B\",\n" +
                "              \"yarn.resourcemanager.resource-tracker.address.rm1\": \"172.16.85.215:8031\",\n" +
                "              \"yarn.resourcemanager.resource-tracker.address.rm2\": \"172.16.85.248:8031\",\n" +
                "              \"ha.health-monitor.connect-retry-interval.ms\": \"1000\",\n" +
                "              \"hive.server2.thrift.port\": \"10004\",\n" +
                "              \"yarn.nodemanager.env-whitelist\": \"JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,YARN_HOME\",\n" +
                "              \"yarn.resourcemanager.container.liveness-monitor.interval-ms\": \"600000\",\n" +
                "              \"dfs.client-write-packet-size\": \"65536\",\n" +
                "              \"dfs.journalnode.edits.dir\": \"/data/hadoop/hdfs/journal\",\n" +
                "              \"dfs.client.block.write.retries\": \"3\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.cgroups.hierarchy\": \"/hadoop-yarn\",\n" +
                "              \"ha.failover-controller.graceful-fence.connection.retries\": \"1\",\n" +
                "              \"yarn.resourcemanager.recovery.enabled\": \"true\",\n" +
                "              \"dfs.namenode.safemode.threshold-pct\": \"0.999f\",\n" +
                "              \"yarn.nodemanager.disk-health-checker.enable\": \"true\",\n" +
                "              \"yarn.nodemanager.disk-health-checker.interval-ms\": \"120000\",\n" +
                "              \"hive.metastore.client.socket.timeout\": \"600s\",\n" +
                "              \"yarn.resourcemanager.admin.address.rm1\": \"172.16.85.215:8033\",\n" +
                "              \"yarn.log.server.url\": \"http://172.16.85.253:19888/jobhistory/logs/\",\n" +
                "              \"yarn.resourcemanager.admin.address.rm2\": \"172.16.85.248:8033\",\n" +
                "              \"dfs.datanode.dns.nameserver\": \"default\",\n" +
                "              \"javax.jdo.option.ConnectionDriverName\": \"com.mysql.jdbc.Driver\",\n" +
                "              \"yarn.resourcemanager.resource-tracker.client.thread-count\": \"50\",\n" +
                "              \"dfs.namenode.edits.journal-plugin.qjournal\": \"org.apache.hadoop.hdfs.qjournal.client.QuorumJournalManager\",\n" +
                "              \"yarn.nodemanager.delete.thread-count\": \"4\",\n" +
                "              \"dfs.nameservices\": \"ns1\",\n" +
                "              \"versionName\": \"Apache Hadoop 2.x\",\n" +
                "              \"dfs.safemode.threshold.pct\": \"0.5\",\n" +
                "              \"ipc.client.idlethreshold\": \"4000\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.cgroups.mount-path\": \"/sys/fs/cgroup\",\n" +
                "              \"yarn.acl.enable\": \"FALSE\",\n" +
                "              \"dfs.client.failover.connection.retries.on.timeouts\": \"0\",\n" +
                "              \"hadoop.http.authentication.simple.anonymous.allowed\": \"true\",\n" +
                "              \"hadoop.security.authorization\": \"true\",\n" +
                "              \"hive.metastore.warehouse.dir\": \"/dtInsight/hive/warehouse\",\n" +
                "              \"hive.server2.webui.host\": \"172.16.85.248\",\n" +
                "              \"yarn.am.liveness-monitor.expiry-interval-ms\": \"600000\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.resources-handler.class\": \"org.apache.hadoop.yarn.server.nodemanager.util.CgroupsLCEResourcesHandler\",\n" +
                "              \"yarn.resourcemanager.client.thread-count\": \"50\",\n" +
                "              \"spark.yarn.driver.memoryOverhead\": \"102000000\",\n" +
                "              \"dfs.image.compression.codec\": \"org.apache.hadoop.io.compress.DefaultCodec\",\n" +
                "              \"hadoop.security.authentication\": \"simple\",\n" +
                "              \"hive.metastore.uris\": \"thrift://172.16.85.248:9083\",\n" +
                "              \"hive.exec.dynamic.partition.mode\": \"nonstrict\",\n" +
                "              \"yarn.resourcemanager.max-completed-applications\": \"1000\",\n" +
                "              \"yarn.resourcemanager.ha.automatic-failover.enabled\": \"true\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.cgroups.mount\": \"false\",\n" +
                "              \"yarn.log-aggregation-enable\": \"true\",\n" +
                "              \"hadoop.proxyuser.admin.hosts\": \"*\",\n" +
                "              \"yarn.resourcemanager.store.class\": \"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\n" +
                "              \"yarn.app.mapreduce.am.job.task.listener.thread-count\": \"30\",\n" +
                "              \"hive.cluster.delegation.token.store.class\": \"org.apache.hadoop.hive.thrift.MemoryTokenStore\",\n" +
                "              \"dfs.namenode.support.allow.format\": \"true\",\n" +
                "              \"yarn.nodemanager.log.retain-seconds\": \"10800\",\n" +
                "              \"yarn.resourcemanager.ha.rm-ids\": \"rm1,rm2\",\n" +
                "              \"dfs.stream-buffer-size\": \"4096\",\n" +
                "              \"hadoop.proxyuser.admin.groups\": \"*\",\n" +
                "              \"yarn.nodemanager.container-executor.class\": \"org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor\",\n" +
                "              \"yarn.resourcemanager.scheduler.client.thread-count\": \"50\",\n" +
                "              \"dfs.bytes-per-checksum\": \"512\",\n" +
                "              \"dfs.datanode.max.transfer.threads\": \"8192\",\n" +
                "              \"ipc.maximum.data.length\": \"134217728\",\n" +
                "              \"yarn.app.mapreduce.am.job.client.port-range\": \"50100-50200\",\n" +
                "              \"ha.failover-controller.new-active.rpc-timeout.ms\": \"60000\",\n" +
                "              \"hive.merge.smallfiles.avgsize\": \"16000000\",\n" +
                "              \"hive.exec.reducers.max\": \"1099\",\n" +
                "              \"hive.fetch.task.conversion.threshold\": \"32000000\",\n" +
                "              \"hadoop.http.authentication.type\": \"simple\",\n" +
                "              \"yarn.scheduler.minimum-allocation-mb\": \"512\",\n" +
                "              \"hive.auto.convert.join.noconditionaltask.size\": \"20000000\",\n" +
                "              \"dfs.permissions.superusergroup\": \"supergroup\",\n" +
                "              \"io.seqfile.compress.blocksize\": \"1000000\",\n" +
                "              \"dfs.namenode.fs-limits.max-directory-items\": \"1048576\",\n" +
                "              \"yarn.resourcemanager.amliveliness-monitor.interval-ms\": \"1000\",\n" +
                "              \"yarn.nodemanager.pmem-check-enabled\": \"true\",\n" +
                "              \"yarn.nodemanager.remote-app-log-dir\": \"/tmp/logs\",\n" +
                "              \"dfs.blockreport.initialDelay\": \"0\",\n" +
                "              \"spark.executor.cores\": \"4\",\n" +
                "              \"yarn.scheduler.maximum-allocation-mb\": \"12288\",\n" +
                "              \"dfs.namenode.safemode.extension\": \"30000\",\n" +
                "              \"yarn.nodemanager.vmem-check-enabled\": \"false\",\n" +
                "              \"yarn.nodemanager.resource.percentage-physical-cpu-limit\": \"80\",\n" +
                "              \"fs.permissions.umask-mode\": \"022\",\n" +
                "              \"hive.reloadable.aux.jars.path\": \"/dtInsight/hive/udf\",\n" +
                "              \"ha.health-monitor.rpc-timeout.ms\": \"45000\",\n" +
                "              \"hive.server2.webui.max.threads\": \"50\",\n" +
                "              \"hive.exec.dynamic.partition\": \"true\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users\": \"true\",\n" +
                "              \"dfs.datanode.http.address\": \"0.0.0.0:60075\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.nonsecure-mode.local-user\": \"admin\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.group\": \"admin\",\n" +
                "              \"dfs.namenode.fs-limits.max-blocks-per-file\": \"1048576\",\n" +
                "              \"yarn.client.failover-proxy-provider\": \"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\n" +
                "              \"dfs.client.block.write.replace-datanode-on-failure.enable\": \"TRUE\",\n" +
                "              \"yarn.nodemanager.remote-app-log-dir-suffix\": \"logs\",\n" +
                "              \"net.topology.node.switch.mapping.impl\": \"org.apache.hadoop.net.ScriptBasedMapping\",\n" +
                "              \"hive.merge.mapredfiles\": \"false\",\n" +
                "              \"hive.mapjoin.localtask.max.memory.usage\": \"0.9\",\n" +
                "              \"fs.df.interval\": \"60000\",\n" +
                "              \"spark.yarn.executor.memoryOverhead\": \"76000000\",\n" +
                "              \"yarn.nodemanager.resource.count-logical-processors-as-cores\": \"true\",\n" +
                "              \"ha.zookeeper.parent-znode\": \"/hadoop-ha\",\n" +
                "              \"hive.exec.max.dynamic.partitions\": \"1000\",\n" +
                "              \"yarn.nodemanager.resource.cpu-vcores\": \"12\",\n" +
                "              \"hive.server2.session.check.interval\": \"60000\",\n" +
                "              \"hive.server2.idle.operation.timeout\": \"6h\",\n" +
                "              \"yarn.log-aggregation.retain-check-interval-seconds\": \"604800\",\n" +
                "              \"dfs.https.enable\": \"FALSE\",\n" +
                "              \"dfs.permissions.enabled\": \"TRUE\",\n" +
                "              \"dfs.blockreport.split.threshold\": \"500000\",\n" +
                "              \"dfs.datanode.balance.bandwidthPerSec\": \"1048576\",\n" +
                "              \"ha.zookeeper.quorum\": \"172.16.85.215:2181,172.16.85.248:2181,172.16.85.253:2181\",\n" +
                "              \"hadoop.http.filter.initializers\": \"org.apache.hadoop.http.lib.StaticUserWebFilter\",\n" +
                "              \"ipc.server.read.threadpool.size\": \"1\",\n" +
                "              \"dfs.namenode.stale.datanode.interval\": \"60000\",\n" +
                "              \"yarn.resourcemanager.webapp.address.rm2\": \"172.16.85.248:18088\",\n" +
                "              \"yarn.resourcemanager.webapp.address.rm1\": \"172.16.85.215:18088\",\n" +
                "              \"yarn.app.mapreduce.client-am.ipc.max-retries\": \"3\",\n" +
                "              \"dfs.default.chunk.view.size\": \"32768\",\n" +
                "              \"typeName\": \"yarn2-hdfs2-hadoop2\",\n" +
                "              \"dfs.client.failover.proxy.provider.ns1\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\n" +
                "              \"yarn.nodemanager.linux-container-executor.cgroups.strict-resource-usage\": \"false\",\n" +
                "              \"hive.merge.sparkfiles\": \"false\",\n" +
                "              \"yarn.resourcemanager.am.max-retries\": \"1\",\n" +
                "              \"dfs.namenode.handler.count\": \"64\",\n" +
                "              \"dfs.replication.max\": \"512\",\n" +
                "              \"io.bytes.per.checksum\": \"512\",\n" +
                "              \"dfs.namenode.name.dir\": \"file:/data/hadoop/hdfs/name\",\n" +
                "              \"yarn.app.mapreduce.client.max-retries\": \"3\",\n" +
                "              \"yarn.nodemanager.resource.memory-mb\": \"24576\",\n" +
                "              \"yarn.nodemanager.disk-health-checker.min-healthy-disks\": \"0.25\",\n" +
                "              \"dfs.datanode.failed.volumes.tolerated\": \"0\",\n" +
                "              \"ipc.client.kill.max\": \"10\",\n" +
                "              \"ipc.server.listen.queue.size\": \"128\",\n" +
                "              \"yarn.nodemanager.localizer.cache.target-size-mb\": \"10240\",\n" +
                "              \"yarn.resourcemanager.admin.client.thread-count\": \"1\",\n" +
                "              \"yarn.resourcemanager.scheduler.class\": \"org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler\",\n" +
                "              \"dfs.blocksize\": \"134217728\",\n" +
                "              \"storeType\": 4,\n" +
                "              \"dfs.datanode.address\": \"0.0.0.0:50010\",\n" +
                "              \"security.job.submission.protocol.acl\": \"admin\",\n" +
                "              \"hive.map.aggr.hash.percentmemory\": \"0.5\",\n" +
                "              \"ha.failover-controller.cli-check.rpc-timeout.ms\": \"20000\",\n" +
                "              \"ipc.client.connect.max.retries\": \"10\",\n" +
                "              \"ha.zookeeper.acl\": \"world:anyone:rwcda\",\n" +
                "              \"dfs.namenode.write.stale.datanode.ratio\": \"0.5f\",\n" +
                "              \"hive.metastore.server.min.threads\": \"200\",\n" +
                "              \"hadoop.tmp.dir\": \"/data/hadoop_admin\",\n" +
                "              \"dfs.datanode.handler.count\": \"10\",\n" +
                "              \"md5zip\": \"41966b4f5c8ca5b33a49e411dcb12839\",\n" +
                "              \"dfs.client.failover.max.attempts\": \"15\",\n" +
                "              \"yarn.resourcemanager.zk-state-store.address\": \"172.16.85.215:2181,172.16.85.248:2181,172.16.85.253:2181\",\n" +
                "              \"dfs.hosts\": \"null\",\n" +
                "              \"mapred.reduce.tasks\": \"-1\",\n" +
                "              \"yarn.resourcemanager.ha.automatic-failover.zk-base-path\": \"/yarn-leader-election\",\n" +
                "              \"hive.fetch.task.conversion\": \"minimal\",\n" +
                "              \"yarn.resourcemanager.ha.enabled\": \"true\",\n" +
                "              \"yarn.app.mapreduce.am.scheduler.heartbeat.interval-ms\": \"1000\",\n" +
                "              \"fs.trash.checkpoint.interval\": \"0\",\n" +
                "              \"dfs.journalnode.http-address\": \"0.0.0.0:18480\",\n" +
                "              \"yarn.nm.liveness-monitor.expiry-interval-ms\": \"600000\",\n" +
                "              \"dfs.datanode.fsdataset.volume.choosing.policy\": \"org.apache.hadoop.hdfs.server.datanode.fsdataset.AvailableSpaceVolumeChoosingPolicy\",\n" +
                "              \"ha.health-monitor.check-interval.ms\": \"1000\",\n" +
                "              \"hive.server2.async.exec.threads\": \"200\",\n" +
                "              \"spark.driver.memory\": \"966367641B\",\n" +
                "              \"dfs.blockreport.intervalMsec\": \"21600000\",\n" +
                "              \"hive.metastore.schema.verification\": \"false\",\n" +
                "              \"hive.metastore.connect.retries\": \"10\",\n" +
                "              \"mapreduce.job.hdfs-servers\": \"${fs.defaultFS}\",\n" +
                "              \"javax.jdo.option.ConnectionPassword\": \"DT@Stack#123\",\n" +
                "              \"io.native.lib.available\": \"TRUE\",\n" +
                "              \"dfs.image.compress\": \"FALSE\",\n" +
                "              \"yarn.nodemanager.webapp.address\": \"0.0.0.0:18042\",\n" +
                "              \"yarn.nodemanager.aux-services.mapreduce_shuffle.class\": \"org.apache.hadoop.mapred.ShuffleHandler\",\n" +
                "              \"dfs.datanode.dns.interface\": \"default\",\n" +
                "              \"hive.mapjoin.smalltable.filesize\": \"25000000L\",\n" +
                "              \"dfs.client.failover.connection.retries\": \"0\",\n" +
                "              \"hive.server2.webui.port\": \"10002\",\n" +
                "              \"hive.server2.thrift.min.worker.threads\": \"300\",\n" +
                "              \"fs.defaultFS\": \"hdfs://ns1\",\n" +
                "              \"yarn.nodemanager.disk-health-checker.max-disk-utilization-per-disk-percentage\": \"90\",\n" +
                "              \"dfs.namenode.fs-limits.max-component-length\": \"0\",\n" +
                "              \"dfs.ha.fencing.methods\": \"sshfence\",\n" +
                "              \"hive.server2.idle.session.check.operation\": \"true\",\n" +
                "              \"dfs.datanode.du.reserved\": \"20971520\",\n" +
                "              \"javax.jdo.option.ConnectionURL\": \"jdbc:mysql://172.16.85.248:3306/metastore?createDatabaseIfNotExist=true&useSSL=false&autoReconnect=true\",\n" +
                "              \"sftpConf\": {\n" +
                "                \"maxWaitMillis\": \"3600000\",\n" +
                "                \"a\": \"c\",\n" +
                "                \"minIdle\": \"16\",\n" +
                "                \"auth\": \"1\",\n" +
                "                \"isUsePool\": \"false\",\n" +
                "                \"timeout\": \"0\",\n" +
                "                \"path\": \"/data/sftp\",\n" +
                "                \"password\": \"dt@sz.com\",\n" +
                "                \"maxIdle\": \"16\",\n" +
                "                \"port\": \"22\",\n" +
                "                \"maxTotal\": \"16\",\n" +
                "                \"host\": \"172.16.100.251\",\n" +
                "                \"fileTimeout\": \"300000\",\n" +
                "                \"username\": \"root\"\n" +
                "              },\n" +
                "              \"dfs.datanode.ipc.address\": \"0.0.0.0:50020\",\n" +
                "              \"hive.merge.size.per.task\": \"256000000\",\n" +
                "              \"dfs.client.block.write.replace-datanode-on-failure.policy\": \"DEFAULT\",\n" +
                "              \"yarn.nodemanager.disk-health-checker.min-free-space-per-disk-mb\": \"10240\",\n" +
                "              \"dfs.namenode.shared.edits.dir\": \"qjournal://172.16.85.215:8485;172.16.85.248:8485;172.16.85.253:8485/namenode-ha-data\",\n" +
                "              \"javax.jdo.option.ConnectionUserName\": \"drpeco\",\n" +
                "              \"hive.mapjoin.followby.gby.localtask.max.memory.usage\": \"0.55\",\n" +
                "              \"dfs.namenode.name.dir.restore\": \"FALSE\",\n" +
                "              \"dfs.heartbeat.interval\": \"3\",\n" +
                "              \"ha.zookeeper.session-timeout.ms\": \"5000\",\n" +
                "              \"hive.server2.zookeeper.namespace\": \"hiveserver2\",\n" +
                "              \"hive.server2.enable.doAs\": \"false\",\n" +
                "              \"dfs.namenode.http-address.ns1.nn2\": \"172.16.85.248:60070\",\n" +
                "              \"dfs.namenode.http-address.ns1.nn1\": \"172.16.85.215:60070\",\n" +
                "              \"yarn.nodemanager.vmem-pmem-ratio\": \"4.1\",\n" +
                "              \"hive.exec.scratchdir\": \"/dtInsight/hive/warehouse\",\n" +
                "              \"hadoop.http.authentication.signature.secret.file\": \"/data/hadoop_base/etc/hadoop/hadoop-http-auth-signature-secret\",\n" +
                "              \"datanucleus.schema.autoCreateAll\": \"true\",\n" +
                "              \"hive.metastore.server.max.threads\": \"100000\",\n" +
                "              \"yarn.nodemanager.log-aggregation.compression-type\": \"none\",\n" +
                "              \"hive.server2.idle.session.timeout\": \"3600000\",\n" +
                "              \"dfs.ha.automatic-failover.enabled\": \"true\"\n" +
                "            },\n" +
                "            \"defaultFS\": \"hdfs://ns1\",\n" +
                "            \"connection\": [\n" +
                "              {\n" +
                "                \"jdbcUrl\": \"jdbc:hive2://172.16.85.248:10004/anmo_test\",\n" +
                "                \"table\": [\n" +
                "                  \"dsddd\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"table\": \"dsddd\",\n" +
                "            \"fileType\": \"parquet\",\n" +
                "            \"sourceIds\": [\n" +
                "              6475\n" +
                "            ],\n" +
                "            \"username\": \"root\",\n" +
                "            \"fullColumnType\": [\n" +
                "              \"string\",\n" +
                "              \"string\"\n" +
                "            ]\n" +
                "          },\n" +
                "          \"name\": \"hdfswriter\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"setting\": {\n" +
                "      \"restore\": {\n" +
                "        \"maxRowNumForCheckpoint\": 0,\n" +
                "        \"isRestore\": false,\n" +
                "        \"restoreColumnName\": \"\",\n" +
                "        \"restoreColumnIndex\": 0\n" +
                "      },\n" +
                "      \"errorLimit\": {\n" +
                "        \"record\": 10\n" +
                "      },\n" +
                "      \"speed\": {\n" +
                "        \"bytes\": 0,\n" +
                "        \"channel\": 1\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        String readerSourceId = Objects.toString(JSONPath.eval(jsonObject, "$.job.content[0].reader.parameter.dtCenterSourceId"), StringUtils.EMPTY);
        String writerSourceId = Objects.toString(JSONPath.eval(jsonObject, "$.job.content[0].writer.parameter.dtCenterSourceId"), StringUtils.EMPTY);
        System.out.println(readerSourceId);
        System.out.println(writerSourceId);
    }
}