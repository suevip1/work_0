package com.dtstack.engine.api.dto;


import com.dtstack.engine.api.domain.AppTenantEntity;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author sishu.yss
 */
@ApiModel
public class UserDTO extends AppTenantEntity {

    private String userName;

    private String phoneNumber;

    private Long dtuicUserId;

    private String email;

    private Integer status;

    private Long defaultProjectId;

    private Integer rootUser;


    private List<String> roleNameList;

    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }

    public Integer getRootUser() {
        return rootUser;
    }

    public void setRootUser(Integer rootUser) {
        this.rootUser = rootUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getDtuicUserId() {
        return dtuicUserId;
    }

    public void setDtuicUserId(Long dtuicUserId) {
        this.dtuicUserId = dtuicUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(Long defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }
}
