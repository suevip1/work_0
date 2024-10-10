package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobRelyCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 10:20 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobRelyCheckDao {

    Integer insertBatch(@Param("scheduleJobRelyCheckList") List<ScheduleJobRelyCheck> scheduleJobRelyCheckList);

    Integer deleteByJobId(@Param("jobId") String jobId);

    Integer deleteByJobIds(@Param("jobIds") List<String> jobIds);

    List<ScheduleJobRelyCheck> findByJobId(@Param("jobId") String jobId);

    List<ScheduleJobRelyCheck> findByJobIds(@Param("jobIds") List<String> jobIds);

    List<ScheduleJobRelyCheck> findByParentsJobIds(@Param("parentJobIds") List<String> parentJobIds);

    Integer deleteByParentJobIds(@Param("jobIds") List<String> jobIds);
}
