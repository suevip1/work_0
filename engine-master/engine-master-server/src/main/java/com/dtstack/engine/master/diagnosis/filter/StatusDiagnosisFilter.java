package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class StatusDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.STATUS_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        if (RdosTaskStatus.FROZEN.getStatus().equals(scheduleJob.getStatus())) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo("实例已冻结");
            jobDiagnosisInformation.setDiagnosisSuggest("解冻实例");
            return jobDiagnosisInformation;
        }
        ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (null == taskShade) {
            jobDiagnosisInformation.setDiagnosisStatusInfo("任务已删除或下线");
            jobDiagnosisInformation.setDiagnosisSuggest("");
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            return jobDiagnosisInformation;
        }

        if (RdosTaskStatus.STOP_STATUS.contains(scheduleJob.getStatus())) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo("实例已取消");
            jobDiagnosisInformation.setDiagnosisSuggest("检查上游是否存在取消任务，检查任务是否设置过期取消配置");
            return jobDiagnosisInformation;
        }

        jobDiagnosisInformation.setDiagnosisResult(isNotReach.test(preResult) ? DiagnosisResultEnum.NOT_REACH.getVal() : DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
        jobDiagnosisInformation.setDiagnosisStatusInfo(isNotReach.test(preResult) ? DiagnosisResultEnum.NOT_REACH.getName() : "实例状态正常");
        return jobDiagnosisInformation;
    }
}
