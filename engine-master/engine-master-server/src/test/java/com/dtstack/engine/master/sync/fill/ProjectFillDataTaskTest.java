package com.dtstack.engine.master.sync.fill;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.mockcontainer.sync.fill.ProjectFillDataTaskMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 1:52 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@MockWith(ProjectFillDataTaskMock.class)
public class ProjectFillDataTaskTest {

    @Test
    public void testProjectFillDataTask() {
        DtApplicationContext dtApplicationContext = new DtApplicationContext();
        ArrayList<FillDataChooseProjectVO> projectList = Lists.newArrayList();
        FillDataChooseProjectVO fillDataChooseProjectVO = new FillDataChooseProjectVO();
        fillDataChooseProjectVO.setProjectId(1L);

        projectList.add(fillDataChooseProjectVO);
        FillDataInfoBO fillDataInfoBO = new FillDataInfoBO(projectList,Lists.newArrayList(),new FillDataChooseTaskVO(), Lists.newArrayList(),Lists.newArrayList(),1L);
        ProjectFillDataTask projectFillDataTask = new ProjectFillDataTask(dtApplicationContext, fillDataInfoBO);
        projectFillDataTask.setFillDataType(1);
        projectFillDataTask.getRunList();
    }

}