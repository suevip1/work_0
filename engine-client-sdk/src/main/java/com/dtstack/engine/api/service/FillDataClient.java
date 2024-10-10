package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataJobListVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataListVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobImmediatelyVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateEnhanceVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateVO;
import com.dtstack.engine.api.vo.task.FillDataTaskVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/17 5:29 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface FillDataClient extends DtInsightServer {

    /**
     * 补数据
     */
    @RequestLine("POST /node/sdk/scheduleJob/fillData")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Long> fillData(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO);


    /**
     * 手动任务
     */
    @RequestLine("POST /node/sdk/scheduleJob/manualTask")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Long> manualTask(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO);


    /**
     * 新版补数据
     * @param scheduleFillJobParticipateEnhanceVO 入参vo
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/enhance/fillData")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Long> enhanceFillData(ScheduleFillJobParticipateEnhanceVO scheduleFillJobParticipateEnhanceVO);

    /**
     * 补数据列表
     *
     * @param vo
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/fillDataList")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<ScheduleFillDataJobPreViewVO>>> fillDataList(FillDataListVO vo);

    /**
     * 补数据详情列表
     *
     * @param vo
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJob/fillDataJobList")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<ScheduleFillDataJobDetailVO>> fillDataJobList(FillDataJobListVO vo);


    @RequestLine("POST /node/sdk/scheduleJob/createFillDataTaskList")
    ApiResponse<FillDataTaskVO>  createFillDataTaskList(@Param("taskId") Long taskId,
                                                 @Param("appType") Integer appType,
                                                 @Param("level") Integer level);


    @RequestLine("POST /node/sdk/scheduleJob/immediately/fillData")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<String>  immediatelyFillJob(ScheduleFillJobImmediatelyVO scheduleFillJobImmediatelyVO);
}
