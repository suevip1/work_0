package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobResourceFile;
import org.apache.ibatis.annotations.Param;

/**
 * @author leon
 * @date 2022-08-29 16:02
 **/
public interface ScheduleJobResourceFileDao {

    Integer insert(ScheduleJobResourceFile scheduleJobResourceFile);

    ScheduleJobResourceFile getByJobId(String jobid);

    void deleteByJobIdAndType(@Param("jobId") String jobId, @Param("type") Integer type);
}
