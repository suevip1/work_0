package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.multiengine.SyncDataSourceReplaceHandler;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.worker.RdosWrapper;

import java.util.List;
import java.util.Map;

public class HadoopJobStartTriggerMock extends BaseMock {

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime,
                               Integer scheduleType) {
        return "";
    }

    @MockInvoke(targetClass = JobChainParamHandler.class)
    public JobChainParamHandleResult handle(String sql, ScheduleTaskShade taskShade, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
        JobChainParamHandleResult result = new JobChainParamHandleResult();
        result.setSql(sql);
        result.setTaskParams(taskShade.getTaskParams());
        return result;
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime) {
        return sql;
    }

    @MockInvoke(targetClass = SyncDataSourceReplaceHandler.class)
    public String replaceSync(String syncJob, String jobId) {
        return syncJob;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
        Component component = new Component();
        component.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
        component.setHadoopVersion("112");
        return component;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public Component getMetadataComponent(Long clusterId) {
        Component component = new Component();
        component.setComponentTypeCode(EComponentType.SPARK_THRIFT.getTypeCode());
        return component;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public String getConfigByKey(Long dtUicTenantId, String componentConfName, Boolean fullKerberos, Map<Integer, String> componentVersionMap, boolean needHdfsConfig) {
        return "{}";
    }

    @MockInvoke(targetClass = ClusterService.class)
    public JSONObject pluginInfoJSON(Long projectId, Integer appType, Integer taskType, Long dtUicTenantId, String engineTypeStr, Long dtUicUserId, Integer deployMode, Long resourceId, Map<Integer, String> componentVersionMap) {
        JSONObject pluginInfo = new JSONObject();
        return pluginInfo;
    }


    @MockInvoke(targetClass = RdosWrapper.class)
    public Integer getDataSourceCodeByDiceName(String diceName, EComponentType eComponentType) {
        return 1;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public String buildHdfsTypeName(Long dtUicTenantId, Long clusterId) {
        return "hdfs2";
    }

    @MockInvoke(targetClass = ClusterService.class)
    public Cluster getCluster(Long dtUicTenantId) {
        Cluster cluster = new Cluster();
        cluster.setId(1L);
        return cluster;
    }
}
