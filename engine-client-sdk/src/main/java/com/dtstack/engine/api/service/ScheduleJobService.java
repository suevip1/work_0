package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.dto.ScheduleSqlTextDTO;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.FillDataParams;
import com.dtstack.engine.api.param.RollingJobLogParam;
import com.dtstack.engine.api.param.ScheduleJobAuthParam;
import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.JobStatusVo;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.OperatorVO;
import com.dtstack.engine.api.vo.RestartJobInfoVO;
import com.dtstack.engine.api.vo.RestartJobVO;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleJobBeanVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import com.dtstack.engine.api.vo.ScheduleServerLogVO;
import com.dtstack.engine.api.vo.TaskExeInfoVo;
import com.dtstack.engine.api.vo.diagnosis.JobDiagnosisInformationVO;
import com.dtstack.engine.api.vo.job.BlockJobNumVO;
import com.dtstack.engine.api.vo.job.BlockJobVO;
import com.dtstack.engine.api.vo.job.JobExecInfoVO;
import com.dtstack.engine.api.vo.job.JobExpandVO;
import com.dtstack.engine.api.vo.job.JobGraphBuildJudgeVO;
import com.dtstack.engine.api.vo.job.JobRunCountVO;
import com.dtstack.engine.api.vo.job.JobStopProcessVO;
import com.dtstack.engine.api.vo.job.MaxCpuMemVO;
import com.dtstack.engine.api.vo.job.ScheduleJobAuthVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleAlterPageVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleAlterReturnVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobRuleTimeVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
public interface ScheduleJobService extends DtInsightServer {

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/getJobById")
    ApiResponse<ScheduleJob> getJobById(@Param("jobId") long jobId);


