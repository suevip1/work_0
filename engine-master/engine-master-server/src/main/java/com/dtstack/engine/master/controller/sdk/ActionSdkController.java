package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.GrammarCheckParam;
import com.dtstack.engine.api.param.CatalogParam;
import com.dtstack.engine.api.vo.JobIdAndStatusVo;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.utils.JobClientUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther: shuxing
 * @date: 2022/3/15 10:48 周二
 * @email: shuxing@dtstack.com
 * @description:
 */
@RestController
@RequestMapping({"/node/sdk/action"})
public class ActionSdkController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @RequestMapping(value = "/statusByJobIds", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "查询的所有job的jobId值", required = true, dataType = "String", allowMultiple = true),
    })
    public List<JobIdAndStatusVo> statusByJobIds(@RequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.statusByJobIdsToVo(jobIds);
    }

    @PostMapping(value="/grammarCheck")
    @ApiOperation(value = "语法检测")
    @ApiImplicitParams({
            @ApiImplicitParam(name="grammarCheckParam",value="语法检测相关参数信息",required=true, paramType="body", dataType = "ParamActionExt")
    })
    public CheckResult grammarCheck(@RequestBody GrammarCheckParam grammarCheckParam) {
        CheckResult checkResult = null;
        try {
            JobClient jobClient = JobClientUtil.convert2JobClient(grammarCheckParam);
            checkResult = enginePluginsOperator.grammarCheck(jobClient);
        } catch (Exception e) {
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return checkResult;
    }


    @PostMapping(value = "/executeCatalog")
    @ApiOperation(value = "执行ddl")
    public String executeCatalog(@RequestBody CatalogParam catalogParam) throws Exception {
        return actionService.executeCatalog(catalogParam);
    }

    @PostMapping(value = "/log")
    @ApiOperation(value = "查询单个Job的log日志")
    public ActionLogVO expandLog(@RequestParam("jobId") String jobId,@RequestParam("num") Integer num) throws Exception {
        return actionService.expandLog(jobId, num);
    }

}
