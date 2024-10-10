package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.master.mockcontainer.impl.ProjectServiceMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@MockWith(ProjectServiceMock.class)
public class ProjectServiceTest {
    ProjectService projectService = new ProjectService();

    @Test
    public void testAddProject() throws Exception {
        ScheduleEngineProjectParam scheduleEngineProjectParam = new ScheduleEngineProjectParam();
        scheduleEngineProjectParam.setProjectAlias("test");
        scheduleEngineProjectParam.setProjectName("test");
        scheduleEngineProjectParam.setProjectId(1L);
        scheduleEngineProjectParam.setAppType(1);
        projectService.addProjectOrUpdate(scheduleEngineProjectParam);
    }


    @Test
    public void testUpdateProject() throws Exception {
        ScheduleEngineProjectParam scheduleEngineProjectParam = new ScheduleEngineProjectParam();
        scheduleEngineProjectParam.setProjectAlias("test");
        scheduleEngineProjectParam.setProjectName("test");
        scheduleEngineProjectParam.setProjectId(2L);
        scheduleEngineProjectParam.setAppType(1);
        projectService.addProjectOrUpdate(scheduleEngineProjectParam);
    }

    @Test
    public void testDeleteProject() throws Exception {
        projectService.deleteProject(1L, 0);
    }

    @Test
    public void testFindFuzzyProjectByProjectAlias() throws Exception {
        List<ScheduleEngineProjectVO> result = projectService.findFuzzyProjectByProjectAlias("name", 0, 1L, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindProject() throws Exception {
        ScheduleEngineProjectVO result = projectService.findProject(1L, 0);
        Assert.assertNull(result);
    }
}