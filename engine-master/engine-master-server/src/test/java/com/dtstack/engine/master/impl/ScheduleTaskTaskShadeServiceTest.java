package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleTaskTaskShadeServiceTestMock;
import com.google.common.collect.HashBasedTable;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

@MockWith(ScheduleTaskTaskShadeServiceTestMock.class)
public class ScheduleTaskTaskShadeServiceTest {

    ScheduleTaskTaskShadeService scheduleTaskTaskShadeService = new ScheduleTaskTaskShadeService();


    @Test
    public void testClearDataByTaskId() throws Exception {
        scheduleTaskTaskShadeService.clearDataByTaskId(1L, 0);
    }

    @Test
    public void testSaveTaskTaskList() throws Exception {
        ScheduleTaskTaskShade scheduleTaskTaskShade = new ScheduleTaskTaskShade();
        scheduleTaskTaskShade.setTaskId(1L);
        scheduleTaskTaskShade.setAppType(1);
        scheduleTaskTaskShade.setParentTaskId(200L);
        scheduleTaskTaskShade.setParentAppType(1);
        SaveTaskTaskVO result = scheduleTaskTaskShadeService.saveTaskTaskList(JSONObject.toJSONString(Lists.newArrayList(scheduleTaskTaskShade)), "commitId");
        Assert.assertNotNull(result);
    }

    @Test
    public void testListChildByTaskKeys() throws Exception {
        List<ScheduleTaskTaskShadeDTO> result = scheduleTaskTaskShadeService.listChildByTaskKeys(Lists.newArrayList("cronJob_test_hive_20220509000000"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testListGroupTask() throws Exception {
        ScheduleTaskVO result = scheduleTaskTaskShadeService.listGroupTask(20L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testDeleteTombstoneByTaskKeys() throws Exception {
        scheduleTaskTaskShadeService.deleteTombstoneByTaskKeys(Lists.newArrayList("test"));
    }

    @Test
    public void testGetAllParentTask() throws Exception {
        scheduleTaskTaskShadeService.getAllParentTask(1L, 0);
    }

    @Test
    public void testDisplayOffSpring() throws Exception {
        scheduleTaskTaskShadeService.displayOffSpring(1L, 1L, 1, 0, 1);
    }


    @Test
    public void testGetRefTask() throws Exception {
        scheduleTaskTaskShadeService.getRefTask(new HashSet<>(Collections.singletonList(1L)), 0, 0, 1L, 0,
                new ArrayList<>(), new HashMap<>(), HashBasedTable.create());
    }


    @Test
    public void testGetAllFlowSubTasks() throws Exception {
        com.dtstack.engine.master.vo.ScheduleTaskVO result = scheduleTaskTaskShadeService.getAllFlowSubTasks(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTaskOtherPlatformByProjectId() throws Exception {
        scheduleTaskTaskShadeService.getTaskOtherPlatformByProjectId(1L, 0, 0);
    }
}