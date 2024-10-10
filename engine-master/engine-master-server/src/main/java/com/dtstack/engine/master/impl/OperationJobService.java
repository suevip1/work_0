package com.dtstack.engine.master.impl;

import cn.hutool.core.collection.CollUtil;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.SchedulePeriodVO;
import com.dtstack.engine.api.vo.job.JobPageVO;
import com.dtstack.engine.api.vo.job.JobReturnPageVO;
import com.dtstack.engine.api.vo.job.JobTimeVO;
import com.dtstack.engine.api.vo.job.SortVO;
import com.dtstack.engine.api.vo.job.StatisticsJobVO;
import com.dtstack.engine.api.vo.task.TaskSearchNameVO;
import com.dtstack.engine.common.callback.Callback;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.DisplayDirect;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.JobPageDTO;
import com.dtstack.engine.dto.JobTimeDTO;
import com.dtstack.engine.dto.SortDTO;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.enums.PriorityEnum;
import com.dtstack.engine.master.mapstruct.OperationJobStruct;
import com.dtstack.engine.master.utils.CheckUtils;
import com.dtstack.engine.master.utils.ListUtil;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.po.ScheduleTaskPriority;
import com.dtstack.engine.po.ScheduleTaskTag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.models.auth.In;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName OperationJobService
 * @date 2022/7/19 2:45 PM
 */
@Service
public class OperationJobService {

    /**
     * 限制查询日期范围，默认半年(183 天)
     */
    @Value("${job.query.restrict.days:183}")
    private Integer jobQueryRestrictDays;

    /**
     * 限制查询条件中的实例数，默认 95w
     */
    @Value("${job.query.restrict.num:950000}")
    private Integer jobQueryRestrictNum;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private OperationJobStruct operationJobStruct;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private ScheduleTaskTagService scheduleTaskTagService;

