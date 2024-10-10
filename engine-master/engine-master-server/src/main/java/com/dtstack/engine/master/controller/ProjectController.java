package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.master.impl.ProjectService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.router.DtParamOrHeader;
import com.dtstack.dtcenter.common.enums.AppType;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/node/project")
@Api(value = "/node/project", tags = {"项目接口"})
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WorkSpaceProjectService workSpaceProjectService;

    @RequestMapping(value="/updateSchedule", method = {RequestMethod.POST})
    public void updateSchedule(@RequestParam("projectId")Long projectId, @RequestParam("appType")Integer appType, @RequestParam("scheduleStatus")Integer scheduleStatus) {
        workSpaceProjectService.updateSchedule(projectId, appType, scheduleStatus);
    }

    @RequestMapping(value = "/addProject", method = {RequestMethod.POST})
    @Deprecated
    public void addProject(@RequestBody ScheduleEngineProjectParam scheduleEngineProjectParam) {
        projectService.addProjectOrUpdate(scheduleEngineProjectParam);
    }

    @RequestMapping(value = "/findProject", method = {RequestMethod.POST})
    @Deprecated
    public ScheduleEngineProjectVO findProject(@RequestParam("projectId") Long projectId,@RequestParam("appType") Integer appType) {
       return projectService.findProject(projectId,appType);
    }

    @RequestMapping(value = "/getNotDeleteTaskByProjectId", method = {RequestMethod.POST})
    @Deprecated
    public List<NotDeleteProjectVO> getNotDeleteTaskByProjectId(@RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType) {
        return workSpaceProjectService.getNotDeleteTaskByProjectId(projectId,appType);
    }

    @RequestMapping(value = "/updateProject", method = {RequestMethod.POST})
    @Deprecated
    public void updateProject(@RequestBody ScheduleEngineProjectParam scheduleEngineProjectParam) {
        projectService.addProjectOrUpdate(scheduleEngineProjectParam);
    }

    @RequestMapping(value = "/deleteProject", method = {RequestMethod.POST})
    @Deprecated
    public void deleteProject(@RequestParam("projectId") Long projectId,@RequestParam("appType") Integer appType) {
        projectService.deleteProject(projectId,appType);
    }

    @RequestMapping(value = "/findFuzzyProjectByProjectAlias", method = {RequestMethod.POST})
    @Deprecated
    public List<ScheduleEngineProjectVO> findFuzzyProjectByProjectAlias(@RequestParam("name") String name,
                                                                        @RequestParam("appType") Integer appType,
                                                                        @DtParamOrHeader(value = "uicTenantId", header = "cookie", cookie = "dt_tenant_id") Long uicTenantId,
                                                                        @RequestParam("projectId") Long projectId ) {
        return projectService.findFuzzyProjectByProjectAlias(name, appType, uicTenantId,projectId);
    }

    @RequestMapping(value = "/listProjects", method = {RequestMethod.POST})
    public List<ProjectNameVO> listProjects(@RequestParam("appType") String appType,
                                            @RequestParam("dtUicTenantId") Long uicTenantId) {
        AppType type = AppType.valueOf(appType);
        return workSpaceProjectService.listProjects(type, uicTenantId);
    }

    @RequestMapping(value = "/listNormalProjects", method = {RequestMethod.POST})
    public List<ProjectNameVO> listNormalProjects(@RequestParam("appType") String appType,
                                            @RequestParam("dtUicTenantId") Long uicTenantId) {
        AppType type = AppType.valueOf(appType);
        return workSpaceProjectService.listCanGrantProjects(type, uicTenantId);
    }

}
