package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.tree.Tree;
import com.dtstack.dtcenter.common.tree.TreeNode;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.role.PermissionVO;
import com.dtstack.engine.api.vo.role.RoleVO;
import com.dtstack.engine.api.vo.user.UserVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.mapstruct.RoleStruct;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthAllPermissionParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthPermissionParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthRoleUserParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.DefaultRoleQueryParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListQueryPmByRoleIdParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListRUByRoleIdAndPIdParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.RemoveAuthRoleUserParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.RoleByIdQueryParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthPermissionVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleUserVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-10-19
 */
@Service
public class RoleService extends Tree {

    protected static Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    private volatile TreeNode root;

    private Map<String, TreeNode> treeNodeMap = new ConcurrentHashMap<>();

    @Autowired
    private AuthCenterAPIClient authCenterAPIClient;

    @Autowired
    private RoleStruct roleStruct;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    /**
     * project 公共组件初始值为-1 tenant为-1 区分开
     */
    public static final Long default_project = 0L;
    public static final Long default_tenant = -2L;

    public static final Integer THREE_MEMBER = 3;
    //系统管理员
    public static final String THREE_MEMBER_SYS_ADMIN = "SYS_ADMIN";
    //安全管理员
    public static final String THREE_MEMBER_SCY_ADMIN = "SCY_ADMIN";
    //安全审计员
    public static final String THREE_MEMBER_SCY_AUDIT = "SCY_AUDIT";


    public PageResult<List<AuthRoleVO>> rolePageQuery() {
        DefaultRoleQueryParam param = new DefaultRoleQueryParam();
        param.setAppType(AppType.CONSOLE.getType());
        param.setRoleName("控制台管理员");
        param.setDtuicTenantId(-1L);
        return authCenterAPIClient.pageByRoleParam(param).getData();
    }


    public TreeNode getTree() {
        if (root == null) {
            synchronized (RoleService.class) {
                if (root == null) {
                    reloadTree();
                }
            }
        }
        return root;
    }

    private void reloadTree() {
        AuthAllPermissionParam param = new AuthAllPermissionParam();
        param.setAppType(AppType.CONSOLE.getType());
        List<AuthPermissionVO> data = authCenterAPIClient.findPermissionListByAppType(param).getData();
        super.reload(data);
    }

    @Override
    public TreeNode getRootNode() {
        return root;
    }

    @Override
    public void setRootNode(TreeNode root) {
        this.root = root;
    }

    @Override
    public Map<String, TreeNode> getTreeNodeMaps() {
        return this.treeNodeMap;
    }

    @Override
    protected TreeNode transform(Object info) {
        AuthPermissionVO permission = (AuthPermissionVO) info;
        TreeNode node = new TreeNode();
        node.setNodeId(Long.toString(permission.getId()));
        node.setParentId(Long.toString(permission.getParentId()));
        node.setBindData(permission);
        return node;
    }


    public RoleVO getPermissionIdsByRoleId(Long roleId) {
        ListQueryPmByRoleIdParam pmByRoleIdParam = new ListQueryPmByRoleIdParam();
        pmByRoleIdParam.setRoleId(roleId);
        pmByRoleIdParam.setAppType(AppType.CONSOLE.getType());
        ApiResponse<List<AuthPermissionVO>> permissionListByRoleId = authCenterAPIClient.getPermissionListByRoleId(pmByRoleIdParam);
        List<AuthPermissionVO> authPermissionVOList = permissionListByRoleId.getData();
        if (CollectionUtils.isEmpty(authPermissionVOList)) {
            return null;
        }
        List<Long> permissionIdList = authPermissionVOList.stream().map(AuthPermissionVO::getId).collect(Collectors.toList());
        RoleByIdQueryParam roleQueryParam = new RoleByIdQueryParam();
        roleQueryParam.setRoleId(roleId);
        ApiResponse<AuthRoleVO> roleInfo = authCenterAPIClient.getRoleById(roleQueryParam);
        AuthRoleVO role = roleInfo.getData();
        RoleVO roleVO = roleStruct.toRoleVO(role);
        roleVO.setPermissionIds(permissionIdList);
        return roleVO;
    }

