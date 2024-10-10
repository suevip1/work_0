package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.Column;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.Database;
import com.dtstack.dtcenter.loader.dto.RoleTableGrantsDTO;
import com.dtstack.dtcenter.loader.dto.RoleTableGrantsParamDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.Table;
import com.dtstack.dtcenter.loader.dto.TableInfo;
import com.dtstack.dtcenter.loader.dto.WriteFileDTO;
import com.dtstack.dtcenter.loader.dto.metadata.MetadataCollectCondition;
import com.dtstack.dtcenter.loader.dto.metadata.entity.MetadataEntity;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.dtcenter.loader.metadata.MetaDataCollectManager;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.dto.PreCheckSyncDTO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.listener.AlterEvent;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.multiengine.checker.ColumnChecker;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.security.DataSecurityChecker;
import com.dtstack.rpc.download.IDownloader;
import com.dtstack.schedule.common.metric.batch.IMetric;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncJobStartTriggerTest {
    SyncJobStartTrigger syncJobStartTrigger = new SyncJobStartTrigger();

    @Test
    public void testSyncHadoopJobTrigger() throws Exception {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(0L);
        scheduleTaskShade.setExtraInfo("{}");
        scheduleTaskShade.setTenantId(1L);
        scheduleTaskShade.setProjectId(1L);
        scheduleTaskShade.setNodePid(1L);
        scheduleTaskShade.setName("testJob");
        scheduleTaskShade.setTaskType(EScheduleJobType.SYNC.getType());
        scheduleTaskShade.setEngineType(2);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setSqlText("select");
        scheduleTaskShade.setTaskParams("");
        scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setPeriodType(1);
        scheduleTaskShade.setScheduleStatus(1);
        scheduleTaskShade.setSubmitStatus(1);
        scheduleTaskShade.setGmtCreate(new Timestamp(1592559742000L));
        scheduleTaskShade.setGmtModified(new Timestamp(1592559742000L));
        scheduleTaskShade.setModifyUserId(1L);
        scheduleTaskShade.setCreateUserId(1L);
        scheduleTaskShade.setOwnerUserId(1L);
        scheduleTaskShade.setVersionId(1);
        scheduleTaskShade.setTaskDesc("null");
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setIsDeleted(0);
        scheduleTaskShade.setMainClass("DataCollection");
        scheduleTaskShade.setExeArgs("null");
        scheduleTaskShade.setFlowId(0L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setProjectScheduleStatus(0);
        scheduleTaskShade.setAppType(AppType.RDOS.getType());
        scheduleTaskShade.setEngineType(1);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setTaskId(1487L);
        scheduleTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"\\\",\\\"computeType\\\":1,\\\"engineIdentity\\\":\\\"dev\\\",\\\"engineType\\\":\\\"flink\\\",\\\"taskParams\\\":\\\"mr.job.parallelism = 1\\\\n\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":2,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"testInc\\\",\\\"tenantId\\\":1,\\\"job\\\":\\\"{\\\\\\\"job\\\\\\\":{\\\\\\\"content\\\\\\\":[{\\\\\\\"reader\\\\\\\":{\\\\\\\"parameter\\\\\\\":{\\\\\\\"password\\\\\\\":\\\\\\\"DT@Stack#123\\\\\\\",\\\\\\\"customSql\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"startLocation\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"increColumn\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"column\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"INT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"task_id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"INT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"task_id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"type\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"INT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"type\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_name\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"VARCHAR\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_name\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_command\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"VARCHAR\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_command\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_create\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"DATETIME\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_create\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_modified\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"DATETIME\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_modified\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"is_deleted\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"TINYINT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"is_deleted\\\\\\\"}],\\\\\\\"connection\\\\\\\":[{\\\\\\\"sourceId\\\\\\\":39,\\\\\\\"password\\\\\\\":\\\\\\\"DT@Stack#123\\\\\\\",\\\\\\\"jdbcUrl\\\\\\\":[\\\\\\\"jdbc:mysql://172.16.100.115:3306/ide\\\\\\\"],\\\\\\\"type\\\\\\\":1,\\\\\\\"table\\\\\\\":[\\\\\\\"rdos_batch_task_param\\\\\\\"],\\\\\\\"username\\\\\\\":\\\\\\\"drpeco\\\\\\\"}],\\\\\\\"sourceIds\\\\\\\":[39],\\\\\\\"username\\\\\\\":\\\\\\\"drpeco\\\\\\\"},\\\\\\\"name\\\\\\\":\\\\\\\"mysqlreader\\\\\\\"},\\\\\\\"writer\\\\\\\":{\\\\\\\"parameter\\\\\\\":{\\\\\\\"fileName\\\\\\\":\\\\\\\"pt=2020\\\\\\\",\\\\\\\"column\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"index\\\\\\\":0,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"int\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"task_id\\\\\\\",\\\\\\\"index\\\\\\\":1,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"int\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"task_id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"type\\\\\\\",\\\\\\\"index\\\\\\\":2,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"int\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"type\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_name\\\\\\\",\\\\\\\"index\\\\\\\":3,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_name\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_command\\\\\\\",\\\\\\\"index\\\\\\\":4,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_command\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_create\\\\\\\",\\\\\\\"index\\\\\\\":5,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_create\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_modified\\\\\\\",\\\\\\\"index\\\\\\\":6,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_modified\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"is_deleted\\\\\\\",\\\\\\\"index\\\\\\\":7,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"tinyint\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"is_deleted\\\\\\\"}],\\\\\\\"writeMode\\\\\\\":\\\\\\\"overwrite\\\\\\\",\\\\\\\"fieldDelimiter\\\\\\\":\\\\\\\"\\\\\\\\u0001\\\\\\\",\\\\\\\"encoding\\\\\\\":\\\\\\\"utf-8\\\\\\\",\\\\\\\"fullColumnName\\\\\\\":[\\\\\\\"id\\\\\\\",\\\\\\\"task_id\\\\\\\",\\\\\\\"type\\\\\\\",\\\\\\\"param_name\\\\\\\",\\\\\\\"param_command\\\\\\\",\\\\\\\"gmt_create\\\\\\\",\\\\\\\"gmt_modified\\\\\\\",\\\\\\\"is_deleted\\\\\\\"],\\\\\\\"path\\\\\\\":\\\\\\\"hdfs://ns1/user/hive/warehouse/dev.db/rdos_batch_task_param1\\\\\\\",\\\\\\\"partition\\\\\\\":\\\\\\\"pt=2020\\\\\\\",\\\\\\\"hadoopConfig\\\\\\\":{\\\\\\\"javax.jdo.option.ConnectionDriverName\\\\\\\":\\\\\\\"com.mysql.jdbc.Driver\\\\\\\",\\\\\\\"dfs.replication\\\\\\\":\\\\\\\"2\\\\\\\",\\\\\\\"dfs.ha.fencing.ssh.private-key-files\\\\\\\":\\\\\\\"~/.ssh/id_rsa\\\\\\\",\\\\\\\"dfs.nameservices\\\\\\\":\\\\\\\"ns1\\\\\\\",\\\\\\\"dfs.safemode.threshold.pct\\\\\\\":\\\\\\\"0.5\\\\\\\",\\\\\\\"dfs.ha.namenodes.ns1\\\\\\\":\\\\\\\"nn1,nn2\\\\\\\",\\\\\\\"dfs.journalnode.rpc-address\\\\\\\":\\\\\\\"0.0.0.0:8485\\\\\\\",\\\\\\\"dfs.journalnode.http-address\\\\\\\":\\\\\\\"0.0.0.0:8480\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:9000\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:9000\\\\\\\",\\\\\\\"hive.metastore.warehouse.dir\\\\\\\":\\\\\\\"/user/hive/warehouse\\\\\\\",\\\\\\\"hive.server2.webui.host\\\\\\\":\\\\\\\"172.16.10.34\\\\\\\",\\\\\\\"hive.metastore.schema.verification\\\\\\\":\\\\\\\"false\\\\\\\",\\\\\\\"hive.server2.support.dynamic.service.discovery\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionPassword\\\\\\\":\\\\\\\"abc123\\\\\\\",\\\\\\\"hive.metastore.uris\\\\\\\":\\\\\\\"thrift://kudu1:9083\\\\\\\",\\\\\\\"hive.exec.dynamic.partition.mode\\\\\\\":\\\\\\\"nonstrict\\\\\\\",\\\\\\\"hadoop.proxyuser.admin.hosts\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"hive.zookeeper.quorum\\\\\\\":\\\\\\\"kudu1:2181,kudu2:2181,kudu3:2181\\\\\\\",\\\\\\\"ha.zookeeper.quorum\\\\\\\":\\\\\\\"kudu1:2181,kudu2:2181,kudu3:2181\\\\\\\",\\\\\\\"hive.server2.thrift.min.worker.threads\\\\\\\":\\\\\\\"200\\\\\\\",\\\\\\\"hive.server2.webui.port\\\\\\\":\\\\\\\"10002\\\\\\\",\\\\\\\"fs.defaultFS\\\\\\\":\\\\\\\"hdfs://ns1\\\\\\\",\\\\\\\"hadoop.proxyuser.admin.groups\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"dfs.ha.fencing.methods\\\\\\\":\\\\\\\"sshfence\\\\\\\",\\\\\\\"dfs.client.failover.proxy.provider.ns1\\\\\\\":\\\\\\\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\\\\\",\\\\\\\"typeName\\\\\\\":\\\\\\\"yarn2-hdfs2-hadoop2\\\\\\\",\\\\\\\"hadoop.proxyuser.root.groups\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionURL\\\\\\\":\\\\\\\"jdbc:mysql://kudu2:3306/ide?useSSL=false\\\\\\\",\\\\\\\"dfs.qjournal.write-txns.timeout.ms\\\\\\\":\\\\\\\"60000\\\\\\\",\\\\\\\"fs.trash.interval\\\\\\\":\\\\\\\"30\\\\\\\",\\\\\\\"hadoop.proxyuser.root.hosts\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"dfs.namenode.shared.edits.dir\\\\\\\":\\\\\\\"qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionUserName\\\\\\\":\\\\\\\"dtstack\\\\\\\",\\\\\\\"hive.server2.thrift.port\\\\\\\":\\\\\\\"10000\\\\\\\",\\\\\\\"ha.zookeeper.session-timeout.ms\\\\\\\":\\\\\\\"5000\\\\\\\",\\\\\\\"hadoop.tmp.dir\\\\\\\":\\\\\\\"/data/hadoop_${user.name}\\\\\\\",\\\\\\\"dfs.journalnode.edits.dir\\\\\\\":\\\\\\\"/data/dtstack/hadoop/journal\\\\\\\",\\\\\\\"hive.server2.zookeeper.namespace\\\\\\\":\\\\\\\"hiveserver2\\\\\\\",\\\\\\\"hive.server2.enable.doAs\\\\\\\":\\\\\\\"/false\\\\\\\",\\\\\\\"dfs.namenode.http-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:50070\\\\\\\",\\\\\\\"dfs.namenode.http-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:50070\\\\\\\",\\\\\\\"md5zip\\\\\\\":\\\\\\\"667e20cff7e9f2636fddc1440a9f5728\\\\\\\",\\\\\\\"hive.exec.scratchdir\\\\\\\":\\\\\\\"/user/hive/warehouse\\\\\\\",\\\\\\\"hive.server2.webui.max.threads\\\\\\\":\\\\\\\"100\\\\\\\",\\\\\\\"datanucleus.schema.autoCreateAll\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"hive.exec.dynamic.partition\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"hive.server2.thrift.bind.host\\\\\\\":\\\\\\\"kudu1\\\\\\\",\\\\\\\"dfs.ha.automatic-failover.enabled\\\\\\\":\\\\\\\"true\\\\\\\"},\\\\\\\"defaultFS\\\\\\\":\\\\\\\"hdfs://ns1\\\\\\\",\\\\\\\"connection\\\\\\\":[{\\\\\\\"jdbcUrl\\\\\\\":\\\\\\\"jdbc:hive2://172.16.8.107:10000/dev\\\\\\\",\\\\\\\"table\\\\\\\":[\\\\\\\"rdos_batch_task_param1\\\\\\\"]}],\\\\\\\"fileType\\\\\\\":\\\\\\\"parquet\\\\\\\",\\\\\\\"sourceIds\\\\\\\":[37],\\\\\\\"username\\\\\\\":\\\\\\\"admin\\\\\\\",\\\\\\\"fullColumnType\\\\\\\":[\\\\\\\"int\\\\\\\",\\\\\\\"int\\\\\\\",\\\\\\\"int\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"tinyint\\\\\\\"]},\\\\\\\"name\\\\\\\":\\\\\\\"hdfswriter\\\\\\\"}}],\\\\\\\"setting\\\\\\\":{\\\\\\\"dirty\\\\\\\":{\\\\\\\"path\\\\\\\":\\\\\\\"null/task_name=testInc/time=1608962546892\\\\\\\",\\\\\\\"hadoopConfig\\\\\\\":{\\\\\\\"dfs.ha.namenodes.ns1\\\\\\\":\\\\\\\"nn1,nn2\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:9000\\\\\\\",\\\\\\\"dfs.client.failover.proxy.provider.ns1\\\\\\\":\\\\\\\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:9000\\\\\\\",\\\\\\\"dfs.nameservices\\\\\\\":\\\\\\\"ns1\\\\\\\"},\\\\\\\"tableName\\\\\\\":\\\\\\\"dev.dirty_testInc\\\\\\\"},\\\\\\\"restore\\\\\\\":{\\\\\\\"maxRowNumForCheckpoint\\\\\\\":0,\\\\\\\"isRestore\\\\\\\":false,\\\\\\\"restoreColumnName\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"restoreColumnIndex\\\\\\\":0},\\\\\\\"errorLimit\\\\\\\":{\\\\\\\"record\\\\\\\":100},\\\\\\\"speed\\\\\\\":{\\\\\\\"bytes\\\\\\\":0,\\\\\\\"channel\\\\\\\":1}}}}\\\",\\\"dataSourceType\\\":7,\\\"taskId\\\":391}\"}");
        JSONObject jsonObject = JSONObject.parseObject(scheduleTaskShade.getExtraInfo());
        JSONObject info = jsonObject.getJSONObject("info");
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        ScheduleJob job = new ScheduleJob();
        job.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        job.setCycTime("20220228023500");
        job.setJobId("4hsohrrlo7n0");
        try {
            syncJobStartTrigger.readyForTaskStartTrigger(actionParam, scheduleTaskShade, job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() {


        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.setKeepAliveSeconds(0);
        threadPoolTaskExecutor.setQueueCapacity(5);
        threadPoolTaskExecutor.initialize();
        PrivateAccessor.set(syncJobStartTrigger, "commonExecutor", threadPoolTaskExecutor);
    }

    public static class Mock extends BaseMock {

        @MockInvoke(targetClass = JobChainParamHandler.class)
        public JobChainParamHandleResult handle(String sql, ScheduleTaskShade taskShade, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
            JobChainParamHandleResult result = new JobChainParamHandleResult();
            result.setSql(sql);
            result.setTaskParams(taskShade.getTaskParams());
            return result;
        }

        @MockInvoke(targetClass = JobParamReplace.class)
        public String paramReplace(String sql,
                                   List<ScheduleTaskParamShade> paramList,
                                   String cycTime,
                                   Integer scheduleType) {
            return sql;
        }


        @MockInvoke(
                targetClass = PluginInfoToSourceDTO.class,
                targetMethod = "getSourceDTO"
        )
        public static ISourceDTO getSourceDTO(String data, Long dtUicTenantId) {
            return new ISourceDTO() {
                @Override
                public String getUsername() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public Integer getSourceType() {
                    return null;
                }

                @Override
                public Long getTenantId() {
                    return null;
                }

                @Override
                public void setTenantId(Long aLong) {

                }
            };
        }

        @MockInvoke(
                targetClass = ClientCache.class,
                targetMethod = "getClient"
        )
        public static IClient getClient(Integer dataSourceType) {
            return new IClient() {
                @Override
                public List<RoleTableGrantsDTO> getRoleTableGrantsInfo(ISourceDTO sourceDTO, RoleTableGrantsParamDTO roleTableGrantsParamDTO) {
                    return null;
                }

                @Override
                public Boolean testCon(ISourceDTO iSourceDTO) {
                    return null;
                }

                @Override
                public List<Map<String, Object>> executeQuery(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return new ArrayList<>();
                }

                @Override
                public Boolean executeBatchQuery(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return true;
                }

                @Override
                public Map<String, List<Map<String, Object>>> executeMultiQuery(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public Integer executeUpdate(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public Boolean executeSqlWithoutResultSet(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<String> getTableList(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<TableInfo> getTableListWithView(ISourceDTO source, SqlQueryDTO queryDTO) {
                    return null;
                }

                @Override
                public List<String> getTableListBySchema(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<String> getColumnClassInfo(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<ColumnMetaDTO> getColumnMetaDataWithSql(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public String getTableMetaComment(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<List<Object>> getPreview(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<List<Object>> executeSelectQuery(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public IDownloader getDownloader(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) throws Exception {
                    return null;
                }

                @Override
                public IDownloader getDownloader(ISourceDTO iSourceDTO, String s, Integer integer) throws Exception {
                    return null;
                }

                @Override
                public List<String> getAllDatabases(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<String> getRootDatabases(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public String getCreateTableSql(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public Table getTable(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public String getCurrentDatabase(ISourceDTO iSourceDTO) {
                    return null;
                }

                @Override
                public Boolean createDatabase(ISourceDTO iSourceDTO, String s, String s1) {
                    return null;
                }

                @Override
                public Boolean isDatabaseExists(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public Boolean isTableExistsInDatabase(ISourceDTO iSourceDTO, String s, String s1) {
                    return null;
                }

                @Override
                public List<String> getCatalogs(ISourceDTO iSourceDTO) {
                    return null;
                }

                @Override
                public String getVersion(ISourceDTO iSourceDTO) {
                    return null;
                }

                @Override
                public List<String> listFileNames(ISourceDTO iSourceDTO, String s, Boolean aBoolean, Boolean aBoolean1, Integer integer, String s1) {
                    return null;
                }

                @Override
                public Database getDatabase(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public Boolean writeByFile(ISourceDTO iSourceDTO, WriteFileDTO writeFileDTO) {
                    return null;
                }

                @Override
                public TableInfo getTableInfo(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public MetaDataCollectManager getMetadataCollectManager(ISourceDTO iSourceDTO) {
                    return null;
                }

                @Override
                public Boolean dropSchema(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public void releaseCacheConnect(ISourceDTO iSourceDTO, String s) {

                }

                // @Override
                // public List<RoleTableGrantsDTO> getRoleTableGrantsInfo(ISourceDTO iSourceDTO, RoleTableGrantsParamDTO roleTableGrantsParamDTO) {
                //     return null;
                // }

                @Override
                public List<MetadataEntity> metadataCollect(ISourceDTO sourceDTO, MetadataCollectCondition metadataCollectDTO, ISourceDTO storeSourceDTO) {
                    return IClient.super.metadataCollect(sourceDTO, metadataCollectDTO, storeSourceDTO);
                }
            };
        }

        @MockInvoke(
                targetClass = DataSecurityChecker.class,
                targetMethod = "preCheckSync"
        )
        public void preCheckSync(PreCheckSyncDTO preCheckSyncDTO, JSONObject jobJson, ScheduleTaskShade taskShade, ScheduleJob scheduleJob,
                                 List<ScheduleTaskParamShade> taskParamsToReplace, Long userId, Long dtUicTenantId) {
        }

        @MockInvoke(
                targetClass = ClusterService.class,
                targetMethod = "getCluster"
        )
        public Cluster getCluster(Long dtUicTenantId) {
            Cluster cluster = new Cluster();
            cluster.setId(-1L);
            return cluster;
        }

        @MockInvoke(
                targetClass = ComponentService.class,
                targetMethod = "getComponentByClusterId"
        )
        public Component getComponentByClusterId(Long clusterId, Integer componentType,
                                                 String componentVersion) {

            Component component = new Component();
            component.setHadoopVersion("110");
            return component;
        }

        @MockInvoke(
                targetClass = ComponentService.class,
                targetMethod = "getMetadataComponent"
        )
        public Component getMetadataComponent(Long clusterId) {
            return null;
        }

        @MockInvoke(
                targetClass = ClusterService.class,
                targetMethod = "getConfigByKey"
        )
        public String getConfigByKey(Long dtUicTenantId, String componentConfName, Boolean fullKerberos,
                                     Map<Integer, String> componentVersionMap, boolean needHdfsConfig) {
            return "{}";
        }

        @MockInvoke(
                targetClass = ScheduleJobDao.class,
                targetMethod = "getByTaskIdAndStatusOrderByIdLimit"
        )
        public ScheduleJob getByTaskIdAndStatusOrderByIdLimit(Long taskId, Integer status,
                                                              Timestamp time, Integer appType, Integer type) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setExecStartTime(Timestamp.from(Instant.now()));
            scheduleJob.setExecEndTime(Timestamp.from(Instant.now()));
            scheduleJob.setStatus(5);
            scheduleJob.setEngineJobId("test");
            return scheduleJob;
        }


        @MockInvoke(
                targetClass = ComponentService.class,
                targetMethod = "listConfigOfComponents"
        )
        public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long dtUicTenantId,
                                                                           Integer engineType, Map<Integer, String> componentVersionMap) {

            ComponentsConfigOfComponentsVO componentsConfigOfComponentsVO = new ComponentsConfigOfComponentsVO();
            componentsConfigOfComponentsVO.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(EDeployMode.SESSION.getMode(), new JSONObject());
            componentsConfigOfComponentsVO.setComponentConfig(jsonObject.toJSONString());
            return Lists.newArrayList(componentsConfigOfComponentsVO);
        }

        @MockInvoke(
                targetClass = TaskParamsService.class,
                targetMethod = "parseDeployTypeByTaskParams"
        )
        public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType,
                                                       String engineType, Long tenantId) {
            return EDeployMode.SESSION;
        }

        @MockInvoke(
                targetClass = IMetric.class,
                targetMethod = "getMetric"
        )
        public Object getMetric() {
            return 1;
        }

        @MockInvoke(
                targetClass = ColumnChecker.class,
                targetMethod = "checkColumn"
        )
        public String checkColumn(List<Column> columnList, Long datasourceId, String table, String schema, Integer dataSourceType) {
            return "元数据发生变更 (" + table + ")";
        }

        @MockInvoke(
                targetClass = AlterEvent.class,
                targetMethod = "event"
        )
        public void event(final AlterEventContext context) {
        }
    }


    @Test
    public void checkJob() {

        Map<String, Object> actionParam = new HashMap<>();
        actionParam.put("metaDataVerification", "{\"reader\":true,\"readerSourceId\":455,\"writerColumnInfoList\":[{\"name\":\"company_id\",\"type\":\"string\"},{\"name\":\"company_title\",\"type\":\"string\"}],\"readerColumnInfoList\":[{\"name\":\"company_id\",\"type\":\"INT UNSIGNED\"},{\"name\":\"company_title\",\"type\":\"VARCHAR\"}],\"writerSourceId\":17,\"writer\":true}");
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        ScheduleJob scheduleJob = new ScheduleJob();
        String json = "{\"job\":{\"content\":[{\"reader\":{\"parameter\":{\"password\":\"Mysql@123\",\"customSql\":\"\",\"startLocation\":\"\",\"dtCenterSourceId\":455,\"increColumn\":\"\",\"metadataVerification\":true,\"column\":[{\"customConverterType\":\"INT UNSIGNED\",\"name\":\"company_id\",\"isPart\":false,\"type\":\"INT UNSIGNED\",\"key\":\"company_id\"},{\"customConverterType\":\"VARCHAR\",\"name\":\"company_title\",\"isPart\":false,\"type\":\"VARCHAR\",\"key\":\"company_title\"}],\"connection\":[{\"schema\":\"tester\",\"sourceId\":501,\"password\":\"Mysql@123\",\"jdbcUrl\":[\"jdbc:mysql://172.16.100.186:3306/developer\"],\"type\":1,\"table\":[\"company\"],\"username\":\"developer\"}],\"sourceIds\":[501],\"username\":\"developer\"},\"name\":\"mysqlreader\"},\"writer\":{\"parameter\":{\"schema\":\"batch_test\",\"fileName\":\"\",\"dtCenterSourceId\":17,\"metadataVerification\":true,\"column\":[{\"customConverterType\":\"string\",\"name\":\"company_id\",\"index\":0,\"isPart\":false,\"type\":\"string\",\"key\":\"company_id\"},{\"customConverterType\":\"string\",\"name\":\"company_title\",\"index\":1,\"isPart\":false,\"type\":\"string\",\"key\":\"company_title\"}],\"writeMode\":\"overwrite\",\"encoding\":\"utf-8\",\"fullColumnName\":[\"company_id\",\"company_title\"],\"path\":\"hdfs://ns1/dtInsight/hive/warehouse/batch_test.db/company\",\"defaultFS\":\"hdfs://ns1\",\"connection\":[{\"jdbcUrl\":\"jdbc:hive2://172.16.85.248:10004/batch_test\",\"table\":[\"company\"]}],\"table\":\"company\",\"fileType\":\"orc\",\"sourceIds\":[1],\"fullColumnType\":[\"string\",\"string\"]},\"name\":\"hdfswriter\"}}],\"setting\":{\"restore\":{\"maxRowNumForCheckpoint\":0,\"isRestore\":false,\"restoreColumnName\":\"\",\"restoreColumnIndex\":0},\"errorLimit\":{\"record\":7654,\"percentage\":88},\"speed\":{\"readerChannel\":1,\"writerChannel\":1,\"bytes\":0}}}}";
        try {
            syncJobStartTrigger.checkJob(json, actionParam, scheduleTaskShade, scheduleJob, new ArrayList<>());
        } catch (Exception e) {

        }
    }
}
