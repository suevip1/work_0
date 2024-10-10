package com.dtstack.engine.api.vo.role;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/4/13 3:54 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class PermissionVO {

    private Boolean isRoot;

    private List<String> permissions;

    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
