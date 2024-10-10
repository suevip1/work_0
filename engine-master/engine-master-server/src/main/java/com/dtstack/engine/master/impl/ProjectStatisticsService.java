package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.ProjectStatisticsDao;
import com.dtstack.engine.po.ProjectStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author yuebai
 * @date 2022/8/16
 */
@Service
public class ProjectStatisticsService {

    public final Integer timeInterval = 5;

    public Integer getTimeInterval() {
        return timeInterval;
    }

    @Autowired
    private ProjectStatisticsDao projectStatisticsDao;

    public LocalDateTime getRecentStartTime() {
        int minute = LocalDateTime.now().getMinute();
        return LocalDateTime.now().withMinute(Math.floorDiv(minute, timeInterval) * timeInterval)
                .withSecond(0)
                .withNano(0);
    }


    public void insertBatch(List<ProjectStatistics> projectStatistics) {
        projectStatisticsDao.insertBatch(projectStatistics);
    }

    public void delete(Date gmtCreate) {
        projectStatisticsDao.delete(gmtCreate);
    }

    public ProjectStatistics queryRecent(Long projectId, Integer appType, Long tenantId, Long userId) {
        return projectStatisticsDao.queryLast(projectId, appType, tenantId, userId, null);
    }

    public ProjectStatistics queryTotalTenantRecentTime(Long tenantId, Integer appType, Long userId, Date gmtCreate) {
        return projectStatisticsDao.queryTotalTenantRecentTime(tenantId, appType, userId, gmtCreate);
    }

    public ProjectStatistics queryTotalUserRecentTime(Long userId, Integer appType, Long tenantId, Date gmtCreate) {
        return projectStatisticsDao.queryTotalUserRecentTime(userId, appType, tenantId, gmtCreate);
    }

    public ProjectStatistics queryTotalProjects(List<Long> projectIds, Integer appType, Long dtuicTenantId, Long userId, Date gmtCreate) {
        return projectStatisticsDao.queryTotalProjects(projectIds, appType, dtuicTenantId, userId, gmtCreate);
    }
}
