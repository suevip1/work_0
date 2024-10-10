package com.dtstack.engine.master.executor;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.RestartJobExecutorMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/29 11:39 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(RestartJobExecutorMock.class)
public class RestartJobExecutorTest {

    RestartJobExecutor restartJobExecutor = new RestartJobExecutor();

    @Test
    public void  listExecJob () throws Exception {
        restartJobExecutor.getListMinId(null,null);
        restartJobExecutor.stop();
    }

}
