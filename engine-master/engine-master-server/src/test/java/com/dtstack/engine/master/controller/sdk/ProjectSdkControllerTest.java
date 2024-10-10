package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.param.ProjectDetailParam;
import com.dtstack.engine.dto.ScheduleJobCount;
import com.dtstack.engine.master.impl.AlertTriggerRecordService;
import com.dtstack.engine.master.impl.ProjectStatisticsService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.po.ProjectStatistics;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MockWith(ProjectSdkControllerMock.class)
public class ProjectSdkControllerTest {

    private ProjectSdkController controller = new ProjectSdkController();

    @Test
    public void getProjectInfo() {
        ProjectDetailParam projectDetailParam = new ProjectDetailParam();
        projectDetailParam.setProjectIds(Lists.newArrayList(1L));
        controller.getProjectInfo(projectDetailParam);
    }

    @Test
    public void getDetail() {
        ProjectDetailParam param1 = new ProjectDetailParam();
        param1.setType(1);
        ProjectDetailParam param2 = new ProjectDetailParam();
        param2.setType(2);
        ProjectDetailParam param3 = new ProjectDetailParam();
        param3.setType(3);
        controller.getDetail(param1);
        controller.getDetail(param2);
        controller.getDetail(param3);
    }
}

class ProjectSdkControllerMock {

    @MockInvoke(targetClass = ProjectStatisticsService.class)
    public ProjectStatistics queryTotalTenantRecentTime(Long tenantId, Integer appType, Long userId, Date gmtCreate) {
        ProjectStatistics projectStatistics = new ProjectStatistics();
        projectStatistics.setRunJob(1);
        projectStatistics.setTotalJob(1);
        projectStatistics.setTotalJob(1);
        projectStatistics.setFailJob(0);
        projectStatistics.setAlarmTotal(1);
        projectStatistics.setUnSubmitJob(0);
        return projectStatistics;
    }

    @MockInvoke(targetClass = ProjectStatisticsService.class)
    public ProjectStatistics queryTotalProjects(List<Long> projectIds, Integer appType, Long dtuicTenantId, Long userId, Date gmtCreate) {
        ProjectStatistics projectStatistics = new ProjectStatistics();
        projectStatistics.setRunJob(1);
        projectStatistics.setTotalJob(1);
        projectStatistics.setTotalJob(1);
        projectStatistics.setFailJob(0);
        projectStatistics.setAlarmTotal(1);
        projectStatistics.setUnSubmitJob(0);
        return projectStatistics;
    }

        @MockInvoke(targetClass = ProjectStatisticsService.class)
    public ProjectStatistics queryTotalUserRecentTime(Long userId, Integer appType, Long tenantId, Date gmtCreate) {
        ProjectStatistics projectStatistics = new ProjectStatistics();
        projectStatistics.setRunJob(1);
        projectStatistics.setTotalJob(1);
        projectStatistics.setTotalJob(1);
        projectStatistics.setFailJob(0);
        projectStatistics.setAlarmTotal(1);
        projectStatistics.setUnSubmitJob(0);
        return projectStatistics;
    }

        @MockInvoke(targetClass = ProjectStatisticsService.class)
    public LocalDateTime getRecentStartTime() {
        int minute = LocalDateTime.now().getMinute();
        return LocalDateTime.now().withMinute(Math.floorDiv(minute, 10) * 10)
                .withSecond(0)
                .withNano(0);
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJobCount> getScheduleJobCounts(List<Long> projectIds, Long tenantId, Integer appType, Long dtuicTenantId, List<Integer> status) {
        List<ScheduleJobCount> list = new ArrayList<>();
        ScheduleJobCount jobCount = new ScheduleJobCount();
        jobCount.setProjectId(1L);
        jobCount.setCount(1);
        list.add(jobCount);
        return list;
    }

    @MockInvoke(targetClass = AlertTriggerRecordService.class)
    public Integer getCountWithCreateTime(Long projectId, Timestamp time, Integer appType) {
        return 1;
    }
}