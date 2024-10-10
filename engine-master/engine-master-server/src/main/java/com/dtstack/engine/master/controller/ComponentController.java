package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.lineage.ComponentMultiTestResult;
import com.dtstack.engine.api.vo.ComponentUserVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.api.vo.task.TaskGetSupportJobTypesResultVO;
import com.dtstack.engine.common.client.bean.DtScriptAgentLabelDTO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.ComponentUserAffectProjectDTO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ConsoleComponentUserService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.router.DtParamOrHeader;
import com.dtstack.engine.master.vo.JdbcUrlTipVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node/component")
@Api(value = "/node/component", tags = {"组件接口"})
public class ComponentController {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ConsoleComponentUserService consoleComponentUserService;

    @Autowired
    private TenantService tenantService;

    @RequestMapping(value = "/jdbcUrlTips", method = {RequestMethod.POST})
    public List<JdbcUrlTipVO> listJdbcUrlTips() {
        return componentService.listJdbcUrlTips();
    }

    @RequestMapping(value="/listConfigOfComponents", method = {RequestMethod.POST})
    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") Integer engineType) {
        return componentService.listConfigOfComponents(dtUicTenantId, engineType,null);
    }

    @RequestMapping(value="/listComponents", method = {RequestMethod.POST})
    public List<Component> listComponents(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("engineType") Integer engineType) {
        return componentService.listComponents(dtUicTenantId,engineType);
    }

    @RequestMapping(value="/listCommonComponents", method = {RequestMethod.POST})
    public List<Component> listCommonComponents(@RequestParam("clusterId") Long clusterId) {
        return componentService.listCommonComponents(clusterId);
    }

    /**
     * @param clusterId
     * @return
     */
    @RequestMapping(value="/hasRanger", method = {RequestMethod.POST})
    @ApiOperation(value = "判断当前集群是否配置了 ranger，且存在租户绑定，则返回 true")
    public boolean hasRanger(@RequestParam("clusterId") Long clusterId) {
        List<Component> commonComponents = componentService.listCommonComponents(clusterId);
        if (CollectionUtils.isEmpty(commonComponents)) {
            return false;
        }
        List<Integer> commonComponentTypeCodes = commonComponents.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        boolean hasRanger = commonComponentTypeCodes.contains(EComponentType.RANGER.getTypeCode());
        boolean hasBindTenants = tenantService.hasBindTenants(clusterId);
        return hasRanger && hasBindTenants;
    }

    @RequestMapping(value="/hasRangerAndLdap", method = {RequestMethod.POST})
    @ApiOperation(value = "判断当前集群是否配置了 ranger、ldap，则返回 true")
    public boolean hasRangerAndLdap(@RequestParam("clusterId") Long clusterId) {
        return clusterService.hasRangerAndLdap(clusterId);
    }

    @RequestMapping(value="/getOne", method = {RequestMethod.POST})
    public Component getOne(@RequestParam("id") Long id) {
        return componentService.getOne(id);
    }


    @RequestMapping(value="/getKerberosConfig", method = {RequestMethod.POST})
    public KerberosConfig getKerberosConfig(@RequestParam("clusterId") Long clusterId, @RequestParam("componentType") Integer componentType,@RequestParam("componentVersion") String componentVersion) {
        return componentService.getKerberosConfig(clusterId, componentType,componentVersion);
    }

    @RequestMapping(value="/updateKrb5Conf", method = {RequestMethod.POST})
    public void updateKrb5Conf(@RequestParam("krb5Content") String krb5Content) {
        componentService.updateKrb5Conf(krb5Content);
    }

    @RequestMapping(value="/closeKerberos", method = {RequestMethod.POST})
    @ApiOperation(value="移除kerberos配置")
    public void closeKerberos(@RequestParam("componentId") Long componentId) {
        componentService.closeKerberos(componentId);
    }

    @RequestMapping(value="/closeSSL", method = {RequestMethod.POST})
    @ApiOperation(value="移除SSL配置")
    public void closeSSL(@RequestParam("componentId") Long componentId) {
        componentService.closeSSL(componentId);
    }

    @RequestMapping(value="/addOrCheckClusterWithName", method = {RequestMethod.POST})
    public ComponentsResultVO addOrCheckClusterWithName(@RequestParam("clusterName") String clusterName) {
        return componentService.addOrCheckClusterWithName(clusterName);
    }


    @RequestMapping(value="/loadTemplate", method = {RequestMethod.POST})
    @ApiOperation(value = "加载各个组件的默认值, 解析yml文件转换为前端渲染格式")
    @ApiImplicitParams({
            @ApiImplicitParam(name="clusterId",value="集群id",required=true, dataType = "long",example = "-1L"),
            @ApiImplicitParam(name="versionName",value="组件版本名称",required=true, dataType = "string"),
            @ApiImplicitParam(name="deployType",value="deploy类型",required=true, dataType = "int"),
            @ApiImplicitParam(name="componentCode",value="组件code",required=true, dataType = "int"),
            @ApiImplicitParam(name="storeType",value="存储组件code",required=true, dataType = "int"),
    })
    public List<ClientTemplate> loadTemplate(@RequestParam("componentType") Integer componentType, @RequestParam("clusterId") Long clusterId,
                                             @RequestParam("versionName") String versionName,@RequestParam("storeType")Integer storeType,
                                             @RequestParam("deployType") Integer deployType) {
        EComponentType type = EComponentType.getByCode(componentType);
        EComponentType storeComponentType = storeType == null ? null : EComponentType.getByCode(storeType);
        return componentService.loadTemplate(clusterId, type, versionName, storeComponentType,deployType);
    }


    @RequestMapping(value="/delete", method = {RequestMethod.POST})
    @ApiOperation(value = "删除组件")
    public void delete(@RequestParam("componentIds") List<Integer> componentIds) {
        componentService.delete(componentIds);
    }

    @RequestMapping(value="/getComponentVersion", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public Map getComponentVersion(Long clusterId) {
        return componentService.getComponentVersion(clusterId);
    }

    @RequestMapping(value="/getComponentStore", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public List<Component> getComponentStore(@RequestParam("clusterName") String clusterName,@RequestParam("componentType") Integer componentType) {
        return componentService.getComponentStore(clusterName,componentType);
    }

    @RequestMapping(value="/testConnects", method = {RequestMethod.POST})
    @ApiOperation(value = "测试所有组件连通性")
    public List<ComponentMultiTestResult> testConnects(@RequestParam("clusterName") String clusterName,@DtParamOrHeader(value = "dtuicTenantId",header = "cookie",cookie = "dt_tenant_id") Long dtuicTenantId) {
        return componentService.testConnects(clusterName,dtuicTenantId);
    }

    @RequestMapping(value="/testConnect", method = {RequestMethod.POST})
    @ApiOperation(value = "测试单个组件连通性")
    public ComponentTestResult testConnect(@RequestParam("clusterName") String clusterName,@RequestParam("componentType") Integer componentType,@RequestParam("versionName")String versionName,@DtParamOrHeader(value = "dtuicTenantId",header = "cookie",cookie = "dt_tenant_id") Long dtuicTenantId) {
        return componentService.testConnect(clusterName,componentType,versionName,dtuicTenantId);
    }

    @RequestMapping(value="/refresh", method = {RequestMethod.POST})
    @ApiOperation(value = "刷新组件信息")
    public List<ComponentTestResult> refresh(@RequestParam("clusterName") String clusterName) {
        return componentService.refresh(clusterName);
    }

    @RequestMapping(value="/isYarnSupportGpus", method = {RequestMethod.POST})
    @ApiOperation(value = "判断集群是否支持gpu")
    public Boolean isYarnSupportGpus(@RequestParam("clusterName") String clusterName) {
        return componentService.isYarnSupportGpus(clusterName);
    }

    @RequestMapping(value="/getSupportJobTypes", method = {RequestMethod.POST})
    public List<TaskGetSupportJobTypesResultVO>  getSupportJobTypes(@RequestParam("appType") Integer appType,
                                                              @RequestParam("projectId") Long projectId,
                                                              @DtParamOrHeader(value = "dtuicTenantId",header = "cookie",cookie = "dt_tenant_id") Long dtuicTenantId) {
        return componentService.getSupportJobTypes(appType, projectId, dtuicTenantId);
    }

    @RequestMapping(value="/getDtScriptAgentLabel", method = {RequestMethod.POST})
    @ApiOperation(value = "获取dtScript agent label信息")
    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(@RequestParam("agentAddress")String agentAddress){
        return componentService.getDtScriptAgentLabel(agentAddress);
    }

    @RequestMapping(value = "/getComponentVersionByEngineType",method = {RequestMethod.POST})
    @ApiOperation(value = "租户和engineType获取集群组件信息")
    public List<Component> getComponentVersionByEngineType(@RequestParam("uicTenantId") Long tenantId,@RequestParam("engineType")String  engineType){
        return componentService.getComponentVersionByEngineType(tenantId,engineType);
    }

    @RequestMapping(value = "/addOrUpdateComponentUser",method = {RequestMethod.POST})
    public void addOrUpdateComponentUser(@RequestBody List<ComponentUserVO> componentUserList){
        if (CollectionUtils.isEmpty(componentUserList)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        consoleComponentUserService.saveComponentUser(componentUserList);
    }

    /**
     * 删除节点标签会影响到的项目
     * @param componentUserList
     */
    @RequestMapping(value = "/findAffectedProjects",method = {RequestMethod.POST})
    public ComponentUserAffectProjectDTO findAffectedProjects(@RequestBody List<ComponentUserVO> componentUserList){
        return consoleComponentUserService.findAffectedProjects(componentUserList);
    }

    @RequestMapping(value = "/getClusterComponentUser",method = {RequestMethod.POST})
    public List<ComponentUserVO> getClusterComponentUser(@RequestParam("clusterId")Long clusterId,
                                                         @RequestParam("componentTypeCode")Integer componentTypeCode,
                                                         @RequestParam("needRefresh") Boolean needRefresh,
                                                         @RequestParam("agentAddress")String agentAddress){
        return componentService.getClusterComponentUser(clusterId,componentTypeCode,needRefresh,agentAddress,false);
    }

    @RequestMapping(value = "/getComponentUserByUic",method = {RequestMethod.POST})
    public List<ComponentUserVO> getComponentUserByUic(@RequestParam("uicId")Long uicId,
                                                         @RequestParam("componentTypeCode")Integer componentTypeCode,
                                                         @RequestParam("needRefresh") Boolean needRefresh,
                                                         @RequestParam("agentAddress")String agentAddress){
        return componentService.getClusterComponentUser(uicId,componentTypeCode,needRefresh,agentAddress,true);
    }

    @PostMapping(value = "/syncDataSecurity")
    @ApiOperation(value = "同步至数据安全, 返回 true 表示同步成功, 若失败则抛出异常信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public boolean syncDataSecurity(Long componentId) {
        return componentService.syncDataSecurity(componentId);
    }

    @PostMapping(value = "/hasSecurityPolicy")
    @ApiOperation(value = "若组件在数据安全中配置了 Policy，返回 true，否则返回 false")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public boolean hasSecurityPolicy(Long componentId) {
        if (componentId == null) {
            return false;
        }
        return componentService.hasSecurityPolicy(componentId);
    }
}