package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.resource.ResourceGroupQueryParam;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.resource.ResourceGroupVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.mapstruct.ResourceGroupStruct;
import com.dtstack.engine.master.utils.CheckParamUtils;
import com.dtstack.dtcenter.common.enums.Deleted;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/node/sdk/resource"})
@Api(value = "/node/sdk/resource", tags = {"资源组接口"})
public class ResourceGroupSdkController {

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private ResourceGroupStruct resourceGroupStruct;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @RequestMapping(value = "/getResourceUsedMonitor", method = {RequestMethod.POST})
    public ResourceGroupUsedVO getResourceUsedMonitor(@RequestParam("resourceId") Long resourceId) {
        return resourceGroupService.getResourceUsedMonitor(resourceId);
    }

    @RequestMapping(value = "/isAccessedByProject", method = {RequestMethod.POST})
    public boolean isAccessedByProject(@RequestParam("resourceId") Long resourceId, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId) {
        return resourceGroupService.isAccessedByProject(resourceId, appType, projectId);
    }

    @RequestMapping(value = "/listAccessedResources", method = {RequestMethod.POST})
    public AccessedResourceGroupVO<ResourceGroupDetailVO> listAccessedResources(@RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId) {
        return resourceGroupService.listAccessedResourceDetails(projectId, appType);
    }

    @RequestMapping(value = "/getHandOver", method = {RequestMethod.POST})
    public List<HandOverVO> getHandOver(@RequestParam("oldResourceIds") List<Long> oldResourceIds, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId) {
        return resourceGroupService.getHandOver(oldResourceIds, appType, projectId);
    }

    @RequestMapping(value = "/getResourceGroup", method = {RequestMethod.POST})
    public ResourceGroupVO getResourceGroup(@RequestParam("resourceId") Long resourceId) {
        Optional<ResourceGroup> resourceGroup = resourceGroupService.getResourceGroup(resourceId);
        return resourceGroup.map(group -> resourceGroupStruct.toVO(group)).orElse(null);
    }

    @PostMapping(value = "/default")
    public ResourceGroupListVO getDefault(@RequestParam("dtUicTenantId") Long dtUicTenantId){
        ResourceGroup group = resourceGroupService.getResourceGroupByDtUicTenantId(dtUicTenantId);
        return resourceGroupStruct.toListVO(group);
    }

    @PostMapping(value = "/batchChangeResource")
    public void batchChangeResource(@RequestParam("dtUicTenantId") @NotNull Long dtUicTenantId, @RequestParam("taskIds") @NotNull List<Long> taskIds,
                                                   @RequestParam("appType") @NotNull Integer appType, @RequestParam("resourceId") @NotNull Long resourceId) {
        CheckParamUtils.checkAppType(appType);
        CheckParamUtils.checkDtUicTenantId(dtUicTenantId);
        Optional<ResourceGroup> resourceGroupOptional = resourceGroupService.getResourceGroup(resourceId);
        if (!resourceGroupOptional.isPresent() || Deleted.DELETED.getStatus().equals(resourceGroupOptional.get().getIsDeleted())) {
            throw new RdosDefineException(ErrorCode.RESOURCE_GROUP_NOT_FOUND);
        }
        ResourceGroup resourceGroup = resourceGroupOptional.get();
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(dtUicTenantId);
        if (clusterTenant == null) {
            throw new RdosDefineException(ErrorCode.TENANT_NOT_BIND);
        }
        Long clusterId = clusterTenant.getClusterId();
        if (!resourceGroup.getClusterId().equals(clusterId)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_CHANGE_RESOURCE);
        }
        resourceGroupService.batchChangeResource(resourceId,appType,taskIds,dtUicTenantId);
    }

    /******************************************** dtscript agent 资源组接口 **************************************/

    /**
     * 查询项目已授权的节点标签
     * @param resourceGroupQueryParam
     * @return
     */
    @RequestMapping(value = "/listAccessedLabelResources", method = {RequestMethod.POST})
    public AccessedResourceGroupVO<ResourceGroupLabelDetailVO> listAccessedLabelResources(@RequestBody ResourceGroupQueryParam resourceGroupQueryParam) {
        return resourceGroupService.listAccessedLabelResources(resourceGroupQueryParam);
    }

    /**
     * 设置项目级默认节点标签
     * @param resourceGrantId
     * @param isProjectDefault
     * @return
     */
    @RequestMapping(value = "/changeProjectDefaultResource", method = {RequestMethod.POST})
    public Boolean changeProjectDefaultResource(@RequestParam("resourceGrantId") Long resourceGrantId, @RequestParam("isProjectDefault") @NotNull Integer isProjectDefault) {
        return resourceGroupService.changeProjectDefaultResource(resourceGrantId, isProjectDefault);
    }

    /**
     * 根据 ID 查询节点标签
     * @param resourceIds
     * @return
     */
    @RequestMapping(value = "/listLabelResourceByIds", method = {RequestMethod.POST})
    public AccessedResourceGroupVO<ResourceGroupLabelDetailVO> listLabelResourceByIds(@RequestParam("resourceIds") List<Long> resourceIds) {
        return resourceGroupService.listLabelResourceByIds(resourceIds);
    }

    /**
     * 根据节点标签和用户名查询是否具有授权的标签资源组
     * @param resourceGroupQueryParam
     * @return
     */
    @RequestMapping(value = "/findAccessedByLabelAndUser", method = {RequestMethod.POST})
    public ResourceGroupLabelDetailVO findAccessedByLabelAndUser(@RequestBody ResourceGroupQueryParam resourceGroupQueryParam) {
        if (StringUtils.isBlank(resourceGroupQueryParam.getLabel())
                || StringUtils.isBlank(resourceGroupQueryParam.getUserName())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        return resourceGroupService.findAccessedByLabelAndUser(resourceGroupQueryParam);
    }
}
