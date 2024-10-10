package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleTaskCalender;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScheduleTaskCalenderDao {

    void insertBatch(@Param("scheduleTaskCalenders") List<ScheduleTaskCalender> scheduleTaskTimes);

    void deleteByTaskIds(@Param("taskIds")List<Long> taskIds,  @Param("appType")int appType);

    ScheduleTaskCalender findByTaskId(@Param("taskId")Long taskId,  @Param("appType")int appType);

    List<ScheduleTaskCalender> findByTaskIds(@Param("taskIds")List<Long> taskIds,  @Param("appType")int appType);

    List<CalenderTaskDTO> getTaskByCalenderId(@Param("calenderIds")List<Long> calenderIds);

    List<Long> getAllTaskByCalenderId(@Param("calenderIds")List<Long> calenderIds,@Param("appType")int appType);

    List<CalenderTaskDTO> pageQuery(PageQuery query);

    int findTotalTasks(@Param("calenderId") Long calenderId);

    List<CalenderTaskDTO> getCalenderByTasks(@Param("taskIds")List<Long> taskIds,@Param("appType")int appType);
}
