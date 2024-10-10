package com.dtstack.engine.master.plugininfo.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.engine.SourceConstant;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.constrant.UicConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.security.DtKerberosUtil;
import com.dtstack.engine.master.security.DtKerberosParam;
import com.dtstack.engine.master.security.chooser.DefaultKerberosChooser;
import com.dtstack.engine.master.security.kerberos.ClusterKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.KerberosReq;
import com.dtstack.engine.master.security.chooser.DsKerberosChooser;
import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

import static com.dtstack.dtcenter.common.convert.Consistent.KERBEROS_PATH_KEY;
import static com.dtstack.dtcenter.common.convert.Consistent.KERBEROS_REMOTE_PATH;
import static com.dtstack.dtcenter.common.convert.Consistent.PATH;
import static com.dtstack.dtcenter.common.convert.Consistent.SEPARATOR;
import static com.dtstack.dtcenter.common.convert.Consistent.SFTP_CONF;
import static com.dtstack.engine.common.constrant.ConfigConstant.LDAP_USER_NAME;
import static com.dtstack.engine.common.constrant.ConfigConstant.USERNAME;

/**
 * pluginInfo service
 *
 * @author ：wangchuan
 * date：Created in 14:29 2022/11/18
 * company: www.dtstack.com
 */
@Service
public class PluginInfoService {

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private ClusterService clusterService;

    @Resource
    private DsKerberosChooser dsKerberosChooser;

