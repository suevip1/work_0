package com.dtstack.engine.dao;

import com.dtstack.engine.po.BaselineTaskTask;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 10:57 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineTaskTaskDao {
    /**
     * 删除TaskTask
     */
    Integer deleteByBaselineTaskId(@Param("baselineTaskId") Long baselineTaskId);

    Integer batchInsert(@Param("baselineTaskTasks") List<BaselineTaskTask> baselineTaskTasks);

    List<BaselineTaskTask> selectByBaselineTaskId(@Param("baselineTaskId") Long baselineTaskId);

    List<BaselineTaskTask> selectByBaselineTaskIds(@Param("baselineTaskIds") List<Long> baselineTaskIds);

    List<Long> selectByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    List<BaselineTaskTask> selectAllByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    Integer deleteByIds(@Param("ids") List<Long> ids);
}
