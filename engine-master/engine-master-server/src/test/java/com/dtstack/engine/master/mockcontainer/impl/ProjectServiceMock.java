package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ScheduleEngineProject;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;

import java.util.List;

public class ProjectServiceMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getFuzzyProjectByProjectAliasLimit() {
        return 1;
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public void grantToProjects(Long resourceId, List<Long> projectIds, Integer appType, Long dtuicTenantId) {
        return;

    }

    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    Integer deleteByProjectIdAppType(@Param("projectId") Long projectId, @Param("appType") Integer appType) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    Integer updateById(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    List<ScheduleEngineProject> selectFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId, @Param("projectId") Long projectId, @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit) {
        ScheduleEngineProject scheduleEngineProject = new ScheduleEngineProject();
        scheduleEngineProject.setProjectId(1L);
        scheduleEngineProject.setProjectName("test");
        scheduleEngineProject.setDefaultResourceId(1L);
        scheduleEngineProject.setProjectIdentifier("test");
        scheduleEngineProject.setDefaultResourceId(1L);
        scheduleEngineProject.setAppType(1);
        scheduleEngineProject.setUicTenantId(1L);
        scheduleEngineProject.setIsDeleted(1);
        scheduleEngineProject.setCreateUserId(1);
        return Lists.newArrayList(scheduleEngineProject);
    }


    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    Integer insert(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    ScheduleEngineProject getProjectByProjectIdAndApptype(@Param("projectId") Long projectId, @Param("appType") Integer appType) {
        if (2 == projectId) {
            ScheduleEngineProject engineProject = new ScheduleEngineProject();
            engineProject.setId(1L);
            return engineProject;
        }
        return null;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    ClusterTenant getByDtuicTenantId(@Param("dtUicTenantId") Long dtUicTenantId) {
        ClusterTenant clusterTenant = new ClusterTenant();
        clusterTenant.setDefaultResourceId(1L);
        return clusterTenant;
    }
}
