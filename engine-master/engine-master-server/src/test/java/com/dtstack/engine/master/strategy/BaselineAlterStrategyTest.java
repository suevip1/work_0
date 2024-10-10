package com.dtstack.engine.master.strategy;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.master.mockcontainer.strategy.BaselineAlterStrategyMock;
import com.dtstack.engine.po.BaselineJob;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 4:36 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@MockWith(BaselineAlterStrategyMock.class)
public class BaselineAlterStrategyTest {


    @Test
    public void testBaselineAlterStrategy() {
        BaselineAlterStrategy baselineAlterStrategy = new BaselineAlterStrategy();
        AlterEventContext context = new AlterEventContext();
        context.setKey(AlterKey.baseline);
        BaselineJob baselineJob = new BaselineJob();
        baselineJob.setBaselineTaskId(1L);
        baselineJob.setAppType(1);
        context.setBaselineJob(baselineJob);
        context.setBaselineStatus(1);
        context.setFinishStatus(3);
        baselineAlterStrategy.alert(context);
    }
}