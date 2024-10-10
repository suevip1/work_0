package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class CycTimeJobDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.CYC_TIME_JOB_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        String cycTime = scheduleJob.getCycTime();
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        if (EScheduleType.FILL_DATA.getType().equals(scheduleJob.getType())) {
            jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("补数据可直接运行，不需要等待起调时间：{}", formatTime(cycTime)));
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
            return jobDiagnosisInformation;
        }

        Pair<String, String> cycTimeLimitEndNow = jobRichOperator.getCycTimeLimitEndNow(false);
        if (null == cycTime) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo("调度任务计划时间为空");
            return jobDiagnosisInformation;
        }
        if (null == cycTimeLimitEndNow) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo("调度范围为空");
            return jobDiagnosisInformation;
        }

        // 补数据任务不检查起调时间
        if (EScheduleType.FILL_DATA.getType().equals(scheduleJob.getType())) {
            return getNotContainsInformation();
        }

        String startTime = cycTimeLimitEndNow.getLeft();
        String endTime = cycTimeLimitEndNow.getRight();
        if (NumberUtils.toLong(startTime) > NumberUtils.toLong(cycTime) && RdosTaskStatus.UNSUBMIT.getStatus().equals(scheduleJob.getStatus()) &&
                !EScheduleType.FILL_DATA.getType().equals(scheduleJob.getType())) {
            //补数据 重跑 无起调时间限制
            ScheduleJobOperatorRecord restart = scheduleJobOperatorRecordDao.getOperatorByTypeAndJobId(scheduleJob.getJobId(), OperatorType.RESTART.getType());
            if (null == restart) {
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("起调时间: {}早于调度轮训起始时间: {}", formatTime(cycTime), formatTime(startTime)));
                return jobDiagnosisInformation;
            }
        }

        if (NumberUtils.toLong(endTime) < NumberUtils.toLong(cycTime)) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("未达起调时间：{}", formatTime(cycTime)));
            return jobDiagnosisInformation;
        }
        jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("到达起调时间：{}", formatTime(cycTime)));
        jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
        return jobDiagnosisInformation;
    }

    private String formatTime(String time) {
        LocalDateTime parse = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return df.format(parse);
    }
}
