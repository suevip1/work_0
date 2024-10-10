package com.dtstack.engine.master.diagnosis.filter;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class CycTimeJobDiagnosisFilterTest {
    private CycTimeJobDiagnosisFilter filter = new CycTimeJobDiagnosisFilter();

    @Test
    public void diagnosis() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setCycTime("20221131000100");
        scheduleJob.setAppType(1);
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        JobDiagnosisInformation diagnosis = filter.diagnosis(scheduleJob, DiagnosisResultEnum.DIAGNOSIS_NORMAL);
        Assert.assertNotNull(diagnosis);
    }

    public static class Mock {
        @MockInvoke(targetClass = JobRichOperator.class)
        public Pair<String, String> getCycTimeLimitEndNow(Boolean mindJobId) {
            return new ImmutablePair<>("20221131000000", "20231131000100");
        }
    }
}
