package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-07-06
 */
public interface ScheduleJobOperatorRecordDao {

    Long insert(ScheduleJobOperatorRecord engineJobStopRecord);

    Long insertIgnore(ScheduleJobOperatorRecord operatorRecord);

    Integer delete(@Param("id") Long id);

    Integer deleteByJobIds(@Param("jobIds") List<String> jobIds);

    Integer deleteByJobIdAndType(@Param("jobId") String jobId,@Param("type")Integer type);
    
    Integer deleteByJobIdsAndType(@Param("jobIds") List<String> jobIds, @Param("type") Integer type);

    Integer updateOperatorExpiredVersion(@Param("id") Long id, @Param("operatorExpired") Timestamp operatorExpired, @Param("version") Integer version);

    List<ScheduleJobOperatorRecord> listStopJob(@Param("startId") Long startId);

    List<String> listStopRecordByJobIds(@Param("jobIds") List<String> jobIds);

    List<String> listConsoleStopJobIdsByJobIds(@Param("jobIds") List<String> jobIds);

    List<ScheduleJobOperatorRecord> listTimeoutRecordByJobIds(@Param("jobIds") List<String> jobIds);

    Timestamp getJobCreateTimeById(@Param("id") Long id);

    Long insertBatch(@Param("records") Collection<ScheduleJobOperatorRecord> records);

    List<ScheduleJobOperatorRecord> listJobs(@Param("startId")Long startId, @Param("nodeAddress")String nodeAddress, @Param("types")List<Integer> types,@Param("limit") Integer limit);

    void updateNodeAddressByIds(@Param("nodeAddress") String nodeAddress, @Param("jobIds")List<String> value);

    ScheduleJobOperatorRecord getOperatorByTypeAndJobId(@Param("jobId") String jobId,@Param("type")Integer type);

    List<ScheduleJobOperatorRecord> listOperatorByJobIdsAndType(@Param("jobIds")List<String> jobIds,@Param("types")List<Integer> types);

    Integer update(ScheduleJobOperatorRecord jobStopRecord);

    Long countByType(@Param("type") Integer type);

}
