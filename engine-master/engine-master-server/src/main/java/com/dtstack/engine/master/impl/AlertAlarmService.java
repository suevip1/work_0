package com.dtstack.engine.master.impl;

import cn.hutool.core.stream.CollectorUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.AlertAlarmModel;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmScopeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.api.enums.OpenStatusEnum;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlarmRuleAddOrDeleteVO;
import com.dtstack.engine.api.vo.alert.AlarmRuleDeleteVO;
import com.dtstack.engine.api.vo.alert.AlarmRuleVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmMapBusinessVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageConditionVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmScopeVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmVO;
import com.dtstack.engine.common.enums.ETaskGroupEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.AlertAlarmDao;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.dao.BaselineTaskTaskDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.SearchTypeEnum;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.faced.sdk.PublicServiceAlertApiClientSdkFaced;
import com.dtstack.engine.master.mapstruct.AlertAlarmStruct;
import com.dtstack.engine.master.utils.CacheKeyUtil;
import com.dtstack.engine.master.utils.ListUtil;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.ScheduleTaskTag;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 2:43 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertAlarmService {

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private AlertAlarmDao alertAlarmDao;

    @Autowired
    private AlertAlarmRuleDao alertAlarmRuleDao;

    @Autowired
    private AlertAlarmStruct alertAlarmStruct;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private BaselineTaskService baselineTaskService;

    @Autowired
    private BaselineTaskTaskDao baselineTaskTaskDao;

    @Autowired
    private ScheduleTaskTagService scheduleTaskTagService;

    @Autowired
    private PublicServiceAlertApiClientSdkFaced publicServiceAlertApiClientSdkFaced;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public PageResult<List<AlertAlarmPageVO>> page(AlertAlarmPageConditionVO vo) {
        String taskName = vo.getTaskName();

        List<Long> alarmIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(taskName)) {
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(vo.getProjectId(),
                    vo.getTaskName(),
                    vo.getAppType(),
                    null, null, SearchTypeEnum.FUZZY.getType(),null,null);

            List<Long> taskIds = taskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(taskIds)) {
                alarmIds.addAll(alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(AlarmRuleBusinessTypeEnum.TASK.getCode(), taskIds));
                List<Long> baseTaskIds = baselineTaskTaskDao.selectByTaskIds(taskIds, vo.getAppType());

                if (CollectionUtils.isNotEmpty(baseTaskIds)) {
                    alarmIds.addAll(alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(AlarmRuleBusinessTypeEnum.BASELINE.getCode(), baseTaskIds));
                }
            } else {
                return new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), 0, Lists.newArrayList());
            }
        }


        AlertAlarmModel alertAlarmModel = new AlertAlarmModel(
                vo.getAlarmTypes(),
                alarmIds,
                vo.getCreateUserId(),
                vo.getAppType(),
                vo.getProjectId(),
                vo.getTenantId(),
                vo.getOpenStatus(),
                vo.getAlarmName(),
                vo.getCurrentPage(),
                vo.getPageSize());

        List<AlertAlarmPageVO> vos = Lists.newArrayList();
        Long count = alertAlarmDao.countByModel(alertAlarmModel);

        if (count > 0) {
            List<AlertAlarm> alertAlarmList = alertAlarmDao.selectPageByModel(alertAlarmModel);
            vos = alertAlarmStruct.alertAlarmListToAlertAlarmPageVOs(alertAlarmList);

            List<Long> ids = alertAlarmList.stream().map(AlertAlarm::getId).collect(Collectors.toList());
            List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(ids,null);
            Map<Long, List<AlertAlarmRule>> alertAlarmRuleMap = alertAlarmRules.stream().collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId));
            Map<Long, List<Long>> receiverIdMap = Maps.newHashMap();
            Map<Long, List<String>> taskNameMap = Maps.newHashMap();
            Map<Long, List<String>> baselineMap = Maps.newHashMap();
            Map<Long, List<Long>> ruleNameMap = Maps.newHashMap();
            Map<Long, List<String>> channelNameMap = Maps.newHashMap();

            fillingMap(alertAlarmRules, receiverIdMap,
                    taskNameMap,baselineMap, ruleNameMap,
                    channelNameMap,vo.getAppType());

            for (AlertAlarmPageVO pageVO : vos) {
                Long id = pageVO.getId();
                pageVO.setReceiverIds(receiverIdMap.get(id));
                pageVO.setTaskNames(taskNameMap.get(id));
                pageVO.setRuleIds(ruleNameMap.get(id));
                pageVO.setChannelNames(channelNameMap.get(id));
                pageVO.setBaselineNames(baselineMap.get(id));

                List<AlertAlarmRule> alarmRuleList = alertAlarmRuleMap.getOrDefault(pageVO.getId(), new ArrayList<>());
                Map<Integer, List<AlertAlarmRule>> alarmRuleMap = alarmRuleList.stream().collect(Collectors.groupingBy(AlertAlarmRule::getBusinessType));
                if (AlarmTypeEnum.TASK.getCode().equals(pageVO.getAlarmType())) {
                    pageVO.setTaskIdList(
                            alarmRuleMap.getOrDefault(AlarmRuleBusinessTypeEnum.TASK.getCode(), new ArrayList<>()).stream()
                                    .map(AlertAlarmRule::getBusinessId)
                                    .collect(Collectors.toList()));
                } else if (AlarmTypeEnum.BASELINE.getCode().equals(pageVO.getAlarmType())) {
                    pageVO.setBaselineIdList(
                            alarmRuleMap.getOrDefault(AlarmRuleBusinessTypeEnum.BASELINE.getCode(), new ArrayList<>()).stream()
                                    .map(AlertAlarmRule::getBusinessId)
                                    .collect(Collectors.toList()));
                }
            }
        }
        return new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), count.intValue(), vos);
    }

    private void fillingMap(List<AlertAlarmRule> alertAlarmRules,
                            Map<Long, List<Long>> receiverIdMap,
                            Map<Long, List<String>> taskNameMap,
                            Map<Long, List<String>> baselineMap,
                            Map<Long, List<Long>> ruleNameMap,
                            Map<Long, List<String>> channelNameMap,
                            Integer appType) {
        Map<Integer, List<AlertAlarmRule>> businessMap = alertAlarmRules.stream().collect(Collectors.groupingBy(AlertAlarmRule::getBusinessType));
        // 查询数据
        for (Integer integer : businessMap.keySet()) {
            List<AlertAlarmRule> businessValues = businessMap.get(integer);
            if (AlarmRuleBusinessTypeEnum.USER.getCode().equals(integer)) {
                receiverIdMap.putAll(businessValues.stream().collect(
                        Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId,
                                Collectors.mapping(AlertAlarmRule::getBusinessId, Collectors.toList()))));
            }

            List<Long> businessIds = businessValues.stream().map(AlertAlarmRule::getBusinessId).distinct().collect(Collectors.toList());
            if (AlarmRuleBusinessTypeEnum.TASK.getCode().equals(integer)) {
                List<ScheduleTaskShade> taskShades = scheduleTaskShadeService.getTaskByIds(businessIds, appType);
                Map<Long, String> nameMap = taskShades.stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, ScheduleTaskShade::getName));
                statistics(businessValues, nameMap, taskNameMap);
            }

            if (AlarmRuleBusinessTypeEnum.RULE.getCode().equals(integer)) {
                ruleNameMap.putAll(businessValues.stream().collect(
                        Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId,
                                Collectors.mapping(AlertAlarmRule::getBusinessId, Collectors.toList()))));
            }

            if (AlarmRuleBusinessTypeEnum.CHANNEL.getCode().equals(integer)) {
                List<ClusterAlertResultDTO> clusterAlertResultDTOS = publicServiceAlertApiClientSdkFaced.listShow();
                Map<Long, String> nameMap = clusterAlertResultDTOS.stream()
                        .collect(Collectors.toMap(e -> e.getAlertId().longValue(), ClusterAlertResultDTO::getAlertGateName));
                statistics(businessValues, nameMap, channelNameMap);
            }

            if (AlarmRuleBusinessTypeEnum.BASELINE.getCode().equals(integer)) {
                List<BaselineTaskDTO> baselineTaskDTOS = baselineTaskService.getBaselineTaskByIds(businessIds);
                Map<Long, String> nameMap = baselineTaskDTOS.stream().collect(Collectors.toMap(BaselineTaskDTO::getId, BaselineTaskDTO::getName));
                statistics(businessValues, nameMap, baselineMap);
            }
        }
    }

    private void statistics(List<AlertAlarmRule> businessValues, Map<Long, String> nameMap, Map<Long, List<String>> taskNameMap) {
        Map<Long, List<String>> statistics = Maps.newHashMap();
        for (AlertAlarmRule businessValue : businessValues) {
            String businessTaskName = nameMap.get(businessValue.getBusinessId());
            if (StringUtils.isNotBlank(businessTaskName)) {
                List<String> taskNameList = statistics.get(businessValue.getAlertAlarmId());
                if (CollectionUtils.isNotEmpty(taskNameList)) {
                    taskNameList.add(businessTaskName);
                } else {
                    taskNameList = Lists.newArrayList();
                    taskNameList.add(businessTaskName);
                }
                statistics.put(businessValue.getAlertAlarmId(),taskNameList);
            }
        }

        taskNameMap.putAll(statistics);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateAlertAlarm(AlertAlarmVO alertAlarmVO) {
        checkAlarm(alertAlarmVO);
        AlertAlarm alertAlarm = toAlertAlarmPo(alertAlarmVO);
        // 处理 alertAlarm
        Long alertAlarmId = processAlertAlarm(alertAlarm);
        // 处理 alertAlarmRule
        processAlertAlarmRule(alertAlarmId, alertAlarmVO);
        //清除redis缓存
        redisTemplate.delete(CacheKeyUtil.AlarmTaskKey.ALARM_TASK);

    }

    private void processAlertAlarmRule(Long alertAlarmId, AlertAlarmVO alertAlarmVO) {
        // 删除前面添加的关系
        alertAlarmRuleDao.deleteByAlertId(alertAlarmId);
        // 添加关系
        List<AlertAlarmRule> alertAlarmRules = Lists.newArrayList();
        // 处理任务告警对象和关联的附加信息（类目，黑名单）
        processTasksAndAttachedInfo(alertAlarmVO, alertAlarmId, alertAlarmRules);
        // 处理基线告警对象
        processBaselines(alertAlarmVO, alertAlarmId, alertAlarmRules);
        // 处理触发方式，失败，停止，成功...
        processTriggerMode(alertAlarmVO, alertAlarmId, alertAlarmRules);
        // 处理通道，钉钉，短信，告警...
        processChannels(alertAlarmVO, alertAlarmId, alertAlarmRules);
        // 处理接收人， 任务责任人，其他接收人，其他用户组
        processReceivers(alertAlarmVO, alertAlarmId, alertAlarmRules);

        if (CollectionUtils.isNotEmpty(alertAlarmRules)) {
            alertAlarmRuleDao.batchInsert(alertAlarmRules);
        }
    }

    private void processBaselines(AlertAlarmVO alertAlarmVO, Long alertAlarmId, List<AlertAlarmRule> alertAlarmRules) {
        List<Long> baselineIds = alertAlarmVO.getBaselineIds();
        if (CollectionUtils.isNotEmpty(baselineIds)) {
            for (Long baselineId : baselineIds) {
                alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, baselineId, AlarmRuleBusinessTypeEnum.BASELINE));
            }
        }
    }

    private void processChannels(AlertAlarmVO alertAlarmVO, Long alertAlarmId, List<AlertAlarmRule> alertAlarmRules) {
        List<Long> channelIds = alertAlarmVO.getChannelIds();
        for (Long channelId : channelIds) {
            alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, channelId, AlarmRuleBusinessTypeEnum.CHANNEL));
        }
    }

    private void processTriggerMode(AlertAlarmVO alertAlarmVO, Long alertAlarmId, List<AlertAlarmRule> alertAlarmRules) {
        List<Long> ruleIds = alertAlarmVO.getRuleIds();
        for (Long ruleId : ruleIds) {
            alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, ruleId, AlarmRuleBusinessTypeEnum.RULE));
        }
    }

    private void processReceivers(AlertAlarmVO alertAlarmVO, Long alertAlarmId,List<AlertAlarmRule> alertAlarmRules) {
        /*
          选了任务责任人，等到告警触发 才根据任务获取责任人
          receiverIds 包含其他接受人 需要保存
         */
        Set<Long> receiverIds = Sets.newHashSet(alertAlarmVO.getReceiverIds());

        if (BooleanUtils.isTrue(alertAlarmVO.getChooseTaskOwner())) {
            alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, -1L, AlarmRuleBusinessTypeEnum.TASK_OWNER_USER));
        }

        receiverIds = receiverIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        for (Long receiverId : receiverIds) {
            alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, receiverId, AlarmRuleBusinessTypeEnum.USER));
        }

        List<Long> otherUserIds = alertAlarmVO.getOtherUserIds();
        if (CollectionUtils.isNotEmpty(otherUserIds)) {
            for (Long otherUserId : otherUserIds) {
                alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, otherUserId, AlarmRuleBusinessTypeEnum.OTHER_USER));
            }
        }

        List<Long> groupIds = alertAlarmVO.getGroupIds();
        if (CollectionUtils.isNotEmpty(groupIds)) {
            for (Long groupId : groupIds) {
                alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, groupId, AlarmRuleBusinessTypeEnum.GROUP));
            }
        }

        if (BooleanUtils.isTrue(alertAlarmVO.isSupportFillData())) {
            alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, -1L, AlarmRuleBusinessTypeEnum.FILL_DATA_ALERT));
        }
    }

    private Long processAlertAlarm(AlertAlarm alertAlarm) {
        Long alertAlarmId = alertAlarm.getId();
        String alertAlarmName = alertAlarm.getName();
        if (alertAlarmId == null || alertAlarmId <= 0) {
            String findName = alertAlarmDao.selectByProjectIdAndName(alertAlarm.getProjectId(), alertAlarmName);
            if (StringUtils.isNotBlank(findName)) {
                throw new RdosDefineException("告警名称已存在");
            }
            // 添加操作
            alertAlarm.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            alertAlarm.setOpenStatus(OpenStatusEnum.OPEN.getCode());
            alertAlarmDao.insert(alertAlarm);
            alertAlarmId = alertAlarm.getId();
        } else {
            AlertAlarm dbAlert = alertAlarmDao.selectByPrimaryKey(alertAlarmId);
            if (dbAlert != null && !alertAlarmName.equals(dbAlert.getName())) {
                String findName = alertAlarmDao.selectByProjectIdAndName(alertAlarm.getProjectId(), alertAlarmName);
                if (StringUtils.isNotBlank(findName)) {
                    throw new RdosDefineException("告警名称已存在");
                }
            }
            alertAlarmDao.updateByPrimaryKeySelective(alertAlarm);
        }
        return alertAlarmId;
    }

    private AlertAlarm toAlertAlarmPo(AlertAlarmVO alertAlarmVO) {
        AlertAlarm alertAlarm = alertAlarmStruct.alertAlarmVOToAlertAlarm(alertAlarmVO);
        Map<String, Object> extraParams = alertAlarmVO.getExtraParamMap();
        if (MapUtils.isNotEmpty(extraParams)) {
            alertAlarm.setExtraParams(JSON.toJSONString(extraParams));
        }
        alertAlarm.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertAlarm.setGmtModified(new Timestamp(System.currentTimeMillis()));
        return alertAlarm;
    }

    private void processTasksAndAttachedInfo(AlertAlarmVO alertAlarmVO, Long alertAlarmId, List<AlertAlarmRule> alertAlarmRules) {
        // 默认范围为选择任务
        if (Objects.isNull(alertAlarmVO.getScope())) {
            alertAlarmVO.setScope(AlarmScopeEnum.SELECT.getCode());
        }

        List<ScheduleTaskShade> filterTaskShades = filterAlarmTask(alertAlarmVO);

        processTaskAlarmRule(alertAlarmVO, alertAlarmId, filterTaskShades, alertAlarmRules);
        processTaskAttachedAlarmRule(alertAlarmVO, alertAlarmId, alertAlarmRules);
    }

    private void processTaskAlarmRule(AlertAlarmVO alertAlarmVO,
                                      Long alertAlarmId,
                                      List<ScheduleTaskShade> filterTaskShades,
                                      List<AlertAlarmRule> alertAlarmRules) {

        if (AlarmScopeEnum.isSelectScope(alertAlarmVO.getScope())) {
            insertTaskAlarmRule(alertAlarmVO.getTaskIds(), alertAlarmRules, alertAlarmId);
            return;
        }
        // 所有任务,类目选择，需要取出项目中、类目下所有任务加进去
        if (AlarmScopeEnum.isALLOrCatalogueScope(alertAlarmVO.getScope()) && CollectionUtils.isNotEmpty(filterTaskShades)) {
            List<Long> targetProjectTaskIds = filterTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            insertTaskAlarmRule(targetProjectTaskIds, alertAlarmRules, alertAlarmId);
        }
    }


    private void processTaskAttachedAlarmRule(AlertAlarmVO alertAlarmVO, Long alertAlarmId, List<AlertAlarmRule> alertAlarmRules) {
        // 选择的类目存下来
        if (AlarmScopeEnum.isCatalogueScope(alertAlarmVO.getScope()) && CollectionUtils.isNotEmpty(alertAlarmVO.getRootNodePIds())) {
            alertAlarmVO.getRootNodePIds().forEach(nodePId -> alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, nodePId, AlarmRuleBusinessTypeEnum.CATALOGUE)));
        }

        // 选择的标签
        if (AlarmScopeEnum.isTagScope(alertAlarmVO.getScope())) {
            alertAlarmVO.getTagIds().forEach(tagId -> alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, tagId, AlarmRuleBusinessTypeEnum.TAG_LIST)));
        }

        // 黑名单保存下来
        if (CollectionUtils.isNotEmpty(alertAlarmVO.getBlacklist())) {
            alertAlarmVO.getBlacklist().forEach(blackTaskId -> alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, blackTaskId, AlarmRuleBusinessTypeEnum.BLACK_LIST)));
        }
    }


    private List<ScheduleTaskShade> filterAlarmTask(AlertAlarmVO alertAlarmVO) {
        if (!AlarmScopeEnum.isALLOrCatalogueScope(alertAlarmVO.getScope())) {
            return Lists.newArrayList();
        }

        if (AlarmScopeEnum.isCatalogueScope(alertAlarmVO.getScope())) {
            return getTasksAndFilterBlacklistByCatalogue(alertAlarmVO,convertAlertTypeToETaskGroupEnum(alertAlarmVO.getAlarmType()));
        }

        if (AlarmScopeEnum.isAllScope(alertAlarmVO.getScope())) {
            return getTasksAndFilterBlacklistByProject(alertAlarmVO,convertAlertTypeToETaskGroupEnum(alertAlarmVO.getAlarmType()));
        }

        if (AlarmScopeEnum.isTagScope(alertAlarmVO.getScope())) {
            return getTasksAndFilterBlacklistByTag(alertAlarmVO,convertAlertTypeToETaskGroupEnum(alertAlarmVO.getAlarmType()));
        }

        return Lists.newArrayList();
    }

    private List<ScheduleTaskShade> getTasksAndFilterBlacklistByTag(AlertAlarmVO alertAlarmVO, Integer taskGroup) {
        if (CollectionUtils.isEmpty(alertAlarmVO.getTagIds())) {
            return new ArrayList<>();
        }

        List<ScheduleTaskTag> tagsByTagIds = scheduleTaskTagService.findTagsByTagIds(alertAlarmVO.getTagIds(),alertAlarmVO.getAppType());

        if (CollectionUtils.isEmpty(tagsByTagIds)) {
            return new ArrayList<>();
        }

        List<Long> allTasks = Lists.newArrayList();
        Map<Long, List<ScheduleTaskTag>> tagMap = tagsByTagIds.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId));

        for (Long task : tagMap.keySet()) {
            List<ScheduleTaskTag> scheduleTaskTags = tagMap.get(task);
            List<Long> taskTagIds = scheduleTaskTags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());

            // tagIds 是否是taskTagIds的子集
            if (ListUtil.isSubList(taskTagIds,alertAlarmVO.getTagIds())) {
                allTasks.add(task);
            }
        }
        
        if (CollectionUtils.isEmpty(allTasks)) {
            return new ArrayList<>();
        }

        List<ScheduleTaskShade> shades = scheduleTaskShadeService.findSimpleTaskByTaskIds(allTasks, alertAlarmVO.getAppType(), taskGroup);
        return filterAndCollectTasks(shades, alertAlarmVO.getBlacklist());
    }

    public Integer convertAlertTypeToETaskGroupEnum(Integer alertType) {
        if (AlarmTypeEnum.TASK.getCode().equals(alertType)) {
            return ETaskGroupEnum.NORMAL_SCHEDULE.getType();
        }
        if (AlarmTypeEnum.MANUAL_TASK.getCode().equals(alertType)) {
            return ETaskGroupEnum.MANUAL.getType();
        }
        if (AlarmTypeEnum.BASELINE.getCode().equals(alertType)) {
            return ETaskGroupEnum.NORMAL_SCHEDULE.getType();
        }
        return null;
    }

    private void insertTaskAlarmRule(List<Long> taskIds, List<AlertAlarmRule> alertAlarmRules, Long alertAlarmId) {
        if (CollectionUtils.isNotEmpty(taskIds)) {
            for (Long taskId : taskIds) {
                alertAlarmRules.add(buildAlertAlarmRule(alertAlarmId, taskId, AlarmRuleBusinessTypeEnum.TASK));
            }
        }
    }

    private List<ScheduleTaskShade> getTasksAndFilterBlacklistByCatalogue(AlertAlarmVO alertAlarmVO,Integer taskGroup) {

        if (CollectionUtils.isEmpty(alertAlarmVO.getNodePIds())) {
            return new ArrayList<>();
        }
        List<ScheduleTaskShade> targetTasks = scheduleTaskShadeService.findSimpleTaskByNodePIds(
                alertAlarmVO.getNodePIds(),
                alertAlarmVO.getProjectId(),
                alertAlarmVO.getAppType(),
                taskGroup
        );

        return filterAndCollectTasks(targetTasks, alertAlarmVO.getBlacklist());
    }


    private List<ScheduleTaskShade> getTasksAndFilterBlacklistByProject(AlertAlarmVO alertAlarmVO,Integer taskGroup) {
        List<ScheduleTaskShade> targetTasks = scheduleTaskShadeService.findSimpleTaskByProjectId(alertAlarmVO.getProjectId(), alertAlarmVO.getAppType(),taskGroup);
        return filterAndCollectTasks(targetTasks, alertAlarmVO.getBlacklist());
    }


    public <T extends ScheduleTaskShade> List<T> filterAndCollectTasks(List<T> targetTasks, List<Long> blacklist) {
        // 筛选掉在黑名单里的 taskId
        if (CollectionUtils.isNotEmpty(blacklist) && CollectionUtils.isNotEmpty(targetTasks)) {
            return targetTasks.stream().filter(targetTask -> !blacklist.contains(targetTask.getTaskId())).collect(Collectors.toList());
        }
        return targetTasks;
    }

    private void checkAlarm(AlertAlarmVO alertAlarmVO) {
        List<Long> receiverIds = alertAlarmVO.getReceiverIds();

        if (StringUtils.isBlank(alertAlarmVO.getName())) {
            throw new RdosDefineException("告警规则名称必填");
        }

        // 选择范围为所有任务或者类目的，这里不判断接收人是否为空
        if (CollectionUtils.isEmpty(receiverIds)
                && !AlarmScopeEnum.isALLOrCatalogueScope(alertAlarmVO.getScope()) && BooleanUtils.isFalse(alertAlarmVO.getChooseTaskOwner())) {
            throw new RdosDefineException("接收人必填");
        }

        List<Long> channelIds = alertAlarmVO.getChannelIds();

        if (CollectionUtils.isEmpty(channelIds)) {
            throw new RdosDefineException("通道必选");
        }

        List<Long> ruleIds = alertAlarmVO.getRuleIds();

        if (CollectionUtils.isEmpty(ruleIds)) {
            throw new RdosDefineException("触发方式必填");
        }

        Integer alarmType = alertAlarmVO.getAlarmType();
        if (AlarmTypeEnum.isTask(alarmType)) {
            List<Long> taskIds = alertAlarmVO.getTaskIds();
            if (CollectionUtils.isEmpty(taskIds) && !AlarmScopeEnum.isALLOrCatalogueScope(alertAlarmVO.getScope())) {
                throw new RdosDefineException("创建任务告警不许选择任务必填");
            }
            if (AlarmScopeEnum.CATALOGUE.getCode().equals(alertAlarmVO.getScope())) {
                if (CollectionUtils.isEmpty(alertAlarmVO.getRootNodePIds()) || CollectionUtils.isEmpty(alertAlarmVO.getNodePIds())) {
                    throw new RdosDefineException("按目录选择，类目 id 不能为空");
                }
            }
        } else if (AlarmTypeEnum.BASELINE.getCode().equals(alarmType)) {
            List<Long> baselineIds = alertAlarmVO.getBaselineIds();
            if (CollectionUtils.isEmpty(baselineIds)) {
                throw new RdosDefineException("创建基线告警不许选择基线必填");
            }
        }

    }

    public AlertAlarmRule buildAlertAlarmRule(Long id, Long receiverId,AlarmRuleBusinessTypeEnum alarmRuleBusinessTypeEnum) {
        if (Objects.isNull(alarmRuleBusinessTypeEnum)) {
            throw new RdosDefineException("告警规则类型不存在");
        }
        AlertAlarmRule alertAlarmRule = new AlertAlarmRule();
        alertAlarmRule.setAlertAlarmId(id);
        alertAlarmRule.setBusinessType(alarmRuleBusinessTypeEnum.getCode());
        alertAlarmRule.setBusinessId(receiverId);
        alertAlarmRule.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertAlarmRule.setGmtModified(new Timestamp(System.currentTimeMillis()));
        alertAlarmRule.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        return alertAlarmRule;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer delete(Long id) {
        alertAlarmRuleDao.deleteByAlertId(id);
        int delete = alertAlarmDao.deleteByPrimaryKey(id);
        redisTemplate.delete(CacheKeyUtil.AlarmTaskKey.ALARM_TASK);
        return delete;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByProjectId(Long projectId) {
        List<Long> ids = alertAlarmDao.selectIdByProjectId(projectId, Collections.emptyList())
                .stream().map(AlertAlarm::getId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ids)) {
            alertAlarmRuleDao.deleteByAlertIds(ids,null,null);
            Integer byIds = alertAlarmDao.deleteByIds(ids);
            redisTemplate.delete(CacheKeyUtil.AlarmTaskKey.ALARM_TASK);
            return byIds;
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer updateOpenStatus(Long id,Integer openStatus) {
        Integer update = alertAlarmDao.updateOpenStatusById(id, openStatus);
        //清除redis缓存
        redisTemplate.delete(CacheKeyUtil.AlarmTaskKey.ALARM_TASK);
        return update;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAlarmTask(List<Long> alertAlarmBusinessIds,Integer appType,AlarmRuleBusinessTypeEnum alarmRuleBusinessTypeEnum) {
        List<Long> alarmIds = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(alarmRuleBusinessTypeEnum.getCode(),
                Lists.newArrayList(alertAlarmBusinessIds));
        if (CollectionUtils.isEmpty(alarmIds)) {
            return;
        }
        List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alarmIds, appType, null);
        // 两种情况 任务删除或者下线
        if (CollectionUtils.isNotEmpty(alertAlarmList)) {
            // 开始删除任务
            alertAlarmRuleDao.deleteByAlertIds(alarmIds, alarmRuleBusinessTypeEnum.getCode(), alertAlarmBusinessIds);
        }
    }

    public AlertAlarmVO getAlertAlarmDetails(Long id) {
        if (id == null) {
            return null;
        }

        AlertAlarm alertAlarm = alertAlarmDao.selectByPrimaryKey(id);

        if (alertAlarm != null) {
            AlertAlarmVO vo = alertAlarmStruct.toAlertAlarmVO(alertAlarm);
            List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(Lists.newArrayList(alertAlarm.getId()),null);

            if (CollectionUtils.isNotEmpty(alertAlarmRules)) {
                vo.setSupportFillData(alertAlarmRules.stream()
                        .anyMatch(a -> AlarmRuleBusinessTypeEnum.FILL_DATA_ALERT.getCode().equals(a.getBusinessType())));
            }

            Map<Integer, List<AlertAlarmRule>> businessMap = alertAlarmRules.stream().collect(Collectors.groupingBy(AlertAlarmRule::getBusinessType));

            for (Integer integer : businessMap.keySet()) {
                List<AlertAlarmRule> businessValues = businessMap.get(integer);
                List<Long> businessIds = businessValues.stream().map(AlertAlarmRule::getBusinessId).distinct().collect(Collectors.toList());
                if (AlarmRuleBusinessTypeEnum.USER.getCode().equals(integer)) {
                    vo.setReceiverIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.OTHER_USER.getCode().equals(integer)) {
                    vo.setOtherUserIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.GROUP.getCode().equals(integer)) {
                    vo.setGroupIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.CATALOGUE.getCode().equals(integer)) {
                    vo.setRootNodePIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.TAG_LIST.getCode().equals(integer)) {
                    vo.setTagIds(businessIds);
                }

                // 只有选择任务的范围才返回 task Id
                if (AlarmRuleBusinessTypeEnum.TASK.getCode().equals(integer)) {
                    vo.setTaskIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.RULE.getCode().equals(integer)) {
                    vo.setRuleIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.CHANNEL.getCode().equals(integer)) {
                    vo.setChannelIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.BASELINE.getCode().equals(integer)) {
                    vo.setBaselineIds(businessIds);
                }

                if (AlarmRuleBusinessTypeEnum.BLACK_LIST.getCode().equals(integer)) {
                    vo.setBlacklist(businessIds);
                }
            }

            JSONObject jsonObject = JSON.parseObject(alertAlarm.getExtraParams());
            vo.setExtraParamMap(jsonObject);
            return vo;
        }

        return null;
    }

    public List<AlertAlarmVO> getAlertAlarmByProjectIdAndName(Long projectId, List<String> names) {
        if (projectId == null) {
            return null;
        }
        List<AlertAlarm> alertAlarms = alertAlarmDao.selectIdByProjectId(projectId, names);
        return alertAlarmStruct.toAlertAlarmVOs(alertAlarms);
    }

    public List<AlertAlarmMapBusinessVO> getAlertAlarmByTaskId(List<Long> taskIds,
                                                               Integer appType,
                                                               Integer businessType,
                                                               Long projectId) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Lists.newArrayList();
        }
        List<AlertAlarmMapBusinessVO> vos = Lists.newArrayList();
        List<Long> alarmIds = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(businessType, taskIds);

        if (CollectionUtils.isNotEmpty(alarmIds)) {
            List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alarmIds,appType,projectId);
            List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(alarmIds,null);
            Map<Long, List<AlertAlarmRule>> rulesMap = alertAlarmRules.stream().collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId));
            for (AlertAlarm alertAlarm : alertAlarmList) {
                AlertAlarmMapBusinessVO alertAlarmMapBusinessVO = alertAlarmStruct.toAlertAlarmMapBusinessVO(alertAlarm);
                alertAlarmMapBusinessVO.setExtraParamMap(JSONObject.parseObject(alertAlarm.getExtraParams()));
                List<AlertAlarmRule> rules = rulesMap.get(alertAlarm.getId());
                if (CollectionUtils.isNotEmpty(rules)) {
                    alertAlarmMapBusinessVO.setSupportFillData(rules.stream()
                            .anyMatch(a -> AlarmRuleBusinessTypeEnum.FILL_DATA_ALERT.getCode().equals(a.getBusinessType())));
                    alertAlarmMapBusinessVO.setAlertAlarmBusinessVOS(alertAlarmStruct.toAlertAlarmBusinessVO(rules));
                }
                vos.add(alertAlarmMapBusinessVO);
            }
        }

        return vos;
    }

    public Integer updateAlarmRule(Long alarmRuleId, Long businessId) {
        if (alarmRuleId == null) {
            return 0;
        }

        AlertAlarmRule alertAlarmRule = new AlertAlarmRule();
        alertAlarmRule.setId(alarmRuleId);
        alertAlarmRule.setBusinessId(businessId);
        return alertAlarmRuleDao.updateByPrimaryKeySelective(alertAlarmRule);
    }

    public Integer updateAlarmRules(List<AlarmRuleVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return 0;
        }

        Integer count = 0;
        for (AlarmRuleVO vo : vos) {
            Integer integer = updateAlarmRule(vo.getAlarmRuleId(), vo.getBusinessId());

            count+=integer;
        }
        //清除redis缓存
        redisTemplate.delete(CacheKeyUtil.AlarmTaskKey.ALARM_TASK);
        return count;
    }


    public List<AlertAlarm> scanningAlertAlarmByIdLimit(Long id, Integer limit,AlarmTypeEnum alarmTypeEnum) {
        return alertAlarmDao.scanningAlertAlarmByIdLimit(id, Optional.ofNullable(alarmTypeEnum).map(AlarmTypeEnum::getCode).orElse(null),limit);
    }

    /**
     * 支持删或者新增告警规则, 同一个事务中执行
     *
     * @param alarmRuleAddOrDeleteVO vo
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean addOrDeleteAlarmRules(AlarmRuleAddOrDeleteVO alarmRuleAddOrDeleteVO) {
        // 先删除再新增
        if (CollectionUtils.isNotEmpty(alarmRuleAddOrDeleteVO.getAlarmRuleDeleteVOS())) {
            for (AlarmRuleDeleteVO alarmRuleDeleteVO : alarmRuleAddOrDeleteVO.getAlarmRuleDeleteVOS()) {
                if (CollectionUtils.isEmpty(alarmRuleDeleteVO.getAlarmIds())) {
                    throw new RdosDefineException("需要删除的告警 id 不能为空");
                }
                alertAlarmRuleDao.deleteByCondition(alarmRuleDeleteVO.getAlarmIds(), alarmRuleDeleteVO.getBusinessTypes(), alarmRuleDeleteVO.getBusinessIds());
            }
        }

        // 新增
        if (CollectionUtils.isNotEmpty(alarmRuleAddOrDeleteVO.getAlarmRuleAddVOS())) {
            List<AlertAlarmRule> alarmRuleList = alarmRuleAddOrDeleteVO.getAlarmRuleAddVOS().stream()
                    .filter(vo -> Objects.nonNull(vo.getAlarmId()) && Objects.nonNull(vo.getBusinessType()) && Objects.nonNull(vo.getBusinessId()))
                    .map(vo -> {
                        AlarmRuleBusinessTypeEnum businessTypeEnum = AlarmRuleBusinessTypeEnum.getByCode(vo.getBusinessType());
                        return buildAlertAlarmRule(vo.getAlarmId(), vo.getBusinessId(), businessTypeEnum);
                    }).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(alarmRuleList)) {
                alertAlarmRuleDao.batchInsert(alarmRuleList);
            }
        }
        redisTemplate.delete(CacheKeyUtil.AlarmTaskKey.ALARM_TASK);
        return Boolean.TRUE;
    }

    public List<AlertAlarmScopeVO> getAlarmByProjectIdAndScope(Long projectId, Integer appType, List<Integer> scopes) {
        List<AlertAlarmScopeVO> result;
        List<AlertAlarm> alertAlarms = alertAlarmDao.listByProjectIdAndAppTypeAndScope(projectId, appType, scopes);

        result = alertAlarmStruct.toAlertAlarmScopeVOs(alertAlarms);

        for (AlertAlarmScopeVO scopeVO : result) {
            // 如果选择范围是目录，返回目录 id
            if (AlarmScopeEnum.isCatalogueScope(scopeVO.getScope())) {
                List<AlertAlarmRule> catalogues = alertAlarmRuleDao.selectByAlarmIds(Lists.newArrayList(scopeVO.getId()), AlarmRuleBusinessTypeEnum.CATALOGUE.getCode());
                if (CollectionUtils.isNotEmpty(catalogues)) {
                    scopeVO.setNodePIds(catalogues.stream().map(AlertAlarmRule::getBusinessId).collect(Collectors.toList()));
                }
            }
            // 如果选择范围是目录和全部，返回黑名单 id
            if (AlarmScopeEnum.isALLOrCatalogueScope(scopeVO.getScope())) {
                List<AlertAlarmRule> blackList = alertAlarmRuleDao.selectByAlarmIds(Lists.newArrayList(scopeVO.getId()), AlarmRuleBusinessTypeEnum.BLACK_LIST.getCode());
                if (CollectionUtils.isNotEmpty(blackList)) {
                    scopeVO.setBlacklist(blackList.stream().map(AlertAlarmRule::getBusinessId).collect(Collectors.toList()));
                }
            }

            if (AlarmScopeEnum.isTagScope(scopeVO.getScope())) {
                List<AlertAlarmRule> tagList = alertAlarmRuleDao.selectByAlarmIds(Lists.newArrayList(scopeVO.getId()), AlarmRuleBusinessTypeEnum.TAG_LIST.getCode());
                if (CollectionUtils.isNotEmpty(tagList)) {
                    scopeVO.setTagIds(tagList.stream().map(AlertAlarmRule::getBusinessId).collect(Collectors.toList()));
                }
            }
        }

        return result;
    }

}
