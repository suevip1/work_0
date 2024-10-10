package com.dtstack.engine.master.diagnosis.filter;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.po.TenantResource;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class TenantResourceDiagnosisFilterTest {

    private TenantResourceDiagnosisFilter filter = new TenantResourceDiagnosisFilter();

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
            return new ScheduleTaskShade();
        }

        @MockInvoke(
                targetClass = TenantResourceDao.class,
                targetMethod = "selectByUicTenantIdAndTaskType"
        )
        public TenantResource selectByUicTenantIdAndTaskType(Long dtUicTenantId, Integer taskType) {
            return new TenantResource();
        }

        @MockInvoke(
                targetClass = ScheduleTaskShadeService.class,
                targetMethod = "checkResourceLimit"
        )
        public List<String> checkResourceLimit(Long dtuicTenantId, Integer taskType,
                                               String resourceParams, Long taskId) {
            return Lists.newArrayList("cpu > 10");
        }
    }
}
