package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ClusterTenantResourceBindingParam;
import com.dtstack.engine.api.vo.ClusterTenantResourceVO;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.TenantNameVO;
import com.dtstack.engine.api.vo.tenant.TenantResourceVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mapstruct.TenantStruct;
import com.dtstack.engine.master.vo.CheckedTenantVO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(TenantControllerTest.TenantControllerMock.class)
public class TenantControllerTest {

    private static final TenantController tenantController = new TenantController();

    static class TenantControllerMock {

        @MockInvoke(targetClass = TenantService.class)
        public PageResult<List<ClusterTenantVO>> pageQuery(Long clusterId,
                                                           Integer engineType,
                                                           String tenantName,
                                                           int pageSize,
                                                           int currentPage){

            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = TenantStruct.class)
        List<EngineTenantVO> toClusterTenantVO(List<ClusterTenantVO> engineTenantVO) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = TenantService.class)
        public List<UserTenantVO> listTenant(String dtToken) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = TenantService.class)
        public PageResult<List<ClusterTenantResourceVO>> pageQueryWithResource(Long clusterId,
                                                                               Integer engineType,
                                                                               String tenantName,
                                                                               int pageSize,
                                                                               int currentPage){
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = TenantService.class)
        public List<TenantNameVO> listBoundedTenants(Long clusterId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = TenantService.class)
        public boolean hasBindTenants(Long clusterId) {
            return true;
        }

        @MockInvoke(targetClass = ClusterService.class)
        public boolean hasRangerAndLdap(Long clusterId) {
            return true;
        }

        @MockInvoke(targetClass = TenantService.class)
        public void bindingTenant( Long dtUicTenantId,  Long clusterId,
                                   Long queueId,  String dtToken,String namespace) throws Exception {}


        @MockInvoke(targetClass = TenantService.class)
        public void bindingResource(Long resourceId, Long dtUicTenantId,String taskTypeResourceJson) {}

        @MockInvoke(targetClass = TenantService.class)
        public void bindingQueue( Long queueId,
                                  Long dtUicTenantId,String taskTypeResourceJson) {}

        @MockInvoke(targetClass = TenantService.class)
        public List<TenantResourceVO> queryTaskResourceLimits(Long dtUicTenantId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = TenantService.class)
        public String queryResourceLimitByTenantIdAndTaskType(Long dtUicTenantId, Integer taskType) {
            return "";
        }

        @MockInvoke(targetClass = TenantService.class)
        public void bindingTenantWithResource(ClusterTenantResourceBindingParam param,String dtToken) throws Exception {}

        @MockInvoke(targetClass = TenantService.class)
        public List<TenantNameVO> findCacheTenant(Long clusterId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ComponentService.class)
        public Long addOrUpdateNamespaces(Long clusterId, String namespace, Long queueId, Long dtUicTenantId) {
            return 1L;
        }

        @MockInvoke(targetClass = TenantService.class)
        public List<CheckedTenantVO> listCheckedTenants(String tenantName) {
            return new ArrayList<>();
        }
    }

    @Test
    public void pageQuery() {
        tenantController.pageQuery(1L,1,"xx",1,1);
    }


    @Test
    public void listEngineTenant() {
        tenantController.listTenant("dsd");
    }

    @Test
    public void listTenants() {
        tenantController.listTenants("dsd");
    }

    @Test
    public void listBoundedTenants() {
        tenantController.listBoundedTenants(1L,"",true);
    }

    @Test
    public void hasBindTenants() {
        tenantController.hasBindTenants(1L);
    }

    @Test
    public void canShowLdapTip() {
        tenantController.canShowLdapTip(1L);
    }

    @Test
    public void listTenant() {
        tenantController.listTenant("sds");
    }

    @Test
    public void bindingTenant() throws Exception {
        tenantController.bindingTenant(1L,1L,1L,"","");
    }

    @Test
    public void bindingResource() {
        tenantController.bindingResource(1L,1L,"","",true);
    }

    @Test
    public void bindingQueue() {
        tenantController.bindingQueue(1L,1L,"");
    }

    @Test
    public void queryResourceLimitByTenantIdAndTaskType() {
        tenantController.queryResourceLimitByTenantIdAndTaskType(1L,1);
    }

    @Test
    public void bindNamespace() {
        tenantController.bindNamespace(1L,",",1L,1L);
    }

    @Test
    public void bindingTenantWithResource() throws Exception {
        tenantController.bindingTenantWithResource(new ClusterTenantResourceBindingParam(),"")
        ;
    }

    @Test
    public void findCacheTenant() throws Exception {
        tenantController.findCacheTenant(1L);
    }
}