package com.dtstack.engine.master.config;

import com.alibaba.testable.core.tool.PrivateAccessor;
import org.junit.Test;

public class SwaggerConfigTest {

    private static final SwaggerConfig swaggerConfig = new SwaggerConfig();

    static {
        PrivateAccessor.set(swaggerConfig,"swaggerEnable",true);
    }
    @Test
    public void swaggerApi() {
//        Assert.notNull(swaggerConfig.swaggerApi());
    }
}