package com.dtstack.engine.common.client;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Reason:
 * Date: 2018/1/11
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class ClientOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOperator.class);

    private static ClientCache clientCache;

    private static ClientOperator singleton;

    private ClientOperator() {
    }

    public static ClientOperator getInstance(String pluginPath) {
        if (singleton == null) {
            synchronized (ClientOperator.class) {
                if (singleton == null) {
                    clientCache = ClientCache.getInstance(pluginPath);
                    LOGGER.info("init client operator plugin path {}",pluginPath);
                    singleton = new ClientOperator();
                }
            }
        }
        return singleton;
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);

        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId)) {
            throw new RdosDefineException("can't get job of jobId is empty or null!");
        }

        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            Object result = client.getJobStatus(jobIdentifier);

            if (result == null) {
                return null;
            }

            return (RdosTaskStatus) result;
        } catch (Exception e) {
            LOGGER.error("getStatus happens error：{}",jobId, e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);

        String logInfo;
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            logInfo = client.getJobLog(jobIdentifier);
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }

        return logInfo;
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getCheckpoints(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("get job checkpoints:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getJobMaster(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("get job master exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        if(jobClient.getEngineTaskId() == null){
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        JobIdentifier jobIdentifier = new JobIdentifier(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId()
        ,jobClient.getTenantId(),jobClient.getEngineType(),jobClient.getDeployMode(),jobClient.getUserId(),jobClient.getResourceId(),jobClient.getPluginInfo(),jobClient.getComponentVersion());
        jobIdentifier.setForceCancel(jobClient.getForceCancel());
        jobIdentifier.setProjectId(jobClient.getProjectId());
        jobIdentifier.setAppType(jobClient.getAppType());
        jobIdentifier.setTaskType(jobClient.getTaskType());
        jobIdentifier.setClassArgs(jobClient.getClassArgs());
        jobIdentifier.setTaskParams(jobClient.getTaskParams());
        checkoutOperator(jobClient.getEngineType(), jobClient.getPluginInfo(), jobIdentifier);

        jobIdentifier.setTimeout(getCheckoutTimeout(jobClient));
        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return client.cancelJob(jobIdentifier);
    }

    public Long getCheckoutTimeout(JobClient jobClient) {
        try {
            Long timeout = ConfigConstant.DEFAULT_CHECKPOINT_TIMEOUT;
            Properties taskProps = jobClient.getConfProperties();
            if (taskProps == null || taskProps.size() == 0) {
                return timeout;
            }
            if (taskProps.containsKey(ConfigConstant.SQL_CHECKPOINT_TIMEOUT)) {
                timeout = Long.valueOf(taskProps.getProperty(ConfigConstant.SQL_CHECKPOINT_TIMEOUT));
            } else if (taskProps.containsKey(ConfigConstant.FLINK_CHECKPOINT_TIMEOUT)) {
                timeout = Long.valueOf(taskProps.getProperty(ConfigConstant.FLINK_CHECKPOINT_TIMEOUT));
            }
            return timeout;
        } catch (Exception e) {
            return ConfigConstant.DEFAULT_CHECKPOINT_TIMEOUT;
        }
    }

    public List<String> containerInfos(JobClient jobClient) throws Exception {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId());
        checkoutOperator(jobClient.getEngineType(), jobClient.getPluginInfo(), jobIdentifier);
        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return client.getContainerInfos(jobIdentifier);
    }

    private void checkoutOperator(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        if (null == engineType || null == pluginInfo || null == jobIdentifier) {
            throw new IllegalArgumentException("engineType|pluginInfo|jobIdentifier is null.");
        }
        // todo
        jobIdentifier.setPluginInfo(pluginInfo);
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.judgeSlots(jobClient);
    }

    public JobResult submitJob(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.submitJob(jobClient);
    }

    public ComponentTestResultDTO testConnect(String engineType, String pluginInfo) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(engineType, pluginInfo, false);
        return clusterClient.testConnect(pluginInfo);
    }

    public List<List<Object>> executeQuery(String engineType, String pluginInfo, String sql, String database) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.executeQuery(sql, database);
    }

    public String uploadStringToHdfs(String engineType, String pluginInfo, String bytes, String hdfsPath) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.uploadStringToHdfs(bytes, hdfsPath);
    }

    public ClusterResourceDTO getClusterResource(String engineType, String pluginInfo) throws ClientAccessException{
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.getClusterResource();
    }

    public List<String> getRollingLogBaseInfo(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getRollingLogBaseInfo(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("get job rollingLogBaseInfo:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public CheckResultDTO grammarCheck(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.grammarCheck(jobClient);
    }

    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String engineType, String pluginInfo) {
        IClient client = clientCache.getDefaultPlugin(engineType);
        return client.getDtScriptAgentLabel(pluginInfo);
    }

    public List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpointPath, String engineType, String pluginInfo) throws ClientAccessException {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.clearCheckpoint(checkpointPath);
    }

    public List<ApplicationInfoDTO> listApplication(String typeName, String pluginInfo, RdosTaskStatus rdosTaskStatus,String name,String applicationId) throws ClientAccessException {
        IClient client = clientCache.getClient(typeName, pluginInfo);
        return client.listApplication(rdosTaskStatus,name,applicationId);
    }

    public ApplicationInfoDTO retrieveJob(JobClient jobClient) throws ClientAccessException {
        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId());
        jobIdentifier.setPluginInfo(jobClient.getPluginInfo());
        return client.retrieveJob(jobIdentifier);
    }

    public String getHdfsWithScript(String engineType, String pluginInfo, String hdfsPath) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.getHdfsWithScript(hdfsPath);
    }

    public List<HdfsContentSummary> getContentSummary(String engineType, String pluginInfo, List<String> hdfsDirPaths) throws Exception {
        checkParamsNotNull(engineType, pluginInfo);
        IClient client = clientCache.getClient(engineType, pluginInfo);
        List<HdfsContentSummary> contentSummary = client.getContentSummary(hdfsDirPaths);
        return contentSummary;
    }

    public Boolean deleteHdfsFile(String engineType, String pluginInfo, String deleteHdfsFile, boolean isRecursion) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.deleteHdfsFile(deleteHdfsFile, isRecursion);
    }

    public List<String> getHdfsWithJob(String engineType, String pluginInfo, HdfsQueryDTO hdfsQueryDTO) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.getHdfsWithJob(hdfsQueryDTO);
    }

    public String getJobMetricsAnalysis(String pluginInfo, JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime) throws Exception {
        IClient client = clientCache.getClient(jobIdentifier.getEngineType(), pluginInfo);
        return client.getJobMetricsAnalysis(jobIdentifier,status,startTime,endTime);
    }

    public Optional<CheckResultDTO> executeSql(JobClient jobClient) throws Exception {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.executeSql(jobClient);
    }

    public Optional<List<FlinkTableResultDTO>> executeFlinkSQL(JobClient jobClient) throws Exception {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.executeFlinkSQL(jobClient);
    }

    public FlinkQueryResultDTO queryJobData(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.queryJobData(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("queryJobData:" + jobIdentifier.getEngineJobId() + ", exception:" + ExceptionUtil.getErrorMessage(e));
        }

    }

    private static void checkParamsNotNull(String engineType, String pluginInfo) {
        if (null == engineType || null == pluginInfo) {
            throw new IllegalArgumentException("engineType|pluginInfo is null.");
        }
    }

    public SparkThriftServerDTO getThriftServerUrl(String pluginInfo,JobIdentifier jobIdentifier) throws Exception{
        IClient client = clientCache.getClient(jobIdentifier.getEngineType(), pluginInfo);
        return client.getThriftServerUrl(jobIdentifier);
    }

    public FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier) throws Exception {
        IClient client = clientCache.getClient(jobIdentifier.getEngineType(), jobIdentifier.getPluginInfo());
        return client.getWebMonitorUrl(jobIdentifier);
    }
}
