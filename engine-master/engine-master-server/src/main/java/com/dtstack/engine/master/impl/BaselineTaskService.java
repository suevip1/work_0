package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.dto.BaselineTaskBatchDTO;
import com.dtstack.engine.po.BaselineTaskConditionModel;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.api.dto.BaselineTaskBatchDTO;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.BaselineTypeEnum;
import com.dtstack.engine.api.enums.OpenStatusEnum;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.BaselineBatchVO;
import com.dtstack.engine.api.vo.BaselineTaskBatchVO;
import com.dtstack.engine.api.vo.alert.*;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.BaselineTaskDao;
import com.dtstack.engine.dao.BaselineTaskTaskDao;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.mapstruct.BaselineTaskBatchStruct;
import com.dtstack.engine.master.mapstruct.BaselineTaskStruct;
import com.dtstack.engine.master.sync.baseline.DAGMapBaselineJobBuildr;
import com.dtstack.engine.master.utils.SetUtil;
import com.dtstack.engine.po.BaselineTask;
import com.dtstack.engine.po.BaselineTaskBatch;
import com.dtstack.engine.po.BaselineTaskConditionModel;
import com.dtstack.engine.po.BaselineTaskTask;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 11:02 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class BaselineTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaselineTaskService.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private BaselineTaskDao baselineTaskDao;

    @Autowired
    private BaselineTaskTaskDao baselineTaskTaskDao;

    @Autowired
    private BaselineTaskStruct baselineTaskStruct;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private AlertAlarmService alertAlarmService;

    @Autowired
    private BaselineTaskBatchService baselineTaskBatchService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BaselineTaskBatchStruct baselineTaskBatchStruct;

    public PageResult<List<BaselineTaskPageVO>> page(BaselineTaskConditionVO vo) {

        BaselineTaskConditionModel baselineTaskConditionModel = baselineTaskStruct.voToBaselineTaskConditionModel(vo);
        Long count = baselineTaskDao.countByModel(baselineTaskConditionModel);

        List<BaselineTaskPageVO> vos = null;
        if (count > 0) {
            List<BaselineTask> baselineTasks = baselineTaskDao.selectByModel(baselineTaskConditionModel);
            vos =  baselineTaskStruct.baselineTaskToBaselineTaskPageVO(baselineTasks);
            List<Long> ids = vos.stream().map(BaselineTaskPageVO::getId).collect(Collectors.toList());
            List<BaselineTaskBatchDTO> baselineBatchByBaselineTaskIds = baselineTaskBatchService.getBaselineBatchByBaselineTaskIds(ids);
            Map<Long, List<BaselineTaskBatchDTO>> batchMap = baselineBatchByBaselineTaskIds.stream().collect(Collectors.groupingBy(BaselineTaskBatchDTO::getBaselineTaskId));

            for (BaselineTaskPageVO taskPageVO : vos) {
                List<BaselineTaskBatchDTO> baselineTaskBatchDTOS = batchMap.get(taskPageVO.getId());
                if (CollectionUtils.isNotEmpty(baselineTaskBatchDTOS)) {
                    List<String> replyTime = baselineTaskBatchDTOS.stream()
                            .filter(baselineTaskBatchDTO -> OpenStatusEnum.OPEN.getCode().equals(baselineTaskBatchDTO.getOpenStatus()))
                            .map(BaselineTaskBatchDTO::getReplyTime).collect(Collectors.toList());
                    taskPageVO.setReplyTime(Joiner.on(", ").join(replyTime));
                }
            }
        }

        return new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), count.intValue(), vos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer addOrUpdateBaselineTask(BaselineTaskVO baselineTaskVO) {
        List<AlarmChooseTaskDTO> alarmChooseTaskDTOList =  baselineTaskBatchStruct.toAlarmChooseTaskDTOs(baselineTaskVO.getTaskVOS());
        List<BaselineBatchVO> baselineBatchDTOS = calculateBatch(alarmChooseTaskDTOList,baselineTaskVO);
        check(baselineTaskVO,baselineBatchDTOS);
        Integer count;
        BaselineTask baselineTask = baselineTaskStruct.baselineTaskVOTOBaselineTask(baselineTaskVO);
        baselineTask.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());

        if (baselineTask.getPriority()==null) {
            baselineTask.setPriority(1);
        }

        Long id = baselineTask.getId();
        String baselineName = baselineTaskVO.getName();

        if (StringUtils.isBlank(baselineName)) {
            throw new RdosDefineException("新增基线必须输入基线名称");
        }

        if (id == null) {
            String name = baselineTaskDao.selectByName(baselineName, baselineTaskVO.getProjectId());
            if (StringUtils.isNotBlank(name)) {
                throw new RdosDefineException(name+ "基线任务已存在，请变更名称！");
            }

            Integer openStatus = baselineTask.getOpenStatus();
            if (openStatus == null) {
                baselineTask.setOpenStatus(OpenStatusEnum.OPEN.getCode());
            }

            count = baselineTaskDao.insert(baselineTask);
            id = baselineTask.getId();
        } else {
            BaselineTask dbBaselineTask = baselineTaskDao.selectByPrimaryKey(id);

            if (dbBaselineTask == null || !baselineName.equals(dbBaselineTask.getName())) {
                String name = baselineTaskDao.selectByName(baselineName, baselineTaskVO.getProjectId());
                if (StringUtils.isNotBlank(name)) {
                    throw new RdosDefineException(name + "基线任务已存在，请变更名称！");
                }
            }
            count = baselineTaskDao.updateByPrimaryKeySelective(baselineTask);
        }

        List<AlarmChooseTaskVO> taskVOS = baselineTaskVO.getTaskVOS();

        // 删除 tasktask
        baselineTaskTaskDao.deleteByBaselineTaskId(id);
        List<BaselineTaskTask> baselineTaskTasks = Lists.newArrayList();
        for (AlarmChooseTaskVO taskVO : taskVOS) {
            BaselineTaskTask task = new BaselineTaskTask();
            task.setTaskId(taskVO.getTaskId());
            task.setTaskAppType(taskVO.getAppType());
            task.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            task.setBaselineTaskId(id);
            baselineTaskTasks.add(task);
        }
        baselineTaskTaskDao.batchInsert(baselineTaskTasks);

        // 删除批次
        baselineTaskBatchService.deleteByBaselineTaskId(id);
        List<BaselineTaskBatch> baselineTaskBatches = Lists.newArrayList();
        for (BaselineBatchVO baselineBatchDTO : baselineBatchDTOS) {
            BaselineTaskBatch baselineTaskBatch = new BaselineTaskBatch();
            baselineTaskBatch.setBaselineTaskId(id);
            baselineTaskBatch.setOpenStatus(baselineBatchDTO.getOpenStatus() == null ?
                    OpenStatusEnum.OPEN.getCode() : baselineBatchDTO.getOpenStatus());
            baselineTaskBatch.setReplyTime(baselineBatchDTO.getReplyTime());
            baselineTaskBatch.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());

            if (StringUtils.isNotBlank(baselineBatchDTO.getCycTime())) {
                baselineTaskBatch.setCycTime(baselineBatchDTO.getCycTime());
            } else {
                baselineTaskBatch.setCycTime("");
            }

            baselineTaskBatches.add(baselineTaskBatch);
        }
        baselineTaskBatchService.batchInsert(baselineTaskBatches);
        return count;
    }

    private List<BaselineBatchVO> calculateBatch(List<AlarmChooseTaskDTO> alarmChooseTaskDTOList, BaselineTaskVO baselineTaskVO) {
        if (BaselineTypeEnum.SINGLE_BATCH.getCode().equals(baselineTaskVO.getBatchType())) {
            return baselineTaskVO.getBaselineBatchDTOS();
        } else {
            List<BaselineBatchVO> baselineBatchDTOS = baselineTaskVO.getBaselineBatchDTOS();
            Map<String, List<BaselineBatchVO>> baseMap = baselineBatchDTOS.stream().collect(Collectors.groupingBy(BaselineBatchVO::getCycTime));
            List<BaselineBatchVO> baselineBatchVOS = calculateBatch(alarmChooseTaskDTOList, baselineTaskVO.getAppType());

            for (BaselineBatchVO baselineBatchVO : baselineBatchVOS) {
                List<BaselineBatchVO> baselineTaskBatchVOS = baseMap.get(baselineBatchVO.getCycTime());
                if (CollectionUtils.isNotEmpty(baselineTaskBatchVOS)) {
                    Optional<BaselineBatchVO> baselineTaskBatchDTO = baselineTaskBatchVOS.stream().findFirst();
                    if (baselineTaskBatchDTO.isPresent()) {
                        BaselineBatchVO dto = baselineTaskBatchDTO.get();
                        baselineBatchVO.setReplyTime(dto.getReplyTime());
                        baselineBatchVO.setOpenStatus(dto.getOpenStatus());
                    }
                }
            }
            return baselineBatchVOS;
        }
    }

    private List<BaselineBatchVO> calculateBatch ( List<AlarmChooseTaskDTO> alarmChooseTaskDTOList,Integer appType) {

        String triggerDay = DateTime.now().toString(DateUtil.DATE_FORMAT);
        Set<String> cycTimeSet = baselineTaskBatchService.calculationBatch(triggerDay, alarmChooseTaskDTOList);

        if (CollectionUtils.isEmpty(cycTimeSet)) {
            throw new RdosDefineException("创建时当日必须有批次，选择的任务在"+triggerDay+"中没有批次，或者任务的批次不一致，所以无法创建");
        }

        Map<String,String> estimated =  new DAGMapBaselineJobBuildr(applicationContext)
                .getBatchEstimatedFinish(triggerDay+ " 00:00:00",alarmChooseTaskDTOList,appType,cycTimeSet);

        List<BaselineBatchVO> baselineBatchVOS = Lists.newArrayList();
        for (String cycTime : cycTimeSet) {
            String timeStrWithoutSymbol = DateUtil.getTimeStrWithoutSymbol(cycTime);
            BaselineBatchVO baselineBatchVO = new BaselineBatchVO();
            baselineBatchVO.setOpenStatus(OpenStatusEnum.OPEN.getCode());
            try {
                baselineBatchVO.setCycTime(DateUtil.getDateStrTOFormat(cycTime,DateUtil.STANDARD_DATETIME_FORMAT,DateUtil.STANDARD_FORMAT));
            } catch (ParseException e) {
                LOGGER.error("",e);
            }
            baselineBatchVO.setEstimatedFinishTime(estimated.get(timeStrWithoutSymbol)!=null ? estimated.get(timeStrWithoutSymbol): null);
            baselineBatchVOS.add(baselineBatchVO);
        }
        return baselineBatchVOS;
    }

    private void check(BaselineTaskVO baselineTaskVO,List<BaselineBatchVO> calculateBatchDTOS) {
        List<BaselineBatchVO> baselineBatchDTOS = baselineTaskVO.getBaselineBatchDTOS();


        if (CollectionUtils.isEmpty(calculateBatchDTOS)) {
            throw new RdosDefineException("无法计算批次");
        }

        for (BaselineBatchVO baselineBatchDTO : baselineBatchDTOS) {
            if (baselineBatchDTO.getOpenStatus() == null) {
                baselineBatchDTO.setOpenStatus(OpenStatusEnum.OPEN.getCode());
            }
        }

        Map<Integer, List<BaselineBatchVO>> map = baselineBatchDTOS.stream().collect(Collectors.groupingBy(BaselineBatchVO::getOpenStatus));
        List<BaselineBatchVO> baselineBatchVOS = map.get(OpenStatusEnum.OPEN.getCode());
        if (CollectionUtils.isEmpty(baselineBatchVOS)) {
            throw new RdosDefineException("至少有一个批次加入监控");
        }

        if (CollectionUtils.isEmpty(baselineBatchDTOS)) {
            throw new RdosDefineException("请输入承诺时间");
        }

        if (baselineBatchDTOS.size() == calculateBatchDTOS.size()) {
            Set<String> batchList = baselineBatchDTOS.stream().map(BaselineBatchVO::getCycTime).collect(Collectors.toSet());
            Set<String> calcuateList = calculateBatchDTOS.stream().map(BaselineBatchVO::getCycTime).collect(Collectors.toSet());

            if (!SetUtil.isSetEqual(batchList,calcuateList)) {
                throw new RdosDefineException("修改任务后请确保批次调度周期完全一致后在提交");
            }
        }

        for (BaselineBatchVO baselineBatchDTO : baselineBatchDTOS) {
            if (OpenStatusEnum.OPEN.getCode().equals(baselineBatchDTO.getOpenStatus())) {
                if (baselineBatchDTO.getReplyTime() == null) {
                    throw new RdosDefineException("请输入承诺时间");
                }

                String replyTime = new DateTime().toString(DateUtil.DATE_FORMAT) + " " +baselineBatchDTO.getReplyTime();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.STANDARD_DATE_FORMAT);
                try {
                    simpleDateFormat.parse(replyTime);
                } catch (ParseException e) {
                    throw new RdosDefineException("承诺时间格式有误，请输入： 00:00");
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteBaselineTask(Long id) {
        if (id == null) {
            return 0;
        }

        BaselineTask baselineTask = baselineTaskDao.selectByPrimaryKey(id);

        if (baselineTask != null) {
            Integer integer = baselineTaskDao.deleteById(id);
            baselineTaskTaskDao.deleteByBaselineTaskId(id);
            alertAlarmService.deleteAlarmTask(Lists.newArrayList(baselineTask.getId()), baselineTask.getAppType(), AlarmRuleBusinessTypeEnum.BASELINE);
            return integer;
        }

        return 0;
    }


    public Integer openOrClose(Long id, Integer openStatus) {
        BaselineTask baselineTask = new BaselineTask();
        baselineTask.setOpenStatus(openStatus);
        baselineTask.setId(id);
        return baselineTaskDao.updateByPrimaryKeySelective(baselineTask);
    }

    public BaselineTaskVO getBaselineTaskDetails (Long id) {
        BaselineTask baselineTask = baselineTaskDao.selectByPrimaryKey(id);
        if (baselineTask == null) {
            throw new RdosDefineException("基线任务不存在");
        }
        BaselineTaskVO vo = baselineTaskStruct.baselineTaskToBaselineTaskVO(baselineTask);
        List<AlarmChooseTaskVO> vos = getAlarmChooseTaskVOS(id, baselineTask.getAppType());
        vo.setTaskVOS(vos);

        List<BaselineTaskBatchDTO> baselineBatchByBaselineTaskDTO = baselineTaskBatchService.getBaselineBatchByBaselineTaskId(id);
        List<String> replyTime = baselineBatchByBaselineTaskDTO.stream().map(BaselineTaskBatchDTO::getReplyTime).collect(Collectors.toList());
        List<BaselineBatchVO> baselineBatchVOS;

        if (BaselineTypeEnum.SINGLE_BATCH.getCode().equals(baselineTask.getBatchType())) {
            baselineBatchVOS = Lists.newArrayList();
            BaselineBatchVO baselineBatchVO = new BaselineBatchVO();
            baselineBatchVO.setReplyTime(Joiner.on(",").join(replyTime));
            baselineBatchVO.setCycTime("");
            baselineBatchVO.setOpenStatus(OpenStatusEnum.OPEN.getCode());
            List<Long> taskIds = vos.stream().map(AlarmChooseTaskVO::getTaskId).collect(Collectors.toList());
            baselineBatchVO.setEstimatedFinishTime(estimatedFinish(taskIds,vo.getAppType()));
            baselineBatchVOS.add(baselineBatchVO);
        } else {
            baselineBatchVOS = calculateBatch(baselineTaskBatchStruct.toAlarmChooseTaskDTOs(vos), vo.getAppType());
            Map<String, List<BaselineTaskBatchDTO>>  baseMap = baselineBatchByBaselineTaskDTO.stream().collect(Collectors.groupingBy(BaselineTaskBatchDTO::getCycTime));
            for (BaselineBatchVO baselineBatchVO : baselineBatchVOS) {
                List<BaselineTaskBatchDTO> baselineTaskBatchDTOS = null;
                baselineTaskBatchDTOS = baseMap.get(baselineBatchVO.getCycTime());
                if (CollectionUtils.isNotEmpty(baselineTaskBatchDTOS)) {
                    Optional<BaselineTaskBatchDTO> baselineTaskBatchDTO = baselineTaskBatchDTOS.stream().findFirst();
                    if (baselineTaskBatchDTO.isPresent()) {
                        BaselineTaskBatchDTO dto = baselineTaskBatchDTO.get();
                        baselineBatchVO.setReplyTime(dto.getReplyTime());
                        baselineBatchVO.setOpenStatus(dto.getOpenStatus());
                    }
                }
            }
        }
        vo.setBaselineBatchDTOS(baselineBatchVOS);
        return vo;
    }

    public List<BaselineTaskVO> getBaselineTaskDetailsByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }

        List<BaselineTask> baselineTasks = baselineTaskDao.selectByIds(ids);

        if (CollectionUtils.isEmpty(baselineTasks)) {
            return Lists.newArrayList();
        }

        return baselineTaskStruct.toBaselineTaskVOs(baselineTasks);
    }

    @NotNull
    public List<AlarmChooseTaskVO> getAlarmChooseTaskVOS(Long id, Integer appType) {
        List<BaselineTaskTask> baselineTaskTasks = baselineTaskTaskDao.selectByBaselineTaskId(id);

        List<Long> taskIds = baselineTaskTasks.stream().map(BaselineTaskTask::getTaskId).collect(Collectors.toList());
        List<ScheduleTaskShade> taskShades = scheduleTaskShadeService.getTaskByIds(taskIds, appType);
        Map<String, ScheduleTaskShade> taskMap = taskShades.stream().collect(Collectors.toMap(taskShade -> taskShade.getTaskId() + "-" + taskShade.getAppType()
                , g -> (g)));
        List<AlarmChooseTaskVO> vos = Lists.newArrayList();
        for (BaselineTaskTask taskTask : baselineTaskTasks) {
            AlarmChooseTaskVO alarmChooseTaskVO = new AlarmChooseTaskVO();
            alarmChooseTaskVO.setTaskId(taskTask.getTaskId());
            alarmChooseTaskVO.setAppType(taskTask.getTaskAppType());
            ScheduleTaskShade scheduleTaskShade = taskMap.get(taskTask.getTaskId() + "-" + taskTask.getTaskAppType());
            if (scheduleTaskShade != null) {
                alarmChooseTaskVO.setOwnerUserId(scheduleTaskShade.getOwnerUserId());
                alarmChooseTaskVO.setTaskName(scheduleTaskShade.getName());
                alarmChooseTaskVO.setTaskType(scheduleTaskShade.getTaskType());
                alarmChooseTaskVO.setPeriodType(scheduleTaskShade.getPeriodType());
            }
            vos.add(alarmChooseTaskVO);
        }
        return vos;
    }

    public String estimatedFinish(List<Long> taskIds, Integer appType) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return GlobalConst.ESTIMATED_FINISH_TIME;
        }

        if (taskIds.size() > environmentContext.getMaxBaselineTaskNum()) {
            throw new RdosDefineException("选择任务数不能超过: " + environmentContext.getMaxBaselineTaskNum());
        }
        long max = 0;
        for (Long taskId : taskIds) {
            List<java.sql.Timestamp> execTimes = scheduleJobService.findTopEndRunTimeByTaskIdsAndAppType(taskId, appType, environmentContext.getMaxBaselineJobNum());

            if (CollectionUtils.isEmpty(execTimes)) {
                return GlobalConst.ESTIMATED_FINISH_TIME;
            }
            long sum = execTimes.stream().mapToLong(java.sql.Timestamp::getTime).sum();
            long average = sum / execTimes.size();

            if (max < average) {
                max = average;
            }
        }

        if (max == 0) {
            return GlobalConst.ESTIMATED_FINISH_TIME;
        }

        return new DateTime(max).toString("HH:mm:ss");
    }


    public List<BaselineTaskDTO> scanningBaselineTask(Long startId, Integer limit) {
        if (startId == null || limit == null) {
            return Lists.newArrayList();
        }

        List<BaselineTask> baselineTasks = baselineTaskDao.selectRangeIdOfLimit(startId, OpenStatusEnum.OPEN.getCode(), limit);
        List<Long> baselineTaskIds = baselineTasks.stream().map(BaselineTask::getId).collect(Collectors.toList());
        List<BaselineTaskTask> baselineTaskTasks = baselineTaskTaskDao.selectByBaselineTaskIds(baselineTaskIds);
        Map<Long, List<BaselineTaskTask>> baselineTaskTaskMap = baselineTaskTasks.stream().collect(Collectors.groupingBy(BaselineTaskTask::getBaselineTaskId));

        List<BaselineTaskDTO> dtoList = Lists.newArrayList();
        for (BaselineTask baselineTask : baselineTasks) {
            BaselineTaskDTO dto = buildBaselineDTO(baselineTaskTaskMap, baselineTask);
            dtoList.add(dto);
        }

        return dtoList;
    }

    public BaselineTaskDTO buildBaselineDTO(Map<Long, List<BaselineTaskTask>> baselineTaskTaskMap, BaselineTask baselineTask) {
        BaselineTaskDTO dto = baselineTaskStruct.baselineTaskToBaselineTaskDTO(baselineTask);

        List<BaselineTaskTask> taskTasks = baselineTaskTaskMap.get(baselineTask.getId());

        if (CollectionUtils.isNotEmpty(taskTasks)) {
            List<AlarmChooseTaskDTO> taskVOS = baselineTaskStruct.baselineTaskTasksToAlarmChooseTaskDTOs(taskTasks);
            dto.setTaskVOS(taskVOS);
        }

        return dto;
    }

    public List<BaselineTaskDTO> getBaselineTaskByIds(List<Long> baselineTaskIds) {
        if (CollectionUtils.isEmpty(baselineTaskIds)) {
            return Lists.newArrayList();
        }

        List<BaselineTask> baselineTasks = baselineTaskDao.selectByIds(baselineTaskIds);
        return baselineTaskStruct.baselineTaskToBaselineTaskDTOs(baselineTasks);
    }


    public Integer deleteBaselineTaskByProjectId(Long projectId,Integer appType) {
        if (projectId == null) {
            return 0;
        }
        return baselineTaskDao.deleteBaselineTaskByProjectId(projectId,appType);
    }

    public BaselineTaskDTO getBaselineTaskDTO(Long id) {
        if (id == null) {
            return null;
        }

        BaselineTask baselineTask = baselineTaskDao.selectByPrimaryKey(id);

        if (baselineTask == null) {
            return null;
        }

        List<BaselineTaskTask> baselineTaskTasks = baselineTaskTaskDao.selectByBaselineTaskIds(Lists.newArrayList(baselineTask.getId()));
        Map<Long, List<BaselineTaskTask>> baselineTaskTaskMap = baselineTaskTasks.stream().collect(Collectors.groupingBy(BaselineTaskTask::getBaselineTaskId));
        return buildBaselineDTO(baselineTaskTaskMap,baselineTask);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskOfBaseline(List<Long> taskIds, Integer appType) {
        if (CollectionUtils.isEmpty(taskIds) || appType == null) {

            return;
        }
        List<Long> baseLineTaskList = baselineTaskTaskDao.selectByTaskIds(taskIds, appType);

        if (CollectionUtils.isEmpty(baseLineTaskList)) {
            return;
        }

        List<BaselineTask> baselineTasks = baselineTaskDao.selectByIds(baseLineTaskList);

        List<Long> isDeleteTaskTask = Lists.newArrayList();
        List<Long> isDeleteBaselineTask = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(baselineTasks)) {
            List<Long> baseLineTaskIdList = baselineTasks.stream().map(BaselineTask::getId).collect(Collectors.toList());
            List<BaselineTaskTask> baselineTaskTasks = baselineTaskTaskDao.selectByBaselineTaskIds(baseLineTaskIdList);

            Map<Long, List<BaselineTaskTask>> baselineTaskTaskMaps = baselineTaskTasks.stream().collect(Collectors.groupingBy(BaselineTaskTask::getBaselineTaskId));

            for (Long baselineTaskId : baselineTaskTaskMaps.keySet()) {
                List<BaselineTaskTask> baselineTaskTaskList = baselineTaskTaskMaps.get(baselineTaskId);

                if (CollectionUtils.isEmpty(baselineTaskTaskList)) {
                    continue;
                }

                if (baselineTaskTaskList.size()==1) {
                    BaselineTaskTask baselineTaskTask = baselineTaskTaskList.get(0);

                    if (taskIds.contains(baselineTaskTask.getTaskId())
                            && appType.equals(baselineTaskTask.getTaskAppType())) {
                        isDeleteTaskTask.add(baselineTaskTask.getId());
                        isDeleteBaselineTask.add(baselineTaskId);
                    }
                } else {
                    for (BaselineTaskTask baselineTaskTask : baselineTaskTaskList) {
                        if (taskIds.contains(baselineTaskTask.getTaskId())
                                && appType.equals(baselineTaskTask.getTaskAppType())) {
                            isDeleteTaskTask.add(baselineTaskTask.getId());
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(isDeleteBaselineTask)) {
            baselineTaskDao.deleteByIds(isDeleteBaselineTask);
        }

        if (CollectionUtils.isNotEmpty(isDeleteTaskTask)) {
            baselineTaskTaskDao.deleteByIds(isDeleteTaskTask);
        }

        if(CollectionUtils.isNotEmpty(isDeleteBaselineTask)){
            alertAlarmService.deleteAlarmTask(isDeleteBaselineTask,appType, AlarmRuleBusinessTypeEnum.BASELINE);
        }

    }

    public Integer updateBaselineOwner(List<Long> oldOwnerUserIds, Long newOwnerUserId) {
        if (CollectionUtils.isEmpty(oldOwnerUserIds) && newOwnerUserId == null) {
            return 0;
        }

        return baselineTaskDao.updateOwnerByOwnerIds(oldOwnerUserIds,newOwnerUserId);
    }

    public List<BaselineSimpleVO> findBaselineInfoByTaskIds(List<Long> taskIds, Integer appType) {
        List<BaselineSimpleVO> baselineSimpleVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(taskIds)) {
            return baselineSimpleVOS;
        }

        List<BaselineTaskTask> baselineTaskTasks = baselineTaskTaskDao.selectAllByTaskIds(taskIds, appType);

        if (CollectionUtils.isEmpty(baselineTaskTasks)) {
            return baselineSimpleVOS;
        }

        List<Long> baseLineTaskIds = baselineTaskTasks.stream().map(BaselineTaskTask::getBaselineTaskId).collect(Collectors.toList());
        Map<Long, List<BaselineTaskTask>> taskTaskMaps = baselineTaskTasks.stream().collect(Collectors.groupingBy(BaselineTaskTask::getBaselineTaskId));
        List<BaselineTask> baselineTasks = baselineTaskDao.selectByIds(baseLineTaskIds);

        for (BaselineTask baselineTask : baselineTasks) {
            BaselineSimpleVO baselineSimpleVO = new BaselineSimpleVO();
            baselineSimpleVO.setId(baselineTask.getId());
            baselineSimpleVO.setBaselineName(baselineTask.getName());
            baselineSimpleVO.setAppType(baselineTask.getAppType());

            List<BaselineTaskTask> baselineTaskTask = taskTaskMaps.get(baselineTask.getId());

            if (CollectionUtils.isNotEmpty(baselineTaskTask)) {
                List<Long> baselineTaskIds = baselineTaskTask.stream().map(BaselineTaskTask::getTaskId).collect(Collectors.toList());
                baselineSimpleVO.setTaskIds(baselineTaskIds);
            }

            baselineSimpleVOS.add(baselineSimpleVO);
        }

        return baselineSimpleVOS;
    }

    public List<BaselineBatchVO> getBaselineBatch(BaselineTaskBatchVO vo) {
        List<Long> taskIds = vo.getTaskIds();

        if (CollectionUtils.isEmpty(taskIds)){
            return Lists.newArrayList();
        }

        List<AlarmChooseTaskDTO> alarmChooseTaskDTOList =  Lists.newArrayList();
        for (Long taskId : taskIds) {
            AlarmChooseTaskDTO alarmChooseTaskDTO = new AlarmChooseTaskDTO();
            alarmChooseTaskDTO.setTaskId(taskId);
            alarmChooseTaskDTO.setAppType(vo.getAppType());
            alarmChooseTaskDTOList.add(alarmChooseTaskDTO);
        }

        return calculateBatch(alarmChooseTaskDTOList,vo.getAppType()).stream()
                .sorted((a,b) -> (int)(DateUtil.getTimestamp(a.getCycTime(),DateUtil.STANDARD_FORMAT)
                        - DateUtil.getTimestamp(b.getCycTime(),DateUtil.STANDARD_FORMAT))).collect(Collectors.toList());
    }


}
