package com.dtstack.engine.master.executor;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.mockcontainer.impl.FillJobExecutorMock;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.po.ScheduleFillDataJob;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/29 11:18 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(FillJobExecutorMock.class)
public class FillJobExecutorTest {
    FillJobExecutor fillJobExecutor = new FillJobExecutor();

    @Test
    public void  listExecJob () throws Exception {

        // todo
//        fillJobExecutor.listExecJob(0L,"",Boolean.FALSE);
    }

    @Test
    public void fillDataIsPutQueue() throws Exception {
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();
        checkRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        fillJobExecutor.fillDataIsPutQueue(checkRunInfo, new ScheduleBatchJob(new ScheduleJob()), new ScheduleFillDataJob());
        checkRunInfo.setStatus(JobCheckStatus.NO_TASK);
        fillJobExecutor.fillDataIsPutQueue(checkRunInfo, new ScheduleBatchJob(new ScheduleJob()), new ScheduleFillDataJob());
    }
}
