package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.ProjectStatus;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.api.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.mapstruct.ProjectStruct;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.FuzzyQueryAuthProjectParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListQueryProParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.QueryAuthProjectByIdsParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.QueryAuthProjectParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class WorkSpaceProjectService {


    private final Logger LOGGER = LoggerFactory.getLogger(WorkSpaceProjectService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private AuthCenterAPIClient authCenterAPIClient;

    @Autowired
    private ProjectStruct projectStruct;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus) {
        if (null == projectId || null == appType || null == scheduleStatus) {
            return;
        }
        LOGGER.info("update project {} status {} ", projectId, scheduleStatus);
        scheduleTaskShadeDao.updateProjectScheduleStatus(projectId, appType, scheduleStatus);
    }

    public List<ProjectNameVO> listProjects(AppType type, Long uicTenantId) {
        Integer appType = Objects.isNull(type) ? null : type.getType();
        List<AuthProjectVO> data = listProjectsByAppAndUicTenant(appType, uicTenantId);
        return projectStruct.toProjectNameVo(data);
    }

    public List<ProjectNameVO> listCanGrantProjects(AppType type, Long uicTenantId) {
        Integer appType = Objects.isNull(type) ? null : type.getType();
        List<AuthProjectVO> data = listProjectsByAppAndUicTenant(appType, uicTenantId, (t -> Objects.nonNull(t.getStatus()) && ProjectStatus.NORMAL.getStatus() == t.getStatus()));
        return projectStruct.toProjectNameVo(data);
    }

    public List<AuthProjectVO> listProjectsByAppAndUicTenant(Integer appType, Long uicTenantId, Predicate<? super AuthProjectVO> predicate) {
        return listProjectsByAppAndUicTenant(appType, uicTenantId).stream().filter(predicate).collect(Collectors.toList());
    }

    public List<AuthProjectVO> listProjectsByAppAndUicTenant(Integer appType, Long uicTenantId) {
        ListQueryProParam listQueryProParam = new ListQueryProParam();
        listQueryProParam.setAppType(appType);
        listQueryProParam.setUicTenantId(uicTenantId);
        ApiResponse<List<AuthProjectVO>> listApiResponse = authCenterAPIClient.listProByAppTypeAndTenantId(listQueryProParam);
        return listApiResponse.getData();
    }

    public String findProjectWithCache(Table<Long, Integer, String> projectCache, Long projectId, Integer appType) {
        String projectAlias = projectCache.get(projectId, appType);
        if (null != projectAlias) {
            return projectAlias;
        }
        AuthProjectVO authProjectVO = finProject(projectId, appType);
        projectAlias = authProjectVO == null ? "" : authProjectVO.getProjectAlias();
        projectCache.put(projectId, appType, projectAlias);
        return projectAlias;
    }

    public AuthProjectVO finProject(Long projectId,Integer appType) {
        QueryAuthProjectParam queryAuthProjectParam = new QueryAuthProjectParam();
        queryAuthProjectParam.setProjectId(projectId);
        queryAuthProjectParam.setAppType(appType);
        ApiResponse<AuthProjectVO> listApiResponse = authCenterAPIClient.findProjectWithNoException(queryAuthProjectParam);
        return listApiResponse.getData();
    }
    public List<NotDeleteProjectVO> getNotDeleteTaskByProjectId(Long projectId, Integer appType) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (projectId == null) {
            throw new RdosDefineException("projectId must be passed");
        }

        List<NotDeleteProjectVO> notDeleteTaskVOS = Lists.newArrayList();

        List<ScheduleTaskTaskShade> scheduleTaskShades = scheduleTaskShadeService.getTaskOtherPlatformByProjectId(projectId, appType, environmentContext.getListChildTaskLimit());

        for (ScheduleTaskTaskShade scheduleTaskShade : scheduleTaskShades) {
            ScheduleTaskShade batchTaskById = scheduleTaskShadeService.getBatchTaskById(scheduleTaskShade.getParentTaskId(), scheduleTaskShade.getParentAppType());
            List<NotDeleteTaskVO> notDeleteTask = scheduleTaskShadeService.getNotDeleteTask(scheduleTaskShade.getParentTaskId(), scheduleTaskShade.getParentAppType());
            NotDeleteProjectVO notDeleteProjectVO = new NotDeleteProjectVO();

            notDeleteProjectVO.setTaskName(batchTaskById.getName());
            notDeleteProjectVO.setNotDeleteTaskVOList(notDeleteTask);
            notDeleteTaskVOS.add(notDeleteProjectVO);
        }

        return notDeleteTaskVOS;
    }

    public List<AuthProjectVO> findProjects(Integer appType, List<Long> ids) {
        QueryAuthProjectByIdsParam queryAuthProjectParam = new QueryAuthProjectByIdsParam();
        queryAuthProjectParam.setProjectIds(ids);
        queryAuthProjectParam.setAppType(appType);
        ApiResponse<List<AuthProjectVO>> fuzzyProjectByProjectIds = authCenterAPIClient.findFuzzyProjectByProjectIds(queryAuthProjectParam);
        return fuzzyProjectByProjectIds.getData();
    }

    public List<AuthProjectVO> fullProjectName(Long dtuicTenantId,String projectName,Integer appType) {
        FuzzyQueryAuthProjectParam queryAuthProjectParam = new FuzzyQueryAuthProjectParam();
        queryAuthProjectParam.setUicTenantId(dtuicTenantId);
        queryAuthProjectParam.setAppType(appType);
        queryAuthProjectParam.setName(projectName);
        ApiResponse<List<AuthProjectVO>> fuzzyProjectByProjectIds = authCenterAPIClient.findFuzzyProjectByProjectAliasForEngine(queryAuthProjectParam);
        return fuzzyProjectByProjectIds.getData();
    }

    public Table<Integer, Long, AuthProjectVO> getProjectGroupAppType(Map<Integer, List<Long>> appProjectMap) {
        Table<Integer,Long,AuthProjectVO> projectInfoTable = HashBasedTable.create();
        for (Integer app : appProjectMap.keySet()) {
            List<AuthProjectVO> authProjectVOS = findProjects(app, appProjectMap.get(app));
            for (AuthProjectVO authProjectVO : authProjectVOS) {
                projectInfoTable.put(authProjectVO.getAppType(), authProjectVO.getProjectId(), authProjectVO);
            }
        }
        return projectInfoTable;
    }
}
