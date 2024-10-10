package com.dtstack.engine.api.vo.console;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 项目级账号绑定
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 15:37
 */
public class ConsoleProjectAccountVO implements Serializable {
    private static final long serialVersionUID = 2907748271663136815L;

    private Long id;

    /**
     * 项目id
     */
    @NotNull(message = "projectId not null")
    private Long projectId;

    /**
     * 应用类型
     */
    @NotNull(message = "appType not null")
    private Integer appType;

    /**
     * 用户名
     */
    @NotNull(message = "userName not null")
    private String userName;

    /**
     * 密码
     */
    @NotNull(message = "password not null")
    private String password;

    /**
     * 组件类型
     */
    @NotNull(message = "componentTypeCode not null")
    private Integer componentTypeCode;

    /**
     * 是否启动，0否 1是
     */
    @NotNull(message = "status not null")
    private Integer status;

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
}
