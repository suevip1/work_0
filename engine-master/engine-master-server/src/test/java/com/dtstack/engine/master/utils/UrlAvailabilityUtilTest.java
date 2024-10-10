package com.dtstack.engine.master.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UrlAvailabilityUtilTest {

    @Test
    public void canConnect() {
        UrlAvailabilityUtil.canConnect("www.baidu.com");
    }
}