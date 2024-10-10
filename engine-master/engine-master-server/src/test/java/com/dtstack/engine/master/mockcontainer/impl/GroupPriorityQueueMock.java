package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockNew;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dto.FillDataQueryDTO;
import com.dtstack.engine.dto.GroupOverviewDTO;
import com.dtstack.engine.dto.JobPageDTO;
import com.dtstack.engine.dto.JobTimeDTO;
import com.dtstack.engine.dto.ScheduleJobCount;
import com.dtstack.engine.dto.SimpleJob;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobSubmitDealer;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.SimpleScheduleJobPO;
import com.dtstack.engine.po.TodayJobGraph;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 11:09 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class GroupPriorityQueueMock extends BaseMock {


    @MockInvoke(targetClass = JobDealer.class)
    public void saveCache(JobClient jobClient, String jobResource, int stage, boolean insert) {
    }

    @MockInvoke(targetClass = ApplicationContext.class)
    <T> T getBean(Class<T> interfaceClass) throws BeansException, InstantiationException, IllegalAccessException {
        if (interfaceClass.isInterface()) {
            if ("EngineJobCacheDao".equals(interfaceClass.getSimpleName())) {
                return (T) new EngineJobCacheDao() {

                    @Override
                    public int insert(String jobId, String engineType, Integer computeType, int stage, String jobInfo, String nodeAddress, String jobName, Long jobPriority, String jobResource, Long tenantId, String taskName) {
                        return 0;
                    }

                    @Override
                    public int updateTaskName(String jobId, String taskName) {
                        return 0;
                    }

                    @Override
                    public List<String> getJobIdWithTaskNameIsNull() {
                        return null;
                    }

                    @Override
                    public Long countByJobResource(String jobResource, Integer stage, String nodeAddress, Long projectId, String taskName, List<Long> tenantIds) {
                        return null;
                    }

                    @Override
                    public List<EngineJobCache> listByJobResource(String jobResource, Integer stage, String nodeAddress, Integer start, Integer pageSize, Long projectId, String taskName, List<Long> tenantIds) {
                        return null;
                    }

                    @Override
                    public EngineJobCache getOneByJobResource(String jobResource) {
                        return null;
                    }

                    @Override
                    public int delete(String jobId) {
                        return 0;
                    }

                    @Override
                    public EngineJobCache getOne(String jobId) {
                        return null;
                    }

                    @Override
                    public int updateStage(String jobId, Integer stage, String nodeAddress, Long jobPriority, String waitReason) {
                        return 0;
                    }

                    @Override
                    public int updateStageBatch(List<String> jobIds, Integer stage, String nodeAddress) {
                        return 0;
                    }

                    @Override
                    public List<EngineJobCache> listByStage(Long id, String nodeAddress, Integer stage, List<Long> tenantIds, String jobResource) {
                        return null;
                    }

                    @Override
                    public int countLessByStage(Long id, String nodeAddress, String jobResource, List<Integer> stages) {
                        return 0;
                    }

                    @Override
                    public List<EngineJobCache> getByJobIds(List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public List<String> listNames(String jobName) {
                        return null;
                    }

                    @Override
                    public int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
                        return 0;
                    }

                    @Override
                    public Long minPriorityByStage(String jobResource, List<Integer> stages, String nodeAddress) {
                        return null;
                    }

                    @Override
                    public List<String> getAllNodeAddress() {
                        return null;
                    }

                    @Override
                    public Integer updateNodeAddressFailover(String nodeAddress, List<String> ids, Integer stage) {
                        return null;
                    }

                    @Override
                    public List<EngineJobCache> listByFailover(Long id, String nodeAddress, Integer stage) {
                        return null;
                    }

                    @Override
                    public List<String> getJobResources() {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> groupByJobResource(String nodeAddress) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> groupByJobResourceFilterByCluster(String nodeAddress, String clusterName, Long tenantId) {
                        return null;
                    }

                    @Override
                    public List<GroupOverviewDTO> getGroupOverviews(String nodeAddress, List<Long> tenantIds) {
                        return null;
                    }


                    @Override
                    public Integer deleteByJobIds(List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public Integer updateJobInfo(String jobInfo, String jobId) {
                        return null;
                    }

                    @Override
                    public List<EngineJobCache> findByJobResourceNoTenant(String jobResource, List<Long> tenantIds) {
                        return null;
                    }

                    @Override
                    public List<String> listJobIdByJobResourceAndNodeAddressAndStages(String jobResource, String nodeAddress, Integer stage) {
                        return null;
                    }

                    @Override
                    public List<EngineJobCache> getAllCache(Integer stage, String jobResource, List<String> aliveNodes, Long minPriority, Long maxPriority) {
                        return null;
                    }


                    @Override
                    public List<String> getAllJobResource(String localAddress) {
                        return null;
                    }

                    @Override
                    public Long getMaxCurrentPriority() {
                        return null;
                    }


                };
            }
            if ("ScheduleJobDao".equals(interfaceClass.getSimpleName())) {
                return (T) new ScheduleJobDao() {
                    @Override
                    public List<ScheduleJob> getSubJobsByFlowIdsAndQuery(List<String> newArrayList, JobPageDTO jobPageDTO) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listByTimeAndStatus(Long tenantId, Long projectId, Integer appType, Long start, List<Integer> statusList, Long resourceId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listFlowSubTaskIdByStatus(List<Integer> newJobStatuses, JobTimeDTO jobTimeDTO, Integer appType, Long tenantId, Long projectId, List<Integer> type, Long fillId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> alterPageList(List<Long> taskIds, String startCycTime, String engCycTime, Integer appType, Long projectId, Integer start, Integer end) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listByFillAndRangCycTime(Long fillId, String startCycTime, String endCycTime) {
                        return null;
                    }

                    @Override
                    public HashSet<String> listJobIdByJobIdAndStatus(HashSet<String> jobIds, List<Integer> statues) {
                        return null;
                    }
                    @Override
                    public List<Long> getFillIdByJobIds(Set<String> jobIds) {
                        return null;
                        }

                    public List<String> listByFillId(Long fillId) {
                        return null;
                    }

                    @Override
                    public List<String> listJobIdsByAppType(Integer appType) {
                        return null;
                    }

                    @Override
                    public List<Long> getDistinctProjectByJobIds(List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getOne(Long id) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listByJobIds(List<Long> jobIds) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getByJobKeyAndType(String jobKey, int type) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getByJobKey(String jobKey) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> countByStatusAndType(Integer type, String startTime, String endTime, Long tenantId, Long projectId, Integer appType, Long dtuicTenantId, List<Integer> status) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJobCount> countByStatusAndTypeProjectIds(Integer type, String startTime, String endTime, Long tenantId, List<Long> projectIds, Integer appType, Long dtuicTenantId, List<Integer> status) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> selectStatusAndType(Integer type, String startTime, String endTime, Long tenantId, Long projectId, Integer appType, Long dtuicTenantId, List<Integer> status, Integer startPage, Integer pageSize) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> listTopRunTime(Long projectId, Timestamp startTime, Timestamp endTime, PageQuery pageQuery, Integer appType, Long dtuicTenantId) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> listTopSizeRunTimeJob(ScheduleJobDTO scheduleJobDTO) {
                        return null;
                    }

                    @Override
                    public List<JobTopErrorVO> listTopErrorByType(Long dtuicTenantId, Long tenantId, Long projectId, Integer type, String cycTime, List<Integer> status, PageQuery pageQuery, Integer appType) {
                        return null;
                    }

                    @Override
                    public List<JobTopErrorVO> listTopError(Long dtuicTenantId, Long projectId, Integer type, String startCycTime, String endCycTime, List<Integer> status, PageQuery pageQuery, Integer appType) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> listTodayJobs(String today, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
                        return null;
                    }

                    @Override
                    public List<TodayJobGraph> listTodayJobGraph(String today, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> listYesterdayJobs(String yesterday, String today, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> listMonthJobs(String lastMonth, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Object>> listThirtyDayJobs(List<Integer> statusList, Integer type, List<Integer> taskTypes, Long projectId, Long tenantId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listJobByJobKeys(Collection<String> jobKeys) {
                        return null;
                    }

                    @Override
                    public List<SimpleJob> listSimpleJobByJobKeys(Collection<String> jobKeys) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listRuleJobByJobKeys(Collection<String> jobKeys, Integer ruleCode) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listIdByTaskIdAndStatus(Long taskId, List<Integer> status, Integer appType, String cycTime, Integer type) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listIdByTaskIdAndStatusBeforeCyctime(Long taskId, List<Integer> status, Integer appType, String cycTime, Integer type) {
                        return null;
                    }

                    @Override
                    public List<String> listJobIdByTaskIdAndStatus(Long taskId, Integer appType, List<Integer> status) {
                        return null;
                    }

                    @Override
                    public List<Map<String, String>> listTaskExeTimeInfo(Long taskId, List<Integer> status, PageQuery pageQuery, Integer appType) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getByJobId(String jobId, Integer isDeleted) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getByJobIds(List<String> jobIds, Integer isDeleted) {
                        return null;
                    }

                    @Override
                    public Long getId(String jobId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> generalQuery(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> generalScienceQuery(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> generalQueryWithMinAndHour(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getJobRangeByCycTimeByLimit(Long taskId, boolean isAfter, String cycTime, Integer appType, Integer type, Integer limit, Long fillId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getJobRangeByCycTime(Long taskId, String startTime, String endTime, Integer appType) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getNewestTempJob(Long taskId, Integer appType, Integer type, Integer status) {
                        return null;
                    }

                    @Override
                    public Integer generalCount(ScheduleJobDTO object) {
                        return null;
                    }

                    @Override
                    public Integer generalScienceCount(ScheduleJobDTO object) {
                        return null;
                    }

                    @Override
                    public Integer generalCountWithMinAndHour(ScheduleJobDTO object) {
                        return null;
                    }

                    @Override
                    public Integer minOrHourJobCount(ScheduleJobDTO object) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> minOrHourJobQuery(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public List<StatusCount> getJobsStatusStatistics(ScheduleJobDTO object) {
                        return null;
                    }

                    @Override
                    public Integer batchInsert(Collection batchJobs) {
                        return null;
                    }

                    @Override
                    public Integer update(ScheduleJob scheduleJob) {
                        return null;
                    }

                    @Override
                    public Integer updateStatusWithExecTime(ScheduleJob job) {
                        return null;
                    }

                    @Override
                    public Integer updateNullTime(String jobId) {
                        return null;
                    }

                    @Override
                    public Integer updateToDeleted(List<String> jobIds, Integer appType) {
                        return 0;
                    }

                    @Override
                    public List<String> selectDeletedJobIds(Integer appType) {
                        return new ArrayList<>();
                    }

                    @Override
                    public List<String> listFillJobName(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public Integer countFillJobNameDistinct(ScheduleJobDTO batchJobDTO) {
                        return null;
                    }

                    @Override
                    public Integer countFillJobNameDistinctWithOutTask(ScheduleJobDTO batchJobDTO) {
                        return null;
                    }

                    @Override
                    public Integer countFillJobName(ScheduleJobDTO batchJobDTO) {
                        return null;
                    }

                    @Override
                    public List<String> listFillJobBizDate(ScheduleJobDTO dto) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listNeedStopFillDataJob(Long startId, String fillDataJobName, List<Integer> statusList, Long projectId, Integer appType, Integer limit) {
                        return null;
                    }


                    @Override
                    public List<Map<String, Object>> listTaskExeInfo(Long taskId, Long projectId, int limitNum, Integer appType, List<Integer> type) {
                        return null;
                    }


                    @Override
                    public List<ScheduleJob> getSubJobsAndStatusByFlowId(String jobId) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getSubJobsAndStatusByFlowIdLimit(String jobId) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Long>> countFillDataAllStatusByJobName(String jobName, List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getWorkFlowSubJobId(List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getSubJobsByFlowIds(List<String> flowIds) {
                        return null;
                    }

                    @Override
                    public List<Map<String, Long>> countByFillDataAllStatus(List<Long> fillJobIdList, Long projectId, Long tenantId, Integer appType, Integer type) {
                        return null;
                    }

                    @Override
                    public List<Long> listFillIdList(PageQuery<ScheduleJobDTO> pageQuer) {
                        return null;
                    }

                    @Override
                    public List<Long> listFillIdListWithOutTask(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> queryFillData(PageQuery pageQuery) {
                        return null;
                    }

                    @Override
                    public Integer countByFillData(ScheduleJobDTO batchJobDTO) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getWorkFlowTopNode(String jobId) {
                        return null;
                    }

                    @Override
                    public Map<String, Object> countScienceJobStatus(Integer runStatus, List<Long> projectIds, Integer type, List<Integer> taskTypes, long tenantId, String cycStartDay, String cycEndDay) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listByJobIdList(Collection<String> jobIds, Long projectId) {
                        return null;
                    }

                    @Override
                    public List<String> listJobIdByTaskType(Integer taskType) {
                        return null;
                    }

                    @Override
                    public Integer getStatusByJobId(String jobId) {
                        return null;
                    }

                    @Override
                    public Timestamp getJobExecStartTime(String jobId) {
                        return null;
                    }

                    @Override
                    public Integer countTasksByCycTimeTypeAndAddress(String nodeAddress, Integer scheduleType, String cycStartTime, String cycEndTime) {
                        return null;
                    }

                    @Override
                    public List<SimpleScheduleJobPO> listSimpleJobByStatusAddress(Long startId, List<Integer> statuses, String nodeAddress) {
                        return null;
                    }

                    @Override
                    public Integer updateNodeAddress(String nodeAddress, List<String> ids) {
                        return null;
                    }

                    @Override
                    public Integer updateJobStatusByIds(Integer status, List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public void stopUnsubmitJob(String likeName, Long projectId, Integer appType, Integer status) {

                    }

                    @Override
                    public List<ScheduleJob> listExecJobByCycTimeTypeAddress(Long startId, String nodeAddress, Integer scheduleType, String cycStartTime, String cycEndTime, Integer phaseStatus, Boolean isEq, Timestamp lastTime, Integer isRestart,Integer limit) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listExecJobByJobIds(String nodeAddress, Integer phaseStatus, Integer isRestart, Collection<String> jobIds) {
                        return null;
                    }

                    @Override
                    public Integer updateJobInfoByJobId(String jobId, Integer status, Timestamp execStartTime, Timestamp execEndTime, Long execTime, Integer retryNum, List<Integer> stopStatuses) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getByTaskIdAndStatusOrderByIdLimit(Long taskId, Integer status, Timestamp time, Integer appType, Integer type) {
                        return null;
                    }

                    @Override
                    public Integer updateStatusByJobId(String jobId, Integer status, Integer versionId, Date execStartTime, Date execEndTime) {
                        return null;
                    }

                    @Override
                    public Integer updateResourceIdByJobId(String jobId, Long resourceId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listJobByCyctimeAndJobName(String preCycTime, String preJobName, Integer scheduleType) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listJobByCyctimeAndJobNameBatch(Long startId, String preCycTime, String preJobName, Integer scheduleType, Integer batchJobSize) {
                        return null;
                    }

                    @Override
                    public Integer countJobByCyctimeAndJobName(String preCycTime, String preJobName, Integer scheduleType) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listJobsByTaskIdAndApptype(List<Long> taskIds, Integer appType) {
                        return null;
                    }

                    @Override
                    public void deleteByJobKey(List<String> jobKeyList) {

                    }

                    @Override
                    public List<String> getAllNodeAddress() {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> syncQueryJob(PageQuery<ScheduleJobDTO> pageQuery) {
                        return null;
                    }

                    @Override
                    public Integer insert(ScheduleJob scheduleJob) {
                        return null;
                    }

                    @Override
                    public void jobFinish(String jobId, int status) {

                    }

                    @Override
                    public void updateJobStatus(String jobId, int status) {

                    }

                    @Override
                    public void updateTaskStatusNotStopped(String jobId, int status, List<Integer> stopStatuses) {

                    }

                    @Override
                    public void updateJobStatusAndExecTime(String jobId, int status) {

                    }

                    @Override
                    public void updateJobSubmitSuccess(String jobId, String engineId, String appId) {

                    }

                    @Override
                    public void updateJobAppId(String jobId, String engineId, String appId) {

                    }

                    @Override
                    public ScheduleJob getRdosJobByJobId(String jobId) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getRdosJobByJobIds(List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getSimpleInfoByJobIds(List<String> jobIds) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getByName(String jobName) {
                        return null;
                    }

                    @Override
                    public void updateRetryNum(String jobId, Integer retryNum) {

                    }

                    @Override
                    public List<String> getJobIdsByStatus(Integer status, Integer computeType) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> listJobStatus(Timestamp timeStamp, Integer computeType, Integer appType) {
                        return null;
                    }

                    @Override
                    public Integer updateJobStatusByJobIds(List<String> jobIds, Integer status) {
                        return null;
                    }

                    @Override
                    public Integer updatePhaseStatusById(Long id, Integer original, Integer update) {
                        return null;
                    }

                    @Override
                    public Long getListMinId(String nodeAddress, Integer scheduleType, String left, String right, Integer code, Integer isRestart) {
                        return null;
                    }

                    @Override
                    public Integer updateListPhaseStatus(List<String> ids, Integer update) {
                        return null;
                    }

                    @Override
                    public Integer updateJobStatusAndPhaseStatus(List<String> jobIds, Integer status, Integer phaseStatus, Integer isRestart, String nodeAddress, Integer oldStatus) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getLastScheduleJob(Long taskId, Long id) {
                        return null;
                    }

                    @Override
                    public ScheduleJob getFatherJob(String jobId, Long taskId, Integer appType) {
                        return null;
                    }

                    @Override
                    public void updateStatusByJobIdEqualsStatus(String jobId, Integer status, Integer status1) {

                    }

                    @Override
                    public Integer updateJobStatusAndPhaseStatusByIds(List<String> jobIds, Integer status, Integer phaseStatus) {
                        return null;
                    }

                    @Override
                    public List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(Long startId, List<Integer> statuses, String nodeAddress, Integer phaseStatus) {
                        return null;
                    }

                    @Override
                    public Integer updateFlowJob(String placeholder, String flowJob) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> getByTaskIdAndAppType(Long id, Integer appType, Long taskId, Integer limit) {
                        return null;
                    }

                    @Override
                    public Integer pageFillDateCount(FillDataQueryDTO fillDataQueryDTO) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> pageFillDate(FillDataQueryDTO fillDataQueryDTO) {
                        return null;
                    }

                    @Override
                    public void updateJobStatusWithNodeAddress(String jobId, int status, String nodeAddress) {

                    }

                    @Override
                    public void updateJobStatusAndExecTimeWithNodeAddress(String jobId, int status, String nodeAddress) {

                    }

                    @Override
                    public void updateExecTimeAndStatusLogInfo(ScheduleJob updateJob) {

                    }

                    @Override
                    public void updateJobStatusANdExceTimeByIds(Integer status, List<String> jobIds) {

                    }

                    @Override
                    public void updateResourceIdByProjectIdAndOldResourceIdAndStatus(Long handOver, Long projectId, Long resourceId, Integer status) {

                    }

                    @Override
                    public void updateResourceIdByTaskIdAndOldResourceIdAndStatus(Long handOver, Long taskId, Integer appType, Long resourceId, Integer status) {

                    }

                    @Override
                    public void updateResourceByTaskIds(Long resourceId, Integer appType, Collection<Long> taskIds, Long dtuicTenantId, List<Integer> statusList) {

                    }

                    @Override
                    public List<ScheduleJob> listNoDeletedByJobIds(Collection<String> jobIds) {
                        return null;
                    }

                    @Override
                    public void updateByTaskIdAndVersionId(Integer versionId, Long taskId, Integer appType, Integer oldVersionId, Integer status) {

                    }

                    @Override
                    public List<Timestamp> findTopEndRunTimeByTaskIdsAndAppType(Long taskId, Integer appType, Integer num) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> selectJobByTaskIdAndJobExecuteOrder(Long start, Long end, List<Long> taskIds, Integer appType, String cycTime) {
                        return null;
                    }

                    @Override
                    public List<Integer> selectExecTimeByTaskIdAndAppType(Long taskId, Integer appType, Integer status, Integer maxBaselineJobNum) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> selectJobByTaskIdAndApptype(List<Long> taskIds, List<Integer> types, Integer appType, Long range) {
                        return null;
                    }

                    @Override
                    public Integer countByJobPageDTO(JobPageDTO dto) {
                        return null;
                    }

                    @Override
                    public List<ScheduleJob> pageByJobPageDTO(JobPageDTO dto) {
                        return null;
                    }

                    @Override
                    public List<StatusCount> statusStatisticByJobPageVO(JobPageDTO dto) {
                        return null;
                    }
                };
            }
            return null;
        }
        return interfaceClass.newInstance();
    }

    @MockNew
    public JobSubmitDealer createJobSubmitDealer(String localAddress, GroupPriorityQueue priorityQueue, ApplicationContext applicationContext) {
        return null;
    }

    @MockInvoke(targetClass = ExecutorService.class)
    public Future<?> submit(Runnable task) {
        return null;
    }

    @MockInvoke(targetClass = JobSubmitDealer.class)
    public int getDelayJobQueueSize() {
        return 1;
    }

}
