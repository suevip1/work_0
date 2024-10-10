package com.dtstack.schedule.common.metric.prometheus.func;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class IRateFuncTest {

    private IRateFunc func = new IRateFunc();

    @Test
    public void getRangeVector() {
        func.getRangeVector();
    }

    @Test
    public void setRangeVector() {
        func.setRangeVector("rangeVector");
    }

    @Test
    public void checkParam() {
        func.checkParam();
    }

    @Test
    public void build() throws UnsupportedEncodingException {
        func.setRangeVector("rafafa");
        func.build("content");
    }

    @Test
    public void testToString() {
        func.toString();
    }
}