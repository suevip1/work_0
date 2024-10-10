package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * schedule job param
 *
 * @author ：wangchuan
 * date：Created in 14:09 2022/7/26
 * company: www.dtstack.com
 */
public interface ScheduleJobParamDao {

    /**
     * 批量插入数据
     *
     * @param scheduleJobParams 实例任务参数集合
     */
    void insertBatch(@Param("scheduleJobParams") List<ScheduleJobParam> scheduleJobParams);


    /**
     * 根据 jobId 删除实例任务参数
     *
     * @param jobId 实例任务 id
     */
    void deleteByJobId(@Param("jobId") String jobId);

    /**
     * 根据 jobId 查询实例的任务参数
     *
     * @param jobId 实例任务 id
     * @return 任务参数信息
     */
    List<ScheduleJobParam> selectByJobId(@Param("jobId") String jobId);
}
