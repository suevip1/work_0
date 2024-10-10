package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.FileFindDTO;
import com.dtstack.dtcenter.loader.dto.FileFindResultDTO;
import com.dtstack.dtcenter.loader.dto.FileStatus;
import com.dtstack.dtcenter.loader.dto.HDFSContentSummary;
import com.dtstack.dtcenter.loader.dto.HdfsQueryDTO;
import com.dtstack.dtcenter.loader.dto.HdfsWriterDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.enums.FileFormat;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.CheckpointType;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.StreamTaskService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.po.ScheduleJobHistory;
import com.dtstack.rpc.download.IDownloader;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MockWith(FlinkFileDeleteCronTestMock.class)
public class FlinkFileDeleteCronTest {

    FlinkFileDeleteCron deleteCron = new FlinkFileDeleteCron();

    @Before
    public void init() {
        PrivateAccessor.set(deleteCron, "flinkJarFilePath", "/user/${username}/.flink");
        Map<String, String> versionValCache = new HashMap<>();
        versionValCache.put("1.12", "112");
        PrivateAccessor.set(deleteCron, "versionValCache", versionValCache);
    }
    
    @Test
    public void testParseLog(){
        String log = "{\n" +
                "    \"archive\":[\n" +
                "        {\n" +
                "            \"path\":\"/jobs/overview\",\n" +
                "            \"json\":\"{\\\"jobs\\\":[{\\\"jid\\\":\\\"fc8f26387b66a8b395407e6d4b16809b\\\",\\\"name\\\":\\\"streamwork614_FlinkSQL112_test_4n852tnau3p0\\\",\\\"state\\\":\\\"CANCELED\\\",\\\"start-time\\\":1703147652981,\\\"end-time\\\":1703231036476,\\\"duration\\\":83383495,\\\"last-modification\\\":1703231036476,\\\"tasks\\\":{\\\"total\\\":2,\\\"created\\\":0,\\\"scheduled\\\":0,\\\"deploying\\\":0,\\\"running\\\":0,\\\"finished\\\":0,\\\"canceling\\\":0,\\\"canceled\\\":2,\\\"failed\\\":0,\\\"reconciling\\\":0}}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/config\",\n" +
                "            \"json\":\"{\\\"jid\\\":\\\"fc8f26387b66a8b395407e6d4b16809b\\\",\\\"name\\\":\\\"streamwork614_FlinkSQL112_test_4n852tnau3p0\\\",\\\"execution-config\\\":{\\\"execution-mode\\\":\\\"PIPELINED\\\",\\\"restart-strategy\\\":\\\"Restart deactivated.\\\",\\\"job-parallelism\\\":1,\\\"object-reuse-mode\\\":false,\\\"user-config\\\":{\\\"execution.checkpointing.interval\\\":\\\"1min\\\",\\\"taskmanager.memory.process.size\\\":\\\"2g\\\",\\\"table.exec.state.ttl\\\":\\\"1d\\\",\\\"logLevel\\\":\\\"INFO\\\",\\\"python.executable\\\":\\\"python3\\\",\\\"execution.checkpointing.externalized-checkpoint-retention\\\":\\\"RETAIN_ON_CANCELLATION\\\",\\\"parallelism.default\\\":\\\"1\\\",\\\"taskmanager.numberOfTaskSlots\\\":\\\"1\\\",\\\"jobmanager.memory.process.size\\\":\\\"1g\\\",\\\"python.client.executable\\\":\\\"python3\\\",\\\"tenant.id\\\":\\\"10451\\\"}}}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/config\",\n" +
                "            \"json\":\"{\\\"mode\\\":\\\"exactly_once\\\",\\\"interval\\\":60000,\\\"timeout\\\":600000,\\\"min_pause\\\":0,\\\"max_concurrent\\\":1,\\\"externalization\\\":{\\\"enabled\\\":true,\\\"delete_on_cancellation\\\":false},\\\"state_backend\\\":\\\"RocksDBStateBackend\\\",\\\"unaligned_checkpoints\\\":false,\\\"tolerable_failed_checkpoints\\\":0}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints\",\n" +
                "            \"json\":\"{\\\"counts\\\":{\\\"restored\\\":1,\\\"total\\\":1389,\\\"in_progress\\\":0,\\\"completed\\\":1389,\\\"failed\\\":0},\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":15936,\\\"max\\\":15936,\\\"avg\\\":15936},\\\"end_to_end_duration\\\":{\\\"min\\\":65,\\\"max\\\":1095,\\\"avg\\\":85},\\\"alignment_buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed_data\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted_data\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"latest\\\":{\\\"completed\\\":{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1736,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230941628,\\\"latest_ack_timestamp\\\":1703230941704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1736\\\",\\\"discarded\\\":false},\\\"savepoint\\\":{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1737,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":true,\\\"trigger_timestamp\\\":1703230995782,\\\"latest_ack_timestamp\\\":1703230995910,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":128,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"SAVEPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/savepoints/savepoint-fc8f26-0692bea22eff\\\",\\\"discarded\\\":false},\\\"failed\\\":null,\\\"restored\\\":{\\\"id\\\":348,\\\"restore_timestamp\\\":1703147653765,\\\"is_savepoint\\\":true,\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/c52a65df1e8c1a4b55f3891a570bb0f5/chk-348\\\"}},\\\"history\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1737,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":true,\\\"trigger_timestamp\\\":1703230995782,\\\"latest_ack_timestamp\\\":1703230995910,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":128,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"SAVEPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/savepoints/savepoint-fc8f26-0692bea22eff\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1736,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230941628,\\\"latest_ack_timestamp\\\":1703230941704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1736\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1735,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230881628,\\\"latest_ack_timestamp\\\":1703230881710,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":82,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1735\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1734,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230821628,\\\"latest_ack_timestamp\\\":1703230821704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1734\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1733,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230761629,\\\"latest_ack_timestamp\\\":1703230761713,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":84,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1733\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1732,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230701629,\\\"latest_ack_timestamp\\\":1703230701716,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":87,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1732\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1731,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230641628,\\\"latest_ack_timestamp\\\":1703230641709,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":81,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1731\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1730,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230581629,\\\"latest_ack_timestamp\\\":1703230581740,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":111,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1730\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1729,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230521628,\\\"latest_ack_timestamp\\\":1703230521700,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":72,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1729\\\",\\\"discarded\\\":false},{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1728,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230461629,\\\"latest_ack_timestamp\\\":1703230461705,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1728\\\",\\\"discarded\\\":false}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1737\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1737,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":true,\\\"trigger_timestamp\\\":1703230995782,\\\"latest_ack_timestamp\\\":1703230995910,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":128,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"SAVEPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1737,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230995910,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":128,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/savepoints/savepoint-fc8f26-0692bea22eff\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1736\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1736,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230941628,\\\"latest_ack_timestamp\\\":1703230941704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1736,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230941704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1736\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1735\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1735,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230881628,\\\"latest_ack_timestamp\\\":1703230881710,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":82,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1735,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230881710,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":82,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1735\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1734\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1734,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230821628,\\\"latest_ack_timestamp\\\":1703230821704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1734,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230821704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1734\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1733\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1733,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230761629,\\\"latest_ack_timestamp\\\":1703230761713,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":84,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1733,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230761713,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":84,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1733\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1732\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1732,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230701629,\\\"latest_ack_timestamp\\\":1703230701716,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":87,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1732,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230701716,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":87,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1732\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1731\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1731,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230641628,\\\"latest_ack_timestamp\\\":1703230641709,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":81,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1731,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230641709,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":81,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1731\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1730\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1730,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230581629,\\\"latest_ack_timestamp\\\":1703230581740,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":111,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1730,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230581740,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":111,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1730\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1729\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1729,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230521628,\\\"latest_ack_timestamp\\\":1703230521700,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":72,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1729,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230521700,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":72,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1729\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1728\",\n" +
                "            \"json\":\"{\\\"@class\\\":\\\"completed\\\",\\\"id\\\":1728,\\\"status\\\":\\\"COMPLETED\\\",\\\"is_savepoint\\\":false,\\\"trigger_timestamp\\\":1703230461629,\\\"latest_ack_timestamp\\\":1703230461705,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"checkpoint_type\\\":\\\"CHECKPOINT\\\",\\\"tasks\\\":{\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\":{\\\"id\\\":1728,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230461705,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2}},\\\"external_path\\\":\\\"hdfs://ns1/dtInsight/flink112/checkpoints/fc8f26387b66a8b395407e6d4b16809b/chk-1728\\\",\\\"discarded\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/plan\",\n" +
                "            \"json\":\"{\\\"plan\\\":{\\\"jid\\\":\\\"fc8f26387b66a8b395407e6d4b16809b\\\",\\\"name\\\":\\\"streamwork614_FlinkSQL112_test_4n852tnau3p0\\\",\\\"nodes\\\":[{\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"parallelism\\\":2,\\\"operator\\\":\\\"\\\",\\\"operator_strategy\\\":\\\"\\\",\\\"description\\\":\\\"Source: TableSourceScan(table=[[default_catalog, default_database, sourceTable]], fields=[id, name, age]) -> Sink: Sink(table=[default_catalog.default_database.mysqlResultTable], fields=[id, name, age])\\\",\\\"optimizer_properties\\\":{}}]}}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1737/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1737,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230995910,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":128,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":120,\\\"max\\\":128,\\\"avg\\\":124},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":7,\\\"max\\\":13,\\\"avg\\\":10},\\\"async\\\":{\\\"min\\\":5,\\\"max\\\":6,\\\"avg\\\":5}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":95,\\\"avg\\\":47}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230995902,\\\"end_to_end_duration\\\":120,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":13,\\\"async\\\":6},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":95,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230995910,\\\"end_to_end_duration\\\":128,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":7,\\\"async\\\":5},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1736/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1736,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230941704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":74,\\\"max\\\":76,\\\"avg\\\":75},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":3,\\\"max\\\":7,\\\"avg\\\":5},\\\"async\\\":{\\\"min\\\":5,\\\"max\\\":6,\\\"avg\\\":5}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":59,\\\"avg\\\":29}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230941702,\\\"end_to_end_duration\\\":74,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":3,\\\"async\\\":6},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":59,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230941704,\\\"end_to_end_duration\\\":76,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":7,\\\"async\\\":5},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1735/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1735,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230881710,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":82,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":80,\\\"max\\\":82,\\\"avg\\\":81},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":5,\\\"max\\\":7,\\\"avg\\\":6},\\\"async\\\":{\\\"min\\\":8,\\\"max\\\":11,\\\"avg\\\":9}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":58,\\\"avg\\\":29}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230881708,\\\"end_to_end_duration\\\":80,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":7,\\\"async\\\":8},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":58,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230881710,\\\"end_to_end_duration\\\":82,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":5,\\\"async\\\":11},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1734/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1734,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230821704,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":74,\\\"max\\\":76,\\\"avg\\\":75},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":5,\\\"max\\\":6,\\\"avg\\\":5},\\\"async\\\":{\\\"min\\\":5,\\\"max\\\":6,\\\"avg\\\":5}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":59,\\\"avg\\\":29}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230821702,\\\"end_to_end_duration\\\":74,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":5,\\\"async\\\":5},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":59,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230821704,\\\"end_to_end_duration\\\":76,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":6,\\\"async\\\":6},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1733/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1733,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230761713,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":84,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":82,\\\"max\\\":84,\\\"avg\\\":83},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":7,\\\"max\\\":8,\\\"avg\\\":7},\\\"async\\\":{\\\"min\\\":6,\\\"max\\\":7,\\\"avg\\\":6}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":60,\\\"avg\\\":30}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230761711,\\\"end_to_end_duration\\\":82,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":8,\\\"async\\\":6},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":60,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230761713,\\\"end_to_end_duration\\\":84,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":7,\\\"async\\\":7},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1732/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1732,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230701716,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":87,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":78,\\\"max\\\":87,\\\"avg\\\":82},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":2,\\\"max\\\":6,\\\"avg\\\":4},\\\"async\\\":{\\\"min\\\":5,\\\"max\\\":7,\\\"avg\\\":6}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":66,\\\"avg\\\":33}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230701707,\\\"end_to_end_duration\\\":78,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":2,\\\"async\\\":5},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":66,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230701716,\\\"end_to_end_duration\\\":87,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":6,\\\"async\\\":7},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1731/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1731,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230641709,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":81,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":77,\\\"max\\\":81,\\\"avg\\\":79},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":7,\\\"max\\\":7,\\\"avg\\\":7},\\\"async\\\":{\\\"min\\\":7,\\\"max\\\":9,\\\"avg\\\":8}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":58,\\\"avg\\\":29}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230641705,\\\"end_to_end_duration\\\":77,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":7,\\\"async\\\":7},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":58,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230641709,\\\"end_to_end_duration\\\":81,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":7,\\\"async\\\":9},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1730/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1730,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230581740,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":111,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":110,\\\"max\\\":111,\\\"avg\\\":110},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":16,\\\"max\\\":24,\\\"avg\\\":20},\\\"async\\\":{\\\"min\\\":8,\\\"max\\\":11,\\\"avg\\\":9}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":71,\\\"avg\\\":35}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230581740,\\\"end_to_end_duration\\\":111,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":24,\\\"async\\\":11},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":71,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230581739,\\\"end_to_end_duration\\\":110,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":16,\\\"async\\\":8},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1729/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1729,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230521700,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":72,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":66,\\\"max\\\":72,\\\"avg\\\":69},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":4,\\\"max\\\":4,\\\"avg\\\":4},\\\"async\\\":{\\\"min\\\":2,\\\"max\\\":7,\\\"avg\\\":4}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":55,\\\"avg\\\":27}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230521694,\\\"end_to_end_duration\\\":66,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":4,\\\"async\\\":2},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":55,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230521700,\\\"end_to_end_duration\\\":72,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":4,\\\"async\\\":7},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/checkpoints/details/1728/subtasks/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":1728,\\\"status\\\":\\\"COMPLETED\\\",\\\"latest_ack_timestamp\\\":1703230461705,\\\"state_size\\\":15936,\\\"end_to_end_duration\\\":76,\\\"alignment_buffered\\\":0,\\\"processed_data\\\":0,\\\"persisted_data\\\":0,\\\"num_subtasks\\\":2,\\\"num_acknowledged_subtasks\\\":2,\\\"summary\\\":{\\\"state_size\\\":{\\\"min\\\":7957,\\\"max\\\":7979,\\\"avg\\\":7968},\\\"end_to_end_duration\\\":{\\\"min\\\":75,\\\"max\\\":76,\\\"avg\\\":75},\\\"checkpoint_duration\\\":{\\\"sync\\\":{\\\"min\\\":4,\\\"max\\\":5,\\\"avg\\\":4},\\\"async\\\":{\\\"min\\\":6,\\\"max\\\":7,\\\"avg\\\":6}},\\\"alignment\\\":{\\\"buffered\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"processed\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"persisted\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0},\\\"duration\\\":{\\\"min\\\":0,\\\"max\\\":0,\\\"avg\\\":0}},\\\"start_delay\\\":{\\\"min\\\":0,\\\"max\\\":57,\\\"avg\\\":28}},\\\"subtasks\\\":[{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":0,\\\"ack_timestamp\\\":1703230461705,\\\"end_to_end_duration\\\":76,\\\"state_size\\\":7957,\\\"checkpoint\\\":{\\\"sync\\\":5,\\\"async\\\":7},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":57,\\\"status\\\":\\\"completed\\\"},{\\\"@class\\\":\\\"completed\\\",\\\"index\\\":1,\\\"ack_timestamp\\\":1703230461704,\\\"end_to_end_duration\\\":75,\\\"state_size\\\":7979,\\\"checkpoint\\\":{\\\"sync\\\":4,\\\"async\\\":6},\\\"alignment\\\":{\\\"buffered\\\":0,\\\"processed\\\":0,\\\"persisted\\\":0,\\\"duration\\\":0},\\\"start_delay\\\":0,\\\"status\\\":\\\"completed\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/exceptions\",\n" +
                "            \"json\":\"{\\\"root-exception\\\":null,\\\"timestamp\\\":null,\\\"all-exceptions\\\":[],\\\"truncated\\\":false}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b\",\n" +
                "            \"json\":\"{\\\"jid\\\":\\\"fc8f26387b66a8b395407e6d4b16809b\\\",\\\"name\\\":\\\"streamwork614_FlinkSQL112_test_4n852tnau3p0\\\",\\\"isStoppable\\\":false,\\\"state\\\":\\\"CANCELED\\\",\\\"start-time\\\":1703147652981,\\\"end-time\\\":1703231036476,\\\"duration\\\":83383495,\\\"now\\\":1703231036803,\\\"timestamps\\\":{\\\"RESTARTING\\\":0,\\\"CANCELLING\\\":1703230996078,\\\"RUNNING\\\":1703147653804,\\\"CANCELED\\\":1703231036476,\\\"CREATED\\\":1703147653097,\\\"INITIALIZING\\\":1703147652981,\\\"SUSPENDED\\\":0,\\\"FAILED\\\":0,\\\"FAILING\\\":0,\\\"FINISHED\\\":0,\\\"RECONCILING\\\":0},\\\"vertices\\\":[{\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"name\\\":\\\"Source: TableSourceScan(table=[[default_catalog, default_database, sourceTable]], fields=[id, name, age]) -> Sink: Sink(table=[default_catalog.default_database.mysqlResultTable], fields=[id, name, age])\\\",\\\"parallelism\\\":2,\\\"status\\\":\\\"CANCELED\\\",\\\"start-time\\\":1703147664975,\\\"end-time\\\":1703231036475,\\\"duration\\\":83371500,\\\"tasks\\\":{\\\"FINISHED\\\":0,\\\"CANCELED\\\":2,\\\"DEPLOYING\\\":0,\\\"RECONCILING\\\":0,\\\"FAILED\\\":0,\\\"CREATED\\\":0,\\\"RUNNING\\\":0,\\\"CANCELING\\\":0,\\\"SCHEDULED\\\":0},\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true}}],\\\"status-counts\\\":{\\\"FINISHED\\\":0,\\\"CANCELED\\\":1,\\\"DEPLOYING\\\":0,\\\"RECONCILING\\\":0,\\\"FAILED\\\":0,\\\"CREATED\\\":0,\\\"RUNNING\\\":0,\\\"CANCELING\\\":0,\\\"SCHEDULED\\\":0},\\\"plan\\\":{\\\"jid\\\":\\\"fc8f26387b66a8b395407e6d4b16809b\\\",\\\"name\\\":\\\"streamwork614_FlinkSQL112_test_4n852tnau3p0\\\",\\\"nodes\\\":[{\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"parallelism\\\":2,\\\"operator\\\":\\\"\\\",\\\"operator_strategy\\\":\\\"\\\",\\\"description\\\":\\\"Source: TableSourceScan(table=[[default_catalog, default_database, sourceTable]], fields=[id, name, age]) -> Sink: Sink(table=[default_catalog.default_database.mysqlResultTable], fields=[id, name, age])\\\",\\\"optimizer_properties\\\":{}}]}}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/accumulators\",\n" +
                "            \"json\":\"{\\\"job-accumulators\\\":[],\\\"user-task-accumulators\\\":[{\\\"name\\\":\\\"numWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"conversionErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"writeDuration\\\",\\\"type\\\":\\\"LongMaximum\\\",\\\"value\\\":\\\"83329685\\\"},{\\\"name\\\":\\\"duplicateErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"numRead\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"InputIsRunning\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"2\\\"},{\\\"name\\\":\\\"errorsBytes\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nReadErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"snapshotWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"otherErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"readDuration\\\",\\\"type\\\":\\\"LongMaximum\\\",\\\"value\\\":\\\"83349105\\\"},{\\\"name\\\":\\\"byteRead\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nWriteErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"byteWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nullErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"OutputIsRunning\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"}],\\\"serialized-user-task-accumulators\\\":{\\\"numWrite\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"writeDuration\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAT3gpV4\\\",\\\"conversionErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"numRead\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"duplicateErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"InputIsRunning\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAJ4\\\",\\\"nReadErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"errorsBytes\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"snapshotWrite\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"readDuration\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAT3znF4\\\",\\\"otherErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"byteRead\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"nWriteErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"byteWrite\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"OutputIsRunning\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"nullErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\",\\\"nErrors\\\":\\\"rO0ABXNyACVvcmcuYXBhY2hlLmZsaW5rLnV0aWwuT3B0aW9uYWxGYWlsdXJlAAAAAAAAAAEDAAFMAAxmYWlsdXJlQ2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTt4cHBzcgAOamF2YS5sYW5nLkxvbmc7i+SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAAAAAAB4\\\"}}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2/subtasktimes\",\n" +
                "            \"json\":\"{\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"name\\\":\\\"Source: TableSourceScan(table=[[default_catalog, default_database, sourceTable]], fields=[id, name, age]) -> Sink: Sink(table=[default_catalog.default_database.mysqlResultTable], fields=[id, name, age])\\\",\\\"now\\\":1703231036815,\\\"subtasks\\\":[{\\\"subtask\\\":0,\\\"host\\\":\\\"172-16-22-103\\\",\\\"duration\\\":83382603,\\\"timestamps\\\":{\\\"FINISHED\\\":0,\\\"CANCELED\\\":1703231036420,\\\"DEPLOYING\\\":1703147664975,\\\"RECONCILING\\\":0,\\\"FAILED\\\":0,\\\"CREATED\\\":1703147653251,\\\"RUNNING\\\":1703147665184,\\\"CANCELING\\\":1703230996278,\\\"SCHEDULED\\\":1703147653817}},{\\\"subtask\\\":1,\\\"host\\\":\\\"172-16-21-107\\\",\\\"duration\\\":83382636,\\\"timestamps\\\":{\\\"FINISHED\\\":0,\\\"CANCELED\\\":1703231036475,\\\"DEPLOYING\\\":1703147665881,\\\"RECONCILING\\\":0,\\\"FAILED\\\":0,\\\"CREATED\\\":1703147653251,\\\"RUNNING\\\":1703147666055,\\\"CANCELING\\\":1703230996286,\\\"SCHEDULED\\\":1703147653839}}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2/subtasks/0/attempts/0\",\n" +
                "            \"json\":\"{\\\"subtask\\\":0,\\\"status\\\":\\\"CANCELED\\\",\\\"attempt\\\":0,\\\"host\\\":\\\"172-16-22-103\\\",\\\"start-time\\\":1703147664975,\\\"end-time\\\":1703231036420,\\\"duration\\\":83371445,\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true},\\\"taskmanager-id\\\":\\\"container_e12_1699432271134_14177_01_000002\\\",\\\"start_time\\\":1703147664975}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2/subtasks/1/attempts/0\",\n" +
                "            \"json\":\"{\\\"subtask\\\":1,\\\"status\\\":\\\"CANCELED\\\",\\\"attempt\\\":0,\\\"host\\\":\\\"172-16-21-107\\\",\\\"start-time\\\":1703147665881,\\\"end-time\\\":1703231036475,\\\"duration\\\":83370594,\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true},\\\"taskmanager-id\\\":\\\"container_e12_1699432271134_14177_01_000003\\\",\\\"start_time\\\":1703147665881}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2/subtasks/0/attempts/0/accumulators\",\n" +
                "            \"json\":\"{\\\"subtask\\\":0,\\\"attempt\\\":0,\\\"id\\\":\\\"f80bbf503b0d0f7a7614d7e71bcae349\\\",\\\"user-accumulators\\\":[{\\\"name\\\":\\\"numWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"writeDuration\\\",\\\"type\\\":\\\"LongMaximum\\\",\\\"value\\\":\\\"83329685\\\"},{\\\"name\\\":\\\"conversionErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"numRead\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"duplicateErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"InputIsRunning\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"1\\\"},{\\\"name\\\":\\\"nReadErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"errorsBytes\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"snapshotWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"readDuration\\\",\\\"type\\\":\\\"LongMaximum\\\",\\\"value\\\":\\\"83349105\\\"},{\\\"name\\\":\\\"otherErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"byteRead\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nWriteErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"byteWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"OutputIsRunning\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nullErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2/subtasks/1/attempts/0/accumulators\",\n" +
                "            \"json\":\"{\\\"subtask\\\":1,\\\"attempt\\\":0,\\\"id\\\":\\\"77cc5c588aab2d08003949861297d661\\\",\\\"user-accumulators\\\":[{\\\"name\\\":\\\"numWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"writeDuration\\\",\\\"type\\\":\\\"LongMaximum\\\",\\\"value\\\":\\\"83327756\\\"},{\\\"name\\\":\\\"conversionErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"numRead\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"duplicateErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"InputIsRunning\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"1\\\"},{\\\"name\\\":\\\"nReadErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"errorsBytes\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"snapshotWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"readDuration\\\",\\\"type\\\":\\\"LongMaximum\\\",\\\"value\\\":\\\"83347251\\\"},{\\\"name\\\":\\\"otherErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"byteRead\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nWriteErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"byteWrite\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"OutputIsRunning\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nullErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"},{\\\"name\\\":\\\"nErrors\\\",\\\"type\\\":\\\"LongCounter\\\",\\\"value\\\":\\\"0\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2/taskmanagers\",\n" +
                "            \"json\":\"{\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"name\\\":\\\"Source: TableSourceScan(table=[[default_catalog, default_database, sourceTable]], fields=[id, name, age]) -> Sink: Sink(table=[default_catalog.default_database.mysqlResultTable], fields=[id, name, age])\\\",\\\"now\\\":1703231036851,\\\"taskmanagers\\\":[{\\\"host\\\":\\\"172-16-21-107:39644\\\",\\\"status\\\":\\\"CANCELED\\\",\\\"start-time\\\":1703147665881,\\\"end-time\\\":1703231036475,\\\"duration\\\":83370594,\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true},\\\"status-counts\\\":{\\\"FINISHED\\\":0,\\\"CANCELED\\\":1,\\\"DEPLOYING\\\":0,\\\"RECONCILING\\\":0,\\\"FAILED\\\":0,\\\"CREATED\\\":0,\\\"RUNNING\\\":0,\\\"CANCELING\\\":0,\\\"SCHEDULED\\\":0},\\\"taskmanager-id\\\":\\\"container_e12_1699432271134_14177_01_000003\\\"},{\\\"host\\\":\\\"172-16-22-103:46601\\\",\\\"status\\\":\\\"CANCELED\\\",\\\"start-time\\\":1703147664975,\\\"end-time\\\":1703231036420,\\\"duration\\\":83371445,\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true},\\\"status-counts\\\":{\\\"FINISHED\\\":0,\\\"CANCELED\\\":1,\\\"DEPLOYING\\\":0,\\\"RECONCILING\\\":0,\\\"FAILED\\\":0,\\\"CREATED\\\":0,\\\"RUNNING\\\":0,\\\"CANCELING\\\":0,\\\"SCHEDULED\\\":0},\\\"taskmanager-id\\\":\\\"container_e12_1699432271134_14177_01_000002\\\"}]}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"path\":\"/jobs/fc8f26387b66a8b395407e6d4b16809b/vertices/cbc357ccb763df2852fee8c4fc7d55f2\",\n" +
                "            \"json\":\"{\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"name\\\":\\\"Source: TableSourceScan(table=[[default_catalog, default_database, sourceTable]], fields=[id, name, age]) -> Sink: Sink(table=[default_catalog.default_database.mysqlResultTable], fields=[id, name, age])\\\",\\\"parallelism\\\":2,\\\"now\\\":1703231036856,\\\"subtasks\\\":[{\\\"subtask\\\":0,\\\"status\\\":\\\"CANCELED\\\",\\\"attempt\\\":0,\\\"host\\\":\\\"172-16-22-103\\\",\\\"start-time\\\":1703147664975,\\\"end-time\\\":1703231036420,\\\"duration\\\":83371445,\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true},\\\"taskmanager-id\\\":\\\"container_e12_1699432271134_14177_01_000002\\\",\\\"start_time\\\":1703147664975},{\\\"subtask\\\":1,\\\"status\\\":\\\"CANCELED\\\",\\\"attempt\\\":0,\\\"host\\\":\\\"172-16-21-107\\\",\\\"start-time\\\":1703147665881,\\\"end-time\\\":1703231036475,\\\"duration\\\":83370594,\\\"metrics\\\":{\\\"read-bytes\\\":0,\\\"read-bytes-complete\\\":true,\\\"write-bytes\\\":0,\\\"write-bytes-complete\\\":true,\\\"read-records\\\":0,\\\"read-records-complete\\\":true,\\\"write-records\\\":0,\\\"write-records-complete\\\":true},\\\"taskmanager-id\\\":\\\"container_e12_1699432271134_14177_01_000003\\\",\\\"start_time\\\":1703147665881}]}\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        System.out.println(deleteCron.readSpPathFromLogJSON(log));

    }

    @Test
    public void testDel() {
        deleteCron.deleteFile();
    }

}

