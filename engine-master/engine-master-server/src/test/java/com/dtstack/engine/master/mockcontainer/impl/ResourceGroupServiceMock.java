package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.ResourceGroupGrantDao;
import com.dtstack.engine.dao.ResourceHandOverDao;
import com.dtstack.engine.dao.ResourceQueueUsedDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.GrantedProjectDTO;
import com.dtstack.engine.dto.ResourceGroupGrantSearchDTO;
import com.dtstack.engine.dto.TimeUsedNode;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.impl.QueueService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ResourceGroupGrant;
import com.dtstack.engine.po.ResourceHandOver;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-27 21:41
 */
public class ResourceGroupServiceMock {

    @MockInvoke(targetClass = ResourceHandOverDao.class)
    public void deleteByIds(@Param("ids") List<Long> ids) {

    }

    @MockInvoke(targetClass = ConsoleService.class)
    public ClusterResource clusterResources(String clusterName, Map<Integer, String> componentVersionMap, Long dtuicTenantId) {
        ClusterResource clusterResource = new ClusterResource();
        List<JSONObject> queues = JSONArray.parseArray("[\n" +
                "      {\n" +
                "        \"hideReservationQueues\": false,\n" +
                "        \"numPendingApplications\": 0,\n" +
                "        \"intraQueuePreemptionDisabled\": true,\n" +
                "        \"type\": \"capacitySchedulerLeafQueueInfo\",\n" +
                "        \"numActiveApplications\": 6,\n" +
                "        \"capacity\": 100,\n" +
                "        \"pendingContainers\": 0,\n" +
                "        \"absoluteCapacity\": 100,\n" +
                "        \"allocatedContainers\": 8,\n" +
                "        \"absoluteUsedCapacity\": 22.222223,\n" +
                "        \"preemptionDisabled\": true,\n" +
                "        \"capacities\": {\n" +
                "          \"queueCapacitiesByPartition\": [\n" +
                "            {\n" +
                "              \"absoluteCapacity\": 100,\n" +
                "              \"partitionName\": \"\",\n" +
                "              \"absoluteUsedCapacity\": 22.222223,\n" +
                "              \"usedCapacity\": 22.222223,\n" +
                "              \"maxAMLimitPercentage\": 100,\n" +
                "              \"maxCapacity\": 100,\n" +
                "              \"absoluteMaxCapacity\": 100,\n" +
                "              \"capacity\": 100\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"userLimitFactor\": 1,\n" +
                "        \"state\": \"RUNNING\",\n" +
                "        \"nodeLabels\": [\n" +
                "          \"*\"\n" +
                "        ],\n" +
                "        \"userLimit\": 100,\n" +
                "        \"AMResourceLimit\": {\n" +
                "          \"memory\": 73728,\n" +
                "          \"vCores\": 36\n" +
                "        },\n" +
                "        \"absoluteMaxCapacity\": 100,\n" +
                "        \"numApplications\": 6,\n" +
                "        \"defaultPriority\": 0,\n" +
                "        \"usedCapacity\": 22.22,\n" +
                "        \"maxApplicationsPerUser\": 10000,\n" +
                "        \"userAMResourceLimit\": {\n" +
                "          \"memory\": 73728,\n" +
                "          \"vCores\": 36\n" +
                "        },\n" +
                "        \"maxCapacity\": 100,\n" +
                "        \"resources\": {\n" +
                "          \"resourceUsagesByPartition\": [\n" +
                "            {\n" +
                "              \"amLimit\": {\n" +
                "                \"memory\": 73728,\n" +
                "                \"vCores\": 36\n" +
                "              },\n" +
                "              \"partitionName\": \"\",\n" +
                "              \"reserved\": {\n" +
                "                \"memory\": 0,\n" +
                "                \"vCores\": 0\n" +
                "              },\n" +
                "              \"pending\": {\n" +
                "                \"memory\": 0,\n" +
                "                \"vCores\": 0\n" +
                "              },\n" +
                "              \"amUsed\": {\n" +
                "                \"memory\": 11264,\n" +
                "                \"vCores\": 6\n" +
                "              },\n" +
                "              \"userAmLimit\": {\n" +
                "                \"memory\": 73728,\n" +
                "                \"vCores\": 36\n" +
                "              },\n" +
                "              \"used\": {\n" +
                "                \"memory\": 15360,\n" +
                "                \"vCores\": 8\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"resourcesUsed\": {\n" +
                "          \"memory\": 15360,\n" +
                "          \"vCores\": 8\n" +
                "        },\n" +
                "        \"users\": [\n" +
                "          {\n" +
                "            \"maxAMResource\": {\n" +
                "              \"memory\": 73728,\n" +
                "              \"vCores\": 36\n" +
                "            },\n" +
                "            \"numPendingApplications\": 0,\n" +
                "            \"userWeight\": 1,\n" +
                "            \"resources\": {\n" +
                "              \"resourceUsagesByPartition\": [\n" +
                "                {\n" +
                "                  \"amLimit\": {\n" +
                "                    \"memory\": 73728,\n" +
                "                    \"vCores\": 36\n" +
                "                  },\n" +
                "                  \"partitionName\": \"\",\n" +
                "                  \"reserved\": {\n" +
                "                    \"memory\": 0,\n" +
                "                    \"vCores\": 0\n" +
                "                  },\n" +
                "                  \"pending\": {\n" +
                "                    \"memory\": 0,\n" +
                "                    \"vCores\": 0\n" +
                "                  },\n" +
                "                  \"amUsed\": {\n" +
                "                    \"memory\": 11264,\n" +
                "                    \"vCores\": 6\n" +
                "                  },\n" +
                "                  \"userAmLimit\": {\n" +
                "                    \"memory\": 0,\n" +
                "                    \"vCores\": 0\n" +
                "                  },\n" +
                "                  \"used\": {\n" +
                "                    \"memory\": 15360,\n" +
                "                    \"vCores\": 8\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"AMResourceUsed\": {\n" +
                "              \"memory\": 11264,\n" +
                "              \"vCores\": 6\n" +
                "            },\n" +
                "            \"isActive\": false,\n" +
                "            \"maxResource\": {\n" +
                "              \"memory\": 73728,\n" +
                "              \"vCores\": 36\n" +
                "            },\n" +
                "            \"resourcesUsed\": {\n" +
                "              \"memory\": 15360,\n" +
                "              \"vCores\": 8\n" +
                "            },\n" +
                "            \"numActiveApplications\": 6,\n" +
                "            \"userResourceLimit\": {\n" +
                "              \"memory\": 73728,\n" +
                "              \"vCores\": 36\n" +
                "            },\n" +
                "            \"username\": \"admin\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"numContainers\": 8,\n" +
                "        \"maxApplications\": 10000,\n" +
                "        \"usedAMResource\": {\n" +
                "          \"memory\": 11264,\n" +
                "          \"vCores\": 6\n" +
                "        },\n" +
                "        \"queueName\": \"default\",\n" +
                "        \"reservedContainers\": 0\n" +
                "      }\n" +
                "    ]", JSONObject.class);
        clusterResource.setQueues(queues);
        return clusterResource;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Long countByDefaultResourceId(Long resourceId) {
        return 0L;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    ClusterTenant getByDtuicTenantId(Long dtUicTenantId) {
        ClusterTenant t = new ClusterTenant();
        t.setDtUicTenantId(1L);
        t.setClusterId(-1L);
        t.setDtUicTenantId(dtUicTenantId);
        t.setDefaultResourceId(1L);
        t.setQueueId(1L);
        return t;
    }

    @MockInvoke(targetClass = QueueService.class)
    public Queue getQueueByPath(Long clusterId, String queuePath) {
        Queue queue = new Queue();
        queue.setQueuePath(queuePath);
        queue.setClusterId(clusterId);
        queue.setMaxCapacity("20");
        queue.setCapacity("2");
        return queue;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public Cluster getCluster(Long dtUicTenantId) {
        return ClusterServiceMock.mockDefaultCluster();
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public AuthProjectVO finProject(Long projectId, Integer appType) {
        AuthProjectVO vo = new AuthProjectVO();
        vo.setAppType(appType);
        vo.setProjectId(projectId);
        return vo;
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public Table<Integer, Long, AuthProjectVO> getProjectGroupAppType(Map<Integer, List<Long>> appProjectMap) {
        return HashBasedTable.create();
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public List<AuthProjectVO> fullProjectName(Long dtuicTenantId, String projectName, Integer appType) {
        return Collections.emptyList();
    }


    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public List<ProjectNameVO> listProjects(AppType type, Long uicTenantId) {
        ProjectNameVO p = new ProjectNameVO();
        p.setAppType(type.getType());
        p.setProjectId(1L);
        p.setName("default");
        return Lists.newArrayList(p);
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class, targetMethod = "generalQuery")
    List<GrantedProjectDTO> generalQueryOfResourceGroupGrantDao(PageQuery<ResourceGroup> pageQuery) {
        GrantedProjectDTO dto = new GrantedProjectDTO();
        dto.setProjectId(1L);
        dto.setTenantId(1L);
        dto.setAppType(AppType.RDOS.getType());
        return Lists.newArrayList(dto);
    }

    @MockInvoke(targetClass = TenantService.class)
    public List<UserFullTenantVO> getTenantByFullName(String tenantName) {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = TenantService.class)
    public Map<Long, TenantDeletedVO> listAllTenantByDtUicTenantIds(Collection<Long> ids) {
        return Maps.newHashMap();
    }


    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    List<ResourceGroupDetail> listAccessedResourceDetailByProjectId(Long projectId, Integer appType) {
        ResourceGroupDetail t = new ResourceGroupDetail();
        t.setCapacity("2");
        t.setMaxCapacity("20");
        t.setQueuePath("default");
        t.setResourceId(1L);
        t.setClusterId(1L);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    List<ResourceGroupDetail> listAccessedResourceByProjectId(Long projectId, Integer appType) {
        ResourceGroupDetail t = new ResourceGroupDetail();
        t.setCapacity("2");
        t.setMaxCapacity("20");
        t.setQueuePath("default");
        t.setResourceId(1L);
        t.setClusterId(1L);
        return Lists.newArrayList(t);
    }


    @MockInvoke(targetClass = ResourceHandOverDao.class)
    void updateTargetResourceIdByProjectIdAndTargetResourceId(Long projectId, Integer appType,
                                                              Long oldResourceId,
                                                              Long targetResourceId) {
        return;
    }

    @MockInvoke(targetClass = ResourceHandOverDao.class)
    void update(ResourceHandOver resourceHandOver) {
        return;
    }

    @MockInvoke(targetClass = ResourceHandOverDao.class)
    void insert(ResourceHandOver resourceHandOver) {
        return;
    }

    @MockInvoke(targetClass = ResourceHandOverDao.class)
    List<ResourceHandOver> findByProjectIdAndOldResourceIds(Long projectId, Integer appType, List<Long> oldResourceIds) {
        ResourceHandOver r = new ResourceHandOver();
        r.setOldResourceId(2L);
        r.setTargetResourceId(1L);
        r.setProjectId(projectId);
        r.setAppType(appType);
        return Lists.newArrayList(r);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateResourceByTaskIds(Long resourceId, Integer appType, Collection<Long> taskIds, Long dtuicTenantId, List<Integer> statusList) {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateResourceIdByProjectIdAndOldResourceIdAndStatus(Long handOver, Long projectId, Long resourceId, Integer status) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    void updateResourceByTaskIds(Long resourceId, Integer appType, Collection<Long> taskIds, Long dtuicTenantId) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    void updateResourceIdByProjectIdAndOldResourceId(Long resourceId, Long projectId, Long oldResourceId, Integer appType) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    void delete(Long resourceId, Long projectId, Integer appType) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    long countAccessedResourceByProjectId(Long projectId, Integer appType) {
        return 10L;
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    Integer generalCount(ResourceGroupGrantSearchDTO group) {
        return 0;
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    long countByResourceIdAndEngineProjectId(Long resourceId,
                                             Long projectId, Integer appType) {
        if (resourceId.equals(1L) && projectId.equals(1L)) {
            return 1L;
        }
        return 0L;
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    void insert(ResourceGroupGrant grant) {
        return;
    }


    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    void insertAll(List<ResourceGroupGrant> grants) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupGrantDao.class)
    List<Long> listGrantedProjects(Long resourceId, Integer appType) {
        if (resourceId.equals(1L)) {
            return Lists.newArrayList(1L);
        }
        return Collections.emptyList();
    }


    @MockInvoke(targetClass = ResourceGroupDao.class)
    List<ResourceGroupDetail> listDetailByIds(Collection<Long> resourceIds) {
        ResourceGroupDetail t = new ResourceGroupDetail();
        t.setCapacity("2");
        t.setMaxCapacity("20");
        t.setQueuePath("default");
        t.setResourceId(1L);
        t.setClusterId(1L);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    List<ResourceGroupDetail> listDropDownByClusterId(Long clusterId) {
        ResourceGroupDetail t = new ResourceGroupDetail();
        t.setCapacity("2");
        t.setMaxCapacity("20");
        t.setQueuePath("default");
        t.setResourceId(1L);
        t.setClusterId(1L);
        return Lists.newArrayList(t);
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    List<String> listNamesByQueueName(Long clusterId, String queuePath) {
        if (queuePath.equals("default")) {
            return Lists.newArrayList("default");
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    ResourceGroup getOne(Long id, Integer isDeleted) {
        ResourceGroup g = new ResourceGroup();
        if (id.equals(1L)) {
            return g;
        }
        return null;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    void insert(ResourceGroup group) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    void update(ResourceGroup group) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    long countByClusterIdAndName(Long clusterId, String name) {
        if (name.equals("default")) {
            return 1L;
        }
        return 0L;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    List<ResourceGroup> generalQuery(PageQuery<ResourceGroup> pageQuery) {
        return JSONArray.parseArray("[\n" +
                "      {\n" +
                "        \"id\": 739,\n" +
                "        \"name\": \"a1\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 737,\n" +
                "        \"name\": \"a\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 735,\n" +
                "        \"name\": \"test_xasdasd\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"root.jixia_test\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 731,\n" +
                "        \"name\": \"jiuwei_resourceGroup\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 727,\n" +
                "        \"name\": \"xjxjxj\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 687,\n" +
                "        \"name\": \"4_24\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 667,\n" +
                "        \"name\": \"4_19_3\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 665,\n" +
                "        \"name\": \"4_19_2\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 663,\n" +
                "        \"name\": \"4_19test\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"default\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 635,\n" +
                "        \"name\": \"资源组n\",\n" +
                "        \"description\": null,\n" +
                "        \"queuePath\": \"a\"\n" +
                "      }\n" +
                "    ]", ResourceGroup.class);
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    void delete(@Param("id") Long id) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    Integer generalCount(ResourceGroup group) {
        return 1;
    }

    @MockInvoke(targetClass = QueueDao.class)
    Queue getByClusterIdAndQueuePath(Long clusterId, String queuePath) {
        Queue q = new Queue();
        q.setQueuePath("default");
        q.setCapacity("20");
        q.setMaxCapacity("20");
        q.setQueueName("default");
        return q;
    }

    @MockInvoke(targetClass = ResourceQueueUsedDao.class)
    List<TimeUsedNode> listLastTwoDayByClusterIdAndQueueName(Long clusterId,
                                                             String queueName,
                                                             Date start,
                                                             Date stop) {
        TimeUsedNode t = new TimeUsedNode();
        t.setTime(new Date());
        t.setUsed("default");
        return Lists.newArrayList(t);
    }
}
