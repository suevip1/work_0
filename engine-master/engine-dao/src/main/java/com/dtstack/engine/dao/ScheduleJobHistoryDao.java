package com.dtstack.engine.dao;

import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.po.ScheduleJobHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ScheduleJobHistoryDao {

    List<ScheduleJobHistory> selectByJobId(PageQuery<ScheduleJobHistory> pageQuery);

    Integer selectJobIdByCount(PageQuery<ScheduleJobHistory> pageQuery);

    Integer insert(ScheduleJobHistory history);

    void updateByApplicationId(@Param("applicationId") String applicationId, @Param("appType") Integer appType);

    List<ScheduleJobHistory> listByJobId(@Param("jobId") String jobId, @Param("limit") Integer limit);

    void deleteByJobIds(@Param("jobIds") List<String> jobIds, @Param("appType") Integer appType);

    void deleteByIds(@Param("ids") List<Long> jobIds);

    List<ScheduleJobHistory> listJobHistoriesByStartId(@Param("startId") long startId, @Param("limit") Integer limit, @Param("time") Timestamp time);
    List<String> getJobIdsByAppType(@Param("appType") Integer appType);
}
