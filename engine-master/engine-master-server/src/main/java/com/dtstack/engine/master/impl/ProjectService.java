package com.dtstack.engine.master.impl;

import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-01-19
 */
@Service
public class ProjectService {

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private EnvironmentContext environmentContext;



    @Transactional(rollbackFor = Exception.class)
    public void addProjectOrUpdate(ScheduleEngineProjectParam scheduleEngineProjectParam) {
        Long projectId = scheduleEngineProjectParam.getProjectId();
        Integer appType = scheduleEngineProjectParam.getAppType();

        if (projectId == null || appType == null) {
            throw new RdosDefineException("projectId or appType can not be empty");
        }

        ScheduleEngineProject scheduleEngineProject = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(projectId,appType);


        ScheduleEngineProject project = buildEngineProject(scheduleEngineProjectParam);
        if (scheduleEngineProject == null) {
            ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(scheduleEngineProjectParam.getUicTenantId());
            if(null == clusterTenant){
                throw new RdosDefineException(ErrorCode.TENANT_NOT_BIND);
            }
            project.setDefaultResourceId(clusterTenant.getDefaultResourceId());
            // 插入项目
            scheduleEngineProjectDao.insert(project);
            if (clusterTenant.getDefaultResourceId() != null) {
                resourceGroupService.grantToProjects(clusterTenant.getDefaultResourceId(), Lists.newArrayList(projectId),appType,scheduleEngineProjectParam.getUicTenantId());
            }
        } else {
            // 更新项目
            project.setId(scheduleEngineProject.getId());
            scheduleEngineProjectDao.updateById(project);
        }
    }

    private ScheduleEngineProject buildEngineProject(ScheduleEngineProjectParam scheduleEngineProjectParam) {
        ScheduleEngineProject scheduleEngineProject = new ScheduleEngineProject();
        if (scheduleEngineProjectParam.getId() != null) {
            scheduleEngineProject.setId(scheduleEngineProjectParam.getId());
        }

        if (scheduleEngineProjectParam.getProjectId() != null) {
            scheduleEngineProject.setProjectId(scheduleEngineProjectParam.getProjectId());
        }

        if (scheduleEngineProjectParam.getUicTenantId() != null) {
            scheduleEngineProject.setUicTenantId(scheduleEngineProjectParam.getUicTenantId());
        }

        if (scheduleEngineProjectParam.getAppType() != null) {
            scheduleEngineProject.setAppType(scheduleEngineProjectParam.getAppType());
        }

        if (scheduleEngineProjectParam.getProjectName() != null) {
            scheduleEngineProject.setProjectName(scheduleEngineProjectParam.getProjectName());
        }

        if (scheduleEngineProjectParam.getProjectAlias() != null) {
            scheduleEngineProject.setProjectAlias(scheduleEngineProjectParam.getProjectAlias());
        }

        if (scheduleEngineProjectParam.getProjectIdentifier() != null) {
            scheduleEngineProject.setProjectIdentifier(scheduleEngineProjectParam.getProjectIdentifier());
        }

        if (scheduleEngineProjectParam.getProjectDesc() != null) {
            scheduleEngineProject.setProjectDesc(scheduleEngineProjectParam.getProjectDesc());
        }

        if (scheduleEngineProjectParam.getStatus() != null) {
            scheduleEngineProject.setStatus(scheduleEngineProjectParam.getStatus());
        }

        if (scheduleEngineProjectParam.getCreateUserId() != null) {
            scheduleEngineProject.setCreateUserId(scheduleEngineProjectParam.getCreateUserId());
        }

        if (scheduleEngineProjectParam.getIsDeleted() != null) {
            scheduleEngineProject.setIsDeleted(scheduleEngineProjectParam.getIsDeleted());
        }

        scheduleEngineProject.setGmtModified(new Date());
        return scheduleEngineProject;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long projectId, Integer appType) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (projectId == null) {
            throw new RdosDefineException("projectId must be passed");
        }

        scheduleEngineProjectDao.deleteByProjectIdAppType(projectId, appType);
    }

    public List<ScheduleEngineProjectVO> findFuzzyProjectByProjectAlias(String name, Integer appType, Long uicTenantId,Long projectId) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (uicTenantId == null) {
            throw new RdosDefineException("uicTenantId must be passed");
        }

        List<ScheduleEngineProject> deans = scheduleEngineProjectDao.selectFuzzyProjectByProjectAlias(name, appType, uicTenantId,projectId, environmentContext.getFuzzyProjectByProjectAliasLimit());

        return buildProjectList(deans);
    }

    private List<ScheduleEngineProjectVO> buildProjectList(List<ScheduleEngineProject> deans) {
        if (CollectionUtils.isEmpty(deans)) {
            return Lists.newArrayList();
        }

        List<ScheduleEngineProjectVO> vos = Lists.newArrayList();

        for (ScheduleEngineProject dean : deans) {
            ScheduleEngineProjectVO vo = new ScheduleEngineProjectVO();
            build(dean, vo);

            vos.add(vo);
        }

        return vos;
    }

    private void build(ScheduleEngineProject dean, ScheduleEngineProjectVO vo) {
        vo.setId(dean.getId());
        vo.setProjectId(dean.getProjectId());
        vo.setUicTenantId(dean.getUicTenantId());
        vo.setAppType(dean.getAppType());
        vo.setProjectName(dean.getProjectName());
        vo.setProjectAlias(dean.getProjectAlias());
        vo.setProjectIdentifier(dean.getProjectIdentifier());
        vo.setProjectDesc(dean.getProjectDesc());
        vo.setStatus(dean.getStatus());
        vo.setCreateUserId(dean.getCreateUserId());
        vo.setGmtCreate(dean.getGmtCreate());
        vo.setGmtModified(dean.getGmtModified());
        vo.setIsDeleted(dean.getIsDeleted());
    }

    public ScheduleEngineProjectVO findProject(Long projectId, Integer appType) {
        if (projectId == null) {
            throw new RdosDefineException("projectId not null");
        }

        if (appType == null) {
            throw new RdosDefineException("projectId not null");
        }

        ScheduleEngineProject dean = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(projectId,appType);
        ScheduleEngineProjectVO vo = new ScheduleEngineProjectVO();

        if (dean == null) {
            return null;
        }
        
        build(dean, vo);
        return vo;
    }




}

