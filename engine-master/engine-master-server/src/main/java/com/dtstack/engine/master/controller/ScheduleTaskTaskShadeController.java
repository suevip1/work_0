package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/scheduleTaskTaskShade")
@Api(value = "/node/scheduleTaskTaskShade", tags = {"任务依赖接口"})
public class ScheduleTaskTaskShadeController {

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @RequestMapping(value="/clearDataByTaskId", method = {RequestMethod.POST})
    public void clearDataByTaskId(@RequestParam("taskId") Long taskId, @RequestParam("appType")Integer appType) {
        scheduleTaskTaskShadeService.clearDataByTaskId(taskId, appType);
    }

    @RequestMapping(value="/saveTaskTaskList", method = {RequestMethod.POST})
    public SaveTaskTaskVO saveTaskTaskList(@RequestParam("taskTask") String taskLists,@RequestParam("commitId") String commitId) {
        return scheduleTaskTaskShadeService.saveTaskTaskList(taskLists,commitId);
    }

    @RequestMapping(value="/getAllParentTask", method = {RequestMethod.POST})
    public List<ScheduleTaskTaskShade> getAllParentTask(@RequestParam("taskId") Long taskId,@RequestParam("appType")Integer appType) {
        return scheduleTaskTaskShadeService.getAllParentTask(taskId,appType);
    }

    @RequestMapping(value="/listChildTask", method = {RequestMethod.POST})
    public List<ScheduleTaskTaskShade> listChildTask(@RequestBody ScheduleTaskTaskShade taskTaskShade) {
        return scheduleTaskTaskShadeService.listChildTask(taskTaskShade.getParentTaskId(), taskTaskShade.getParentAppType());
    }

    @RequestMapping(value="/displayOffSpring", method = {RequestMethod.POST})
    public ScheduleTaskVO displayOffSpring(@RequestParam("taskId") Long taskId,
                                           @RequestParam("projectId") Long projectId,
                                           @RequestParam("level") Integer level,
                                           @RequestParam("type") Integer directType, @RequestParam("appType")Integer appType) {
        return scheduleTaskTaskShadeService.displayOffSpring(taskId, projectId, level, directType, appType);
    }

    @RequestMapping(value="/getAllFlowSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流全部节点信息 -- 依赖树")
    public ScheduleTaskVO getAllFlowSubTasks(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType) {
        return scheduleTaskTaskShadeService.getAllFlowSubTasks(taskId, appType);
    }


    @RequestMapping(value="/listGroupTask", method = {RequestMethod.POST})
    public ScheduleTaskVO listGroupTask(@RequestParam("taskId") Long taskId,
                                        @RequestParam("appType")Integer appType) {
        return scheduleTaskTaskShadeService.listGroupTask(taskId, appType);
    }
}