    public void addUserAdmin(List<Long> dtUicUserId, Long operatorUserId) {
        Long roleId = getAdminRoleId().getId();
        ListRUByRoleIdAndPIdParam roleUserParam = new ListRUByRoleIdAndPIdParam();
        roleUserParam.setAppType(AppType.CONSOLE.getType());
        roleUserParam.setRoleIds(Lists.newArrayList(roleId));
        roleUserParam.setProjectId(default_project);
        ApiResponse<List<AuthRoleUserVO>> listApiResponse = authCenterAPIClient.listByRoleIdsAndProId(roleUserParam);
        List<AuthRoleUserVO> roleUserResponses = listApiResponse.getData();
        if (CollectionUtils.isNotEmpty(roleUserResponses) && roleUserResponses.size() + dtUicUserId.size() > environmentContext.getAdminSize()) {
            throw new RdosDefineException(ErrorCode.ROLE_SIZE_LIMIT);
        }
        AuthRoleUserParam authRoleUserParam = new AuthRoleUserParam();
        authRoleUserParam.setAppType(AppType.CONSOLE.getType());
        authRoleUserParam.setProjectId(default_project);
        authRoleUserParam.setRoleId(roleId);
        authRoleUserParam.setTargetUserIds(dtUicUserId);
        authRoleUserParam.setDtuicTenantId(default_tenant);
        LOGGER.info("operator [{}] add console admin [{}]", operatorUserId, dtUicUserId);
        authCenterAPIClient.addRoleUser(authRoleUserParam);
    }

