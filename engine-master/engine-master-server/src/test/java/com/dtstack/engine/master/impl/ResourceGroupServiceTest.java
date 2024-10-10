package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.param.ResourceGroupGrantPageParam;
import com.dtstack.engine.api.param.ResourceGroupParam;
import com.dtstack.engine.master.mapstruct.ResourceGroupStructImpl;
import com.dtstack.engine.master.mockcontainer.impl.ResourceGroupServiceMock;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-27 21:41
 */
@MockWith(ResourceGroupServiceMock.class)
public class ResourceGroupServiceTest {
    ResourceGroupService rgService = new ResourceGroupService();

    @Before
    public void before() {
        PrivateAccessor.set(rgService, "resourceGroupStruct", new ResourceGroupStructImpl());
    }

    @Test
    public void pageQuery() {
        rgService.pageQuery(-1L, null, 1, 10);
    }

    @Test
    public void addOrUpdate() {
        ResourceGroupParam param = new ResourceGroupParam();
        param.setId(1L);
        param.setName("xxx");
        rgService.addOrUpdate(param);
    }

    @Test
    public void getNames() {
        rgService.getNames(-1L, "default");
    }

    @Test
    public void getDropDownList() {
        rgService.getDropDownList(-1L);
    }

    @Test
    public void grantToProject() {
        rgService.grantToProject(1L, -1L, AppType.RDOS, 2L);
        rgService.grantToProject(1L, -1L, AppType.RDOS, -1L);
    }

    @Test
    public void grantToProjects() {
        rgService.grantToProject(1L, -1L, AppType.RDOS, -1L);
    }

    @Test
    public void pageGrantedProjects() {
        ResourceGroupGrantPageParam param = new ResourceGroupGrantPageParam();
        param.setTenantName("default");
        param.setProjectName("default");
        rgService.pageGrantedProjects(param);
    }

    @Test
    public void isAccessedByProject() {
        rgService.isAccessedByProject(1L, AppType.RDOS.getType(), 1L);
    }

    @Test
    public void listAccessedResourceDetails() {
        rgService.listAccessedResourceDetails(1L, AppType.RDOS.getType());
    }

    @Test
    public void getHandOver() {
        rgService.getHandOver(Lists.newArrayList(1L), 1, 3L);
    }

    @Test
    public void listAccessedResources() {
        rgService.listAccessedResources(1L, 1, null);
    }

    @Test
    public void reversalGrant() {
        rgService.reversalGrant(1L, 1L, 1L, 1, null);
    }

    @Test
    public void delete() {
        rgService.delete(1L);
    }

    @Test
    public void checkReversalGrant() {
        rgService.checkReversalGrant(1L, 1L, 1, null);
    }

    @Test
    public void getResourceUsedMonitor() {
        rgService.getResourceUsedMonitor(1L);
    }

    @Test
    public void batchInsert() {
        // rgService.batchInsert();
    }

    @Test
    public void getResourceGroup() {
        // rgService.getResourceGroup()
    }

    @Test
    public void clearByTimeInterval() {
        // rgService.clearByTimeInterval();
    }

    @Test
    public void getGroupInfo() {
        rgService.getGroupInfo(Lists.newArrayList(1L));
    }

    @Test
    public void fillTaskGroupInfo() {
        rgService.fillTaskGroupInfo(Lists.newArrayList(1L), Collections.emptyList());
    }

    @Test
    public void getResourceGroupByDtUicTenantId() {
        rgService.getResourceGroupByDtUicTenantId(1L);
    }

    @Test
    public void batchChangeResource() {
        rgService.batchChangeResource(1L, 1, Lists.newArrayList(1L), 1L);
    }
}