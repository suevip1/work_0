package com.dtstack.engine.master.controller;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.dtcenter.loader.dto.trinoResource.TrinoOverView;
import com.dtstack.dtcenter.loader.dto.trinoResource.TrinoResouceDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.QueueParam;
import com.dtstack.engine.api.param.ResourceGroupGrantPageParam;
import com.dtstack.engine.api.param.ResourceGroupPageParam;
import com.dtstack.engine.api.param.ResourceGroupParam;
import com.dtstack.engine.api.vo.GrantedProjectVO;
import com.dtstack.engine.api.vo.ResourceGroupDropDownVO;
import com.dtstack.engine.api.vo.ResourceGroupListVO;
import com.dtstack.engine.api.vo.ReversalGrantCheckResultVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dto.LabelGrantedProjectDTO;
import com.dtstack.engine.master.dto.LabelResourceGroupDTO;
import com.dtstack.engine.master.dto.LabelReversalGrantCheckResultDTO;
import com.dtstack.engine.dto.GrantedProjectDTO;
import com.dtstack.engine.api.param.*;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.GroupVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping({"/node/resource"})
@Api(value = "/node/resource", tags = {"资源组接口"})
public class ResourceGroupController {

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @RequestMapping(value = "/pageQuery", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_view_all",
            tenant = "console_resource_resource_view_tenant")
    public PageResult<List<ResourceGroupListVO>> pageQuery(@RequestBody @Valid ResourceGroupPageParam param,
                                                           @RequestParam("dtToken") String dtToken,
                                                           @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        // 判断权限
        if (!isAllAuth) {
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
            List<Long> bindTenantIds = clusterTenantDao.listBoundedTenants(param.getClusterId());

            List<Long> retainList = Lists.newArrayList(tenantIds);
            retainList.retainAll(bindTenantIds);
            if (CollectionUtils.isEmpty(retainList)) {
                // 说明不相交
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }

        return resourceGroupService.pageQuery(param.getClusterId(), param.getComponentTypeCode(), param.getCurrentPage(), param.getPageSize());
    }

    /**
     * 展示节点标签资源组列表
     * @param param
     * @param dtToken
     * @param isAllAuth
     * @return
     */
    @RequestMapping(value = "/listLabelUserResource", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_view_all",
            tenant = "console_resource_resource_view_tenant")
    public List<LabelResourceGroupDTO> listLabelUserResource(@RequestBody ResourceGroupParam param,
                                                             @RequestParam("dtToken") String dtToken,
                                                             @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        return resourceGroupService.listLabelUserResource(param.getClusterId());
    }

    /**
     * 分页查询trino资源组
     * @param param
     * @return
     */
    @RequestMapping(value="/pageQueryTrinoResource", method = {RequestMethod.POST})
    public PageResult<List<TrinoResouceDTO>> pageQueryTrinoResource(@RequestBody @Valid ResourceGroupPageParam param) {
        return resourceGroupService.pageQueryTrinoResource(param.getClusterId(),param.getDtuicTenantId() ,param.getCurrentPage(), param.getPageSize());
    }

    /**
     * 分页查询trino资源组绑定的用户组信息
     * @param param
     * @return
     */
    @RequestMapping(value="/pageGrantedUserGroups", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_granted_projects")
    public PageResult<List<GrantedUserGroupVO>> pageGrantedUserGroups(@RequestBody @Valid TrinoResourceGroupGrantPageParam param) {
        return resourceGroupService.pageGrantedUserGroups(param);
    }

    /**
     * trino资源组授权给用户组
     * @param clusterId
     * @param resourceName  资源组名称，多级的用-分隔拼接
     * @param dtUicTenantId
     * @param groupIds
     */
    @RequestMapping(value = "/grantToUserGroup", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_group_project_grant")
    public void grantToUserGroup(@RequestParam Long clusterId,
            @RequestParam("resourceName") String resourceName,
                               @RequestParam("dtUicTenantId") Long dtUicTenantId,
                               @RequestParam("groupIds") List<Long> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            throw new RuntimeException("groupIds不能为空");
        }
        if (dtUicTenantId == null) {
            throw new RuntimeException("dtUicTenantId不能为空");
        }
        resourceGroupService.grantToUserGroup(clusterId,resourceName,dtUicTenantId,groupIds);

    }

    /**
     * 用户组换绑资源组
     * @param resourceId
     * @param dtUicTenantId
     * @param
     */
    @RequestMapping(value = "/reversalUserGroupGrant", method = {RequestMethod.POST})
    public void reversalUserGroupGrant(
            @RequestParam Long clusterId,
                                       @RequestParam("resourceId") Long resourceId,
                                       @RequestParam("dtUicTenantId") Long dtUicTenantId,
                                       @RequestParam("groupId") Long groupId,
            @RequestParam("oldResourceName")String oldResourceName) {

        resourceGroupService.reversalUserGroupGrant(clusterId,resourceId, dtUicTenantId, groupId,oldResourceName);
    }


    @RequestMapping(value="/addOrUpdate", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_group_change_all")
    public void addOrUpdate(@RequestBody @Valid ResourceGroupParam param) {
        resourceGroupService.addOrUpdate(param);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_group_delete_all")
    public void delete(@RequestParam("id") Long id) {
        resourceGroupService.delete(id);
    }

    @RequestMapping(value="/getResourceNames", method = {RequestMethod.POST})
    public List<String> getNames(@RequestBody @Valid QueueParam param) {
        return resourceGroupService.getNames(param.getClusterId(), param.getQueueName());
    }

    @RequestMapping(value = "/getDropDownList", method = {RequestMethod.POST})
    public List<ResourceGroupDropDownVO> getDropDownList(@RequestParam("clusterId") Long clusterId) {
        return resourceGroupService.getDropDownList(clusterId);
    }

    /**
     * 节点标签下拉选框
     * @param clusterId
     * @return
     */
    @RequestMapping(value = "/getDropDownLabelUsers", method = {RequestMethod.POST})
    public List<LabelResourceGroupDTO> getDropDownLabelUsers(@RequestParam("clusterId")Long clusterId) {
        return resourceGroupService.listLabelUserResource(clusterId);
    }

    @RequestMapping(value = "/pageGrantedProjects", method = {RequestMethod.POST})
    public PageResult<List<GrantedProjectVO>> pageGrantedProjects(@RequestBody @Valid ResourceGroupGrantPageParam param) {
        return resourceGroupService.pageGrantedProjects(param);
    }

    /**
     * 展示节点标签资源组授权列表
     * @param param
     * @return
     */
    @RequestMapping(value = "/pageLabelGrantedProjects", method = {RequestMethod.POST})
    public PageResult<List<LabelGrantedProjectDTO>> pageLabelGrantedProjects(@RequestBody ResourceGroupGrantPageParam param) {
        return resourceGroupService.pageLabelGrantedProjects(param);
    }

    @RequestMapping(value = "/grantToProject", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_granted_all",
            tenant = "console_resource_resource_granted_tenant")
    public void grantToProject(@RequestParam("resourceId") Long resourceId,
                               @RequestParam("dtUicTenantId") Long dtUicTenantId,
                               @RequestParam("productType") String productType,
                               @RequestParam("projectId") List<Long> projectIds,
                               @RequestParam("dtToken") String dtToken,
                               @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        judgeAuth(dtUicTenantId, dtToken, isAllAuth);
        grantToProjectForSingleResource(resourceId, dtUicTenantId, productType, projectIds);
    }

    private void judgeAuth(Long dtUicTenantId, String dtToken, Boolean isAllAuth) {
        // 判断权限
        if (!isAllAuth) {
            UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (!tenantIds.contains(dtUicTenantId)) {
                // 改租户不在账号租户下
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
    }

    /**
     * 为单个资源组授权
     * @param resourceId
     * @param dtUicTenantId
     * @param productType
     * @param projectIds
     */
    private void grantToProjectForSingleResource(Long resourceId, Long dtUicTenantId, String productType, List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            throw new RuntimeException("projectId不能为空");
        }

        // 授权全部项目
        if (projectIds.size() == 1 && projectIds.get(0) == -1) {
            resourceGroupService.grantToProject(resourceId, dtUicTenantId, AppType.valueOf(productType), projectIds.get(0));
        } else {
            projectIds.forEach(itemProjectId -> {
                resourceGroupService.grantToProject(resourceId, dtUicTenantId, AppType.valueOf(productType), itemProjectId);
            });
        }
    }

    /**
     * 多个服务器用户授权
     * @param resourceIds
     * @param dtUicTenantId
     * @param productType
     * @param projectIds
     * @param dtToken
     * @param isAllAuth
     */
    @RequestMapping(value = "/dtScriptAgentGrantToProject", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_granted_all",
            tenant = "console_resource_resource_granted_tenant")
    public void dtScriptAgentGrantToProject(@RequestParam("resourceIds") List<Long> resourceIds,
                                               @RequestParam("dtUicTenantId") Long dtUicTenantId,
                                               @RequestParam("productType") String productType,
                                               @RequestParam("projectId") List<Long> projectIds,
                                               @RequestParam("dtToken") String dtToken,
                                               @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        judgeAuth(dtUicTenantId, dtToken, isAllAuth);

        if (CollectionUtils.isEmpty(resourceIds)) {
            throw new RuntimeException("resourceIds不能为空");
        }
        AppType appType = AppType.valueOf(productType);
        if (appType != AppType.RDOS) {
            throw new RuntimeException("only support rdos");
        }
        for (Long resourceId : resourceIds) {
            grantToProjectForSingleResource(resourceId, dtUicTenantId, productType, projectIds);
        }
    }

    @RequestMapping(value = "/checkReversalGrant", method = {RequestMethod.POST})
    public ReversalGrantCheckResultVO checkReversalGrant(@RequestParam("resourceId") Long resourceId,
                                                         @RequestParam("projectId") Long projectId,
                                                         @RequestParam("appType") Integer appType,
                                                         @RequestParam("componentTypeCode")Integer componentTypeCode,
                                                         @RequestParam("dtToken") String dtToken,
                                                         @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        return resourceGroupService.checkReversalGrant(resourceId, projectId, appType, componentTypeCode);
    }

    @RequestMapping(value = "/dtScriptAgentCheckReversalGrant", method = {RequestMethod.POST})
    public LabelReversalGrantCheckResultDTO dtScriptAgentCheckReversalGrant(@RequestParam("resourceId") Long resourceId,
                                                                            @RequestParam("projectId") Long projectId,
                                                                            @RequestParam("appType") Integer appType,
                                                                            @RequestParam("componentTypeCode")Integer componentTypeCode,
                                                                            @RequestParam("dtToken") String dtToken,
                                                                            @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        return resourceGroupService.dtScriptAgentCheckReversalGrant(resourceId, projectId, appType, componentTypeCode);
    }

    // dtScript 组件可复用
    @RequestMapping(value = "/reversalGrant", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_granted_all",
            tenant = "console_resource_resource_granted_tenant")
    public void reversalGrant(@RequestParam("resourceId") Long resourceId,
                              @RequestParam("projectId") Long projectId,
                              @RequestParam("handOver") Long handOver,
                              @RequestParam("appType") Integer appType,
                              @RequestParam("componentTypeCode")Integer componentTypeCode) {
        resourceGroupService.reversalGrant(resourceId, projectId, handOver, appType, componentTypeCode);
    }


    @RequestMapping(value = "/listUnBindUserGroups", method = {RequestMethod.POST})
    public List<GroupVO> listUnBindUserGroups(@RequestParam ("resourceName") String resourceName,
                                              @RequestParam("dtUicTenantId") Long dtUicTenantId){

        return resourceGroupService.listUnBindUserGroups(resourceName,dtUicTenantId);
    }

    @RequestMapping(value = "/getTrinoOverView", method = {RequestMethod.POST})
    public TrinoOverView getTrinoOverView(@RequestParam ("clusterId") Long clusterId,
                                          @RequestParam("dtUicTenantId") Long dtUicTenantId){

        return resourceGroupService.getTrinoOverView(clusterId,dtUicTenantId);
    }


    /**
     * 用户组换绑资源组时，获取用户组可以绑定的trino资源组
     * @param clusterId
     * @param resourceName
     * @param dtuicTenantId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/getCanBindTrinoResources", method = {RequestMethod.POST})
    public List<ResourceGroupListVO> getCanBindTrinoResources(@RequestParam ("clusterId") Long clusterId,
                                                          @RequestParam("resourceName" ) String resourceName,
                                                          @RequestParam ("dtuicTenantId") Long dtuicTenantId,
                                                          @RequestParam("groupId" ) Long groupId){

        return resourceGroupService.getCanBindTrinoResources(clusterId,resourceName,groupId,dtuicTenantId);
    }

}
