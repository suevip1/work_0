package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.loader.cache.client.yarn.YarnClientProxy;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IYarn;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDTO;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.JobGraphTriggerDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.mapstruct.PlugInStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.JobGraphTrigger;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserTenantsVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICUserListResult;
import com.dtstack.rpc.annotation.RpcNodeSign;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-05-26 17:09
 */
public class ConsoleServiceMock extends BaseMock {

    @MockInvoke(targetClass = ClusterService.class)
    public JSONObject getYarnInfo(Long clusterId) {
        return new JSONObject();
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    List<String> listStopRecordByJobIds(@Param("jobIds") List<String> jobIds) {
        return new ArrayList<>();
    }


    @MockInvoke(targetClass = UIcUserTenantRelApiClient.class)
    public ApiResponse<List<Long>> findTenantByUserIdNoLimit(@com.dtstack.sdk.core.feign.Param("userId")Long userId) {
        ApiResponse<List<Long>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Lists.newArrayList(1L));
        return apiResponse;
     }

    @MockInvoke(targetClass = JobGraphTriggerDao.class)
    public JobGraphTrigger getStartTriggerTime() {
        return null;
    }

    @MockInvoke(targetClass = ZkService.class)
    public List<String> getAliveBrokersChildren() {
        return Arrays.asList("127.0.0.1");
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByName(String jobName) {
        ScheduleJob job = new ScheduleJob();
        job.setJobId("jobId");
        job.setJobName(jobName);
        return job;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        if (Objects.equals(jobId, "j1")) {
            return mockOneEngineJobCache(jobId);
        }
        return mockOneEngineJobCache(jobId);
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<EngineJobCache> findByJobResourceNoTenant(String jobResource, List<Long> tenantIds) {
        return Collections.emptyList();
    }


    @NotNull
    private EngineJobCache mockOneEngineJobCache(String jobId) {
        EngineJobCache jobCache = mockOneEngineJobCache(jobId, "j1", EJobCacheStage.DB.getStage(), "127.0.0.1");
        jobCache.setJobInfo(mockFlinkJobInfo());
        jobCache.setGmtCreate(new Date());
        return jobCache;
    }

    @NotNull
    private EngineJobCache mockOneEngineJobCache(String jobId, String jobResource, Integer stage, String nodeAddress) {
        EngineJobCache jobCache = new EngineJobCache();
        jobCache.setJobId(jobId);
        jobCache.setJobInfo(mockFlinkJobInfo());
        jobCache.setNodeAddress(nodeAddress);
        jobCache.setGmtCreate(new Date());
        jobCache.setJobResource(jobResource);
        jobCache.setStage(stage);
        return jobCache;
    }


    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<String> listNames(@Param("jobName") String jobName) {
        return Lists.newArrayList(jobName);
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<String> getJobResources() {
        return Lists.newArrayList("flink_data_security_default_batch_FlinkYarnSession2");
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<Map<String, Object>> groupByJobResourceFilterByCluster(String nodeAddress, String clusterName, Long tenantId) {
        List<Map<String, Object>> groupResult = new ArrayList<>();
        Map<String, Object> innerResult = new HashMap<>();
        innerResult.put("jobResource", "j1");
        innerResult.put("stage", EJobCacheStage.PRIORITY.getStage());
        innerResult.put("generateTime", new Date().getTime());
        innerResult.put("jobSize", 1);
        groupResult.add(innerResult);
        return groupResult;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    Long countByJobResource(String jobResource, Integer stage, String nodeAddress) {
        return 1L;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<EngineJobCache> listByJobResource(String jobResource, Integer stage, String nodeAddress, Integer start, Integer pageSize) {
        EngineJobCache jobCache = mockOneEngineJobCache("jobId", jobResource, stage, nodeAddress);
        return Lists.newArrayList(jobCache);
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    Long minPriorityByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return 0L;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<EngineJobCache> listByStage(Long startId, String nodeAddress, Integer stage, String jobResource) {
        if (startId < 1) {
            EngineJobCache jobCache = mockOneEngineJobCache("j1", jobResource, stage, nodeAddress);
            jobCache.setId(1L);
            return Lists.newArrayList(jobCache);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    Integer deleteByJobIds(List<String> jobIds) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateJobStatusByJobIds(List<String> jobIds, Integer status) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> getRdosJobByJobIds(List<String> jobIds) {
        return jobIds.stream().map(jobId -> {
            ScheduleJob t = new ScheduleJob();
            t.setJobId(jobId);
            t.setDtuicTenantId(-1L);
            return t;
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = TenantService.class)
    public Map<Long, TenantDeletedVO> listAllTenantByDtUicTenantIds(Collection<Long> ids) {
        Map<Long, TenantDeletedVO> map = new HashMap<>();
        ids.stream().forEach(id -> {
            TenantDeletedVO vo = new TenantDeletedVO();
            vo.setTenantId(id);
            map.put(id, vo);
        });
        return map;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public void updateJobStatus(String jobId, Integer status) {
        return;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public GroupPriorityQueue getGroupPriorityQueue(String jobResource) {
        GroupPriorityQueue groupPriorityQueue = GroupPriorityQueue.builder();
        groupPriorityQueue.setJobResource(jobResource);
        return groupPriorityQueue;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public boolean addGroupPriorityQueue(String jobResource, JobClient jobClient, boolean judgeBlock, boolean insert) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    List<String> listByJobIds(List<String> jobIds) {
        return Lists.newArrayList("a");
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    Long insert(ScheduleJobOperatorRecord engineJobStopRecord) {
        return 1L;
    }


    @MockInvoke(targetClass = UicTenantApiClient.class)
    ApiResponse<UICTenantVO> findTenantById(Long tenantId) {
        ApiResponse<UICTenantVO> resp = new ApiResponse<>();
        UICTenantVO uicTenantVO = new UICTenantVO();
        uicTenantVO.setTenantId(tenantId);
        uicTenantVO.setTenantName("tenantName");
        resp.setData(uicTenantVO);
        return resp;
    }

    @MockInvoke(targetClass = UIcUserTenantRelApiClient.class)
    ApiResponse<List<UserTenantsVO>> findTenantByUserId(Long userId) {
        UserTenantsVO t = new UserTenantsVO();
        t.setId(userId);
        ArrayList<UserTenantsVO> result = Lists.newArrayList(t);

        ApiResponse<List<UserTenantsVO>> rep = new ApiResponse<>();
        rep.setData(result);
        return rep;
    }

    public static String mockFlinkJobInfo() {
        return "{\"appType\":1,\"clientType\":0,\"computeType\":1,\"engineType\":\"flink\",\"exeArgs\":\"-jobid runJob_run_sync_task_spark_spark_1653636262278_20220527152422 -job %7B%22job%22%3A%7B%22content%22%3A%5B%7B%22reader%22%3A%7B%22parameter%22%3A%7B%22path%22%3A%22hdfs%3A%2F%2Fns1%2FdtInsight%2Fhive%2Fwarehouse%2Fanquan_test00.db%2Fabcd%22%2C%22dtCenterSourceId%22%3A1777%2C%22hadoopConfig%22%3A%7B%22hive.exec.reducers.bytes.per.reducer%22%3A%2264000000%22%2C%22yarn.resourcemanager.zk-address%22%3A%22172.16.84.199%3A2181%2C172.16.84.227%3A2181%2C172.16.84.34%3A2181%22%2C%22yarn.application.classpath%22%3A%22%2Fdata%2Fhadoop_base%2Fetc%2Fhadoop%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fcommon%2Flib%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fcommon%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fhdfs%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fhdfs%2Flib%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fhdfs%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fmapreduce%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fyarn%2Flib%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fyarn%2F*%3A%2Fdata%2Fhadoop_base%2Fcontrib%2Fcapacity-scheduler%2F*.jar%22%2C%22dfs.replication%22%3A%223%22%2C%22yarn.admin.acl%22%3A%22*%22%2C%22dfs.ha.fencing.ssh.private-key-files%22%3A%22%7E%2F.ssh%2Fid_rsa%22%2C%22yarn.app.mapreduce.am.job.committer.cancel-timeout%22%3A%2260000%22%2C%22xasecure.audit.destination.db%22%3A%22false%22%2C%22hive.exec.max.dynamic.partitions.pernode%22%3A%22100%22%2C%22dfs.ha.namenodes.ns1%22%3A%22nn1%2Cnn2%22%2C%22yarn.resourcemanager.address.rm1%22%3A%22172.16.84.199%3A8032%22%2C%22dfs.namenode.avoid.read.stale.datanode%22%3A%22FALSE%22%2C%22dfs.journalnode.rpc-address%22%3A%220.0.0.0%3A8485%22%2C%22yarn.resourcemanager.address.rm2%22%3A%22172.16.84.34%3A8032%22%2C%22yarn.scheduler.maximum-allocation-vcores%22%3A%224%22%2C%22dfs.namenode.rpc-address.ns1.nn2%22%3A%22172.16.84.34%3A9000%22%2C%22hive.map.aggr%22%3A%22true%22%2C%22ipc.client.connection.maxidletime%22%3A%2210000%22%2C%22dfs.namenode.rpc-address.ns1.nn1%22%3A%22172.16.84.199%3A9000%22%2C%22yarn.resourcemanager.cluster-id%22%3A%22yarn-rm-cluster%22%2C%22hive.merge.mapfile%22%3A%22true%22%2C%22ha.health-monitor.sleep-after-disconnect.ms%22%3A%221000%22%2C%22yarn.nodemanager.aux-services%22%3A%22mapreduce_shuffle%22%2C%22hadoop.util.hash.type%22%3A%22murmur%22%2C%22yarn.webapp.api-service.enable%22%3A%22false%22%2C%22dfs.hosts.exclude%22%3A%22null%22%2C%22dfs.namenode.replication.min%22%3A%221%22%2C%22hive.server2.support.dynamic.service.discovery%22%3A%22true%22%2C%22dfs.permissions%22%3A%22true%22%2C%22yarn.nodemanager.container-manager.thread-count%22%3A%2220%22%2C%22dfs.datanode.directoryscan.threads%22%3A%221%22%2C%22version%22%3A%222.7.6%22%2C%22dfs.namenode.fs-limits.min-block-size%22%3A%221048576%22%2C%22dfs.datanode.directoryscan.interval%22%3A%2221600%22%2C%22net.topology.script.number.args%22%3A%22100%22%2C%22hadoop.http.authentication.token.validity%22%3A%2236000%22%2C%22ha.failover-controller.graceful-fence.rpc-timeout.ms%22%3A%225000%22%2C%22yarn.resourcemanager.nm.liveness-monitor.interval-ms%22%3A%221000%22%2C%22dfs.namenode.datanode.registration.ip-hostname-check%22%3A%22false%22%2C%22hive.zookeeper.quorum%22%3A%22172-16-84-199%3A2181%2C172-16-84-227%3A2181%2C172-16-84-34%3A2181%22%2C%22dfs.permissions.ContentSummary.subAccess%22%3A%22true%22%2C%22yarn.nodemanager.localizer.cache.cleanup.interval-ms%22%3A%223600000%22%2C%22dfs.client.file-block-storage-locations.num-threads%22%3A%2210%22%2C%22yarn.resourcemanager.scheduler.address.rm1%22%3A%22172.16.84.199%3A8030%22%2C%22yarn.resourcemanager.scheduler.address.rm2%22%3A%22172.16.84.34%3A8030%22%2C%22yarn.nodemanager.delete.debug-delay-sec%22%3A%22600%22%2C%22dfs.webhdfs.enabled%22%3A%22true%22%2C%22hadoop.proxyuser.hdfs.groups%22%3A%22*%22%2C%22yarn.log-aggregation.retain-seconds%22%3A%22604800%22%2C%22dfs.namenode.avoid.write.stale.datanode%22%3A%22FALSE%22%2C%22fs.trash.interval%22%3A%2214400%22%2C%22yarn.scheduler.minimum-allocation-vcores%22%3A%221%22%2C%22dfs.namenode.num.extra.edits.retained%22%3A%221000000%22%2C%22hive.security.authorization.sqlstd.confwhitelist.append%22%3A%22mapred.*%7Chive.*%7Cmapreduce.*%7Cspark.*%22%2C%22ipc.client.connect.max.retries.on.timeouts%22%3A%2245%22%2C%22dfs.datanode.data.dir%22%3A%22file%3A%2Fdata%2Fhadoop%2Fhdfs%2Fdata%22%2C%22hadoop.proxyuser.hive.hosts%22%3A%22*%22%2C%22spark.executor.memory%22%3A%22456340275B%22%2C%22yarn.resourcemanager.resource-tracker.address.rm1%22%3A%22172.16.84.199%3A8031%22%2C%22yarn.resourcemanager.resource-tracker.address.rm2%22%3A%22172.16.84.34%3A8031%22%2C%22ha.health-monitor.connect-retry-interval.ms%22%3A%221000%22%2C%22hive.server2.thrift.port%22%3A%2210004%22%2C%22yarn.nodemanager.env-whitelist%22%3A%22JAVA_HOME%2CHADOOP_COMMON_HOME%2CHADOOP_HDFS_HOME%2CHADOOP_CONF_DIR%2CYARN_HOME%22%2C%22yarn.resourcemanager.container.liveness-monitor.interval-ms%22%3A%22600000%22%2C%22dfs.client-write-packet-size%22%3A%2265536%22%2C%22dfs.journalnode.edits.dir%22%3A%22%2Fdata%2Fhadoop%2Fhdfs%2Fjournal%22%2C%22dfs.client.block.write.retries%22%3A%223%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.hierarchy%22%3A%22%2Fhadoop-yarn%22%2C%22ha.failover-controller.graceful-fence.connection.retries%22%3A%221%22%2C%22yarn.resourcemanager.recovery.enabled%22%3A%22true%22%2C%22dfs.namenode.safemode.threshold-pct%22%3A%220.999f%22%2C%22yarn.nodemanager.disk-health-checker.enable%22%3A%22true%22%2C%22hadoop.proxyuser.yarn.hosts%22%3A%22*%22%2C%22yarn.nodemanager.disk-health-checker.interval-ms%22%3A%22120000%22%2C%22hive.metastore.client.socket.timeout%22%3A%22600s%22%2C%22yarn.resourcemanager.admin.address.rm1%22%3A%22172.16.84.199%3A8033%22%2C%22yarn.log.server.url%22%3A%22http%3A%2F%2F172.16.84.227%3A19888%2Fjobhistory%2Flogs%2F%22%2C%22yarn.resourcemanager.admin.address.rm2%22%3A%22172.16.84.34%3A8033%22%2C%22dfs.datanode.dns.nameserver%22%3A%22default%22%2C%22javax.jdo.option.ConnectionDriverName%22%3A%22com.mysql.jdbc.Driver%22%2C%22yarn.resourcemanager.resource-tracker.client.thread-count%22%3A%2250%22%2C%22ranger.plugin.spark.policy.rest.url%22%3A%22http%3A%2F%2F172.16.84.34%3A6080%22%2C%22dfs.namenode.edits.journal-plugin.qjournal%22%3A%22org.apache.hadoop.hdfs.qjournal.client.QuorumJournalManager%22%2C%22yarn.nodemanager.delete.thread-count%22%3A%224%22%2C%22dfs.nameservices%22%3A%22ns1%22%2C%22versionName%22%3A%22Apache+Hadoop+2.x%22%2C%22dfs.safemode.threshold.pct%22%3A%220.5%22%2C%22ipc.client.idlethreshold%22%3A%224000%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.mount-path%22%3A%22%2Fsys%2Ffs%2Fcgroup%22%2C%22yarn.acl.enable%22%3A%22FALSE%22%2C%22dfs.client.failover.connection.retries.on.timeouts%22%3A%220%22%2C%22hadoop.http.authentication.simple.anonymous.allowed%22%3A%22true%22%2C%22hadoop.security.authorization%22%3A%22true%22%2C%22hive.security.authorization.sqlstd.confwhitelist%22%3A%22mapred.*%7Chive.*%7Cmapreduce.*%7Cspark.*%22%2C%22hive.metastore.warehouse.dir%22%3A%22%2FdtInsight%2Fhive%2Fwarehouse%22%2C%22hive.server2.webui.host%22%3A%220.0.0.0%22%2C%22yarn.am.liveness-monitor.expiry-interval-ms%22%3A%22600000%22%2C%22yarn.nodemanager.linux-container-executor.resources-handler.class%22%3A%22org.apache.hadoop.yarn.server.nodemanager.util.CgroupsLCEResourcesHandler%22%2C%22yarn.resourcemanager.client.thread-count%22%3A%2250%22%2C%22spark.yarn.driver.memoryOverhead%22%3A%22102000000%22%2C%22dfs.image.compression.codec%22%3A%22org.apache.hadoop.io.compress.DefaultCodec%22%2C%22hadoop.security.authentication%22%3A%22simple%22%2C%22hadoop.proxyuser.hdfs.hosts%22%3A%22*%22%2C%22hive.metastore.uris%22%3A%22thrift%3A%2F%2F172-16-84-34%3A9083%22%2C%22hive.exec.dynamic.partition.mode%22%3A%22nonstrict%22%2C%22yarn.resourcemanager.max-completed-applications%22%3A%221000%22%2C%22yarn.resourcemanager.ha.automatic-failover.enabled%22%3A%22true%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.mount%22%3A%22false%22%2C%22yarn.log-aggregation-enable%22%3A%22true%22%2C%22yarn.resourcemanager.store.class%22%3A%22org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore%22%2C%22ranger.plugin.spark.policy.source.impl%22%3A%22org.apache.ranger.admin.client.RangerAdminRESTClient%22%2C%22yarn.app.mapreduce.am.job.task.listener.thread-count%22%3A%2230%22%2C%22hadoop.proxyuser.hive.groups%22%3A%22*%22%2C%22hive.cluster.delegation.token.store.class%22%3A%22org.apache.hadoop.hive.thrift.MemoryTokenStore%22%2C%22dfs.namenode.support.allow.format%22%3A%22true%22%2C%22yarn.nodemanager.log.retain-seconds%22%3A%2210800%22%2C%22yarn.resourcemanager.ha.rm-ids%22%3A%22rm1%2Crm2%22%2C%22dfs.stream-buffer-size%22%3A%224096%22%2C%22yarn.nodemanager.container-executor.class%22%3A%22org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor%22%2C%22yarn.resourcemanager.scheduler.client.thread-count%22%3A%2250%22%2C%22dfs.bytes-per-checksum%22%3A%22512%22%2C%22dfs.datanode.max.transfer.threads%22%3A%228192%22%2C%22ipc.maximum.data.length%22%3A%22134217728%22%2C%22yarn.app.mapreduce.am.job.client.port-range%22%3A%2250100-50200%22%2C%22ha.failover-controller.new-active.rpc-timeout.ms%22%3A%2260000%22%2C%22hive.merge.smallfiles.avgsize%22%3A%2216000000%22%2C%22hive.exec.reducers.max%22%3A%221099%22%2C%22hive.fetch.task.conversion.threshold%22%3A%2232000000%22%2C%22hadoop.http.authentication.type%22%3A%22simple%22%2C%22yarn.scheduler.minimum-allocation-mb%22%3A%22512%22%2C%22dfs.namenode.inode.attributes.provider.class%22%3A%22org.apache.ranger.authorization.hadoop.RangerHdfsAuthorizer%22%2C%22hive.auto.convert.join.noconditionaltask.size%22%3A%2220000000%22%2C%22dfs.permissions.superusergroup%22%3A%22hadoop%22%2C%22io.seqfile.compress.blocksize%22%3A%221000000%22%2C%22dfs.namenode.fs-limits.max-directory-items%22%3A%221048576%22%2C%22hive.support.concurrency%22%3A%22false%22%2C%22yarn.resourcemanager.amliveliness-monitor.interval-ms%22%3A%221000%22%2C%22yarn.nodemanager.pmem-check-enabled%22%3A%22true%22%2C%22yarn.nodemanager.remote-app-log-dir%22%3A%22%2Ftmp%2Flogs%22%2C%22dfs.blockreport.initialDelay%22%3A%220%22%2C%22spark.executor.cores%22%3A%224%22%2C%22yarn.scheduler.maximum-allocation-mb%22%3A%2212288%22%2C%22dfs.namenode.safemode.extension%22%3A%2230000%22%2C%22yarn.nodemanager.vmem-check-enabled%22%3A%22false%22%2C%22hive.server2.active.passive.ha.enable%22%3A%22true%22%2C%22yarn.nodemanager.resource.percentage-physical-cpu-limit%22%3A%2280%22%2C%22fs.permissions.umask-mode%22%3A%22000%22%2C%22hive.reloadable.aux.jars.path%22%3A%22%2FdtInsight%2Fhive%2Fudf%22%2C%22ha.health-monitor.rpc-timeout.ms%22%3A%2245000%22%2C%22hive.server2.webui.max.threads%22%3A%2250%22%2C%22hive.exec.dynamic.partition%22%3A%22true%22%2C%22yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users%22%3A%22false%22%2C%22dfs.datanode.http.address%22%3A%220.0.0.0%3A60075%22%2C%22yarn.nodemanager.linux-container-executor.nonsecure-mode.local-user%22%3A%22yarn%22%2C%22yarn.nodemanager.linux-container-executor.group%22%3A%22yarn%22%2C%22dfs.namenode.fs-limits.max-blocks-per-file%22%3A%221048576%22%2C%22yarn.client.failover-proxy-provider%22%3A%22org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider%22%2C%22dfs.client.block.write.replace-datanode-on-failure.enable%22%3A%22TRUE%22%2C%22yarn.nodemanager.remote-app-log-dir-suffix%22%3A%22logs%22%2C%22net.topology.node.switch.mapping.impl%22%3A%22org.apache.hadoop.net.ScriptBasedMapping%22%2C%22hive.merge.mapredfiles%22%3A%22false%22%2C%22hive.mapjoin.localtask.max.memory.usage%22%3A%220.9%22%2C%22ranger.plugin.spark.policy.pollIntervalMs%22%3A%225000%22%2C%22fs.df.interval%22%3A%2260000%22%2C%22spark.yarn.executor.memoryOverhead%22%3A%2276000000%22%2C%22yarn.nodemanager.resource.count-logical-processors-as-cores%22%3A%22true%22%2C%22ha.zookeeper.parent-znode%22%3A%22%2Fhadoop-ha%22%2C%22hive.exec.max.dynamic.partitions%22%3A%221000%22%2C%22yarn.nodemanager.resource.cpu-vcores%22%3A%228%22%2C%22xasecure.audit.destination.db.jdbc.url%22%3A%22jdbc%3Amysql%3A%2F%2Flocalhost%2Frangeradiut%22%2C%22hive.server2.session.check.interval%22%3A%2260000%22%2C%22hive.server2.idle.operation.timeout%22%3A%226h%22%2C%22yarn.log-aggregation.retain-check-interval-seconds%22%3A%22604800%22%2C%22dfs.https.enable%22%3A%22FALSE%22%2C%22dfs.permissions.enabled%22%3A%22true%22%2C%22dfs.blockreport.split.threshold%22%3A%22500000%22%2C%22dfs.datanode.balance.bandwidthPerSec%22%3A%221048576%22%2C%22ha.zookeeper.quorum%22%3A%22172.16.84.199%3A2181%2C172.16.84.227%3A2181%2C172.16.84.34%3A2181%22%2C%22hadoop.http.filter.initializers%22%3A%22org.apache.hadoop.http.lib.StaticUserWebFilter%22%2C%22ipc.server.read.threadpool.size%22%3A%221%22%2C%22dfs.namenode.stale.datanode.interval%22%3A%2260000%22%2C%22ranger.plugin.spark.policy.cache.dir%22%3A%22.%2Fpolicycache%22%2C%22yarn.resourcemanager.webapp.address.rm2%22%3A%22172.16.84.34%3A18088%22%2C%22yarn.resourcemanager.webapp.address.rm1%22%3A%22172.16.84.199%3A18088%22%2C%22yarn.app.mapreduce.client-am.ipc.max-retries%22%3A%223%22%2C%22dfs.default.chunk.view.size%22%3A%2232768%22%2C%22typeName%22%3A%22yarn2-hdfs2-hadoop2%22%2C%22dfs.client.failover.proxy.provider.ns1%22%3A%22org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.strict-resource-usage%22%3A%22false%22%2C%22hive.merge.sparkfiles%22%3A%22false%22%2C%22yarn.resourcemanager.am.max-retries%22%3A%221%22%2C%22dfs.namenode.handler.count%22%3A%2264%22%2C%22dfs.replication.max%22%3A%22512%22%2C%22xasecure.audit.destination.db.password%22%3A%22ranger%22%2C%22io.bytes.per.checksum%22%3A%22512%22%2C%22dfs.namenode.name.dir%22%3A%22file%3A%2Fdata%2Fhadoop%2Fhdfs%2Fname%22%2C%22yarn.app.mapreduce.client.max-retries%22%3A%223%22%2C%22yarn.nodemanager.resource.memory-mb%22%3A%2212288%22%2C%22yarn.nodemanager.disk-health-checker.min-healthy-disks%22%3A%220.25%22%2C%22dfs.datanode.failed.volumes.tolerated%22%3A%220%22%2C%22ipc.client.kill.max%22%3A%2210%22%2C%22ipc.server.listen.queue.size%22%3A%22128%22%2C%22yarn.nodemanager.localizer.cache.target-size-mb%22%3A%2210240%22%2C%22yarn.resourcemanager.admin.client.thread-count%22%3A%221%22%2C%22yarn.resourcemanager.scheduler.class%22%3A%22org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler%22%2C%22dfs.blocksize%22%3A%22134217728%22%2C%22storeType%22%3A4%2C%22hive.txn.manager%22%3A%22org.apache.hadoop.hive.ql.lockmgr.DummyTxnManager%22%2C%22dfs.datanode.address%22%3A%220.0.0.0%3A50010%22%2C%22security.job.submission.protocol.acl%22%3A%22hdfs%22%2C%22hive.map.aggr.hash.percentmemory%22%3A%220.5%22%2C%22ha.failover-controller.cli-check.rpc-timeout.ms%22%3A%2220000%22%2C%22ipc.client.connect.max.retries%22%3A%2210%22%2C%22ha.zookeeper.acl%22%3A%22world%3Aanyone%3Arwcda%22%2C%22dfs.namenode.write.stale.datanode.ratio%22%3A%220.5f%22%2C%22hive.metastore.server.min.threads%22%3A%22200%22%2C%22hadoop.tmp.dir%22%3A%22%2Fdata%2Fhadoop_hdfs%22%2C%22dfs.datanode.handler.count%22%3A%2210%22%2C%22xasecure.audit.destination.db.user%22%3A%22ranger%22%2C%22md5zip%22%3A%22242557d4010c0d811007a34b94fc5d4c%22%2C%22dfs.client.failover.max.attempts%22%3A%2215%22%2C%22yarn.resourcemanager.zk-state-store.address%22%3A%22172.16.84.199%3A2181%2C172.16.84.227%3A2181%2C172.16.84.34%3A2181%22%2C%22dfs.hosts%22%3A%22null%22%2C%22mapred.reduce.tasks%22%3A%22-1%22%2C%22yarn.resourcemanager.ha.automatic-failover.zk-base-path%22%3A%22%2Fyarn-leader-election%22%2C%22xasecure.audit.destination.db.jdbc.driver%22%3A%22com.mysql.jdbc.Driver%22%2C%22hive.fetch.task.conversion%22%3A%22minimal%22%2C%22yarn.resourcemanager.ha.enabled%22%3A%22true%22%2C%22yarn.app.mapreduce.am.scheduler.heartbeat.interval-ms%22%3A%221000%22%2C%22fs.trash.checkpoint.interval%22%3A%2230%22%2C%22dfs.journalnode.http-address%22%3A%220.0.0.0%3A18480%22%2C%22yarn.nm.liveness-monitor.expiry-interval-ms%22%3A%22600000%22%2C%22dfs.datanode.fsdataset.volume.choosing.policy%22%3A%22org.apache.hadoop.hdfs.server.datanode.fsdataset.AvailableSpaceVolumeChoosingPolicy%22%2C%22ha.health-monitor.check-interval.ms%22%3A%221000%22%2C%22hive.server2.async.exec.threads%22%3A%22200%22%2C%22hive.execution.engine%22%3A%22mr%22%2C%22spark.driver.memory%22%3A%22966367641B%22%2C%22dfs.blockreport.intervalMsec%22%3A%2221600000%22%2C%22hive.metastore.schema.verification%22%3A%22false%22%2C%22hive.metastore.connect.retries%22%3A%2210%22%2C%22mapreduce.job.hdfs-servers%22%3A%22%24%7Bfs.defaultFS%7D%22%2C%22javax.jdo.option.ConnectionPassword%22%3A%22DT%40Stack%23123%22%2C%22io.native.lib.available%22%3A%22TRUE%22%2C%22hadoop.proxyuser.yarn.groups%22%3A%22*%22%2C%22dfs.image.compress%22%3A%22FALSE%22%2C%22yarn.nodemanager.webapp.address%22%3A%220.0.0.0%3A18042%22%2C%22yarn.nodemanager.aux-services.mapreduce_shuffle.class%22%3A%22org.apache.hadoop.mapred.ShuffleHandler%22%2C%22dfs.datanode.dns.interface%22%3A%22default%22%2C%22hive.mapjoin.smalltable.filesize%22%3A%2225000000L%22%2C%22dfs.client.failover.connection.retries%22%3A%220%22%2C%22hive.server2.webui.port%22%3A%2210002%22%2C%22hive.server2.thrift.min.worker.threads%22%3A%22300%22%2C%22fs.defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22xasecure.audit.is.enabled%22%3A%22true%22%2C%22yarn.nodemanager.disk-health-checker.max-disk-utilization-per-disk-percentage%22%3A%2290%22%2C%22dfs.namenode.fs-limits.max-component-length%22%3A%220%22%2C%22dfs.ha.fencing.methods%22%3A%22sshfence%22%2C%22hive.server2.idle.session.check.operation%22%3A%22true%22%2C%22dfs.datanode.du.reserved%22%3A%2220971520%22%2C%22javax.jdo.option.ConnectionURL%22%3A%22jdbc%3Amysql%3A%2F%2F172.16.84.34%3A3306%2Fmetastore%3FcreateDatabaseIfNotExist%3Dtrue%26useSSL%3Dfalse%26autoReconnect%3Dtrue%22%2C%22sftpConf%22%3A%7B%22maxWaitMillis%22%3A%223600000%22%2C%22minIdle%22%3A%2216%22%2C%22auth%22%3A%221%22%2C%22isUsePool%22%3A%22true%22%2C%22timeout%22%3A%223000%22%2C%22path%22%3A%22%2Fhome%2Fadmin%2Fsftp%2Fdttest50_top%22%2C%22password%22%3A%22password%22%2C%22maxIdle%22%3A%2216%22%2C%22port%22%3A%2222%22%2C%22maxTotal%22%3A%2216%22%2C%22host%22%3A%22172.16.82.70%22%2C%22fileTimeout%22%3A%22300000%22%2C%22username%22%3A%22admin%22%7D%2C%22dfs.datanode.ipc.address%22%3A%220.0.0.0%3A50020%22%2C%22hive.merge.size.per.task%22%3A%22256000000%22%2C%22dfs.client.block.write.replace-datanode-on-failure.policy%22%3A%22DEFAULT%22%2C%22hive.metastore.event.db.notification.api.auth%22%3A%22false%22%2C%22yarn.nodemanager.disk-health-checker.min-free-space-per-disk-mb%22%3A%2210240%22%2C%22dfs.namenode.shared.edits.dir%22%3A%22qjournal%3A%2F%2F172.16.84.199%3A8485%3B172.16.84.227%3A8485%3B172.16.84.34%3A8485%2Fnamenode-ha-data%22%2C%22javax.jdo.option.ConnectionUserName%22%3A%22drpeco%22%2C%22hive.mapjoin.followby.gby.localtask.max.memory.usage%22%3A%220.55%22%2C%22hive.warehouse.subdir.inherit.perms%22%3A%22false%22%2C%22dfs.namenode.name.dir.restore%22%3A%22FALSE%22%2C%22dfs.heartbeat.interval%22%3A%223%22%2C%22ha.zookeeper.session-timeout.ms%22%3A%225000%22%2C%22hive.server2.enable.doAs%22%3A%22false%22%2C%22hive.server2.zookeeper.namespace%22%3A%22hiveserver2%22%2C%22dfs.namenode.http-address.ns1.nn2%22%3A%22172.16.84.34%3A60070%22%2C%22dfs.namenode.http-address.ns1.nn1%22%3A%22172.16.84.199%3A60070%22%2C%22ranger.plugin.spark.service.name%22%3A%22spark%22%2C%22yarn.nodemanager.vmem-pmem-ratio%22%3A%224.1%22%2C%22hive.exec.scratchdir%22%3A%22%2FdtInsight%2Fhive%2Fwarehouse%22%2C%22hadoop.http.authentication.signature.secret.file%22%3A%22%2Fdata%2Fhadoop_base%2Fetc%2Fhadoop%2Fhadoop-http-auth-signature-secret%22%2C%22datanucleus.schema.autoCreateAll%22%3A%22true%22%2C%22hive.metastore.server.max.threads%22%3A%22100000%22%2C%22yarn.nodemanager.log-aggregation.compression-type%22%3A%22none%22%2C%22hive.server2.idle.session.timeout%22%3A%223600000%22%2C%22dfs.ha.automatic-failover.enabled%22%3A%22true%22%7D%2C%22column%22%3A%5B%7B%22name%22%3A%22id%22%2C%22index%22%3A0%2C%22isPart%22%3Afalse%2C%22type%22%3A%22int%22%2C%22key%22%3A%22id%22%7D%5D%2C%22defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22connection%22%3A%5B%7B%22jdbcUrl%22%3A%22jdbc%3Ahive2%3A%2F%2F172.16.84.34%3A10004%2Fanquan_test00%22%2C%22table%22%3A%5B%22abcd%22%5D%7D%5D%2C%22encoding%22%3A%22utf-8%22%2C%22fileType%22%3A%22orc%22%2C%22sourceIds%22%3A%5B839%5D%2C%22table%22%3A%22abcd%22%2C%22username%22%3A%22admin%22%7D%2C%22name%22%3A%22hdfsreader%22%7D%2C%22writer%22%3A%7B%22parameter%22%3A%7B%22fileName%22%3A%22%22%2C%22dtCenterSourceId%22%3A1777%2C%22column%22%3A%5B%7B%22name%22%3A%22id%22%2C%22index%22%3A0%2C%22isPart%22%3Afalse%2C%22type%22%3A%22int%22%2C%22key%22%3A%22id%22%7D%5D%2C%22writeMode%22%3A%22append%22%2C%22encoding%22%3A%22utf-8%22%2C%22fullColumnName%22%3A%5B%22id%22%5D%2C%22path%22%3A%22hdfs%3A%2F%2Fns1%2FdtInsight%2Fhive%2Fwarehouse%2Fanquan_test00.db%2Fabcd%22%2C%22hadoopConfig%22%3A%7B%22hive.exec.reducers.bytes.per.reducer%22%3A%2264000000%22%2C%22yarn.resourcemanager.zk-address%22%3A%22172.16.84.199%3A2181%2C172.16.84.227%3A2181%2C172.16.84.34%3A2181%22%2C%22yarn.application.classpath%22%3A%22%2Fdata%2Fhadoop_base%2Fetc%2Fhadoop%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fcommon%2Flib%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fcommon%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fhdfs%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fhdfs%2Flib%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fhdfs%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fmapreduce%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fyarn%2Flib%2F*%3A%2Fdata%2Fhadoop_base%2Fshare%2Fhadoop%2Fyarn%2F*%3A%2Fdata%2Fhadoop_base%2Fcontrib%2Fcapacity-scheduler%2F*.jar%22%2C%22dfs.replication%22%3A%223%22%2C%22yarn.admin.acl%22%3A%22*%22%2C%22dfs.ha.fencing.ssh.private-key-files%22%3A%22%7E%2F.ssh%2Fid_rsa%22%2C%22yarn.app.mapreduce.am.job.committer.cancel-timeout%22%3A%2260000%22%2C%22xasecure.audit.destination.db%22%3A%22false%22%2C%22hive.exec.max.dynamic.partitions.pernode%22%3A%22100%22%2C%22dfs.ha.namenodes.ns1%22%3A%22nn1%2Cnn2%22%2C%22yarn.resourcemanager.address.rm1%22%3A%22172.16.84.199%3A8032%22%2C%22dfs.namenode.avoid.read.stale.datanode%22%3A%22FALSE%22%2C%22dfs.journalnode.rpc-address%22%3A%220.0.0.0%3A8485%22%2C%22yarn.resourcemanager.address.rm2%22%3A%22172.16.84.34%3A8032%22%2C%22yarn.scheduler.maximum-allocation-vcores%22%3A%224%22%2C%22dfs.namenode.rpc-address.ns1.nn2%22%3A%22172.16.84.34%3A9000%22%2C%22hive.map.aggr%22%3A%22true%22%2C%22ipc.client.connection.maxidletime%22%3A%2210000%22%2C%22dfs.namenode.rpc-address.ns1.nn1%22%3A%22172.16.84.199%3A9000%22%2C%22yarn.resourcemanager.cluster-id%22%3A%22yarn-rm-cluster%22%2C%22hive.merge.mapfile%22%3A%22true%22%2C%22ha.health-monitor.sleep-after-disconnect.ms%22%3A%221000%22%2C%22yarn.nodemanager.aux-services%22%3A%22mapreduce_shuffle%22%2C%22hadoop.util.hash.type%22%3A%22murmur%22%2C%22yarn.webapp.api-service.enable%22%3A%22false%22%2C%22dfs.hosts.exclude%22%3A%22null%22%2C%22dfs.namenode.replication.min%22%3A%221%22%2C%22hive.server2.support.dynamic.service.discovery%22%3A%22true%22%2C%22dfs.permissions%22%3A%22true%22%2C%22yarn.nodemanager.container-manager.thread-count%22%3A%2220%22%2C%22dfs.datanode.directoryscan.threads%22%3A%221%22%2C%22version%22%3A%222.7.6%22%2C%22dfs.namenode.fs-limits.min-block-size%22%3A%221048576%22%2C%22dfs.datanode.directoryscan.interval%22%3A%2221600%22%2C%22net.topology.script.number.args%22%3A%22100%22%2C%22hadoop.http.authentication.token.validity%22%3A%2236000%22%2C%22ha.failover-controller.graceful-fence.rpc-timeout.ms%22%3A%225000%22%2C%22yarn.resourcemanager.nm.liveness-monitor.interval-ms%22%3A%221000%22%2C%22dfs.namenode.datanode.registration.ip-hostname-check%22%3A%22false%22%2C%22hive.zookeeper.quorum%22%3A%22172-16-84-199%3A2181%2C172-16-84-227%3A2181%2C172-16-84-34%3A2181%22%2C%22dfs.permissions.ContentSummary.subAccess%22%3A%22true%22%2C%22yarn.nodemanager.localizer.cache.cleanup.interval-ms%22%3A%223600000%22%2C%22dfs.client.file-block-storage-locations.num-threads%22%3A%2210%22%2C%22yarn.resourcemanager.scheduler.address.rm1%22%3A%22172.16.84.199%3A8030%22%2C%22yarn.resourcemanager.scheduler.address.rm2%22%3A%22172.16.84.34%3A8030%22%2C%22yarn.nodemanager.delete.debug-delay-sec%22%3A%22600%22%2C%22dfs.webhdfs.enabled%22%3A%22true%22%2C%22hadoop.proxyuser.hdfs.groups%22%3A%22*%22%2C%22yarn.log-aggregation.retain-seconds%22%3A%22604800%22%2C%22dfs.namenode.avoid.write.stale.datanode%22%3A%22FALSE%22%2C%22fs.trash.interval%22%3A%2214400%22%2C%22yarn.scheduler.minimum-allocation-vcores%22%3A%221%22%2C%22dfs.namenode.num.extra.edits.retained%22%3A%221000000%22%2C%22hive.security.authorization.sqlstd.confwhitelist.append%22%3A%22mapred.*%7Chive.*%7Cmapreduce.*%7Cspark.*%22%2C%22ipc.client.connect.max.retries.on.timeouts%22%3A%2245%22%2C%22dfs.datanode.data.dir%22%3A%22file%3A%2Fdata%2Fhadoop%2Fhdfs%2Fdata%22%2C%22hadoop.proxyuser.hive.hosts%22%3A%22*%22%2C%22spark.executor.memory%22%3A%22456340275B%22%2C%22yarn.resourcemanager.resource-tracker.address.rm1%22%3A%22172.16.84.199%3A8031%22%2C%22yarn.resourcemanager.resource-tracker.address.rm2%22%3A%22172.16.84.34%3A8031%22%2C%22ha.health-monitor.connect-retry-interval.ms%22%3A%221000%22%2C%22hive.server2.thrift.port%22%3A%2210004%22%2C%22yarn.nodemanager.env-whitelist%22%3A%22JAVA_HOME%2CHADOOP_COMMON_HOME%2CHADOOP_HDFS_HOME%2CHADOOP_CONF_DIR%2CYARN_HOME%22%2C%22yarn.resourcemanager.container.liveness-monitor.interval-ms%22%3A%22600000%22%2C%22dfs.client-write-packet-size%22%3A%2265536%22%2C%22dfs.journalnode.edits.dir%22%3A%22%2Fdata%2Fhadoop%2Fhdfs%2Fjournal%22%2C%22dfs.client.block.write.retries%22%3A%223%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.hierarchy%22%3A%22%2Fhadoop-yarn%22%2C%22ha.failover-controller.graceful-fence.connection.retries%22%3A%221%22%2C%22yarn.resourcemanager.recovery.enabled%22%3A%22true%22%2C%22dfs.namenode.safemode.threshold-pct%22%3A%220.999f%22%2C%22yarn.nodemanager.disk-health-checker.enable%22%3A%22true%22%2C%22hadoop.proxyuser.yarn.hosts%22%3A%22*%22%2C%22yarn.nodemanager.disk-health-checker.interval-ms%22%3A%22120000%22%2C%22hive.metastore.client.socket.timeout%22%3A%22600s%22%2C%22yarn.resourcemanager.admin.address.rm1%22%3A%22172.16.84.199%3A8033%22%2C%22yarn.log.server.url%22%3A%22http%3A%2F%2F172.16.84.227%3A19888%2Fjobhistory%2Flogs%2F%22%2C%22yarn.resourcemanager.admin.address.rm2%22%3A%22172.16.84.34%3A8033%22%2C%22dfs.datanode.dns.nameserver%22%3A%22default%22%2C%22javax.jdo.option.ConnectionDriverName%22%3A%22com.mysql.jdbc.Driver%22%2C%22yarn.resourcemanager.resource-tracker.client.thread-count%22%3A%2250%22%2C%22ranger.plugin.spark.policy.rest.url%22%3A%22http%3A%2F%2F172.16.84.34%3A6080%22%2C%22dfs.namenode.edits.journal-plugin.qjournal%22%3A%22org.apache.hadoop.hdfs.qjournal.client.QuorumJournalManager%22%2C%22yarn.nodemanager.delete.thread-count%22%3A%224%22%2C%22dfs.nameservices%22%3A%22ns1%22%2C%22versionName%22%3A%22Apache+Hadoop+2.x%22%2C%22dfs.safemode.threshold.pct%22%3A%220.5%22%2C%22ipc.client.idlethreshold%22%3A%224000%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.mount-path%22%3A%22%2Fsys%2Ffs%2Fcgroup%22%2C%22yarn.acl.enable%22%3A%22FALSE%22%2C%22dfs.client.failover.connection.retries.on.timeouts%22%3A%220%22%2C%22hadoop.http.authentication.simple.anonymous.allowed%22%3A%22true%22%2C%22hadoop.security.authorization%22%3A%22true%22%2C%22hive.security.authorization.sqlstd.confwhitelist%22%3A%22mapred.*%7Chive.*%7Cmapreduce.*%7Cspark.*%22%2C%22hive.metastore.warehouse.dir%22%3A%22%2FdtInsight%2Fhive%2Fwarehouse%22%2C%22hive.server2.webui.host%22%3A%220.0.0.0%22%2C%22yarn.am.liveness-monitor.expiry-interval-ms%22%3A%22600000%22%2C%22yarn.nodemanager.linux-container-executor.resources-handler.class%22%3A%22org.apache.hadoop.yarn.server.nodemanager.util.CgroupsLCEResourcesHandler%22%2C%22yarn.resourcemanager.client.thread-count%22%3A%2250%22%2C%22spark.yarn.driver.memoryOverhead%22%3A%22102000000%22%2C%22dfs.image.compression.codec%22%3A%22org.apache.hadoop.io.compress.DefaultCodec%22%2C%22hadoop.security.authentication%22%3A%22simple%22%2C%22hadoop.proxyuser.hdfs.hosts%22%3A%22*%22%2C%22hive.metastore.uris%22%3A%22thrift%3A%2F%2F172-16-84-34%3A9083%22%2C%22hive.exec.dynamic.partition.mode%22%3A%22nonstrict%22%2C%22yarn.resourcemanager.max-completed-applications%22%3A%221000%22%2C%22yarn.resourcemanager.ha.automatic-failover.enabled%22%3A%22true%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.mount%22%3A%22false%22%2C%22yarn.log-aggregation-enable%22%3A%22true%22%2C%22yarn.resourcemanager.store.class%22%3A%22org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore%22%2C%22ranger.plugin.spark.policy.source.impl%22%3A%22org.apache.ranger.admin.client.RangerAdminRESTClient%22%2C%22yarn.app.mapreduce.am.job.task.listener.thread-count%22%3A%2230%22%2C%22hadoop.proxyuser.hive.groups%22%3A%22*%22%2C%22hive.cluster.delegation.token.store.class%22%3A%22org.apache.hadoop.hive.thrift.MemoryTokenStore%22%2C%22dfs.namenode.support.allow.format%22%3A%22true%22%2C%22yarn.nodemanager.log.retain-seconds%22%3A%2210800%22%2C%22yarn.resourcemanager.ha.rm-ids%22%3A%22rm1%2Crm2%22%2C%22dfs.stream-buffer-size%22%3A%224096%22%2C%22yarn.nodemanager.container-executor.class%22%3A%22org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor%22%2C%22yarn.resourcemanager.scheduler.client.thread-count%22%3A%2250%22%2C%22dfs.bytes-per-checksum%22%3A%22512%22%2C%22dfs.datanode.max.transfer.threads%22%3A%228192%22%2C%22ipc.maximum.data.length%22%3A%22134217728%22%2C%22yarn.app.mapreduce.am.job.client.port-range%22%3A%2250100-50200%22%2C%22ha.failover-controller.new-active.rpc-timeout.ms%22%3A%2260000%22%2C%22hive.merge.smallfiles.avgsize%22%3A%2216000000%22%2C%22hive.exec.reducers.max%22%3A%221099%22%2C%22hive.fetch.task.conversion.threshold%22%3A%2232000000%22%2C%22hadoop.http.authentication.type%22%3A%22simple%22%2C%22yarn.scheduler.minimum-allocation-mb%22%3A%22512%22%2C%22dfs.namenode.inode.attributes.provider.class%22%3A%22org.apache.ranger.authorization.hadoop.RangerHdfsAuthorizer%22%2C%22hive.auto.convert.join.noconditionaltask.size%22%3A%2220000000%22%2C%22dfs.permissions.superusergroup%22%3A%22hadoop%22%2C%22io.seqfile.compress.blocksize%22%3A%221000000%22%2C%22dfs.namenode.fs-limits.max-directory-items%22%3A%221048576%22%2C%22hive.support.concurrency%22%3A%22false%22%2C%22yarn.resourcemanager.amliveliness-monitor.interval-ms%22%3A%221000%22%2C%22yarn.nodemanager.pmem-check-enabled%22%3A%22true%22%2C%22yarn.nodemanager.remote-app-log-dir%22%3A%22%2Ftmp%2Flogs%22%2C%22dfs.blockreport.initialDelay%22%3A%220%22%2C%22spark.executor.cores%22%3A%224%22%2C%22yarn.scheduler.maximum-allocation-mb%22%3A%2212288%22%2C%22dfs.namenode.safemode.extension%22%3A%2230000%22%2C%22yarn.nodemanager.vmem-check-enabled%22%3A%22false%22%2C%22hive.server2.active.passive.ha.enable%22%3A%22true%22%2C%22yarn.nodemanager.resource.percentage-physical-cpu-limit%22%3A%2280%22%2C%22fs.permissions.umask-mode%22%3A%22000%22%2C%22hive.reloadable.aux.jars.path%22%3A%22%2FdtInsight%2Fhive%2Fudf%22%2C%22ha.health-monitor.rpc-timeout.ms%22%3A%2245000%22%2C%22hive.server2.webui.max.threads%22%3A%2250%22%2C%22hive.exec.dynamic.partition%22%3A%22true%22%2C%22yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users%22%3A%22false%22%2C%22dfs.datanode.http.address%22%3A%220.0.0.0%3A60075%22%2C%22yarn.nodemanager.linux-container-executor.nonsecure-mode.local-user%22%3A%22yarn%22%2C%22yarn.nodemanager.linux-container-executor.group%22%3A%22yarn%22%2C%22dfs.namenode.fs-limits.max-blocks-per-file%22%3A%221048576%22%2C%22yarn.client.failover-proxy-provider%22%3A%22org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider%22%2C%22dfs.client.block.write.replace-datanode-on-failure.enable%22%3A%22TRUE%22%2C%22yarn.nodemanager.remote-app-log-dir-suffix%22%3A%22logs%22%2C%22net.topology.node.switch.mapping.impl%22%3A%22org.apache.hadoop.net.ScriptBasedMapping%22%2C%22hive.merge.mapredfiles%22%3A%22false%22%2C%22hive.mapjoin.localtask.max.memory.usage%22%3A%220.9%22%2C%22ranger.plugin.spark.policy.pollIntervalMs%22%3A%225000%22%2C%22fs.df.interval%22%3A%2260000%22%2C%22spark.yarn.executor.memoryOverhead%22%3A%2276000000%22%2C%22yarn.nodemanager.resource.count-logical-processors-as-cores%22%3A%22true%22%2C%22ha.zookeeper.parent-znode%22%3A%22%2Fhadoop-ha%22%2C%22hive.exec.max.dynamic.partitions%22%3A%221000%22%2C%22yarn.nodemanager.resource.cpu-vcores%22%3A%228%22%2C%22xasecure.audit.destination.db.jdbc.url%22%3A%22jdbc%3Amysql%3A%2F%2Flocalhost%2Frangeradiut%22%2C%22hive.server2.session.check.interval%22%3A%2260000%22%2C%22hive.server2.idle.operation.timeout%22%3A%226h%22%2C%22yarn.log-aggregation.retain-check-interval-seconds%22%3A%22604800%22%2C%22dfs.https.enable%22%3A%22FALSE%22%2C%22dfs.permissions.enabled%22%3A%22true%22%2C%22dfs.blockreport.split.threshold%22%3A%22500000%22%2C%22dfs.datanode.balance.bandwidthPerSec%22%3A%221048576%22%2C%22ha.zookeeper.quorum%22%3A%22172.16.84.199%3A2181%2C172.16.84.227%3A2181%2C172.16.84.34%3A2181%22%2C%22hadoop.http.filter.initializers%22%3A%22org.apache.hadoop.http.lib.StaticUserWebFilter%22%2C%22ipc.server.read.threadpool.size%22%3A%221%22%2C%22dfs.namenode.stale.datanode.interval%22%3A%2260000%22%2C%22ranger.plugin.spark.policy.cache.dir%22%3A%22.%2Fpolicycache%22%2C%22yarn.resourcemanager.webapp.address.rm2%22%3A%22172.16.84.34%3A18088%22%2C%22yarn.resourcemanager.webapp.address.rm1%22%3A%22172.16.84.199%3A18088%22%2C%22yarn.app.mapreduce.client-am.ipc.max-retries%22%3A%223%22%2C%22dfs.default.chunk.view.size%22%3A%2232768%22%2C%22typeName%22%3A%22yarn2-hdfs2-hadoop2%22%2C%22dfs.client.failover.proxy.provider.ns1%22%3A%22org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider%22%2C%22yarn.nodemanager.linux-container-executor.cgroups.strict-resource-usage%22%3A%22false%22%2C%22hive.merge.sparkfiles%22%3A%22false%22%2C%22yarn.resourcemanager.am.max-retries%22%3A%221%22%2C%22dfs.namenode.handler.count%22%3A%2264%22%2C%22dfs.replication.max%22%3A%22512%22%2C%22xasecure.audit.destination.db.password%22%3A%22ranger%22%2C%22io.bytes.per.checksum%22%3A%22512%22%2C%22dfs.namenode.name.dir%22%3A%22file%3A%2Fdata%2Fhadoop%2Fhdfs%2Fname%22%2C%22yarn.app.mapreduce.client.max-retries%22%3A%223%22%2C%22yarn.nodemanager.resource.memory-mb%22%3A%2212288%22%2C%22yarn.nodemanager.disk-health-checker.min-healthy-disks%22%3A%220.25%22%2C%22dfs.datanode.failed.volumes.tolerated%22%3A%220%22%2C%22ipc.client.kill.max%22%3A%2210%22%2C%22ipc.server.listen.queue.size%22%3A%22128%22%2C%22yarn.nodemanager.localizer.cache.target-size-mb%22%3A%2210240%22%2C%22yarn.resourcemanager.admin.client.thread-count%22%3A%221%22%2C%22yarn.resourcemanager.scheduler.class%22%3A%22org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler%22%2C%22dfs.blocksize%22%3A%22134217728%22%2C%22storeType%22%3A4%2C%22hive.txn.manager%22%3A%22org.apache.hadoop.hive.ql.lockmgr.DummyTxnManager%22%2C%22dfs.datanode.address%22%3A%220.0.0.0%3A50010%22%2C%22security.job.submission.protocol.acl%22%3A%22hdfs%22%2C%22hive.map.aggr.hash.percentmemory%22%3A%220.5%22%2C%22ha.failover-controller.cli-check.rpc-timeout.ms%22%3A%2220000%22%2C%22ipc.client.connect.max.retries%22%3A%2210%22%2C%22ha.zookeeper.acl%22%3A%22world%3Aanyone%3Arwcda%22%2C%22dfs.namenode.write.stale.datanode.ratio%22%3A%220.5f%22%2C%22hive.metastore.server.min.threads%22%3A%22200%22%2C%22hadoop.tmp.dir%22%3A%22%2Fdata%2Fhadoop_hdfs%22%2C%22dfs.datanode.handler.count%22%3A%2210%22%2C%22xasecure.audit.destination.db.user%22%3A%22ranger%22%2C%22md5zip%22%3A%22242557d4010c0d811007a34b94fc5d4c%22%2C%22dfs.client.failover.max.attempts%22%3A%2215%22%2C%22yarn.resourcemanager.zk-state-store.address%22%3A%22172.16.84.199%3A2181%2C172.16.84.227%3A2181%2C172.16.84.34%3A2181%22%2C%22dfs.hosts%22%3A%22null%22%2C%22mapred.reduce.tasks%22%3A%22-1%22%2C%22yarn.resourcemanager.ha.automatic-failover.zk-base-path%22%3A%22%2Fyarn-leader-election%22%2C%22xasecure.audit.destination.db.jdbc.driver%22%3A%22com.mysql.jdbc.Driver%22%2C%22hive.fetch.task.conversion%22%3A%22minimal%22%2C%22yarn.resourcemanager.ha.enabled%22%3A%22true%22%2C%22yarn.app.mapreduce.am.scheduler.heartbeat.interval-ms%22%3A%221000%22%2C%22fs.trash.checkpoint.interval%22%3A%2230%22%2C%22dfs.journalnode.http-address%22%3A%220.0.0.0%3A18480%22%2C%22yarn.nm.liveness-monitor.expiry-interval-ms%22%3A%22600000%22%2C%22dfs.datanode.fsdataset.volume.choosing.policy%22%3A%22org.apache.hadoop.hdfs.server.datanode.fsdataset.AvailableSpaceVolumeChoosingPolicy%22%2C%22ha.health-monitor.check-interval.ms%22%3A%221000%22%2C%22hive.server2.async.exec.threads%22%3A%22200%22%2C%22hive.execution.engine%22%3A%22mr%22%2C%22spark.driver.memory%22%3A%22966367641B%22%2C%22dfs.blockreport.intervalMsec%22%3A%2221600000%22%2C%22hive.metastore.schema.verification%22%3A%22false%22%2C%22hive.metastore.connect.retries%22%3A%2210%22%2C%22mapreduce.job.hdfs-servers%22%3A%22%24%7Bfs.defaultFS%7D%22%2C%22javax.jdo.option.ConnectionPassword%22%3A%22DT%40Stack%23123%22%2C%22io.native.lib.available%22%3A%22TRUE%22%2C%22hadoop.proxyuser.yarn.groups%22%3A%22*%22%2C%22dfs.image.compress%22%3A%22FALSE%22%2C%22yarn.nodemanager.webapp.address%22%3A%220.0.0.0%3A18042%22%2C%22yarn.nodemanager.aux-services.mapreduce_shuffle.class%22%3A%22org.apache.hadoop.mapred.ShuffleHandler%22%2C%22dfs.datanode.dns.interface%22%3A%22default%22%2C%22hive.mapjoin.smalltable.filesize%22%3A%2225000000L%22%2C%22dfs.client.failover.connection.retries%22%3A%220%22%2C%22hive.server2.webui.port%22%3A%2210002%22%2C%22hive.server2.thrift.min.worker.threads%22%3A%22300%22%2C%22fs.defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22xasecure.audit.is.enabled%22%3A%22true%22%2C%22yarn.nodemanager.disk-health-checker.max-disk-utilization-per-disk-percentage%22%3A%2290%22%2C%22dfs.namenode.fs-limits.max-component-length%22%3A%220%22%2C%22dfs.ha.fencing.methods%22%3A%22sshfence%22%2C%22hive.server2.idle.session.check.operation%22%3A%22true%22%2C%22dfs.datanode.du.reserved%22%3A%2220971520%22%2C%22javax.jdo.option.ConnectionURL%22%3A%22jdbc%3Amysql%3A%2F%2F172.16.84.34%3A3306%2Fmetastore%3FcreateDatabaseIfNotExist%3Dtrue%26useSSL%3Dfalse%26autoReconnect%3Dtrue%22%2C%22sftpConf%22%3A%7B%22maxWaitMillis%22%3A%223600000%22%2C%22minIdle%22%3A%2216%22%2C%22auth%22%3A%221%22%2C%22isUsePool%22%3A%22true%22%2C%22timeout%22%3A%223000%22%2C%22path%22%3A%22%2Fhome%2Fadmin%2Fsftp%2Fdttest50_top%22%2C%22password%22%3A%22password%22%2C%22maxIdle%22%3A%2216%22%2C%22port%22%3A%2222%22%2C%22maxTotal%22%3A%2216%22%2C%22host%22%3A%22172.16.82.70%22%2C%22fileTimeout%22%3A%22300000%22%2C%22username%22%3A%22admin%22%7D%2C%22dfs.datanode.ipc.address%22%3A%220.0.0.0%3A50020%22%2C%22hive.merge.size.per.task%22%3A%22256000000%22%2C%22dfs.client.block.write.replace-datanode-on-failure.policy%22%3A%22DEFAULT%22%2C%22hive.metastore.event.db.notification.api.auth%22%3A%22false%22%2C%22yarn.nodemanager.disk-health-checker.min-free-space-per-disk-mb%22%3A%2210240%22%2C%22dfs.namenode.shared.edits.dir%22%3A%22qjournal%3A%2F%2F172.16.84.199%3A8485%3B172.16.84.227%3A8485%3B172.16.84.34%3A8485%2Fnamenode-ha-data%22%2C%22javax.jdo.option.ConnectionUserName%22%3A%22drpeco%22%2C%22hive.mapjoin.followby.gby.localtask.max.memory.usage%22%3A%220.55%22%2C%22hive.warehouse.subdir.inherit.perms%22%3A%22false%22%2C%22dfs.namenode.name.dir.restore%22%3A%22FALSE%22%2C%22dfs.heartbeat.interval%22%3A%223%22%2C%22ha.zookeeper.session-timeout.ms%22%3A%225000%22%2C%22hive.server2.enable.doAs%22%3A%22false%22%2C%22hive.server2.zookeeper.namespace%22%3A%22hiveserver2%22%2C%22dfs.namenode.http-address.ns1.nn2%22%3A%22172.16.84.34%3A60070%22%2C%22dfs.namenode.http-address.ns1.nn1%22%3A%22172.16.84.199%3A60070%22%2C%22ranger.plugin.spark.service.name%22%3A%22spark%22%2C%22yarn.nodemanager.vmem-pmem-ratio%22%3A%224.1%22%2C%22hive.exec.scratchdir%22%3A%22%2FdtInsight%2Fhive%2Fwarehouse%22%2C%22hadoop.http.authentication.signature.secret.file%22%3A%22%2Fdata%2Fhadoop_base%2Fetc%2Fhadoop%2Fhadoop-http-auth-signature-secret%22%2C%22datanucleus.schema.autoCreateAll%22%3A%22true%22%2C%22hive.metastore.server.max.threads%22%3A%22100000%22%2C%22yarn.nodemanager.log-aggregation.compression-type%22%3A%22none%22%2C%22hive.server2.idle.session.timeout%22%3A%223600000%22%2C%22dfs.ha.automatic-failover.enabled%22%3A%22true%22%7D%2C%22defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22connection%22%3A%5B%7B%22jdbcUrl%22%3A%22jdbc%3Ahive2%3A%2F%2F172.16.84.34%3A10004%2Fanquan_test00%22%2C%22table%22%3A%5B%22abcd%22%5D%7D%5D%2C%22table%22%3A%22abcd%22%2C%22fileType%22%3A%22orc%22%2C%22sourceIds%22%3A%5B839%5D%2C%22username%22%3A%22admin%22%2C%22fullColumnType%22%3A%5B%22int%22%5D%7D%2C%22name%22%3A%22hdfswriter%22%7D%7D%5D%2C%22setting%22%3A%7B%22restore%22%3A%7B%22maxRowNumForCheckpoint%22%3A0%2C%22isRestore%22%3Afalse%2C%22restoreColumnName%22%3A%22%22%2C%22restoreColumnIndex%22%3A0%7D%2C%22errorLimit%22%3A%7B%22record%22%3A100%7D%2C%22speed%22%3A%7B%22bytes%22%3A0%2C%22channel%22%3A1%7D%7D%7D%7D\",\"generateTime\":1653636262536,\"groupName\":\"data_security_default\",\"lackingCount\":0,\"maxRetryNum\":0,\"name\":\"runJob_run_sync_task_spark_spark_1653636262278_20220527152422\",\"priority\":1653637262536,\"projectId\":477,\"requestStart\":0,\"resourceId\":53,\"sqlText\":\"\",\"stopJobId\":0,\"submitExpiredTime\":0,\"taskId\":\"4irtau3pasf1\",\"taskParams\":\"## 任务运行方式：\\n## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\\n## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job\\n## flinkTaskRunMode=per_job\\n## per_job模式下jobManager配置的内存大小，默认1024（单位M)\\n## jobmanager.memory.mb=1024\\n## per_job模式下taskManager配置的内存大小，默认1024（单位M）\\n## taskmanager.memory.mb=1024\\n## per_job模式下每个taskManager 对应 slot的数量\\n## slots=1\\n\\n## checkpoint保存时间间隔\\n## flink.checkpoint.interval=300000\\n## 任务优先级, 范围:1-1000\\n## job.priority=10\\npipeline.operator-chainning = false\",\"taskType\":2,\"tenantId\":10597}";
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getByClusterName(String clusterName) {
        if ("default".equals(clusterName)) {
            return ClusterServiceMock.mockDefaultCluster();
        }
        return null;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public JSONObject getYarnInfo(Long clusterId, Long dtUicTenantId) {
        JSONObject yarnInfo = new JSONObject();
        yarnInfo.put(ConfigConstant.TYPE_NAME_KEY, "yarn2");
        return yarnInfo;
    }

    @MockInvoke(targetClass = RdosWrapper.class)
    public Integer getDataSourceCodeByDiceName(String diceName, EComponentType eComponentType) {
        return 1;
    }

    @MockInvoke(targetClass = ClientCache.class)
    public static IYarn getYarn(Integer dataSourceType) {
        return new YarnClientProxy(null);
    }

    @MockInvoke(targetClass = IYarn.class)
    YarnResourceDTO getYarnResource(@RpcNodeSign("tenantId") ISourceDTO source) {
        return new YarnResourceDTO();
    }

    @MockInvoke(targetClass = PlugInStruct.class)
    ClusterResource yarnResourceDTOtoClusterResource(YarnResourceDTO yarnResource) {
        return new ClusterResource();
    }

    @MockInvoke(targetClass = JobGraphTriggerDao.class)
    List<JobGraphTrigger> listByTriggerTime(@Param("beginTime") Timestamp begin, @Param("endTime") Timestamp end) {
        JobGraphTrigger trigger = new JobGraphTrigger();
        trigger.setTriggerTime(Timestamp.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()));
        trigger.setGmtCreate(Timestamp.from(ZonedDateTime.now().minusDays(1L).truncatedTo(ChronoUnit.DAYS).toInstant()));
        return Lists.newArrayList(trigger);
    }

    @MockInvoke(targetClass = DtUicUserConnect.class)
    public List<Map<String, Object>> getAllUicUsers(String url, String productCode, Long tenantId, String dtToken) {
        Map<String, Object> users = new HashMap<>();
        users.put("userId", 2L);
        users.put("userName", "userName");
        return Lists.newArrayList(users);
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    ApiResponse<List<UICUserVO>> getByUserIds(List<Long> userIds) {
        UICUserVO vo = new UICUserVO();
        vo.setTenantId(1L);
        ApiResponse<List<UICUserVO>> resp = new ApiResponse<>();
        resp.setData(Lists.newArrayList(vo));
        return resp;
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    ApiResponse<List<UICUserListResult>> findCurrentUserByName(String userName) {
        UICUserListResult vo = new UICUserListResult();
        vo.setUserId(1L);
        ApiResponse<List<UICUserListResult>> resp = new ApiResponse<>();
        resp.setData(Lists.newArrayList(vo));
        return resp;
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    List<ScheduleDict> listDictByType(Integer type) {
        if (type.equals(DictType.ALERT_CONFIG.type)) {
            ScheduleDict scheduleDict = new ScheduleDict();
            scheduleDict.setDictValue(mockAlertConfig());
            return Lists.newArrayList(scheduleDict);
        }
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    Integer insert(ScheduleDict dict) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    Integer updateValue(ScheduleDict dict) {
        return 1;
    }

    public static String mockAlertConfig() {
        return "{\"enabled\":true,\"methods\":[{\"alertGateName\":\"自定义告警\",\"alertGateSource\":\"zidingyi\",\"alertGateType\":4}],\"receivers\":[101855]}";
    }
}