package com.dtstack.engine.master.model.cluster.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.system.Context;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StoragePart extends PartImpl {

    public StoragePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, null);
    }

    @Override
    public String getPluginName() {
        Component resourceComponent = tryGetResourceComponent();
        if (resourceComponent == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelConfig = context.getModelConfig(resourceType, resourceVersion);
        JSONObject storageModelConfig = resourceModelConfig.map(res -> res.getJSONObject(type.name())).orElseThrow(() ->
                new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(), resourceComponent.getComponentName(), type.name(), versionName))
        );
        return storageModelConfig.getString(type.name());
    }

    @Override
    public String getVersionValue() {
        //special hdfs same as yarn
        // todo 前端这里传值错误，最好修正掉
        if(StringUtils.isBlank(versionName) && EComponentType.HDFS.equals(type)){
            Component resourceComponent = componentScheduleGroup.get(EComponentScheduleType.RESOURCE).get(0);
            versionName = resourceComponent.getVersionName();
        }
        return context.getComponentModel(type).getVersionValue(versionName);
    }

    @Override
    public Long getExtraVersionParameters() {
        Component resourceComponent = componentScheduleGroup.get(EComponentScheduleType.RESOURCE).get(0);
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelExtraVersionParameters = context.getModelExtraVersionParameters(resourceType, resourceVersion);
        if (resourceModelExtraVersionParameters.isPresent()) {
            return resourceModelExtraVersionParameters.map(res -> res.getJSONObject(type.name())).orElse(new JSONObject()).getLong(type.name());
        }
        return null;
    }

    @Override
    public Long getSecurityParamTemplate() {
        // 先判断是否开启了数据安全
        if (!openSecurity()) {
            return null;
        }
        String versionValue;
        // hdfs on yarn 需要归集到大类
        if (type == EComponentType.HDFS) {
            Component resourceComponent = componentScheduleGroup.get(EComponentScheduleType.RESOURCE).get(0);
            versionName = resourceComponent.getVersionName();
            String suffix = context.getComponentModel(type).getVersionValue(versionName).substring(0, 1);
            // hdfs2 or hdfs3
            versionValue = super.hdfsPrefix + suffix;
        } else {
            versionValue = context.getComponentModel(type).getVersionValue(versionName);
        }
        return context.getSecurityParamTemplate(type, versionValue);
    }
}
