package com.dtstack.engine.master.model.cluster.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.system.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StandaloneComputePart extends PartImpl {
    public StandaloneComputePart(EComponentType componentType, String versionName, EComponentType storageType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                                 Context context, DataSource dataSource, EDeployType deployType) {
        super(componentType, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
    }

    @Override
    public String getPluginName() {
        Optional<JSONObject> modelConfig = context.getModelConfig(type, versionName);
        return modelConfig.map(config -> config.getString(versionName)).orElse(null);
    }

    @Override
    public String getVersionValue() {
        return context.getComponentModel(type).getVersionValue(versionName);
    }

    @Override
    public Long getExtraVersionParameters() {
        Optional<JSONObject> modelConfig = context.getModelExtraVersionParameters(type, versionName);
        return modelConfig.map(config -> config.getLong(versionName)).orElse(null);
    }
}
