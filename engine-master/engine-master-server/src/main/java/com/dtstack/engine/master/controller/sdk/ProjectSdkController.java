package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.param.ProjectDetailParam;
import com.dtstack.engine.dto.ScheduleJobCount;
import com.dtstack.engine.api.vo.project.ProjectInfoVO;
import com.dtstack.engine.api.vo.project.ProjectStatisticsInfoVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.impl.AlertTriggerRecordService;
import com.dtstack.engine.master.impl.ProjectStatisticsService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.po.ProjectStatistics;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/8/10
 */

@RestController
@RequestMapping({"/node/sdk/project"})
public class ProjectSdkController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private AlertTriggerRecordService alertTriggerRecordService;

    @Autowired
    private ProjectStatisticsService projectStatisticsService;

    @PostMapping(value = "/getProjectInfo")
    @ApiOperation(value = "获取项目告警 实例信息")
    public List<ProjectInfoVO> getProjectInfo(@RequestBody ProjectDetailParam param) {
        if (CollectionUtils.isEmpty(param.getProjectIds())) {
            return new ArrayList<>();
        }
        List<ScheduleJobCount> statusCountByProjectIds = scheduleJobService.getScheduleJobCounts(param.getProjectIds(), null, param.getAppType(), param.getDtuicTenantId(), null);
        Map<Long, List<ScheduleJobCount>> jobMapping = statusCountByProjectIds.stream()
                .collect(Collectors.groupingBy(ScheduleJobCount::getProjectId));
        return param.getProjectIds().stream().map(projectId -> {
            ProjectInfoVO projectInfoVO = new ProjectInfoVO();
            projectInfoVO.setProjectId(projectId);
            projectInfoVO.setAppType(param.getAppType());
            Integer totalAlarm = alertTriggerRecordService.getCountWithCreateTime(projectId, new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis()),
                    param.getAppType());
            projectInfoVO.setAlarmCount(totalAlarm);
            List<ScheduleJobCount> scheduleJobCounts = jobMapping.get(projectId);
            if (CollectionUtils.isNotEmpty(scheduleJobCounts)) {
                int total = scheduleJobCounts.stream().map(ScheduleJobCount::getCount).reduce(0, Integer::sum);
                int failJob = scheduleJobCounts.stream().filter(scheduleJobCount -> RdosTaskStatus.FAILED_STATUS.contains(scheduleJobCount.getStatus()))
                        .map(ScheduleJobCount::getCount).reduce(0, Integer::sum);
                projectInfoVO.setTotalJob(total);
                projectInfoVO.setFailJob(failJob);
            }
            return projectInfoVO;
        }).collect(Collectors.toList());
    }


    @PostMapping(value = "/getDetail")
    @ApiOperation(value = "获取单个项目、个人、租户的 实例等信息")
    public ProjectStatisticsInfoVO getDetail(@RequestBody ProjectDetailParam param) {
        if (param == null || null == param.getType()) {
            throw new RdosDefineException("type can not be null");
        }
        LocalDateTime recentStartTime = projectStatisticsService.getRecentStartTime();
        if (ProjectInfoType.USER.getVal() == param.getType()) {
            //获取我的项目
            ProjectStatistics projectStatistics = projectStatisticsService.queryTotalUserRecentTime(param.getUserId(), param.getAppType(), param.getDtuicTenantId(),
                    Date.from(recentStartTime.toInstant(DateUtil.DEFAULT_ZONE)));
            if (null != projectStatistics) {
                ProjectStatistics lastProjectStatistics = projectStatisticsService.queryTotalUserRecentTime(param.getUserId(), param.getAppType(), param.getDtuicTenantId(),
                        Date.from(recentStartTime.plusDays(-1L).toInstant(DateUtil.DEFAULT_ZONE)));
                return getInfo(projectStatistics, lastProjectStatistics);
            }
        }

        if (ProjectInfoType.PROJECT.getVal() == param.getType()) {
            //获取我所在的项目
            List<Long> projectIds = param.getProjectIds();
            if (CollectionUtils.isEmpty(projectIds)) {
                return null;
            }
            ProjectStatistics projectStatistics = projectStatisticsService.queryTotalProjects(projectIds, param.getAppType(), param.getDtuicTenantId(), -1L,
                    Date.from(recentStartTime.toInstant(DateUtil.DEFAULT_ZONE)));
            if (null != projectStatistics) {
                ProjectStatistics lastProjectStatistics = projectStatisticsService.queryTotalProjects(projectIds, param.getAppType(), param.getDtuicTenantId(), param.getUserId()
                        , Date.from(recentStartTime.plusDays(-1L).toInstant(DateUtil.DEFAULT_ZONE)));
                return getInfo(projectStatistics, lastProjectStatistics);
            }
        }

        if (ProjectInfoType.TENANT.getVal() == param.getType()) {
            //获取所有项目
            ProjectStatistics projectStatistics = projectStatisticsService.queryTotalTenantRecentTime(param.getDtuicTenantId(), param.getAppType(), -1L,
                    Date.from(recentStartTime.toInstant(DateUtil.DEFAULT_ZONE)));
            if (null != projectStatistics) {
                ProjectStatistics lastProjectStatistics = projectStatisticsService.queryTotalTenantRecentTime(param.getDtuicTenantId(), param.getAppType(), -1L,
                        Date.from(recentStartTime.plusDays(-1L).toInstant(DateUtil.DEFAULT_ZONE)));
                return getInfo(projectStatistics, lastProjectStatistics);
            }
        }
        return null;
    }

    private ProjectStatisticsInfoVO getInfo(ProjectStatistics projectStatistics, ProjectStatistics lastProjectStatistics) {
        ProjectStatisticsInfoVO projectStatisticsInfoVO = new ProjectStatisticsInfoVO();
        projectStatisticsInfoVO.setRunJob(projectStatistics.getRunJob());
        projectStatisticsInfoVO.setTotalJob(projectStatistics.getTotalJob());
        projectStatisticsInfoVO.setFailJob(projectStatistics.getFailJob());
        projectStatisticsInfoVO.setAlarmCount(projectStatistics.getAlarmTotal());
        projectStatisticsInfoVO.setUnSubmitJob(projectStatistics.getUnSubmitJob());
        if (lastProjectStatistics != null) {
            DecimalFormat format = new DecimalFormat("0.00");
            format.setMaximumFractionDigits(2);
            String totalJobTrend = format.format(getAnInt(lastProjectStatistics.getTotalJob(), projectStatistics.getTotalJob()) * 100L);
            projectStatisticsInfoVO.setTotalJobTrend(totalJobTrend);
            String failJobTrend = format.format(getAnInt(lastProjectStatistics.getFailJob(), projectStatistics.getFailJob()) * 100L);
            projectStatisticsInfoVO.setFailJobTrend(failJobTrend);
            String runJobTrend = format.format(getAnInt(lastProjectStatistics.getRunJob(), projectStatistics.getRunJob()) * 100L);
            projectStatisticsInfoVO.setRunJobTrend(runJobTrend);
            String unSubmitJobTrend = format.format(getAnInt(lastProjectStatistics.getUnSubmitJob(), projectStatistics.getUnSubmitJob()) * 100L);
            projectStatisticsInfoVO.setUnSubmitJobTrend(unSubmitJobTrend);
            String alertTrend = format.format(getAnInt(lastProjectStatistics.getAlarmTotal(), projectStatistics.getAlarmTotal()) * 100L);
            projectStatisticsInfoVO.setAlarmTrend(alertTrend);
        }
        return projectStatisticsInfoVO;
    }

    private float getAnInt(Integer oldJob, Integer newJob) {
        if (oldJob == 0) {
            return newJob;
        }
        float difference = newJob - oldJob;
        return difference / (float) oldJob;
    }

}

enum ProjectInfoType {
    USER(1), PROJECT(2), TENANT(3);
    private int val;

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    ProjectInfoType(int val) {
        this.val = val;
    }
}