package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobExpand;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/8/26 2:33 下午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobExpandDao {

    Integer insert(@Param("scheduleJobExpand") ScheduleJobExpand scheduleJobExpand);

    String getLogInfoByJobId(@Param("jobId") String jobId);

    Integer updateLogInfoByJobId(@Param("jobId") String jobId, @Param("logInfo") String logInfo,@Param("status") Integer status);

    Integer updateLogInfoAndTimeByJobId(@Param("jobId") String jobId, @Param("logInfo") String logInfo,@Param("status") Integer status);

    Integer updateJobExpandByJobId(@Param("jobId") String jobId, @Param("logInfo") String logInfo, @Param("status") Integer status, @Param("execStartTime") Date execStartTime , @Param("execEndTime") Date execEndTime);

    Integer updateLogInfoByJobIds(@Param("jobIds") List<String> jobIds, @Param("logInfo") String logInfo);

    Integer updateLogByJobId(@Param("jobId") String jobId, @Param("logInfo") String logInfo, @Param("engineLog") String engineLog,@Param("retryTaskParams")String retryTaskParam,@Param("engineId") String engineId, @Param("appId") String appId);

    ScheduleJobExpand getLogByJobId(@Param("jobId") String jobId);

    ScheduleJobExpand getLogByJobIdAndNum(@Param("jobId") String jobId, @Param("num") Integer num);

    List<ScheduleJobExpand> getLogByJobIds(@Param("jobIds") List<String> jobIds);

    String getJobExtraInfo(@Param("jobId") String jobId);

    String getJobExtraInfoById(@Param("expendId") Long expendId);

    Integer updateExtraInfo(@Param("jobId") String jobId, @Param("jobExtraInfo") String jobExtraInfo);

    Integer updateExtraInfoAndLog(@Param("jobId") String jobId, @Param("jobExtraInfo") String jobExtraInfo, @Param("logInfo") String logInfo,
                                  @Param("engineLog") String engineLog, @Param("runSqlText") String runSqlText);

    Integer updateEngineLog(@Param("jobId") String jobId, @Param("engineLog") String engineLog);

    Integer batchInsert(@Param("expands") List<ScheduleJobExpand> expands);

    Integer batchInsertIgnore(@Param("expands") List<ScheduleJobExpand> insertExpands);

    List<ScheduleJobExpand> getExpandByJobIds(@Param("jobIds")List<String> jobIds);

    ScheduleJobExpand getExpandByJobId(@Param("jobId") String jobId);

    ScheduleJobExpand getExpandByJobIdByNum(@Param("jobId") String jobId, Integer num);

    String getRunSqlText(@Param("jobId") String jobId, @Param("runNum") Integer runNum);

    Integer updateRunSubTaskIdByJobId(@Param("jobId") String jobId, @Param("runSubTaskId") String runSubTaskId);

    String getRunSubTaskIdByJobId(@Param("jobId") String jobId);
    
    Integer clearLog(@Param("jobIds") List<String> jobIds);

    List<ScheduleJobExpand> getSimplifyExpandByJobId(@Param("jobId") String jobId, @Param("limit") Integer limit);

    List<ScheduleJobExpand> getSimplifyExpandByJobIds(@Param("jobIds") List<String> jobIds);

    Integer updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") Integer status);

    Integer updateTaskStatusNotStopped(@Param("jobId") String jobId, @Param("status") Integer status, @Param("stoppedStatus") List<Integer> stoppedStatus);

    Integer updateJobStatus(@Param("jobId") String jobId, @Param("status") Integer status);

    Integer setEnableJobMonitor(@Param("jobId") String jobId);

    Integer updateJobDiagnosisInfo(@Param("jobId") String jobId, @Param("diagnosisInfo") String diagnosisInfo);

    String getJobDiagnosisInfo(@Param("jobId") String jobId, @Param("runNum") Integer runNum);

    Integer updateJobSubmitSuccess(@Param("jobId") String jobId, @Param("engineId") String engineTaskId, @Param("appId") String appId);

    /**
     * 更新实例yarn运行消耗的cpu和内存
     * @param jobId
     * @param coreNum
     * @param memNum
     * @return
     */
    Integer updateJobCoreAndMem(@Param("jobId") String jobId, @Param("coreNum") Integer coreNum, @Param("memNum") Integer memNum,@Param("runNum") Integer runNum);

    Integer updateAuthId(@Param("authId")Long authId, @Param("jobId")String jobId);

    Long getAuthId(@Param("jobId") String jobId, @Param("runNum") Integer runNum);

}
