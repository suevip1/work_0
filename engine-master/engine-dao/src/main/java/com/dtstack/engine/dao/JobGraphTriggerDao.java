package com.dtstack.engine.dao;

import com.dtstack.engine.po.JobGraphTrigger;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface JobGraphTriggerDao {

    Integer insert(JobGraphTrigger jobGraphTrigger);

    JobGraphTrigger getByTriggerTimeAndTriggerType(@Param("triggerTime") Timestamp timestamp, @Param("triggerType") int triggerType);

    /**
     * 根据触发时间查询关联最小任务ID
     * @param triggerStartTime
     * @param triggerEndTime
     * @return
     */
    String  getMinJobIdByTriggerTime(@Param("triggerStartTime")String triggerStartTime,@Param("triggerEndTime")String triggerEndTime);

    List<JobGraphTrigger> listByTriggerTime(@Param("beginTime") Timestamp begin, @Param("endTime") Timestamp end);

    JobGraphTrigger getStartTriggerTime();

}
