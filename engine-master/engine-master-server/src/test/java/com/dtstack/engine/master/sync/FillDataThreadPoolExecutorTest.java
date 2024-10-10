package com.dtstack.engine.master.sync;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.mockcontainer.impl.FillDataRunnableMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/1 1:53 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(BaseMock.class)
public class FillDataThreadPoolExecutorTest {

    FillDataThreadPoolExecutor fillDataThreadPoolExecutor = new FillDataThreadPoolExecutor();


    @Test
    public void testAfterPropertiesSet() throws Exception {
        fillDataThreadPoolExecutor.afterPropertiesSet();
        fillDataThreadPoolExecutor.destroy();
    }
}
