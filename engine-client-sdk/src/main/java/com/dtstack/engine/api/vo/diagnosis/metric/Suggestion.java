package com.dtstack.engine.api.vo.diagnosis.metric;

public class Suggestion {

    /**
     * 建议内容
     */
    private String content;

    /**
     * 建议需要修改的配置名称
     */
    private String config;

    public String getSuggestion() {
        return "" + content + "" + config;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}