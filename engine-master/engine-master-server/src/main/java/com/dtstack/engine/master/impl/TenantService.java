package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ClusterTenantResourceBindingParam;
import com.dtstack.engine.api.pojo.lineage.ComponentMultiTestResult;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.api.vo.TenantNameVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.api.vo.tenant.TenantAdminVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.ResourceGroupGrantDao;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.master.enums.CrudEnum;
import com.dtstack.engine.master.mapstruct.TenantStruct;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.router.login.domain.TenantAdmin;
import com.dtstack.engine.master.router.login.domain.UserTenant;
import com.dtstack.engine.master.vo.CheckedTenantVO;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.TenantResource;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.TenantIdListParam;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.SearchTenantParam;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.UserTenantRelParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/16
 */
@Service
public class TenantService {

    private static Logger LOGGER = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private ConsoleCache consoleCache;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private WorkSpaceProjectService projectService;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ResourceGroupDao resourceGroupDao;

    @Autowired
    private UicTenantApiClient uicTenantApiClient;

    @Autowired
    private TenantStruct tenantStruct;

    @Autowired
    private ResourceGroupGrantDao resourceGroupGrantDao;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private ConsoleComponentUserService consoleComponentUserService;

    public static final String DEFAULT_CLUSTER_TENANT_COMMON_CONFIG = "{\n" +
            "        \"enableDownloadResult\":false,\n" +
            "        \"downloadLimit\":0,\n" +
            "        \"selectLimit\":1000\n" +
            "    }";

    public PageResult<List<ClusterTenantVO>> pageQuery(Long clusterId,
                                                       Integer engineType,
                                                       String tenantName,
                                                       int pageSize,
                                                       int currentPage){
        Cluster cluster = clusterDao.getOne(clusterId);
        if(cluster == null){
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }

        PageQuery query = new PageQuery(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        List<Long> dtuicTenantIds = new ArrayList<>();
        if(StringUtils.isNotBlank(tenantName)){
            List<UserFullTenantVO> tenantByFullName = getTenantByFullName(tenantName);
            dtuicTenantIds = tenantByFullName.stream().map(UserFullTenantVO::getTenantId).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(dtuicTenantIds)){
                return PageResult.EMPTY_PAGE_RESULT;
            }
        }
        int count = clusterTenantDao.generalCount(clusterId,dtuicTenantIds);
        if (count == 0){
            return PageResult.EMPTY_PAGE_RESULT;
        }

        List<ClusterTenant> clusterTenants = clusterTenantDao.generalQuery(query, clusterId, dtuicTenantIds);
        List<ClusterTenantVO> clusterTenantVOS = fillTenantName(clusterTenants);
        fillQueue(clusterTenantVOS);

        return new PageResult<>(clusterTenantVOS, count, query);
    }

    /**
     * 获取处于统一集群的全部tenant
     *
     * @param dtuicTenantId
     * @param engineType
     * @return
     */
    public List<ClusterTenantVO> listEngineTenant( Long dtuicTenantId,
                                                  Integer engineType) {
        List<ClusterTenantVO> engineTenantVOS = listClusterTenantVOSInTenantId(dtuicTenantId);
        if(CollectionUtils.isEmpty(engineTenantVOS)){
            return engineTenantVOS;
        }
        fillQueue(engineTenantVOS);
        return engineTenantVOS;
    }

    public List<ClusterTenantVO> listClusterTenantVOSInTenantId(Long dtuicTenantId) {
        Long clusterId = getClusterIdByDtUicTenantId(dtuicTenantId);
        List<ClusterTenant> clusterTenants = clusterTenantDao.listEngineTenant(clusterId);
        if(CollectionUtils.isEmpty(clusterTenants)){
            return new ArrayList<>(0);
        }
        return fillTenantName(clusterTenants);
    }

