package com.dtstack.engine.api.param;

import com.dtstack.engine.api.vo.project.ProjectVO;

import java.io.Serializable;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-16 01:43
 */
public class ComponentInfoParam implements Serializable {
    private static final long serialVersionUID = -1217461115747649997L;

    /**
     * 租户 id
     */
    private Long dtUicTenantId;

    /**
     * 用户 id (如果控制台账号绑定中有对应 id,会替换 sql 组件的用户密码）
     */
    private Long dtUicUserId;

    /**
     * 组件 code
     */
    private Integer componentTypCode;

    /**
     * 组件版本，为空返回默认版本
     */
    private String componentVersion;

    /**
     * 项目信息，不为空时，会看是否有项目级的账号，进行替换
     */
    private ProjectVO projectVO;

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getDtUicUserId() {
        return dtUicUserId;
    }

    public void setDtUicUserId(Long dtUicUserId) {
        this.dtUicUserId = dtUicUserId;
    }

    public Integer getComponentTypCode() {
        return componentTypCode;
    }

    public void setComponentTypCode(Integer componentTypCode) {
        this.componentTypCode = componentTypCode;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public ProjectVO getProjectVO() {
        return projectVO;
    }

    public void setProjectVO(ProjectVO projectVO) {
        this.projectVO = projectVO;
    }
}