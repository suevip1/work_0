package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobGanttChart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface ScheduleJobGanttChartDao {

    Integer insert(ScheduleJobGanttChart ganttChart);

    void updateTime(@Param("jobId") String jobId, @Param("column") String column);

    ScheduleJobGanttChart selectOne(@Param("jobId") String jobId);

    void deleteByJobId(@Param("jobId") String jobId);

    void deleteByJobIds(@Param("jobIds") Set<String> jobIds);

}