package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.param.ComponentConfigQueryParam;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ComponentUserVO;
import com.dtstack.engine.api.vo.components.ComponentVersionVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.api.vo.task.TaskGetSupportJobTypesResultVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface ComponentService extends DtInsightServer {

    // @RequestLine("POST /node/component/listConfigOfComponents")
    // @Deprecated
    // ApiResponse<List<ComponentsConfigOfComponentsVO>> listConfigOfComponents(@Param("tenantId") Long dtUicTenantId, @Param("engineType") Integer engineType);


    @RequestLine("POST /node/component/listComponents")
    ApiResponse<List<Component>> listComponents(@Param("tenantId") Long dtUicTenantId, @Param("engineType") Integer engineType);

    // @Deprecated
    // @RequestLine("POST /node/component/getOne")
    // ApiResponse<Component> getOne(@Param("id") Long id);

    @RequestLine("POST /node/component/getKerberosConfig")
    ApiResponse<KerberosConfig> getKerberosConfig(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    // @RequestLine("POST /node/component/closeKerberos")
    // @Deprecated
    // ApiResponse<Void> closeKerberos(@Param("componentId") Long componentId);

    // @RequestLine("POST /node/component/addOrCheckClusterWithName")
    // @Deprecated
    // ApiResponse<ComponentsResultVO> addOrCheckClusterWithName(@Param("clusterName") String clusterName);

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType
     * @return
     */
    // @RequestLine("POST /node/component/loadTemplate")
    // @Deprecated
    // ApiResponse<List<ClientTemplate>> loadTemplate(@Param("componentType") Integer componentType, @Param("clusterName") String clusterName, @Param("version") String version);

    /**
     * 删除组件
     *
     * @param componentIds
     */
    // @RequestLine("POST /node/component/delete")
    // @Deprecated
    // ApiResponse<Void> delete( @Param("componentIds") List<Long> componentIds);

    /***
     * 获取对应的组件版本信息
     * @return
     */
    // @RequestLine("POST /node/component/getComponentVersion")
    // @Deprecated
    // ApiResponse<Map> getComponentVersion();

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    // @Deprecated
    // @RequestLine("POST /node/component/testConnects")
    // ApiResponse<List<ComponentTestResult>> testConnects(@Param("clusterName") String clusterName);

    @RequestLine("POST /node/component/isYarnSupportGpus")
    ApiResponse<Boolean> isYarnSupportGpus(@Param("clusterName") String clusterName);

    @RequestLine("POST /node/component/getSupportJobTypes")
    ApiResponse<List<TaskGetSupportJobTypesResultVO>>  getSupportJobTypes(@Param("appType") Integer appType,
                                                                          @Param("projectId") Long projectId,
                                                                          @Param("dtTenantId") Long dtuicTenantId);


    /**
     * @see ComponentService#getComponentVersion(java.lang.Long, java.lang.Integer)
     * @param tenantId
     * @param engineType
     * @return
     */
    // @RequestLine("POST /node/component/getComponentVersionByEngineType")
    // @Deprecated
    // ApiResponse<List<Component>> getComponentVersionByEngineType(@Param("uicTenantId") Long tenantId,@Param("engineType") String  engineType);


    @RequestLine("POST /node/component/getClusterComponentUser")
    ApiResponse <List<ComponentUserVO> > getClusterComponentUser(@Param("clusterId") Long clusterId,
                                                         @Param("componentTypeCode") Integer componentTypeCode,
                                                         @Param("needRefresh") Boolean needRefresh,
                                                         @Param("agentAddress") String agentAddress);


    @RequestLine("POST /node/component/getComponentUserByUic")
    ApiResponse<List<ComponentUserVO>> getComponentUserByUic(@Param("uicId")Long uicId,
                                                       @Param("componentTypeCode")Integer componentTypeCode,
                                                       @Param("needRefresh") Boolean needRefresh,
                                                       @Param("agentAddress")String agentAddress);


    /**
     * 获取组件版本信息
     *
     * @param tenantId      uic租户id
     * @param componentType 组件code
     * @return
     */
    @RequestLine("POST /node/sdk/component/getComponentVersion")
    ApiResponse<List<ComponentVersionVO>> getComponentVersion(@Param("uicTenantId") Long tenantId, @Param("componentType") Integer componentType);

    /**
     * 根据租户id和组件code判断租户是否有该组件
     *
     * @param tenantId      uic租户id
     * @param componentType 组件code
     * @return
     */
    @RequestLine("POST /node/sdk/component/existsComponentByTenantIdAndComponentType")
    ApiResponse<Boolean> existsComponentByTenantIdAndComponentType(@Param("uicTenantId") Long tenantId, @Param("componentType") Integer componentType);


    /**
     * 获取组件的基础配置信息(不包含kerberosConfig、version 、ssl等信息)
     * 如果租户未绑定集群 则 返回未null
     * @param queryParam
     * @return
     */
    @RequestLine("POST /node/sdk/component/getComponentSimpleConfig")
    ApiResponse<JSONObject> getComponentSimpleConfig(ComponentConfigQueryParam queryParam);


}
