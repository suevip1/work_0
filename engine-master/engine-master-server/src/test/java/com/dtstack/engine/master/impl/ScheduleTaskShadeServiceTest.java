package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.FindTaskVO;
import com.dtstack.engine.api.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.engine.api.vo.task.TaskTypeVO;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.dto.TaskName;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleTaskShadeServiceMock;
import com.dtstack.schedule.common.enums.EParamType;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MockWith(ScheduleTaskShadeServiceMock.class)
@EnablePrivateAccess
public class ScheduleTaskShadeServiceTest {
    ScheduleTaskShadeService scheduleTaskShadeService = new ScheduleTaskShadeService();

    @Test
    public void testAddOrUpdate() {
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        scheduleTaskShadeDTO.setTaskId(-1L);
        scheduleTaskShadeDTO.setAppType(1);
        scheduleTaskShadeDTO.setDtuicTenantId(1L);
        scheduleTaskShadeDTO.setComponentVersion("2.1");
        scheduleTaskShadeDTO.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);

        scheduleTaskShadeDTO.setTaskId(100L);
        scheduleTaskShadeDTO.setAppType(1);
        scheduleTaskShadeDTO.setDtuicTenantId(1L);
        scheduleTaskShadeDTO.setCalenderId(1L);
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
    }

    @Test
    public void testDeleteTask() {
        scheduleTaskShadeService.deleteTask(1L, 0L, 1, EScheduleJobType.GROUP.getType());
    }

    @Test
    public void testGetNotDeleteTask() {
        List<NotDeleteTaskVO> result = scheduleTaskShadeService.getNotDeleteTask(1L, 0);
        Assert.assertNotNull(result);
    }


    @Test
    public void testCountTaskByType() {
        ScheduleTaskShadeCountTaskVO result = scheduleTaskShadeService.countTaskByType(1L, 1L, 1L, 0, Collections.singletonList(0));
        Assert.assertNotNull(result);
    }

    @Test
    public void testCountTaskByTypes() {
        List<ScheduleTaskShadeCountTaskVO> result = scheduleTaskShadeService.countTaskByTypes(1L, 1L, Collections.<Long>singletonList(1L), 0, Arrays.<Integer>asList(0));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetByIds() {
        Map<Long, ScheduleTaskShade> result = scheduleTaskShadeService.getByIds(Collections.<Long>singletonList(1L));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetNamesByIds() {

        Map<Long, TaskName> result = scheduleTaskShadeService.getNamesByIds(Collections.<Long>singletonList(1L));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTaskByIds() {
        List<ScheduleTaskShade> result = scheduleTaskShadeService.getTaskByIds(Collections.singletonList(1L), 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetSimpleTaskRangeAllByIds() {
        List<ScheduleTaskShade> result = scheduleTaskShadeService.getSimpleTaskRangeAllByIds(Collections.<Long>singletonList(1L), 0);
        Assert.assertNotNull(result);
    }


    @Test
    public void testPageQuery() {
        PageResult<List<ScheduleTaskShadeVO>> result = scheduleTaskShadeService.pageQuery(new ScheduleTaskShadeDTO());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetBatchTaskById() {
        ScheduleTaskShade result = scheduleTaskShadeService.getBatchTaskById(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryTasksByCondition() {
        FindTaskVO findTaskVO = new FindTaskVO();
        findTaskVO.setName("test");
        ScheduleTaskShadePageVO result = scheduleTaskShadeService.queryTasksByCondition(findTaskVO);
        Assert.assertNotNull(result);
    }

    @Test
    public void testQueryTasks() {
        ScheduleTaskShadePageVO result = scheduleTaskShadeService.queryTasks(1L, 1L, 1L, "name", 1L,
                1L, 1L, 0, "1", "1", 0, 0, "searchType", 0, Collections.singletonList(1L));
        Assert.assertNotNull(result);
    }


    @Test
    public void testDealFlowWorkTask() {
        ScheduleTaskVO result = scheduleTaskShadeService.dealFlowWorkTask(-1L, 0, Collections.singletonList(0), 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFlowWorkSubTasks() {
        List<ScheduleTaskShade> result = scheduleTaskShadeService.getFlowWorkSubTasks(1L, 0, Collections.singletonList(0), 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindTaskId() {
        ScheduleTaskShade result = scheduleTaskShadeService.findTaskId(1L, 0, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindTaskIds() {
        List<ScheduleTaskShade> result = scheduleTaskShadeService.findTaskIds(Collections.<Long>singletonList(1L), 0, 0, true);
        Assert.assertNotNull(result);
    }

    @Test
    public void testInfo() {
        JSONObject jsonObject = new JSONObject();
        ScheduleTaskParamShade scheduleTaskParamShade = new ScheduleTaskParamShade();
        scheduleTaskParamShade.setTaskId(1L);
        scheduleTaskParamShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskParamShade.setType(EParamType.INPUT.getType());
        scheduleTaskParamShade.setParamCommand("test");
        scheduleTaskParamShade.setParamName("test");
        scheduleTaskParamShade.setFatherParamName("test");
        scheduleTaskParamShade.setFatherTaskId(-1L);
        jsonObject.put("taskParamsToReplace", Lists.newArrayList(scheduleTaskParamShade));
        scheduleTaskShadeService.info(1L, 1, jsonObject.toString());
    }


    @Test
    public void testCheckResourceLimit() {
        String sparkParam = "## Driver程序使用的CPU核数,默认为1\n" +
                "# driver.cores=1\n" +
                "\n" +
                "## Driver程序使用内存大小,默认512m\n" +
                "# driver.memory=512m\n" +
                "\n" +
                "## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n" +
                "## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n" +
                "# driver.maxResultSize=1g\n" +
                "\n" +
                "## 启动的executor的数量，默认为1\n" +
                "executor.instances=1\n" +
                "\n" +
                "## 每个executor使用的CPU核数，默认为1\n" +
                "executor.cores=1\n" +
                "\n" +
                "## 每个executor内存大小,默认512m\n" +
                "executor.memory=512m\n" +
                "\n" +
                "## 任务优先级, 值越小，优先级越高，范围:1-1000\n" +
                "job.priority=10\n" +
                "\n" +
                "## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n" +
                "# logLevel = INFO\n" +
                "\n" +
                "## spark中所有网络交互的最大超时时间\n" +
                "# spark.network.timeout=120s\n" +
                "\n" +
                "## executor的OffHeap内存，和spark.executor.memory配置使用\n" +
                "# spark.yarn.executor.memoryOverhead";
        List<String> result = scheduleTaskShadeService.checkResourceLimit(1L, EScheduleJobType.SPARK_SQL.getType(), sparkParam, 1L);
        Assert.assertNotNull(result);
        String shell = "\n" +
                "## 每个worker所占内存，比如512m\n" +
                "worker.memory=512m\n" +
                "\n" +
                "## 每个worker所占的cpu核的数量\n" +
                "worker.cores=1\n" +
                "\n" +
                "## 是否独占机器节点 \n" +
                "exclusive=false\n" +
                "\n" +
                "## worker数量 \n" +
                "worker.num=1\n" +
                "\n" +
                "## 任务优先级, 值越小，优先级越高，范围:1-1000\n" +
                "job.priority=10";
        result = scheduleTaskShadeService.checkResourceLimit(1L, EScheduleJobType.SHELL.getType(), shell, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testAddOrUpdateBatchTask() {
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        scheduleTaskShadeDTO.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
        scheduleTaskShadeService.addOrUpdateBatchTask(Collections.singletonList(scheduleTaskShadeDTO), "commitId");
    }

    @Test
    public void testInfoCommit() {
        scheduleTaskShadeService.infoCommit(1L, 0, "info", "commitId");
    }

    @Test
    public void testTaskCommit() {
        scheduleTaskShadeService.taskCommit("commitId");
    }

    @Test
    public void testFindFuzzyTaskNameByCondition() {
        List<ScheduleTaskShadeTypeVO> result = scheduleTaskShadeService.findFuzzyTaskNameByCondition("name", "name",0, 1L, Collections.singletonList(1L), 0, 0);
        Assert.assertNotNull(result);
    }


    @Test
    public void testFindTaskRuleTask() {
        ScheduleDetailsVO result = scheduleTaskShadeService.findTaskRuleTask(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindChildTaskRuleByTaskId() {
        List<ScheduleTaskShade> result = scheduleTaskShadeService.findChildTaskRuleByTaskId(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testListTaskShadeByIdAndType() {

        Map<Integer, List<ScheduleTaskShade>> result = scheduleTaskShadeService.listTaskShadeByIdAndType(new HashMap<Integer, Set<Long>>() {{
            put(0, new HashSet<>(Collections.singletonList(1L)));
        }});
        Assert.assertNotNull(result);
    }

    @Test
    public void testCheckCronExpression() {
        scheduleTaskShadeService.checkCronExpression("0 10 13 L 3,6,9,12 ?", 1L);
    }

    @Test
    public void testRecentlyRunTime() {
        List<String> result = scheduleTaskShadeService.recentlyRunTime("2022-05-24", "2022-05-25", "0 10 13 L 3,6,9,12 ?", 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTaskType() {
        List<TaskTypeVO> result = scheduleTaskShadeService.getTaskType();
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindTaskKeyByProjectId() {
        List<ScheduleTaskShadeDTO> result = scheduleTaskShadeService.findTaskKeyByProjectId(1L, 1,Lists.newArrayList(1),Lists.newArrayList(),1);
        Assert.assertNotNull(result);
    }


    @Test
    public void testFindTask() {
        FindTaskVO findTaskVO = new FindTaskVO();
        findTaskVO.setAppType(1);
        findTaskVO.setName("test");
        findTaskVO.setLimit(1);
        List<ScheduleTaskShadeTypeVO> result = scheduleTaskShadeService.findTask(findTaskVO);
        Assert.assertNotNull(result);
    }



    @Test
    public void testGetTaskNameByJobKeys() {
        List<String> result = scheduleTaskShadeService.getTaskNameByJobKeys(Collections.singletonList("cronTrigger_33821_20220226105000"));
        Assert.assertNotNull(result);
    }


    @Test
    public void testOffline() {
        OfflineReturnVO result = scheduleTaskShadeService.offline(Collections.singletonList(1L), 0);
        Assert.assertNotNull(result);
    }
}