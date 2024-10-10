package com.dtstack.engine.master.strategy;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.strategy.WorkerRpcNodeAddressStrategyMock;
import com.dtstack.rpc.exception.ExecuteException;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 4:37 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(WorkerRpcNodeAddressStrategyMock.class)
public class WorkerRpcNodeAddressStrategyTest {


    @Test
    public void testWorkerRpcNodeAddressStrategy() {
        WorkerRpcNodeAddressStrategy workerRpcNodeAddressStrategy = new WorkerRpcNodeAddressStrategy();

        workerRpcNodeAddressStrategy.getRpcRemoteType();
        try {
            workerRpcNodeAddressStrategy.getAllNodes(null);
        } catch (ExecuteException e) {
            e.printStackTrace();
        }

    }
}