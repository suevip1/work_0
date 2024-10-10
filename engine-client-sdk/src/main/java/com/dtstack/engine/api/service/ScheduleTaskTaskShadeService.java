package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.api.vo.task.TaskViewRetrunVO;
import com.dtstack.engine.api.vo.task.TaskViewVO;
import com.dtstack.engine.api.vo.task.WorkflowSubViewList;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeService extends DtInsightServer {

    @RequestLine("POST /node/scheduleTaskTaskShade/clearDataByTaskId")
    ApiResponse<Void> clearDataByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleTaskTaskShade/saveTaskTaskList")
    ApiResponse<SaveTaskTaskVO> saveTaskTaskList(@Param("taskTask") String taskLists,@Param("commitId") String commitId);

    @RequestLine("POST /node/scheduleTaskTaskShade/getAllParentTask")
    ApiResponse<List<ScheduleTaskTaskShade>> getAllParentTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleTaskTaskShade/listChildTask")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ScheduleTaskTaskShade>> listChildTask(@RequestBody ScheduleTaskTaskShade taskTaskShade);

    @RequestLine("POST /node/scheduleTaskTaskShade/displayOffSpring")
    ApiResponse<ScheduleTaskVO> displayOffSpring(@Param("taskId") Long taskId,
                                                 @Param("projectId") Long projectId,
                                                 @Param("userId") Long userId,
                                                 @Param("level") Integer level,
                                                 @Param("type") Integer directType, @Param("appType")Integer appType);

    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/scheduleTaskTaskShade/getAllFlowSubTasks")
    ApiResponse<ScheduleTaskVO> getAllFlowSubTasks(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    /**
     * 查询组任务信息
     * @param taskId
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleTaskTaskShade/listGroupTask")
    ApiResponse<ScheduleTaskVO> listGroupTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    @RequestLine("POST /node/sdk/scheduleTaskTaskShade/view")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<TaskViewRetrunVO> view(TaskViewVO taskViewVO);

    @RequestLine("POST /node/sdk/scheduleTaskTaskShade/workflowSubViewList")
    ApiResponse<WorkflowSubViewList> workflowSubViewList(@Param("taskId") Long taskId, @Param("appType") Integer appType);

}
