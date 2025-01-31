package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/5 10:47 上午
 * @Email:dazhi@dtstack.com
 * @Description: 项目信息 需要从业务中心获取 projectDao 已废弃 仅保留白名单功能
 */
public interface ScheduleEngineProjectDao {

    Integer insert(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    ScheduleEngineProject getProjectByProjectIdAndApptype(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    Integer updateById(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    Integer deleteByProjectIdAppType(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    List<ScheduleEngineProject> selectFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId, @Param("projectId") Long projectId, @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit);

    List<ScheduleEngineProject> listWhiteListProject();

    List<ProjectNameVO> listByAppTypeAndUicTenantId(@Param("type") Integer type, @Param("uicTenantId") Long uicTenantId);

    List<Long> listProjectIdByUicTenantId(@Param("uicTenantId") Long uicTenantId);
}
