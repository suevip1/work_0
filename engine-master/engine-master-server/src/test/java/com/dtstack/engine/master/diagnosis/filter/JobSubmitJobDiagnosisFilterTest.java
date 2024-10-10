package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import org.junit.Assert;
import org.junit.Test;

public class JobSubmitJobDiagnosisFilterTest {
    private JobSubmitDiagnosisFilter filter = new JobSubmitDiagnosisFilter();

    @Test
    public void diagnosis() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setCycTime("20221131000100");
        scheduleJob.setAppType(1);
        scheduleJob.setStatus(RdosTaskStatus.RESTARTING.getStatus());
        JobDiagnosisInformation diagnosis = filter.diagnosis(scheduleJob, DiagnosisResultEnum.DIAGNOSIS_NORMAL);
        Assert.assertNotNull(diagnosis);
    }

    public static class Mock {

    }
}
