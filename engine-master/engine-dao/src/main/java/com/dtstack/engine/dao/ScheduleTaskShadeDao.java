package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.dto.TaskPageDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.dto.TaskName;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.task.FindTaskVO;
import com.dtstack.engine.api.vo.task.TaskSearchNameVO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface ScheduleTaskShadeDao {

    List<TaskName> listNameByIds(@Param("ids") Collection<Long> ids);

    List<ScheduleTaskShade> listByIds(@Param("ids") Collection<Long> ids);

    ScheduleTaskShade getOne(@Param("taskId") long taskId, @Param("appType") Integer appType);

    ScheduleTaskShade getOneIncludeDelete(@Param("taskId") Long taskId , @Param("appType")Integer appType);

    List<ScheduleTaskShade> listTaskByStatus(@Param("startId") Long startId, @Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus, @Param("batchTaskSize") Integer batchTaskSize
    ,@Param("projectIds")Collection<Long> projectIds, @Param("appType")Integer appType,@Param("taskGroup")Integer taskGroup);

    Integer countTaskByStatus(@Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus,@Param("projectIds")Collection<Long> projectIds, @Param("appType")Integer appType, @Param("taskGroup")Integer taskGroup);

    List<ScheduleTaskShadeCountTaskVO> countTaskByType(@Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId, @Param("projectIds")List<Long> projectIds, @Param("appType")Integer appType, @Param("taskTypes")List<Integer> taskTypes,
                                                       @Param("flowId") Long flowId);

    List<ScheduleTaskShade> listByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted, @Param("appType")Integer appType);

    List<ScheduleTaskShade> listByTaskNames(@Param("taskNames") List<String> taskNames, @Param("appType")Integer appType, @Param("projectId") Long projectId);

    List<ScheduleTaskShade> listByNameLike(@Param("projectId") Long projectId, @Param("name") String name, @Param("appType")Integer appType, @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds);

    List<ScheduleTaskShade> listByNameLikeWithSearchType(@Param("projectId") Long projectId, @Param("name") String name, @Param("appType")Integer appType,
                                                   @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds,@Param("searchType")Integer searchType,@Param("componentVersions") List<String> componentVersions,@Param("computeType") Integer computeType);

    ScheduleTaskShade getByName(@Param("projectId") long projectId, @Param("name") String name, @Param("appType") Integer appType,@Param("flowId") Long flowId);

    List<Map<String,Object>> listDependencyTask(@Param("projectId") long projectId, @Param("name") String name, @Param("taskIds") List<Long> taskIds);

    List<Map<String,Object>> listByTaskIdsNotIn(@Param("projectId") long projectId, @Param("taskIds") List<Long> taskIds);

    Integer updateTaskName(@Param("taskId") long taskId, @Param("name") String name,@Param("appType")Integer appType);

    Integer delete(@Param("taskId") long taskId, @Param("userId") long modifyUserId,@Param("appType")Integer appType);

    Integer insert(ScheduleTaskShade batchTaskShade);

    Integer update(ScheduleTaskShade batchTaskShade);

    List<ScheduleTaskShade> listByType(@Param("projectId") Long projectId, @Param("type") Integer type, @Param("taskName") String taskName);

    List<ScheduleTaskShade> generalQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery);

    Integer generalCount(@Param("model") Object model);

    Integer batchUpdateTaskScheduleStatus(@Param("taskIds") List<Long> taskIds, @Param("scheduleStatus") Integer scheduleStatus,@Param("appType")Integer appType);

    List<ScheduleTaskShade> simpleQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery);

    Integer simpleCount(@Param("model") ScheduleTaskShadeDTO pageQuery);

    Integer updatePublishStatus(@Param("list") List<Long> list, @Param("status") Integer status);

    Integer countPublishToProduce(@Param("projectId") Long projectId,@Param("appType")Integer appType);

    List<ScheduleTaskForFillDataDTO> listSimpleTaskByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted,@Param("appType")Integer appType);

    String getSqlTextById(@Param("id") Long id);

    ScheduleTaskShade getWorkFlowTopNode(@Param("workFlowId") Long workFlowId, @Param("appType") Integer appType);

    /**
     *  ps- 省略了一些大字符串 如 sql_text、task_params
     * @param taskIds
     * @param isDeleted
     * @return
     */
    List<ScheduleTaskShade> listSimpleByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted,@Param("appType") Integer appType);

    ScheduleTaskShade getOneWithDeleted(@Param("id") Long id, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType);

    void updateTaskExtInfo(@Param("taskId") long taskId, @Param("appType")Integer appType, @Param("extraInfo") String extraInfo);

    String getExtInfoByTaskId(@Param("taskId") long taskId, @Param("appType")Integer appType);

    String getExtInfoByTaskIdIncludeDelete(@Param("taskId") long taskId, @Param("appType")Integer appType);

    ScheduleTaskShade getById(@Param("id") Long id);

    void updateProjectScheduleStatus(@Param("projectId")Long projectId,@Param("appType")Integer appType,@Param("scheduleStatus") Integer scheduleStatus);
    
    List<ScheduleTaskShade> findFuzzyTaskNameByCondition(@Param("name") String name,
                                                         @Param("appType") Integer appType,
                                                         @Param("uicTenantId") Long uicTenantId,
                                                         @Param("tenantIds") List<Long> tenantIds,
                                                         @Param("projectIds") List<Long> projectIds,
                                                         @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit,
                                                         @Param("projectScheduleStatus") Integer projectScheduleStatus);

    List<ScheduleTaskShade> getChildTaskByOtherPlatform(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("limit") Integer limit);

    List<ScheduleTaskShade> getTaskOtherPlatformByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("limit") Integer limit);

    List<ScheduleTaskShade> listTaskRuleTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    /**
     * 根据appType和taskId查询任务
     * @param taskId
     * @param appType
     * @return
     */
    ScheduleTaskShade getOneByTaskIdAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    List<ScheduleTaskShade> listByUicTenantId(@Param("tenantIds") List<Long> tenantIds, @Param("appType") Integer appType);

    List<ScheduleTaskShade> findTaskKeyByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("taskTypes") List<Integer> taskTypes, @Param("taskGroup") Integer taskGroup, @Param("taskIds") List<Long> taskIds);

    List<ScheduleTaskShade> listByIdsAndTaskName(@Param("ids") List<Long> ids, @Param("taskName") String taskName);

    List<ScheduleTaskShade> listByFindTask(@Param("findTaskVO") FindTaskVO findTaskVO);

    void deleteByFlowId(@Param("flowId")Long flowId, @Param("userId")long modifyUserId, @Param("appType")Integer appType);

    void updateScheduleConf(@Param("taskIds")List<Long> taskIds, @Param("appType")Integer appType, @Param("modifyUserId")long modifyUserId,@Param("scheduleConf")String scheduleConf,@Param("periodType") Integer periodType);

    List<ScheduleTaskShade> getTasksByFlowId(@Param("flowId") Long flowId,@Param("appType")Integer appType);

    void updateResourceIdByProjectIdAndOldResourceId(@Param("resourceId") Long resourceId, @Param("projectId") Long projectId, @Param("oldResourceId") Long oldResourceId,@Param("appType") Integer appType);

    Integer deleteTombstoneByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("appType") Integer appType);

    void updateResourceByTaskIds(@Param("resourceId") Long resourceId, @Param("appType") Integer appType, @Param("taskIds") Collection<Long> taskIds,@Param("dtuicTenantId") Long dtuicTenantId);

    void changeTaskVersion(@Param("targetVersion") String targetTaskShadeComponentVersion, @Param("versions") List<String> changeTaskShadeComponentVersions,
                           @Param("dtuicTenantIds") List<Long> dtUicTenantIdByIds,@Param("appType") Integer appType);

    Integer countTaskByTaskPageDTO(@Param("taskPageDTO")  TaskPageDTO taskPageDTO);

    List<ScheduleTaskShade> pageTaskByTaskPageDTO(@Param("taskPageDTO") TaskPageDTO taskPageDTO);


    List<Long> searchTaskIdBySearchName(@Param("taskName") String taskName,
                                        @Param("searchType") Integer searchType,
                                        @Param("appType") Integer appType,
                                        @Param("projectId") Long projectId,
                                        @Param("tenantId") Long tenantId,
                                        @Param("ownerId") Long ownerId,
                                        @Param("businessType") String businessType,
                                        @Param("taskTypes") List<Integer> taskTypes,
                                        @Param("scheduleStatus") List<Integer> scheduleStatus,
                                        @Param("isDeleted") Integer isDeleted);
    List<Long> listTaskIdsInOwnerId(@Param("appType") Integer appType,@Param("projectId") Long projectId,@Param("ownerUserId") Long ownerUserId);

    List<Map<String,Integer>> listOwnerIds(@Param("appType") Integer appType);

    List<ScheduleTaskShade> listSimpleByNodePIdsAndAppTypeAndProjectIdAndTaskIds(@Param("taskIds") List<Long> taskIds,
                                                                                 @Param("nodePIds")List<Long> nodePIds,
                                                                                 @Param("projectId") Long projectId,
                                                                                 @Param("appType") Integer appType,
                                                                                 @Param("taskGroup") Integer taskGroup);

    Long findMinIdByTaskType(@Param("taskTypes") List<Integer> taskTypes,  @Param("taskIds") List<Long> taskIds,@Param("taskGroup") Integer taskGroup);

    List<ScheduleTaskShade> findTaskByTaskType(@Param("minId") Long minId,
                                               @Param("taskTypes") List<Integer> taskTypes,
                                               @Param("taskIds") List<Long> taskIds,
                                               @Param("fillDataLimitSize") Integer fillDataLimitSize,
                                               @Param("taskGroup") Integer taskGroup,
                                               @Param("isEquals") Boolean isEquals
                                               );

    Integer batchUpdateTaskOwnerUserId(@Param("taskIds") List<Long> taskIds, @Param("ownerUserId") Long ownerUserId,@Param("appType")Integer appType);

    List<ScheduleTaskShade> listSimpleFiledByTaskIds(@Param("taskIds") Set<Long> allTaskIds, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType);

    ScheduleTaskShade getSimpleByTaskIdAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    List<Long> findPriorityOneTask(@Param("appType") Integer appType);

    List<ScheduleTaskShade> listByAppAndResourceId(@Param("appType") Integer appType, @Param("resourceIds") List<Long> resourceIds);
}
