package com.dtstack.schedule.common.metric.prometheus.func;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class CommonFuncTest {

    private CommonFunc commonFunc = new CommonFunc("test");

    @Test
    public void checkParam() {
        commonFunc.checkParam();
    }

    @Test
    public void build() throws UnsupportedEncodingException {
        commonFunc.build("content");
    }

    @Test
    public void setFunctionName() {
        commonFunc.setFunctionName("function");
    }

    @Test
    public void getFunctionName() {
        commonFunc.getFunctionName();
    }

    @Test
    public void setByLabel() {
        commonFunc.setByLabel(Lists.newArrayList("label"));
    }

    @Test
    public void getByLabel() {
        commonFunc.getByLabel();
    }

    @Test
    public void setWithoutLabel() {
        commonFunc.setWithoutLabel(Lists.newArrayList("withoutlabel"));
    }

    @Test
    public void getWithoutLabel() {
        commonFunc.getWithoutLabel();
    }


    @Test
    public void testToString() {
        commonFunc.toString();
    }

    @Test
    public void dealLabelFilter() throws UnsupportedEncodingException {
        commonFunc.dealLabelFilter("query");
    }
}