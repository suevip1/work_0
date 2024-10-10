package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.workflow.RunWorkflowParamVO;
import com.dtstack.engine.api.vo.workflow.RunWorkflowResultVO;
import com.dtstack.engine.api.vo.workflow.SubTaskActionParamVO;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.dtstack.engine.po.WorkflowTmpRunInfo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author leon
 * @date 2023-03-08 16:14
 **/
@Mapper(componentModel = "spring")
public interface WorkflowStruct {

    WorkflowSubTmpRunInfo toEntity(SubTaskActionParamVO param);

    List<WorkflowSubTmpRunInfo> toEntities(List<SubTaskActionParamVO> params);

    WorkflowTmpRunInfo toEntity(RunWorkflowParamVO param);

    List<WorkflowTmpRunInfo> toWorkflowTmpRunInfoEntities(List<RunWorkflowParamVO> params);

    RunWorkflowResultVO.SubJobInfo toSubJobInfo(WorkflowSubTmpRunInfo workflowSubTmpRunInfo);

    List<RunWorkflowResultVO.SubJobInfo> toSubJobInfos(List<WorkflowSubTmpRunInfo> simpleSubTmpRunInfos);
}
