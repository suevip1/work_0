package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import dt.insight.plat.lang.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class JobSubmitDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.JOB_SUBMIT_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        if (RdosTaskStatus.SUBMITFAILD.getStatus().equals(scheduleJob.getStatus())) {
            ScheduleJobExpand expand = scheduleJobExpandDao.getLogByJobId(scheduleJob.getJobId());
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("实例提交失败\n" +
                    "\n" +
                    "失败原因：{}\n" +
                    "\n", expand.getLogInfo()));
        }
        if (RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo("实例提交成功");
            return jobDiagnosisInformation;
        }
        checkFinish.check(jobDiagnosisInformation, scheduleJob, "实例提交成功", (information -> {
            if (isNotReach.test(preResult)) {
                jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
            } else if (RdosTaskStatus.SUBMITFAILD.getStatus().equals(scheduleJob.getStatus())) {
                jobDiagnosisInformation.setDiagnosisStatusInfo("实例提交失败");
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            } else if (RdosTaskStatus.UNSUBMIT.getStatus().equals(scheduleJob.getStatus())) {
                jobDiagnosisInformation.setDiagnosisStatusInfo("实例正在提交");
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
            } else {
                jobDiagnosisInformation.setDiagnosisStatusInfo("实例提交成功");
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
            }
        }));
        return jobDiagnosisInformation;
    }
}
