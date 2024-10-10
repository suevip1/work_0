package com.dtstack.engine.master.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.param.ComponentConfigQueryParam;
import com.dtstack.engine.api.vo.components.ComponentVersionVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mapstruct.ComponentStruct;
import com.dtstack.engine.master.utils.CheckParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-10-29
 */
@RestController
@RequestMapping("/node/sdk/component")
@Api(value = "/node/sdk/component", tags = {"sdk组件接口"})
public class ComponentSdkController {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentStruct componentStruct;

    @Autowired
    private TenantService tenantService;

    @RequestMapping(value = "/getComponentVersion", method = {RequestMethod.POST})
    @ApiOperation(value = "租户和engineType获取集群组件信息")
    public List<ComponentVersionVO> getComponentVersionByEngineType(@RequestParam("uicTenantId") Long tenantId, @RequestParam("componentType") Integer componentType) {
        CheckParamUtils.checkDtUicTenantId(tenantId);
        if (null == componentType) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        EComponentType component = EComponentType.getByCode(componentType);
        List<Component> components = componentService.getComponents(tenantId, component);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        return componentStruct.toComponentVersions(components);
    }

    @RequestMapping(value = "/existsComponentByTenantIdAndComponentType", method = {RequestMethod.POST})
    @ApiOperation(value = "根据租户id和组件类型判断 租户对应的集群下是否存在传进来的组件类型的组件")
    public Boolean existsComponentByTenantIdAndComponentType(@RequestParam("uicTenantId") Long tenantId, @RequestParam("componentType") Integer componentType) {
        CheckParamUtils.checkDtUicTenantId(tenantId);
        if (null == componentType) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        EComponentType component = EComponentType.getByCode(componentType);
        List<Component> components = componentService.getComponents(tenantId, component);
        return !CollectionUtils.isEmpty(components);
    }

    @PostMapping(value = "/getComponentSimpleConfig")
    @ApiOperation(value = "获取组件基础配置信息")
    public JSONObject getComponentSimpleConfig(@RequestBody ComponentConfigQueryParam componentConfigQueryParam) {
        if (null == componentConfigQueryParam.getComponentCode()) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        if (componentConfigQueryParam.getTenantId() == null) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        Long clusterIdByDtUicTenantId = tenantService.getClusterIdByDtUicTenantId(componentConfigQueryParam.getTenantId());
        if (clusterIdByDtUicTenantId == null) {
            return null;
        }
        Map<Integer, String> componentVersionMap = new HashMap<>();
        componentVersionMap.put(componentConfigQueryParam.getComponentCode(), componentConfigQueryParam.getComponentVersion());
        return componentService.getComponentByClusterId(clusterIdByDtUicTenantId, componentConfigQueryParam.getComponentCode(), true, JSONObject.class, componentVersionMap);
    }

}
