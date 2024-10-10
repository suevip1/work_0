package com.dtstack.engine.master.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.engine.SourceConstant;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.utils.CheckParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-13
 */
@RestController
@RequestMapping("/node/sdk/tenant")
public class TenantSdkController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantSdkController.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @RequestMapping(value = "/pageQuery", method = {RequestMethod.POST})
    public PageResult<List<ClusterTenantVO>> clusterTenantPageQuery(@RequestParam("clusterId") Long clusterId,
                                                                    @RequestParam("tenantName") String tenantName,
                                                                    @RequestParam("pageSize") int pageSize,
                                                                    @RequestParam("currentPage") int currentPage) {
        CheckParamUtils.checkPageSize(currentPage, pageSize);
        if (null == clusterId) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        return tenantService.pageQuery(clusterId, null, tenantName, pageSize, currentPage);
    }

    @RequestMapping(value = "/listClusterTenant", method = {RequestMethod.POST})
    public List<ClusterTenantVO> listClusterTenant(@RequestParam("dtuicTenantId") Long dtuicTenantId) {
        if (null == dtuicTenantId) {
            throw new RdosDefineException(ErrorCode.INVALID_UIC_TENANT_ID);
        }
        return tenantService.listClusterTenantVOSInTenantId(dtuicTenantId);
    }

    @RequestMapping(value = "/openDataSecurity", method = {RequestMethod.POST})
    public Boolean openDataSecurity(@RequestParam("dtuicTenantId") Long dtuicTenantId) {
        if (dtuicTenantId == null) {
            throw new RdosDefineException(ErrorCode.INVALID_UIC_TENANT_ID);
        }
        Long clusterId = tenantService.getClusterIdByDtUicTenantId(dtuicTenantId);
        if (clusterId == null) {
            return false;
        }
        boolean dataSecurityControl = false;
        Component commonComponent = componentService.getComponentByClusterId(clusterId, EComponentType.COMMON.getTypeCode(), null);
        if (commonComponent != null) {
            JSONObject commonComponentConfig = componentService.getComponentByClusterId(commonComponent.getId(), false, JSONObject.class);
            // 1）common 组件配置了是否对接客户自己的权限管控体系
            dataSecurityControl = commonComponentConfig.getBooleanValue(GlobalConst.DATA_SECURITY_CONTROL);
        }

        // 2) 是否开启了 hive.proxy.enable，兼容联通历史客户场景
        boolean hiveProxyEnable = false;
        JSONObject configObj = componentService.getComponentByClusterId(clusterId, EComponentType.HIVE_SERVER.getTypeCode(), false, JSONObject.class, null);
        configObj = (configObj == null ? new JSONObject() : configObj);
        clusterService.setTenantComponentConfig(EComponentType.HIVE_SERVER, dtuicTenantId, clusterId, configObj);
        hiveProxyEnable = configObj.getBooleanValue(SourceConstant.HIVE_PROXY_ENABLE);

        // 3）是否对接了数栈的 ranger 权限管控
        boolean hasRangerAndLdap = clusterService.hasRangerAndLdap(clusterId);
        LOGGER.info("openDataSecurity, dtuicTenantId:{}, dataSecurityControl:{}, hiveProxyEnable:{}, hasRangerAndLdap:{}", dtuicTenantId, dataSecurityControl, hiveProxyEnable, hasRangerAndLdap);
        return dataSecurityControl || hiveProxyEnable || hasRangerAndLdap;
    }
}
