package com.dtstack.engine.master.utils;

import org.junit.Test;

import java.time.Duration;

public class TimeUtilsTest {

    @Test
    public void parseDuration() {
        TimeUtils.parseDuration("123ms");
    }

    @Test
    public void getStringInMillis() {
    }

    @Test
    public void formatWithHighestUnit() {
        TimeUtils.formatWithHighestUnit(Duration.ofMillis(60000));
    }
}