package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.FindParentJobVO;
import com.dtstack.engine.api.vo.job.JobViewVO;
import com.dtstack.engine.api.vo.job.ParentNodesVO;
import com.dtstack.engine.api.vo.job.RelyResultVO;
import com.dtstack.engine.api.vo.job.WorkflowJobViewReturnVO;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/8 1:52 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleJobJob")
@Api(value = "/node/sdk/scheduleJobJob", tags = {"任务实例依赖接口"})
public class ScheduleJobJobSdkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobJobSdkController.class);

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @RequestMapping(value = "/parentNode", method = {RequestMethod.POST})
    public ParentNodesVO parentNode(@RequestParam("jobId")String jobId,
                                    @RequestParam("taskName") String taskName) {
        return scheduleJobJobService.parentNode(jobId, taskName);
    }

    @RequestMapping(value = "/removeRely", method = {RequestMethod.POST})
    public RelyResultVO removeRely(@RequestParam("relyJobKey") String relyJobKey,
                                   @RequestParam("relyJobParentKeys") List<String> relyJobParentKeys,
                                   @RequestParam("operateId") Long operateId) {
        return scheduleJobJobService.removeRely(relyJobKey, relyJobParentKeys,operateId);
    }

    @RequestMapping(value = "/findParentJob", method = {RequestMethod.POST})
    public FindParentJobVO findParentJob(@RequestParam("jobId") String jobId) {
        return scheduleJobJobService.findParentJob(jobId);
    }

    @RequestMapping(value = "/view", method = {RequestMethod.POST})
    public JobViewVO view( @RequestParam("jobIds") List<String> jobIds,@RequestParam("level") Integer level,
                           @RequestParam("directType")  Integer directType,@RequestParam("showSelf") boolean showSelf) {
        return scheduleJobJobService.view(jobIds,level,directType,showSelf);
    }

    @RequestMapping(value = "/workflowSubViewList", method = {RequestMethod.POST})
    public WorkflowJobViewReturnVO workflowSubViewList(@RequestParam("jobId") String jobId) {
        return scheduleJobJobService.workflowSubViewList(jobId);
    }


}
