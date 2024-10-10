package com.dtstack.engine.master.controller;

import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.tree.TreeNode;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.param.RoleParam;
import com.dtstack.engine.api.vo.role.PermissionVO;
import com.dtstack.engine.api.vo.role.RoleVO;
import com.dtstack.engine.api.vo.user.UserVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-30
 */
@RestController
@RequestMapping("/node/role")
@Validated
@Api(value = "/node/role", tags = {"角色接口"})
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SessionUtil sessionUtil;

    @ApiOperation(value = "添加控制台管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "dtUic用户Id", required = true, dataType = "Long"),
    })
    @RequestMapping(value = "/addAdmin", method = {RequestMethod.POST})
    public void addAdmin(@RequestBody RoleParam roleParam,@RequestParam(name = "dt_user_id") Long dtUserId,@RequestParam(name = "dt_token") String dtToken) {
        if (null == roleParam || CollectionUtils.isEmpty(roleParam.getUserId())) {
            throw new RdosDefineException(ErrorCode.INVALID_PAGE_PARAM);
        }
        if(!checkCanDistributeAdmin(dtToken)){
            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
        }
        roleService.addUserAdmin(roleParam.getUserId(),dtUserId);
    }


    @ApiOperation(value = "是否能分配管理员")
    @RequestMapping(value = "/checkCanDistributeAdmin", method = {RequestMethod.POST})
    public boolean checkCanDistributeAdmin(@RequestParam(name = "dt_token") String dtToken) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (user == null) {
            return false;
        }
        return roleService.checkIsScyAdmin(user);
    }


    @ApiOperation(value = "移除管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "dtUic用户Id", required = true, dataType = "Long"),
    })
    @RequestMapping(value = "/removeAdmin", method = {RequestMethod.POST})
    public void removeAdmin(@NotNull @RequestParam("userId") Long userId, @RequestParam(name = "dt_user_id") Long dtUserId,
                            @RequestParam(name = "dt_token") String dtToken) {
        if(!checkCanDistributeAdmin(dtToken)){
            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
        }
        roleService.removeAdmin(userId,dtUserId);
    }


    @ApiOperation(value = "管理员列表")
    @RequestMapping(value = "/listAdmin", method = {RequestMethod.POST, RequestMethod.GET})
    public List<UserVO> listAdmin() {
       return roleService.listAdmin();
    }


    @PostMapping(value = "getPermissionIdsByRoleId")
    @ApiOperation("根据角色id获取角色下所有的权限点Id")
    public RoleVO getPermissionIdsByRoleId(@RequestBody RoleParam vo) {
        return roleService.getPermissionIdsByRoleId(vo.getRoleId());
    }

    @PostMapping(value = "getPermission")
    @ApiOperation("根据角色id获取角色下所有的权限点Id")
    public PermissionVO getPermission(@RequestParam("dt_token") String dtToken) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }

        if (null != user.getRootUser() && 1 == user.getRootUser()) {
            return roleService.getPermission();
        }

        return roleService.getPermissionByUserId(user.getDtuicUserId());
    }

    @PostMapping(value = "tree")
    @ApiOperation("权限树")
    public TreeNode getTree() {
        return roleService.getTree();
    }


    /**
     * 查询角色列表
     * @return
     */
    @PostMapping(value = "pageQuery")
    @ApiOperation("pageQuery")
    public PageResult<List<AuthRoleVO>> pageQuery() {
        return roleService.rolePageQuery();
    }

}
