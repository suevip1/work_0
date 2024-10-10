package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ClusterPageQueryDTO;
import com.dtstack.engine.master.dto.ComponentProxyConfigDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.api.vo.ComponentMultiVersionVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.IComponentVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.impl.*;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.security.KerberosHandler;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleKerberosProject;
import com.dtstack.engine.po.ConsoleSSL;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantApiVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.UserTenantRelParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEFAULT_CLUSTER_ID;
import static com.dtstack.engine.common.constrant.ConfigConstant.DEFAULT_CLUSTER_NAME;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-05-16 22:00
 */
public class ClusterServiceMock extends BaseMock {

    @MockInvoke(targetClass = PluginInfoManager.class)
    public JSONObject buildConsolePluginInfo(com.dtstack.engine.api.domain.Component component,
                                             Integer deployMode,
                                             Map<String, Object> selfExtraParam,
                                             Map<Integer, String> componentVersionMap) {
        return new JSONObject();
    }

    @MockInvoke(targetClass = ClusterDao.class)
    public Integer getPageQueryCount(@Param("model") ClusterPageQueryDTO clusterPageQueryDTO) {
        return 0;
    }


    @MockInvoke(targetClass = UicTenantApiClient.class)
    public ApiResponse<List<UserFullTenantVO>> getFullByTenantName(UserTenantRelParam var1) {
        ApiResponse<List<UserFullTenantVO>> objectApiResponse = new ApiResponse<>();
        objectApiResponse.setSuccess(true);
        objectApiResponse.setData(new ArrayList<>());
        return objectApiResponse;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    List<Cluster> generalQuery(PageQuery<ClusterDTO> pageQuery) {
        List<Cluster> clusterVOS = Lists.newArrayList(mockDefaultCluster());
        return clusterVOS;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Integer generalCount(ClusterDTO clusterDTO) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Integer insertWithId(Cluster cluster) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getByClusterName(String clusterName) {
        if ("default".equals(clusterName)) {
            return mockDefaultCluster();
        }
        return null;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Integer insert(Cluster cluster) {
        cluster.setId(ThreadLocalRandom.current().nextLong());
        return 1;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getOne(@Param("id") Long clusterId) {
        Cluster cluster = new Cluster();
        cluster.setId(clusterId);
        return cluster;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    void deleteCluster(Long clusterId) {
        return;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    List<Cluster> listAll() {
        Cluster cluster = new Cluster();
        cluster.setId(-1L);
        return Lists.newArrayList(cluster);
    }


    @MockInvoke(targetClass = ClusterTenantDao.class)
    Long getClusterIdByDtUicTenantId(Long dtUicTenantId) {
        return dtUicTenantId;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<ClusterTenantVO> listEngineTenant(Long clusterId) {
        if (clusterId.equals(-999L)) {
            return Collections.emptyList();
        }
        ClusterTenantVO clusterTenantVO = new ClusterTenantVO();
        return Lists.newArrayList(clusterTenantVO);
    }


    @MockInvoke(targetClass = ClusterTenantDao.class)
    ClusterTenant getByDtuicTenantId(Long dtUicTenantId) {
        ClusterTenant clusterTenant = new ClusterTenant();
        clusterTenant.setDtUicTenantId(dtUicTenantId);
        clusterTenant.setClusterId(-1L);
        clusterTenant.setDefaultResourceId(-1L);
        return clusterTenant;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<Long> listBoundedTenants(Long clusterId) {
        if (clusterId.equals(-1L)) {
            return Lists.newArrayList(-1L);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ResourceGroupDao.class)
    ResourceGroup getOne(Long id, Integer isDeleted) {
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setIsDeleted(isDeleted);
        resourceGroup.setId(id);
        resourceGroup.setQueuePath("queuePath");
        return resourceGroup;
    }

    @MockInvoke(targetClass = QueueDao.class)
    Queue getByClusterIdAndQueuePath(Long clusterId, String queuePath) {
        Queue queue = new Queue();
        queue.setQueuePath(queuePath);
        queue.setClusterId(clusterId);
        return queue;
    }

    @MockInvoke(targetClass = QueueDao.class)
    List<Queue> listByClusterWithLeaf(List<Long> clusterId) {
        if (CollectionUtils.isNotEmpty(clusterId) && clusterId.contains(-1L)) {
            Queue q1 = new Queue();
            q1.setClusterId(-1L);
            q1.setQueuePath("queuePath");
            return Lists.newArrayList(q1);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getByClusterIdAndComponentType(Long clusterId, Integer type, String componentVersion, Integer deployType) {
        Component component = new Component();
        component.setClusterId(clusterId);
        component.setComponentTypeCode(type);
        component.setHadoopVersion(componentVersion);
        component.setDeployType(deployType);
        return component;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listByClusterId(Long clusterId, Integer typeCode, boolean isDefault) {
        if (clusterId.equals(-1L)) {
            return mockDefaultComponents();
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listComponentByClusterId(List<Long> clusterIds, List<Integer> typeCodes) {
        if (CollectionUtils.isNotEmpty(typeCodes)) {
            List<Component> components = typeCodes.stream().map(s -> {
                Component component = new Component();
                component.setComponentTypeCode(s);
                return component;
            }).collect(Collectors.toList());
            return components;
        }
        if (clusterIds.contains(-1L)) {
            return mockDefaultComponents();
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentDao.class)
    String getDefaultComponentVersionByClusterAndComponentType(Long clusterId, Integer type) {
        return "";
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    List<IComponentVO> getComponentVoByComponent(List<Component> components, boolean isFilter, Long clusterId, boolean multiVersion) {
        List<Component> mockDefaultComponents = mockDefaultComponents();
        Map<Integer, IComponentVO> componentVoMap = new HashMap<>(mockDefaultComponents.size());
        mockDefaultComponents.stream().collect(Collectors.groupingBy(Component::getComponentTypeCode, Collectors.toList()))
                .forEach((k, v) -> componentVoMap.put(k, multiVersion ?
                        ComponentMultiVersionVO.getInstanceWithCapacityAndType(k, v.size()) : ComponentVO.getInstance()));

        List<IComponentVO> componentVoList = new ArrayList<>(mockDefaultComponents.size());
        for (Component component : mockDefaultComponents) {
            IComponentVO customComponent = componentVoMap.get(component.getComponentTypeCode());
            ComponentVO componentVO = IComponentVO.getComponentVo(customComponent, component);
            if (customComponent.multiVersion()) {
                customComponent.addComponent(componentVO);
            }
        }

        componentVoList.addAll(componentVoMap.values());
        return componentVoList;
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public List<IComponentVO> getComponentVo(List<Component> components, boolean multiVersion) {
        List<Component> mockDefaultComponents = mockDefaultComponents();
        Map<Integer, IComponentVO> componentVoMap = new HashMap<>(mockDefaultComponents.size());
        mockDefaultComponents.stream().collect(Collectors.groupingBy(Component::getComponentTypeCode, Collectors.toList()))
                .forEach((k, v) -> componentVoMap.put(k, multiVersion ?
                        ComponentMultiVersionVO.getInstanceWithCapacityAndType(k, v.size()) : ComponentVO.getInstance()));

        List<IComponentVO> componentVoList = new ArrayList<>(mockDefaultComponents.size());
        for (Component component : mockDefaultComponents) {
            IComponentVO customComponent = componentVoMap.get(component.getComponentTypeCode());
            ComponentVO componentVO = IComponentVO.getComponentVo(customComponent, component);
            if (customComponent.multiVersion()) {
                customComponent.addComponent(componentVO);
            }
        }

        componentVoList.addAll(componentVoMap.values());
        return componentVoList;
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public List<ComponentConfig> getComponentConfigListByTypeCodeAndKey(Long clusterId, Integer componentTypeCode, String key) {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> getTenantByTenantId(Long componentId, boolean isFilter) {
        String configJson = "";
        return null;
    }

    @MockInvoke(targetClass = UicTenantApiClient.class)
    ApiResponse<TenantApiVO> getTenantByTenantId(String tenantId, String dtToken) {
        TenantApiVO tenantApiVO = new TenantApiVO();
        tenantApiVO.setTbdsSecureId("tbdsSecureId");
        tenantApiVO.setTbdsSecureKey("tbdsSecureKey");
        tenantApiVO.setTbdsUsername("tbdsUsername");
        ApiResponse<TenantApiVO> resp = new ApiResponse<>();
        resp.setData(tenantApiVO);
        return resp;
    }

    @MockInvoke(targetClass = ComponentService.class)
    ConsoleSSL getSSLConfig(Long clusterId, Integer componentCode, String componentVersion) {
        return new ConsoleSSL();
    }

    @MockInvoke(targetClass = ComponentService.class)
    public String formatVersion(Integer componentCode, String componentVersion) {
        return "";
    }

    @MockInvoke(targetClass = ComponentService.class)
    <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap) {
        return mockGetComponentByClusterId(clusterId, componentType, isFilter, clazz, componentVersionMap, null);
    }

    @MockInvoke(targetClass = ComponentService.class)
    public <T> T getComponentByClusterId(Long componentId, boolean isFilter, Class<T> clazz) {
        return mockGetComponentByClusterId(null, null, isFilter, clazz, null, componentId);
    }

    @MockInvoke(targetClass = ComponentService.class)
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap, Long componentId) {
        return mockGetComponentByClusterId(clusterId, componentType, isFilter, clazz, componentVersionMap, componentId);
    }

    @MockInvoke(targetClass = ComponentService.class)
    public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
        Component component = new Component();
        component.setClusterId(clusterId);
        component.setComponentTypeCode(componentType);
        component.setHadoopVersion(componentVersion);
        return component;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public String buildSftpPath(Long clusterId, Integer componentCode) {
        return "CONSOLE_" + clusterId + File.separator + EComponentType.getByCode(componentCode).name();
    }

    @MockInvoke(targetClass = ComponentService.class)
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String versionName, Integer storeType, Integer deployType) {
        if (EComponentType.YARN.getTypeCode().equals(componentType)) {
            if (StringUtils.isEmpty(versionName)) {
                return "yarn2";
            }
        }
        return null;
    }

    @MockInvoke(targetClass = ConsoleKerberosProjectService.class)
    public ConsoleKerberosProject findKerberosProject(Long projectId, Integer appType) {
        return new ConsoleKerberosProject();
    }


    @MockInvoke(targetClass = ComponentAuxiliaryService.class)
    public ComponentProxyConfigDTO queryOpenAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {
        ComponentProxyConfigDTO proxyConfigDTO = new ComponentProxyConfigDTO();
        return proxyConfigDTO;
    }

    @MockInvoke(targetClass = ComponentAuxiliaryService.class)
    public void removeAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {
        return;
    }

    @MockInvoke(targetClass = KerberosDao.class)
    KerberosConfig getByComponentType(Long clusterId, Integer componentType, String componentVersion) {
        return new KerberosConfig();
    }

    @MockInvoke(targetClass = UserService.class)
    User findByUser(Long dtUicUserId) {
        User user = new User();
        user.setDtuicUserId(dtUicUserId);
        user.setId(dtUicUserId);
        return user;
    }

//    @MockInvoke(targetClass = UserDao.class)
//    User getByDtUicUserId(Long dtUicUserId) {
//        User user = new User();
//        user.setDtuicUserId(-1L);
//        return user;
//    }
//
//    @MockInvoke(targetClass = TenantDao.class)
//    Long getIdByDtUicTenantId(Long dtUicTenantId) {
//        return -1L;
//    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    AccountTenant getByUserIdAndTenantIdAndEngineType(Long userId, Long tenantId, Integer engineType) {
        AccountTenant accountTenant = new AccountTenant();
        accountTenant.setDtuicTenantId(tenantId);
        return accountTenant;
    }

    @MockInvoke(targetClass = AccountDao.class)
    Account getById(Long id) {
        Account account = new Account();
        account.setId(id);
        account.setPassword(Base64Util.baseEncode("abc123"));
        return account;
    }

    @MockInvoke(targetClass = ConsoleFileSyncDao.class)
    int removeByClusterId(Long clusterId) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleFileSyncDetailDao.class)
    int deleteByClusterId(Long clusterId) {
        return 1;
    }

    @MockInvoke(targetClass = KerberosHandler.class)
    public KerberosConfig authentication(Long clusterId, Integer componentCode, String componentVersion, Integer taskType, Long projectId, Integer appType
            , Long userId, Long tenantId, JSONObject sftpConfig) {
        return new KerberosConfig();
    }

    @MockInvoke(targetClass = KerberosHandler.class)
    public void setKerberos(JSONObject pluginJson, KerberosConfig authentication) {

    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public List<String> getSupportTenantConfigComponentType() {
        return new ArrayList<>();
    }

        public static Cluster mockDefaultCluster() {
        Cluster cluster = new Cluster();
        cluster.setId(DEFAULT_CLUSTER_ID);
        cluster.setClusterName(DEFAULT_CLUSTER_NAME);
        cluster.setHadoopVersion("");
        cluster.setIsDeleted(0);
        return cluster;
    }

    public static List<Component> mockDefaultComponents() {
        Component sftp = new Component();
        sftp.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        sftp.setClusterId(-1L);
        sftp.setIsDefault(true);

        Component yarn = new Component();
        yarn.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        yarn.setClusterId(-1L);
        yarn.setIsDefault(true);

        Component hdfs = new Component();
        hdfs.setComponentTypeCode(EComponentType.HDFS.getTypeCode());
        hdfs.setClusterId(-1L);
        hdfs.setIsDefault(true);

        Component flink = new Component();
        flink.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
        flink.setClusterId(-1L);
        flink.setStoreType(EComponentType.HDFS.getTypeCode());
        flink.setHadoopVersion("110");
        flink.setDeployType(EDeployMode.PERJOB.getType());
        flink.setVersionName("1.10");
        flink.setIsDefault(true);
        return Lists.newArrayList(sftp, yarn, hdfs, flink);
    }

    public <T> T mockGetComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap, Long componentId) {
        HashMap<String, Object> result = new HashMap<>();
        if (componentId != null) {
            // flink
            result.put("test", "test");
            result.put(EDeployMode.SESSION.getMode(), new JSONObject());
            if (clazz.isInstance(Map.class)) {
                return (T) result;
            }
            String configStr = JSONObject.toJSONString(result);
            if (clazz.isInstance(String.class)) {
                return (T) configStr;
            }
            return JSONObject.parseObject(configStr, clazz);
        }
        if (EComponentType.getByCode(componentType) == EComponentType.FLINK) {
            JSONObject conf = new JSONObject();
            conf.put(ConfigConstant.NAMESPACE, ConfigConstant.NAMESPACE);
            result.put(EDeployMode.SESSION.getMode(), conf);
        }
        if (EComponentType.getByCode(componentType) == EComponentType.HDFS) {
            JSONObject hadoopConf = new JSONObject();
            hadoopConf.put("dfs.replication", "3");
            result.put(EComponentType.HDFS.getConfName(), hadoopConf);
        }
        if (clazz.isInstance(Map.class)) {
            return (T) result;
        }
        String configStr = JSONObject.toJSONString(result);
        if (clazz.isInstance(String.class)) {
            return (T) configStr;
        }
        return JSONObject.parseObject(configStr, clazz);
    }

}
