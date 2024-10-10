package com.dtstack.engine.api.pojo.sftp;

public class SftpConfig {

    //SFTP主机地址
    private String host;
    //SFTP端口地址
    private Integer port;
    //主机用户名
    private String username;
    //主机文件路径
    private String path;
    //是否使用连接池
    private boolean isUsePool;
    private Integer auth;
    private String password;
    private String rsaPath;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isUsePool() {
        return isUsePool;
    }

    public void setUsePool(boolean usePool) {
        isUsePool = usePool;
    }

    public Integer getAuth() {
        return auth;
    }

    public void setAuth(Integer auth) {
        this.auth = auth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRsaPath() {
        return rsaPath;
    }

    public void setRsaPath(String rsaPath) {
        this.rsaPath = rsaPath;
    }
}
