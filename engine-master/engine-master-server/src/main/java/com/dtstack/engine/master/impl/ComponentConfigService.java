package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.vo.ComponentMultiVersionVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.IComponentVO;
import com.dtstack.engine.common.constrant.UicConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.model.cluster.ClusterFactory;
import com.dtstack.engine.master.model.cluster.Part;
import com.dtstack.engine.master.model.cluster.PartCluster;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.utils.SM2Util;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.TYPE_NAME_KEY;

/**
 * @author yuebai
 * @date 2021-02-18
 */
@Service
public class ComponentConfigService {

    private final static Logger logger = LoggerFactory.getLogger(ComponentConfigService.class);

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ClusterFactory clusterFactory;

    private List<String> SYNC_TO_RANGER_HDFS_KEYS;
    @Value("${syncRangerHdfsKeys:}")
    private void setSyncRangerHdfsKeys(List<String> syncRangerHdfsKeys) {
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(syncRangerHdfsKeys)) {
            List<String> result = new ArrayList<>(syncRangerHdfsKeys);
            result.addAll(UicConstant.SYNC_RANGER_HDFS_KEYS);
            SYNC_TO_RANGER_HDFS_KEYS = Collections.unmodifiableList(result);
        } else {
            SYNC_TO_RANGER_HDFS_KEYS = UicConstant.SYNC_RANGER_HDFS_KEYS;
        }
    }

    @Autowired
    private Context context;

    /**
     * 保存页面展示数据
     *
     * @param clientTemplates
     * @param componentId
     * @param clusterId
     * @param componentTypeCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateComponentConfig(List<ClientTemplate> clientTemplates, Long componentId, Long clusterId, Integer componentTypeCode, Consumer<List<ComponentConfig>> configsPreprocessing) {
        if (null == clusterId || null == componentId || null == componentTypeCode || CollectionUtils.isEmpty(clientTemplates)) {
            throw new RdosDefineException("参数不能为空");
        }
        componentConfigDao.deleteByComponentId(componentId);
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, componentId, null, null, componentTypeCode);
        if (!CollectionUtils.isEmpty(componentConfigs) && null != configsPreprocessing) {
            configsPreprocessing.accept(componentConfigs);
        }
        batchSaveComponentConfig(componentConfigs);
    }

    private void validateConfigParamIfNeeded(List<ComponentConfig> componentConfigs, Long clusterId, Integer componentTypeCode) {
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return;
        }
        // 如果配置了 ranger，需要推送组件信息到 uic，所以此处进行前置校验
        Component rangerComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.RANGER.getTypeCode(), null, null);
        if (rangerComponent == null) {
            return;
        }
        Map<String, Object> key2ValueMap = ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
        this.validateParamForSync2Ranger(key2ValueMap, componentTypeCode);
    }

    public void validateParamForSync2Ranger(Map<String, Object> paramMap, Integer componentTypeCode) {
        if (!EComponentType.HIVE_SERVER.getTypeCode().equals(componentTypeCode)
                && !EComponentType.TRINO_SQL.getTypeCode().equals(componentTypeCode)
                && !EComponentType.HDFS.getTypeCode().equals(componentTypeCode)) {
            return;
        }

        String serviceName = (String) paramMap.get(UicConstant.SERVICE_NAME);
        EComponentType componentType = EComponentType.getByCode(componentTypeCode);
        DataSourceService.validateServiceName(serviceName, componentType.getName());

        String username = (String) paramMap.get(UicConstant.USER_NAME);
        String password = (String) paramMap.get(UicConstant.PASSWORD);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new RdosDefineException(String.format("配置 ranger 后，%s 的 %s、%s 必填", componentType.getName(), UicConstant.USER_NAME, UicConstant.PASSWORD));
        }

        validateHdfsParamIfNeed(paramMap, componentTypeCode);
    }

    private void validateHdfsParamIfNeed(Map<String, Object> paramMap, Integer componentTypeCode) {
        if (!EComponentType.HDFS.getTypeCode().equals(componentTypeCode)) {
            return;
        }
        // String description = (String) paramMap.get(UicConstant.DESCRIPTION);
        // if (StringUtils.isBlank(description)) {
        //     throw new RdosDefineException(String.format("配置 ranger 后，%s 的 %s 必填", EComponentType.HDFS.getName(), UicConstant.DESCRIPTION));
        // }
    }

    public void deleteComponentConfig(Long componentId) {
        logger.info("delete 【{}】component config ", componentId);
        componentConfigDao.deleteByComponentId(componentId);
    }

    public void deleteByComponentIdAndKeyAndComponentTypeCode(Long componentId, String key, Integer typeCode) {
        logger.info("delete 【{}, {}, {}】component config ", componentId, key, typeCode);
        componentConfigDao.deleteByComponentIdAndKeyAndComponentTypeCode(componentId, key, typeCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSaveComponentConfig(List<ComponentConfig> saveComponent) {
        if (CollectionUtils.isEmpty(saveComponent)) {
            return;
        }
        List<List<ComponentConfig>> partition = Lists.partition(saveComponent, 50);
        for (List<ComponentConfig> componentConfigs : partition) {
            componentConfigDao.insertBatch(componentConfigs);
        }
    }


    /**
     * 仅在第一次将console_component中component_template 转换为 console_component_config的数据使用
     * component_template旧数据默认最大深度不超过三层
     * typeName必须要从componentConfig获取
     *
     * @param componentConfig
     * @param componentTemplate
     */
    @Deprecated
    public void deepOldClientTemplate(String componentConfig, String componentTemplate, Long componentId, Long clusterId, Integer componentTypeCode) {
        if (null == clusterId || null == componentId || null == componentTypeCode || StringUtils.isBlank(componentTemplate)) {
            throw new RdosDefineException("参数不能为空");
        }
        List<ClientTemplate> clientTemplates = null;
        if (EComponentType.noControlComponents.contains(EComponentType.getByCode(componentTypeCode))) {
            clientTemplates = ComponentConfigUtils.convertXMLConfigToComponentConfig(componentConfig);
        } else {
            clientTemplates = JSONArray.parseArray(componentTemplate, ClientTemplate.class);
        }
        for (ClientTemplate clientTemplate : clientTemplates) {
            if (clientTemplate.getId() > 0L && StringUtils.isBlank(clientTemplate.getType())) {
                //兼容旧数据 前端的自定义参数标识
                clientTemplate.setType(EFrontType.CUSTOM_CONTROL.name());
            }
            if (ComponentConfigUtils.DEPLOY_MODE.equalsIgnoreCase(clientTemplate.getKey()) && clientTemplate.getValue() instanceof String) {
                // {
                //     "deploymode":"perjob",
                //}
                //兼容为数组
                String templateValue = (String) clientTemplate.getValue();
                if (!templateValue.startsWith("[")) {
                    JSONArray templateArray = new JSONArray();
                    templateArray.add(templateValue);
                    clientTemplate.setValue(templateArray);
                }
            }
        }

        if (EComponentType.SFTP.getTypeCode().equals(componentTypeCode)) {
            clientTemplates = ComponentConfigUtils.convertOldSftpTemplate(componentConfig);
        } else {
            clientTemplates = ComponentConfigUtils.convertOldClientTemplateToTree(clientTemplates);
        }
        //提取typeName
        if (StringUtils.isNotBlank(componentConfig)) {
            String typeNameValue = JSONObject.parseObject(componentConfig).getString(TYPE_NAME_KEY);
            if (StringUtils.isNotBlank(typeNameValue)) {
                clientTemplates.add(ComponentConfigUtils.buildOthers(TYPE_NAME_KEY, typeNameValue));
            }
        }
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, componentId, null, null, componentTypeCode);
        batchSaveComponentConfig(componentConfigs);
    }

    public ComponentConfig getComponentConfigByKey(Long componentId, String key) {
        return componentConfigDao.listByKey(componentId, key);
    }

    public Map<String, Object> getComponentConfigByType(Long componentId, EFrontType frontType) {
        List<ComponentConfig> configs = componentConfigDao.listByType(componentId, frontType.name());
        return ComponentConfigUtils.convertComponentConfigToMap(configs);
    }

    public List<ComponentConfig> getComponentConfigListByTypeCodeAndKey(Long clusterId, Integer componentTypeCode, String key) {
        return componentConfigDao.listByComponentTypeAndKey(clusterId, key, componentTypeCode);
    }

    public Map<String, Object> convertComponentConfigToMap(Long componentId, boolean isFilter) {
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(componentId, isFilter);
        return ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
    }

    public Map<String, Object> findSyncRangerComponentConfig(Long componentId, Integer componentTypeCode, boolean isFilter) {
        if (!DataSourceService.SYNC_TO_RANGER_COMPONENTS.contains(componentTypeCode)) {
            return Collections.emptyMap();
        }
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(componentId, isFilter);

        if (EComponentType.HDFS.getTypeCode().equals(componentTypeCode)) {
            // 只获取部分参数，防止参数过多卡死
            componentConfigs = componentConfigs.stream()
                    .filter(config -> SYNC_TO_RANGER_HDFS_KEYS.contains(config.getKey())
                            || EFrontType.CUSTOM_CONTROL.name().equalsIgnoreCase(config.getType())).collect(Collectors.toList());
        }
        return ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
    }

    public List<IComponentVO> getComponentVo(List<Component> components, boolean multiVersion) {
        if (CollectionUtils.isEmpty(components)) {
            return Collections.emptyList();
        }
        // 组件按类型分组, 因为可能存在组件有多个版本, 此时需要兼容单版本和多版本格式问题
        Map<Integer, IComponentVO> componentVoMap = new HashMap<>(components.size());
        components.stream().collect(Collectors.groupingBy(Component::getComponentTypeCode, Collectors.toList()))
                .forEach((k, v) -> componentVoMap.put(k, multiVersion ?
                        ComponentMultiVersionVO.getInstanceWithCapacityAndType(k, v.size()) : ComponentVO.getInstance()));
        List<IComponentVO> componentVoList = new ArrayList<>(components.size());
        for (Component component : components) {
            IComponentVO customComponent = componentVoMap.get(component.getComponentTypeCode());
            ComponentVO componentVO = IComponentVO.getComponentVo(customComponent, component);
            boolean isHadoopControl = EComponentType.hadoopVersionComponents.contains(EComponentType.getByCode(component.getComponentTypeCode()));
            if (isHadoopControl) {
                // do nothing
            } else {
                componentVO.setDeployType(component.getDeployType());
            }
            // 多版本才需要调用
            if (customComponent.multiVersion()) {
                customComponent.addComponent(componentVO);
            }
        }

        componentVoList.addAll(componentVoMap.values());
        return componentVoList;
    }

    public List<IComponentVO> getComponentVoByComponent(List<Component> components, boolean isFilter, Long clusterId, boolean multiVersion,boolean sm2Encrypt) {
        if (null == clusterId) {
            throw new RdosDefineException("集群id不能为空");
        }
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        // 集群所关联的组件的配置
        List<ComponentConfig> componentConfigs = componentConfigDao.listByClusterId(clusterId, isFilter).stream()
                .peek(componentConfig -> {
                    if (sm2Encrypt && scheduleDictService.checkDecryptKey(componentConfig)) {
                        //加密
                        componentConfig.setValue(SM2Util.encrypt(componentConfig.getValue(), environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey()));
                    }
                }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(componentConfigs)) {
            return new ArrayList<>(0);
        }
        // 组件按类型分组, 因为可能存在组件有多个版本, 此时需要兼容单版本和多版本格式问题
        Map<Integer, IComponentVO> componentVoMap = new HashMap<>(components.size());
        components.stream().collect(Collectors.groupingBy(Component::getComponentTypeCode, Collectors.toList()))
                .forEach((k, v) -> componentVoMap.put(k, multiVersion ?
                        ComponentMultiVersionVO.getInstanceWithCapacityAndType(k, v.size()) : ComponentVO.getInstance()));
        // 配置按照组件进行分组, 存在组件有多个版本
        Map<Long, List<ComponentConfig>> componentIdConfigs = componentConfigs.stream().collect(Collectors.groupingBy(ComponentConfig::getComponentId));
        List<IComponentVO> componentVoList = new ArrayList<>(components.size());
        for (Component component : components) {
            IComponentVO customComponent = componentVoMap.get(component.getComponentTypeCode());
            ComponentVO componentVO = IComponentVO.getComponentVo(customComponent, component);
            ;
            // 当前组件的配置
            List<ComponentConfig> configs = Optional.ofNullable(componentIdConfigs.get(component.getId())).orElse(new ArrayList<>());

            // 未选中的级联配置
            List<ComponentConfig> notChooseCascadeConfigs = getNotChooseCascadeConfigs(component, configs);

            List<ComponentConfig> allConfigForTemplate = new ArrayList<>();
            allConfigForTemplate.addAll(configs);
            allConfigForTemplate.addAll(notChooseCascadeConfigs);

            context.populateTip(allConfigForTemplate,component.getComponentTypeCode());
            // hdfs yarn 才将自定义参数移除 过滤返回给前端
            boolean isHadoopControl = EComponentType.hadoopVersionComponents.contains(EComponentType.getByCode(component.getComponentTypeCode()));
            if (isHadoopControl) {
                // 配置按照编辑类型进行分组
                Map<String, List<ComponentConfig>> configTypeMapping = configs.stream().collect(Collectors.groupingBy(ComponentConfig::getType));
                Map<String, List<ComponentConfig>> configTypeMappingForAllConfig = allConfigForTemplate.stream().collect(Collectors.groupingBy(ComponentConfig::getType));

                //hdfs yarn 4.1 template只有自定义参数
                componentVO.setComponentTemplate(JSONObject.toJSONString(ComponentConfigUtils.buildDBDataToClientTemplate(configTypeMappingForAllConfig.get(EFrontType.CUSTOM_CONTROL.name()))));
                //hdfs yarn 4.1 config为xml配置参数
                componentVO.setComponentConfig(JSONObject.toJSONString(ComponentConfigUtils.convertComponentConfigToMap(configTypeMapping.get(EFrontType.XML.name()))));
            } else {
                Map<String, Object> configToMap = ComponentConfigUtils.convertComponentConfigToMap(configs);
                Map<String, ScheduleDict> tipGroup = scheduleDictService.findTipGroup(component.getComponentTypeCode());

                componentVO.setComponentTemplate(JSONObject.toJSONString(ComponentConfigUtils.buildDBDataToTipsClientTemplate(allConfigForTemplate, component.getComponentTypeCode(), component.getDeployType(), tipGroup)));
                componentVO.setComponentConfig(JSONObject.toJSONString(configToMap));
                componentVO.setDeployType(component.getDeployType());
            }
            // 多版本才需要调用
            if (customComponent.multiVersion()) {
                customComponent.addComponent(componentVO);
            }
        }
        componentVoList.addAll(componentVoMap.values());
        return componentVoList;
    }

    private List<ComponentConfig> getNotChooseCascadeConfigs(Component component, List<ComponentConfig> configs) {
        List<ComponentConfig> result = new ArrayList<>();

        if (Objects.isNull(component) || org.apache.commons.collections.CollectionUtils.isEmpty(configs)) {
            return result;
        }

        // 获取组件模板
        List<ComponentConfig> templateComponentConfigs = loadTemplate(component);
        if (CollectionUtils.isEmpty(templateComponentConfigs)) {
            return result;
        }
        // 模板配置按照级联key进行分组
        Map<String, List<ComponentConfig>> cascadeKeyToConfigForTemplate = templateComponentConfigs
                .stream()
                .filter(e -> StringUtils.isNotBlank(e.getCascadeKey()))
                .collect(Collectors.groupingBy(ComponentConfig::getCascadeKey));

        if (MapUtils.isEmpty(cascadeKeyToConfigForTemplate)) {
            return result;
        }

        // 配置按照级联key进行分组
        Map<String, List<ComponentConfig>> cascadeKeyToConfig = configs
                .stream()
                .filter(e -> StringUtils.isNotBlank(e.getCascadeKey()))
                .collect(Collectors.groupingBy(ComponentConfig::getCascadeKey));

        // 如果本身包含级联 key, 但是级联的 config 为空，那么就把所有级联的 config 都返回
        if (CollectionUtils.isEmpty(cascadeKeyToConfig)) {
            Map<String, List<ComponentConfig>> collect = configs.stream().collect(Collectors.groupingBy(ComponentConfig::getKey));
            cascadeKeyToConfigForTemplate.forEach((k, v) -> {
                List<ComponentConfig> componentConfigs = collect.get(k);
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(componentConfigs)) {
                    result.addAll(v);
                }
            });
            return result;
        }


        for (Map.Entry<String, List<ComponentConfig>> entry : cascadeKeyToConfig.entrySet()) {
            String cascadeKey = entry.getKey();
            List<ComponentConfig> cascadeTemplateConfigs = cascadeKeyToConfigForTemplate.get(cascadeKey);
            if (CollectionUtils.isEmpty(cascadeTemplateConfigs)) {
                continue;
            }
            List<ComponentConfig> cascadeConfigs = entry.getValue();
            if (Objects.isNull(cascadeConfigs)) {
                cascadeConfigs = new ArrayList<>();
            }

            Map<String, List<ComponentConfig>> cascadeValueToConfig =
                    cascadeConfigs.stream().collect(Collectors.groupingBy(ComponentConfig::getCascadeValue));

            for (ComponentConfig cascadeTemplateConfig : cascadeTemplateConfigs) {
                String cascadeTemplateValue = cascadeTemplateConfig.getCascadeValue();
                List<ComponentConfig> componentConfigs = cascadeValueToConfig.get(cascadeTemplateValue);
                if (CollectionUtils.isEmpty(componentConfigs)) {
                    result.add(cascadeTemplateConfig);
                }
            }

        }
        return result;
    }

    private List<ComponentConfig> loadTemplate(Component component) {
        Integer componentTypeCode = component.getComponentTypeCode();
        Long clusterId = component.getClusterId();
        String versionName = component.getVersionName();
        Integer storeType = component.getStoreType();
        Integer deployType = component.getDeployType();


        PartCluster cluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        Part part = cluster.create(
                EComponentType.getByCode(componentTypeCode),
                versionName,
                EComponentType.getByCode(storeType),
                deployType);

        List<ComponentConfig> componentConfigs = part.loadTemplate();
        return componentConfigs;
    }

    public void updateValueComponentConfig(ComponentConfig componentConfig) {
        componentConfigDao.update(componentConfig);
    }

    @Cacheable(cacheNames = "component")
    public Map<String, Object> getCacheComponentConfigMap(Long clusterId, Integer componentType, boolean isFilter, Map<Integer, String> componentVersionMap, Long componentId) {
        if (null != componentId) {
            return convertComponentConfigToMap(componentId, isFilter);
        }
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, componentType, ComponentVersionUtil.getComponentVersion(componentVersionMap, componentType), null);
        if (null == component) {
            return null;
        }
        return convertComponentConfigToMap(component.getId(), isFilter);
    }

    @CacheEvict(cacheNames = "component", allEntries = true)
    public void clearComponentCache() {
        logger.info(" clear all component cache ");
    }
}
