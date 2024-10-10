package com.dtstack.engine.master.workflow.temporary;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.WorkflowDao;
import com.dtstack.engine.master.workflow.temporary.model.Workflow;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.dtstack.engine.po.WorkflowTmpRunInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工作流临时运行的 DB 操作
 *
 * @author leon
 * @date 2023-03-10 16:59
 **/
@Component
public class WorkflowDBOperator {

    private final WorkflowDao workflowDao;

    public WorkflowDBOperator(WorkflowDao workflowDao) {
        this.workflowDao = workflowDao;
    }

    public void updateWorkflowStatus(Workflow workflow, RdosTaskStatus status, String errorMessage) {
        String flowJobId = Optional.ofNullable(workflow).map(Workflow::getFlowJobId).orElse(null);
        if (StringUtils.isBlank(flowJobId) || Objects.isNull(status)) {
            return;
        }
        updateWorkflowStatus(flowJobId, status, errorMessage);
    }

    public void updateWorkflowStatus(String flowJobId, RdosTaskStatus status) {
        updateWorkflowStatus(flowJobId, status, "");
    }

    public void updateWorkflowStatus(String flowJobId, RdosTaskStatus status, String errorMessage) {
        if (Objects.isNull(status)) {
            return;
        }
        workflowDao.updateWorkflowStatusAndErrorMsgByFlowJobId(flowJobId, status.getStatus(), errorMessage);
    }

    public boolean isWorkflowExistInDB(String flowJobId) {
        return Objects.nonNull(workflowDao.getWorkflowTmpRunInfo(flowJobId));
    }

    public RdosTaskStatus getWorkflowStatus(String flowJobId) {
        WorkflowTmpRunInfo workFlowRunInfo = workflowDao.getWorkflowTmpRunInfo(flowJobId);
        return Optional.of(
                Optional.ofNullable(workFlowRunInfo)
                        .map(WorkflowTmpRunInfo::getStatus)
                        .map(RdosTaskStatus::getTaskStatus)
                        .orElse(RdosTaskStatus.NOTFOUND)).orElse(RdosTaskStatus.NOTFOUND);
    }

    public ParamTaskAction getParamTaskAction(String flowJobId, Long flowId, Long taskId) {
        WorkflowSubTmpRunInfo subTmpRunInfo = workflowDao.getWorkflowSubTmpRunInfoByFlowJobIdAndJobIdAndTaskId(flowJobId, flowId, taskId);
        ParamTaskAction paramTaskAction = Optional.ofNullable(subTmpRunInfo)
                .map(WorkflowSubTmpRunInfo::getParamTaskAction)
                .map(e -> JSONObject.parseObject(e, ParamTaskAction.class))
                .orElse(null);

        if (Objects.nonNull(paramTaskAction)) {
            paramTaskAction.setJobId(subTmpRunInfo.getJobId());
            paramTaskAction.setFlowJobId(subTmpRunInfo.getFlowJobId());
        }
        return paramTaskAction;
    }

    public List<String> getSubJobIds(String flowJobId) {
        List<WorkflowSubTmpRunInfo> subTmpRunInfos = getSubTmpRunInfos(flowJobId);
        return subTmpRunInfos.stream().map(WorkflowSubTmpRunInfo::getJobId).collect(Collectors.toList());
    }

    public List<WorkflowSubTmpRunInfo> getSubTmpRunInfos(String flowJobId) {
        if (StringUtils.isBlank(flowJobId)) {
            return Lists.newArrayList();
        }
        return workflowDao.getSimpleWorkflowSubTmpRunInfosByFlowJobId(flowJobId);
    }

    public WorkflowSubTmpRunInfo getSubTmpRunInfo(String flowJobId, Long flowId, Long taskId) {
        return workflowDao.getWorkflowSubTmpRunInfoByFlowJobIdAndJobIdAndTaskId(flowJobId, flowId, taskId);
    }

    public void updateSubJobParentFail(String flowJobId, Long flowId, Long taskId) {
        workflowDao.updateSubJobParentFail(flowJobId, flowId, taskId);
    }

    public void updateSubJobSkip(String flowJobId, Long flowId, Long taskId) {
        workflowDao.updateSubJobSkip(flowJobId, flowId, taskId);
    }
}
