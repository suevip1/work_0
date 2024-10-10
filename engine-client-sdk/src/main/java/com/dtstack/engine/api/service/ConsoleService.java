package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ConsoleService extends DtInsightServer {

    /**
     * 获得引擎的zk下的节点路径
     *
     * @return
     */
    // @RequestLine("POST /node/console/nodeAddress")
    // @Deprecated
    // ApiResponse<List<String>> nodeAddress();

    /**
     * 搜索任务，按照任务名搜索
     *
     * @param jobName
     * @return
     */
    // @RequestLine("POST /node/console/searchJob")
    // @Deprecated
    // ApiResponse<ConsoleJobVO> searchJob(@Param("jobName") String jobName);

    // @RequestLine("POST /node/console/listNames")
    // @Deprecated
    // ApiResponse<List<String>> listNames(@Param("jobName") String jobName);

    // @RequestLine("POST /node/console/jobResources")
    // @Deprecated
    // ApiResponse<List<String>> jobResources();

    /**
     * 根据计算引擎类型显示任务
     */
    // @RequestLine("POST /node/console/overview")
    // @Deprecated
    // ApiResponse<Collection<Map<String, Object>>> overview(@Param("nodeAddress") String nodeAddress, @Param("clusterName") String clusterName);

    // @RequestLine("POST /node/console/groupDetail")
    // @Deprecated
    // ApiResponse<PageResult> groupDetail(@Param("jobResource") String jobResource,
    //                                     @Param("nodeAddress") String nodeAddress,
    //                                     @Param("stage") Integer stage,
    //                                     @Param("pageSize") Integer pageSize,
    //                                     @Param("currentPage") Integer currentPage, @Param("dtToken") String dtToken);

    // @RequestLine("POST /node/console/jobStick")
    // @Deprecated
    // ApiResponse<Boolean> jobStick(@Param("jobId") String jobId);

    // @RequestLine("POST /node/console/stopJob")
    // @Deprecated
    // ApiResponse<Void> stopJob(@Param("jobId") String jobId) ;

    /**
     * 概览，杀死全部
     */
    // @RequestLine("POST /node/console/stopAll")
    // @Deprecated
    // ApiResponse<Void> stopAll(@Param("jobResource") String jobResource,
    //                     @Param("nodeAddress") String nodeAddress) ;

    // @RequestLine("POST /node/console/stopJobList")
    // @Deprecated
    // ApiResponse<Void> stopJobList(@Param("jobResource") String jobResource,
    //                         @Param("nodeAddress") String nodeAddress,
    //                         @Param("stage") Integer stage,
    //                         @Param("jobIdList") List<String> jobIdList) ;

    /**
     * 算法使用
     * @param clusterName
     * @return
     */
    @RequestLine("POST /node/console/clusterResources")
    ApiResponse<ClusterResource> clusterResources(@Param("clusterName") String clusterName);


}
