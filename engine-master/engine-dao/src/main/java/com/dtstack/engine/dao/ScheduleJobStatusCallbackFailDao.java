package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobStatusCallbackFail;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-16 19:52
 */
public interface ScheduleJobStatusCallbackFailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ScheduleJobStatusCallbackFail record);

    int insertSelective(ScheduleJobStatusCallbackFail record);

    ScheduleJobStatusCallbackFail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ScheduleJobStatusCallbackFail record);

    int updateByPrimaryKey(ScheduleJobStatusCallbackFail record);

    int batchInsert(@Param("list") List<ScheduleJobStatusCallbackFail> list);

    List<ScheduleJobStatusCallbackFail> listByAppTypeKey(String appTypeKey);

    List<ScheduleJobStatusCallbackFail> listAll();

    Integer checkExists();

    ScheduleJobStatusCallbackFail selectByJobIdAndMonitorId(@Param("jobId") String jobId, @Param("monitorId") Integer monitorId);

    void deleteByThreshold(@Param("failRemainLimitInDay") Integer failRemainLimitInDay, @Param("failMaxRetryNum") Integer failMaxRetryNum);

    void deleteWhenNoAppType();
}