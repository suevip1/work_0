package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.CalenderTimeMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

@MockWith(CalenderTimeMock.class)
public class CalenderTimeServiceTest {

    CalenderTimeService calenderTimeService = new CalenderTimeService();


    @Test
    public void testListBetweenTime() throws Exception {
        List<String> result = calenderTimeService.listBetweenTime(1L, "0", "202207021200000", "202207051200000",0);
        Assert.assertEquals(Collections.singletonList("20220702120000"), result);
    }

    @Test
    public void testGetNearestTime() throws Exception {
        String result = calenderTimeService.getNearestTime(1L, "0", "202207021200000", 0,true);
        Assert.assertEquals("20220702120000", result);
    }
}
