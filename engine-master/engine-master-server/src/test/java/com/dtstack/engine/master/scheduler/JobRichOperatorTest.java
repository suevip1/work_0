package com.dtstack.engine.master.scheduler;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.JobRichOperatorMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 10:53 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobRichOperatorMock.class)
public class JobRichOperatorTest {

    JobRichOperator jobRichOperator = new JobRichOperator();

    @Test
    public void getCycTimeLimit() throws Exception {
        jobRichOperator.getCycTimeLimit();
    }

    @Test
    public void getCycTimeLimitEndNow() throws Exception {
        jobRichOperator.getCycTimeLimitEndNow(true);
    }

    @Test
    public void getCycTime() throws Exception {
        jobRichOperator.getCycTime(1);
        jobRichOperator.getCycTime(null);
    }


}
