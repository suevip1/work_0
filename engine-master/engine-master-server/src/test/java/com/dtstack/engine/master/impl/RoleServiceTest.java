package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.tree.TreeNode;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.role.PermissionVO;
import com.dtstack.engine.api.vo.role.RoleVO;
import com.dtstack.engine.api.vo.user.UserVO;
import com.dtstack.engine.master.mockcontainer.impl.RoleServiceMock;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthPermissionVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleVO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;


@MockWith(RoleServiceMock.class)
public class RoleServiceTest {

    RoleService roleService = new RoleService();

    @Test
    public void testRolePageQuery() throws Exception {
        PageResult<List<AuthRoleVO>> result = roleService.rolePageQuery();
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTree() throws Exception {
        TreeNode result = roleService.getTree();
        Assert.assertNull(result);
    }

    @Test
    public void testTransform() throws Exception {
        AuthPermissionVO authPermissionVO = new AuthPermissionVO();
        authPermissionVO.setId(1L);
        authPermissionVO.setParentId(-1L);
        TreeNode result = roleService.transform(authPermissionVO);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPermissionIdsByRoleId() throws Exception {
        RoleVO result = roleService.getPermissionIdsByRoleId(1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testAddUserAdmin() throws Exception {
        roleService.addUserAdmin(Collections.singletonList(1L), 1L);
    }

    @Test
    public void testRemoveAdmin() throws Exception {
        roleService.removeAdmin(1L, 1L);
    }

    @Test
    public void testListAdmin() throws Exception {
        List<UserVO> result = roleService.listAdmin();
        Assert.assertNotNull(result);
    }

    @Test
    public void testCheckIsSysAdmin() throws Exception {
        boolean result = roleService.checkIsSysAdmin(new UserDTO());
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckIsSysAudit() throws Exception {
        boolean result = roleService.checkIsSysAudit(new UserDTO());
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckIsScyAdmin() throws Exception {
        boolean result = roleService.checkIsScyAdmin(new UserDTO());
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckRole() throws Exception {
        boolean result = roleService.checkRole(new UserDTO(), "roleName", null);
        Assert.assertFalse(result);
    }

    @Test
    public void testGetPermissionByUserId() throws Exception {
        PermissionVO result = roleService.getPermissionByUserId(1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPermission() throws Exception {
        PermissionVO result = roleService.getPermission();
        Assert.assertNotNull(result);
    }
}