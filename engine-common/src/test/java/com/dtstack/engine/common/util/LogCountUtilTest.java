package com.dtstack.engine.common.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogCountUtilTest {

    @Test
    public void count() {
        Assert.assertTrue(LogCountUtil.count(10, 2));
    }
}