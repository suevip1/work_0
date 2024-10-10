package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleTaskProjectParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-06 13:38
 */
public interface ScheduleTaskProjectParamDao {
    int deleteByPrimaryKey(Long id);

    int insert(ScheduleTaskProjectParam record);

    int insertSelective(ScheduleTaskProjectParam record);

    ScheduleTaskProjectParam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ScheduleTaskProjectParam record);

    int updateByPrimaryKey(ScheduleTaskProjectParam record);

    int batchInsert(@Param("list") List<ScheduleTaskProjectParam> list);

    List<ScheduleTaskProjectParam> findTaskBindProjectParams(@Param("taskId")Long taskId, @Param("appType")Integer appType);

    List<Long> distinctExistProjectParamIds(@Param("projectParamIds")List<Long> projectParamIds);

    List<ScheduleTaskProjectParam> findByProjectParamId(@Param("projectParamId")Long projectParamId);

    int removeIdenticalProjectParam(@Param("taskId")Long taskId, @Param("appType")Integer appType, @Param("paramNames")List<String> paramNames);

    int removeByTask(@Param("taskId")Long taskId, @Param("appType")Integer appType);

    int removeByTasks(@Param("taskIds")List<Long> taskIds, @Param("appType")Integer appType);
}