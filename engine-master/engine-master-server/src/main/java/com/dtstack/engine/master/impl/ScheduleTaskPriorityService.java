package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.task.*;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskPriorityDao;
import com.dtstack.engine.master.utils.JobExecuteOrderUtil;
import com.dtstack.engine.po.ScheduleTaskPriority;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2023/6/14 10:38 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskPriorityService {

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskPriorityDao scheduleTaskPriorityDao;


    public Integer insertBatch(List<ScheduleTaskPriority> scheduleTaskPriorityList) {
        if (CollectionUtils.isNotEmpty(scheduleTaskPriorityList)) {
            return scheduleTaskPriorityDao.insertBatch(scheduleTaskPriorityList);
        }

        return 0;
    }

    public Integer selectPriorityByTaskId(Long taskId, Integer appType) {
        if (taskId != null && appType != null) {
            List<Integer> priority = scheduleTaskPriorityDao.selectByTaskId(taskId, appType);
            if (CollectionUtils.isNotEmpty(priority)) {
                return Collections.max(priority);
            }
        }
        return 1;
    }

    public List<ScheduleTaskPriority> selectPriorityByTaskIds(List<Long> taskIds, Integer appType) {
        if (CollectionUtils.isNotEmpty(taskIds) && appType != null) {
            return scheduleTaskPriorityDao.selectPriorityByTaskIds(taskIds,appType);
        }

        return Lists.newArrayList();
    }

    public ScheduleTaskPriorityReturnVO findPriorityByTask(ScheduleTaskPriorityVO vo) {
        Integer priority = selectPriorityByTaskId(vo.getTaskId(), vo.getAppType());
        ScheduleTaskPriorityReturnVO returnVO = new ScheduleTaskPriorityReturnVO();
        returnVO.setPriority(priority);
        returnVO.setTaskId(vo.getTaskId());
        returnVO.setAppType(vo.getAppType());
        return returnVO;
    }

    public StatisticsPriorityReturnVO statisticsPriorityByTask(StatisticsPriorityVO vo) {
        StatisticsPriorityReturnVO returnVO = new StatisticsPriorityReturnVO();
        Map<String,List<StatisticsPriorityCountVO>> statisticsMap = Maps.newHashMap();
        returnVO.setCurrentRefreshTime(new DateTime().toString(DateUtil.STANDARD_DATETIME_FORMAT));

        if (vo.getAppType() != null
                && vo.getProjectId() != null
                && vo.getTenantId() != null) {
            Long start = JobExecuteOrderUtil.buildJobExecuteOrder(DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()), new AtomicInteger());

            List<Integer> statusList = Lists.newArrayList();
            List<Integer> unsubmitStatus = RdosTaskStatus.UNSUBMIT_STATUS;
            List<Integer> waitStatus = RdosTaskStatus.WAIT_STATUS;

            statusList.addAll(unsubmitStatus);
            statusList.addAll(waitStatus);

            List<ScheduleJob> scheduleJobStatusCounts = scheduleJobDao.listByTimeAndStatus(vo.getTenantId(),
                    vo.getProjectId(), vo.getAppType(), start,
                    statusList,vo.getResourceId());

            List<Long> taskIds = scheduleJobStatusCounts.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());

            List<ScheduleTaskPriority> scheduleTaskPriorities = selectPriorityByTaskIds(taskIds, vo.getAppType());
            Map<String, List<ScheduleTaskPriority>> priorityMap = scheduleTaskPriorities.stream().collect(
                    Collectors.groupingBy(scheduleTaskPriority ->
                            scheduleTaskPriority.getTaskId() + GlobalConst.PARTITION + scheduleTaskPriority.getAppType()));

            Map<Integer,StatisticsPriorityCountVO>  priorityUnSubmitMap = Maps.newHashMap();
            Map<Integer,StatisticsPriorityCountVO>  priorityWaitRunMap = Maps.newHashMap();
            for (ScheduleJob scheduleJob : scheduleJobStatusCounts) {
                List<ScheduleTaskPriority> priorityList = priorityMap.get(scheduleJob.getTaskId() + GlobalConst.PARTITION + scheduleJob.getAppType());
                int maxPriority;
                if (CollectionUtils.isEmpty(priorityList)) {
                    maxPriority = 1;
                } else {
                    maxPriority = priorityList.stream().map(ScheduleTaskPriority::getPriority).max(Comparator.comparingInt(priority -> priority)).orElse(1);
                }

                if (unsubmitStatus.contains(scheduleJob.getStatus())) {
                    fillPriorityMap(priorityUnSubmitMap, maxPriority);
                } else if (waitStatus.contains(scheduleJob.getStatus())) {
                    fillPriorityMap(priorityWaitRunMap, maxPriority);
                }
            }

            statisticsMap.put("UNSUBMIT_STATUS",Lists.newArrayList(priorityUnSubmitMap.values()));
            statisticsMap.put("WAIT_STATUS", Lists.newArrayList(priorityWaitRunMap.values()));
            returnVO.setStatisticsMap(statisticsMap);
            return returnVO;
        }

        return returnVO;
    }

    private void fillPriorityMap(Map<Integer, StatisticsPriorityCountVO> priorityUnSubmitMap, int maxPriority) {
        StatisticsPriorityCountVO statisticsPriorityCountVO = priorityUnSubmitMap.get(maxPriority);

        if (statisticsPriorityCountVO == null) {
            statisticsPriorityCountVO = new StatisticsPriorityCountVO();
            statisticsPriorityCountVO.setPriority(maxPriority);
            statisticsPriorityCountVO.setCount(0);
        }

        Integer count = statisticsPriorityCountVO.getCount();
        statisticsPriorityCountVO.setCount(++count);

        priorityUnSubmitMap.put(maxPriority,statisticsPriorityCountVO);
    }

    public List<Long> findTaskIdByPriorities(List<Integer> priorityList) {
        if (CollectionUtils.isNotEmpty(priorityList)) {
            return scheduleTaskPriorityDao.selectTaskByPriorities(priorityList);
        }
        return Lists.newArrayList();
    }

    public void clearPriority(Long baselineTaskId) {
        scheduleTaskPriorityDao.clearPriority(baselineTaskId);
    }
}
