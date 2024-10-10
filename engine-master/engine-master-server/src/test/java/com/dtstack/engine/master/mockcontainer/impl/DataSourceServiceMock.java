package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentConfigService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.AddTenantIdAndServiceRelationshipParam;
import com.dtstack.pubsvc.sdk.dto.param.CreateServiceParam;
import com.dtstack.pubsvc.sdk.dto.param.datasource.EditConsoleParam;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsShiftReturnDTO;
import com.dtstack.pubsvc.sdk.ranger.PubRangerServiceClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.TenantIdListParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.feign.Param;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-26 20:26
 */
public class DataSourceServiceMock extends BaseMock {


    @MockInvoke(targetClass = ComponentConfigService.class)
    public Map<String, Object> findSyncRangerComponentConfig(Long componentId, Integer componentTypeCode, boolean isFilter) {
        Map<String, Object> map = new HashMap<>();
        map.put("serviceName","default");
        return map;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listByClusterId(Long clusterId, Integer typeCode, boolean isDefault) {
        return ComponentServiceMock.mockDefaultComponents();
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public Map<String, Object> getComponentConfigByType(Long componentId, EFrontType frontType) {
        return null;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap) {
        return getComponentByClusterId(clusterId, componentType, isFilter, clazz, componentVersionMap, null);
    }

    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap, Long componentId) {
        Map<String, Object> configMap = null;
        if (EComponentType.SFTP.getTypeCode().equals(componentType)) {
            List<ComponentConfig> sftpComponentConfigs = ComponentConfigServiceMock.mockSftpComponentConfigs(-1L);
            configMap = ComponentConfigUtils.convertComponentConfigToMap(sftpComponentConfigs);
        }
        if (MapUtils.isEmpty(configMap)) {
            return null;
        }
        if (clazz.isInstance(Map.class)) {
            return (T) configMap;
        }
        String configStr = JSONObject.toJSONString(configMap);
        if (clazz.isInstance(String.class)) {
            return (T) configStr;
        }
        return JSONObject.parseObject(configStr, clazz);
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public Map<String, Object> convertComponentConfigToMap(Long componentId, boolean isFilter) {
        List<ComponentConfig> sparkComponentConfigs = ComponentConfigServiceMock.mockHiveComponentConfigs(-1L);
        return ComponentConfigUtils.convertComponentConfigToMap(sparkComponentConfigs);
    }

    @MockInvoke(targetClass = PubRangerServiceClient.class)
    ApiResponse<Boolean> addTenantIdAndServiceRelationship(AddTenantIdAndServiceRelationshipParam param) {
        return null;
    }

    @MockInvoke(targetClass = PubRangerServiceClient.class)
    ApiResponse<Boolean> addOrUpdateService(CreateServiceParam param) {
        return null;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getOne(Long clusterId) {
        return ClusterServiceMock.mockDefaultCluster();
    }

    @MockInvoke(targetClass = DataSourceAPIClient.class)
    ApiResponse<DsServiceInfoDTO> getDsInfoById(@Param("dataInfoId") Long dataInfoId) {
        DsServiceInfoDTO dto = new DsServiceInfoDTO();
        dto.setIsMeta(1);
        dto.setDataJson("{}");
        ApiResponse<DsServiceInfoDTO> result = new ApiResponse<>();
        result.setData(dto);
        return result;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public void setLdapUser(EComponentType componentType, Long dtUicUserId, Long dtTenantId, JSONObject pluginInfo) {
        return;
    }

    @MockInvoke(targetClass = TenantService.class)
    public Set<Long> findUicTenantIdsByClusterId(Long clusterId) {
        return Sets.newHashSet(-1L);
    }

    @MockInvoke(targetClass = UicTenantApiClient.class)
    ApiResponse<Boolean> bindHistoryLdap(TenantIdListParam param) {
        return null;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getByClusterIdAndComponentType( Long clusterId, Integer type,  String componentVersion,   Integer deployType) {
        return new Component();
    }

    @MockInvoke(targetClass = DataSourceAPIClient.class)
    ApiResponse<List<DsShiftReturnDTO>> editConsoleDs(EditConsoleParam consoleParam) {
        return null;
    }
}
