package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordConditionVO;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordPageVO;
import com.dtstack.engine.api.vo.alert.CountAlarmVO;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 5:57 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlertTriggerRecordService extends DtInsightServer {

    /**
     * 分页查询告警记录
     */
    @RequestLine("POST /node/sdk/alert/trigger/record/page")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<PageResult<List<AlertTriggerRecordPageVO>>> page(AlertTriggerRecordConditionVO vo);

    /**
     * 统计告警记录
     * 已弃用 替代接口 @see com.dtstack.engine.api.service.AlertTriggerRecordService#countAlarm(com.dtstack.engine.api.vo.project.ProjectVO)
     */
    // @Deprecated
    // @RequestLine("POST /node/sdk/alert/trigger/record/countAlarm")
    // ApiResponse<CountAlarmVO> countAlarm(@Param("projectId") Long projectId);

    /**
     * 统计告警记录
     */
    @RequestLine("POST /node/sdk/alert/trigger/record/countAlarm")
    ApiResponse<CountAlarmVO> countAlarm(ProjectVO projectVO);



}