    private List<ClusterTenantVO> fillTenantName(List<ClusterTenant> clusterTenants) {
        List<Long> dtUicTenantIds = clusterTenants.stream()
                .map(ClusterTenant::getDtUicTenantId)
                .collect(Collectors.toList());
        Map<Long, TenantDeletedVO> detailVOMap = listAllTenantByDtUicTenantIds(dtUicTenantIds);
        return clusterTenants.stream().map(clusterTenant -> {
            ClusterTenantVO clusterTenantVO = tenantStruct.toClusterTenantVO(clusterTenant);
            TenantDeletedVO tenantDetailVO = detailVOMap.get(clusterTenant.getDtUicTenantId());
            clusterTenantVO.setTenantName(tenantDetailVO == null ? "" : tenantDetailVO.getTenantName());
            clusterTenantVO.setTenantId(clusterTenant.getDtUicTenantId());
            return clusterTenantVO;
        }).collect(Collectors.toList());
    }

    public Long getClusterIdByDtUicTenantId(Long dtuicTenantId) {
        return clusterTenantDao.getClusterIdByDtUicTenantId(dtuicTenantId);
    }

    /**
     * 获取租户及其绑定状态
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<CheckedTenantVO> listCheckedTenants(String tenantName) {
        List<UserTenant> userTenants = dtUicUserConnect.getUserTenants(env.getPublicServiceNode(), env.getSdkToken(), tenantName);
        if (CollectionUtils.isEmpty(userTenants)) {
            return Collections.emptyList();
        }

        List<Long> dbDtUicTenantSet = clusterTenantDao.listAllDtUicTenantId();
        List<CheckedTenantVO> vos = new ArrayList<>();
        for (UserTenant ut : userTenants) {
            CheckedTenantVO vo = new CheckedTenantVO();
            vo.setTenantId(ut.getTenantId());
            vo.setTenantName(ut.getTenantName());
            vo.setBounded(dbDtUicTenantSet.contains(ut.getTenantId()));
            vos.add(vo);
        }
        return vos;
    }

    private void fillQueue(List<ClusterTenantVO> engineTenantVOS){

        List<Long> queueIds = engineTenantVOS.stream().filter(v -> v.getQueueId() != null).map(ClusterTenantVO::getQueueId).collect(Collectors.toList());
        Map<Long, com.dtstack.engine.api.domain.Queue> queueMap = new HashMap<>(16);
        List<com.dtstack.engine.api.domain.Queue> queueList = queueDao.listByIds(queueIds);
        for (com.dtstack.engine.api.domain.Queue queue : queueList) {
            queueMap.put(queue.getId(), queue);
        }
        for (ClusterTenantVO engineTenantVO : engineTenantVOS) {
            if(engineTenantVO.getQueueId() == null){
                continue;
            }
            com.dtstack.engine.api.domain.Queue queue = queueMap.get(engineTenantVO.getQueueId());
            if(queue == null){
                continue;
            }
            engineTenantVO.setQueue(queue.getQueuePath());
            engineTenantVO.setMaxCapacity(NumberUtils.toDouble(queue.getMaxCapacity(),0) * 100 + "%");
            engineTenantVO.setMinCapacity(NumberUtils.toDouble(queue.getCapacity(),0) * 100 + "%");
        }
    }


    public List<UserTenantVO> listTenant(String dtToken) {
        List<UserTenant> tenantList = postTenantList(dtToken);
        if (CollectionUtils.isEmpty(tenantList)) {
            return Lists.newArrayList();
        }
        List<Long> hasClusterTenantIds = clusterTenantDao.listAllDtUicTenantId();
        if (hasClusterTenantIds.isEmpty()) {
            return Lists.newArrayList();
        }
        tenantList.removeIf(tenant -> hasClusterTenantIds.contains(tenant.getTenantId()));

        return beanConversionVo(tenantList);
    }

    private List<UserTenantVO> beanConversionVo(List<UserTenant> tenantList) {
        List<UserTenantVO> vos = Lists.newArrayList();
        for (UserTenant userTenant : tenantList) {
            UserTenantVO vo = new UserTenantVO();
            BeanUtils.copyProperties(userTenant, vo);
            List<TenantAdmin> adminList = userTenant.getAdminList();
            List<TenantAdminVO> tenantAdminVOS = Lists.newArrayList();
            for (TenantAdmin tenantAdmin : adminList) {
                TenantAdminVO tenantAdminVO = new TenantAdminVO();
                BeanUtils.copyProperties(tenantAdmin, tenantAdminVO);
                tenantAdminVOS.add(tenantAdminVO);
            }
            vo.setAdminList(tenantAdminVOS);
        }
        return vos;
    }

    private List<UserTenant> postTenantList(String dtToken) {
        //uic对数据量做了限制，可能未查询到租户信息
        return dtUicUserConnect.getUserTenants(env.getPublicServiceNode(), dtToken, "");
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindingTenant( Long dtUicTenantId,  Long clusterId,
                               Long queueId,  String dtToken,String namespace) throws Exception {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.CANT_NOT_FIND_CLUSTER.getMsg(), ErrorCode.CANT_NOT_FIND_CLUSTER);

        checkTenantBindStatus(dtUicTenantId);
        checkClusterCanUse(cluster.getClusterName(),dtUicTenantId);


        addEngineTenant(clusterId,dtUicTenantId);
        Set<Long> uicTenantIds = new HashSet<>();
        uicTenantIds.add(dtUicTenantId);
        List<Component> components = componentDao.listByClusterId(clusterId, null, false);
        Set<Integer> codes = components.stream().map(Component::getComponentTypeCode).collect(Collectors.toSet());
        codes.forEach(c -> dataSourceService.publishComponent(clusterId, c, uicTenantIds));

        if (StringUtils.isNotBlank(namespace)) {
            //k8s
            componentService.addOrUpdateNamespaces(cluster.getId(), namespace, null, dtUicTenantId);
        } else if (queueId != null) {
            //hadoop
            updateTenantQueue(dtUicTenantId,clusterId, queueId);
        }
        //oracle tidb queueId可以为空
    }

    /**
     * 绑定资源组
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingResource(Long resourceId, Long dtUicTenantId,String taskTypeResourceJson) {
        ResourceGroup resource = resourceGroupDao.getOne(resourceId, Deleted.NORMAL.getStatus());
        if (resource == null) {
            throw new RdosDefineException("Resource does not exist", ErrorCode.DATA_NOT_FIND);
        }
        try {
            //修改租户各个任务资源限制
            updateTenantTaskResource(dtUicTenantId,taskTypeResourceJson);
            LOGGER.info("switch queue, tenantId:{} resourceId:{} queueName:{} clusterId:{}",dtUicTenantId,resourceId,resource.getQueuePath(),resource.getClusterId());
            updateTenantResource(dtUicTenantId, resource.getClusterId(), resourceId, resource.getQueuePath());
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Failed to switch queue");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindingTenantWithResource(ClusterTenantResourceBindingParam param,String dtToken) throws Exception {
        Cluster cluster = clusterDao.getOne(param.getClusterId());
        EngineAssert.assertTrue(cluster != null, "Cluster does not exist", ErrorCode.DATA_NOT_FIND);

        checkTenantBindStatus(param.getTenantId());
        checkClusterCanUse(cluster.getClusterName(), param.getTenantId());

        addEngineTenant(cluster.getId(), param.getTenantId());
        Set<Long> uicTenantIds = new HashSet<>();
        uicTenantIds.add(param.getTenantId());
        List<Component> components = componentDao.listByClusterId(cluster.getId(), null, false);
        Set<Integer> codes = components.stream().map(Component::getComponentTypeCode).collect(Collectors.toSet());
        codes.forEach(c -> dataSourceService.publishComponent(cluster.getId(), c, uicTenantIds));

        if (StringUtils.isNotBlank(param.getNamespace())) {
            //k8s
            componentService.addOrUpdateNamespaces(cluster.getId(), param.getNamespace(), null, param.getTenantId());
        } else if (param.getResourceId() != null) {
            ResourceGroup resource = resourceGroupDao.getOne(param.getResourceId(),Deleted.NORMAL.getStatus());
            if (resource == null) {
                throw new RdosDefineException("Resource does not exist", ErrorCode.DATA_NOT_FIND);
            }

            //hadoop
            updateTenantResource(param.getTenantId(), cluster.getId(), param.getResourceId(), resource.getQueuePath());
        }
        //oracle tidb queueId可以为空
        // dtscript 节点标签授权
        bindLabelResourceIfNeed(param);

        // 调用业务中心接口：绑定 ldap 账号
        dataSourceService.bindLdapIfNeeded(cluster.getId(), uicTenantIds);
        // 同步数据
        components.forEach(component -> {
            if (!DataSourceService.SYNC_TO_RANGER_COMPONENTS.contains(component.getComponentTypeCode())) {
                return;
            }
            this.syncToRangerIfNeededWhenBind(cluster.getId(), component, param.getTenantId());
        });
    }

    /**
     * dtscript 节点标签授权
     * @param param
     */
    private void bindLabelResourceIfNeed(ClusterTenantResourceBindingParam param) {
        Long labelResourceId = param.getLabelResourceId();
        if (labelResourceId == null) {
            return;
        }
        ResourceGroup resource = resourceGroupDao.getOne(labelResourceId, Deleted.NORMAL.getStatus());
        if (resource == null) {
            throw new RdosDefineException("Resource does not exist", ErrorCode.DATA_NOT_FIND);
        }
        Long dtUicTenantId = param.getTenantId();
        Long clusterId = param.getClusterId();
        LOGGER.info("bindLabelResourceIfNeed resource, dtUicTenantId:{} resourceId:{} clusterId:{}", dtUicTenantId, labelResourceId, clusterId);
        // 离线项目
        grantLabelResourceToTenantProjects(labelResourceId, dtUicTenantId);
        int result = clusterTenantDao.updateLabelResourceId(dtUicTenantId, clusterId, labelResourceId);
        if(result == 0){
            throw new RdosDefineException(String.format("update default label resource:%s failed", labelResourceId));
        }
        //缓存刷新
        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    @Transactional(rollbackFor = Exception.class)
    public void unbindTenant(ClusterTenantResourceBindingParam param) {
        // 要解绑的集群
        Long clusterId = param.getClusterId();
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, "Cluster does not exist", ErrorCode.DATA_NOT_FIND);

        // 要解绑的租户
        Long dtUicTenantId = param.getTenantId();
        Long clusterIdFromDB = getClusterIdByDtUicTenantId(dtUicTenantId);
        if (clusterIdFromDB == null) {
            throw new RdosDefineException(String.format("租户 %s 未绑定集群，请核实后重试", dtUicTenantId));
        }
        if (!clusterIdFromDB.equals(clusterId)) {
            throw new RdosDefineException(String.format("租户已绑定集群与要解绑集群不一致，已绑定集群:%s，要解绑集群:%s", clusterIdFromDB, clusterId));
        }
        
        // 开启数据安全，不支持解绑
        boolean openSecurity = clusterService.hasRangerAndLdap(clusterId);
        if (openSecurity) {
            throw new RdosDefineException(String.format("租户 %s 已开启数据安全，不支持解绑", dtUicTenantId));
        }

        consoleComponentUserService.unbindTenant(clusterId, dtUicTenantId);
        // 处理绑定关系，console_cluster_tenant 中的 queue、default_resource_id 信息可以不用处理，因为 console_cluster_tenant 最后会被删掉
        clusterTenantDao.unbindTenant(clusterId, dtUicTenantId);
        // 修改租户对应的项目的状态为「集群已解绑」
        // scheduleEngineProjectDao.updateStatusByUicTenantId(ProjectStatus.CLUSTER_UNBIND.getStatus(), dtUicTenantId);

        // 处理已推送到 public service 的租户
        TenantIdListParam tenantIdListParam = new TenantIdListParam();
        tenantIdListParam.setTenantIdList(Lists.newArrayList(dtUicTenantId));
        dataSourceAPIClient.removeConsoleDataSource(tenantIdListParam);

        // 最后清理缓存
        componentService.asyncUpdateCache(clusterId, null, Sets.newHashSet(dtUicTenantId), false);
    }

