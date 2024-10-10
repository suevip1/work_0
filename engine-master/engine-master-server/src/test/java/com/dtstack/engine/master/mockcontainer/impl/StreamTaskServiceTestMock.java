package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleJobExpand;
import org.assertj.core.util.Lists;

import java.util.List;

public class StreamTaskServiceTestMock extends BaseMock {


    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    List<EngineJobCheckpoint> listFailedByTaskIdAndRangeTime(String taskEngineId,
                                                             Long triggerStart,
                                                             Long triggerEnd, Integer size) {
        EngineJobCheckpoint engineJobCheckpoint = new EngineJobCheckpoint();
        return Lists.newArrayList(engineJobCheckpoint);
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    EngineJobCheckpoint findLatestSavepointByTaskId(String taskEngineId) {
        return new EngineJobCheckpoint();
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    EngineJobCheckpoint getByTaskIdAndEngineTaskId(String taskId, String taskEngineId) {
        return new EngineJobCheckpoint();
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    void updateFailedCheckpoint(List<EngineJobCheckpoint> checkPointList) {
        return;
    }

    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    List<EngineJobCheckpoint> listByTaskIdAndRangeTime(String taskEngineId,
                                                       Long triggerStart,
                                                       Long triggerEnd) {
        EngineJobCheckpoint engineJobCheckpoint = new EngineJobCheckpoint();
        return Lists.newArrayList(engineJobCheckpoint);
    }


    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    List<ScheduleJobExpand> getLogByJobIds(List<String> jobIds) {
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setJobId("test");
        scheduleJobExpand.setLogInfo("");
        scheduleJobExpand.setEngineLog("");
        return Lists.newArrayList(scheduleJobExpand);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<String> getJobIdsByStatus(Integer status, Integer computeType) {
        return Lists.newArrayList("application_131");
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        if ("stream".equals(jobId) || "error".equals(jobId)) {
            scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
            scheduleJob.setApplicationId("application_131");
        }
        return scheduleJob;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(String jobId) {
        if("error".equals(jobId)){
            return null;
        }
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobInfo("{\"appType\":1,\"businessDate\":\"20220615172451\",\"computeType\":1,\"cycTime\":\"20220616172451\",\"deployMode\":\"2\",\"dtuicTenantId\":1,\"engineType\":\"flink\",\"exeArgs\":\"-jobid runJob_run_sync_task_k1_1655371491363_20220616172451 -job %7B%22job%22%3A%7B%22content%22%3A%5B%7B%22reader%22%3A%7B%22parameter%22%3A%7B%22dtCenterSourceId%22%3A24243%2C%22column%22%3A%5B%7B%22name%22%3A%22data%22%2C%22type%22%3A%22STRING%22%2C%22key%22%3A%22data%22%7D%2C%7B%22name%22%3A%22success%22%2C%22type%22%3A%22INT%22%2C%22key%22%3A%22success%22%7D%5D%2C%22requestMode%22%3A%22POST%22%2C%22decode%22%3A%22json%22%2C%22url%22%3A%22http%3A%2F%2Fdumplings.top%3A3060%2Frestful%2FgetRestful%22%2C%22sourceIds%22%3A%5B6407%5D%7D%2C%22name%22%3A%22restapireader%22%7D%2C%22writer%22%3A%7B%22parameter%22%3A%7B%22method%22%3A%22POST%22%2C%22dtCenterSourceId%22%3A24281%2C%22column%22%3A%5B%7B%22name%22%3A%22data%22%2C%22type%22%3A%22STRING%22%2C%22key%22%3A%22data%22%7D%2C%7B%22name%22%3A%22success%22%2C%22type%22%3A%22INT%22%2C%22key%22%3A%22success%22%7D%5D%2C%22url%22%3A%22dumplings.top%3A3060%2Frestful%2FinsertArray%22%2C%22sourceIds%22%3A%5B6451%5D%7D%2C%22name%22%3A%22restapiwriter%22%7D%7D%5D%2C%22setting%22%3A%7B%22restore%22%3A%7B%22maxRowNumForCheckpoint%22%3A0%2C%22isRestore%22%3Afalse%2C%22restoreColumnName%22%3A%22%22%2C%22restoreColumnIndex%22%3A0%7D%2C%22errorLimit%22%3A%7B%22record%22%3A100%7D%2C%22speed%22%3A%7B%22bytes%22%3A0%2C%22channel%22%3A1%7D%7D%7D%7D\",\"generateTime\":1655371491388,\"isFailRetry\":false,\"lackingCount\":0,\"maxRetryNum\":0,\"name\":\"runJob_run_sync_task_k1_1655371491363_20220616172451\",\"priority\":0,\"projectId\":4351,\"requestStart\":0,\"resourceId\":663,\"sourceType\":2,\"stopJobId\":0,\"submitExpiredTime\":0,\"taskId\":\"4j0r53sq2e21\",\"taskSourceId\":48675,\"taskType\":2,\"tenantId\":1,\"type\":2}");
        return engineJobCache;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> getRdosJobByJobIds(List<String> jobIds) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("test");
        scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        return Lists.newArrayList(scheduleJob);
    }


    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public CheckResult grammarCheck(JobClient jobClient) throws Exception {
        return new CheckResult();
    }


    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        return Lists.newArrayList("test");
    }


    @MockInvoke(targetClass = EngineJobCheckpointDao.class)
    Long getTotalSize(String taskId) {
        return 1L;
    }
}
