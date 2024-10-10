package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.BaselineBatchVO;
import com.dtstack.engine.api.vo.BaselineTaskBatchVO;
import com.dtstack.engine.api.vo.alert.*;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 5:47 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface BaselineTaskService extends DtInsightServer {

    /**
     * 基线任务列表
     */
    @RequestLine("POST /node/sdk/baseline/task/page")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<BaselineTaskPageVO>>> page(BaselineTaskConditionVO vo);

    /**
     * 新增或者修改基线任务
     */
    @RequestLine("POST /node/sdk/baseline/task/addOrUpdateBaselineTask")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Integer> addOrUpdateAlertAlarm(BaselineTaskVO baselineTaskVO);


    /**
     * 新增或者修改基线任务
     */
    @RequestLine("POST /node/sdk/baseline/task/updateBaselineOwner")
    ApiResponse<Integer> updateBaselineOwner(@Param("oldOwnerUserIds") List<Long> oldOwnerUserIds,
                                             @Param("newOwnerUserId") Long newOwnerUserId);

    /**
     * 删除基线任务
     */
    @RequestLine("POST /node/sdk/baseline/task/deleteBaselineTask")
    ApiResponse<Integer> deleteBaselineTask(@Param("id") Long id);

    @RequestLine("POST /node/sdk/baseline/task/deleteBaselineTaskByProject")
    ApiResponse<Integer> deleteBaselineTaskByProject(@Param("projectId") Long projectId,@Param("appType")Integer appType);
    /**
     * 设置基线开关： 0 开始，1关闭
     */
    @RequestLine("POST /node/sdk/baseline/task/openOrClose")
    ApiResponse<Integer> openOrClose(@Param("id") Long id, @Param("openStatus") Integer openStatus);

    /**
     * 基线详情
     */
    @RequestLine("POST /node/sdk/baseline/task/getBaselineTaskDetails")
    ApiResponse<BaselineTaskVO> getBaselineTaskDetails (@Param("id") Long id);

    /**
     * 批量查询基线详情 （不包括任务）
     */
    @RequestLine("POST /node/sdk/baseline/task/getBaselineTaskDetailsByIds")
    ApiResponse<List<BaselineTaskVO>> getBaselineTaskDetailsByIds(@Param("ids") List<Long> ids);

    /**
     * 获得基线下所有的任务
     */
    @RequestLine("POST /node/sdk/baseline/task/getBaselineTaskInfo")
    ApiResponse<List<AlarmChooseTaskVO>> getBaselineTaskInfo(@Param("id") Long id, @Param("appType") Integer appType);

    /**
     * 计算基线任务预计完成时间
     */
    @RequestLine("POST /node/sdk/baseline/task/estimatedFinish")
    ApiResponse<String> estimatedFinish(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    /**
     * 通过任务id查询基线简单信息
     */
    @RequestLine("POST /node/sdk/baseline/task/findBaselineInfoByTaskIds")
    ApiResponse<List<BaselineSimpleVO>> findBaselineInfoByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    @RequestLine("POST /node/sdk/baseline/task/getBaselineBatch")
    ApiResponse<List<BaselineBatchVO>> getBaselineBatch (BaselineTaskBatchVO vo);
}
