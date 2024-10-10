package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import com.dtstack.engine.master.impl.EnvironmentParamTemplateService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@MockWith(EnvironmentParamSdkControllerMock.class)
public class EnvironmentParamSdkControllerTest {
    EnvironmentParamSdkController environmentParamSdkController = new EnvironmentParamSdkController();

    @Test
    public void testGetTaskEnvironmentParam() throws Exception {
        environmentParamSdkController.getTaskEnvironmentParam("version", 0, 0);
    }
}

class EnvironmentParamSdkControllerMock {
    @MockInvoke(targetClass = EnvironmentParamTemplateService.class)
    public EnvironmentParamTemplateVO getTaskEnvironmentParam(String version, Integer taskType, Integer appType) {
        return new EnvironmentParamTemplateVO();
    }
}