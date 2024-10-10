package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface ProjectStruct {

    List<ProjectNameVO> toProjectNameVo(List<AuthProjectVO> authProjectVOS);

    @Mapping(source = "projectAlias", target = "name")
    ProjectNameVO toProjectNameVo(AuthProjectVO authProjectVOS);
}
