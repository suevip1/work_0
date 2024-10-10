package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.convert.utils.ProxyUserUtils;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.dtcenter.loader.engine.SourceConstant;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ClusterPageDTO;
import com.dtstack.engine.api.dto.ClusterPageQueryDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.vo.ClusterEngineInfoVO;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterPageVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.EngineVO;
import com.dtstack.engine.api.vo.IComponentVO;
import com.dtstack.engine.api.vo.KerberosConfigVO;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.SchedulingVo;
import com.dtstack.engine.api.vo.TenantClusterCommonConfigVO;
import com.dtstack.engine.api.vo.TenantClusterInfoVO;
import com.dtstack.engine.api.vo.TenantNameVO;
import com.dtstack.engine.api.vo.console.ConsoleProjectAccountVO;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.constrant.UicConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.AccountDao;
import com.dtstack.engine.dao.AccountTenantDao;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ConsoleFileSyncDao;
import com.dtstack.engine.dao.ConsoleFileSyncDetailDao;
import com.dtstack.engine.dao.ConsoleTenantComponentDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ResourceGroupDao;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.master.bo.LdapUserBO;
import com.dtstack.engine.master.dto.ComponentProxyConfigDTO;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.faced.sdk.PublicServiceUicLicenseApiClientSdkFaced;
import com.dtstack.engine.master.mapstruct.ClusterStruct;
import com.dtstack.engine.master.mapstruct.TenantStruct;
import com.dtstack.engine.master.plugininfo.PluginInfoCacheManager;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleSSL;
import com.dtstack.engine.po.ConsoleTenantComponent;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.TenantResource;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.LdapUserVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.LicenseProductComponentVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.LicenseProductComponentWidgetVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.ProxyUserVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.SearchTenantParam;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.UserTenantId;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.UserTenantRelParam;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.comparators.BooleanComparator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEFAULT_CLUSTER_ID;
import static com.dtstack.engine.common.constrant.ConfigConstant.DEFAULT_CLUSTER_NAME;
import static com.dtstack.engine.common.constrant.ConfigConstant.DEPLOY_MODEL;
import static com.dtstack.engine.common.constrant.ConfigConstant.FLINK_ON_STANDALONE_CONF;
import static com.dtstack.engine.common.constrant.ConfigConstant.IS_METADATA;
import static com.dtstack.engine.common.constrant.ConfigConstant.NAMESPACE;
import static com.dtstack.engine.common.constrant.ConfigConstant.PASSWORD;
import static com.dtstack.engine.common.constrant.ConfigConstant.SSL_CLIENT;
import static com.dtstack.engine.common.constrant.ConfigConstant.STORE_TYPE;
import static com.dtstack.engine.common.constrant.ConfigConstant.USERNAME;
import static com.dtstack.engine.common.constrant.ConfigConstant.LDAP_GROUP_NAME;
import static com.dtstack.engine.common.exception.ErrorCode.TENANT_NOT_BIND;
import static java.lang.String.format;