    /**
     * 若集群没有绑定过其他租户，推送组件信息；若集群绑定过其他租户，说明集群的信息已经推过了，那就只推送当前租户、集群(service)关系信息
     * @param clusterId
     * @param component
     * @param toBindUicTenantId
     */
    private void syncToRangerIfNeededWhenBind(Long clusterId, Component component, Long toBindUicTenantId) {
        Set<Long> uicTenantIdsFromDb = this.findUicTenantIdsByClusterId(clusterId);
        Set<Long> toBindUicTenantIds = Sets.newHashSet(toBindUicTenantId);
        boolean isNewUicTenantId = toBindUicTenantIds.containsAll(uicTenantIdsFromDb);
        if (isNewUicTenantId) {
            dataSourceService.syncToRangerIfNeeded(clusterId, component, toBindUicTenantIds, CrudEnum.UPDATE);
        } else {
            dataSourceService.syncRelationToRangerIfNeeded(clusterId, component, toBindUicTenantId);
        }
    }

    private void checkTenantBindStatus(Long dtUicTenantId) {
        Long clusterId = getClusterIdByDtUicTenantId(dtUicTenantId);
        if (null != clusterId) {
            throw new RdosDefineException(ErrorCode.TENANT_ALREADY_BIND);
        }
    }

    public void checkClusterCanUse(String clusterName,Long dtUicTenantId) throws Exception {
        List<ComponentMultiTestResult> testConnectionVO = componentService.testConnects(clusterName,dtUicTenantId);
        boolean canUse = true;
        StringBuilder msg = new StringBuilder();
        msg.append(ErrorCode.CHECK_CONNECT_NOT_PASS.getMsg() + ".\n");
        for (ComponentMultiTestResult testResult : testConnectionVO) {
            EComponentType componentType = EComponentType.getByCode(testResult.getComponentTypeCode());
            if(!noNeedCheck(componentType) && !testResult.getResult()){
                canUse = false;
                msg.append(ErrorCode.CHECK_CONNECT_COMPONENT.getMsg()).append(componentType.getName()).append(" ").append(JSON.toJSONString(testResult.getErrorMsg())).append("\n");
            }
        }

        if(!canUse){
            throw new RdosDefineException(msg.toString());
        }
    }

