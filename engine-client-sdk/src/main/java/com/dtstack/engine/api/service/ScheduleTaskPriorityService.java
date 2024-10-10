package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.task.ScheduleTaskPriorityReturnVO;
import com.dtstack.engine.api.vo.task.ScheduleTaskPriorityVO;
import com.dtstack.engine.api.vo.task.StatisticsPriorityReturnVO;
import com.dtstack.engine.api.vo.task.StatisticsPriorityVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther: dazhi
 * @Date: 2023-07-04 14:24
 * @Email: dazhi@dtstack.com
 * @Description: ScheduleTaskPriorityService
 */
public interface ScheduleTaskPriorityService extends DtInsightServer {

    /**
     * 通过任务id获取优先级
     * @param vo 任务信息
     * @return 优先级
     */
    @RequestLine("POST /node/sdk/scheduleTaskPriority/findPriorityByTask")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<ScheduleTaskPriorityReturnVO> findPriorityByTask(@RequestBody ScheduleTaskPriorityVO vo);


    @RequestLine("POST /node/sdk/scheduleTaskPriority/statisticsPriorityByTask")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<StatisticsPriorityReturnVO> statisticsPriorityByTask(@RequestBody StatisticsPriorityVO vo);
}
