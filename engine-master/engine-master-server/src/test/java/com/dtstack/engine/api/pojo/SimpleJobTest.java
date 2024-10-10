package com.dtstack.engine.api.pojo;

import com.dtstack.engine.dto.SimpleJob;
import org.junit.Test;

public class SimpleJobTest {
    SimpleJob simpleJob = new SimpleJob();

    @Test
    public void testSetCycTime() throws Exception {
        simpleJob.setCycTime("cycTime");
    }
}