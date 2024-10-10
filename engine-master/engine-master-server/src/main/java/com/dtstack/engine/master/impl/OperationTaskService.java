package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.task.TaskPageVO;
import com.dtstack.engine.api.vo.task.TaskReturnPageVO;
import com.dtstack.engine.api.vo.task.TaskSearchNameVO;
import com.dtstack.engine.api.vo.task.TaskTimeVO;
import com.dtstack.engine.common.callback.Callback;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.TaskPageDTO;
import com.dtstack.engine.dto.TaskTimeDTO;
import com.dtstack.engine.master.enums.PriorityEnum;
import com.dtstack.engine.master.mapstruct.OperationTaskStruct;
import com.dtstack.engine.master.utils.CheckUtils;
import com.dtstack.engine.master.utils.ListUtil;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ScheduleTaskPriority;
import com.dtstack.engine.po.ScheduleTaskTag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName OperationTaskService
 * @date 2022/7/18 5:12 PM
 */
@Service
public class OperationTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationTaskService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private OperationTaskStruct operationTaskStruct;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private ScheduleTaskTagService scheduleTaskTagService;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;


    public PageResult<List<TaskReturnPageVO>> page(TaskPageVO vo) {
        Integer totalCount = 0;
        PageResult<List<TaskReturnPageVO>> pageResult = new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), totalCount, Lists.newArrayList());
        TaskPageDTO taskPageDTO = getTaskPageDTO(vo);
        if (taskPageDTO == null) {
            return pageResult;
        }

        totalCount =  scheduleTaskShadeDao.countTaskByTaskPageDTO(taskPageDTO);
        if (totalCount > 0) {
            List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.pageTaskByTaskPageDTO(taskPageDTO);

            if (CollectionUtils.isNotEmpty(scheduleTaskShades)) {
                List<TaskReturnPageVO> taskReturnPageVOS = getTaskReturnPageVOS(scheduleTaskShades,vo.getAppType(),taskPageDTO.getFlowMap(),(param)->
                        Optional.of(buildTaskReturnPageVO(param.groupInfo,param.userMap,param.flowMap,param.tagMaps,param.priorityMap,param.scheduleTaskShade)));

                pageResult.setData(taskReturnPageVOS);
                pageResult.setTotalCount(totalCount);
            }
        }

        return pageResult;
    }

    private TaskPageDTO getTaskPageDTO(TaskPageVO vo) {
        TaskPageDTO taskPageDTO = operationTaskStruct.taskPageVOToTaskPageDTO(vo);
        List<Long> calenderIds = vo.getCalenderIds();
        Map<Long, List<Long>> flowMap = Maps.newHashMap();
        Set<Long> selectTaskIds = Sets.newHashSet();
        Set<Long> allTaskIds = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(calenderIds)) {
            List<Long> taskIds = calenderService.getAllTaskByCalenderId(calenderIds, vo.getAppType());
            if (CollectionUtils.isEmpty(taskIds)) {
                return null;
            }

            allTaskIds.addAll(taskIds);
        }

        TaskSearchNameVO searchName = vo.getSearchName();
        Long ownerId = vo.getOwnerId();
        String businessType = vo.getBusinessType();
        if ((searchName != null && searchName.getSearchType() != null && StringUtils.isNotEmpty(searchName.getTaskName()))
                || ownerId != null
                || CollectionUtils.isNotEmpty(vo.getTaskTypes())
                || CollectionUtils.isNotEmpty(vo.getScheduleStatus())
                || StringUtils.isNotBlank(businessType)) {
            String taskName = searchName == null ? null : searchName.getTaskName();
            Integer searchType = searchName == null ? null : searchName.getSearchType();
            List<Long> taskIds = scheduleTaskShadeDao.searchTaskIdBySearchName(CheckUtils.handName(searchType, taskName), searchType,
                    taskPageDTO.getAppType(), taskPageDTO.getProjectId(), taskPageDTO.getTenantId(), vo.getOwnerId(),businessType,
                    vo.getTaskTypes(),vo.getScheduleStatus(),IsDeletedEnum.NOT_DELETE.getType());

            if (CollectionUtils.isEmpty(taskIds)) {
                return null;
            }

            if (CollectionUtils.isEmpty(allTaskIds)) {
                allTaskIds.addAll(taskIds);
            } else {
                allTaskIds.retainAll(taskIds);

                if (CollectionUtils.isEmpty(allTaskIds)) {
                    return null;
                }
            }
        }

        if (CollectionUtils.isNotEmpty(vo.getPriorityList())) {
            List<Integer> priorityList = Lists.newArrayList(vo.getPriorityList());
            List<Long> priorityTaskId = Lists.newArrayList();
            if (priorityList.contains(PriorityEnum.one.getNumber())) {
                priorityTaskId.addAll(scheduleTaskShadeDao.findPriorityOneTask(vo.getAppType())) ;
            }

            priorityList.remove(PriorityEnum.one.getNumber());

            if (CollectionUtils.isNotEmpty(priorityList)) {
                priorityTaskId.addAll(scheduleTaskPriorityService.findTaskIdByPriorities(priorityList));
            }

            if (CollectionUtils.isEmpty(priorityTaskId)) {
                return null;
            }

            if (CollectionUtils.isEmpty(allTaskIds)) {
                allTaskIds.addAll(priorityTaskId);
            } else {
                allTaskIds.retainAll(priorityTaskId);

                if (CollectionUtils.isEmpty(allTaskIds)) {
                    return null;
                }
            }
        }

        if (CollectionUtils.isNotEmpty(allTaskIds)) {
            List<ScheduleTaskShade> scheduleTaskShadeList = scheduleTaskShadeDao.
                    listSimpleFiledByTaskIds(allTaskIds, IsDeletedEnum.NOT_DELETE.getType(), taskPageDTO.getAppType());

            for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShadeList) {
                if (scheduleTaskShade.getFlowId() != 0) {
                    selectTaskIds.add(scheduleTaskShade.getFlowId());
                    List<Long> taskIds = flowMap.computeIfAbsent(scheduleTaskShade.getFlowId(), key -> Lists.newArrayList());
                    taskIds.add(scheduleTaskShade.getTaskId());
                    flowMap.put(scheduleTaskShade.getFlowId(),taskIds);
                } else {
                    selectTaskIds.add(scheduleTaskShade.getTaskId());
                }
            }
        }

        List<Long> tagIds = vo.getTagIds();
        if (CollectionUtils.isNotEmpty(tagIds)) {
            List<ScheduleTaskTag> tagsByTagIds = scheduleTaskTagService.findTagsByTagIds(tagIds,vo.getAppType());

            if (CollectionUtils.isEmpty(tagsByTagIds)) {
                return null;
            }
            List<Long> tagTaskIds = Lists.newArrayList();
            Map<Long, List<ScheduleTaskTag>> tagMap = tagsByTagIds.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId));
            boolean flg = Boolean.TRUE;
            for (Long task : tagMap.keySet()) {
                List<ScheduleTaskTag> scheduleTaskTags = tagMap.get(task);
                List<Long> taskTagIds = scheduleTaskTags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());
                // tagIds 是否是taskTagIds的子集
                if (ListUtil.isSubList(taskTagIds,tagIds)) {
                    tagTaskIds.add(task);
                    flg= Boolean.FALSE;
                }
            }
            if (flg) {
                return null;
            }
            if (CollectionUtils.isNotEmpty(selectTaskIds)) {
                // 保留 selectTaskIds 中也存在于 tagTaskIds 的记录
                selectTaskIds.retainAll(tagTaskIds);
            } else {
                // 添加 tagTaskIds
                selectTaskIds.addAll(tagTaskIds);
            }
            if (CollectionUtils.isEmpty(selectTaskIds)) {
                return null;
            }
        }

        taskPageDTO.setFlowMap(flowMap);
        taskPageDTO.setTaskIds(selectTaskIds);
        setTime(vo, taskPageDTO);
        return taskPageDTO;
    }

    private List<TaskReturnPageVO> buildTaskReturnPageVOs(List<ScheduleTaskShade> scheduleTaskShades,
                                                          Map<Long, ResourceGroupDetail> groupInfo,
                                                          Map<Long, User> userMap,
                                                          Map<Long, List<Long>> tagMaps,
                                                          Map<Long, List<Long>> flowMap,
                                                          Map<String, List<ScheduleTaskPriority>> priorityMap,
                                                          Callback<TaskParam, Optional<TaskReturnPageVO>> callback) {
        List<TaskReturnPageVO> taskReturnPageVOS = Lists.newArrayList();
        for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShades) {
            TaskParam taskParam = new TaskParam(groupInfo,userMap,flowMap,tagMaps,priorityMap,scheduleTaskShade);
            Optional<TaskReturnPageVO> taskReturnPageVO = callback.callback(taskParam);
            taskReturnPageVO.ifPresent(taskReturnPageVOS::add);
        }
        return taskReturnPageVOS;
    }

    private TaskReturnPageVO buildTaskReturnPageVO(Map<Long, ResourceGroupDetail> groupInfo,
                                       Map<Long, User> userMap,
                                       Map<Long, List<Long>> flowMap,
                                       Map<Long, List<Long>> tagMaps,
                                       Map<String, List<ScheduleTaskPriority>> priorityMap,
                                       ScheduleTaskShade scheduleTaskShade) {
        if (EScheduleJobType.WORK_FLOW.getType().equals(scheduleTaskShade.getTaskType())
                || EScheduleJobType.ALGORITHM_LAB.getVal().equals(scheduleTaskShade.getTaskType())) {
            TaskReturnPageVO taskReturnPageVO = getTaskReturnPageVO(groupInfo, userMap,tagMaps,priorityMap, scheduleTaskShade);
            List<Long> taskIds = flowMap.get(scheduleTaskShade.getTaskId());
            if (CollectionUtils.isNotEmpty(taskIds)) {
                List<TaskReturnPageVO> flowVOs = Lists.newArrayList();
                List<ScheduleTaskShade> shades = scheduleTaskShadeDao.listByTaskIds(taskIds, IsDeletedEnum.NOT_DELETE.getType(), scheduleTaskShade.getAppType());
                Map<Long, ScheduleTaskShade> taskShadeMap = shades.stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));

                for (Long taskId : taskIds) {
                    ScheduleTaskShade shade = taskShadeMap.get(taskId);
                    if (shade != null) {
                        flowVOs.add(getTaskReturnPageVO(groupInfo, userMap,tagMaps,priorityMap, shade));
                    }
                }
                taskReturnPageVO.setFlowVOs(flowVOs);
            }
            return taskReturnPageVO;
        } else {
            return getTaskReturnPageVO(groupInfo, userMap,tagMaps,priorityMap, scheduleTaskShade);
        }
    }

    @NotNull
    private TaskReturnPageVO getTaskReturnPageVO(Map<Long, ResourceGroupDetail> groupInfo,
                                                 Map<Long, User> userMap,
                                                 Map<Long, List<Long>> tagMaps,
                                                 Map<String, List<ScheduleTaskPriority>> priorityMap,
                                                 ScheduleTaskShade scheduleTaskShade) {
        TaskReturnPageVO vo = new TaskReturnPageVO();
        vo.setAppType(scheduleTaskShade.getAppType());
        vo.setTaskId(scheduleTaskShade.getTaskId());
        vo.setIsDeleted(scheduleTaskShade.getIsDeleted());
        vo.setTaskName(scheduleTaskShade.getName());
        vo.setTaskType(scheduleTaskShade.getTaskType());
        vo.setTagIds(tagMaps.get(scheduleTaskShade.getTaskId()));
        vo.setPeriodType(scheduleTaskShade.getPeriodType());
        vo.setScheduleStatus(scheduleTaskShade.getScheduleStatus());

        Timestamp gmtModified = scheduleTaskShade.getGmtModified();
        if (gmtModified != null) {
            long time = gmtModified.getTime();
            vo.setSubmitTime(DateUtil.getDate(time,DateUtil.STANDARD_DATETIME_FORMAT));
        }

        ResourceGroupDetail resourceGroupDetail = groupInfo.get(scheduleTaskShade.getResourceId());
        if (resourceGroupDetail != null) {
            vo.setResourceId(resourceGroupDetail.getResourceId());
            vo.setResourceName(resourceGroupDetail.getResourceName());
        }
        Long ownerUserId = scheduleTaskShade.getOwnerUserId();
        vo.setOwnerId(ownerUserId);
        vo.setFlowId(scheduleTaskShade.getFlowId());
        User user = userMap.get(ownerUserId);
        if (user != null) {
            vo.setOwnerName(user.getUserName());
        }

        List<ScheduleTaskPriority> scheduleTaskPriorityList = priorityMap.get(scheduleTaskShade.getTaskId() + GlobalConst.PARTITION + scheduleTaskShade.getAppType());
        Integer maxPriority = 1;
        if (CollectionUtils.isNotEmpty(scheduleTaskPriorityList)) {
            maxPriority = scheduleTaskPriorityList.stream().map(ScheduleTaskPriority::getPriority).max(Comparator.comparingInt(priority -> priority)).orElse(1);
        } else {
            maxPriority = scheduleTaskPriorityService.selectPriorityByTaskId(scheduleTaskShade.getTaskId(), scheduleTaskShade.getAppType());
        }
        vo.setPriority(maxPriority);
        return vo;
    }

    private void setTime(TaskPageVO vo, TaskPageDTO taskPageDTO) {
        TaskTimeVO taskTimeVO = vo.getTaskTime();
        TaskTimeDTO taskTime = new TaskTimeDTO();
        if (taskTimeVO != null) {
            try {
                String startCreateDate = taskTimeVO.getStartCreateDate();
                String endCreateDate = taskTimeVO.getEndCreateDate();
                if (StringUtils.isNotEmpty(startCreateDate)
                        && StringUtils.isNotEmpty(endCreateDate)) {
                    Long startMillie = DateUtil.getDateMilliSecondTOFormat(startCreateDate, DateUtil.STANDARD_DATETIME_FORMAT);
                    if (startMillie != null) {
                        taskTime.setStartCreateTime(new Timestamp(startMillie));
                    }
                    Long endMillie = DateUtil.getDateMilliSecondTOFormat(endCreateDate, DateUtil.STANDARD_DATETIME_FORMAT);
                    if (endMillie != null) {
                        taskTime.setEndCreateTime(new Timestamp(endMillie));
                    }
                }

                String startUpdateDate = taskTimeVO.getStartUpdateDate();
                String endUpdateDate = taskTimeVO.getEndUpdateDate();

                if (StringUtils.isNotEmpty(startUpdateDate)
                        && StringUtils.isNotEmpty(endUpdateDate)) {
                    Long startMillie = DateUtil.getDateMilliSecondTOFormat(startUpdateDate, DateUtil.STANDARD_DATETIME_FORMAT);
                    if (startMillie != null) {
                        taskTime.setStartUpdateTime(new Timestamp(startMillie));
                    }

                    Long endMillie = DateUtil.getDateMilliSecondTOFormat(endUpdateDate, DateUtil.STANDARD_DATETIME_FORMAT);
                    if (endMillie != null) {
                        taskTime.setEndUpdateTime(new Timestamp(endMillie));
                    }
                }

                taskPageDTO.setTaskTime(taskTime);
            } catch (Exception e) {
                LOGGER.error("set time for OperationTask error:{}", e.getMessage(), e);
            }

        }
    }

    public List<TaskReturnPageVO> workflowPage(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeDao.getOne(taskId, appType);

        if (scheduleTaskShade == null || !EScheduleJobType.WORK_FLOW.getType().equals(scheduleTaskShade.getTaskType())) {
            throw new RdosDefineException("taskId:"+taskId+" appType: "+appType+" must workflow task");
        }

        List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.getTasksByFlowId(taskId, appType);

        if (CollectionUtils.isNotEmpty(scheduleTaskShades)) {
            return getTaskReturnPageVOS(scheduleTaskShades,appType,new HashMap<>(),(param)->
                    Optional.of(getTaskReturnPageVO(param.getGroupInfo(), param.getUserMap(),param.tagMaps,param.priorityMap, param.scheduleTaskShade)));
        }

        return Lists.newArrayList();
    }

    @NotNull
    private List<TaskReturnPageVO> getTaskReturnPageVOS(List<ScheduleTaskShade> scheduleTaskShades,
                                                        Integer appType,
                                                        Map<Long, List<Long>> flowMap,
                                                        Callback<TaskParam, Optional<TaskReturnPageVO>> callback) {
        List<Long> resourceIds = scheduleTaskShades.stream().map(ScheduleTaskShade::getResourceId).collect(Collectors.toList());
        Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(resourceIds);

        // 查用户信息
        Set<Long> ownerIds = scheduleTaskShades.stream().map(ScheduleTaskShade::getOwnerUserId).collect(Collectors.toSet());
        List<User> userWithFill = userService.findUserWithFill(ownerIds);

        // 查标签信息
        Set<Long> taskIds = scheduleTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toSet());
        List<ScheduleTaskTag> scheduleTaskTags = scheduleTaskTagService.findTagByTaskIds(taskIds,appType);
        Map<Long, List<Long>> tagMaps;
        if (CollectionUtils.isEmpty(scheduleTaskTags)) {
            tagMaps = new HashMap<>();
        } else {
            tagMaps = scheduleTaskTags.stream()
                    .collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId,
                            Collectors.mapping(ScheduleTaskTag::getTagId, Collectors.toList())));
        }

        List<ScheduleTaskPriority> scheduleTaskPriorityList = scheduleTaskPriorityService.selectPriorityByTaskIds(Lists.newArrayList(taskIds), appType);
        Map<String, List<ScheduleTaskPriority>> priorityMap = scheduleTaskPriorityList.stream().collect(
                Collectors.groupingBy(scheduleTaskPriority ->
                        scheduleTaskPriority.getTaskId() + GlobalConst.PARTITION + scheduleTaskPriority.getAppType()));


        Map<Long, User> userMap = userWithFill.stream().collect(Collectors.toMap(User::getDtuicUserId, g -> (g)));
        return buildTaskReturnPageVOs(scheduleTaskShades, groupInfo, userMap,tagMaps,flowMap,priorityMap,callback);
    }

    static class TaskParam {

        private Map<Long, ResourceGroupDetail> groupInfo;

        private Map<Long, User> userMap;

        private Map<Long, List<Long>> flowMap;

        private Map<Long, List<Long>> tagMaps;

        private Map<String, List<ScheduleTaskPriority>> priorityMap;

        private ScheduleTaskShade scheduleTaskShade;

        public TaskParam(Map<Long, ResourceGroupDetail> groupInfo,
                         Map<Long, User> userMap,
                         Map<Long, List<Long>> flowMap,
                         Map<Long, List<Long>> tagMaps,
                         Map<String, List<ScheduleTaskPriority>> priorityMap,
                         ScheduleTaskShade scheduleTaskShade) {
            this.groupInfo = groupInfo;
            this.userMap = userMap;
            this.flowMap = flowMap;
            this.tagMaps = tagMaps;
            this.priorityMap = priorityMap;
            this.scheduleTaskShade = scheduleTaskShade;
        }

        public Map<Long, ResourceGroupDetail> getGroupInfo() {
            return groupInfo;
        }

        public void setGroupInfo(Map<Long, ResourceGroupDetail> groupInfo) {
            this.groupInfo = groupInfo;
        }

        public Map<Long, User> getUserMap() {
            return userMap;
        }

        public void setUserMap(Map<Long, User> userMap) {
            this.userMap = userMap;
        }

        public Map<Long, List<Long>> getFlowMap() {
            return flowMap;
        }

        public void setFlowMap(Map<Long, List<Long>> flowMap) {
            this.flowMap = flowMap;
        }

        public Map<Long, List<Long>> getTagMaps() {
            return tagMaps;
        }

        public void setTagMaps(Map<Long, List<Long>> tagMaps) {
            this.tagMaps = tagMaps;
        }

        public Map<String, List<ScheduleTaskPriority>> getPriorityMap() {
            return priorityMap;
        }

        public void setPriorityMap(Map<String, List<ScheduleTaskPriority>> priorityMap) {
            this.priorityMap = priorityMap;
        }

        public ScheduleTaskShade getScheduleTaskShade() {
            return scheduleTaskShade;
        }

        public void setScheduleTaskShade(ScheduleTaskShade scheduleTaskShade) {
            this.scheduleTaskShade = scheduleTaskShade;
        }
    }


}
