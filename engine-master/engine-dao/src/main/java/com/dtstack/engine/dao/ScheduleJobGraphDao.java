package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobGraph;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName ScheduleJobGraphDao
 * @date 2022/8/2 7:22 PM
 */
public interface ScheduleJobGraphDao {

    void batchInsert(@Param("scheduleJobGraphs") List<ScheduleJobGraph> scheduleJobGraphs);

    List<ScheduleJobGraph> selectByDate(@Param("dtuicTenantId") Long dtuicTenantId, @Param("appType") Integer appType, @Param("projectId") Long projectId, @Param("date") String date);
}
