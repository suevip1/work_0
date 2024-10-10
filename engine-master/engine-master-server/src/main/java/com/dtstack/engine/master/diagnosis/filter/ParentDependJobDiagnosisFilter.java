package com.dtstack.engine.master.diagnosis.filter;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.DependencyType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.bo.DiagnosisContent;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.JobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.utils.JobKeyUtils;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.google.common.collect.Table;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Component
public class ParentDependJobDiagnosisFilter implements JobDiagnosisFilter {

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private WorkSpaceProjectService workSpaceProjectService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Override
    public DiagnosisEnum order() {
        return DiagnosisEnum.PARENT_DEPEND_JOB_DIAGNOSIS;
    }

    @Override
    public JobDiagnosisInformation diagnosis(ScheduleJob scheduleJob, DiagnosisResultEnum preResult) {
        if (isNotReach.test(preResult)) {
            JobDiagnosisInformation jobDiagnosisInformation = new JobDiagnosisInformation();
            jobDiagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
            jobDiagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
            return jobDiagnosisInformation;
        }
        List<ScheduleJobJob> parentJobKeys = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey(), RelyTypeEnum.NORMAL.getType());
        Set<String> parentKeys = parentJobKeys.stream()
                .map(ScheduleJobJob::getParentJobKey)
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(parentJobKeys)) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(parentKeys);
            if (scheduleJobs.size() != parentJobKeys.size()) {
                Set<String> createJobKey = scheduleJobs.stream()
                        .map(ScheduleJob::getJobKey)
                        .collect(Collectors.toSet());
                parentKeys.removeAll(createJobKey);
                if (EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType())
                        && RdosTaskStatus.FAILED.getStatus().equals(scheduleJob.getStatus())) {
                    //周期调度父任务缺少 导致 失败
                    return getLostJobDiagnosisInformation(parentKeys);
                }
            }

            if (RdosTaskStatus.FINISH_STATUS.contains(scheduleJob.getStatus())) {
                JobDiagnosisInformation diagnosisInformation = new JobDiagnosisInformation();
                diagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
                diagnosisInformation.setDiagnosisStatusInfo("上游依赖实例已全部运行成功");
                return diagnosisInformation;
            }

            scheduleJobs = scheduleJobs.stream()
                    .filter(j -> !RdosTaskStatus.FINISHED.getStatus().equals(j.getStatus())
                            || (EScheduleJobType.WORK_FLOW.getType().equals(j.getTaskType())
                            && StringUtils.isNotBlank(scheduleJob.getFlowJobId())
                            && j.getJobId().equals(scheduleJob.getFlowJobId())))
                    .collect(Collectors.toList());

            removeSelfDependencyJob(scheduleJobs, scheduleJob);

            if (scheduleJobs.size() == 0) {
                JobDiagnosisInformation diagnosisInformation = new JobDiagnosisInformation();
                diagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_NORMAL.getVal());
                diagnosisInformation.setDiagnosisStatusInfo("上游依赖实例已全部运行成功");
                return diagnosisInformation;
            }

            // 递归查找失败的父任务
            scheduleJobs = findUpperLayerFailJob(scheduleJobs, environmentContext.getSdkTimeout() + System.currentTimeMillis());

            JobDiagnosisInformation diagnosisInformation = new JobDiagnosisInformation();
            if (CollectionUtils.isEmpty(scheduleJobs)) {
                diagnosisInformation.setDiagnosisStatusInfo(DiagnosisResultEnum.NOT_REACH.getName());
                diagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.NOT_REACH.getVal());
            } else {
                if (scheduleJobs.stream().anyMatch(job -> RdosTaskStatus.RUNNING_STATUS.contains(job.getStatus()))) {
                    diagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.UNDER_DIAGNOSIS.getVal());
                    diagnosisInformation.setDiagnosisStatusInfo("上游依赖实例链路中存在实例处于运行状态");
                    diagnosisInformation.setContents(formatJobDetailInfo(scheduleJobs));
                    diagnosisInformation.setDiagnosisSuggest("等待上游实例运行完成");
                } else {
                    diagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
                    diagnosisInformation.setDiagnosisStatusInfo("上游依赖实例链路中存在实例处于异常状态");
                    diagnosisInformation.setContents(formatJobDetailInfo(scheduleJobs));
                    diagnosisInformation.setDiagnosisSuggest("检查并重跑异常上游实例");
                }
            }

            return diagnosisInformation;
        } else {
            return getNotContainsInformation();
        }
    }

    /**
     * 如果当前任务调度周期是 {@link DependencyType.SELF_DEPENDENCY_END}，则移除掉上游结束的自依赖任务
     * @param parentJobs 父任务
     * @param scheduleJob 当前任务
     */
    private void removeSelfDependencyJob(List<ScheduleJob> parentJobs, ScheduleJob scheduleJob) {
        Integer dependencyType = scheduleJob.getDependencyType();
        if (!DependencyType.SELF_DEPENDENCY_END.getType().equals(dependencyType)) {
            return;
        }
        Long jobTaskShadeId = JobKeyUtils.getTaskShadeIdFromJobKey(scheduleJob.getJobKey());

        // 移除掉 end 状态的上游自依赖任务
        parentJobs.removeIf(j -> {
            Long parentTaskId = JobKeyUtils.getTaskShadeIdFromJobKey(j.getJobKey());
            boolean isSelfDependency = (parentTaskId != -1) && (jobTaskShadeId != -1) && Objects.equals(parentTaskId, jobTaskShadeId);
            return isSelfDependency && RdosTaskStatus.getStoppedStatus().contains(j.getStatus());
        });
    }

    private JobDiagnosisInformation getLostJobDiagnosisInformation(Set<String> parentKeys) {
        List<DiagnosisContent> lostJobContent = parentKeys.stream().map(k -> new DiagnosisContent("上游任务名称", k)).collect(Collectors.toList());
        JobDiagnosisInformation diagnosisInformation = new JobDiagnosisInformation();
        diagnosisInformation.setDiagnosisResult(DiagnosisResultEnum.DIAGNOSIS_FAIL.getVal());
        diagnosisInformation.setDiagnosisStatusInfo("上游依赖未生成实例");
        diagnosisInformation.setContents(lostJobContent);
        return diagnosisInformation;
    }

    /**
     * 根据依赖关系获取上层的失败任务节点
     * 超过endSystemMills 返回能获取到最近的任务节点
     *
     * @param noRunParent
     * @return
     */
    private List<ScheduleJob> findUpperLayerFailJob(List<ScheduleJob> noRunParent, long endSystemMills) {
        //上游失败
        List<ScheduleJob> failJob = noRunParent.stream()
                .filter(j -> RdosTaskStatus.STOPPED_STATUS.contains(j.getStatus()) && !RdosTaskStatus.FINISH_STATUS.contains(j.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(failJob)) {
            return failJob;
        }

        //上游运行中
        List<ScheduleJob> runJob = noRunParent.stream()
                .filter(j -> RdosTaskStatus.RUNNING_STATUS.contains(j.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(runJob)) {
            return runJob;
        }

        Set<String> jobKeySet = noRunParent.stream()
                .map(ScheduleJob::getJobKey)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(jobKeySet)) {
            return new ArrayList<>();
        }

        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKeys(jobKeySet, RelyTypeEnum.NORMAL.getType());
        if (CollectionUtils.isNotEmpty(scheduleJobJobs)) {
            List<String> parentJobKey = scheduleJobJobs.stream()
                    .map(ScheduleJobJob::getParentJobKey)
                    .collect(Collectors.toList());

            List<ScheduleJob> parentJobs = scheduleJobDao.listJobByJobKeys(parentJobKey);
            if (System.currentTimeMillis() >= endSystemMills) {
                return parentJobs.stream()
                        .filter(j -> RdosTaskStatus.UNSUBMIT_STATUS.contains(j.getStatus()) || RdosTaskStatus.RUNNING_STATUS.contains(j.getStatus()))
                        .collect(Collectors.toList());
            }
            return findUpperLayerFailJob(parentJobs, endSystemMills);
        }
        return new ArrayList<>();
    }

    private List<DiagnosisContent> formatJobDetailInfo(List<ScheduleJob> failParent) {
        List<Long> tenantId = failParent.stream()
                .map(ScheduleJob::getDtuicTenantId)
                .collect(Collectors.toList());
        Map<Long, TenantDeletedVO> tenantInfoMap = tenantService.listAllTenantByDtUicTenantIds(tenantId);

        Map<Integer, List<Long>> appProjectIds = failParent.stream()
                .collect(Collectors.groupingBy(ScheduleJob::getAppType, Collectors.mapping(ScheduleJob::getProjectId, Collectors.toList())));
        Table<Integer, Long, AuthProjectVO> projectVOTable = workSpaceProjectService.getProjectGroupAppType(appProjectIds);
        return failParent.stream().flatMap(job -> {
            List<DiagnosisContent> diagnosisContents = new ArrayList<>(3);
            ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOneIncludeDelete(job.getTaskId(), job.getAppType());
            if (taskShade != null) {
                String taskName = taskShade.getName();
                TenantDeletedVO tenantDeletedVO = tenantInfoMap.get(taskShade.getDtuicTenantId());
                String tenantName = "";
                if (tenantDeletedVO == null) {
                    UICTenantVO tenant = tenantService.getTenant(taskShade.getDtuicTenantId());
                    tenantName = tenant.getTenantName();
                } else {
                    tenantName = tenantDeletedVO.getTenantName();
                }

                String appName = AppType.getValue(taskShade.getAppType()).getName();
                String projectName = "";
                AuthProjectVO authProjectVO = projectVOTable.get(taskShade.getAppType(), taskShade.getProjectId());
                if (authProjectVO == null) {
                    authProjectVO = workSpaceProjectService.finProject(taskShade.getProjectId(), taskShade.getAppType());
                }
                projectName = authProjectVO==null?"":authProjectVO.getProjectAlias();
                diagnosisContents.add(new DiagnosisContent("上游实例名称", Strings.format("：{}（租户：{}，产品：{}，项目：{})", taskName, tenantName, appName, projectName)));
            }
            long mill = DateUtil.getTimestamp(job.getCycTime(), DateUtil.UN_STANDARD_DATETIME_FORMAT);
            String dateTime = DateUtil.getFormattedDate(mill, DateUtil.STANDARD_DATETIME_FORMAT);
            diagnosisContents.add(new DiagnosisContent("上游实例计划时间", dateTime));
            diagnosisContents.add(new DiagnosisContent("上游实例状态", RdosTaskStatus.getShowStatusDesc(job.getStatus())));
            return diagnosisContents.stream();
        }).collect(Collectors.toList());
    }
}
