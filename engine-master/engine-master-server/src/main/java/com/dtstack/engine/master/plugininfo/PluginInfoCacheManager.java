package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.master.enums.AuxiliaryTypeEnum;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.dto.ComponentProxyConfigDTO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentAuxiliaryService;
import com.dtstack.engine.master.impl.ComponentConfigService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-20 09:17
 */
@Component
public class PluginInfoCacheManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentConfigService.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;

    @Resource
    protected ComponentDao componentDao;

    @Autowired
    protected ComponentAuxiliaryService componentAuxiliaryService;

    @Autowired
    private YarnPluginInfoAdapter yarnPluginInfoAdapter;

    /**
     * 获取多版本集群信息
     *
     * @param cluster
     * @param multiVersion
     * @return
     */
    @Cacheable(cacheNames = "pluginInfo", key = "#cluster.id + ':' + #multiVersion", cacheManager = "engineCacheManager")
    public ClusterVO cacheCluster(Cluster cluster, boolean multiVersion) {
        return clusterService.getCluster4PluginInfo(cluster, multiVersion);
    }

    /**
     * 拼接集群信息
     *
     * @param cluster
     * @return
     */
    @Cacheable(cacheNames = "pluginInfo", key = "#cluster.id", cacheManager = "engineCacheManager")
    public JSONObject buildClusterConfig(ClusterVO cluster) {
        return clusterService.buildClusterConfig(cluster, null);
    }

    /**
     * 拼接插件信息
     *
     * @param pluginInfoContext
     * @param needStoreDep
     * @param clusterConfigJson
     * @return
     */
    @Cacheable(cacheNames = "pluginInfo",
            key = "#pluginInfoContext.getCluster().id + ':' + #pluginInfoContext.getComponent().componentTypeCode + ':' + #pluginInfoContext.getComponentVersionMap() + ':' + #pluginInfoContext.getDeployMode() + ':' + #needStoreDep",
            cacheManager = "engineCacheManager")
    public JSONObject cachePluginInfo(PluginInfoContext pluginInfoContext, boolean needStoreDep, JSONObject clusterConfigJson) {
        JSONObject pluginInfo = new JSONObject();
        // 填充依赖组件配置信息
        fillDependencyConf(pluginInfoContext, pluginInfo, clusterConfigJson, needStoreDep);
        // classloader 隔离前缀 key
        pluginInfo.put(ConfigConstant.CLUSTER, pluginInfoContext.getCluster().getClusterName());
        return pluginInfo;
    }

    @CacheEvict(cacheNames = "pluginInfo", allEntries = true)
    public void clearPluginInfoCache() {
        LOGGER.info("clear all pluginInfo cache");
    }

    @CacheEvict(cacheNames = "pluginInfo", key = "#clusterId + '*'", cacheManager = "engineCacheManager")
    public void clearPluginInfoClusterCache(Long clusterId) {
        LOGGER.info("clear all clusterId:{} pluginInfo cache", clusterId);
    }

    /**
     * 填充依赖组件配置如 sftp、存储组件等
     *
     * @param pluginInfoContext
     * @param pluginInfo        pluginInfo 信息
     * @param clusterConfigJson 集群配置信息
     * @param needStoreDep
     */
    private void fillDependencyConf(PluginInfoContext pluginInfoContext, JSONObject pluginInfo, JSONObject clusterConfigJson, boolean needStoreDep) {
        com.dtstack.engine.api.domain.Component component = pluginInfoContext.getComponent();
        // 填充 sftp 配置
        JsonUtil.putIgnoreEmpty(pluginInfo, EComponentType.SFTP.getConfName(), clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName()));
        // 填充存储组件配置
        if (needStoreDep) {
            // 存储组件不区分多版本
            JSONObject storeComponentJson = componentService.getComponentByClusterId(component.getClusterId(), component.getStoreType(), true, JSONObject.class, null);
            // 填充存储组件
            JsonUtil.putIgnoreEmpty(pluginInfo, EComponentType.getByCode(component.getStoreType()).getConfName(), storeComponentJson);
            // 处理调度组件
            boolean isKubernetes = MapUtils.isNotEmpty(clusterConfigJson.getJSONObject(EComponentType.KUBERNETES.getConfName()));
            if (isKubernetes) {
                fillKubernetesConfig(pluginInfoContext, pluginInfo, clusterConfigJson);
            } else {
                // 调度组件不区分多版本
                JSONObject scheduleComponentJson = componentService.getComponentByClusterId(component.getClusterId(), EComponentType.YARN.getTypeCode(), true, JSONObject.class, null);
                yarnPluginInfoAdapter.setPluginInfoContext(pluginInfoContext);
                yarnPluginInfoAdapter.appendKnoxConfig(scheduleComponentJson);
                // 填充调度组件
                JsonUtil.putIgnoreEmpty(pluginInfo, EComponentType.YARN.getConfName(), scheduleComponentJson);
            }
        }
    }

    /**
     * 填充 k8s 配置信息
     *
     * @param pluginInfoContext
     * @param pluginInfo        pluginInfo 信息
     * @param clusterConfigJson 集群配置信息
     */
    private void fillKubernetesConfig(PluginInfoContext pluginInfoContext, JSONObject pluginInfo, JSONObject clusterConfigJson) {
        com.dtstack.engine.api.domain.Component component = pluginInfoContext.getComponent();
        com.dtstack.engine.api.domain.Component kubernetes = componentDao.getByClusterIdAndComponentType(component.getClusterId(), EComponentType.KUBERNETES.getTypeCode(), ComponentVersionUtil.getComponentVersion(pluginInfoContext.getComponentVersionMap(), EComponentType.KUBERNETES), null);
        if (Objects.nonNull(kubernetes)) {
            // 添加 kubernetes context
            JSONObject kubernetesConf = new JSONObject();
            JSONObject componentConfig = componentService.getComponentByClusterId(kubernetes.getId(), false, JSONObject.class);
            if (Objects.nonNull(componentConfig)) {
                kubernetesConf.put(GlobalConst.KUBERNETES_CONTEXT, componentConfig.getString(GlobalConst.KUBERNETES_CONTEXT));
            }
            // 添加 logTrace 配置
            ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(component.getClusterId(), EComponentType.KUBERNETES.getTypeCode(), AuxiliaryTypeEnum.LOG_TRACE.name());
            ComponentProxyConfigDTO logTraceConfig = componentAuxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
            if (Objects.nonNull(logTraceConfig)) {
                JSONObject logTrace = new JSONObject(logTraceConfig.getConfig());
                // 如果使用 hdfs 需要填充 hadoopConf、kerberosConf、sftpConf
                if (StringUtils.equalsIgnoreCase(logTrace.getString("traceType"), "HDFS")) {
                    logTrace.put(EComponentType.HDFS.getConfName(), clusterConfigJson.getJSONObject(EComponentType.HDFS.getConfName()));
                }
                kubernetesConf.putIfAbsent(GlobalConst.LOG_TRACE, logTrace);
            }
            JsonUtil.putIgnoreEmpty(kubernetesConf, "kubernetesConfigName", kubernetes.getUploadFileName());
            JSONObject sftpConf = clusterConfigJson.getJSONObject("sftpConf");
            if (Objects.nonNull(sftpConf)) {
                String path = sftpConf.getString("path") + File.separator + componentService.buildSftpPath(component.getClusterId(), EComponentType.KUBERNETES.getTypeCode(),null);
                JsonUtil.putIgnoreEmpty(kubernetesConf, "remoteDir", path);
            }
            JsonUtil.putIgnoreEmpty(pluginInfo, EComponentType.KUBERNETES.getConfName(), kubernetesConf);
        }
    }
}
