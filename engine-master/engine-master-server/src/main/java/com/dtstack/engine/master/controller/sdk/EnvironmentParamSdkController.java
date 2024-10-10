package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.template.EnvironmentParamTemplateVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.EnvironmentParamTemplateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/sdk/environmentParam")
public class EnvironmentParamSdkController {

    @Autowired
    private EnvironmentParamTemplateService environmentParamTemplateService;

    @RequestMapping(value = "/getTaskEnvironmentParam", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务环境参数")
    public EnvironmentParamTemplateVO getTaskEnvironmentParam(@RequestParam("version") String version, @RequestParam("taskType") Integer taskType, @RequestParam("appType") Integer appType) {
        if (null == taskType) {
            throw new RdosDefineException(ErrorCode.TASK_TYPE_NOT_NULL);
        }
        if (null == appType) {
            throw new RdosDefineException(ErrorCode.APP_TYPE_NOT_NULL);
        }
        return environmentParamTemplateService.getTaskEnvironmentParam(version, taskType, appType);
    }
}
