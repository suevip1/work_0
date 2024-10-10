package com.dtstack.engine.master.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.vo.KerberosConfigVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.constrant.UicConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.master.enums.CrudEnum;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.plugininfo.service.PluginInfoService;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.AddTenantIdAndServiceRelationshipParam;
import com.dtstack.pubsvc.sdk.dto.param.CreateServiceParam;
import com.dtstack.pubsvc.sdk.dto.param.DeleteServiceParam;
import com.dtstack.pubsvc.sdk.dto.param.ServiceHasPolicyParam;
import com.dtstack.pubsvc.sdk.dto.param.datasource.EditConsoleParam;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.pubsvc.sdk.ranger.PubRangerServiceClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.TenantIdListParam;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static com.dtstack.engine.common.constrant.ConfigConstant.HADOOP_VERSION;
import static com.dtstack.engine.common.constrant.ConfigConstant.KERBEROS_CONFIG;

/**
 * @author yuebai
 * @date 2021-04-06
 */
@Service
public class DataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Autowired(required = false)
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private UicTenantApiClient uicTenantApiClient;

    @Autowired(required = false)
    private PubRangerServiceClient pubRangerClient;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private PluginInfoService pluginInfoService;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    /**
     * service name：字母、数字、下划线、横杠、空格
     */
    public static final Pattern REGULAR_SERVICE_NAME = Pattern.compile("^[a-zA-Z-0-9_ ]*$");

    public static final List<Integer> SYNC_TO_RANGER_COMPONENTS = Collections.unmodifiableList(
            Lists.newArrayList(EComponentType.HIVE_SERVER.getTypeCode(),
                    EComponentType.TRINO_SQL.getTypeCode(),
                    EComponentType.HDFS.getTypeCode()));

    public static final List<Integer> SECURITY_COMPONENTS = Collections.unmodifiableList(
            Lists.newArrayList(EComponentType.LDAP.getTypeCode(),
                    EComponentType.RANGER.getTypeCode()));

    public static final List<Integer> COMPONENTS_RELATED_TO_COMMON = Collections.unmodifiableList(
            Lists.newArrayList(EComponentType.HIVE_SERVER.getTypeCode(),
                    EComponentType.SPARK_THRIFT.getTypeCode(),
                    EComponentType.HDFS.getTypeCode())
    );

    /**
     * 开启 hadoop 代理的数据源类型
     */
    public static List<Integer> hadoopProxyDataSourceTypeList;

    static {
        hadoopProxyDataSourceTypeList = ImmutableList.of(
                DataSourceType.HIVE.getVal(), DataSourceType.HIVE1X.getVal(),
                DataSourceType.HIVE3.getVal(),DataSourceType.HIVE3X_CDP.getVal(),
                DataSourceType.SPARKTHRIFT2_1.getVal(),
                DataSourceType.HDFS.getVal(),
                DataSourceType.HDFS3.getVal(), DataSourceType.HDFS3_CDP.getVal()
        );
    }

    /**
     * 将最新的组件的信息提交到数据源中心
     *
     * @param clusterId
     * @param componentTypeCode
     * @param dtUicTenantIds
     */
    public void publishComponent(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {
        if (null == dataSourceAPIClient) {
            LOGGER.info("datasource url is not init so skip, clusterId:{}, componentTypeCode:{}", clusterId, componentTypeCode);
            return;
        }
        if (clusterId == null || componentTypeCode == null || CollectionUtils.isEmpty(dtUicTenantIds)) {
            return;
        }
        if (EComponentType.YARN.getTypeCode().equals(componentTypeCode)) {
            // yarn 变化，如版本号变更，需要重新推送 hdfs
            pushDatasourceToPublicService(clusterId, EComponentType.HDFS.getTypeCode(), dtUicTenantIds);
        } else if (EComponentType.HDFS.getTypeCode().equals(componentTypeCode)) {
            pushDatasourceToPublicService(clusterId, EComponentType.HIVE_SERVER.getTypeCode(), dtUicTenantIds);
            pushDatasourceToPublicService(clusterId, EComponentType.SPARK_THRIFT.getTypeCode(), dtUicTenantIds);
            pushDatasourceToPublicService(clusterId, EComponentType.HDFS.getTypeCode(), dtUicTenantIds);
        } else if (EComponentType.COMMON.getTypeCode().equals(componentTypeCode)) {
            // Common 组件发生了变化，需要推送受影响的其他组件
            for (Integer relatedComponent : COMPONENTS_RELATED_TO_COMMON) {
                pushDatasourceToPublicService(clusterId, relatedComponent, dtUicTenantIds);
            }
        } else {
            pushDatasourceToPublicService(clusterId, componentTypeCode, dtUicTenantIds);
        }
    }

    private void pushDatasourceToPublicService(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {
        List<Component> components = componentDao.listByClusterId(clusterId, componentTypeCode, false);
        Component component = CollectionUtils.isEmpty(components) ? null : components.get(0);
        if (null == component) {
            LOGGER.info("clusterId {} componentType {} component is null", clusterId, componentTypeCode);
            return;
        }
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(componentTypeCode), component.getHadoopVersion(), component.getVersionName());
        if (null == dataSourceType) {
            LOGGER.info("clusterId {} componentType {} is not support datasource ", clusterId, componentTypeCode);
            return;
        }
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        try {
            if (EComponentType.SFTP.getTypeCode().equals(componentTypeCode)) {
                editConsoleParam = getSftpConsoleParam(clusterId, dtUicTenantIds);
            } else {
                Map<String, Object> configMap = componentConfigService.convertComponentConfigToMap(component.getId(), false);
                editConsoleParam = getEditConsoleParam(clusterId, componentTypeCode, dtUicTenantIds, component,configMap);
            }
            Cluster cluster = clusterDao.getOne(clusterId);
            editConsoleParam.setClusterName(null == cluster ? "" : cluster.getClusterName());
            editConsoleParam.setType(dataSourceType.getVal());
            dataSourceAPIClient.editConsoleDs(editConsoleParam);
            LOGGER.info("update datasource jdbc clusterId {} componentType {} component info {}", clusterId, componentTypeCode, editConsoleParam.toString());
        } catch (Exception e) {
            LOGGER.error("update datasource jdbc clusterId {} componentType {} component info {} error ", clusterId, componentTypeCode, editConsoleParam.toString(), e);
            throw new RdosDefineException(ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * 同步 ldap 目录信息
     * @param componentCode
     * @param clusterId
     * @param clusterName
     */
    public void syncLdapDir(Integer componentCode, Long clusterId, String clusterName) {
        if (!EComponentType.LDAP.getTypeCode().equals(componentCode)) {
            return;
        }
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.LDAP.getTypeCode(), null, null);
        if (component == null) {
            return;
        }
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        Map<String, Object> configMap = componentConfigService.convertComponentConfigToMap(component.getId(), false);
        editConsoleParam.setUsername((String) configMap.get(ConfigConstant.USERNAME));
        editConsoleParam.setPassword((String) configMap.get(ConfigConstant.PASSWORD));
        editConsoleParam.setJdbcUrl((String)configMap.get(GlobalConst.URL));
        editConsoleParam.setBaseDN((String)configMap.get(UicConstant.BASE_DN));
        editConsoleParam.setUserOu((String)configMap.get(UicConstant.USER_OU));
        editConsoleParam.setGroupOu((String)configMap.get(UicConstant.USER_GROUP_OU));
        editConsoleParam.setClusterName(clusterName);
        editConsoleParam.setType(DataSourceType.LDAP.getVal());
        dataSourceAPIClient.createLdapDirectory(editConsoleParam);
        LOGGER.info("createLdapDirectory, clusterId:{}, component info: {}", clusterId, editConsoleParam);
    }

    private EditConsoleParam getSftpConsoleParam(Long clusterId, Set<Long> dtUicTenantIds) {
        JSONObject sftpConfig = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, JSONObject.class, null);
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        editConsoleParam.setDsDtuicTenantIdList(new ArrayList<>(dtUicTenantIds));
        editConsoleParam.setSftpConf(sftpConfig);
        return editConsoleParam;
    }

    public EditConsoleParam getEditConsoleParam(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds, Component component,Map<String, Object> configMap) {
        //推送数据源中心
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        editConsoleParam.setDsDtuicTenantIdList(new ArrayList<>(dtUicTenantIds));
        if (EComponentType.RANGER.getTypeCode().equals(componentTypeCode)
                || EComponentType.LDAP.getTypeCode().equals(componentTypeCode)) {
            editConsoleParam.setJdbcUrl((String)configMap.get(GlobalConst.URL));
            editConsoleParam.setBaseDN((String)configMap.get(UicConstant.BASE_DN));
            editConsoleParam.setUserOu((String)configMap.get(UicConstant.USER_OU));
            editConsoleParam.setGroupOu((String)configMap.get(UicConstant.USER_GROUP_OU));
        } else {
            editConsoleParam.setJdbcUrl((String) configMap.get(ConfigConstant.JDBCURL));
        }
        if (!EComponentType.HDFS.getTypeCode().equals(componentTypeCode)) {
            // 配置数据安全，hdfs 可能有 username、password 的自定义参数，此时不要进行传输
            editConsoleParam.setUsername((String) configMap.get(ConfigConstant.USERNAME));
            editConsoleParam.setPassword((String) configMap.get(ConfigConstant.PASSWORD));
        }
        editConsoleParam.setHiveMetaStoreUris((String)configMap.getOrDefault(ConfigConstant.METASTORE_URI,""));
        editConsoleParam.setDataVersion(component.getHadoopVersion());
        editConsoleParam.setIsMetaHadoop(BooleanUtils.toBoolean(Optional.ofNullable(component.getIsMetadata()).orElse(0)));
        JSONObject sftpConfig = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, JSONObject.class, null);
        editConsoleParam.setSftpConf(sftpConfig);

        if (StringUtils.isNotBlank(component.getSslFileName())) {
            JSONObject sslConfig = clusterService.getSslConfig(clusterId, EComponentType.getByCode(componentTypeCode), component.getHadoopVersion());
            editConsoleParam.setSslConf(sslConfig);
        }

        if (StringUtils.isNotBlank(component.getKerberosFileName())) {
            //kerberos 配置信息
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentTypeCode, ComponentVersionUtil.formatMultiVersion(componentTypeCode, component.getHadoopVersion()));
            KerberosConfigVO kerberosConfigVO = clusterService.addKerberosConfigWithHdfs(componentTypeCode, clusterId, kerberosConfig);
            editConsoleParam.setKerberosConfig(JSONObject.parseObject(JSONObject.toJSONString(kerberosConfigVO)));
        } else {
            // 是否需要补充 hdfs 的 kerberos 信息
            boolean needPatchHdfsKerberos = EComponentType.HIVE_SERVER.getTypeCode().equals(componentTypeCode)
                    || EComponentType.SPARK_THRIFT.getTypeCode().equals(componentTypeCode);
            if (needPatchHdfsKerberos) {
                // 若当前组件未配置 kerberos，则获取 hdfs 的 kerberos 文件，数据源中心校验联通性会用到。
                // 出现这一场景原因: hiveServer 开启了 ldap 认证，就不用传 kerberos 认证，但此时 hdfs 又需要 kerberos 认证。
                Component hdfsComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.HDFS.getTypeCode(),null,null);
                if (hdfsComponent != null) {
                    KerberosConfig hdfsKerberosConfig = kerberosDao.getByComponentType(clusterId, EComponentType.HDFS.getTypeCode(), ComponentVersionUtil.formatMultiVersion(EComponentType.HDFS.getTypeCode(), hdfsComponent.getHadoopVersion()));
                    KerberosConfigVO hdfsKerberosConfigVO = clusterService.addKerberosConfigWithHdfs(componentTypeCode, clusterId, hdfsKerberosConfig);
                    if (hdfsKerberosConfigVO != null) {
                        editConsoleParam.setKerberosConfig(JSONObject.parseObject(JSONObject.toJSONString(hdfsKerberosConfigVO)));
                    }
                }
            }
        }

        JSONObject hdfsConfig = componentService.getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(), false, JSONObject.class, null);
        if (null != hdfsConfig) {
            if (EComponentType.TRINO_SQL.getTypeCode().equals(componentTypeCode)) {
                setTrinoHdfsKerberosConfig(clusterId, componentTypeCode, editConsoleParam);
            } else {
                editConsoleParam.setHdfsConfig(hdfsConfig);
            }
        }
        Map<String, Object> customMap = componentConfigService.getComponentConfigByType(component.getId(), EFrontType.CUSTOM_CONTROL);
        if (null == customMap) {
            customMap = new HashMap<>();
        }
        //hive 补充hadoopVersion 用于业务中心区分tbds账号信息
        if (EComponentType.HIVE_SERVER.getTypeCode().equals(component.getComponentTypeCode())) {
            customMap.put(HADOOP_VERSION, component.getVersionName());
        }
        if (MapUtils.isNotEmpty(customMap)) {
            editConsoleParam.setCustomConfig(new JSONObject(customMap));
        }
        // 将数据源使用必须的参数推到数据源中心
        editConsoleParam.setConfigMap(new JSONObject(configMap));

        // 补充公共配置信息，比如 hadoop.proxy.enable
        Map<String, Object> commonConf = componentService.getComponentByClusterId(clusterId, EComponentType.COMMON.getTypeCode(), false, Map.class, null);
        Optional.ofNullable(commonConf).ifPresent(c -> editConsoleParam.setCommonConf(new JSONObject(commonConf)));
        return editConsoleParam;
    }

    private void setTrinoHdfsKerberosConfig(Long clusterId, Integer componentTypeCode, EditConsoleParam editConsoleParam) {
        Component hdfsComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.HDFS.getTypeCode(),null,null);
        if (hdfsComponent != null) {
            KerberosConfig hdfsKerberosConfig = kerberosDao.getByComponentType(clusterId, EComponentType.HDFS.getTypeCode(), ComponentVersionUtil.formatMultiVersion(EComponentType.HDFS.getTypeCode(), hdfsComponent.getHadoopVersion()));
            KerberosConfigVO hdfsKerberosConfigVO = clusterService.addKerberosConfigWithHdfs(componentTypeCode, clusterId, hdfsKerberosConfig);
            if (hdfsKerberosConfigVO != null) {
                JSONObject kerberosConfig = JSONObject.parseObject(JSONObject.toJSONString(hdfsKerberosConfigVO));
                JSONObject hdfsConfig = new JSONObject();
                hdfsConfig.put(KERBEROS_CONFIG, kerberosConfig);
                editConsoleParam.setHdfsConfig(hdfsConfig);
            }
        }
    }

    public void loadJdbcInfoAndUpdateCache(JobClient jobClient, String pluginInfo) {
        try {
            if (null == dataSourceAPIClient) {
                LOGGER.info("datasource url is not init so skip");
                return;
            }
            LOGGER.info("jobId: {}, pluginInfo:{}", jobClient.getTaskId(), pluginInfo);
            if (StringUtils.isNotBlank(pluginInfo)) {
                try {
                    JSONObject pluginInfoJson = JSON.parseObject(pluginInfo);
                    Long dataSourceId = pluginInfoJson.getLong(ConfigConstant.DATA_SOURCE_ID);
                    if (dataSourceId != null) {
                        DsServiceInfoDTO data = dataSourceAPIClient.getDsInfoById(dataSourceId).getData();
                        if (data != null) {
                            LOGGER.info("load jdbc info, sourceId: {}, jdbc data json:{}", dataSourceId, data.getDataJson());
                            JSONObject convertPluginInfo = pluginInfoService.convertDsPluginInfo(data, null, null);
                            // 填充 ldapUser 信息
                            if (data.getIsMeta().equals(GlobalConst.YES)) {
                                String engineType = jobClient.getEngineType();
                                EngineTypeComponentType type = EngineTypeComponentType.getByEngineName(engineType);
                                if (type != null) {
                                    JSONObject securityConf = Objects.isNull(convertPluginInfo.getJSONObject(PluginInfoConst.SECURITY_CONF)) ?
                                            new JSONObject() : convertPluginInfo.getJSONObject(PluginInfoConst.SECURITY_CONF);
                                    Long clusterId = clusterService.getClusterId(data.getDtuicTenantId());
                                    JSONObject ldapConf = clusterService.getLdapConf(type.getComponentType().getTypeCode(), jobClient.getUserId(), data.getDtuicTenantId(), clusterId, convertPluginInfo.getJSONObject(PluginInfoConst.SELF_CONF));
                                    securityConf.put(PluginInfoConst.LDAP_CONF, ldapConf);
                                    JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.PROXY_CONF, pluginInfoService.getProxyConf(ldapConf, JSONObject.parseObject(data.getDataJson())));
                                    pluginInfoJson.put(PluginInfoConst.SECURITY_CONF, securityConf);
                                }
                            }
                            // 设置 pluginInfo
                            pluginInfoJson.putAll(convertPluginInfo);
                            jobClient.setPluginInfo(pluginInfoJson.toJSONString());
                            // 更新 jobInfo
                            engineJobCacheDao.updateJobInfo(JobClientUtil.getParamAction(jobClient).toString(), jobClient.getTaskId());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("load dtDataSourceId{} error ", pluginInfo, e);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("load dtDataSourceId{} error ", pluginInfo, e);
        }
    }

    /**
     * 集群已经绑定了租户的情况下，此时租户新增了 ldap/ranger，需要对这些租户进行处理
     * @param componentCode
     * @param clusterId
     */
    public void bindHistoryLdapIfNeeded(Integer componentCode, Long clusterId, CrudEnum crudEnum) {
        if (crudEnum == CrudEnum.DELETE) {
            return;
        }
        if (!SECURITY_COMPONENTS.contains(componentCode)) {
            return;
        }
        if (clusterId == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        // 校验另一个组件的存在性
        Component anotherComponent;
        if (EComponentType.LDAP.getTypeCode().equals(componentCode)) {
            anotherComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.RANGER.getTypeCode(), null, null);
        } else {
            anotherComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.LDAP.getTypeCode(), null, null);
        }
        if (anotherComponent == null) {
            return;
        }
        Set<Long> dtUicTenantIds = tenantService.findUicTenantIdsByClusterId(clusterId);
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return;
        }
        if (crudEnum == CrudEnum.ADD) {
            // 调用业务中心接口
            bindHistoryLdap(clusterId, dtUicTenantIds);
        } else if (crudEnum == CrudEnum.UPDATE) {
            throw new RdosDefineException(String.format("集群已绑定租户，%s 已配置，请勿修改 %s", anotherComponent.getComponentName(), EComponentType.getByCode(componentCode).getName()));
        }
    }

    public void bindLdapIfNeeded(Long clusterId, final Set<Long> dtUicTenantIds) {
        // 如果 cluster 配置了 ldap 和 ranger，则调用业务中心接口
        Component ldapComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.LDAP.getTypeCode(), null, null);
        if (ldapComponent == null) {
            return;
        }
        Component rangerComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.RANGER.getTypeCode(), null, null);
        if (rangerComponent == null) {
            return;
        }
        bindHistoryLdap(clusterId, dtUicTenantIds);
    }

    private void bindHistoryLdap(Long clusterId, Set<Long> dtUicTenantIds) {
        try {
            TenantIdListParam tenantIdListParam = new TenantIdListParam();
            tenantIdListParam.setTenantIdList(Lists.newArrayList(dtUicTenantIds));
            uicTenantApiClient.bindHistoryLdap(tenantIdListParam);
            LOGGER.info("bindHistoryLdap, ok, clusterId:{}, dtUicTenantIds:{}", clusterId, dtUicTenantIds);
        } catch (Exception e) {
            LOGGER.error("bindHistoryLdap, error, clusterId:{}, dtUicTenantIds:{}", clusterId, dtUicTenantIds, e);
        }
    }

    /**
     * 根据用户操作类型，将 hive/trino 组件信息同步至业务中心
     *
     * @param clusterId
     * @param component
     * @param dtUicTenantIds
     * @param crudEnum
     */
    public void syncToRangerIfNeeded(Long clusterId, Component component, Set<Long> dtUicTenantIds, CrudEnum crudEnum) {
        Integer componentTypeCode = component.getComponentTypeCode();
        if (noNeedSyncToRanger(clusterId, dtUicTenantIds, componentTypeCode)) {
            return;
        }
        // 将组件信息同步到 ranger
        try {
            if (crudEnum == CrudEnum.DELETE) {
                DeleteServiceParam deleteServiceParam = parseConfigMap2DeleteServiceParam(dtUicTenantIds, component);
                pubRangerClient.deleteService(deleteServiceParam);
            } else {
                // 增、改
                Map<String, Object> configMap = componentConfigService.findSyncRangerComponentConfig(component.getId(), component.getComponentTypeCode(), false);
                CreateServiceParam createServiceParam = parseConfigMap2CreateServiceParam(configMap, dtUicTenantIds, component);
                pubRangerClient.addOrUpdateService(createServiceParam);
            }
            LOGGER.info("syncToRangerIfNeeded, {} ok, clusterId:{}, componentTypeCode:{}, dtUicTenantIds:{}", crudEnum.name(), clusterId, componentTypeCode, dtUicTenantIds);
        } catch(RdosDefineException e){
            throw e;
        } catch (Exception e) {
            LOGGER.error("syncToRangerIfNeeded, {} error, clusterId:{}, componentTypeCode:{}, dtUicTenantIds:{}", crudEnum.name(),  clusterId, componentTypeCode, dtUicTenantIds, e);
            throw new RdosDefineException(ErrorCode.HTTP_CALL_ERROR + ":" + e.getMessage(), e);
        }
    }

    /**
     * 将 hive、trino 与租户的绑定关系同步至业务中心
     * @param clusterId
     * @param component
     * @param dtUicTenantId
     */
    public void syncRelationToRangerIfNeeded(Long clusterId, Component component, Long dtUicTenantId) {
        Integer componentTypeCode = component.getComponentTypeCode();
        if (noNeedSyncToRanger(clusterId, Sets.newHashSet(dtUicTenantId), componentTypeCode)) {
            return;
        }
        Map<String, Object> configMap = componentConfigService.findSyncRangerComponentConfig(component.getId(), component.getComponentTypeCode(), false);
        try {
            AddTenantIdAndServiceRelationshipParam relationServiceParam = parseConfigMap2ReletionServiceParam(configMap, dtUicTenantId, component);
            pubRangerClient.addTenantIdAndServiceRelationship(relationServiceParam);
            LOGGER.info("syncRelationToRangerIfNeeded, ok, clusterId:{}, componentTypeCode:{}, dtUicTenantIds:{}", clusterId, componentTypeCode, dtUicTenantId);
        } catch (RdosDefineException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("syncRelationToRangerIfNeeded, error, clusterId:{}, componentTypeCode:{}, dtUicTenantIds:{}", clusterId, componentTypeCode, dtUicTenantId, e);
            throw new RdosDefineException(ErrorCode.HTTP_CALL_ERROR + ":" + e.getMessage(), e);
        }
    }

    /**
     * 用户新增 ranger 组件，需要将历史的组件同步至业务中心
     * @param clusterId
     */
    public void syncHistoryInfoToRangerIfNeeded(Long clusterId) {
        Set<Long> dtUicTenantIds = tenantService.findUicTenantIdsByClusterId(clusterId);
        for (Integer syncToRangerComponentTypeCode : SYNC_TO_RANGER_COMPONENTS) {
            Component syncToRangerComponent = componentDao.getByClusterIdAndComponentType(clusterId, syncToRangerComponentTypeCode, null, null);
            // 可能还没有配该组件，此时为 null，需跳过
            if (syncToRangerComponent == null) {
                continue;
            }
            Map<String, Object> configMap = componentConfigService.findSyncRangerComponentConfig(syncToRangerComponent.getId(), syncToRangerComponent.getComponentTypeCode(), false);
            componentConfigService.validateParamForSync2Ranger(configMap, syncToRangerComponentTypeCode);
            CreateServiceParam createServiceParam = parseConfigMap2CreateServiceParam(configMap, dtUicTenantIds, syncToRangerComponent);
            if (CollectionUtils.isEmpty(dtUicTenantIds)) {
                continue;
            }
            try {
                // 将组件信息同步到 ranger
                pubRangerClient.addOrUpdateService(createServiceParam);
                LOGGER.info("syncHistoryToRangerIfNeeded, ok, clusterId:{}, componentTypeCode:{}, dtUicTenantIds:{}", clusterId, syncToRangerComponentTypeCode, dtUicTenantIds);
            } catch (Exception e) {
                LOGGER.error("syncHistoryToRangerIfNeeded, error, clusterId:{}, componentTypeCode:{}, dtUicTenantIds:{}", clusterId, syncToRangerComponentTypeCode, dtUicTenantIds, e);
                throw new RdosDefineException(ErrorCode.HTTP_CALL_ERROR + ":" + e.getMessage(), e);
            }
        }
    }

    /**
     * 是否存在数据安全策略，true 表示存在，false 表示不存在
     * @param component
     * @param dtUicTenantIds
     * @return
     */
    public boolean serviceHasPolicy(Component component, Set<Long> dtUicTenantIds) {
        ServiceHasPolicyParam hasPolicyParam = new ServiceHasPolicyParam();
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(component.getComponentTypeCode()), component.getHadoopVersion(), component.getVersionName());
        if (dataSourceType == null) {
            throw new RdosDefineException(ErrorCode.NOT_SUPPORT);
        }
        hasPolicyParam.setDataTypeCode(dataSourceType.getVal());
        hasPolicyParam.setDtuicTenantIds(new ArrayList<>(dtUicTenantIds));

        boolean hasPolicy = false;
        try {
            ApiResponse<Boolean> resp = pubRangerClient.serviceHasPolicy(hasPolicyParam);
            if (resp != null && resp.getData() != null) {
                hasPolicy = resp.getData();
            } else {
                hasPolicy = false;
            }
        } catch (Exception e) {
            LOGGER.error("serviceHasPolicy error, componentTypeCode:{}", component.getComponentTypeCode(), e);
            throw new RdosDefineException(ErrorCode.HTTP_CALL_ERROR, e);
        }
        return hasPolicy;
    }

    /**
     * 是否需要同步组件信息到 ranger
     * @param clusterId
     * @param dtUicTenantIds
     * @param componentTypeCode
     * @return true 即不需要
     */
    private boolean noNeedSyncToRanger(Long clusterId, Set<Long> dtUicTenantIds, Integer componentTypeCode) {
        if (!SYNC_TO_RANGER_COMPONENTS.contains(componentTypeCode)) {
            return true;
        }
        Component rangerComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.RANGER.getTypeCode(), null, null);
        if (rangerComponent == null) {
            LOGGER.info("syncToRangerIfNeeded, rangerComponent is null, so skip, clusterId:{}", clusterId);
            return true;
        }
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return true;
        }
        return false;
    }

    /**
     * 将控制台配置参数转换为 uic 需要的格式
     * @param configMap
     * @param dtUicTenantIds
     * @param component
     * @return
     */
    private CreateServiceParam parseConfigMap2CreateServiceParam(Map<String, Object> configMap, Set<Long> dtUicTenantIds, Component component) {
        CreateServiceParam serviceParam = new CreateServiceParam();
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(component.getComponentTypeCode()), component.getHadoopVersion(), component.getVersionName());
        if (dataSourceType == null) {
            throw new RdosDefineException(ErrorCode.NOT_SUPPORT);
        }
        serviceParam.setDataTypeCode(dataSourceType.getVal());
        serviceParam.setIsEnabled(Boolean.TRUE);
        String serviceName = (String) configMap.get(UicConstant.SERVICE_NAME);
        serviceParam.setName(serviceName);
        serviceParam.setDisplayName(serviceName);
        serviceParam.setDtuicTenantIds(new ArrayList<>(dtUicTenantIds));
        serviceParam.setDescription((String) configMap.get(UicConstant.DESCRIPTION));
        Map<String, String> configs = transformComponentConfigs(configMap, component);
        serviceParam.setConfigs(configs);
        return serviceParam;
    }

    /**
     * 参数转换
     * @param componentConfigs
     * @param component
     * @return
     */
    private Map<String, String> transformComponentConfigs(Map<String, Object> componentConfigs, Component component) {
        Map<String, String> configs = new HashMap<>(componentConfigs.size());
        if (component.getComponentTypeCode().equals(EComponentType.HIVE_SERVER.getTypeCode())) {
            transformSqlComponentConfigs(componentConfigs, configs);
            configs.put(UicConstant.JDBC_DRIVER_CLASS_NAME, UicConstant.JDBC_HIVE_DRIVER);
        } else if (component.getComponentTypeCode().equals(EComponentType.TRINO_SQL.getTypeCode())) {
            transformSqlComponentConfigs(componentConfigs, configs);
            configs.put(UicConstant.JDBC_DRIVER_CLASS_NAME, UicConstant.JDBC_TRINO_DRIVER);
        } else if (component.getComponentTypeCode().equals(EComponentType.HDFS.getTypeCode())) {
            // key 来自 UicConstant.SYNC_RANGER_HDFS_KEYS
            componentConfigs.forEach((k, v) -> configs.put(k, (String) v));
            configs.putIfAbsent("hadoop.security.authentication", "kerberos");
            String defaultFs = processNameNodeUrl(configs.get(UicConstant.DFS_NAMENODE_RPC_ADDRESS_NS1_NN1),
                    configs.get(UicConstant.DFS_NAMENODE_RPC_ADDRESS_NS1_NN2));
            configs.putIfAbsent(UicConstant.FS_DEFAULT_NAME, defaultFs);
            configs.putIfAbsent("hadoop.rpc.protection", "authentication");
            configs.putIfAbsent("tag.download.auth.users", "hdfs");
            configs.putIfAbsent("policy.download.auth.users", "hdfs");
            configs.remove(UicConstant.SERVICE_NAME);
            configs.remove(UicConstant.DESCRIPTION);
            configs.remove(UicConstant.DFS_NAMENODE_RPC_ADDRESS_NS1_NN1);
            configs.remove(UicConstant.DFS_NAMENODE_RPC_ADDRESS_NS1_NN2);
        }
        return configs;
    }

    private void transformSqlComponentConfigs(Map<String, Object> configMap, Map<String, String> configs) {
        configMap.forEach((k, v) -> {
            if (UicConstant.JDBC_URL.equalsIgnoreCase(k)) {
                String jdbcUrl = (String) configMap.get(UicConstant.JDBC_URL);
                // 去除端口后面的内容
                int trimIdx = StringUtils.ordinalIndexOf(jdbcUrl, GlobalConst.SLASH, 3);
                jdbcUrl = trimIdx > 0 ? StringUtils.substring(jdbcUrl, 0, trimIdx) : StringUtils.substring(jdbcUrl, 0);
                configs.put("jdbc.url", jdbcUrl);
                return;
            }
            if (UicConstant.SERVICE_NAME.equalsIgnoreCase(k)) {
                // skip this loop, 不需要 service_name 参数
                return;
            }
            configs.put(k, (String) v);
        });
    }

    /**
     * 拼接 nameNodeUrl
     * @param ns1
     * @param ns2
     * @return
     */
    private static String processNameNodeUrl(String ns1, String ns2) {
        if (StringUtils.isEmpty(ns1) && StringUtils.isEmpty(ns2)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS + ", dfs.namenode.rpc-address.ns1.nn1 or dfs.namenode.rpc-address.ns1.nn2 empty");
        }
        List<String> nameNodeUrl = new ArrayList<>(2);
        if (StringUtils.isNotEmpty(ns1)) {
            nameNodeUrl.add(ns1);
        }
        if (StringUtils.isNotEmpty(ns2)) {
            nameNodeUrl.add(ns2);
        }
        for (int i = 0; i < nameNodeUrl.size(); i++) {
            String url = nameNodeUrl.get(i);
            // 非 「hdfs://」打头，则补充之
            if (!StringUtils.startsWith(url, UicConstant.HDFS_PREFIX)) {
                url = UicConstant.HDFS_PREFIX + url;
            }
            nameNodeUrl.set(i, url);
        }
        return StringUtils.join(nameNodeUrl, GlobalConst.COMMA);
    }

    private AddTenantIdAndServiceRelationshipParam parseConfigMap2ReletionServiceParam(Map<String, Object> configMap, Long dtUicTenantId, Component component) {
        AddTenantIdAndServiceRelationshipParam relationServiceParam = new AddTenantIdAndServiceRelationshipParam();

        CreateServiceParam serviceParam = new CreateServiceParam();
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(component.getComponentTypeCode()), component.getHadoopVersion(), component.getVersionName());
        if (dataSourceType == null) {
            throw new RdosDefineException(ErrorCode.NOT_SUPPORT);
        }
        serviceParam.setDataTypeCode(dataSourceType.getVal());
        String serviceName = (String) configMap.get(UicConstant.SERVICE_NAME);
        validateServiceName(serviceName, component.getComponentName());
        serviceParam.setName(serviceName);
        relationServiceParam.setDtuicTenantId(dtUicTenantId);
        relationServiceParam.setServiceParams(Lists.newArrayList(serviceParam));
        return relationServiceParam;
    }

    private DeleteServiceParam parseConfigMap2DeleteServiceParam(Set<Long> dtUicTenantIds, Component component) {
        DeleteServiceParam deleteServiceParam = new DeleteServiceParam();
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(component.getComponentTypeCode()), component.getHadoopVersion(), component.getVersionName());
        if (dataSourceType == null) {
            throw new RdosDefineException(ErrorCode.NOT_SUPPORT);
        }
        deleteServiceParam.setDataTypeCode(dataSourceType.getVal());
        deleteServiceParam.setDtuicTenantIds(new ArrayList<>(dtUicTenantIds));
        return deleteServiceParam;
    }

    public static void validateServiceName(String serviceName, String componentName) {
        if (StringUtils.isEmpty(serviceName)) {
            throw new RdosDefineException(componentName + ",serviceName 不能为空");
        }
        if (serviceName.startsWith(StringUtils.SPACE) || serviceName.endsWith(StringUtils.SPACE)) {
            throw new RdosDefineException(componentName + ",serviceName 不能以空格开头、结尾");
        }
        if (!isRegularServiceName(serviceName)) {
            throw new RdosDefineException(componentName + ",serviceName 不允许特殊字符，除了-_和空格");
        }
        if (serviceName.length() > 255) {
            throw new RdosDefineException(componentName + ",serviceName 长度不能超过 255 个字符");
        }
    }

    public static boolean isRegularServiceName(String name) {
        return REGULAR_SERVICE_NAME.matcher(name).matches();
    }
}
