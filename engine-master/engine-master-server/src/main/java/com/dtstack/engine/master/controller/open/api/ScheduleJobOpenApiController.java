package com.dtstack.engine.master.controller.open.api;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.open.api.EventJobResponse;
import com.dtstack.engine.api.open.api.EventJobTrigger;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.enums.EventStatusType;
import com.dtstack.engine.master.impl.ScheduleFillDataJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/node/openapi/job")
@Api(value = "/node/openapi/job", tags = {"任务实例接口"})
public class ScheduleJobOpenApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobOpenApiController.class);


    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleFillDataJobService scheduleFillDataJobService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    private static final Integer FAILED = 2;
    private static final Integer SUCCESS = 1;

    private static final String logTemplate = "外部任务：%s\n" +
            "\n" +
            "状态：%s(%s)";

    @ApiOperation(value = "事件任务触发")
    @PostMapping(value = "/trigger")
    public EventJobResponse trigger(@RequestBody EventJobTrigger eventJobTrigger, @RequestHeader("token") String token) throws Exception {

        EventJobResponse response = new EventJobResponse();

        if (StringUtils.isBlank(token)) {
            return responseFail(response, String.format("token %s is empty", token));
        }

        if (StringUtils.isBlank(eventJobTrigger.getEventJobId())) {
            return responseFail(response, "eventJobId param is empty");
        }

        if (StringUtils.isBlank(eventJobTrigger.getTriggerJobTime())) {
            return responseFail(response, "trigger job time param is empty");
        }


        long taskId = Long.parseLong(eventJobTrigger.getEventJobId());
        Integer appType = eventJobTrigger.getAppType() == null ? AppType.RDOS.getType() : eventJobTrigger.getAppType();

        ScheduleJob dagEventJob = null;
        boolean isTemp = "000000000000".equals(eventJobTrigger.getTriggerJobTime());
        if (isTemp) {
            //触发临时运行 可能没有task 信息提交
            dagEventJob = scheduleJobService.getNewestJob(taskId, appType, EScheduleType.TEMP_JOB.getType(), RdosTaskStatus.RUNNING.getStatus());
        } else {
            try {
                DateUtil.getTimestamp(eventJobTrigger.getTriggerJobTime(), DateUtil.MINUTE_FORMAT);
            } catch (Exception e) {
                response.setEventJobStatus(FAILED);
                response.setEventJobStatusDesc(String.format("trigger job time param is not %s format : error msg %s", eventJobTrigger.getTriggerJobTime(), ExceptionUtil.getErrorMessage(e)));
                return response;
            }
            if (StringUtils.isNotBlank(eventJobTrigger.getManuallyTriggerBatchName())) {
                //手动任务 会有多个 根据手动名称区分
                ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(taskId, appType);
                if (null == taskShade) {
                    return responseFail(response, String.format("can not find manual task %s  %s ", taskId, appType));
                }
                Long fillId = scheduleFillDataJobService.getByName(eventJobTrigger.getManuallyTriggerBatchName().trim(), taskShade.getProjectId());
                dagEventJob = scheduleJobService.getJobRangeByCycTimeLimitOne(taskId, appType, true, eventJobTrigger.getTriggerJobTime() + "00",
                        EScheduleType.MANUAL.getType(), fillId);
            } else {
                //调度任务
                dagEventJob = scheduleJobService.getJobRangeByCycTimeLimitOne(taskId, appType, true, eventJobTrigger.getTriggerJobTime() + "00",
                        EScheduleType.NORMAL_SCHEDULE.getType(), null);
            }

        }

        if (dagEventJob == null) {
            return responseFail(response, String.format("can not find task %s appType %s ", taskId, appType));
        }

        JSONObject sqlText = scheduleJobService.getSqlText(isTemp, taskId, appType, dagEventJob.getJobId());

        if (sqlText == null) {
            return responseFail(response, String.format("can not find task %s sql text appType %s ", taskId, appType));
        }
        //校验token是否和数据库一致
        String taskToken = sqlText.getString(GlobalConst.TOKEN);
        if (!token.equals(taskToken)) {
            responseFail(response, "token is invalid");
            return response;
        }

        response.setEventJobStatus(SUCCESS);
        response.setEventJobId(eventJobTrigger.getEventJobId());

        if (RdosTaskStatus.RUNNING.getStatus().equals(dagEventJob.getStatus())) {
            //运行中 接受触发
            String triggerLog;
            //触发成功
            RdosTaskStatus dagStatus = null;
            if (EventStatusType.SUCCESS.getStatus() == eventJobTrigger.getTriggerJobStatus()) {
                triggerLog = String.format(logTemplate, eventJobTrigger.getTriggerJob(), eventJobTrigger.getTriggerJobStatus(), "成功");
                dagStatus = RdosTaskStatus.FINISHED;
            } else {
                triggerLog = String.format(logTemplate, eventJobTrigger.getTriggerJob(), eventJobTrigger.getTriggerJobStatus(), "失败");
                dagStatus = RdosTaskStatus.FAILED;

            }
            scheduleJobService.updateLogAndFinish(dagEventJob.getJobId(), triggerLog, dagStatus.getStatus());
            response.setEventJobStatusDesc("trigger success,job change to finish");
            LOGGER.info("event job {} trigger success by trigger job {} time {}", dagEventJob.getJobId(), eventJobTrigger.getTriggerJob(), eventJobTrigger.getTriggerJobTime());
        } else {
            //等待提交 需要等数栈上游任务运行完成 放入operator表
            scheduleJobService.insertIgnoreOperator(dagEventJob.getJobId(), OperatorType.EVENT, "");
            response.setEventJobStatusDesc("trigger success,wait parent job finish");
            LOGGER.info("event job {} trigger success by trigger job {} time {}, parent job not finish put into operator ", dagEventJob.getJobId(), eventJobTrigger.getTriggerJob(), eventJobTrigger.getTriggerJobTime());
        }

        return response;
    }

    private EventJobResponse responseFail(EventJobResponse response, String errorMsg) {
        response.setEventJobStatus(FAILED);
        response.setEventJobStatusDesc(errorMsg);
        return response;
    }
}
