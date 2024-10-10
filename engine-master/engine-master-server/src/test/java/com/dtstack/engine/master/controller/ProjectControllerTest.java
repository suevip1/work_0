package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.master.impl.ProjectService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(ProjectControllerTest.ProjectControllerTestMock.class)
public class ProjectControllerTest {

    private ProjectController controller = new ProjectController();

    static class ProjectControllerTestMock {

        @MockInvoke(targetClass = WorkSpaceProjectService.class)
        public void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus) {

        }

        @MockInvoke(targetClass = ProjectService.class)
        public void addProjectOrUpdate(ScheduleEngineProjectParam scheduleEngineProjectParam) {

        }

        @MockInvoke(targetClass = ProjectService.class)
        public ScheduleEngineProjectVO findProject(Long projectId, Integer appType) {
            return new ScheduleEngineProjectVO();
        }

        @MockInvoke(targetClass = WorkSpaceProjectService.class)
        public List<NotDeleteProjectVO> getNotDeleteTaskByProjectId(Long projectId, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ProjectService.class)
        public void deleteProject(Long projectId, Integer appType) {

        }

        @MockInvoke(targetClass = ProjectService.class)
        public List<ScheduleEngineProjectVO> findFuzzyProjectByProjectAlias(String name, Integer appType, Long uicTenantId,Long projectId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = WorkSpaceProjectService.class)
        public List<ProjectNameVO> listProjects(AppType type, Long uicTenantId) {
            return new ArrayList<>();
        }


        }

    @Test
    public void updateSchedule() {
        controller.updateSchedule(1L,1,1);
    }

    @Test
    public void addProject() {
        controller.addProject(new ScheduleEngineProjectParam());
    }

    @Test
    public void findProject() {
        controller.findProject(1L,1);
    }

    @Test
    public void getNotDeleteTaskByProjectId() {
        controller.getNotDeleteTaskByProjectId(1L,1);
    }

    @Test
    public void updateProject() {
        controller.updateProject(new ScheduleEngineProjectParam());
    }

    @Test
    public void deleteProject() {
        controller.deleteProject(1L,1);
    }

    @Test
    public void findFuzzyProjectByProjectAlias() {
        controller.findFuzzyProjectByProjectAlias("1",1,1L,1L);
    }

    @Test
    public void listProjects() {
        controller.listProjects("RDOS",1L);
    }
}