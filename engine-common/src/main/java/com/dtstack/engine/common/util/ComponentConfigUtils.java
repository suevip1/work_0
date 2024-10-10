package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.enums.TipsGroupType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-02-22
 */
public class ComponentConfigUtils {

    public final static String DEPLOY_MODE = "deploymode";
    private final static String dependencySeparator = "$";
    private static Predicate<String> isOtherControl = s -> "typeName".equalsIgnoreCase(s) || "md5Key".equalsIgnoreCase(s);
    private static Predicate<ClientTemplate> isAuth = c -> (c.getKey().equalsIgnoreCase("password") || c.getKey().equalsIgnoreCase("rsaPath"))
            && "auth".equalsIgnoreCase(c.getDependencyKey());

    private static final List<Integer> NEED_TIPS_GROUP_COMPONENT = Arrays.asList(EComponentType.FLINK.getTypeCode()
    ,EComponentType.SPARK.getTypeCode(),EComponentType.VOLCANO.getTypeCode());


    /**
     * 将数据库数据转换为前端展示的树结构
     *
     * @param componentConfigs
     * @return
     */
    public static List<ClientTemplate> buildDBDataToClientTemplate(List<ComponentConfig> componentConfigs) {
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return new ArrayList<>(0);
        }
        Map<String, List<ComponentConfig>> dependencyMapping = componentConfigs
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getDependencyKey()))
                .collect(Collectors.groupingBy(ComponentConfig::getDependencyKey));
        List<ClientTemplate> reduceTemplate = new ArrayList<>();
        List<ComponentConfig> emptyDependencyValue = componentConfigs
                .stream()
                .filter(c -> StringUtils.isBlank(c.getDependencyKey()))
                .collect(Collectors.toList());
        for (ComponentConfig componentConfig : emptyDependencyValue) {
            ClientTemplate clientTemplate = componentConfigToTemplate(componentConfig);
            deepToBuildClientTemplate(dependencyMapping, dependencyMapping.size(), clientTemplate, clientTemplate.getKey());
            reduceTemplate.add(clientTemplate);
        }
        return sortByKey(reduceTemplate);
    }


    /**
     * 解析config成clientTemplate树结构
     *
     * @param dependencyMapping
     * @param maxDeep
     * @param clientTemplate
     * @param dependencyKey
     */
    private static void deepToBuildClientTemplate(Map<String, List<ComponentConfig>> dependencyMapping, Integer maxDeep, ClientTemplate clientTemplate, String dependencyKey) {
        if (maxDeep <= 0) {
            return;
        }
        List<ComponentConfig> dependValueConfig = dependencyMapping.get(dependencyKey);
        if (!CollectionUtils.isEmpty(dependValueConfig)) {
            maxDeep = --maxDeep;
            List<ClientTemplate> valuesTemplate = new ArrayList<>();
            for (ComponentConfig componentConfig : dependValueConfig) {
                ClientTemplate componentClientTemplate = componentConfigToTemplate(componentConfig);
                componentClientTemplate.setRequired(BooleanUtils.toBoolean(componentConfig.getRequired()));
                deepToBuildClientTemplate(dependencyMapping, maxDeep, componentClientTemplate, dependencyKey + dependencySeparator + componentClientTemplate.getKey());
                valuesTemplate.add(componentClientTemplate);
            }

            clientTemplate.setValues(valuesTemplate);
        }

    }

    /**
     * 根据 component 类型判断是否要进行 tips分组
     * @param componentConfigs
     * @param componentTypeCode
     * @param tipGroup
     * @return
     *
     */
    public static List<ClientTemplate> buildDBDataToTipsClientTemplate(List<ComponentConfig> componentConfigs, Integer componentTypeCode, Integer deployType, Map<String, ScheduleDict> tipGroup) {
        if (CollectionUtils.isEmpty(componentConfigs)){
            return new ArrayList<>(0);
        }
        List<ClientTemplate> reduceTemplate = buildDBDataToClientTemplate(componentConfigs);
        if (NEED_TIPS_GROUP_COMPONENT.contains(componentTypeCode)) {
            // 若根节点就有 字典依赖 直接根节点分组
            if (StringUtils.isNotEmpty(reduceTemplate.get(0).getDependName())) {
                return tipsGroup(reduceTemplate, null, tipGroup);
            }
            for (ClientTemplate clientTemplateRoot : reduceTemplate) {
                buildingTipsTree(null, clientTemplateRoot, tipGroup);
            }
        }
        return reduceTemplate;
    }

    /**
     * 构建tips分组树
     * @return
     */
    private static void buildingTipsTree(ClientTemplate pre, ClientTemplate cur, Map<String, ScheduleDict> tipGroup){
        // 无子集且type不为空 进行分组
        if (CollectionUtils.isEmpty(cur.getValues())) {
            if (pre != null && StringUtils.isNotEmpty(cur.getType())) {
                pre.setValues(tipsGroup(pre.getValues(), pre, tipGroup));
            }
            return;
        }
        for (ClientTemplate value : cur.getValues()) {
            buildingTipsTree(cur,value, tipGroup);
            // values为空 表示 到达了叶子节点
            if (CollectionUtils.isEmpty(value.getValues())){
                return;
            }
        }
    }

    /**
     * tips分组
     * @param valuesTemplate
     * @param clientTemplate
     * @param tipGroup
     * @return
     */
    private static List<ClientTemplate> tipsGroup(List<ClientTemplate> valuesTemplate,ClientTemplate clientTemplate, Map<String, ScheduleDict> tipGroup){
        List<ClientTemplate> tipsTemplateValues = new ArrayList<>();
        Map<String, List<ClientTemplate>> tempTipsGroup = valuesTemplate
                .stream()
                .map(c -> {
                    if (c.getDependName() == null){
                        // 处理没有组别的情况
                        if (EFrontType.CUSTOM_CONTROL.name().equals(c.getType())) {
                            c.setDependName(TipsGroupType.CUSTOM.value);
                        } else {
                            // 获取不到组别，就归类到「其他」
                            c.setDependName(TipsGroupType.OTHER.value);
                        }
                    }
                    return c;
                })
                .collect(Collectors.groupingBy(ClientTemplate::getDependName));
        mockCustomGroupTipIfNeeded(tempTipsGroup);
        for (Map.Entry<String, List<ClientTemplate>> entry : tempTipsGroup.entrySet()){
            String key = entry.getKey();
            List<ClientTemplate> value = entry.getValue();
            ClientTemplate template = new ClientTemplate();
            template.setKey(key);
            // 填充组别的顺序
            ScheduleDict tipGroupDict = tipGroup.getOrDefault(key, new ScheduleDict());
            template.setSort(tipGroupDict.getSort());
            if (clientTemplate != null){
                template.setDependencyKey(clientTemplate.getKey());
            }
            template.setType(EFrontType.TIPS.name());
            // 对组内元素进行排序
            value.sort(Comparator.nullsLast(
                    Comparator.comparing(ClientTemplate::getSort, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(ClientTemplate::getId)
            ));
            template.setGroupValues(value);
            tipsTemplateValues.add(template);
        }

        // 对组别进行排序
        tipsTemplateValues.sort(Comparator.comparing(ClientTemplate::getSort, Comparator.nullsLast(Integer::compareTo)));
        return tipsTemplateValues;
    }

    /**
     * 塞入空的自定义参数，便于前端组装
     * @param tempTipsGroup 原始分组
     *
     */
    private static void mockCustomGroupTipIfNeeded(Map<String, List<ClientTemplate>> tempTipsGroup) {
        if (CollectionUtils.isEmpty(tempTipsGroup)) {
            return;
        }
        tempTipsGroup.putIfAbsent(TipsGroupType.CUSTOM.value, Collections.emptyList());
    }

    /**
     * 将数据库数据转换为key-value形式 供插件使用
     * @param configs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertComponentConfigToMap(List<ComponentConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            return new HashMap<>(0);
        }
        Map<String, List<ComponentConfig>> dependencyMapping = configs
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getDependencyKey()))
                .collect(Collectors.groupingBy(ComponentConfig::getDependencyKey));
        List<ComponentConfig> emptyDependencyValue = configs
                .stream()
                .filter(c -> StringUtils.isBlank(c.getDependencyKey()))
                .collect(Collectors.toList());
        Map<String, Object> configMaps = new HashMap<>(configs.size());
        for (ComponentConfig componentConfig : emptyDependencyValue) {
            Map<String, Object> deepToBuildConfigMap = ComponentConfigUtils.deepToBuildConfigMap(dependencyMapping, dependencyMapping.size(), componentConfig.getKey());
            if (DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey()) || EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                configMaps.put(componentConfig.getKey(), DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey()) ?
                        JSONArray.parseArray(componentConfig.getValue()) : componentConfig.getValue());
                Object specialDeepConfig = deepToBuildConfigMap.get(componentConfig.getKey());
                if (specialDeepConfig instanceof Map) {
                    // DEPLOY_MODE特殊处理 需要将特殊结构下的value 放到同级下 保持原结构一致
//                    {
//                        "deploymode":[{"perjob":""},{"session":""}],
//                        "typeName":"yarn2-hdfs2-spark210"
//                    }
//                      调整为
//                    {
//                        "deploymode":["perjob"],
//                        "perjob":"",
//                        "session":"",
//                        "typeName":"yarn2-hdfs2-spark210"
//                    }
                    if(DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey())){
                        //只设置对应的值
                        JSONArray deployValues = JSONArray.parseArray(componentConfig.getValue());
                        for (Object deployValue : deployValues) {
                            Map deployValueMap = new HashMap<>();
                            deployValueMap.put(deployValue,((Map<?, ?>) specialDeepConfig).get(deployValue));
                            configMaps.putAll(deployValueMap);
                        }
                    }

                } else if (EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                    //group正常处理
//                    {
//                        "pythonConf":{},
//                        "jupyterConf":{},
//                        "typeName":"yarn2-hdfs2-dtscript",
//                        "commonConf":{}
//                    }
                    configMaps.put(componentConfig.getKey(), deepToBuildConfigMap);
                } else {
                    configMaps.putAll(deepToBuildConfigMap);
                }

            } else {
                if (!CollectionUtils.isEmpty(deepToBuildConfigMap)) {
                    if (EFrontType.RADIO_LINKAGE.name().equalsIgnoreCase(componentConfig.getType())) {
                        parseRadioLinkage(dependencyMapping, configMaps, componentConfig, deepToBuildConfigMap);
                    } else if (EFrontType.SELECT.name().equalsIgnoreCase(componentConfig.getType())){
                        configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                    } else {
                        configMaps.putAll(deepToBuildConfigMap);
                        if (EFrontType.RADIO.name().equalsIgnoreCase(componentConfig.getType())) {
                            //radio 需要将子配置添加进去 自身也需要
                            configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                        }
                    }
                } else {
                    configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                }
            }
        }
        return configMaps;
    }

    private static void parseRadioLinkage(Map<String, List<ComponentConfig>> dependencyMapping, Map<String, Object> configMaps, ComponentConfig componentConfig, Map<String, Object> deepToBuildConfigMap) {
        //radio 联动 需要将设置radio选择的值 并根据radio的指选择values中对于的key value
        configMaps.put(componentConfig.getKey(), componentConfig.getValue());
        //radio联动的值
        Map<String, Map> radioLinkageValues = (Map) deepToBuildConfigMap.get(componentConfig.getKey());
        //radio联动的控件
        List<ComponentConfig> radioLinkageComponentConfigValue = dependencyMapping.get(componentConfig.getKey());
        if (!CollectionUtils.isEmpty(radioLinkageComponentConfigValue)) {
            Optional<ComponentConfig> first = radioLinkageComponentConfigValue
                    .stream()
                    .filter(r -> r.getValue().equalsIgnoreCase(componentConfig.getValue()))
                    .findFirst();
            if (first.isPresent()) {
                //根据选择的控件 选择对应的值 没有选择的值不能设置 否则前端测试联通性判断会出现key不一致
                Map map = radioLinkageValues.get(first.get().getKey());
                if (MapUtils.isNotEmpty(map)) {
                    configMaps.putAll(map);
                }
            }
        }
    }


    /**
     * 将前端展示模板templates转换为key-value形式 供插件使用
     *
     * @param templates
     * @return
     */
    public static Map<String, Object> convertClientTemplateToMap(List<ClientTemplate> templates) {
        if (CollectionUtils.isEmpty(templates)) {
            return new HashMap<>(0);
        }
        List<ComponentConfig> configs = saveTreeToList(templates, null, null, null, null, null);
        return convertComponentConfigToMap(configs);
    }


    /**
     * 将展示的树结构转换为List存入数据库中
     *
     * @param reduceTemplate
     * @param clusterId
     * @param componentId
     * @param dependKey
     * @param dependValue
     */
    public static List<ComponentConfig> saveTreeToList(List<ClientTemplate> reduceTemplate, Long clusterId, Long componentId, String dependKey, String dependValue, Integer componentTypeCode) {
        List<ComponentConfig> saveComponentConfigs = new ArrayList<>();
        for (ClientTemplate clientTemplate : reduceTemplate) {
            ComponentConfig componentConfig = ComponentConfigUtils.convertClientTemplateToConfig(clientTemplate);
            componentConfig.setClusterId(clusterId);
            componentConfig.setComponentId(componentId);
            if (StringUtils.isNotBlank(dependKey)) {
                componentConfig.setDependencyKey(dependKey);
            }
            if (StringUtils.isNotBlank(dependValue) && componentConfig.getType().equalsIgnoreCase(EFrontType.GROUP.name())) {
                componentConfig.setDependencyValue(dependValue);
            }

            componentConfig.setRequired(BooleanUtils.toInteger(clientTemplate.isRequired(), 1, 0, 0));
            componentConfig.setComponentTypeCode(componentTypeCode);

            if (!EFrontType.TIPS.name().equals(clientTemplate.getType())) {
                saveComponentConfigs.add(componentConfig);
            }

            List<ClientTemplate> values = clientTemplate.getValues();
            if (CollectionUtils.isEmpty(values)) {
                values = clientTemplate.getGroupValues();
            }

            if (!CollectionUtils.isEmpty(values)) {
                String dependKeys = "";
                //拼接前缀 用以区分不同group下同名key的情况
                //deploymode$session$metrics.reporter.promgateway.deleteOnShutdown
                //deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown
                if (StringUtils.isNotBlank(dependKey)) {
                    dependKeys = dependKey + dependencySeparator + clientTemplate.getKey();
                } else {
                    dependKeys = clientTemplate.getKey();
                }

                String dependencyValue = clientTemplate.getDependencyValue();
                if (EFrontType.TIPS.name().equals(clientTemplate.getType())) {
                    dependKeys = dependKey;
                    dependencyValue = dependValue;
                }

                List<ComponentConfig> componentConfigs = saveTreeToList(values, clusterId, componentId,
                        dependKeys, dependencyValue, componentTypeCode);
                if (!CollectionUtils.isEmpty(componentConfigs)) {
                    saveComponentConfigs.addAll(componentConfigs);
                }
            }
        }
        return saveComponentConfigs;
    }


    private static Map<String, Object> deepToBuildConfigMap(Map<String, List<ComponentConfig>> dependencyMapping, Integer maxDeep, String key) {
        if (maxDeep <= 0) {
            return new HashMap<>(0);
        }
        List<ComponentConfig> dependValueConfig = dependencyMapping.get(key);
        if (!CollectionUtils.isEmpty(dependValueConfig)) {
            maxDeep = --maxDeep;
            Map<String, Object> keyValuesConfigMaps = new HashMap<>();
            Map<String, Object> oneToMoreConfigMaps = new HashMap<>();
            for (ComponentConfig componentConfig : dependValueConfig) {
                //INPUT为单选
                if (EFrontType.INPUT.name().equalsIgnoreCase(componentConfig.getType()) || EFrontType.SELECT.name().equalsIgnoreCase(componentConfig.getType())) {
                    //key-value 一对一
                    keyValuesConfigMaps.put(componentConfig.getKey(), componentConfig.getValue());
                } else {
                    Map<String, Object> sonConfigMap = deepToBuildConfigMap(dependencyMapping, maxDeep, key + dependencySeparator + componentConfig.getKey());
                    if (!CollectionUtils.isEmpty(sonConfigMap)) {
                        //key-value 一对多
                        oneToMoreConfigMaps.put(componentConfig.getKey(), sonConfigMap);
                    } else {
                        //type类型为空
                        keyValuesConfigMaps.put(componentConfig.getKey(), componentConfig.getValue());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(oneToMoreConfigMaps)) {
                keyValuesConfigMaps.put(key, oneToMoreConfigMaps);
            }
            return keyValuesConfigMaps;
        }
        return new HashMap<>(0);
    }


    /**
     * 将数据库console_component的component_template调整为完全的树结构
     *
     * @param clientTemplates
     * @return
     */
    @Deprecated
    public static List<ClientTemplate> convertOldClientTemplateToTree(List<ClientTemplate> clientTemplates) {
        if (CollectionUtils.isEmpty(clientTemplates)) {
            return new ArrayList<>(0);
        }
        //子key
        Map<String, List<ClientTemplate>> dependMapping = clientTemplates
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getDependencyKey()))
                .collect(Collectors.groupingBy(ClientTemplate::getDependencyKey));
        List<ClientTemplate> reduceTemplate = new ArrayList<>();
        for (ClientTemplate clientTemplate : clientTemplates) {
            if (StringUtils.isBlank(clientTemplate.getDependencyKey())) {
                reduceTemplate.add(clientTemplate);
            }
        }
        for (ClientTemplate clientTemplate : reduceTemplate) {
            List<ClientTemplate> sonTemplate = dependMapping.get(clientTemplate.getKey());
            if (CollectionUtils.isEmpty(sonTemplate)) {
                continue;
            }
            for (ClientTemplate template : sonTemplate) {
                if (null != template) {
                    List<ClientTemplate> values = clientTemplate.getValues();
                    if (null == values) {
                        clientTemplate.setValues(new ArrayList<>());
                    } else {
                        clientTemplate.getValues().removeIf(s -> s.getKey().equalsIgnoreCase(template.getKey()));
                    }
                    clientTemplate.getValues().add(template);
                }
            }
        }
        return reduceTemplate;
    }

    public static ComponentConfig convertClientTemplateToConfig(ClientTemplate clientTemplate) {
        ComponentConfig componentConfig = new ComponentConfig();
        BeanUtils.copyProperties(clientTemplate, componentConfig);
        if (clientTemplate.getValue() instanceof List) {
            componentConfig.setValue(JSONArray.toJSONString(clientTemplate.getValue()));
        } else {
            componentConfig.setValue(null == clientTemplate.getValue() ? "" : String.valueOf(clientTemplate.getValue()));
        }
        if (null != componentConfig.getValue()) {
            componentConfig.setValue(componentConfig.getValue().trim());
        }
        if (isOtherControl.test(componentConfig.getKey())) {
            componentConfig.setType(EFrontType.OTHER.name());
        } else if (EFrontType.PASSWORD.name().equalsIgnoreCase(componentConfig.getKey())
                && StringUtils.isBlank(componentConfig.getDependencyKey())
                && !componentConfig.getType().equals(EFrontType.CUSTOM_CONTROL.name())) {
            //key password的控件转换为加密显示, 自定义参数通过配置项加密
            componentConfig.setType(EFrontType.PASSWORD.name());
        } else {
            componentConfig.setType(Optional.ofNullable(clientTemplate.getType()).orElse("").toUpperCase());
        }
        return componentConfig;
    }

    private static ClientTemplate componentConfigToTemplate(ComponentConfig componentConfig) {
        ClientTemplate clientTemplate = new ClientTemplate();
        BeanUtils.copyProperties(componentConfig, clientTemplate);
        clientTemplate.setRequired(BooleanUtils.toBoolean(componentConfig.getRequired()));
        if (StringUtils.isNotBlank(componentConfig.getValue()) && componentConfig.getValue().startsWith("[")) {
            clientTemplate.setValue(JSONArray.parseArray(componentConfig.getValue()));
        } else {
            if (StringUtils.isBlank(componentConfig.getValue()) && EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                //group key 和value 同值
                clientTemplate.setValue(componentConfig.getKey());
            } else {
                clientTemplate.setValue(componentConfig.getValue());
            }
        }
        if (null == clientTemplate.getDependencyValue()) {
            clientTemplate.setDependencyKey(null);
        }
        return clientTemplate;
    }

    /**
     * 将yarn hdfs 等xml配置信息转换为clientTemplate
     *
     * @param componentConfigString
     * @return
     */
    public static List<ClientTemplate> convertXMLConfigToComponentConfig(String componentConfigString) {
        if (StringUtils.isBlank(componentConfigString)) {
            return new ArrayList<>(0);
        }
        JSONObject componentConfigObj = JSONObject.parseObject(componentConfigString);
        List<ClientTemplate> configs = new ArrayList<>(componentConfigObj.size());
        for (String key : componentConfigObj.keySet()) {
            configs.add(buildCustom(key, componentConfigObj.get(key), EFrontType.XML.name()));
        }
        return configs;
    }

    /**
     * 原sftp数据clientTemplate结构变更无法做转换
     * 将原来数据的password 和rsaPath移除 将其内嵌到auth下的input
     *
     * @param componentConfig
     * @return
     */
    @Deprecated
    public static List<ClientTemplate> convertOldSftpTemplate(String componentConfig) {
        if (StringUtils.isEmpty(componentConfig)) {
            return new ArrayList<>(0);
        }
        String templateStr = "[{\"key\":\"auth\",\"required\":true,\"type\":\"RADIO_LINKAGE\",\"value\":\"2\",\"values\":[{\"dependencyKey\":\"auth\",\"dependencyValue\":\"1\",\"key\":\"password\",\"required\":true,\"type\":\"\"," +
                "\"value\":\"1\",\"values\":[{\"dependencyKey\":\"auth$password\",\"dependencyValue\":\"\",\"key\":\"password\",\"required\":true,\"type\":\"PASSWORD\",\"value\":\"\"}]}," +
                "{\"dependencyKey\":\"auth\",\"dependencyValue\":\"2\",\"key\":\"rsaPath\",\"required\":true,\"type\":\"\",\"value\":\"2\",\"values\":[{\"dependencyKey\":\"auth$rsaPath\"," +
                "\"dependencyValue\":\"\",\"key\":\"rsaPath\",\"required\":true,\"type\":\"INPUT\",\"value\":\"\"}]}]},{\"key\":\"fileTimeout\",\"required\":true,\"type\":\"INPUT\",\"value\":\"300000\"}," +
                "{\"key\":\"host\",\"required\":true,\"type\":\"INPUT\",\"value\":\"\"},{\"key\":\"isUsePool\",\"required\":true,\"type\":\"INPUT\",\"value\":\"true\"}," +
                "{\"key\":\"maxIdle\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"maxTotal\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"}," +
                "{\"key\":\"maxWaitMillis\",\"required\":true,\"type\":\"INPUT\",\"value\":\"3600000\"},{\"key\":\"minIdle\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"}," +
                "{\"key\":\"path\",\"required\":true,\"type\":\"INPUT\",\"value\":\"\"},{\"key\":\"port\",\"required\":true,\"type\":\"INPUT\",\"value\":\"22\"}," +
                "{\"key\":\"timeout\",\"required\":true,\"type\":\"INPUT\",\"value\":\"0\"},{\"key\":\"username\",\"required\":true,\"type\":\"INPUT\",\"value\":\"\"}]\n";
        List<ClientTemplate> clientTemplates = JSONObject.parseArray(templateStr, ClientTemplate.class);
        JSONObject originConfig = JSONObject.parseObject(componentConfig);
        for (ClientTemplate clientTemplate : clientTemplates) {
            fillClientTemplate(clientTemplate, originConfig);
        }
        return clientTemplates;
    }


    public static void fillClientTemplate(ClientTemplate clientTemplate, JSONObject config) {
        String value = config.getString(clientTemplate.getKey());
        if (StringUtils.isNotBlank(value)) {
            if(!isAuth.test(clientTemplate)){
                clientTemplate.setValue(value);
            }
            if (!CollectionUtils.isEmpty(clientTemplate.getValues())) {
                for (ClientTemplate clientTemplateValue : clientTemplate.getValues()) {
                    fillClientTemplate(clientTemplateValue, config);
                }
            }
        }
    }

    public static ClientTemplate buildOthers(String key, String value) {
        return buildCustom(key, value, EFrontType.OTHER.name());
    }

    public static ClientTemplate buildCustom(String key, Object value, String type) {
        ClientTemplate componentConfig = new ClientTemplate();
        componentConfig.setType(type);
        componentConfig.setKey(key);
        componentConfig.setValue(value);
        return componentConfig;
    }

    /**
     * 根据key值来排序
     *
     * @param clientTemplates
     * @return
     */
    public static List<ClientTemplate> sortByKey(List<ClientTemplate> clientTemplates) {
        if (CollectionUtils.isEmpty(clientTemplates)) {
            return clientTemplates;
        }
        // 先按 sort 排序，再按 key 字典序排序
        clientTemplates.sort(Comparator.nullsFirst(
                Comparator.comparing(ClientTemplate::getSort, Comparator.nullsLast(Integer::compare))
                        .thenComparing(ClientTemplate::getKey, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
        ));
        for (ClientTemplate clientTemplate : clientTemplates) {
            ComponentConfigUtils.sortByKey(clientTemplate.getValues());
        }
        return clientTemplates;
    }

}
