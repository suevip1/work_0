package com.dtstack.engine.master.worker;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.dtcenter.loader.dto.JobParam;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.master.mockcontainer.worker.DataSourceXOperatorMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-19 20:09
 */
@MockWith(DataSourceXOperatorMock.class)
public class DataSourceXOperatorTest {
    DataSourceXOperator dataSourceXOperator = new DataSourceXOperator();

    List<String> dataSourceStatus = Lists.newArrayList("NEW", "PENDING", "RUNNING",
            "STOPPED", "CANCELLED",
            "FINISHED",
            "ERROR",
            "SUSPENDED","DISCARDED");

    @Test
    public void getStatus() {
        for (String sourceStatus : dataSourceStatus) {
            DataSourceXOperator.adaptStatus(null, sourceStatus);
        }
    }

    @Test
    public void judgeSlots() {
        JobClient jobClient = new JobClient();
        jobClient.setPluginInfo("{\"selfConf\":{\"a\":\"b\"}}");
        dataSourceXOperator.judgeSlots(jobClient);
    }

    @Test
    public void submitJob() {
        JobClient jobClient = new JobClient();
        jobClient.setPluginInfo("{\"selfConf\":{\"a\":\"b\"}}");
        dataSourceXOperator.submitJob(jobClient);
    }

    @Test
    public void getJobStatus() throws Exception {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance("jobId", null, null);
        jobIdentifier.setEngineType(EngineType.Kylin.name());
        PrivateAccessor.set(jobIdentifier, "pluginInfo", "{\"selfConf\":{\"a\":\"b\"}}");
        dataSourceXOperator.getJobStatus(jobIdentifier, null);
    }

    @Test
    public void getEngineLog() {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance("jobId", null, null);
        jobIdentifier.setEngineType(EngineType.Kylin.name());
        PrivateAccessor.set(jobIdentifier, "pluginInfo", "{\"selfConf\":{\"a\":\"b\"}}");
        dataSourceXOperator.getEngineLog(jobIdentifier);
    }

    @Test
    public void stopJob() throws Exception {
        JobClient jobClient = new JobClient();
        jobClient.setPluginInfo("{\"selfConf\":{\"a\":\"b\"}}");
        dataSourceXOperator.stopJob(jobClient);
    }
}