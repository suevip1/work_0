package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.dto.ScheduleSqlTextDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.JobStatusVo;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.OperatorVO;
import com.dtstack.engine.api.vo.RestartJobInfoVO;
import com.dtstack.engine.api.vo.RestartJobVO;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobBeanVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import com.dtstack.engine.api.vo.ScheduleServerLogVO;
import com.dtstack.engine.api.vo.TaskExeInfoVo;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataJobListVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobRuleTimeVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MockWith(ScheduleJobControllerTest.ScheduleJobControllerMock.class)
public class ScheduleJobControllerTest {

    private static final ScheduleJobController controller = new ScheduleJobController();

    static class ScheduleJobControllerMock {

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay,
                                                                                        Long bizStartDay, Long bizEndDay, Long dutyUserId,
                                                                                        Long projectId, Integer appType,
                                                                                        Integer currentPage, Integer pageSize, Long tenantId, Long dtuicTenantId, Integer fillDataType) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJob getJobById(long jobId) {
            return new ScheduleJob();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJob getJobByJobKeyAndType(String jobKey, int type) {
            return new ScheduleJob();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult getStatusJobList(Long projectId, Long tenantId, Integer appType, Long dtuicTenantId, Integer status, int pageSize, int pageIndex) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJobStatusVO getStatusCount(Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
            return new ScheduleJobStatusVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJobStatusVO> getStatusCountByProjectIds(List<Long> projectIds, Long tenantId, Integer appType, Long dtuicTenantId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<JobTopOrderVO> runTimeTopOrder(Long projectId, Long startTime, Long endTime, Integer appType, Long dtuicTenantId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<JobTopOrderVO> queryTopSizeRunTimeJob(ScheduleJobTopRunTimeVO topRunTimeVO) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<JobTopErrorVO> errorTopOrder(Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJobChartVO getJobGraph(Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
            return new ScheduleJobChartVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ChartDataVO getScienceJobGraph(long projectId, Long tenantId, String taskType) {
            return new ChartDataVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJobScienceJobStatusVO countScienceJobStatus(List<Long> projectIds, Long tenantId, Integer runStatus, Integer type, String taskType, String cycStartTime, String cycEndTime) {
            return new ScheduleJobScienceJobStatusVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<List<ScheduleJobVO>> queryJobs(QueryJobDTO vo) throws Exception {
            return PageResult.emptyPageResult();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<SchedulePeriodInfoVO> displayPeriods(boolean isAfter, Long jobId, Long projectId, int limit) throws Exception {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public com.dtstack.engine.master.vo.ScheduleJobVO getRelatedJobs(String jobId, String query) throws Exception {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Map<String, Long> queryJobsStatusStatistics(QueryJobDTO vo) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public JobStatusVo queryJobsStatusStatisticsToVo(QueryJobDTO vo) {
            return new JobStatusVo();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleRunDetailVO> jobDetail(Long taskId, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer updateStatusAndLogInfoAndExecTimeById(String jobId, Integer status, String msg, Date execStartTime, Date execEndTime) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer updateStatusAndLogInfoById(String jobId, Integer status, String msg) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer updateStatusByJobId(String jobId, Integer status, Integer versionId) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Long startJob(ScheduleJob scheduleJob) throws Exception {
            return 1L;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer updateStatusWithExecTime(ScheduleJob updateJob) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void testTrigger(String jobId) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void sendTaskStartTrigger(ScheduleJob scheduleJob) throws Exception {
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String stopJob(long jobId, Integer appType, Long operateId) throws Exception {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String stopJobByJobId(String jobId, Integer appType, Long operateId) throws Exception {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void stopFillDataJobs(String fillDataJobName, Long projectId, Long dtuicTenantId, Integer appType, Long operateId) throws Exception {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public int batchStopJobs(List<Long> jobIdList, Long operateId) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void insertJobList(Collection<ScheduleBatchJob> batchJobCollection, Integer scheduleType) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String fillTaskData(String taskJsonStr, String fillName, Long fromDay, Long toDay, String beginTime, String endTime, Long projectId, Long userId, Long tenantId, Boolean isRoot, Integer appType, Long dtuicTenantId, Boolean ignoreCycTime) throws Exception {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay, Long bizStartDay, Long bizEndDay, Long dutyUserId, Long projectId, Integer appType, Integer currentPage, Integer pageSize, Long tenantId, Long dtuicTenantId) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo, String fillJobName, Long dutyUserId) throws Exception {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<ScheduleFillDataJobDetailVO> getJobGetFillDataDetailInfo(String taskName, Long bizStartDay, Long bizEndDay, List<String> flowJobIdList, String fillJobName, Long dutyUserId, String searchType, Integer appType, Long projectId, Long dtuicTenantId, String execTimeSort, String execStartSort, String execEndSort, String cycSort, String businessDateSort, String retryNumSort, String taskType, String jobStatuses, Integer currentPage, Integer pageSize) throws Exception {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo(String queryJobDTO, List<String> flowJobIdList, String fillJobName, Long dutyUserId, String searchType, Integer appType) throws Exception {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData(String jobId, String query, String fillJobName) throws Exception {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<RestartJobVO> getRootRestartJob(String jobKey, boolean isOnlyNextChild) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> listByJobIds(List<String> jobIds) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Long getTaskShadeIdFromJobKey(String jobKey) {
            return 1L;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String getJobTriggerTimeFromJobKey(String jobKey) {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> getSubJobsAndStatusByFlowId(String jobId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<String> listJobIdByTaskNameAndStatusList(String taskName, List<Integer> statusList, Long projectId, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Map<String, ScheduleJob> getLabTaskRelationMap(List<String> jobIdList, Long projectId) {
            return new HashMap<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<Map<String, Object>> statisticsTaskRecentInfo(Long taskId, Integer appType, Long projectId, Integer count) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<TaskExeInfoVo> statisticsTaskRecentInfoToVo(Long taskId, Integer appType, Long projectId, Integer count) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer BatchJobsBatchUpdate(String jobs) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer updateTimeNull(String jobId) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJob getById(Long id) {
            return new ScheduleJob();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJob getByJobId(String jobId, Integer isDeleted) {
            return new ScheduleJob();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer getJobStatus(String jobId) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Timestamp getJobExecStartTime(String jobId) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> getByIds(List<Long> ids) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> getSameDayChildJob(String batchJob, boolean isOnlyNextChild) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> getSameDayChildJob(ScheduleJob job, boolean isOnlyNextChild) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer generalCount(ScheduleJobDTO query) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer generalCountWithMinAndHour(ScheduleJobDTO query) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> generalQuery(PageQuery query) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> generalQueryWithMinAndHour(PageQuery query) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJob getLastSuccessJob(Long taskId, Timestamp time, Integer appType) {
            return new ScheduleJob();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleServerLogVO setAlogrithmLabLog(Integer status, Integer taskType, String jobId, String info, String logVo, Integer appType) throws Exception {
            return new ScheduleServerLogVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ActionLogVO getLogInfoFromEngine(String jobId) {
            return new ActionLogVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJobVO> minOrHourJobQuery(ScheduleJobDTO batchJobDTO) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void updateJobStatusAndLogInfo(String jobId, Integer status, String logInfo) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String testCheckCanRun(String jobId) {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void createTodayTaskShade(Long taskId, Integer appType, String date) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> listByCyctimeAndJobName(String preCycTime, String preJobName, Integer scheduleType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> listByCyctimeAndJobName(Long startId, String preCycTime, String preJobName, Integer scheduleType, Integer batchJobSize) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer countByCyctimeAndJobName(String preCycTime, String preJobName, Integer scheduleType) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void deleteJobsByJobKey(List<String> jobKeyList) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> syncBatchJob(QueryJobDTO dto) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> listJobsByTaskIdsAndApptype(List<Long> taskIds, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void buildTaskJobGraphTest(String triggerDay) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
            return true;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Long getListMinId(String left, String right) {
            return 1L;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String getJobGraphJSON(String jobId) {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void updateNotRuleResult(String jobId, Integer rule, String result) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void updateStatusByJobIdEqualsStatus(String jobId, Integer status, Integer status1) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> listJobByJobKeys(List<String> parentJobKeys) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Map<String, List<ScheduleJob>> getParentJobKeyMap(List<String> parentJobKeys) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void handleTaskRule(ScheduleJob scheduleJob, Integer bottleStatus) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public boolean hasTaskRule(ScheduleJob scheduleJob) {
            return true;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void updateLogInfoById(String jobId, String msg) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJobBeanVO> findTaskRuleJobById(Long id) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJob> getTaskRuleSonJob(ScheduleJob scheduleJob) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleDetailsVO findTaskRuleJob(String jobId) {
            return new ScheduleDetailsVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public boolean syncRestartJob(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds) {
            return true;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public boolean restartJob(RestartType restartType, List<String> jobIds, Long operateId) {
            return true;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer stopJobByConditionTotal(ScheduleJobKillJobVO vo) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer stopJobByCondition(ScheduleJobKillJobVO vo) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleJobRuleTimeVO> getJobsRuleTime(List<ScheduleJobRuleTimeVO> jobList) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void removeOperatorRecord(Collection<String> jobIds, Collection<ScheduleJobOperatorRecord> records) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Integer updateFlowJob(String placeholder, String flowJob) {
            return 1;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public String getJobExtraInfoOfValue(String jobId, String key) {
            return "";
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public OperatorVO restartJobAndResume(List<Long> jobIdList, Boolean runCurrentJob) {
            return new OperatorVO();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public Long fillData(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO) {
            return 1L;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void createFillJob(Set<String> all, Set<String> run, List<String> blackTaskKeyList, Long fillId, String fillName, String beginTime, String endTime, String startDay, String endDay, Long projectId, Long tenantId, Long dtuicTenantId, Long userId, Boolean ignoreCycTime) throws Exception {
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult<ScheduleFillDataJobDetailVO> fillDataJobList(FillDataJobListVO vo) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void batchRestartScheduleJob(Map<String, String> resumeBatchJobs) {
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<RestartJobInfoVO> restartJobInfo(String jobKey, Long parentTaskId, Integer level) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public PageResult queryStreamJobs(PageQuery<ScheduleJobDTO> pageQuery) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<StatusCount> queryStreamJobsStatusStatistics(ScheduleJobDTO scheduleJobDTO) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void addOrUpdateScheduleJob(ScheduleJob scheduleJob) {

        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<ScheduleSqlTextDTO> querySqlText(List<String> jobIds) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public List<Map<String, Object>> statisticsTaskRecentInfo(Long taskId, Integer appType, Long projectId, Integer count, Integer type) {
            return new ArrayList<>();
        }
    }

    @Test
    public void getJobById() {
        controller.getJobById(1L);
    }

    @Test
    public void getStatusJobList() {
        controller.getStatusJobList(1L,null,null,null,null,1,1);
    }

    @Test
    public void getStatusCount() {
        controller.getStatusCount(null,1L,1,1L);
    }

    @Test
    public void getStatusCountByProjectIds() {
        controller.getStatusCountByProjectIds(null,1L,1,1L);
    }

    @Test
    public void runTimeTopOrder() {
        controller.runTimeTopOrder(1L,1L,1L,1,11L);
    }

    @Test
    public void errorTopOrder() {
        controller.errorTopOrder(1L,1L,1,1L);
    }

    @Test
    public void getJobGraph() {
        controller.getJobGraph(1L,1L,1,1L);
    }

    @Test
    public void getScienceJobGraph() {
        controller.getScienceJobGraph(1L,1L,"");
    }

    @Test
    public void countScienceJobStatus() {
        controller.countScienceJobStatus(null,null,null,null,null,null,null);
    }

    @Test
    public void queryJobs() throws Exception {
        controller.queryJobs(null);
    }

    @Test
    public void displayPeriods() throws Exception {
        controller.displayPeriods(true,1L,1L,1);
    }

    @Test
    public void getRelatedJobs() throws Exception {
        ScheduleJobVO relatedJobs = controller.getRelatedJobs("", "");
    }

    @Test
    public void queryJobsStatusStatistics() {
        controller.queryJobsStatusStatistics(null);
    }

    @Test
    public void jobDetail() {
        controller.jobDetail(1L,1);
    }

    @Test
    public void sendTaskStartTrigger() throws Exception {
        controller.sendTaskStartTrigger(new ScheduleJob());
    }

    @Test
    public void stopJob() throws Exception {
        controller.stopJob(1L,1,1L);
    }

    @Test
    public void stopFillDataJobs() throws Exception {
        controller.stopFillDataJobs("",1L,1L,1,1L);
    }

    @Test
    public void batchStopJobs() {
        controller.batchStopJobs(null,1L);
    }

    @Test
    public void fillTaskData() throws Exception {
        controller.fillTaskData("1",null,null,null,null,null,null,null,null,null,null,null,null);
    }

    @Test
    public void getFillDataJobInfoPreview() {
        controller.getFillDataJobInfoPreview(null,null,null,null,null,null,null,null,null,null,null);
    }

    @Test
    public void getFillDataDetailInfoOld() throws Exception {
        controller.getFillDataDetailInfoOld(null,null,null);
    }

    @Test
    public void getFillDataDetailInfo() throws Exception {
        controller.getFillDataDetailInfo(null,null,null,null,null,null);
    }

    @Test
    public void getJobGetFillDataDetailInfo() throws Exception {
        controller.getJobGetFillDataDetailInfo(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    @Test
    public void getRelatedJobsForFillData() throws Exception {
        controller.getRelatedJobsForFillData(null,null,null);
    }

    @Test
    public void getRestartChildJob() {
        controller.getRestartChildJob(null,null,true);
    }

    @Test
    public void listJobIdByTaskNameAndStatusList() {
        controller.listJobIdByTaskNameAndStatusList(null,null,null,null);
    }

    @Test
    public void getLabTaskRelationMap() {
        controller.getLabTaskRelationMap(null,null);
    }

    @Test
    public void statisticsTaskRecentInfo() {
        controller.statisticsTaskRecentInfo(null,null,null,null);
    }


    @Test
    public void updateTimeNull() {
        controller.updateTimeNull("1L");
    }

    @Test
    public void getById() {
        controller.getById(1L);
    }

    @Test
    public void getByJobId() {
        controller.getByJobId("",1);
    }

    @Test
    public void getByIds() {
        controller.getByIds(null);
    }

    @Test
    public void getSameDayChildJob() {
        controller.getSameDayChildJob("",true,1);
    }

    @Test
    public void getAllChildJobWithSameDay() {
        controller.getAllChildJobWithSameDay(new ScheduleJob(),true,1);
    }

    @Test(expected = Exception.class)
    public void generalCount() {
        controller.generalCount(null);
    }

    @Test
    public void generalCountWithMinAndHour() {
        controller.generalCountWithMinAndHour(null);
    }

    @Test
    public void generalQuery() {
        controller.generalQuery(null);
    }

    @Test
    public void generalQueryWithMinAndHour() {
        controller.generalCountWithMinAndHour(null);
    }

    @Test
    public void getLastSuccessJob() {
        controller.getLastSuccessJob(null,null,null);
    }

    @Test
    public void setAlogrithmLabLog() throws Exception {
        controller.setAlogrithmLabLog(null,null,null,null,null,null);
    }

    @Test
    public void minOrHourJobQuery() {
        controller.minOrHourJobQuery(null);
    }

    @Test
    public void updateJobStatusAndLogInfo() {
        controller.updateJobStatusAndLogInfo(null,null,null);
    }

    @Test
    public void createTodayTaskShade() {
        controller.createTodayTaskShade(null,null,null);
    }

    @Test
    public void listByBusinessDateAndPeriodTypeAndStatusList() {
        controller.listByBusinessDateAndPeriodTypeAndStatusList(null);
    }

    @Test
    public void listByCyctimeAndJobName() {
        controller.listByCyctimeAndJobName(null,null,null,null,null);
    }

    @Test
    public void testListByCyctimeAndJobName() {
        controller.listByCyctimeAndJobName(null,null,null);
    }

    @Test
    public void countByCyctimeAndJobName() {
        controller.countByCyctimeAndJobName("",null,null);
    }

    @Test
    public void deleteJobsByJobKey() {
        controller.deleteJobsByJobKey(null);
    }

    @Test
    public void syncBatchJob() {
        controller.syncBatchJob(null);
    }

    @Test
    public void listJobsByTaskIdsAndApptype() {
        controller.listJobsByTaskIdsAndApptype(null,1);
    }

    @Test
    public void stopJobByJobId() throws Exception {
        controller.stopJobByJobId("",1,1L);
    }

    @Test
    public void buildTaskJobGraphTest() {
        controller.buildTaskJobGraphTest(null);
    }

    @Test
    public void testTrigger() {
        controller.testTrigger("");
    }

    @Test
    public void getJobGraphJSON() {
        controller.getJobGraphJSON("");
    }

    @Test
    public void updateNotRuleResult() {
        controller.updateNotRuleResult("",1,"");
    }

    @Test
    public void findTaskRuleJobById() {
        controller.findTaskRuleJobById(1L);
    }

    @Test
    public void findTaskRuleJob() {
        controller.findTaskRuleJob(null);
    }

    @Test
    public void syncRestartJob() {
        controller.syncRestartJob(null,null,null,null);
    }

    @Test
    public void restartJobAndResume() {
        controller.restartJobAndResume(null,null);
    }

    @Test
    public void stopJobByCondition() {
        controller.stopJobByCondition(null);
    }

    @Test
    public void getJobsRuleTime() {
        controller.getJobsRuleTime(null);
    }
}