package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.engine.SourceConstant;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.common.util.PluginInfoUtil;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentAuxiliaryService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.plugininfo.service.PluginInfoService;
import com.dtstack.engine.master.security.DtKerberosUtil;
import com.dtstack.engine.master.security.chooser.DefaultKerberosChooser;
import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.KerberosReq;
import com.dtstack.engine.master.utils.MapUtil;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantApiVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICSimpleUserVo;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dtstack.engine.common.constrant.ConfigConstant.LDAP_USER_NAME;
import static com.dtstack.engine.common.constrant.ConfigConstant.TBDS;
import static com.dtstack.engine.common.constrant.ConfigConstant.TBDS_SECURE_ID;
import static com.dtstack.engine.common.constrant.ConfigConstant.TBDS_SECURE_KEY;
import static com.dtstack.engine.common.constrant.ConfigConstant.TBDS_USER_NAME;
import static com.dtstack.engine.common.constrant.ConfigConstant.TYPE_NAME;
import static com.dtstack.engine.common.constrant.ConfigConstant.URI_PARAMS_DELIM;

/**
 * abstract plugin adapter
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/10/10
 * company: www.dtstack.com
 */
public abstract class AbstractPluginAdapter implements IPluginInfoAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPluginAdapter.class);

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;

    @Autowired
    protected UicTenantApiClient uicTenantApiClient;

    @Autowired
    protected UicUserApiClient uicUserApiClient;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Resource
    protected KerberosDao kerberosDao;

    @Resource
    protected ComponentDao componentDao;

    @Autowired
    protected ComponentAuxiliaryService componentAuxiliaryService;

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Resource
    protected ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    protected PluginInfoService pluginInfoService;

    @Autowired
    private PluginInfoCacheManager pluginInfoCacheManager;

    @Resource
    private DefaultKerberosChooser defaultKerberosChooser;

    private ConcurrentHashMap<String, Integer> pluginInfoVersion = new ConcurrentHashMap<>();

    private AtomicInteger versionBuilder = new AtomicInteger(0);
    /**
     * 不参与 pluginInfo md5 计算的 key, 格式需要符合 JSONPATH 规则
     */
    private static final List<String> MD5_UNWANTED_PATH = Lists.newArrayList
            (
                    "$." + PluginInfoConst.SECURITY_CONF,
                    "$." + PluginInfoConst.APP_TYPE
            );

    /**
     * threadLocal 存储参数信息
     */
    private final ThreadLocal<PluginInfoContext> pluginInfoContextThreadLocal = new ThreadLocal<>();

    @Override
    public PluginInfoContext getPluginInfoContext() {
        return pluginInfoContextThreadLocal.get();
    }

    @Override
    public void setPluginInfoContext(PluginInfoContext pluginInfoContext) {
        this.pluginInfoContextThreadLocal.set(pluginInfoContext);
    }

    @Override
    public JSONObject buildPluginInfo(boolean isConsole) {

        // pluginInfo 构建前置处理
        beforeProcessPluginInfo();

        // 构建 pluginInfo
        JSONObject pluginInfo = doProcessPluginInfo(isConsole);

        // pluginInfo 构建后置处理
        afterProcessPluginInfo(pluginInfo);

        generatePluginInfoVersion(pluginInfo);

        return pluginInfo;
    }

    private void generatePluginInfoVersion(JSONObject pluginInfo) {
        String md5Sum = PluginInfoUtil.getMd5Sum(pluginInfo);
        Integer versionId = pluginInfoVersion.computeIfAbsent(md5Sum, (key) -> versionBuilder.incrementAndGet());
        pluginInfo.put(PluginInfoConst.PLUGIN_INFO_VERSION, versionId);
    }

    /**
     * pluginInfo 前置处理, 主要是参数校验等
     */
    protected void beforeProcessPluginInfo() {
        // check pluginInfo context isn't null
        if (Objects.isNull(getPluginInfoContext())) {
            throw new DtCenterDefException("plugin info context is null.");
        }
        if (Objects.isNull(getPluginInfoContext().getComponent())) {
            throw new DtCenterDefException("component is null.");
        }
        Component component = getPluginInfoContext().getComponent();
        if (MapUtils.isEmpty(getPluginInfoContext().getComponentVersionMap()))  {
            getPluginInfoContext().setComponentVersionMap(Collections.singletonMap(component.getComponentTypeCode(), component.getHadoopVersion()));
        }

        Cluster cluster = getPluginInfoContext().getCluster();
        if (cluster == null) {
            cluster = clusterService.getClusterByIdOrDefault(component.getClusterId());
            getPluginInfoContext().setCluster(cluster);
        }
    }

    /**
     * 处理 pluginInfo
     *
     * @param isConsole 是否是控制台自身配置
     */
    protected JSONObject doProcessPluginInfo(boolean isConsole) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        // 集群配置信息
        JSONObject clusterConfigJson = clusterService.buildClusterConfig(pluginInfoContext.getCluster(), pluginInfoContext.getDtUicTenantId(),
                pluginInfoContext.getDtUicUserId(), pluginInfoContext.getComponentVersionMap(), true);
        JSONObject pluginInfo = pluginInfoCacheManager.cachePluginInfo(getPluginInfoContext(), needStoreDep(), clusterConfigJson);
        pluginInfo.put("appType",pluginInfoContext.getAppType());

        // 填充自身组件信息
        fillSelfConf(pluginInfo, clusterConfigJson, isConsole);
        // 填充认证信息
        fillSecurityConf(pluginInfo, clusterConfigJson, isConsole);
        // 填充公共配置信息
        fillCommonConf(pluginInfo, clusterConfigJson);
        // 最后填充其他信息
        fillOtherConf(pluginInfo, clusterConfigJson);
        // 填充 typeName
        fillTypeName(pluginInfo);
        if(Objects.equals(AppType.STREAM.getType(), pluginInfoContext.getAppType())){
            JSONObject flinkConf = pluginInfo.getJSONObject("flinkConf");
            if (flinkConf !=null){
                for (Map.Entry<String, Object> entry : flinkConf.entrySet()) {
                    if(entry.getKey().startsWith("stream_")){
                        String streamKey = entry.getKey();
                        String oldKey = streamKey.substring("stream_".length());
                        flinkConf.put(oldKey,entry.getValue());
                    }
                }
            }
        }
        return pluginInfo;
    }

    /**
     * pluginInfo 后置处理
     *
     * @param pluginInfo pluginInfo 信息
     */
    protected void afterProcessPluginInfo(JSONObject pluginInfo) {
        // 去除空值
        MapUtil.removeBlankValue(pluginInfo);
    }

    /**
     * 填充 typeName
     *
     * @param pluginInfo        pluginInfo 信息
     */
    protected void fillTypeName(JSONObject pluginInfo) {
        Component component = getPluginInfoContext().getComponent();
        Cluster cluster = getPluginInfoContext().getCluster();
        // 添加 typeName
        JSONObject selfConfJson = pluginInfo.getJSONObject(getSelfConfName());

        String typeName = StringUtils.isBlank(selfConfJson.getString(TYPE_NAME)) ?
                componentService.convertComponentTypeToClient(cluster.getClusterName(),
                        component.getComponentTypeCode(), component.getVersionName(), component.getStoreType(), component.getDeployType()) : selfConfJson.getString(TYPE_NAME);
        // 最外层添加
        JsonUtil.putIgnoreEmpty(pluginInfo, TYPE_NAME, typeName);
    }

    /**
     * 填充其他配置, 最后再计算 md5 值
     *
     * @param pluginInfo        pluginInfo 信息
     * @param clusterConfigJson 集群配置信息
     */
    protected void fillOtherConf(JSONObject pluginInfo, JSONObject clusterConfigJson) {
        JSONObject otherConf = getOtherConf(pluginInfo, clusterConfigJson);
        pluginInfo.put(PluginInfoConst.OTHER_CONF, otherConf);
        // clone pluginInfo
        JSONObject md5PluginInfo = JSONObject.parseObject(JSONObject.toJSONString(pluginInfo));
        removeMd5Param(md5PluginInfo);
        // 添加 md5 信息
        JsonUtil.putIgnoreEmpty(otherConf, ConfigConstant.MD5_SUM_KEY, PluginInfoUtil.getMd5Sum(md5PluginInfo));
    }

    /**
     * 获取其他配置信息
     *
     * @param pluginInfo        pluginInfo 信息
     * @param clusterConfigJson 集群配置信息
     * @return 其他配置信息
     */
    protected JSONObject getOtherConf(JSONObject pluginInfo, JSONObject clusterConfigJson) {
        JSONObject otherConf = new JSONObject();
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        Component component = pluginInfoContext.getComponent();
        Cluster cluster = pluginInfoContext.getCluster();
        // 添加队列信息
        Queue queue = clusterService.getQueue(pluginInfoContext.getDtUicTenantId(),
                component.getClusterId(), pluginInfoContext.getResourceId());
        JsonUtil.putIgnoreEmpty(otherConf, ConfigConstant.QUEUE, queue == null ? "" : queue.getQueueName());
        JsonUtil.putIgnoreEmpty(otherConf, ConfigConstant.NAMESPACE, queue == null ? "" : queue.getQueueName());
        JsonUtil.putIgnoreEmpty(otherConf, ConfigConstant.CLUSTER, cluster.getClusterName());

        // 设置组件类型
        JsonUtil.putIgnoreEmpty(otherConf, PluginInfoConst.COMPONENT_TYPE, String.valueOf(component.getComponentTypeCode()));
        JsonUtil.putIgnoreEmpty(otherConf, PluginInfoConst.COMPONENT_VERSION, component.getHadoopVersion());

        // otherConf 中设置 deployMode
        JSONObject selfConf = pluginInfo.getJSONObject(EComponentType.getByCode(component.getComponentTypeCode()).getConfName());
        if (MapUtils.isNotEmpty(selfConf) && selfConf.containsKey(ConfigConstant.DEPLOY_MODEL)) {
            JsonUtil.putIgnoreNull(otherConf, ConfigConstant.DEPLOY_MODEL, selfConf.getInteger(ConfigConstant.DEPLOY_MODEL));
        }

        JSONObject hadoopConf = clusterConfigJson.getJSONObject(EComponentType.HDFS.getConfName());
        // md5zip 需要从 hadoopConf 中取
        if (MapUtils.isNotEmpty(clusterConfigJson) && MapUtils.isNotEmpty(hadoopConf) && StringUtils.isNotBlank(hadoopConf.getString(ConfigConstant.MD5_ZIP_KEY))) {
            JsonUtil.putIgnoreEmpty(otherConf, ConfigConstant.MD5_ZIP_KEY, hadoopConf.getString(ConfigConstant.MD5_ZIP_KEY));
            hadoopConf.remove(ConfigConstant.MD5_ZIP_KEY);
        }
        return otherConf;
    }

    /**
     * 填充认证相关信息
     *
     * @param clusterConfigJson 所有组件配置
     * @param pluginInfo        pluginInfo
     * @param isConsole         是否只填充控制台配置
     */
    private void fillSecurityConf(JSONObject pluginInfo, JSONObject clusterConfigJson, boolean isConsole) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        Component selfComponent = pluginInfoContext.getComponent();
        EComponentType componentType = EComponentType.getByCode(selfComponent.getComponentTypeCode());

        JSONObject securityConf = new JSONObject();
        // proxy 暂时注释
        // setProxyUserName(pluginJson, dtUicTenantId, clusterId);
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.KERBEROS_CONF, getKerberosConf(clusterConfigJson, isConsole));
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.SSL_CONF, getSslConf());
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.TBDS_CONF, getTbdsConf());
        // ldap conf
        JSONObject ldapConf = clusterService.getLdapConf(selfComponent.getComponentTypeCode(), pluginInfoContext.getDtUicUserId(), pluginInfoContext.getDtUicTenantId(), selfComponent.getClusterId(), pluginInfo.getJSONObject(getSelfConfName()));
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.LDAP_CONF, ldapConf);
        // proxy conf, 取 ldap conf 中的 dtProxyUserName, 取 commonConf 中的 hadoop.proxy.enable
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.PROXY_CONF, pluginInfoService.getProxyConf(ldapConf, clusterConfigJson));
        if (needStoreDep()) {
            JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.STORE_KERBEROS_CONF, getKerberosConfWithoutVersion(selfComponent.getStoreType()));
        }
        // securityConf 这一层必传
        pluginInfo.put(PluginInfoConst.SECURITY_CONF, securityConf);
    }

    /**
     * 剔除计算 md5 不需要的参数
     *
     * @param md5PluginInfo 原始 json
     */
    private void removeMd5Param(JSONObject md5PluginInfo) {
        MD5_UNWANTED_PATH.forEach(path -> JSONPath.remove(md5PluginInfo, path));
    }

    /**
     * 填充公共配置信息
     *
     * @param pluginInfo        pluginInfo 信息
     * @param clusterConfigJson 所有组件配置
     */
    protected void fillCommonConf(JSONObject pluginInfo, JSONObject clusterConfigJson) {
        // 提取 commonConf
        JSONObject commonConfJson = MapUtils.isNotEmpty(clusterConfigJson.getJSONObject(PluginInfoConst.COMMON_CONF)) ?
                clusterConfigJson.getJSONObject(PluginInfoConst.COMMON_CONF) : new JSONObject();
        // 填充参数
        JSONObject selfConf = pluginInfo.getJSONObject(getSelfConfName());
        if (MapUtils.isNotEmpty(selfConf)) {
            // 计算组件 hadoop.user.name > 计算组件 hadoopUserName > common 组件的 hadoop.user.name
            JsonUtil.putIgnoreEmpty(commonConfJson, PluginInfoConst.HADOOP_USER_NAME_DOT, selfConf.getString(PluginInfoConst.HADOOP_USER_NAME));
            JsonUtil.putIgnoreEmpty(commonConfJson, PluginInfoConst.HADOOP_USER_NAME_DOT, selfConf.getString(PluginInfoConst.HADOOP_USER_NAME_DOT));
            // 兼容性处理
            JsonUtil.putIgnoreEmpty(commonConfJson, PluginInfoConst.HADOOP_USER_NAME, commonConfJson.getString(PluginInfoConst.HADOOP_USER_NAME_DOT));
        }

        String zkUrl = environmentContext.getNodeZkAddress().split("/")[0];
        JsonUtil.putIgnoreEmpty(commonConfJson, PluginInfoConst.ENGINE_ZOOKEEPER_URL, zkUrl);
        pluginInfo.put(PluginInfoConst.COMMON_CONF, commonConfJson);
    }

    /**
     * 填充自身配置信息
     *
     * @param pluginInfo        pluginInfo 配置
     * @param clusterConfigJson 集群全部配置信息
     * @param isConsole         是否只填充控制台配置
     */
    private void fillSelfConf(JSONObject pluginInfo, JSONObject clusterConfigJson, boolean isConsole) {
        // component 信息
        Component component = getPluginInfoContext().getComponent();
        // 1. 获取当前组件信息
        JSONObject selfConfig = getSelfConfig(clusterConfigJson, isConsole);
        // 2. 填充组件自身扩展配置
        Map<String, Object> selfExtraParam = getPluginInfoContext().getSelfExtraParam();
        if (MapUtils.isNotEmpty(selfExtraParam)) {
            selfConfig.putAll(selfExtraParam);
        }
        // 3. 租户 id 不为空且 isConsole 为 false 则替换租户级别的组件配置,
        if (Objects.nonNull(getPluginInfoContext().getDtUicTenantId()) && !isConsole) {
            clusterService.setTenantComponentConfig(EComponentType.getByCode(component.getComponentTypeCode()),
                    getPluginInfoContext().getDtUicTenantId(), component.getClusterId(), selfConfig);
        }
        // 4. 后置处理 selfConfig
        afterProcessSelfConfig(selfConfig);
        JsonUtil.putIgnoreEmpty(pluginInfo, getSelfConfName(), selfConfig);
    }

    protected String getSelfConfName() {
        return PluginInfoConst.SELF_CONF;
    }

    /**
     * 获取自身配置信息
     *
     * @param clusterConfigJson 集群所有组件配置信息
     * @return 自身配置信息
     */
    protected JSONObject getSelfConfig(JSONObject clusterConfigJson, boolean isConsole) {
        if (isConsole) {
            return getSelfConfigWithConsole(clusterConfigJson);
        }
        return getSelfConfigWithTask(clusterConfigJson);
    }

    protected JSONObject getSelfConfigWithConsole(JSONObject clusterConfigJson) {
        // 默认返回
        return clusterConfigJson.getJSONObject(EComponentType.getByCode(getPluginInfoContext().getComponent().getComponentTypeCode()).getConfName());
    }

    protected JSONObject getSelfConfigWithTask(JSONObject clusterConfigJson) {
        // 默认返回
        return clusterConfigJson.getJSONObject(EComponentType.getByCode(getPluginInfoContext().getComponent().getComponentTypeCode()).getConfName());
    }

    /**
     * 自身组件配置信息后置处理
     *
     * @param selfConfig 自身配置信息
     */
    protected void afterProcessSelfConfig(JSONObject selfConfig) {
    }

    /**
     * 是否需要存储组件
     *
     * @return true | false
     */
    protected boolean needStoreDep() {
        return true;
    }

    /**
     * 获取任务中的 paramsJson
     *
     * @param selfConf 自身配置信息
     * @return 任务 paramsJson
     */
    protected JSONObject getParamsJson(JSONObject selfConf) {
        Map<String, Object> actionParam = getPluginInfoContext().getActionParam();
        if (MapUtils.isEmpty(selfConf) || MapUtils.isEmpty(actionParam)) {
            return new JSONObject();
        }
        String dbUrl = selfConf.getString(ConfigConstant.JDBCURL);
        if (StringUtils.isEmpty(dbUrl)) {
            return new JSONObject();
        }

        String jobId = (String) actionParam.get("taskId");
        Integer appType = MapUtils.getInteger(actionParam, "appType");
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (Objects.isNull(scheduleJob) || Objects.isNull(appType)) {
            LOGGER.info("dbUrl {} jobId {} appType or scheduleJob is null", dbUrl, jobId);
            return new JSONObject();
        }
        JSONObject info = JSONObject.parseObject(scheduleTaskShadeDao.getExtInfoByTaskId(scheduleJob.getTaskId(), appType));
        if (MapUtils.isEmpty(info)) {
            return new JSONObject();
        }
        return info.getJSONObject("info").getJSONObject("jdbcUrlParams");
    }

    /**
     * 获取 params str
     *
     * @param paramsJson paramsJson
     * @param jdbcUrl    jdbc url
     * @return params str
     */
    protected String getParamsStr(JSONObject paramsJson, String jdbcUrl) {
        if (MapUtils.isEmpty(paramsJson)) {
            return StringUtils.EMPTY;
        }
        if (jdbcUrl.contains(URI_PARAMS_DELIM)) {
            String paramsStr = jdbcUrl.split("\\?")[1];
            for (String param : paramsStr.split(ConfigConstant.PARAMS_DELIM)) {
                String[] paramsSplit = param.split("=");
                if (!paramsJson.containsKey(paramsSplit[0])) {
                    paramsJson.put(paramsSplit[0], paramsSplit[1]);
                }
            }
        }
        List<String> paramsList = Lists.newArrayList();
        paramsJson.forEach((key, value) -> paramsList.add(String.format("%s=%s", key, value)));
        return StringUtils.join(paramsList, ConfigConstant.PARAMS_DELIM);
    }

    /**
     * 处理 url 参数, 拼接需要的参数
     *
     * @param selfConf 自身配置
     */
    protected void dealUrlParam(JSONObject selfConf) {
        String jdbcUrl = selfConf.getString(ConfigConstant.JDBCURL);
        JSONObject paramsJson = getParamsJson(selfConf);
        if (MapUtils.isEmpty(paramsJson)) {
            return;
        }
        String paramsStr = getParamsStr(paramsJson, jdbcUrl);
        if (StringUtils.isBlank(paramsStr)) {
            return;
        }
        if (jdbcUrl.contains(URI_PARAMS_DELIM)) {
            jdbcUrl = jdbcUrl.split("\\?")[0];
        }
        jdbcUrl = jdbcUrl + URI_PARAMS_DELIM + paramsStr;
        selfConf.put("jdbcUrl", jdbcUrl);
    }

    private void setProxyUserName(JSONObject pluginJson, Long dtUicTenantId, Long clusterId) {
        // 集群开启了数据安全
        boolean hasRangerAndLdap = clusterService.hasRangerAndLdap(clusterId);
        if (!hasRangerAndLdap) {
            return;
        }
        JSONObject hadoopConfig = pluginJson.getJSONObject(EComponentType.HDFS.getConfName());
        if (hadoopConfig == null) {
            return;
        }
        setProxyUserName(hadoopConfig, dtUicTenantId);
    }

    public void setProxyUserName(JSONObject info, Long dtUicUserId) {
        try {
            ApiResponse<UICSimpleUserVo> userInfo = uicUserApiClient.getUserInfo(environmentContext.getSdkToken(), dtUicUserId);
            if (userInfo.getData() != null) {
                UICSimpleUserVo uicSimpleUserVo = userInfo.getData();
                if (uicSimpleUserVo.isLdapUser()) {
                    String dtProxyUserName = uicSimpleUserVo.getUserName();
                    info.put(LDAP_USER_NAME, dtProxyUserName);
                    LOGGER.info("setRealLdapUserName ok, dtUicUserId:{}, dtProxyUserName:{}", dtUicUserId, dtProxyUserName);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("setRealLdapUserName error, dtUicUserId:{}, getUserInfo error:{}", dtUicUserId, e.getMessage(), e);
        }
    }

    /**
     * 获取 tbds 配置
     *
     * @return tbds 配置
     */
    private JSONObject getTbdsConf() {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        Component component = pluginInfoContext.getComponent();
        JSONObject tdbsConfig = new JSONObject();
        List<Component> components = componentService.listByClusterId(component.getClusterId(), EComponentType.YARN.getTypeCode(), true);
        Optional<Component> tbds = Optional.empty();
        if (CollectionUtils.isNotEmpty(components)) {
            tbds = components.stream()
                    .filter(config -> StringUtils.isNotBlank(config.getVersionName()) && config.getVersionName().startsWith(TBDS))
                    .findFirst();
        }
        if (tbds.isPresent()) {
            ApiResponse<TenantApiVO> tenantInfo = uicTenantApiClient.getTenantByTenantId(String.valueOf(pluginInfoContext.getDtUicTenantId()), environmentContext.getSdkToken());
            TenantApiVO data = tenantInfo.getData();
            if (null != data) {
                JsonUtil.putIgnoreEmpty(tdbsConfig, SourceConstant.TBDS_ID, data.getTbdsSecureId());
                JsonUtil.putIgnoreEmpty(tdbsConfig, SourceConstant.TBDS_KEY, data.getTbdsSecureKey());
                JsonUtil.putIgnoreEmpty(tdbsConfig, SourceConstant.TBDS_USERNAME, data.getTbdsUsername());
            }
        }
        return tdbsConfig;
    }

    /**
     * 获取不区分版本组件的 kerberos 配置如存储组件
     *
     * @param componentType 组件类型
     * @return kerberos conf
     */
    private JSONObject getKerberosConfWithoutVersion(Integer componentType) {
        Component component = getPluginInfoContext().getComponent();
        KerberosConfig authentication = kerberosDao.getByComponentType(component.getClusterId(),
                componentType, null);
        return DtKerberosUtil.parse2Json(authentication);
    }

    /**
     * 获取 kerberos 配置 json, 非自身配置 clusterConfigJson 不能为空
     *
     * @param clusterConfigJson 集群配置信息
     * @param isConsole         是否只获取控制台自身 kerberos 配置
     * @return kerberos json
     */
    private JSONObject getKerberosConf(JSONObject clusterConfigJson, boolean isConsole) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        Component component = pluginInfoContext.getComponent();
        String formatVersion = ComponentService.formatVersion(component.getComponentTypeCode(), component.getHadoopVersion());
        if (isConsole) {
            return findConsoleKerberos(component);
        }
        if (MapUtils.isEmpty(clusterConfigJson)) {
            throw new DtCenterDefException("clusterConfigJson can't be empty.");
        }
        JSONObject sftpConf = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        KerberosReq kerberosReq = KerberosReq.builder()
                .clusterId(component.getClusterId())
                .componentCode(component.getComponentTypeCode())
                .componentVersion(formatVersion)
                .taskType(pluginInfoContext.getTaskType())
                .projectId(pluginInfoContext.getProjectId())
                .appType(pluginInfoContext.getAppType())
                .userId(pluginInfoContext.getDtUicUserId())
                .tenantId(pluginInfoContext.getDtUicTenantId())
                .sftpConfig(sftpConf)
                .build();
        if (skipKerberosReplace(pluginInfoContext)) {
            return findConsoleKerberos(component);
        }
        Pair<IKerberosAuthentication, KerberosConfig> authentication = defaultKerberosChooser.authentication(kerberosReq);
        return DtKerberosUtil.parse2Json(authentication.getValue());
    }

    private boolean skipKerberosReplace(PluginInfoContext pluginInfoContext) {
        Integer appType = pluginInfoContext.getAppType();
        Integer taskType = pluginInfoContext.getTaskType();
        Integer deployMode = pluginInfoContext.getDeployMode();
        // 离线数据同步任务非 perjob 模式，仍然用控制台的 kerberos
        boolean isRdosSyncNoPerjob = AppType.RDOS.getType().equals(appType)
            && EScheduleJobType.SYNC.getVal().equals(taskType)
            && !EDeployMode.PERJOB.getType().equals(deployMode);
        return isRdosSyncNoPerjob;
    }

    private JSONObject findConsoleKerberos(Component component) {
        String formatVersion = ComponentService.formatVersion(component.getComponentTypeCode(), component.getHadoopVersion());
        KerberosConfig authentication = kerberosDao.getByComponentType(component.getClusterId(),
                component.getComponentTypeCode(), formatVersion);
        return DtKerberosUtil.parse2Json(authentication);
    }

    /**
     * 获取组件的 ssl 配置信息
     * 1. Trino 只查询当前 trino 组件的 ssl 配置
     * 2. 其他组件优先查询自身组件 -> yarn ssl -> hdfs ssl
     *
     * @return ssl conf
     */
    private JSONObject getSslConf() {
        Component component = getPluginInfoContext().getComponent();
        String formatVersion = ComponentService.formatVersion(component.getComponentTypeCode(), component.getHadoopVersion());
        return clusterService.getSslConfig(component.getClusterId(), EComponentType.getByCode(component.getComponentTypeCode()), formatVersion);
    }
}
