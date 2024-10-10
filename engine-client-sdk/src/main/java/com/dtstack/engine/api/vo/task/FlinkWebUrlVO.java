package com.dtstack.engine.api.vo.task;

/**
 * @author jiuwei@dtstack.com
 **/
public class FlinkWebUrlVO {
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
        return "FlinkWebUrlVO{" +
                "proxyUrl='" + proxyUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
                '}';
    }
}
