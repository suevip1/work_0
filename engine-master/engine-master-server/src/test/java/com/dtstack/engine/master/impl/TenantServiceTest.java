package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.api.param.ClusterTenantResourceBindingParam;
import com.dtstack.engine.master.mapstruct.TenantStructImpl;
import com.dtstack.engine.master.mockcontainer.impl.TenantServiceMock;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-29 17:55
 */
@MockWith(TenantServiceMock.class)
public class TenantServiceTest {

    TenantService tenantService = new TenantService();

    @Before
    public void before() {
        PrivateAccessor.set(tenantService, "tenantStruct", new TenantStructImpl());
    }

    @Test
    public void pageQuery() {
        tenantService.pageQuery(-1L, null, "default", 1, 10);
    }


    @Test
    public void listEngineTenant() {
        tenantService.listEngineTenant(-1L, null);
    }

    @Test
    public void listClusterTenantVOSInTenantId() {
        tenantService.listClusterTenantVOSInTenantId(-1L);
    }

    @Test
    public void getClusterIdByDtUicTenantId() {
        tenantService.getClusterIdByDtUicTenantId(-1L);
    }

    @Test
    public void listCheckedTenants() {
        tenantService.listCheckedTenants("default");
    }

    @Test
    public void listTenant() {
        tenantService.listTenant("xxx");
    }

    @Test
    public void bindingTenant() throws Exception {
        tenantService.bindingTenant(99L, -1L, 1L, "dtToken", "namespace");
    }

    @Test
    public void bindingResource() {
        String taskTypeResourceJson = "[{\"taskType\":\"1\",\"resourceParams\":\"10\"}]";
        tenantService.bindingResource(1L, -1L, taskTypeResourceJson);
    }

    @Test
    public void bindingTenantWithResource() throws Exception {
        ClusterTenantResourceBindingParam p = new ClusterTenantResourceBindingParam();
        p.setResourceId(1L);
        p.setClusterId(-1L);
        p.setTenantId(99L);
        p.setNamespace("namespace");
        tenantService.bindingTenantWithResource(p, null);
    }

    @Test
    public void checkClusterCanUse() throws Exception {
        tenantService.checkClusterCanUse("default", -1L);
    }

    @Test
    public void updateTenantResource() {
        tenantService.updateTenantResource(-1L, -1L, 1L, "default");
    }

    @Test
    public void updateTenantQueue() {
        tenantService.updateTenantQueue(-1L, -1L, 3L);
    }

    @Test
    public void bindingQueue() {
        String taskTypeResourceJson = "[{\"taskType\":\"1\",\"resourceParams\":\"10\"}]";
        tenantService.bindingQueue(1L, -1L, taskTypeResourceJson);
    }

    @Test
    public void updateTenantTaskResource() {
        String taskTypeResourceJson = "[{\"taskType\":\"1\",\"resourceParams\":\"10\"}]";
        tenantService.updateTenantTaskResource(-1L, taskTypeResourceJson);
    }


    @Test
    public void queryResourceLimitByTenantIdAndTaskType() {
        // tenantService.queryResourceLimitByTenantIdAndTaskType(-1L, null);
    }

    @Test
    public void deleteTenantId() {
        // tenantService.deleteTenantId(-1L);
    }

    @Test
    public void listBoundedTenants() {
        tenantService.listBoundedTenants(-1L,Lists.newArrayList());
    }

    @Test
    public void listAllTenantByDtUicTenantIds() {
        tenantService.listAllTenantByDtUicTenantIds(Lists.newArrayList(-1L));
    }

    @Test
    public void getTenant() {
        tenantService.getTenant(-1L);
    }

    @Test
    public void getTenantName() {
        tenantService.getTenantName(-1L);
    }

    @Test
    public void getTenantByFullName() {
        tenantService.getTenantByFullName("default");
    }

    @Test
    public void findUicTenantIdsByClusterId() {
        tenantService.findUicTenantIdsByClusterId(-1L);
    }

    @Test
    public void findCacheTenant() {
        tenantService.findCacheTenant(-1L);
    }

    @Test
    public void hasBindTenants() {
        tenantService.hasBindTenants(-1L);
    }
}