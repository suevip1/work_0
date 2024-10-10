package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleTaskPriority;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/6/14 10:03 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskPriorityDao {


    Integer insertBatch(@Param("scheduleTaskPriorityList") List<ScheduleTaskPriority> scheduleTaskPriorityList);

    List<Integer> selectByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    List<ScheduleTaskPriority> selectPriorityByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    List<Long> selectTaskByPriorities(@Param("priorityList") List<Integer> priorityList);

    void clearPriority(@Param("baselineTaskId") Long baselineTaskId);
}
