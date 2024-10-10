package com.dtstack.engine.master.diagnosis.filter;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class ValidJobDiagnosisFilterTest {

    private ValidJobDiagnosisFilter filter = new ValidJobDiagnosisFilter();

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
                targetClass = ScheduleJobJobDao.class,
                targetMethod = "listByParentJobKey"
        )
        public List<ScheduleJobJob> listByParentJobKey(String jobKey, Integer relyType) {
            ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
            scheduleJobJob.setJobKey("job1");
            scheduleJobJob.setParentJobKey("parentJob1");

            return Lists.newArrayList(scheduleJobJob);
        }

        @MockInvoke(
                targetClass = ScheduleJobDao.class,
                targetMethod = "listJobByJobKeys"
        )
        public List<ScheduleJob> listJobByJobKeys(Collection<String> jobKeys) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobKey("job1");
            scheduleJob.setAppType(AppType.DATAASSETS.getType());
            scheduleJob.setTaskRule(TaskRuleEnum.STRONG_RULE.getCode());
            scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
            scheduleJob.setExecStartTime(Timestamp.valueOf(LocalDateTime.now()));
            return Lists.newArrayList(scheduleJob);
        }
    }
}
