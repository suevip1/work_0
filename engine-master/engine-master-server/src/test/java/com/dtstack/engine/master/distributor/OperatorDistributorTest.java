package com.dtstack.engine.master.distributor;

import com.dtstack.engine.common.enums.ClientTypeEnum;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/25 10:48 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class OperatorDistributorTest {

    OperatorDistributor operatorDistributor = new OperatorDistributor();


    @Test
    public void paramReplaceTest() throws Exception {
        operatorDistributor.getOperator(ClientTypeEnum.DATASOURCEX_NO_STATUS,null);
        operatorDistributor.getOperator(ClientTypeEnum.WORKER_STATUS,null);
        operatorDistributor.getOperator(ClientTypeEnum.DATASOURCEX_STATUS,null);
    }
}
