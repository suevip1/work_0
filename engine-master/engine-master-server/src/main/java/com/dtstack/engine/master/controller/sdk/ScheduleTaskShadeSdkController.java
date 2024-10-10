package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.param.QueryTaskParam;
import com.dtstack.engine.api.param.TaskInfoChangeParam;
import com.dtstack.engine.api.vo.DependencyTaskVo;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.FindTaskVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.utils.CheckParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/10/29 10:32 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleTaskShade")
@Api(value = "/node/sdk/scheduleTaskShade", tags = {"任务接口"})
public class ScheduleTaskShadeSdkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskShadeSdkController.class);

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @ApiOperation(value = "模糊查询任务")
    @PostMapping(value = "/findTask")
    public List<ScheduleTaskShadeTypeVO> findTask(@RequestBody FindTaskVO findTaskVO) {
        return scheduleTaskShadeService.findTask(findTaskVO);
    }



    @PostMapping(value = "/deleteTaskByTaskType")
    @ApiOperation(value = "根据任务类型删除任务")
    public void deleteTaskByTaskType(@RequestParam("taskId") Long taskId, @RequestParam("modifyUserId") long modifyUserId, @RequestParam("appType") Integer appType, @RequestParam("taskType") Integer taskType) {
        scheduleTaskShadeService.deleteTask(taskId, modifyUserId, appType,taskType);
    }

    @PostMapping(value = "/batchUpdateSchedule")
    @ApiOperation(value = "批量修改任务调度属性")
    public void batchUpdateSchedule(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("modifyUserId") long modifyUserId, @RequestParam("appType") Integer appType, @RequestParam("scheduleConf") String scheduleConf) {
       if(CollectionUtils.isEmpty(taskIds)){
           return;
       }
        CheckParamUtils.checkAppType(appType);
        int periodType;
        try {
            ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleConf, null, null, null);
            periodType = scheduleCron.getPeriodType();
        } catch (Exception e) {
            LOGGER.error("batchUpdateSchedule update {} error",scheduleConf,e);
            throw new RdosDefineException(e);
        }
        scheduleTaskShadeService.batchUpdateScheduleConf(taskIds,modifyUserId,appType,scheduleConf,periodType);
    }

    @ApiOperation(value = "按条件查询任务")
    @RequestMapping(value = "/queryTasksByCondition", method = {RequestMethod.POST})
    public ScheduleTaskShadePageVO queryTasksByCondition(@RequestBody FindTaskVO findTaskVO) {
        return scheduleTaskShadeService.queryTasksByCondition(findTaskVO);
    }



    @RequestMapping(value = "/offline",method = {RequestMethod.POST})
    public OfflineReturnVO offline(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("appType") Integer appType) {
        if (CollectionUtils.isEmpty(taskIds) || appType == null) {
            OfflineReturnVO offlineReturnVO = new OfflineReturnVO();
            offlineReturnVO.setSuccess(Boolean.FALSE);
            offlineReturnVO.setMsg("taskIds or appType must pass");
            return offlineReturnVO;
        }

        return scheduleTaskShadeService.offline(taskIds,appType);
    }


    @ApiOperation(value = "按条件查询依赖任务")
    @RequestMapping(value = "/listDependencyTask", method = {RequestMethod.POST})
    public List<DependencyTaskVo> listDependencyTask(@RequestParam("taskIds") List<Long> taskId, @RequestParam("appType") Integer appType, @RequestParam("name") String name, @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listDependencyTaskToVo(taskId, name, projectId);
    }

    @ApiOperation(value = "按条件查询不在taskId里的依赖任务")
    @RequestMapping(value = "/listByTaskIdsNotIn", method = {RequestMethod.POST})
    public List<DependencyTaskVo> listByTaskIdsNotIn(@RequestParam("taskIds") List<Long> taskId, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listByTaskIdsNotInToVo(taskId, appType, projectId);
    }

    @PostMapping(value = "/queryTasks")
    public ScheduleTaskShadePageVO queryTasks(@RequestBody QueryTaskParam queryTaskParam) {
        return scheduleTaskShadeService.queryTasks(null,queryTaskParam.getDtuicTenantId(),queryTaskParam.getProjectId(),
                queryTaskParam.getName(), queryTaskParam.getOwnerId(), queryTaskParam.getStartTime(),queryTaskParam.getEndTime()
        ,queryTaskParam.getScheduleStatus(),queryTaskParam.getTaskTypeList(),queryTaskParam.getPeriodTypeList(),queryTaskParam.getCurrentPage(),queryTaskParam.getPageSize()
        ,queryTaskParam.getSearchType(),queryTaskParam.getAppType(),queryTaskParam.getResourceIds());
    }

    @PostMapping(value = "/queryTasksByNames")
    public List<ScheduleTaskShade> queryTasksByNames(@RequestParam("taskNames") List<String> taskNames,
                                                     @RequestParam("appType") Integer appType,
                                                     @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.queryTasksByNames(taskNames, appType, projectId);
    }


    @PostMapping(value = "/updateTaskOwner")
    @ApiOperation(value = "修改任务责任人")
    public void updateTaskOwner(@RequestBody TaskInfoChangeParam taskInfoChangeParam) {
        scheduleTaskShadeService.updateTaskOwner(taskInfoChangeParam.getTaskIdList(), taskInfoChangeParam.getOwnerUserId(), taskInfoChangeParam.getAppType());
    }

    @PostMapping(value = "/deleteTag")
    @ApiOperation(value = "删除标签")
    public Integer deleteTag(Long tagId,Integer appType) {
        return scheduleTaskShadeService.deleteTag(tagId,appType);
    }

    @PostMapping(value = "/selectTagByTask")
    @ApiOperation(value = "通过任务有多少标签")
    public List<Long> selectTagByTask(Long taskId,Integer appType) {
        return scheduleTaskShadeService.selectTagByTask(taskId,appType);
    }

}
