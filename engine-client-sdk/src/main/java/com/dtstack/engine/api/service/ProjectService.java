package com.dtstack.engine.api.service;

import com.dtstack.engine.api.param.ProjectDetailParam;
import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.project.ProjectInfoVO;
import com.dtstack.engine.api.vo.project.ProjectStatisticsInfoVO;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface ProjectService extends DtInsightServer {

    @RequestLine("POST /node/project/updateSchedule")
    ApiResponse<Void> updateSchedule(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("scheduleStatus") Integer scheduleStatus);

    /**
     *
     * @param scheduleEngineProjectParam
     * @return
     */
    @RequestLine("POST /node/project/addProject")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Void> addProject(ScheduleEngineProjectParam scheduleEngineProjectParam);

    /**
     * 备注：离线还在调用
     * @param scheduleEngineProjectParam
     * @return
     */
    @RequestLine("POST /node/project/updateProject")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Void> updateProject(ScheduleEngineProjectParam scheduleEngineProjectParam);

    /**
     * 备注：离线还在调用
     * @param projectId
     * @param appType
     * @return
     */
    @RequestLine("POST /node/project/deleteProject")
    ApiResponse<Void> deleteProject(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    @RequestLine("POST /node/project/findFuzzyProjectByProjectAlias")
    ApiResponse<List<ScheduleEngineProjectVO>> findFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId);

    @RequestLine("POST /node/project/findProject")
    ApiResponse<ScheduleEngineProjectVO> findProject(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    @RequestLine("POST /node/project/getNotDeleteTaskByProjectId")
    ApiResponse<List<NotDeleteProjectVO>> getNotDeleteTaskByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    /**
     * 获取项目告警 实例信息 和 失败任务
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/sdk/project/getProjectInfo")
    ApiResponse<List<ProjectInfoVO>> getProjectInfo(ProjectDetailParam param);

    /**
     * 获取单个项目、个人、租户的 实例等信息
     *
     * @param param 我的(1), 我所在的项目(2), 所在项目(3);
     *              com.dtstack.engine.api.param.ProjectDetailParam#type
     * @return
     */
    @RequestLine("POST /node/sdk/project/getDetail")
    ApiResponse<ProjectStatisticsInfoVO> getDetail(ProjectDetailParam param);
}