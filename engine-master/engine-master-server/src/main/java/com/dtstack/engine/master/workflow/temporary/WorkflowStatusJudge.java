package com.dtstack.engine.master.workflow.temporary;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.po.WorkflowSubTmpRunInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dtstack.engine.master.utils.SetUtils.hasIntersection;

/**
 * @author leon
 * @date 2023-04-12 13:53
 **/
public class WorkflowStatusJudge {

    private static final Set<Integer> RUNNING_STATUS = new HashSet<>(RdosTaskStatus.RUNNING_STATUS);

    private static final Set<Integer> STOPPED_STATUS = new HashSet<>(RdosTaskStatus.STOPPED_STATUS);

    private static final Set<Integer> FAILED_STATUS = new HashSet<>(RdosTaskStatus.FAILED_STATUS);

    public static RdosTaskStatus getJudgeStatus(List<Integer> subJobStatus,
                                          List<WorkflowSubTmpRunInfo> subTmpRunInfos) {

        Set<Integer> uniqueSubJobStatus = new HashSet<>(subJobStatus);
        // 包含运行中状态即工作流为运行中
        if (hasIntersection(RUNNING_STATUS, uniqueSubJobStatus)) {
            return RdosTaskStatus.RUNNING;
        }
        // 都是停止状态并且所有任务都执行完成了
        if (isAllSubJobDone(subJobStatus, subTmpRunInfos, uniqueSubJobStatus)) {
            // 有失败的任务即工作流为失败状态
            if (hasIntersection(FAILED_STATUS, uniqueSubJobStatus)) {
                return RdosTaskStatus.FAILED;
            }
            // 有取消的任务即工作流为取消状态
            if (uniqueSubJobStatus.contains(RdosTaskStatus.CANCELED.getStatus())
                            || uniqueSubJobStatus.contains(RdosTaskStatus.KILLED.getStatus())) {
                return RdosTaskStatus.CANCELED;
            }

            return RdosTaskStatus.FINISHED;
        }

        // 没有失败，但是包含取消，即工作流为取消状态
        if (uniqueSubJobStatus.contains(RdosTaskStatus.CANCELED.getStatus())) {
            return RdosTaskStatus.CANCELED;
        }

        return RdosTaskStatus.RUNNING;
    }

    private static boolean isAllSubJobDone(List<Integer> subJobStatus,
                                           List<WorkflowSubTmpRunInfo> subTmpRunInfos,
                                           Set<Integer> uniqueSubJobStatus) {
        // 过滤掉因上游任务失败或者条件分支导致没有执行的任务
        subTmpRunInfos =
                subTmpRunInfos.stream()
                        .filter(e -> e.getIsParentFail() != 1 && e.getIsSkip() != 1)
                        .collect(Collectors.toList());

        return STOPPED_STATUS.containsAll(uniqueSubJobStatus) &&
                subJobStatus.size() == subTmpRunInfos.size();
    }
}
