package com.dtstack.engine.master.worker;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.FlinkTableResult;
import com.dtstack.engine.api.vo.stream.FlinkQueryResultVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.api.IJobService;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import com.dtstack.engine.common.util.PluginInfoUtil;
import com.dtstack.engine.dto.ApplicationInfo;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.mapstruct.PlugInStruct;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class EnginePluginsOperator implements TaskOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnginePluginsOperator.class);

    @Autowired
    private IJobService iJobService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private PlugInStruct plugInStruct;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private EnvironmentContext environmentContext;

    private void buildPluginInfo(JobClient jobClient) {
        // 补充插件配置信息
        try {
            if (environmentContext.openDummy()) {
                JSONObject info = new JSONObject();
                info.put(ConfigConstant.TYPE_NAME_KEY,"dummy");
                jobClient.setPluginWrapperInfo(info);
                return;
            }

            //jobClient中如果有pluginInfo(数据质量)以jobClient自带优先
            JSONObject info = JSONObject.parseObject(jobClient.getPluginInfo());
            if (null != info && !info.isEmpty()) {
                return;
            }



            JSONObject pluginInfo = pluginInfoManager.buildTaskPluginInfo(JobClientUtil.getParamAction(jobClient));
            jobClient.setPluginWrapperInfo(pluginInfo);

            Integer deployMode = PluginInfoUtil.getDeployMode(pluginInfo);
            if (Objects.nonNull(deployMode)) {
                jobClient.setDeployMode(deployMode);
            }
        } catch (Exception e) {
            LOGGER.error("{} buildPluginInfo failed!", jobClient.getTaskId(), e);
            throw new RdosDefineException("buildPluginInfo error", e);
        }
    }

    private String getPluginInfo(JobIdentifier jobIdentifier) {
        if (null != jobIdentifier) {
            if (environmentContext.openDummy()) {
                JSONObject info = new JSONObject();
                info.put(ConfigConstant.TYPE_NAME_KEY,"dummy");
                return info.toJSONString();
            }

            JSONObject info = JSONObject.parseObject(jobIdentifier.getPluginInfo());
            if (null != info && !info.isEmpty()) {
                return jobIdentifier.getPluginInfo();
            }
        }

        if (null == jobIdentifier || null == jobIdentifier.getEngineType() || null == jobIdentifier.getTenantId()) {
            LOGGER.error("pluginInfo params lost {}", jobIdentifier);
            throw new RdosDefineException("pluginInfo params lost");
        }
        EngineTypeComponentType engineTypeComponentType = EngineTypeComponentType.getByEngineName(jobIdentifier.getEngineType());
        Map<Integer, String> componentVersionMap = Maps.newHashMap();
        if (engineTypeComponentType != null) {
            String engineName = engineTypeComponentType.getScheduleEngineType().getEngineName();
            String componentVersionValue = scheduleDictService.convertVersionNameToValue(jobIdentifier.getComponentVersion(), engineName);
            componentVersionMap = Collections.singletonMap(engineTypeComponentType.getComponentType().getTypeCode(), componentVersionValue);
        }
        JSONObject info = pluginInfoManager.buildTaskPluginInfo(jobIdentifier.getProjectId(), jobIdentifier.getAppType(), jobIdentifier.getTaskType(),
                jobIdentifier.getTenantId(), jobIdentifier.getEngineType(), jobIdentifier.getUserId(), jobIdentifier.getDeployMode(), jobIdentifier.getResourceId(),
                componentVersionMap);
        if (null == info) {
            return null;
        }
        return info.toJSONString();
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return iJobService.judgeSlots(jobClient);
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        JobResult jobResult = iJobService.submitJob(jobClient);

        if (jobResult == null) {
            return JobResult.createErrorResult("because lacking resource, submit job failed.");
        }
        return jobResult;
    }

    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier, Consumer<String> originStatusConsumer) {
        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId)) {
            throw new RdosDefineException("can't get job of jobId is empty or null!");
        }

        try {
            RdosTaskStatus status = iJobService.getJobStatus(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == status) {
                status = RdosTaskStatus.NOTFOUND;
            }
            return status;
        } catch (Exception e) {
            LOGGER.error("getStatus happens error：{}", jobId, e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Override
    public ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId, String versionName) {
        try {
            ComponentTestResult componentTestResult = plugInStruct.toComponentTestResult(iJobService.testConnect(engineType, pluginInfo));
            return Objects.isNull(componentTestResult) ? ComponentTestResult.createFailResult("check result is null."): componentTestResult;
        } catch (Exception e) {
            LOGGER.error("testConnect failed, engineType: {}, pluginInfo: {}", engineType, pluginInfo, e);
            return ComponentTestResult.createFailResult(ExceptionUtil.getErrorMessage(e));
        }
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        String logInfo;
        if (StringUtils.isNotBlank(jobIdentifier.getEngineJobId())) {
            LOGGER.warn("jobIdentifier:{}", jobIdentifier);
        }

        try {
            logInfo = iJobService.getEngineLog(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == logInfo) {
                logInfo = org.apache.commons.lang3.StringUtils.EMPTY;
            }
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }
        return logInfo;
    }

    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String checkpoints = org.apache.commons.lang3.StringUtils.EMPTY;

        try {
            checkpoints = iJobService.getCheckpoints(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == checkpoints) {
                checkpoints = org.apache.commons.lang3.StringUtils.EMPTY;
            }
        } catch (Exception e) {
            LOGGER.error("getCheckpoints failed!", e);
        }
        return checkpoints;
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        List<String> rollingLogBaseInfo = Lists.newArrayList();

        try {
            rollingLogBaseInfo = iJobService.getRollingLogBaseInfo(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == rollingLogBaseInfo || rollingLogBaseInfo.size() == 0) {
                rollingLogBaseInfo = Lists.newArrayList();
            }

        } catch (Exception e) {
            LOGGER.error("getRollingLogBaseInfo failed!", e);
        }
        return rollingLogBaseInfo;
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        if (jobClient.getEngineTaskId() == null) {
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        return iJobService.stopJob(jobClient);
    }

    public List<String> containerInfos(JobClient jobClient) {
        this.buildPluginInfo(jobClient);

        try {
            List<String> containerInfos = iJobService.containerInfos(jobClient);
            if (null == containerInfos) {
                containerInfos = new ArrayList<>(0);
            }
            return containerInfos;
        } catch (Exception e) {
            LOGGER.error("getCheckpoints failed!", e);
            return null;
        }
    }

    public CheckResult grammarCheck(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return plugInStruct.toCheckResult(iJobService.grammarCheck(jobClient));
    }

    public List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpoints, String engineType, String pluginInfo) throws ClientAccessException {
        return iJobService.clearCheckpoint(checkpoints, engineType, pluginInfo);
    }

    public ApplicationInfo retrieveJob(JobClient jobClient) throws ClientAccessException {
        this.buildPluginInfo(jobClient);

        return plugInStruct.toApplicationInfoDTO(iJobService.retrieveJob(jobClient));
    }

    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String engineType, String pluginInfo) throws Exception {
        return iJobService.getDtScriptAgentLabel(engineType, pluginInfo);
    }

    public String getHdfsWithScript(String engineType, String pluginInfo, String hdfsPath) throws Exception {
        return iJobService.getHdfsWithScript(engineType, pluginInfo, hdfsPath);
    }

    public List<HdfsContentSummary> getContentSummary(String engineType, String pluginInfo, List<String> hdfsDirPaths) throws Exception {
        return iJobService.getContentSummary(engineType, pluginInfo, hdfsDirPaths);
    }

    public Boolean deleteHdfsFile(String engineType, String pluginInfo, String deleteHdfsFile, boolean isRecursion) throws Exception {
        return iJobService.deleteHdfsFile(engineType, pluginInfo, deleteHdfsFile, isRecursion);
    }

    public List<String> getHdfsWithJob(String engineType, String pluginInfo, HdfsQueryDTO hdfsQueryDTO) throws Exception {
        return iJobService.getHdfsWithJob(engineType, pluginInfo, hdfsQueryDTO);
    }

    public String getJobMetricsAnalysis(JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime) throws Exception {
        return iJobService.getJobMetricsAnalysis(getPluginInfo(jobIdentifier), jobIdentifier, status, startTime, endTime);
    }

    public CheckResult executeSql(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return plugInStruct.toCheckResult(iJobService.executeSql(jobClient));
    }

    public List<FlinkTableResult> executeFlinkSQL(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return plugInStruct.toFlinkTableResultList(iJobService.executeFlinkSQL(jobClient));
    }

    /**
     * 获取 flink select 查询结果
     *
     * @param jobIdentifier
     * @return
     * @throws Exception
     */
    public FlinkQueryResultVO queryJobData(JobIdentifier jobIdentifier) throws Exception {
        FlinkQueryResultDTO flinkQueryResultDTO = iJobService.queryJobData(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
        return plugInStruct.toFlinkQueryResultVO(flinkQueryResultDTO);
    }

    @Override
    public List<YarnApplicationInfoDTO> listApplicationByTag(JobClient jobClient, String tag) {
        throw new DtCenterDefException("engine plugins is not support method [listApplicationByTag]");
    }

    @Override
    public JobExecInfo getJobExecInfo(JobIdentifier jobIdentifier, boolean removeResult, boolean removeOutput) {
        throw new DtCenterDefException("engine plugins is not support method [getJobExecInfo]");
    }

    public SparkThriftServerDTO getThriftServerUrl(JobIdentifier jobIdentifier) throws Exception {
        return iJobService.getThriftServerUrl(getPluginInfo(jobIdentifier), jobIdentifier);
    }

    public FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier) throws Exception {
        jobIdentifier.setPluginInfo(this.getPluginInfo(jobIdentifier));
        return iJobService.getWebMonitorUrl(jobIdentifier);
    }
}
