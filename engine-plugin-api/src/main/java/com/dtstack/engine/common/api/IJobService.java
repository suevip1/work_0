package com.dtstack.engine.common.api;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import com.dtstack.rpc.annotation.RpcService;
import com.dtstack.rpc.enums.RpcRemoteType;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/2/28 2:57 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RpcService(rpcRemoteType = RpcRemoteType.DAGSCHEDULEX_CLIENT)
public interface IJobService {

    JudgeResult judgeSlots(JobClient jobClient) throws Exception;

    JobResult submitJob(JobClient jobClient) throws Exception;

    RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception;

    String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier);

    String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception;

    List<String> getRollingLogBaseInfo(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception;

    JobResult stopJob(JobClient jobClient) throws Exception;

    List<String> containerInfos(JobClient jobClient) throws Exception;

    ComponentTestResultDTO testConnect(String engineType, String pluginInfo) throws Exception;

    CheckResultDTO grammarCheck(JobClient jobClient) throws Exception;

    List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpoints, String engineType, String pluginInfo) throws ClientAccessException;

    List<ApplicationInfoDTO> listApplication(String typeName, String pluginInfo, RdosTaskStatus status, String name, String applicationId) throws ClientAccessException;

    ApplicationInfoDTO retrieveJob(JobClient jobClient) throws ClientAccessException;

    List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String engineType, String pluginInfo);

    String getHdfsWithScript(String engineType, String pluginInfo, String hdfsPath) throws Exception;

    List<HdfsContentSummary> getContentSummary(String engineType, String pluginInfo, List<String> hdfsDirPaths) throws Exception;

    Boolean deleteHdfsFile(String engineType, String pluginInfo, String deleteHdfsFile, boolean isRecursion) throws Exception;

    List<String> getHdfsWithJob(String engineType, String pluginInfo, HdfsQueryDTO hdfsQueryDTO) throws Exception;

    String getJobMetricsAnalysis(String pluginInfo, JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime) throws Exception;

    CheckResultDTO executeSql(JobClient jobClient) throws Exception;

    List<FlinkTableResultDTO> executeFlinkSQL(JobClient jobClient) throws Exception;

    SparkThriftServerDTO getThriftServerUrl(String pluginInfo,JobIdentifier jobIdentifier) throws Exception;

    FlinkQueryResultDTO queryJobData(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception;

    FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier) throws Exception;
}
