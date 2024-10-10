package com.dtstack.engine.master.sync.fill;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.master.mockcontainer.sync.fill.BatchEnhanceFillDataTaskMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:25 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(BatchEnhanceFillDataTaskMock.class)
public class BatchEnhanceFillDataTaskTest {

    @Test
    public void testBatchEnhanceFillDataTask() {
        List<TaskKeyVO> chooseTaskInfo = Lists.newArrayList();
        TaskKeyVO vo = new TaskKeyVO();
        vo.setTaskId(1L);
        vo.setAppType(1);
        chooseTaskInfo.add(vo);
        BatchEnhanceFillDataTask batchEnhanceFillDataTask = new BatchEnhanceFillDataTask(new DtApplicationContext(), chooseTaskInfo, Lists.newArrayList());
        batchEnhanceFillDataTask.setFillDataType(1);
        batchEnhanceFillDataTask.getRunList();
    }
}
