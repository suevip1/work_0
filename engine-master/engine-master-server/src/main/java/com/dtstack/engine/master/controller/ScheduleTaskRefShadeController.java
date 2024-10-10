package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.vo.task.SaveTaskRefResultVO;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/scheduleTaskRefShade")
@Api(value = "/node/scheduleTaskRefShade", tags = {"任务引用接口"})
public class ScheduleTaskRefShadeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskRefShadeController.class);

    @Autowired
    private ScheduleTaskRefShadeService scheduleTaskRefShadeService;


    @RequestMapping(value = "/saveTaskRefList", method = {RequestMethod.POST})
    public SaveTaskRefResultVO saveTaskRefList(@RequestParam("taskRefListJsonStr") String taskRefListJsonStr, @RequestParam("commitId") String commitId) {
        SaveTaskRefResultVO saveTaskRefResultVO;
        try {
            saveTaskRefResultVO = scheduleTaskRefShadeService.saveTaskRefList(taskRefListJsonStr, commitId);
        } catch (Throwable e) {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("commitId:{}, saveTaskRefList fail:{}", commitId, errorMessage, e);
            return SaveTaskRefResultVO.fail(errorMessage);
        }

        return saveTaskRefResultVO;
    }
}
