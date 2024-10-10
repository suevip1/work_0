package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.param.CatalogParam;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.GrammarCheckParam;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.JobIdAndStatusVo;
import com.dtstack.engine.api.vo.JobLogVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.api.vo.action.ApplicationVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface ActionService extends DtInsightServer {
    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    @RequestLine("POST /node/action/start")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> start(ParamActionExt paramActionExt);

    /**
     * 任务热更新 仅支持实时 异步
     *
     * @param paramActionExt
     * @return
     */
    @RequestLine("POST /node/action/hotReloading")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Void> hotReloading(ParamActionExt paramActionExt);

    /**
     * 如在当前节点,则直接处理任务(包括预处理)
     *
     * @param paramTaskAction
     * @return
     */
    @RequestLine("POST /node/action/startJob")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> startJob(ParamTaskAction paramTaskAction);

    /**
     * 执行前预处理，逻辑
     *
     * @param paramActionExt
     * @return
     */
    @RequestLine("POST /node/action/paramActionExt")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<ParamActionExt> paramActionExt(ParamTaskAction paramActionExt);


    /**
     *
     * @param jobIds 任务id
     * @
     */
    @RequestLine("POST /node/action/stop")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> stop(@Param("jobIds") List<String> jobIds, @Param("userId") Long userId) ;

    /**
     *
     * @param jobIds 任务id
     * @param isForce 是否强制
     * @return
     */
    @RequestLine("POST /node/action/forceStop")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> stop(@Param("jobIds") List<String> jobIds,@Param("isForce") Integer isForce, @Param("userId")Long userId);

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    @RequestLine("POST /node/action/status")
    ApiResponse<Integer> status(@Param("jobId") String jobId, @Param("computeType") Integer computeType) ;

    /**
     * 根据jobid 和 计算类型，查询job的状态
     *
     * 接口已废弃， 替代接口 ---> POST /node/sdk/action/statusByJobIds
     *
     */
    // @Deprecated
    // @RequestLine("POST /node/action/statusByJobIds")
    // ApiResponse<Map<String, Integer>> statusByJobIds(@Param("jobIds") List<String> jobIds, @Param("computeType") Integer computeType) ;

    /**
     * 根据jobid 和 计算类型，查询job的状态 ， 封装成 JobIdAndStatusVo 对象集合
     *
     */
    @RequestLine("POST /node/sdk/action/statusByJobIds")
    ApiResponse<List<JobIdAndStatusVo>> statusByJobIdsBySdk(@Param("jobIds") List<String> jobIds, @Param("computeType") Integer computeType) ;


    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    @RequestLine("POST /node/action/startTime")
    ApiResponse<Long> startTime(@Param("jobId") String jobId, @Param("computeType") Integer computeType) ;

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    @RequestLine("POST /node/action/log")
    ApiResponse<ActionLogVO> log(@Param("jobId") String jobId, @Param("computeType") Integer computeType) ;

    /**
     * 根据jobid 和 计算类型，查询k8s调度下job的日志
     */
    @RequestLine("POST /node/action/logFromEs")
    ApiResponse<String> logFromEs(@Param("jobId") String jobId, @Param("computeType") Integer computeType);

    /**
     * 引擎提供统一的单个Job的log日志信息
     * @param jobId
     * @param pageInfo
     * @return
     */
    @RequestLine("POST /node/action/log/unite")
    ApiResponse<JobLogVO> logUnite(@Param("jobId") String jobId, @Param("pageInfo") Integer pageInfo);


    @RequestLine("POST /node/action/retryLog")
    ApiResponse<List<ActionRetryLogVO>> retryLog(@Param("jobId") String jobId, @Param("computeType") Integer computeType);

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    @RequestLine("POST /node/action/retryLog")
    ApiResponse<List<ActionRetryLogVO>> retryLog(@Param("jobId") String jobId, @Param("computeType") Integer computeType,@Param("expandId") Long expandId) ;

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    @RequestLine("POST /node/action/retryLogDetail")
    ApiResponse<ActionRetryLogVO> retryLogDetail(@Param("jobId") String jobId, @Param("computeType") Integer computeType, @Param("retryNum") Integer retryNum) ;

    /**
     * 根据jobids 和 计算类型，查询job
     */
    @RequestLine("POST /node/action/entitys")
    ApiResponse<List<ActionJobEntityVO>> entitys(@Param("jobIds") List<String> jobIds, @Param("computeType") Integer computeType) ;

    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    @RequestLine("POST /node/action/containerInfos")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<String>> containerInfos(ParamAction paramAction) ;


    /**
     * 重置任务状态为未提交
     * @return
     */
    @RequestLine("POST /node/action/resetTaskStatus")
    ApiResponse<String> resetTaskStatus(@Param("jobId") String jobId, @Param("computeType") Integer computeType);

    /**
     * task 工程使用
     */
    // @RequestLine("POST /node/action/listJobStatus")
    // @Deprecated
    // ApiResponse<List<ActionJobStatusVO>> listJobStatus(@Param("time") Long time,@Param("appType") Integer appType);

    /**
     * 备注：业务中心有用到
     * @param time
     * @param appType
     * @return
     */
    @RequestLine("POST /node/action/listJobStatusScheduleJob")
    ApiResponse<List<ScheduleJob>> listJobStatusScheduleJob(@Param("time") Long time, @Param("appType") Integer appType);

    @RequestLine("POST /node/action/listJobStatusByJobIds")
    ApiResponse<List<ActionJobStatusVO>> listJobStatusByJobIds(@Param("jobIds") List<String> jobIds) ;

    @RequestLine("POST /node/action/generateUniqueSign")
    ApiResponse<String> generateUniqueSign();


    /**
     * 保存 或 更新 job数据
     * jobId必传
     *
     * @param paramTaskAction
     * @return
     */
    @RequestLine("POST /node/action/addOrUpdateJob")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Boolean> addOrUpdateJob(ParamTaskAction paramTaskAction);

    /**
     * 获取yarn上运行任务
     *
     * @return
     */
    @RequestLine("POST /node/action/listApplication")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<ApplicationVO>> listApplication(@Param("uicTenantId") Long tenantId,@Param("jobIds") List<String> jobIds);

    /**
     * 刷新任务状态 仅支持流计算任务
     *
     * @param paramActionExt
     * @return
     */
    @RequestLine("POST /node/action/refreshStatus")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Boolean> refreshStatus(ParamActionExt paramActionExt);


    /**
     * 语法检测
     * sqlText 需要格式化
     *
     * @param grammarCheckParam
     * @return
     */
    @RequestLine("POST /node/sdk/action/grammarCheck")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<CheckResult> grammarCheck(GrammarCheckParam grammarCheckParam);


    /**
     * 执行catalog
     */
    @RequestLine("POST /node/sdk/action/executeCatalog")
    ApiResponse<String> executeCatalog(CatalogParam catalogParam);

    @RequestLine("POST /node/sdk/action/log")
    ApiResponse<ActionLogVO> expandLog(@Param("jobId") String jobId,@Param("num") Integer num);

}
