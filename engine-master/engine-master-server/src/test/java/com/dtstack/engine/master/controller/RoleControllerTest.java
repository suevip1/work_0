package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.OmniConstructor;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.tree.TreeNode;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.param.RoleParam;
import com.dtstack.engine.api.vo.role.PermissionVO;
import com.dtstack.engine.api.vo.role.RoleVO;
import com.dtstack.engine.api.vo.user.UserVO;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleVO;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(RoleControllerTest.RoleControllerTestMock.class)
public class RoleControllerTest {

    private static final RoleController controller = new RoleController();

    static class RoleControllerTestMock{

        @MockInvoke(targetClass = RoleService.class)
        public void addUserAdmin(List<Long> dtUicUserId, Long operatorUserId) {

        }

        @MockInvoke(targetClass = RoleService.class)
        public boolean checkIsScyAdmin(UserDTO user) {
            return true;
        }
        @MockInvoke(targetClass = SessionUtil.class)
        public <T> T getUser(String token, Class<T> clazz) {
            UserDTO userDTO = new UserDTO();
            userDTO.setRootUser(1);
            return (T)userDTO;
        }

        @MockInvoke(targetClass = RoleService.class)
        public void removeAdmin(Long dtUicUserId, Long operatorUserId) {
        }

        @MockInvoke(targetClass = RoleService.class)
        public List<UserVO> listAdmin() {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = RoleService.class)
        public RoleVO getPermissionIdsByRoleId(Long roleId) {
            return null;
        }

        @MockInvoke(targetClass = RoleService.class)
        public PermissionVO getPermission() {
            return null;
        }

        @MockInvoke(targetClass = RoleService.class)
        public TreeNode getTree() {
            return new TreeNode();
        }

        @MockInvoke(targetClass = RoleService.class)
        public PageResult<List<AuthRoleVO>> rolePageQuery() {
            return PageResult.EMPTY_PAGE_RESULT;
        }

    }

    @Test
    public void addAdmin() {
        RoleParam roleParam = OmniConstructor.newInstance(RoleParam.class);
        roleParam.setUserId(Lists.newArrayList(1L));
        controller.addAdmin(roleParam,1L,"");
    }

    @Test
    public void checkCanDistributeAdmin() {
        controller.checkCanDistributeAdmin("");
    }

    @Test
    public void removeAdmin() {
        controller.removeAdmin(1L,1L,"");
    }

    @Test
    public void listAdmin() {
        controller.listAdmin();
    }

    @Test
    public void getPermissionIdsByRoleId() {
        controller.getPermissionIdsByRoleId(OmniConstructor.newInstance(RoleParam.class));
    }

    @Test
    public void getPermission() {
        controller.getPermission("");
    }

    @Test
    public void getTree() {
        controller.getTree();
    }

    @Test
    public void pageQuery() {
        controller.pageQuery();
    }
}