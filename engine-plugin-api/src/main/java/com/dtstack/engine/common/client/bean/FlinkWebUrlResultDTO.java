package com.dtstack.engine.common.client.bean;

/**
 * @author jiuwei@dtstack.com
 **/
public class FlinkWebUrlResultDTO {
    private String proxyUrl;
    private String originUrl;

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public String toString() {
        return "FlinkUrlResultDTO{" +
                "proxyUrl='" + proxyUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
                '}';
    }
}
