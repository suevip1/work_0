package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.po.ScheduleTaskCommit;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.api.vo.task.TaskViewElementVO;
import com.dtstack.engine.api.vo.task.TaskViewRetrunVO;
import com.dtstack.engine.api.vo.task.TaskViewSideVO;
import com.dtstack.engine.api.vo.task.TaskViewVO;
import com.dtstack.engine.api.vo.task.WorkflowSubViewList;
import com.dtstack.engine.common.enums.DisplayDirect;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleTaskCommitMapper;
import com.dtstack.engine.dao.ScheduleTaskTaskShadeDao;
import com.dtstack.engine.master.druid.DtDruidRemoveAbandoned;
import com.dtstack.engine.master.enums.UpDownRelyTypeEnum;
import com.dtstack.engine.master.mapstruct.ScheduleTaskTaskShadeStruct;
import com.dtstack.engine.master.sync.fill.AbstractFillDataTask;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleTaskTaskShadeService {

    private static final Long IS_WORK_FLOW_SUBNODE = 0L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskTaskShadeService.class);

    @Autowired
    private ScheduleTaskTaskShadeDao scheduleTaskTaskShadeDao;

    @Autowired
    private ScheduleTaskShadeService taskShadeService;

    @Autowired
    private EnvironmentContext context;

    @Autowired
    private WorkSpaceProjectService projectService;

    @Autowired
    private ScheduleTaskCommitMapper scheduleTaskCommitMapper;

    @Autowired
    private ScheduleTaskTaskShadeStruct scheduleTaskTaskShadeStruct;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private DQStrongRuleTaskChecker dqStrongRuleTaskChecker;

    public void clearDataByTaskId( Long taskId,Integer appType) {
        scheduleTaskTaskShadeDao.deleteByTaskId(taskId,appType);
    }

    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public SaveTaskTaskVO saveTaskTaskList(String taskLists,String commitId) {
        if(StringUtils.isBlank(taskLists)){
            return SaveTaskTaskVO.save();
        }
        try {
            List<ScheduleTaskTaskShade> taskTaskList = JSONObject.parseArray(taskLists, ScheduleTaskTaskShade.class);
            if(CollectionUtils.isEmpty(taskTaskList)){
                return SaveTaskTaskVO.save();
            }

            // 保存时成环检测
            for (ScheduleTaskTaskShade scheduleTaskTaskShade : taskTaskList) {
                Long parentTaskId = scheduleTaskTaskShade.getParentTaskId();
                if (checkParentCommit(commitId, scheduleTaskTaskShade, parentTaskId)){
                    return SaveTaskTaskVO.noSave("任务依赖报错失败，父任务不存在，请检查父任务");
                }

                List<ScheduleTaskTaskShade> shades = Lists.newArrayList(taskTaskList);
                if (checkTaskTaskIsLoop(scheduleTaskTaskShade, shades)) {
                    return SaveTaskTaskVO.noSave("任务依赖成环，无法提交");
                }
            }

            SaveTaskTaskVO saveTaskTaskVO = dqStrongRuleTaskChecker.checkDQRuleTaskIllegal(taskTaskList);
            if (!saveTaskTaskVO.getSave()) {
                return saveTaskTaskVO;
            }

            Map<String, ScheduleTaskTaskShade> keys = new HashMap<>(16);
            // 去重
            for (ScheduleTaskTaskShade scheduleTaskTaskShade : taskTaskList) {
                keys.put(String.format("%s.%s.%s", scheduleTaskTaskShade.getTaskId(), scheduleTaskTaskShade.getParentTaskId(), scheduleTaskTaskShade.getProjectId()), scheduleTaskTaskShade);
                Preconditions.checkNotNull(scheduleTaskTaskShade.getTaskId());
                Preconditions.checkNotNull(scheduleTaskTaskShade.getAppType());
                // 清除原来关系
                scheduleTaskTaskShadeDao.deleteByTaskId(scheduleTaskTaskShade.getTaskId(), scheduleTaskTaskShade.getAppType());
            }
            // 保存现有任务关系
            for (ScheduleTaskTaskShade taskTaskShade : keys.values()) {
                if (taskTaskShade.getParentAppType() == null) {
                    taskTaskShade.setParentAppType(taskTaskShade.getAppType());
                }

                if (UpDownRelyTypeEnum.DEFAULT.getType().equals(taskTaskShade.getUpDownRelyType())) {
                    taskTaskShade.setCustomOffset(0);
                }

                if (Objects.isNull(taskTaskShade.getUpDownRelyType())) {
                    taskTaskShade.setUpDownRelyType(UpDownRelyTypeEnum.DEFAULT.getType());
                    taskTaskShade.setCustomOffset(0);
                }
                scheduleTaskTaskShadeDao.insert(taskTaskShade);
            }
        } catch (Exception e) {
            LOGGER.error("saveTaskTaskList error:{}", ExceptionUtil.getErrorMessage(e));
            throw new RdosDefineException("保存任务依赖列表异常");
        }
        return SaveTaskTaskVO.save();
    }

    private boolean checkParentCommit(String commitId, ScheduleTaskTaskShade scheduleTaskTaskShade, Long parentTaskId) {
        if (parentTaskId != null) {
            Integer parentAppType = scheduleTaskTaskShade.getParentAppType();
            if (parentAppType == null ) {
                parentAppType = scheduleTaskTaskShade.getAppType();
            }


            if (!scheduleTaskTaskShade.getAppType().equals(parentAppType)) {
                ScheduleTaskShade batchTaskById = taskShadeService.getBatchTaskById(parentTaskId, parentAppType);
                return batchTaskById == null;
            } else {
                if (StringUtils.isNotBlank(commitId)) {
                    ScheduleTaskShade batchTaskById = taskShadeService.getBatchTaskById(parentTaskId, parentAppType);
                    if (batchTaskById == null) {
                        ScheduleTaskCommit taskCommitByTaskId = scheduleTaskCommitMapper.getTaskCommitByTaskId(parentTaskId, parentAppType, commitId);
                        return taskCommitByTaskId == null;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否成环
     *
     * @param scheduleTaskTaskShade
     * @return
     */
    private Boolean checkTaskTaskIsLoop(ScheduleTaskTaskShade scheduleTaskTaskShade,List<ScheduleTaskTaskShade> taskTask) {
        if (scheduleTaskTaskShade == null || scheduleTaskTaskShade.getParentTaskId() == null) {
            // 节点或者是父节点是null，都表示没有成环
            return Boolean.FALSE;
        }
        Map<Side,Set<String>> sideMap = Maps.newHashMap();
        Side side = new Side();
        side.setDown(scheduleTaskTaskShade.getTaskKey());
        side.setUp(scheduleTaskTaskShade.getParentTaskKey());
        sideMap.put(side,Sets.newHashSet(scheduleTaskTaskShade.getTaskKey()));

        // 向上查询,向上查询会查询出自己的边
        Integer loopUp = 0;
        List<ScheduleTaskTaskShade> scheduleParentTaskTaskShades = addParentTaskTask(Lists.newArrayList(scheduleTaskTaskShade.getTaskKey()),taskTask);
        while (CollectionUtils.isNotEmpty(scheduleParentTaskTaskShades)) {
            List<String> parentKeys = Lists.newArrayList();

            if (setKeyAndJudgedLoop(sideMap, scheduleParentTaskTaskShades, parentKeys,Boolean.FALSE)) {
                return Boolean.TRUE;
            }

            if (CollectionUtils.isEmpty(parentKeys)) {
                // 说明集合里面全部都是头节点
                break;
            }
            loopUp++;

            LOGGER.info("loopUp:{} select key:{}",loopUp,parentKeys);
            scheduleParentTaskTaskShades = addParentTaskTask(parentKeys,taskTask);
        }

        // 向下查询
        Integer loopUnder = 0;
        List<ScheduleTaskTaskShade> scheduleChildTaskTaskShades = addChildTaskTask(Lists.newArrayList(scheduleTaskTaskShade.getTaskKey()), taskTask);
        while (CollectionUtils.isNotEmpty(scheduleChildTaskTaskShades)) {
            List<String> childKeys = Lists.newArrayList();

            if (setKeyAndJudgedLoop(sideMap, scheduleChildTaskTaskShades, childKeys,Boolean.TRUE)) {
                return Boolean.TRUE;
            }

            if (CollectionUtils.isEmpty(childKeys)) {
                // 说明集合里面全部都是头节点
                break;
            }
            loopUnder++;

            LOGGER.info("loopUnder:{} select key:{}",loopUnder,childKeys);
            scheduleChildTaskTaskShades = addChildTaskTask(childKeys, taskTask);

        }

        return Boolean.FALSE;
    }

    public List<ScheduleTaskTaskShadeDTO> listChildByTaskKeys(List<String> taskKeys) {
        List<ScheduleTaskTaskShadeDTO> scheduleTaskTaskShadeDTOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskKeys)) {
            List<ScheduleTaskTaskShade> taskTaskShades = scheduleTaskTaskShadeDao.listParentTaskKeys(taskKeys);
            Map<Integer, List<ScheduleTaskTaskShade>> taskMaps = taskTaskShades.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getAppType));
            for (Integer appType : taskMaps.keySet()) {
                List<ScheduleTaskTaskShade> shades = taskMaps.get(appType);

                if (CollectionUtils.isNotEmpty(shades)) {
                    List<Long> taskIds = shades.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toList());
                    List<ScheduleTaskShade> scheduleTaskShades = taskShadeService.getTaskByIds(taskIds, appType);
                    Map<String, ScheduleTaskShade> taskKeyMap = scheduleTaskShades.stream().collect(Collectors.toMap(task -> task.getTaskId() + AbstractFillDataTask.FillDataConst.KEY_DELIMITER + task.getAppType(), g -> (g)));

                    for (ScheduleTaskTaskShade shade : shades) {
                        ScheduleTaskShade scheduleTaskShade = taskKeyMap.get(shade.getTaskKey());
                        if (scheduleTaskShade != null) {
                            ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = new ScheduleTaskTaskShadeDTO();
                            scheduleTaskTaskShadeDTO.setTaskKey(shade.getTaskKey());
                            scheduleTaskTaskShadeDTO.setParentTaskKey(shade.getParentTaskKey());
                            scheduleTaskTaskShadeDTO.setScheduleStatus(scheduleTaskShade.getScheduleStatus());
                            scheduleTaskTaskShadeDTOS.add(scheduleTaskTaskShadeDTO);
                        }
                    }
                }
            }
        }
        return scheduleTaskTaskShadeDTOS;
    }

    public ScheduleTaskVO listGroupTask(Long taskId, Integer appType) {
        ScheduleTaskShade task = taskShadeService.getBatchTaskById(taskId,appType);
        if(null == task){
            return null;
        }
        AuthProjectVO authProjectVO = projectService.finProject(task.getProjectId(), task.getAppType());
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(task, true);
        if (authProjectVO != null) {
            vo.setProjectAlias(authProjectVO.getProjectAlias());
            vo.setProjectName(authProjectVO.getProjectName());
        }
        vo.setTenantName(tenantService.getTenantName(task.getDtuicTenantId()));
        if(!EScheduleJobType.GROUP.getType().equals(task.getTaskType())){
            return vo;
        }
        List<ScheduleTaskShade> subTaskShades = taskShadeService.getTasksByFlowId(task.getTaskId(), appType);


        if(CollectionUtils.isEmpty(subTaskShades)){
            return vo;
        }
        String tenantName = tenantService.getTenantName(task.getDtuicTenantId());
        List<ScheduleTaskVO> scheduleTaskVOS = subTaskShades.stream()
                .map(scheduleTaskShade -> {
                    com.dtstack.engine.master.vo.ScheduleTaskVO scheduleTaskVO = new com.dtstack.engine.master.vo.ScheduleTaskVO(task, true);
                    if (authProjectVO != null) {
                        scheduleTaskVO.setProjectAlias(authProjectVO.getProjectAlias());
                        scheduleTaskVO.setProjectName(authProjectVO.getProjectName());
                    }
                    scheduleTaskVO.setTenantName(tenantName);
                    return scheduleTaskVO;
                }).collect(Collectors.toList());
        vo.setSubTaskVOS(scheduleTaskVOS);

        return vo;
    }

    /**
     * 任务下线，清楚下线的任务下的所有边和被依赖的边
     *
     * @param jobKeys
     */
    public Integer deleteTombstoneByTaskKeys(Collection<String> jobKeys) {
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            Integer count = scheduleTaskTaskShadeDao.deleteByTaskKeys(jobKeys);
            count += scheduleTaskTaskShadeDao.deleteByParentTaskKeys(jobKeys);
            return count;
        }
        return 0;
    }

    public TaskViewRetrunVO view(TaskViewVO taskViewVO) {
        Integer level = taskViewVO.getLevel();
        if (level == null || level < 1) {
            level = 1;
        }

        if (level > context.getJobJobLevel()) {
            level = context.getJobJobLevel();
        }

        Integer directType = taskViewVO.getDirectType();
        if (directType == null) {
            directType = DisplayDirect.FATHER.getType();
        }

        List<TaskKeyVO> taskKeyVO = taskViewVO.getTaskKeyVOS();

        if (CollectionUtils.isEmpty(taskKeyVO)) {
            throw new RdosDefineException("查询视图必须传taskId和appType");
        }

        List<TaskViewElementVO> viewElementVOS = Lists.newArrayList();
        Set<TaskViewSideVO> taskViewSideVOS = Sets.newHashSet();

        Map<Integer, List<Long>> voMap = taskKeyVO.stream().collect(Collectors.groupingBy(TaskKeyVO::getAppType,
                Collectors.mapping(TaskKeyVO::getTaskId, Collectors.toList())));
        List<ScheduleTaskShade> taskLists = Lists.newArrayList();
        for (Map.Entry<Integer, List<Long>> entry : voMap.entrySet()) {
            Integer key = entry.getKey();
            List<Long> value = entry.getValue();
            List<ScheduleTaskShade> scheduleTaskShades = taskShadeService.getTaskByIds(value, key);
            if (CollectionUtils.isNotEmpty(scheduleTaskShades)) {
                taskLists.addAll(scheduleTaskShades);
            }
        }
        viewElementVOS.addAll(scheduleTaskTaskShadeStruct.toTaskViewElementVO(taskLists));

        while (level > 0 && CollectionUtils.isNotEmpty(taskLists)) {
            // 查询实体
            List<String> taskKeys = taskLists.stream().map(taskShade ->
                            taskShade.getTaskId() + "-" + taskShade.getAppType())
                    .collect(Collectors.toList());

            List<ScheduleTaskTaskShade> taskTaskShades = null;
            Map<Integer, List<Long>> listMap = null;
            if (DisplayDirect.FATHER.getType().equals(directType)) {
                taskTaskShades = scheduleTaskTaskShadeDao.listTaskKeys(taskKeys);
                listMap = taskTaskShades.stream()
                        .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType, Collectors.mapping(ScheduleTaskTaskShade::getParentTaskId, Collectors.toList())));
            } else if (DisplayDirect.CHILD.getType().equals(directType)) {
                taskTaskShades = scheduleTaskTaskShadeDao.listParentTaskKeys(taskKeys);
                listMap = taskTaskShades.stream()
                        .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getAppType, Collectors.mapping(ScheduleTaskTaskShade::getTaskId, Collectors.toList())));
            }

            if (CollectionUtils.isEmpty(taskTaskShades)) {
                break;
            }

            taskLists = Lists.newArrayList();
            for (Integer integer : listMap.keySet()) {
                List<Long> nextTaskIds = listMap.get(integer);
                List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(nextTaskIds, integer);
                tasks = tasks.stream().filter(task -> TaskRuleEnum.NO_RULE.getCode().equals(task.getTaskRule()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(tasks)) {
                    taskLists.addAll(tasks);
                    viewElementVOS.addAll(scheduleTaskTaskShadeStruct.toTaskViewElementVO(tasks));
                }
            }

            taskViewSideVOS.addAll(scheduleTaskTaskShadeStruct.toTaskViewSideVO(taskTaskShades));
            level--;
        }

        for (TaskViewElementVO viewElementVO : viewElementVOS) {
            List<ScheduleTaskShade> taskShades = taskShadeService.findChildTaskRuleByTaskId(viewElementVO.getTaskId(), viewElementVO.getAppType());
            if (CollectionUtils.isNotEmpty(taskShades)) {
                // 绑定了规则任务
                viewElementVO.setExistsOnRule(Boolean.TRUE);
            } else {
                viewElementVO.setExistsOnRule(Boolean.FALSE);
            }
        }

        TaskViewRetrunVO taskViewRetrunVO = new TaskViewRetrunVO();
        taskViewRetrunVO.setTaskViewSideVOS(Lists.newArrayList(taskViewSideVOS));
        taskViewRetrunVO.setTaskViewElementVOS(viewElementVOS);
        return taskViewRetrunVO;
    }

    public WorkflowSubViewList workflowSubViewList(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = taskShadeService.getBatchTaskById(taskId, appType);

        if (scheduleTaskShade == null || !EScheduleJobType.WORK_FLOW.getType().equals(scheduleTaskShade.getTaskType())) {
            throw new RdosDefineException("taskId:"+taskId+" appType: "+appType+" must workflow task");
        }

        WorkflowSubViewList workflowSubViewList = new WorkflowSubViewList();
        workflowSubViewList.setTaskId(scheduleTaskShade.getTaskId());
        workflowSubViewList.setAppType(scheduleTaskShade.getAppType());
        TaskViewRetrunVO taskViewRetrunVO = new TaskViewRetrunVO();

        List<ScheduleTaskShade> scheduleTaskShades = taskShadeService.getTasksByFlowId(taskId, appType);
        if (CollectionUtils.isNotEmpty(scheduleTaskShades)) {
            List<String> taskKey = scheduleTaskShades.stream().map(task -> task.getTaskId() + "-" + task.getAppType()).collect(Collectors.toList());
            List<ScheduleTaskTaskShade> scheduleTaskTaskShades = scheduleTaskTaskShadeDao.listTaskKeys(taskKey);
            List<TaskViewSideVO> taskViewSideVOS = Lists.newArrayList();
            for (ScheduleTaskTaskShade scheduleTaskTaskShade : scheduleTaskTaskShades) {
                if (!scheduleTaskTaskShade.getParentTaskKey().equals(taskId + "-" + appType)) {
                    TaskViewSideVO taskViewSideVO = new TaskViewSideVO();

                    taskViewSideVO.setTaskKey(scheduleTaskTaskShade.getTaskKey());
                    taskViewSideVO.setParentTaskKey(scheduleTaskTaskShade.getParentTaskKey());
                    taskViewSideVOS.add(taskViewSideVO);
                }
            }
            taskViewRetrunVO.setTaskViewSideVOS(taskViewSideVOS);

            List<TaskViewElementVO> taskViewElementVOS = Lists.newArrayList();
            for (ScheduleTaskShade taskShade : scheduleTaskShades) {
                TaskViewElementVO taskViewElementVO = new TaskViewElementVO();
                taskViewElementVO.setTaskId(taskShade.getTaskId());
                taskViewElementVO.setTaskKey(taskShade.getTaskId() + "-" + taskShade.getAppType());
                taskViewElementVO.setTaskName(taskShade.getName());
                taskViewElementVO.setTaskRule(taskShade.getTaskRule());
                taskViewElementVO.setTaskType(taskShade.getTaskType());
                taskViewElementVO.setProjectId(taskShade.getProjectId());
                taskViewElementVO.setScheduleStatus(taskShade.getScheduleStatus());
                taskViewElementVO.setFlowId(taskShade.getFlowId());
                taskViewElementVO.setAppType(taskShade.getAppType());
                taskViewElementVOS.add(taskViewElementVO);
            }

            taskViewRetrunVO.setTaskViewElementVOS(taskViewElementVOS);
        }

        workflowSubViewList.setTaskViewRetrunVO(taskViewRetrunVO);
        return workflowSubViewList;
    }

    public List<ScheduleTaskTaskShade> getAllChildTask( Long taskId,Integer appType) {
        return scheduleTaskTaskShadeDao.listChildTask(taskId,appType);
    }

    static class Side {
        private String up;
        private String down;

        public String getUp() {
            return up;
        }

        public void setUp(String up) {
            this.up = up;
        }

        public String getDown() {
            return down;
        }

        public void setDown(String down) {
            this.down = down;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Side side = (Side) o;
            return up.equals(side.up) &&
                    down.equals(side.down);
        }

        @Override
        public int hashCode() {
            return Objects.hash(up, down);
        }
    }

    private boolean setKeyAndJudgedLoop(Map<Side, Set<String>> sideMap, List<ScheduleTaskTaskShade> scheduleChildTaskTaskShades, List<String> parentKeys, Boolean isChild) {
        Map<Side, Set<String>> newSideMap = Maps.newHashMap(sideMap);

        if (isChild) {
            // 向下查询
            Map<String, List<ScheduleTaskTaskShade>> parentKey = scheduleChildTaskTaskShades.stream()
                    .filter(scheduleTaskTaskShade -> StringUtils.isNotBlank(scheduleTaskTaskShade.getParentTaskKey()))
                    .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentTaskKey));

            for (Map.Entry<Side, Set<String>> entry : newSideMap.entrySet()) {
                Side side = entry.getKey();
                String taskKey = side.getDown();
                List<ScheduleTaskTaskShade> taskTaskShades = parentKey.get(taskKey);

                if (CollectionUtils.isNotEmpty(taskTaskShades)) {

                    for (ScheduleTaskTaskShade taskTaskShade : taskTaskShades) {
                        Side sideSon = new Side();
                        Set<String> element = entry.getValue();

                        sideSon.setUp(side.up);
                        sideSon.setDown(taskTaskShade.getTaskKey());

                        Set<String> newElement = Sets.newHashSet(element);

                        if (!newElement.add(taskTaskShade.getTaskKey())) {
                            // 添加不进去，说明边重复了，已经成环
                            LOGGER.warn("saveTaskTask is loop,loop:{} -------- repeat side:{}, map: {}", taskTaskShade.getTaskKey(),newElement, sideMap);
                            return Boolean.TRUE;
                        }
                        sideMap.remove(side);
                        sideMap.put(sideSon,newElement);
                        parentKeys.add(taskTaskShade.getTaskKey());
                    }
                }
            }
        } else {
            // 向上查询
            Map<String, List<ScheduleTaskTaskShade>> parentKey = scheduleChildTaskTaskShades.stream()
                    .filter(scheduleTaskTaskShade -> StringUtils.isNotBlank(scheduleTaskTaskShade.getParentTaskKey()))
                    .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentTaskKey));

            for (Map.Entry<Side, Set<String>> entry : newSideMap.entrySet()) {
                Side side = entry.getKey();
                String taskKey = side.getUp();

                List<ScheduleTaskTaskShade> taskTaskShades = parentKey.get(taskKey);

                if (CollectionUtils.isNotEmpty(taskTaskShades)) {
                    for (ScheduleTaskTaskShade taskTaskShade : taskTaskShades) {
                        Side sideSon = new Side();
                        Set<String> element = entry.getValue();

                        sideSon.setUp(taskTaskShade.getParentTaskKey());
                        sideSon.setDown(side.down);

                        Set<String> newElement = Sets.newHashSet(element);

                        if (!newElement.add(taskTaskShade.getParentTaskKey())) {
                            // 添加不进去，说明边重复了，已经成环
                            LOGGER.warn("saveTaskTask is loop,loop:{} -------- repeat side:{}, map: {}", taskTaskShade.getTaskKey(),newElement, sideMap);
                            return Boolean.TRUE;
                        }
                        sideMap.remove(side);
                        sideMap.put(sideSon,newElement);
                        parentKeys.add(taskTaskShade.getParentTaskKey());
                    }
                }
            }
        }

        // 未成环
        return Boolean.FALSE;
    }

    private List<ScheduleTaskTaskShade> addChildTaskTask(List<String> childKey, List<ScheduleTaskTaskShade> taskTasks) {
        List<ScheduleTaskTaskShade> scheduleChildTaskTaskShades = scheduleTaskTaskShadeDao.listParentTaskKeys(childKey);
        List<String> sides = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(scheduleChildTaskTaskShades)) {
            sides = scheduleChildTaskTaskShades.stream().map(taskShade -> taskShade.getTaskKey()+"&"+taskShade.getParentTaskKey()).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(taskTasks)) {
            List<String> finalSides = sides;
            List<ScheduleTaskTaskShade> shades = taskTasks.stream()
                    .filter(taskTask -> childKey.contains(taskTask.getParentTaskKey()) && !finalSides.contains(taskTask.getTaskKey()+"&"+taskTask.getParentTaskKey()))
                    .collect(Collectors.toList());
            scheduleChildTaskTaskShades.addAll(shades);
        }
        return scheduleChildTaskTaskShades;
    }

    private List<ScheduleTaskTaskShade> addParentTaskTask(List<String> parentKeys, List<ScheduleTaskTaskShade> taskTasks) {
        List<ScheduleTaskTaskShade> taskTaskShades = scheduleTaskTaskShadeDao.listTaskKeys(parentKeys);
        List<String> sides = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskTaskShades)) {
            sides = taskTaskShades.stream().map(taskShade -> taskShade.getTaskKey()+"&"+taskShade.getParentTaskKey()).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(taskTasks)) {
            List<String> finalSides = sides;
            List<ScheduleTaskTaskShade> shades = taskTasks.stream()
                    .filter(taskTask -> parentKeys.contains(taskTask.getTaskKey()) && !finalSides.contains(taskTask.getTaskKey()+"&"+taskTask.getParentTaskKey()))
                    .collect(Collectors.toList());
            taskTaskShades.addAll(shades);
        }
        return taskTaskShades;
    }

    public List<ScheduleTaskTaskShade> getAllParentTask( Long taskId,Integer appType) {
        return scheduleTaskTaskShadeDao.listParentTask(taskId,appType);
    }

    public List<ScheduleTaskTaskShade> listChildTask(Long parentTaskId,Integer parentAppType) {
        return scheduleTaskTaskShadeDao.listChildTask(parentTaskId, parentAppType);
    }

    public List<ScheduleTaskTaskShade> getAllParentTaskExcludeParentAppTypes(Long taskId, List<Integer> excludeAppTypes) {
        return scheduleTaskTaskShadeDao.listParentTaskExcludeParentAppTypes(taskId, excludeAppTypes);
    }

    public ScheduleTaskTaskShade getOneByTaskId(Long taskId, Long parentTaskId, Integer appType) {
        return scheduleTaskTaskShadeDao.getOneByTaskId(taskId, parentTaskId, appType);
    }

    public com.dtstack.engine.master.vo.ScheduleTaskVO displayOffSpring( Long taskId,
                                                                         Long projectId,
                                                                         Integer level,
                                                                         Integer directType, Integer appType) {
        ScheduleTaskShade task = taskShadeService.getBatchTaskById(taskId,appType);
        if(null == task){
            return null;
        }

        if (level == null || level < 1) {
            level = 1;
        }
        if( level > context.getJobJobLevel()){
            level = context.getJobJobLevel();
        }
        if(directType == null){
            directType = 0;
        }
        Map<Long,UICTenantVO> cacheTenant = new HashMap<>();
        Table<Long,Integer,AuthProjectVO> cacheProject = HashBasedTable.create();
        if(context.getUseOptimize()) {
            return this.getOffSpringNew(task, level, directType, projectId, appType, new ArrayList<>(),cacheTenant,cacheProject);
        }else{
            return this.getOffSpring(task,level,directType,projectId,appType,cacheTenant,cacheProject);
        }
    }

    /**
     * 展开依赖节点,优化后
     * 0 展开上下游, 1:展开上游 2:展开下游
     *
     * @author toutian
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOffSpringNew(ScheduleTaskShade taskShade, int level,
                                                                        Integer directType, Long currentProjectId, Integer appType, List<String> taskIdRelations,
                                                                        Map<Long,UICTenantVO> cacheTenant,Table<Long,Integer,AuthProjectVO> cacheProject) {
        //1、如果是工作流子节点,则展开全部工作流子节点
        if (!taskShade.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal()) &&
                !taskShade.getFlowId().equals(IS_WORK_FLOW_SUBNODE)) {
            //若为工作流子节点，则展开工作流全部子节点
            return getOnlyAllFlowSubTasksNew(taskShade.getFlowId(), appType,cacheTenant,cacheProject);
        }
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        setTenant(vo.getDtuicTenantId(),cacheTenant,vo);
        setProject(vo.getProjectId(),vo.getAppType(),cacheProject,vo);
        vo.setCurrentProject(currentProjectId.equals(taskShade.getProjectId()));
        if (EScheduleJobType.WORK_FLOW.getVal().equals(taskShade.getTaskType())) {
            //2、如果是工作流，则获取工作流子节点,包括工作流本身
            //构建父节点信息
            com.dtstack.engine.master.vo.ScheduleTaskVO parentNode = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
            com.dtstack.engine.master.vo.ScheduleTaskVO onlyAllFlowSubTasksNew = getOnlyAllFlowSubTasksNew(taskShade.getTaskId(), taskShade.getAppType(),cacheTenant,cacheProject);
            parentNode.setSubTaskVOS(Arrays.asList(onlyAllFlowSubTasksNew));
            vo.setSubNodes(parentNode);
        }

        // 查询是否有绑定任务
        List<ScheduleTaskShade> taskShades = taskShadeService.findChildTaskRuleByTaskId(taskShade.getTaskId(), taskShade.getAppType());
        if (CollectionUtils.isNotEmpty(taskShades)) {

            // 绑定了规则任务
            vo.setExistsOnRule(Boolean.TRUE);
        } else {
            vo.setExistsOnRule(Boolean.FALSE);
        }

        if (level == 0) {
            //控制最多展示多少层，防止一直循环。
            return vo;
        }
        level--;
        List<ScheduleTaskTaskShade> taskTasks = null;
        List<ScheduleTaskTaskShade> childTaskTasks = null;
        //展开上游节点
        if (DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.FATHER.getType().equals(directType)) {
            taskTasks = scheduleTaskTaskShadeDao.listParentTask(taskShade.getTaskId(), taskShade.getAppType());
            if (checkIsLoop(taskIdRelations, taskTasks)) {
                //成环了，直接返回
                return vo;
            }
        }
        //展开下游节点
        if (DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.CHILD.getType().equals(directType)) {
            childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(), taskShade.getAppType());
            if (checkIsLoop(taskIdRelations, childTaskTasks)) {
                //成环了，直接返回
                return vo;
            }
        }
        if (CollectionUtils.isEmpty(taskTasks) && CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        List<ScheduleTaskVO> parentTaskList = null;
        List<ScheduleTaskVO> childTaskList = null;
        List<ScheduleTaskVO> taskRuleList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(taskTasks)) {
            //向上展开
            Map<Integer, List<ScheduleTaskTaskShade>> listMap = taskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType));
            parentTaskList = getRefTaskNew(listMap, level, DisplayDirect.FATHER.getType(), currentProjectId, taskIdRelations,taskRuleList,cacheTenant,cacheProject);
            if (CollectionUtils.isNotEmpty(parentTaskList) && parentTaskList.get(0) != null) {
                vo.setTaskVOS(parentTaskList);
            }

            if (CollectionUtils.isNotEmpty(taskRuleList) && taskRuleList.get(0) != null) {
                vo.setTaskRuleList(taskRuleList);
            }
        }
        if (!CollectionUtils.isEmpty(childTaskTasks)) {
            //向下展开
            Map<Integer, List<ScheduleTaskTaskShade>> listMap = childTaskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getAppType));
            childTaskList = getRefTaskNew(listMap, level, DisplayDirect.CHILD.getType(), currentProjectId, taskIdRelations,taskRuleList,cacheTenant,cacheProject);
            if (CollectionUtils.isNotEmpty(childTaskList) && childTaskList.get(0) != null) {
                vo.setSubTaskVOS(childTaskList);
            }

            if (CollectionUtils.isNotEmpty(taskRuleList) && taskRuleList.get(0) != null) {
                vo.setTaskRuleList(taskRuleList);
            }
        }
        return vo;
    }

    /**
     * @author newman
     * @Description 检测任务是否成环
     * @Date 2021/1/6 8:23 下午
     * @param taskIdRelations:
     * @param taskTasks:
     * @return: void
     **/
    private Boolean checkIsLoop(List<String> taskIdRelations, List<ScheduleTaskTaskShade> taskTasks) {

        if(CollectionUtils.isNotEmpty(taskTasks)) {
            for (ScheduleTaskTaskShade taskTask : taskTasks) {
                String taskRelation = taskTask.getTaskId() + "&" + taskTask.getAppType() + "-" + taskTask.getParentTaskId() + "&" + taskTask.getParentAppType();
                if (taskIdRelations.contains(taskRelation)) {
                    LOGGER.error("该任务成环了,taskRelation:{} 所有的关系视图:{}", taskRelation,taskIdRelations.toString());
                    return true;
                } else {
                    taskIdRelations.add(taskRelation);
                }
            }
        }
        return false;
    }

    /**
     * 展开依赖节点
     * 0 展开上下游, 1:展开上游 2:展开下游
     * @author toutian
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOffSpring(ScheduleTaskShade taskShade, int level, Integer directType, Long currentProjectId, Integer appType,
                                                                     Map<Long,UICTenantVO> cacheTenant,Table<Long,Integer,AuthProjectVO> cacheProject) {

        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        vo.setCurrentProject(currentProjectId.equals(taskShade.getProjectId()));
        setTenant(vo.getDtuicTenantId(),cacheTenant,vo);
        setProject(vo.getProjectId(),vo.getAppType(),cacheProject,vo);
        if (EScheduleJobType.WORK_FLOW.getVal().equals(taskShade.getTaskType())) {
            //如果是工作流，则获取工作流及其子节点
            com.dtstack.engine.master.vo.ScheduleTaskVO subTaskVO = getAllFlowSubTasks(taskShade.getTaskId(),taskShade.getAppType());
            vo.setSubNodes(subTaskVO);
        }
        if (level == 0) {
            //控制最多展示多少层，防止一直循环。
            return vo;
        }
        level--;
        List<ScheduleTaskTaskShade> taskTasks = null;
        List<ScheduleTaskTaskShade> childTaskTasks = null;
        if(taskShade.getTaskType().intValue() != EScheduleJobType.WORK_FLOW.getVal() &&
                !taskShade.getFlowId().equals(IS_WORK_FLOW_SUBNODE)){
            //若为工作流子节点，则展开工作流全部子节点
            return getOnlyAllFlowSubTasks(taskShade.getFlowId(),appType);
        }
        //展开上游节点
        if(DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.FATHER.getType().equals(directType)){
            taskTasks = scheduleTaskTaskShadeDao.listParentTask(taskShade.getTaskId(),taskShade.getAppType());
        }
        //展开下游节点
        if(DisplayDirect.FATHER_CHILD.getType().equals(directType) || DisplayDirect.CHILD.getType().equals(directType)){
            childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(),taskShade.getAppType());
        }
        if (CollectionUtils.isEmpty(taskTasks) && CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        List<ScheduleTaskVO> parentTaskList = null;
        List<ScheduleTaskVO> childTaskList = null;
        List<ScheduleTaskVO> ruleTaskList = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(taskTasks)){
            //向上展开
            Set<Long> taskIds = new HashSet<>(taskTasks.size());
            taskTasks.forEach(taskTask -> taskIds.add(taskTask.getParentTaskId()));
            parentTaskList = getRefTask(taskIds, level, DisplayDirect.FATHER.getType(), currentProjectId,appType,ruleTaskList,cacheTenant,cacheProject);
            if(parentTaskList != null){
                vo.setTaskVOS(parentTaskList);
            }
        }
        if(!CollectionUtils.isEmpty(childTaskTasks)){
            //向下展开
            Set<Long> taskIds = new HashSet<>(childTaskTasks.size());
            childTaskTasks.forEach(taskTask -> taskIds.add(taskTask.getTaskId()));
            childTaskList = getRefTask(taskIds, level, DisplayDirect.CHILD.getType(), currentProjectId,appType,ruleTaskList,cacheTenant,cacheProject);
            if(childTaskList != null){
                vo.setSubTaskVOS(childTaskList);
            }
        }
        if (CollectionUtils.isEmpty(ruleTaskList)) {
            vo.setTaskRuleList(ruleTaskList);
        }

        return vo;
    }

    private Map<Long, UICTenantVO> setTenant(Long dtUicTenantId, Map<Long, UICTenantVO> cacheTenant, ScheduleTaskVO vo) {
        cacheTenant.computeIfAbsent(dtUicTenantId, id -> tenantService.getTenant(dtUicTenantId));
        if (cacheTenant.containsKey(dtUicTenantId)) {
            vo.setTenantName(cacheTenant.get(dtUicTenantId).getTenantName());
        }
        return cacheTenant;
    }

    private Table<Long, Integer, AuthProjectVO> setProject(Long projectId, Integer appType,Table<Long, Integer, AuthProjectVO> cacheProject, ScheduleTaskVO vo) {
        AuthProjectVO scheduleEngineProject = cacheProject.get(projectId, appType);
        if (scheduleEngineProject == null) {
            AuthProjectVO authProjectVO = projectService.finProject(projectId, appType);
            if (authProjectVO != null) {
                cacheProject.put(projectId, appType, authProjectVO);
                scheduleEngineProject = authProjectVO;
            }
        }
        if (scheduleEngineProject != null) {
            vo.setProjectName(scheduleEngineProject.getProjectName());
            vo.setProjectAlias(scheduleEngineProject.getProjectAlias());
        }

        return cacheProject;
    }

    public List<ScheduleTaskVO> getRefTaskNew(Map<Integer, List<ScheduleTaskTaskShade>> listMap, int level, Integer directType,
                                              Long currentProjectId,List<String> taskIdRelations,List<ScheduleTaskVO> taskVOList,
                                              Map<Long,UICTenantVO> cacheTenant,Table<Long,Integer,AuthProjectVO> cacheProject){
        List<ScheduleTaskShade> tasks = getScheduleTaskShades(listMap,directType);

        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            Integer taskRule = task.getTaskRule();

            if (TaskRuleEnum.NO_RULE.getCode().equals(taskRule)) {
                refTaskVoList.add(this.getOffSpringNew(task, level, directType, currentProjectId,task.getAppType(),taskIdRelations,cacheTenant,cacheProject));
            } else {
                // 规则任务
                taskVOList.add(this.getOffSpringNew(task, level, directType, currentProjectId,task.getAppType(),taskIdRelations,cacheTenant,cacheProject));
            }
        }
        return refTaskVoList;
    }

    private List<ScheduleTaskShade> getScheduleTaskShades(Map<Integer, List<ScheduleTaskTaskShade>> listMap,Integer directType) {
        List<ScheduleTaskShade> tasks = Lists.newArrayList();
        //获得所有父节点task
        for (Map.Entry<Integer, List<ScheduleTaskTaskShade>> entry : listMap.entrySet()) {
            Integer appType = entry.getKey();
            List<ScheduleTaskTaskShade> value = entry.getValue();
            if (CollectionUtils.isNotEmpty(value)) {
                List<Long> taskId = Lists.newArrayList();
                if (DisplayDirect.FATHER.getType().equals(directType)) {
                   taskId = value.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());
                } else if (DisplayDirect.CHILD.getType().equals(directType)) {
                   taskId = value.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toList());
                }
                tasks.addAll(taskShadeService.getTaskByIds(new ArrayList<>(taskId), appType));

            }
        }
        return tasks;
    }

    public List<ScheduleTaskVO> getRefTask(Set<Long> taskIds, int level, Integer directType, Long currentProjectId, Integer appType, List<ScheduleTaskVO> ruleTaskList,
                                           Map<Long,UICTenantVO> cacheTenant,Table<Long,Integer,AuthProjectVO> cacheProject){

        if (CollectionUtils.isEmpty(ruleTaskList)) {
            ruleTaskList = Lists.newArrayList();
        }

        //获得所有父节点task
        List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            Integer taskRule = task.getTaskRule();

            if (TaskRuleEnum.NO_RULE.getCode().equals(taskRule)) {
                refTaskVoList.add(this.getOffSpring(task, level, directType, currentProjectId,task.getAppType(),cacheTenant,cacheProject));
            } else {
                ruleTaskList.add(this.getOffSpring(task, level, directType, currentProjectId,task.getAppType(),cacheTenant,cacheProject));
            }
        }
        return refTaskVoList;
    }

    /**
     * 获取工作流全部子节点信息 -- 依赖树
     *  ps- 不包括工作流父节点
     * 优化新
     * @param flowId 工作流父节点id
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOnlyAllFlowSubTasksNew(Long flowId, Integer appType,Map<Long,UICTenantVO> cacheTenant,Table<Long,Integer,AuthProjectVO> cacheProject) {

        //工作流最多展开多少层
        Integer level = context.getWorkFlowLevel();
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO();
        //获取工作流顶部节点
        ScheduleTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(flowId,appType);
        if(beginTaskShade!=null) {
            //展开工作流全部节点，不包括工作流父节点
            vo = getFlowWorkOffSpringNew(beginTaskShade,appType,level,new ArrayList<>(),cacheTenant,cacheProject);
        }
        return vo;
    }

    /**
     * 获取工作流全部子节点信息 -- 依赖树
     *  ps- 不包括工作流父节点
     *
     * @param flowId 工作流父节点id
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getOnlyAllFlowSubTasks(Long flowId, Integer appType) {

        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO();
        //获取工作流顶部节点
        ScheduleTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(flowId,appType);
        if(beginTaskShade!=null) {
            //展开工作流全部节点，不包括工作流父节点
            Integer workFlowLevel = context.getWorkFlowLevel();
            vo = getFlowWorkOffSpring(beginTaskShade, 1,appType,workFlowLevel);
        }
        return vo;
    }




    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    public com.dtstack.engine.master.vo.ScheduleTaskVO getAllFlowSubTasks( Long taskId,  Integer appType) {

        //工作流任务信息
        ScheduleTaskShade task = taskShadeService.getBatchTaskById(taskId,appType);
        if (null == task) {
            return null;
        }
        //构建父节点信息
        com.dtstack.engine.master.vo.ScheduleTaskVO parentNode = new com.dtstack.engine.master.vo.ScheduleTaskVO(task, true);
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO();
        //获取工作流最顶层结点
        ScheduleTaskShade beginTaskShade = taskShadeService.getWorkFlowTopNode(taskId,appType);
        if(beginTaskShade!=null) {
            //获取工作流下游结点
            Integer workFlowLevel = context.getWorkFlowLevel();
            vo = getFlowWorkOffSpring(beginTaskShade, 1,appType,workFlowLevel);
        }
        parentNode.setSubTaskVOS(Arrays.asList(vo));
        return parentNode;
    }

    /**
     * 向下展开工作流全部节点,增加max参数，万一循环依赖,优化的新方法
     * 最多查询10层就返回，防止内存溢出
     * @param taskShade
     * @param level
     * @param taskIdRelations 任务id关联列表，用来做成环检测
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getFlowWorkOffSpringNew(ScheduleTaskShade taskShade, Integer appType,int level,List<String> taskIdRelations,Map<Long,UICTenantVO> cacheTenant,Table<Long,Integer,AuthProjectVO> cacheProject) {


        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        setTenant(vo.getDtuicTenantId(),cacheTenant,vo);
        setProject(vo.getProjectId(),vo.getAppType(),cacheProject,vo);
        //查询子任务列表
        List<ScheduleTaskTaskShade> childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(),taskShade.getAppType());
        if (CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        if(checkIsLoop(taskIdRelations,childTaskTasks)){
            //如果成环，则返回null
            return vo;
        }
        if(level<=0){
            return vo;
        }
        //获取子任务taskId集合
        Set<Long> taskIds = childTaskTasks.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toSet());
        level--;
        //获得所有父节点task
        List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return vo;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            refTaskVoList.add(this.getFlowWorkOffSpringNew(task,appType,level,taskIdRelations,cacheTenant,cacheProject));
        }
        if (CollectionUtils.isNotEmpty(refTaskVoList) && refTaskVoList.get(0)!=null) {
            vo.setSubTaskVOS(refTaskVoList);
        }
        return vo;
    }

    /**
     * 向下展开工作流全部节点,增加max参数，万一循环依赖
     * 最多查询10层就返回，防止内存溢出
     * @param taskShade
     * @param level
     * @return
     */
    private com.dtstack.engine.master.vo.ScheduleTaskVO getFlowWorkOffSpring(ScheduleTaskShade taskShade, int level, Integer appType,int max) {

        if(max<=0){
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        List<ScheduleTaskTaskShade> childTaskTasks = null;
        //查询子任务列表
        childTaskTasks = scheduleTaskTaskShadeDao.listChildTask(taskShade.getTaskId(),taskShade.getAppType());
        if (CollectionUtils.isEmpty(childTaskTasks)) {
            return vo;
        }
        Set<Long> taskIds = new HashSet<>(childTaskTasks.size());
        //获取子任务id集合
        childTaskTasks.forEach(taskTask -> taskIds.add(taskTask.getTaskId()));
        max--;
        List<ScheduleTaskVO> childTaskList = getFlowWorkSubTasksRefTask(taskIds, level, DisplayDirect.CHILD.getType(),appType,max);
        if (childTaskList != null) {
            vo.setSubTaskVOS(childTaskList);
        }
        return vo;
    }

    /**
     * @author newman
     * @Description 获取所有工作流子节点的子任务
     * @Date 2020-12-17 10:25
     * @param taskIds:
     * @param level:
     * @param directType:
     * @param appType:
     * @return: java.util.List<com.dtstack.engine.api.vo.ScheduleTaskVO>
     **/
    public List<ScheduleTaskVO> getFlowWorkSubTasksRefTask(Set<Long> taskIds, int level, Integer directType, Integer appType,int max) {

        //获得所有父节点task
        List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(new ArrayList<>(taskIds),appType);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        List<ScheduleTaskVO> refTaskVoList = new ArrayList<>(tasks.size());
        for (ScheduleTaskShade task : tasks) {
            refTaskVoList.add(this.getFlowWorkOffSpring(task, level,appType,max));
        }

        return refTaskVoList;
    }

    public List<ScheduleTaskTaskShade> getTaskOtherPlatformByProjectId(Long projectId, Integer appType, Integer listChildTaskLimit) {
        return scheduleTaskTaskShadeDao.getTaskOtherPlatformByProjectId(projectId,appType,listChildTaskLimit);
    }
}
