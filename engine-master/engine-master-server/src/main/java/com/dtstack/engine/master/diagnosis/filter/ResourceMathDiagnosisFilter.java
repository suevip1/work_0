package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.bo.DiagnosisContent;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.google.common.collect.Lists;
import dt.insight.plat.lang.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class ResourceMathDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.RESOURCE_MATH_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        EngineJobCache cache = engineJobCacheDao.getOne(scheduleJob.getJobId());
        // 需要先判断任务状态
        if (RdosTaskStatus.isStopped(scheduleJob.getStatus()) || null == cache) {
            checkFinish.check(jobDiagnosisInformation, scheduleJob, "实例资源匹配成功", (information -> {
                if (isNotReach.test(preResult)) {
                    jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
                    jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
                } else if (RdosTaskStatus.FAILED_STATUS.contains(scheduleJob.getStatus())) {
                    jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
                    jobDiagnosisInformation.setDiagnosisStatusInfo("实例资源匹配成功");
                } else if (RdosTaskStatus.EXPIRE.getStatus().equals(scheduleJob.getStatus())) {
                    information.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
                    information.setDiagnosisStatusInfo("实例资源匹配成功");
                } else {
                    jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
                    jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
                }
            }));
            return jobDiagnosisInformation;
        } else {
            return getJobDiagnosisInformation(jobDiagnosisInformation, cache);
        }
    }

    private JobDiagnosisInformation getJobDiagnosisInformation(JobDiagnosisInformation jobDiagnosisInformation, EngineJobCache cache) {
        EJobCacheStage stage = EJobCacheStage.getStage(cache.getStage());
        List<DiagnosisContent> diagnosisContents = new ArrayList<>();
        switch (stage) {
            case SUBMITTED:
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo("实例提交成功");
                return jobDiagnosisInformation;
            case DB:
            case PRIORITY:
                List<Integer> unSubmitStage = Arrays.stream(EJobCacheStage.values()).filter(eJobCacheStage -> EJobCacheStage.SUBMITTED != eJobCacheStage)
                        .map(EJobCacheStage::getStage).collect(Collectors.toList());
                int lessByStage = engineJobCacheDao.countLessByStage(cache.getId(), cache.getJobResource(), environmentContext.getLocalAddress()
                        , unSubmitStage);
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo("资源不足");
                jobDiagnosisInformation.setDiagnosisSuggest("可杀死部分低优先级任务保障高优先级任务尽快运行");
                diagnosisContents.add(new DiagnosisContent("等待中实例数", lessByStage + ""));
                jobDiagnosisInformation.setContents(diagnosisContents);
                return jobDiagnosisInformation;
            case RESTART:
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo(Strings.format("任务重试中"));
                return jobDiagnosisInformation;
            case LACKING:
                jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                jobDiagnosisInformation.setDiagnosisStatusInfo("资源不足");
                jobDiagnosisInformation.setDiagnosisSuggest("可杀死部分低优先级任务保障高优先级任务尽快运行或调整集群资源");
                int lackStage = engineJobCacheDao.countLessByStage(cache.getId(), cache.getJobResource(), environmentContext.getLocalAddress(),
                        Lists.newArrayList(EJobCacheStage.LACKING.getStage()));
                diagnosisContents.add(new DiagnosisContent("等待原因", cache.getWaitReason()));
                diagnosisContents.add(new DiagnosisContent("队列中实例数", lackStage + ""));
                jobDiagnosisInformation.setContents(diagnosisContents);
                return jobDiagnosisInformation;

        }
        return null;
    }
}
