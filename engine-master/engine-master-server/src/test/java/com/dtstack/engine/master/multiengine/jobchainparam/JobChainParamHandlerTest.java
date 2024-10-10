package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.mockcontainer.impl.JobChainParamHandlerMock;
import com.dtstack.engine.po.ScheduleTaskChainParam;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@MockWith(JobChainParamHandlerMock.class)
public class JobChainParamHandlerTest {

    JobChainParamHandler jobChainParamHandler = new JobChainParamHandler();

    @Test
    public void testHandle() throws Exception {
        String sql = "select order_header_id as `未认购库存(供货口径)货值-90天以下`\n" +
                ",order_date as `未认购库存(供货口径)货值-90-180天`\n" +
                ",shop_id as `未认购库存(供货口径)货值-180-360天`\n" +
                ",customer_id as `未认购库存(供货口径)货值-360-720天`\n" +
                ",order_status as `未认购库存(供货口径)货值-720天以上`\n" +
                ",pay_date as `未认购库存(面)_合计`\n" +
                "from ods_order_orc\n" +
                "where 1=1 and ds='2020-12-22'";
        String taskPamramArray = "[{\"annotation\":\"分区\",\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7969,\"isDeleted\":0,\"outputParamType\":1,\"paramCommand\":\"key1\",\"paramName\":\"key2\",\"taskId\":39623,\"taskType\":0,\"type\":6,\"version\":24743},{\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7967,\"isDeleted\":0,\"paramCommand\":\"test1\",\"paramName\":\"key1\",\"taskId\":39623,\"taskType\":0,\"type\":1,\"version\":24743},{\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7965,\"isDeleted\":0,\"paramCommand\":\"yyyyMMdd-1\",\"paramName\":\"bdp.system.bizdate\",\"taskId\":39623,\"taskType\":0,\"type\":0,\"version\":24743}]";
        List<ScheduleTaskParamShade> scheduleTaskParamShades = JSONObject.parseArray(taskPamramArray, ScheduleTaskParamShade.class);
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskId(1L);
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        ScheduleJob scheduleJob = new ScheduleJob();

        JobChainParamHandleResult result = jobChainParamHandler.handle(sql, scheduleTaskShade, scheduleTaskParamShades, scheduleJob);
        Assert.assertNotNull(result);


    }

    @Test
    public void testDtScriptHandle() throws Exception {
        String sql = "echo 'a'";
        String taskPamramArray = "[{\"annotation\":\"分区\",\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7969,\"isDeleted\":0,\"outputParamType\":3,\"paramCommand\":\"key1\",\"paramName\":\"key2\",\"taskId\":39623,\"taskType\":0,\"type\":6,\"version\":24743},{\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7967,\"isDeleted\":0,\"paramCommand\":\"test1\",\"paramName\":\"key1\",\"taskId\":39623,\"taskType\":0,\"type\":1,\"version\":24743},{\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7965,\"isDeleted\":0,\"paramCommand\":\"yyyyMMdd-1\",\"paramName\":\"bdp.system.bizdate\",\"taskId\":39623,\"taskType\":0,\"type\":0,\"version\":24743}]";
        List<ScheduleTaskParamShade> scheduleTaskParamShades = JSONObject.parseArray(taskPamramArray, ScheduleTaskParamShade.class);
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setTaskType(EScheduleJobType.SHELL.getType());
        ScheduleJob scheduleJob = new ScheduleJob();

        JobChainParamHandleResult result = jobChainParamHandler.handle(sql, scheduleTaskShade, scheduleTaskParamShades, scheduleJob);
        Assert.assertNotNull(result);


    }

    @Test
    public void testCheckOutputParamIfNeed() throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setComputeType(ComputeType.BATCH.getType());
        jobChainParamHandler.checkOutputParamIfNeed(RdosTaskStatus.FINISHED, scheduleJob, null, null);
    }

    @Test
    public void testCheckOutputParamIfNeedFailed() throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setComputeType(ComputeType.BATCH.getType());
        jobChainParamHandler.checkOutputParamIfNeed(RdosTaskStatus.FAILED, scheduleJob, null, null);
    }


    @Test
    public void testFindParamIndexes() throws Exception {
        List<Integer> result = JobChainParamHandler.findParamIndexes("echo a[0,1]");
        Assert.assertNotNull(result);
    }

    @Test
    public void testContainsInOutParam() throws Exception {
        boolean result = JobChainParamHandler.containsInOutParam(null);
        Assert.assertFalse(result);
    }

    @Test
    public void testTrans2TaskChainParams() throws Exception {
        ScheduleTaskParamShade scheduleTaskParamShade = new ScheduleTaskParamShade();
        scheduleTaskParamShade.setParamName("test");
        List<ScheduleTaskChainParam> result = JobChainParamHandler.trans2TaskChainParams(Lists.newArrayList(scheduleTaskParamShade));
        Assert.assertNotNull(result);
    }


    @Test
    public void testSupportChainParam() throws Exception {
        try {
            JobChainParamHandler.supportChainParam(null, null);
        } catch (Exception e) {
        }
    }

}