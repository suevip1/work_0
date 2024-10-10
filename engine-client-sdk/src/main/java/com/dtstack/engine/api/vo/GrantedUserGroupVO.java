package com.dtstack.engine.api.vo;

/**
 * trino资源组绑定用户组情况
 */
public class GrantedUserGroupVO {

    /**租户名称**/
    private String tenantName;

    private Long tenantId;

    private Long groupId;

    /**用户组名称**/
    private String userGroupName;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