    /**
     * 获取运
     *
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getStatusJobList")
    ApiResponse<PageResult> getStatusJobList(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,
                                             @Param("dtuicTenantId") Long dtuicTenantId, @Param("status") Integer status, @Param("pageSize") int pageSize, @Param("pageIndex") int pageIndex);

    /**
     * 获取各个状态任务的数量
     */
    @RequestLine("POST /node/scheduleJob/getStatusCount")
    ApiResponse<ScheduleJobStatusVO> getStatusCount(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 按照projectIds获取各个状态任务的数量
     *
     * @param projectIds
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getStatusCountByProjectIds")
    ApiResponse<List<ScheduleJobStatusVO>> getStatusCountByProjectIds(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 运行时长top排序
     * 已废弃，替代接口 com.dtstack.engine.api.service.ScheduleJobService#queryTopTenJob(com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO)
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleJob/runTimeTopOrder")
    // ApiResponse<List<JobTopOrderVO>> runTimeTopOrder(@Param("projectId") Long projectId,
    //                                                  @Param("startTime") Long startTime,
    //                                                  @Param("endTime") Long endTime, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    @RequestLine("POST /node/sdk/scheduleJob/queryTopTenJob")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<JobTopOrderVO>> queryTopTenJob(ScheduleJobTopRunTimeVO scheduleJobTopRunTimeVO);

    /**
     * 近30天任务出错排行
     */
    @RequestLine("POST /node/scheduleJob/errorTopOrder")
    ApiResponse<List<JobTopErrorVO>> errorTopOrder(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);


    /**
     * 曲线图数据
     */
    @RequestLine("POST /node/scheduleJob/getJobGraph")
    ApiResponse<ScheduleJobChartVO> getJobGraph(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getScienceJobGraph")
    ApiResponse<ChartDataVO> getScienceJobGraph(@Param("projectId") long projectId, @Param("tenantId") Long tenantId,
                                                @Param("taskType") String taskType);

    @RequestLine("POST /node/scheduleJob/countScienceJobStatus")
    ApiResponse<ScheduleJobScienceJobStatusVO> countScienceJobStatus(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId, @Param("runStatus") Integer runStatus, @Param("type") Integer type, @Param("taskType") String taskType,
                                                                     @Param("cycStartDay") String cycStartTime, @Param("cycEndDay") String cycEndTime);

    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/queryJobs")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<ScheduleJobVO>>> queryJobs(QueryJobDTO vo);

    @RequestLine("POST /node/scheduleJob/displayPeriods")
    ApiResponse<List<SchedulePeriodInfoVO>> displayPeriods(@Param("isAfter") boolean isAfter, @Param("jobId") Long jobId, @Param("projectId") Long projectId, @Param("limit") int limit);

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @
     */
    @RequestLine("POST /node/scheduleJob/getRelatedJobs")
    ApiResponse<ScheduleJobVO> getRelatedJobs(@Param("jobId") String jobId, @Param("vo") String query);

    /**
     * 获取任务的状态统计信息
     * 接口已弃用，替代接口： /node/sdk/scheduleJob/queryJobsStatusStatistics
     *
     * @author toutian
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleJob/queryJobsStatusStatistics")
    // @Headers(value = {"Content-Type: application/json"})
    // ApiResponse<Map<String, Long>> queryJobsStatusStatistics(QueryJobDTO vo);

    /**
     * 获取任务的状态统计信息
     *
     * @param vo
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/queryJobsStatusStatistics")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<JobStatusVo> queryJobsStatusStatisticsBySdk(QueryJobDTO vo);


    @RequestLine("POST /node/scheduleJob/jobDetail")
    ApiResponse<List<ScheduleRunDetailVO>> jobDetail(@Param("taskId") Long taskId, @Param("appType") Integer appType);


    /**
     * 触发 engine 执行指定task
     */
    @RequestLine("POST /node/scheduleJob/sendTaskStartTrigger")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Void> sendTaskStartTrigger(ScheduleJob scheduleJob);

    @RequestLine("POST /node/scheduleJob/stopJob")
    ApiResponse<String> stopJob(@Param("jobId") long jobId, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                                @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType);


    @RequestLine("POST /node/scheduleJob/stopFillDataJobs")
    ApiResponse<Void> stopFillDataJobs(@Param("fillDataJobName") String fillDataJobName, @Param("projectId") Long projectId, @Param("dtuicTenantId") Long dtuicTenantId, @Param("appType") Integer appType, @Param("userId") Long userId);


    @RequestLine("POST /node/scheduleJob/batchStopJobs")
    ApiResponse<Integer> batchStopJobs(@Param("jobIdList") List<Long> jobIdList,
                                       @Param("projectId") Long projectId,
                                       @Param("dtuicTenantId") Long dtuicTenantId,
                                       @Param("appType") Integer appType, @Param("userId") Long userId);


    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     * FillDataClient.fillData 替代
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleJob/fillTaskData")
    // ApiResponse<String> fillTaskData(@Param("taskJson") String taskJsonStr, @Param("fillName") String fillName,
    //                                  @Param("fromDay") Long fromDay, @Param("toDay") Long toDay,
    //                                  @Param("concreteStartTime") String beginTime, @Param("concreteEndTime") String endTime,
    //                                  @Param("projectId") Long projectId, @Param("userId") Long userId,
    //                                  @Param("tenantId") Long tenantId,
    //                                  @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId,
    //                                  @Param("ignoreCycTime") Boolean ignoreCycTime);


    /**
     * 先查询出所有的补数据名称
     * <p>
     * jobName dutyUserId userId 需要关联task表（防止sql慢） 其他情况不需要
     * FillDataClient.fillDataList 替代
     *
     * @param jobName
     * @param runDay
     * @param bizStartDay
     * @param bizEndDay
     * @param dutyUserId
     * @param projectId
     * @param appType
     * @param userId
     * @param currentPage
     * @param pageSize
     * @param tenantId
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleJob/getFillDataJobInfoPreview")
    // ApiResponse<PageResult<List<ScheduleFillDataJobPreViewVO>>> getFillDataJobInfoPreview(@Param("jobName") String jobName, @Param("runDay") Long runDay,
    //                                                                                       @Param("bizStartDay") Long bizStartDay, @Param("bizEndDay") Long bizEndDay, @Param("dutyUserId") Long dutyUserId,
    //                                                                                       @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("user") Integer userId,
    //                                                                                       @Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("tenantId") Long tenantId);

    /**
     * @param fillJobName
     * @return
     */
    // @RequestLine("POST /node/scheduleJob/getFillDataDetailInfoOld?fillJobName={fillJobName}&dutyUserId={dutyUserId}")
    // @Headers(value = {"Content-Type: application/json"})
    // @Deprecated
    // ApiResponse<PageResult<ScheduleFillDataJobDetailVO>> getFillDataDetailInfoOld(QueryJobDTO vo,
    //                                                                               @Param("fillJobName") String fillJobName,
    //                                                                               @Param("dutyUserId") Long dutyUserId);

    /**
     * FillDataClient.fillDataJobList 替代
     *
     * @param queryJobDTO
     * @param flowJobIdList
     * @param fillJobName
     * @param dutyUserId
     * @param searchType
     * @param appType
     * @return
     */
    // @RequestLine("POST /node/scheduleJob/getFillDataDetailInfo")
    // @Deprecated
    // ApiResponse<PageResult<ScheduleFillDataJobDetailVO>> getFillDataDetailInfo(@Param("vo") String queryJobDTO,
    //                                                                            @Param("flowJobIdList") List<String> flowJobIdList,
    //                                                                            @Param("fillJobName") String fillJobName,
    //                                                                            @Param("dutyUserId") Long dutyUserId, @Param("searchType") String searchType, @Param("appType") Integer appType);

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @
     */
    @RequestLine("POST /node/scheduleJob/getRelatedJobsForFillData")
    ApiResponse<ScheduleFillDataJobDetailVO.FillDataRecord> getRelatedJobsForFillData(@Param("jobId") String jobId, @Param("vo") String query,
                                                                                      @Param("fillJobName") String fillJobName);


    /**
     * 获取重跑的数据节点信息
     */
    @RequestLine("POST /node/scheduleJob/getRestartChildJob")
    ApiResponse<List<RestartJobVO>> getRestartChildJob(@Param("jobKey") String jobKey, @Param("taskId") Long parentTaskId, @Param("isOnlyNextChild") boolean isOnlyNextChild);


    @RequestLine("POST /node/scheduleJob/listJobIdByTaskNameAndStatusList")
    ApiResponse<List<String>> listJobIdByTaskNameAndStatusList(@Param("taskName") String taskName, @Param("statusList") List<Integer> statusList, @Param("projectId") Long projectId, @Param("appType") Integer appType);


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getLabTaskRelationMap")
    ApiResponse<Map<String, ScheduleJob>> getLabTaskRelationMap(@Param("jobIdList") List<String> jobIdList, @Param("projectId") Long projectId);

    /**
     * 接口已废弃
     * 获取任务执行信息
     * 接口已废弃  替代接口  ---->   POST /node/sdk/scheduleJob/statisticsTaskRecentInfo
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleJob/statisticsTaskRecentInfo")
    // ApiResponse<List<Map<String, Object>>> statisticsTaskRecentInfo(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId, @Param("count") Integer count);


    /**
     * 获取任务执行接口
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/statisticsTaskRecentInfo")
    ApiResponse<List<TaskExeInfoVo>> statisticsTaskRecentInfoBySdk(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId, @Param("count") Integer count);


    /**
     * 批量更新
     *
     * @param jobs
     */
    @RequestLine("POST /node/scheduleJob/BatchJobsBatchUpdate")
    ApiResponse<Integer> BatchJobsBatchUpdate(@Param("jobs") String jobs);

    /**
     * 把开始时间和结束时间置为null
     *
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/updateTimeNull")
    ApiResponse<Integer> updateTimeNull(@Param("jobId") String jobId);


    @RequestLine("POST /node/scheduleJob/getById")
    ApiResponse<ScheduleJob> getById(@Param("id") Long id);

    @RequestLine("POST /node/scheduleJob/getByJobId")
    ApiResponse<ScheduleJob> getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    @RequestLine("POST /node/scheduleJob/getByIds")
    ApiResponse<List<ScheduleJob>> getByIds(@Param("ids") List<Long> ids, @Param("project") Long projectId);


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getSameDayChildJob")
    ApiResponse<List<ScheduleJob>> getSameDayChildJob(@Param("batchJob") String batchJob,
                                                      @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType);

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param scheduleJob
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getAllChildJobWithSameDay?isOnlyNextChild={isOnlyNextChild}&appType={appType}")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> getAllChildJobWithSameDay(ScheduleJob scheduleJob,
                                                             @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType);


    @RequestLine("POST /node/scheduleJob/generalCount")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Integer> generalCount(ScheduleJobDTO query);

    @RequestLine("POST /node/scheduleJob/generalCountWithMinAndHour")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Integer> generalCountWithMinAndHour(ScheduleJobDTO query);


    @RequestLine("POST /node/scheduleJob/generalQuery")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> generalQuery(PageQuery query);

    @RequestLine("POST /node/scheduleJob/generalQueryWithMinAndHour")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> generalQueryWithMinAndHour(PageQuery query);

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getLastSuccessJob")
    ApiResponse<ScheduleJob> getLastSuccessJob(@Param("taskId") Long taskId, @Param("time") Timestamp time, @Param("appType") Integer appType);


    /**
     * 设置算法实验日志
     * 获取全部子节点日志
     *
     * @param status
     * @param taskType
     * @param jobId
     * @param logVo
     * @
     */
    @RequestLine("POST /node/scheduleJob/setAlogrithmLabLog")
    ApiResponse<ScheduleServerLogVO> setAlogrithmLabLog(@Param("status") Integer status, @Param("taskType") Integer taskType, @Param("jobId") String jobId,
                                                        @Param("info") String info, @Param("logVo") String logVo, @Param("appType") Integer appType);


    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     */
    @RequestLine("POST /node/scheduleJob/minOrHourJobQuery")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ScheduleJobVO>> minOrHourJobQuery(ScheduleJobDTO scheduleJobDTO);


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    @RequestLine("POST /node/scheduleJob/updateJobStatusAndLogInfo")
    ApiResponse<Void> updateJobStatusAndLogInfo(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo);


    /**
     * 测试任务 是否可以运行
     *
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/testCheckCanRun")
    ApiResponse<String> testCheckCanRun(@Param("jobId") String jobId);

    /**
     * 生成当天任务实例
     *
     * @
     */
    @RequestLine("POST /node/scheduleJob/createTodayTaskShade")
    ApiResponse<Void> createTodayTaskShade(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleJob/listByBusinessDateAndPeriodTypeAndStatusList")
    ApiResponse<List<ScheduleJob>> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query);

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     *
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/listByCyctimeAndJobName")
    ApiResponse<List<ScheduleJob>> listByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     *
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    @RequestLine("POST /node/scheduleJob/listByCyctimeAndJobNameWithStartId")
    ApiResponse<List<ScheduleJob>> listByCyctimeAndJobName(@Param("startId") Long startId, @Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType, @Param("batchJobSize") Integer batchJobSize);

    @RequestLine("POST /node/scheduleJob/countByCyctimeAndJobName")
    ApiResponse<Integer> countByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    /**
     * 根据jobKey删除job jobjob记录
     *
     * @param jobKeyList
     */
    @RequestLine("POST /node/scheduleJob/deleteJobsByJobKey")
    ApiResponse<Void> deleteJobsByJobKey(@Param("jobKeyList") List<String> jobKeyList);


    @RequestLine("POST /node/scheduleJob/syncBatchJob")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> syncBatchJob(QueryJobDTO dto);

    /**
     * 根据taskId、appType 拿到对应的job集合
     *
     * @param taskIds
     * @param appType
     */
    @RequestLine("POST /node/scheduleJob/listJobsByTaskIdsAndApptype")
    ApiResponse<List<ScheduleJob>> listJobsByTaskIdsAndApptype(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    /**
     * 根据任务ID 停止任务
     *
     * @param jobId
     * @param userId
     * @param projectId
     * @param tenantId
     * @param dtuicTenantId
     * @param isRoot
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/stopJobByJobId")
    ApiResponse<String> stopJobByJobId(@Param("jobId") String jobId, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                                       @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType);

    /**
     * 生成指定日期的周期实例(需要数据库无对应记录)
     *
     * @param triggerDay
     * @return
     */
    @RequestLine("POST /node/scheduleJob/buildTaskJobGraphTest")
    ApiResponse<Void> buildTaskJobGraphTest(@Param("triggerDay") String triggerDay);

    @RequestLine("POST /node/scheduleJob/testTrigger")
    ApiResponse<Void> testTrigger(@Param("jobId") String jobId);

    @RequestLine("POST /node/scheduleJob/getJobGraphJSON")
    ApiResponse<String> getJobGraphJSON(@Param("jobId") String jobId);

    @RequestLine("POST /node/scheduleJob/updateNotRuleResult")
    ApiResponse<Void> updateNotRuleResult(@Param("jobId") String jobId, @Param("rule") Integer rule, @Param("resultLog") String resultLog);

    @RequestLine("POST /node/scheduleJob/findTaskRuleJobById")
    ApiResponse<List<ScheduleJobBeanVO>> findTaskRuleJobById(@Param("id") Long id);

    @RequestLine("POST /node/scheduleJob/findTaskRuleJob")
    ApiResponse<ScheduleDetailsVO> findTaskRuleJob(@Param("jobId") String jobId);

    /**
     * 已废弃 请使用/node/sdk/scheduleJob/restartJob
     *
     * @param id
     * @param justRunChild
     * @param setSuccess
     * @param subJobIds
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/scheduleJob/syncRestartJob")
    // ApiResponse<Boolean> syncRestartJob(@Param("id") Long id, @Param("justRunChild") Boolean justRunChild, @Param("setSuccess") Boolean setSuccess, @Param("subJobIds") List<Long> subJobIds);

    @RequestLine("POST /node/scheduleJob/restartJobAndResume")
    ApiResponse<OperatorVO> restartJobAndResume(@Param("jobIdList") List<Long> jobIdList, @Param("runCurrentJob") Boolean runCurrentJob);

    /**
     * @param jobIds      勾选的任务id
     * @param restartType 重跑当前节点:RESTART_CURRENT_NODE(0, Boolean.FALSE, Boolean.FALSE)
     *                    重跑及其下游:RESTART_CURRENT_AND_DOWNSTREAM_NODE(1,Boolean.TRUE, Boolean.FALSE)
     *                    置成功不恢复调度:SET_SUCCESSFULLY(3, Boolean.FALSE, Boolean.TRUE)
     *                    置成功并恢复调度:SET_SUCCESSFULLY_AND_RESUME_SCHEDULING(2,Boolean.TRUE, Boolean.TRUE)
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/restartJob")
    ApiResponse<Boolean> restartJob(@Param("jobIds") List<String> jobIds, @Param("restartType") Integer restartType, @Param("operateId") Long operateId);

    /**
     * 获取重跑的数据节点信息
     */
    @RequestLine("POST /node/sdk/scheduleJob/restartJobInfo")
    ApiResponse<List<RestartJobInfoVO>> restartJobInfo(@Param("jobKey") String jobKey, @Param("taskId") Long parentTaskId);


    @RequestLine("POST /node/scheduleJob/stopJobByCondition")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Integer> stopJobByCondition(ScheduleJobKillJobVO scheduleJobKillJobVO);

    @RequestLine("POST /node/sdk/scheduleJob/stopJobByConditionTotal")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Integer> stopJobByConditionTotal(ScheduleJobKillJobVO scheduleJobKillJobVO);

    @RequestLine("POST /node/scheduleJob/getJobsRuleTime")
    ApiResponse<List<ScheduleJobRuleTimeVO>> getJobsRuleTime(List<ScheduleJobRuleTimeVO> jobList);

    /**
     * jobId 获取job集合
     * 仅包含job基本信息
     * 最大条数为100
     *
     * @param jobIds
     */
    @RequestLine("POST /node/sdk/scheduleJob/listByJobIds")
    ApiResponse<List<ScheduleJobVO>> listByJobIds(@Param("jobIds") List<String> jobIds);

    /**
     * 获取补数据信息
     *
     * @param fillDataParams
     */
    @RequestLine("POST /node/sdk/scheduleJob/getJobGetFillDataDetailInfo")
    ApiResponse<PageResult<ScheduleFillDataJobDetailVO>> getJobGetFillDataDetailInfo(FillDataParams fillDataParams);

    /**
     * 获取 sqlText
     *
     * @param jobIds
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/querySqlText")
    ApiResponse<List<ScheduleSqlTextDTO>> querySqlText(@Param("jobIds") List<String> jobIds);


    /**
     * 获取任务执行sql 状态变成运行中才会有对应的运行sql
     *
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/getRunSqlText")
    ApiResponse<String> getRunSqlText(@Param("jobId") String jobId,@Param("runNum")Integer runNum);

    /**
     * 根据 jobId 获取可以运行的下游任务 taskId, 仅对条件分支任务有效
     *
     * @param jobId 条件分支任务 jobId
     * @return 可以运行的下游任务信息
     */
    @RequestLine("POST /node/sdk/scheduleJob/getRunSubTaskId")
    ApiResponse<List<Long>> getRunSubTaskId(@Param("jobId") String jobId);

    /**
     * 判断是否可以生成当天的实例
     *
     * @param scheduleTaskShadeDTO 任务信息
     * @return 校验结果
     */
    @RequestLine("POST /node/sdk/scheduleJob/canBuildJobGraphToday")
    ApiResponse<JobGraphBuildJudgeVO> canBuildJobGraphToday(ScheduleTaskShadeDTO scheduleTaskShadeDTO);

    /**
     * 根据jobKeys获取job信息
     *
     * @param jobKeys
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/listJobByJobKeys")
    ApiResponse<List<ScheduleJobVO>> listJobByJobKeys(@Param("jobKeys") List<String> jobKeys);


    @RequestLine("POST /node/sdk/scheduleJob/diagnosis")
    ApiResponse<List<JobDiagnosisInformationVO>> diagnosis(@Param("jobId") String jobId);

    @RequestLine("POST /node/sdk/scheduleJob/diagnosisByRunNum")
    ApiResponse<List<JobDiagnosisInformationVO>> diagnosisByRunNum(@Param("jobId") String jobId, @Param("runNum") Integer runNum);

    /**
     * 获取离线运行任务(ON Yarn)的日志URL
     * 迁移到新接口 {@link ScheduleJobService#getRunningTaskLogUrlV2}
     * @param jobId
     * @return
     */
    @Deprecated
    @RequestLine("POST /node/sdk/scheduleJob/getRunningTaskLogUrl")
    ApiResponse<List<String>> getRunningTaskLogUrl(@Param("jobId") String jobId);

    /**
     * 获取离线运行任务(ON Yarn)的日志URL
     * @param rollingJobLogParam
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/getRunningTaskLogUrlV2")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<String>> getRunningTaskLogUrlV2(RollingJobLogParam rollingJobLogParam);

    /**
     * 获取离线运行任务运行信息
     *
     * @param jobId 任务 id
     * @return job 执行信息
     */
    @RequestLine("POST /node/sdk/scheduleJob/getJobExecInfo")
    ApiResponse<JobExecInfoVO> getJobExecInfo(@Param("jobId") String jobId);

    /**
     * 获取任务停止进度
     *
     * @param jobIds
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/getStopProgress")
    ApiResponse<List<JobStopProcessVO>> getStopProgress(@Param("jobIds") List<String> jobIds);

    @RequestLine("POST /node/sdk/scheduleJob/getJobExtraInfoOfValue")
    ApiResponse<String> getJobExtraInfoOfValue(@Param("jobId") String jobId, @Param("key") String key);

    @RequestLine("POST /node/sdk/scheduleJob/getJobExtraInfoOfValueById")
    ApiResponse<String> getJobExtraInfoOfValueById(@Param("id") Long id, @Param("key") String key);

    @RequestLine("POST /node/sdk/scheduleJob/runNumList")
    ApiResponse<List<JobRunCountVO>> runNumList(@Param("jobId") String jobId);

    @RequestLine("POST /node/sdk/scheduleJob/blockJobList")
    ApiResponse<List<BlockJobVO>> blockJobList(@Param("jobId") String jobId);

    @RequestLine("POST /node/sdk/scheduleJob/blockJobNum")
    ApiResponse<BlockJobNumVO> blockJobNum(@Param("jobId") String jobId);

    /**
     * 获得实例执行使用的认证信息
     * @param scheduleJobAuthParam
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/getJobAuth")
    ApiResponse<ScheduleJobAuthVO> getJobAuth(ScheduleJobAuthParam scheduleJobAuthParam);

    @RequestLine(value = "POST /node/scheduleJob/alterPageList")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<ScheduleAlterReturnVO>> alterPageList(ScheduleAlterPageVO pageVO);


    @RequestLine("POST /node/sdk/scheduleJob/listJobExpandByJobId")
    ApiResponse<JobExpandVO> listJobExpandByJobId(@Param("jobId") String jobId);

    /**
     * 统计近7天的资源使用情况
     * @param appType
     * @param projectIds
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/getSevenDayResources")
    ApiResponse<List<MaxCpuMemVO>> getSevenDayResources(@Param("appType") Integer appType, @Param("projectIds") List<Long> projectIds);
}
