package com.dtstack.engine.common.client;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.ApplicationInfoDTO;
import com.dtstack.engine.common.client.bean.CheckResultDTO;
import com.dtstack.engine.common.client.bean.ClusterResourceDTO;
import com.dtstack.engine.common.client.bean.ColumnDTO;
import com.dtstack.engine.common.client.bean.ComponentTestResultDTO;
import com.dtstack.engine.common.client.bean.DtScriptAgentLabelDTO;
import com.dtstack.engine.common.client.bean.EngineJobCheckpointDTO;
import com.dtstack.engine.common.client.bean.FlinkQueryResultDTO;
import com.dtstack.engine.common.client.bean.FlinkWebUrlResultDTO;
import com.dtstack.engine.common.client.bean.SparkThriftServerDTO;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JobStatusFrequency;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public abstract class AbstractClient implements IClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);

    public Map<String, JobStatusFrequency> jobStatusMap = Maps.newConcurrentMap();

    public AbstractClient() {
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try {
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null) {
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(), ",") + ")");
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            jobResult = JobResult.createErrorResult(e);
        } finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        RdosTaskStatus status = RdosTaskStatus.NOTFOUND;
        try {
            status = processJobStatus(jobIdentifier);
        }catch (Exception e) {
            LOGGER.error("get job status error: {}", e.getMessage());
        } finally {
            handleJobStatus(jobIdentifier, status);
        }
        return status;
    }

    protected RdosTaskStatus processJobStatus(JobIdentifier jobIdentifier) {
        return RdosTaskStatus.NOTFOUND;
    }

    protected void handleJobStatus(JobIdentifier jobIdentifier, RdosTaskStatus status) {
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        return null;
    }

    /**
     * job 处理具体实现的抽象
     *
     * @param jobClient 对象参数
     * @return 处理结果
     */
    protected abstract JobResult processSubmitJobWithType(JobClient jobClient);

    @Override
    public String getJobLog(JobIdentifier jobId) {
        return "";
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.notOk( "");
    }

    protected void beforeSubmitFunc(JobClient jobClient) {
    }

    protected void afterSubmitFunc(JobClient jobClient) {
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }


    @Override
    public ComponentTestResultDTO testConnect(String pluginInfo) {
        return null;
    }

    @Override
    public List<List<Object>> executeQuery(String sql, String database) {
        return null;
    }

    @Override
    public String uploadStringToHdfs(String bytes, String hdfsPath) {
        return null;
    }

    @Override
    public ClusterResourceDTO getClusterResource() {
        return null;
    }

    @Override
    public List<ColumnDTO> getAllColumns(String tableName,String schemaName, String dbName) {
        return null;
    }

    @Override
    public CheckResultDTO grammarCheck(JobClient jobClient){
        return null;
    }

    @Override
    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String pluginInfo) {
        return null;
    }

    @Override
    public List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpointPath) {
        throw new RdosDefineException("subclass must be overridden to call");
    }

    @Override
    public List<ApplicationInfoDTO> listApplication(RdosTaskStatus status,String name,String applicationId) {
        return new ArrayList<>();
    }

    @Override
    public ApplicationInfoDTO retrieveJob(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getHdfsWithScript(String hdfsPath) {
        return null;
    }

    @Override
    public List<String> getHdfsWithJob(HdfsQueryDTO hdfsQueryDTO) {
        return null;
    }

    @Override
    public List<HdfsContentSummary> getContentSummary(List<String> hdfsDirPaths) {
        return null;
    }

    @Override
    public Boolean deleteHdfsFile(String hdfsPath, boolean isRecursion) {
        return null;
    }

    @Override
    public String getJobMetricsAnalysis(JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime) {
        return null;
    }

    @Override
    public void close(String pluginInfo) {
        // ignore
    }

    @Override
    public SparkThriftServerDTO getThriftServerUrl(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public FlinkQueryResultDTO queryJobData(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier) {
        return null;
    }
}
