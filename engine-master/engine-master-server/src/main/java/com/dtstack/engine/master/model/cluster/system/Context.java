package com.dtstack.engine.master.model.cluster.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.model.cluster.exception.InvalidComponentException;
import com.dtstack.engine.master.model.cluster.system.config.ComponentModel;
import com.dtstack.engine.master.model.cluster.system.config.ComponentModelExtraParameters;
import com.dtstack.engine.master.model.cluster.system.config.ComponentModelTypeConfig;
import com.dtstack.engine.master.model.cluster.system.config.SystemConfigMapperException;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 组件依赖以及组件模版上下文
 */
@Component
public class Context {

    private final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final ScheduleDictDao dictDao;

    /**
     * 组件模版pluginName - component_config 对应信息
     */
    private final Map<String, Long> baseTemplates;

    /**
     * 组件 - 组件config配置信息
     */
    private final Map<EComponentType, ComponentModel> componentConfigs;

    /**
     * 资源组件 - version-依赖资源组件typeName
     */
    private final Map<EComponentType, Map<String, ComponentModelTypeConfig>> componentModelTypeConfig;

    /**
     * 资源组件 - version-额外参数信息
     */
    private final Map<EComponentType, Map<String, ComponentModelExtraParameters>> componentModelExtraParams;

    /**
     * 组件和任务类型的关系 - {@link com.dtstack.engine.common.enums.ClientTypeEnum}
     */
    private final Map<String, Integer> taskTypeClientDice;

    /**
     * 组件 tip 缓存 - {componentTypeCode, {key, ScheduleDict}}
     */
    private final Map<Integer, Map<String, ScheduleDict>> tipDictCache;

    /**
     * 组件数据安全参数配置模板，配置方式 {version : templateId}
     * 举例：
     * hdfs 组件，{"hdfs2":-1005,"hdfs3":-1005}
     * @return templateId
     */
    private final Map<EComponentType, Map<String, Long>> securityParamTemplates;

    /**
     * 需要利用 sm2 算法加密的 key
     */
    private final List<String> sm2EncryptKeys;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    public Context(ScheduleDictDao dictDao) {
        this.dictDao = dictDao;
        this.baseTemplates = Collections.unmodifiableMap(initBaseTemplates());
        this.componentConfigs = Collections.unmodifiableMap(initComponentModels());
        this.componentModelTypeConfig = Collections.unmodifiableMap(initComponentModelTypeConfig());
        this.componentModelExtraParams = Collections.unmodifiableMap(initComponentModelExtraParams());
        this.taskTypeClientDice = Collections.unmodifiableMap(initTaskTypeClientDict());
        this.tipDictCache = Collections.unmodifiableMap(initTipDictCache());
        this.securityParamTemplates = Collections.unmodifiableMap(initSecurityParamTemplates());
        this.sm2EncryptKeys = Collections.unmodifiableList(initSm2EncryptKeys());
    }

    private Map<String, Integer> initTaskTypeClientDict() {
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.TASK_CLIENT_TYPE.type);
        Map<String, Integer> taskTypeClientDice = Maps.newHashMap();

        for (ScheduleDict dict : dicts) {
            try {
                taskTypeClientDice.put(dict.getDictName(),Integer.parseInt(dict.getDictValue()));
            } catch (Exception e) {
                LOGGER.error("init initTaskTypeClientDict error:{}", e.getMessage(), e);
            }
        }
        return taskTypeClientDice;
    }

    private Map<EComponentType, Map<String, ComponentModelTypeConfig>> initComponentModelTypeConfig() {
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.RESOURCE_MODEL_CONFIG.type);
        return parseComponentConfig(dicts, ComponentModelTypeConfig::new);
    }

    private Map<EComponentType, Map<String, ComponentModelExtraParameters>> initComponentModelExtraParams() {
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.EXTRA_VERSION_TEMPLATE.type);
        return parseComponentConfig(dicts, ComponentModelExtraParameters::new);
    }

    private <T> Map<EComponentType, Map<String, T>> parseComponentConfig(List<ScheduleDict> dicts, BiFunction<String, String, T> function) {
        return dicts.stream().collect(Collectors.groupingBy(scheduleDict -> Enum.valueOf(EComponentType.class, scheduleDict.getDependName()),
                Collectors.collectingAndThen(Collectors.toCollection(ArrayList<ScheduleDict>::new),
                        scheduleDicts -> scheduleDicts.stream().collect(Collectors.toMap(ScheduleDict::getDictName,
                                d -> function.apply(d.getDictName(), d.getDictValue()))))));
    }

    private Map<EComponentType, ComponentModel> initComponentModels() {
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.COMPONENT_MODEL.type);
        requiredNotEmptyName(dicts, DictType.COMPONENT_MODEL);
