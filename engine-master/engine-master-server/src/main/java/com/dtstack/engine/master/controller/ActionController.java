package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IYarn;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationStatus;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.AppTypeVO;
import com.dtstack.engine.api.vo.JobLogVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.api.vo.action.ApplicationVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dto.ApplicationInfo;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/node/action")
@Api(value = "/node/action", tags = {"任务动作接口"})
public class ActionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionController.class);


    @Autowired
    private ActionService actionService;

    @Autowired
    private ScheduleJobService scheduleJobService;


    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private RdosWrapper rdosWrapper;

    @RequestMapping(value = "/listJobStatusByJobIds", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态、执行时间等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "查询的所有job的jobId值", required = true, dataType = "String", allowMultiple = true)
    })
    public List<ActionJobStatusVO> listJobStatusByJobIds(@RequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.listJobStatusByJobIds(jobIds);
    }

    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    @ApiOperation(value = "开始任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramActionExt", value = "请求开始的任务的相关信息及集群信息", required = true, paramType = "body", dataType = "ParamActionExt")
    })
    public Boolean start(@RequestBody ParamActionExt paramActionExt) {
        return actionService.start(paramActionExt);
    }

    @PostMapping(value = "/hotReloading")
    @ApiOperation(value = "热更新任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramActionExt", value = "请求开始的任务的相关信息及集群信息", required = true, paramType = "body", dataType = "ParamActionExt")
    })
    public void hotReloading(@RequestBody ParamActionExt paramActionExt) {
         actionService.hotReloading(paramActionExt);
    }

    @RequestMapping(value = "/startJob", method = {RequestMethod.POST})
    @ApiOperation(value = "立即执行任务")
    public Boolean startJob(@RequestBody ParamTaskAction paramTaskAction) {
        return actionService.startJob(paramTaskAction.getBatchTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
    }

    @RequestMapping(value = "/paramActionExt", method = {RequestMethod.POST})
    @ApiOperation(value = "提交前预处理接口")
    public ParamActionExt paramActionExt(@RequestBody ParamTaskAction paramTaskAction) throws Exception {
        return actionService.paramActionExt(paramTaskAction.getBatchTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
    }

    @RequestMapping(value = "/stop", method = {RequestMethod.POST})
    @ApiOperation(value = "停止任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "请求停止任务的相关信息，jobIds", required = true, paramType = "body")
    })
    public Boolean stop(@RequestParam(value = "jobIds") List<String> jobIds,@RequestParam("userId") Long userId) throws Exception {
        return actionService.stop(jobIds,userId);
    }

    @RequestMapping(value = "/forceStop", method = {RequestMethod.POST})
    @ApiOperation(value = "停止任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "请求停止任务的相关信息，jobIds", required = true, paramType = "body")
    })
    public Boolean stop(@RequestParam(value = "jobIds") List<String> jobIds, @RequestParam("isForce") Integer isForce,@RequestParam("userId") Long userId) throws Exception {
        return actionService.stop(jobIds, isForce,userId);
    }

    @RequestMapping(value = "/status", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String"),
    })
    public Integer status(@RequestParam("jobId") String jobId) throws Exception {
        return actionService.status(jobId);
    }

    @RequestMapping(value = "/statusByJobIds", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "查询的所有job的jobId值", required = true, dataType = "String", allowMultiple = true),
    })
    @Deprecated
    public Map<String, Integer> statusByJobIds(@RequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.statusByJobIds(jobIds);
    }

    @RequestMapping(value = "/startTime", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job开始运行的时间", notes = "返回值为毫秒级时间戳")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String")
    })
    public Long startTime(@RequestParam("jobId") String jobId) throws Exception {
        return actionService.startTime(jobId);
    }

    @RequestMapping(value = "/log", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "computeType", value = "查询的job的computeType值", required = true, dataType = "int")
    })
    public ActionLogVO log(@RequestParam("jobId") String jobId, @RequestParam("computeType") Integer computeType) throws Exception {
        return actionService.log(jobId, computeType);
    }

    @RequestMapping(value = "/log/unite", method = {RequestMethod.POST})
    @ApiOperation(value = "引擎提供统一的单个Job的log日志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageInfo", value = "重试次数", required = true, dataType = "int"),
    })
    public JobLogVO logUnite(@RequestParam("jobId") String jobId,@RequestParam("pageInfo") Integer pageInfo) throws Exception {
        return actionService.logUnite(jobId,pageInfo);
    }

    @RequestMapping(value = "/logFromEs", method = {RequestMethod.POST})
    @ApiOperation(value = "K8s调度下，查询单个Job的log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String")
    })
    public String logFromEs(@RequestParam("jobId") String jobId) throws Exception {
        return actionService.logFromEs(jobId);
    }

    @RequestMapping(value = "/retryLog", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的重试log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String"),
    })
    public List<ActionRetryLogVO> retryLog(@RequestParam("jobId") String jobId,@RequestParam("expandId") Long expandId) throws Exception {
        return actionService.retryLog(jobId,expandId);
    }

    @RequestMapping(value = "/retryLogDetail", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的详细重试log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "查询的job的jobId值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "retryNum", value = "查询的job的retryNum值", required = true, dataType = "int")
    })
    public ActionRetryLogVO retryLogDetail(@RequestParam("jobId") String jobId, @RequestParam("retryNum") Integer retryNum) throws Exception {
        return actionService.retryLogDetail(jobId, retryNum);
    }

    @RequestMapping(value = "/entitys", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态、相关日志等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "查询的所有job的jobId值", required = true, dataType = "String", allowMultiple = true),
    })
    public List<ActionJobEntityVO> entitys(@RequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.entitys(jobIds);
    }

    @RequestMapping(value = "/containerInfos", method = {RequestMethod.POST})
    @ApiOperation(value = "查询容器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramAction", value = "jobId、计算类型等信息", required = true, paramType = "body", dataType = "ParamAction")
    })
    public List<String> containerInfos(@RequestBody ParamAction paramAction) throws Exception {
        return actionService.containerInfos(paramAction);
    }

    @RequestMapping(value = "/resetTaskStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "重置任务状态为未提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "重置的job的jobId值", required = true, dataType = "String")
    })
    public String resetTaskStatus(@RequestParam("jobId") String jobId) {
        return actionService.resetTaskStatus(jobId);
    }

    @RequestMapping(value = "/listJobStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "查询某个时间开始的Job的状态、执行时间等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "查询的job的调整的时间点", required = true, dataType = "long")
    })
    public List<ActionJobStatusVO> listJobStatus(@RequestParam("time") Long time, @RequestParam("appType") Integer appType) {
        return actionService.listJobStatus(time, appType);
    }

    @RequestMapping(value = "/listJobStatusScheduleJob", method = {RequestMethod.POST})
    @ApiOperation(value = "查询某个时间开始的Job的状态、执行时间等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "查询的job的调整的时间点", required = true, dataType = "long")
    })
    public List<ScheduleJob> listJobStatusScheduleJob(@RequestParam("time") Long time, @RequestParam("appType") Integer appType) {
        return actionService.listJobStatusScheduleJob(time, appType);
    }

    @RequestMapping(value = "/generateUniqueSign", method = {RequestMethod.POST, RequestMethod.GET})
    public String generateUniqueSign() {
        return actionService.generateUniqueSign();
    }

    @RequestMapping(value = "/appType", method = {RequestMethod.POST, RequestMethod.GET})
    public List<AppTypeVO> appType() {
        return actionService.getAllAppType();
    }

    @RequestMapping(value = "/addOrUpdateJob", method = {RequestMethod.POST})
    @ApiOperation(value = "保存job信息")
    public Boolean addOrUpdateJob(@RequestBody ParamTaskAction paramTaskAction) {
        if (StringUtils.isBlank(paramTaskAction.getJobId())) {
            throw new RdosDefineException(ErrorCode.JOB_ID_CAN_NOT_EMPTY);
        }
        try {
            ScheduleJob scheduleJob = actionService.buildScheduleJob(paramTaskAction.getBatchTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
            scheduleJobService.addOrUpdateScheduleJob(scheduleJob);
        } catch (Exception e) {
            LOGGER.error("addOrUpdateJob {} error", paramTaskAction.getJobId(), e);
            return false;
        }
        return true;
    }

    /**
     * 获取yarn上运行任务
     * @return
     */
    @RequestMapping(value = "/listApplication", method = {RequestMethod.POST})
    public List<ApplicationVO> listApplication(@RequestParam("uicTenantId") Long tenantId, @RequestParam("jobIds") List<String> jobIds) {
        if (null == tenantId) {
            throw new RdosDefineException(ErrorCode.INVALID_UIC_TENANT_ID);
        }
        Long clusterId = tenantService.getClusterIdByDtUicTenantId(tenantId);
        JSONObject yarnInfo = clusterService.getYarnInfo(clusterId,tenantId,null);
        List<ApplicationVO> results = new ArrayList<>();
        if (MapUtils.isEmpty(yarnInfo)) {
            return results;
        }
        for (String jobId : jobIds) {
            try {
                String typeName = yarnInfo.getString(ConfigConstant.TYPE_NAME_KEY);
                Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.YARN);
                yarnInfo.put(ConfigConstant.DATASOURCE_TYPE, dataSourceCodeByDiceName);
                IYarn yarn = ClientCache.getYarn(dataSourceCodeByDiceName);
                ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(yarnInfo.toJSONString(), tenantId);
                List<YarnApplicationInfoDTO> yarnApplicationInfoDTOS = yarn.listApplication(sourceDTO, YarnApplicationStatus.RUNNING, jobId, null);
                if (!CollectionUtils.isEmpty(yarnApplicationInfoDTOS)) {
                    List<ApplicationVO> applicationVOS = yarnApplicationInfoDTOS.stream()
                            .map(a -> buildApplicationVO(a, jobId))
                            .collect(Collectors.toList());
                    results.addAll(applicationVOS);
                }
            } catch (Exception e) {
                throw new RdosDefineException(e);
            }
        }
        return results;
    }

    private ApplicationVO buildApplicationVO(YarnApplicationInfoDTO yarnApplicationInfoDTO, String jobId) {
        ApplicationVO vo = new ApplicationVO();
        vo.setApplicationId(yarnApplicationInfoDTO.getApplicationId());
        vo.setFinishTime(yarnApplicationInfoDTO.getFinishTime());
        vo.setJobId(jobId);
        vo.setName(yarnApplicationInfoDTO.getApplicationId());
        vo.setStartTime(yarnApplicationInfoDTO.getStartTime());
        vo.setStatus(convertToRdosTaskStatus(yarnApplicationInfoDTO.getStatus()).getStatus());
        return vo;
    }

    public static RdosTaskStatus convertToRdosTaskStatus(YarnApplicationStatus status) {

        switch (status) {
            case NEW:
            case NEW_SAVING:
            case SUBMITTED:
            case ACCEPTED:
                return RdosTaskStatus.COMPUTING;
            case RUNNING:
                return RdosTaskStatus.RUNNING;
            case FINISHED:
                return RdosTaskStatus.FINISHED;
            case FAILED:
                return RdosTaskStatus.FAILED;
            case KILLED:
                return RdosTaskStatus.KILLED;
            default:
                throw new UnsupportedOperationException(status + "status is not supported yet.");
        }
    }

    @RequestMapping(value = "/refreshStatus", method = {RequestMethod.POST})
    public Boolean refreshStatus(@RequestBody ParamActionExt paramActionExt) {
        if (StringUtils.isBlank(paramActionExt.getTaskId())) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        if (StringUtils.isBlank(paramActionExt.getApplicationId())) {
            throw new RdosDefineException(ErrorCode.APPLICATION_CAT_NOT_EMPTY);
        }

        //校验applicationId对应的任务名称包含自身jobId
        Boolean checkRefreshStatusName = environmentContext.checkRefreshStatusName();
        if (checkRefreshStatusName) {
            ApplicationInfo applicationInfo = getApplicationInfo(paramActionExt.getTenantId(), paramActionExt.getApplicationId(), paramActionExt.getTaskId());
            if (!applicationInfo.getName().contains(paramActionExt.getTaskId())) {
                throw new RdosDefineException(ErrorCode.APPLICATION_NOT_MATCH);
            }
        }
        return actionService.refreshStatus(paramActionExt);
    }

    private ApplicationInfo getApplicationInfo(Long tenantId, String applicationId, String taskId) {
        Long clusterId = tenantService.getClusterIdByDtUicTenantId(tenantId);
        JSONObject yarnInfo = clusterService.getYarnInfo(clusterId,tenantId,null);
        String typeName = yarnInfo.getString(ConfigConstant.TYPE_NAME_KEY);
        try {
            Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.YARN);
            yarnInfo.put("dataSourceType",dataSourceCodeByDiceName);
            IYarn yarn = ClientCache.getYarn(dataSourceCodeByDiceName);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(yarnInfo.toJSONString(), tenantId);
            List<YarnApplicationInfoDTO> yarnApplicationInfoDTOS = yarn.listApplication(sourceDTO, YarnApplicationStatus.RUNNING, taskId, applicationId);
            List<ApplicationInfo> applicationInfos = builds(yarnApplicationInfoDTOS);
            if (CollectionUtils.isEmpty(applicationInfos)) {
                throw new RdosDefineException(ErrorCode.APPLICATION_NOT_FOUND);
            }
            return applicationInfos.get(0);
        } catch (Exception e) {
            LOGGER.error("refreshStatus {} applicationId {} error", taskId, applicationId, e);
            throw new RdosDefineException(e);
        }
    }

    private List<ApplicationInfo> builds(List<YarnApplicationInfoDTO> yarnApplicationInfoDTOS) {
        List<ApplicationInfo> applicationInfoList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(yarnApplicationInfoDTOS)) {
            for (YarnApplicationInfoDTO yarnApplicationInfoDTO : yarnApplicationInfoDTOS) {
                ApplicationInfo applicationInfo = new ApplicationInfo();
                applicationInfo.setApplicationId(yarnApplicationInfoDTO.getApplicationId());
                applicationInfo.setStatus(convertToRdosTaskStatus(yarnApplicationInfoDTO.getStatus()).getStatus());
                applicationInfo.setName(yarnApplicationInfoDTO.getName());
                applicationInfo.setFinishTime(yarnApplicationInfoDTO.getFinishTime());
                applicationInfo.setStartTime(yarnApplicationInfoDTO.getStartTime());
                applicationInfoList.add(applicationInfo);
            }
        }
        return applicationInfoList;
    }

}
