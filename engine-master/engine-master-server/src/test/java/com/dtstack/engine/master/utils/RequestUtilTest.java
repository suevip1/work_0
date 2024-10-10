package com.dtstack.engine.master.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestUtilTest {

    @Test
    public void paramToMap() {
        RequestUtil.paramToMap("sdsd=sds;dsd=sds");
    }
}