    private AuthRoleVO getAdminRoleId() {
        PageResult<List<AuthRoleVO>> listPageResult = rolePageQuery();
        List<AuthRoleVO> data = listPageResult.getData();
        if (CollectionUtils.isEmpty(data)) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }
        return data.get(0);
    }

    public void removeAdmin(Long dtUicUserId, Long operatorUserId) {
        AuthRoleVO role = getAdminRoleId();
        RemoveAuthRoleUserParam authRoleUserParam = new RemoveAuthRoleUserParam();
        authRoleUserParam.setAppType(AppType.CONSOLE.getType());
        authRoleUserParam.setProjectId(default_project);
        authRoleUserParam.setDtuicUserId(operatorUserId);
        authRoleUserParam.setRoleIds(Lists.newArrayList(role.getId()));
        authRoleUserParam.setTargetUserId(dtUicUserId);
        authRoleUserParam.setDtuicTenantId(default_tenant);
        authRoleUserParam.setIsRoot(true);
        LOGGER.info("operator [{}] remove console admin [{}]", operatorUserId, dtUicUserId);
        authCenterAPIClient.removeRoleUserByParam(authRoleUserParam);
    }

    public List<UserVO> listAdmin() {
        AuthRoleVO role = getAdminRoleId();
        ListRUByRoleIdAndPIdParam roleUserParam = new ListRUByRoleIdAndPIdParam();
        roleUserParam.setAppType(AppType.CONSOLE.getType());
        roleUserParam.setRoleIds(Lists.newArrayList(role.getId()));
        roleUserParam.setProjectId(default_project);
        ApiResponse<List<AuthRoleUserVO>> listApiResponse = authCenterAPIClient.listByRoleIdsAndProId(roleUserParam);
        List<AuthRoleUserVO> data = listApiResponse.getData();
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }
        List<Long> userIds = data.stream().map(AuthRoleUserVO::getDtuicUserId).collect(Collectors.toList());
        ApiResponse<List<UICUserVO>> userInfo = uicUserApiClient.getByUserIds(userIds);
        List<UICUserVO> userData = userInfo.getData();
        if (CollectionUtils.isEmpty(userData)) {
            return new ArrayList<>();
        }
        Map<Long, UICUserVO> userIdMapping = userData.stream().collect(Collectors.toMap(UICUserVO::getUserId, u -> u));
        List<UserVO> userVOS = new ArrayList<>(userData.size());
        for (AuthRoleUserVO datum : data) {
            UserVO userVO = new UserVO();
            userVO.setDtuicUserId(datum.getDtuicUserId());
            UICUserVO uicUserVO = userIdMapping.get(datum.getDtuicUserId());
            userVO.setJoinAdminTime(new Timestamp(datum.getGmtCreate().getTime()));
            if (null == uicUserVO) {
                userVOS.add(userVO);
                continue;
            }
            userVO.setUserName(uicUserVO.getUserName());
            userVO.setEmail(uicUserVO.getEmail());
            userVO.setPhoneNumber(uicUserVO.getPhone());
            userVOS.add(userVO);
        }
        return userVOS;
    }

    private boolean checkIsConsoleAdmin(Long dtuicUserId) {
        AuthRoleVO role = getAdminRoleId();
        ListRUByRoleIdAndPIdParam roleUserParam = new ListRUByRoleIdAndPIdParam();
        roleUserParam.setAppType(AppType.CONSOLE.getType());
        roleUserParam.setRoleIds(Lists.newArrayList(role.getId()));
        roleUserParam.setProjectId(default_project);
        ApiResponse<List<AuthRoleUserVO>> listApiResponse = authCenterAPIClient.listByRoleIdsAndProId(roleUserParam);
        List<AuthRoleUserVO> data = listApiResponse.getData();
        return CollectionUtils.isNotEmpty(data) && data.stream().anyMatch(u -> u.getDtuicUserId().equals(dtuicUserId));
    }

    public boolean checkIsSysAdmin(UserDTO user) {
        return checkRole(user,THREE_MEMBER_SYS_ADMIN,(userDTO -> checkIsConsoleAdmin(user.getDtuicUserId())));
    }

    public boolean checkIsSysAudit(UserDTO user) {
        return checkRole(user,THREE_MEMBER_SCY_AUDIT,null);
    }

    public boolean checkIsScyAdmin(UserDTO user) {
        return checkRole(user,THREE_MEMBER_SCY_ADMIN,null);
    }

    public boolean checkRole(UserDTO user, String roleName, Predicate<UserDTO> handler) {
        if (THREE_MEMBER.equals(user.getVersion())) {
            //threeMember
            if (CollectionUtils.isNotEmpty(user.getRoleNameList())) {
                if (user.getRoleNameList().stream().anyMatch(roleName::equals)) {
                    return true;
                }
            }
        } else {
            //root
            if (null != user.getRootUser() && 1 == user.getRootUser()) {
                return true;
            }
        }
        if (null != handler) {
            return handler.test(user);
        }
        return false;
    }

    public PermissionVO getPermissionByUserId(Long dtuicUserId) {
        AuthPermissionParam param = new AuthPermissionParam();
        param.setAppType(AppType.CONSOLE.getType());
        param.setDtuicUserId(dtuicUserId);
        ApiResponse<List<AuthPermissionVO>> permission = authCenterAPIClient.findPermissionListByUserId(param);
        List<AuthPermissionVO> data = permission.getData();
        PermissionVO vo = new PermissionVO();
        vo.setRoot(false);
        if (CollectionUtils.isEmpty(data)) {
            return vo;
        }
        vo.setPermissions(data.stream().map(AuthPermissionVO::getCode).collect(Collectors.toList()));
        return vo;
    }

    public PermissionVO getPermission() {
        AuthAllPermissionParam authAllPermissionParam = new AuthAllPermissionParam();
        authAllPermissionParam.setAppType(AppType.CONSOLE.getType());
        ApiResponse<List<AuthPermissionVO>> permission = authCenterAPIClient.findPermissionListByAppType(authAllPermissionParam);
        List<AuthPermissionVO> data = permission.getData();

        PermissionVO vo = new PermissionVO();
        vo.setRoot(true);

        if (CollectionUtils.isEmpty(data)) {
            return vo;
        }
        vo.setPermissions(data.stream().map(AuthPermissionVO::getCode).collect(Collectors.toList()));
        return vo;
    }
}
