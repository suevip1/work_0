package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.BaselineBatchVO;
import com.dtstack.engine.api.vo.BaselineTaskBatchVO;
import com.dtstack.engine.api.vo.alert.*;
import com.dtstack.engine.master.impl.BaselineTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 5:42 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping({"/node/sdk/baseline/task"})
public class BaselineTaskSdkController {

    @Autowired
    private BaselineTaskService baselineTaskService;

    @PostMapping(value = "/page")
    public PageResult<List<BaselineTaskPageVO>> page(@RequestBody BaselineTaskConditionVO vo) {
        return baselineTaskService.page(vo);
    }

    @PostMapping(value = "/addOrUpdateBaselineTask")
    public Integer addOrUpdateBaselineTask(@RequestBody BaselineTaskVO baselineTaskVO) {
        return baselineTaskService.addOrUpdateBaselineTask(baselineTaskVO);
    }

    @PostMapping(value = "/updateBaselineOwner")
    public Integer updateBaselineOwner(@RequestParam("oldOwnerUserIds")List<Long> oldOwnerUserIds,
                                       @RequestParam("newOwnerUserId") Long newOwnerUserId) {
        return baselineTaskService.updateBaselineOwner(oldOwnerUserIds,newOwnerUserId);
    }

    @PostMapping(value = "/deleteBaselineTask")
    public Integer deleteBaselineTask(@RequestParam("id") Long id) {
        return baselineTaskService.deleteBaselineTask(id);
    }

    @PostMapping(value = "/deleteBaselineTaskByProject")
    public Integer deleteBaselineTaskByProject(@RequestParam("projectId") Long projectId,@RequestParam("appType")Integer appType) {
        return baselineTaskService.deleteBaselineTaskByProjectId(projectId, appType);
    }

    @PostMapping(value = "/openOrClose")
    public Integer openOrClose(@RequestParam("id") Long id, @RequestParam("openStatus") Integer openStatus) {
        return baselineTaskService.openOrClose(id, openStatus);
    }

    @PostMapping(value = "/getBaselineTaskDetails")
    public BaselineTaskVO getBaselineTaskDetails(@RequestParam("id") Long id) {
        return baselineTaskService.getBaselineTaskDetails(id);
    }

    @PostMapping(value = "/getBaselineTaskDetailsByIds")
    public List<BaselineTaskVO> getBaselineTaskDetailsByIds(@RequestParam("ids") List<Long> ids) {
        return baselineTaskService.getBaselineTaskDetailsByIds(ids);
    }

    @PostMapping(value = "/getBaselineTaskInfo")
    public List<AlarmChooseTaskVO> getBaselineTaskInfo(@RequestParam("id") Long id, @RequestParam("appType") Integer appType) {
        return baselineTaskService.getAlarmChooseTaskVOS(id, appType);
    }

    @PostMapping(value = "/estimatedFinish")
    public String estimatedFinish(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("appType") Integer appType) {
        return baselineTaskService.estimatedFinish(taskIds, appType);
    }

    @PostMapping(value = "/findBaselineInfoByTaskIds")
    public List<BaselineSimpleVO> findBaselineInfoByTaskIds(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("appType") Integer appType) {
        return baselineTaskService.findBaselineInfoByTaskIds(taskIds, appType);
    }

    @PostMapping(value = "/getBaselineBatch")
    public List<BaselineBatchVO> getBaselineBatch (@RequestBody BaselineTaskBatchVO vo) {
        return baselineTaskService.getBaselineBatch(vo);
    }

}
