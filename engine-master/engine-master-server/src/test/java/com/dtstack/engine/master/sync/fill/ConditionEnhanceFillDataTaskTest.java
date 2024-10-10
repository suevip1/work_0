package com.dtstack.engine.master.sync.fill;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataConditionInfoVO;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.master.mockcontainer.sync.fill.ConditionEnhanceFillDataTaskMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:53 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(ConditionEnhanceFillDataTaskMock.class)
public class ConditionEnhanceFillDataTaskTest {

    @Test
    public void testConditionEnhanceFillDataTask() {
        FillDataConditionInfoVO rootTaskId = new FillDataConditionInfoVO();
        rootTaskId.setTaskTypes(Lists.newArrayList(1,2,3));
        rootTaskId.setFilterFrozen(1);

        List<TaskKeyVO> whitelist = Lists.newArrayList();
        TaskKeyVO e = new TaskKeyVO();
        e.setTaskId(1L);
        e.setAppType(1);
        whitelist.add(e);
        ConditionEnhanceFillDataTask batchFillDataTask = new ConditionEnhanceFillDataTask(new DtApplicationContext(), rootTaskId, whitelist);
        batchFillDataTask.setFillDataType(1);
        batchFillDataTask.getRunList();

        List<FillDataChooseProjectVO> projects = Lists.newArrayList();
        FillDataChooseProjectVO vo = new FillDataChooseProjectVO();
        vo.setAppType(1);
        vo.setProjectId(1L);
        projects.add(vo);

        rootTaskId.setProjects(projects);
        ConditionEnhanceFillDataTask batchFillDataTask2 = new ConditionEnhanceFillDataTask(new DtApplicationContext(), rootTaskId, whitelist);
        batchFillDataTask2.setFillDataType(1);
        batchFillDataTask2.getRunList();
    }

}
