package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleTaskTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2023/2/23 10:00 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskTagDao {

    Integer insertBatch(@Param("tags") List<ScheduleTaskTag> scheduleTaskTags);

    Integer deleteByTaskIdAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    List<ScheduleTaskTag> findTaskByTagIds(@Param("tagIds") List<Long> tagIds, @Param("appType") Integer appType);

    List<ScheduleTaskTag> findTagByTaskIds(@Param("taskIds") Set<Long> taskIds, @Param("appType") Integer appType);

    Integer deleteTagByTagId(@Param("tagId") Long tagId);
}
