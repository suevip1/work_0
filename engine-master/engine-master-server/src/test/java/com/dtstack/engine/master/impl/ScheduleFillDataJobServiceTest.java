package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleFillDataMock;
import com.dtstack.engine.po.ScheduleFillDataJob;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

@MockWith(ScheduleFillDataMock.class)
public class ScheduleFillDataJobServiceTest {

    ScheduleFillDataJobService scheduleFillDataJobService = new ScheduleFillDataJobService();

//    @Test
//    public void testCheckExistsName() throws Exception {
//        boolean result = scheduleFillDataJobService.checkExistsName("jobName", 1L);
//        Assert.assertTrue(result);
//    }

    @Test
    public void testGetFillJobList() throws Exception {
        List<ScheduleFillDataJob> result = scheduleFillDataJobService.getFillJobList(Collections.singletonList("String"), 0L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testSaveData() throws Exception {
        ScheduleFillDataJob result = scheduleFillDataJobService.saveData("jobName", 1L, 1L, "runDay", "fromDay", "toDay", 1L, 0, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFillByFillIds() throws Exception {
        List<ScheduleFillDataJob> result = scheduleFillDataJobService.getFillByFillIds(Collections.singletonList(1L));
        Lists.newArrayList(result);
    }

    @Test
    public void testIncrementParallelNum() throws Exception {
        Integer result = scheduleFillDataJobService.incrementParallelNum(1L);
        Assert.assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testGetFillById() throws Exception {
        ScheduleFillDataJob result = scheduleFillDataJobService.getFillById(1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testDecrementParallelNum() throws Exception {
        Integer result = scheduleFillDataJobService.decrementParallelNum(1L);
        Assert.assertEquals(Integer.valueOf(1), result);
    }
}