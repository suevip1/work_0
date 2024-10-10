package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.task.ScheduleTaskPriorityReturnVO;
import com.dtstack.engine.api.vo.task.ScheduleTaskPriorityVO;
import com.dtstack.engine.api.vo.task.StatisticsPriorityReturnVO;
import com.dtstack.engine.api.vo.task.StatisticsPriorityVO;
import com.dtstack.engine.master.impl.ScheduleTaskPriorityService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Auther: dazhi
 * @Date: 2021/10/29 10:32 上午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleTaskPriority")
@Api(value = "/node/sdk/scheduleTaskPriority", tags = {"任务优先级"})
public class ScheduleTaskPriorityController {

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    @PostMapping(value = "/findPriorityByTask")
    public ScheduleTaskPriorityReturnVO findPriorityByTask(@RequestBody ScheduleTaskPriorityVO vo) {
        return scheduleTaskPriorityService.findPriorityByTask(vo);
    }

    @PostMapping(value = "/statisticsPriorityByTask")
    public StatisticsPriorityReturnVO statisticsPriorityByTask(@RequestBody StatisticsPriorityVO vo) {
        return scheduleTaskPriorityService.statisticsPriorityByTask(vo);
    }


}
