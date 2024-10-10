package com.dtstack.engine.master.impl;


import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.BaselineTaskBatchDTO;
import com.dtstack.engine.api.enums.BaselineStatusEnum;
import com.dtstack.engine.api.enums.BaselineTaskTypeEnum;
import com.dtstack.engine.api.enums.BaselineTypeEnum;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.BaselineBatchVO;
import com.dtstack.engine.api.vo.alert.*;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.BaselineBlockJobRecordDao;
import com.dtstack.engine.dao.BaselineJobDao;
import com.dtstack.engine.dao.BaselineJobJobDao;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.listener.JobGraphFinishBaselineListener;
import com.dtstack.engine.master.mapstruct.BaselineJobStruct;
import com.dtstack.engine.po.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:52 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class BaselineJobService {

    private final Logger LOGGER = LoggerFactory.getLogger(BaselineJobService.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private BaselineJobDao baselineJobDao;

    @Autowired
    private BaselineJobJobDao baselineJobJobDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private BaselineJobStruct baselineJobStruct;

    @Autowired
    private BaselineTaskService baselineTaskService;

    @Autowired
    private BaselineBlockJobRecordDao baselineBlockJobRecordDao;

    @Autowired
    private BaselineTaskBatchService baselineTaskBatchService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private JobGraphFinishBaselineListener jobGraphFinishBaselineListener;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");


    @Transactional(rollbackFor = Exception.class)
    public Integer savaBaselineTask(BaselineJob baselineJob, List<BaselineJobJob> baselineJobJobs,BaselineTaskDTO baselineTaskDTO) {
        Integer count = baselineJobDao.insert(baselineJob);
        Integer priority = baselineTaskDTO.getPriority();

        // 批量插入jobjob
        if (CollectionUtils.isNotEmpty(baselineJobJobs)) {
            baselineJobJobs.forEach(baselineJobJob -> {
                baselineJobJob.setBaselineJobId(baselineJob.getId());
                baselineJobJob.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            });
            List<List<BaselineJobJob>> jobJobs = Lists.partition(baselineJobJobs, environmentContext.getBatchJobInsertSize());
            for (List<BaselineJobJob> jobJob : jobJobs) {
                baselineJobJobDao.batchInsert(jobJob);

                List<ScheduleTaskPriority> scheduleTaskPriorityList = Lists.newArrayList();
                for (BaselineJobJob baselineJobJob : jobJob) {
                    ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(baselineJobJob.getTaskId(), baselineJobJob.getTaskAppType());

                    if (scheduleTaskShade == null) {
                        LOGGER.error("task not fount taskId:{}  appType:{}",baselineJobJob.getTaskId(),baselineJobJob.getTaskAppType());
                        continue;
                    }

                    ScheduleTaskPriority scheduleTaskPriority = new ScheduleTaskPriority();

                    scheduleTaskPriority.setAppType(baselineJobJob.getTaskAppType());
                    scheduleTaskPriority.setTaskId(baselineJobJob.getTaskId());
                    scheduleTaskPriority.setBaselineTaskId(baselineTaskDTO.getId());
                    scheduleTaskPriority.setPriority(priority);
                    scheduleTaskPriority.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
                    scheduleTaskPriorityList.add(scheduleTaskPriority);

                    if (EScheduleJobType.WORK_FLOW.getType().equals(scheduleTaskShade.getTaskType())) {
                        List<ScheduleTaskShade> subTasks = scheduleTaskShadeService.getFlowWorkSubTasks(baselineJobJob.getTaskId(), baselineJobJob.getTaskAppType(), null, null);

                        for (ScheduleTaskShade subTask : subTasks) {
                            ScheduleTaskPriority subTaskPriority = new ScheduleTaskPriority();

                            subTaskPriority.setAppType(subTask.getAppType());
                            subTaskPriority.setTaskId(subTask.getTaskId());
                            subTaskPriority.setBaselineTaskId(baselineTaskDTO.getId());
                            subTaskPriority.setPriority(priority);
                            subTaskPriority.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
                            scheduleTaskPriorityList.add(subTaskPriority);
                        }
                    }
                }

                scheduleTaskPriorityService.insertBatch(scheduleTaskPriorityList);
            }
        }
        return count;

    }


    public List<BaselineJob> scanningBaselineJobById(Long startId,
                                                     Timestamp businessDate,
                                                     List<Integer> statusEnums,
                                                     Integer finishStatus,
                                                     Integer limit) {
        return baselineJobDao.scanningBaselineJobById(startId,businessDate,statusEnums,finishStatus,limit);
    }

    public Integer updateFinishStatus(Long id, Integer finishStatus,Timestamp finishTime,Integer baselineStatus) {
        return baselineJobDao.updateFinishStatus(id,finishStatus,finishTime,baselineStatus);
    }

    public PageResult<List<BaselineJobPageVO>> page(BaselineJobConditionVO vo) {
        BaselineJobConditionModel baselineJobConditionModel = baselineJobStruct.toBaselineJobConditionModel(vo);
        if (vo.getStartBusinessDate() != null) {
            baselineJobConditionModel.setStartBusinessTime(new Timestamp(vo.getStartBusinessDate() * 1000));
        }

        if (vo.getEndBusinessDate() != null) {
            baselineJobConditionModel.setEndBusinessTime(new Timestamp(vo.getEndBusinessDate() * 1000));
        }

        Long count = baselineJobDao.countByModel(baselineJobConditionModel);

        List<BaselineJobPageVO> baselineJobPageVOS = Lists.newArrayList();
        if (count > 0) {
            List<BaselineJob> baselineJobs = baselineJobDao.selectByModel(baselineJobConditionModel);

            List<Long> baselineTaskIds = baselineJobs.stream().map(BaselineJob::getBaselineTaskId).collect(Collectors.toList());
            List<BaselineTaskDTO> baselineTaskDTOS = baselineTaskService.getBaselineTaskByIds(baselineTaskIds);
            List<BaselineTaskBatchDTO> baseBatchLists = baselineTaskBatchService.getBaselineBatchByBaselineTaskIds(baselineTaskIds);
            Map<Long, List<BaselineTaskBatchDTO>> baseBatchMaps = baseBatchLists.stream().collect(Collectors.groupingBy(BaselineTaskBatchDTO::getBaselineTaskId));


            Map<Long, BaselineTaskDTO> taskDTOMap = baselineTaskDTOS.stream().collect(Collectors.toMap(BaselineTaskDTO::getId, g -> (g)));
            for (BaselineJob baselineJob : baselineJobs) {
                BaselineJobPageVO pageVO = new BaselineJobPageVO();

                String businessTime = DateUtil.getFormattedDate(baselineJob.getBusinessDate().getTime(), DateUtil.DATE_FORMAT);
                pageVO.setId(baselineJob.getId());
                pageVO.setBusinessTime(businessTime);
                pageVO.setName(baselineJob.getName());
                pageVO.setOwnerUserId(baselineJob.getOwnerUserId());
                pageVO.setBaselineStatus(baselineJob.getBaselineStatus());
                pageVO.setFinishStatus(baselineJob.getFinishStatus());
                pageVO.setBaselineTaskId(baselineJob.getBaselineTaskId());
                pageVO.setBatchType(baselineJob.getBatchType());
                String cycTime = DateUtil.addTimeSplit(baselineJob.getCycTime());
                pageVO.setCycTime(cycTime);
                BaselineTaskDTO dto = taskDTOMap.get(baselineJob.getBaselineTaskId());
                if (dto != null) {
                    pageVO.setEarlyWarnMargin(dto.getEarlyWarnMargin());

                    List<BaselineTaskBatchDTO> baselineTaskBatchDTOS = baseBatchMaps.get(dto.getId());
                    if (BaselineTypeEnum.SINGLE_BATCH.getCode().equals(dto.getBatchType())) {
                        // 单批次只有一个批次
                        Optional<BaselineTaskBatchDTO> optional = baselineTaskBatchDTOS.stream().findFirst();
                        if (optional.isPresent()) {
                            String replyTime = businessTime + " " + optional.get().getReplyTime();
                            pageVO.setReplyTime(replyTime);
                            pageVO.setCurrentMargin(getCurrentMargin(replyTime,baselineJob.getFinishTime()));
                        }
                    } else if (BaselineTypeEnum.MANY_BATCH.getCode().equals(dto.getBatchType())) {
                        Map<String, List<BaselineTaskBatchDTO>> baseCycTimeMap = baselineTaskBatchDTOS.stream().collect(Collectors.groupingBy(BaselineTaskBatchDTO::getCycTime));
                        String time = null;
                        try {
                            time = DateUtil.getDateStrTOFormat(cycTime, DateUtil.STANDARD_DATETIME_FORMAT, DateUtil.STANDARD_FORMAT);
                        } catch (ParseException e) {
                            LOGGER.error("",e);
                        }
                        if (CollectionUtils.isNotEmpty(baseCycTimeMap.get(time))) {
                            Optional<BaselineTaskBatchDTO> optional = baseCycTimeMap.get(time).stream().findFirst();
                            if (optional.isPresent()) {
                                String replyTime = businessTime + " " + optional.get().getReplyTime();
                                pageVO.setReplyTime(replyTime);
                                pageVO.setCurrentMargin(getCurrentMargin(replyTime,baselineJob.getFinishTime()));
                            }
                        }
                    } else {
                        LOGGER.error("batch not support type :{}",dto.getBatchType());
                    }
                }

                if (baselineJob.getFinishTime() == null) {
                    setFinishTime(baselineJob, pageVO);
                } else {
                    Timestamp finishTime = baselineJob.getFinishTime();
                    String replyTime = pageVO.getReplyTime();
                    if (StringUtils.isNotBlank(replyTime)) {
                        Date date = DateUtil.parseDate(replyTime, "yyyy-MM-dd HH:mm");
                        if (date != null) {
                            long delayTime = finishTime.getTime() - date.getTime();
                            pageVO.setDelayTime((int)(delayTime/ (1000 * 60)));
                        }
                    }
                    pageVO.setFinishTime(DateUtil.getDate(baselineJob.getFinishTime(),DateUtil.STANDARD_DATETIME_FORMAT));
                }

                baselineJobPageVOS.add(pageVO);
            }
        }
        return new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), count.intValue(), baselineJobPageVOS);
    }

    private void setFinishTime(BaselineJob baselineJob, BaselineJobPageVO pageVO) {
        if (BaselineStatusEnum.OTHERS.getCode().equals(baselineJob.getBaselineStatus())) {
            pageVO.setFinishTime("-");
        } else if (BaselineStatusEnum.SAFETY.getCode().equals(baselineJob.getBaselineStatus())) {
            pageVO.setFinishTime(DateUtil.getDate(baselineJob.getExpectFinishTime(),DateUtil.STANDARD_DATETIME_FORMAT));
        } else {
            try {
                List<BaselineJobJob> baselineJobJobs = baselineJobJobDao.selectBaselineByBaselineJobId(baselineJob.getId(), BaselineTaskTypeEnum.CHOOSE_TASK.getCode());
                List<String> jobIds = baselineJobJobs.stream().map(BaselineJobJob::getJobId).collect(Collectors.toList());
                List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(jobIds);
                Map<String, ScheduleJob> scheduleJobMap = scheduleJobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId, g -> (g)));

                boolean isCalculate = Boolean.TRUE;
                List<Long> endTimeList = Lists.newArrayList();
                for (BaselineJobJob baselineJobJob : baselineJobJobs) {
                    String jobId = baselineJobJob.getJobId();

                    ScheduleJob scheduleJob = scheduleJobMap.get(jobId);

                    if (scheduleJob == null) {
                        pageVO.setFinishTime("-");
                        isCalculate = Boolean.FALSE;
                        break;
                    }

                    if (RdosTaskStatus.FAILED_STATUS.contains(scheduleJob.getStatus())) {
                        pageVO.setFinishTime("-");
                        isCalculate = Boolean.FALSE;
                        break;
                    }

                    if (RdosTaskStatus.UNSUBMIT_STATUS.contains(scheduleJob.getStatus()) ||
                            RdosTaskStatus.WAIT_STATUS.contains(scheduleJob.getStatus()) ||
                            RdosTaskStatus.SUBMITTING_STATUS.contains(scheduleJob.getStatus())) {
                        if (baselineJobJob.getExpectEndTime() != null) {
                            endTimeList.add(baselineJobJob.getExpectEndTime().getTime());
                        }
                    }

                    if (RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
                        Timestamp execStartTime = scheduleJob.getExecStartTime();

                        if (execStartTime != null) {
                            // 运行时长
                            long currentExecTime = System.currentTimeMillis() - execStartTime.getTime();

                            // 预计运行时长
                            Integer estimatedExecTime = baselineJobJob.getEstimatedExecTime();

                            if (currentExecTime > estimatedExecTime) {
                                endTimeList.add(System.currentTimeMillis());
                            } else {
                                endTimeList.add(execStartTime.getTime() + estimatedExecTime);
                            }
                        }
                    }
                }

                if (isCalculate) {
                    long maxTime = endTimeList.stream().max(Long::compareTo).orElse(0L);

                    if (maxTime != 0L) {
                        pageVO.setFinishTime(DateUtil.getDate(maxTime, DateUtil.STANDARD_DATETIME_FORMAT));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("setFinishTime error:{}", e.getMessage(), e);
            }
        }
    }

    private Integer getCurrentMargin(String replyTime,Timestamp finishTime) {
        // 当前余量 = 承诺时间 - 当前时间
        try {

            long currMill = System.currentTimeMillis() / 1000;
            if (finishTime != null) {
                currMill = finishTime.getTime() / 1000;
            }

            long mill = DateUtil.getDateMillTOFormat(replyTime, "yyyy-MM-dd HH:mm");
            return (int)(mill-currMill)/60;
        } catch (Exception e) {
            LOGGER.error("baseline job getCurrentMargin error:{}", e.getMessage(), e);
            return 0;
        }
    }

    public List<BaselineViewVO> baselineJobGraph(Long baselineTaskId) {
        int plusDays = -15;
        long millis = DateTime.now().plusDays(plusDays)
                .withTime(0, 0, 0, 0).getMillis();
        BaselineTaskVO baselineTaskDetails = baselineTaskService.getBaselineTaskDetails(baselineTaskId);
        List<BaselineBatchVO> baselineBatchDTOS = baselineTaskDetails.getBaselineBatchDTOS();

        List<BaselineView> baselineViews = baselineJobDao.selectBaselineJobGraph(baselineTaskId, millis);
        List<BaselineViewVO> baselineViewVOS = Lists.newArrayList();

        Map<String, List<BaselineView>> map = baselineViews.stream().collect(Collectors.groupingBy(baselineView ->
                DateUtil.getDate(baselineView.getBusinessDate().getTime(), DateUtil.DATE_FORMAT)));


        while (plusDays <= 0) {
            BaselineViewVO baselineViewVO = new BaselineViewVO();
            String businessDate = DateTime.now().plusDays(plusDays).toString(DateUtil.DATE_FORMAT);
            baselineViewVO.setBusinessDate(businessDate);

            List<BaselineView> views = map.get(businessDate);

            if (CollectionUtils.isNotEmpty(views)) {
                BaselineView baselineView = views.get(0);

                if (baselineView!=null && baselineView.getFinishTime() != null) {
                    baselineViewVO.setFinishTime(baselineView.getFinishTime().getTime());
                } else {
                    baselineViewVO.setFinishTime(0L);
                }

                if (baselineView!=null && baselineView.getExpectFinishTime() != null) {
                    baselineViewVO.setExpectFinishTime(baselineView.getExpectFinishTime().getTime());
                } else {
                    baselineViewVO.setExpectFinishTime(0L);
                }

                if (baselineView != null) {
                    String replyTimeView = "";
                    if (StringUtils.isBlank(baselineView.getCycTime()) && CollectionUtils.isNotEmpty(baselineBatchDTOS)) {
                        BaselineBatchVO baselineBatchVO = baselineBatchDTOS.get(0);
                        replyTimeView  = businessDate + " " + baselineBatchVO.getReplyTime();
                    } else {
                        for (BaselineBatchVO baselineBatchDTO : baselineBatchDTOS) {
                            String cycTime = baselineBatchDTO.getCycTime();
                            try {
                                cycTime = DateUtil.getDateStrTOFormat(cycTime,DateUtil.STANDARD_DATETIME_FORMAT,DateUtil.STANDARD_FORMAT);
                            } catch (ParseException ignored) {
                            }

                            if (cycTime.equals(baselineBatchDTO.getCycTime())) {
                                replyTimeView  = businessDate + " " + baselineBatchDTO.getReplyTime();
                            }
                        }
                    }

                    if (StringUtils.isNotBlank(replyTimeView)) {
                        try {
                            baselineViewVO.setReplyTime(DateUtil.stringToLong(replyTimeView, DateUtil.STANDARD_DATE_FORMAT));
                        } catch (ParseException e) {
                            LOGGER.error("", e);
                        }
                    }
                }
            }
            baselineViewVOS.add(baselineViewVO);
            plusDays++;
        }
        return baselineViewVOS;
    }


    public List<String> getBaselineJobInfo(Long baselineJob) {
        if (baselineJob == null) {
            return Lists.newArrayList();
        }
        return baselineJobJobDao.selectJobIdByBaselineJobId(baselineJob, BaselineTaskTypeEnum.CHOOSE_TASK.getCode());
    }

    public List<BaselineBlockJobRecordVO> baselineBlockJob(Long baselineJobId) {
        if (baselineJobId == null) {
            return Lists.newArrayList();
        }

        List<BaselineBlockJobRecord> baselineBlockJobRecordList = baselineBlockJobRecordDao.selectByBaselineJobId(baselineJobId);
        return baselineJobStruct.baselineBlockJob(baselineBlockJobRecordList);
    }

    public boolean hasJobByBusinessDate(Timestamp timestamp,Long baselineTaskId) {
        if (timestamp == null) {
            return false;
        }
        List<BaselineJob> baselineJobs = baselineJobDao.selectByBusinessDate(timestamp, baselineTaskId);
        return baselineJobs.size() > 0;
    }

    public void createBaselineJob(Long id, String triggerDay) {
        if (id != null) {
            scheduleTaskPriorityService.clearPriority(id);
            BaselineTaskDTO baselineTaskDTO = baselineTaskService.getBaselineTaskDTO(id);
            jobGraphFinishBaselineListener.createBaselineJob(triggerDay, baselineTaskDTO);
        } else {
            jobGraphFinishBaselineListener.successEvent(triggerDay);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void againCreateBaseJob(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        List<BaselineJob> baselineJobs = baselineJobDao.selectByIds(ids);

        // 删除基线
        ids = baselineJobs.stream().map(BaselineJob::getId).collect(Collectors.toSet());
        baselineJobDao.deleteByIds(ids);
        baselineJobJobDao.deleteByBaselineJobIds(ids);

        // 重新生成基线
        for (BaselineJob baselineJob : baselineJobs) {
            Timestamp businessDate = baselineJob.getBusinessDate();
            createBaselineJob(baselineJob.getBaselineTaskId(),DateUtil.getDate(businessDate.getTime(),DateUtil.STANDARD_DATETIME_FORMAT));
        }
    }
}
