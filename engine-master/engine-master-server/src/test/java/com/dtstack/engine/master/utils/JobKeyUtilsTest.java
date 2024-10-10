package com.dtstack.engine.master.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class JobKeyUtilsTest {

    @Test
    public void getJobTriggerTimeFromJobKey() {
        JobKeyUtils.getJobTriggerTimeFromJobKey("sdsdsd");
    }

    @Test
    public void getTaskShadeIdFromJobKey() {
        JobKeyUtils.getTaskShadeIdFromJobKey("sdsd_123_123");
    }
}