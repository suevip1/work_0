package com.dtstack.engine.master.diagnosis;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author yuebai
 * @date 2022/7/14
 */
public interface JobDiagnosisFilter {

    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    Predicate<DiagnosisResultEnum> isNotReach = result -> DiagnosisResultEnum.DIAGNOSIS_FAIL.equals(result) || DiagnosisResultEnum.NOT_REACH.equals(result)
            || DiagnosisResultEnum.UNDER_DIAGNOSIS.equals(result);

    CheckFinish checkFinish = (information, job, msg, callBack) -> {
        if (RdosTaskStatus.FINISH_STATUS.contains(job.getStatus())) {
            information.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
            information.setDiagnosisStatusInfo(msg);
        } else {
            callBack.accept(information);
        }
        return information;
    };

    DiagnosisEnum order();

    JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult);

    @FunctionalInterface
    interface CheckFinish {
        JobDiagnosisInformation check(JobDiagnosisInformation jobDiagnosisInformation, ScheduleJob scheduleJob, String finishMsg, Consumer<JobDiagnosisInformation> callBack);
    }

    default JobDiagnosisInformation getNotContainsInformation() {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_CONTAINS.getVal());
        jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_CONTAINS.getName());
        return jobDiagnosisInformation;
    }
}

