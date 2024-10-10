package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.convert.load.ConsoleLoaderService;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.dtcenter.loader.dto.source.TrinoSourceDTO;
import com.dtstack.dtcenter.loader.dto.trinoResource.TrinoOverView;
import com.dtstack.dtcenter.loader.dto.trinoResource.TrinoResouceDTO;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ResourceGroupGrantPageParam;
import com.dtstack.engine.api.param.ResourceGroupParam;
import com.dtstack.engine.api.param.TrinoResourceGroupGrantPageParam;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.AccessedResourceGroupVO;
import com.dtstack.engine.api.vo.GrantedProjectVO;
import com.dtstack.engine.api.vo.HandOverVO;
import com.dtstack.engine.api.vo.ResourceGroupDetailVO;
import com.dtstack.engine.api.vo.ResourceGroupDropDownVO;
import com.dtstack.engine.api.vo.ResourceGroupLabelDetailVO;
import com.dtstack.engine.api.vo.ResourceGroupListVO;
import com.dtstack.engine.api.vo.ResourceGroupUsedVO;
import com.dtstack.engine.api.vo.ReversalGrantCheckResultVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.api.vo.resource.ResourceGroupQueryParam;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.IsDefaultEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.ResourceGroupGrantDao;
import com.dtstack.engine.dao.ResourceHandOverDao;
import com.dtstack.engine.dao.ResourceQueueUsedDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.GrantedProjectDTO;
import com.dtstack.engine.dto.ResourceGroupGrantSearchDTO;
import com.dtstack.engine.dto.TimeUsedNode;
import com.dtstack.engine.dto.LabelGrantedProjectDTO;
import com.dtstack.engine.master.dto.LabelResourceGroupDTO;
import com.dtstack.engine.master.dto.LabelResourceGroupUserDTO;
import com.dtstack.engine.master.dto.LabelReversalGrantCheckResultDTO;
import com.dtstack.engine.master.mapstruct.ResourceGroupStruct;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ComponentUser;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ResourceGroupGrant;
import com.dtstack.engine.po.ResourceHandOver;
import com.dtstack.engine.po.ResourceQueueUsed;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.GroupVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import dt.insight.plat.lang.exception.biz.BizException;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResourceGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceGroupService.class);

    @Autowired
    private ResourceGroupDao resourceGroupDao;

    @Autowired
    private ResourceGroupGrantDao resourceGroupGrantDao;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private WorkSpaceProjectService workSpaceProjectService;

    @Autowired
    private ResourceHandOverDao resourceHandOverDao;

    @Autowired
    private ResourceQueueUsedDao resourceQueueUsedDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ResourceGroupStruct resourceGroupStruct;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterDao clusterDao;

    public PageResult<List<ResourceGroupListVO>> pageQuery(Long clusterId, Integer componentTypeCode, int currentPage, int pageSize) {
        PageQuery<ResourceGroup> pageQuery = new PageQuery<>(currentPage, pageSize,
                "gmt_modified", Sort.DESC.name());
        ResourceGroup model = new ResourceGroup();
        model.setClusterId(clusterId);
        model.setComponentTypeCode(componentTypeCode);
        model.setIsDeleted(Deleted.NORMAL.getStatus());
        int count = resourceGroupDao.generalCount(model);
        List<ResourceGroupListVO> groups = new ArrayList<>();
        if (count > 0) {
            pageQuery.setModel(model);
            List<ResourceGroup> resourceGroups = resourceGroupDao.generalQuery(pageQuery);
            groups = resourceGroupStruct.toListVOs(resourceGroups);
        }

        return new PageResult<>(groups, count, pageQuery);
    }


    public PageResult<List<TrinoResouceDTO>> pageQueryTrinoResource(Long clusterId,Long dtuicTenantId, int currentPage, int pageSize) {

        PageQuery<ResourceGroup> pageQuery = new PageQuery<>(currentPage, pageSize,
                "gmt_modified", Sort.DESC.name());
        List<TrinoResouceDTO> resouceDTOS = consoleService.clusterTrinoResources(clusterId, dtuicTenantId);
        saveTrinoResource(clusterId,resouceDTOS);
        //内存分页
        List<TrinoResouceDTO> result;
        int start = pageQuery.getStart();
        int end = pageQuery.getStart() + pageSize;
        if(resouceDTOS.size()>=end){
             result = resouceDTOS.subList(start,end);
        }else if(start+1 > resouceDTOS.size()){
            result = ListUtils.EMPTY_LIST;
        }else{
            result =  resouceDTOS.subList(start, resouceDTOS.size());
        }
        return new PageResult<>(result,resouceDTOS.size(),pageQuery);
    }

    private void saveTrinoResource(Long clusterId,List<TrinoResouceDTO> resouceDTOS){
        //底层可以查出来，数据库中没有的入库
        List<ResourceGroup> resourceGroups = resourceGroupDao.listAllTrinoResource(1,clusterId,0);
        if(CollectionUtils.isEmpty(resouceDTOS)){
            return;
        }
        List<TrinoResouceDTO> allResources = Lists.newArrayList();
        allResources.addAll(resouceDTOS);
        tileTrinoResource(allResources, resouceDTOS);
        List<String> groupNames = resourceGroups.stream().map(ResourceGroup::getName).collect(Collectors.toList());
        List<String> allGroupNames = allResources.stream().map(r -> String.join("-", r.getGroupId())).collect(Collectors.toList());
        List<TrinoResouceDTO> shouldSaveDTOs = allResources.stream().distinct().filter(r -> !groupNames.contains(String.join("-", r.getGroupId()))).collect(Collectors.toList());
        List<ResourceGroup> shouldSaveResources = Lists.newArrayList();
        List<ResourceGroup> shouldUpdateResources = Lists.newArrayList();
        for (TrinoResouceDTO shouldSaveDTO : shouldSaveDTOs) {
            String groupIdStr = String.join("-", shouldSaveDTO.getGroupId());
            //新增的是子节点，原来的节点需要置为非子节点
            shouldUpdateResources.addAll(resourceGroups.stream().filter(resourceGroup -> groupIdStr.startsWith(resourceGroup.getName())).collect(Collectors.toList()));
            ResourceGroup resourceGroup = new ResourceGroup();
            resourceGroup.setClusterId(clusterId);
            resourceGroup.setName(String.join("-",shouldSaveDTO.getGroupId()));
            resourceGroup.setQueuePath("");
            resourceGroup.setResourceType(1);
            resourceGroup.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            resourceGroup.setGmtModified(new Timestamp(System.currentTimeMillis()));
            //判断是否是最后一个节点
            resourceGroup.setDescription(CollectionUtils.isEmpty(shouldSaveDTO.getStaticDescendants()) ? "1":"0");
            shouldSaveResources.add(resourceGroup);
        }
        List<String> oldRGroups = Lists.newArrayList(groupNames);
        //数据库中的减去查出来的，得到需要删除的资源组
        groupNames.removeAll(allGroupNames);
        //删除的如果是叶子节点，叶子节点的父节点应该变为叶子节点
        Set<String> toLeaveGroupNameSet = new HashSet<>();
        for (String groupName : groupNames) {
            Set<String> set = oldRGroups.stream().filter(o -> !groupName.equals(o) && groupName.startsWith(o)).collect(Collectors.toSet());
            String toLeaveGroupName = set.stream().max(Comparator.comparingInt(String::length)).orElse("");
            toLeaveGroupNameSet.add(toLeaveGroupName);
        }
        if(!CollectionUtils.isEmpty(toLeaveGroupNameSet)){
            //非叶子节点变成叶子节点
            resourceGroupDao.batchUpdateToLeaf(Lists.newArrayList(toLeaveGroupNameSet));
        }
        if(!CollectionUtils.isEmpty(groupNames)){
            //需要删除的资源组
            LOGGER.info("delete trino resource gorup,names:{}", JSON.toJSONString(groupNames));
            resourceGroupDao.deleteTrinoResource(groupNames);
        }
        if(!CollectionUtils.isEmpty(shouldUpdateResources)){
            resourceGroupDao.batchUpdateToUnLeaf(shouldUpdateResources);
        }
        if(CollectionUtils.isEmpty(shouldSaveResources)){
            return;
        }
        resourceGroupDao.batchInsert(shouldSaveResources);
    }


    private List<TrinoResouceDTO> tileTrinoResource(List<TrinoResouceDTO> allResources,List<TrinoResouceDTO> resouceDTOS){

        for (TrinoResouceDTO resouceDTO : resouceDTOS) {
            if(!CollectionUtils.isEmpty(resouceDTO.getStaticDescendants())){
                allResources.addAll(resouceDTO.getStaticDescendants());
                tileTrinoResource(allResources,resouceDTO.getStaticDescendants());
            }
        }
        return allResources;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(ResourceGroupParam param) {
        ResourceGroup group;
        if (param.getId() != null) {
            group = resourceGroupDao.getOne(param.getId(),Deleted.NORMAL.getStatus());
            if (group == null) {
                throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
            }
            if (param.getClusterId() != null && !param.getClusterId().equals(group.getClusterId())) {
                throw new RdosDefineException("不允许修改资源组所属集群ID。");
            }
        } else {
            group = new ResourceGroup();
        }

        if (!param.getName().equals(group.getName())) {
            long cnt = resourceGroupDao.countByClusterIdAndName(param.getClusterId(), param.getName(), param.getComponentTypeCode());
            if (cnt > 0) {
                throw new RdosDefineException("该资源组名称已存在。");
            }
        }

        group.setName(param.getName());
        group.setQueuePath(param.getQueueName());
        group.setDescription(param.getDescription());
        group.setClusterId(param.getClusterId());
        group.setId(param.getId());
        group.setIsDeleted(Deleted.NORMAL.getStatus());
        group.setComponentTypeCode(param.getComponentTypeCode());
        checkQueueIsExist(param, group);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (group.getId() == null) {
            group.setGmtCreate(now);
            group.setGmtModified(now);
            resourceGroupDao.insert(group);
        } else {
            group.setGmtModified(now);
            resourceGroupDao.update(group);
        }
    }

    // todo can move
    @Transactional(rollbackFor = Exception.class)
    public ResourceGroup addAgentLabelResource(ResourceGroupParam param, Timestamp now) {
        ResourceGroup group = new ResourceGroup();
        long cnt = resourceGroupDao.countByLabelAndUser(param.getClusterId(), param.getName(), param.getQueueName(), param.getComponentTypeCode());
        if (cnt > 0) {
            throw new RdosDefineException(String.format("节点标签:%s, 服务器用户:%s 已存在", param.getName(), param.getQueueName()));
        }

        group.setName(param.getName());
        group.setQueuePath(param.getQueueName());
        group.setDescription(param.getDescription());
        group.setClusterId(param.getClusterId());
        group.setIsDeleted(Deleted.NORMAL.getStatus());
        group.setComponentTypeCode(param.getComponentTypeCode());
        group.setGmtCreate(now);
        group.setGmtModified(now);
        resourceGroupDao.insert(group);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ResourceGroup> addBatchAgentLabelResource(List<ComponentUser> componentUsers, Timestamp now) {
        List<ResourceGroup> result = new ArrayList<>(componentUsers.size());
        for (ComponentUser componentUser : componentUsers) {
            ResourceGroupParam resourceGroupParam = transToAgentResource(componentUser);
            ResourceGroup resourceGroup = addAgentLabelResource(resourceGroupParam, now);
            result.add(resourceGroup);
        }
        return result;
    }

    public static ResourceGroupParam transToAgentResource(ComponentUser componentUser) {
        if (componentUser == null) {
            return null;
        }
        ResourceGroupParam param = new ResourceGroupParam();
        param.setClusterId(componentUser.getClusterId());
        param.setComponentTypeCode(componentUser.getComponentTypeCode());
        param.setName(componentUser.getLabel());
        param.setQueueName(componentUser.getUserName());
        param.setDescription(StringUtils.EMPTY);
        return param;
    }

    private void checkQueueIsExist(ResourceGroupParam param, ResourceGroup group) {
        Queue queue = queueDao.getByClusterIdAndQueuePath(param.getClusterId(), group.getQueuePath());
        if (queue != null) {
            return;
        }
        //新增未同步的队列
        Cluster cluster = clusterDao.getOne(param.getClusterId());
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        List<ComponentTestResult> refresh = componentService.refresh(cluster.getClusterName());
        if (!refresh.get(0).getResult()) {
            throw new RdosDefineException(refresh.get(0).getErrorMsg());
        }
    }

    public List<String> getNames(Long clusterId, String queuePath) {
        return resourceGroupDao.listNamesByQueueName(clusterId, queuePath, null);
    }

    public List<ResourceGroupDropDownVO> getDropDownList(Long clusterId) {
        List<ResourceGroupDetail> details = resourceGroupDao.listDropDownByClusterId(clusterId, null);
        return resourceGroupStruct.toDropDownVOs(details);
    }

    @Transactional(rollbackFor = Exception.class)
    public void grantToProject(Long resourceId, Long dtUicTenantId, AppType productType, Long projectId) {
        //授权全部项目
        if (projectId < 0) {
            List<ProjectNameVO> names = workSpaceProjectService.listCanGrantProjects(productType, dtUicTenantId);
            List<Long> ids = names.stream()
                    .map(ProjectNameVO::getProjectId)
                    .collect(Collectors.toList());
            grantToProjects(resourceId, ids,productType.getType(),dtUicTenantId);
        } else {
            // 已经授权了的项目，就不再授权
            long cnt = resourceGroupGrantDao.countByResourceIdAndEngineProjectId(resourceId, projectId,productType.getType());
            if (cnt > 0) {
                return;
            }
            ResourceGroupGrant grant = new ResourceGroupGrant();
            grant.setResourceId(resourceId);
            grant.setProjectId(projectId);
            grant.setAppType(productType.getType());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            grant.setGmtCreate(now);
            grant.setDtuicTenantId(dtUicTenantId);
            grant.setGmtModified(now);
            grant.setIsDeleted(Deleted.NORMAL.getStatus());
            resourceGroupGrantDao.insert(grant);

            List<ResourceHandOver> handOverList = resourceHandOverDao.findByProjectIdAndOldResourceIds(projectId,productType.getType(), Lists.newArrayList(resourceId));

            if (!CollectionUtils.isEmpty(handOverList)) {
                List<Long> ids = handOverList.stream().map(ResourceHandOver::getId).collect(Collectors.toList());
                resourceHandOverDao.deleteByIds(ids);
            }

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void grantToProjects(Long resourceId, List<Long> projectIds, Integer appType,Long dtuicTenantId) {
        List<Long> copyProjectIds = new ArrayList<>(projectIds);
        List<Long> grantedProjectIds = resourceGroupGrantDao.listGrantedProjects(resourceId,appType);
        // 剔除掉已经授权的项目
        copyProjectIds.removeAll(grantedProjectIds);

        if (copyProjectIds.isEmpty()) {
            return;
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<ResourceGroupGrant> grants = new ArrayList<>();
        copyProjectIds.forEach(id -> {
            ResourceGroupGrant grant = new ResourceGroupGrant();
            grant.setResourceId(resourceId);
            grant.setDtuicTenantId(dtuicTenantId);
            grant.setProjectId(id);
            grant.setAppType(appType);
            grant.setGmtCreate(now);
            grant.setGmtModified(now);
            grant.setIsDeleted(Deleted.NORMAL.getStatus());
            grants.add(grant);

            List<ResourceHandOver> handOverList = resourceHandOverDao.findByProjectIdAndOldResourceIds(id,appType, Lists.newArrayList(resourceId));

            if (!CollectionUtils.isEmpty(handOverList)) {
                List<Long> ids = handOverList.stream().map(ResourceHandOver::getId).collect(Collectors.toList());
                resourceHandOverDao.deleteByIds(ids);
            }
        });

        resourceGroupGrantDao.insertAll(grants);
    }

    public PageResult<List<GrantedProjectVO>> pageGrantedProjects(ResourceGroupGrantPageParam param) {
        AppType appType = null;
        if (StringUtils.isNotBlank(param.getAppType())) {
            appType = AppType.valueOf(param.getAppType());
        }
        PageQuery<ResourceGroupGrantSearchDTO> query = new PageQuery<>(param.getCurrentPage(), param.getPageSize());
        ResourceGroupGrantSearchDTO dto = new ResourceGroupGrantSearchDTO();
        String tenantName = param.getTenantName();
        if (StringUtils.isNotBlank(tenantName)) {
            List<UserFullTenantVO> tenantByFullName = tenantService.getTenantByFullName(tenantName);
            dto.setDtuicTenantIds(tenantByFullName.stream()
                    .map(UserFullTenantVO::getTenantId)
                    .collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(tenantByFullName)) {
                return PageResult.EMPTY_PAGE_RESULT;
            }
        }
        if (StringUtils.isNotBlank(param.getProjectName())) {
            String projectName = param.getProjectName();
            List<AuthProjectVO> authProjectVOS = workSpaceProjectService.fullProjectName(null, projectName, null);
            dto.setProjectIds(authProjectVOS.stream()
                    .map(AuthProjectVO::getProjectId)
                    .collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(authProjectVOS)) {
                return PageResult.EMPTY_PAGE_RESULT;
            }
        }
        dto.setResourceId(param.getResourceId());
        dto.setAppType(appType != null ? appType.getType() : null);
        int total = resourceGroupGrantDao.generalCount(dto);

        query.setModel(dto);
        List<GrantedProjectDTO> res = resourceGroupGrantDao.generalQuery(query);
        Set<Long> tenantIds = res.stream().map(GrantedProjectDTO::getTenantId).collect(Collectors.toSet());
        Map<Integer, List<Long>> appProjectMap = res.stream().collect(Collectors.groupingBy(GrantedProjectDTO::getAppType,
                Collectors.mapping(GrantedProjectDTO::getProjectId, Collectors.toList())));
        Table<Integer, Long, AuthProjectVO> projectInfoTable = workSpaceProjectService.getProjectGroupAppType(appProjectMap);
        Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);
        List<GrantedProjectVO> voRes = new ArrayList<>();
        res.forEach(d -> {
            GrantedProjectVO vo = new GrantedProjectVO();
            vo.setProjectId(d.getProjectId());
            AuthProjectVO authProjectVO = projectInfoTable.get(d.getAppType(), d.getProjectId());
            vo.setProjectName(null == authProjectVO ? "" : authProjectVO.getProjectAlias());
            vo.setProductName(AppType.getValue(d.getAppType()).getName());
            TenantDeletedVO tenantDetailVO = tenantMap.get(d.getTenantId());
            vo.setTenantName(null == tenantDetailVO ? "" : tenantDetailVO.getTenantName());
            vo.setAppType(d.getAppType());
            voRes.add(vo);
        });
        return new PageResult<>(voRes, total, query);
    }

    /**
     * 展示节点标签资源组授权列表
     * @param param
     * @return
     */
    public PageResult<List<LabelGrantedProjectDTO>> pageLabelGrantedProjects(ResourceGroupGrantPageParam param) {
        if (CollectionUtils.isEmpty(param.getResourceIds())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        AppType appType = StringUtils.isNotBlank(param.getAppType()) ? AppType.valueOf(param.getAppType()) : null;

        ResourceGroupGrantSearchDTO searchDto = new ResourceGroupGrantSearchDTO();
        String tenantName = param.getTenantName();
        if (StringUtils.isNotBlank(tenantName)) {
            List<UserFullTenantVO> tenantByFullName = tenantService.getTenantByFullName(tenantName);
            searchDto.setDtuicTenantIds(tenantByFullName.stream()
                    .map(UserFullTenantVO::getTenantId)
                    .collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(tenantByFullName)) {
                return PageResult.EMPTY_PAGE_RESULT;
            }
        }
        if (StringUtils.isNotBlank(param.getProjectName())) {
            String projectName = param.getProjectName();
            List<AuthProjectVO> authProjectVOS = workSpaceProjectService.fullProjectName(null, projectName, null);
            searchDto.setProjectIds(authProjectVOS.stream()
                    .map(AuthProjectVO::getProjectId)
                    .collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(authProjectVOS)) {
                return PageResult.EMPTY_PAGE_RESULT;
            }
        }
        searchDto.setResourceIds(param.getResourceIds());
        searchDto.setResourceId(param.getResourceId());
        searchDto.setAppType(appType != null ? appType.getType() : null);
        int total = resourceGroupGrantDao.generalLabelCount(searchDto);
        if (total <= 0) {
            return PageResult.EMPTY_PAGE_RESULT;
        }
        PageQuery<ResourceGroupGrantSearchDTO> query = new PageQuery<>(param.getCurrentPage(), param.getPageSize());
        query.setModel(searchDto);
        List<LabelGrantedProjectDTO> labelGrantedProjectList = resourceGroupGrantDao.generalLabelQuery(query);
        fillLabelGrantedProjectList(labelGrantedProjectList, param.getResourceIds());
        return new PageResult<>(labelGrantedProjectList, total, query);
    }

    private void fillLabelGrantedProjectList(List<LabelGrantedProjectDTO> labelGrantedProjectList, List<Long> resourceIds) {
        Set<Long> tenantIds = labelGrantedProjectList.stream().map(LabelGrantedProjectDTO::getTenantId).collect(Collectors.toSet());
        // 全部的租户信息
        Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);

        // 全部的资源组信息
        Map<Long, LabelResourceGroupUserDTO> labelResourceGroupUserMap = resourceGroupDao.listByIds(resourceIds).stream().map(t -> {
            LabelResourceGroupUserDTO groupUserDTO = new LabelResourceGroupUserDTO();
            groupUserDTO.setResourceId(t.getId());
            groupUserDTO.setLabel(t.getName());
            groupUserDTO.setUserName(t.getQueuePath());
            groupUserDTO.setComponentTypeCode(t.getComponentTypeCode());
            return groupUserDTO;
        }).collect(Collectors.toMap(LabelResourceGroupUserDTO::getResourceId, Function.identity()));

        Map<Integer, List<Long>> appProjectMap = labelGrantedProjectList.stream().collect(Collectors.groupingBy(LabelGrantedProjectDTO::getAppType,
                Collectors.mapping(LabelGrantedProjectDTO::getProjectId, Collectors.toList())));
        // 全部的项目信息
        Table<Integer, Long, AuthProjectVO> projectInfoTable = workSpaceProjectService.getProjectGroupAppType(appProjectMap);
        labelGrantedProjectList.forEach(labelGrantedProject -> {
            AuthProjectVO authProjectVO = projectInfoTable.get(labelGrantedProject.getAppType(), labelGrantedProject.getProjectId());
            labelGrantedProject.setProjectName(null == authProjectVO ? "" : authProjectVO.getProjectAlias());
            labelGrantedProject.setProductName(AppType.getValue(labelGrantedProject.getAppType()).getName());
            TenantDeletedVO tenantDetailVO = tenantMap.get(labelGrantedProject.getTenantId());
            labelGrantedProject.setTenantName(null == tenantDetailVO ? "" : tenantDetailVO.getTenantName());
            labelGrantedProject.setAppType(labelGrantedProject.getAppType());

            // 填充资源组信息
            String concatResourceIds = labelGrantedProject.getConcatResourceIds();
            if (StringUtils.isNotBlank(concatResourceIds)) {
                String[] resourceIdArray = concatResourceIds.split(",");
                List<LabelResourceGroupUserDTO> labelResourceGroupUsers = new ArrayList<>(resourceIdArray.length);
                for (String resourceIdStr : resourceIdArray) {
                    Long resourceId = Long.valueOf(resourceIdStr);
                    LabelResourceGroupUserDTO groupUserDTO = labelResourceGroupUserMap.get(resourceId);
                    Optional.ofNullable(groupUserDTO).ifPresent(labelResourceGroupUsers::add);
                }
                labelGrantedProject.setLabelResourceGroupUsers(labelResourceGroupUsers);
            }
        });
    }

    public boolean isAccessedByProject(Long resourceId, Integer appType, Long projectId) {
        long cnt = resourceGroupGrantDao.countByResourceIdAndEngineProjectId(resourceId, projectId, appType);
        return cnt > 0;
    }

    public AccessedResourceGroupVO<ResourceGroupDetailVO> listAccessedResourceDetails(Long projectId, Integer appType) {
        AuthProjectVO projectVO = workSpaceProjectService.finProject(projectId, appType);
        if (null == projectVO) {
            throw new RdosDefineException("项目不存在");
        }
        Long uicTenantId = projectVO.getDtuicTenantId();
        Cluster cluster = clusterService.getCluster(uicTenantId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        String clusterName = cluster.getClusterName();
        Map<String, String> queueRealCapacityMap = getQueueWithException(uicTenantId, cluster, clusterName);

        List<ResourceGroupDetail> details = resourceGroupGrantDao.listAccessedResourceDetailByProjectId(projectId, appType, null);
        List<ResourceGroupDetailVO> detailVOs = details.stream().map(
                d -> {
                    ResourceGroupDetailVO resourceGroupDetailVO = resourceGroupStruct.toGroupDetailVO(d);
                    String used = queueRealCapacityMap.getOrDefault(d.getQueuePath(), "");
                    resourceGroupDetailVO.setUsed(used);

                    Queue queue = queueService.getQueueByPath(d.getClusterId(), d.getQueuePath());
                    if (queue != null) {
                        resourceGroupDetailVO.setCapacity(NumberUtils.toDouble(queue.getCapacity(), 0) * 100 + "%");
                        resourceGroupDetailVO.setMaxCapacity(NumberUtils.toDouble(queue.getMaxCapacity(), 0) * 100 + "%");
                        resourceGroupDetailVO.setQueueName(queue.getQueueName());
                        resourceGroupDetailVO.setQueuePath(queue.getQueuePath());
                    }
                    return resourceGroupDetailVO;
                }

        ).collect(Collectors.toList());
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(uicTenantId);
        AccessedResourceGroupVO<ResourceGroupDetailVO> result = new AccessedResourceGroupVO<>();
        if (clusterTenant != null) {
            result.setDefaultResourceId(clusterTenant.getDefaultResourceId());
        }
        result.setAccessedResources(detailVOs);
        return result;
    }

    private Map<String, String> getQueueWithException(Long uicTenantId, Cluster cluster, String clusterName) {
        ClusterResource resource = null;
        try {
            resource = consoleService.clusterResources(clusterName, null, uicTenantId);
        } catch (Exception e) {
            return new HashMap<>();
        }
        if (resource == null || org.apache.commons.collections.CollectionUtils.isEmpty(resource.getQueues())) {
            LOGGER.warn("get empty when get resources from cluster {}", cluster.getClusterName());
            return new HashMap<>();
        }
        List<JSONObject> queues = resource.getQueues();
        return queues.stream().
                collect(Collectors.toMap(q ->
                        q.getString("queueName"), q -> q.getString("usedCapacity")));
    }

    public List<HandOverVO> getHandOver(List<Long> oldResourceIds, Integer appType, Long projectId) {
        List<ResourceHandOver> pos = resourceHandOverDao.findByProjectIdAndOldResourceIds(projectId, appType, oldResourceIds);
        if (CollectionUtils.isEmpty(pos)) {
            return new ArrayList<>();
        }
        Map<Long, ResourceHandOver> oldMap = pos.stream().collect(Collectors.groupingBy(ResourceHandOver::getOldResourceId,
                Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));
        return oldResourceIds.stream().map(id -> {
            HandOverVO vo = new HandOverVO();
            vo.setSourceResourceId(id);
            Long target = null;
            if (oldMap.containsKey(id)) {
                target = oldMap.get(id).getTargetResourceId();
            }
            vo.setNeedHandOver(target != null && !target.equals(id));
            vo.setTargetResourceId(target);
            return vo;
        }).collect(Collectors.toList());
    }

    public List<ResourceGroupDetail> listAccessedResources(Long projectId, Integer appType, Integer componentTypeCode) {
        return resourceGroupGrantDao.listAccessedResourceByProjectId(projectId, appType, componentTypeCode);
    }

    public List<ResourceGroup> listAccessedResourceGroups(Long projectId, Integer appType, Integer componentTypeCode) {
        return resourceGroupGrantDao.listAccessedResourceGroups(projectId, appType, componentTypeCode);
    }

    /**
     *
     * @param resourceId 需要交接的原 resourceId
     * @param projectId
     * @param handOver 交接后的目标 resourceId
     * @param appType
     * @param componentTypeCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void reversalGrant(Long resourceId, Long projectId, Long handOver, Integer appType, Integer componentTypeCode) {
        // 要交接需要保证项目原先有 >1 个资源组
        if (resourceGroupGrantDao.countAccessedResourceByProjectId(projectId, appType, componentTypeCode) <= 1) {
            throw new RdosDefineException("项目需要至少有一个资源组.");
        }

        if (handOver != null) {
            scheduleTaskShadeDao.updateResourceIdByProjectIdAndOldResourceId(handOver, projectId, resourceId, appType);
            scheduleJobDao.updateResourceIdByProjectIdAndOldResourceIdAndStatus(handOver, projectId, resourceId, RdosTaskStatus.UNSUBMIT.getStatus());
            // 旧交接记录是否存在
            List<ResourceHandOver> exists = resourceHandOverDao.findByProjectIdAndOldResourceIds(projectId, appType, Collections.singletonList(resourceId));
            Timestamp now = Timestamp.from(Instant.now());
            if (CollectionUtils.isEmpty(exists)) {
                ResourceHandOver newOver = new ResourceHandOver();
                newOver.setProjectId(projectId);
                newOver.setAppType(appType);
                newOver.setOldResourceId(resourceId);
                newOver.setTargetResourceId(handOver);
                newOver.setGmtModified(now);
                newOver.setGmtCreate(now);
                newOver.setIsDeleted(Deleted.NORMAL.getStatus());
                // 不存在旧交接记录，则插入一条
                resourceHandOverDao.insert(newOver);
            } else {
                ResourceHandOver exist = exists.get(0);
                exist.setTargetResourceId(handOver);
                exist.setGmtModified(now);
                // 存在旧交接记录，则将 targetResourceId 更新为要转接的目标资源组 id
                resourceHandOverDao.update(exist);
            }
            // 将 DB 中项目原来交接给 resourceId 的记录，调整为给 handOver
            // 第一次交接：<p1--r1--r2>  第二次交接：<p1--r2--r3>，然后会将第一次交接的记录修改为 <p1--r1--r3>
            resourceHandOverDao.updateTargetResourceIdByProjectIdAndTargetResourceId(projectId, appType, resourceId, handOver);
        }

        resourceGroupGrantDao.delete(resourceId, projectId, appType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        long cnt = clusterTenantDao.countByDefaultResourceId(id);
        if (cnt > 0) {
            throw new RdosDefineException("当前资源组是某一租户的默认资源组，无法删除。");
        }
        resourceGroupDao.delete(id);
    }

    public ReversalGrantCheckResultVO checkReversalGrant(Long resourceId, Long projectId, Integer appType, Integer componentTypeCode) {
        List<ResourceGroupDetail> details = listAccessedResources(projectId, appType, componentTypeCode);
        if (details.stream().noneMatch(v -> v.getResourceId().equals(resourceId))) {
            throw new RdosDefineException("未授权，无法取消授权。");
        }

        ReversalGrantCheckResultVO vo = new ReversalGrantCheckResultVO();
        if (details.size() == 1) {
            //如果项目被删除了 也可以取消授权
            AuthProjectVO authProjectVO = workSpaceProjectService.finProject(projectId, appType);
            if (null == authProjectVO) {
                vo.setState(2);
            }
            vo.setState(1);
            return vo;
        }

        vo.setState(2);
        details = details.stream()
                .filter(v -> !v.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
        vo.setRemainAccessed(resourceGroupStruct.toDropDownVOs(details));
        return vo;
    }

    public LabelReversalGrantCheckResultDTO dtScriptAgentCheckReversalGrant(Long resourceId, Long projectId, Integer appType, Integer componentTypeCode) {
        List<ResourceGroup> details = listAccessedResourceGroups(projectId, appType, componentTypeCode);
        if (details.stream().noneMatch(v -> v.getId().equals(resourceId))) {
            throw new RdosDefineException("未授权，无法取消授权。");
        }

        LabelReversalGrantCheckResultDTO result = new LabelReversalGrantCheckResultDTO();
        if (details.size() == 1) {
            //如果项目被删除了 也可以取消授权
            AuthProjectVO authProjectVO = workSpaceProjectService.finProject(projectId, appType);
            if (null == authProjectVO) {
                result.setState(2);
            }
            result.setState(1);
            return result;
        }

        result.setState(2);

        details = details.stream()
                .filter(v -> !v.getId().equals(resourceId))
                .collect(Collectors.toList());

        // 按节点标签分组
        List<LabelResourceGroupDTO> remainAccessed = processResourcesGroupByLabel(details);
        result.setRemainAccessed(remainAccessed);
        return result;
    }

    public ResourceGroupUsedVO getResourceUsedMonitor(Long resourceId) {
        ResourceGroup resourceGroup = resourceGroupDao.getOne(resourceId, Deleted.NORMAL.getStatus());
        ResourceGroupUsedVO vo = new ResourceGroupUsedVO();
        if (resourceGroup == null) {
            return vo;
        }
        String queuePath = resourceGroup.getQueuePath();
        Queue queue = queueDao.getByClusterIdAndQueuePath(resourceGroup.getClusterId(), queuePath);
        if (queue == null) {
            return vo;
        }
        DateTime yesterdayStart = DateTime.now().plusDays(-1).withTime(0, 0, 0, 0);
        DateTime yesterdayEnd = DateTime.now().plusDays(-1).withTime(23, 59, 59, 59);

        /*
        com.dtstack.dtcenter.common.loader.yarn.client.YarnClientImpl#getYarnResource
        接口采集的queueName 实际为全路径queuePath (dtstack.pord)
        所以根据资源组查询的时候 要使用队列的queuePath 路径去匹配 console_resource_queue_used#queue_name字段
         */
        List<TimeUsedNode> yesterdayUsed = resourceQueueUsedDao.listLastTwoDayByClusterIdAndQueueName(resourceGroup.getClusterId(),
                queue.getQueuePath(), yesterdayStart.toDate(), yesterdayEnd.toDate());

        DateTime todayStart = DateTime.now().withTime(0, 0, 0, 0);

        List<TimeUsedNode> todayUsed = resourceQueueUsedDao.listLastTwoDayByClusterIdAndQueueName(resourceGroup.getClusterId(),
                queue.getQueuePath(), todayStart.toDate(), new Date(System.currentTimeMillis()));

        vo.setToday(resourceGroupStruct.toUserNodeVOs(todayUsed));
        vo.setYesterday(resourceGroupStruct.toUserNodeVOs(yesterdayUsed));
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<ResourceQueueUsed> useds) {
        resourceQueueUsedDao.batchInsert(useds);
    }

    public Optional<ResourceGroup> getResourceGroup(Long resourceId) {
        return Optional.ofNullable(resourceGroupDao.getOne(resourceId, null));
    }


    public void clearByTimeInterval(Integer timeInterVal) {
        resourceQueueUsedDao.clearByTimeInterval(timeInterVal);
    }

    public Map<Long, ResourceGroupDetail> getGroupInfo(List<Long> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return new HashMap<>();
        }
        List<ResourceGroupDetail> details = resourceGroupDao.listDetailByIds(new HashSet<>(resourceIds));
        if (!CollectionUtils.isEmpty(details)) {
            return details.stream().collect(Collectors.groupingBy(ResourceGroupDetail::getResourceId,
                    Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), groups -> groups.get(0))));
        }
        return new HashMap<>();
    }

    public void fillTaskGroupInfo(List<Long> resourceIds, List<ScheduleTaskVO> scheduleTaskVOS) {
        Map<Long, ResourceGroupDetail> groupInfo = getGroupInfo(resourceIds);
        if (MapUtils.isNotEmpty(groupInfo)) {
            for (ScheduleTaskVO scheduleTaskVO : scheduleTaskVOS) {
                ResourceGroupDetail groupDetail = groupInfo.get(scheduleTaskVO.getResourceId());
                if (groupDetail != null) {
                    scheduleTaskVO.setResourceGroupName(groupDetail.getResourceName());
                }
            }
        }
    }


    public ResourceGroup getResourceGroupByDtUicTenantId(Long dtUicTenantId) {
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(dtUicTenantId);
        if (null == clusterTenant) {
            return null;
        }
        return getResourceGroup(clusterTenant.getDefaultResourceId()).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchChangeResource(Long resourceId, Integer appType, List<Long> taskIds, Long dtUicTenantId) {
        LOGGER.info("change task {} appType {} to resource group {}", taskIds, appType, resourceId);
        scheduleTaskShadeDao.updateResourceByTaskIds(resourceId, appType, taskIds, dtUicTenantId);
        ArrayList<RdosTaskStatus> canChangeStatus = Lists.newArrayList(RdosTaskStatus.UNSUBMIT, RdosTaskStatus.CANCELED, RdosTaskStatus.AUTOCANCELED);
        List<Integer> canChangeStatusList = canChangeStatus.stream().map(RdosTaskStatus::getStatus).collect(Collectors.toList());
        canChangeStatusList.addAll(RdosTaskStatus.FROZEN_STATUS);
        canChangeStatusList.addAll(RdosTaskStatus.FAILED_STATUS);
        scheduleJobDao.updateResourceByTaskIds(resourceId, appType, taskIds, dtUicTenantId, canChangeStatusList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGrantByProject(Long projectId, Integer appType) {
        resourceGroupGrantDao.removeByProject(projectId, appType);
    }

    /**
     * 展示节点标签资源组列表
     * @param clusterId
     * @return
     */
    public List<LabelResourceGroupDTO> listLabelUserResource(Long clusterId) {
        List<ResourceGroup> resourceGroups = resourceGroupDao.listByClusterIdAndComponentTypeCode(clusterId, EComponentType.DTSCRIPT_AGENT.getTypeCode());
        if (CollectionUtils.isEmpty(resourceGroups)) {
            return Collections.emptyList();
        }
        return processResourcesGroupByLabel(resourceGroups);
    }

    private List<LabelResourceGroupDTO> processResourcesGroupByLabel(List<ResourceGroup> resourceGroups) {
        // 按节点标签分组，排序保证展示结果的稳定性
        Map<String, List<ResourceGroup>> label2UsersMap = resourceGroups.stream()
                .sorted(Comparator.comparing(ResourceGroup::getId))
                .collect(Collectors.groupingBy(ResourceGroup::getName, LinkedHashMap::new, Collectors.toList()));
        List<LabelResourceGroupDTO> result = new ArrayList<>(label2UsersMap.size());
        for (Map.Entry<String, List<ResourceGroup>> label2Users : label2UsersMap.entrySet()) {
            LabelResourceGroupDTO oneLabelResourceGroupDTO = new LabelResourceGroupDTO();
            oneLabelResourceGroupDTO.setLabel(label2Users.getKey());

            List<ResourceGroup> resourceGroupList = label2Users.getValue();
            // 展示节点标签下的所有用户
            List<LabelResourceGroupUserDTO> userList = resourceGroupList.stream().map(resourceGroup -> {
                LabelResourceGroupUserDTO userShowDTO = new LabelResourceGroupUserDTO();
                userShowDTO.setLabel(resourceGroup.getName());
                userShowDTO.setUserName(resourceGroup.getQueuePath());
                userShowDTO.setResourceId(resourceGroup.getId());
                userShowDTO.setComponentTypeCode(resourceGroup.getComponentTypeCode());
                return userShowDTO;
            }).collect(Collectors.toList());
            oneLabelResourceGroupDTO.setUserList(userList);
            result.add(oneLabelResourceGroupDTO);
        }
        return result;
    }

    /**
     * 查询项目已授权的节点标签
     * @param resourceGroupQueryParam
     * @return
     */
    public AccessedResourceGroupVO<ResourceGroupLabelDetailVO> listAccessedLabelResources(ResourceGroupQueryParam resourceGroupQueryParam) {
        Long projectId = resourceGroupQueryParam.getProjectId();
        Integer appType = resourceGroupQueryParam.getAppType();

        AuthProjectVO projectVO = workSpaceProjectService.finProject(projectId, appType);
        if (null == projectVO) {
            throw new RdosDefineException("项目不存在");
        }
        Long uicTenantId = projectVO.getDtuicTenantId();
        Cluster cluster = clusterService.getCluster(uicTenantId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(uicTenantId);


        List<ResourceGroupDetail> details = resourceGroupGrantDao.listAccessedResourceDetailByProjectId(projectId, appType, EComponentType.DTSCRIPT_AGENT.getTypeCode());
        List<ResourceGroupLabelDetailVO> labelDetails = details.stream().map(s -> {
            ResourceGroupLabelDetailVO rg = new ResourceGroupLabelDetailVO();
            rg.setLabel(s.getResourceName());
            rg.setUserName(s.getQueuePath());
            rg.setResourceId(s.getResourceId());
            rg.setIsProjectDefault(s.getIsProjectDefault());
            rg.setResourceGrantId(s.getResourceGrantId());
            rg.setProjectId(s.getProjectId());
            boolean isTenantDefault = s.getResourceId().equals(clusterTenant.getDefaultLabelResourceId());
            rg.setIsTenantDefault(isTenantDefault ? IsDefaultEnum.DEFAULT.getType() : IsDefaultEnum.NOT_DEFAULT.getType());
            return rg;
        }).collect(Collectors.toList());
        if (labelDetails.size() > 1) {
            labelDetails.sort(Comparator.comparing(ResourceGroupLabelDetailVO::getLabel));
        }
        AccessedResourceGroupVO<ResourceGroupLabelDetailVO> result = new AccessedResourceGroupVO<>();
        result.setAccessedResources(labelDetails);
        return result;
    }

    /**
     * 设置项目级默认节点标签
     * @param resourceGrantId
     * @param isProjectDefault
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeProjectDefaultResource(Long resourceGrantId, Integer isProjectDefault) {
        ResourceGroupGrant resourceGroupGrant = resourceGroupGrantDao.getById(resourceGrantId);
        if (resourceGroupGrant == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        boolean result = false;
        if (IsDefaultEnum.DEFAULT.getType().equals(isProjectDefault)) {
            // 将自己修改为项目级默认，其余的默认要取消掉
            result = resourceGroupGrantDao.changeProjectDefault(resourceGrantId, isProjectDefault);
            List<ResourceGroupDetail> details = resourceGroupGrantDao.listAccessedResourceDetailByProjectId(resourceGroupGrant.getProjectId(),
                    resourceGroupGrant.getAppType(), EComponentType.DTSCRIPT_AGENT.getTypeCode());
            List<Long> revertProjectDefaultIds = details.stream()
                    .filter(s -> !resourceGrantId.equals(s.getResourceGrantId()))
                    .filter(s -> IsDefaultEnum.DEFAULT.getType().equals(s.getIsProjectDefault()))
                    .map(ResourceGroupDetail::getResourceGrantId)
                    .collect(Collectors.toList());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(revertProjectDefaultIds)) {
                resourceGroupGrantDao.revertProjectDefault(revertProjectDefaultIds);
            }
        } else {
            // 取消掉自己的项目级默认
            result = resourceGroupGrantDao.changeProjectDefault(resourceGrantId, isProjectDefault);
        }
        return result;
    }

    /**
     * 根据 id 列表获取节点标签
     * @param resourceIds
     * @return
     */
    public AccessedResourceGroupVO<ResourceGroupLabelDetailVO> listLabelResourceByIds(List<Long> resourceIds) {
        AccessedResourceGroupVO<ResourceGroupLabelDetailVO> result = new AccessedResourceGroupVO<>();
        if (CollectionUtils.isEmpty(resourceIds)) {
            result.setAccessedResources(Collections.emptyList());
            return result;
        }
        List<ResourceGroupLabelDetailVO> labelDetails = resourceGroupDao.listByIds(resourceIds).stream().map(s -> {
            ResourceGroupLabelDetailVO t = new ResourceGroupLabelDetailVO();
            t.setLabel(s.getName());
            t.setUserName(s.getQueuePath());
            t.setResourceId(s.getId());
            return t;
        }).collect(Collectors.toList());
        result.setAccessedResources(labelDetails);
        return result;
    }

    public ResourceGroupLabelDetailVO findAccessedByLabelAndUser(ResourceGroupQueryParam resourceGroupQueryParam) {
        Long projectId = resourceGroupQueryParam.getProjectId();
        Integer appType = resourceGroupQueryParam.getAppType();

        AuthProjectVO projectVO = workSpaceProjectService.finProject(projectId, appType);
        if (null == projectVO) {
            throw new RdosDefineException("项目不存在");
        }
        Long uicTenantId = projectVO.getDtuicTenantId();
        Cluster cluster = clusterService.getCluster(uicTenantId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        resourceGroupQueryParam.setComponentTypeCode(EComponentType.DTSCRIPT_AGENT.getTypeCode());
        ResourceGroupDetail result = resourceGroupGrantDao.findAccessedResourceDetail(resourceGroupQueryParam);
        return Optional.ofNullable(result).map(s -> {
            ResourceGroupLabelDetailVO rg = new ResourceGroupLabelDetailVO();
            rg.setLabel(s.getResourceName());
            rg.setUserName(s.getQueuePath());
            rg.setResourceId(s.getResourceId());
            rg.setIsProjectDefault(s.getIsProjectDefault());
            rg.setResourceGrantId(s.getResourceGrantId());
            rg.setProjectId(s.getProjectId());
            return rg;
        }).orElse(null);
    }

    public PageResult<List<GrantedUserGroupVO>> pageGrantedUserGroups(TrinoResourceGroupGrantPageParam param) {

        PageQuery<ResourceGroupGrantSearchDTO> pageQuery = new PageQuery<>(param.getCurrentPage(), param.getPageSize(),
                "gmt_modified", Sort.DESC.name());
        ResourceGroup resourceGroup = resourceGroupDao.getTrinoResourceByName(param.getResourceName(),param.getClusterId());
        if(null == resourceGroup){
            return PageResult.EMPTY_PAGE_RESULT;
        }
        ResourceGroupGrantSearchDTO dto = new ResourceGroupGrantSearchDTO();
        String tenantName = param.getTenantName();
        if (StringUtils.isNotBlank(tenantName)) {
            List<UserFullTenantVO> tenantByFullName = tenantService.getTenantByFullName(tenantName);
            dto.setDtuicTenantIds(tenantByFullName.stream()
                    .map(UserFullTenantVO::getTenantId)
                    .collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(tenantByFullName)) {
                return PageResult.EMPTY_PAGE_RESULT;
            }
        }
        if(StringUtils.isNotBlank(param.getGroupName())){
            List<GroupVO> groupVOS = userGroupService.queryUserGroupListByName(param.getGroupName());
            if(CollectionUtils.isEmpty(groupVOS)){
                return PageResult.EMPTY_PAGE_RESULT;
            }
            dto.setUserGroupIds(groupVOS.stream().map(GroupVO::getGroupId).collect(Collectors.toList()));
        }
        dto.setResourceId(resourceGroup.getId());
        Integer count = resourceGroupGrantDao.generalCount(dto);
        if(count ==0){
            return PageResult.EMPTY_PAGE_RESULT;
        }
        pageQuery.setModel(dto);
        List<GrantedProjectDTO> grantedProjectDTOS = resourceGroupGrantDao.generalQuery(pageQuery);

        Set<Long> tenantIds = grantedProjectDTOS.stream().map(GrantedProjectDTO::getTenantId).collect(Collectors.toSet());
        Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);
        Set<Long> groupIds = grantedProjectDTOS.stream().map(GrantedProjectDTO::getEngineProjectId).collect(Collectors.toSet());
        List<GroupVO> userGroupByGroupIds = userGroupService.getUserGroupByGroupIds(Lists.newArrayList(groupIds));
        Map<Long, GroupVO> groupVOMap = userGroupByGroupIds.stream().collect(Collectors.toMap(GroupVO::getGroupId, Function.identity()));
        List<GrantedUserGroupVO> voRes = new ArrayList<>();
        for (GrantedProjectDTO d : grantedProjectDTOS) {
            GroupVO groupVO = groupVOMap.get(d.getEngineProjectId());
            if(null == groupVO){
                continue;
            }
            GrantedUserGroupVO vo = new GrantedUserGroupVO();
            TenantDeletedVO tenantDetailVO = tenantMap.get(d.getTenantId());
            vo.setTenantName(null == tenantDetailVO ? "" : tenantDetailVO.getTenantName());
            vo.setUserGroupName(groupVO.getGroupName());
            vo.setGroupId(groupVO.getGroupId());
            vo.setTenantId(null == tenantDetailVO ?  0 : tenantDetailVO.getTenantId());
            voRes.add(vo);
        }
        return new PageResult<>(voRes,count,pageQuery);

    }

    /**
     * trino资源组绑定用户组
     * @param clusterId
     * @param resourceName
     * @param dtUicTenantId
     * @param groupIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void grantToUserGroup(Long clusterId, String resourceName, Long dtUicTenantId, List<Long> groupIds) {

        //根据资源组名称和clusterId查询资源组
        ResourceGroup resourceByName = resourceGroupDao.getTrinoResourceByName(resourceName, clusterId);
        if(null == resourceByName){
            throw new BizException("该资源组不存在:",resourceName);
        }
        PageQuery<ResourceGroupGrantSearchDTO> pageQuery = new PageQuery<>();
        ResourceGroupGrantSearchDTO grantSearchDTO = new ResourceGroupGrantSearchDTO();
        grantSearchDTO.setResourceId(resourceByName.getId());
        grantSearchDTO.setUserGroupIds(groupIds);
        grantSearchDTO.setDtuicTenantIds(Lists.newArrayList(dtUicTenantId));
        pageQuery.setModel(grantSearchDTO);
        List<GrantedProjectDTO> grantedProjectDTOS = resourceGroupGrantDao.generalQuery(pageQuery);
        if(!CollectionUtils.isEmpty(grantedProjectDTOS)){
            throw new BizException("所选用户组已经绑定了该资源组,groupIds:",groupIds);
        }
        List<ResourceGroupGrant> resourceGroupGrants = Lists.newArrayList();
        for (Long groupId : groupIds) {
            ResourceGroupGrant resourceGroupGrant = new ResourceGroupGrant();
            resourceGroupGrant.setResourceId(resourceByName.getId());
            resourceGroupGrant.setDtuicTenantId(dtUicTenantId);
            resourceGroupGrant.setProjectId(1L);
            resourceGroupGrant.setEngineProjectId(groupId);
            resourceGroupGrant.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            resourceGroupGrant.setGmtModified(new Timestamp(System.currentTimeMillis()));
            resourceGroupGrants.add(resourceGroupGrant);
        }
        resourceGroupGrantDao.insertAll(resourceGroupGrants);
        //调用datasourcex，绑定用户组到底层trino资源组上
        consoleService.bindTrinoResource(clusterId,dtUicTenantId,resourceName,groupIds,0);
    }

    /**
     * 用户组换绑资源组
     * @param clusterId
     * @param resourceId
     * @param dtUicTenantId
     * @param groupId
     * @param oldResourceName
     */
    @Transactional(rollbackFor = Exception.class)
    public void reversalUserGroupGrant(Long clusterId,Long resourceId, Long dtUicTenantId, Long groupId,String oldResourceName) {

        ResourceGroup group = resourceGroupDao.getOne(resourceId, 0);
        if(group == null){
            throw new BizException("所选资源组不存在，resourceId:",resourceId);
        }
        ResourceGroup resourceGroup = resourceGroupDao.getTrinoResourceByName(oldResourceName, clusterId);
        if(resourceGroup == null){
            throw new BizException("资源组不存在，oldResourceName:",oldResourceName);
        }
        resourceGroupGrantDao.updateByResourceIdAndGroupId(resourceGroup.getId(),resourceId,groupId);
        //调用datasourcex，解除绑定用户组
        consoleService.bindTrinoResource(clusterId,dtUicTenantId,group.getName(),Lists.newArrayList(groupId),2);
    }

    /**
     * 获取未绑定该资源组的用户组信息
     * @param resourceName
     * @param dtUicTenantId
     * @return
     */
    public List<GroupVO> listUnBindUserGroups(String resourceName, Long dtUicTenantId) {

        //获取该租户下的所有用户组
        List<GroupVO> allUserGroups = userGroupService.getUserGroupListByTenantId(dtUicTenantId);
        String parentName;
        if(resourceName.split("-").length>=3){
            parentName = resourceName.substring(0,resourceName.lastIndexOf("-"));
        }else {
            return allUserGroups;
        }
        //获取该资源组的父资源组或父资源组下的其他子资源组已经绑定了的用户组、过滤已经绑定了的用户组
        List<GrantedProjectDTO> grantedProjectDTOS = resourceGroupGrantDao.fuzzyQueryByResourceName(parentName,dtUicTenantId);
        List<Long> groupIds = grantedProjectDTOS.stream().map(GrantedProjectDTO::getEngineProjectId).collect(Collectors.toList());
        return allUserGroups.stream().filter(group ->! groupIds.contains(group.getGroupId())).collect(Collectors.toList());
    }


    public TrinoOverView getTrinoOverView(Long clusterId,Long dtuicTenantId) {


        return consoleService.getTrinoOverView(clusterId, dtuicTenantId);

    }



    /**
     * 用户组换绑资源组时，获取用户组可以绑定的trino资源组
     * 只能交接给同一级的子资源组
     * @param clusterId
     * @return
     */
    public List<ResourceGroupListVO> getCanBindTrinoResources(Long clusterId,String resourceName,Long groupId,Long dtuicTenantId) {

        List<ResourceGroup> resourceGroups = resourceGroupDao.listAllTrinoResource(1, clusterId,1);
        String parentName;
        if(resourceName.split("-").length>=3) {
            //上一级名称
            parentName = resourceName.substring(0, resourceName.lastIndexOf("-"));
        }else{
            return Lists.newArrayList();
        }
        List<ResourceGroup> resourceGroupList = resourceGroups.stream().
                filter(r -> r.getName().startsWith(parentName) && !r.getName().equals(resourceName))
                .collect(Collectors.toList());
        return resourceGroupStruct.toListVOs(resourceGroupList);
    }

    public static void main(String[] args) {
        String name = "a-b-c";
        String parentName = name.substring(0, name.lastIndexOf("-"));
        System.out.println(parentName);
    }
}
