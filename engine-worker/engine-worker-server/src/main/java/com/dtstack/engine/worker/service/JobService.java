package com.dtstack.engine.worker.service;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.api.IJobService;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: dazhi
 * @Date: 2022/2/28 5:18 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobService  implements IJobService {

    @Autowired
    private ClientOperator clientOperator;

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) throws Exception {
        return clientOperator.judgeSlots(jobClient);
    }

    @Override
    public JobResult submitJob(JobClient jobClient) throws Exception {
        return clientOperator.submitJob(jobClient);
    }

    @Override
    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return clientOperator.getJobStatus(engineType,pluginInfo,jobIdentifier);
    }

    @Override
    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        return clientOperator.getEngineLog(engineType,pluginInfo,jobIdentifier);
    }

    @Override
    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return clientOperator.getCheckpoints(engineType,pluginInfo,jobIdentifier);
    }

    @Override
    public List<String> getRollingLogBaseInfo(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return clientOperator.getRollingLogBaseInfo(engineType,pluginInfo,jobIdentifier);
    }

    @Override
    public JobResult stopJob(JobClient jobClient) throws Exception {
        return clientOperator.stopJob(jobClient);
    }

    @Override
    public List<String> containerInfos(JobClient jobClient) throws Exception {
        return clientOperator.containerInfos(jobClient);
    }

    @Override
    public ComponentTestResultDTO testConnect(String engineType, String pluginInfo) throws Exception {
        return clientOperator.testConnect(engineType,pluginInfo);
    }

    @Override
    public CheckResultDTO grammarCheck(JobClient jobClient) throws Exception {
        return clientOperator.grammarCheck(jobClient);
    }

    @Override
    public List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpoints, String engineType, String pluginInfo) throws ClientAccessException {
        return clientOperator.clearCheckpoint(checkpoints, engineType, pluginInfo);
    }

    @Override
    public List<ApplicationInfoDTO> listApplication(String typeName, String pluginInfo, RdosTaskStatus status, String name, String applicationId) throws ClientAccessException {
        return clientOperator.listApplication(typeName,pluginInfo,status,name,applicationId);
    }

    @Override
    public ApplicationInfoDTO retrieveJob(JobClient jobClient) throws ClientAccessException {
        return clientOperator.retrieveJob(jobClient);
    }

    @Override
    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String engineType, String pluginInfo) {
        return clientOperator.getDtScriptAgentLabel(engineType,pluginInfo);
    }

    @Override
    public String getHdfsWithScript(String engineType, String pluginInfo, String hdfsPath) throws Exception {
        return clientOperator.getHdfsWithScript(engineType,pluginInfo,hdfsPath);
    }

    @Override
    public List<HdfsContentSummary> getContentSummary(String engineType, String pluginInfo, List<String> hdfsDirPaths) throws Exception {
        return clientOperator.getContentSummary(engineType,pluginInfo,hdfsDirPaths);
    }

    @Override
    public Boolean deleteHdfsFile(String engineType, String pluginInfo, String deleteHdfsFile, boolean isRecursion) throws Exception {
        return clientOperator.deleteHdfsFile(engineType,pluginInfo,deleteHdfsFile,isRecursion);
    }

    @Override
    public List<String> getHdfsWithJob(String engineType, String pluginInfo, HdfsQueryDTO hdfsQueryDTO) throws Exception {
        return clientOperator.getHdfsWithJob(engineType,pluginInfo,hdfsQueryDTO);
    }

    @Override
    public String getJobMetricsAnalysis(String pluginInfo, JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime) throws Exception {
        return clientOperator.getJobMetricsAnalysis(pluginInfo,jobIdentifier,status,startTime,endTime);
    }

    @Override
    public CheckResultDTO executeSql(JobClient jobClient) throws Exception {
        Optional<CheckResultDTO> checkResultDTO = clientOperator.executeSql(jobClient);
        return checkResultDTO.orElseGet(CheckResultDTO::new);
    }

    @Override
    public List<FlinkTableResultDTO> executeFlinkSQL(JobClient jobClient) throws Exception {
        Optional<List<FlinkTableResultDTO>> checkResultDTOList = clientOperator.executeFlinkSQL(jobClient);
        return checkResultDTOList.orElseGet(ArrayList::new);
    }

    @Override
    public FlinkQueryResultDTO queryJobData(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return clientOperator.queryJobData(engineType, pluginInfo, jobIdentifier);
    }

    @Override
    public FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier) throws Exception {
        return clientOperator.getWebMonitorUrl(jobIdentifier);
    }

    @Override
    public SparkThriftServerDTO getThriftServerUrl(String pluginInfo,JobIdentifier jobIdentifier) throws Exception{
        return clientOperator.getThriftServerUrl(pluginInfo,jobIdentifier);
    }
}
