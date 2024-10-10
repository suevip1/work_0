package com.dtstack.engine.master.sync.fill;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.mockcontainer.sync.fill.BatchFillDataTaskMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:37 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(BatchFillDataTaskMock.class)
public class BatchFillDataTaskTest {

    @Test
    public void testBatchFillDataTask() {
        FillDataChooseTaskVO rootTaskId = new FillDataChooseTaskVO();
        BatchFillDataTask batchFillDataTask = new BatchFillDataTask(new DtApplicationContext(), new FillDataInfoBO(Lists.newArrayList(),
                Lists.newArrayList(), rootTaskId, Lists.newArrayList(), Lists.newArrayList(),1L));
        batchFillDataTask.setFillDataType(1);
        batchFillDataTask.getRunList();

        BatchFillDataTask batchFillDataTask1 = new BatchFillDataTask(new DtApplicationContext(), new FillDataInfoBO(Lists.newArrayList(),
                Lists.newArrayList(), null, Lists.newArrayList(), Lists.newArrayList(),1L));
        batchFillDataTask1.setFillDataType(1);


        batchFillDataTask1.getRunList();

    }
}
