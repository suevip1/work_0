package com.dtstack.engine.dao;

import com.dtstack.engine.po.WorkflowSubTmpRunInfo;
import com.dtstack.engine.po.WorkflowTmpRunInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author leon
 * @date 2023-03-07 17:03
 **/
public interface WorkflowDao {

    Integer batchInsertWorkflowSubRunInfo(@Param("workflowSubTmpRunInfos") List<WorkflowSubTmpRunInfo> workflowSubTmpRunInfos);

    WorkflowTmpRunInfo getWorkflowTmpRunInfo(@Param("flowJobId") String flowJobId);

    WorkflowSubTmpRunInfo getWorkflowSubTmpRunInfoByFlowJobIdAndJobIdAndTaskId(@Param("flowJobId") String flowJobId,
                                                                               @Param("flowId") Long flowId,
                                                                               @Param("taskId") Long taskId);

    WorkflowSubTmpRunInfo getWorkflowSubTmpRunInfoByFlowJobIdAndJobId(@Param("flowJobId") String flowJobId, @Param("jobId") String jobId);

    List<WorkflowSubTmpRunInfo> getSimpleWorkflowSubTmpRunInfosByFlowJobId(@Param("flowJobId") String flowJobId);

    Integer deleteWorkflowSubRunInfoByFlowJobIdAndTaskIds(@Param("flowJobId")String flowJobId,@Param("taskIds") List<Long> taskIds);

    Integer insertWorkflowTmpRunInfo(WorkflowTmpRunInfo workflowTmpRunInfo);

    Integer updateWorkflowStatusAndErrorMsgByFlowJobId(@Param("flowJobId") String flowJobId,
                                                    @Param("status") Integer status,
                                                    @Param("errorMsg") String errorMsg);

    void updateSubJobParentFail(@Param("flowJobId") String flowJobId,
                                @Param("flowId") Long flowId,
                                @Param("taskId") Long taskId);

    void updateSubJobSkip(@Param("flowJobId") String flowJobId,
                          @Param("flowId") Long flowId,
                          @Param("taskId") Long taskId);

}
