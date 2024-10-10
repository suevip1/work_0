package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskRefShade;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleTaskRefShadeDao {

    Integer insert(ScheduleTaskRefShade scheduleTaskRefShade);

    Integer deleteByTaskIdAndRefTaskIdAndAppType(@Param("taskId") long taskId, @Param("refTaskId") long refTaskId ,@Param("appType") Integer appType);

    List<ScheduleTaskRefShade> listByTaskKeys(@Param("taskKeys") List<String> taskKeys);

    List<ScheduleTaskRefShade> listByRefTaskKeys(@Param("taskKeys") List<String> taskKeys);

    List<Long> listRefTaskIdByTaskId(@Param("taskId") long taskId, @Param("appType") Integer appType);

    List<ScheduleTaskRefShade> listRefTaskByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

}
