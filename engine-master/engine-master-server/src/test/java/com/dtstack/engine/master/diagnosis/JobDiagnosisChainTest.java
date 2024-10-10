package com.dtstack.engine.master.diagnosis;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.diagnosis.JobDiagnosisInformationVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.filter.CycTimeJobDiagnosisFilter;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JobDiagnosisChainTest {

    private static final JobDiagnosisChain jobDiagnosisChain = new JobDiagnosisChain();
    private static final CycTimeJobDiagnosisFilter cycTimeJobDiagnosisFilter = new CycTimeJobDiagnosisFilter();
    List<JobDiagnosisFilter> jobDiagnosisFilters = Lists.newArrayList();

    ScheduleJob scheduleJob = new ScheduleJob();

    @Before
    public void init() {
        scheduleJob.setCycTime("20221131000100");
        scheduleJob.setAppType(1);
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
    }


    @Test
    public void testDiagnosis() throws Exception {
        PrivateAccessor.set(jobDiagnosisChain, "filters", jobDiagnosisFilters);
        List<JobDiagnosisInformationVO> result = jobDiagnosisChain.diagnosisByRunNum("job", 1);
        Assert.assertNotNull(result);
    }

}