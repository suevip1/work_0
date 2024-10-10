package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.EnvironmentParamTemplate;
import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import org.mapstruct.Mapper;


/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface EnvironmentParamTemplateStruct {

    EnvironmentParamTemplateVO templateTOVO(EnvironmentParamTemplate environmentParamTemplate);
}
