package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.OperatorParam;
import com.dtstack.engine.api.vo.ScheduleJobOperateVO;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleJobOperatorMock;
import com.dtstack.engine.po.ScheduleJobOperate;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

@MockWith(ScheduleJobOperatorMock.class)
public class ScheduleJobOperateServiceTest {
    ScheduleJobOperateService scheduleJobOperateService = new ScheduleJobOperateService();

    @Test
    public void testAddScheduleJobOperate() throws Exception {
        Boolean result = scheduleJobOperateService.addScheduleJobOperate("jobId", 0, "operateContent", 1L);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testAddScheduleJobOperates() throws Exception {
        scheduleJobOperateService.addScheduleJobOperates(Collections.singletonList(new ScheduleJobOperate()));
    }

    @Test
    public void testPage() throws Exception {
        OperatorParam operatorParam = new OperatorParam();
        PageResult<List<ScheduleJobOperateVO>> result = scheduleJobOperateService.page(operatorParam);
        Assert.assertNotNull(result);
    }

    @Test
    public void testCount() throws Exception {
        Integer result = scheduleJobOperateService.count("jobId");
        Assert.assertEquals(Integer.valueOf(1), result);
    }
}