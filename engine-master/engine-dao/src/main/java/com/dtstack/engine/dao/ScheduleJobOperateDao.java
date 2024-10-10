package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobOperate;
import com.dtstack.engine.api.param.OperatorParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 1:40 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobOperateDao {

    void insert(@Param("scheduleJobOperate") ScheduleJobOperate scheduleJobOperate);

    void insertBatch(@Param("scheduleJobOperates") List<ScheduleJobOperate> scheduleJobOperates);

    Integer countPage(@Param("jobId") String jobId);

    List<ScheduleJobOperate> page(@Param("pageParam") OperatorParam pageParam);
}
