package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.dto.FillDataQueryDTO;
import com.dtstack.engine.dto.JobPageDTO;
import com.dtstack.engine.dto.JobTimeDTO;
import com.dtstack.engine.dto.ScheduleJobCount;
import com.dtstack.engine.dto.SimpleJob;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.po.SimpleScheduleJobPO;
import com.dtstack.engine.po.TodayJobGraph;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobDao {

    ScheduleJob getOne(@Param("id") Long id);

    List<ScheduleJob> listByJobIds(@Param("jobIds") List<Long> jobIds);

    ScheduleJob getByJobKeyAndType(@Param("jobKey") String jobKey, @Param("type") int type);

    ScheduleJob getByJobKey(@Param("jobKey") String jobKey);

    List<Map<String, Object>> countByStatusAndType(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status);

    List<ScheduleJobCount> countByStatusAndTypeProjectIds(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectIds") List<Long> projectIds, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status);

    List<Map<String, Object>> selectStatusAndType(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType,
                                                  @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status, @Param("startPage") Integer startPage, @Param("pageSize") Integer pageSize);

    List<Map<String, Object>> listTopRunTime(@Param("projectId") Long projectId, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listTopSizeRunTimeJob(@Param("model") ScheduleJobDTO scheduleJobDTO);

    List<JobTopErrorVO> listTopErrorByType(@Param("dtuicTenantId") Long dtuicTenantId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("type") Integer type, @Param("cycTime") String cycTime, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType);

    List<JobTopErrorVO> listTopError(@Param("dtuicTenantId") Long dtuicTenantId, @Param("projectId") Long projectId, @Param("type") Integer type, @Param("startCycTime") String startCycTime,@Param("endCycTime") String endCycTime, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType);

    List<Map<String, Object>> listTodayJobs(@Param("today") String today, @Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<TodayJobGraph> listTodayJobGraph(@Param("today")String today, @Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType);

    List<Map<String, Object>> listYesterdayJobs(@Param("yesterday")String yesterday,@Param("today")String today,@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listMonthJobs(@Param("lastMonth")String lastMonth,@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listThirtyDayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("taskTypes") List<Integer> taskTypes, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    List<ScheduleJob> listJobByJobKeys(@Param("jobKeys") Collection<String> jobKeys);

    List<SimpleJob> listSimpleJobByJobKeys(@Param("jobKeys") Collection<String> jobKeys);

    List<ScheduleJob> listRuleJobByJobKeys(@Param("jobKeys") Collection<String> jobKeys, @Param("ruleCode") Integer ruleCode);

    List<ScheduleJob> listIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("appType") Integer appType,@Param("cycTime") String cycTime,@Param("type") Integer type);

    List<ScheduleJob> listIdByTaskIdAndStatusBeforeCyctime(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("appType") Integer appType,@Param("cycTime") String cycTime,@Param("type") Integer type);

    List<String> listJobIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("statuses") List<Integer> status);

    List<Map<String, String>> listTaskExeTimeInfo(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType);

    ScheduleJob getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    List<ScheduleJob> getByJobIds(@Param("jobIds") List<String> jobIds, @Param("isDeleted") Integer isDeleted);

    Long getId(@Param("jobId") String jobId);

    List<ScheduleJob> generalQuery(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> generalScienceQuery(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> generalQueryWithMinAndHour(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> getJobRangeByCycTimeByLimit(@Param("taskId") Long taskId, @Param("isAfter") boolean isAfter, @Param("cycTime") String cycTime,@Param("appType") Integer appType,
                                                  @Param("type") Integer type,@Param("limit") Integer limit,@Param("fillId")Long fillId);

    List<ScheduleJob> getJobRangeByCycTime(@Param("taskId") Long taskId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("appType") Integer appType);

    ScheduleJob getNewestTempJob(@Param("taskId") Long taskId,@Param("appType") Integer appType,@Param("type") Integer type,@Param("status") Integer status);

    Integer generalCount(@Param("model") ScheduleJobDTO object);

    Integer generalScienceCount(@Param("model") ScheduleJobDTO object);

    Integer generalCountWithMinAndHour(@Param("model") ScheduleJobDTO object);

    Integer minOrHourJobCount(@Param("model") ScheduleJobDTO object);

    List<ScheduleJob> minOrHourJobQuery(PageQuery<ScheduleJobDTO> pageQuery);

    List<StatusCount> getJobsStatusStatistics(@Param("model") ScheduleJobDTO object);

    Integer batchInsert(Collection batchJobs);

    Integer update(ScheduleJob scheduleJob);

    Integer updateStatusWithExecTime(ScheduleJob job);

    Integer updateNullTime(@Param("jobId") String jobId);

    Integer updateToDeleted(@Param("jobIds") List<String> jobIds,@Param("appType") Integer appType);

    List<String> selectDeletedJobIds(@Param("appType") Integer appType);

    List<String> listFillJobName(PageQuery<ScheduleJobDTO> pageQuery);

    Integer countFillJobNameDistinct(@Param("model") ScheduleJobDTO batchJobDTO);

    Integer countFillJobNameDistinctWithOutTask(@Param("model") ScheduleJobDTO batchJobDTO);

    Integer countFillJobName(@Param("model") ScheduleJobDTO batchJobDTO);

    List<String> listFillJobBizDate(@Param("model") ScheduleJobDTO dto);

    List<ScheduleJob> listNeedStopFillDataJob(@Param("startId") Long startId, @Param("fillDataJobName") String fillDataJobName, @Param("statusList") List<Integer> statusList,
                                              @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("limit") Integer limit);

    List<Map<String, Object>> listTaskExeInfo(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("limitNum") int limitNum, @Param("appType") Integer appType,@Param("types") List<Integer> types);

    /**
     * 根据jobId获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    List<ScheduleJob> getSubJobsAndStatusByFlowId(@Param("jobId") String jobId);

    ScheduleJob getSubJobsAndStatusByFlowIdLimit(@Param("jobId") String jobId);

    /**
     * 获取补数据job的各状态的数量
     *
     * @param jobName
     * @return
     */
    List<Map<String, Long>> countFillDataAllStatusByJobName(@Param("jobName") String jobName, @Param("jobIds") List<String> jobIds);

    /**
     * 获取其中工作流类型的实例
     *
     * @param
     * @return
     */
    List<ScheduleJob> getWorkFlowSubJobId(@Param("jobIds") List<String> jobIds);

    /**
     * 根据工作流实例jobId获取全部子任务实例
     *
     * @param flowIds
     * @return
     */
    List<ScheduleJob> getSubJobsByFlowIds(@Param("flowIds") List<String> flowIds);

    List<Map<String, Long>> countByFillDataAllStatus(@Param("fillIdList") List<Long> fillJobIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("type") Integer type);

    List<Long> listFillIdList(PageQuery<ScheduleJobDTO> pageQuer);

    List<Long> listFillIdListWithOutTask(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> queryFillData(PageQuery pageQuery);

    Integer countByFillData(@Param("model") ScheduleJobDTO batchJobDTO);

    ScheduleJob getWorkFlowTopNode(@Param("jobId") String jobId);

    Map<String, Object> countScienceJobStatus(@Param("status") Integer runStatus, @Param("projectIds") List<Long> projectIds, @Param("type") Integer type, @Param("taskTypes") List<Integer> taskTypes, @Param("tenantId") long tenantId,@Param("cycStartDay") String cycStartDay, @Param("cycEndDay") String cycEndDay);

    List<ScheduleJob> listByJobIdList(@Param("jobIds") Collection<String> jobIds, @Param("projectId") Long projectId);

    List<String> listJobIdByTaskType(@Param("taskType") Integer taskType);

    Integer getStatusByJobId(@Param("jobId") String jobId);

    Timestamp getJobExecStartTime(@Param("jobId") String jobId);

    Integer countTasksByCycTimeTypeAndAddress(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    List<SimpleScheduleJobPO> listSimpleJobByStatusAddress(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress);

    Integer updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("jobIds") List<String> ids);

    Integer updateJobStatusByIds(@Param("status") Integer status, @Param("jobIds") List<String> jobIds);

    void stopUnsubmitJob(@Param("likeName") String likeName, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("status") Integer status);

    List<ScheduleJob> listExecJobByCycTimeTypeAddress(@Param("startId") Long startId, @Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime, @Param("phaseStatus") Integer phaseStatus,
                                                      @Param("isEq") Boolean isEq, @Param("lastTime") Timestamp lastTime, @Param("isRestart") Integer isRestart, @Param("limit") Integer limit);

    List<ScheduleJob> listExecJobByJobIds(@Param("nodeAddress") String nodeAddress,@Param("phaseStatus") Integer phaseStatus,@Param("isRestart") Integer isRestart,@Param("jobIds") Collection<String> jobIds);

    Integer updateJobInfoByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("execStartTime") Timestamp execStartTime, @Param("execEndTime") Timestamp execEndTime, @Param("execTime") Long execTime, @Param("retryNum") Integer retryNum,@Param("stopStatuses") List<Integer> stopStatuses);

    ScheduleJob getByTaskIdAndStatusOrderByIdLimit(@Param("taskId") Long taskId, @Param("status") Integer status, @Param("time") Timestamp time, @Param("appType") Integer appType, @Param("type") Integer type);

    Integer updateStatusByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("versionId") Integer versionId, @Param("execStartTime") Date execStartTime,@Param("execEndTime") Date execEndTime);

    Integer updateResourceIdByJobId(@Param("jobId") String jobId, @Param("resourceId") Long resourceId);

    List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> listJobByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    List<ScheduleJob> listJobByCyctimeAndJobNameBatch(@Param("startId") Long startId, @Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType, @Param("batchJobSize") Integer batchJobSize);

    Integer countJobByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    List<ScheduleJob> listJobsByTaskIdAndApptype(@Param("taskIds") List<Long> taskIds,@Param("appType") Integer appType);

    void deleteByJobKey(@Param("jobKeyList") List<String> jobKeyList);

    List<String> getAllNodeAddress();

    List<ScheduleJob> syncQueryJob(PageQuery<ScheduleJobDTO> pageQuery);


    Integer insert(ScheduleJob scheduleJob);

    void jobFinish(@Param("jobId") String jobId, @Param("status") int status);

    void updateJobStatus(@Param("jobId") String jobId, @Param("status") int status);

    void updateTaskStatusNotStopped(@Param("jobId") String jobId, @Param("status") int status, @Param("stopStatuses") List<Integer> stopStatuses);

    void updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);

    void updateJobSubmitSuccess(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("appId") String appId);

    void updateJobAppId(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("appId") String appId);

    ScheduleJob getRdosJobByJobId(@Param("jobId") String jobId);

    List<ScheduleJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

    List<Long> getDistinctProjectByJobIds(@Param("jobIds")List<String> jobIds);

    List<ScheduleJob> getSimpleInfoByJobIds(@Param("jobIds")List<String> jobIds);

    ScheduleJob getByName(@Param("jobName") String jobName);

    void updateRetryNum(@Param("jobId")String jobId, @Param("retryNum")Integer retryNum);

    List<String> getJobIdsByStatus(@Param("status")Integer status, @Param("computeType")Integer computeType);

    List<ScheduleJob> listJobStatus(@Param("time") Timestamp timeStamp, @Param("computeType")Integer computeType,@Param("appType")Integer appType);

    Integer updateJobStatusByJobIds(@Param("jobIds") List<String> jobIds, @Param("status") Integer status);

    Integer updatePhaseStatusById(@Param("id") Long id, @Param("original") Integer original, @Param("update") Integer update);

    Long getListMinId(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String left, @Param("cycEndTime") String right, @Param("phaseStatus") Integer code,@Param("isRestart") Integer isRestart);

    Integer updateListPhaseStatus(@Param("jobIds") List<String> ids, @Param("update") Integer update);

    Integer updateJobStatusAndPhaseStatus(@Param("jobIds") List<String> jobIds, @Param("status") Integer status, @Param("phaseStatus") Integer phaseStatus,@Param("isRestart") Integer isRestart,@Param("nodeAddress") String nodeAddress,@Param("oldStatus") Integer oldStatus);

    ScheduleJob getLastScheduleJob(@Param("taskId") Long taskId,@Param("id") Long id);
    /**获取上游任务实例**/
    ScheduleJob getFatherJob(@Param("jobId") String jobId, @Param("taskId") Long taskId, @Param("appType")Integer appType);

    void updateStatusByJobIdEqualsStatus(@Param("jobId") String jobId, @Param("updateStatus") Integer updateStatus, @Param("oldStatus") Integer oldStatus);

    Integer updateJobStatusAndPhaseStatusByIds(@Param("jobIds") List<String> jobIds, @Param("status") Integer status, @Param("phaseStatus") Integer phaseStatus);

    List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress,@Param("phaseStatus") Integer phaseStatus);

    Integer updateFlowJob(@Param("placeholder") String placeholder, @Param("flowJob") String flowJob);

    List<ScheduleJob> getByTaskIdAndAppType(@Param("id") Long id, @Param("appType") Integer appType, @Param("taskId") Long taskId, @Param("limit") Integer limit);

    Integer pageFillDateCount(@Param("dto") FillDataQueryDTO fillDataQueryDTO);

    List<ScheduleJob> pageFillDate(@Param("dto") FillDataQueryDTO fillDataQueryDTO);

    void updateJobStatusWithNodeAddress(@Param("jobId") String jobId, @Param("status") int status, @Param("nodeAddress") String nodeAddress);

    void updateJobStatusAndExecTimeWithNodeAddress(@Param("jobId") String jobId, @Param("status") int status, @Param("nodeAddress") String nodeAddress);

    void updateExecTimeAndStatusLogInfo(ScheduleJob updateJob);

    void updateJobStatusANdExceTimeByIds(@Param("status") Integer status, @Param("jobIds") List<String> jobIds);

    void updateResourceIdByProjectIdAndOldResourceIdAndStatus(@Param("resourceId") Long handOver, @Param("projectId") Long projectId, @Param("oldResourceId") Long resourceId, @Param("status") Integer status);

    void updateResourceIdByTaskIdAndOldResourceIdAndStatus(@Param("resourceId") Long handOver, @Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("oldResourceId") Long resourceId, @Param("status") Integer status);

    void updateResourceByTaskIds(@Param("resourceId") Long resourceId, @Param("appType") Integer appType, @Param("taskIds") Collection<Long> taskIds,@Param("dtuicTenantId") Long dtuicTenantId, @Param("statusList") List<Integer> statusList);

    List<ScheduleJob> listNoDeletedByJobIds(@Param("jobIds") Collection<String> jobIds);

    void updateByTaskIdAndVersionId(@Param("versionId")Integer versionId, @Param("taskId")Long taskId, @Param("appType")Integer appType, @Param("oldVersionId")Integer oldVersionId, @Param("status")Integer status);


    List<Timestamp> findTopEndRunTimeByTaskIdsAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("num") Integer num);

    List<ScheduleJob> selectJobByTaskIdAndJobExecuteOrder(@Param("start") Long start, @Param("end") Long end, @Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType,@Param("cycTime")String cycTime);

    List<Integer> selectExecTimeByTaskIdAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("status") Integer status, @Param("maxBaselineJobNum") Integer maxBaselineJobNum);

    List<ScheduleJob> selectJobByTaskIdAndApptype(@Param("taskIds") List<Long> taskIds, @Param("types") List<Integer> types, @Param("appType") Integer appType, @Param("range") Long range);

    Integer countByJobPageDTO(@Param("dto") JobPageDTO dto);

    List<ScheduleJob> pageByJobPageDTO(@Param("dto") JobPageDTO dto);

    List<StatusCount> statusStatisticByJobPageVO(@Param("dto") JobPageDTO dto);

    List<ScheduleJob> getSubJobsByFlowIdsAndQuery(@Param("jobIds") List<String> newArrayList, @Param("dto") JobPageDTO jobPageDTO);

    List<ScheduleJob> listByTimeAndStatus(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("start") Long start, @Param("statusList") List<Integer> statusList,@Param("resourceId")Long resourceId);


    List<ScheduleJob> listFlowSubTaskIdByStatus(@Param("newJobStatuses") List<Integer> newJobStatuses,
                                                @Param("jobTimeDTO") JobTimeDTO jobTimeDTO,
                                                @Param("appType") Integer appType,
                                                @Param("tenantId") Long tenantId,
                                                @Param("projectId") Long projectId,
                                                @Param("types") List<Integer> types,
                                                @Param("fillId") Long fillId);

    List<ScheduleJob> alterPageList(@Param("taskIds") List<Long> taskIds,
                                              @Param("startCycTime") String startCycTime,
                                              @Param("engCycTime") String engCycTime,
                                              @Param("appType") Integer appType,
                                              @Param("projectId") Long projectId,
                                              @Param("start") Integer start,
                                              @Param("end") Integer end);

    List<ScheduleJob> listByFillAndRangCycTime(@Param("fillId") Long fillId, @Param("startCycTime") String startCycTime, @Param("endCycTime") String endCycTime);

    HashSet<String> listJobIdByJobIdAndStatus(@Param("jobIds") HashSet<String> jobIds, @Param("statues") List<Integer> statues);
    List<Long> getFillIdByJobIds(@Param("jobIds") Set<String> jobIds);

    List<String> listJobIdsByAppType(@Param("appType")Integer appType);
    List<String> listByFillId(@Param("fillId") Long fillId);
}
