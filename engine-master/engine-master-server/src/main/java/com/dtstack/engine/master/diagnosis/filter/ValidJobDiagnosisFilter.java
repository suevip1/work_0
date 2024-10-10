package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.bo.DiagnosisContent;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.utils.TimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class ValidJobDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.VALID_JOB_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        String jobKey = scheduleJob.getJobKey();
        // 查询当前任务的所有父任务的运行状态
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(jobKey, RelyTypeEnum.NORMAL.getType());
        if (CollectionUtils.isNotEmpty(scheduleJobJobs)) {
            List<String> jobKeys = scheduleJobJobs.stream()
                    .map(ScheduleJobJob::getJobKey)
                    .collect(Collectors.toList());
            List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(jobKeys);
            List<ScheduleJob> jobs = scheduleJobs.stream()
                    .filter(job -> (TaskRuleEnum.STRONG_RULE.getCode().equals(job.getTaskRule()) || TaskRuleEnum.WEAK_RULE.getCode().equals(job.getTaskRule()))
                            && !job.getJobKey().equals(scheduleJob.getJobKey()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(jobs)) {
                List<ScheduleJob> validJob = jobs.stream()
                        .filter(j -> AppType.DQ.getType().equals(j.getAppType()) || AppType.DATAASSETS.getType().equals(j.getAppType()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(validJob)) {
                    if (validJob.stream().anyMatch(job -> RdosTaskStatus.UNSUBMIT_STATUS.contains(job.getStatus()))) {
                        //上游任务 失败 下游任务等待提交
                        jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
                        jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
                    } else {
                        jobDiagnosisInformation.setDiagnosisStatusInfo("质量校验成功");
                        jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
                    }
                    List<DiagnosisContent> diagnosisContents = new ArrayList<>();
                    for (ScheduleJob validScheduleJob : validJob) {
                        if (RdosTaskStatus.RUNNING.getStatus().equals(validScheduleJob.getStatus())) {
                            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.UNDER_DIAGNOSIS.getVal());
                            jobDiagnosisInformation.setDiagnosisStatusInfo("质量校验中");
                            String timeDiff = TimeUtils.formatTimeDiff(validScheduleJob.getExecStartTime(), DateTime.now().toDate());
                            diagnosisContents.add(new DiagnosisContent("已用时", timeDiff));
                        } else if (RdosTaskStatus.FAILED_STATUS.contains(validScheduleJob.getStatus()) &&
                                TaskRuleEnum.STRONG_RULE.getCode().equals(validScheduleJob.getTaskRule())) {
                            diagnosisContents.add(new DiagnosisContent("失败实例", validScheduleJob.getJobId()));
                            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                            jobDiagnosisInformation.setDiagnosisStatusInfo("数据质量校验失败，触发强规则");
                            jobDiagnosisInformation.setDiagnosisSuggest("检查实例代码或相关表数据并重跑");
                        }
                        if (null == jobDiagnosisInformation.getDiagnosisTime() && null != validScheduleJob.getExecStartTime()) {
                            jobDiagnosisInformation.setDiagnosisTime(validScheduleJob.getExecStartTime());
                        }
                    }
                    jobDiagnosisInformation.setContents(diagnosisContents);
                    return jobDiagnosisInformation;

                }
            }
        }
        return getNotContainsInformation();
    }
}
