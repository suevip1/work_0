package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.JobGraphTriggerDao;
import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleJobFailedDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.dao.ScheduleJobParamDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.ScheduleJobCount;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.dto.SimpleJob;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.dto.TaskName;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.impl.ScheduleFillDataJobService;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.queue.JobPartitioner;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.sync.FillDataRunnable;
import com.dtstack.engine.master.sync.FillDataThreadPoolExecutor;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.po.ScheduleJobParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;
import org.codehaus.jackson.node.ArrayNode;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class ScheduleJobServiceMock extends BaseMock {


    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime,
                               Integer scheduleType) {
        return "";
    }

    @MockInvoke(targetClass = ActionService.class)
    public void fillConsoleParam(Map<String, Object> actionParam, Long taskId, Integer appType, String jobId) {

    }

    @MockInvoke(targetClass = ParamService.class)
    public void convertGlobalToParamType(String info, BiConsumer<List<ConsoleParamBO>, List<ScheduleTaskParamShade>> convertConsumer) {}


        @MockInvoke(targetClass = ScheduleJobParamDao.class)
    public List<ScheduleJobParam> selectByJobId(@Param("jobId") String jobId) {
        return new ArrayList<>();
    }


    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    public Integer clearLog(@Param("jobIds") List<String> jobIds) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public ScheduleJob getOne(Long jobId) {
        return new ScheduleJob();
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        ScheduleJob scheduleJob = new ScheduleJob();
        if ("WorkFlow".equalsIgnoreCase(jobId)) {
            scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        } else if ("noTask".equals(jobId)) {
            scheduleJob.setTaskType(EScheduleJobType.NOT_DO_TASK.getType());
        }
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        if ("stop".equalsIgnoreCase(jobId)) {
            scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        }
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setPeriodType(ESchedulePeriodType.CALENDER.getVal());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId, Integer isDeleted) {
        ScheduleJob scheduleJob = new ScheduleJob();
        if ("WorkFlow".equalsIgnoreCase(jobId)) {
            scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        }
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        if ("WorkFlow".equalsIgnoreCase(jobId)) {
            scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        }
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public ScheduleJob getByJobKeyAndType(String jobKey, int type) {
        return new ScheduleJob();
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Object>> countByStatusAndType(Integer type, String startTime, String endTime, Long tenantId,
                                                   Long projectId, Integer appType, Long dtuicTenantId, List<Integer> status) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("count", 100);
        count.put("status", RdosTaskStatus.UNSUBMIT.getStatus());
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Object>> selectStatusAndType(Integer type, String startTime, String endTime, Long tenantId,
                                                  Long projectId, Integer appType, Long dtuicTenantId, List<Integer> status, Integer pageSize, Integer PageNo) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("count", 100);
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJobCount> countByStatusAndTypeProjectIds(Integer type, String startTime,
                                                          String endTime, Long tenantId, List<Long> projectIds, Integer appType, Long dtuicTenantId, List<Integer> status) {
        List<ScheduleJobCount> result = new ArrayList<>();
        ScheduleJobCount scheduleJobCount = new ScheduleJobCount();
        scheduleJobCount.setProjectId(1L);
        scheduleJobCount.setCount(100);
        scheduleJobCount.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        result.add(scheduleJobCount);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    public ScheduleJobExpand getLogByJobId(String jobId) {
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setLogInfo("{}");
        scheduleJobExpand.setEngineLog("{\"driverLog\": [], \"appLog\": [{\"id\":\"application_1639670432612_22987\",\"value\":\"\"}]}");
        return scheduleJobExpand;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public List<Map<String, Object>> listTopRunTime(Long projectId, Timestamp startTime, Timestamp endTime, PageQuery pageQuery, Integer appType, Long dtuicTenantId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("id", 100);
        count.put("taskId", 100);
        count.put("cycTime", "20220518000000");
        count.put("type", "0");
        count.put("execTime", "10");
        count.put("taskType", "2");
        count.put("isDeleted", "0");
        count.put("createUserId", "0");
        count.put("taskName", "test");
        count.put("ownerUserId", "0");
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public List<Map<String, Object>> listTopSizeRunTimeJob(ScheduleJobDTO scheduleJobDTO) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("id", 100);
        count.put("taskId", 100);
        count.put("cycTime", "20220518000000");
        count.put("type", "0");
        count.put("execTime", "10");
        count.put("taskType", "2");
        count.put("isDeleted", "0");
        count.put("createUserId", "0");
        count.put("taskName", "test");
        count.put("ownerUserId", "0");
        result.add(count);
        return result;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    public List<JobTopErrorVO> listTopErrorByType(Long dtuicTenantId, Long tenantId, Long projectId, Integer type, String cycTime, List<Integer> status, PageQuery pageQuery, Integer appType) {
        List<JobTopErrorVO> jobTopErrorVOS = new ArrayList<>();
        JobTopErrorVO jobTopErrorVO = new JobTopErrorVO();
        jobTopErrorVO.setErrorCount(1);
        jobTopErrorVO.setTaskName("test");
        jobTopErrorVO.setTaskId(1L);
        jobTopErrorVO.setCreateUser("admin");
        jobTopErrorVO.setIsDeleted(1);
        jobTopErrorVOS.add(jobTopErrorVO);
        return jobTopErrorVOS;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Object>> listTodayJobs(String today, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("hour", 11);
        count.put("data", 100);
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Object>> listYesterdayJobs(String yesterday, String today, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("hour", 11);
        count.put("data", 100);
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Object>> listMonthJobs(String lastMonth, List<Integer> statusList, Integer type, Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("hour", 11);
        count.put("data", 100);
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Object>> listThirtyDayJobs(List<Integer> statusList, Integer type, List<Integer> taskTypes, Long projectId, Long tenantId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> count = new HashMap<>();
        count.put("hour", 11);
        count.put("data", 100);
        result.add(count);
        return result;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Map<String, Object> countScienceJobStatus(Integer runStatus, List<Long> projectIds, Integer type, List<Integer> taskTypes, long tenantId, String cycStartDay, String cycEndDay) {
        Map<String, Object> count = new HashMap<>();
        count.put("deployCount", 11);
        count.put("failCount", 100);
        count.put("successCount", 100);
        count.put("total", 100);
        return count;
    }

    @MockInvoke(targetClass = ScheduleJobFailedDao.class)
    List<JobTopErrorVO> listTopError(Integer appType,
                                     Long dtuicTenantId,
                                     Long projectId,
                                     Timestamp timeTo) {
        List<JobTopErrorVO> jobTopErrorVOS = new ArrayList<>();
        JobTopErrorVO jobTopErrorVO = new JobTopErrorVO();
        jobTopErrorVO.setErrorCount(1);
        jobTopErrorVO.setTaskName("test");
        jobTopErrorVO.setTaskId(1L);
        jobTopErrorVO.setCreateUser("admin");
        jobTopErrorVO.setIsDeleted(1);
        jobTopErrorVOS.add(jobTopErrorVO);
        return jobTopErrorVOS;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer generalCount(ScheduleJobDTO scheduleJobDTO) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> generalQuery(PageQuery<ScheduleJobDTO> pageQuery) {
        List<ScheduleJob> scheduleJobs = new ArrayList<>();
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setResourceId(1L);
        scheduleJob.setBusinessDate("20220214000000");
        scheduleJobs.add(scheduleJob);
        scheduleJob.setCycTime("20220215000000");
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        return scheduleJobs;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listAfterOrBeforeJobs(Long taskId, boolean isAfter, String cycTime, Integer appType, Integer type) {
        List<ScheduleJob> scheduleJobs = new ArrayList<>();
        ScheduleJob gtScheduleJob = new ScheduleJob();
        gtScheduleJob.setCycTime("2022051800000");
        scheduleJobs.add(gtScheduleJob);

        ScheduleJob ltScheduleJob = new ScheduleJob();
        ltScheduleJob.setCycTime("2022051900000");
        scheduleJobs.add(ltScheduleJob);
        return scheduleJobs;
    }


    @MockInvoke(targetClass = UserService.class)
    public void fillTopOrderJobUserName(List<JobTopOrderVO> jobTopOrderVOS) {
    }

    @MockInvoke(targetClass = UserService.class)
    public void fillScheduleJobVO(List<ScheduleJobVO> jobVOS) {
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public Map<Long, ResourceGroupDetail> getGroupInfo(List<Long> resourceIds) {
        return new HashMap<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskForFillDataDTO> listSimpleTaskByTaskIds(Collection<Long> taskIds, Integer isDeleted, Integer appType) {
        List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS = new ArrayList<>();
        ScheduleTaskForFillDataDTO scheduleTaskForFillDataDTO = new ScheduleTaskForFillDataDTO();
        scheduleTaskForFillDataDTO.setTaskId(100L);
        scheduleTaskForFillDataDTO.setName("test");
        scheduleTaskForFillDataDTO.setOwnerUserId(1L);
        scheduleTaskForFillDataDTO.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleTaskForFillDataDTOS.add(scheduleTaskForFillDataDTO);
        return scheduleTaskForFillDataDTOS;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.HIVE_SQL.getType());
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer updateLogInfoByJobId(String jobId, String logInfo) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateStatusByJobId(String jobId, Integer status, Integer versionId, Date execStartTime, Date execEndTime) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateStatusWithExecTime(ScheduleJob job) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    String getExtInfoByTaskId(long taskId, Integer appType) {
        JSONObject object = new JSONObject();
        object.put(TaskConstant.INFO, new JSONObject());
        return object.toJSONString();
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, String>> listTaskExeTimeInfo(Long taskId, List<Integer> status, PageQuery pageQuery, Integer appType) {
        Map<String, String> job = new HashMap<>();
        job.put("execStartTime", "2022051800000");
        job.put("execEndTime", "2022051800000");
        job.put("execTime", "2");
        return Lists.newArrayList(job);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<StatusCount> getJobsStatusStatistics(ScheduleJobDTO object) {
        List<StatusCount> statusCounts = new ArrayList<>(0);
        StatusCount statusCount = new StatusCount();
        statusCount.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        statusCount.setCount(100);
        statusCounts.add(statusCount);
        return statusCounts;
    }

    @MockInvoke(targetClass = ActionService.class)
    public ParamActionExt paramActionExt(ScheduleTaskShade batchTask, ScheduleJob scheduleJob, JSONObject extraInfo) throws Exception {
        return new ParamActionExt();
    }

    @MockInvoke(targetClass = ActionService.class)
    public Boolean start(ParamActionExt paramActionExt) {
        return true;
    }

    @MockInvoke(targetClass = JobStopDealer.class)
    public int addStopJobs(List<ScheduleJob> jobs, Long operateId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJob> listByParentJobKey(String jobKey, Integer relyType) {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey("cronJob_47_20220524000000");
        scheduleJobJob.setParentJobKey("cronTrigger_38981_20220226000000");
        scheduleJobJob.setRelyType(TaskRuleEnum.STRONG_RULE.getCode());
        return Lists.newArrayList(scheduleJobJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listJobByJobKeys(Collection<String> jobKeys) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setJobKey("cronJob_47_20220226000000");
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        scheduleJob.setPeriodType(ESchedulePeriodType.HOUR.getVal());
        return Lists.newArrayList(scheduleJob);
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listNeedStopFillDataJob(String fillDataJobName, List<Integer> statusList,
                                              Long projectId, Integer appType) {
        return Lists.newArrayList(new ScheduleJob());
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void stopUnsubmitJob(String likeName, Long projectId, Integer appType, Integer status) {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> getWorkFlowSubJobId(List<String> jobIds) {
        return Lists.newArrayList(new ScheduleJob());
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listByJobIds(List<String> jobIds) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer batchInsert(Collection batchJobs) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer batchInsert(List<ScheduleJobExpand> expands) {
        return 1;
    }

    @MockInvoke(targetClass = JobPartitioner.class)
    public Map<String, Integer> computeBatchJobSize(Integer type, int jobSize) {
        Map<String, Integer> jobSizeMap = new HashMap<>();
        jobSizeMap.put("127.0.0.1:8090", 100);
        return jobSizeMap;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobService.class)
    public boolean checkExistsName(String jobName, Long projectId) {
        return false;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobService.class)
    public ScheduleFillDataJob saveData(String jobName, Long tenantId, Long projectId, String runDay,
                                        String fromDay, String toDay, Long userId, Integer appType, Long dtuicTenantId) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        scheduleFillDataJob.setId(1L);
        return scheduleFillDataJob;
    }

    @MockInvoke(targetClass = JobGraphBuilder.class)
    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(ArrayNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId, Long projectId,
                                                               Long tenantId, Boolean isRoot, Integer appType, Long fillId, Long dtuicTenantId,
                                                               AtomicInteger count) throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
        HashMap<String, ScheduleBatchJob> result = new HashMap<>();
        result.put(scheduleBatchJob.getJobKey(), scheduleBatchJob);
        return result;
    }


    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    Long insertBatch(Collection<ScheduleJobOperatorRecord> records) {
        return 1L;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTasksByName(Long projectId,
                                                  String name, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer countListFillJobByPageQuery(PageQuery<ScheduleJobDTO> pageQuery) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    List<ScheduleFillDataJob> listFillJobByPageQuery(PageQuery<ScheduleJobDTO> pageQuery) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        scheduleFillDataJob.setId(1L);
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        return Lists.newArrayList(scheduleFillDataJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<Map<String, Long>> countByFillDataAllStatus(List<Long> fillJobIdList, Long projectId, Long tenantId, Integer appType) {
        Map<String, Long> status = new HashMap<>();
        status.put("fillId", 1L);
        status.put("status", RdosTaskStatus.UNSUBMIT.getStatus().longValue());
        status.put("count", 1L);
        return Lists.newArrayList(status);
    }

    @MockInvoke(targetClass = UserService.class)
    public void fillFillDataJobUserName(List<ScheduleFillDataJobPreViewVO> resultContent) {
        return;
    }

    @MockInvoke(targetClass = UserService.class)
    public void fillScheduleTaskForFillDataDTO(List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listByNameLikeWithSearchType(Long projectId, String name, Integer appType,
                                                         Long ownerId, List<Long> projectIds, Integer searchType, List<String> componentVersions, Integer computeType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    ScheduleFillDataJob getByJobName(String jobName, Long projectId) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        scheduleFillDataJob.setId(1L);
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        return scheduleFillDataJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer countByFillData(ScheduleJobDTO batchJobDTO) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> queryFillData(PageQuery pageQuery) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobKey(String jobKey) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setJobKey("cronJob_47_20220226000000");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShade getById(Long id) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleJobJobService.class)
    public List<ScheduleJobJob> getJobChild(Set<String> parentJobKey) {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey("cronJob_47_20220226000000");
        scheduleJobJob.setParentJobKey("cronTrigger_38981_20220226000000");
        scheduleJobJob.setRelyType(TaskRuleEnum.STRONG_RULE.getCode());
        return Lists.newArrayList(scheduleJobJob);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public Map<Long, TaskName> getNamesByIds(Collection<Long> ids) {
        Map<Long, TaskName> nameMap = new HashMap<>();
        TaskName taskName = new TaskName();
        taskName.setId(47L);
        taskName.setName("test");
        nameMap.put(47L, taskName);
        return nameMap;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<SimpleJob> listSimpleJobByJobKeys(Collection<String> jobKeys) {
        SimpleJob scheduleJob = new SimpleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setJobKey("cronJob_47_20220226000000");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listByJobIdList(Collection<String> jobIds, Long projectId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setJobKey("cronJob_47_20220226000000");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setJobId("1");
        scheduleJob.setAppType(AppType.RDOS.getType());
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer update(ScheduleJob scheduleJob) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public Map<Long, ScheduleTaskShade> getByIds(Collection<Long> ids) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        Map<Long, ScheduleTaskShade> map = new HashMap<>();
        map.put(100L, scheduleTaskShade);
        return map;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOneByTaskIdAndAppType(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.HIVE_SQL.getType());
        JSONObject jsonObject = new JSONObject();
        JSONObject info = new JSONObject();
        info.put("sqlText", "show tables");
        jsonObject.put(TaskConstant.INFO, info);
        scheduleTaskShade.setExtraInfo(jsonObject.toJSONString());
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime) {
        return sql;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listSimpleByTaskIds(Collection<Long> taskIds, Integer isDeleted, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleTaskShade.setOwnerUserId(1L);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = UserService.class)
    public List<User> findUserWithFill(Set<Long> userIds) {
        User user = new User();
        user.setDtuicUserId(1L);
        return Lists.newArrayList(user);
    }

    @MockInvoke(targetClass = ScheduleJobJobService.class)
    public List<ScheduleJobJob> getJobChild(String parentJobKey) {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey("cronJob_47_20220226000000");
        scheduleJobJob.setParentJobKey("cronTrigger_38981_20220226000000");
        scheduleJobJob.setRelyType(TaskRuleEnum.STRONG_RULE.getCode());
        return Lists.newArrayList(scheduleJobJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateJobStatusAndPhaseStatus(List<String> jobIds, Integer status, Integer phaseStatus, Integer isRestart, String nodeAddress) {
        return 1;
    }

    @MockInvoke(targetClass = JobGraphBuilder.class)
    public void buildFillDataJobGraph(String fillName, Long fillId, Set<String> all, Set<String> run,
                                      List<String> blackTaskKey, String triggerDay, String beginTime,
                                      String endTime, Long projectId, Long tenantId, Long dtuicTenantId,
                                      Long userId, Boolean ignoreCycTime) throws Exception {
        return;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer insert(ScheduleFillDataJob fillDataJob) {
        return 1;
    }

    @MockInvoke(targetClass = FillDataThreadPoolExecutor.class)
    public void submit(FillDataRunnable fillDataRunnable) {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public boolean syncRestartJob(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    public String getJobExtraInfo(String jobId) {
        JSONObject object = new JSONObject();
        object.put("key", "test");
        return object.toJSONString();
    }

    @MockInvoke(targetClass = EngineJobCache.class)
    public EngineJobCache getOne(String jobId) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId(jobId);
        engineJobCache.setStage(EJobCacheStage.DB.getStage());
        return engineJobCache;
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String convertParam(Integer type, String paramName, String paramCommand, String cycTime, Long taskId) {
        return paramCommand;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public Long getCalenderIdByTaskId(Long taskId, Integer appType) {
        return -1L;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public ConsoleCalender findById(long calenderId) {
        ConsoleCalender consoleCalender = new ConsoleCalender();
        consoleCalender.setCalenderName("test");
        return consoleCalender;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOne(long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.HIVE_SQL.getType());
        JSONObject jsonObject = new JSONObject();
        JSONObject info = new JSONObject();
        info.put("sqlText", "show tables");
        jsonObject.put(TaskConstant.INFO, info);
        scheduleTaskShade.setExtraInfo(jsonObject.toJSONString());
        return scheduleTaskShade;
    }


    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public AuthProjectVO finProject(Long projectId, Integer appType) {
        AuthProjectVO authProjectVO = new AuthProjectVO();
        authProjectVO.setProjectName("test");
        return authProjectVO;
    }

    @MockInvoke(targetClass = TenantService.class)
    public String getTenantName(Long tenantId) {
        return "";
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public Optional<ResourceGroup> getResourceGroup(Long resourceId) {
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setName("test");
        return Optional.ofNullable(resourceGroup);
    }

    @MockInvoke(targetClass = ScheduleJobOperateService.class)
    public Integer count(String jobId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJob> listByJobKey(String jobKey, Integer relyType) {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey("cronJob_47_20220226000000");
        scheduleJobJob.setParentJobKey("cronTrigger_38981_20220226000000");
        scheduleJobJob.setRelyType(TaskRuleEnum.STRONG_RULE.getCode());
        return Lists.newArrayList(scheduleJobJob);
    }


    @MockInvoke(targetClass = JobGraphTriggerDao.class)
    String getMinJobIdByTriggerTime(String triggerStartTime, String triggerEndTime) {
        return "0";
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updatePhaseStatusById(Long id, Integer original, Integer update) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> syncQueryJob(PageQuery<ScheduleJobDTO> pageQuery) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = JobGraphBuilder.class)
    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType,
                                                  boolean needAddFather, boolean needSelfDependency, String triggerDay,
                                                  String jobName, Long createUserId, Long projectId, Long tenantId, AtomicInteger count) throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setFlowJobId("0");
        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
        return Lists.newArrayList(scheduleBatchJob);
    }

    @MockInvoke(targetClass = JobRichOperator.class)
    public JobCheckRunInfo checkJobCanRun(ScheduleBatchJob scheduleBatchJob, Integer status, Integer scheduleType,
                                          ScheduleTaskShade batchTaskShade) throws ParseException {

        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        return jobCheckRunInfo;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer minOrHourJobCount(ScheduleJobDTO object) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> getSubJobsByFlowIds(List<String> flowIds){
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setFlowJobId("0");
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> minOrHourJobQuery(PageQuery<ScheduleJobDTO> pageQuery) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setBusinessDate("202202180035");
        scheduleJob.setCycTime("20220218003500");
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setFlowJobId("0");
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listByTaskIds( Collection<Long> taskIds,  Integer isDeleted, Integer appType){
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.HIVE_SQL.getType());
        JSONObject jsonObject = new JSONObject();
        JSONObject info = new JSONObject();
        info.put("sqlText", "show tables");
        jsonObject.put(TaskConstant.INFO, info);
        scheduleTaskShade.setExtraInfo(jsonObject.toJSONString());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ActionService.class)
    public ActionLogVO log(String jobId, Integer computeType) throws Exception {
        ActionLogVO actionLogVO = new ActionLogVO();
        actionLogVO.setEngineLog("");
        actionLogVO.setLogInfo("");
        return actionLogVO;
    }

}