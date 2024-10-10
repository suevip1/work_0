package com.dtstack.engine.master.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.master.mockcontainer.impl.JobRestartDealerMock;
import com.dtstack.engine.po.EngineJobCache;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 10:38 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobRestartDealerMock.class)
public class JobRestartDealerTest {

    JobRestartDealer jobRestartDealer = new JobRestartDealer();

    @Test
    public void checkAndRestartForSubmitResultTest () {
        JobClient jobClient1 = new JobClient();
        jobClient1.setMaxRetryNum(5);
        JobResult jobResult = new JobResult();
        jobResult.setCheckRetry(true);
        jobClient1.setJobResult(jobResult);
        jobRestartDealer.checkAndRestartForSubmitResult(jobClient1);
    }

    @Test
    public void checkAndRestartTest () {
        Integer status = 8;
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("12345");
        EngineJobCache jobCache = new EngineJobCache();
        jobCache.setJobInfo("{\"taskType\":1,\"computeType\":1,\"maxRetryNum\":10,\"engineType\":\"kylin\",\"pluginInfo\":{}}");
        jobCache.setEngineType("kylin");
        jobRestartDealer.checkAndRestart(status,scheduleJob,jobCache,null);
    }


}
