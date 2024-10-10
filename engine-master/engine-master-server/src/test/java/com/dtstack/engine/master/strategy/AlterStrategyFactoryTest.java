package com.dtstack.engine.master.strategy;

import com.dtstack.engine.master.enums.AlterKey;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 4:31 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterStrategyFactoryTest {

    @Test
    public void testAlterStrategyFactory() {
        AlterStrategyFactory alterStrategyFactory = new AlterStrategyFactory();

        alterStrategyFactory.getAlterStrategy(AlterKey.scanning);
        alterStrategyFactory.getAlterStrategy(AlterKey.baseline);
        alterStrategyFactory.getAlterStrategy(AlterKey.status_change);
    }

}