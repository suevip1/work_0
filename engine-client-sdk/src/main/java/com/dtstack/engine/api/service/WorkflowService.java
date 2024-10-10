package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.workflow.RunWorkflowParamVO;
import com.dtstack.engine.api.vo.workflow.RunWorkflowResultVO;
import com.dtstack.engine.api.vo.workflow.WorkflowSubTaskActionParamVO;
import com.dtstack.engine.api.vo.workflow.WorkflowTempRunStatusVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * 工作流临时运行接口
 *
 * @author leon
 * @date 2023-03-07 15:53
 **/
public interface WorkflowService extends DtInsightServer {


    /**
     * 提交工作流临时运行子任务执行参数
     * @param vo
     */
    @RequestLine("POST /node/sdk/workflow/saveWorkflowSubTaskActionParam")
    ApiResponse<Void> saveWorkflowSubTaskActionParam(WorkflowSubTaskActionParamVO vo);


    /**
     * 触发工作流临时运行
     * @param param
     * @return
     */
    @RequestLine("POST /node/sdk/workflow/runWorkflow")
    ApiResponse<RunWorkflowResultVO> runWorkflow(RunWorkflowParamVO param);

    /**
     * 获取工作流临时运行状态
     * @param flowJobId 工作流临时运行的 jobId，标识一次运行操作
     * @return
     */
    @RequestLine("POST /node/sdk/workflow/getStatus")
    ApiResponse<WorkflowTempRunStatusVO> getStatus(@Param("flowJobId") String flowJobId);


    /**
     * 停止工作流临时运行
     * @param flowJobId 工作流临时运行的 jobId，标识一次运行操作
     */
    @RequestLine("POST /node/sdk/workflow/stop")
    ApiResponse<Void> stop(@Param("flowJobId") String flowJobId,@Param("userId") Long userId);

}
