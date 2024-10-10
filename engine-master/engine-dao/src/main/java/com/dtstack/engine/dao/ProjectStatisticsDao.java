package com.dtstack.engine.dao;

import com.dtstack.engine.po.ProjectStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author yuebai
 * @date 2022/8/11
 */
public interface ProjectStatisticsDao {

    void insertBatch(List<ProjectStatistics> projectStatistics);

    void delete(@Param("gmtCreate") Date gmtCreate);

    ProjectStatistics queryLast(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("tenantId") Long tenantId,
                                @Param("userId") Long userId, @Param("gmtCreate") Date gmtCreate);

    ProjectStatistics queryTotalUserRecentTime(@Param("userId") Long userId, @Param("appType") Integer appType, @Param("tenantId") Long tenantId, @Param("gmtCreate") Date gmtCreate);

    ProjectStatistics queryTotalTenantRecentTime(@Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("userId") Long userId, @Param("gmtCreate") Date gmtCreate);

    ProjectStatistics queryTotalProjects(@Param("projectIds") List<Long> projectIds, @Param("appType") Integer appType, @Param("tenantId") Long tenantId,
                                         @Param("userId") Long userId, @Param("gmtCreate") Date gmtCreate);
}