    private Boolean noNeedCheck(EComponentType componentType) {
        switch (componentType) {
            case LIBRA_SQL:
            case IMPALA_SQL:
            case TIDB_SQL:
            case SPARK_THRIFT:
            case CARBON_DATA:
            case SFTP: return true;
            default: return false;
        }
    }

    private void addEngineTenant(Long clusterId,Long dtUicTenantId){
        ClusterTenant et = new ClusterTenant();
        et.setDtUicTenantId(dtUicTenantId);
        et.setClusterId(clusterId);
        et.setCommonConfig(DEFAULT_CLUSTER_TENANT_COMMON_CONFIG);
        clusterTenantDao.insert(et);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateTenantResource(Long dtUicTenantId, Long clusterId, Long resourceId, String queuePath){
        LOGGER.info("switch resource, dtUicTenantId:{} resourceId:{} clusterId:{}",
                dtUicTenantId, resourceId, clusterId);
        List<ProjectNameVO> projectNameVOS = projectService.listProjects(null, dtUicTenantId);
        Map<Integer, List<Long>> appProjectMapping = projectNameVOS.stream().collect(Collectors.groupingBy(ProjectNameVO::getAppType, Collectors.mapping(
                ProjectNameVO::getProjectId, Collectors.toList()
        )));
        for (Integer app : appProjectMapping.keySet()) {
            resourceGroupService.grantToProjects(resourceId, appProjectMapping.get(app),app,dtUicTenantId);
        }
        Queue queue = queueDao.getByClusterIdAndQueuePath(clusterId, queuePath);
        int result = clusterTenantDao.updateResourceId(dtUicTenantId, clusterId, resourceId, queue == null ? null : queue.getId());
        if(result == 0){
            throw new RdosDefineException("The update default resource failed");
        }
        //缓存刷新
        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    public void updateTenantQueue(Long dtUicTenantId, Long clusterId, Long queueId){
        Integer childCount = queueDao.countByParentQueueId(queueId);
        if (childCount != 0) {
            throw new RdosDefineException(ErrorCode.QUEUE_HAS_SUB_QUEUE.getMsg(), ErrorCode.DATA_NOT_FIND);
        }

        LOGGER.info("switch queue, dtUicTenantId:{} queueId:{} clusterId:{}",dtUicTenantId,queueId,clusterId);
        int result = clusterTenantDao.updateQueueId(dtUicTenantId, clusterId, queueId);
        if(result == 0){
            throw new RdosDefineException(ErrorCode.UPDATE_EXCEPTION);
        }
        //缓存刷新
        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    /**
     * 绑定/切换队列
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingQueue( Long queueId,
                              Long dtUicTenantId,String taskTypeResourceJson) {
        com.dtstack.engine.api.domain.Queue queue = queueDao.getOne(queueId);
        if (queue == null) {
            throw new RdosDefineException(ErrorCode.QUEUE_NOT_EXIST.getMsg(), ErrorCode.DATA_NOT_FIND);
        }
        try {
            //修改租户各个任务资源限制
            updateTenantTaskResource(dtUicTenantId,taskTypeResourceJson);
            LOGGER.info("switch queue, tenantId:{} queueId:{} queueName:{} clusterId:{}",dtUicTenantId,queueId,queue.getQueueName(),queue.getClusterId());
            updateTenantQueue(dtUicTenantId, queue.getClusterId(), queueId);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException(ErrorCode.QUEUE_SWITCH_FAIL);
        }
    }

    /**
    * @author zyd
    * @Description 修改租户的任务资源限制
    * @Date 11:14 上午 2020/10/15
    * @Param [tenantId, dtUicTenantId, taskTypeResourceMap]
    * @retrun void
    **/
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantTaskResource(Long dtUicTenantId, String taskTypeResourceJson) {
        if(StringUtils.isBlank(taskTypeResourceJson)){
            return;
        }
        JSONArray jsonArray = JSON.parseArray(taskTypeResourceJson);
        TenantResource tenantResource = new TenantResource();
        tenantResource.setDtUicTenantId(dtUicTenantId.intValue());
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            Integer taskType = jsonObj.getInteger("taskType");
            tenantResource.setTaskType(taskType);
            EScheduleJobType eJobType = EScheduleJobType.getEJobType(taskType);
            if(null == eJobType){
                throw new RdosDefineException(ErrorCode.TASK_TYPE_ERROR);
            }else{
                tenantResource.setEngineType(eJobType.getName());
            }
            tenantResource.setResourceLimit(jsonObj.getString("resourceParams"));
            //先删除原来的资源限制
            tenantResourceDao.deleteByTenantIdAndTaskType(dtUicTenantId,taskType);
            tenantResourceDao.insert(tenantResource);
        }
    }

    /**
    * @author zyd
    * @Description 根据租户id和taskType获取资源限制信息
    * @Date 9:56 上午 2020/10/16
    * @Param [dtUicTenantId, taskType]
    * @retrun java.lang.String
    **/
    public String queryResourceLimitByTenantIdAndTaskType(Long dtUicTenantId, Integer taskType) {

        TenantResource tenantResource = null;
        try {
            tenantResource = tenantResourceDao.selectByUicTenantIdAndTaskType(dtUicTenantId, taskType);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException(ErrorCode.QUERY_EXCEPTION);
        }
        if(null != tenantResource){
            return tenantResource.getResourceLimit();
        }else{
            return "";
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteTenantId(Long dtUicTenantId) {
        LOGGER.info("delete tenant {} dtUicTenantId {} ", dtUicTenantId, dtUicTenantId);
        clusterTenantDao.deleteTenantId(dtUicTenantId);
        resourceGroupGrantDao.removeTenantId(dtUicTenantId);
    }

    public List<TenantNameVO> listBoundedTenants(Long clusterId, List<Long> retainTenantIds) {
        List<Long> tenantIds = clusterTenantDao.listBoundedTenants(clusterId);
        if (CollectionUtils.isEmpty(tenantIds)) {
            return new ArrayList<>(0);
        }

        if (CollectionUtils.isNotEmpty(retainTenantIds)) {
            tenantIds.retainAll(retainTenantIds);
        }

        Map<Long, TenantDeletedVO> detailVOMap = listAllTenantByDtUicTenantIds(tenantIds);
        return detailVOMap.values()
                .stream()
                .map(tenantDetailVO -> tenantStruct.toNameVO(tenantDetailVO))
                .collect(Collectors.toList());
    }

    public Map<Long, TenantDeletedVO> listAllTenantByDtUicTenantIds(Collection<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return new HashMap<>();
        }
        SearchTenantParam searchTenantParam = new SearchTenantParam();
        searchTenantParam.setTenantIds(new ArrayList<>(ids));
        ApiResponse<List<TenantDeletedVO>> listApiResponse = uicTenantApiClient.getByDtUicTenantIds(searchTenantParam);
        if (listApiResponse.getData() == null) {
            return new HashMap<>();
        }
        List<TenantDeletedVO> data = listApiResponse.getData();
        return data.stream().collect(Collectors.toMap(TenantDeletedVO::getTenantId, Function.identity()));
    }

    public UICTenantVO getTenant(Long tenantId) {
        ApiResponse<UICTenantVO> listApiResponse = uicTenantApiClient.findTenantById(tenantId);
        if (listApiResponse.getData() == null) {
            return null;
        }
        return listApiResponse.getData();
    }

    public String getTenantName(Long tenantId) {
        UICTenantVO tenant = getTenant(tenantId);
        return tenant == null ? "" : tenant.getTenantName();
    }

    public String getTenantNameWithCache(Map<Long, String> tenantCache, Long uicId) {
        return tenantCache.computeIfAbsent(uicId, this::getTenantName);
    }

    public List<UserFullTenantVO> getTenantByFullName(String tenantName){
        if(StringUtils.isNotBlank(tenantName)){
            UserTenantRelParam userTenantRelParam = new UserTenantRelParam();
            userTenantRelParam.setTenantName(tenantName);
            ApiResponse<List<UserFullTenantVO>> fullByTenantName = uicTenantApiClient.getFullByTenantName(userTenantRelParam);
            return fullByTenantName.getData();
        }
        return new ArrayList<>(0);
    }


    @NotNull
    public Set<Long> findUicTenantIdsByClusterId(Long clusterId) {
        List<ClusterTenant> tenantVOS = clusterTenantDao.listEngineTenant(clusterId);
        return tenantVOS.stream()
                .filter(s-> s != null && s.getDtUicTenantId() != null)
                .map(ClusterTenant::getDtUicTenantId).collect(Collectors.toSet());
    }

    public List<TenantNameVO> findCacheTenant(Long clusterId) {
        if (clusterId == null) {
            throw new RdosDefineException(ErrorCode.CLUSTER_ID_EMPTY);
        }
        List<ClusterTenant> clusterTenants = clusterTenantDao.listEngineTenant(clusterId);
        if (CollectionUtils.isNotEmpty(clusterTenants)) {
            List<ClusterTenantVO> clusterTenantVOS = fillTenantName(clusterTenants);
           return tenantStruct.clusterTenantVOToNameVO(clusterTenantVOS);
        }
        return new ArrayList<>();
    }

    public boolean hasBindTenants(Long clusterId) {
        if (clusterId == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if(cluster == null){
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        return clusterTenantDao.hasBindTenants(clusterId) != null;
    }

    public boolean hasRangerAndLdap(Long dtUicTenantId) {
        Long clusterId = getClusterIdByDtUicTenantId(dtUicTenantId);
        if (Objects.isNull(clusterId)) {
            return false;
        }
        return clusterService.hasRangerAndLdap(clusterId);
    }

    public void bindingLabelResource(Long labelResourceId, Long dtUicTenantId) {
        ResourceGroup resource = resourceGroupDao.getOne(labelResourceId, Deleted.NORMAL.getStatus());
        if (resource == null) {
            throw new RdosDefineException("Resource does not exist", ErrorCode.DATA_NOT_FIND);
        }
        LOGGER.info("switch labelResource, tenantId:{}, resourceId:{}, clusterId:{}", dtUicTenantId, labelResourceId, resource.getClusterId());
        grantLabelResourceToTenantProjects(labelResourceId, dtUicTenantId);
        int result = clusterTenantDao.updateLabelResourceId(dtUicTenantId, resource.getClusterId(), labelResourceId);
        if (result == 0) {
            throw new RdosDefineException(String.format("update default label resource:%s failed", labelResourceId));
        }
        //缓存刷新
        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    private void grantLabelResourceToTenantProjects(Long labelResourceId, Long dtUicTenantId) {
        List<ProjectNameVO> projectNameVOS = projectService.listCanGrantProjects(AppType.RDOS, dtUicTenantId);
        Map<Integer, List<Long>> appProjectMapping = projectNameVOS.stream().collect(Collectors.groupingBy(ProjectNameVO::getAppType, Collectors.mapping(
                ProjectNameVO::getProjectId, Collectors.toList()
        )));
        for (Integer app : appProjectMapping.keySet()) {
            resourceGroupService.grantToProjects(labelResourceId, appProjectMapping.get(app), app, dtUicTenantId);
        }
    }
}