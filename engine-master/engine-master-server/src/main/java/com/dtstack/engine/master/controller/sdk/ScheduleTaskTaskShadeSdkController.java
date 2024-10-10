package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.task.TaskViewRetrunVO;
import com.dtstack.engine.api.vo.task.TaskViewVO;
import com.dtstack.engine.api.vo.task.WorkflowSubViewList;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 10:13 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleTaskTaskShade")
@Api(value = "/node/sdk/scheduleTaskTaskShade", tags = {"任务实例依赖接口"})
public class ScheduleTaskTaskShadeSdkController {

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @RequestMapping(value = "/view", method = {RequestMethod.POST})
    public TaskViewRetrunVO view(@RequestBody TaskViewVO taskViewVO) {
        return scheduleTaskTaskShadeService.view(taskViewVO);
    }

    @RequestMapping(value = "/workflowSubViewList", method = {RequestMethod.POST})
    public WorkflowSubViewList workflowSubViewList(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType) {
        return scheduleTaskTaskShadeService.workflowSubViewList(taskId,appType);
    }
}
