package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.ScheduleTaskParamReplaceVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;

public interface ScheduleTaskParamService extends DtInsightServer {

    /**
     * 根据参数替换文本
     * @param scheduleTaskParamReplaceVO 参数替换规则
     * @param scheduleTaskParamReplaceVO 替换文本
     * @param scheduleTaskParamReplaceVO 任务调度时间
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleTaskParam/replace")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<String> replace(ScheduleTaskParamReplaceVO scheduleTaskParamReplaceVO);

    /**
     * 替换临时任务上游输入参数
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleTaskParam/replaceTempInputParam")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<String> replaceTempInputParam(ScheduleTaskParamReplaceVO scheduleTaskParamReplaceVO);
}