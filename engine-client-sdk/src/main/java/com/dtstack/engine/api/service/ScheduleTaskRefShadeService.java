package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.task.SaveTaskRefResultVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;


public interface ScheduleTaskRefShadeService extends DtInsightServer {

    @RequestLine("POST /node/scheduleTaskRefShade/saveTaskRefList")
    ApiResponse<SaveTaskRefResultVO> saveTaskRefList(@Param("taskRefListJsonStr") String taskRefListJsonStr, @Param("commitId") String commitId);

}
