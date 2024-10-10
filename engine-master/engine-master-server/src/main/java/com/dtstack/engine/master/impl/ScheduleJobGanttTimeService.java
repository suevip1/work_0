package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.ScheduleJobGanttChartDao;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.po.ScheduleJobGanttChart;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/8/22
 */
@Service
public class ScheduleJobGanttTimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    private static final String GANTT_CACHE_KEY = "%s-%s";

    @Autowired
    private ScheduleJobGanttChartDao scheduleJobGanttChartDao;

    private Cache<String, Boolean> updateCache =
            CacheBuilder.newBuilder().maximumSize(1000)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build();

    /**
     * 记录流程结束时间
     *
     * @param jobId
     * @param jobGanttChartEnum
     */
    public void ganttChartTime(String jobId, JobGanttChartEnum jobGanttChartEnum) {
        try {
            String cacheKey = generateGanttCacheKey(jobId, jobGanttChartEnum);
            if (Boolean.TRUE.equals(updateCache.getIfPresent(cacheKey))) {
                return;
            }
            scheduleJobGanttChartDao.updateTime(jobId, jobGanttChartEnum.toString().toLowerCase(Locale.ROOT));
            updateCache.put(cacheKey, true);
        } catch (Exception e) {
            LOGGER.error("save time error {}", jobId, e);
        }
    }

    public void ganttChartTime(String jobId, JobGanttChartEnum jobGanttChartEnum, Integer scheduleType, Integer computeType) {
        try {
            if (!ComputeType.BATCH.getType().equals(computeType)) {
                return;
            }
            if (EScheduleType.TEMP_JOB.getType().equals(scheduleType)) {
                return;
            }
            ganttChartTime(jobId, jobGanttChartEnum);
        } catch (Exception e) {
            LOGGER.error("save time error {}", jobId, e);
        }
    }

    public void ganttChartTimeRunImmediately(String jobId) {
        ganttChartTime(jobId, JobGanttChartEnum.JOB_SUBMIT_TIME);
        ganttChartTime(jobId, JobGanttChartEnum.RESOURCE_MATCH_TIME);
        ganttChartTime(jobId, JobGanttChartEnum.RUN_JOB_TIME);
    }

    public void insert(String jobId) {
        try {
            ScheduleJobGanttChart scheduleJobGanttChart = new ScheduleJobGanttChart();
            scheduleJobGanttChart.setJobId(jobId);
            scheduleJobGanttChartDao.insert(scheduleJobGanttChart);
            this.cleanGanttCache(jobId);
        } catch (Exception e) {
            LOGGER.error("save time error {}", jobId, e);
        }
    }

    public boolean checkExist(String jobId) {
        return select(jobId) != null;
    }

    public ScheduleJobGanttChart select(String jobId) {
        return scheduleJobGanttChartDao.selectOne(jobId);
    }

    public void delete(String jobId) {
        scheduleJobGanttChartDao.deleteByJobId(jobId);
    }

    public static String generateGanttCacheKey(String jobId, JobGanttChartEnum jobGanttChartEnum) {
        return String.format(GANTT_CACHE_KEY, jobId, jobGanttChartEnum.getVal());
    }

    private void cleanGanttCache(String jobId) {
        List<String> caches = Arrays.stream(JobGanttChartEnum.values())
                .map(g -> generateGanttCacheKey(jobId, g))
                .collect(Collectors.toList());
        updateCache.invalidateAll(caches);
    }

    public void batchClear(Set<String> jobIds) {
        scheduleJobGanttChartDao.deleteByJobIds(jobIds);
    }
}
