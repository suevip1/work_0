package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import org.junit.Test;

public class JobStartTriggerDispatcherTest {
    JobStartTriggerDispatcher jobStartTriggerDispatcher = new JobStartTriggerDispatcher();

    @Test
    public void testDispatcher() {
        for (EScheduleJobType value : EScheduleJobType.values()) {
            jobStartTriggerDispatcher.dispatch(value.getType(), ScheduleEngineType.Spark.getVal());
        }

    }

    public static class Mock {
    }
}