@Service
public class ClusterService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    @Autowired
    private ComponentAuxiliaryService componentAuxiliaryService;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountTenantDao accountTenantDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private ConsoleFileSyncDao consoleFileSyncDao;
    @Autowired
    private ConsoleFileSyncDetailDao consoleFileSyncDetailDao;

    @Autowired
    private ResourceGroupDao resourceGroupDao;

    @Autowired
    private ConsoleTenantComponentDao consoleTenantComponentDao;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    // 集群分页页面展示绑定租户的阈值
    @Value("${display.cluster.binding.tenant.threshold:3}")
    private Integer displayClusterBindingTenantThreshold;

    @Autowired
    private UicTenantApiClient uicTenantApiClient;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    @Autowired
    private ClusterStruct clusterStruct;

    @Autowired
    private TenantStruct tenantStruct;

    @Autowired
    private PluginInfoCacheManager pluginInfoCacheManager;

    @Autowired
    private ConsoleProjectAccountService consoleProjectAccountService;

    @Autowired
    private PublicServiceUicLicenseApiClientSdkFaced publicServiceUicLicenseApiClientSdkFaced;

    // 业务中心租户接口信息缓存
    private final Cache<Long, TenantNameVO> tenantNameVOCache = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(100).expireAfterAccess(10, TimeUnit.MINUTES).build();

    // component => MultiEngineType
    private static final Function<Component,MultiEngineType> TO_MULTI_ENGINE_TYPE_FUNCTION =
            c -> EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()), c.getDeployType());

    /**
     * 为了不影响之前的代码，新增一个返回 DTSCRIPT_AGENT 的方法
     */
    private static final Function<Component,MultiEngineType> SHOW_MULTI_ENGINE_TYPE_FUNCTION =
            c -> {
        if (EComponentType.getByCode(c.getComponentTypeCode()) == EComponentType.DTSCRIPT_AGENT) {
            return MultiEngineType.DTSCRIPT_AGENT;
        }
        return EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()), c.getDeployType());
    };

    // 排除掉公共组件
    private static final Predicate<Component> NOT_COMMON_MULTI_ENGINE_TYPE_PREDICATE = e -> {
        MultiEngineType multiEngineType = TO_MULTI_ENGINE_TYPE_FUNCTION.apply(e);
        return Objects.nonNull(multiEngineType) && !MultiEngineType.COMMON.equals(multiEngineType);
    };

    /**
     * 为了不影响之前的代码，新增一个返回 DTSCRIPT_AGENT 的方法
     */
    private static final Predicate<Component> SHOW_NOT_COMMON_MULTI_ENGINE_TYPE_PREDICATE = e -> {
        MultiEngineType multiEngineType = SHOW_MULTI_ENGINE_TYPE_FUNCTION.apply(e);
        return Objects.nonNull(multiEngineType) && !MultiEngineType.COMMON.equals(multiEngineType);
    };

    @Override
    public void afterPropertiesSet() {
        if (isDefaultClusterExist()) {
            return;
        }

        try {
            addDefaultCluster();
        } catch (Exception e) {
            LOGGER.error(" ", e);
        }
    }

    private boolean isDefaultClusterExist() {
        Cluster cluster = clusterDao.getOne(DEFAULT_CLUSTER_ID);
        if (cluster == null) {
            cluster = clusterDao.getByClusterName(DEFAULT_CLUSTER_NAME);
            return cluster != null;
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addDefaultCluster() throws Exception {
        Cluster cluster = new Cluster();
        cluster.setId(DEFAULT_CLUSTER_ID);
        cluster.setClusterName(DEFAULT_CLUSTER_NAME);
        cluster.setHadoopVersion("");
        clusterDao.insertWithId(cluster);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClusterVO addCluster(ClusterDTO clusterDTO) {
        checkLicense();
        EngineAssert.assertTrue(StringUtils.isNotEmpty(clusterDTO.getClusterName()), ErrorCode.INVALID_PARAMETERS.getDescription());
        checkName(clusterDTO.getClusterName());

        Cluster cluster = new Cluster();
        cluster.setClusterName(clusterDTO.getClusterName());
        cluster.setHadoopVersion("");
        Cluster byClusterName = clusterDao.getByClusterName(clusterDTO.getClusterName());
        if (byClusterName != null) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST.getDescription());
        }
        clusterDao.insert(cluster);
        clusterDTO.setId(cluster.getId());
        return ClusterVO.toVO(cluster);
    }

    /**
     * 通过 license 控制集群的创建个数
     */
    private void checkLicense() {
        LicenseProductComponentVO licenseProductComponentVO = publicServiceUicLicenseApiClientSdkFaced.readComponent(UicConstant.CLUSTER_CONFIG);
        Integer countAll = ObjectUtils.defaultIfNull(clusterDao.countAll(), 0) ;
        if (licenseProductComponentVO == null || CollectionUtils.isEmpty(licenseProductComponentVO.getWidgets())) {
            // 缺省的情况下，视为历史客户，不予限制
            return;
        }
        LicenseProductComponentWidgetVO widget = licenseProductComponentVO.getWidgets().stream()
                .filter(k -> UicConstant.CLUSTER_NUM_LABEL.equals(k.getFieldLabel()))
                .findFirst()
                .orElse(null);
        // 缺省的情况下，视为历史客户，不予限制
        if (widget == null) {
            return;
        }
        Integer license = ObjectUtils.defaultIfNull((Integer) widget.getValue(), 0);
        if (countAll.compareTo(license) >= 0) {
            throw new RdosDefineException(String.format("集群数量 %s 达到 license 上限:%s", countAll, license));
        }
    }

    private void checkName(String name) {
        if (StringUtils.isNotBlank(name)) {
            if (name.length() > 24) {
                throw new RdosDefineException("名称过长");
            }
        } else {
            throw new RdosDefineException("名称不能为空");
        }
    }

    public ClusterVO getClusterByName(String clusterName) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        return ClusterVO.toVO(cluster);
    }


    public List<ClusterEngineInfoVO> getClusterEngineInfo(Long clusterId) {
        List<ClusterEngineInfoVO> result = new ArrayList<>();
        // 集群关联的组件
        List<Component> clusterComponents = componentDao.listAllByClusterId(clusterId);

        Predicate<MultiEngineType> isDisPlayEnginePredicate = e -> MultiEngineType.HADOOP.equals(e) || MultiEngineType.canBindAccount(e)
                || MultiEngineType.DTSCRIPT_AGENT.equals(e);

        Predicate<Component> hasK8sComponent = e -> EComponentType.KUBERNETES.getTypeCode().equals(e.getComponentTypeCode());

        Set<MultiEngineType> multiEngineTypes = clusterComponents.stream()
                .filter(SHOW_NOT_COMMON_MULTI_ENGINE_TYPE_PREDICATE)
                .map(SHOW_MULTI_ENGINE_TYPE_FUNCTION)
                .filter(isDisPlayEnginePredicate)
                .collect(Collectors.toSet());

        // 集群是否 on k8s
        boolean clusterOnK8s = clusterComponents.stream().anyMatch(hasK8sComponent);
        String clusterResourceType = clusterOnK8s ?  EComponentType.KUBERNETES.getName() : EComponentType.YARN.getName();

        multiEngineTypes.forEach(m -> {
            ClusterEngineInfoVO vo = new ClusterEngineInfoVO();
            vo.setEngineType(m.getType());
            vo.setEngineName(m.getName());
            // 是否支持绑定账号
            vo.setCanBindAccount(MultiEngineType.canBindAccount(m));
            // on yarn or on k8s
            vo.setResourceType(clusterResourceType);
            result.add(vo);
        });

        result.sort(Comparator.comparingInt(ClusterEngineInfoVO::getEngineType));

        return result;
    }


    public TenantClusterInfoVO getTenantClusterInfo(Long clusterId, Long tenantId) {
        // 资源信息
        TenantClusterInfoVO.TenantClusterResourceInfoVO tenantClusterResourceInfoVO = getTenantClusterResourceInfo(tenantId);
        // 任务资源限制
        List<TenantClusterInfoVO.TenantClusterResourceLimitInfoVO> tenantClusterResourceLimitInfoVOS = getTenantClusterResourceLimitInfo(tenantId);
        // 租户级集群组件参数
        List<TenantClusterInfoVO.ConsoleTenantComponentVO>  consoleTenantComponentVOS = getConsoleTenantComponent(clusterId, tenantId);
        // 标签资源信息
        TenantClusterInfoVO.TenantClusterLabelResourceInfoVO tenantClusterLabelResourceInfoVO = getTenantClusterLabelResourceInfo(tenantId);

        return TenantClusterInfoVO.TenantClusterInfoVOBuilder.aTenantClusterInfoVO()
                .tenantClusterResourceInfoVO(tenantClusterResourceInfoVO)
                .tenantClusterLabelResourceInfoVO(tenantClusterLabelResourceInfoVO)
                .tenantClusterResourceLimitInfoVOS(tenantClusterResourceLimitInfoVOS)
                .consoleTenantComponentVOS(consoleTenantComponentVOS)
                .tenantId(tenantId)
                .clusterId(clusterId)
                .build();
    }


    private TenantClusterInfoVO.TenantClusterResourceInfoVO getTenantClusterResourceInfo(Long tenantId) {
        TenantClusterInfoVO.TenantClusterResourceInfoVO vo = new TenantClusterInfoVO.TenantClusterResourceInfoVO();

        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(tenantId);
        Optional.ofNullable(clusterTenant).ifPresent(e -> {
            vo.setDefaultResourceId(e.getDefaultResourceId());
            vo.setQueueId(e.getQueueId());
        });


        Optional<ResourceGroupDetail> resourceGroupDetail = Optional
                .ofNullable(clusterTenant)
                .map(ClusterTenant::getDefaultResourceId)
                .map(resourceId -> resourceGroupDao.getDetailById(resourceId));

        resourceGroupDetail.ifPresent(d -> {
                 vo.setDefaultResourceName(d.getResourceName());
                 vo.setMinCapacity(NumberUtils.toDouble(d.getCapacity(),0) * 100 + "%");
                 vo.setMaxCapacity(NumberUtils.toDouble(d.getMaxCapacity(),0) * 100 + "%");
             });

        if (!resourceGroupDetail.isPresent()) {
            // k8s namespace
            Optional.ofNullable(clusterTenant)
                    .map(ClusterTenant::getQueueId)
                    .map(queueDao::getOne)
                    .ifPresent(q -> vo.setQueue(q.getQueueName()));
            return vo;
        }

        return vo;
    }

    private TenantClusterInfoVO.TenantClusterLabelResourceInfoVO getTenantClusterLabelResourceInfo(Long tenantId) {
        TenantClusterInfoVO.TenantClusterLabelResourceInfoVO vo = new TenantClusterInfoVO.TenantClusterLabelResourceInfoVO();

        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(tenantId);
        Optional.ofNullable(clusterTenant).ifPresent(e -> {
            vo.setDefaultLabelResourceId(e.getDefaultLabelResourceId());
        });

        Optional<ResourceGroup> resourceGroup = Optional
                .ofNullable(clusterTenant)
                .map(ClusterTenant::getDefaultLabelResourceId)
                .map(resourceId -> resourceGroupDao.getOne(resourceId, Deleted.NORMAL.getStatus()));

        resourceGroup.ifPresent(d -> {
            vo.setDefaultLabel(d.getName());
            vo.setDefaultUserName(d.getQueuePath());
        });
        return vo;
    }

    private List<TenantClusterInfoVO.TenantClusterResourceLimitInfoVO> getTenantClusterResourceLimitInfo(Long tenantId) {
        List<TenantClusterInfoVO.TenantClusterResourceLimitInfoVO> vos = new ArrayList<>();
        List<TenantResource> tenantResources = tenantResourceDao.selectByUicTenantId(tenantId);
        for (TenantResource tenantResource:tenantResources) {
            // 资源限制配置为空，不展示
            if (StringUtils.isBlank(tenantResource.getResourceLimit()) || MapUtils.isEmpty(JSONObject.parseObject(tenantResource.getResourceLimit()))) {
                continue;
            }
            TenantClusterInfoVO.TenantClusterResourceLimitInfoVO vo = new TenantClusterInfoVO.TenantClusterResourceLimitInfoVO();
            vo.setTaskType(tenantResource.getTaskType());
            Optional.ofNullable(tenantResource.getTaskType()).map(EScheduleJobType::getEJobType).ifPresent(e -> vo.setTaskName(e.getName()));
            vo.setResourceLimit(JSONObject.parseObject(tenantResource.getResourceLimit()));
            vos.add(vo);
        }
        return vos;
    }

    private List<TenantClusterInfoVO.ConsoleTenantComponentVO> getConsoleTenantComponent(Long clusterId, Long tenantId) {
        List<TenantClusterInfoVO.ConsoleTenantComponentVO> vos = new ArrayList<>();
        List<String> supportTenantConfigComponentType = environmentContext.getSupportTenantConfigComponentType();

        supportTenantConfigComponentType.forEach(c -> {
            EComponentType componentType = EComponentType.getByCode(Integer.parseInt(c));
            TenantClusterInfoVO.ConsoleTenantComponentVO vo = new TenantClusterInfoVO.ConsoleTenantComponentVO();
            vo.setComponentName(componentType.getName());
            vo.setComponentTypeCode(componentType.getTypeCode());
            List<ConsoleTenantComponent> components = consoleTenantComponentDao.getByTenantIdAndClusterIdAndComponentTypeCode(tenantId, clusterId, Integer.parseInt(c));
            if (CollectionUtils.isNotEmpty(components)) {
                ConsoleTenantComponent consoleTenantComponent = components.get(0);
                vo.setComponentConfig(consoleTenantComponent.getComponentConfig());
            }

            vos.add(vo);
        });

        return vos;
    }


    public List<TenantNameVO> getClusterBindingTenants(Long clusterId, String fuzzyTenantName, UserDTO user, Boolean isAllAuth) {
        List<TenantNameVO> result = new ArrayList<>();
        // 根据模糊搜索租户名调用业务中心接口获取租户id
        List<Long> fuzzyTenantIds = getTenantIdsFromFuzzyTenantName(fuzzyTenantName);

        if (StringUtils.isNotBlank(fuzzyTenantName) && CollectionUtils.isEmpty(fuzzyTenantIds)) {
            return new ArrayList<>();
        }

        List<Long> tenantIds = clusterTenantDao.getTenantIdsByClusterId(clusterId, fuzzyTenantIds);
        if (CollectionUtils.isEmpty(tenantIds)) {
            return result;
        }

        if (!isAllAuth) {
            // 如果是租户级别的，就只显示改租户下的数据
            List<Long> userTenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
            tenantIds.retainAll(userTenantIds);
        }

        if (CollectionUtils.isEmpty(tenantIds)) {
            return result;
        }

        return getTenantNameVOS(tenantIds);
    }

    public PageResult pageQuery(int currentPage,
                                int pageSize,
                                String fuzzyClusterName,
                                String fuzzyTenantName,
                                Boolean isAllAuth,
                                UserDTO user)  {

        List<ClusterPageVO> pageVOS = new ArrayList<>();
        PageQuery<ClusterPageQueryDTO> pageQuery = new PageQuery<>(currentPage, pageSize);

        List<Long> tenantIds = null;
        if (StringUtils.isNotBlank(fuzzyTenantName)) {
            // 根据模糊搜索租户名调用业务中心接口获取租户id
            tenantIds = getTenantIdsFromFuzzyTenantName(fuzzyTenantName);

            if (CollectionUtils.isEmpty(tenantIds)) {
                return PageResult.EMPTY_PAGE_RESULT;
            }

            if (!isAllAuth) {
                List<Long> userTenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
                // 取交集
                tenantIds.retainAll(userTenantIds);

                if (CollectionUtils.isEmpty(tenantIds)) {
                    return PageResult.EMPTY_PAGE_RESULT;
                }
            }
        } else {
            if (!isAllAuth) {
                List<Long> userTenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
                tenantIds = Lists.newArrayList(userTenantIds);
            }
        }

        ClusterPageQueryDTO clusterPageQueryDTO = ClusterPageQueryDTO.ClusterPageQueryDTOBuilder.aClusterPageQueryDTO()
                .fuzzyClusterName(fuzzyClusterName)
                .tenantIds(tenantIds)
                .build();
        pageQuery.setModel(clusterPageQueryDTO);


        Integer pageQueryCount = clusterDao.getPageQueryCount(clusterPageQueryDTO);
        if (pageQueryCount == 0) {
            return new PageResult<>(pageVOS, pageQueryCount, pageQuery);
        }

        List<ClusterPageDTO> clusterPageDTOS = clusterDao.pageQuery(pageQuery);
        pageVOS = toPageVOS(clusterPageDTOS);

        return new PageResult<>(pageVOS, pageQueryCount, pageQuery);
    }

    private List<ClusterPageVO> toPageVOS(List<ClusterPageDTO> clusterPageDTOS) {
        List<ClusterPageVO> result = new ArrayList<>();
        for (ClusterPageDTO dto : clusterPageDTOS) {
            ClusterPageVO clusterPageVO = clusterStruct.toPageVO(dto);
            // com.dtstack.engine.api.dto.ClusterPageDTO.tenantIds 是 com.dtstack.engine.dao.ClusterDao.pageQuery sql 中 GROUP_CONCAT 的结果
            // @see com.dtstack.engine.dao.ClusterDao.pageQuery
            List<TenantNameVO> bindingTenants = getBindingTenants(dto.getTenantIds());
            clusterPageVO.setBindingTenants(bindingTenants);
            // 集群是否包含 HadoopEngine (不包括 k8s)，前端根据这个判断是否展示资源全景的页面
            clusterPageVO.setHasHadoopEngine(clusterHasHadoopEngine(dto.getClusterId()));
            result.add(clusterPageVO);
        }
        return result;
    }


    /**
     * 判断集群是否包含 HadoopEngine (不包括 k8s)
     * @param clusterId clusterId
     * @return true/false
     */
    private Boolean clusterHasHadoopEngine(Long clusterId) {
        List<Component> clusterComponents = Optional.ofNullable(clusterId).map(componentDao::listAllByClusterId).orElse(null);
        if (CollectionUtils.isEmpty(clusterComponents)) {
            return false;
        }
        Predicate<Component> hasK8sComponent = e -> EComponentType.KUBERNETES.getTypeCode().equals(e.getComponentTypeCode());

        // 是 Hadoop 引擎，且不包含 k8s 组件
        return clusterComponents.stream().filter(NOT_COMMON_MULTI_ENGINE_TYPE_PREDICATE).map(TO_MULTI_ENGINE_TYPE_FUNCTION).anyMatch(MultiEngineType.HADOOP::equals)
                &&
                clusterComponents.stream().noneMatch(hasK8sComponent);

    }


    private List<TenantNameVO> getBindingTenants(String tenantIdsStr) {
        List<TenantNameVO> vos = new ArrayList<>();
        if (StringUtils.isBlank(tenantIdsStr)) {
            return vos;
        }
        String[] splitTenantIds = tenantIdsStr.split(",");
        List<Long> tenantIds = new ArrayList<>();
        Arrays.stream(splitTenantIds).forEach(e -> tenantIds.add(Long.parseLong(e)));
        // 页面上最多只需要展示 3 个绑定的租户
        return getTenantNameVOSIgnoreException(tenantIds, displayClusterBindingTenantThreshold);
    }

    private List<TenantNameVO> getTenantNameVOS(List<Long> tenantIds) {
        return getTenantNameVOS(tenantIds, false, null);
    }

    private List<TenantNameVO> getTenantNameVOSIgnoreException(List<Long> tenantIds, Integer threshold) {
        return getTenantNameVOS(tenantIds, true, threshold);
    }

    private List<TenantNameVO> getTenantNameVOS(List<Long> tenantIds, boolean ignoreException, Integer threshold) {
        // 处理下异常值
        tenantIds = tenantIds.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<TenantNameVO> result = new ArrayList<>();
        List<Long> notInCacheIds = new ArrayList<>();
        for (Long tenantId : tenantIds) {
            TenantNameVO vo = cacheEffect(tenantId);
            if (Objects.isNull(vo)) {
                notInCacheIds.add(tenantId);
                continue;
            }
            result.add(vo);
            // 已经满足阈值了，就直接返回
            if (Objects.nonNull(threshold) && result.size() >= threshold) {
                return result;
            }
        }
        // 如果返回结果有数量限制，这里判断一下
        if (Objects.nonNull(threshold) && result.size() < threshold) {
            int leftoverSize = threshold - result.size();
            if (notInCacheIds.size() >= leftoverSize) {
                notInCacheIds = notInCacheIds.subList(0, leftoverSize);
            }
        }

        if (CollectionUtils.isEmpty(notInCacheIds)) {
            return result;
        }

        // 不在缓存里的租户，批量取一次
        ApiResponse<List<TenantDeletedVO>> apiResponse = getTenantByTenantId(notInCacheIds, ignoreException);
        if (Objects.nonNull(apiResponse) && apiResponse.getSuccess() && Objects.nonNull(apiResponse.getData())) {
            List<TenantDeletedVO> data = apiResponse.getData();
            // 忽略删除的租户
            List<TenantDeletedVO> collect = data.stream().filter(e -> !e.getDeleted()).collect(Collectors.toList());
            List<TenantNameVO> tenantNameVOS = tenantStruct.toNameVOS(collect);
            result.addAll(tenantNameVOS);

            // 增加到缓存里
            tenantNameVOCache.putAll(tenantNameVOS.stream().collect(Collectors.toMap(TenantNameVO::getTenantId, Function.identity())));
        }

        return result;

    }

    private TenantNameVO cacheEffect(Long tenantId) {
        return tenantNameVOCache.getIfPresent(tenantId);
    }

    private ApiResponse<List<TenantDeletedVO>> getTenantByTenantId(List<Long> tenantIds, boolean ignoreException) {
        try {
            SearchTenantParam param = new SearchTenantParam();
            param.setTenantIds(tenantIds);
            return uicTenantApiClient.getIncludeDeletedTenantByIds(param);
        } catch (Exception e) {
            LOGGER.error("tenantIds: {}, invoke com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient.getTenantByTenantId error: {}", JSONObject.toJSONString(tenantIds), e.getMessage(), e);
            if (!ignoreException) {
                throw e;
            }
            return null;
        }
    }

    private List<Long> getTenantIdsFromFuzzyTenantName(String fuzzyTenantName) {
        UserTenantRelParam param = new UserTenantRelParam();
        param.setTenantName(fuzzyTenantName);
        ApiResponse<List<UserFullTenantVO>> apiResponse = uicTenantApiClient.getFullByTenantName(param);
        if (apiResponse.getSuccess() && CollectionUtils.isNotEmpty(apiResponse.getData())) {
            List<UserFullTenantVO> data = apiResponse.getData();
            // 缓存一下业务中心接口返回值
            cacheTenantNameVOs(data);

            if (StringUtils.isBlank(fuzzyTenantName)) {
                return new ArrayList<>();
            }

            return data.stream().map(UserFullTenantVO::getTenantId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private void cacheTenantNameVOs(List<UserFullTenantVO> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        Map<Long, TenantNameVO> cache = data.stream()
                .map(tenantStruct::toNameVO)
                .collect(Collectors.toMap(TenantNameVO::getTenantId, Function.identity()));

        tenantNameVOCache.putAll(cache);
    }

    /**
     * 对外接口
     */
    public String clusterInfo(Long tenantId) {
        ClusterVO cluster = getClusterByTenant(tenantId);
        if (cluster != null) {
            JSONObject config = pluginInfoCacheManager.buildClusterConfig(cluster);
            return config.toJSONString();
        }
        return StringUtils.EMPTY;
    }

    public ClusterVO clusterExtInfo(Long uicTenantId,boolean multiVersion) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(uicTenantId);
        if(null == clusterId){
            return null;
        }
        return getCluster(clusterId,false,multiVersion);
    }

    /**
     * 替换组件租户级配置
     *
     * @param componentType 组件类型
     * @param dtUicTenantId 租户 id
     * @param clusterId     集群 id
     * @param componentJson 组件配置信息
     */
    public void setTenantComponentConfig(EComponentType componentType, Long dtUicTenantId, Long clusterId, JSONObject componentJson) {
        if (dtUicTenantId == null) {
            return;
        }

        List<String> supportTenantConfigComponentType = environmentContext.getSupportTenantConfigComponentType();

        if (supportTenantConfigComponentType.isEmpty() || !supportTenantConfigComponentType.contains(componentType.getTypeCode().toString())) {
            return;
        }

        List<ConsoleTenantComponent> consoleTenantComponents
                = consoleTenantComponentDao.getByTenantIdAndClusterIdAndComponentTypeCode(dtUicTenantId, clusterId, componentType.getTypeCode());
        if (CollectionUtils.isEmpty(consoleTenantComponents)) {
            return;
        }

        ConsoleTenantComponent consoleTenantComponent = consoleTenantComponents.get(0);
        JSONObject componentConfig = Optional.ofNullable(consoleTenantComponent.getComponentConfig()).map(JSONObject::parseObject).orElse(new JSONObject());

        // 租户级别的组件配置优先
        componentJson.putAll(componentConfig);
    }

    public String pluginInfo( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode) {
        return "{}";
    }

    public JSONObject getSslConfig(Long clusterId, EComponentType componentType, String version) {
        ConsoleSSL targetSSLConfig = getTargetSSLConfig(clusterId, componentType, version);
        if (null != targetSSLConfig) {
            JSONObject sslClient = new JSONObject();
            sslClient.put("sslFileTimestamp", targetSSLConfig.getGmtModified());
            sslClient.put("remoteSSLDir", targetSSLConfig.getRemotePath());
            sslClient.put("sslClientConf", targetSSLConfig.getSslClient());
            return sslClient;
        }
        return null;
    }

    public ConsoleSSL getTargetSSLConfig(Long clusterId, EComponentType componentType, String version) {
        if (EComponentType.TRINO_SQL.equals(componentType) || EComponentType.SPARK_THRIFT.equals(componentType)) {
            return componentService.getSSLConfig(clusterId, componentType.getTypeCode(), version);
        } else {
            ConsoleSSL sslConfig = componentService.getSSLConfig(clusterId, componentType.getTypeCode(), null);
            if (null == sslConfig) {
                sslConfig = componentService.getSSLConfig(clusterId, EComponentType.YARN.getTypeCode(), null);
                if (null == sslConfig) {
                    sslConfig = componentService.getSSLConfig(clusterId, EComponentType.HDFS.getTypeCode(), null);
                }
            }
            return sslConfig;
        }
    }

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    public String clusterSftpDir( Long tenantId,  Integer componentType) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);
        if (clusterId != null) {
            if(null == componentType){
                componentType = EComponentType.SPARK_THRIFT.getTypeCode();
            }
            Map<String, String> sftpConfig = componentService.getComponentByClusterId(clusterId,EComponentType.SFTP.getTypeCode(),false,Map.class,null);
            if (sftpConfig != null) {
                String versionValue= null;
                if (ComponentVersionUtil.isMultiVersionComponent(componentType)) {
                    versionValue = componentDao.getDefaultComponentVersionByClusterAndComponentType(clusterId,componentType);
                }

                KerberosConfig kerberosDaoByComponentType = kerberosDao.getByComponentType(clusterId, componentType,versionValue);
                if(null != kerberosDaoByComponentType){
                    return kerberosDaoByComponentType.getRemotePath();
                }
                return sftpConfig.get("path") + File.separator + componentService.buildSftpPath(clusterId, componentType,versionValue);
            }
        }
        return null;
    }

    public Queue getQueue(Long dtUicTenantId, Long clusterId, Long resourceId) {
        if (null == resourceId) {
            ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(dtUicTenantId);
            if (null != clusterTenant && null != clusterTenant.getDefaultResourceId()) {
                resourceId = clusterTenant.getDefaultResourceId();
            }
        }

        if (resourceId != null) {
            ResourceGroup resourceGroup = resourceGroupDao.getOne(resourceId,Deleted.NORMAL.getStatus());
            if (resourceGroup != null) {
                Queue queue = queueDao.getByClusterIdAndQueuePath(clusterId, resourceGroup.getQueuePath());
                if (queue != null) {
                    return queue;
                }
            }
            return null;
        }

        Long queueId = clusterTenantDao.getQueueIdByDtUicTenantId(dtUicTenantId);
        if (queueId != null) {
            Queue queue = queueDao.getOne(queueId);
            if (queue != null) {
                return queue;
            }
        }

        List<Queue> queues = queueDao.listByClusterWithLeaf(Lists.newArrayList(clusterId));
        if (CollectionUtils.isEmpty(queues)) {
            return null;
        }

        // 没有绑定集群和队列时，返回第一个队列
        return queues.get(0);
    }

    public String getNamespace(ParamAction action, Long tenantId, String engineName, ComputeType computeType) {

        try {
            Map actionParam = PublicUtil.objectToMap(action);
            Integer deployMode = MapUtils.getInteger(actionParam, DEPLOY_MODEL);
            EngineTypeComponentType type = EngineTypeComponentType.getByEngineName(engineName);

            if (type == null) {
                return null;
            }

            EDeployMode deploy = EDeployMode.PERJOB;
            if (ComputeType.BATCH == computeType && EngineTypeComponentType.FLINK.equals(type)) {
                deploy = EDeployMode.SESSION;
            }
            if (null != deployMode) {
                deploy = EDeployMode.getByType(deployMode);
            }

            Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);
            if (null == clusterId) {
                return null;
            }
            JSONObject componentConf = componentService.getComponentByClusterId(clusterId,type.getComponentType().getTypeCode(),false,JSONObject.class,null);
            if (null == componentConf) {
                return null;
            }
            JSONObject pluginInfo = componentConf.getJSONObject(deploy.getMode());
            if (null == pluginInfo) {
                return null;
            }
            return pluginInfo.getString(NAMESPACE);
        } catch (IOException e) {
            LOGGER.error("Get namespace error " + e.getMessage());
        }
        return null;
    }


    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    @Deprecated
    public String hiveInfo( Long dtUicTenantId,  Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
        return getConfigByKey(dtUicTenantId, EComponentType.SPARK_THRIFT.getConfName(),fullKerberos,componentVersionMap,true);
    }

    /**
     * 对外接口
     */
    @Deprecated
    public String hiveServerInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
        return getConfigByKey(dtUicTenantId, EComponentType.HIVE_SERVER.getConfName(),fullKerberos,componentVersionMap,true);
    }

    /**
     * 对外接口
     */
    @Deprecated
    public String hadoopInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
        return getConfigByKey(dtUicTenantId, EComponentType.HDFS.getConfName(),fullKerberos,componentVersionMap,true);
    }

    /**
     * 对外接口
     */
    @Deprecated
    public String carbonInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
        return getConfigByKey(dtUicTenantId, EComponentType.CARBON_DATA.getConfName(),fullKerberos,componentVersionMap,true);
    }

    /**
     * 对外接口
     */
    @Deprecated
    public String impalaInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
        return getConfigByKey(dtUicTenantId, EComponentType.IMPALA_SQL.getConfName(),fullKerberos,componentVersionMap,true);
    }

    /**
     * 对外接口
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    public String prestoInfo(Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
        return getConfigByKey(dtUicTenantId, EComponentType.PRESTO_SQL.getConfName(), fullKerberos,componentVersionMap,true);
    }

    /**
     * 对外接口
     */
    public String sftpInfo( Long dtUicTenantId) {
        return getConfigByKey(dtUicTenantId, EComponentType.SFTP.getConfName(),false,null,false);
    }

    /**
     * 构建集群配置 json, 包含所需要的所有组件配置
     *
     * @param cluster             集群信息
     * @param dtUicTenantId       租户 id
     * @param dtUicUserId         用户 id
     * @param componentVersionMap 指定版本
     * @param multiVersion        是否是多版本
     * @return 集群组件配置
     */
    public JSONObject buildClusterConfig(Cluster cluster, Long dtUicTenantId, Long dtUicUserId, Map<Integer, String> componentVersionMap, boolean multiVersion) {
        if (cluster == null) {
            String msg = format("The tenant [%s] is not bound to any cluster", dtUicTenantId);
            throw new RdosDefineException(msg);
        }
        ClusterVO clusterVO = pluginInfoCacheManager.cacheCluster(cluster, multiVersion);
        clusterVO.setDtUicTenantId(dtUicTenantId);
        clusterVO.setDtUicUserId(dtUicUserId);
        return buildClusterConfig(clusterVO, componentVersionMap);
    }

    public JSONObject buildClusterConfig(ClusterVO cluster, Map<Integer,String> componentVersionMap) {
        JSONObject config = new JSONObject();
        List<SchedulingVo> scheduling = cluster.getScheduling();
        if (CollectionUtils.isNotEmpty(scheduling)) {
            for (SchedulingVo schedulingVo : scheduling) {
                List<IComponentVO> components = schedulingVo.getComponents();
                if (CollectionUtils.isNotEmpty(components)) {
                    for (IComponentVO componentVO : components) {
                        String version = MapUtils.isEmpty(componentVersionMap) ? "" : componentVersionMap.getOrDefault(componentVO.getComponentTypeCode(),"");
                        EComponentType type = EComponentType.getByCode(componentVO.getComponentTypeCode());
                        //IComponentVO contains  flink on standalone and on yarn
                        ComponentVO component = componentVO.getComponent(version);
                        if(null == component){
                            continue;
                        }
                        JSONObject componentConfig = componentService.getComponentByClusterId(component.getId(),false, JSONObject.class);
                        if(EComponentType.FLINK.equals(type) && EDeployType.STANDALONE.getType().equals(component.getDeployType())){
                            config.put(FLINK_ON_STANDALONE_CONF, componentConfig);
                            continue;
                        }
                        if (EComponentType.YARN.equals(type) && componentConfig != null) {
                            ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(cluster.getClusterId(), EComponentType.YARN.getTypeCode());
                            ComponentProxyConfigDTO proxyConfig = componentAuxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
                            if (proxyConfig != null) {
                                componentConfig.putIfAbsent(GlobalConst.PROXY, JSONObject.toJSONString(proxyConfig));
                            }
                        }
                        // 后续优化为查询附属配置列表, 然后统一封装返回
                        if (EComponentType.KUBERNETES.equals(type) && componentConfig != null) {
                            ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(cluster.getClusterId(), EComponentType.KUBERNETES.getTypeCode());
                            ComponentProxyConfigDTO proxyConfig = componentAuxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
                            if (proxyConfig != null) {
                                componentConfig.putIfAbsent(GlobalConst.LOG_TRACE, proxyConfig);
                            }
                        }
                        config.put(type.getConfName(), componentConfig);
                    }
                }
            }
        }
        config.put("clusterName", cluster.getClusterName());
        return config;
    }

    /**
     * 如果只用集群Id 不要使用此接口
     * 填充信息到组件 集群—>引擎->组件
     * @param dtUicTenantId
     * @return
     */
    public ClusterVO getClusterByTenant(Long dtUicTenantId) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);
        if(Objects.isNull(clusterId)){
            return getCluster4PluginInfo(DEFAULT_CLUSTER_ID,false);
        }
        return getCluster4PluginInfo(clusterId,false);
    }

    /**
     * 只有集群信息
     * @param dtUicTenantId
     * @return
     */
    public Cluster getCluster(Long dtUicTenantId) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);
        if(null == clusterId){
            return null;
        }
        return clusterDao.getOne(clusterId);
    }

    public String getConfigByKey(Long dtUicTenantId, String componentConfName, Boolean fullKerberos,Map<Integer,String > componentVersionMap,boolean needHdfsConfig) {
        Long clusterId = Optional.ofNullable(clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId)).orElse(DEFAULT_CLUSTER_ID);
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(componentConfName);
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), ComponentVersionUtil.getComponentVersion(componentVersionMap,componentType),null);
        if (null == component) {
            return "{}";
        }
        JSONObject configObj = componentService.getComponentByClusterId(clusterId, component.getComponentTypeCode(), false, JSONObject.class,componentVersionMap);

        // 设置租户级别的组件配置 #5360
        setTenantComponentConfig(componentType, dtUicTenantId, clusterId, configObj);

        if (configObj != null) {
            KerberosConfig kerberosConfig = null;
            if (StringUtils.isNotBlank(component.getKerberosFileName())) {
                //开启kerberos的kerberosFileName不为空
                String componentVersion = ComponentVersionUtil.getComponentVersion(componentVersionMap, componentType.getTypeCode());
                if(StringUtils.isBlank(componentVersion)){
                    componentVersion = component.getHadoopVersion();
                }
                kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType.getTypeCode(), ComponentVersionUtil.formatMultiVersion(componentType.getTypeCode(),componentVersion));
            }
            //返回版本
            configObj.put(ConfigConstant.VERSION, component.getHadoopVersion());
            configObj.put(ConfigConstant.VERSION_NAME, component.getVersionName());
            configObj.put(IS_METADATA, component.getIsMetadata());
            configObj.put(STORE_TYPE, component.getStoreType());

            if (needHdfsConfig) {
                // 添加组件的kerberos配置信息 应用层使用 兼容历史老接口 新接口needHdfsConfig默认为false
                configObj.put(ConfigConstant.KERBEROS_CONFIG, addKerberosConfigWithHdfs(component.getComponentTypeCode(), clusterId, kerberosConfig));
            } else if (null != kerberosConfig) {
                //平台需要principalFile 需要转换一层
                KerberosConfigVO kerberosConfigVO = KerberosConfigVO.toVO(kerberosConfig);
                configObj.put(ConfigConstant.KERBEROS_CONFIG, kerberosConfigVO);
            }
            //填充sftp配置项
            Map sftpMap = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class,null);
            if (MapUtils.isNotEmpty(sftpMap)) {
                configObj.put(EComponentType.SFTP.getConfName(), sftpMap);
            }
            if (StringUtils.isNotBlank(component.getSslFileName())) {
                JSONObject sslConfig = getSslConfig(clusterId, componentType, component.getHadoopVersion());
                configObj.putIfAbsent(SSL_CLIENT, sslConfig);
            }
            //开启 kerberos 默认添加yarnConf
            if (null != kerberosConfig) {
                Map yarnConf = componentService.getComponentByClusterId(clusterId, EComponentType.YARN.getTypeCode(), false, Map.class, null);
                if (MapUtils.isNotEmpty(yarnConf)) {
                    configObj.put(EComponentType.YARN.getConfName(), yarnConf);
                }
            }
            // 添加 logTrace 信息
            if (EComponentType.KUBERNETES.equals(componentType)) {
                ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(clusterId, EComponentType.KUBERNETES.getTypeCode());
                ComponentProxyConfigDTO logTraceConfig = componentAuxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
                if (logTraceConfig != null) {
                    configObj.putIfAbsent(GlobalConst.LOG_TRACE, logTraceConfig);
                }
            }
            // 添加 knox 信息
            if (EComponentType.YARN.equals(componentType)) {
                ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(clusterId, EComponentType.YARN.getTypeCode());
                ComponentProxyConfigDTO proxyConfig = componentAuxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
                if (proxyConfig != null) {
                    configObj.putIfAbsent(GlobalConst.PROXY, JSONObject.toJSONString(proxyConfig));
                }
            }
            return configObj.toJSONString();
        }
        return "{}";
    }



    /**
     * 如果开启集群开启了kerberos认证，kerberosConfig中还需要包含hdfs配置
     *
     * @param componentType
     * @param clusterId
     * @param kerberosConfig
     */
    public KerberosConfigVO addKerberosConfigWithHdfs(Integer componentType, Long clusterId, KerberosConfig kerberosConfig) {
        if (Objects.nonNull(kerberosConfig)) {
            KerberosConfigVO kerberosConfigVO = KerberosConfigVO.toVO(kerberosConfig);
            if (!Objects.equals(EComponentType.HDFS.getTypeCode(), componentType)) {
                Map hdfsComponent = componentService.getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(),false,Map.class,null);
                if (MapUtils.isEmpty(hdfsComponent)) {
                    throw new RdosDefineException("开启kerberos后需要预先保存hdfs组件");
                }
                kerberosConfigVO.setHdfsConfig(hdfsComponent);
            }
            kerberosConfigVO.setKerberosFileTimestamp(kerberosConfig.getGmtModified());
            return kerberosConfigVO;
        }
        return null;
    }

    Cache<String, Optional<LdapUserVO>> ldapCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    /**
     * 获取 ldap user, 用户名 key 为 username, 密码的 key 为 password
     *
     * @return ldap user
     */
    public JSONObject getLdapConf(Integer componentTypeCode, Long dtUicUserId, Long dtTenantId, Long clusterId, JSONObject selfConf) {
        JSONObject ldapUserJson = new JSONObject();
        Set<Integer> ldapSupportComponentType = environmentContext.getLdapSupportComponentType();
        if (ldapSupportComponentType.isEmpty()) {
            return ldapUserJson;
        }
        if (!ldapSupportComponentType.contains(componentTypeCode)) {
            return ldapUserJson;
        }
        if (null == dtUicUserId || null == dtTenantId) {
            return ldapUserJson;
        }
        Optional<LdapUserBO> ldapUserBO = getLdapUser(dtUicUserId, dtTenantId, clusterId, EComponentType.getByCode(componentTypeCode), selfConf);
        if (ldapUserBO.isPresent()) {
            JsonUtil.putIgnoreEmpty(ldapUserJson, USERNAME, ldapUserBO.get().getLdapUserName());
            JsonUtil.putIgnoreEmpty(ldapUserJson, PASSWORD, ldapUserBO.get().getLdapPassWord());
            JsonUtil.putIgnoreEmpty(ldapUserJson,LDAP_GROUP_NAME,ldapUserBO.get().getLdapGroupName());
        }
        return ldapUserJson;
    }

    /**
     * 获取 ldap 用户信息
     * @param dtUicUserId
     * @param dtTenantId
     * @param clusterId
     * @param componentType
     * @return
     */
    public Optional<LdapUserBO> getLdapUser(Long dtUicUserId,
                                            Long dtTenantId,
                                            Long clusterId,
                                            EComponentType componentType,
                                            JSONObject selfConf) {

        // 开启数据安全的情况统一走数据安全 ldap 接口
        if (hasRangerAndLdap(clusterId)) {
            LdapUserVO ldapUser = getLdapUserOnOpenRangerAndLdap(dtUicUserId, dtTenantId);
            return Optional.ofNullable(ldapUser)
                    .map(
                            e -> LdapUserBO.LdapUserBOBuilder.builder()
                                    .ldapUserName(ProxyUserUtils.preprocessingLdapUserName(e.getLdapUserName()))
                                    .ldapPassWord(e.getLdapPassWord())
                                    .ldapGroupName(e.getLdapGroupName())
                                    .build()
                    );
        }

        // 没有开启数据安全但是组件类型是 hiveServer 并且 hive.proxy.enable = true,调用 uic 接口获取 ldap 用户名
        // 这里是为了支持没有开启数据安全但是要支持 hive 的代理用户执行：https://dtstack.yuque.com/rd-center/sm6war/tgsvnz
        // hive.proxy.enable = true 即表示开启 hive 代理
        // 这种情况只需要 username
        boolean hiveProxyEnable = BooleanUtils.toBoolean(selfConf.getBoolean(SourceConstant.HIVE_PROXY_ENABLE));
        if (EComponentType.hiveProxyUserComponents.contains(componentType) && hiveProxyEnable) {
            Optional<ProxyUserVO> proxyUser = ProxyUserUtils.getProxyUser(dtUicUserId, environmentContext.getSdkToken(), uIcUserTenantRelApiClient);
            if (proxyUser.isPresent()) {
                ProxyUserVO proxyUserVO = proxyUser.get();
                return Optional.of(LdapUserBO.LdapUserBOBuilder.builder()
                        .ldapUserName(ProxyUserUtils.preprocessingLdapUserName(proxyUserVO.getLdapUserName()))
                        .ldapPassWord(proxyUserVO.getLdapPassWord())
                        .build());
            }
        }
        return Optional.empty();
    }


    /**
     * 只有开启数据安全的情况下，业务中心的这个接口才能拿到数据：
     * com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient#findLdapUserByUserIdAndTenantId()
     * @param dtUicUserId
     * @param dtTenantId
     * @return
     */
    public LdapUserVO getLdapUserOnOpenRangerAndLdap(Long dtUicUserId, Long dtTenantId) {
        Optional<LdapUserVO> ldapUserOptional;
        String cacheUserKey = dtUicUserId + GlobalConst.STAR + dtTenantId;
        if (environmentContext.isOpenLdapCache()) {
            ldapUserOptional = ldapCache.getIfPresent(cacheUserKey);
            // cache 中可能不存在值，此时 ldapUserOptional 为 null
            if (ldapUserOptional != null) {
                return ldapUserOptional.orElse(null);
            }
        }

        UserTenantId userTenantId = new UserTenantId();
        userTenantId.setTenantId(dtTenantId);
        userTenantId.setUserId(dtUicUserId);
        ApiResponse<LdapUserVO> ldapUserResp = null;
        try {
            ldapUserResp = uIcUserTenantRelApiClient.findLdapUserByUserIdAndTenantId(userTenantId);
            LOGGER.info("getLdapUser  ok, dtUicUserId:{}, dtTenantId:{}, resp:{}", dtUicUserId, dtTenantId, JSONObject.toJSONString(ldapUserResp));
            if (ldapUserResp != null) {
                ldapUserOptional = Optional.ofNullable(ldapUserResp.getData());
                // 缓存 null
                ldapCache.put(cacheUserKey, ldapUserOptional);
                return ldapUserResp.getData();
            }
        } catch (Exception e) {
            LOGGER.error("getLdapUser error, invoke public servers sdk /user/tenant/findLdapUserByUserIdAndTenantId :{}, dtUicUserId: {}", e.getMessage(), dtUicUserId, e);
        }

        return null;
    }



    /**
     * 集群下拉列表
     */
    public List<ClusterVO> clusters() {
        PageQuery<ClusterDTO> pageQuery = new PageQuery<>(1, 1000, "gmt_modified", Sort.DESC.name());
        //查询未被删除的
        ClusterDTO clusterDTO = new ClusterDTO();
        clusterDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        pageQuery.setModel(clusterDTO);
        List<Cluster> clusterVOS = clusterDao.generalQuery(pageQuery);
        if (CollectionUtils.isNotEmpty(clusterVOS)) {
            return ClusterVO.toVOs(clusterVOS);
        }
        return Lists.newArrayList();
    }

    public String tiDBInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.TiDB,componentVersionMap, projectVO);
    }

    public String oracleInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.Oracle,componentVersionMap, projectVO);
    }

    public String mysqlInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.MySQL,componentVersionMap, projectVO);
    }

    public String hanaInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.SAP_HANA1,componentVersionMap, projectVO);
    }

    public String db2Info(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.DB2,componentVersionMap, projectVO);
    }

    public String sqlServerInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.SQLServer,componentVersionMap, projectVO);
    }

    public String oceanBaseInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.OceanBase,componentVersionMap, projectVO);
    }

    public String greenplumInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.GREENPLUM6,componentVersionMap, projectVO);
    }

    public String prestoInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO) {
        return accountInfo(dtUicTenantId, dtUicUserId, DataSourceType.Presto,componentVersionMap, projectVO);
    }

    public String inceptorSqlInfo(Long dtUicTenantId, Long dtUicUserId, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.INCEPTOR_SQL,null, projectVO);
    }

    public String adbPostgrepsqlInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.ADB_POSTGREPSQL,componentVersionMap, projectVO);
    }

    public String libraInfo(Long dtUicTenantId, Long dtUicUserId, Map<Integer,String> componentVersionMap, ProjectVO projectVO) {
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.LIBRA, componentVersionMap, projectVO);
    }

    public String trinoSqlInfo(Long dtUicTenantId, Long dtUicUserId, Map<Integer,String > componentVersionMap, ProjectVO projectVO){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.TRINO,componentVersionMap, projectVO);
    }

    public String starRocksInfo(Long dtUicTenantId, Long dtUicUserId, Map<Integer, String> componentVersionMap, ProjectVO projectVO) {
        return accountInfo(dtUicTenantId, dtUicUserId, DataSourceType.STARROCKS, componentVersionMap, projectVO);
    }

    public String hashDataInfo(Long dtUicTenantId, Long dtUicUserId, Map<Integer, String> componentVersionMap, ProjectVO projectVO) {
        return accountInfo(dtUicTenantId, dtUicUserId, DataSourceType.HASHDATA, componentVersionMap, projectVO);
    }

    public String oushuDbInfo(Long dtUicTenantId, Long dtUicUserId, Map<Integer, String> componentVersionMap, ProjectVO projectVO) {
        return accountInfo(dtUicTenantId, dtUicUserId, DataSourceType.OUSHUDB, componentVersionMap, projectVO);
    }

    private String accountInfo(Long dtUicTenantId, Long dtUicUserId, DataSourceType dataSourceType,Map<Integer,String > componentVersionMap, ProjectVO projectVO) {
        EComponentType componentType = null;
        if (DataSourceType.Oracle.equals(dataSourceType)) {
            componentType = EComponentType.ORACLE_SQL;
        } else if (DataSourceType.TiDB.equals(dataSourceType)) {
            componentType = EComponentType.TIDB_SQL;
        } else if (DataSourceType.GREENPLUM6.equals(dataSourceType)) {
            componentType = EComponentType.GREENPLUM_SQL;
        } else if (DataSourceType.Presto.equals(dataSourceType)) {
            componentType = EComponentType.PRESTO_SQL;
        } else if (DataSourceType.INCEPTOR_SQL.equals(dataSourceType)){
            componentType=EComponentType.INCEPTOR_SQL;
        } else if (DataSourceType.ADB_POSTGREPSQL.equals(dataSourceType)){
            componentType = EComponentType.ANALYTICDB_FOR_PG;
        } else if (DataSourceType.MySQL.equals(dataSourceType)) {
            componentType = EComponentType.MYSQL;
        } else if (DataSourceType.SAP_HANA1.equals(dataSourceType)) {
            componentType = EComponentType.HANA;
        } else if (DataSourceType.DB2.equals(dataSourceType)) {
            componentType = EComponentType.DB2;
        } else if (DataSourceType.SQLServer.equals(dataSourceType)) {
            componentType = EComponentType.SQL_SERVER;
        } else if (DataSourceType.OceanBase.equals(dataSourceType)) {
            componentType = EComponentType.OCEANBASE;
        } else if (DataSourceType.TRINO.equals(dataSourceType)) {
            componentType = EComponentType.TRINO_SQL;
        } else if (DataSourceType.STARROCKS.equals(dataSourceType)) {
            componentType = EComponentType.STARROCKS;
        } else if (DataSourceType.HASHDATA.equals(dataSourceType)) {
            componentType = EComponentType.HASHDATA;
        } else if (DataSourceType.OUSHUDB.equals(dataSourceType)) {
            componentType = EComponentType.OUSHUDB;
        }
        if (componentType == null) {
            throw new RdosDefineException("Unsupported data source type");
        }
        // 集群级账号
        String jdbcInfo = getConfigByKey(dtUicTenantId, componentType.getConfName(), false,componentVersionMap,false);

        /************* 优先级: 个人 > 项目 > 集群 ************/
        // 项目级账号
        jdbcInfo = replaceProjectAccountIfNeed(projectVO, componentType, jdbcInfo);

        // 个人级账号
        User user = userService.findByUser(dtUicUserId);
        if (null == user) {
            LOGGER.info("accountInfo, component:{}, username:{}", componentType.getName(), Optional.ofNullable(JSONObject.parseObject(jdbcInfo)).map(j -> j.getString(GlobalConst.USERNAME)).orElse(StringUtils.EMPTY));
            return jdbcInfo;
        }
        AccountTenant dbAccountTenant = accountTenantDao.getByUserIdAndTenantIdAndEngineType(user.getDtuicUserId(), dtUicTenantId, dataSourceType.getVal());
        if (null == dbAccountTenant) {
            LOGGER.info("accountInfo, component:{}, username:{}", componentType.getName(), Optional.ofNullable(JSONObject.parseObject(jdbcInfo)).map(j -> j.getString(GlobalConst.USERNAME)).orElse(StringUtils.EMPTY));
            return jdbcInfo;
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if(null == account){
            LOGGER.info("accountInfo, component:{}, username:{}", componentType.getName(), Optional.ofNullable(JSONObject.parseObject(jdbcInfo)).map(j -> j.getString(GlobalConst.USERNAME)).orElse(StringUtils.EMPTY));
            return jdbcInfo;
        }
        JSONObject data = JSONObject.parseObject(jdbcInfo);
        data.put(GlobalConst.USERNAME, account.getName());
        data.put(GlobalConst.PASS_WORD, Base64Util.baseDecode(account.getPassword()));
        LOGGER.info("accountInfo, component:{}, username:{}", componentType.getName(), Optional.ofNullable(JSONObject.parseObject(jdbcInfo)).map(j -> j.getString(GlobalConst.USERNAME)).orElse(StringUtils.EMPTY));
        return data.toJSONString();
    }

    public DataSourceType getDataSourceType(EComponentType componentType) {
        switch (componentType){
            case ORACLE_SQL:
                return DataSourceType.Oracle;
            case TIDB_SQL:
                return DataSourceType.TiDB;
            case GREENPLUM_SQL:
                return DataSourceType.GREENPLUM6;
            case PRESTO_SQL:
                return DataSourceType.Presto;
            case INCEPTOR_SQL:
                return DataSourceType.INCEPTOR_SQL;
            case ANALYTICDB_FOR_PG:
                return DataSourceType.ADB_POSTGREPSQL;
            case OUSHUDB:
                return DataSourceType.OUSHUDB;
            case TRINO_SQL:
                return DataSourceType.TRINO;
            case MYSQL:
                return DataSourceType.MySQL;
            case SQL_SERVER:
                return DataSourceType.SQLServer;
            case HANA:
                return DataSourceType.SAP_HANA1;
            case STARROCKS:
                return DataSourceType.STARROCKS;
            case HASHDATA:
                return DataSourceType.HASHDATA;
            case DB2:
                return DataSourceType.DB2;
            case OCEANBASE:
                return DataSourceType.OceanBase;
            case LIBRA_SQL:
                // 对应离线的 GaussDB
                return DataSourceType.LIBRA;
            default:
                return null;
        }
    }


    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCluster(Long clusterId){
        if(null == clusterId){
            throw new RdosDefineException("Cluster cannot be empty");
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException("Cluster does not exist");
        }
        if(DEFAULT_CLUSTER_ID.equals(clusterId)){
            throw new RdosDefineException("The default cluster cannot be deleted");
        }
        // 集群是否绑定租户
        List<Long> tenants = clusterTenantDao.listBoundedTenants(clusterId);
        if(CollectionUtils.isNotEmpty(tenants)){
            throw new RdosDefineException(String.format("Cluster %s has tenants and cannot be deleted",cluster.getClusterName()));
        }
        clusterDao.logicRemoveCluster(clusterId);

        consoleFileSyncDao.removeByClusterId(clusterId);
        consoleFileSyncDetailDao.deleteByClusterId(clusterId);

        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(clusterId, EComponentType.YARN.getTypeCode(), null);
        componentAuxiliaryService.removeAuxiliaryConfig(auxiliary);

        componentService.asyncUpdateCache(clusterId, null, Sets.newHashSet(tenants), false);
    }

    /**
     * 获取集群配置
     * @param clusterId 集群id
     * @param removeTypeName
     * @param multiVersion 组件默认版本
     * @return
     */
    public ClusterVO getCluster( Long clusterId, Boolean removeTypeName,boolean multiVersion) {
        return getCluster(clusterId,removeTypeName,false,multiVersion,false);
    }

    public ClusterVO getCluster4PluginInfo(Long clusterId, boolean multiVersion) {
        Cluster cluster = clusterDao.getOne(clusterId);
        return pluginInfoCacheManager.cacheCluster(cluster, multiVersion);
    }

    /**
     * 获取集群及其组件
     * 备注：内部使用
     *
     * @param cluster 集群信息
     * @return 集群及组件配置
     */
    public ClusterVO getCluster4PluginInfo(Cluster cluster, boolean multiVersion) {
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        // 查询默认版本或者多个版本
        List<Component> components = componentDao.listByClusterId(cluster.getId(),null, !multiVersion);
        if (CollectionUtils.isNotEmpty(components)) {
            //默认优先
            components = components.stream()
                    .sorted(Comparator.comparing(Component::getIsDefault, (c1, c2) -> BooleanComparator.getTrueFirstComparator().compare(c1, c2)))
                    .collect(Collectors.toList());
        }
        List<IComponentVO> componentVoList = componentConfigService.getComponentVo(components, multiVersion);
        Map<EComponentScheduleType, List<IComponentVO>> scheduleType = new HashMap<>(8);
        // 组件根据用途分组(计算,资源)
        if (CollectionUtils.isNotEmpty(componentVoList)) {
            scheduleType = componentVoList.stream().collect(Collectors.groupingBy(c -> EComponentType.getScheduleTypeByComponent(c.getComponentTypeCode())));
        }
        List<SchedulingVo> schedulingVos = convertComponentToScheduling(null, scheduleType);
        clusterVO.setScheduling(schedulingVos);
        clusterVO.setCanModifyMetadata(checkMetadata(cluster.getId(), components));
        return clusterVO;
    }

    /**
     * 根据 clusterId 获取集群信息, 查询不到则返回 default 集群
     * @param clusterId 集群 id
     * @return 集群信息
     */
    public Cluster getClusterByIdOrDefault(Long clusterId) {
        Cluster cluster = clusterDao.getOne(clusterId);
        return Objects.isNull(cluster) ? clusterDao.getOne(DEFAULT_CLUSTER_ID) : cluster;
    }

    /**
     * 获取集群信息详情 需要根据组件分组
     *
     * @param clusterId 集群 id
     * @return 集群配置信息
     */
    public ClusterVO getCluster(Long clusterId, Boolean removeTypeName,boolean isFullPrincipal,boolean multiVersion,boolean sm2Encrypt) {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        // 查询默认版本或者多个版本
        List<Component> components = componentDao.listByClusterId(clusterId,null,!multiVersion);
        if (CollectionUtils.isNotEmpty(components)) {
            //默认优先 防止前端判断不到默认
            components = components.stream()
                    .sorted(Comparator.comparing(Component::getIsDefault, (c1, c2) -> BooleanComparator.getTrueFirstComparator().compare(c1, c2)))
                    .collect(Collectors.toList());
        }
        List<IComponentVO> componentConfigs = componentConfigService.getComponentVoByComponent(components,
                null == removeTypeName || removeTypeName , clusterId, multiVersion,sm2Encrypt);
        Table<Integer,String ,KerberosConfig> kerberosTable = null;
        // k8s的配置
        if(isFullPrincipal){
            kerberosTable= HashBasedTable.create();
            for (KerberosConfig kerberosConfig : kerberosDao.getByClusters(clusterId)) {
                kerberosTable.put(kerberosConfig.getComponentType(), StringUtils.isBlank(kerberosConfig.getComponentVersion())?
                        StringUtils.EMPTY:kerberosConfig.getComponentVersion(),kerberosConfig);
            }
        }

        Map<EComponentScheduleType, List<IComponentVO>> scheduleType = new HashMap<>(4);
        // 组件根据用途分组(计算,资源)
        if (CollectionUtils.isNotEmpty(componentConfigs)) {
            scheduleType = componentConfigs.stream().collect(Collectors.groupingBy(c -> EComponentType.getScheduleTypeByComponent(c.getComponentTypeCode())));
        }
        List<SchedulingVo> schedulingVos = convertComponentToScheduling(kerberosTable, scheduleType);
        clusterVO.setScheduling(schedulingVos);
        clusterVO.setCanModifyMetadata(checkMetadata(clusterId, components));
        return clusterVO;
    }

    private boolean checkMetadata(Long clusterId, List<Component> components) {
        if (components.stream().anyMatch(c -> EComponentType.metadataComponents.contains(EComponentType.getByCode(c.getComponentTypeCode())))) {
            List<Long> clusterTenants = clusterTenantDao.listBoundedTenants(clusterId);
            return CollectionUtils.isEmpty(clusterTenants);
        }
        return true;
    }

    private List<SchedulingVo> convertComponentToScheduling(Table<Integer,String ,KerberosConfig> kerberosTable, Map<EComponentScheduleType, List<IComponentVO>> scheduleType) {
        List<SchedulingVo> schedulingVos = new ArrayList<>();
        //为空也返回
        for (EComponentScheduleType value : EComponentScheduleType.values()) {
            SchedulingVo schedulingVo = new SchedulingVo();
            schedulingVo.setSchedulingCode(value.getType());
            schedulingVo.setSchedulingName(value.getName());
            List<IComponentVO> componentVoList = scheduleType.getOrDefault(value,Collections.emptyList());
            if(Objects.nonNull(kerberosTable) && !kerberosTable.isEmpty() && CollectionUtils.isNotEmpty(componentVoList)){
                componentVoList.forEach(component->{
                    // 组件每个版本设置k8s参数
                    for (ComponentVO componentVO : component.loadComponents()) {
                        KerberosConfig kerberosConfig;
                        if (!ComponentVersionUtil.isMultiVersionComponent(componentVO.getComponentTypeCode())) {
                            kerberosConfig = kerberosTable.get(componentVO.getComponentTypeCode(), StringUtils.EMPTY);
                        } else {
                            kerberosConfig = kerberosTable.get(componentVO.getComponentTypeCode(), StringUtils.isBlank(componentVO.getHadoopVersion()) ?
                                    StringUtils.EMPTY : componentVO.getHadoopVersion());
                        }
                        if(Objects.nonNull(kerberosConfig)){
                            componentVO.setPrincipal(kerberosConfig.getPrincipal());
                            componentVO.setPrincipals(kerberosConfig.getPrincipals());
                            componentVO.setMergeKrb5Content(kerberosConfig.getMergeKrbContent());
                        }
                    }
                });
            }
            schedulingVo.setComponents(componentVoList);
            schedulingVos.add(schedulingVo);
        }
        return schedulingVos;
    }



    public List<ClusterEngineVO> getAllCluster() {
        List<ClusterEngineVO> result = new ArrayList<>();
        List<Cluster> clusters = clusterDao.listAll();
        if(CollectionUtils.isEmpty(clusters)){
            return new ArrayList<>();
        }

        List<Long> clusterIds = clusters.stream().map(Cluster::getId).collect(Collectors.toList());
        List<Component> components = componentDao.listComponentByClusterId(clusterIds,null);
        Map<Long, Set<MultiEngineType>> clusterEngineMapping = new HashMap<>();
        Set<Long> kubernetesCluster = new HashSet<>();
        if(CollectionUtils.isNotEmpty(components)){
            clusterEngineMapping = components.stream().filter(c -> {
                MultiEngineType multiEngineType = EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()),c.getDeployType());
                return null != multiEngineType && !MultiEngineType.COMMON.equals(multiEngineType);
            }).collect(Collectors.groupingBy(Component::getClusterId,
                    Collectors.mapping(c -> EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()),c.getDeployType()), Collectors.toSet())));

            kubernetesCluster = components.stream().filter(c -> EComponentType.KUBERNETES.getTypeCode().equals(c.getComponentTypeCode())).map(Component::getClusterId).collect(Collectors.toSet());
        }
        List<Queue> queues = queueDao.listByClusterWithLeaf(clusterIds);

        Map<Long, List<Queue>> engineQueueMapping = queues
                .stream()
                .collect(Collectors.groupingBy(Queue::getClusterId));

        for (Cluster cluster : clusters) {
            ClusterEngineVO vo = fillEngineQueueInfo(clusterEngineMapping, engineQueueMapping, cluster,kubernetesCluster);
            result.add(vo);
        }

        return result;
    }

    private ClusterEngineVO fillEngineQueueInfo(Map<Long, Set<MultiEngineType>> clusterEngineMapping, Map<Long, List<Queue>> engineQueueMapping, Cluster cluster, Set<Long> kubernetesCluster) {
        ClusterEngineVO vo = ClusterEngineVO.toVO(cluster);
        Set<MultiEngineType> engineList = clusterEngineMapping.get(vo.getClusterId());
        if (CollectionUtils.isNotEmpty(engineList)) {
            List<EngineVO> engineVOS = new ArrayList<>();
            for (MultiEngineType multiEngineType : engineList) {
                EngineVO engineVO = new EngineVO();
                engineVO.setEngineType(multiEngineType.getType());
                engineVO.setEngineName(multiEngineType.getName());
                engineVO.setClusterId(cluster.getId());
                if (MultiEngineType.HADOOP.equals(multiEngineType)) {
                    engineVO.setQueues(QueueVO.toVOs(engineQueueMapping.get(cluster.getId())));
                }
                //前端区分是否是on yarn 还是on kubernetes
                if (kubernetesCluster.contains(cluster.getId())) {
                    engineVO.setResourceType(EComponentType.KUBERNETES.getName());
                } else {
                    engineVO.setResourceType(EComponentType.YARN.getName());
                }
                engineVOS.add(engineVO);
            }
            engineVOS.sort(Comparator.comparingInt(Engine::getEngineType));
            vo.setEngines(engineVOS);
        }
        return vo;
    }

    public String pluginInfoForType(Long dtUicTenantId, Boolean fullKerberos, Integer pluginType,boolean needHdfsConfig) {
        EComponentType type = EComponentType.getByCode(pluginType);
        return getConfigByKey(dtUicTenantId, type.getConfName(),fullKerberos,null,needHdfsConfig);
    }

    public String dbInfo(Long dtUicTenantId, Long dtUicUserId, Integer type, ProjectVO projectVO) {
        DataSourceType sourceType = DataSourceType.getSourceType(type);
        return accountInfo(dtUicTenantId,dtUicUserId,sourceType,null, projectVO);
    }

    public Boolean isSameCluster(Long dtUicTenantId, List<Long> dtUicTenantIds) {
        if (dtUicTenantId ==null) {
            throw new RdosDefineException("The tenant id cannot be null");
        }

        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return Boolean.FALSE;
        }

        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);

        if (clusterId == null) {
            throw new RdosDefineException("租户id:"+dtUicTenantId+"不存在!");
        }

        for (Long uicTenantId : dtUicTenantIds) {
            Long checkClusterId = clusterTenantDao.getClusterIdByDtUicTenantId(uicTenantId);

            if (checkClusterId != null) {
                if (clusterId.equals(checkClusterId)) {
                    // dtUicTenantIds集合中存在和 dtUicTenantId相同的集群
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

    @Cacheable(cacheNames = "standalone")
    public boolean hasStandalone(Long tenantId, Integer typeCode) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);
        if (null != clusterId) {
            return null != componentDao.getByClusterIdAndComponentType(clusterId, typeCode, null, EDeployType.STANDALONE.getType());
        }
        return false;
    }

    @CacheEvict(cacheNames = "standalone", allEntries = true)
    public void clearStandaloneCache() {
        LOGGER.info("clear all standalone cache");
    }

    public List<Component> clusterComponent(Long uicTenantId, boolean multiVersion) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(uicTenantId);
        // 查询默认版本或者多个版本
        return componentDao.listByClusterId(clusterId, null, !multiVersion);
    }

    public String componentInfo(Long dtUicTenantId, Long dtUicUserId, Integer componentTypCode, String componentVersion, ProjectVO projectVO) {
        EComponentType componentType = EComponentType.getByCode(componentTypCode);
        if(null == componentType){
            throw new RdosDefineException("not support component");
        }
        Map<Integer,String> componentMap = new HashMap<>();
        componentMap.put(componentTypCode,componentVersion);

        // 集群级账号
        String componentInfo = getConfigByKey(dtUicTenantId, componentType.getConfName(), false,componentMap,false);
        DataSourceType dataSourceType = getDataSourceType(componentType);
        if (null == dataSourceType) {
            LOGGER.warn("componentInfo, {} not find dataSourceType, skip", componentType);
            return componentInfo;
        }
        /************* 优先级: 个人 > 项目 > 集群 ************/
        // 项目级账号
        componentInfo = replaceProjectAccountIfNeed(projectVO, componentType, componentInfo);

        //优先绑定账号
        AccountTenant dbAccountTenant = accountTenantDao.getByUserIdAndTenantIdAndEngineType(dtUicUserId, dtUicTenantId, dataSourceType.getVal());
        if (null == dbAccountTenant) {
            return componentInfo;
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if(null == account){
            return componentInfo;
        }
        JSONObject data = JSONObject.parseObject(componentInfo);
        data.put(USERNAME, account.getName());
        data.put(PASSWORD, Base64Util.baseDecode(account.getPassword()));
        return data.toJSONString();
    }

    /**
     * 如果存在项目级账号，则替换
     * @param projectVO
     * @param componentType
     * @param componentInfo
     * @return
     */
    private String replaceProjectAccountIfNeed(ProjectVO projectVO, EComponentType componentType, String componentInfo) {
        if (projectVO == null) {
            return componentInfo;
        }
        ConsoleProjectAccountVO cpo = new ConsoleProjectAccountVO();
        cpo.setProjectId(projectVO.getProjectId());
        cpo.setAppType(projectVO.getAppType());
        cpo.setComponentTypeCode(componentType.getTypeCode());
        ConsoleProjectAccountVO projectAccountVO = consoleProjectAccountService.findByProjectAndComponent(cpo);
        if (projectAccountVO != null && ESwitch.ON.getCode().equals(projectAccountVO.getStatus())) {
            JSONObject data = JSONObject.parseObject(componentInfo);
            // 覆盖掉集群级账号
            data.put(GlobalConst.USERNAME, projectAccountVO.getUserName());
            data.put(GlobalConst.PASS_WORD, Base64Util.baseDecode(projectAccountVO.getPassword()));
            componentInfo = data.toJSONString();
        }
        return componentInfo;
    }

    /**
     * 获取 yarn 的 pluginInfo
     *
     * @param clusterId 集群 id
     * @return yarn plugin info
     */
    public JSONObject getYarnInfo(Long clusterId, Long dtUicTenantId,
                                  Long dtUicUserId) {
        try {
            Cluster cluster = clusterDao.getOne(clusterId);
            if (cluster == null) {
                throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
            }
            Component yarnComponent = componentService.getComponentByClusterId(cluster.getId(),EComponentType.YARN.getTypeCode(),null);
            return pluginInfoManager.buildConsolePluginInfo(yarnComponent, cluster, null, null, null,dtUicTenantId,dtUicUserId);
        } catch (Exception e) {
            LOGGER.error("getYarnInfo error: ", e);
            throw new RdosDefineException("acquire getYarnInfo error.");
        }
    }

    /**
     * 获取 trino 的 pluginInfo
     *
     * @param clusterId 集群 id
     * @return yarn plugin info
     */
    public JSONObject getTrinoInfo(Long clusterId, Long dtUicTenantId,
                                  Long dtUicUserId) {
        try {
            Cluster cluster = clusterDao.getOne(clusterId);
            if (cluster == null) {
                throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
            }
            Component yarnComponent = componentService.getComponentByClusterId(cluster.getId(),EComponentType.TRINO_SQL.getTypeCode(),null);
            return pluginInfoManager.buildConsolePluginInfo(yarnComponent, cluster, null, null, null,dtUicTenantId,dtUicUserId);
        } catch (Exception e) {
            LOGGER.error("getTrinoInfo error: ", e);
            throw new RdosDefineException("acquire getTrinoInfo error.");
        }
    }

    public Long getClusterId(Long dtUicTenantId) {
      return clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);
    }


    public boolean hasRangerAndLdap(Long clusterId) {
        List<Component> commonComponents = componentService.listCommonComponents(clusterId);
        if (CollectionUtils.isEmpty(commonComponents)) {
            return false;
        }
        List<Integer> commonComponentTypeCodes = commonComponents.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        return commonComponentTypeCodes.containsAll(Lists.newArrayList(EComponentType.LDAP.getTypeCode(),EComponentType.RANGER.getTypeCode()));
    }

    public TenantClusterCommonConfigVO getTenantClusterCommonConfig(Long tenantId) {
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(tenantId);
        if (Objects.isNull(clusterTenant)) {
            throw new RdosDefineException(TENANT_NOT_BIND);
        }
        String commonConfig = clusterTenant.getCommonConfig();
        return JSONObject.parseObject(commonConfig, TenantClusterCommonConfigVO.class);
    }

    public void updateTenantClusterCommonConfig(Long tenantId, String clusterCommonConfigVO) {
        ClusterTenant clusterTenant = clusterTenantDao.getByDtuicTenantId(tenantId);
        if (Objects.isNull(clusterTenant) || StringUtils.isBlank(clusterCommonConfigVO)) {
            return;
        }
        clusterTenantDao.updateCommonConfig(tenantId, clusterCommonConfigVO);
    }

    public boolean hasComponent(Long clusterId, List<Integer> componentTypes) {
        if (Objects.isNull(clusterId) || CollectionUtils.isEmpty(componentTypes)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        List<Component> clusterComponents = componentDao.listAllByClusterIdAndComponentTypes(clusterId, componentTypes);
        List<Integer> allComponentTypes = clusterComponents.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        return allComponentTypes.containsAll(componentTypes);
    }
}

