package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.EngineJobCheckpointDTO;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.mapstruct.PlugInStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Auther: dazhi
 * @Date: 2022/7/1 10:54 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobCheckpointDealerMock extends BaseMock {

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        String cache = "  {\n" +
                "    \"id\": 37975,\n" +
                "    \"job_id\": \"4j488uulgha0\",\n" +
                "    \"job_name\": \"test22_yunweitest2_4j488uulgha0\",\n" +
                "    \"engine_type\": \"flink\",\n" +
                "    \"compute_type\": 0,\n" +
                "    \"stage\": 5,\n" +
                "    \"job_info\": \"{\\\"appType\\\":7,\\\"clientType\\\":0,\\\"componentVersion\\\":\\\"1.12\\\",\\\"computeType\\\":0,\\\"engineType\\\":\\\"flink\\\",\\\"generateTime\\\":1656572034115,\\\"groupName\\\":\\\"dev_cdh_kerberos_root.default\\\",\\\"lackingCount\\\":0,\\\"maxRetryNum\\\":0,\\\"name\\\":\\\"test22_yunweitest2_4j488uulgha0\\\",\\\"priority\\\":1656573034115,\\\"projectId\\\":1917,\\\"requestStart\\\":0,\\\"resourceId\\\":745,\\\"sqlText\\\":\\\"-- name yunweitest2\\\\n-- type FlinkSQL\\\\n-- author admin@dtstack.com\\\\n-- create time 2022-06-30 14:41:20\\\\n-- desc \\\\nCREATE TABLE ods_k(\\\\n    id BIGINT,\\\\n    name STRING\\\\n )WITH(\\\\n    'properties.bootstrap.servers'='172.16.83.206:9092',\\\\n    'connector'='kafka-x',\\\\n    'scan.parallelism'='1',\\\\n    'format'='json',\\\\n    'topic'='chen_part_test',\\\\n    'scan.startup.mode'='latest-offset'\\\\n );\\\\nCREATE TABLE v(\\\\n    id BIGINT,\\\\n    name STRING\\\\n )WITH(\\\\n    'connector'='stream-x'\\\\n );\\\\nINSERT    \\\\nINTO\\\\n    v\\\\n    select\\\\n        id,\\\\n        name                       \\\\n    FROM\\\\n        ods_k;\\\",\\\"stopJobId\\\":0,\\\"submitExpiredTime\\\":0,\\\"taskId\\\":\\\"4j488uulgha0\\\",\\\"taskParams\\\":\\\"parallelism.default=1\\\\ntaskmanager.numberOfTaskSlots=1\\\\njobmanager.memory.process.size=1g\\\\ntaskmanager.memory.process.size=2g\\\\nexecution.checkpointing.interval=5min\\\\nexecution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION\\\\ntable.exec.state.ttl=1d\\\\nlog.level=INFO\\\\npython.executable=python3\\\\npython.client.executable=python3\\\",\\\"taskType\\\":0,\\\"tenantId\\\":10907}\",\n" +
                "    \"node_address\": \"192.168.106.66:8090\",\n" +
                "    \"gmt_create\": \"2022-06-30 14:52:54\",\n" +
                "    \"gmt_modified\": \"2022-07-01 15:34:59\",\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"job_priority\": 1656573034115,\n" +
                "    \"job_resource\": \"flink_dev_cdh_kerberos_root.default_stream_Yarn2\",\n" +
                "    \"is_failover\": 0,\n" +
                "    \"wait_reason\": null,\n" +
                "    \"fill_id\": 0,\n" +
                "    \"tenant_id\": 10907\n" +
                "  }";
        return JSON.parseObject(cache,EngineJobCache.class);
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    int insert(String taskId, String engineTaskId,
                String checkpointId,
                Timestamp checkpointTrigger,
                String checkpointSavepath,
                String checkpointCounts,
               Long checkpointSize,
               Long checkpointDuration) {
        return 1;
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpoints, String engineType, String pluginInfo) throws ClientAccessException {
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return "{\"counts\":{\"restored\":0,\"total\":124,\"in_progress\":0,\"completed\":0,\"failed\":124},\"summary\":{\"state_size\":{\"min\":0,\"max\":0,\"avg\":0},\"end_to_end_duration\":{\"min\":0,\"max\":0,\"avg\":0},\"alignment_buffered\":{\"min\":0,\"max\":0,\"avg\":0},\"processed_data\":{\"min\":0,\"max\":0,\"avg\":0},\"persisted_data\":{\"min\":0,\"max\":0,\"avg\":0}},\"latest\":{\"completed\":null,\"savepoint\":null,\"failed\":{\"@class\":\"failed\",\"id\":124,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656604491695,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":1006,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656604492701,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},\"restored\":null},\"history\":[{\"@class\":\"failed\",\"id\":124,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656604491695,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":1006,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656604492701,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":123,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656604232121,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":17496,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656604249617,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":122,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656603612577,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":69852,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656603682429,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":121,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656603507244,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":13124,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656603520368,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":120,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656603182302,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":13955,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656603196257,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":119,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656602711295,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":79822,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656602791117,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":118,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656602341598,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":44372,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656602385970,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":117,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656602296390,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":8556,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656602304946,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":116,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656602188136,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":35783,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656602223919,\"failure_message\":\"Checkpoint Coordinator is suspending.\"},{\"@class\":\"failed\",\"id\":115,\"status\":\"FAILED\",\"is_savepoint\":false,\"trigger_timestamp\":1656601998163,\"latest_ack_timestamp\":-1,\"state_size\":0,\"end_to_end_duration\":63710,\"alignment_buffered\":0,\"processed_data\":0,\"persisted_data\":0,\"num_subtasks\":1,\"num_acknowledged_subtasks\":0,\"checkpoint_type\":\"CHECKPOINT\",\"tasks\":{},\"failure_timestamp\":1656602061873,\"failure_message\":\"Checkpoint Coordinator is suspending.\"}]}";
    }

    @MockInvoke(targetClass = PlugInStruct.class)
    List<EngineJobCheckpointDTO> toEngineJobCheckpointDTO(List<EngineJobCheckpoint> engineJobCheckpoints) {
        return Lists.newArrayList();
    }
    @MockInvoke(targetClass = TaskParamsService.class)
    public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType, String engineType, Long tenantId) {
        return EDeployMode.PERJOB;
    }

    @MockInvoke(targetClass = TaskParamsService.class)
    public Map<String, Object> convertPropertiesToMap(String taskParams) {
        try {
            Properties properties = com.dtstack.engine.common.util.PublicUtil.stringToProperties(taskParams);
            return new HashMap<String, Object>((Map) properties);
        } catch (IOException e) {
        }
        return new HashMap<>();
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    String getJobExtraInfo( String jobId) {
        return "{}";
    }

    @MockInvoke(targetClass = ScheduleDictService.class)
    public String convertVersionNameToValue(String componentVersion, String engineType) {
        return null;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public JSONObject pluginInfoJSON(Long projectId, Integer appType, Integer taskType, Long dtUicTenantId, String engineTypeStr, Long dtUicUserId, Integer deployMode, Long resourceId, Map<Integer, String> componentVersionMap) {
        return new JSONObject();
    }


    @MockInvoke(targetClass = DelayBlockingQueue.class)
    public JobCheckpointInfo take() throws InterruptedException {
        return new JobCheckpointInfo(ComputeType.BATCH.getType(),"1234",null,"123",1L);
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    Long getMinIdByTaskId( String taskId) {
        return 1L;
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    List<EngineJobCheckpoint> listByTaskIdAndLimit( Long id,  String taskId,  Integer limit, Boolean isE){
        ArrayList<EngineJobCheckpoint> engineJobCheckpoints = Lists.newArrayList();
        if (id != 1L) {
            return engineJobCheckpoints;
        }

        EngineJobCheckpoint e = new EngineJobCheckpoint();

        engineJobCheckpoints.add(e);
        return engineJobCheckpoints;
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    public EngineJobCheckpoint getByTaskId(String taskId) {
        return new EngineJobCheckpoint();
    }



}
