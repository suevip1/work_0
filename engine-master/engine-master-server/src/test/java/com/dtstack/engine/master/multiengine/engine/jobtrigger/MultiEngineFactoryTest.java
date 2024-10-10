package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.MultiEngineType;
import org.junit.Test;

public class MultiEngineFactoryTest {

    MultiEngineFactory factory = new MultiEngineFactory();

    @Test
    public void test() {
        for (MultiEngineType value : MultiEngineType.values()) {
            factory.getJobTriggerService(value.getType(), EScheduleJobType.SPARK_SQL.getType(), ScheduleEngineType.Spark.getVal());
        }
    }

    public static class Mock {
        @MockInvoke(
                targetClass = JobStartTriggerDispatcher.class,
                targetMethod = "dispatch"
        )
        public JobStartTriggerBase dispatch(Integer taskType, Integer taskEngineType) {
            return null;
        }
    }
}
