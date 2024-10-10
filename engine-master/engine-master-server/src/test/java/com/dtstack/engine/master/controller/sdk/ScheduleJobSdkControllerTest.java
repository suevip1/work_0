package com.dtstack.engine.master.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleSqlTextDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.FillDataParams;
import com.dtstack.engine.api.vo.JobStatusVo;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.RestartJobInfoVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.TaskExeInfoVo;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.mapstruct.ScheduleJobStruct;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@MockWith(ScheduleJobSdkControllerMock.class)
public class ScheduleJobSdkControllerTest {
    ScheduleJobSdkController scheduleJobSdkController = new ScheduleJobSdkController();

    @Test
    public void testRestartJob() throws Exception {
        scheduleJobSdkController.restartJob(Collections.singletonList("String"), 0, 1L);
    }

    @Test
    public void testRestartJobInfo() throws Exception {
        scheduleJobSdkController.restartJobInfo("jobKey", 1L);
    }

    @Test
    public void testQueryTopTenJob() throws Exception {
        ScheduleJobTopRunTimeVO scheduleJobTopRunTimeVO = new ScheduleJobTopRunTimeVO();
        scheduleJobTopRunTimeVO.setStartTime(1L);
        scheduleJobTopRunTimeVO.setEndTime(2l);
        scheduleJobTopRunTimeVO.setTopSize(1);
        scheduleJobSdkController.queryTopTenJob(scheduleJobTopRunTimeVO);
    }

    @Test
    public void testListByJobIds() throws Exception {
        scheduleJobSdkController.listByJobIds(Arrays.asList("String"));
    }

    @Test
    public void testQueryJobsStatusStatistics() throws Exception {
        scheduleJobSdkController.queryJobsStatusStatistics(new QueryJobDTO());
    }

    @Test
    public void testStatisticsTaskRecentInfo() throws Exception {
        scheduleJobSdkController.statisticsTaskRecentInfo(1L, 0, 1L, 0);
    }

    @Test
    public void testStopJobByConditionTotal() throws Exception {
        scheduleJobSdkController.stopJobByConditionTotal(new ScheduleJobKillJobVO());
    }

    @Test
    public void testQuerySqlText() throws Exception {
        scheduleJobSdkController.querySqlText(Arrays.asList("String"));
    }

    @Test
    public void testGetJobGetFillDataDetailInfo() throws Exception {
        scheduleJobSdkController.getJobGetFillDataDetailInfo(new FillDataParams());
    }

    @Test
    public void testGetRunSqlText() throws Exception {
        scheduleJobSdkController.getRunSqlText("jobId",1);

    }
}

class ScheduleJobSdkControllerMock {

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime,
                               Integer scheduleType) {
        return "";
    }

    @MockInvoke(targetClass = ParamService.class)
    public void convertGlobalToParamType(String info, BiConsumer<List<ConsoleParamBO>, List<ScheduleTaskParamShade>> convertConsumer) {

    }

        @MockInvoke(targetClass = ActionService.class)
    public void fillConsoleParam(Map<String, Object> actionParam, Long taskId, Integer appType, String jobId) {

    }

        @MockInvoke(targetClass = ScheduleJobService.class)
    public List<TaskExeInfoVo> statisticsTaskRecentInfoToVo( Long taskId,  Integer appType,  Long projectId,  Integer count,Integer type) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<RestartJobInfoVO> restartJobInfo(String jobKey, Long parentTaskId, Integer level) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<JobTopOrderVO> queryTopSizeRunTimeJob(ScheduleJobTopRunTimeVO topRunTimeVO) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJob> listByJobIds(List<String> jobIds) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobStruct.class)
    List<ScheduleJobVO> toVos(List<ScheduleJob> scheduleJobs) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<TaskExeInfoVo> statisticsTaskRecentInfoToVo(Long taskId, Integer appType, Long projectId, Integer count) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer stopJobByConditionTotal(ScheduleJobKillJobVO vo) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleSqlTextDTO> querySqlText(List<String> jobIds) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public JobStatusVo queryJobsStatusStatisticsToVo(QueryJobDTO vo) {
        return new JobStatusVo();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public PageResult<ScheduleFillDataJobDetailVO> getJobGetFillDataDetailInfo(String taskName, Long bizStartDay,
                                                                               Long bizEndDay, List<String> flowJobIdList,
                                                                               String fillJobName, Long dutyUserId,
                                                                               String searchType, Integer appType,
                                                                               Long projectId, Long dtuicTenantId,
                                                                               String execTimeSort, String execStartSort,
                                                                               String execEndSort, String cycSort,
                                                                               String businessDateSort, String retryNumSort,
                                                                               String taskType, String jobStatuses,
                                                                               Integer currentPage, Integer pageSize) throws Exception {
        return PageResult.EMPTY_PAGE_RESULT;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskId(1L);
        scheduleJob.setAppType(1);
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public boolean restartJob(RestartType restartType, List<String> jobIds, Long operateId) {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxBatchSize() {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    public String getExtInfoByTaskId(long taskId, Integer appType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(GlobalConst.sqlText, "select * from test");
        return jsonObject.toJSONString();
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime) {
        return sql;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    public String getRunSqlText(String jobId) {
        return "";
    }
}