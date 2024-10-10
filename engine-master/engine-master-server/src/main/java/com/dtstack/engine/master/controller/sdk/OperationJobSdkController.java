package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.SchedulePeriodVO;
import com.dtstack.engine.api.vo.job.JobPageVO;
import com.dtstack.engine.api.vo.job.JobReturnPageVO;
import com.dtstack.engine.api.vo.job.StatisticsJobVO;
import com.dtstack.engine.master.impl.OperationJobService;
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
 * @ClassName OperationJobController
 * @date 2022/7/19 2:40 PM
 */
@RestController
@RequestMapping("/node/sdk/operation/job")
@Api(value = "/node/sdk/operation/job")
public class OperationJobSdkController {

    @Autowired
    private OperationJobService operationJobService;

    @PostMapping(value = "/page")
    public PageResult<List<JobReturnPageVO>> page(@RequestBody JobPageVO vo) {
        return operationJobService.page(vo);
    }

    @PostMapping(value = "/queryJobsStatusStatistics")
    public List<StatisticsJobVO> queryJobsStatusStatistics(@RequestBody JobPageVO vo) {
        return operationJobService.queryJobsStatusStatistics(vo);
    }

    @PostMapping(value = "/displayPeriods")
    public List<SchedulePeriodVO> displayPeriods(@RequestParam("jobId") String jobId,
                                                 @RequestParam("directType") Integer directType,
                                                 @RequestParam("level") Integer level) {
        return operationJobService.displayPeriods(jobId,directType,level);
    }

    @Deprecated
    @PostMapping(value = "/workflow/page")
    public List<JobReturnPageVO> workflowPage(@RequestParam("jobId") String jobId) {
        JobPageVO pageVO = new JobPageVO();
        pageVO.setJobId(jobId);
        return operationJobService.workflowPage(pageVO);
    }


    @PostMapping(value = "/workflow/pageQuery")
    public List<JobReturnPageVO> workflowPage(@RequestBody JobPageVO vo) {
        return operationJobService.workflowPage(vo);
    }


}
