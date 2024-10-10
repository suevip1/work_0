package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.*;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 5:54 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlertAlarmService extends DtInsightServer {

    /**
     * 告警规则分页查询
     */
    @RequestLine("POST /node/sdk/alarm/page")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<PageResult<List<AlertAlarmPageVO>>> page(AlertAlarmPageConditionVO vo);

    /**
     * 添加或者修改规则
     */
    @RequestLine("POST /node/sdk/alarm/addOrUpdateAlertAlarm")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> addOrUpdateAlertAlarm(AlertAlarmVO alertAlarmVO);

    /**
     * 详情
     */
    @RequestLine("POST /node/sdk/alarm/getAlertAlarmDetails")
    ApiResponse<AlertAlarmVO> getAlertAlarmDetails(@Param("id")Long id);

    /**
     * 通过项目id和名称获取告警规则
     */
    @RequestLine("POST /node/sdk/alarm/getAlertAlarmByProjectIdAndName")
    ApiResponse<List<AlertAlarmVO>> getAlertAlarmByProjectIdAndName(@Param("projectId") Long projectId, @Param("name") String name);

    /**
     * 通过项目id和名称列表获取告警规则
     */
    @RequestLine("POST /node/sdk/alarm/listAlarmsByProjectIdAndNames")
    ApiResponse<List<AlertAlarmVO>> listAlarmsByProjectIdAndNames(@Param("projectId") Long projectId, @Param("names") List<String> names);

    /**
     * 获得所有规则
     */
    @RequestLine("POST /node/sdk/alarm/getAllRule")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<AlterRuleVO>> getAllRule(AlterRuleConditionVO alterRuleConditionVO);

    /**
     * 按照任务id查询告警规则
     */
    @RequestLine("POST /node/sdk/alarm/getAlertAlarmByTaskId")
    ApiResponse<List<AlertAlarmMapBusinessVO>> getAlertAlarmByTaskId(@Param("taskIds") List<Long> taskIds,@Param("businessType") Integer businessType, @Param("appType") Integer appType, @Param("projectId") Long projectId);

    /**
     * 修改规则状态也 ： 0 开始 1关闭
     */
    @RequestLine("POST /node/sdk/alarm/updateOpenStatus")
    ApiResponse<Integer> updateOpenStatus(@Param("id") Long id, @Param("openStatus") Integer openStatus);

    /**
     * 修改规则接口
     */
    @RequestLine("POST /node/sdk/alarm/updateAlarmRules")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Integer> updateAlarmRules(List<AlarmRuleVO> vos);

    /**
     * 删除规则
     */
    @RequestLine("POST /node/sdk/alarm/delete")
    ApiResponse<Integer> delete(@Param("id") Long id);

    @RequestLine("POST /node/sdk/alarm/deleteByProjectId")
    ApiResponse<Integer> deleteByProjectId(@Param("projectId") Long projectId);

    /**
     * 增加或删除规则
     */
    @RequestLine("POST /node/sdk/alarm/addOrDeleteAlarmRules")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> addOrDeleteAlarmRules(AlarmRuleAddOrDeleteVO alarmRuleAddOrDeleteVO);


    /**
     * 根据项目id，appType, scope 查询告警规则
     */
    @RequestLine(value = "POST /node/sdk/alarm/getAlarmByProjectIdAndScope")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<AlertAlarmScopeVO>> getAlarmByProjectIdAndScope(@Param("projectId") Long projectId,
                                                                     @Param("appType") Integer appType,
                                                                     @Param("scopes") List<Integer> scopes);
}
