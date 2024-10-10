package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.po.TenantResource;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.master.bo.DiagnosisContent;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class TenantResourceDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.TENANT_RESOURCE_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
        ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());

        if (null == taskShade) {
            jobDiagnosisInformation.setDiagnosisStatusInfo("任务已删除或下线");
            jobDiagnosisInformation.setDiagnosisSuggest("");
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            return jobDiagnosisInformation;
        }
        TenantResource tenantResource = tenantResourceDao.selectByUicTenantIdAndTaskType(scheduleJob.getDtuicTenantId(), scheduleJob.getTaskType());
        if (null == tenantResource) {
            return getNotContainsInformation();
        }

        List<String> resourceLimit = scheduleTaskShadeService.checkResourceLimit(scheduleJob.getDtuicTenantId(), scheduleJob.getTaskType(), taskShade.getTaskParams(), scheduleJob.getTaskId());
        if (CollectionUtils.isNotEmpty(resourceLimit)) {
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
            jobDiagnosisInformation.setDiagnosisStatusInfo("实例在环境参数配置的资源超过租户资源上限");
            List<DiagnosisContent> diagnosisContents = resourceLimit.stream()
                    .map(r -> new DiagnosisContent(r, ""))
                    .collect(Collectors.toList());
            jobDiagnosisInformation.setContents(diagnosisContents);
            jobDiagnosisInformation.setDiagnosisSuggest("将实例对应任务在环境参数中的资源配置调小，或将租户对任务资源限制调大");
            return jobDiagnosisInformation;
        }
        jobDiagnosisInformation.setDiagnosisResult(isNotReach.test(preResult) ? DiagnosisResultEnum.NOT_REACH.getVal() : DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
        jobDiagnosisInformation.setDiagnosisStatusInfo(isNotReach.test(preResult) ? DiagnosisResultEnum.NOT_REACH.getName() : "资源限制检查通过");
        return jobDiagnosisInformation;
    }
}
