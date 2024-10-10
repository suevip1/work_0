package com.dtstack.engine.master.diagnosis.filter;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RunningJobDiagnosisFilterTest {

    private RunningJobDiagnosisFilter filter = new RunningJobDiagnosisFilter();

    @Test
    public void diagnosis() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setCycTime("20221131000100");
        scheduleJob.setAppType(1);
        scheduleJob.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleJob.setExecStartTime(Timestamp.valueOf(LocalDateTime.now()));
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setJobId("-1");
        filter.diagnosis(scheduleJob, DiagnosisResultEnum.DIAGNOSIS_NORMAL);
    }

    public static class Mock {
        @MockInvoke(
                targetClass = ScheduleTaskShadeService.class,
                targetMethod = "getBatchTaskById"
        )
        public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
            ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
            scheduleTaskShade.setEngineType(1);
            return scheduleTaskShade;
        }

        @MockInvoke(
                targetClass = EnginePluginsOperator.class,
                targetMethod = "getJobMetricsAnalysis"
        )
        public String getJobMetricsAnalysis(JobIdentifier jobIdentifier, RdosTaskStatus status,
                                            Timestamp startTime, Timestamp endTime) {
            return "{}";
        }
    }
}