    @Resource
    private DefaultKerberosChooser defaultKerberosChooser;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInfoService.class);

    /**
     * 转换数据源中心的 dataJson 为调度的 pluginInfo 结构
     *
     * @param datasourceId 数据源中心的 datasource id
     * @param schema       需要设置的 schema 信息
     */
    public JSONObject convertDsPluginInfo(Long datasourceId, String schema, DtKerberosParam dtKerberosParam) {
        if (Objects.isNull(datasourceId)) {
            LOGGER.warn("datasourceId is null: {}", datasourceId);
            return null;
        }
        ApiResponse<DsServiceInfoDTO> ds = dataSourceAPIClient.getDsInfoById(datasourceId);
        if (ds == null || ds.getData() == null || StringUtils.isEmpty(ds.getData().getDataJson())) {
            LOGGER.error("can't find dataSource's dataJson by publicService, datasourceId: {}", datasourceId);
            return null;
        }
        return convertDsPluginInfo(ds.getData(), schema, dtKerberosParam);
    }

    /**
     * 转换数据源中心的 dataJson 为调度的 pluginInfo 结构
     *
     * @param dsData 数据源中心的数据源信息
     * @param schema 需要设置的 schema 信息
     */
    public JSONObject convertDsPluginInfo(DsServiceInfoDTO dsData, String schema, DtKerberosParam dtKerberosParam) {
        // 处理 pluginInfo
        JSONObject pluginInfo = new JSONObject();
        pluginInfo.put(SourceConstant.DATASOURCE_TYPE_KEY, dsData.getType());
        // 从数据源中心获取 jdbc 连接信息
        JSONObject dataJson = JSONObject.parseObject(dsData.getDataJson());

        // 设置传入的 schema
        JsonUtil.putIgnoreEmpty(pluginInfo, SourceConstant.SCHEMA, schema);

        // 设置 sftp
        JSONObject sftpConf = getSftpConf(dsData.getDtuicTenantId());
        pluginInfo.put(EComponentType.SFTP.getConfName(), sftpConf);

        // 设置 hadoopConf
        JSONObject hadoopConf = parseHadoopConf(pluginInfo);
        JsonUtil.putIgnoreEmpty(pluginInfo, EComponentType.HDFS.getConfName(), hadoopConf);

        // 设置 securityConf
        JSONObject securityConf = new JSONObject();
        pluginInfo.put(PluginInfoConst.SECURITY_CONF, securityConf);

        // kerberos
        JSONObject kerberosConf = null;
        Integer isMeta = dsData.getIsMeta();
        boolean shouldChooseKerberos = (GlobalConst.YES.equals(isMeta) && dtKerberosParam != null
            && DataSourceType.needReplaceKerbersDataSource.contains(dsData.getType()));
        if (shouldChooseKerberos) {
            KerberosReq realKerberosReq = KerberosReq.builder()
                    .sftpConfig(sftpConf)
                    .dataJson(dataJson)
                    .taskType(dtKerberosParam.getTaskType())
                    .projectId(dtKerberosParam.getProjectId())
                    .appType(dtKerberosParam.getAppType())
                    .build();
            Pair<IKerberosAuthentication, KerberosConfig> kerberosConfigPair = dsKerberosChooser.authentication(realKerberosReq);
            KerberosConfig kerberosConfig = kerberosConfigPair.getValue();
            kerberosConf = DtKerberosUtil.parse2Json(kerberosConfig);
            JsonUtil.putIgnoreEmpty(kerberosConf, SFTP_CONF, sftpConf);
            dataJson.remove(GlobalConst.KERBEROS_CONFIG);
        } else {
            // 原始逻辑
            kerberosConf = parseKerberosConf(dataJson, sftpConf);
        }
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.KERBEROS_CONF, kerberosConf);

        // ssl
        JSONObject sslConf = parseSslConf(dataJson);
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.SSL_CONF, sslConf);

        // selfConf
        pluginInfo.put(PluginInfoConst.SELF_CONF, dataJson);

        // commonConf
        JSONObject commonConf = parseCommonConf(dataJson);
        JsonUtil.putIgnoreEmpty(pluginInfo, PluginInfoConst.COMMON_CONF, commonConf);

        return pluginInfo;
    }

    private JSONObject parseHadoopConf(JSONObject pluginInfo) {

        JSONObject hadoopConfig = pluginInfo.getJSONObject(GlobalConst.HADOOP_CONFIG);
        if (MapUtils.isEmpty(hadoopConfig)) {
            return null;
        }
        pluginInfo.remove(GlobalConst.HADOOP_CONFIG);
        return hadoopConfig;
    }

    private JSONObject parseSslConf(JSONObject pluginInfo) {
        JSONObject sslConfig = pluginInfo.getJSONObject(GlobalConst.SSL_CONFIG);
        if (MapUtils.isEmpty(sslConfig)) {
            return null;
        }
        pluginInfo.remove(GlobalConst.SSL_CONFIG);

        // keyPath --> remoteSSLDir
        String keyPath = sslConfig.getString(UicConstant.KEY_PATH);
        if (StringUtils.isNotEmpty(keyPath)) {
            sslConfig.putIfAbsent(SourceConstant.REMOTE_SSL_DIR, keyPath);
        }
        return sslConfig;
    }

    public JSONObject parseCommonConf(JSONObject pluginInfo) {
        JSONObject commonConf = pluginInfo.getJSONObject(PluginInfoConst.COMMON_CONF);
        if (MapUtils.isEmpty(commonConf)) {
            return null;
        }
        pluginInfo.remove(PluginInfoConst.COMMON_CONF);
        return commonConf;
    }

    public void fillProxyConf(JSONObject pluginInfo, DsServiceInfoDTO dsInfo, EngineTypeComponentType type, Long userId) {
        JSONObject securityConf = Objects.isNull(pluginInfo.getJSONObject(PluginInfoConst.SECURITY_CONF)) ?
                new JSONObject() : pluginInfo.getJSONObject(PluginInfoConst.SECURITY_CONF);
        Long clusterId = clusterService.getClusterId(dsInfo.getDtuicTenantId());
        JSONObject ldapConf = clusterService.getLdapConf(type.getComponentType().getTypeCode(), userId, dsInfo.getDtuicTenantId(), clusterId, pluginInfo.getJSONObject(PluginInfoConst.SELF_CONF));
        securityConf.put(PluginInfoConst.LDAP_CONF, ldapConf);
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.PROXY_CONF, getProxyConf(ldapConf, JSONObject.parseObject(dsInfo.getDataJson())));
        pluginInfo.put(PluginInfoConst.SECURITY_CONF, securityConf);
    }

    /**
     * 获取 proxyConf
     *
     * @param ldapConf   ldap conf
     * @param pluginInfo pluginInfo
     * @return proxyConf
     */
    public JSONObject getProxyConf(JSONObject ldapConf, JSONObject pluginInfo) {
        JSONObject proxyConf = new JSONObject();
        if (MapUtils.isNotEmpty(ldapConf) && StringUtils.isNotBlank(ldapConf.getString(USERNAME))) {
            proxyConf.put(LDAP_USER_NAME, ldapConf.getString(USERNAME));
        }

        JSONObject commonConf = pluginInfo.getJSONObject(PluginInfoConst.COMMON_CONF);
        if (MapUtils.isNotEmpty(commonConf) && Objects.nonNull(commonConf.get(PluginInfoConst.HADOOP_PROXY_ENABLE))
                && StringUtils.isNotBlank(proxyConf.getString(LDAP_USER_NAME))) {
            proxyConf.put(PluginInfoConst.HADOOP_PROXY_ENABLE, commonConf.get(PluginInfoConst.HADOOP_PROXY_ENABLE));
        }
        return proxyConf;
    }

    private JSONObject getSftpConf(Long dtUicTenantId) {
        String sftpConfStr = clusterService.getConfigByKey(dtUicTenantId, EComponentType.SFTP.getConfName(), false, null, false);
        if (StringUtils.isBlank(sftpConfStr)) {
            return null;
        }
        return JSONObject.parseObject(sftpConfStr);
    }

    /**
     * 设置 kerberos 信息
     *
     * @param dataJson 数据源中心 dataJson 信息
     * @param sftpConf sftp 配置
     */
    private JSONObject parseKerberosConf(JSONObject dataJson, JSONObject sftpConf) {
        JSONObject dataJsonKerberosConfig = dataJson.getJSONObject(GlobalConst.KERBEROS_CONFIG);
        if (MapUtils.isEmpty(dataJsonKerberosConfig)) {
            return null;
        }
        String kerberosDir = dataJsonKerberosConfig.getString(KERBEROS_PATH_KEY);
        if (StringUtils.isEmpty(kerberosDir)) {
            return null;
        }
        JSONObject kerberosConf = new JSONObject(dataJsonKerberosConfig);
        if (sftpConf != null) {
            kerberosConf.put(SFTP_CONF, sftpConf);
            kerberosConf.put(KERBEROS_REMOTE_PATH, sftpConf.getString(PATH) + SEPARATOR + kerberosDir);
            if (!kerberosConf.containsKey(SourceConstant.OPEN_KERBEROS)) {
                kerberosConf.put(SourceConstant.OPEN_KERBEROS, 1);
            }
        }
        // 剔除 dataJson 中的
        dataJson.remove(GlobalConst.KERBEROS_CONFIG);
        return kerberosConf;
    }

    public JSONObject buildJdbcInfo(Component component, Cluster cluster,
                                    Integer isMeta, Integer sourceType, String schema,
                                    Long dtuicTenantId, KerberosReq kerberosReq) {
        JSONObject pluginInfo = pluginInfoManager.buildConsolePluginInfo(component, cluster, null, null, null, dtuicTenantId, null);
        pluginInfo.put(SourceConstant.DATASOURCE_TYPE_KEY, sourceType);
        pluginInfo.put(SourceConstant.SCHEMA, schema);

        JSONObject securityConf = Optional.ofNullable(pluginInfo.getJSONObject(PluginInfoConst.SECURITY_CONF))
                .orElse(new JSONObject());
        // 判断是否需要替换 kerberos
        JSONObject kerberosConf = null;
        KerberosConfig kerberosConfig = null;
        Integer judgeIsMeta = (isMeta != null ? isMeta : component.getIsMetadata());
        boolean shouldReplaceKerberos = (GlobalConst.YES.equals(judgeIsMeta) && kerberosReq != null
                && DataSourceType.needReplaceKerbersDataSource.contains(sourceType));
        if (!shouldReplaceKerberos) {
            return pluginInfo;
        }

        Pair<IKerberosAuthentication, KerberosConfig> kerberosConfigPair = defaultKerberosChooser.authentication(kerberosReq);
        IKerberosAuthentication authenticationImpl = kerberosConfigPair.getKey();
        if (authenticationImpl == null) {
            return pluginInfo;
        }
        // 结果仍然是集群级 kerberos，说明没有发生变化，无须替换
        if (ClusterKerberosAuthentication.CLUSTER.equals(authenticationImpl.name())) {
            return pluginInfo;
        }

        kerberosConfig = kerberosConfigPair.getValue();
        kerberosConf = DtKerberosUtil.parse2Json(kerberosConfig);
        securityConf.put(PluginInfoConst.KERBEROS_CONF, kerberosConf);
        pluginInfo.put(PluginInfoConst.SECURITY_CONF, securityConf);
        return pluginInfo;
    }
}
