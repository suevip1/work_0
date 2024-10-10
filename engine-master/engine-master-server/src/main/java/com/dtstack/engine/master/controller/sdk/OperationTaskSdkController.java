package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.task.TaskPageVO;
import com.dtstack.engine.api.vo.task.TaskReturnPageVO;
import com.dtstack.engine.common.enums.ETaskGroupEnum;
import com.dtstack.engine.master.impl.OperationTaskService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @className OperationTaskController
 * @date 2022/7/18 4:45 PM
 */
@RestController
@RequestMapping("/node/sdk/operation/task")
@Api(value = "/node/sdk/operation/task")
public class OperationTaskSdkController {

    @Autowired
    private OperationTaskService operationTaskService;

    @PostMapping(value = "/page")
    public PageResult<List<TaskReturnPageVO>> page(@RequestBody TaskPageVO vo) {
        if (null == vo.getTaskGroup()) {
            vo.setTaskGroup(ETaskGroupEnum.NORMAL_SCHEDULE.getType());
        }
        return operationTaskService.page(vo);
    }

    @PostMapping(value = "/workflow/page")
    public List<TaskReturnPageVO> workflowPage(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType) {
        return operationTaskService.workflowPage(taskId, appType);
    }
}
