package com.dtstack.engine.master.diagnosis.filter;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.EngineJobCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ResourceMathDiagnosisFilterTest {

    private ResourceMathDiagnosisFilter filter = new ResourceMathDiagnosisFilter();

    @Test
    public void diagnosis() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setCycTime("20221131000100");
        scheduleJob.setAppType(1);
        scheduleJob.setStatus(RdosTaskStatus.RESTARTING.getStatus());
        for (EJobCacheStage value : EJobCacheStage.values()) {
            String s = String.valueOf(value.getStage());
            scheduleJob.setJobId(s);
            JobDiagnosisInformation diagnosis = filter.diagnosis(scheduleJob, DiagnosisResultEnum.DIAGNOSIS_NORMAL);
            Assert.assertNotNull(diagnosis);
        }
        scheduleJob.setJobId("-1");
        filter.diagnosis(scheduleJob, DiagnosisResultEnum.DIAGNOSIS_NORMAL);
    }

    public static class Mock extends BaseMock {
        @MockInvoke(
                targetClass = EngineJobCacheDao.class,
                targetMethod = "getOne"
        )
        public EngineJobCache getOne(String jobId) {
            if ("-1".equals(jobId)) {
                return null;
            }
            EngineJobCache engineJobCache = new EngineJobCache();
            engineJobCache.setStage(Integer.valueOf(jobId));
            return engineJobCache;
        }

        @MockInvoke(
                targetClass = EngineJobCacheDao.class,
                targetMethod = "countLessByStage"
        )
        public int countLessByStage(Long id, String nodeAddress, String jobResource,
                                    List<Integer> stages) {
            return 10;
        }
    }
}
