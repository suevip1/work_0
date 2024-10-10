package com.dtstack.engine.master.controller.sdk;

import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.vo.ScheduleTaskParamReplaceVO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/node/sdk/scheduleTaskParam")
@Api(value = "/node/sdk/scheduleTaskParam", tags = {"任务参数"})
public class ScheduleTaskParamSdkController {

    @Resource
    private JobParamReplace jobParamReplace;

    @Autowired
    private ParamService paramService;

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private JobChainParamHandler jobChainParamHandler;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @PostMapping(value = "/replace")
    public String replace(@RequestBody ScheduleTaskParamReplaceVO scheduleTaskParamReplaceVO) {
        if (CollectionUtils.isEmpty(scheduleTaskParamReplaceVO.getTaskParamShades()) || StringUtils.isEmpty(scheduleTaskParamReplaceVO.getCycTime())
                || StringUtils.isEmpty(scheduleTaskParamReplaceVO.getReplaceText())) {
            return scheduleTaskParamReplaceVO.getReplaceText();
        }
        List<ScheduleTaskParamShade> taskParamShades = scheduleTaskParamReplaceVO.getTaskParamShades();
        List<ScheduleTaskParamShade> totalTaskParamShade = new ArrayList<>();

        if (!CollectionUtils.isEmpty(taskParamShades)) {
            paramService.convertGlobalToParamType(taskParamShades, (globalTaskParams, selfTaskParams) -> {
                List<ScheduleTaskParamShade> globalTaskShades = paramStruct.BOtoTaskParams(globalTaskParams);
                totalTaskParamShade.addAll(globalTaskShades);
                totalTaskParamShade.addAll(selfTaskParams);
            });
        }

        // 系统参数加进去替换
        List<ConsoleParam> systemParams = paramService.selectSysParam();
        List<ScheduleTaskParamShade> scheduleTaskParamShades = paramStruct.toTaskParams(systemParams);
        totalTaskParamShade.addAll(scheduleTaskParamShades);


        Integer scheduleType = scheduleTaskParamReplaceVO.getScheduleType();
        ScheduleTaskShade taskShade = scheduleTaskParamReplaceVO.getTaskShade();
        Long projectId = (taskShade != null) ? taskShade.getProjectId() : null;

        Timestamp runtime = null;
        String jobId = scheduleTaskParamReplaceVO.getJobId();
        if (Objects.nonNull(jobId)) {
            ScheduleJob job = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
            String cycTime = job.getCycTime();
            Timestamp cyctimeStamp = new Timestamp(Long.parseLong(cycTime));
            // 优先取 execstart time
            // 用来做 ${bdp.system.runtime} 的参数替换
            runtime = Optional.of(job).map(ScheduleJob::getExecStartTime).orElse(cyctimeStamp);
        }

        return jobParamReplace.paramReplace(scheduleTaskParamReplaceVO.getReplaceText(), totalTaskParamShade, scheduleTaskParamReplaceVO.getCycTime(), scheduleType, runtime, projectId);
    }

    /**
     * 替换临时任务上游输入参数
     */
    @PostMapping(value = "/replaceTempInputParam")
    public String replaceTempInputParam(@RequestBody ScheduleTaskParamReplaceVO scheduleTaskParamReplaceVO) {
        if (CollectionUtils.isEmpty(scheduleTaskParamReplaceVO.getTaskParamShades()) || StringUtils.isEmpty(scheduleTaskParamReplaceVO.getCycTime())
                || StringUtils.isEmpty(scheduleTaskParamReplaceVO.getReplaceText())) {
            return scheduleTaskParamReplaceVO.getReplaceText();
        }
        ScheduleJob tempInputJob = new ScheduleJob();
        tempInputJob.setType(EScheduleType.TEMP_JOB.getType());
        tempInputJob.setCycTime(scheduleTaskParamReplaceVO.getCycTime());
        tempInputJob.setJobId("-1");

        JobChainParamHandleResult handleResult = jobChainParamHandler.handle(scheduleTaskParamReplaceVO.getReplaceText(),
                scheduleTaskParamReplaceVO.getTaskShade(),
                scheduleTaskParamReplaceVO.getTaskParamShades(),
                tempInputJob
        );
        return handleResult.getSql();
    }
}