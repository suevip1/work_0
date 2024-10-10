package com.dtstack.engine.master.impl;

import com.dtstack.engine.po.EnvironmentParamTemplate;
import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import com.dtstack.engine.dao.EnvironmentParamTemplateDao;
import com.dtstack.engine.master.mapstruct.EnvironmentParamTemplateStruct;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvironmentParamTemplateService {

    @Autowired
    private EnvironmentParamTemplateDao environmentParamTemplateDao;

    @Autowired
    private EnvironmentParamTemplateStruct environmentParamTemplateStruct;

    public EnvironmentParamTemplateVO getTaskEnvironmentParam(String version, Integer taskType, Integer appType) {
        List<EnvironmentParamTemplate> paramsByTaskAndVersion = environmentParamTemplateDao.getParamsByTaskAndVersion(version, taskType, appType);
        if (CollectionUtils.isNotEmpty(paramsByTaskAndVersion)) {
            //子产品的任务参数优先
            return environmentParamTemplateStruct.templateTOVO(paramsByTaskAndVersion.get(0));
        }
        return null;
    }
}
