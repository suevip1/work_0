package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.dto.ApplicationInfo;
import com.dtstack.engine.api.vo.action.ApplicationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author yuebai
 * @date 2021-11-12
 */
@Mapper(componentModel = "spring")
public interface ActionStruct {

    @Mapping(source = "jobId", target = "jobId")
    ApplicationVO toApplicationVO(ApplicationInfo applicationInfo,String jobId);
}
