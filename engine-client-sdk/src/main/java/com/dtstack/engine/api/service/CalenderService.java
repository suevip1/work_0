package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CalenderParam;
import com.dtstack.engine.api.vo.calender.ConsoleCalenderVO;
import com.dtstack.engine.api.vo.calender.TaskCalenderVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface CalenderService extends DtInsightServer {

    /**
     * 查询调度日历列表
     *
     * @return
     */
    @RequestLine("POST /node/sdk/calender/getAllCalender")
    ApiResponse<PageResult<List<ConsoleCalenderVO>>> getAllCalender(CalenderParam calenderParam);


    /**
     * 调度日历预览
     *
     * @param calenderParam#calenderId 日历id
     * @param calenderParam#limit      查询条数
     * @param calenderParam#afterTime  查询对应时间之后数据  格式 yyyyMMddHHmm
     * @return
     */
    @RequestLine("POST /node/sdk/calender/overview")
    ApiResponse<ConsoleCalenderVO> overview(CalenderParam calenderParam);


    /**
     * 根据名称查询调度日历
     *
     * @param calenderParam#calenderName 日历名称
     * @return
     */
    @RequestLine("POST /node/sdk/calender/queryByName")
    ApiResponse<ConsoleCalenderVO> queryByName(CalenderParam calenderParam);

    /**
     * 根据名称查询调度日历
     *
     * @param calenderParam#taskIds 日历名称
     * @param calenderParam#appType
     * @return
     */
    @RequestLine("POST /node/sdk/calender/queryTaskCalender")
    ApiResponse<List<TaskCalenderVO>> queryTaskCalender(CalenderParam calenderParam);

    /**
     * 根据id查询调度日历
     *
     * @param calenderParam#calenderId 日历id
     * @return
     */
    @RequestLine("POST /node/sdk/calender/findById")
    ApiResponse<ConsoleCalenderVO> findById(CalenderParam calenderParam);

    /**
     * 校验自定义调度日历的多批次时间文本是否符合格式
     */
    @RequestLine("POST /node/sdk/calender/checkIntervalTimesCondition")
    ApiResponse<Boolean> checkIntervalTimesCondition(@Param("expandTime") String expandTime);
}
