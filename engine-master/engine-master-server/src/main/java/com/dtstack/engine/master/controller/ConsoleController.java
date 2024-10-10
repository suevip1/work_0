package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.faced.sdk.PublicServiceAlertApiClientSdkFaced;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.router.DtParamOrHeader;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.engine.master.vo.AddOrUpdateTenantComponentVO;
import com.dtstack.engine.master.vo.AlertConfigVO;
import com.dtstack.engine.master.vo.GroupOverviewVO;
import com.dtstack.engine.master.vo.JobGenerationVO;
import com.dtstack.engine.master.vo.NotYetKilledJobCountVO;
import com.dtstack.engine.master.vo.NotifyMethodVO;
import com.dtstack.engine.master.vo.ProductNameVO;
import com.dtstack.engine.master.vo.QueryNotYetKilledJobCountVO;
import com.dtstack.engine.master.vo.StopJobResult;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.vo.UserNameVO;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node/console")
@Api(value = "/node/console", tags = {"控制台接口"})
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private PublicServiceAlertApiClientSdkFaced publicServiceAlertApiClientSdkFaced;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @RequestMapping(value = "/nodeAddress", method = {RequestMethod.POST})
    public List<String> nodeAddress() {
        return consoleService.nodeAddress();
    }

    @RequestMapping(value = "/searchJob", method = {RequestMethod.POST})
    public ConsoleJobVO searchJob(@RequestParam("jobName") String jobName) {
        return consoleService.searchJob(jobName);
    }

    @RequestMapping(value = "/listNames", method = {RequestMethod.POST})
    public List<String> listNames(@RequestParam("jobName") String jobName) {
        return consoleService.listNames(jobName);
    }

    @RequestMapping(value = "/jobResources", method = {RequestMethod.POST})
    public List<String> jobResources() {
        return consoleService.jobResources();
    }

    @RequestMapping(value = "/overview", method = {RequestMethod.POST})
    @ApiOperation(value = "根据计算引擎类型显示任务")
    @Authenticate(all = "console_queue_view_record_all",
            tenant = "console_queue_view_record_tenant")
    public List<GroupOverviewVO> overview(@RequestParam("nodeAddress") String nodeAddress,
                                          @RequestParam("clusterName") String clusterName,
                                          @RequestParam("tenantId") Long tenantId,
                                          @RequestParam("dtToken") String dtToken,
                                          @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        return consoleService.overview(nodeAddress, clusterName, tenantId,user, isAllAuth);
    }

    @RequestMapping(value = "/groupDetail", method = {RequestMethod.POST})
    @Authenticate(all = "console_queue_view_details_all",
            tenant = "console_queue_view_details_tenant")
    public PageResult groupDetail(@RequestParam("jobResource") String jobResource,
                                  @RequestParam("nodeAddress") String nodeAddress,
                                  @RequestParam("stage") Integer stage,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("currentPage") Integer currentPage,
                                  @RequestParam("dtToken") String dtToken,
                                  @RequestParam("taskName") String taskName,
                                  @RequestParam("projectId") Long projectId,
                                  @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        return consoleService.groupDetail(jobResource, nodeAddress, stage, pageSize, currentPage, user, taskName, projectId,isAllAuth);
    }

    @RequestMapping(value = "/getProjectOptions", method = {RequestMethod.POST})
    public List<ProjectNameVO> getProjectOptions(@RequestParam("jobResource") String jobResource,
                                                 @RequestParam("nodeAddress") String nodeAddress,
                                                 @RequestParam("stage") Integer stage,
                                                 @RequestParam("searchKey") String searchKey) {
        return consoleService.getProjectOptions(jobResource, searchKey, stage, nodeAddress);
    }

    @RequestMapping(value = "/getUrlCheckList", method = {RequestMethod.POST})
    public List<Integer> getUrlCheckList() {
        return consoleService.getUrlCheckList();
    }

    @RequestMapping(value = "/jobStick", method = {RequestMethod.POST})
    public Boolean jobStick(@RequestParam("jobId") String jobId) {
        return consoleService.jobStick(jobId);
    }

    @RequestMapping(value = "/stopJob", method = {RequestMethod.POST})
    @Authenticate(all = "console_queue_view_kill_all",
            tenant = "console_queue_view_kill_tenant")
    public void stopJob(@RequestParam("jobId") String jobId,
                        @RequestParam("dtToken") String dtToken,
                        @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        consoleService.stopJobOfAuth(null, null, Lists.newArrayList(jobId), ForceCancelFlag.YES.getFlag(), isAllAuth,user);
    }

    @ApiOperation(value = "概览，杀死全部")
    @Authenticate(all = "console_queue_view_kill_all",
            tenant = "console_queue_view_kill_tenant")
    @RequestMapping(value = "/stopAll", method = {RequestMethod.POST})
    public StopJobResult stopAll(@RequestParam("jobResource") String jobResource,
                                 @RequestParam("nodeAddress") String nodeAddress,
                                 @RequestParam("dtToken") String dtToken,
                                 @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        return consoleService.stopJobOfAuth(jobResource, nodeAddress, null, ForceCancelFlag.YES.getFlag(), isAllAuth,user);
    }

    @RequestMapping(value = "/stopJobList", method = {RequestMethod.POST})
    @Authenticate(all = "console_queue_view_kill_all",
            tenant = "console_queue_view_kill_tenant")
    public StopJobResult stopJobList(@RequestParam("jobResource") String jobResource,
                                     @RequestParam("nodeAddress") String nodeAddress,
                                     @RequestParam("jobIdList") List<String> jobIdList,
                                     @RequestParam("dtToken") String dtToken,
                                     @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        return consoleService.stopJobOfAuth(jobResource, nodeAddress,jobIdList,ForceCancelFlag.YES.getFlag(),isAllAuth,user);
    }

    @RequestMapping(value = "/clusterResources", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_resource_view_all",
            tenant = "console_resource_resource_view_tenant")
    public ClusterResource clusterResources(@RequestParam("clusterName") String clusterName,
                                            @DtParamOrHeader(value = "dtuicTenantId", header = "cookie", cookie = "dt_tenant_id") Long dtuicTenantId,
                                            @RequestParam("dtToken") String dtToken,
                                            @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        // 判断权限
         if (!isAllAuth) {
             List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
             Cluster cluster = clusterDao.getByClusterName(clusterName);

             if (cluster == null) {
                 throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
             }

             List<Long> bindTenantIds = clusterTenantDao.listBoundedTenants(cluster.getId());

             List<Long> retainList = Lists.newArrayList(tenantIds);
             retainList.retainAll(bindTenantIds);
             if (CollectionUtils.isEmpty(retainList)) {
                 // 说明不相交
                 throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
             }
         }

        return consoleService.clusterResources(clusterName, null, dtuicTenantId);
    }

    @ApiOperation(value = "获取任务类型及对应的资源模板")
    @RequestMapping(value = "/getTaskResourceTemplate", method = {RequestMethod.POST})
    public List<TaskTypeResourceTemplateVO> getTaskResourceTemplate(
            @DtParamOrHeader(value = "dtuicTenantId", header = "cookie", cookie = "dt_tenant_id") Long dtuicTenantId) {
        return consoleService.getTaskResourceTemplate();
    }

    @ApiOperation(value = "获取周期实例生成记录")
    @RequestMapping(value = "/listJobGenerationRecord", method = {RequestMethod.POST})
    @Authenticate(all = "console_queue_graph_record_all")
    public List<JobGenerationVO> listJobGenerationRecord(@RequestParam("status") @Max(1) @Min(0) Integer status) {
        return consoleService.listJobGenerationRecord(status);
    }

    @ApiOperation(value = "获取周期实例告警配置")
    @RequestMapping(value = "/getAlertConfig", method = {RequestMethod.POST})
    public AlertConfigVO getAlertConfig() {
        return consoleService.getAlertConfig();
    }

    @ApiOperation(value = "保存周期实例告警配置")
    @RequestMapping(value = "/saveAlertConfig", method = {RequestMethod.POST})
    @Authenticate(all =  "console_queue_graph_alter_config_all")
    public AlertConfigVO saveAlertConfig(@RequestBody AlertConfigVO vo) {
        return consoleService.saveAlertConfig(vo);
    }

    @ApiOperation(value = "列出所有通知方式")
    @RequestMapping(value = "/listNotifyMethod", method = {RequestMethod.POST})
    public List<NotifyMethodVO> listNotifyMethod() {
        List<ClusterAlertResultDTO> clusterAlertResultDTOS = publicServiceAlertApiClientSdkFaced.listShow();
        return clusterAlertResultDTOS.stream().map(po -> {
            NotifyMethodVO vo = new NotifyMethodVO();
            vo.setAlertGateSource(po.getAlertGateSource());
            vo.setAlertGateName(po.getAlertGateName());
            vo.setAlertGateType(po.getAlertGateType());
            return vo;
        }).collect(Collectors.toList());
    }

    @ApiOperation(value = "列出所有接收人")
    @RequestMapping(value = "/getReceivers", method = {RequestMethod.POST})
    public List<UserNameVO> getReceivers(@RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("name") String name,
                                         @RequestParam("userIds") List<Long> userIds) {
        try {
            //兼容uic不升级
            return consoleService.getUser(Optional.ofNullable(name).orElse(""), userIds);
        } catch (Exception e) {
            return consoleService.getUser(dtuicTenantId, userIds);
        }
    }


    @ApiOperation(value = "查询所有用户")
    @RequestMapping(value = "/getUsers", method = {RequestMethod.POST})
    public List<UserNameVO> getUsers(@RequestParam("dtuicTenantId") Long dtuicTenantId,
                                     @RequestParam("name") String name,
                                     @RequestParam("userIds") List<Long> userIds) {
        try {
            return consoleService.getUser(Optional.ofNullable(name).orElse(StringUtils.EMPTY), userIds);
        } catch (Exception e) {
            return consoleService.getUser(dtuicTenantId, userIds);
        }
    }

    @ApiOperation(value = "查询所有数栈产品")
    @RequestMapping(value = "/getProducts", method = {RequestMethod.POST})
    public List<ProductNameVO> getProducts() {
        return consoleService.getModules();
    }


    @RequestMapping(value = "/addOrUpdateTenantComponent", method = {RequestMethod.POST})
    @ApiOperation(value = "添加租户组件配置")
    @Authenticate(all = "console_resource_bind_component_all",
            tenant = "console_resource_bind_component_tenant")
    public void addOrUpdateTenantComponent(@RequestBody List<AddOrUpdateTenantComponentVO> vos,
                                           @DtParamOrHeader(value = "dtuicTenantId", header = "cookie", cookie = "dt_tenant_id") Long dtuicTenantId,
                                           @RequestParam("dtToken") String dtToken,
                                           @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (!isAllAuth) {
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
            if (!tenantIds.contains(dtuicTenantId)) {
                // 说明不相交
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
        consoleService.addOrUpdateTenantComponent(vos);
    }



    @RequestMapping(value = "/getNotYetKilledJob", method = {RequestMethod.POST})
    @ApiOperation(value = "轮训接口，获取未杀死的job数量")
    public List<NotYetKilledJobCountVO> getNotYetKilledJob(@RequestBody List<QueryNotYetKilledJobCountVO> queryNotYetKilledJobCountVOS) {
        return consoleService.getNotYetKilledJob(queryNotYetKilledJobCountVOS);
    }


}
