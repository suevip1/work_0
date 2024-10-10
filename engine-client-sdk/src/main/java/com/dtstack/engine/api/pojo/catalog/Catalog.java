package com.dtstack.engine.api.pojo.catalog;


import com.dtstack.engine.api.pojo.sftp.SftpConfig;

public class Catalog {

    private boolean cacheEnabled = true;

    /**
     * catalog类型: hive/iceberg
     */
    private String type;

    /**
     * 内置catalog类型:hive/hadoop
     */
    private String catalogType;

    /**
     * 设置hive metastore对应的hive-site.xml
     */
    private String hiveConfDir;


    private String defaultDatabase;

    /**
     * hive元数据存储的thrift uri
     */
    private String uri;

    /**
     * hive metastore客户端池的大小
     */
    private String clients;

    private Integer propertyVersion = 1;

    private Integer operateType;

    private String name;

    private SftpConfig sftpConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SftpConfig getSftpConfig() {
        return sftpConfig;
    }

    public void setSftpConfig(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    public String getHiveConfDir() {
        return hiveConfDir;
    }

    public void setHiveConfDir(String hiveConfDir) {
        this.hiveConfDir = hiveConfDir;
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void setDefaultDatabase(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getClients() {
        return clients;
    }

    public void setClients(String clients) {
        this.clients = clients;
    }

    public Integer getPropertyVersion() {
        return propertyVersion;
    }

    public void setPropertyVersion(Integer propertyVersion) {
        this.propertyVersion = propertyVersion;
    }
}