class FlinkFileDeleteCronTestMock extends BaseMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getFlinkFileSaveDays() {
        return 1;
    }

    @MockInvoke(
            targetClass = ScheduleJobHistoryService.class,
            targetMethod = "listJobHistoriesByStartId"
    )
    public List<ScheduleJobHistory> listJobHistoriesByStartId(long startId, int limit,
                                                              Timestamp timestamp) {
        if (startId > 1) {
            return null;
        }
        ScheduleJobHistory scheduleJobHistory = new ScheduleJobHistory();
        scheduleJobHistory.setJobId("4kg85uum6380");
        scheduleJobHistory.setEngineJobId("352a1142a7fc727ee2033ec839f0395b");
        scheduleJobHistory.setApplicationId("application_1697677280384_6143");
        scheduleJobHistory.setId(2L);
        return Lists.newArrayList(scheduleJobHistory);
    }

    @MockInvoke(
            targetClass = ScheduleJobService.class,
            targetMethod = "getByJobId"
    )
    public ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        ScheduleJob job = new ScheduleJob();
        job.setDtuicTenantId(1L);
        job.setJobId(jobId);
        return job;
    }

    @MockInvoke(
            targetClass = ClusterTenantDao.class,
            targetMethod = "getClusterIdByDtUicTenantId"
    )
    public Long getClusterIdByDtUicTenantId(Long dtUicTenantId) {
        return 1L;
    }

    @MockInvoke(
            targetClass = ComponentService.class,
            targetMethod = "listByClusterId"
    )
    public List<Component> listByClusterId(Long clusterId, Integer typeCode, boolean isDefault) {
        Component component = new Component();
        component.setHadoopVersion("1.12");
        return Lists.newArrayList(component);
    }

    @MockInvoke(
            targetClass = ClusterService.class,
            targetMethod = "getConfigByKey"
    )
    public String getConfigByKey(Long dtUicTenantId, String componentConfName, Boolean fullKerberos,
                                 Map<Integer, String> componentVersionMap, boolean needHdfsConfig) {

        JSONObject flinkConfig = new JSONObject();
        flinkConfig.put(ConfigConstant.CHECK_POINTS_DIR, "hdfs://ns1/dtInsight/flink112/checkpoints");
        flinkConfig.put(ConfigConstant.SAVE_POINTS_DIR, "hdfs://ns1/dtInsight/flink112/savepoints");
        flinkConfig.put(ConfigConstant.COMPLETE_LOGS_DIR, "hdfs://ns1/dtInsight/flink112/completed-jobs");
        flinkConfig.put(ConfigConstant.HA_STORAGE_DIR, "hdfs://ns1/dtInsight/flink112/ha");
        JSONObject mode = new JSONObject();
        mode.put("perjob", flinkConfig.toJSONString());
        return mode.toJSONString();
    }

    @MockInvoke(
            targetClass = PluginInfoManager.class,
            targetMethod = "buildTaskPluginInfo"
    )
    public JSONObject buildTaskPluginInfo(Long projectId, Integer appType, Integer taskType,
                                          Long dtUicTenantId, String engineTypeStr, Long dtUicUserId, Integer deployMode,
                                          Long resourceId, Map<Integer, String> componentVersionMap) {
        return new JSONObject().fluentPut("dataSourceType", DataSourceType.HDFS.getVal());
    }

    @MockInvoke(
            targetClass = StreamTaskService.class,
            targetMethod = "getCheckpointDir"
    )
    public String getCheckpointDir(String dirPath, String engineJobId,
                                   CheckpointType checkpointType) {
        return dirPath + ConfigConstant.SP + engineJobId;
    }

    @MockInvoke(
            targetClass = StreamTaskService.class,
            targetMethod = "fillPluginInfoAndGetHdfsClient"
    )
    public IHdfsFile fillPluginInfoAndGetHdfsClient(JSONObject pluginInfo, Long dtUicTenantId) {
        return new IHdfsFile() {

            @Override
            public FileStatus getStatus(ISourceDTO source, String remotePath) {
                return null;
            }

            @Override
            public IDownloader getLogDownloader(ISourceDTO source, SqlQueryDTO queryDTO) {
                return null;
            }

            @Override
            public List<String> getTaskManagerList(ISourceDTO source, String submitUser, String appId) {
                return null;
            }

            @Override
            public IDownloader getFileDownloader(ISourceDTO source, String path) {
                return null;
            }

            @Override
            public boolean downloadFileFromHdfs(ISourceDTO source, String remotePath, String localDir) {
                return false;
            }

            @Override
            public boolean uploadLocalFileToHdfs(ISourceDTO source, String localFilePath, String remotePath) {
                return false;
            }

            @Override
            public boolean uploadInputStreamToHdfs(ISourceDTO source, byte[] bytes, String remotePath) {
                return false;
            }

            @Override
            public String uploadStringToHdfs(ISourceDTO source, String str, String remotePath) {
                return null;
            }

            @Override
            public boolean createDir(ISourceDTO source, String remotePath, Short permission) {
                return false;
            }

            @Override
            public boolean isFileExist(ISourceDTO source, String remotePath) {
                return false;
            }

            @Override
            public boolean checkAndDelete(ISourceDTO source, String remotePath) {
                return false;
            }

            @Override
            public boolean checkAndDeleteBySuperUser(ISourceDTO source, String remotePath) {
                return false;
            }

            @Override
            public boolean delete(ISourceDTO source, String remotePath, boolean recursive) {
                return true;
            }

            @Override
            public long getDirSize(ISourceDTO source, String remotePath) {
                return 0;
            }

            @Override
            public boolean deleteFiles(ISourceDTO source, List<String> fileNames) {
                return false;
            }

            @Override
            public boolean isDirExist(ISourceDTO source, String remotePath) {
                return false;
            }

            @Override
            public boolean setPermission(ISourceDTO source, String remotePath, String mode) {
                return false;
            }

            @Override
            public boolean rename(ISourceDTO source, String src, String dist) {
                return false;
            }

            @Override
            public boolean copyFile(ISourceDTO source, String src, String dist, boolean isOverwrite) {
                return false;
            }

            @Override
            public boolean copyDirector(ISourceDTO source, String src, String dist) {
                return false;
            }

            @Override
            public boolean fileMerge(ISourceDTO source, String src, String mergePath, FileFormat fileFormat, Long maxCombinedFileSize, Long needCombineFileSizeLimit) {
                return false;
            }

            @Override
            public List<FileStatus> listStatus(ISourceDTO source, String remotePath) {
                return null;
            }

            @Override
            public List<String> listAllFilePath(ISourceDTO source, String remotePath) {
                return null;
            }

            @Override
            public List<FileStatus> listAllFiles(ISourceDTO source, String remotePath, boolean isIterate) {
                return null;
            }

            @Override
            public Long listFileSize(ISourceDTO source, String remotePath, boolean isIterate) {
                return null;
            }

            @Override
            public boolean copyToLocal(ISourceDTO source, String srcPath, String dstPath) {
                return false;
            }

            @Override
            public boolean copyFromLocal(ISourceDTO source, String srcPath, String dstPath, boolean overwrite) {
                return false;
            }

            @Override
            public IDownloader getDownloaderByFormat(ISourceDTO source, String tableLocation, List<String> columnNames, String fieldDelimiter, String fileFormat) {
                return null;
            }

            @Override
            public IDownloader getDownloaderByFormatWithType(ISourceDTO source, String tableLocation, List<ColumnMetaDTO> allColumns, List<String> filterColumns, Map<String, String> filterPartition, List<String> partitions, String fieldDelimiter, String fileFormat) {
                return null;
            }

            @Override
            public IDownloader getDownloaderByFormatWithType(ISourceDTO source, String tableLocation, List<ColumnMetaDTO> allColumns, List<String> filterColumns, Map<String, String> filterPartition, List<String> partitions, String fieldDelimiter, String fileFormat, Boolean isTransTable) {
                return null;
            }

            @Override
            public List<ColumnMetaDTO> getColumnList(ISourceDTO source, SqlQueryDTO queryDTO, String fileFormat) {
                return null;
            }

            @Override
            public int writeByPos(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) {
                return 0;
            }

            @Override
            public int writeByName(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) {
                return 0;
            }

            @Override
            public int getCsvUploadProgress(String tableName) {
                return 0;
            }

            @Override
            public List<HDFSContentSummary> getContentSummary(ISourceDTO source, List<String> hdfsDirPaths) {
                HDFSContentSummary build = HDFSContentSummary.builder().spaceConsumed(100L).build();
                return Lists.newArrayList(build);
            }

            @Override
            public HDFSContentSummary getContentSummary(ISourceDTO source, String hdfsDirPath) {
                return null;
            }

            @Override
            public String getHdfsWithScript(ISourceDTO source, String hdfsPath) {
                return null;
            }

            @Override
            public List<String> getHdfsWithJob(ISourceDTO source, HdfsQueryDTO hdfsQueryDTO) {
                return null;
            }

            @Override
            public List<FileFindResultDTO> listFiles(ISourceDTO source, List<FileFindDTO> fileFindDTOS) {
                FileStatus fileStatus = FileStatus.builder().modification_time(DateTime.now().plusDays(-100).getMillis())
                        .path("hdfs://ns1/dtInsight/flink112/checkpoints/ffe2f45f3b017f3b39409e04ea8a9b8f/ck-1").build();
                return Lists.newArrayList(FileFindResultDTO.builder()
                        .fileStatusList(Lists.newArrayList(fileStatus))
                        .build());
            }

            @Override
            public List<String> getContainerIdByFlinkJob(ISourceDTO source, String flinkJobId, String flinkJobManagerArchiveFsDir) {
                return null;
            }
        };
    }

    @MockInvoke(
            targetClass = EnvironmentContext.class,
            targetMethod = "getHadoopUserName"
    )
    public String getHadoopUserName() {
        return "admin";
    }

    @MockInvoke(
            targetClass = ScheduleJobHistoryService.class,
            targetMethod = "deleteByJobIds"
    )
    public void deleteByJobIds(List<Long> ids) {
    }

    @MockInvoke(
            targetClass = ScheduleJobService.class,
            targetMethod = "queryDeletedJobs"
    )
    public List<String> queryDeletedJobs(Integer appType) {
        return new ArrayList<>();
    }

}
