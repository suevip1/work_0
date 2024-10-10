package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface ScheduleProjectParamService extends DtInsightServer {

    /**
     * 保存项目参数
     * @param projectParamDTO
     * @return
     */
    @RequestLine("POST /node/sdk/project/param/addOrUpdate")
    ApiResponse<Long> addOrUpdate(ScheduleProjectParamDTO projectParamDTO);

    /**
     * 删除项目参数
     * @param ids
     * @return
     */
    @RequestLine("POST /node/sdk/project/param/remove")
    ApiResponse<Boolean> remove(@Param("ids")List<Long> ids);

    /**
     * 根据 id 查询项目参数
     * @param id
     * @return
     */
    @RequestLine("POST /node/sdk/project/param/findById")
    ApiResponse<ScheduleProjectParamDTO> findById(@Param("id")Long id);

    /**
     * 查询项目下的项目参数
     * @param projectId
     * @return
     */
    @RequestLine("POST /node/sdk/project/param/findByProjectId")
    ApiResponse<List<ScheduleProjectParamDTO>> findByProjectId(@Param("projectId")Long projectId);

    /**
     * 查询项目参数被哪些任务引用
     * @param projectParamId
     * @return
     */
    @RequestLine("POST /node/sdk/project/param/findTasksByProjectParamId")
    ApiResponse<List<ScheduleTaskShade>> findTasksByProjectParamId(@Param("projectParamId")Long projectParamId);
}