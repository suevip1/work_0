package com.dtstack.engine.master.sync.fill.strategy;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.master.mockcontainer.impl.AbstractFillDataTaskMock;
import com.dtstack.engine.master.mockcontainer.sync.fill.strategy.AllSetStrategyFactoryMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.dtstack.engine.master.sync.fill.BatchEnhanceFillDataTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 2:06 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(AllSetStrategyFactoryMock.class)
public class AllSetStrategyFactoryTest {

    @Test
    public void testAllSetStrategyFactory() {
        Set<String> run = Sets.newHashSet();
        run.add("1");

        AllSetStrategy recursion = AllSetStrategyFactory.getAllSetStrategy("recursion", new DtApplicationContext());
        recursion.getAllList(run);
        AllSetStrategy stack = AllSetStrategyFactory.getAllSetStrategy("stack", new DtApplicationContext());
        stack.getAllList(run);

        run.add("2");
        recursion.getAllList(run);
        stack.getAllList(run);
    }
}