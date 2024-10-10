package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.api.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mapstruct.ProjectStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.FuzzyQueryAuthProjectParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListQueryProParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.QueryAuthProjectByIdsParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.QueryAuthProjectParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.stream.Collectors;

public class MockSpaceProjectServiceMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    void updateProjectScheduleStatus(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("scheduleStatus") Integer scheduleStatus) {
        return;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthProjectVO>> findFuzzyProjectByProjectIds(QueryAuthProjectByIdsParam fuzzyQueryAuthProjectParam) {
        AuthProjectVO projectVO = new AuthProjectVO();
        projectVO.setProjectId(1L);
        projectVO.setProjectName("test");
        projectVO.setAppType(1);
        ApiResponse<List<AuthProjectVO>> response = new ApiResponse<>();
        response.setData(Lists.newArrayList(projectVO));
        return response;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthProjectVO>> findFuzzyProjectByProjectAliasForEngine(FuzzyQueryAuthProjectParam fuzzyQueryAuthProjectParam){
        AuthProjectVO projectVO = new AuthProjectVO();
        projectVO.setProjectId(1L);
        projectVO.setProjectName("test");
        ApiResponse<List<AuthProjectVO>> response = new ApiResponse<>();
        response.setData(Lists.newArrayList(projectVO));
        return response;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<AuthProjectVO> findProject(QueryAuthProjectParam queryAuthProjectParam) {
        AuthProjectVO projectVO = new AuthProjectVO();
        projectVO.setProjectId(1L);
        projectVO.setProjectName("test");
        ApiResponse<AuthProjectVO> response = new ApiResponse<>();
        response.setData(projectVO);
        return response;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthProjectVO>> listProByAppTypeAndTenantId(ListQueryProParam listQueryProParam) {
        AuthProjectVO projectVO = new AuthProjectVO();
        projectVO.setProjectId(1L);
        projectVO.setProjectName("test");
        ApiResponse<List<AuthProjectVO>> response = new ApiResponse<>();
        response.setData(Lists.newArrayList(projectVO));
        return response;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<NotDeleteTaskVO> getNotDeleteTask(Long taskId, Integer appType) {
        NotDeleteTaskVO notDeleteTaskVO = new NotDeleteTaskVO();
        notDeleteTaskVO.setTaskName("ts");
        notDeleteTaskVO.setAppType(appType);
        return Lists.newArrayList(notDeleteTaskVO);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskTaskShade> getTaskOtherPlatformByProjectId(Long projectId, Integer appType, Integer listChildTaskLimit) {
        ScheduleTaskTaskShade scheduleTaskTaskShade = new ScheduleTaskTaskShade();
        scheduleTaskTaskShade.setTaskId(1L);
        scheduleTaskTaskShade.setAppType(1);
        scheduleTaskTaskShade.setParentTaskId(10L);
        scheduleTaskTaskShade.setParentAppType(1);
        return Lists.newArrayList(scheduleTaskTaskShade);
    }


    @MockInvoke(targetClass = ProjectStruct.class)
    List<ProjectNameVO> toProjectNameVo(List<AuthProjectVO> authProjectVOS) {
        return authProjectVOS.stream().map(authProjectVO -> {
            ProjectNameVO projectNameVO = new ProjectNameVO();
            projectNameVO.setName(authProjectVO.getProjectName());
            projectNameVO.setProjectId(authProjectVO.getProjectId());
            return projectNameVO;
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<AuthProjectVO> findProjectWithNoException(QueryAuthProjectParam queryAuthProjectParam) {
        AuthProjectVO authProjectVO = new AuthProjectVO();
        ApiResponse<AuthProjectVO> apiResponse = new ApiResponse<>();
        apiResponse.setData(authProjectVO);
        return apiResponse;
    }

}
