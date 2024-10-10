package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.cache.DictCache;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.model.cluster.ComponentFacade;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.utils.SM2Util;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuebai
 * @date 2021-03-02
 */
@Service
public class ScheduleDictService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleDictService.class);
    public static final Predicate<String> defaultVersion = version -> "hadoop2".equalsIgnoreCase(version) || "hadoop3".equalsIgnoreCase(version)
            || "Hadoop 2.x".equalsIgnoreCase(version) || "Hadoop 3.x".equalsIgnoreCase(version);

    @Autowired
    private ScheduleDictDao scheduleDictDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ComponentFacade componentFacade;

    @Autowired
    private Context context;

    /**
     * 获取hadoop 和 flink spark 等组件的版本(有版本选择的才会在这获取)
     *
     * @return
     */
    public Map<String, List<ClientTemplate>> getVersion(Long clusterId) {
        Map<String, List<ClientTemplate>> versions = new HashMap<>(8);
        versions.put("hadoopVersion", getHadoopVersion());

        Component resourceComponent = componentFacade.tryGetResourceComponent(clusterId);
        List<Component> storageComponents = componentFacade.tryGetStorageComponent(clusterId);
        List<Component> flinkComponents = componentFacade.tryGetFlinkComponent(clusterId);

        versions.put(EComponentType.FLINK.getName(), getFlinkVersionByDepends(resourceComponent,flinkComponents));
        versions.put(EComponentType.SPARK_THRIFT.getName(), getNormalVersion(DictType.SPARK_THRIFT_VERSION.type));
        versions.put(EComponentType.SPARK.getName(), getSparkVersionByDepends(resourceComponent, storageComponents));

        versions.put(EComponentType.HIVE_SERVER.getName(), getNormalVersion(DictType.HIVE_VERSION.type));
        versions.put(EComponentType.INCEPTOR_SQL.getName(), getNormalVersion(DictType.INCEPTOR_SQL.type));
        versions.put(EComponentType.S3.getName(), getNormalVersion(DictType.S3_VERSION.type));
        versions.put(EComponentType.STARROCKS.getName(), getNormalVersion(DictType.STARROCKS_VERSION.type));
        return versions;
    }

    /**
     * 根据版本和组件 加载出额外的配置参数(需以自定义参数的方式)
     * yarn和hdfs的xml配置参数暂时不添加
     *
     * @param version
     * @param componentCode
     * @return
     */
    public List<ComponentConfig> loadExtraComponentConfig(String version, Integer componentCode) {
        if (StringUtils.isBlank(version) || defaultVersion.test(version) || !environmentContext.isCanAddExtraConfig()) {
            return new ArrayList<>(0);
        }
        EComponentType componentType = EComponentType.getByCode(componentCode);
        ScheduleDict extraConfig = scheduleDictDao.getByNameValue(DictType.COMPONENT_CONFIG.type, version.trim(), null, componentType.name().toUpperCase());
        if (null == extraConfig) {
            return new ArrayList<>(0);
        }
        return componentConfigDao.listByComponentId(Long.parseLong(extraConfig.getDictValue()), false);
    }

    public ScheduleDict getByNameAndValue(Integer dictType, String dictName, String dictValue, String dependName) {
        return scheduleDictDao.getByNameValue(dictType, dictName, dictValue, dependName);
    }

    private List<ClientTemplate> getNormalVersion(Integer type) {
        List<ScheduleDict> normalVersionDict = scheduleDictDao.listDictByType(type);
        if (CollectionUtils.isEmpty(normalVersionDict)) {
            return new ArrayList<>(0);
        }

        return normalVersionDict
                .stream()
                .map(s -> {
                    ClientTemplate clientTemplate = new ClientTemplate(s.getDictName(), s.getDictValue());
                    if (DictType.FLINK_VERSION.type.equals(s.getType()) && StringUtils.isNotBlank(s.getDependName())) {
                        List<Integer> collect = Stream.of(s.getDependName().split(",")).mapToInt(Integer::parseInt)
                                .boxed().collect(Collectors.toList());
                        clientTemplate.setDeployTypes(collect);
                    }
                    return clientTemplate;
                })
                .collect(Collectors.toList());
    }

    private List<ClientTemplate> getFlinkVersionByDepends(Component resourceComponent,List<Component> flinkComponents) {
        List<ScheduleDict> normalVersionDict = scheduleDictDao.listDictByType(DictType.FLINK_VERSION.type);
        if (CollectionUtils.isEmpty(normalVersionDict)) {
            return Collections.emptyList();
        }
        IntPredicate judgeDeploy;
        if (resourceComponent == null) {
            // 如果未配置资源调度组件，则只返回 EDeployType.STANDALONE 部署模式
            judgeDeploy = deploy -> (EDeployType.STANDALONE.getType() == deploy);
        } else {
            // 如果配置了资源调度组件，则只取跟当前配置的资源调度组件匹配的部署模式
            if (EComponentType.KUBERNETES.getTypeCode().equals(resourceComponent.getComponentTypeCode())) {
                judgeDeploy = deploy -> (EDeployType.KUBERNETES.getType() == deploy || EDeployType.STANDALONE.getType() == deploy);
            } else if (EComponentType.YARN.getTypeCode().equals(resourceComponent.getComponentTypeCode())) {
                judgeDeploy = deploy -> (EDeployType.YARN.getType() == deploy || EDeployType.STANDALONE.getType() == deploy);
            } else {
                throw new RdosDefineException(ErrorCode.COMPONENT_INVALID);
            }
        }

        Iterator<ScheduleDict> dictIterator = normalVersionDict.iterator();
        List<ClientTemplate> result = new ArrayList<>(normalVersionDict.size());

        List<ScheduleDict> versionParamDictList = scheduleDictDao.listDictByType(DictType.FLINK_VERSION_PARAM.type);
        Map<String, List<ScheduleDict>> versionParamDictMap = versionParamDictList.stream().collect(Collectors.groupingBy(ScheduleDict::getDictName));

        while (dictIterator.hasNext()) {
            ScheduleDict s = dictIterator.next();
            if (StringUtils.isNotBlank(s.getDependName())) {
                // 处理部署模式
                List<Integer> deployTypes = Stream.of(s.getDependName().split(ConfigConstant.COMMA))
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::parseInt)
                        .filter(judgeDeploy)
                        .boxed().collect(Collectors.toList());
                if (!deployTypes.isEmpty()) {
                    ClientTemplate clientTemplate = new ClientTemplate(s.getDictName(), s.getDictValue());
                    clientTemplate.setDeployTypes(deployTypes);

                    List<ScheduleDict> hiddenDicts = versionParamDictMap.get(ConfigConstant.FLINK_HIDDEN);
                    if (!CollectionUtils.isEmpty(hiddenDicts)) {
                        for (ScheduleDict scheduleDict : hiddenDicts) {
                            if (clientTemplate.getKey().equals(scheduleDict.getDictValue())) {
                                if (CollectionUtils.isEmpty(flinkComponents)) {
                                    clientTemplate.setHidden(Boolean.TRUE);
                                    continue;
                                }

                                boolean hidden = Boolean.TRUE;
                                for (Component flinkComponent : flinkComponents) {
                                    if (StringUtils.isNotBlank(flinkComponent.getVersionName())
                                            && flinkComponent.getVersionName().equals(s.getDictName())) {
                                        // 说明隐藏版本已经存在
                                        hidden = Boolean.FALSE;
                                        break;
                                    }
                                }

                                clientTemplate.setHidden(hidden);
                            }
                        }
                    }

                    List<ScheduleDict> flinkDefaultDicts = versionParamDictMap.get(ConfigConstant.FLINK_DEFAULT);
                    if (!CollectionUtils.isEmpty(flinkDefaultDicts)) {
                        for (ScheduleDict scheduleDict : flinkDefaultDicts) {
                            if (clientTemplate.getKey().equals(scheduleDict.getDictValue())) {
                                clientTemplate.setDefaulted(Boolean.TRUE);
                            }
                        }

                        if (clientTemplate.getDefaulted() == null) {
                            clientTemplate.setDefaulted(Boolean.FALSE);
                        }
                    }

                    result.add(clientTemplate);
                }
            }
        }
        return result;
    }

    private List<ClientTemplate> getSparkVersionByDepends(Component resourceComponent, List<Component> storageComponents) {
        List<ClientTemplate> sparkVersions = getNormalVersion(DictType.SPARK_VERSION.type);
        if (resourceComponent == null || CollectionUtils.isEmpty(storageComponents)) {
            return sparkVersions;
        }
        if (!EComponentType.KUBERNETES.getTypeCode().equals(resourceComponent.getComponentTypeCode())) {
            // 非 k8s 直接返回全部
            return sparkVersions;
        }
        // 针对 k8s 做特殊处理，筛选出 k8s-hdfs2-spark240 -- 产品要求，后期若变更，则再适配
        boolean containsHdfs2 = false;
        for (Component storageComponent : storageComponents) {
            if (storageComponent.getComponentTypeCode().equals(EComponentType.HDFS.getTypeCode())
                    && storageComponent.getHadoopVersion().startsWith("2")) {
                containsHdfs2 = true;
                break;
            }
        }
        if (!containsHdfs2) {
            // 非 hdfs2，则不设置 spark 版本参数
            return Collections.emptyList();
        }

        Iterator<ClientTemplate> iterator = sparkVersions.iterator();
        while (iterator.hasNext()) {
            ClientTemplate next = iterator.next();
            // 过滤掉非 2.4 的版本
            if (!"2.4".equals(next.getKey())) {
                iterator.remove();
            }
        }
        return sparkVersions;
    }

    private List<ClientTemplate> getHadoopVersion() {
        List<ScheduleDict> scheduleDicts = scheduleDictDao.listDictByType(DictType.HADOOP_VERSION.type);
        Map<String, List<ScheduleDict>> versions = scheduleDicts
                .stream()
                .collect(Collectors.groupingBy(ScheduleDict::getDependName));
        List<ClientTemplate> clientTemplates = new ArrayList<>(versions.size());
        for (String dependName : versions.keySet()) {
            List<ScheduleDict> keyDicts = versions.get(dependName);
            keyDicts = keyDicts.stream().sorted(Comparator.comparing(ScheduleDict::getSort)).collect(Collectors.toList());
            List<ClientTemplate> templates = keyDicts.stream().map(s -> new ClientTemplate(s.getDictName(), s.getDictValue()))
                    .collect(Collectors.toList());
            ClientTemplate vendorFolder = new ClientTemplate();
            vendorFolder.setKey(dependName);
            vendorFolder.setValues(templates);
            clientTemplates.add(vendorFolder);
        }
        clientTemplates.sort(Comparator.comparing(ClientTemplate::getKey));
        return clientTemplates;
    }

    /**
     * 转化 versionName -> versionValue
     *
     * @param componentVersion 组件版本名称
     * @param engineType       engine type
     * @return versionValue
     */
    public String convertVersionNameToValue(String componentVersion, String engineType) {
        if (StringUtils.isNotBlank(componentVersion)) {
            Integer componentType = EngineTypeComponentType.engineName2ComponentType(engineType);
            if (null != componentType) {
                Integer dictType = DictType.getByEComponentType(EComponentType.getByCode(componentType));
                if (null != dictType) {
                    ScheduleDict versionDict = getByNameAndValue(dictType, componentVersion.trim(), null, null);
                    if (null != versionDict) {
                        return versionDict.getDictValue();
                    }
                }
            }
        }
        return null;
    }


    public String convertVersionToVersionFormat(String componentVersion) {
        if (StringUtils.isNotBlank(componentVersion)) {
            Integer dictType = DictType.VERSION_FORMAT.type;
            ScheduleDict versionDict = getByNameAndValue(dictType, componentVersion.trim(), null, null);
            if (null != versionDict) {
                return versionDict.getDictValue();
            }
        }
        return componentVersion;
    }

    public List<ScheduleDict> listById(Long id, Integer size) {
        if (id == null) {
            id = 0L;
        }

        if (size == null) {
            size = DictCache.size;
        }

        return scheduleDictDao.listById(id, size);
    }

    public List<ScheduleDict> listByDictType(DictType dictType) {
        return scheduleDictDao.listDictByType(dictType.type);
    }

    public List<ScheduleDict> listByDictTypeNames(DictType dictType,List<String> dictNames) {
        return scheduleDictDao.listByTypeAndNames(dictType.type,dictNames);
    }
    public ScheduleDict listByDictTypeName(DictType dictType,String dictName) {
        return scheduleDictDao.listByTypeAndName(dictType.type,dictName);
    }

    public Map<String, Object> operatorValue(Map<String, Object> configMap, Function<String, String> operatorFunction) {
        if (MapUtils.isEmpty(configMap) || StringUtils.isBlank(environmentContext.getSM2PrivateKey())) {
            return configMap;
        }
        List<String> encryptKey = context.listSm2EncryptKeys();
        if (org.apache.commons.collections.CollectionUtils.isEmpty(encryptKey)) {
            return configMap;
        }
        for (String key : encryptKey) {
            Object xmlValue = configMap.get(key);
            if (xmlValue == null) {
                continue;
            }
            if (xmlValue instanceof String) {
                String xmlValueStr = (String) xmlValue;
                // xmlValue 为空，sm2 加解密会夯住，需要跳过
                if (StringUtils.isEmpty(xmlValueStr)) {
                    continue;
                }
                String operatorValue = operatorFunction.apply(xmlValueStr);
                configMap.put(key, operatorValue);
            }
        }
        return configMap;
    }

    public Map<String, Object> encryptValue(Map<String, Object> map) {
        return operatorValue(map, (value) -> SM2Util.encrypt(value, environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey()));
    }

    public String decryptIfNecessary(ComponentConfig componentConfig) {
        if (checkDecryptKey(componentConfig)) {
            return SM2Util.decryptIgnoreException(componentConfig.getValue(), environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey());
        }
        return componentConfig.getValue();
    }

    public boolean checkDecryptKey(ComponentConfig componentConfig){
        List<String> encryptKey = context.listSm2EncryptKeys();
        return encryptKey.contains(componentConfig.getKey()) && StringUtils.isBlank(componentConfig.getDependencyValue());
    }

    /**
     * 获取组别信息
     *
     * @param componentTypeCode
     * @return
     */
    public Map<String, ScheduleDict> findTipGroup(Integer componentTypeCode) {
        return scheduleDictDao.getByDependName(DictType.TIPS.type, StringUtils.EMPTY)
                .stream()
                .filter(s -> StringUtils.equals(String.valueOf(componentTypeCode), s.getDictDesc()))
                .collect(Collectors.toMap(ScheduleDict::getDictName, s -> s, (oldValue, newValue) -> newValue));
    }

    public ScheduleDict getOne(Long id) {
        return scheduleDictDao.getOne(id);
    }

    public void updateScheduleDict(ScheduleDict scheduleDict) {
        scheduleDictDao.updateValue(scheduleDict);
    }
}
