package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.plugininfo.proxy.PluginInfoAdapterProxyHandle;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEPLOY_MODEL;

/**
 * pluginInfo adapter factory
 *
 * @author ：wangchuan
 * date：Created in 15:37 2022/10/10
 * company: www.dtstack.com
 */
@Component
@Order(1)
public class PluginInfoManager implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInfoManager.class);

    /**
     * pluginInfo adapter 缓存
     */
    public static final Map<EComponentType, IPluginInfoAdapter> PLUGIN_INFO_ADAPTER_MAP = Maps.newHashMap();

    /**
     * default pluginInfo adapter 缓存
     */
    public static IPluginInfoAdapter DEFAULT_PLUGIN_INFO_ADAPTER = null;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    public static final String COMPONENT_VERSION_VALUE = "componentVersionValue";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("pluginInfo manager init start...");
        Map<String, IPluginInfoAdapter> beansOfType = applicationContext.getBeansOfType(IPluginInfoAdapter.class);
        beansOfType.forEach((name, adapter) -> {
            if (Objects.isNull(adapter.getEComponentType())) {
                // 默认 adapter
                if (Objects.nonNull(DEFAULT_PLUGIN_INFO_ADAPTER)) {
                    throw new DtCenterDefException("default pluginInfoAdapter duplicate");
                }
                DEFAULT_PLUGIN_INFO_ADAPTER = createProxyAdapter(adapter);
            } else {
                PLUGIN_INFO_ADAPTER_MAP
                        .put(adapter.getEComponentType(), createProxyAdapter(adapter));
            }
        });
        if (Objects.isNull(DEFAULT_PLUGIN_INFO_ADAPTER)) {
            throw new DtCenterDefException("default pluginInfoAdapter is null.");
        }
        LOGGER.info("pluginInfo manager init end...");
    }

    public JSONObject buildTaskPluginInfo(Long projectId,
                                          Integer appType,
                                          Integer taskType,
                                          Long dtUicTenantId,
                                          String engineTypeStr,
                                          Long dtUicUserId,
                                          Integer deployMode,
                                          Long resourceId,
                                          Map<Integer, String> componentVersionMap) {
        return buildTaskPluginInfo(
                projectId,
                appType,
                taskType,
                deployMode,
                engineTypeStr,
                dtUicTenantId,
                dtUicUserId,
                resourceId,
                null,
                componentVersionMap,
                null);
    }

    public JSONObject buildTaskPluginInfo(Long projectId,
                                          Integer appType,
                                          Integer taskType,
                                          String taskParams,
                                          String engineType,
                                          Long tenantId,
                                          Long userId,
                                          Long resourceId,
                                          String componentVersionName) {

        Integer componentType = EngineTypeComponentType.engineName2ComponentType(engineType);
        Integer deployMode = null;
        if (ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)) {
            //解析参数
            deployMode = taskParamsService.parseDeployTypeByTaskParams(taskParams, componentType, EngineType.Flink.name(), tenantId).getType();
        }
        String componentVersionValue = scheduleDictService.convertVersionNameToValue(componentVersionName, engineType);
        return buildTaskPluginInfo(
                projectId,
                appType,
                taskType,
                deployMode,
                engineType,
                tenantId,
                userId,
                resourceId,
                null,
                Collections.singletonMap(componentType, componentVersionValue),
                null);
    }

    public JSONObject buildTaskPluginInfo(ParamAction action) {
        Map<String, Object> actionParam;
        try {
            actionParam = PublicUtil.objectToMap(action);
        } catch (IOException e) {
            throw new DtCenterDefException(String.format("paramAction to Map error: %s", e.getMessage()), e);
        }
        Integer deployMode = MapUtils.getInteger(actionParam, DEPLOY_MODEL);
        Long tenantId = action.getTenantId();
        String engineType = action.getEngineType();
        if (null == deployMode && ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)) {
            //解析参数
            deployMode = taskParamsService.parseDeployTypeByTaskParams(action.getTaskParams(), action.getComputeType(), EngineType.Flink.name(), tenantId).getType();
        }
        String componentVersionValue = scheduleDictService.convertVersionNameToValue(action.getComponentVersion(), action.getEngineType());
        Integer componentType = EngineTypeComponentType.engineName2ComponentType(engineType);
        JSONObject jsonObject = buildTaskPluginInfo(
                action.getProjectId(),
                action.getAppType(),
                action.getTaskType(),
                deployMode,
                action.getEngineType(),
                tenantId,
                action.getUserId(),
                action.getResourceId(),
                null,
                Collections.singletonMap(componentType, componentVersionValue),
                actionParam);
        savaVersion(jsonObject,action.getTaskId());
        return jsonObject;
    }

    private void savaVersion(JSONObject jsonObject, String jobId) {
        if (jsonObject != null) {
            String componentVersion = (String) JSONPath.eval(jsonObject, "$.otherConf.componentVersion");
            componentVersion = scheduleDictService.convertVersionToVersionFormat(componentVersion);
            String jobExtraInfo = scheduleJobExpandDao.getJobExtraInfo(jobId);
            JSONObject jobExtraInfoJSon = JSONObject.parseObject(jobExtraInfo);
            if (jobExtraInfoJSon == null) {
                jobExtraInfoJSon= new JSONObject();
            }

            jobExtraInfoJSon.put(COMPONENT_VERSION_VALUE,componentVersion);
            scheduleJobExpandDao.updateExtraInfo(jobId,jobExtraInfoJSon.toJSONString());
        }


    }

    public JSONObject buildTaskPluginInfo(Long projectId,
                                          Integer appType,
                                          Integer taskType,
                                          Integer deployMode,
                                          String engineType,
                                          Long tenantId,
                                          Long userId,
                                          Long resourceId,
                                          Map<String, Object> selfExtraParam,
                                          Map<Integer, String> componentVersionMap,
                                          Map<String, Object> actionParam) {
        Integer componentType = EngineTypeComponentType.engineName2ComponentType(engineType);
        String componentVersionValue = MapUtils.isEmpty(componentVersionMap) ? null : componentVersionMap.get(componentType);
        Cluster cluster = clusterService.getCluster(tenantId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.TENANT_NOT_BIND);
        }
        com.dtstack.engine.api.domain.Component component = componentService.getComponentByVersion(cluster.getId(), componentType, componentVersionValue);
        return buildTaskPluginInfo(
                component,
                cluster,
                deployMode,
                selfExtraParam,
                componentVersionMap,
                projectId, appType,
                taskType,
                tenantId,
                userId,
                resourceId,
                actionParam);
    }

    /**
     * 构建任务需要的 pluginInfo
     *
     * @param component           组件配置信息
     * @param deployMode          deployMode
     * @param selfExtraParam      自身扩展配置
     * @param componentVersionMap 多版本配置信息
     * @param projectId           项目 id
     * @param appType             应用类型
     * @param taskType            任务类型
     * @param dtUicTenantId       租户 id
     * @param dtUicUserId         用户 id
     * @param resourceId          资源队列 id
     * @param actionParam         任务参数
     * @return pluginInfo 信息
     */
    public JSONObject buildTaskPluginInfo(com.dtstack.engine.api.domain.Component component,
                                          Cluster cluster,
                                          Integer deployMode,
                                          Map<String, Object> selfExtraParam,
                                          Map<Integer, String> componentVersionMap,
                                          Long projectId,
                                          Integer appType,
                                          Integer taskType,
                                          Long dtUicTenantId,
                                          Long dtUicUserId,
                                          Long resourceId,
                                          Map<String, Object> actionParam) {
        PluginInfoContext pluginInfoContext = PluginInfoContext.getTaskInstance(
                component,
                cluster,
                deployMode,
                selfExtraParam,
                componentVersionMap,
                projectId,
                appType,
                taskType,
                dtUicTenantId,
                dtUicUserId,
                resourceId,
                actionParam);
        return getAdapter(component.getComponentTypeCode(), pluginInfoContext).buildPluginInfo(false);
    }

    /**
     * 构建 console pluginInfo
     *
     * @param component           组件配置信息
     * @param deployMode          deployMode
     * @param selfExtraParam      自身配置
     * @param componentVersionMap 多版本信息
     * @return pluginInfo
     */
    public JSONObject buildConsolePluginInfo(com.dtstack.engine.api.domain.Component component,
                                             Cluster cluster,
                                             Integer deployMode,
                                             Map<String, Object> selfExtraParam,
                                             Map<Integer, String> componentVersionMap,
                                             Long dtUicTenantId,
                                             Long dtUicUserId) {
        PluginInfoContext pluginInfoContext = PluginInfoContext.getConsoleInstance(
                component,
                cluster,
                deployMode,
                selfExtraParam,
                componentVersionMap,dtUicTenantId,dtUicUserId);
        return getAdapter(component.getComponentTypeCode(), pluginInfoContext).buildPluginInfo(true);
    }

    /**
     * 获取 pluginInfoAdapter
     *
     * @param componentTypeCode 组件类型枚举值
     * @param pluginInfoContext pluginInfo context
     * @return pluginInfoAdapter
     */
    private IPluginInfoAdapter getAdapter(Integer componentTypeCode, PluginInfoContext pluginInfoContext) {
        return getAdapter(EComponentType.getByCode(componentTypeCode), pluginInfoContext);
    }

    /**
     * 获取 pluginInfoAdapter
     *
     * @param componentType     组件类型
     * @param pluginInfoContext pluginInfo context
     * @return pluginInfoAdapter
     */
    private IPluginInfoAdapter getAdapter(EComponentType componentType, PluginInfoContext pluginInfoContext) {
        IPluginInfoAdapter pluginInfoAdapter = PLUGIN_INFO_ADAPTER_MAP.get(componentType);
        // 默认 adapter
        if (Objects.isNull(componentType)
                || Objects.isNull(pluginInfoAdapter)) {
            (DEFAULT_PLUGIN_INFO_ADAPTER).setPluginInfoContext(pluginInfoContext);
            return DEFAULT_PLUGIN_INFO_ADAPTER;
        }
        pluginInfoAdapter.setPluginInfoContext(pluginInfoContext);
        return pluginInfoAdapter;
    }

    /**
     * 创建 adapter 代理并返回
     *
     * @param pluginInfoAdapter 原始对象
     * @return adapter 代理
     */
    private IPluginInfoAdapter createProxyAdapter(IPluginInfoAdapter pluginInfoAdapter) {
        return (IPluginInfoAdapter) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{IPluginInfoAdapter.class},
                new PluginInfoAdapterProxyHandle(pluginInfoAdapter));
    }
}