//        Map<EComponentType, ScheduleDict> typeMap = Maps.newHashMap();
        Map<EComponentType, ScheduleDict> typeMap = parseComponentTypes(dicts, DictType.COMPONENT_MODEL);
        return parseComponentModels(typeMap, DictType.COMPONENT_MODEL);
    }

    private Map<EComponentType, ScheduleDict> parseComponentTypes(
            List<ScheduleDict> dicts, DictType type) {
        try {
            return dicts.stream()
                    .collect(Collectors.toMap(
                            d -> EComponentType.valueOf(d.getDictName()),
                            Function.identity()
                    ));
        } catch (IllegalArgumentException e) {
            throw new SystemException(StringUtils.format(
                    "There are unknown component type name in dictionary of type {}",
                    type
            ), e);
        } catch (IllegalStateException e) {
            throw new SystemException(StringUtils.format(
                    "There are duplicate name in dictionary of type {}",
                    type
            ), e);
        }
    }

    private Map<EComponentType, ComponentModel> parseComponentModels(
            Map<EComponentType, ScheduleDict> dicts, DictType type) {
        try {
            return dicts.keySet().stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            t -> new ComponentModel(t, dicts.get(t), this.dictDao)
                    ));
        } catch (SystemConfigMapperException e) {
            throw new SystemException(StringUtils.format(
                    "There are error component model config in dictionary of type {}.",
                    type
            ), e);
        }
    }


    private Map<String, Long> initBaseTemplates() {
        List<ScheduleDict> dicts = this.dictDao.listDictByType(DictType.TYPENAME_MAPPING.type);
        requiredNotEmptyName(dicts, DictType.TYPENAME_MAPPING);
        Map<String, Long> res = parseTemplates(dicts, DictType.TYPENAME_MAPPING);
        requiredNegativeValue(res, DictType.TYPENAME_MAPPING);
        return res;
    }

    private void requiredNotEmptyName(List<ScheduleDict> dicts, DictType type) {
        if (dicts.stream().anyMatch(scheduleDict -> StringUtils.isBlank(scheduleDict.getDictName()))) {
            throw new SystemException(StringUtils.format(
                    "There are empty name in dictionary of type {}",
                    type
            ));
        }
    }


    private Map<String, Long> parseTemplates(List<ScheduleDict> dicts, DictType type) {
        try {
            return dicts.stream().collect(Collectors.toMap(
                    ScheduleDict::getDictName,
                    d -> Long.parseLong(d.getDictValue())
            ));
        } catch (IllegalStateException e) {
            throw new SystemException(StringUtils.format(
                    "There are duplicate name in dictionary of type {}",
                    type
            ), e);
        } catch (NumberFormatException e) {
            throw new SystemException(StringUtils.format(
                    "There are error number format of value in dictionary of type {}",
                    type
            ), e);
        }
    }

    private void requiredNegativeValue(Map<String, Long> map, DictType type) {
        map.forEach((k, v) -> {
            if (v >= 0) {
                throw new SystemException(StringUtils.format(
                        "There are not negative value in dictionary of type {}",
                        type
                ));
            }
        });
    }

    public EComponentScheduleType getOwner(EComponentType type) {
        Objects.requireNonNull(type, "Type is null.");
        ComponentModel componentModel = getComponentModel(type);
        return componentModel.getOwner();
    }

    public List<EComponentScheduleType> getDependsOn(EComponentType type) {
        Objects.requireNonNull(type, "Type is null");
        return getComponentModel(type).getDependsOn();
    }

    public Optional<Long> getBaseTemplateId(String pluginName) {
        Objects.requireNonNull(pluginName, "pluginName is null.");
        return Optional.ofNullable(this.baseTemplates.get(pluginName));
    }


    public ComponentModel getComponentModel(EComponentType type) {
        ComponentModel config = this.componentConfigs.get(type);
        if (config == null) {
            throw new SystemException(StringUtils.format("Component model of type {} is not found.", type));
        }
        return config;
    }

    public Optional<JSONObject> getModelConfig(EComponentType type, String versionName) {
        Map<String, ComponentModelTypeConfig> resourceModelConfigMap = componentModelTypeConfig.get(type);
        if (MapUtils.isEmpty(resourceModelConfigMap)) {
            throw new InvalidComponentException(type, "is not config resource model");
        }
        ComponentModelTypeConfig resourceModelConfig = resourceModelConfigMap.get(versionName);
        if (null == resourceModelConfig || null == resourceModelConfig.getComponentModelConfig()) {
            throw new InvalidComponentException(type, String.format(" version %s is not support resource model", versionName));
        }
        return Optional.ofNullable(resourceModelConfig.getComponentModelConfig());
    }

    public Optional<JSONObject> getModelExtraVersionParameters(EComponentType resourceType, String resourceVersion) {
        Map<String, ComponentModelExtraParameters> componentModelExtraParametersMap = componentModelExtraParams.get(resourceType);
        if(null == componentModelExtraParametersMap){
            return Optional.empty();
        }
        ComponentModelExtraParameters parameters = componentModelExtraParametersMap.get(resourceVersion);
        if(null == parameters){
            return Optional.empty();
        }
        return Optional.ofNullable(parameters.getComponentModelConfig());
    }

    public ClientTypeEnum getClientType(String engineType) {
        return ClientTypeEnum.getClientTypeEnum(taskTypeClientDice.get(engineType.toLowerCase()));
    }

    public ClientTypeEnum getClientType(String engineType, Integer taskType) {
        if (EScheduleJobType.CONDITION_BRANCH.getType().equals(taskType) || EScheduleJobType.EVENT.getType().equals(taskType)) {
            return ClientTypeEnum.DAGSCHEDULEX_LOCAL;
        }
        return ClientTypeEnum.getClientTypeEnum(taskTypeClientDice.get(engineType.toLowerCase()));
    }

    private Map<Integer, Map<String, ScheduleDict>> initTipDictCache() {
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.TIPS.type);
        return dicts.stream().collect(Collectors.groupingBy(dict -> Integer.valueOf(dict.getDictDesc()),
                Collectors.toMap(ScheduleDict::getDictName, Function.identity(), (x, y) -> y)
        ));
    }

    /**
     * 填充组件 tip
     * componentConfig 模版会复用
     * @param componentConfigs
     */
    public void populateTip(List<ComponentConfig> componentConfigs,Integer componentType) {
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return;
        }
        for (ComponentConfig componentConfig : componentConfigs) {
            Map<String, ScheduleDict> key2Dict = this.tipDictCache.get(componentType);
            if (CollectionUtils.isEmpty(key2Dict)) {
                continue;
            }
            ScheduleDict dict = key2Dict.get(componentConfig.getKey());
            if (dict == null) {
                continue;
            }
            // 自定义参数无需 tip
            if (StringUtils.isEmpty(componentConfig.getType())
                    || EFrontType.CUSTOM_CONTROL.name().equalsIgnoreCase(componentConfig.getType())) {
                continue;
            }
            componentConfig.setDependName(dict.getDependName());
            componentConfig.setKeyDescribe(dict.getDictValue());
            componentConfig.setSort(dict.getSort());
        }
    }

    public Long getSecurityParamTemplate(EComponentType eComponentType, String versionValue) {
        Map<String, Long> versionName2TemplateId = this.securityParamTemplates.get(eComponentType);
        if (MapUtils.isEmpty(versionName2TemplateId)) {
            return null;
        }
        return versionName2TemplateId.get(versionValue);
    }

    private Map<EComponentType, Map<String, Long>> initSecurityParamTemplates() {
        List<ScheduleDict> dicts = this.dictDao.listDictByType(DictType.SECURITY_PARAM_TEMPLATE.type);
        requiredNotEmptyName(dicts, DictType.SECURITY_PARAM_TEMPLATE);
        Map<EComponentType, Map<String, Long>> res = new HashMap<>(dicts.size());

        for (ScheduleDict dict : dicts) {
            String componentName = dict.getDictName();
            Map versionName2TemplateId = JSON.parseObject(dict.getDictValue(), Map.class);
            Map<String, Long> map = new HashMap<>(versionName2TemplateId.size());
            versionName2TemplateId.forEach((k, v) -> {
                try {
                    map.put(String.valueOf(k), Long.parseLong(String.valueOf(v)));
                } catch (NumberFormatException e) {
                    LOGGER.warn("initSecurityParamTemplates error, k:{}", k, e);
                }
            });
            res.put(Enum.valueOf(EComponentType.class, componentName), Collections.unmodifiableMap(map));
        }
        return res;
    }

    private List<String> initSm2EncryptKeys() {
        List<ScheduleDict> dicts = this.dictDao.listDictByType(DictType.ENCRYPT_KEY.type);
        return dicts.stream().map(ScheduleDict::getDictName).collect(Collectors.toList());
    }

    public List<String> listSm2EncryptKeys() {
        return this.sm2EncryptKeys;
    }

    public Pair<String, String> sm2PrivateWithPublicKey() {
        return new ImmutablePair<>(environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey());
    }
}