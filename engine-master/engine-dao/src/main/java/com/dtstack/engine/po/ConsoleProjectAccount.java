package com.dtstack.engine.po;

import java.util.Date;

/**
 * 项目级绑定账号设置
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 15:20
 */
public class ConsoleProjectAccount {
    private Long id;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码, 备注: base64 加密
     */
    private String password;

    /**
     * 组件类型
     */
    private Integer componentTypeCode;

    /**
     * 是否启动，0否 1是
     */
    private Integer status;

    /**
     * 新增时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 0正常 1逻辑删除
     */
    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}