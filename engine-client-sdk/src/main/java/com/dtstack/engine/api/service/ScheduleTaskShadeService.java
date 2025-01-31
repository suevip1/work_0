package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.CronExceptionVO;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.QueryTaskParam;
import com.dtstack.engine.api.param.TaskInfoChangeParam;
import com.dtstack.engine.api.vo.DependencyTaskVo;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.*;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskShadeService extends DtInsightServer {

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    @RequestLine("POST /node/scheduleTaskShade/addOrUpdate")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> addOrUpdate( ScheduleTaskShadeDTO batchTaskShadeDTO);

    /**
     * 批量新增任务接口
     *
     * @param batchTaskShadeDTO
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/addOrUpdateBatchTask?commitId={commitId}")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<String> addOrUpdateBatchTask(List<ScheduleTaskShadeDTO> batchTaskShadeDTO,@Param("commitId") String commitId);

    /**
     * 保存任务提交engine的额外信息,不会直接提交，只有commit之后才会提交
     *
     * @param taskId
     * @param appType
     * @param info
     */
    @RequestLine("POST /node/scheduleTaskShade/infoCommit")
    ApiResponse<Void> infoCommit(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("extraInfo") String info,@Param("commitId") String commitId);

    /**
     * 提交任务
     *
     * @param commitId
     */
    @RequestLine("POST /node/scheduleTaskShade/taskCommit")
    ApiResponse<Void> taskCommit(@Param("commitId") String commitId);

    /**
     * web 接口
     * task删除时触发同步清理
     */
    @RequestLine("POST /node/scheduleTaskShade/deleteTask")
    ApiResponse<Void> deleteTask(@Param("taskId") Long taskId, @Param("modifyUserId") long modifyUserId, @Param("appType") Integer appType);

    /**
     * 获得其他可以删除的信息
     *
     * @param taskId
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/getNotDeleteTask")
    ApiResponse<List<NotDeleteTaskVO>> getNotDeleteTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);
    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    @RequestLine("POST /node/scheduleTaskShade/getTasksByName")
    ApiResponse<List<ScheduleTaskShade>> getTasksByName(@Param("projectId") long projectId,
                                                        @Param("name") String name, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleTaskShade/getByName")
    ApiResponse<ScheduleTaskShade> getByName(@Param("projectId") long projectId,
                                             @Param("name") String name, @Param("appType") Integer appType, @Param("flowId") Long flowId);

    @RequestLine("POST /node/scheduleTaskShade/updateTaskName")
    ApiResponse<Void> updateTaskName(@Param("taskId") long id, @Param("taskName") String taskName, @Param("appType") Integer appType);

    /**
     * 分页查询已提交的任务
     */
    // @RequestLine("POST /node/scheduleTaskShade/pageQuery")
    // @Headers(value={"Content-Type: application/json"})
    // @Deprecated
    /**
     * @see ScheduleTaskShadeService#newPageQuery(com.dtstack.engine.api.dto.ScheduleTaskShadeDTO)
     */
    // ApiResponse<PageResult<List<ScheduleTaskShadeVO>>> pageQuery( ScheduleTaskShadeDTO dto);

    /**
     * 分页查询已提交的任务
     */
    @RequestLine("POST /node/scheduleTaskShade/v2/pageQuery")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<ScheduleTaskShadeVO>>> newPageQuery(ScheduleTaskShadeDTO dto);


    @RequestLine("POST /node/scheduleTaskShade/getBatchTaskById")
    ApiResponse<ScheduleTaskShade> getBatchTaskById(@Param("id") Long taskId, @Param("appType") Integer appType);

    /**
     * @see ScheduleTaskShadeService#queryTasksByCondition(com.dtstack.engine.api.vo.task.FindTaskVO)
     * @param tenantId
     * @param projectId
     * @param name
     * @param ownerId
     * @param startTime
     * @param endTime
     * @param scheduleStatus
     * @param taskTypeList
     * @param periodTypeList
     * @param currentPage
     * @param pageSize
     * @param searchType
     * @param appType
     * @param resourceIds
     * @return
     */
    // @RequestLine("POST /node/scheduleTaskShade/queryTasks")
    // @Deprecated
    // ApiResponse<ScheduleTaskShadePageVO> queryTasks(@Param("tenantId") Long tenantId,
    //                                                 @Param("projectId") Long projectId,
    //                                                 @Param("name") String name,
    //                                                 @Param("ownerId") Long ownerId,
    //                                                 @Param("startTime") Long startTime,
    //                                                 @Param("endTime") Long endTime,
    //                                                 @Param("scheduleStatus") Integer scheduleStatus,
    //                                                 @Param("taskType") String taskTypeList,
    //                                                 @Param("taskPeriodId") String periodTypeList,
    //                                                 @Param("currentPage") Integer currentPage,
    //                                                 @Param("pageSize") Integer pageSize, @Param("searchType") String searchType,
    //                                                 @Param("appType") Integer appType,@Param("resourceIds") List<Long> resourceIds);


    @RequestLine("POST /node/sdk/scheduleTaskShade/queryTasksByCondition")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<ScheduleTaskShadePageVO> queryTasksByCondition(FindTaskVO findTaskVO);

    /**
     * 冻结任务
     *
     * @param taskIdList
     * @param scheduleStatus
     * @param projectId
     * @param userId
     * @param appType
     */
    @RequestLine("POST /node/scheduleTaskShade/frozenTask")
    ApiResponse<Void> frozenTask(@Param("taskIdList") List<Long> taskIdList, @Param("scheduleStatus") int scheduleStatus,
                           @Param("projectId") Long projectId, @Param("userId") Long userId,
                           @Param("appType") Integer appType);


    /**
     * 查询工作流下子节点
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/dealFlowWorkTask")
    ApiResponse<ScheduleTaskVO> dealFlowWorkTask(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("taskTypes") List<Integer> taskTypes, @Param("ownerId") Long ownerId);

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/getFlowWorkSubTasks")
    ApiResponse<List<ScheduleTaskShade>> getFlowWorkSubTasks(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("taskTypes") List<Integer> taskTypes, @Param("ownerId") Long ownerId);


    @RequestLine("POST /node/scheduleTaskShade/findTaskId")
    ApiResponse<ScheduleTaskShade> findTaskId(@Param("taskId") Long taskId, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType);

    /**
     * @param taskIds
     * @param isDeleted
     * @param appType
     * @param isSimple  不查询sql
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/findTaskIds")
    ApiResponse<List<ScheduleTaskShade>> findTaskIds(@Param("taskIds") List<Long> taskIds, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType, @Param("isSimple") boolean isSimple);

    /**
     * 获得任务信息
     *
     * @param taskKeyVOS
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/findTaskNames")
    ApiResponse<List<TaskInfoVO>> findTaskNames(List<TaskKeyVO> taskKeyVOS);


    /**
     * 保存任务提交engine的额外信息
     *
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/info")
    ApiResponse<Void> info(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("extraInfo") String info);


    /**
     * 接口已废弃 , 替代接口  --->  POST /node/sdk/scheduleTaskShade/listDependencyTask
     * @param taskId
     * @param appType
     * @param name
     * @param projectId
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleTaskShade/listDependencyTask")
    // ApiResponse<List<Map<String, Object>>> listDependencyTask(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("name") String name, @Param("projectId") Long projectId);


    @RequestLine("POST /node/sdk/scheduleTaskShade/listDependencyTask")
    ApiResponse<List<DependencyTaskVo>> listDependencyTaskBySdk(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("name") String name, @Param("projectId") Long projectId);

    /**
     * 接口已废弃 , 替代接口  ---->  POST /node/sdk/scheduleTaskShade/listByTaskIdsNotIn
     * @param taskId
     * @param appType
     * @param projectId
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleTaskShade/listByTaskIdsNotIn")
    // ApiResponse<List<Map<String, Object>>> listByTaskIdsNotIn(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId);

    @RequestLine("POST /node/sdk/scheduleTaskShade/listByTaskIdsNotIn")
    ApiResponse<List<DependencyTaskVo>> listByTaskIdsNotInBySdk(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId);


    /**
     * 根据任务类型查询已提交到task服务的任务数
     *
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @param appType
     * @param taskTypes
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/countTaskByType")
    ApiResponse<ScheduleTaskShadeCountTaskVO> countTaskByType(@Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                                                              @Param("projectId") Long projectId, @Param("appType") Integer appType,
                                                              @Param("taskTypes") List<Integer> taskTypes);


    @RequestLine("POST /node/scheduleTaskShade/getTaskByIds")
    ApiResponse<List<ScheduleTaskShade>> getTaskByIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleTaskShade/countTaskByTypes")
    ApiResponse<List<ScheduleTaskShadeCountTaskVO>> countTaskByTypes(@Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                                                            @Param("projectIds") List<Long> projectIds, @Param("appType") Integer appType,
                                                            @Param("taskTypes") List<Integer> taskTypes);


    @RequestLine("POST /node/scheduleTaskShade/checkResourceLimit")
    ApiResponse<List<String>> checkResourceLimit(@Param("dtuicTenantId") Long dtuicTenantId,
                                           @Param("taskType") Integer taskType,
                                           @Param("resourceParams") String resourceParams);

    /**
     * 已废弃，请迁移至 方法findTask
     *  /node/sdk/scheduleTaskShade/findTask
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleTaskShade/findFuzzyTaskNameByCondition")
    // ApiResponse<List<ScheduleTaskShadeTypeVO>> findFuzzyTaskNameByCondition(@Param("name") String name,
    //                                                                         @Param("appType") Integer appType,
    //                                                                         @Param("uicTenantId") Long uicTenantId,
    //                                                                         @Param("projectId") Long projectId);

    /**
     * 已废弃，请迁移至 方法findTask
     *  /node/sdk/scheduleTaskShade/findTask
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleTaskShade/findFuzzyTaskNameByProjectIds")
    // ApiResponse<List<ScheduleTaskShadeTypeVO>> findFuzzyTaskNameByProjectIds(@Param("name") String name,
    //                                                                          @Param("appType") Integer appType,
    //                                                                          @Param("uicTenantId") Long uicTenantId,
    //                                                                          @Param("projectIds") List<Long> projectIds,
    //                                                                          @Param("limit") Integer limit);

    /**
     * 搜索任务,用于任务关系查询
     *
     * @param findTaskVO
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/findTask")
    ApiResponse<List<ScheduleTaskShadeTypeVO>> findTask(FindTaskVO findTaskVO);

    @RequestLine("POST /node/scheduleTaskShade/findTaskRuleTask")
    ApiResponse<ScheduleDetailsVO> findTaskRuleTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);


    @RequestLine("POST /node/scheduleTaskShade/checkCronExpression")
    ApiResponse<CronExceptionVO> checkCronExpression(@Param("cron") String cron, @Param("minPeriod") Long minPeriod);

    @RequestLine("POST /node/scheduleTaskShade/recentlyRunTime")
    ApiResponse<List<String >> recentlyRunTime(@Param("startDate") String startDate,@Param("endDate") String endDate,
                                               @Param("cron") String cron,@Param("num") Integer num);

    @RequestLine("POST /node/scheduleTaskShade/taskType")
    ApiResponse<List<TaskTypeVO>> getTaskType();

    /**
     * 根据任务类型删除任务
     * 组任务会删除对应组下所有任务
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/deleteTaskByTaskType")
    ApiResponse<Void> deleteTaskByTaskType(@Param("taskId") Long taskId, @Param("modifyUserId") long modifyUserId, @Param("appType") Integer appType,@Param("taskType") Integer taskType);


    /**
     * 批量更新任务调度参数
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/batchUpdateSchedule")
    ApiResponse<Void> batchUpdateSchedule(@Param("taskIds") List<Long> taskIds, @Param("modifyUserId") long modifyUserId, @Param("appType") Integer appType, @Param("scheduleConf") String scheduleConf);

    /**
     * 任务下线
     *
     * @param taskIds 任务集合
     * @param appType 应用id
     * @return 下线信息
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/offline")
    ApiResponse<OfflineReturnVO> offline(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);


    /**
     * 查询任务
     * @param queryTaskParam
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/queryTasks")
    ApiResponse<ScheduleTaskShadePageVO> queryTasks(QueryTaskParam queryTaskParam);


    @RequestLine("POST /node/sdk/scheduleTaskShade/queryTasksByNames")
    ApiResponse<List<ScheduleTaskShade>> queryTasksByNames(@Param("taskNames") List<String> taskNames,
                                                           @Param("appType") Integer appType,
                                                           @Param("projectId") Long projectId);

    /**
     * 批量修改任务责任人
     *
     * @param taskInfoChangeParam
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/updateTaskOwner")
    ApiResponse<Void> updateTaskOwner(TaskInfoChangeParam taskInfoChangeParam);

    /**
     * 删除标签
     *
     * @param tagId 标签id
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/deleteTag")
    ApiResponse<Integer> deleteTag(@Param("tagId") Long tagId, @Param("appType") Integer appType);

    /**
     * 获得任务下的标签
     * @param taskId
     * @param appType
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleTaskShade/selectTagByTask")
    ApiResponse<List<Long>> selectTagByTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);
}
