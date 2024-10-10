package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.AccessedResourceGroupVO;
import com.dtstack.engine.api.vo.HandOverVO;
import com.dtstack.engine.api.vo.ResourceGroupDetailVO;
import com.dtstack.engine.api.vo.ResourceGroupListVO;
import com.dtstack.engine.api.vo.ResourceGroupUsedVO;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.mapstruct.ResourceGroupStruct;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ResourceGroup;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@MockWith(ResourceGroupSdkControllerMock.class)
public class ResourceGroupSdkControllerTest {
    ResourceGroupSdkController resourceGroupSdkController = new ResourceGroupSdkController();

    @Test
    public void testGetResourceUsedMonitor() throws Exception {
        resourceGroupSdkController.getResourceUsedMonitor(1L);
    }

    @Test
    public void testIsAccessedByProject() throws Exception {
        resourceGroupSdkController.isAccessedByProject(1L, 0, 1L);
    }

    @Test
    public void testListAccessedResources() throws Exception {
        resourceGroupSdkController.listAccessedResources(0, 1L);
    }

    @Test
    public void testGetHandOver() throws Exception {
        resourceGroupSdkController.getHandOver(Collections.singletonList(1L), 0, 1L);
    }

    @Test
    public void testGetResourceGroup() throws Exception {
//        resourceGroupSdkController.getResourceGroup(1L);
    }

    @Test
    public void testGetDefault() throws Exception {
        resourceGroupSdkController.getDefault(1L);
    }

    @Test
    public void testBatchChangeResource() throws Exception {
        resourceGroupSdkController.batchChangeResource(1L, Collections.singletonList(1L), 1, 1L);
    }
}

class ResourceGroupSdkControllerMock {
    @MockInvoke(targetClass = ResourceGroupService.class)
    public void batchChangeResource(Long resourceId, Integer appType, List<Long> taskIds, Long dtUicTenantId) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public AccessedResourceGroupVO<ResourceGroupDetailVO> listAccessedResourceDetails(Long projectId, Integer appType) {
        return null;
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public boolean isAccessedByProject(Long resourceId, Integer appType, Long projectId) {
        return false;

    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public ResourceGroup getResourceGroupByDtUicTenantId(Long dtUicTenantId) {
        return new ResourceGroup();

    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    public ClusterTenant getByDtuicTenantId(@Param("dtUicTenantId") Long dtUicTenantId) {
        ClusterTenant clusterTenant = new ClusterTenant();
        clusterTenant.setClusterId(1L);
        clusterTenant.setDefaultResourceId(1L);
        return clusterTenant;
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public Optional<ResourceGroup> getResourceGroup(Long resourceId) {
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setId(1L);
        resourceGroup.setClusterId(1L);
        resourceGroup.setIsDeleted(0);
        return Optional.ofNullable(resourceGroup);
    }

    @MockInvoke(targetClass = ResourceGroupStruct.class)
    public ResourceGroupListVO toListVO(ResourceGroup resourceGroup) {
        return new ResourceGroupListVO();
    }


    @MockInvoke(targetClass = ResourceGroupService.class)
    public List<HandOverVO> getHandOver(List<Long> oldResourceIds, Integer appType, Long projectId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public ResourceGroupUsedVO getResourceUsedMonitor(Long resourceId) {
        return null;
    }
}