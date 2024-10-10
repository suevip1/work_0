package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import com.dtstack.engine.dao.EnvironmentParamTemplateDao;
import com.dtstack.engine.master.mapstruct.EnvironmentParamTemplateStruct;
import com.dtstack.engine.po.EnvironmentParamTemplate;
import org.assertj.core.util.Lists;

import java.util.List;

public class EnvironmentParamTemplateMock {
    @MockInvoke(targetClass = EnvironmentParamTemplateDao.class)
    List<EnvironmentParamTemplate> getParamsByTaskAndVersion(String taskVersion, int taskType, int appType) {
        EnvironmentParamTemplate environmentParamTemplate = new EnvironmentParamTemplate();
        environmentParamTemplate.setParams("test");
        return Lists.newArrayList(environmentParamTemplate);
    }

    @MockInvoke(targetClass = EnvironmentParamTemplateStruct.class)
    EnvironmentParamTemplateVO templateTOVO(EnvironmentParamTemplate environmentParamTemplate) {
        EnvironmentParamTemplateVO environmentParamTemplateVO = new EnvironmentParamTemplateVO();
        environmentParamTemplateVO.setParams(environmentParamTemplate.getParams());
        return environmentParamTemplateVO;
    }
}
