package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.FindParentJobVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.job.JobViewVO;
import com.dtstack.engine.api.vo.job.ParentNodesVO;
import com.dtstack.engine.api.vo.job.RelyResultVO;
import com.dtstack.engine.api.vo.job.WorkflowJobViewReturnVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobJobService extends DtInsightServer {
    /**
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJobJob/displayOffSpring")
    ApiResponse<ScheduleJobVO> displayOffSpring(@Param("jobId") Long jobId,
                                                @Param("projectId") Long projectId,
                                                @Param("level") Integer level) ;
    /**
     * 为工作流节点展开子节点
     */
    @RequestLine("POST /node/scheduleJobJob/displayOffSpringWorkFlow")
    ApiResponse<ScheduleJobVO> displayOffSpringWorkFlow(@Param("jobId") Long jobId, @Param("appType") Integer appType) ;

    @RequestLine("POST /node/scheduleJobJob/displayForefathers")
    ApiResponse<ScheduleJobVO> displayForefathers(@Param("jobId") Long jobId, @Param("level") Integer level) ;

    /**
     * 用于紧急去依赖 获得上游节点列表
     * @param jobId 去依赖的job
     * @param taskName 模糊上游节点名称
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJobJob/parentNode")
    ApiResponse<ParentNodesVO> parentNode(@Param("jobId") String jobId,@Param("taskName")String taskName);

    /**
     * 去依赖接口
     *
     * @param relyJobKey        被去依赖的key
     * @param relyJobParentKeys 去依赖的key
     * @return
     */
    @RequestLine("POST /node/sdk/scheduleJobJob/removeRely")
    ApiResponse<RelyResultVO> removeRely(@Param("relyJobKey") String relyJobKey, @Param("relyJobParentKeys") List<String> relyJobParentKeys, @Param("operateId") Long operateId);

    @RequestLine("POST /node/sdk/scheduleJobJob/findParentJob")
    ApiResponse<FindParentJobVO> findParentJob(@Param("jobId") String jobId);

    @RequestLine("POST /node/sdk/scheduleJobJob/view")
    ApiResponse<JobViewVO> view(@Param("jobIds") List<String> jobIds, @Param("level") Integer level,
                                @Param("directType") Integer directType,@Param("showSelf") boolean showSelf);

    @RequestLine("POST /node/sdk/scheduleJobJob/workflowSubViewList")
    ApiResponse<WorkflowJobViewReturnVO> workflowSubViewList(@Param("jobId") String jobId);
}