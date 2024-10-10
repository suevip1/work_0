package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.ScheduleConfUtils;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.bo.DiagnosisContent;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.utils.TimeUtils;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import dt.insight.plat.lang.base.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class RunningJobDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.RUNNING_JOB_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        if (RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
            String timeDiff = TimeUtils.formatTimeDiff(scheduleJob.getExecStartTime(), DateTime.now().toDate());
            jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("运行中\n" +
                    "\n" +
                    "已用时：{}", timeDiff));
        }
        if (EScheduleJobType.SPARK_SQL.getType().equals(scheduleJob.getTaskType())) {
            ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());

            if (null == taskShade) {
                jobDiagnosisInformation.setDiagnosisStatusInfo("任务已删除或下线");
                jobDiagnosisInformation.setDiagnosisSuggest("");
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                return jobDiagnosisInformation;
            }
            //获取spark 在线信息
            JobIdentifier jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getJobId(),
                    scheduleJob.getDtuicTenantId(), EngineType.getEngineType(taskShade.getEngineType()).getEngineName(),
                    EDeployMode.PERJOB.getType(),
                    taskShade.getOwnerUserId(), taskShade.getResourceId(), null, taskShade.getComponentVersion());
            RdosTaskStatus taskStatus = RdosTaskStatus.getTaskStatus(scheduleJob.getStatus());
            try {
                String jobMetricsAnalysis = enginePluginsOperator.getJobMetricsAnalysis(jobIdentifier, taskStatus, scheduleJob.getExecStartTime(), scheduleJob.getExecEndTime());
                jobDiagnosisInformation.setMetricsResult(jobMetricsAnalysis);
            } catch (Exception e) {
                LOGGER.error("diagnosis {} error", scheduleJob.getJobId(), e);
            }
        }
        checkFinish.check(jobDiagnosisInformation, scheduleJob, "运行成功", (information -> {
            if (isNotReach.test(preResult)) {
                jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
            } else if (RdosTaskStatus.EXPIRE.getStatus().equals(scheduleJob.getStatus())) {
                dealTimeoutJob(scheduleJob, jobDiagnosisInformation);
            } else if (RdosTaskStatus.FAILED_STATUS.contains(scheduleJob.getStatus())) {
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo("运行失败");
                jobDiagnosisInformation.setDiagnosisSuggest("检查实例日志并重跑");
            } else if (RdosTaskStatus.RUNNING_STATUS.contains(scheduleJob.getStatus())) {
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.UNDER_DIAGNOSIS.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo("实例运行中");
            } else {
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
            }
        }));
        return jobDiagnosisInformation;
    }

    private void dealTimeoutJob(ScheduleJob scheduleJob, JobDiagnosisInformation jobDiagnosisInformation) {
        ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        String timeoutStr = ScheduleConfUtils.getRunTimeoutStr(taskShade.getScheduleConf());
        if (StringUtils.isBlank(timeoutStr)) {
            return;
        }
        List<DiagnosisContent> diagnosisContents = new ArrayList<>();
        jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
        jobDiagnosisInformation.setDiagnosisStatusInfo("运行超时");
        diagnosisContents.add(new DiagnosisContent("原因", String.format("实例运行时间超出任务设置的超时时间：%s", timeoutStr)));
        jobDiagnosisInformation.setContents(diagnosisContents);
    }
}
