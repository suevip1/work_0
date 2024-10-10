package com.dtstack.engine.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RenderUtilTest {

    @Test
    public void renderTemplate() {
        Map<String, String> param = new HashMap<>();
        String now = String.valueOf(System.currentTimeMillis());
        param.put("cyctime",now);
        Assert.assertEquals(now,RenderUtil.renderTemplate("${cyctime}", param));
    }
}