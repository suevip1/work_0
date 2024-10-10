package com.dtstack.engine.common.client;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/27
 */
public interface IClient {

	void init(Properties prop) throws Exception;

	void close(String pluginInfo);

	JobResult submitJob(JobClient jobClient);

	JobResult cancelJob(JobIdentifier jobIdentifier);

	RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException;

	String getJobMaster(JobIdentifier jobIdentifier);

	String getMessageByHttp(String path);

	String getJobLog(JobIdentifier jobIdentifier);

	JudgeResult judgeSlots(JobClient jobClient);

	List<String> getContainerInfos(JobIdentifier jobIdentifier);

	String getCheckpoints(JobIdentifier jobIdentifier);

	ComponentTestResultDTO testConnect(String pluginInfo);

	List<List<Object>> executeQuery(String sql,String database);

	String uploadStringToHdfs(String bytes, String hdfsPath);

	ClusterResourceDTO getClusterResource();

	List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier);

	List<ColumnDTO> getAllColumns(String tableName, String schemaName, String dbName);

	CheckResultDTO grammarCheck(JobClient jobClient);

	List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String pluginInfo);

	List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpointPath);

	List<ApplicationInfoDTO> listApplication(RdosTaskStatus status,String name,String applicationId);

	ApplicationInfoDTO retrieveJob(JobIdentifier jobIdentifier);

	/**
	 * 读取hdfs数据，执行脚本任务时（shell,python等）
	 *
	 * @param hdfsPath hdfs文件路径，绝对路径
	 * @return
	 */
	String getHdfsWithScript(String hdfsPath);

	/**
	 * 读取hdfs数据，执行job任务时（hive,spark等）
	 *
	 * @param hdfsQueryDTO 查询对象
	 * @return 文件内数据, key:hdfsPath , value:数据
	 */
	List<String> getHdfsWithJob(HdfsQueryDTO hdfsQueryDTO);

	/**
	 * 统计文件夹内容摘要，包括文件的数量，文件夹的数量，文件变动时间，以及这个文件夹的占用存储等内容
	 *
	 * @param hdfsDirPaths 多个hdfs路径
	 * @return
	 */
	List<HdfsContentSummary> getContentSummary(List<String> hdfsDirPaths);

	/**
	 * 删除hdfs上的文件
	 *
	 * @param hdfsPath    hdfs文件路径，绝对路径
	 * @param isRecursion 是否递归删除
	 * @return 是否删除成功
	 */
	Boolean deleteHdfsFile(String hdfsPath, boolean isRecursion);


    /**
     * 获取任务指标信息
     * @param jobIdentifier
     * @param status
     * @return
     */
    String getJobMetricsAnalysis(JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime);


	default Optional<CheckResultDTO> executeSql(JobClient jobClient) {
		return Optional.of(new CheckResultDTO());
	};

	default Optional<List<FlinkTableResultDTO>> executeFlinkSQL(JobClient jobClient) {
		return Optional.of(Lists.newArrayList(new FlinkTableResultDTO()));
	};


    /**
     * 获取spark thrift 地址
     * @param jobIdentifier
     * @return
     */
	SparkThriftServerDTO getThriftServerUrl(JobIdentifier jobIdentifier);

	/**
	 * 获取 flink select 结果
	 * @param jobIdentifier
	 * @return
	 */
	FlinkQueryResultDTO queryJobData(JobIdentifier jobIdentifier);

	FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier);
}
