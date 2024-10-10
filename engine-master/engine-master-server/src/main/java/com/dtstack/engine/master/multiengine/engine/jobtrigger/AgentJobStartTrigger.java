package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.IsDefaultEnum;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentUserDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.ResourceGroupGrantDao;
import com.dtstack.engine.master.impl.ComponentConfigService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ConsoleComponentUserService;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentScriptHandler;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentScriptHandlerManager;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant;
import com.dtstack.engine.master.utils.TaskParamUtil;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ComponentUser;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 处理跑在 agentServer 上的任务，主要动作是将 actionParam 中 sqlText 替换为最终要执行的脚本 @_@ <br/>
 * 任务包括:
 * <ul>
 *     <li>shell on agent {@link com.dtstack.engine.common.enums.EScheduleJobType#SHELL_ON_AGENT}</li>
 *     <li>python on agent {@link com.dtstack.engine.common.enums.EScheduleJobType#PYTHON_ON_AGENT }</li>
 * </ur>
 *
 * @author leon
 * @date 2023-04-19 10:42
 **/
@Component
public class AgentJobStartTrigger extends HadoopJobStartTrigger {

    private final AgentScriptHandlerManager handlerManager;

    private final ComponentService componentService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ResourceGroupDao resourceGroupDao;

    @Autowired
    private ResourceGroupGrantDao resourceGroupGrantDao;

    @Autowired
    private ComponentUserDao componentUserDao;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ConsoleComponentUserService consoleComponentUserService;

    private static final String USER_NAME = "user.name";

    private static final String USER_PASSWORD = "user.password";

    private static final String AGENT_IP = "agent.ip";

    private static final String USER_LABEL = "node.label";

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentJobStartTrigger.class);

    public AgentJobStartTrigger(AgentScriptHandlerManager handlerManager,
                                ComponentService componentService) {
        this.handlerManager = handlerManager;
        this.componentService = componentService;
    }

    @Override
    protected void doProcess(Map<String, Object> actionParam,
                             ScheduleTaskShade taskShade,
                             ScheduleJob scheduleJob) throws Exception {
        // agent 任务的前置处理，主要是根据任务生成不同的 agent 执行脚本
        // 替换到 actionParam.sqlText 中
        handle(actionParam, taskShade, scheduleJob);

        // 处理下 agent 任务的 taskParam，主要是 username, label, ip 等的设置
        setTaskParams(actionParam, scheduleJob);
    }


    private void handle(Map<String, Object> actionParam,
                        ScheduleTaskShade taskShade,
                        ScheduleJob scheduleJob) throws Exception {
        AgentHandlerContext handlerContext = AgentHandlerContext.of(actionParam, taskShade, scheduleJob);
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(taskShade.getDtuicTenantId());
        setPath(handlerContext, clusterId);
        AgentScriptHandler handler = handlerManager.chooseHandler(handlerContext);
        handler.handle(handlerContext);
    }

    private void setPath(AgentHandlerContext handlerContext, Long clusterId) {
        List<ComponentConfig> python2Configs = componentConfigService.
                getComponentConfigListByTypeCodeAndKey(clusterId, EComponentType.DTSCRIPT_AGENT.getTypeCode(), HandleConstant.PYTHON2_PATH);

        if (CollectionUtils.isNotEmpty(python2Configs)) {
            Optional<ComponentConfig> first = python2Configs.stream().findFirst();
            if (first.isPresent()) {
                ComponentConfig componentConfig = first.get();
                handlerContext.setPython2Path(componentConfig.getValue());
            }
        }

        List<ComponentConfig> python3Configs = componentConfigService.
                getComponentConfigListByTypeCodeAndKey(clusterId, EComponentType.DTSCRIPT_AGENT.getTypeCode(), HandleConstant.PYTHON3_PATH);

        if (CollectionUtils.isNotEmpty(python3Configs)) {
            Optional<ComponentConfig> first = python3Configs.stream().findFirst();
            if (first.isPresent()) {
                ComponentConfig componentConfig = first.get();
                handlerContext.setPython3Path(componentConfig.getValue());
            }
        }
    }

    private void setTaskParams(Map<String, Object> actionParam, ScheduleJob scheduleJob) {
        String taskParams = Objects.toString(actionParam.get(GlobalConst.TASK_PARAMS), StringUtils.EMPTY);
        taskParams = addTaskPrams(taskParams, scheduleJob);
        actionParam.put(GlobalConst.TASK_PARAMS, taskParams);
        LOGGER.info("jobId:{}, taskParam: {}", scheduleJob.getJobId(), taskParams);
    }

    /**
     * 填充 user.password、agent.ip 等信息
     *
     * @param taskParam
     * @param scheduleJob
     * @return
     */
    protected String addTaskPrams(String taskParam,ScheduleJob scheduleJob) {
        Long resourceId = scheduleJob.getResourceId();
        Map<String, String> taskParamMap = TaskParamUtil.splitTaskParam(taskParam);
        // 已经授权的资源组
        List<ResourceGroupDetail> grantedResourceGroups = resourceGroupGrantDao.listAccessedResourceDetailByProjectId(scheduleJob.getProjectId(),
                        scheduleJob.getAppType(), EComponentType.DTSCRIPT_AGENT.getTypeCode())
                .stream().collect(Collectors.toList());
        // 命中的资源组
        ResourceGroup hitResourceGroup = chooseLabelResourceGroup(resourceId, grantedResourceGroups,
                scheduleJob.getDtuicTenantId(),
                scheduleJob.getJobId());

        if (hitResourceGroup != null) {
            ComponentUser componentUser = componentService.getComponentUser(scheduleJob.getDtuicTenantId(),
                    EComponentType.DTSCRIPT_AGENT.getTypeCode(),
                    hitResourceGroup.getName(),
                    hitResourceGroup.getQueuePath());
            if (componentUser != null) {
                // 覆盖 USER_LABEL、USER_NAME -- 传了授权信息就要以授权信息为准
                taskParamMap.put(USER_LABEL, componentUser.getLabel());
                taskParamMap.put(USER_NAME, componentUser.getUserName());
                taskParamMap.put(USER_PASSWORD, Base64Util.baseDecode(componentUser.getPassword()));
                taskParamMap.put(AGENT_IP, componentUser.getLabelIp());
                LOGGER.info("jobId:{}, find hitResourceGroup:{}", scheduleJob.getJobId(), hitResourceGroup.getId());
            } else {
                // 容错
                LOGGER.warn("jobId:{}, hitResourceGroup:{}, not find componentUser", scheduleJob.getJobId(), hitResourceGroup.getId());
            }
            return TaskParamUtil.concatTaskParam(taskParamMap);
        } else {
            // 授权信息不存在的情况下，使用历史逻辑处理
            LOGGER.info("jobId:{}, no resourceGroup", scheduleJob.getJobId());
            return addTaskPramsWhenNoResourceGroup(taskParam, scheduleJob, taskParamMap);
        }
    }

    /**
     * 选择使用的标签资源组
     * @param jobResourceId
     * @param grantedResourceGroups
     * @param dtuicTenantId
     * @param jobId
     * @return
     */
    private ResourceGroup chooseLabelResourceGroup(Long jobResourceId, List<ResourceGroupDetail> grantedResourceGroups, Long dtuicTenantId, String jobId) {
        List<Long> grantedResourceGroupIds = grantedResourceGroups.stream().map(ResourceGroupDetail::getResourceId).collect(Collectors.toList());
        // 命中的资源组
        ResourceGroup hitResourceGroup = null;
        ResourceGroup tmpHitResourceGroup = null;

        // 优先取用户传的标签资源组
        if (jobResourceId != null) {
            tmpHitResourceGroup = resourceGroupDao.getOne(jobResourceId, Deleted.NORMAL.getStatus());
            // 必须要已授权
            if (tmpHitResourceGroup != null && grantedResourceGroupIds.contains(tmpHitResourceGroup.getId())) {
                hitResourceGroup = tmpHitResourceGroup;
                LOGGER.info("jobId:{}, use scheduleJob.hitResourceGroup:{}", jobId, hitResourceGroup.getId());
                return hitResourceGroup;
            }
        }

        // 没找到或者没传，使用项目级的默认标签资源组
        Long defaultProjectLabelResourceId = grantedResourceGroups.stream()
                .filter(t -> IsDefaultEnum.DEFAULT.getType().equals(t.getIsProjectDefault()))
                .map(ResourceGroupDetail::getResourceId)
                .findFirst()
                .orElse(null);
        if (defaultProjectLabelResourceId != null) {
            tmpHitResourceGroup = resourceGroupDao.getOne(defaultProjectLabelResourceId, Deleted.NORMAL.getStatus());
            if (tmpHitResourceGroup != null) {
                hitResourceGroup = tmpHitResourceGroup;
                LOGGER.info("jobId:{}, use project default hitResourceGroup:{}", jobId, hitResourceGroup.getId());
                return hitResourceGroup;
            }
        }

        // 没找到或者没传，使用租户绑定的默认标签资源组
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(dtuicTenantId);
        Long defaultLabelResourceId = clusterTenant.getDefaultLabelResourceId();
        if (defaultLabelResourceId != null && grantedResourceGroupIds.contains(defaultLabelResourceId)) {
            // 必须要已授权
            tmpHitResourceGroup = resourceGroupDao.getOne(defaultLabelResourceId, Deleted.NORMAL.getStatus());
            if (tmpHitResourceGroup != null) {
                hitResourceGroup = tmpHitResourceGroup;
                LOGGER.info("jobId:{}, use tenant default hitResourceGroup:{}", jobId, hitResourceGroup.getId());
                return hitResourceGroup;
            }
        }

        // 还是没找到，使用集群的默认标签资源组
        List<ComponentUser> defaultComponentUsers = componentUserDao.getComponentUserByCluster(clusterTenant.getClusterId(), EComponentType.DTSCRIPT_AGENT.getTypeCode())
                .stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsDefault()))
                .filter(t -> StringUtils.isNotBlank(t.getUserName()))
                .collect(Collectors.toList());
        List<ResourceGroup> defaultLabelResourceGroups = consoleComponentUserService.findLabelResourceGroup(defaultComponentUsers);
        Collections.sort(defaultLabelResourceGroups, Comparator.comparing(ResourceGroup::getId));
        for (ResourceGroup defaultLabelResourceGroup : defaultLabelResourceGroups) {
            if (grantedResourceGroupIds.contains(defaultLabelResourceGroup.getId())) {
                // 按照 id 排序后，任取一个
                hitResourceGroup = defaultLabelResourceGroup;
                LOGGER.info("jobId:{}, use cluster default hitResourceGroup:{}", jobId, hitResourceGroup.getId());
                break;
            }
        }

        return hitResourceGroup;
    }

    private String addTaskPramsWhenNoResourceGroup(String originTaskParam, ScheduleJob scheduleJob, Map<String, String> taskParamMap) {
        Map<String, String> labelUserMap = new HashMap<>(2);
        if (taskParamMap.containsKey(USER_LABEL)) {
            labelUserMap.put(USER_LABEL, taskParamMap.get(USER_LABEL));
        }
        if (taskParamMap.containsKey(USER_NAME)) {
            labelUserMap.put(USER_NAME, taskParamMap.get(USER_NAME));
        }
        if (labelUserMap.size() < 2) {
            return originTaskParam;
        }

        ComponentUser componentUser =
                componentService.getComponentUser(scheduleJob.getDtuicTenantId(),
                        EComponentType.DTSCRIPT_AGENT.getTypeCode(),
                        labelUserMap.get(USER_LABEL),
                        labelUserMap.get(USER_NAME));

        if (Objects.nonNull(componentUser)) {
            if (StringUtils.isNotBlank(componentUser.getPassword())) {
                originTaskParam = originTaskParam + String.format(
                        "\r\n%s=%s",
                        USER_PASSWORD,
                        Base64Util.baseDecode(componentUser.getPassword()));
            }

            if (Objects.nonNull(componentUser.getLabelIp())) {
                originTaskParam = originTaskParam + String.format(
                        "\r\n%s=%s",
                        AGENT_IP,
                        componentUser.getLabelIp());
            }
        }
        return originTaskParam;
    }
}
