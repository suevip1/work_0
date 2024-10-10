package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.workflow.RunWorkflowParamVO;
import com.dtstack.engine.api.vo.workflow.RunWorkflowResultVO;
import com.dtstack.engine.api.vo.workflow.SubTaskActionParamVO;
import com.dtstack.engine.api.vo.workflow.WorkflowSubTaskActionParamVO;
import com.dtstack.engine.api.vo.workflow.WorkflowTempRunStatusVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.WorkflowService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

/**
 * 工作流临时运行接口
 *
 * @author leon
 * @date 2023-03-07 15:20
 **/
@RestController
@RequestMapping("/node/sdk/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 提交工作流临时运行子任务执行参数
     * @param vo
     */
    @RequestMapping(value = "/saveWorkflowSubTaskActionParam", method = {RequestMethod.POST})
    public void saveWorkflowSubTaskActionParam(@RequestBody WorkflowSubTaskActionParamVO vo) {
        checkParams(vo);
        workflowService.saveWorkflowSubTaskActionParam(vo);
    }

    /**
     * 触发工作流临时运行
     * @param param
     * @return
     */
    @RequestMapping(value = "/runWorkflow")
    public RunWorkflowResultVO runWorkflow(@RequestBody RunWorkflowParamVO param) {
        checkParams(param);
        return workflowService.runWorkFlow(param);
    }


    /**
     * 获取工作流临时运行状态
     * @param flowJobId
     * @return
     */
    @RequestMapping(value = "getStatus")
    public WorkflowTempRunStatusVO getStatus(@RequestParam("flowJobId") String flowJobId) throws Exception {
        if (StringUtils.isBlank(flowJobId)) {
            throw new RdosDefineException("flowJobId can not be null");
        }
        return workflowService.getStatus(flowJobId);
    }

    /**
     * 停止工作流临时运行
     * @param flowJobId
     */
    @RequestMapping("stop")
    public void stop(@RequestParam("flowJobId") String flowJobId,@RequestParam("userId") Long userId) {
        if (StringUtils.isBlank(flowJobId)) {
            throw new RdosDefineException("flowJobId can not be null");
        }
        workflowService.stop(flowJobId, userId);
    }


    private void checkParams(WorkflowSubTaskActionParamVO vo) {
        if (StringUtils.isBlank(vo.getFlowJobId())) {
            throw new RdosDefineException("flowJobId can not be null");
        }
        if (CollectionUtils.isEmpty(vo.getSubTaskActionParams())) {
            throw new RdosDefineException("subTaskActionParam can not be null");
        }
        Optional<SubTaskActionParamVO> any
                = vo.getSubTaskActionParams().stream()
                .filter(e -> Objects.isNull(e.getTaskId()) || Objects.isNull(e.getFlowId()) || Objects.isNull(e.getAppType()))
                .findAny();

        if (any.isPresent()) {
            throw new RdosDefineException("taskId or flowId or appType can not be null");
        }
    }


    private void checkParams(RunWorkflowParamVO param) {
        if (StringUtils.isBlank(param.getFlowJobId())) {
            throw new RdosDefineException("flowJobId can not be null");
        }

        if (Objects.isNull(param.getFlowId())) {
            throw new RdosDefineException("flowId can not be null");
        }

        if (Objects.isNull(param.getAppType())) {
            throw new RdosDefineException("appType can not be null");
        }
    }

}
