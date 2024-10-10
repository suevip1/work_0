package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class SqlServerPluginInfoAdapterTest extends DefaultPluginInfoAdapterTest {

    private SqlServerPluginInfoAdapter adapter = new SqlServerPluginInfoAdapter();

    @Test
    public void getSelfConfigWithTask() {
        pluginInfoContext = JSONObject.parseObject(contextJson, PluginInfoContext.class);
        adapter.setPluginInfoContext(pluginInfoContext);
        adapter.getSelfConfig(JSONObject.parseObject("{\"clusterName\":\"CDP_kerberos\",\"hadoopConf\":{\"yarn.log-aggregation.file-formats\":\"IFile,TFile\",\"hive.exec.reducers.bytes.per.reducer\":\"67108864\",\"hive.server2.webui.use.spnego\":\"true\",\"yarn.resourcemanager.zk-address\":\"cdp01:2181,cdp03:2181,cdp02:2181\",\"hive.service.metrics.file.frequency\":\"30000\",\"yarn.application.classpath\":\"$HADOOP_CLIENT_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*\",\"dfs.replication\":\"3\",\"hive.aux.jars.path\":\"file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hive/lib/hive-hbase-handler-3.1.3000.7.1.3.0-100.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-client.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-common.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-hadoop-compat.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-hadoop2-compat.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-protocol.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-server.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/lib/htrace-core.jar\",\"yarn.admin.acl\":\"yarn\",\"yarn.scheduler.increment-allocation-vcores\":\"1\",\"dfs.namenode.audit.log.async\":\"true\",\"hive.stats.fetch.column.stats\":\"true\",\"dfs.namenode.avoid.read.stale.datanode\":\"true\",\"hadoop.proxyuser.impala.hosts\":\"*\",\"hive.vectorized.adaptor.usage.mode\":\"chosen\",\"yarn.scheduler.maximum-allocation-vcores\":\"8\",\"dfs.qjournal.select-input-streams.timeout.ms\":\"20000\",\"hive.map.aggr\":\"true\",\"ipc.client.connection.maxidletime\":\"30000\",\"hive.smbjoin.cache.rows\":\"10000\",\"hadoop.proxyuser.httpfs.hosts\":\"*\",\"dfs.thrift.timeout\":\"60\",\"hive.server2.metrics.enabled\":\"true\",\"yarn.webapp.api-service.enable\":\"true\",\"hive.auto.convert.join\":\"true\",\"dfs.namenode.replication.min\":\"1\",\"dfs.permissions\":\"true\",\"hive.merge.mapfiles\":\"true\",\"dfs.namenode.acls.enabled\":\"true\",\"hive.exec.post.hooks\":\"org.apache.hadoop.hive.ql.hooks.HiveProtoLoggingHook\",\"yarn.resourcemanager.nm.liveness-monitor.interval-ms\":\"1000\",\"dfs.access.time.precision\":\"3600000\",\"hadoop.security.group.mapping\":\"org.apache.hadoop.security.ShellBasedUnixGroupsMapping\",\"dfs.web.authentication.kerberos.keytab\":\"hdfs.keytab\",\"hive.zookeeper.quorum\":\"cdp01,cdp03,cdp02\",\"hive.metastore.kerberos.principal\":\"hive/_HOST@DTSTACK.COM\",\"dfs.namenode.snapshot.capture.openfiles\":\"true\",\"dfs.http.policy\":\"HTTPS_ONLY\",\"yarn.resourcemanager.admin.address\":\"cdp01:8033\",\"dfs.namenode.safemode.min.datanodes\":\"1\",\"hive.mv.files.thread\":\"15\",\"dfs.datanode.kerberos.principal\":\"hdfs/_HOST@DTSTACK.COM\",\"hive.metastore.fshandler.threads\":\"15\",\"dfs.webhdfs.enabled\":\"true\",\"hadoop.proxyuser.knox.hosts\":\"*\",\"hadoop.proxyuser.hdfs.groups\":\"*\",\"dfs.client.use.datanode.hostname\":\"true\",\"hive.compute.query.using.stats\":\"true\",\"yarn.log-aggregation.retain-seconds\":\"604800\",\"dfs.namenode.avoid.write.stale.datanode\":\"true\",\"dfs.namenode.kerberos.principal\":\"hdfs/_HOST@DTSTACK.COM\",\"fs.trash.interval\":\"1440\",\"yarn.scheduler.minimum-allocation-vcores\":\"1\",\"hadoop.proxyuser.knox.groups\":\"*\",\"hadoop.proxyuser.hive.hosts\":\"*\",\"hive.vectorized.groupby.checkinterval\":\"4096\",\"hadoop.security.instrumentation.requires.admin\":\"false\",\"hive.server2.thrift.port\":\"10000\",\"yarn.resourcemanager.container.liveness-monitor.interval-ms\":\"600000\",\"hive.exec.failure.hooks\":\"org.apache.hadoop.hive.ql.hooks.HiveProtoLoggingHook\",\"yarn.scheduler.capacity.resource-calculator\":\"org.apache.hadoop.yarn.util.resource.DominantResourceCalculator\",\"hive.vectorized.use.checked.expressions\":\"true\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"dfs.namenode.safemode.threshold-pct\":\"0.999\",\"hadoop.proxyuser.yarn.hosts\":\"*\",\"hive.metastore.client.socket.timeout\":\"300\",\"hive.log.explain.output\":\"false\",\"yarn.resourcemanager.address\":\"cdp01:8032\",\"dfs.namenode.kerberos.principal.pattern\":\"*\",\"yarn.resourcemanager.scheduler.monitor.enable\":\"true\",\"dfs.namenode.replication.max-streams\":\"20\",\"hive.vectorized.execution.reduce.enabled\":\"true\",\"yarn.resourcemanager.resource-tracker.client.thread-count\":\"50\",\"yarn.resourcemanager.webapp.cross-origin.enabled\":\"true\",\"dfs.namenode.keytab.file\":\"hdfs.keytab\",\"ipc.client.idlethreshold\":\"8000\",\"yarn.resourcemanager.proxy-user-privileges.enabled\":\"true\",\"yarn.acl.enable\":\"true\",\"hive.vectorized.use.vectorized.input.format\":\"true\",\"dfs.namenode.replication.work.multiplier.per.iteration\":\"10\",\"hadoop.security.authorization\":\"true\",\"hive.optimize.index.filter\":\"true\",\"hive.metastore.warehouse.dir\":\"/warehouse/tablespace/managed/hive\",\"hive.server2.webui.host\":\"0.0.0.0\",\"yarn.am.liveness-monitor.expiry-interval-ms\":\"600000\",\"hive.cbo.enable\":\"true\",\"yarn.resourcemanager.client.thread-count\":\"50\",\"yarn.resourcemanager.placement-constraints.handler\":\"scheduler\",\"hadoop.security.authentication\":\"kerberos\",\"hadoop.proxyuser.hdfs.hosts\":\"*\",\"hive.optimize.reducededuplication.min.reducer\":\"4\",\"hadoop.proxyuser.hue.hosts\":\"*\",\"hive.metastore.uris\":\"thrift://cdp01:9083\",\"yarn.resourcemanager.zk-timeout-ms\":\"60000\",\"dfs.qjournal.finalize-segment.timeout.ms\":\"120000\",\"hive.server2.thrift.max.worker.threads\":\"500\",\"yarn.resourcemanager.max-completed-applications\":\"10000\",\"dfs.namenode.replication.max-streams-hard-limit\":\"40\",\"yarn.service.classpath\":\"$HADOOP_CLIENT_CONF_DIR\",\"dfs.namenode.accesstime.precision\":\"0\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.work-preserving-recovery.enabled\":\"true\",\"hive.exec.pre.hooks\":\"org.apache.hadoop.hive.ql.hooks.HiveProtoLoggingHook\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"dfs.image.transfer.timeout\":\"60000\",\"hadoop.proxyuser.hue.groups\":\"*\",\"hadoop.proxyuser.hive.groups\":\"*\",\"hive.cluster.delegation.token.store.class\":\"org.apache.hadoop.hive.thrift.MemoryTokenStore\",\"hive.zookeeper.client.port\":\"2181\",\"dfs.secondary.namenode.kerberos.internal.spnego.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"dfs.namenode.invalidate.work.pct.per.iteration\":\"0.32\",\"hadoop.ssl.client.conf\":\"ssl-client.xml\",\"yarn.resourcemanager.nodes.exclude-path\":\"/var/run/cloudera-scm-agent/process/80-yarn-RESOURCEMANAGER/nodes_exclude.txt\",\"dfs.qjournal.start-segment.timeout.ms\":\"20000\",\"yarn.resourcemanager.scheduler.client.thread-count\":\"50\",\"dfs.cluster.administrators\":\"hdfs\",\"dfs.https.port\":\"9871\",\"hive.async.log.enabled\":\"false\",\"dfs.encrypt.data.transfer.algorithm\":\"3des\",\"hive.merge.smallfiles.avgsize\":\"16777216\",\"dfs.datanode.hdfs-blocks-metadata.enabled\":\"true\",\"hive.exec.reducers.max\":\"1009\",\"hive.fetch.task.conversion.threshold\":\"1073741824\",\"hadoop.http.authentication.type\":\"kerberos\",\"hadoop.proxyuser.phoenix.groups\":\"*\",\"yarn.scheduler.minimum-allocation-mb\":\"1024\",\"hadoop.proxyuser.livy.groups\":\"*\",\"hive.auto.convert.join.noconditionaltask.size\":\"52428800\",\"hive.server2.logging.operation.enabled\":\"true\",\"dfs.https.address\":\"cdp01:9871\",\"dfs.permissions.superusergroup\":\"supergroup\",\"yarn.resourcemanager.nodes.include-path\":\"/var/run/cloudera-scm-agent/process/80-yarn-RESOURCEMANAGER/nodes_allow.txt\",\"hive.support.concurrency\":\"true\",\"yarn.resourcemanager.amliveliness-monitor.interval-ms\":\"1000\",\"yarn.scheduler.maximum-allocation-mb\":\"2904\",\"dfs.namenode.safemode.extension\":\"30000\",\"hive.driver.parallel.compilation\":\"true\",\"hadoop.rpc.protection\":\"privacy\",\"fs.permissions.umask-mode\":\"022\",\"hive.server2.webui.max.threads\":\"50\",\"spark.shuffle.service.enabled\":\"true\",\"dfs.thrift.threads.min\":\"10\",\"yarn.resourcemanager.webapp.spnego-principal\":\"HTTP/_HOST@DTSTACK.COM\",\"hive.optimize.reducededuplication\":\"true\",\"hadoop.http.authentication.kerberos.keytab\":\"hdfs.keytab\",\"dfs.namenode.maintenance.replication.min\":\"1\",\"hive.strict.checks.type.safety\":\"true\",\"dfs.namenode.hosts.provider.classname\":\"org.apache.hadoop.hdfs.server.blockmanagement.CombinedHostFileManager\",\"hive.exec.copyfile.maxsize\":\"33554432\",\"hive.vectorized.execution.enabled\":\"true\",\"hive.metastore.sasl.enabled\":\"true\",\"hadoop.security.auth_to_local\":\"DEFAULT\",\"hive.strict.checks.bucketing\":\"true\",\"yarn.cluster.scaling.recommendation.enable\":\"false\",\"hive.limit.pushdown.memory.usage\":\"0.04\",\"hive.strict.checks.no.partition.filter\":\"false\",\"yarn.resourcemanager.webapp.address\":\"cdp01:8088\",\"hive.server2.webui.spnego.principal\":\"HTTP/cdp01@DTSTACK.COM\",\"hive.merge.mapredfiles\":\"false\",\"hive.msck.repair.batch.size\":\"0\",\"hive.vectorized.use.vector.serde.deserialize\":\"false\",\"hive.server2.webui.spnego.keytab\":\"hive.keytab\",\"hadoop.proxyuser.HTTP.hosts\":\"*\",\"dfs.secondary.namenode.kerberos.principal\":\"hdfs/_HOST@DTSTACK.COM\",\"dfs.encrypt.data.transfer.cipher.suites\":\"AES/CTR/NoPadding\",\"hive.server2.session.check.interval\":\"900000\",\"hive.server2.idle.operation.timeout\":\"21600000\",\"hive.server2.authentication.kerberos.principal\":\"hive/_HOST@DTSTACK.COM\",\"hive.metastore.warehouse.external.dir\":\"/warehouse/tablespace/external/hive\",\"yarn.scheduler.increment-allocation-mb\":\"512\",\"dfs.https.enable\":\"true\",\"dfs.encrypt.data.transfer.cipher.key.bitlength\":\"256\",\"hive.metastore.execute.setugi\":\"true\",\"yarn.resourcemanager.zk-acl\":\"sasl:rm:cdrwa,sasl:yarn:cdrwa\",\"yarn.resourcemanager.webapp.spnego-keytab-file\":\"yarn.keytab\",\"net.topology.script.file.name\":\"/etc/hadoop/conf.cloudera.yarn/topology.py\",\"hadoop.ssl.keystores.factory.class\":\"org.apache.hadoop.security.ssl.FileBasedKeyStoresFactory\",\"hive.exec.input.listing.max.threads\":\"15\",\"hadoop.http.filter.initializers\":\"org.apache.hadoop.security.HttpCrossOriginFilterInitializer,org.apache.hadoop.security.authentication.server.ProxyUserAuthenticationFilterInitializer\",\"dfs.namenode.stale.datanode.interval\":\"30000\",\"dfs.qjournal.accept-recovery.timeout.ms\":\"120000\",\"hadoop.proxyuser.phoenix.hosts\":\"*\",\"hive.server2.webui.use.ssl\":\"false\",\"typeName\":\"yarn3-hdfs3-hadoop3\",\"yarn.resourcemanager.resource-tracker.address\":\"cdp01:8031\",\"dfs.namenode.ec.system.default.policy\":\"RS-6-3-1024k\",\"dfs.namenode.handler.count\":\"30\",\"dfs.namenode.decommission.blocks.per.interval\":\"500000\",\"dfs.image.transfer.bandwidthPerSec\":\"0\",\"dfs.qjournal.write-txns.timeout.ms\":\"20000\",\"hadoop.ssl.enabled\":\"true\",\"dfs.replication.max\":\"512\",\"hive.exec.query.redactor.hooks\":\"org.cloudera.hadoop.hive.ql.hooks.QueryRedactor\",\"dfs.namenode.name.dir\":\"file:///dfs/nn\",\"ssl.server.exclude.cipher.list\":\"^TLS_DHE.*$,^.*SHA$,^TLS_RSA_WITH.*$,^.*MD5.*$,^TLS_DH_.*$,^.*RC4.*$,^.*CCM.*$\",\"yarn.node-labels.enabled\":\"true\",\"hive.lock.query.string.max.length\":\"10000\",\"yarn.resourcemanager.admin.client.thread-count\":\"1\",\"dfs.block.access.token.enable\":\"true\",\"yarn.resourcemanager.scheduler.class\":\"org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler\",\"dfs.blocksize\":\"134217728\",\"hive.strict.checks.cartesian.product\":\"false\",\"hive.map.aggr.hash.percentmemory\":\"0.5\",\"ipc.client.connect.max.retries\":\"50\",\"dfs.namenode.write.stale.datanode.ratio\":\"0.5\",\"dfs.encrypt.data.transfer\":\"true\",\"hive.load.dynamic.partitions.thread\":\"15\",\"hive.service.metrics.file.location\":\"/var/log/hive/metrics-hiveserver2/metrics.log\",\"hadoop.proxyuser.oozie.hosts\":\"*\",\"dfs.data.transfer.protection\":\"privacy\",\"hive.driver.parallel.compilation.global.limit\":\"3\",\"md5zip\":\"b0d29f36a4e8d02faefc91289a99f3d0\",\"hadoop.proxyuser.oozie.groups\":\"*\",\"yarn.resourcemanager.scheduler.address\":\"cdp01:8030\",\"hive.vectorized.groupby.flush.percent\":\"0.1\",\"dfs.namenode.servicerpc-address\":\"cdp01:8022\",\"hive.optimize.sort.dynamic.partition\":\"false\",\"hive.metastore.dml.events\":\"true\",\"dfs.hosts\":\"/var/run/cloudera-scm-agent/process/97-hdfs-NAMENODE/dfs_all_hosts.txt\",\"hadoop.ssl.require.client.cert\":\"false\",\"mapred.reduce.tasks\":\"-1\",\"yarn.resourcemanager.keytab\":\"yarn.keytab\",\"dfs.thrift.threads.max\":\"20\",\"yarn.webapp.ui2.enable\":\"true\",\"yarn.log-aggregation-status.time-out.ms\":\"600000\",\"hive.blobstore.use.blobstore.as.scratchdir\":\"false\",\"hive.fetch.task.conversion\":\"more\",\"hive.server2.authentication\":\"kerberos\",\"fs.trash.checkpoint.interval\":\"60\",\"dfs.client.context\":\"14be5d9b90574718876b855f840bca06\",\"yarn.nm.liveness-monitor.expiry-interval-ms\":\"600000\",\"dfs.namenode.rpc-address\":\"cdp01:8020\",\"dfs.web.authentication.kerberos.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"hadoop.proxyuser.httpfs.groups\":\"*\",\"hive.strict.checks.orderby.no.limit\":\"false\",\"yarn.scheduler.configuration.store.class\":\"zk\",\"hive.metastore.connect.retries\":\"10\",\"dfs.https.server.keystore.resource\":\"ssl-server.xml\",\"yarn.service.framework.path\":\"/user/yarn/services/service-framework/7.1.1/service-dep.tar.gz\",\"hadoop.proxyuser.yarn.groups\":\"*\",\"yarn.resourcemanager.am.max-attempts\":\"2\",\"dfs.namenode.kerberos.internal.spnego.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"hive.query.redaction.rules\":\"/var/run/cloudera-scm-agent/process/85-hive-HIVESERVER2/redaction-rules.json\",\"yarn.log-aggregation.file-controller.IFile.class\":\"org.apache.hadoop.yarn.logaggregation.filecontroller.ifile.LogAggregationIndexedFileController\",\"hive.server2.thrift.min.worker.threads\":\"5\",\"hive.server2.webui.port\":\"10002\",\"hive.server2.authentication.kerberos.keytab\":\"hive.keytab\",\"fs.defaultFS\":\"hdfs://cdp01:8020\",\"hadoop.proxyuser.impala.groups\":\"*\",\"dfs.qjournal.new-epoch.timeout.ms\":\"120000\",\"hadoop.registry.zk.quorum\":\"cdp01:2181,cdp03:2181,cdp02:2181\",\"dfs.namenode.service.handler.count\":\"30\",\"dfs.namenode.fs-limits.max-component-length\":\"255\",\"hive.server2.idle.session.check.operation\":\"true\",\"hive.server2.logging.operation.log.location\":\"/var/log/hive/operation_logs\",\"hive.merge.size.per.task\":\"268435456\",\"yarn.webapp.filter-entity-list-by-user\":\"true\",\"yarn.log-aggregation.file-controller.TFile.class\":\"org.apache.hadoop.yarn.logaggregation.filecontroller.tfile.LogAggregationTFileController\",\"hue.kerberos.principal.shortname\":\"hue\",\"dfs.namenode.http-address\":\"cdp01:9870\",\"hadoop.ssl.server.conf\":\"ssl-server.xml\",\"dfs.qjournal.get-journal-state.timeout.ms\":\"120000\",\"yarn.resourcemanager.principal\":\"yarn/_HOST@DTSTACK.COM\",\"hadoop.caller.context.enabled\":\"true\",\"hadoop.http.authentication.kerberos.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"dfs.namenode.startup.delay.block.deletion.sec\":\"3600\",\"dfs.qjournal.prepare-recovery.timeout.ms\":\"120000\",\"dfs.namenode.decommission.max.concurrent.tracked.nodes\":\"100\",\"dfs.namenode.name.dir.restore\":\"true\",\"hadoop.proxyuser.livy.hosts\":\"*\",\"dfs.namenode.secondary.http-address\":\"cdp02:9868\",\"hadoop.http.logs.enabled\":\"true\",\"hive.optimize.bucketmapjoin.sortedmerge\":\"false\",\"yarn.resourcemanager.webapp.https.address\":\"cdp01:8090\",\"hive.server2.enable.doAs\":\"true\",\"hadoop.security.key.default.bitlength\":\"128\",\"hadoop.http.authentication.signature.secret.file\":\"/var/run/cloudera-scm-agent/process/386-hdfs-NAMENODE/http-auth-signature-secret\",\"hive.server2.idle.session.timeout\":\"86400000\",\"hadoop.proxyuser.HTTP.groups\":\"*\"},\"yarnConf\":{\"yarn.log-aggregation.file-formats\":\"IFile,TFile\",\"hive.exec.reducers.bytes.per.reducer\":\"67108864\",\"hive.server2.webui.use.spnego\":\"true\",\"yarn.resourcemanager.zk-address\":\"cdp01:2181,cdp03:2181,cdp02:2181\",\"hive.service.metrics.file.frequency\":\"30000\",\"yarn.application.classpath\":\"$HADOOP_CLIENT_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*\",\"dfs.replication\":\"3\",\"hive.aux.jars.path\":\"file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hive/lib/hive-hbase-handler-3.1.3000.7.1.3.0-100.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-client.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-common.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-hadoop-compat.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-hadoop2-compat.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-protocol.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/hbase-server.jar,file:///opt/cloudera/parcels/CDH-7.1.3-1.cdh7.1.3.p0.4992530/lib/hbase/lib/htrace-core.jar\",\"yarn.admin.acl\":\"yarn\",\"yarn.scheduler.increment-allocation-vcores\":\"1\",\"dfs.namenode.audit.log.async\":\"true\",\"hive.stats.fetch.column.stats\":\"true\",\"dfs.namenode.avoid.read.stale.datanode\":\"true\",\"hadoop.proxyuser.impala.hosts\":\"*\",\"hive.vectorized.adaptor.usage.mode\":\"chosen\",\"yarn.scheduler.maximum-allocation-vcores\":\"8\",\"dfs.qjournal.select-input-streams.timeout.ms\":\"20000\",\"hive.map.aggr\":\"true\",\"ipc.client.connection.maxidletime\":\"30000\",\"hive.smbjoin.cache.rows\":\"10000\",\"hadoop.proxyuser.httpfs.hosts\":\"*\",\"dfs.thrift.timeout\":\"60\",\"hive.server2.metrics.enabled\":\"true\",\"yarn.webapp.api-service.enable\":\"true\",\"hive.auto.convert.join\":\"true\",\"dfs.namenode.replication.min\":\"1\",\"dfs.permissions\":\"true\",\"hive.merge.mapfiles\":\"true\",\"dfs.namenode.acls.enabled\":\"true\",\"proxy\":\"{\\\"config\\\":{\\\"password\\\":\\\"123456\\\",\\\"user\\\":\\\"admin\\\",\\\"url\\\":\\\"https://cdp03:8443/gateway/cdp-proxy/yarn/ws/v1/cluster/scheduler\\\"},\\\"type\\\":\\\"KNOX\\\"}\",\"hive.exec.post.hooks\":\"org.apache.hadoop.hive.ql.hooks.HiveProtoLoggingHook\",\"yarn.resourcemanager.nm.liveness-monitor.interval-ms\":\"1000\",\"dfs.access.time.precision\":\"3600000\",\"hadoop.security.group.mapping\":\"org.apache.hadoop.security.ShellBasedUnixGroupsMapping\",\"dfs.web.authentication.kerberos.keytab\":\"hdfs.keytab\",\"hive.zookeeper.quorum\":\"cdp01,cdp03,cdp02\",\"hive.metastore.kerberos.principal\":\"hive/_HOST@DTSTACK.COM\",\"dfs.namenode.snapshot.capture.openfiles\":\"true\",\"dfs.http.policy\":\"HTTPS_ONLY\",\"yarn.resourcemanager.admin.address\":\"cdp01:8033\",\"dfs.namenode.safemode.min.datanodes\":\"1\",\"hive.mv.files.thread\":\"15\",\"dfs.datanode.kerberos.principal\":\"hdfs/_HOST@DTSTACK.COM\",\"hive.metastore.fshandler.threads\":\"15\",\"dfs.webhdfs.enabled\":\"true\",\"hadoop.proxyuser.knox.hosts\":\"*\",\"hadoop.proxyuser.hdfs.groups\":\"*\",\"dfs.client.use.datanode.hostname\":\"true\",\"hive.compute.query.using.stats\":\"true\",\"yarn.log-aggregation.retain-seconds\":\"604800\",\"dfs.namenode.avoid.write.stale.datanode\":\"true\",\"dfs.namenode.kerberos.principal\":\"hdfs/_HOST@DTSTACK.COM\",\"fs.trash.interval\":\"1440\",\"yarn.scheduler.minimum-allocation-vcores\":\"1\",\"hadoop.proxyuser.knox.groups\":\"*\",\"hadoop.proxyuser.hive.hosts\":\"*\",\"hive.vectorized.groupby.checkinterval\":\"4096\",\"hadoop.security.instrumentation.requires.admin\":\"false\",\"hive.server2.thrift.port\":\"10000\",\"yarn.resourcemanager.container.liveness-monitor.interval-ms\":\"600000\",\"hive.exec.failure.hooks\":\"org.apache.hadoop.hive.ql.hooks.HiveProtoLoggingHook\",\"yarn.scheduler.capacity.resource-calculator\":\"org.apache.hadoop.yarn.util.resource.DominantResourceCalculator\",\"hive.vectorized.use.checked.expressions\":\"true\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"dfs.namenode.safemode.threshold-pct\":\"0.999\",\"hadoop.proxyuser.yarn.hosts\":\"*\",\"hive.metastore.client.socket.timeout\":\"300\",\"hive.log.explain.output\":\"false\",\"yarn.resourcemanager.address\":\"cdp01:8032\",\"yarn.resourcemanager.scheduler.monitor.enable\":\"true\",\"dfs.namenode.replication.max-streams\":\"20\",\"hive.vectorized.execution.reduce.enabled\":\"true\",\"yarn.resourcemanager.resource-tracker.client.thread-count\":\"50\",\"yarn.resourcemanager.webapp.cross-origin.enabled\":\"true\",\"dfs.namenode.keytab.file\":\"hdfs.keytab\",\"ipc.client.idlethreshold\":\"8000\",\"yarn.resourcemanager.proxy-user-privileges.enabled\":\"true\",\"yarn.acl.enable\":\"true\",\"hive.vectorized.use.vectorized.input.format\":\"true\",\"dfs.namenode.replication.work.multiplier.per.iteration\":\"10\",\"hadoop.security.authorization\":\"true\",\"hive.optimize.index.filter\":\"true\",\"hive.metastore.warehouse.dir\":\"/warehouse/tablespace/managed/hive\",\"hive.server2.webui.host\":\"0.0.0.0\",\"yarn.am.liveness-monitor.expiry-interval-ms\":\"600000\",\"hive.cbo.enable\":\"true\",\"yarn.resourcemanager.client.thread-count\":\"50\",\"yarn.resourcemanager.placement-constraints.handler\":\"scheduler\",\"hadoop.security.authentication\":\"kerberos\",\"hadoop.proxyuser.hdfs.hosts\":\"*\",\"hive.optimize.reducededuplication.min.reducer\":\"4\",\"hadoop.proxyuser.hue.hosts\":\"*\",\"hive.metastore.uris\":\"thrift://cdp01:9083\",\"yarn.resourcemanager.zk-timeout-ms\":\"60000\",\"dfs.qjournal.finalize-segment.timeout.ms\":\"120000\",\"hive.server2.thrift.max.worker.threads\":\"500\",\"yarn.resourcemanager.max-completed-applications\":\"10000\",\"dfs.namenode.replication.max-streams-hard-limit\":\"40\",\"yarn.service.classpath\":\"$HADOOP_CLIENT_CONF_DIR\",\"dfs.namenode.accesstime.precision\":\"0\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.work-preserving-recovery.enabled\":\"true\",\"hive.exec.pre.hooks\":\"org.apache.hadoop.hive.ql.hooks.HiveProtoLoggingHook\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"dfs.image.transfer.timeout\":\"60000\",\"hadoop.proxyuser.hue.groups\":\"*\",\"hadoop.proxyuser.hive.groups\":\"*\",\"hive.cluster.delegation.token.store.class\":\"org.apache.hadoop.hive.thrift.MemoryTokenStore\",\"hive.zookeeper.client.port\":\"2181\",\"dfs.secondary.namenode.kerberos.internal.spnego.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"dfs.namenode.invalidate.work.pct.per.iteration\":\"0.32\",\"hadoop.ssl.client.conf\":\"ssl-client.xml\",\"yarn.resourcemanager.nodes.exclude-path\":\"/var/run/cloudera-scm-agent/process/80-yarn-RESOURCEMANAGER/nodes_exclude.txt\",\"dfs.qjournal.start-segment.timeout.ms\":\"20000\",\"yarn.resourcemanager.scheduler.client.thread-count\":\"50\",\"dfs.cluster.administrators\":\"hdfs\",\"dfs.https.port\":\"9871\",\"hive.async.log.enabled\":\"false\",\"dfs.encrypt.data.transfer.algorithm\":\"3des\",\"hive.merge.smallfiles.avgsize\":\"16777216\",\"dfs.datanode.hdfs-blocks-metadata.enabled\":\"true\",\"hive.exec.reducers.max\":\"1009\",\"hive.fetch.task.conversion.threshold\":\"1073741824\",\"hadoop.http.authentication.type\":\"kerberos\",\"hadoop.proxyuser.phoenix.groups\":\"*\",\"yarn.scheduler.minimum-allocation-mb\":\"1024\",\"hadoop.proxyuser.livy.groups\":\"*\",\"hive.auto.convert.join.noconditionaltask.size\":\"52428800\",\"hive.server2.logging.operation.enabled\":\"true\",\"dfs.https.address\":\"cdp01:9871\",\"dfs.permissions.superusergroup\":\"supergroup\",\"yarn.resourcemanager.nodes.include-path\":\"/var/run/cloudera-scm-agent/process/80-yarn-RESOURCEMANAGER/nodes_allow.txt\",\"hive.support.concurrency\":\"true\",\"yarn.resourcemanager.amliveliness-monitor.interval-ms\":\"1000\",\"yarn.scheduler.maximum-allocation-mb\":\"2904\",\"dfs.namenode.safemode.extension\":\"30000\",\"hive.driver.parallel.compilation\":\"true\",\"yarn.resourcemanager.principal.pattern\":\"*\",\"hadoop.rpc.protection\":\"privacy\",\"fs.permissions.umask-mode\":\"022\",\"hive.server2.webui.max.threads\":\"50\",\"spark.shuffle.service.enabled\":\"true\",\"dfs.thrift.threads.min\":\"10\",\"yarn.resourcemanager.webapp.spnego-principal\":\"HTTP/_HOST@DTSTACK.COM\",\"hive.optimize.reducededuplication\":\"true\",\"hadoop.http.authentication.kerberos.keytab\":\"hdfs.keytab\",\"dfs.namenode.maintenance.replication.min\":\"1\",\"hive.strict.checks.type.safety\":\"true\",\"dfs.namenode.hosts.provider.classname\":\"org.apache.hadoop.hdfs.server.blockmanagement.CombinedHostFileManager\",\"hive.exec.copyfile.maxsize\":\"33554432\",\"hive.vectorized.execution.enabled\":\"true\",\"hive.metastore.sasl.enabled\":\"true\",\"hadoop.security.auth_to_local\":\"DEFAULT\",\"hive.strict.checks.bucketing\":\"true\",\"yarn.cluster.scaling.recommendation.enable\":\"false\",\"hive.limit.pushdown.memory.usage\":\"0.04\",\"hive.strict.checks.no.partition.filter\":\"false\",\"yarn.resourcemanager.webapp.address\":\"cdp01:8088\",\"hive.server2.webui.spnego.principal\":\"HTTP/cdp01@DTSTACK.COM\",\"hive.merge.mapredfiles\":\"false\",\"hive.msck.repair.batch.size\":\"0\",\"hive.vectorized.use.vector.serde.deserialize\":\"false\",\"hive.server2.webui.spnego.keytab\":\"hive.keytab\",\"hadoop.proxyuser.HTTP.hosts\":\"*\",\"dfs.secondary.namenode.kerberos.principal\":\"hdfs/_HOST@DTSTACK.COM\",\"dfs.encrypt.data.transfer.cipher.suites\":\"AES/CTR/NoPadding\",\"hive.server2.session.check.interval\":\"900000\",\"hive.server2.idle.operation.timeout\":\"21600000\",\"hive.server2.authentication.kerberos.principal\":\"hive/_HOST@DTSTACK.COM\",\"hive.metastore.warehouse.external.dir\":\"/warehouse/tablespace/external/hive\",\"yarn.scheduler.increment-allocation-mb\":\"512\",\"dfs.https.enable\":\"true\",\"dfs.encrypt.data.transfer.cipher.key.bitlength\":\"256\",\"hive.metastore.execute.setugi\":\"true\",\"yarn.resourcemanager.zk-acl\":\"sasl:rm:cdrwa,sasl:yarn:cdrwa\",\"yarn.resourcemanager.webapp.spnego-keytab-file\":\"yarn.keytab\",\"net.topology.script.file.name\":\"/etc/hadoop/conf.cloudera.yarn/topology.py\",\"hadoop.ssl.keystores.factory.class\":\"org.apache.hadoop.security.ssl.FileBasedKeyStoresFactory\",\"hive.exec.input.listing.max.threads\":\"15\",\"hadoop.http.filter.initializers\":\"org.apache.hadoop.security.HttpCrossOriginFilterInitializer,org.apache.hadoop.security.authentication.server.ProxyUserAuthenticationFilterInitializer\",\"dfs.namenode.stale.datanode.interval\":\"30000\",\"dfs.qjournal.accept-recovery.timeout.ms\":\"120000\",\"hadoop.proxyuser.phoenix.hosts\":\"*\",\"hive.server2.webui.use.ssl\":\"false\",\"yarn.resourcemanager.resource-tracker.address\":\"cdp01:8031\",\"dfs.namenode.ec.system.default.policy\":\"RS-6-3-1024k\",\"dfs.namenode.handler.count\":\"30\",\"dfs.namenode.decommission.blocks.per.interval\":\"500000\",\"dfs.image.transfer.bandwidthPerSec\":\"0\",\"dfs.qjournal.write-txns.timeout.ms\":\"20000\",\"hadoop.ssl.enabled\":\"true\",\"dfs.replication.max\":\"512\",\"hive.exec.query.redactor.hooks\":\"org.cloudera.hadoop.hive.ql.hooks.QueryRedactor\",\"dfs.namenode.name.dir\":\"file:///dfs/nn\",\"ssl.server.exclude.cipher.list\":\"^TLS_DHE.*$,^.*SHA$,^TLS_RSA_WITH.*$,^.*MD5.*$,^TLS_DH_.*$,^.*RC4.*$,^.*CCM.*$\",\"yarn.node-labels.enabled\":\"true\",\"hive.lock.query.string.max.length\":\"10000\",\"yarn.resourcemanager.admin.client.thread-count\":\"1\",\"dfs.block.access.token.enable\":\"true\",\"yarn.resourcemanager.scheduler.class\":\"org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler\",\"dfs.blocksize\":\"134217728\",\"hive.strict.checks.cartesian.product\":\"false\",\"hive.map.aggr.hash.percentmemory\":\"0.5\",\"ipc.client.connect.max.retries\":\"50\",\"dfs.namenode.write.stale.datanode.ratio\":\"0.5\",\"dfs.encrypt.data.transfer\":\"true\",\"hive.load.dynamic.partitions.thread\":\"15\",\"hive.service.metrics.file.location\":\"/var/log/hive/metrics-hiveserver2/metrics.log\",\"hadoop.proxyuser.oozie.hosts\":\"*\",\"dfs.data.transfer.protection\":\"privacy\",\"hive.driver.parallel.compilation.global.limit\":\"3\",\"hadoop.proxyuser.oozie.groups\":\"*\",\"yarn.resourcemanager.scheduler.address\":\"cdp01:8030\",\"hive.vectorized.groupby.flush.percent\":\"0.1\",\"dfs.namenode.servicerpc-address\":\"cdp01:8022\",\"hive.optimize.sort.dynamic.partition\":\"false\",\"hive.metastore.dml.events\":\"true\",\"dfs.hosts\":\"/var/run/cloudera-scm-agent/process/97-hdfs-NAMENODE/dfs_all_hosts.txt\",\"hadoop.ssl.require.client.cert\":\"false\",\"mapred.reduce.tasks\":\"-1\",\"yarn.resourcemanager.keytab\":\"yarn.keytab\",\"dfs.thrift.threads.max\":\"20\",\"yarn.webapp.ui2.enable\":\"true\",\"yarn.log-aggregation-status.time-out.ms\":\"600000\",\"hive.blobstore.use.blobstore.as.scratchdir\":\"false\",\"hive.fetch.task.conversion\":\"more\",\"hive.server2.authentication\":\"kerberos\",\"fs.trash.checkpoint.interval\":\"60\",\"yarn.nm.liveness-monitor.expiry-interval-ms\":\"600000\",\"dfs.namenode.rpc-address\":\"cdp01:8020\",\"dfs.web.authentication.kerberos.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"hadoop.proxyuser.httpfs.groups\":\"*\",\"hive.strict.checks.orderby.no.limit\":\"false\",\"yarn.scheduler.configuration.store.class\":\"zk\",\"hive.metastore.connect.retries\":\"10\",\"dfs.https.server.keystore.resource\":\"ssl-server.xml\",\"yarn.service.framework.path\":\"/user/yarn/services/service-framework/7.1.1/service-dep.tar.gz\",\"hadoop.proxyuser.yarn.groups\":\"*\",\"yarn.resourcemanager.am.max-attempts\":\"2\",\"dfs.namenode.kerberos.internal.spnego.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"hive.query.redaction.rules\":\"/var/run/cloudera-scm-agent/process/85-hive-HIVESERVER2/redaction-rules.json\",\"yarn.log-aggregation.file-controller.IFile.class\":\"org.apache.hadoop.yarn.logaggregation.filecontroller.ifile.LogAggregationIndexedFileController\",\"hive.server2.thrift.min.worker.threads\":\"5\",\"hive.server2.webui.port\":\"10002\",\"hive.server2.authentication.kerberos.keytab\":\"hive.keytab\",\"fs.defaultFS\":\"hdfs://cdp01:8020\",\"hadoop.proxyuser.impala.groups\":\"*\",\"dfs.qjournal.new-epoch.timeout.ms\":\"120000\",\"hadoop.registry.zk.quorum\":\"cdp01:2181,cdp03:2181,cdp02:2181\",\"dfs.namenode.service.handler.count\":\"30\",\"dfs.namenode.fs-limits.max-component-length\":\"255\",\"hive.server2.idle.session.check.operation\":\"true\",\"hive.server2.logging.operation.log.location\":\"/var/log/hive/operation_logs\",\"hive.merge.size.per.task\":\"268435456\",\"yarn.webapp.filter-entity-list-by-user\":\"true\",\"yarn.log-aggregation.file-controller.TFile.class\":\"org.apache.hadoop.yarn.logaggregation.filecontroller.tfile.LogAggregationTFileController\",\"hue.kerberos.principal.shortname\":\"hue\",\"dfs.namenode.http-address\":\"cdp01:9870\",\"hadoop.ssl.server.conf\":\"ssl-server.xml\",\"dfs.qjournal.get-journal-state.timeout.ms\":\"120000\",\"yarn.resourcemanager.principal\":\"yarn/_HOST@DTSTACK.COM\",\"hadoop.caller.context.enabled\":\"true\",\"hadoop.http.authentication.kerberos.principal\":\"HTTP/_HOST@DTSTACK.COM\",\"dfs.namenode.startup.delay.block.deletion.sec\":\"3600\",\"dfs.qjournal.prepare-recovery.timeout.ms\":\"120000\",\"dfs.namenode.decommission.max.concurrent.tracked.nodes\":\"100\",\"dfs.namenode.name.dir.restore\":\"true\",\"hadoop.proxyuser.livy.hosts\":\"*\",\"dfs.namenode.secondary.http-address\":\"cdp02:9868\",\"hadoop.http.logs.enabled\":\"true\",\"hive.optimize.bucketmapjoin.sortedmerge\":\"false\",\"yarn.resourcemanager.webapp.https.address\":\"cdp01:8090\",\"hive.server2.enable.doAs\":\"true\",\"hadoop.security.key.default.bitlength\":\"128\",\"hadoop.http.authentication.signature.secret.file\":\"/var/run/cloudera-scm-agent/process/386-hdfs-NAMENODE/http-auth-signature-secret\",\"hive.server2.idle.session.timeout\":\"86400000\",\"hadoop.proxyuser.HTTP.groups\":\"*\"},\"sftpConf\":{\"maxWaitMillis\":\"3600000\",\"minIdle\":\"16\",\"auth\":\"1\",\"isUsePool\":\"true\",\"timeout\":\"0\",\"path\":\"/data/sftp\",\"password\":\"dt@sz.com\",\"maxIdle\":\"16\",\"port\":\"22\",\"maxTotal\":\"16\",\"host\":\"172.16.82.142\",\"fileTimeout\":\"300000\",\"username\":\"root\"},\"flinkConf\":{\"deploymode\":[\"perjob\",\"session\"],\"perjob\":{\"state.checkpoints.num-retained\":\"11\",\"prometheusHost\":\"172.16.23.25\",\"metrics.reporter.promgateway.port\":\"9091\",\"yarnAccepterTaskNumber\":\"3\",\"restart-strategy.failure-rate.max-failures-per-interval\":\"2\",\"env.java.opts\":\"-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8\",\"flinkLibDir\":\"/data/112_flinkplugin/lib/\",\"metrics.reporter.promgateway.jobName\":\"112job\",\"flinkxDistDir\":\"/opt/dtstack/flinkxPlugin/flinkx-dist\",\"monitorAcceptedApp\":\"false\",\"state.savepoints.dir\":\"hdfs://cdp01:8020/dtInsight/flink112/savepoints\",\"high-availability.zookeeper.path.root\":\"/flink112\",\"checkpoint.retain.time\":\"7\",\"clusterMode\":\"perjob\",\"metrics.reporter.promgateway.class\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"high-availability.storageDir\":\"hdfs://cdp01:8020/dtInsight/flink112/ha\",\"remoteFlinkLibDir\":\"/data/112_flinkplugin/lib/\",\"metrics.reporter.promgateway.randomJobNameSuffix\":\"true\",\"yarn.application-attempts\":\"3\",\"pluginsDistDir\":\"/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs\",\"taskmanager.numberOfTaskSlots\":\"2\",\"restart-strategy.failure-rate.failure-rate-interval\":\"1 min\",\"akka.ask.timeout\":\"60 s\",\"pluginLoadMode\":\"shipfile\",\"taskmanager.memory.process.size\":\"2048m\",\"jobmanager.archive.fs.dir\":\"hdfs://cdp01:8020/dtInsight/flink112/completed-jobs\",\"restart-strategy.failure-rate.delay\":\"1s\",\"classloader.resolve-order\":\"child-first\",\"state.backend.incremental\":\"true\",\"jobmanager.memory.process.size\":\"1600m\",\"classloader.dtstack-cache\":\"true\",\"metrics.reporter.promgateway.deleteOnShutdown\":\"true\",\"high-availability.zookeeper.quorum\":\"172.16.23.25\",\"yarn.application-attempt-failures-validity-interval\":\"3600000\",\"high-availability\":\"ZOOKEEPER\",\"execution.checkpointing.externalized-checkpoint-retention\":\"RETAIN_ON_CANCELLATION\",\"restart-strategy\":\"failurerate\",\"state.backend\":\"RocksDB\",\"akka.tcp.timeout\":\"60 s\",\"env.java.opts.taskmanager\":\"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9751\",\"remoteFlinkxDistDir\":\"/opt/dtstack/flinkxPlugin/flinkx-dist\",\"prometheusPort\":\"9090\",\"metrics.reporter.promgateway.host\":\"172.16.23.25\",\"state.checkpoints.dir\":\"hdfs://cdp01:8020/dtInsight/flink112/checkpoints\"},\"session\":{\"state.checkpoints.num-retained\":\"11\",\"flinkSessionName\":\"dev_flink_session_142\",\"prometheusHost\":\"172.16.23.25\",\"metrics.reporter.promgateway.port\":\"9091\",\"yarnAccepterTaskNumber\":\"3\",\"env.java.opts\":\"-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8\",\"flinkLibDir\":\"/data/112_flinkplugin/lib/\",\"metrics.reporter.promgateway.jobName\":\"112job\",\"flinkxDistDir\":\"/opt/dtstack/flinkxPlugin/flinkx-dist\",\"monitorAcceptedApp\":\"false\",\"state.savepoints.dir\":\"hdfs://cdp01:8020/dtInsight/flink112/savepoints\",\"high-availability.zookeeper.path.root\":\"/flink112\",\"clusterMode\":\"session\",\"metrics.reporter.promgateway.class\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"high-availability.storageDir\":\"hdfs://cdp01:8020/dtInsight/flink112/ha\",\"sessionStartAuto\":\"true\",\"remoteFlinkLibDir\":\"/data/112_flinkplugin/lib/\",\"metrics.reporter.promgateway.randomJobNameSuffix\":\"true\",\"yarn.application-attempts\":\"3\",\"pluginsDistDir\":\"/home/admin/app/dt-center-engine/pluginLibs\",\"taskmanager.numberOfTaskSlots\":\"2\",\"sessionRetryNum\":\"5\",\"yarn.application.queue\":\"\",\"localFlinkxDistArchive\":\"\",\"pluginLoadMode\":\"shipfile\",\"taskmanager.memory.process.size\":\"2048m\",\"slotmanager.number-of-slots.debug.max\":\"2\",\"jobmanager.archive.fs.dir\":\"hdfs://cdp01:8020/dtInsight/flink112/completed-jobs\",\"remoteFlinkxDistArchive\":\"\",\"classloader.resolve-order\":\"parent-first\",\"checkSubmitJobGraphInterval\":\"60\",\"state.backend.incremental\":\"true\",\"jobmanager.memory.process.size\":\"1600m\",\"classloader.dtstack-cache\":\"true\",\"metrics.reporter.promgateway.deleteOnShutdown\":\"true\",\"slotmanager.number-of-slots.max\":\"10\",\"high-availability.zookeeper.quorum\":\"\",\"yarn.application-attempt-failures-validity-interval\":\"3600000\",\"high-availability\":\"NONE\",\"execution.checkpointing.externalized-checkpoint-retention\":\"RETAIN_ON_CANCELLATION\",\"state.backend\":\"RocksDB\",\"remoteFlinkxDistDir\":\"/opt/dtstack/flinkxPlugin/flinkx-dist\",\"prometheusPort\":\"9090\",\"metrics.reporter.promgateway.host\":\"172.16.23.25\",\"state.checkpoints.dir\":\"hdfs://cdp01:8020/dtInsight/flink112/checkpoints\"},\"typeName\":\"yarn3-hdfs3-flink112\"},\"hiveServerConf\":{\"password\":\"\",\"maxJobPoolSize\":\"\",\"minJobPoolSize\":\"\",\"jdbcUrl\":\"jdbc:hive2://cdp02:10000/%s;principal=hive/cdp02@DTSTACK.COM\",\"serviceName\":\"hive_default\",\"username\":\"\"}}"), false);
        adapter.getSelfConfName();
        adapter.getEComponentType();
        adapter.afterProcessPluginInfo(new JSONObject());
    }

}