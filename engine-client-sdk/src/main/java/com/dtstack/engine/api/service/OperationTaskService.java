package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.task.TaskPageVO;
import com.dtstack.engine.api.vo.task.TaskReturnPageVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName OperationTaskService
 * @date 2022/7/19 3:49 PM
 */
public interface OperationTaskService extends DtInsightServer {

    @RequestLine("POST /node/sdk/operation/task/page")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<TaskReturnPageVO>>> page(@RequestBody TaskPageVO vo);

    @RequestLine("POST /node/sdk/operation/task/workflow/page")
    ApiResponse<List<TaskReturnPageVO>> workflowPage(@Param("taskId") Long taskId, @Param("appType") Integer appType);


}
