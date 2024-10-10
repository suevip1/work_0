package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.vo.ComponentUserVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ComponentUserDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.ResourceGroupGrantDao;
import com.dtstack.engine.dto.LabelGrantedProjectDTO;
import com.dtstack.engine.master.dto.ComponentUserAffectProjectDTO;
import com.dtstack.engine.master.dto.ComponentUserRefreshDTO;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ComponentUser;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupGrant;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-20 22:49
 */
@Service
public class ConsoleComponentUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleComponentUserService.class);

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentUserDao componentUserDao;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ResourceGroupDao resourceGroupDao;

    @Autowired
    private ResourceGroupGrantDao resourceGroupGrantDao;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private WorkSpaceProjectService workSpaceProjectService;

    /**
     * 删除节点标签会影响到的项目
     *
     * @param componentUserList
     * @return
     */
    public ComponentUserAffectProjectDTO findAffectedProjects(List<ComponentUserVO> componentUserList) {
        List<ComponentUser> componentUsers = stretchComponentUser(componentUserList);
        if (CollectionUtils.isEmpty(componentUsers)) {
            return null;
        }
        ComponentUserVO componentUserVO = componentUserList.get(0);
        Long clusterId = componentUserVO.getClusterId();
        Integer componentTypeCode = componentUserVO.getComponentTypeCode();
        // 从 db 捞旧数据
        List<ComponentUser> dbComponentUserList = componentUserDao.getComponentUserByCluster(clusterId, componentTypeCode);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        // 跟新记录比对，区分增删改的记录
        ComponentUserRefreshDTO componentUserRefreshDTO = classifyCRUD(componentUsers, dbComponentUserList, now);
        List<ComponentUser> deleteComponentUsers = componentUserRefreshDTO.getDelete();
        if (CollectionUtils.isEmpty(deleteComponentUsers)) {
            return null;
        }

        // 找删掉的节点标签对应的受影响的资源组
        List<ResourceGroup> affectLabelResourceGroups = findLabelResourceGroup(deleteComponentUsers);
        if (CollectionUtils.isEmpty(affectLabelResourceGroups)) {
            return null;
        }

        Map<Long, ResourceGroup> affectLabelResourceGroupsMap = affectLabelResourceGroups.stream().collect(Collectors.toMap(ResourceGroup::getId, Function.identity(), (x, y) -> y));
        // 受影响的资源组 id 列表
        List<Long> affectLabelResourceGroupIds = new ArrayList<>(affectLabelResourceGroupsMap.keySet());

        // 受影响的授权关系
        List<ResourceGroupGrant> affectResourceGroupGrants = resourceGroupGrantDao.listDistinctProjectsByResourceIdAndAppType(affectLabelResourceGroupIds, AppType.RDOS.getType());
        if (CollectionUtils.isEmpty(affectResourceGroupGrants)) {
            return null;
        }
        ComponentUserAffectProjectDTO affectProjectDTO = new ComponentUserAffectProjectDTO();
        fillDetailComponentUserAffectProject(affectProjectDTO, affectResourceGroupGrants);
        affectProjectDTO.setLabels(affectLabelResourceGroups.stream().map(ResourceGroup::getName).collect(Collectors.toSet()));
        affectProjectDTO.setUserNames(affectLabelResourceGroups.stream().map(ResourceGroup::getQueuePath).collect(Collectors.toSet()));
        return affectProjectDTO;
    }

    /**
     * 删除组件时判断服务器用户的使用情况
     * @param clusterId
     * @param componentTypeCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkAndDeleteByComponentAndCluster(Long clusterId, Integer componentTypeCode) {
        List<ComponentUser> dbComponentUserList = componentUserDao.getComponentUserByCluster(clusterId, componentTypeCode);
        if (CollectionUtils.isEmpty(dbComponentUserList)) {
            return;
        }
        List<ResourceGroup> deleteLabelResourceGroups = findLabelResourceGroup(dbComponentUserList);
        if (CollectionUtils.isEmpty(deleteLabelResourceGroups)) {
            return;
        }
        if (CollectionUtils.isNotEmpty(deleteLabelResourceGroups)) {
            // 集群绑定的所有租户
            List<ClusterTenant> clusterTenants = clusterTenantDao.listByClusterId(clusterId);
            Map<Long, ResourceGroup> deleteLabelResourceGroupMap = deleteLabelResourceGroups.stream().collect(Collectors.toMap(ResourceGroup::getId, Function.identity(), (x, y) -> y));
            List<Long> deleteLabelResourceGroupIds = new ArrayList<>(deleteLabelResourceGroupMap.keySet());
            // 判断被删的资源组是否是租户的默认节点标签资源组
            List<Long> clusterTenantDefaultLabelResourceIds = clusterTenants.stream().map(ClusterTenant::getDefaultLabelResourceId).filter(Objects::nonNull).collect(Collectors.toList());
            for (Long deleteLabelResourceGroupId : deleteLabelResourceGroupIds) {
                if (clusterTenantDefaultLabelResourceIds.contains(deleteLabelResourceGroupId)) {
                    ResourceGroup deleteLabelResourceGroup = deleteLabelResourceGroupMap.get(deleteLabelResourceGroupId);
                    throw new RdosDefineException(String.format("节点标签:%s，服务器用户:%s，是租户默认节点标签，不允许删除", deleteLabelResourceGroup.getName(), deleteLabelResourceGroup.getQueuePath()));
                }
            }
            List<ResourceGroupGrant> deleteResourceGroupGrants = resourceGroupGrantDao.listByResourceIdAndAppType(deleteLabelResourceGroupIds, AppType.RDOS.getType());
            for (ResourceGroupGrant deleteResourceGroupGrant : deleteResourceGroupGrants) {
                ResourceGroup deleteLabelResourceGroup = deleteLabelResourceGroupMap.get(deleteResourceGroupGrant.getResourceId());
                throw new RdosDefineException(String.format("节点标签:%s，服务器用户:%s，已被授权，不允许删除", deleteLabelResourceGroup.getName(), deleteLabelResourceGroup.getQueuePath()));
            }
        }

        // 通过所有的校验后再删除
        componentUserDao.deleteByIds(dbComponentUserList.stream().map(ComponentUser::getId).collect(Collectors.toList()));
    }

    /**
     * 保存节点标签+服务器用户
     *
     * @param componentUserList
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveComponentUser(List<ComponentUserVO> componentUserList) {
        ComponentUserVO componentUserVO = componentUserList.get(0);
        Long clusterId = componentUserVO.getClusterId();
        Integer componentTypeCode = componentUserVO.getComponentTypeCode();

        // 前提是先有组件，才能配置服务器用户，否则创建一堆脏数据资源组出来
        Component dtScriptComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.DTSCRIPT_AGENT.getTypeCode(), null, null);
        if (dtScriptComponent == null) {
            throw new RdosDefineException("请先保存 DtScript Agent 组件，否则无法对服务器用户进行资源管理");
        }

        // 将 label 分组的记录改成按 user 铺平
        List<ComponentUser> componentUsers = stretchComponentUser(componentUserList);
        if (CollectionUtils.isEmpty(componentUsers)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        // 从 db 捞旧数据
        List<ComponentUser> dbComponentUserList = componentUserDao.getComponentUserByCluster(clusterId, componentTypeCode);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        // 跟新记录比对，区分增删改的记录
        ComponentUserRefreshDTO componentUserRefreshDTO = classifyCRUD(componentUsers, dbComponentUserList, now);
        LOGGER.info("componentUserRefreshDTO:{}", componentUserRefreshDTO.prettyLog());

        List<ClusterTenant> clusterTenants = clusterTenantDao.listByClusterId(clusterId);

        // 处理节点标签资源组删除的情况
        List<ComponentUser> deleteComponentUsers = dealDeleteComponentUsers(componentUserRefreshDTO.getDelete(), clusterTenants);

        // 处理节点标签资源组新增的情况
        List<ComponentUser> createComponentUsers = dealAddComponentUsers(now, componentUserRefreshDTO.getCreate(), clusterTenants);

        // 处理节点标签资源组修改为 default 的情况
        dealUpdateToDefaultComponentUsers(componentUserRefreshDTO.getUpdateToDefault(), clusterTenants);

        // 资源组处理完毕，授权关系处理完毕，最后处理 componentUser
        if (CollectionUtils.isNotEmpty(createComponentUsers)) {
            componentUserDao.batchInsert(createComponentUsers);
        }
        List<ComponentUser> updateComponentUsers = componentUserRefreshDTO.getUpdate();
        if (CollectionUtils.isNotEmpty(updateComponentUsers)) {
            componentUserDao.batchUpdate(updateComponentUsers);
        }
        if (CollectionUtils.isNotEmpty(deleteComponentUsers)) {
            List<Long> deleteComponentUserIds = deleteComponentUsers.stream().map(ComponentUser::getId).collect(Collectors.toList());
            componentUserDao.deleteByIds(deleteComponentUserIds);
        }
    }

    /**
     * 处理节点标签资源组修改为 default 的情况
     * @param updateToDefaultComponentUsers
     * @param clusterTenants
     */
    private void dealUpdateToDefaultComponentUsers(List<ComponentUser> updateToDefaultComponentUsers, List<ClusterTenant> clusterTenants) {
        if (CollectionUtils.isNotEmpty(updateToDefaultComponentUsers)) {
            // 如果修改里面有默认而原先不是默认的，则对租户下离线所有项目进行授权
            List<ComponentUser> alsoNeedGrantComponentUsers = updateToDefaultComponentUsers.stream()
                    .filter(t -> StringUtils.isNotBlank(t.getUserName()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(alsoNeedGrantComponentUsers)) {
                List<ResourceGroup> labelResourceGroup = findLabelResourceGroup(alsoNeedGrantComponentUsers);
                Table<String, String, ResourceGroup> labelResourceGroupTable = transResourceGroups2Table(labelResourceGroup);
                grantComponentUsers(clusterTenants, alsoNeedGrantComponentUsers, labelResourceGroupTable);
            }
        }
    }

    /**
     * 处理节点标签资源组删除的情况
     * @param deleteComponentUsers
     * @param clusterTenants
     * @return
     */
    private List<ComponentUser> dealDeleteComponentUsers(List<ComponentUser> deleteComponentUsers, List<ClusterTenant> clusterTenants) {
        // 需要删除的资源组
        List<ResourceGroup> deleteLabelResourceGroups = findLabelResourceGroup(deleteComponentUsers);
        if (CollectionUtils.isNotEmpty(deleteLabelResourceGroups)) {
            Map<Long, ResourceGroup> deleteLabelResourceGroupMap = deleteLabelResourceGroups.stream().collect(Collectors.toMap(ResourceGroup::getId, Function.identity(), (x, y) -> y));
            List<Long> deleteLabelResourceGroupIds = new ArrayList<>(deleteLabelResourceGroupMap.keySet());
            // 判断被删的资源组是否是租户的默认节点标签
            Map<Long, Set<Long>> defaultLabelResourceId2TenantsMap = clusterTenants.stream()
                    .filter(t -> Objects.nonNull(t.getDefaultLabelResourceId()))
                    .collect(Collectors.groupingBy(ClusterTenant::getDefaultLabelResourceId,
                            Collectors.mapping(ClusterTenant::getDtUicTenantId, Collectors.toSet())));

            // 拼接提示信息
            Set<Long> allAffectTenantIds = new HashSet<>();
            StringBuilder allAffectLabels = new StringBuilder();
            for (Long deleteLabelResourceGroupId : deleteLabelResourceGroupIds) {
                Set<Long> affectTenantIds = defaultLabelResourceId2TenantsMap.get(deleteLabelResourceGroupId);
                if (CollectionUtils.isNotEmpty(affectTenantIds)) {
                    allAffectTenantIds.addAll(affectTenantIds);
                    ResourceGroup deleteLabelResourceGroup = deleteLabelResourceGroupMap.get(deleteLabelResourceGroupId);
                    allAffectLabels.append(String.format("节点标签:%s，服务器用户:%s;", deleteLabelResourceGroup.getName(), deleteLabelResourceGroup.getQueuePath()));
                }
            }
            if (CollectionUtils.isNotEmpty(allAffectTenantIds)) {
                Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(new ArrayList<>(allAffectTenantIds));
                String tenantNames = tenantMap.values().stream().map(TenantDeletedVO::getTenantName).collect(Collectors.joining(","));
                throw new RdosDefineException(String.format(allAffectLabels + " 是租户 %s 的默认节点标签下的默认服务器用户，无法删除", tenantNames));
            }

            Map<Long, ClusterTenant> uicTenantMap = clusterTenants.stream()
                    .collect(Collectors.toMap(ClusterTenant::getDtUicTenantId, Function.identity(), (k1, k2) -> k2));
            // 改变授权关系，交接到租户绑定的默认节点标签
            List<ResourceGroupGrant> deleteResourceGroupGrants = resourceGroupGrantDao.listByResourceIdAndAppType(deleteLabelResourceGroupIds, AppType.RDOS.getType());
            // 校验租户是否绑定默认节点标签
            Set<Long> lostDefaultLabelTenants = new HashSet<>();
            // 给用户提示信息
            for (ResourceGroupGrant deleteResourceGroupGrant : deleteResourceGroupGrants) {
                ClusterTenant clusterTenant = uicTenantMap.get(deleteResourceGroupGrant.getDtuicTenantId());
                if (clusterTenant == null || clusterTenant.getDefaultLabelResourceId() == null) {
                    LOGGER.info("resourceGrantId:{} 没有租户默认节点标签可以交接", deleteResourceGroupGrant.getId());
                    // 没有默认节点标签的租户
                    lostDefaultLabelTenants.add(deleteResourceGroupGrant.getDtuicTenantId());
                }
            }
            if (!lostDefaultLabelTenants.isEmpty()) {
                Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(new ArrayList<>(lostDefaultLabelTenants));
                String lostDefaultLabelTenantNames = tenantMap.values().stream().map(TenantDeletedVO::getTenantName).collect(Collectors.joining(","));
                throw new RdosDefineException(String.format("租户 %s 没有默认节点标签可以交接，请核实后重试", lostDefaultLabelTenantNames));
            }

            for (ResourceGroupGrant deleteResourceGroupGrant : deleteResourceGroupGrants) {
                ClusterTenant clusterTenant = uicTenantMap.get(deleteResourceGroupGrant.getDtuicTenantId());
                // 容错处理
                if (deleteResourceGroupGrant.getResourceId().equals(clusterTenant.getDefaultLabelResourceId())) {
                    throw new RdosDefineException(String.format("resourceGrantId:%s 使用了租户默认节点标签，不允许删除，请换绑租户默认节点标签后再尝试删除", deleteResourceGroupGrant.getId()));
                }
                // 需要确保租户默认的资源组没有被取消授权
                boolean accessedByProject = resourceGroupService.isAccessedByProject(clusterTenant.getDefaultLabelResourceId(), AppType.RDOS.getType(), deleteResourceGroupGrant.getProjectId());
                if (!accessedByProject) {
                    throw new RdosDefineException(String.format("reourceId: %s 被删除，项目 id: %s 需要交接到默认节点资源组:%s，但并未授权默认节点资源组，请授权后重试", deleteResourceGroupGrant.getResourceId(),
                            deleteResourceGroupGrant.getProjectId(), clusterTenant.getDefaultLabelResourceId()));
                }
                resourceGroupService.reversalGrant(deleteResourceGroupGrant.getResourceId(), deleteResourceGroupGrant.getProjectId(), clusterTenant.getDefaultLabelResourceId(),
                        AppType.RDOS.getType(), EComponentType.DTSCRIPT_AGENT.getTypeCode());
            }
            // 交接完毕，删除资源组
            resourceGroupDao.batchRemove(deleteLabelResourceGroupIds);
        }
        return deleteComponentUsers;
    }

    /**
     * 处理节点标签资源组新增的情况
     * @param now
     * @param createComponentUsers
     * @param clusterTenants
     * @return
     */
    private List<ComponentUser> dealAddComponentUsers(Timestamp now, List<ComponentUser> createComponentUsers, List<ClusterTenant> clusterTenants) {
        List<ResourceGroup> addResourceGroups = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(createComponentUsers)) {
            List<ComponentUser> createNameNotEmptyComponentUsers = createComponentUsers.stream()
                    .filter(t -> StringUtils.isNotBlank(t.getUserName()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(createNameNotEmptyComponentUsers)) {
                // 增加资源组
                addResourceGroups = resourceGroupService.addBatchAgentLabelResource(createNameNotEmptyComponentUsers, now);
                Table<String, String, ResourceGroup> resourceGroupTable = transResourceGroups2Table(addResourceGroups);

                // 如果新增里面有默认的，对租户下离线所有项目进行授权
                List<ComponentUser> needGrantComponentUsers = createNameNotEmptyComponentUsers.stream()
                        .filter(t -> Boolean.TRUE.equals(t.getIsDefault())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(needGrantComponentUsers)) {
                    grantComponentUsers(clusterTenants, needGrantComponentUsers, resourceGroupTable);
                }
            }
        }
        return createComponentUsers;
    }

    /**
     * 解绑只解除授权关系
     * @param clusterId
     * @param dtUicTenantId
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindTenant(Long clusterId, Long dtUicTenantId) {
        List<ComponentUser> dbComponentUserList = componentUserDao.getComponentUserByCluster(clusterId, EComponentType.DTSCRIPT_AGENT.getTypeCode());
        if (CollectionUtils.isEmpty(dbComponentUserList)) {
            return;
        }
        List<ResourceGroup> labelResourceGroup = findLabelResourceGroup(dbComponentUserList);
        if (CollectionUtils.isEmpty(labelResourceGroup)) {
            return;
        }
        List<Long> resourceIds = labelResourceGroup.stream().map(ResourceGroup::getId).collect(Collectors.toList());
        int results = resourceGroupGrantDao.removeByTenantIdAndResourceIds(dtUicTenantId, resourceIds);
        LOGGER.info("unbindTenant, clusterId:{}, dtUicTenantId:{}, resourceIds:{}, affected rows:{}", clusterId, dtUicTenantId,
                resourceIds, results);
    }

    private void fillDetailComponentUserAffectProject(ComponentUserAffectProjectDTO affectProjectDTO, List<ResourceGroupGrant> affectResourceGroupGrants) {
        // 租户信息
        List<Long> tenantIds = affectResourceGroupGrants.stream().map(ResourceGroupGrant::getDtuicTenantId).collect(Collectors.toList());
        Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);

        // 项目信息
        Map<Integer, List<Long>> appProjectMap = affectResourceGroupGrants.stream().collect(Collectors.groupingBy(ResourceGroupGrant::getAppType,
                Collectors.mapping(ResourceGroupGrant::getProjectId, Collectors.toList())));
        Table<Integer, Long, AuthProjectVO> projectInfoTable = workSpaceProjectService.getProjectGroupAppType(appProjectMap);

        List<LabelGrantedProjectDTO> gts = new ArrayList<>(affectResourceGroupGrants.size());
        for (ResourceGroupGrant affectResourceGroupGrant : affectResourceGroupGrants) {
            LabelGrantedProjectDTO gt = new LabelGrantedProjectDTO();
            gt.setTenantId(affectResourceGroupGrant.getDtuicTenantId());
            TenantDeletedVO tenantDetailVO = tenantMap.get(affectResourceGroupGrant.getDtuicTenantId());
            gt.setTenantName(null == tenantDetailVO ? "" : tenantDetailVO.getTenantName());
            gt.setAppType(affectResourceGroupGrant.getAppType());
            gt.setProductName(AppType.getValue(affectResourceGroupGrant.getAppType()).getName());
            gt.setProjectId(affectResourceGroupGrant.getProjectId());
            AuthProjectVO authProjectVO = projectInfoTable.get(affectResourceGroupGrant.getAppType(), affectResourceGroupGrant.getProjectId());
            gt.setProjectName(null == authProjectVO ? "" : authProjectVO.getProjectAlias());
            gts.add(gt);
        }
        affectProjectDTO.setGrantedProjects(gts);
    }

    /**
     * 将节点标签授权给租户下的所有项目
     *
     * @param clusterTenants
     * @param alsoNeedGrantComponentUsers
     * @param labelResourceGroupTable
     */
    private void grantComponentUsers(List<ClusterTenant> clusterTenants, List<ComponentUser> alsoNeedGrantComponentUsers, Table<String, String, ResourceGroup> labelResourceGroupTable) {
        for (ComponentUser alsoNeedGrantComponentUser : alsoNeedGrantComponentUsers) {
            ResourceGroup resourceGroup = labelResourceGroupTable.get(alsoNeedGrantComponentUser.getLabel(), alsoNeedGrantComponentUser.getUserName());
            // 容错性处理
            if (resourceGroup == null) {
                LOGGER.warn("needGrantResourceGroup not exist, label:{}, userName:{}", alsoNeedGrantComponentUser.getLabel(), alsoNeedGrantComponentUser.getUserName());
                continue;
            }
            // 授权所有绑定租户下的所有项目
            for (ClusterTenant boundedTenant : clusterTenants) {
                resourceGroupService.grantToProject(resourceGroup.getId(), boundedTenant.getDtUicTenantId(), AppType.RDOS, -1L);
            }
        }
    }

    public List<ResourceGroup> findLabelResourceGroup(List<ComponentUser> componentUsers) {
        if (CollectionUtils.isEmpty(componentUsers)) {
            return Collections.emptyList();
        }
        List<ResourceGroup> result = new ArrayList<>(componentUsers.size());
        for (ComponentUser componentUser : componentUsers) {
            // 用户名为空是不会创建对应的资源组的
            if (StringUtils.isBlank(componentUser.getUserName())) {
                continue;
            }
            ResourceGroup labelResourceGroup = resourceGroupDao.findLabelByClusterAndName(componentUser.getClusterId(),
                    componentUser.getComponentTypeCode(), componentUser.getLabel(), componentUser.getUserName());
            if (labelResourceGroup != null) {
                result.add(labelResourceGroup);
            }
        }
        return result;
    }

    /**
     * 区分本次增、删、改的记录，特别地，需要处理修改为 default 的记录，因为要增加授权的操作
     *
     * @param newComponentUsers
     * @param dbComponentUserList
     * @param now
     * @return
     */
    private ComponentUserRefreshDTO classifyCRUD(List<ComponentUser> newComponentUsers, List<ComponentUser> dbComponentUserList, Timestamp now) {
        List<ComponentUser> createComponentUsers = new ArrayList<>(newComponentUsers.size());
        List<ComponentUser> updateComponentUsers = new ArrayList<>(newComponentUsers.size());
        List<ComponentUser> updateToDefaultComponentUsers = new ArrayList<>(newComponentUsers.size());
        List<ComponentUser> deleteComponentUsers = Collections.emptyList();

        Table<String, String, ComponentUser> dbComponentUserTable = transComponentUsers2Table(dbComponentUserList);
        for (ComponentUser newComponentUser : newComponentUsers) {
            ComponentUser dbComponentUser = dbComponentUserTable.get(newComponentUser.getLabel(), newComponentUser.getUserName());
            if (dbComponentUser == null) {
                // 新增的记录
                createComponentUsers.add(newComponentUser);
            } else {
                // 修改的记录
                newComponentUser.setId(dbComponentUser.getId());
                newComponentUser.setGmtModified(now);
                updateComponentUsers.add(newComponentUser);
                // 修改为 default 的记录
                if (Boolean.TRUE.equals(newComponentUser.getIsDefault())) {
                    updateToDefaultComponentUsers.add(newComponentUser);
                }
                // 移除后剩下的就是需要删掉的记录
                dbComponentUserTable.remove(newComponentUser.getLabel(), newComponentUser.getUserName());
            }
        }
        if (!dbComponentUserTable.isEmpty()) {
            // 需要删掉的记录
            deleteComponentUsers = new ArrayList<>(dbComponentUserTable.values());
        }

        return new ComponentUserRefreshDTO.Builder()
                .setCreate(createComponentUsers)
                .setUpdate(updateComponentUsers)
                .setUpdateToDefault(updateToDefaultComponentUsers)
                .setDelete(deleteComponentUsers)
                .build();
    }

    /**
     * 将按 label 分组的 user 记录改成按 user 铺平
     *
     * @param componentUserList
     * @return
     */
    private List<ComponentUser> stretchComponentUser(List<ComponentUserVO> componentUserList) {
        if (CollectionUtils.isEmpty(componentUserList)) {
            return Collections.emptyList();
        }
        List<ComponentUser> result = new ArrayList<>(componentUserList.size());
        // 构建实例
        for (ComponentUserVO userVO : componentUserList) {
            if (CollectionUtils.isEmpty(userVO.getComponentUserInfoList())) {
                ComponentUser emptyUser = new ComponentUser();
                emptyUser.setPassword(StringUtils.EMPTY);
                emptyUser.setUserName(StringUtils.EMPTY);
                emptyUser.setLabel(userVO.getLabel());
                emptyUser.setLabelIp(transferIpStr(userVO.getSidecar()));
                emptyUser.setIsDefault(userVO.getIsDefault());
                emptyUser.setClusterId(userVO.getClusterId());
                emptyUser.setComponentTypeCode(userVO.getComponentTypeCode());
                result.add(emptyUser);
            }
            if (CollectionUtils.isEmpty(userVO.getComponentUserInfoList())) {
                continue;
            }
            for (ComponentUserVO.ComponentUserInfo userInfo : userVO.getComponentUserInfoList()) {
                ComponentUser componentUser = new ComponentUser();
                componentUser.setClusterId(userVO.getClusterId());
                componentUser.setComponentTypeCode(userVO.getComponentTypeCode());
                componentUser.setIsDefault(userVO.getIsDefault());
                componentUser.setLabel(userVO.getLabel());
                componentUser.setLabelIp(transferIpStr(userVO.getSidecar()));
                componentUser.setUserName(userInfo.getUserName());
                componentUser.setPassword(Base64Util.baseEncode(userInfo.getPassword()));
                result.add(componentUser);
            }
        }
        return result;
    }

    private Table<String, String, ResourceGroup> transResourceGroups2Table(List<ResourceGroup> resourceGroups) {
        Table<String, String, ResourceGroup> table = HashBasedTable.create();
        for (ResourceGroup resourceGroup : resourceGroups) {
            table.put(resourceGroup.getName(), resourceGroup.getQueuePath(), resourceGroup);
        }
        return table;
    }

    private Table<String, String, ComponentUser> transComponentUsers2Table(List<ComponentUser> componentUserList) {
        Table<String, String, ComponentUser> table = HashBasedTable.create();
        for (ComponentUser componentUser : componentUserList) {
            table.put(componentUser.getLabel(), componentUser.getUserName(), componentUser);
        }
        return table;
    }

    public static String transferIpStr(List<ComponentUserVO.Sidecar> sidecar) {
        if (CollectionUtils.isEmpty(sidecar)) {
            return StringUtils.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        sidecar.forEach(e -> {
            if (StringUtils.isNotBlank(e.getIp())) {
                stringBuilder.append(e.getIp());
                if (Objects.nonNull(e.getPort())) {
                    stringBuilder.append(":").append(e.getPort());
                }
                stringBuilder.append(",");
            }
        });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}