    @Autowired
    private ScheduleFillDataJobService scheduleFillDataJobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    public PageResult<List<JobReturnPageVO>> page(JobPageVO vo) {
        Integer totalCount = 0;
        PageResult<List<JobReturnPageVO>> pageResult = new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), totalCount, Lists.newArrayList());
        if (!checkFillCreateSuccess(vo,pageResult)) {
            return pageResult;
        }

        JobPageDTO dto = getJobPageDTO(vo, false);
        if (dto == null) {
            return pageResult;
        }
        restrictTooManyJobIds(dto, false);
        totalCount = scheduleJobDao.countByJobPageDTO(dto);
        List<JobReturnPageVO> jobReturnPageVOS = Lists.newArrayList();
        if (totalCount > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.pageByJobPageDTO(dto);
            jobReturnPageVOS = getJobReturnPageVO(scheduleJobs,dto.getFlowMap(),dto.getFlowJobMap(),(param)-> Optional.of(buildJobReturnPageVO(param.groupInfo,
                    param.taskMaps,param.userMap,param.flowMap,param.getTagMaps(),param.flowJobMap,param.priorityMap,param.scheduleJob)));
        }

        pageResult.setData(jobReturnPageVOS);
        pageResult.setTotalCount(totalCount);
        return pageResult;
    }

    private boolean checkFillCreateSuccess(JobPageVO vo, PageResult<List<JobReturnPageVO>> pageResult) {
        Long fillId = vo.getFillId();

        if (fillId == null) {
            return true;
        }

        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobService.getFillById(fillId);

        if (scheduleFillDataJob == null) {
            return true;
        }

        Integer fillGeneratStatus = scheduleFillDataJob.getFillGeneratStatus();

        if (FillGeneratStatusEnum.REALLY_GENERATED.getType().equals(fillGeneratStatus)) {
            pageResult.setSuccess(false);
            pageResult.setMsg(FillGeneratStatusEnum.REALLY_GENERATED.getName());
            return false;
        }

        if (FillGeneratStatusEnum.FILL_FAIL.getType().equals(fillGeneratStatus)) {
            pageResult.setSuccess(false);
            pageResult.setMsg(FillGeneratStatusEnum.FILL_FAIL.getName());
            return false;
        }

        if (FillGeneratStatusEnum.FILL_FAIL_LIMIT.getType().equals(fillGeneratStatus)) {
            pageResult.setSuccess(false);
            pageResult.setMsg(String.format(FillGeneratStatusEnum.FILL_FAIL_LIMIT.getName(),environmentContext.getFillDataCreateLimitSize()));
            return false;
        }

        return true;
    }

    private List<JobReturnPageVO> getJobReturnPageVO(List<ScheduleJob> scheduleJobs,
                                                     Map<Long, List<Long>> flowMap,
                                                     Map<String, List<String>> flowJobMap,
                                                     Callback<JobParam, Optional<JobReturnPageVO>> callback) {
        Map<Integer, List<Long>> taskIdMap = scheduleJobs.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType, Collectors.mapping(ScheduleJob::getTaskId, Collectors.toList())));
        Map<String, List<Long>> tagMaps = Maps.newHashMap();
        Map<String, ScheduleTaskShade> taskMaps = Maps.newHashMap();
        Map<String, List<ScheduleTaskPriority>> priorityMap = Maps.newHashMap();
        for (Integer appTypeGroup : taskIdMap.keySet()) {
            List<Long> taskIdList = taskIdMap.get(appTypeGroup);
            List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listByTaskIds(taskIdList, null, appTypeGroup);

            for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShades) {
                taskMaps.put(scheduleTaskShade.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + scheduleTaskShade.getAppType(), scheduleTaskShade);
            }

            List<ScheduleTaskTag> scheduleTaskTags = scheduleTaskTagService.findTagByTaskIds(Sets.newHashSet(taskIdList), appTypeGroup);

            if (CollectionUtils.isNotEmpty(scheduleTaskTags)) {
                tagMaps.putAll(scheduleTaskTags.stream().collect(Collectors.groupingBy(scheduleTaskTag -> (scheduleTaskTag.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + scheduleTaskTag.getAppType()),
                        Collectors.mapping(ScheduleTaskTag::getTagId, Collectors.toList()))));
            }

            List<ScheduleTaskPriority> scheduleTaskPriorityList = scheduleTaskPriorityService.selectPriorityByTaskIds(taskIdList, appTypeGroup);

            if (CollectionUtils.isNotEmpty(scheduleTaskPriorityList)) {
                priorityMap.putAll(scheduleTaskPriorityList.stream().collect(
                        Collectors.groupingBy(scheduleTaskPriority ->
                                scheduleTaskPriority.getTaskId() + GlobalConst.PARTITION + scheduleTaskPriority.getAppType())));
            }
        }

        List<Long> resourceIds = scheduleJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
        Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(resourceIds);
        Set<Long> ownerIds = taskMaps.values().stream().map(ScheduleTaskShade::getOwnerUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userService.findUserWithFill(ownerIds).stream().collect(Collectors.toMap(User::getDtuicUserId, g -> (g)));

        List<JobReturnPageVO> jobReturnPageVOS = Lists.newArrayList();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            JobParam jobParam = new JobParam(groupInfo, taskMaps, userMap, flowMap, tagMaps,flowJobMap,priorityMap, scheduleJob);
            Optional<JobReturnPageVO> jobReturnPageVO = callback.callback(jobParam);
            jobReturnPageVO.ifPresent(jobReturnPageVOS::add);
        }
        return jobReturnPageVOS;
    }

    private JobReturnPageVO buildJobReturnPageVO(Map<Long, ResourceGroupDetail> groupInfo,
                                                 Map<String, ScheduleTaskShade> taskMaps,
                                                 Map<Long, User> userMap,
                                                 Map<Long, List<Long>> flowMap,
                                                 Map<String, List<Long>> tagMaps,
                                                 Map<String, List<String>> flowJobMap,
                                                 Map<String, List<ScheduleTaskPriority>> priorityMap,
                                                 ScheduleJob scheduleJob) {
        if (EScheduleJobType.WORK_FLOW.getType().equals(scheduleJob.getTaskType())
                || EScheduleJobType.ALGORITHM_LAB.getVal().equals(scheduleJob.getTaskType())) {
            JobReturnPageVO jobReturnPageVO = getJobReturnPageVO(groupInfo, taskMaps, userMap, tagMaps,priorityMap, scheduleJob);
            // 工作流任务
            List<Long> taskIds = flowMap.get(scheduleJob.getTaskId());
            List<String> jobIds = flowJobMap.get(scheduleJob.getJobId());

            if (CollectionUtils.isNotEmpty(taskIds) || CollectionUtils.isNotEmpty(jobIds)) {
                List<ScheduleJob> scheduleJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(scheduleJob.getJobId()));
                List<JobReturnPageVO> subFlowList = Lists.newArrayList();
                for (ScheduleJob job : scheduleJobs) {
                    // 分三种情况
                    // 第一种情况：jobIds和 taskId都不是空的时候 取交集
                    if (CollectionUtils.isNotEmpty(jobIds) && CollectionUtils.isNotEmpty(taskIds)) {
                        if (jobIds.contains(job.getJobId()) && taskIds.contains(job.getTaskId())) {
                            subFlowList.add(getJobReturnPageVO(groupInfo, taskMaps, userMap,tagMaps,priorityMap, job));
                            continue;
                        }
                    }

                    // 第二种情况： jobIds不为空， taskId 是空
                    if (CollectionUtils.isNotEmpty(jobIds) && CollectionUtils.isEmpty(taskIds)) {
                        if (jobIds.contains(job.getJobId())) {
                            subFlowList.add(getJobReturnPageVO(groupInfo, taskMaps, userMap,tagMaps,priorityMap, job));
                            continue;
                        }
                    }

                    // 第三种情况： jobIds是空， taskId 不是空
                    if (CollectionUtils.isEmpty(jobIds) && CollectionUtils.isNotEmpty(taskIds)) {
                        if (taskIds.contains(job.getTaskId())) {
                            subFlowList.add(getJobReturnPageVO(groupInfo, taskMaps, userMap,tagMaps,priorityMap, job));
                            continue;
                        }
                    }
                }
                jobReturnPageVO.setSubFlowList(subFlowList);
            }
            return jobReturnPageVO;
        } else {
            return getJobReturnPageVO(groupInfo, taskMaps, userMap, tagMaps,priorityMap, scheduleJob);
        }
    }

    @NotNull
    private JobReturnPageVO getJobReturnPageVO(Map<Long, ResourceGroupDetail> groupInfo,
                                               Map<String, ScheduleTaskShade> taskMaps,
                                               Map<Long, User> userMap,
                                               Map<String, List<Long>> tagMaps,
                                               Map<String, List<ScheduleTaskPriority>> priorityMap,
                                               ScheduleJob scheduleJob) {
        JobReturnPageVO jobReturnPageVO = new JobReturnPageVO();
        jobReturnPageVO.setId(scheduleJob.getId());
        jobReturnPageVO.setTaskId(scheduleJob.getTaskId());
        jobReturnPageVO.setAppType(scheduleJob.getAppType());
        jobReturnPageVO.setJobName(scheduleJob.getJobName());
        jobReturnPageVO.setType(scheduleJob.getType());

        ScheduleTaskShade scheduleTaskShade = taskMaps.get(scheduleJob.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + scheduleJob.getAppType());
        setTaskInfo(userMap, jobReturnPageVO, scheduleTaskShade);

        jobReturnPageVO.setJobId(scheduleJob.getJobId());
        jobReturnPageVO.setJobKey(scheduleJob.getJobKey());
        jobReturnPageVO.setStatus(RdosTaskStatus.getShowStatusWithoutStop(scheduleJob.getStatus()));
        jobReturnPageVO.setTaskType(scheduleJob.getTaskType());
        jobReturnPageVO.setPeriodType(scheduleJob.getPeriodType());
        jobReturnPageVO.setTagIds(tagMaps.get(scheduleJob.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + scheduleJob.getAppType()));
        jobReturnPageVO.setResourceId(scheduleJob.getResourceId());
        jobReturnPageVO.setResourceName(groupInfo.get(scheduleJob.getResourceId()) != null ?
                groupInfo.get(scheduleJob.getResourceId()).getResourceName() : "");

        DateUtil.addTimeSplit(scheduleJob.getCycTime());
        jobReturnPageVO.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
        jobReturnPageVO.setBusinessDate(generateBizDateFromCycTime(scheduleJob.getCycTime()));
        jobReturnPageVO.setExecStartTime(getFormatExecTime(scheduleJob.getExecStartTime()));
        jobReturnPageVO.setExecEndTime(getFormatExecTime(scheduleJob.getExecEndTime()));
        jobReturnPageVO.setExecTime(DateUtil.getExecTime(scheduleJob.getExecStartTime(), scheduleJob.getExecEndTime(), scheduleJob.getExecTime(), jobReturnPageVO.getStatus()));
        jobReturnPageVO.setRetryNum(scheduleJob.getRetryNum());

        List<ScheduleTaskPriority> scheduleTaskPriorityList = priorityMap.get(scheduleJob.getTaskId() + GlobalConst.PARTITION + scheduleJob.getAppType());

        Integer maxPriority = 1;
        if (CollectionUtils.isNotEmpty(scheduleTaskPriorityList)) {
            maxPriority = scheduleTaskPriorityList.stream().map(ScheduleTaskPriority::getPriority).max(Comparator.comparingInt(priority -> priority)).orElse(1);
        } else {
            maxPriority = scheduleTaskPriorityService.selectPriorityByTaskId(scheduleJob.getTaskId(),scheduleJob.getAppType());
        }
        jobReturnPageVO.setPriority(maxPriority);
        return jobReturnPageVO;
    }

    private void setTaskInfo(Map<Long, User> userMap, JobReturnPageVO jobReturnPageVO, ScheduleTaskShade scheduleTaskShade) {
        if (scheduleTaskShade == null) {
            scheduleTaskShade = scheduleTaskShadeDao.getOne(jobReturnPageVO.getTaskId(),jobReturnPageVO.getAppType());
        }

        if (scheduleTaskShade == null) {
            return;
        }

        jobReturnPageVO.setTaskName(scheduleTaskShade.getName());
        jobReturnPageVO.setOwnerId(scheduleTaskShade.getOwnerUserId());
        jobReturnPageVO.setIsDeleted(scheduleTaskShade.getIsDeleted());
        jobReturnPageVO.setScheduleStatus(scheduleTaskShade.getScheduleStatus());
        jobReturnPageVO.setBusinessType(scheduleTaskShade.getBusinessType());
        jobReturnPageVO.setCreateUserId(scheduleTaskShade.getCreateUserId());
        User user = userMap.get(scheduleTaskShade.getOwnerUserId());
        if (user != null) {
            jobReturnPageVO.setOwnerName(user.getUserName());
        }
    }

    public String generateBizDateFromCycTime(String cycTime) {
        DateTime cycDateTime = new DateTime(DateUtil.getTimestamp(cycTime, DateUtil.UN_STANDARD_DATETIME_FORMAT));
        DateTime bizDate = cycDateTime.minusDays(1);
        return bizDate.toString( DateUtil.STANDARD_DATETIME_FORMAT);
    }

    @Nullable
    private JobPageDTO getJobPageDTO(JobPageVO vo, boolean queryWorkflowDetail) {
        JobPageDTO dto = operationJobStruct.jobPageVOToJobPageDTO(vo);
        List<Long> calenderIds = vo.getCalenderIds();
        Map<Long, List<Long>> flowMap = Maps.newHashMap();
        Set<Long> selectTaskIds = Sets.newHashSet();
        Set<Long> allTaskIds = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(vo.getRangeTaskIds())) {
            allTaskIds.addAll(vo.getRangeTaskIds());
        }

        Map<String, List<String>> flowJobMap = Maps.newHashMap();
        Set<String> selectJobIds = Sets.newHashSet();
        JobTimeVO jobTime = vo.getJobTime();
        if (jobTime != null) {
            JobTimeDTO jobTimeDTO = null;
            String cycTimeStartTime = jobTime.getCycTimeStartTime();
            String cycTimeEndTime = jobTime.getCycTimeEndTime();

            if (StringUtils.isNotBlank(cycTimeStartTime) && StringUtils.isNotBlank(cycTimeEndTime)) {
                jobTimeDTO = getJobTimeDTO(null);
                jobTimeDTO.setCycTimeStartTime(getReplace(cycTimeStartTime));
                jobTimeDTO.setCycTimeEndTime(getReplace(cycTimeEndTime));
            }

            String businessStartTime = jobTime.getBusinessStartTime();
            String businessEndTime = jobTime.getBusinessEndTime();

            if (StringUtils.isNotBlank(businessStartTime) && StringUtils.isNotBlank(businessEndTime)) {
                jobTimeDTO = getJobTimeDTO(jobTimeDTO);
                DateTime bizStartTime = new DateTime(DateUtil.parseDate(businessStartTime, DateUtil.STANDARD_DATETIME_FORMAT));
                DateTime bizEndTime = new DateTime(DateUtil.parseDate(businessEndTime, DateUtil.STANDARD_DATETIME_FORMAT));
                restrictTooManyDays(bizStartTime, bizEndTime, dto.getFillId(), queryWorkflowDetail);
                jobTimeDTO.setCycTimeBussStartTime(bizStartTime.plusDays(1).toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));
                jobTimeDTO.setCycTimeBussEndTime(bizEndTime.plusDays(1).toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));
            }
            checkJobQueryRangeNotEmpty(businessStartTime, businessEndTime, dto.getFillId(), queryWorkflowDetail);
            dto.setJobTimeDTO(jobTimeDTO);
        } else {
            checkJobQueryRangeNotEmpty(null, null, dto.getFillId(), queryWorkflowDetail);
        }

        Integer type = vo.getType();
        if (CollectionUtils.isEmpty(dto.getTypes())) {
            List<Integer> types = Lists.newArrayList();
            if (type == null) {
                types.add(EScheduleType.NORMAL_SCHEDULE.getType());
            } else {
                types.add(type);
            }
            dto.setTypes(types);
        } else {
            List<Integer> types = dto.getTypes();

            if (!types.contains(type)) {
                types.add(type);
            }
            dto.setTypes(types);
        }

        List<Integer> jobStatuses = dto.getJobStatuses();
        if (CollectionUtils.isNotEmpty(jobStatuses)) {
            List<Integer> newJobStatuses = Lists.newArrayList();
            Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getCollectionStatus();
            for (Integer jobStatus : jobStatuses) {
                List<Integer> statusList = statusMap.get(jobStatus);
                if (CollectionUtils.isNotEmpty(statusList)) {
                    newJobStatuses.addAll(statusList);
                } else {
                    //不在失败状态拆分里
                    newJobStatuses.add(jobStatus);
                }
            }
            dto.setJobStatuses(newJobStatuses);
            List<ScheduleJob> scheduleJobs = scheduleJobDao.listFlowSubTaskIdByStatus(newJobStatuses,
                    dto.getJobTimeDTO(), dto.getAppType(), dto.getTenantId(), dto.getProjectId(), dto.getTypes(), dto.getFillId());

            if (CollectionUtils.isEmpty(scheduleJobs)) {
                return null;
            }

            for (ScheduleJob scheduleJob : scheduleJobs) {
                if (StringUtils.isBlank(scheduleJob.getFlowJobId()) || "0".equals(scheduleJob.getFlowJobId())) {
                    selectJobIds.add(scheduleJob.getJobId());
                } else {
                    selectJobIds.add(scheduleJob.getFlowJobId());
                    List<String> jobIds = flowJobMap.computeIfAbsent(scheduleJob.getFlowJobId(), key -> Lists.newArrayList());
                    jobIds.add(scheduleJob.getJobId());
                    flowJobMap.put(scheduleJob.getFlowJobId(),jobIds);
                }
            }
        }
        dto.setJobIds(selectJobIds);
        dto.setFlowJobMap(flowJobMap);

        if (CollectionUtils.isNotEmpty(calenderIds)) {
            List<Long> taskIds = calenderService.getAllTaskByCalenderId(calenderIds, vo.getAppType());
            if (setAllTaskIds(allTaskIds, taskIds)) {
                return null;
            }
        }

        TaskSearchNameVO searchName = vo.getSearchName();
        Long ownerId = vo.getOwnerId();
        List<Long> taskIdsBySearchName = null;
        String businessType = vo.getBusinessType();
        boolean searchNameCondition = searchName != null && searchName.getSearchType() != null && StringUtils.isNotEmpty(searchName.getTaskName());
        if (searchNameCondition || ownerId != null || CollectionUtils.isNotEmpty(vo.getTaskTypes()) || StringUtils.isNotBlank(businessType)) {
            String taskName = searchName == null ? null : searchName.getTaskName();
            Integer searchType = searchName == null ? null : searchName.getSearchType();
            taskIdsBySearchName = scheduleTaskShadeDao.searchTaskIdBySearchName(CheckUtils.handName(searchType, taskName), searchType,
                    dto.getAppType(), null, null, vo.getOwnerId(),businessType, vo.getTaskTypes(),null, null);

            if (setAllTaskIds(allTaskIds, taskIdsBySearchName)) {
                return null;
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

        List<Long> tagIds = vo.getTagIds();
        if (CollectionUtils.isNotEmpty(tagIds)) {
            List<ScheduleTaskTag> tagsByTagIds = scheduleTaskTagService.findTagsByTagIds(tagIds,vo.getAppType());

            if (CollectionUtils.isEmpty(tagsByTagIds)) {
                return null;
            }

            Map<Long, List<ScheduleTaskTag>> tagMap = tagsByTagIds.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId));

            boolean flg = Boolean.TRUE;
            List<Long> tagTaskIds = Lists.newArrayList();
            for (Long task : tagMap.keySet()) {
                List<ScheduleTaskTag> scheduleTaskTags = tagMap.get(task);
                List<Long> taskTagIds = scheduleTaskTags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());

                // tagIds是否是taskTagIds的子集
                if (ListUtil.isSubList(taskTagIds,tagIds)) {
                    tagTaskIds.add(task);
                    flg= Boolean.FALSE;
                }
            }

            if (flg || setAllTaskIds(allTaskIds,tagTaskIds)) {
                return null;
            }

        }


        if (CollectionUtils.isNotEmpty(allTaskIds)) {
            List<ScheduleTaskShade> scheduleTaskShadeList = scheduleTaskShadeDao.
                    listSimpleFiledByTaskIds(allTaskIds, null, dto.getAppType());

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

        dto.setTaskIds(selectTaskIds);
        dto.setFlowMap(flowMap);

        SortVO sortVO = vo.getSortVO();
        if (sortVO != null && sortVO.getSortFiled() != null && sortVO.getSortType() != null) {
            SortDTO sortDTO = new SortDTO();
            sortDTO.setSortFiled(sortVO.getSortFiled());
            sortDTO.setSortType(sortVO.getSortType());
            dto.setSort(sortDTO);
        }

        dto.setFillTypes(Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(),FillJobTypeEnum.RUN_JOB.getType()));
        return dto;
    }

    private static boolean setAllTaskIds(Set<Long> allTaskIds, List<Long> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return true;
        }

        if (CollectionUtils.isEmpty(allTaskIds)) {
            allTaskIds.addAll(taskIds);
        } else {
            allTaskIds.retainAll(taskIds);

            if (CollectionUtils.isEmpty(allTaskIds)) {
                return true;
            }
        }
        return false;
    }

    private JobTimeDTO getJobTimeDTO(JobTimeDTO jobTimeDTO) {
        if (jobTimeDTO == null) {
            jobTimeDTO = new JobTimeDTO();
        }
        return jobTimeDTO;
    }

    @NotNull
    private String getReplace(String cycTimeStartTime) {
        return cycTimeStartTime.replace(" ", "").replace("-", "").replace(":", "");
    }

    private String getFormatExecTime(Timestamp execStartTime) {
        if (execStartTime != null) {
            DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            return timeFormatter.print(execStartTime.getTime());
        }
        return null;
    }

    public List<StatisticsJobVO> queryJobsStatusStatistics(JobPageVO vo) {
        if (!checkFillCreateSuccess(vo, PageResult.emptyPageResult())) {
            return initStatisticsJobVO();
        }
        JobPageDTO dto = getJobPageDTO(vo, false);

        if (dto == null) {
            return initStatisticsJobVO();
        }

        restrictTooManyJobIds(dto, false);
        List<StatisticsJobVO> statisticsJobVOS = Lists.newArrayList();
        List<StatusCount> statisticsCounts =  scheduleJobDao.statusStatisticByJobPageVO(dto);
        Map<Integer, List<Integer>> statusMap = new HashMap<>(RdosTaskStatus.getStatusFailedDetail());
        //实例统计 取消不拆分  任务筛选需要拆分手动取消和自动取消
        statusMap.put(RdosTaskStatus.CANCELED.getStatus(), Stream.of(RdosTaskStatus.getStatusFailedDetail().get(RdosTaskStatus.EXPIRE.getStatus()),
                RdosTaskStatus.getStatusFailedDetail().get(RdosTaskStatus.CANCELED.getStatus())).flatMap(Collection::stream).collect(Collectors.toList()));
        statusMap.remove(RdosTaskStatus.EXPIRE.getStatus());

        int totalNum = 0;
        for (Integer status : statusMap.keySet()) {
            StatisticsJobVO statisticsJobVO = new StatisticsJobVO();
            String statusName = RdosTaskStatus.getCode(status);
            List<Integer> statuses = statusMap.get(status);
            int num = 0;
            for (StatusCount statusCount : statisticsCounts) {
                if (statuses.contains(statusCount.getStatus())) {
                    num += statusCount.getCount();
                }
            }

            statisticsJobVO.setStatusEnum(statusName);
            statisticsJobVO.setStatusCount(num);
            statisticsJobVOS.add(statisticsJobVO);
            totalNum += num;
        }

        StatisticsJobVO statisticsJobVO = new StatisticsJobVO();
        statisticsJobVO.setStatusEnum("ALL");
        statisticsJobVO.setStatusCount(totalNum);
        statisticsJobVOS.add(statisticsJobVO);
        return statisticsJobVOS;
    }

    private List<StatisticsJobVO> initStatisticsJobVO() {
        List<StatisticsJobVO> statisticsJobVOS = Lists.newArrayList();
        Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getStatusFailedDetail();
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            String statusName = RdosTaskStatus.getCode(entry.getKey());
            StatisticsJobVO statisticsJobVO = new StatisticsJobVO();
            statisticsJobVO.setStatusEnum(statusName);
            statisticsJobVO.setStatusCount(0);
            statisticsJobVOS.add(statisticsJobVO);
        }

        return statisticsJobVOS;
    }

    public List<SchedulePeriodVO> displayPeriods(String jobId, Integer directType, Integer level) {
        ScheduleJob job = scheduleJobDao.getByJobId(jobId,IsDeletedEnum.NOT_DELETE.getType());
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        if (level <= 0 || level > environmentContext.getJobJobLevel()) {
            level = environmentContext.getJobJobLevel();
        }

        List<ScheduleJob> scheduleJobs;
        if (DisplayDirect.FATHER.getType().equals(directType)) {
            scheduleJobs =  scheduleJobDao.getJobRangeByCycTimeByLimit(job.getTaskId(),
                    Boolean.TRUE, job.getCycTime(),job.getAppType(),job.getType(),level,null);
        } else if (DisplayDirect.CHILD.getType().equals(directType)) {
            scheduleJobs =  scheduleJobDao.getJobRangeByCycTimeByLimit(job.getTaskId(),
                    Boolean.FALSE, job.getCycTime(),job.getAppType(),job.getType(),level,null);
        } else {
            throw new RdosDefineException("directType error");
        }

        List<SchedulePeriodVO> vos = new ArrayList<>(scheduleJobs.size());
        scheduleJobs.forEach(e -> {
            SchedulePeriodVO vo = new SchedulePeriodVO();
            vo.setJobId(e.getJobId());
            vo.setCycTime(DateUtil.addTimeSplit(e.getCycTime()));
            vo.setStatus(e.getStatus());
            vo.setTaskId(e.getTaskId());
            vo.setVersion(e.getVersionId());
            vos.add(vo);
        });
        return vos;
    }

    public List<JobReturnPageVO> workflowPage(JobPageVO vo) {
        if (null == vo || null == vo.getJobId()) {
            throw new RdosDefineException("jobId can not be null");
        }
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(vo.getJobId(), Deleted.NORMAL.getStatus());

        if (scheduleJob == null || !EScheduleJobType.WORK_FLOW.getType().equals(scheduleJob.getTaskType())) {
            throw new RdosDefineException("jobId:" + vo.getJobId() + " must workflow task");
        }
        boolean queryWorkflowDetail = true;
        JobPageDTO jobPageDTO = getJobPageDTO(vo, queryWorkflowDetail);
        if(null == jobPageDTO){
            return new ArrayList<>(0);
        }
        restrictTooManyJobIds(jobPageDTO, queryWorkflowDetail);
        jobPageDTO.setType(scheduleJob.getType());
        jobPageDTO.setAppType(scheduleJob.getAppType());
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getSubJobsByFlowIdsAndQuery(Lists.newArrayList(vo.getJobId()), jobPageDTO);
        List<JobReturnPageVO> jobReturnPageVOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            jobReturnPageVOS = getJobReturnPageVO(scheduleJobs,jobPageDTO.getFlowMap(),jobPageDTO.getFlowJobMap(),param -> Optional.of(
                    getJobReturnPageVO(param.groupInfo, param.taskMaps, param.userMap,param.tagMaps,param.priorityMap, param.scheduleJob)));
        }
        return jobReturnPageVOS;
    }

    static class JobParam {
        private Map<Long, ResourceGroupDetail> groupInfo;

        private Map<String, ScheduleTaskShade> taskMaps;

        private Map<Long, User> userMap;

        private Map<Long, List<Long>> flowMap;

        private Map<String, List<Long>> tagMaps;

        private Map<String, List<ScheduleTaskPriority>> priorityMap;

        private Map<String, List<String>> flowJobMap;

        private ScheduleJob scheduleJob;

        public JobParam(Map<Long, ResourceGroupDetail> groupInfo,
                        Map<String, ScheduleTaskShade> taskMaps,
                        Map<Long, User> userMap,
                        Map<Long, List<Long>> flowMap ,
                        Map<String, List<Long>> tagMaps ,
                        Map<String, List<String>> flowJobMap,
                        Map<String, List<ScheduleTaskPriority>> priorityMap,
                        ScheduleJob scheduleJob) {
            this.groupInfo = groupInfo;
            this.taskMaps = taskMaps;
            this.userMap = userMap;
            this.flowMap = flowMap;
            this.tagMaps = tagMaps;
            this.priorityMap = priorityMap;
            this.flowJobMap = flowJobMap;
            this.scheduleJob = scheduleJob;
        }


        public Map<Long, ResourceGroupDetail> getGroupInfo() {
            return groupInfo;
        }

        public void setGroupInfo(Map<Long, ResourceGroupDetail> groupInfo) {
            this.groupInfo = groupInfo;
        }

        public Map<String, ScheduleTaskShade> getTaskMaps() {
            return taskMaps;
        }

        public void setTaskMaps(Map<String, ScheduleTaskShade> taskMaps) {
            this.taskMaps = taskMaps;
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

        public Map<String, List<Long>> getTagMaps() {
            return tagMaps;
        }

        public void setTagMaps(Map<String, List<Long>> tagMaps) {
            this.tagMaps = tagMaps;
        }

        public Map<String, List<ScheduleTaskPriority>> getPriorityMap() {
            return priorityMap;
        }

        public void setPriorityMap(Map<String, List<ScheduleTaskPriority>> priorityMap) {
            this.priorityMap = priorityMap;
        }

        public Map<String, List<String>> getFlowJobMap() {
            return flowJobMap;
        }

        public void setFlowJobMap(Map<String, List<String>> flowJobMap) {
            this.flowJobMap = flowJobMap;
        }

        public ScheduleJob getScheduleJob() {
            return scheduleJob;
        }

        public void setScheduleJob(ScheduleJob scheduleJob) {
            this.scheduleJob = scheduleJob;
        }
    }

    private void checkJobQueryRangeNotEmpty(String businessStartTime, String businessEndTime, Long fillId, boolean queryWorkflowDetail) {
        if (fillId != null) {
            return;
        }
        if (queryWorkflowDetail) {
            return;
        }
        if (StringUtils.isBlank(businessStartTime) || StringUtils.isBlank(businessEndTime)) {
            throw new RdosDefineException(ErrorCode.JOB_QUERY_DAYS_EMPTY.getZhMsg());
        }
    }

    private void restrictTooManyDays(DateTime bizStartTime, DateTime bizEndTime, Long fillId, boolean queryWorkflowDetail) {
        if (fillId != null) {
            return;
        }
        if (queryWorkflowDetail) {
            return;
        }
        int daysBetween = Days.daysBetween(bizStartTime, bizEndTime).getDays();
        int baseDays = Optional.ofNullable(jobQueryRestrictDays).orElse(GlobalConst.HALF_YEAR_IN_DAYS);
        if (daysBetween > baseDays) {
            int baseMonth = baseDays / 30;
            throw new RdosDefineException(String.format(ErrorCode.JOB_QUERY_DAYS_TOO_LONG.getZhMsg(), baseMonth, baseMonth));
        }
    }

    private void restrictTooManyJobIds(JobPageDTO dto, boolean queryWorkflowDetail) {
        if (dto.getFillId() != null) {
            return;
        }
        if (dto == null || CollectionUtils.isEmpty(dto.getJobIds())) {
            return;
        }
        if (queryWorkflowDetail) {
            return;
        }
        int baseJobNum = Optional.ofNullable(jobQueryRestrictNum).orElse(GlobalConst.RESTRICT_JOB_QUERY_NUM);
        if (dto.getJobIds().size() > baseJobNum) {
            throw new RdosDefineException(ErrorCode.JOB_QUERY_JOB_ID_TOO_MANY.getZhMsg());
        }
    }
}
