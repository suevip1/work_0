package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.CheckpointDetail;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CheckpointQueryParam;
import com.dtstack.engine.api.param.StreamJobQueryParam;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.FlinkTableResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.stream.EngineStreamJobVO;
import com.dtstack.engine.api.vo.stream.FlinkQueryResultVO;
import com.dtstack.engine.api.vo.stream.ScheduleStreamJobVO;
import com.dtstack.engine.api.vo.task.FlinkWebUrlVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface StreamTaskService extends DtInsightServer {
    /**
     * 查询checkPoint
     * 废弃, 请使用 queryCheckPoint 接口获取 checkpoint
     */
    @Deprecated
    @RequestLine("POST /node/streamTask/getCheckPoint")
    ApiResponse<List<EngineJobCheckpoint>> getCheckPoint(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd);

    /**
     * 查询生成失败的 checkPoint
     */
    @RequestLine("POST /node/streamTask/getFailedCheckPoint")
    ApiResponse<List<EngineJobCheckpoint>> getFailedCheckPoint(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd);

    /**
     * 查询savepoint
     * 废弃, 请使用 queryCheckPoint 接口获取 savepoint
     */
    @Deprecated
    @RequestLine("POST /node/streamTask/getSavePoint")
    ApiResponse<EngineJobCheckpoint> getSavePoint(@Param("taskId") String taskId);

    @RequestLine("POST /node/streamTask/getByTaskIdAndEngineTaskId")
    ApiResponse<EngineJobCheckpoint> getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("engineTaskId") String engineTaskId);

    /**
     * 查询stream job
     */
    @RequestLine("POST /node/streamTask/getEngineStreamJob")
    ApiResponse<List<ScheduleJob>> getEngineStreamJob(@Param("taskIds") List<String> taskIds);

    /**
     * 查询stream job
     */
    @RequestLine("POST /node/streamTask/getEngineStreamJobNew")
    ApiResponse<List<EngineStreamJobVO>> getEngineStreamJobNew(@Param("taskIds") List<String> taskIds);

    /**
     * 获取某个状态的任务task_id
     */
    @RequestLine("POST /node/streamTask/getTaskIdsByStatus")
    ApiResponse<List<String>> getTaskIdsByStatus(@Param("status") Integer status);

    /**
     * 获取任务的状态
     */
    @RequestLine("POST /node/streamTask/getTaskStatus")
    ApiResponse<Integer> getTaskStatus(@Param("taskId") String taskId);

    /**
     * 获取实时计算运行中任务的日志URL
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/streamTask/getRunningTaskLogUrl")
    ApiResponse<List<String>> getRunningTaskLogUrl(@Param("taskId") String taskId);

    /**
     * 语法检测
     *
     * @param paramActionExt
     * @return
     */
    @RequestLine("POST /node/streamTask/grammarCheck")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<CheckResult> grammarCheck(ParamActionExt paramActionExt);


    /**
     * 获取流计算任务checkpoint size总大小
     * @param jobId
     * @return 单位bit
     */
    @Deprecated
    @RequestLine("POST /node/sdk/stream/totalSize")
    ApiResponse<Long> totalSize(@Param("jobId") String jobId);

    /**
     * 获取流处理任务运维列表
     *
     * @param queryParam
     * @return
     */
    @RequestLine("POST /node/sdk/stream/queryJobs")
    ApiResponse<PageResult<List<ScheduleStreamJobVO>>> queryJobs(StreamJobQueryParam queryParam);

    /**
     * 查询流处理任务运维列表状态统计
     *
     * @param queryParam
     * @return "ALL": 4,
     * "FAILED": 0,
     * "RUNNING": 1,
     * "CANCELED": 1,
     * "UNRUNNING": 2
     */
    @RequestLine("POST /node/sdk/stream/queryJobsStatusStatistics")
    ApiResponse<Map<String, Integer>> queryJobsStatusStatistics(StreamJobQueryParam queryParam);


    @RequestLine("POST /node/sdk/stream/executeSql")
    ApiResponse<CheckResult> executeSql(ParamActionExt paramActionExt);

    @RequestLine("POST /node/sdk/stream/executeFlinkSQL")
    ApiResponse<List<FlinkTableResult>> executeFlinkSQL(ParamActionExt paramActionExt);

    /**
     * 根据条件查询 checkpoint 集合
     *
     * @param checkpointQueryParam checkpoint 查询条件
     */
    @RequestLine("POST /node/streamTask/queryCheckPoint")
    ApiResponse<PageResult<List<CheckpointDetail>>> queryCheckPoint(CheckpointQueryParam checkpointQueryParam);

    /**
     * 获取流计算任务 checkpoint size 总大小
     * 废弃, 请使用 queryCheckpointTotalSize 接口
     * @param jobId 任务 id
     * @return 单位bit
     */
    @Deprecated
    @RequestLine("POST /node/streamTask/getCheckpointTotalSize")
    ApiResponse<Long> getCheckpointTotalSize(@Param("jobId") String jobId, @Param("componentVersion") String componentVersion);

    /**
     * 获取流计算任务 checkpoint size 总大小
     *
     * @param checkpointQueryParam
     */
    @RequestLine("POST /node/streamTask/queryCheckpointTotalSize")
    ApiResponse<Long> queryCheckpointTotalSize(CheckpointQueryParam checkpointQueryParam);

    /**
     * 获取 flink select 查询结果
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/sdk/stream/queryJobData")
    ApiResponse<FlinkQueryResultVO> queryJobData(@Param("jobId") String jobId);


    /**
     * 实时任务下线
     * @param taskIds
     */
    @RequestLine("POST /node/sdk/stream//offline")
    ApiResponse<OfflineReturnVO> offline(@Param("taskIds") List<String> taskIds);

    /**
     * 获取flink web url
     *
     */
    @RequestLine("POST /node/streamTask/getWebMonitorUrl")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<FlinkWebUrlVO> getWebMonitorUrl(@Param("taskId") String taskId);


    /**
     * 实时任务删除
     * @param taskIds
     */
    @RequestLine("POST /node/sdk/stream/deleteTask")
    ApiResponse<Integer> deleteTask(@Param("taskIds") List<String> taskIds);


}
