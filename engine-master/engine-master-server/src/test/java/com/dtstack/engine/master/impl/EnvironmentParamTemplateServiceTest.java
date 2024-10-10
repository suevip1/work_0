package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import com.dtstack.engine.master.mockcontainer.impl.EnvironmentParamTemplateMock;
import org.junit.Assert;
import org.junit.Test;

@MockWith(EnvironmentParamTemplateMock.class)
public class EnvironmentParamTemplateServiceTest {

    EnvironmentParamTemplateService environmentParamTemplateService = new EnvironmentParamTemplateService();


    @Test
    public void testGetTaskEnvironmentParam() throws Exception {
        EnvironmentParamTemplateVO result = environmentParamTemplateService.getTaskEnvironmentParam("1.12", 0, 1);
        Assert.assertNotNull(result);
    }
}