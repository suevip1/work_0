package com.dtstack.engine.master.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.common.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.enums.DingTypeEnums;
import com.dtstack.engine.common.enums.IsDefaultEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.AlertTriggerRecordDao;
import com.dtstack.engine.dao.AlertTriggerRecordReceiveDao;
import com.dtstack.engine.dao.AlertTriggerRecordRepeatSendDao;
import com.dtstack.engine.dao.BaselineJobJobDao;
import com.dtstack.engine.dao.BaselineTaskBatchDao;
import com.dtstack.engine.master.faced.sdk.PublicServiceAlertApiClientSdkFaced;
import com.dtstack.engine.master.faced.sdk.PublicServiceNotifyApiClientSdkFaced;
import com.dtstack.engine.master.impl.BaselineTaskService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.AlertRule;
import com.dtstack.engine.po.AlertTriggerRecord;
import com.dtstack.engine.po.AlertTriggerRecordReceive;
import com.dtstack.engine.po.AlertTriggerRecordRepeatSend;
import com.dtstack.engine.po.BaselineJob;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.AlarmSendParam;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.NotifyRecordParam;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;
import com.dtstack.pubsvc.sdk.alert.channel.dto.UserMessageDTO;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 5:54 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractStrategy implements AlterStrategy {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategy.class);

    @Autowired
    protected UicUserApiClient uicUserApiClient;

    @Autowired
    private AlertTriggerRecordRepeatSendDao alertTriggerRecordRepeatSendDao;

    @Autowired
    protected AlertTriggerRecordDao alertTriggerRecordDao;

    @Autowired
    protected AlertTriggerRecordReceiveDao alertTriggerRecordReceiveDao;

    @Autowired
    protected ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Autowired
    protected AuthCenterAPIClient authCenterAPIClient;

    @Autowired
    protected UicTenantApiClient uicTenantApiClient;

    @Autowired
    protected BaselineTaskService baselineTaskService;

    @Autowired
    protected BaselineJobJobDao baselineJobJobDao;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected BaselineTaskBatchDao baselineTaskBatchDao;

    @Autowired
    protected PublicServiceNotifyApiClientSdkFaced publicServiceNotifyApiClientSdkFaced;

    @Autowired
    protected PublicServiceAlertApiClientSdkFaced publicServiceAlertApiClientSdkFaced;

    private final String EXTERNAL_RECEIVER_LIST = "externalReceiverList";

    private static final String WEBHOOK = "webhook";

    private static final String EXTERNAL_RECEIVER = "externalReceiverList";

    private static final String MSGTYPE_ENV_KEY = "msgtype";

    private static final String DT_RECEIVER_EMAILS_ENV_KEY = "DT_RECEIVER_EMAILS";

    private static final String EXTERNAL_RECEIVER_EMAILS_ENV_KEY = "EXTERNAL_RECEIVER_EMAILS";

    protected void sendAlarm(AlertAlarm alertAlarm, List<AlertAlarmRule> rules, AlertRule alertRule, AlterEventContext context) {
        try {
            // 避免相同告警规则同一个jobId重复发送
            String jobId = context.getJobId();
            if (StringUtils.isNotBlank(jobId)) {
                List<AlertTriggerRecordRepeatSend> alertTriggerRecordRepeatSends = alertTriggerRecordRepeatSendDao.selectByJobIdAndRuleId(jobId, alertAlarm.getId());
                if (alertTriggerRecordRepeatSends.size() > 0) {
                    return;
                }
            }

            // 发送人
            List<UserMessageDTO> receivers = getReceivers(rules,context);
            String extraParams = alertAlarm.getExtraParams();
            if (StringUtils.isNotBlank(extraParams)) {
                JSONObject extraParamJson = JSON.parseObject(extraParams);
                String externalReceiverList = extraParamJson.getString(EXTERNAL_RECEIVER_LIST);

                if (StringUtils.isNotBlank(externalReceiverList)) {
                    Iterable<String> split = Splitter.on(",").omitEmptyStrings().split(externalReceiverList);
                    for (String receiver : split) {
                        UserMessageDTO userMessageDTO = new UserMessageDTO();
                        userMessageDTO.setUserId(0L);
                        userMessageDTO.setUsername(receiver);
                        userMessageDTO.setTelephone(receiver);
                        userMessageDTO.setEmail(receiver);
                        receivers.add(userMessageDTO);
                    }
                }
            }
            if (CollectionUtils.isEmpty(receivers)) {
                LOGGER.info("jobId {} not fount receivers",jobId);
                return;
            }

            // 发送通道
            List<ClusterAlertResultDTO> alertChannels = getAlertChannel(rules);
            if (CollectionUtils.isEmpty(alertChannels)) {
                LOGGER.info("jobId {} not fount alertChannels",jobId);
                return;
            }

            Map<String,Object> replaceParamMap = Maps.newHashMap();
            String content = getContent(alertAlarm,alertRule,context,replaceParamMap);

            // 调用业务中心接口发送告警
            long start = System.currentTimeMillis();
            LOGGER.info("jobId {} send request alarm. alter alarm id {} ",jobId,alertAlarm.getId());
            pushAlarm(alertAlarm, alertRule, context, receivers, alertChannels, replaceParamMap, content);

            if (StringUtils.isNotBlank(jobId)) {
                AlertTriggerRecordRepeatSend alertTriggerRecordRepeatSend = new AlertTriggerRecordRepeatSend();
                alertTriggerRecordRepeatSend.setJobId(jobId);
                // 同一条的告警规则只会触发一次告警
                alertTriggerRecordRepeatSend.setRuleId(alertAlarm.getId());
                alertTriggerRecordRepeatSend.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
                alertTriggerRecordRepeatSendDao.insert(alertTriggerRecordRepeatSend);
            }
            // 插入一条告警记录
            insertAlertTriggerRecord(alertAlarm,alertRule,context,alertChannels,receivers,content);

            LOGGER.info("jobId {} send alter success alter alarm id {} ,time {}",jobId,alertAlarm.getId(),System.currentTimeMillis()-start);
        } catch (Throwable e) {
            LOGGER.error("alarm:{} sendAlarm error:{}", alertAlarm.getId(), e.getMessage(), e);
        }
    }

    private List<UserMessageDTO> fillExternalReceiverList(String extraParams) {
        JSONObject extraParamJson = JSON.parseObject(extraParams);
        String externalReceiverList = extraParamJson.getString(EXTERNAL_RECEIVER_LIST);
        if (StringUtils.isBlank(externalReceiverList)) {
            return new ArrayList<>();
        }

        List<String> extraReceiver = Splitter.on(",").splitToList(externalReceiverList);
        return extraReceiver.stream().map(receiver->{
            UserMessageDTO userMessageDTO = new UserMessageDTO();
            userMessageDTO.setUserId(0L);
            userMessageDTO.setUsername(receiver);
            userMessageDTO.setTelephone(receiver);
            userMessageDTO.setEmail(receiver);
            return userMessageDTO;
        }).collect(Collectors.toList());
    }

    private List<UserMessageDTO> getReceivers(List<AlertAlarmRule> rules,AlterEventContext context) {
        boolean sendToTaskOwner = rules.stream()
                .anyMatch(alertAlarmRule -> AlarmRuleBusinessTypeEnum.TASK_OWNER_USER.getCode().equals(alertAlarmRule.getBusinessType()));

        List<Long> userIds = new ArrayList<>();
        if (sendToTaskOwner) {
            getTaskOwnerUserId(context).ifPresent(userIds::add);
        }
        rules.stream()
                .filter(e -> AlarmRuleBusinessTypeEnum.USER.getCode().equals(e.getBusinessType()))
                .forEach(r -> userIds.add(r.getBusinessId()));
        return getUserInfo(userIds);
    }

    private List<ClusterAlertResultDTO> getAlertChannel(List<AlertAlarmRule> entryValue) {
        List<Integer> defaultChannelTypes = new ArrayList<>();
        List<Long> customizeChannelIds = new ArrayList<>();

        for (AlertAlarmRule alertAlarmRule : entryValue) {
            if (AlarmRuleBusinessTypeEnum.CHANNEL.getCode().equals(alertAlarmRule.getBusinessType())) {
                Long businessId = alertAlarmRule.getBusinessId();
                if (AlertGateTypeEnum.SMS.getMockId().equals(businessId)) {
                    defaultChannelTypes.add(AlertGateTypeEnum.SMS.getType());
                } else if (AlertGateTypeEnum.MAIL.getMockId().equals(businessId)) {
                    defaultChannelTypes.add(AlertGateTypeEnum.MAIL.getType());
                } else if (AlertGateTypeEnum.DINGDING.getMockId().equals(businessId)) {
                    defaultChannelTypes.add(AlertGateTypeEnum.DINGDING.getType());
                } else {
                    customizeChannelIds.add(businessId);
                }
            }
        }

        List<ClusterAlertResultDTO> all = publicServiceAlertApiClientSdkFaced.listShow();

        List<ClusterAlertResultDTO> defaultChannels = all.stream()
                .filter(e -> IsDefaultEnum.DEFAULT.getType().equals(e.getIsDefault()))
                .filter(e -> defaultChannelTypes.contains(e.getAlertGateType()))
                .collect(Collectors.toList());

        List<ClusterAlertResultDTO> customizeChannels = all.stream()
                .filter(e -> customizeChannelIds.contains(e.getAlertId().longValue()))
                .collect(Collectors.toList());

        defaultChannels.addAll(customizeChannels);

        return defaultChannels;
    }

    private void pushAlarm(AlertAlarm alertAlarm,
                           AlertRule alertRule,
                           AlterEventContext context,
                           List<UserMessageDTO> receivers,
                           List<ClusterAlertResultDTO> alertChannels,
                           Map<String, Object> replaceParamMap,
                           String content) {

        // env
        Map<String, Object> env = Maps.newHashMap();
        setEnv(env, alertAlarm, receivers);

        // alertGateSources
        List<String> alertGateSources = new ArrayList<>();
        alertChannels.forEach(e -> alertGateSources.add(e.getAlertGateSource()));

        AlarmSendParam alarmSendParam = new AlarmSendParam();
        alarmSendParam.setTitle(getTitle(alertRule, replaceParamMap));
        alarmSendParam.setContent(content);
        alarmSendParam.setEnv(env);
        alarmSendParam.setWebhook(getWebhook(alertAlarm));
        alarmSendParam.setReceivers(receivers);
        alarmSendParam.setTenantId(alertAlarm.getTenantId());
        alarmSendParam.setProjectId(alertAlarm.getProjectId());
        alarmSendParam.setUserId(alertAlarm.getCreateUserId());
        alarmSendParam.setStatus(context.getStatus());
        alarmSendParam.setAppType(context.getAppType());
        alarmSendParam.setAlertGateSources(alertGateSources);

        NotifyRecordParam notifyRecordParam = new NotifyRecordParam();
        notifyRecordParam.setContent(content);
        notifyRecordParam.setAppType(context.getAppType());
        notifyRecordParam.setTenantId(alertAlarm.getTenantId());
        notifyRecordParam.setStatus(0);

        Long contendId = publicServiceNotifyApiClientSdkFaced.generateContent(notifyRecordParam);

        alarmSendParam.setContentId(contendId);
        publicServiceNotifyApiClientSdkFaced.sendAlarmNew(alarmSendParam);
    }

    private Optional<Long> getTaskOwnerUserId(AlterEventContext context) {
        //告警配置中勾选任务责任人，不管是哪个任务触发了告警规则,仅需要给该任务的告警责任人发送告警即可
        //多选 为 任务责任人 + 其他
        Long ownerUserId = context.getOwnerUserId();

        if (ownerUserId != null) {
            return Optional.of(ownerUserId);
        } else {
            Long taskId = context.getTaskId();
            Integer appType = context.getAppType();
            ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.findTaskId(taskId, Deleted.NORMAL.getStatus(), appType);
            if (null != scheduleTaskShade) {
                return Optional.ofNullable(scheduleTaskShade.getOwnerUserId());
            }
            return Optional.empty();
        }

    }

    private void setEnv(Map<String, Object> env, AlertAlarm alertAlarm, List<UserMessageDTO> receivers) {
        // 钉钉告警
        env.put(MSGTYPE_ENV_KEY, DingTypeEnums.TEXT.getMsg());
        // 自定义 jar
        if (CollectionUtils.isNotEmpty(receivers)) {
            List<String> dtReceiverEmails = receivers.stream().map(UserMessageDTO::getEmail).collect(Collectors.toList());
            env.put(DT_RECEIVER_EMAILS_ENV_KEY, dtReceiverEmails);
        }
        List<String> externalReceiver = getExternalReceiver(alertAlarm);
        if (CollectionUtils.isNotEmpty(externalReceiver)) {
            env.put(EXTERNAL_RECEIVER_EMAILS_ENV_KEY, externalReceiver);
        }
    }


    private void insertAlertTriggerRecord(AlertAlarm alertAlarm,
                                          AlertRule alertRule,
                                          AlterEventContext context,
                                          List<ClusterAlertResultDTO> alertChannels,
                                          List<UserMessageDTO> receivers,
                                          String content) {

        if (StringUtils.isBlank(context.getTaskName()) && !AlarmTypeEnum.BASELINE.getCode().equals(alertAlarm.getAlarmType())) {
            ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(context.getTaskId(), context.getAppType());
            if (null != scheduleTaskShade) {
                context.setTaskName(scheduleTaskShade.getName());
                context.setTaskType(scheduleTaskShade.getTaskType());
                context.setOwnerUserId(scheduleTaskShade.getOwnerUserId());
            }
        }

        AlertTriggerRecord alertTriggerRecord = new AlertTriggerRecord();
        alertTriggerRecord.setProjectId(alertAlarm.getProjectId());
        alertTriggerRecord.setTenantId(alertAlarm.getTenantId());
        alertTriggerRecord.setAppType(alertAlarm.getAppType());
        alertTriggerRecord.setTriggerTime(new Timestamp(DateUtil.getTodayStart(System.currentTimeMillis(), "MS")));
        alertTriggerRecord.setRuleId(alertRule.getId());
        alertTriggerRecord.setRuleName(alertRule.getName());
        alertTriggerRecord.setAlarmType(alertAlarm.getAlarmType());
        alertTriggerRecord.setTaskName(context.getTaskName());
        alertTriggerRecord.setAlertAlarmName(alertAlarm.getName());
        alertTriggerRecord.setTaskType(context.getTaskType());
        alertTriggerRecord.setAlertAlarmId(alertAlarm.getId());
        alertTriggerRecord.setAlertChannelName(Joiner.on(",").join(alertChannels.stream().map(ClusterAlertResultDTO::getAlertGateName).collect(Collectors.toList())));
        alertTriggerRecord.setOwnerUserName(getOwnerUserName(context.getOwnerUserId(),receivers));
        alertTriggerRecord.setOwnerUserId(context.getOwnerUserId());
        alertTriggerRecord.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        alertTriggerRecord.setGmtModified(new Timestamp(System.currentTimeMillis()));
        alertTriggerRecord.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertTriggerRecord.setContent(content);

        BaselineJob baselineJob = context.getBaselineJob();
        if (baselineJob != null) {
            alertTriggerRecord.setBaselineName(baselineJob.getName());
        }

        alertTriggerRecordDao.insert(alertTriggerRecord);

        List<AlertTriggerRecordReceive> alertTriggerRecordReceiveList = Lists.newArrayList();

        // 插入接收人
        for (UserMessageDTO receiver : receivers) {
            AlertTriggerRecordReceive alertTriggerRecordReceive = new AlertTriggerRecordReceive();
            alertTriggerRecordReceive.setAlertTriggerRecordId(alertTriggerRecord.getId());
            alertTriggerRecordReceive.setReceiveUserName(receiver.getUsername());
            alertTriggerRecordReceive.setReceiveUserId(receiver.getUserId());
            alertTriggerRecordReceive.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            alertTriggerRecordReceiveList.add(alertTriggerRecordReceive);
        }

        if (CollectionUtils.isNotEmpty(alertTriggerRecordReceiveList)) {
            alertTriggerRecordReceiveDao.batchInsert(alertTriggerRecordReceiveList);
        }
    }

    private String getOwnerUserName(Long ownerUserId, List<UserMessageDTO> receivers) {
        for (UserMessageDTO receiver : receivers) {
            if (receiver.getUserId().equals(ownerUserId)) {
                return receiver.getUsername();
            }
        }
        try {
            ApiResponse<List<UICUserVO>> uicUser = uicUserApiClient.getByUserIds(Lists.newArrayList(ownerUserId));

            if (uicUser.success) {
                List<UICUserVO> data = uicUser.getData();

                if (CollectionUtils.isNotEmpty(data)) {
                    UICUserVO uicUserVO = data.get(0);
                    return uicUserVO.getUserName();
                }
            }
        } catch (Exception e) {
            LOGGER.error("pubservice getByUserIds error:{}", e.getMessage(), e);
        }
        return "";
    }

    private String getWebhook(AlertAlarm alertAlarm) {
        String extraParams = alertAlarm.getExtraParams();
        JSONObject jsonObject = JSON.parseObject(extraParams);
        return jsonObject.getString(WEBHOOK);
    }

    private List<String> getExternalReceiver(AlertAlarm alertAlarm) {
        String extraParams = alertAlarm.getExtraParams();
        JSONObject jsonObject = JSON.parseObject(extraParams);
        return jsonObject.getObject(EXTERNAL_RECEIVER, TypeReference.LIST_STRING);
    }


    private List<UserMessageDTO> getUserInfo(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            LOGGER.warn("alter get user info , userId is null");
            return Lists.newArrayList();
        }

        ApiResponse<List<UICUserVO>> uicUser = uicUserApiClient.getByUserIds(userIds);

        List<UserMessageDTO> userMessageDTOS = Lists.newArrayList();
        if (uicUser.success) {
            List<UICUserVO> data = uicUser.getData();

            for (UICUserVO datum : data) {
                UserMessageDTO userMessageDTO = new UserMessageDTO();
                userMessageDTO.setUserId(datum.getUserId());
                userMessageDTO.setUsername(datum.getUserName());
                userMessageDTO.setEmail(datum.getEmail());
                userMessageDTO.setTelephone(datum.getPhone());
                userMessageDTOS.add(userMessageDTO);
            }
        }
        return userMessageDTOS;
    }

    protected abstract String getContent(AlertAlarm alertAlarm, AlertRule alertRule, AlterEventContext context,Map<String,Object> replaceParamMap);

    protected abstract String getTitle(AlertRule alertRule,Map<String,Object> replaceParamMap);
}
