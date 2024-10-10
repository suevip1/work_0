package com.dtstack.engine.master.utils;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskUtilsTest {

    @Test
    public void dissectJobKey() {
        TaskUtils.dissectJobKey("applicationId_123_sd");
    }

    @Test
    public void dissectJobKeys() {
        TaskUtils.dissectJobKeys(Lists.newArrayList("applicationId_123_sd"));

    }
}