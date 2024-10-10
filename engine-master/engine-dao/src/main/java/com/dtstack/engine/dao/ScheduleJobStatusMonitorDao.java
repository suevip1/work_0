package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobStatusMonitor;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-15 22:08
 */
public interface ScheduleJobStatusMonitorDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ScheduleJobStatusMonitor record);

    int insertSelective(ScheduleJobStatusMonitor record);

    ScheduleJobStatusMonitor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ScheduleJobStatusMonitor record);

    int updateByPrimaryKey(ScheduleJobStatusMonitor record);

    int batchInsert(@Param("list") List<ScheduleJobStatusMonitor> list);

    Integer checkExistsByAppTypeKey(String appTypeKey);

    ScheduleJobStatusMonitor getByAppTypeKey(String appTypeKey);

    List<ScheduleJobStatusMonitor> listAll();
}