package com.dtstack.engine.master.plugininfo;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;

import java.util.Map;
import java.util.Objects;

/**
 * pluginInfo context ...
 *
 * @author ：wangchuan
 * date：Created in 14:12 2022/10/31
 * company: www.dtstack.com
 */
public class PluginInfoContext {

    /**
     * component 可以根据版本信息、集群信息、componentType 推算出来, 该参数不能为空
     */
    private Component component;

    /**
     * 组件对应集群
     */
    private Cluster cluster;

    /**
     * deploy mode
     */
    private Integer deployMode;

    /**
     * 任务相关必须传
     */
    private Long projectId;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 租户 id
     */
    private Long dtUicTenantId;

    /**
     * 用户 id
     */
    private Long dtUicUserId;

    /**
     * 资源队列 id
     */
    private Long resourceId;

    /**
     * 任务参数
     */
    private Map<String, Object> actionParam;

    /**
     * 自身格外属性
     */
    private Map<String, Object> selfExtraParam;

    /**
     * 多版本信息
     */
    private Map<Integer, String> componentVersionMap;

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(Integer deployMode) {
        this.deployMode = deployMode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getDtUicUserId() {
        return dtUicUserId;
    }

    public void setDtUicUserId(Long dtUicUserId) {
        this.dtUicUserId = dtUicUserId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Map<String, Object> getActionParam() {
        return actionParam;
    }

    public void setActionParam(Map<String, Object> actionParam) {
        this.actionParam = actionParam;
    }

    public Map<String, Object> getSelfExtraParam() {
        return selfExtraParam;
    }

    public void setSelfExtraParam(Map<String, Object> selfExtraParam) {
        this.selfExtraParam = selfExtraParam;
    }

    public Map<Integer, String> getComponentVersionMap() {
        return componentVersionMap;
    }

    public void setComponentVersionMap(Map<Integer, String> componentVersionMap) {
        this.componentVersionMap = componentVersionMap;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    private PluginInfoContext() {
    }

    public static PluginInfoContext getTaskInstance(Component component,
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
        PluginInfoContext pluginInfoContext = getConsoleInstance(component, cluster, deployMode, selfExtraParam, componentVersionMap,dtUicTenantId,dtUicUserId);
        pluginInfoContext.setProjectId(projectId);
        pluginInfoContext.setAppType(appType);
        pluginInfoContext.setTaskType(taskType);
        pluginInfoContext.setDtUicTenantId(dtUicTenantId);
        pluginInfoContext.setDtUicUserId(dtUicUserId);
        pluginInfoContext.setResourceId(resourceId);
        pluginInfoContext.setActionParam(actionParam);
        return pluginInfoContext;
    }

    public static PluginInfoContext getConsoleInstance(Component component,
                                                       Cluster cluster,
                                                        Integer deployMode,
                                                        Map<String, Object> selfExtraParam,
                                                        Map<Integer, String> componentVersionMap,
                                                       Long dtUicTenantId,
                                                       Long dtUicUserId) {
        if (Objects.isNull(component)) {
            throw new DtCenterDefException("get console pluginInfo instance error, component is empty.");
        }
        PluginInfoContext pluginInfoContext = new PluginInfoContext();
        pluginInfoContext.setComponent(component);
        pluginInfoContext.setCluster(cluster);
        pluginInfoContext.setDeployMode(deployMode);
        pluginInfoContext.setSelfExtraParam(selfExtraParam);
        pluginInfoContext.setComponentVersionMap(componentVersionMap);
        pluginInfoContext.setDtUicTenantId(dtUicTenantId);
        pluginInfoContext.setDtUicUserId(dtUicUserId);
        return pluginInfoContext;
    }

    @Override
    public String toString() {
        return "PluginInfoContext{" +
                "component=" + component +
                ", deployMode=" + deployMode +
                ", projectId=" + projectId +
                ", appType=" + appType +
                ", taskType=" + taskType +
                ", dtUicTenantId=" + dtUicTenantId +
                ", dtUicUserId=" + dtUicUserId +
                ", resourceId=" + resourceId +
                ", actionParam=" + actionParam +
                ", selfExtraParam=" + selfExtraParam +
                ", componentVersionMap=" + componentVersionMap +
                '}';
    }
}
