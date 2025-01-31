package com.dtstack.engine.master.model.cluster.system.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import dt.insight.plat.lang.base.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 组件 依赖关系、角色、版本模型
 */
public class ComponentModel {

    private static final String CONFIG_NAME = "componentModel";
    private static final String ERROR_MSG_TEMPLATE = "The value of '{}' {}";

    private static final String OWNER_KEY = "owner";
    private static final String DEPENDS_ON_KEY = "dependsOn";
    private static final String ALLOW_COEXISTENCE_KEY = "allowCoexistence";
    private static final String NAME_TEMPLATE_KEY = "nameTemplate";
    private static final String VERSION_DICTIONARY_KEY = "versionDictionary";

    private final EComponentType type;
    private final EComponentScheduleType owner;
    private final List<EComponentScheduleType> dependsOn;
    private final boolean allowCoexistence;
    private final String nameTemplate;
    private Map<String, String> versionDictionary;

    public ComponentModel(EComponentType type, ScheduleDict scheduleDict, ScheduleDictDao dao) {
        this.type = type;
        try {
            JSONObject valueObj = JSONObject.parseObject(scheduleDict.getDictValue());
            if (valueObj == null) {
                throw invalidComponentConfig(StringUtils.format(
                        "Component of type '{}' is not configured.", type));
            }

            this.owner = parseOwner(valueObj);
            this.dependsOn = Collections.unmodifiableList(parseDependsOn(valueObj));
            Boolean allowCoexistence = valueObj.getBoolean(ALLOW_COEXISTENCE_KEY);
            this.allowCoexistence = allowCoexistence != null && allowCoexistence;
            List<ScheduleDict> dicts = loadVersions(valueObj, dao);
            versionDictionary = dicts.stream().collect(Collectors.toMap(ScheduleDict::getDictName, ScheduleDict::getDictValue));

            this.nameTemplate = valueObj.getString(NAME_TEMPLATE_KEY);
        } catch (JSONException e) {
            throw invalidComponentConfig("Data format error.", e);
        }
    }

    private EComponentScheduleType parseOwner(JSONObject value) {
        String ownerName = value.getString(OWNER_KEY);
        nonNull(ownerName, OWNER_KEY);
        return valueOf(ownerName, EComponentScheduleType.class, OWNER_KEY);
    }

    private void nonNull(Object obj, String key) {
        if (obj == null) {
            throw invalidComponentConfig(key, "is required.");
        }
    }

    private List<EComponentScheduleType> parseDependsOn(JSONObject value) {
        List<String> dependsOnNames = getStringList(value, DEPENDS_ON_KEY);
        nonNullElement(dependsOnNames, DEPENDS_ON_KEY);
        requiredNoDuplicate(dependsOnNames, DEPENDS_ON_KEY);
        return dependsOnNames.stream()
                .map(n -> valueOf(n, EComponentScheduleType.class, DEPENDS_ON_KEY))
                .collect(Collectors.toList());
    }

    private List<String> getStringList(JSONObject value, String key) {
        JSONArray res = value.getJSONArray(key);
        return res == null ?
                Collections.emptyList() :
                res.toJavaList(String.class);
    }

    private void requiredNoDuplicate(List<?> list, String key) {
        long distinctCnt = list.stream().distinct().count();
        if (distinctCnt < list.size()) {
            throw invalidComponentConfig(key, "has duplicate elements.");
        }
    }

    private void nonNullElement(List<?> list, String key) {
        boolean hasNull = list.stream().anyMatch(Objects::isNull);
        if (hasNull) {
            throw invalidComponentConfig(key, "has null.");
        }
    }

    private List<ScheduleDict> loadVersions(JSONObject valueObj, ScheduleDictDao dao) {
        String name = valueObj.getString(VERSION_DICTIONARY_KEY);
        if (name == null) {
            return Collections.emptyList();
        }

        DictType vdType = valueOf(name, DictType.class, VERSION_DICTIONARY_KEY);
        return dao.listDictByType(vdType.type);
    }


    private <E extends Enum<E>> E valueOf(String name, Class<E> clazz, String key) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
            throw invalidComponentConfig(key, isUnknownEnumMsg(clazz), e);
        }
    }

    private String isUnknownEnumMsg(Class<? extends Enum<?>> clazz) {
        return StringUtils.format(
                "is unknown '{}'.",
                clazz.getCanonicalName()
        );
    }

    private SystemConfigMapperException invalidComponentConfig(String key, String extraInfo) {
        return invalidComponentConfig(fullErrorMsg(key, extraInfo));
    }

    private SystemConfigMapperException invalidComponentConfig(String key, String extraInfo, Throwable cause) {
        return invalidComponentConfig(fullErrorMsg(key, extraInfo), cause);
    }

    private String fullErrorMsg(String key, String extraInfo) {
        return StringUtils.format(ERROR_MSG_TEMPLATE, key, extraInfo);
    }

    private SystemConfigMapperException invalidComponentConfig(String message) {
        return invalidComponentConfig(message, (Throwable) null);
    }

    private SystemConfigMapperException invalidComponentConfig(String message, Throwable cause) {
        String errorMsg = StringUtils.format(
                "Invalid component model: '{}'. {}", this.type, message);
        return new SystemConfigMapperException(CONFIG_NAME, errorMsg, cause);
    }

    public EComponentScheduleType getOwner() {
        return owner;
    }

    public List<EComponentScheduleType> getDependsOn() {
        return this.dependsOn;
    }

    public boolean isAllowCoexistence() {
        return allowCoexistence;
    }

    public String getNameTemplate() {
        return this.nameTemplate;
    }

    public String getVersionValue(String versionName) {
        if (this.versionDictionary.isEmpty()) {
            return Strings.EMPTY_STRING;
        }

        String versionValue = this.versionDictionary.get(versionName.trim());
        if (StringUtils.isBlank(versionValue)) {
            return Strings.EMPTY_STRING;
        }
        return versionValue;
    }

}
