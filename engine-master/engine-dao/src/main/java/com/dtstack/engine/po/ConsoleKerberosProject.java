package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * @Auther: dazhi
 * @Date: 2022/3/14 1:50 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleKerberosProject extends BaseEntity {
    /**
     * 项目id
     */

    private Long projectId;
    /**
     * 引用类型
     */

    private Integer appType;
    /**
     * 任务类型
     */

    private String taskTypeList;
    /**
     * kerberos文件名称
     */

    private String name;

    /**
     * keyTabName
     */
    private String keytabName;

    /**
     * krb5的文件
     */
    private String confName;
    /**
     * sftp存储路径
     */

    private String remotePath;
    /**
     * principal
     */

    private String principal;

    private Integer openKerberos;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(String taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Integer getOpenKerberos() {
        return openKerberos;
    }

    public void setOpenKerberos(Integer openKerberos) {
        this.openKerberos = openKerberos;
    }

    public String getKeytabName() {
        return keytabName;
    }

    public void setKeytabName(String keytabName) {
        this.keytabName = keytabName;
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }
}
