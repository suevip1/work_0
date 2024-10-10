package com.dtstack.engine.master.model.cluster.part;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.model.cluster.system.config.ComponentModel;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 计算组件(依赖的资源调度组件不带版本，如 k8s)
 * @author yuebai
 * @version 1.0
 * @date 2022-01-04
 */
public class DependComputePart extends PartImpl {

    public DependComputePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                             Context context, DataSource dataSource, EDeployType deployType) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, deployType);
    }

    @Override
    public String getPluginName() {
        validDeployType(deployType);
        if (null == storageType) {
            throw new RdosDefineException(ErrorCode.STORE_COMPONENT_NOT_CONFIG);
        }
        Component resourceComponent = tryGetResourceComponent();
        if (resourceComponent == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelConfig = context.getModelConfig(resourceType, resourceVersion);
        if (!resourceModelConfig.isPresent()) {
            throw new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(), resourceType, type, versionName));
        }
        //唯一的pluginName
        return getValueInConfigWithResourceStore(resourceModelConfig.get(), resourceComponent, this::getPluginNameInModelOrByConfigVersion,true);
    }

    @Override
    public Long getExtraVersionParameters() {
        Component resourceComponent = tryGetResourceComponent();
        if (resourceComponent == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelExtraConfig = context.getModelExtraVersionParameters(resourceType, resourceVersion);
        String extraTemplateId = null;
        if (resourceModelExtraConfig.isPresent()) {
            // 注意 getValueInConfigWithResourceStore 被子类覆写了
            extraTemplateId = getValueInConfigWithResourceStore(resourceModelExtraConfig.get(), resourceComponent, null,false);
            if (StringUtils.isNotBlank(extraTemplateId)) {
                return Long.parseLong(extraTemplateId);
            }
        }
        //依赖resource 但是不依赖resource的类型拼接额外参数信息 如hive2
        extraTemplateId = context.getModelExtraVersionParameters(type, versionName).map((extraConfig) -> extraConfig.getString(versionName)).orElse(null);
        if (StringUtils.isNotBlank(extraTemplateId)) {
            return Long.parseLong(extraTemplateId);
        }
        return null;
    }


    private String getValueInConfigWithResourceStore(JSONObject resourceConfig, Component resourceComponent, Supplier<String> specialSupplier,Boolean isThrowException) {
        JSONObject storageConfig = resourceConfig.getJSONObject(storageType.name());
        if (storageConfig == null) {
            throw new RdosDefineException(ErrorCode.STORE_COMPONENT_CONFIG_NULL);
        }
        if (storageConfig.containsKey(type.name())) {
            //model config 已经定义了pluginName
            if (storageConfig.get(type.name().toUpperCase()) instanceof List) {
                Optional<String> valueWithKey = getValueWithKey(storageConfig.getJSONArray(type.name().toUpperCase()));

                return isThrowException? valueWithKey.orElseThrow(()->new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(),
                                resourceComponent.getComponentName(), type.name(), versionName))) : valueWithKey.orElse("");
            }
            return storageConfig.getString(type.name().toUpperCase());
        } else if (null != specialSupplier) {
            return specialSupplier.get();
        }
        return null;
    }

    protected Optional<String> getValueWithKey(JSONArray computeVersionModelConfig) {
        for (int i = 0; i < computeVersionModelConfig.size(); i++) {
            if (StringUtils.isNotBlank(computeVersionModelConfig.getJSONObject(i).getString(versionName))) {
                return Optional.ofNullable(computeVersionModelConfig.getJSONObject(i).getString(versionName));
            }
        }
        return Optional.empty();
    }

    @Override
    public String getVersionValue() {
        if (StringUtils.isBlank(versionName)) {
            return Strings.EMPTY_STRING;
        }
        ComponentModel componentModel = context.getComponentModel(type);
        if (null != componentModel) {
            return componentModel.getVersionValue(versionName);
        }
        return Strings.EMPTY_STRING;
    }

}
