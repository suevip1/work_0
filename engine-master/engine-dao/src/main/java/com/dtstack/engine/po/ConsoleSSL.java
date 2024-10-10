package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

@ApiModel
public class ConsoleSSL extends BaseEntity {

    private Long clusterId;

    private Integer componentType;

    private String componentVersion;

    private String remotePath;

    private String truststore;

    private String sslClient;

    private String md5;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getComponentType() {
        return componentType;
    }

    public void setComponentType(Integer componentType) {
        this.componentType = componentType;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getTruststore() {
        return truststore;
    }

    public void setTruststore(String truststore) {
        this.truststore = truststore;
    }

    public String getSslClient() {
        return sslClient;
    }

    public void setSslClient(String sslClient) {
        this.sslClient = sslClient;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
