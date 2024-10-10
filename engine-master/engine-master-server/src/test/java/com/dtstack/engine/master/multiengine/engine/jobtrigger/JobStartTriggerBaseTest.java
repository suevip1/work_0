package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.mockcontainer.impl.JobStartTriggerBaseMock;
import org.junit.Test;

import java.util.HashMap;

@MockWith(JobStartTriggerBaseMock.class)
public class JobStartTriggerBaseTest {

    JobStartTriggerBase jobStartTriggerBase = new JobStartTriggerBase();

    @Test
    public void testReadyForTaskStartTrigger() throws Exception {
        String taskPamramArray = "[{\"annotation\":\"分区\",\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7969,\"isDeleted\":0,\"outputParamType\":1,\"paramCommand\":\"key1\",\"paramName\":\"key2\",\"taskId\":39623,\"taskType\":0,\"type\":6,\"version\":24743},{\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7967,\"isDeleted\":0,\"paramCommand\":\"test1\",\"paramName\":\"key1\",\"taskId\":39623,\"taskType\":0,\"type\":1,\"version\":24743},{\"gmtCreate\":1649252315000,\"gmtModified\":1649252315000,\"id\":7965,\"isDeleted\":0,\"paramCommand\":\"yyyyMMdd-1\",\"paramName\":\"bdp.system.bizdate\",\"taskId\":39623,\"taskType\":0,\"type\":0,\"version\":24743}]";

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("sdsdsd");
        scheduleJob.setFlowJobId("sdsds");

        jobStartTriggerBase.readyForTaskStartTrigger(new HashMap<String, Object>() {{
            put("sqlText", "#{jobId} #{flowJobId}");
            put("taskParamsToReplace",taskPamramArray);
        }}, new ScheduleTaskShade(), scheduleJob);
    }
}