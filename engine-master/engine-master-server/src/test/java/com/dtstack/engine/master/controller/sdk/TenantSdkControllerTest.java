package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.master.impl.TenantService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@MockWith(TenantSdkControllerMock.class)
public class TenantSdkControllerTest {
    TenantSdkController tenantSdkController = new TenantSdkController();

    @Test
    public void testClusterTenantPageQuery() throws Exception {
        tenantSdkController.clusterTenantPageQuery(1L, "tenantName", 10, 10);
    }

    @Test
    public void testListClusterTenant() throws Exception {
        tenantSdkController.listClusterTenant(1L);
    }
}

class TenantSdkControllerMock {

    @MockInvoke(targetClass = TenantService.class)
    public List<ClusterTenantVO> listClusterTenantVOSInTenantId(Long dtuicTenantId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = TenantService.class)
    public PageResult<List<ClusterTenantVO>> pageQuery(Long clusterId,
                                                       Integer engineType,
                                                       String tenantName,
                                                       int pageSize,
                                                       int currentPage) {
        return PageResult.EMPTY_PAGE_RESULT;
    }
}