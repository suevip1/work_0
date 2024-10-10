package com.dtstack.engine.master.model.cluster.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.Part;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.utils.SM2Util;
import com.google.common.collect.Lists;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PartImpl implements Part {

    protected Context context;
    protected DataSource dataSource;

    protected EComponentType type;
    protected String versionName;
    protected EComponentType storageType;
    protected EDeployType deployType;
    protected Map<EComponentScheduleType, List<Component>> componentScheduleGroup;
    protected PartImpl part = null;

    private List<EComponentType> NO_VERSION_NAME_RESOURCE_COMPONENTS = Lists.newArrayList(EComponentType.KUBERNETES);
    protected String hdfsPrefix = "hdfs";

    public PartImpl(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType,
            List<Component>> componentScheduleGroup, Context context, DataSource dataSource, EDeployType deployType) {
        this.type = componentType;
        this.versionName = versionName;
        this.storageType = storeType;
        this.context = context;
        this.dataSource = dataSource;
        this.componentScheduleGroup = componentScheduleGroup;
        this.deployType = deployType;
    }

    @Override
    public EComponentType getType() {
        return type;
    }

    @Override
    public String getVersionValue() {
        if (part == null) {
            initPartImpl();
        }
        return part.getVersionValue();
    }

    @Override
    public List<ComponentConfig> loadTemplate() {
        validDependOn();
        String pluginName = getPluginName();
        if (StringUtils.isBlank(pluginName)) {
            throw new RdosDefineException(Strings.format(ErrorCode.NOT_SUPPORT_COMPONENT.getMsg(), type.name(), versionName));
        }
        Long extraVersionParameters = getExtraVersionParameters();
        Optional<Long> baseTemplateOption = context.getBaseTemplateId(pluginName);
        List<ComponentConfig> baseConfig = new ArrayList<>();
        if (baseTemplateOption.isPresent()) {
            Long baseTemplate = baseTemplateOption.get();
            baseConfig = dataSource.listComponentConfig(Lists.newArrayList(baseTemplate), true);
            context.populateTip(baseConfig,type.getTypeCode());
            //适配参数 优先级高于 模版参数
            if (extraVersionParameters != null) {
                List<ComponentConfig> extraConfig = dataSource.listComponentConfig(Lists.newArrayList(extraVersionParameters), false);
                context.populateTip(extraConfig,type.getTypeCode());
                if (CollectionUtils.isNotEmpty(extraConfig)) {
                    Map<String, ComponentConfig> baseConfigMap = baseConfig.stream()
                            .collect(Collectors.toMap(c -> c.getKey() + "" + c.getDependencyKey() + "" + c.getCascadeKey() + "" + c.getCascadeValue(), Function.identity()));
                    Map<String, ComponentConfig> extraConfigMap = extraConfig.stream()
                            .collect(Collectors.toMap(c -> c.getKey() + "" + c.getDependencyKey() + "" + c.getCascadeKey() + "" + c.getCascadeValue(), Function.identity()));
                    baseConfigMap.putAll(extraConfigMap);
                    baseConfig = new ArrayList<>(baseConfigMap.values());
                }
            }
        }
        // 加载数据安全配置
        Long securityParamTemplate = part.getSecurityParamTemplate();
        if (securityParamTemplate != null) {
            List<ComponentConfig> securityConfigs = dataSource.listComponentConfig(Lists.newArrayList(securityParamTemplate), false).stream()
                    .peek(componentConfig -> {
                        if (checkDecryptKey(componentConfig)) {
                            //加密
                            componentConfig.setValue(SM2Util.encrypt(componentConfig.getValue(), context.sm2PrivateWithPublicKey().getLeft(), context.sm2PrivateWithPublicKey().getRight()));
                        }
                    }).collect(Collectors.toList());
            baseConfig.addAll(securityConfigs);
        }
        return baseConfig;
    }

    protected void validDependOn() {
        List<EComponentScheduleType> dependsOn = context.getDependsOn(type);
        if (CollectionUtils.isNotEmpty(dependsOn) && !EDeployType.STANDALONE.equals(deployType)) {
            for (EComponentScheduleType componentScheduleType : dependsOn) {
                if (!componentScheduleGroup.containsKey(componentScheduleType)) {
                    throw new RdosDefineException(ErrorCode.DEPEND_ON_COMPONENT_NOT_CONFIG);
                }
            }
        }
    }


    protected void validDeployType(EDeployType deployType) {
        if (EDeployType.YARN.equals(deployType) && !EComponentType.YARN.equals(getResourceType())) {
            throw new RdosDefineException(Strings.format(ErrorCode.RESOURCE_COMPONENT_NOT_SUPPORT_DEPLOY_TYPE.getMsg(), type, deployType));
        }
        if (EDeployType.KUBERNETES.equals(deployType) && !EComponentType.KUBERNETES.equals(getResourceType())) {
            throw new RdosDefineException(Strings.format(ErrorCode.RESOURCE_COMPONENT_NOT_SUPPORT_DEPLOY_TYPE.getMsg(), type, deployType));
        }
    }

    @Override
    public String getPluginName() {
        if (part == null) {
            initPartImpl();
        }
        return part.getPluginName();
    }

    @Override
    public boolean openSecurity() {
        List<Component> commonComponents = componentScheduleGroup.get(EComponentScheduleType.COMMON);
        if (CollectionUtils.isEmpty(commonComponents)) {
            return false;
        }
        List<Integer> commonComponentTypeCodes = commonComponents.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        return commonComponentTypeCodes.containsAll(Lists.newArrayList(EComponentType.LDAP.getTypeCode(), EComponentType.RANGER.getTypeCode()));
    }

    @Override
    public Long getSecurityParamTemplate() {
        if (!openSecurity()) {
            return null;
        }
        return context.getSecurityParamTemplate(type, getVersionValue());
    }

    private void initPartImpl() {
        EComponentScheduleType owner = context.getOwner(type);
        Component resourceComponent = null;
        boolean dependOnNoVersionName;
        switch (owner) {
            case COMMON:
                part = new CommonPart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                break;
            case RESOURCE:
                part = new ResourcePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                break;
            case STORAGE:
                // 区分出存储组件依赖了 k8s 的情况，进行特殊处理
                resourceComponent = this.tryGetResourceComponent();
                if (resourceComponent == null) {
                    throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
                }
                dependOnNoVersionName = NO_VERSION_NAME_RESOURCE_COMPONENTS.contains(EComponentType.getByCode(resourceComponent.getComponentTypeCode()));
                if (dependOnNoVersionName) {
                    part = new DependNoVersionStoragePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                } else {
                    part = new StoragePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                }
                break;
            case COMPUTE:
                if (EDeployType.STANDALONE.equals(deployType)) {
                    part = new StandaloneComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
                } else {
                    List<EComponentScheduleType> dependsOn = context.getDependsOn(type);
                    if (CollectionUtils.isNotEmpty(dependsOn)) {
                        // 区分出计算组件依赖了 k8s 的情况，进行特殊处理
                        resourceComponent = this.tryGetResourceComponent();
                        if (resourceComponent == null) {
                            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
                        }
                        dependOnNoVersionName = NO_VERSION_NAME_RESOURCE_COMPONENTS.contains(EComponentType.getByCode(resourceComponent.getComponentTypeCode()));
                        if (dependOnNoVersionName) {
                            part = new DependNoVersionComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
                        } else {
                            part = new DependComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
                        }
                    } else {
                        part = new SingleComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                    }
                }
                break;
            default:
                throw new RdosDefineException(ErrorCode.COMPONENT_INVALID);
        }
    }

    @Override
    public EComponentType getResourceType() {
        List<Component> components = componentScheduleGroup.get(EComponentScheduleType.RESOURCE);
        if (CollectionUtils.isEmpty(components)) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        Integer componentTypeCode = components.get(0).getComponentTypeCode();
        return EComponentType.getByCode(componentTypeCode);
    }

    @Override
    public Long getExtraVersionParameters() {
        return part == null ? null : part.getExtraVersionParameters();
    }

    /**
     * model 里面获取nameTemplate
     * 获取不到 在根据版本在modelConfig里面根据版本获取
     *
     * @return
     */
    protected String getPluginNameInModelOrByConfigVersion() {
        String nameTemplate = context.getComponentModel(type).getNameTemplate();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(nameTemplate)) {
            return nameTemplate;
        }
        //依赖resource 但是不依赖resource的类型拼接pluginName 如hive2
        Optional<JSONObject> modelConfig = context.getModelConfig(type, versionName);
        return modelConfig.map(model -> model.getString(versionName)).orElseThrow(() ->
                new RdosDefineException(Strings.format(ErrorCode.COMPONENT_CONFIG_NOT_SUPPORT_VERSION.getMsg(), type.name(), versionName))
        );
    }

    /**
     * 获取当前配置的资源调度组件
     *
     * @return
     */
    protected Component tryGetResourceComponent() {
        List<Component> components = componentScheduleGroup.get(EComponentScheduleType.RESOURCE);
        if (CollectionUtils.isEmpty(components)) {
            return null;
        }
        if (components.size() > 1) {
            throw new RdosDefineException(ErrorCode.COMPONENT_COUNT_ERROR);
        }
        return components.get(0);
    }

    /**
     * 获取当前配置的符合入参的存储组件
     * @return
     */
    protected Component tryGetStorageComponent(Integer storageType) {
        List<Component> components = componentScheduleGroup.get(EComponentScheduleType.STORAGE);
        if (CollectionUtils.isEmpty(components)) {
            return null;
        }
        for (Component component : components) {
            if (component.getComponentTypeCode().equals(storageType)) {
                return component;
            }
        }
        return null;
    }

    public boolean checkDecryptKey(ComponentConfig componentConfig){
        List<String> encryptKey = context.listSm2EncryptKeys();
        return encryptKey.contains(componentConfig.getKey()) && org.apache.commons.lang3.StringUtils.isBlank(componentConfig.getDependencyValue());
    }
}
