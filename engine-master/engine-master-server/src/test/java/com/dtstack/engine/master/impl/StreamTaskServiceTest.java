package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.stream.EngineStreamJobVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.mockcontainer.impl.StreamTaskServiceTestMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

@MockWith(StreamTaskServiceTestMock.class)
public class StreamTaskServiceTest {

    StreamTaskService streamTaskService = new StreamTaskService();

    @Test
    public void testGetFailedCheckPoint() throws Exception {
        List<EngineJobCheckpoint> taskId = streamTaskService.getFailedCheckPoint("taskId", 1L, 1L, 0);
        Assert.assertNotNull(taskId);
    }

    @Test
    public void testGetCheckPoint() throws Exception {
        List<EngineJobCheckpoint> result = streamTaskService.getCheckPoint("taskId", 1L, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetSavePoint() throws Exception {
        EngineJobCheckpoint result = streamTaskService.getSavePoint("taskId");
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetByTaskIdAndEngineTaskId() throws Exception {
        EngineJobCheckpoint result = streamTaskService.getByTaskIdAndEngineTaskId("taskId", "engineTaskId");
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetEngineStreamJob() throws Exception {
        List<ScheduleJob> result = streamTaskService.getEngineStreamJob(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetEngineStreamJobNew() throws Exception {
        List<EngineStreamJobVO> result = streamTaskService.getEngineStreamJobNew(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTaskIdsByStatus() throws Exception {
        List<String> result = streamTaskService.getTaskIdsByStatus(0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTaskStatus() throws Exception {
        Integer result = streamTaskService.getTaskStatus("taskId");
        Assert.assertEquals(RdosTaskStatus.FINISHED.getStatus(), result);
    }

    @Test
    public void testGetRunningTaskLogUrl() throws Exception {
        List<String> result = streamTaskService.getRunningTaskLogUrl("stream");
        Assert.assertEquals(Collections.singletonList("test"), result);

        try {
            result = streamTaskService.getRunningTaskLogUrl("error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGrammarCheck() throws Exception {
        ParamActionExt paramActionExt = JSONObject.parseObject(" {\"appType\":9,\"computeType\":0,\"deployMode\":\"2\",\"engineType\":\"flink\",\"exeArgs\":\"-jobid assets_real_time_3613_engineId_4j0r4gaut9i0 -job %7B%22job%22%3A%7B%22content%22%3A%5B%7B%22reader%22%3A%7B%22parameter%22%3A%7B%22dbList%22%3A%5B%5D%2C%22password%22%3A%22%22%2C%22metaStoreUris%22%3A%22thrift%3A%2F%2F172-16-23-238%3A9083%22%2C%22hadoopConfig%22%3A%7B%7D%2C%22jdbcUrl%22%3A%22jdbc%3Ahive2%3A%2F%2F172.16.23.238%3A8191%2Fvalid%22%2C%22source%22%3A%22hive+server2%22%2C%22version%22%3A%22apache2%22%2C%22username%22%3A%22admin%22%7D%2C%22name%22%3A%22metadatasparkthriftcdcreader%22%7D%2C%22writer%22%3A%7B%22parameter%22%3A%7B%22method%22%3A%22post%22%2C%22column%22%3A%5B%5D%2C%22batchInterval%22%3A1%2C%22header%22%3A%7B%7D%2C%22body%22%3A%7B%7D%2C%22params%22%3A%7B%22sourceId%22%3A1165%2C%22jobId%22%3A%22%24%7BjobId%7D%22%2C%22tenantId%22%3A1%2C%22taskId%22%3A3613%7D%2C%22batchSize%22%3A1%2C%22url%22%3A%22http%3A%2F%2F172.16.82.142%3A8875%2Fdmetadata%2Fv1%2FsyncJob%2FsyncCallBack%22%7D%2C%22name%22%3A%22restapiwriter%22%7D%7D%5D%2C%22setting%22%3A%7B%22restore%22%3A%7B%22isStream%22%3Atrue%2C%22isRestore%22%3Atrue%7D%2C%22errorLimit%22%3A%7B%22record%22%3A100%7D%2C%22speed%22%3A%7B%22bytes%22%3A1048576%2C%22channel%22%3A1%7D%7D%7D%7D -confProp %7B%22flink.checkpoint.interval%22%3A%221800000%22%7D\",\"generateTime\":1655371281398,\"lackingCount\":0,\"name\":\"assets_real_time_3613_engineId_4j0r4gaut9i0\",\"priority\":0,\"projectId\":-1,\"requestStart\":0,\"stopJobId\":0,\"submitExpiredTime\":0,\"taskId\":\"4j0r4gaut9i0\",\"taskType\":2,\"tenantId\":1}",
                ParamActionExt.class);
        streamTaskService.grammarCheck(paramActionExt);
    }

    @Test
    public void testGetTotalSize() throws Exception {
        Long result = streamTaskService.getTotalSize("jobId");
        Assert.assertEquals(Long.valueOf(1), result);
    }
}