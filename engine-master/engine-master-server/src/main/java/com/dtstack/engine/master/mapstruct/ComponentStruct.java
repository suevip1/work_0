package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.vo.components.ClusterComponentVO;
import com.dtstack.engine.api.vo.components.ComponentVersionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-09
 */
@Mapper(componentModel = "spring")
public interface ComponentStruct {

    ClusterComponentVO toClusterComponentVO(Component component);

    @Mapping(source = "hadoopVersion", target = "version")
    @Mapping(source = "isDefault", target = "default")
    ComponentVersionVO toComponentVersion(Component component);

    List<ComponentVersionVO> toComponentVersions(List<Component> component);

}
