package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.*;
import com.dtstack.engine.master.impl.BaselineJobService;
import com.dtstack.engine.master.impl.BaselineTaskService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 3:46 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/baseline/job")
@Api(value = "/node/sdk/baseline/job", tags = {"任务实例依赖接口"})
public class BaselineJobSdkController {

    @Autowired
    private BaselineJobService baselineJobService;

    @PostMapping(value = "/page")
    public PageResult<List<BaselineJobPageVO>> page(@RequestBody BaselineJobConditionVO vo) {
        return baselineJobService.page(vo);
    }

    @PostMapping(value = "/getBaselineJobInfo")
    public List<String> getBaselineJobInfo(@RequestParam("baselineJobId") Long baselineJobId) {
        return baselineJobService.getBaselineJobInfo(baselineJobId);
    }

    @PostMapping(value = "/baselineJobGraph")
    public List<BaselineViewVO> baselineJobGraph(@RequestParam("baselineTaskId") Long baselineTaskId) {
        return baselineJobService.baselineJobGraph(baselineTaskId);
    }

    @PostMapping(value = "/baselineBlockJob")
    public List<BaselineBlockJobRecordVO> baselineBlockJob(@RequestParam("baselineJobId") Long baselineJobId) {
        return baselineJobService.baselineBlockJob(baselineJobId);
    }

    @PostMapping(value = "/createBaselineJob")
    public void createBaselineJob(@RequestParam("baselineTaskId") Long baselineTaskId,@RequestParam("date") String date) {
        baselineJobService.createBaselineJob(baselineTaskId,date);
    }

    @PostMapping(value = "/createAllBaselineJob")
    public void createAllBaselineJob(@RequestParam("date") String date) {
        baselineJobService.createBaselineJob(null,date);
    }


}
