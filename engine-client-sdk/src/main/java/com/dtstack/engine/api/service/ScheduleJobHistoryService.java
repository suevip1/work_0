package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.schedule.job.ScheduleJobHistoryVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-23
 */
public interface ScheduleJobHistoryService extends DtInsightServer {
    /**
     * 获取任务运行的历史 applicationId （目前仅保存stream）
     *
     * @param jobId     任务jobId
     * @param limitSize 返回数量大小 默认为100 最大不超过100
     * @return
     */
    @RequestLine("POST /node/sdk/history/queryJobHistory")
    ApiResponse<List<ScheduleJobHistoryVO>> queryJobHistory(@Param("jobId") String jobId, @Param("limitSize") Integer limitSize);
}
