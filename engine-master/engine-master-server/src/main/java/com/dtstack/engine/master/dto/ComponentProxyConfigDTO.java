package com.dtstack.engine.master.dto;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-22 17:19
 * 插件定义的组件代理信息
 */
public class ComponentProxyConfigDTO implements Serializable {
    private static final long serialVersionUID = -7811898782182187287L;

    /**
     * 代理类型
     */
    private String type;

    /**
     * 代理的配置信息
     */
    private JSONObject config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ComponentProxyConfigDTO{");
        sb.append("type='").append(type).append('\'');
        sb.append(", config=").append(config);
        sb.append('}');
        return sb.toString();
    }
}
