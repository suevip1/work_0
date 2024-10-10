package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ClusterTenantResourceBindingParam;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.TenantNameVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mapstruct.TenantStruct;
import com.dtstack.engine.master.router.DtParamOrHeader;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.engine.master.vo.CheckedTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/node/tenant")
@Api(value = "/node/tenant", tags = {"租户接口"})
public class TenantController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantController.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private TenantStruct tenantStruct;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    @Deprecated
    public PageResult<List<EngineTenantVO>> pageQuery(@RequestParam("clusterId") Long clusterId,
                                                      @RequestParam("engineType") Integer engineType,
                                                      @RequestParam("tenantName") String tenantName,
                                                      @RequestParam("pageSize") int pageSize,
                                                      @RequestParam("currentPage") int currentPage) {
            PageResult<List<ClusterTenantVO>> listPageResult = tenantService.pageQuery(clusterId, engineType, tenantName, pageSize, currentPage);
            List<EngineTenantVO> engineTenantVOS = tenantStruct.toClusterTenantVO(listPageResult.getData());
            return new PageResult<>(listPageResult.getCurrentPage(), listPageResult.getPageSize(),
                    listPageResult.getTotalCount(), listPageResult.getTotalPage(), engineTenantVOS);
    }

    @RequestMapping(value="/listEngineTenant", method = {RequestMethod.POST})
    @ApiOperation(value = "获取处于统一集群的全部tenant")
    @Deprecated
    public List<EngineTenantVO> listEngineTenant(@RequestParam("dtuicTenantId") Long dtuicTenantId,
                                                 @RequestParam("engineType") Integer engineType) {
            List<ClusterTenantVO> clusterTenantVOS = tenantService.listEngineTenant(dtuicTenantId, engineType);
            return tenantStruct.toClusterTenantVO(clusterTenantVOS);
    }

    @RequestMapping(value = "/listTenants", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户及其绑定状态")
    public List<CheckedTenantVO> listTenants(@RequestParam("tenantName") String tenantName) {
        return tenantService.listCheckedTenants(tenantName);
    }

    @RequestMapping(value = "/listBoundedTenants", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户及其绑定状态")
    @Authenticate(all = "console_resource_resource_granted_all",
            tenant = "console_resource_resource_granted_tenant")
    public List<TenantNameVO> listBoundedTenants(@RequestParam("clusterId") Long clusterId,
                                                 @RequestParam("dtToken") String dtToken,
                                                 @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {

        List<Long> tenantIds = Lists.newArrayList();
        if (!isAllAuth) {
            UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
            tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

        }
        return tenantService.listBoundedTenants(clusterId,tenantIds);
    }

    @RequestMapping(value = "/hasBindTenants", method = {RequestMethod.POST})
    @ApiOperation(value = "集群是否绑定租户")
    public boolean hasBindTenants(@RequestParam("clusterId") Long clusterId) {
        return tenantService.hasBindTenants(clusterId);
    }

    @RequestMapping(value = "/canShowLdapTip", method = {RequestMethod.POST})
    @ApiOperation(value = "判断当前集群是否配置了 ldap+ranger，若配置了则提示 ldap 信息")
    public boolean canShowLdapTip(@RequestParam("clusterId") Long clusterId) {
        return clusterService.hasRangerAndLdap(clusterId);
    }

    @RequestMapping(value = "/canShowSecurity", method = {RequestMethod.POST})
    @ApiOperation(value = "判断当前租户是否配置了 ldap+ranger，若配置了则显示数据安全入口")
    public boolean canShowSecurity(@RequestParam("dtUicTenantId") Long dtUicTenantId) {
        return tenantService.hasRangerAndLdap(dtUicTenantId);
    }

    @RequestMapping(value="/dtToken", method = {RequestMethod.POST})
    public List<UserTenantVO> listTenant(@RequestParam("dt_token") String dtToken) {
        return tenantService.listTenant(dtToken);
    }

    @RequestMapping(value="/bindingTenant", method = {RequestMethod.POST})
    public void bindingTenant(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("clusterId") Long clusterId,
                              @RequestParam("queueId") Long queueId, @RequestParam("dt_token") String dtToken,@RequestParam("namespace") String namespace) throws Exception {
        tenantService.bindingTenant(dtUicTenantId, clusterId, queueId, dtToken,namespace);
    }


    @RequestMapping(value = "/bindingResource", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_resource_update_all"
            , tenant = "console_resource_bind_resource_update_tenant")
    public void bindingResource(@RequestParam("resourceId") Long resourceId,
                                @RequestParam("tenantId") Long dtUicTenantId,
                                @RequestParam("taskTypeResourceJson") String taskTypeResourceJson,
                                @RequestParam("dtToken") String dtToken,
                                @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);

        if (!isAllAuth) {
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (CollectionUtils.isNotEmpty(tenantIds) && !tenantIds.contains(dtUicTenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
        tenantService.bindingResource(resourceId, dtUicTenantId, taskTypeResourceJson);
    }

    @RequestMapping(value = "/bindingLabelResource", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_resource_update_all",
            tenant = "console_resource_bind_resource_update_tenant")
    public void bindingLabelResource(@RequestParam("labelResourceId") Long labelResourceId,
                                @RequestParam("tenantId") Long dtUicTenantId,
                                @RequestParam("dtToken") String dtToken,
                                @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (!isAllAuth) {
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (CollectionUtils.isNotEmpty(tenantIds) && !tenantIds.contains(dtUicTenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
        tenantService.bindingLabelResource(labelResourceId, dtUicTenantId);
    }

    @RequestMapping(value="/bindingQueue", method = {RequestMethod.POST})
    public void bindingQueue(@RequestParam("queueId") Long queueId,
                             @RequestParam("tenantId") Long dtUicTenantId,
                             @RequestParam("taskTypeResourceJson") String taskTypeResourceJson
                             ) {
        tenantService.bindingQueue(queueId, dtUicTenantId,taskTypeResourceJson);
    }

    @ApiOperation(value = "根据租户id和taskType获取资源限制信息")
    @RequestMapping(value="/queryResourceLimitByTenantIdAndTaskType", method = {RequestMethod.POST})
    public String queryResourceLimitByTenantIdAndTaskType(@RequestParam("dtUicTenantId") Long dtUicTenantId,
                                                                         @RequestParam("taskType") Integer taskType) {
        return tenantService.queryResourceLimitByTenantIdAndTaskType(dtUicTenantId,taskType);
    }


    @RequestMapping(value="/bindNamespace", method = {RequestMethod.POST})
    @ApiOperation(value = "更新namespace")
    public void bindNamespace(@RequestParam("clusterId") Long clusterId,@RequestParam("namespace") String namespace,@RequestParam("queueId") Long queueId,@RequestParam("tenantId") Long dtUicTenantId) {
        componentService.addOrUpdateNamespaces(clusterId,namespace,queueId,dtUicTenantId);
    }

    @RequestMapping(value="/bindingTenantWithResource", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_cluster_all")
    public void bindingTenantWithResource(@RequestBody @Valid ClusterTenantResourceBindingParam param, @DtParamOrHeader(value = "dtToken",header = "cookie",cookie = "dt_token") String dtToken) throws Exception {
        tenantService.bindingTenantWithResource(param, dtToken);
    }

    @RequestMapping(value="/unbindTenant", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_cluster_all")
    public void unbindTenant(@RequestBody @Valid ClusterTenantResourceBindingParam param, @DtParamOrHeader(value = "dtToken",header = "cookie",cookie = "dt_token") String dtToken) throws Exception {
        tenantService.unbindTenant(param);
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        LOGGER.info("unbindTenant success, dtuicUserId={}, userName={}, param={}",user.getDtuicUserId(),
                user.getUserName(), param);
    }

    @RequestMapping(value="/findCacheTenant", method = {RequestMethod.POST})
    public List<TenantNameVO> findCacheTenant(@RequestParam("clusterId") Long clusterId) throws Exception {
        return tenantService.findCacheTenant(clusterId);
    }
}
