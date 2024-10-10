package com.dtstack.engine.master.sync.fill;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataRelyInfo;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.mockcontainer.sync.fill.RelyEnhanceFillDataTaskMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 2:00 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(RelyEnhanceFillDataTaskMock.class)
public class RelyEnhanceFillDataTaskTest {

    @Test
    public void testProjectFillDataTask() {
        DtApplicationContext dtApplicationContext = new DtApplicationContext();
        FillDataRelyInfo fillDataRelyInfo = new FillDataRelyInfo();
        List<TaskKeyVO> rootTasks = Lists.newArrayList();
        TaskKeyVO vo = new TaskKeyVO();
        vo.setTaskId(1L);
        vo.setAppType(1);
        rootTasks.add(vo);
        fillDataRelyInfo.setRootTasks(rootTasks);
        fillDataRelyInfo.setFilterFrozen(1);
        RelyEnhanceFillDataTask projectFillDataTask = new RelyEnhanceFillDataTask(dtApplicationContext, fillDataRelyInfo,Lists.newArrayList());
        projectFillDataTask.setFillDataType(1);
        projectFillDataTask.getRunList();
    }

}