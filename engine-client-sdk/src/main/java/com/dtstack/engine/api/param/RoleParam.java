package com.dtstack.engine.api.param;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-10-19
 */
public class RoleParam {
    private Long roleId;

    private List<Long> userId;

    public List<Long> getUserId() {
        return userId;
    }

    public void setUserId(List<Long> userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
