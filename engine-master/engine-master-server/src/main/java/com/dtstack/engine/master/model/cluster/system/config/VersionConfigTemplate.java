package com.dtstack.engine.master.model.cluster.system.config;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.util.StringUtils;

class VersionConfigTemplate {

    private static final String ERROR_MSG_TEMPLATE = "The value of '{}' {}";

    private static final String VERSION_KEY = "versionName";
    private static final String TEMPLATE_ID_KEY = "templateId";

    private final String parentKey;
    private final String versionName;
    private final Long templateId;

    VersionConfigTemplate(String parentKey, JSONObject obj) {
        this.parentKey = parentKey;
        try {
            this.versionName = obj.getString(VERSION_KEY);
            this.templateId = parseTemplateId(obj);
        } catch (JSONException e) {
            throw new InnerException("Data format error.", e);
        }
    }

    private Long parseTemplateId(JSONObject obj) {
        Long templateId = obj.getLong(TEMPLATE_ID_KEY);
        nonNull(templateId, TEMPLATE_ID_KEY);
        requiredNegative(templateId, TEMPLATE_ID_KEY);
        return templateId;
    }

    private void nonNull(Object obj, String key) {
        if (obj == null) {
            throw newException(key, "is required.");
        }
    }

    private void requiredNegative(long l, String key) {
        if (l >= 0) {
            throw newException(key, "must be negative.");
        }
    }

    private InnerException newException(String key, String extraInfo) {
        return new InnerException(fullError(key, extraInfo));
    }

    private String fullError(String key, String extraInfo) {
        return StringUtils.format(ERROR_MSG_TEMPLATE, fullPath(key), extraInfo);
    }

    private String fullPath(String key) {
        return StringUtils.format("{}.{}", this.parentKey, key);
    }

    boolean match(String versionName) {
        return this.versionName == null || this.versionName.equals(versionName);
    }

    Long getTemplateId() {
        return templateId;
    }
}
