package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleTaskParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScheduleTaskParamDao {

    void insertBatch(@Param("scheduleTaskParams") List<ScheduleTaskParam> scheduleTaskParams);

    void deleteByTaskIds(@Param("taskIds")List<Long> taskIds,  @Param("appType")int appType);

    List<ScheduleTaskParam> findByTaskId(@Param("taskId")Long taskId,  @Param("appType")int appType);

    List<ScheduleTaskParam> findTask(@Param("paramId")Long paramId,@Param("limit") int limit);


    int findTotalTasks(@Param("paramId") Long paramId);

    int removeIdenticalParam(@Param("taskId")Long taskId, @Param("appType")Integer appType, @Param("paramNames")List<String> paramNames);
}
