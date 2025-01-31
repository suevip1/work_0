package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeDao {

    ScheduleTaskTaskShade getOne(@Param("id") long id);

    ScheduleTaskTaskShade getOneByTaskId(@Param("taskId") Long taskId, @Param("parentTaskId") Long parentTaskId,@Param("appType")Integer appType);

    List<ScheduleTaskTaskShade> listChildTask(@Param("parentTaskId") long parentTaskId,@Param("appType")Integer appType);

    List<ScheduleTaskTaskShade> listChildTaskLimit(@Param("parentTaskId") Long taskId, @Param("appType") Integer appType, @Param("limit") Integer limit);

    List<ScheduleTaskTaskShade> listParentTask(@Param("childTaskId") long childTaskId,@Param("appType")Integer appType);

    Integer deleteByTaskId(@Param("taskId") long taskId,@Param("appType")Integer appType);

    Integer deleteByTaskKeys(@Param("jobKeys") Collection<String> jobKeys);

    Integer deleteByParentTaskKeys(@Param("jobKeys") Collection<String> jobKeys);

    Integer insert(ScheduleTaskTaskShade scheduleTaskTaskShade);

    Integer update(ScheduleTaskTaskShade scheduleTaskTaskShade);

    List<ScheduleTaskTaskShade> listParentTaskKeys(@Param("taskKeys") List<String> taskKeys);

    List<ScheduleTaskTaskShade> listTaskKeys(@Param("taskKeys") List<String> taskKeys);

    List<ScheduleTaskTaskShade> getTaskOtherPlatformByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("listChildTaskLimit") Integer listChildTaskLimit);


    List<ScheduleTaskTaskShade> listParentTaskExcludeParentAppTypes(@Param("childTaskId") Long taskId, @Param("excludeAppTypes") List<Integer> excludeAppTypes);
}
