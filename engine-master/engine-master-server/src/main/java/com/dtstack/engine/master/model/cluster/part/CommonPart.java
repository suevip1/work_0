package com.dtstack.engine.master.model.cluster.part;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.model.cluster.system.config.ComponentModel;

import java.util.List;
import java.util.Map;

public class CommonPart extends PartImpl {

    public CommonPart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                      Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource,null);
    }

    @Override
    public String getVersionValue() {
        return "";
    }

    @Override
    public String getPluginName() {
        ComponentModel componentModel = context.getComponentModel(type);
        return componentModel.getNameTemplate();
    }
}
