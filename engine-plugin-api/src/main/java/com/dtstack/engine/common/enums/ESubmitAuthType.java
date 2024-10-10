package com.dtstack.engine.common.enums;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 提交任务的用户认证类型
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-07-24 15:10
 */
public enum ESubmitAuthType {

    /**
     * NONE 的情况下，用于保存提交用户
     */
    NONE("") {
        @Override
        public String findAuthBizName(JSONObject pluginInfo, String submitUserName) {
            return submitUserName;
        }
    },

    KERBEROS("kerberosConf") {
        @Override
        public String findAuthBizName(JSONObject pluginInfo, String submitUserName) {
            return pluginInfo.getString("principal");
        }
    },

    LDAP("ldapConf") {
        @Override
        public String findAuthBizName(JSONObject pluginInfo, String submitUserName) {
            return pluginInfo.getString("username");
        }
    },

    TBDS("tbdsConf") {
        @Override
        public String findAuthBizName(JSONObject pluginInfo, String submitUserName) {
            return pluginInfo.getString("tbdsUserName");
        }
    },

    PROXY("proxyConf") {
        @Override
        public String findAuthBizName(JSONObject pluginInfo, String submitUserName) {
            return pluginInfo.getString("dtProxyUserName");
        }
    },
    ;

    private static final Map<String, ESubmitAuthType> allAuthTypes = new HashMap<>();

    private String confName;

    static {
        for (ESubmitAuthType eSubmitAuthType : values()) {
            allAuthTypes.put(eSubmitAuthType.name(), eSubmitAuthType);
        }
    }

    ESubmitAuthType(String confName) {
        this.confName = confName;
    }

    public String getConfName() {
        return confName;
    }

    public static ESubmitAuthType findSubmitAuthType(String authType) {
        if (StringUtils.isEmpty(authType)) {
            return ESubmitAuthType.NONE;
        }
        return allAuthTypes.getOrDefault(authType, ESubmitAuthType.NONE);
    }

    /**
     * 获取认证业务名
     *
     * @param pluginInfo     插件认证信息
     * @param submitUserName 提交用户名
     * @return
     */
    public abstract String findAuthBizName(JSONObject pluginInfo, String submitUserName);
}
