package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.OperatorParam;
import com.dtstack.engine.api.vo.ScheduleJobOperateVO;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 2:57 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleJobOperate")
@Api(value = "/node/sdk/scheduleJobOperate", tags = {"任务实例依赖接口"})
public class ScheduleJobOperateSdkController {

    @Autowired
    private ScheduleJobOperateService scheduleJobOperateService;

    @ApiOperation("获取告警通道分页 用于取代console接口: /api/console/service/alert/page")
    @PostMapping("/page")
    public PageResult<List<ScheduleJobOperateVO>> page(@RequestBody OperatorParam pageParam) {
        return scheduleJobOperateService.page(pageParam);
    }

}
