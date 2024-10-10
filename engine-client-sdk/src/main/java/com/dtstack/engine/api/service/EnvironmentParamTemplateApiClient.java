package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.api.vo.template.TaskTemplateVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;


public interface EnvironmentParamTemplateApiClient extends DtInsightServer {

    /**
     * 获取指定任务类型的任务参数
     *
     * @param version  任务版本 如1.8 1.10 1.12
     * @param taskType 任务类型 如SparkSQL 0
     * @param appType  子产品code
     * @return
     */
    @RequestLine("POST /node/sdk/environmentParam/getTaskEnvironmentParam")
    ApiResponse<EnvironmentParamTemplateVO> getTaskEnvironmentParam(@Param("version") String version,
                                                                    @Param("taskType") Integer taskType,
                                                                    @Param("appType") Integer appType);
}
