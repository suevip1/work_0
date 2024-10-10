package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pojo.lineage.ComponentMultiTestResult;
import com.dtstack.engine.api.vo.ClusterTenantResourceVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.DataSourceService;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.router.login.domain.UserTenant;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.TenantResource;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.SearchTenantParam;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.UserTenantRelParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-29 19:56
 */
public class TenantServiceMock extends BaseMock {

    @MockInvoke(targetClass = TenantResourceDao.class)
    public Integer deleteByTenantIdAndTaskType(@Param("dtUicTenantId") Long dtUicTenantId, @Param("taskType") Integer taskType) {
        return 1;
    }


    @MockInvoke(targetClass = ConsoleCache.class)
    public void publishRemoveMessage(String tenantId) {
        return;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Integer hasBindTenants(Long clusterId) {
        if (clusterId.equals(1L)) {
            return 1;
        }
        return 0;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Integer updateQueueId(Long dtUicTenantId, Long clusterId, Long queueId) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Integer updateResourceId(Long dtUicTenantId, Long clusterId, Long resourceId, Long queueId) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<ClusterTenant> generalQuery(PageQuery<Object> query, Long clusterId, List<Long> dtUicTenantId) {
        ClusterTenant ct = new ClusterTenant();
        ct.setClusterId(clusterId);
        ct.setDtUicTenantId(-1L);
        ct.setDefaultResourceId(1L);
        return Lists.newArrayList(ct);
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Integer generalCount(Long clusterId, List<Long> dtUicTenantId) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getOne(Long clusterId) {
        return ClusterServiceMock.mockDefaultCluster();
    }

    @MockInvoke(targetClass = UicTenantApiClient.class)
    ApiResponse<List<TenantDeletedVO>> getByDtUicTenantIds(SearchTenantParam param) {
        TenantDeletedVO vo = new TenantDeletedVO();
        vo.setTenantId(param.getTenantId());
        vo.setTenantName(param.getTenantName());
        vo.setDeleted(false);
        ApiResponse<List<TenantDeletedVO>> result = new ApiResponse<>();
        result.setData(Lists.newArrayList(vo));
        return result;
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public void grantToProjects(Long resourceId, List<Long> projectIds, Integer appType, Long dtuicTenantId) {
        return;
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public List<ProjectNameVO> listProjects(AppType type, Long uicTenantId) {
        ProjectNameVO vo = new ProjectNameVO();
        vo.setName("default");
        vo.setAppType(1);
        vo.setProjectId(1L);
        return Lists.newArrayList(vo);
    }

    @MockInvoke(targetClass = TenantResourceDao.class)
    List<TenantResource> selectByUicTenantId(Long dtUicTenantId) {
        TenantResource t = new TenantResource();
        t.setDtUicTenantId(-1);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = TenantResourceDao.class)
    Integer insert(TenantResource tenantResource) {
        return 1;
    }

    @MockInvoke(targetClass = TenantResourceDao.class)
    Integer delete(@Param("dtUicTenantId") Long dtUicTenantId) {
        return 1;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    ResourceGroup getOne(Long id, Integer isDeleted) {
        ResourceGroup g = new ResourceGroup();
        g.setQueuePath("default");
        g.setId(id);
        g.setClusterId(-1L);
        return g;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    List<ResourceGroupDetail> listDetailByIds(Collection<Long> resourceIds) {
        ResourceGroupDetail t = new ResourceGroupDetail();
        t.setCapacity("2");
        t.setMaxCapacity("20");
        t.setQueuePath("default");
        t.setResourceId(1L);
        t.setClusterId(-1L);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = DtUicUserConnect.class)
    public List<UserTenant> getUserTenants(String url, String token, String tenantName) {
        UserTenant ut = new UserTenant();
        ut.setTenantId(-1L);
        ut.setTenantName(tenantName);
        return Lists.newArrayList(ut);
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<Long> listBoundedTenants(Long clusterId) {
        return Lists.newArrayList(-1L);
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<Long> listAllDtUicTenantId() {
        return Lists.newArrayList(-1L, 2L);
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<ClusterTenant> listEngineTenant(Long clusterId) {
        ClusterTenant t = new ClusterTenant();
        t.setDtUicTenantId(-1L);
        t.setQueueId(1L);
        t.setClusterId(clusterId);
        t.setDefaultResourceId(1L);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Long getClusterIdByDtUicTenantId(Long dtUicTenantId) {
        if (dtUicTenantId.equals(99L)) {
            return null;
        }
        return -1L;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public Long addOrUpdateNamespaces(Long clusterId, String namespace, Long queueId, Long dtUicTenantId) {
        return 1L;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void syncRelationToRangerIfNeeded(Long clusterId, Component component, Long dtUicTenantId) {
        return;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void bindLdapIfNeeded(Long clusterId, final Set<Long> dtUicTenantIds) {
        return;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void publishSqlComponent(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {
        return;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listByClusterId(Long clusterId, Integer typeCode, boolean isDefault) {
        return ComponentServiceMock.mockDefaultComponents();
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Integer insert(ClusterTenant engineTenant) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<ClusterTenantResourceVO> generalQueryWithResource(PageQuery<Object> query,
                                                           Long clusterId,
                                                           List<Long> dtUicTenantId) {
        ClusterTenantResourceVO vo = new ClusterTenantResourceVO();
        vo.setTenantName("default");
        vo.setTenantId(-1L);
        vo.setResourceId(1L);
        return Lists.newArrayList(vo);
    }

    @MockInvoke(targetClass = ComponentService.class)
    public List<ComponentMultiTestResult> testConnects(String clusterName, Long dtuicTenantId) {
        ComponentMultiTestResult t = new ComponentMultiTestResult(EComponentType.YARN.getTypeCode());
        t.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        t.setResult(true);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = QueueDao.class, targetMethod = "getOne")
    Queue getOneOfQueue(Long id) {
        Queue queue = new Queue();
        queue.setQueueName("default");
        queue.setQueuePath("default");
        queue.setParentQueueId(1L);
        return queue;
    }

    @MockInvoke(targetClass = QueueDao.class)
    Integer countByParentQueueId(Long parentQueueId) {
        if (parentQueueId.equals(3L)) {
            return 0;
        }
        return 0;
    }

    @MockInvoke(targetClass = QueueDao.class)
    Queue getByClusterIdAndQueuePath(Long clusterId, String queuePath) {
        Queue q = new Queue();
        q.setParentQueueId(null);
        q.setClusterId(clusterId);
        q.setQueuePath("default");
        q.setQueueName("default");
        return q;
    }

    @MockInvoke(targetClass = QueueDao.class)
    List<Queue> listByIds(List<Long> ids) {
        Queue q = new Queue();
        q.setQueueName("default");
        q.setQueuePath("default");
        q.setClusterId(-1L);
        return Lists.newArrayList(q);
    }

    @MockInvoke(targetClass = UicTenantApiClient.class)
    ApiResponse<UICTenantVO> findTenantById(Long tenantId) {
        UICTenantVO vo = new UICTenantVO();
        vo.setTenantId(tenantId);
        vo.setTenantName("default");
        ApiResponse response = new ApiResponse();
        response.setData(vo);
        return response;
    }

    @MockInvoke(targetClass = UicTenantApiClient.class)
    ApiResponse<List<UserFullTenantVO>> getFullByTenantName(UserTenantRelParam param) {
        UserFullTenantVO vo = new UserFullTenantVO();
        vo.setTenantName(param.getTenantName());
        vo.setTenantId(-1L);
        List<UserFullTenantVO> list = Lists.newArrayList(vo);
        ApiResponse<List<UserFullTenantVO>> result = new ApiResponse<>();
        result.setData(list);
        return result;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void publishComponent(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {

    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getPublicServiceNode() {
        return "127.0.0.1";
    }

    }
