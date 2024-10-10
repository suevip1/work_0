package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ConsoleSdkParam;
import com.dtstack.engine.api.vo.console.param.ConsoleCalenderTimeShowVO;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface ConsoleParamService extends DtInsightServer {

    /**
     * 查询参数列表
     *
     * @return
     */
    @RequestLine("POST /node/sdk/param/getAllParam")
    ApiResponse<PageResult<List<ConsoleParamVO>>> getAllParam(ConsoleSdkParam consoleSdkParam);

    /**
     * 根据名称查询参数
     *
     * @param consoleSdkParam#paramName 参数名称
     * @return
     */
    @RequestLine("POST /node/sdk/param/queryByName")
    ApiResponse<ConsoleParamVO> queryByName(ConsoleSdkParam consoleSdkParam);

    /**
     * 根据id查询参数
     *
     * @param consoleSdkParam#paramId 参数id
     * @return
     */
    @RequestLine("POST /node/sdk/param/findById")
    ApiResponse<ConsoleParamVO> findById(ConsoleSdkParam consoleSdkParam);

    /**
     * 根据参数 id 查询自定义日历
     * @param consoleSdkParam#paramId
     * @return
     */
    @RequestLine("POST /node/sdk/param/showCalenderTime")
    ApiResponse<ConsoleCalenderTimeShowVO> showCalenderTime(ConsoleSdkParam consoleSdkParam);
}
