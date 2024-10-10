package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.dtcenter.common.enums.AppType;
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
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleJobBeanVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import com.dtstack.engine.api.vo.ScheduleServerLogVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillDataInfoVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobRuleTimeVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.dto.FillLimitationDTO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleJobServiceMock;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MockWith(ScheduleJobServiceMock.class)
@EnablePrivateAccess
public class ScheduleJobServiceTest {

    private ScheduleJobService scheduleJobService = new ScheduleJobService();


    @Test
    public void testGetJobById() {
        ScheduleJob result = scheduleJobService.getJobById(0L);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getEngineLog());
    }

    @Test
    public void testGetJobByJobKeyAndType() {
        ScheduleJob result = scheduleJobService.getJobByJobKeyAndType("jobKey", 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetStatusJobList() {
        PageResult result = scheduleJobService.getStatusJobList(1L, 1L, 0, 1L, 0, 0, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetStatusCount() {
        ScheduleJobStatusVO result = scheduleJobService.getStatusCount(1L, 1L, 0, 1L);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getAll());
        Assert.assertSame(result.getAll(), 100);
    }

    @Test
    public void testGetStatusCountByProjectIds() {
        List<ScheduleJobStatusVO> result = scheduleJobService.getStatusCountByProjectIds(Collections.singletonList(1L), 1L, 0, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testRunTimeTopOrder() {
        List<JobTopOrderVO> result = scheduleJobService.runTimeTopOrder(1L, 1L, 1L, 0, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryTopSizeRunTimeJob() {
        ScheduleJobTopRunTimeVO scheduleJobTopRunTimeVO = new ScheduleJobTopRunTimeVO();
        scheduleJobTopRunTimeVO.setStartTime(DateUtil.calTodayMills());
        scheduleJobTopRunTimeVO.setEndTime(DateUtil.TOMORROW_ZERO());

        List<JobTopOrderVO> result = scheduleJobService.queryTopSizeRunTimeJob(scheduleJobTopRunTimeVO);
        Assert.assertNotNull(result);
    }

    @Test
    public void testErrorTopOrder() {
        List<JobTopErrorVO> result = scheduleJobService.errorTopOrder(1L, 1L, 0, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetJobGraph() {
        ScheduleJobChartVO result = scheduleJobService.getJobGraph(1L, 1L, 0, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetScienceJobGraph() {
        ChartDataVO result = scheduleJobService.getScienceJobGraph(0L, 1L, "1");
        Assert.assertNotNull(result);
    }

    @Test
    public void testCountScienceJobStatus() {
        ScheduleJobScienceJobStatusVO result = scheduleJobService.countScienceJobStatus(Collections.singletonList(1L), 1L, 0, 0, "1", "cycStartTime", "cycEndTime");
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryJobs() {
        PageResult<List<ScheduleJobVO>> result = null;
        try {
            QueryJobDTO queryJobDTO = new QueryJobDTO();
            queryJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
            queryJobDTO.setProjectIds(Lists.newArrayList(1L, 2L));
            queryJobDTO.setJobStatuses("0,2");
            result = scheduleJobService.queryJobs(queryJobDTO);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTotalCount(), 1);
    }

    @Test
    public void testDisplayPeriods() {
        List<SchedulePeriodInfoVO> result = null;
        try {
            result = scheduleJobService.displayPeriods(true, 1L, 1L, 2);
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetRelatedJobs() {
        com.dtstack.engine.master.vo.ScheduleJobVO result = null;
        try {
            QueryJobDTO queryJobDTO = new QueryJobDTO();
            queryJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
            result = scheduleJobService.getRelatedJobs("workflow", JSONObject.toJSONString(queryJobDTO));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryJobsStatusStatistics() {
        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        queryJobDTO.setProjectIds(Lists.newArrayList(1L, 2L));
        queryJobDTO.setJobStatuses("0,2");
        Map<String, Long> result = scheduleJobService.queryJobsStatusStatistics(queryJobDTO);
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryJobsStatusStatisticsToVo() {
        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        queryJobDTO.setProjectIds(Lists.newArrayList(1L, 2L));
        queryJobDTO.setJobStatuses("0,2");
        JobStatusVo result = scheduleJobService.queryJobsStatusStatisticsToVo(queryJobDTO);
        Assert.assertNotNull(result);
    }

    @Test
    public void testJobDetail() {
        List<ScheduleRunDetailVO> result = scheduleJobService.jobDetail(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testUpdateStatusAndLogInfoAndExecTimeById() {
        Integer result = scheduleJobService.updateStatusAndLogInfoAndExecTimeById("jobId", 0, "msg", DateTime.now().toDate(), DateTime.now().toDate());
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testUpdateStatusAndLogInfoById() {
        Integer result = scheduleJobService.updateStatusAndLogInfoById("jobId", 0, "msg");
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testUpdateStatusByJobId() {
        Integer result = scheduleJobService.updateStatusByJobId("jobId", 0, 0);
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testStartJob() {
        try {
            //test fail
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobId("121");
            scheduleJobService.startJob(scheduleJob);
        } catch (Exception e) {
        }
    }

    @Test
    public void testUpdateStatusWithExecTime() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("1212");
        scheduleJob.setAppType(EScheduleType.NORMAL_SCHEDULE.getType());
        Integer result = scheduleJobService.updateStatusWithExecTime(scheduleJob);
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testTestTrigger() {
        scheduleJobService.testTrigger("jobId");
    }

    @Test
    public void testSendTaskStartTrigger() {
        try {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobId("1212");
            scheduleJob.setAppType(1);
            scheduleJob.setTaskId(100L);
            scheduleJobService.sendTaskStartTrigger(scheduleJob);
        } catch (Exception e) {

        }
    }

    @Test
    public void testStopJob() {
        try {
            scheduleJobService.stopJob(0L, 0, 1L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testStopJobByJobId() {
        try {
            scheduleJobService.stopJobByJobId("stop", 0, 1L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testStopFillDataJobs() {
        try {
            scheduleJobService.stopFillDataJobs("fillDataJobName", 1L, 1L, 0, 1L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBatchStopJobs() {
        int result = scheduleJobService.batchStopJobs(Collections.singletonList(1L), 1L);
        Assert.assertEquals(1, result);
    }

    @Test
    public void testInsertJobList() {
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
        scheduleJobService.insertJobList(Lists.newArrayList(scheduleBatchJob), EScheduleType.NORMAL_SCHEDULE.getType());
    }

    @Test
    public void testFillTaskData() {
        try {
            JSONObject task = new JSONObject();
            task.put("appType", 1);
            task.put("taskId", 100);
            JSONArray array = new JSONArray();
            array.add(task);
            scheduleJobService.fillTaskData(array.toJSONString(), "fillName", 1653235200L, 1653235200L, null, null,
                    1L, 1L, 1L, Boolean.TRUE, 0, 1L, Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void testGetFillDataJobInfoPreview() {
//        PageResult<List<ScheduleFillDataJobPreViewVO>> result = scheduleJobService.getFillDataJobInfoPreview("jobName", 1L, 1L, 1L, 1L, 1L,
//                0, 0, 0, 1L, 1L);
//        Assert.assertNotNull(result);
//    }

    @Test
    public void testGetFillDataDetailInfoOld() {
        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setType(EScheduleType.FILL_DATA.getType());
        queryJobDTO.setProjectIds(Lists.newArrayList(1L, 2L));
        queryJobDTO.setJobStatuses("0");
        PageResult<ScheduleFillDataJobDetailVO> result = null;
        try {
            result = scheduleJobService.getFillDataDetailInfoOld(queryJobDTO, "fillJobName", 1L);
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetJobGetFillDataDetailInfo() {
        try {
            PageResult<ScheduleFillDataJobDetailVO> result = scheduleJobService.getJobGetFillDataDetailInfo("taskName", 1L, 1L, null, "fillJobName", 1L,
                    "searchType", 0, 1L, 1L, "execTimeSort", "execStartSort", "execEndSort",
                    "cycSort", "businessDateSort", "retryNumSort", EScheduleJobType.WORK_FLOW.getType() + "", "0", 1, 10);
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetFillDataDetailInfo() {
        try {
            scheduleJobService.getFillDataDetailInfo("", null, "fillJobName", 1L, "searchType", 0);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetRelatedJobsForFillData() {
        try {
            QueryJobDTO queryJobDTO = new QueryJobDTO();
            queryJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
            ScheduleFillDataJobDetailVO.FillDataRecord result = scheduleJobService.getRelatedJobsForFillData("jobId", JSONObject.toJSONString(queryJobDTO), "fillJobName");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetRootRestartJob() {
        scheduleJobService.getRootRestartJob("cronTrigger_38981_20220226000000", true);
    }

    @Test
    public void testListByJobIds() {
        List<ScheduleJob> result = scheduleJobService.listByJobIds(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTaskShadeIdFromJobKey() {
        Long result = scheduleJobService.getTaskShadeIdFromJobKey("jobKey");
        Assert.assertEquals(Long.valueOf(-1L), result);
    }

    @Test
    public void testGetJobTriggerTimeFromJobKey() {
        String result = scheduleJobService.getJobTriggerTimeFromJobKey("");
        Assert.assertEquals("", result);
    }


    @Test
    public void testGetLabTaskRelationMap() {
        Map<String, ScheduleJob> result = scheduleJobService.getLabTaskRelationMap(Collections.singletonList("String"), 1L);
    }


    @Test
    public void testBatchJobsBatchUpdate() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("1212");
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        JSONArray array = new JSONArray();
        array.add(scheduleJob);
        scheduleJobService.BatchJobsBatchUpdate(array.toJSONString());
    }

    @Test
    public void testGetById() {
        ScheduleJob result = scheduleJobService.getById(1L);
        Assert.assertNotNull(result);
    }


    @Test
    public void testGetSameDayChildJob() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("12121");
        scheduleJob.setJobKey("cronTrigger_38981_20220226000000");
        List<ScheduleJob> result = scheduleJobService.getSameDayChildJob(JSONObject.toJSONString(scheduleJob), true);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGeneralCount() {
        scheduleJobService.generalCount(new ScheduleJobDTO());
    }


    @Test
    public void testGeneralQuery() {
        scheduleJobService.generalQuery(new PageQuery(1, 10, null, "sort"));
    }


    @Test
    public void testSetAlogrithmLabLog() {
        ScheduleServerLogVO scheduleServerLogVO = new ScheduleServerLogVO();
        scheduleServerLogVO.setTaskType(EScheduleJobType.ALGORITHM_LAB.getType());
        try {
            ScheduleServerLogVO result = scheduleJobService.setAlogrithmLabLog(RdosTaskStatus.FAILED.getStatus(), EScheduleJobType.ALGORITHM_LAB.getType(), "jobId", "{}", JSONObject.toJSONString(scheduleServerLogVO), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testMinOrHourJobQuery() {
        scheduleJobService.minOrHourJobQuery(new ScheduleJobDTO());
    }


//    @Test
//    public void testTestCheckCanRun() {
//        scheduleJobService.testCheckCanRun("jobId");
//    }

    @Test
    public void testCreateTodayTaskShade() {
        scheduleJobService.createTodayTaskShade(1L, 0, "date");
    }


    @Test
    public void testSyncBatchJob() {
        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setAppType(1);
        scheduleJobService.syncBatchJob(queryJobDTO);
    }

    @Test
    public void testUpdatePhaseStatusById() {

        boolean result = scheduleJobService.updatePhaseStatusById(Long.valueOf(1), JobPhaseStatus.CREATE, JobPhaseStatus.CREATE);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testGetListMinId() {
        Long result = scheduleJobService.getListMinId("left", "right");
        Assert.assertEquals(Long.valueOf(0), result);
    }

    @Test
    public void testGetJobGraphJSON() {
        scheduleJobService.getJobGraphJSON("jobId");
    }

    @Test
    public void testUpdateNotRuleResult() {
        scheduleJobService.updateNotRuleResult("noTask", 1, "result");
    }


    @Test
    public void testListJobByJobKeys() {
        List<ScheduleJob> result = scheduleJobService.listJobByJobKeys(Arrays.asList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetParentJobKeyMap() {
        Map<String, List<ScheduleJob>> result = scheduleJobService.getParentJobKeyMap(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testHandleTaskRule() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobKey("cronJob_47_20220226000000");
        scheduleJobService.handleTaskRule(scheduleJob, 0);
    }


    @Test
    public void testHasTaskRule() {
        boolean result = scheduleJobService.hasTaskRule(new ScheduleJob());
        Assert.assertFalse(result);
    }

    @Test
    public void testFindTaskRuleJobById() {
        List<ScheduleJobBeanVO> result = scheduleJobService.findTaskRuleJobById(1L);
        Assert.assertNotNull(result);
    }


    @Test
    public void testFindTaskRuleJob() {
        ScheduleDetailsVO result = scheduleJobService.findTaskRuleJob("jobId");
        Assert.assertNotNull(result);
    }

    @Test
    public void testStopJobByConditionTotal() {
        Integer result = scheduleJobService.stopJobByConditionTotal(new ScheduleJobKillJobVO());
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testStopJobByCondition() {
        Integer result = scheduleJobService.stopJobByCondition(new ScheduleJobKillJobVO());
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testGetJobsRuleTime() {
        ScheduleJobRuleTimeVO scheduleJobRuleTimeVO = new ScheduleJobRuleTimeVO();
        scheduleJobRuleTimeVO.setJobId("1");
        scheduleJobRuleTimeVO.setParamReplace(new ArrayList<>());
        List<ScheduleJobRuleTimeVO> result = scheduleJobService.getJobsRuleTime(Collections.singletonList(scheduleJobRuleTimeVO));
        Assert.assertNotNull(result);
    }

    @Test
    public void testRemoveOperatorRecord() {
        scheduleJobService.removeOperatorRecord(Collections.singletonList("String"),
                Collections.singletonList(new ScheduleJobOperatorRecord()));
    }


    @Test
    public void testGetJobExtraInfoOfValue() {
        String result = scheduleJobService.getJobExtraInfoOfValue("jobId", "key");
        Assert.assertEquals(result, "test");
    }

    @Test
    public void testRestartJobAndResume() {
        OperatorVO result = scheduleJobService.restartJobAndResume(Collections.singletonList(1L), Boolean.TRUE);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFillData() {
        ScheduleFillJobParticipateVO scheduleFillJobParticipateVO = new ScheduleFillJobParticipateVO();
        scheduleFillJobParticipateVO.setStartDay("2022-05-23");
        scheduleFillJobParticipateVO.setEndDay("2022-05-24");
        ScheduleFillDataInfoVO fillDataInfoVO = new ScheduleFillDataInfoVO();
        fillDataInfoVO.setFillDataType(FillDataTypeEnum.CONDITION_PROJECT_TASK.getType());
        scheduleFillJobParticipateVO.setFillDataInfo(fillDataInfoVO);
        scheduleFillJobParticipateVO.setFillName("test");
        try {
            Long result = scheduleJobService.fillData(scheduleFillJobParticipateVO, FillLimitationDTO.build());
        } catch (Exception e) {
        }
    }

//    @Test
//    public void testCreateFillJob() {
//        try {
//            scheduleJobService.createFillJob(new HashSet<>(Collections.singletonList("String")), new HashSet<String>(Collections.singletonList("String")),
//                    Collections.singletonList("String"), 1L, "fillName", null,
//                    null, "2022-05-23", "2022-05-24", 1L, 1L, 1L, 1L, Boolean.TRUE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail(e.getMessage());
//        }
//    }


    @Test
    public void testBatchRestartScheduleJob() {
        scheduleJobService.batchRestartScheduleJob(new HashMap<String, String>() {{
            put("4i5kei225gv2", "20220226000000");
        }});
    }

    @Test
    public void testRestartJobInfo() {
        List<RestartJobInfoVO> result = scheduleJobService.restartJobInfo("cronJob_47_20220226000000", 1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryStreamJobs() {
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(1, 2, null, null);
        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        scheduleJobDTO.setAppType(AppType.STREAM.getType());
        pageQuery.setModel(scheduleJobDTO);
        scheduleJobService.queryStreamJobs(pageQuery);
    }

    @Test
    public void testQuerySqlText() {
        List<ScheduleSqlTextDTO> result = scheduleJobService.querySqlText(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

}
