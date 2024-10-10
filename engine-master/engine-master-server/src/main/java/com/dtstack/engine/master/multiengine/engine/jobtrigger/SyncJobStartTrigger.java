package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.Column;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.Table;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.constrant.UicConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.dto.MetaDataVerification;
import com.dtstack.engine.master.dto.PreCheckSyncDTO;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.multiengine.SyncDataSourceReplaceHandler;
import com.dtstack.engine.master.multiengine.checker.ColumnChecker;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.plugininfo.service.PluginInfoService;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.security.DataSecurityChecker;
import com.dtstack.engine.master.security.DtKerberosParam;
import com.dtstack.engine.master.security.DtKerberosUtil;
import com.dtstack.engine.master.security.chooser.DefaultKerberosChooser;
import com.dtstack.engine.master.security.chooser.SyncKerberosChooser;
import com.dtstack.engine.master.security.kerberos.ClusterKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.KerberosReq;
import com.dtstack.engine.master.security.kerberos.SyncPartitionKerberosAuthentication;
import com.dtstack.engine.master.utils.JdbcUrlUtil;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.schedule.common.enums.DataBaseType;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.metric.batch.IMetric;
import com.dtstack.schedule.common.metric.batch.MetricBuilder;
import com.dtstack.schedule.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-04 11:40
 * 同步任务相关
 */
@Component
public class SyncJobStartTrigger extends HadoopJobStartTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncJobStartTrigger.class);

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private static final String KEY_OPEN_CHECKPOINT = "openCheckpoint";

    private static final String CONF_PROPERTIES = "confProp";

    private static final String KEY_SAVEPOINT = "state.checkpoints.dir";

    private static final String KEY_CHECKPOINT_INTERVAL = "flink.checkpoint.interval";

    public static final String REAL_DATA_BASE = "realDataBase";

    private static final String DEFAULT_VAL_CHECKPOINT_INTERVAL = "300000";

    private static final String KEY_CHECKPOINT_STATE_BACKEND = "flink.checkpoint.stateBackend";

    private static final List<String> FOSSIL_JOB_ARGS_VERSION = ImmutableList.of("1.8", "180", "1.10", "110");

    private static final String JOB_ARGS_TEMPLATE = "-jobid %s -job %s";

    private static final String JOB_SAVEPOINT_ARGS_TEMPLATE = "-confProp %s";

    private static final String ADD_PART_TEMP = "alter table %s add partition(task_name='%s_%s',time='%s')";

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private SyncDataSourceReplaceHandler sourceReplaceHandler;

    @Autowired
    private DataSecurityChecker dataSecurityChecker;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired(required = false)
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private PluginInfoService pluginInfoService;

    @Resource
    private ThreadPoolTaskExecutor commonExecutor;

    @Autowired
    private ColumnChecker columnChecker;

    @Resource
    private SyncKerberosChooser syncKerberosChooser;

    @Resource
    private DefaultKerberosChooser defaultKerberosChooser;

    @Autowired
    private ComponentDao componentDao;

    /**
     * 范围分区标识
     */
    private static final Integer PARTITION_TYPE_RANGE = 2;

    @Override
    protected void doProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        String job = Objects.toString(actionParam.get("job"), StringUtils.EMPTY);
        String taskParams = Objects.toString(actionParam.get(GlobalConst.TASK_PARAMS), StringUtils.EMPTY);
        String taskExeArgs = null;

        List<ScheduleTaskParamShade> taskParamsToReplace = (List<ScheduleTaskParamShade>)actionParam.getOrDefault(GlobalConst.taskParamToReplace, Collections.emptyList());

        String flinkVersion = taskShade.getComponentVersion();
        if (StringUtils.isBlank(flinkVersion)) {
            flinkVersion = getDefaultComponentVersion(taskShade.getDtuicTenantId(), EComponentType.FLINK.getTypeCode());
        }

        // flinkx 数据源信息替换，支持版本：>= 1.12
        if (!FOSSIL_JOB_ARGS_VERSION.contains(flinkVersion)) {
            job = sourceReplaceHandler.replaceSync(job, scheduleJob.getJobId());
        }
        job = this.replaceSyncJobString(actionParam, taskShade, scheduleJob, taskParamsToReplace, job);
        checkJob(job,actionParam,taskShade,scheduleJob,taskParamsToReplace);
        job = this.replaceKerberosForJobRun(actionParam, taskShade, scheduleJob, job, flinkVersion);

        JSONObject confProp = new JSONObject();
        // 构造savepoint参数
        JSONObject savepointArgs = null;
        if (isRestore(job)) {
            String savepointPath = this.getSavepointPath(taskShade.getDtuicTenantId());
            savepointArgs = this.buildSyncTaskExecArgs(savepointPath, taskParams);
            confProp.putAll(savepointArgs);

            taskParams += String.format(" \n %s=%s", KEY_OPEN_CHECKPOINT, Boolean.TRUE);
        }

        String confPath = (String) actionParam.getOrDefault(CONF_PROPERTIES, "");
        if (StringUtils.isNotBlank(confPath)) {
            confProp.putAll(JSONObject.parseObject(confPath));
        }

        job = URLEncoder.encode(job.replace(TaskConstant.JOB_ID, scheduleJob.getJobId()), Charsets.UTF_8.name());

        if (FOSSIL_JOB_ARGS_VERSION.contains(flinkVersion)) {
            taskExeArgs = String.format(JOB_ARGS_TEMPLATE, scheduleJob.getJobName(), job);
        } else {
            taskExeArgs = String.format("-job %s", job);
        }

        if (MapUtils.isNotEmpty(confProp)) {
            String confPropStr = String.format(JOB_SAVEPOINT_ARGS_TEMPLATE, URLEncoder.encode(confProp.toJSONString(), Charsets.UTF_8.name()));
            taskExeArgs += " " + confPropStr;
        }
        if (taskExeArgs != null) {
            this.replaceTaskExeArgs(actionParam, scheduleJob, taskParamsToReplace, taskExeArgs, null);
        }
        actionParam.put(GlobalConst.TASK_PARAMS, taskParams);
    }

    public void checkJob(String job, Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace) {
        JSONObject jobJson = JSONObject.parseObject(job);
        PreCheckSyncDTO preCheckSyncDTO = JSONObject.parseObject((String) actionParam.get("preCheck"),
                PreCheckSyncDTO.class);
        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        if (Objects.isNull(userId)) {
            userId = taskShade.getOwnerUserId();
        }
        Long dtUicTenantId = MapUtils.getLong(actionParam, GlobalConst.TENANT_ID);
        Long finalUserId = userId;
        List<CompletableFuture> futures = new ArrayList<>();
        //check dataSecurity
        futures.add(CompletableFuture.runAsync(() -> dataSecurityChecker.preCheckSync(preCheckSyncDTO, jobJson, taskShade, scheduleJob, taskParamsToReplace, finalUserId, dtUicTenantId), commonExecutor));

        //check column
        checkColumn(actionParam, jobJson, futures);
        //check dataSecurity result
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        //check column result
        //临时直接异常失败 日志打印 其他修改状态发送告警
        StringBuilder resultMsg = new StringBuilder();
        for (int i = 1; i < futures.size(); i++) {
            try {
                String message = (String) futures.get(i).get();
                resultMsg.append(message);
            } catch (Exception e) {
                LOGGER.error("job {} check column ", scheduleJob.getJobId(), e);
            }
        }
        if (StringUtils.isNotBlank(resultMsg.toString())) {
            LOGGER.info("job {} check column has change {}", scheduleJob.getJobId(), resultMsg);
            throw new RdosDefineException(resultMsg.toString());
        }
    }

    private void checkColumn(Map<String, Object> actionParam, JSONObject jobJson, List<CompletableFuture> futures) {
        //兼容历史数据
        Object checkObj = actionParam.getOrDefault("metadataCheck", actionParam.get("metaDataVerification"));
        if (null == checkObj) {
            return;
        }
        MetaDataVerification metaDataVerification = JSONObject.parseObject((String) checkObj, MetaDataVerification.class);
        if (null == metaDataVerification) {
            return;
        }
        if (metaDataVerification.isReader()) {
            Long readSourceId = metaDataVerification.getReaderSourceId();
            String readSchema = (String) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.connection[0].schema");
            String readTable = (String) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.connection[0].table[0]");
            Integer readDataSourceType = (Integer) actionParam.get("readDataSourceType");
            List<Column> readColumnInfoList = metaDataVerification.getReaderColumnInfoList();
            CompletableFuture<String> readColumnFuture = CompletableFuture.supplyAsync(() ->
                    columnChecker.checkColumn(readColumnInfoList, readSourceId, readTable, readSchema, readDataSourceType, true), commonExecutor);
            futures.add(readColumnFuture);
        }

        if (metaDataVerification.isWriter()) {
            Long writerSourceId = metaDataVerification.getWriterSourceId();
            String writerSchema = (String) JSONPath.eval(jobJson, "$.job.content[0].writer.parameter.connection[0].schema");
            String writerTable = (String) JSONPath.eval(jobJson, "$.job.content[0].writer.parameter.connection[0].table[0]");
            Integer writerDataSourceType = (Integer) actionParam.get("dataSourceType");
            List<Column> writeColumnInfoList = metaDataVerification.getWriterColumnInfoList();
            CompletableFuture<String> writeColumnFuture = CompletableFuture.supplyAsync(() ->
                    columnChecker.checkColumn(writeColumnInfoList, writerSourceId, writerTable, writerSchema, writerDataSourceType, false), commonExecutor);
            futures.add(writeColumnFuture);
        }
    }


    private String queryLastLocation(Long dtUicTenantId, String engineJobId, long startTime, long endTime, String taskParam,Integer computeType,String jobId) {
        endTime = endTime + 1000 * 60;
        List<ComponentsConfigOfComponentsVO> componentsConfigOfComponentsVOS = componentService.listConfigOfComponents(dtUicTenantId, MultiEngineType.HADOOP.getType(),null);
        if (CollectionUtils.isEmpty(componentsConfigOfComponentsVOS)) {
            return null;
        }
        Optional<ComponentsConfigOfComponentsVO> flinkComponent = componentsConfigOfComponentsVOS.stream().filter(c -> c.getComponentTypeCode().equals(EComponentType.FLINK.getTypeCode())).findFirst();
        if(flinkComponent.isPresent()){
            ComponentsConfigOfComponentsVO componentsVO = flinkComponent.get();
            JSONObject flinkJsonObject = JSONObject.parseObject(componentsVO.getComponentConfig());
            EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams(taskParam,computeType, EngineType.Flink.name(),dtUicTenantId);
            JSONObject flinkConfig = flinkJsonObject.getJSONObject(eDeployMode.getMode());
            String prometheusHost = flinkConfig.getString("prometheusHost");
            String prometheusPort = flinkConfig.getString("prometheusPort");
            LOGGER.info("last job {} deployMode {} prometheus host {} port {}", jobId, eDeployMode.getType(), prometheusHost, prometheusPort);
            //prometheus的配置信息 从控制台获取
            PrometheusMetricQuery prometheusMetricQuery = new PrometheusMetricQuery(String.format("%s:%s", prometheusHost, prometheusPort));
            IMetric numReadMetric = MetricBuilder.buildMetric("endLocation", engineJobId, startTime, endTime, prometheusMetricQuery);
            if (numReadMetric != null) {
                String startLocation = String.valueOf(numReadMetric.getMetric());
                LOGGER.info("job {} deployMode {} startLocation [{}]", jobId, eDeployMode.getType(),startLocation);
                if (StringUtils.isEmpty(startLocation) || "0".equalsIgnoreCase(startLocation)) {
                    return null;
                }
                return String.valueOf(numReadMetric.getMetric());
            }
        }
        return null;
    }

    /**
     * 创建脏数据表的分区数据
     *
     * @param saveDirty
     * @param sqlText
     * @param taskId
     * @param sourceType
     * @return
     * @throws Exception
     */
    private String replaceTablePath(boolean saveDirty, String sqlText, Long taskId, Integer sourceType, String db, Long dtuicTenantId, ScheduleJob scheduleJob, ScheduleTaskShade taskShade) throws Exception {
        if (StringUtils.isBlank(db)) {
            return sqlText;
        }
        JSONObject sqlObject = JSONObject.parseObject(sqlText);
        JSONObject job = sqlObject.getJSONObject("job");
        JSONObject setting = job.getJSONObject("setting");

        if (setting.containsKey("dirty")) {

            if (!saveDirty) {
                setting.remove("dirty");
                return sqlObject.toJSONString();
            }

            JSONObject dirty = setting.getJSONObject("dirty");
            String tableName = dirty.getString("tableName");
            String path = null;

            if (StringUtils.isNotEmpty(tableName)) {
                //任务提交到task 之前 脏数据表 必须要在 ide 创建
                if (!tableName.contains(".")) {
                    tableName = String.format("%s.%s", db, tableName);
                }
                Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();

                String jobId = scheduleJob.getJobId();
                String taskType = getTaskType(scheduleJob);
                String alterSql = String.format(ADD_PART_TEMP, tableName, taskId, taskType, time);
                String location = "";
                Cluster cluster = clusterService.getCluster(dtuicTenantId);
                com.dtstack.engine.api.domain.Component metadataComponent = null;
                if (DataSourceType.IMPALA.getVal() == sourceType) {
                    metadataComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.IMPALA_SQL.getTypeCode(), null, null);
                    if (metadataComponent == null) {
                        throw new RdosDefineException(String.format("jobId:{}, table:{}, dirtyType:{} not found meta component", jobId, tableName, sourceType));
                    }
                    IClient client = ClientCache.getClient(DataSourceType.IMPALA.getVal());
                    JSONObject jsonObject = buildMetaJdbcInfo(sourceType, db, dtuicTenantId, taskShade, metadataComponent, cluster);
                    ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(jsonObject.toJSONString(), dtuicTenantId);
                    client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(alterSql).schema(db).build());
                    location = this.getTableLocation(client, sourceDTO, db, String.format("DESCRIBE formatted %s", tableName));
                } else if (DataSourceType.hadoopDirtyDataSource.contains(sourceType)) {
                    metadataComponent = componentService.getMetadataComponent(cluster.getId());
                    if (metadataComponent == null) {
                        metadataComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.SPARK_THRIFT.getTypeCode(), null, null);
                    }
                    if (metadataComponent == null) {
                        throw new RdosDefineException(String.format("jobId:{}, table:{}, dirtyType:{} not found meta component", jobId, tableName, sourceType));
                    }

                    IClient client = ClientCache.getClient(sourceType);
                    JSONObject jsonObject = buildMetaJdbcInfo(sourceType, db, dtuicTenantId, taskShade, metadataComponent, cluster);
                    ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(jsonObject.toJSONString(), dtuicTenantId);
                    client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(alterSql).schema(db).build());
                    location = this.getTableLocation(client, sourceDTO, db, String.format("desc formatted %s", tableName));
                }
                if (StringUtils.isBlank(location)) {
                    LOGGER.warn("jobId:{}, table:{}, replace dirty path is null, dirtyType {} ", jobId, tableName, sourceType);
                }
                String partName = String.format("task_name=%s_%s/time=%s", taskId,taskType, time);
                path = location + "/" + partName;

                dirty.put("path", path);
                setting.put("dirty", dirty);
                job.put("setting", setting);
                sqlObject.put("job", job);
            }
        }
        return sqlObject.toJSONString();
    }

    private String getTaskType(ScheduleJob scheduleJob) {
        Integer type = scheduleJob.getType();

        if (EScheduleType.NORMAL_SCHEDULE.getType().equals(type)) {
            return "scheduled_instance";
        }

        if (EScheduleType.FILL_DATA.getType().equals(type)) {
            return "temporary_instance";
        }

        if (EScheduleType.MANUAL.getType().equals(type)) {
            return "manual_instance";
        }

        if (EScheduleType.TEMP_JOB.getType().equals(type)) {
            return "test_instance";
        }

        return "";
    }

    /**
     * 创建分区
     */
    private String createPartition(ScheduleTaskShade taskShade, ScheduleJob scheduleJob, String job, Integer sourceType, Map<String, Object> actionParam) {
        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);

        if (Objects.isNull(userId)) {
            userId = taskShade.getOwnerUserId();
        }
        
        Long dtuicTenantId = taskShade.getDtuicTenantId();
        JSONObject jobJSON = JSONObject.parseObject(job);
        JSONObject jobObj = jobJSON.getJSONObject("job");
        JSONObject readerParameter = jobObj.getJSONArray("content").getJSONObject(0)
                .getJSONObject("reader").getJSONObject("parameter");
        if (readerParameter.containsKey("checkTableEmpty") && readerParameter.containsKey("connection")) {
            Boolean checkTableEmpty = readerParameter.getBoolean("checkTableEmpty");

            if (checkTableEmpty) {
                // 检查表是否是空的
                Long dtCenterSourceId = readerParameter.getLong("dtCenterSourceId");
                JSONObject connection = readerParameter.getJSONArray("connection").getJSONObject(0);
                String table = connection.getJSONArray("table").getString(0);
                String schema = connection.getString("schema");

                String partition = readerParameter.getString("partition");
                // 兼容 inceptor 范围分区
                boolean isRangePartition = PARTITION_TYPE_RANGE.equals(readerParameter.getInteger("partType"));
                Map<String, String> formattedMap = null;
                if (StringUtils.isNotBlank(partition)) {
                    Map<String, String> split = new HashMap<>();
                    if (isRangePartition) {
                        // 范围分区只有分区名称，举例: etl_date
                        split.put(partition, StringUtils.EMPTY);
                    }else if (StringUtils.countMatches(partition, "/") == 1 && StringUtils.countMatches(partition, "=") == 1) {
                        //pt=2020/04 分区中带/
                        String[] splits = partition.split("=");
                        split.put(splits[0], splits[1]);
                    } else {
                        //pt='asdfasd'/ds='1231231' 2级分区
                        split = Splitter.on("/").withKeyValueSeparator("=").split(partition);
                    }

                    formattedMap = Maps.newHashMap();
                    for (Map.Entry<String, String> entry : split.entrySet()) {
                        String value = entry.getValue();
                        String key = entry.getKey();
                        if (value.startsWith("'") || value.startsWith("\"")) {
                            value = value.substring(1);
                        }
                        if (value.endsWith("'") || value.endsWith("\"")) {
                            value = value.substring(0, value.length() - 1);
                        }
                        formattedMap.put(key, value);
                    }
                }


                ApiResponse<DsServiceInfoDTO> ds = dataSourceAPIClient.getDsInfoById(dtCenterSourceId);
                String jobId = scheduleJob.getJobId();
                if (ds == null || ds.getData() == null || org.apache.commons.lang3.StringUtils.isEmpty(ds.getData().getDataJson())) {
                    throw new RdosDefineException(String.format("jobId:%s can't find dataSource's dataJson, dtCenterSourceId:%s", jobId, dtCenterSourceId));
                }
                DsServiceInfoDTO dsInfo = ds.getData();
                Integer dataSourceType = dsInfo.getType();

                DtKerberosParam dtKerberosParam = DtKerberosParam.builder()
                        .projectId(taskShade.getProjectId())
                        .taskType(taskShade.getTaskType())
                        .appType(taskShade.getAppType())
                        .build();
                JSONObject pluginInfo = pluginInfoService.convertDsPluginInfo(dsInfo, schema, dtKerberosParam);

                if (GlobalConst.YES.equals(dsInfo.getIsMeta())) {
                    String engineType = com.dtstack.schedule.common.enums.DataSourceType.getEngineType(com.dtstack.schedule.common.enums.DataSourceType.getSourceType(dataSourceType));
                    EngineTypeComponentType type = EngineTypeComponentType.getByEngineName(engineType);
                    if (type != null) {
                        pluginInfoService.fillProxyConf(pluginInfo, dsInfo, type, userId);
                    }
                }

                ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dsInfo.getDtuicTenantId());
                IClient client = ClientCache.getClient(dataSourceType);
                try {
                    List<List<Object>> preview = client.getPreview(sourceDTO, SqlQueryDTO.builder().partitionColumns(formattedMap).tableName(table).build());

                    if (CollectionUtils.isEmpty(preview) || preview.size() <= 1) {
                        throw new RdosDefineException("任务已开启源表为空不运行的校验，源表"+table+"为空");
                    }

                } catch (Throwable e) {
                    LOGGER.error("table Check Empty, jobId:{}", jobId, e);
                    throw new RdosDefineException("jobId:"
                            + jobId + "," + ExceptionUtil.getErrorMessage(e));
                }
            }

        }

        JSONObject writerParameter = jobObj.getJSONArray("content").getJSONObject(0)
                .getJSONObject("writer").getJSONObject("parameter");
        if (writerParameter.containsKey("partition") && writerParameter.containsKey("connection")) {
            JSONObject connection = writerParameter.getJSONArray("connection").getJSONObject(0);
            String username = writerParameter.containsKey(ConfigConstant.USERNAME) ? writerParameter.getString(ConfigConstant.USERNAME) : "";
            String password = writerParameter.containsKey(ConfigConstant.PASSWORD) ? writerParameter.getString(ConfigConstant.PASSWORD) : "";
            String jdbcUrl = connection.getString(ConfigConstant.JDBCURL);
            String table = connection.getJSONArray("table").getString(0);

            String partition = writerParameter.getString("partition");
            // 兼容 inceptor 范围分区
            boolean isRangePartition = PARTITION_TYPE_RANGE.equals(writerParameter.getInteger("partType"));

            Map<String, String> split = new HashMap<>();
            if (isRangePartition) {
                // do nothing
            } else if (StringUtils.countMatches(partition, "/") == 1 && StringUtils.countMatches(partition, "=") == 1) {
                //(etl_date='2020-09-17'/etl_hour='23')
                //pt=2020/04 分区中带/
                String[] splits = partition.split("=");
                split.put(splits[0], splits[1]);
            } else {
                //pt='asdfasd'/ds='1231231' 2级分区
                split = Splitter.on("/").withKeyValueSeparator("=").split(partition);
            }
            Map<String, String> formattedMap = new HashMap<>();
            for (Map.Entry<String, String> entry : split.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                if (value.startsWith("'") || value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("'") || value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
                formattedMap.put(key, value);
            }

            String sql = null;
            if (isRangePartition) {
                String partitionRangeValue = writerParameter.getString("partitionRangeValue");
                if (StringUtils.isNotBlank(partitionRangeValue)) {
                    // 向导模式下 partitionRangeValue 可能为空
                    sql = String.format("alter table %s add if not exists partition (%s) values less than (%s)", table, partition, partitionRangeValue);
                }
            } else {
                // fileName  需要处理引号
                writerParameter.put("fileName", partition);
                String join = Joiner.on("',").withKeyValueSeparator("='").join(formattedMap);
                partition = join + "'";
                sql = String.format("alter table %s add if not exists partition (%s)", table, partition);
            }

            try {
                //临时运行重试 会导致页面接口超时
                Integer scheduleType = scheduleJob.getType();
                int retryNumber = EScheduleType.TEMP_JOB.getType().equals(scheduleType) ? 1 : environmentContext.getRetryFrequency();
                Long finalUserId = userId;
                String finalSql = sql;
                RetryUtil.executeWithRetry(() -> {
                    if (StringUtils.isNotBlank(finalSql)) {
                        LOGGER.info("jobId {} create partition  {}", scheduleJob.getJobId(), finalSql);
                        JSONObject pluginInfo = buildDataSourcePluginInfo(writerParameter.getJSONObject("hadoopConfig"), sourceType, username, password, jdbcUrl, writerParameter.getLong(UicConstant.DT_CENTER_SOURCE_ID), finalUserId, taskShade);
                        pluginInfo.put("dataSourceType", sourceType);
                        // 以脚本中的 schema 为主
                        String schema = writerParameter.getString("schema");
                        if (StringUtils.isBlank(schema)) {
                            schema = pluginInfo.getString(REAL_DATA_BASE);
                        }
                        pluginInfo.put("schema", schema);
                        IClient client = ClientCache.getClient(sourceType);
                        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtuicTenantId);
                        client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(finalSql).build());
                    }
                    cleanFileName(writerParameter);
                    return null;
                }, retryNumber, environmentContext.getRetryInterval(), false, null);
            } catch (Exception e) {
                LOGGER.error("create partition error:", e);
                throw new RdosDefineException("create partition error:" + ExceptionUtil.getErrorMessage(e));
            }
        }
        return jobJSON.toJSONString();
    }

    private String getTableLocation(IClient client,ISourceDTO sourceDTO, String dbName,String sql) throws Exception {
        String location = null;
        List<Map<String, Object>> result = client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(sql).schema(dbName).build());
        Iterator<Map<String, Object>> var6 = result.iterator();

        while(var6.hasNext()) {
            Map<String, Object> next = var6.next();
            List<Object> objects = Lists.newArrayList(next.values());
            if (objects.get(0).toString().contains("Location")) {
                location = objects.get(1).toString();
            }
        }

        return location;
    }

    private void cleanFileName(JSONObject parameter) {
        String jobPartition = parameter.getString("fileName").replaceAll("'", "").replaceAll("\"", "").replaceAll(" ", "");
        parameter.put("fileName", jobPartition);
    }

    /**
     * 替换数据同步中部分信息
     * @param actionParam
     * @param taskShade
     * @param scheduleJob
     * @param taskParamsToReplace
     * @param job
     * @return
     */
    private String replaceSyncJobString(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace, String job) {
        if (StringUtils.isBlank(job)) {
            throw new RdosDefineException("Data synchronization information cannot be empty");
        }

        // 替换数据同步任务的上游传递参数
        JobChainParamHandleResult jobChainParamHandleResult = jobChainParamHandler.handle(job, taskShade, taskParamsToReplace, scheduleJob);
        job = jobChainParamHandleResult.getSql();

        //替换系统参数
        job = jobParamReplace.paramReplace(job, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());

        //TODO 数据资产任务值为空 需要设置默认值
        Integer sourceType = (Integer) actionParam.getOrDefault("dataSourceType", DataSourceType.HIVE.getVal());
        //有可能 mysql-kudu 脏数据表是hive 用以区分数据同步目标表类型 还是脏数据表类型
        Integer dirtyDataSourceType = (Integer) actionParam.getOrDefault("dirtyDataSourceType", DataSourceType.HIVE.getVal());
        String engineIdentity = (String) actionParam.get("engineIdentity");

        // 获取脏数据存储路径
        try {
            job = this.replaceTablePath(true, job, taskShade.getTaskId(), dirtyDataSourceType, engineIdentity,taskShade.getDtuicTenantId(), scheduleJob, taskShade);
        } catch (Exception e) {
            LOGGER.error("create dirty table  partition error {}", scheduleJob.getJobId(), e);
        }

        try {
            // 创建数据同步目标表分区
            job = this.createPartition(taskShade, scheduleJob, job, sourceType, actionParam);
        } catch (Exception e) {
            LOGGER.error("create partition error {}", scheduleJob.getJobId(), e);
            throw e;
        }
        //多分区替换
        if (actionParam.containsKey(GlobalConst.MULTI_PARTITIONS)) {
            String multiPartition = String.valueOf(actionParam.get(GlobalConst.MULTI_PARTITIONS));
            if (BooleanUtils.toBoolean(multiPartition)) {
                Integer readDataSourceType = (Integer) actionParam.get("readDataSourceType");
                Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
                job = getMultiPartitionsURI(JSONObject.parseObject(job), readDataSourceType, taskShade.getDtuicTenantId(), scheduleJob.getJobId(), userId, taskShade);
            }
        }

        // 查找上一次同步位置
        if (EScheduleType.incrementalType.contains(scheduleJob.getType())) {
            job = getLastSyncLocation(taskShade.getTaskId(), job, scheduleJob.getCycTime(),taskShade.getDtuicTenantId(),taskShade.getAppType(),taskShade.getTaskParams(),
                    scheduleJob.getJobId(),scheduleJob.getType());
        } else {
            job = removeIncreConf(job);
        }
        return job;
    }

    private void check(String sqlText, Long dtuicTenantId) {
        JSONObject sqlObject = JSONObject.parseObject(sqlText);
        Boolean checkTableEmpty;
        try {
            checkTableEmpty = sqlObject.getJSONObject("job").getJSONArray("content").getJSONObject(0)
                    .getJSONObject("reader").getJSONObject("parameter").getBoolean("checkTableEmpty");
        } catch (Exception e) {
            return;
        }


    }

    private boolean isRestore(String job) {
        JSONObject jobJson = JSONObject.parseObject(job);
        Object isRestore = JSONPath.eval(jobJson, "$.job.setting.restore.isRestore");
        return BooleanUtils.toBoolean(String.valueOf(isRestore));
    }

    /**
     * 查找上一次同步位置 通过prometheus
     *
     * @return
     */
    private String getLastSyncLocation(Long taskId, String jobContent, String cycTime, Long dtuicTenantId, Integer appType, String taskparams, String jobId,Integer scheduleType) {
        JSONObject jsonJob = JSONObject.parseObject(jobContent);

        Timestamp time = new Timestamp(dayFormatterAll.parseDateTime(cycTime).toDate().getTime());
        // 查找上一次成功的job
        ScheduleJob job = scheduleJobDao.getByTaskIdAndStatusOrderByIdLimit(taskId, RdosTaskStatus.FINISHED.getStatus(), time, appType, scheduleType);
        if (job != null && StringUtils.isNotEmpty(job.getEngineJobId())) {
            try {
                JSONObject reader = (JSONObject) JSONPath.eval(jsonJob, "$.job.content[0].reader");
                String increCol = (String)(JSONPath.eval(reader, "$.parameter.increColumn"));
                if (StringUtils.isNotBlank(increCol) && null != job.getExecStartTime() && null != job.getExecEndTime()) {
                    String lastEndLocation = this.queryLastLocation(dtuicTenantId, job.getEngineJobId(), job.getExecStartTime().getTime(), job.getExecEndTime().getTime(), taskparams, job.getComputeType(), jobId);
                    LOGGER.info("job {} last job {} applicationId {} startTime {} endTime {} location {}", job, job.getJobId(), job.getEngineJobId(), job.getExecStartTime(), job.getExecEndTime(), lastEndLocation);
                    reader.getJSONObject("parameter").put("startLocation", lastEndLocation);
                }

            } catch (Exception e) {
                LOGGER.error("get sync job {} lastSyncLocation error ", job.getJobId(), e);
            }
        }

        return jsonJob.toJSONString();
    }
    /**
     * 获取flink任务checkpoint的存储路径
     *
     * @param dtuicTenantId 租户id
     * @return checkpoint存储路径
     */
    private String getSavepointPath(Long dtuicTenantId) {
        String clusterInfoStr = clusterService.clusterInfo(dtuicTenantId);
        JSONObject clusterJson = JSONObject.parseObject(clusterInfoStr);
        JSONObject flinkConf = clusterJson.getJSONObject(EComponentType.FLINK.getConfName());
        if (!flinkConf.containsKey(KEY_SAVEPOINT)) {
            return null;
        }

        String savepointPath = flinkConf.getString(KEY_SAVEPOINT);
        LOGGER.info("savepoint path:{}", savepointPath);

        if (StringUtils.isEmpty(savepointPath)) {
            throw new RdosDefineException("savepoint path can not be null");
        }

        return savepointPath;
    }

    private String getDefaultComponentVersion(Long dtUicTenantId,Integer componentCode) {
        Cluster cluster = clusterService.getCluster(dtUicTenantId);
        if (cluster != null) {
            com.dtstack.engine.api.domain.Component defaultComponent = componentService.getComponentByClusterId(cluster.getId(), componentCode, null);
            if (defaultComponent != null) {
                return defaultComponent.getHadoopVersion();
            }
        }
        return null;
    }

    private String removeIncreConf(String jobContent) {
        JSONObject jobJson = JSONObject.parseObject(jobContent);
        JSONPath.remove(jobJson, "$.job.content[0].reader.parameter.increColumn");
        JSONPath.remove(jobJson, "$.job.content[0].reader.parameter.startLocation");

        return jobJson.toJSONString();
    }

    private JSONObject buildSyncTaskExecArgs(String savepointPath, String taskParams) throws Exception {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(Charsets.UTF_8.name())));
        String interval = properties.getProperty(KEY_CHECKPOINT_INTERVAL, DEFAULT_VAL_CHECKPOINT_INTERVAL);

        JSONObject confProp = new JSONObject();
        confProp.put(KEY_CHECKPOINT_STATE_BACKEND, savepointPath);
        confProp.put(KEY_CHECKPOINT_INTERVAL, interval);
        return confProp;
    }

    /**
     * 拼接数据源的连接信息
     * hive 需要判断是否开启了kerberos
     * @param sourceType
     * @param username
     * @param password
     * @param jdbcUrl
     * @return
     */
    private JSONObject buildDataSourcePluginInfo(JSONObject hadoopConfig, Integer sourceType, String username, String password, String jdbcUrl, Long dtCenterSourceId, Long userId, ScheduleTaskShade taskShade) {
        JSONObject pluginInfo = new JSONObject();
        //解析jdbcUrl中的database,将数据库名称替换成default，防止数据库不存在报 NoSuchDatabaseException
        try {
            String jdbcUrlStr = jdbcUrl;
            if(jdbcUrl.contains(";")) {
                //是开启了kerbers的url
                jdbcUrlStr = jdbcUrl.substring(0,jdbcUrl.indexOf(";"));
            }
            String realDataBase = JdbcUrlUtil.getSchema(jdbcUrlStr);
            String newJdbcUrl = jdbcUrl.replaceFirst(realDataBase, "default");
            pluginInfo.put(REAL_DATA_BASE,realDataBase);
            pluginInfo.put(ConfigConstant.JDBCURL, newJdbcUrl);
        } catch (Exception e) {
            //替换database异常，则走原来逻辑
            pluginInfo.put(ConfigConstant.JDBCURL,jdbcUrl);
        }
        pluginInfo.put(ConfigConstant.USERNAME, username);
        pluginInfo.put(ConfigConstant.PASSWORD, password);
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, DataBaseType.getHiveTypeName(DataSourceType.getSourceType(sourceType)));
        // selfConf
        JSONObject selfConf = new JSONObject();
        selfConf.put(ConfigConstant.JDBCURL,jdbcUrl);
        selfConf.put(ConfigConstant.USERNAME, username);
        selfConf.put(ConfigConstant.PASSWORD, password);
        pluginInfo.put(PluginInfoConst.SELF_CONF, selfConf);
        if (null == hadoopConfig) {
            return pluginInfo;
        }

        // securityConf
        JSONObject securityConf = new JSONObject();

        // sslConf
        JSONObject sslClient = hadoopConfig.getJSONObject(ConfigConstant.SSL_CLIENT);
        JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.SSL_CONF, sslClient);

        boolean isMeta = false;
        if (dtCenterSourceId != null) {
            DsServiceInfoDTO data = dataSourceAPIClient.getDsInfoById(dtCenterSourceId).getData();
            if (data != null && GlobalConst.YES.equals(data.getIsMeta())) {
                isMeta = true;
            }
        }
        KerberosConfig kerberosConfig = null;
        JSONObject sftpConf = hadoopConfig.getJSONObject(EComponentType.SFTP.getConfName());

        KerberosReq kerberosReq = KerberosReq.builder()
                .taskType(taskShade.getTaskType())
                .projectId(taskShade.getProjectId())
                .appType(taskShade.getAppType())
                .userId(userId)
                .tenantId(taskShade.getDtuicTenantId())
                .hadoopConfig(hadoopConfig)
                .sftpConfig(sftpConf)
                .build();
        boolean replaceKerberos = isMeta && DataSourceType.needReplaceKerbersDataSource.contains(sourceType);
        if (replaceKerberos) {
            Pair<IKerberosAuthentication, KerberosConfig> kerberosConfigPair = syncKerberosChooser.authentication(kerberosReq);
            kerberosConfig = kerberosConfigPair.getValue();
        } else {
            IKerberosAuthentication syncPartitionKerberosAuthentication = syncKerberosChooser.getByName(SyncPartitionKerberosAuthentication.SYNC_PARTITION);
            kerberosConfig = syncPartitionKerberosAuthentication.getKerberosAuthenticationConfig(kerberosReq);
        }

        if (kerberosConfig != null) {
            if (null == sftpConf || sftpConf.size() <= 0) {
                throw new RdosDefineException("data synchronization hadoopConfig sftpConf field cannot be empty");
            }
            pluginInfo.put(EComponentType.SFTP.getConfName(), sftpConf);
            JSONObject kerberosJson = DtKerberosUtil.parse2Json(kerberosConfig);
            kerberosJson.put(EComponentType.YARN.getConfName(), hadoopConfig);
            JsonUtil.putIgnoreEmpty(securityConf, PluginInfoConst.KERBEROS_CONF, kerberosJson);
        }
        JsonUtil.putIgnoreNull(pluginInfo, PluginInfoConst.SECURITY_CONF, securityConf);

        // 填充 proxyConf
        if (dtCenterSourceId != null) {
            DsServiceInfoDTO data = dataSourceAPIClient.getDsInfoById(dtCenterSourceId).getData();
            if (data != null && GlobalConst.YES.equals(data.getIsMeta())) {
                Integer type = data.getType();
                DataSourceType dataSourceType = DataSourceType.getSourceType(type);
                String engineType = DataSourceType.getEngineType(dataSourceType);
                EngineTypeComponentType engineTypeComponentType = EngineTypeComponentType.getByEngineName(engineType);

                pluginInfoService.fillProxyConf(pluginInfo, data, engineTypeComponentType, userId);
                JSONObject dataJson = JSONObject.parseObject(data.getDataJson());

                // 填充 commonConf
                JSONObject commonConf = pluginInfoService.parseCommonConf(dataJson);
                JsonUtil.putIgnoreEmpty(pluginInfo, PluginInfoConst.COMMON_CONF, commonConf);
            }
        }
        return pluginInfo;
    }


    /**
     * 多分区数据同步 需要先获取符合sql的分区 在手动替换hdfs路径
     * @param syncJson
     * @param sourceType
     * @param dtuicTenantId
     * @param jobId
     * @return
     */
    public String getMultiPartitionsURI(JSONObject syncJson, Integer sourceType, Long dtuicTenantId, String jobId,Long userId, ScheduleTaskShade taskShade) {
        if (sourceType != null && !DataSourceType.multiPartitionDatasource.contains(sourceType)) {
            return syncJson.toJSONString();
        }

        JSONObject hadoopConfig = (JSONObject) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.hadoopConfig");
        String username = (String) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.username");
        String password = (String) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.password");
        String jdbcUrl = (String) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.connection[0].jdbcUrl");
        Integer datasourceId = (Integer) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.dtCenterSourceId");
        JSONObject pluginInfo = buildDataSourcePluginInfo(hadoopConfig, sourceType, username, password, jdbcUrl, datasourceId == null ? 0L : datasourceId.longValue(), userId, taskShade);
        pluginInfo.put("dataSourceType", sourceType);
        //以脚本中的 schema 为主
        String schema = (String) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.schema");
        String tableName = (String) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.table");
        //pt系统参数已经被替换过
        String ptSql = (String) JSONPath.eval(syncJson, "$.job.content[0].reader.parameter.partition");
        pluginInfo.put("schema", schema);
        IClient client = ClientCache.getClient(sourceType);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtuicTenantId);
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tableName).build();
        // 多分区已经默认排序
        List<ColumnMetaDTO> partitionColumn = client.getPartitionColumn(sourceDTO, sqlQueryDTO);
        if (CollectionUtils.isEmpty(partitionColumn)) {
            return syncJson.toJSONString();
        }

        Table table = client.getTable(sourceDTO, sqlQueryDTO);
        if (null == table) {
            return syncJson.toJSONString();
        }
        // 表路径
        String tablePath = table.getPath();
        if (StringUtils.isBlank(tablePath)) {
            return syncJson.toJSONString();
        }
        // 分区字段
        partitionColumn = partitionColumn.stream()
                .filter(p -> BooleanUtils.isTrue(p.getPart()))
                .collect(Collectors.toList());
        String partitionColumnSql = partitionColumn.stream().map(ColumnMetaDTO::getKey).collect(Collectors.joining(","));
        // 查询符合条件的分区 结果有重复的 需要去重
        String filterPartitionSQL = String.format("select distinct(%s) from %s where %s", partitionColumnSql, tableName, ptSql);
        sqlQueryDTO.setSql(filterPartitionSQL);
        List<Map<String, Object>> maps = client.executeQuery(sourceDTO, sqlQueryDTO);
        /*
         * +------+-------+--+
         * | day  | hour  |
         * +------+-------+--+
         * | 30   | 12    |
         * | 31   | 14    |
         * +------+-------+--+
         *
         */
        //
        // hdfs://ns1/dtInsight/hive/warehouse/what_are_you_talking_about.db/pt=2022,hdfs://ns1/dtInsight/hive/warehouse/what_are_you_talking_about.db/pt=2021
        String tablePartitionPaths = maps.stream().map(ptMap -> tablePath + "/" + Joiner.on("/").withKeyValueSeparator("=").join(ptMap))
                .collect(Collectors.joining(","));
        LOGGER.info("jobId {}:  partition sql [{}] query get paths is {}", jobId, ptSql, tablePartitionPaths);
        JSONPath.set(syncJson, "$.job.content[0].reader.parameter.path", tablePartitionPaths);
        JSONPath.set(syncJson, "$.job.content[0].reader.parameter.fileName", "");
        return syncJson.toJSONString();
    }

    /**
     * 最后替换 kerberos 信息，以便任务运行
     *
     * @param actionParam
     * @param taskShade
     * @param scheduleJob
     * @param originJob
     * @param flinkVersion
     * @return
     */
    private String replaceKerberosForJobRun(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, String originJob, String flinkVersion) {
        if (!AppType.RDOS.getType().equals(taskShade.getAppType())) {
            return originJob;
        }
        Long dtuicTenantId = taskShade.getDtuicTenantId();
        EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams(taskShade.getTaskParams(), scheduleJob.getComputeType(), EngineType.Flink.name(), dtuicTenantId);
        if (EDeployMode.PERJOB != eDeployMode) {
            return originJob;
        }

        Cluster cluster = clusterService.getCluster(dtuicTenantId);
        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        if (Objects.isNull(userId)) {
            userId = taskShade.getOwnerUserId();
        }
        Integer readDataSourceType = (Integer) actionParam.get("readDataSourceType");
        String processedJob = replaceSingleDataSourceKerberos(originJob, "reader", readDataSourceType, taskShade, flinkVersion, cluster, userId, dtuicTenantId);
        Integer writeDataSourceType = (Integer) actionParam.get("writeDataSourceType");
        String finalJob = replaceSingleDataSourceKerberos(processedJob, "writer", writeDataSourceType, taskShade, flinkVersion, cluster, userId, dtuicTenantId);
        boolean replaced = !originJob.equals(finalJob);
        if (replaced) {
            LOGGER.info("replaceKerberosForJobRun, jobId:{}, kerberos replaced", scheduleJob.getJobId());
        }
        return finalJob;
    }

    private String replaceSingleDataSourceKerberos(String originJob, String contentType, Integer dataSourceType, ScheduleTaskShade taskShade, String flinkVersion, Cluster cluster, Long userId, Long dtUicTenantId) {
        if (!DataSourceType.needReplaceKerbersDataSource.contains(dataSourceType)) {
            return originJob;
        }
        JSONObject originJobJson = JSONObject.parseObject(originJob);

        String dtCenterSourceIdStr = Objects.toString(JSONPath.eval(originJobJson, String.format("$.job.content[0].%s.parameter.dtCenterSourceId", contentType)), org.apache.commons.lang3.StringUtils.EMPTY);
        // 数据源 id
        Long dtCenterSourceId = org.apache.commons.lang3.StringUtils.isNotEmpty(dtCenterSourceIdStr) ? Long.valueOf(dtCenterSourceIdStr) : null;
        if (dtCenterSourceId == null) {
            return originJob;
        }
        ApiResponse<DsServiceInfoDTO> ds = dataSourceAPIClient.getDsInfoById(dtCenterSourceId);
        if (ds == null || ds.getData() == null || org.apache.commons.lang3.StringUtils.isEmpty(ds.getData().getDataJson())) {
            return originJob;
        }
        DsServiceInfoDTO dsInfo = ds.getData();
        Integer isMeta = dsInfo.getIsMeta();
        if (!GlobalConst.YES.equals(isMeta)) {
            return originJob;
        }
        JSONObject parameter = (JSONObject) JSONPath.eval(originJobJson, String.format("$.job.content[0].%s.parameter", contentType));
        JSONObject originKerberosConfig = parameter.getJSONObject("kerberosConfig");
        JSONObject originHadoopConfig = parameter.getJSONObject("hadoopConfig");
        boolean existsOriginHadoopConfig = originHadoopConfig != null;
        JSONObject originHadoopKerberosConfig = (existsOriginHadoopConfig ? originHadoopConfig.getJSONObject("kerberosConfig") : null);
        // 原 kerberos 信息不存在，则不替换
        if (originKerberosConfig == null && originHadoopKerberosConfig == null) {
            return originJob;
        }
        JSONObject sftpConf = (existsOriginHadoopConfig ? originHadoopConfig.getJSONObject(EComponentType.SFTP.getConfName()) : null);
        KerberosReq kerberosReq = KerberosReq.builder()
                .clusterId(cluster.getId())
                .componentCode(EComponentType.FLINK.getTypeCode())
                .componentVersion(flinkVersion)
                .taskType(taskShade.getTaskType())
                .projectId(taskShade.getProjectId())
                .appType(taskShade.getAppType())
                .userId(userId)
                .tenantId(dtUicTenantId)
                .sftpConfig(sftpConf)
                .build();
        Pair<IKerberosAuthentication, KerberosConfig> authentication = defaultKerberosChooser.authentication(kerberosReq);
        IKerberosAuthentication authenticationImpl = authentication.getKey();
        if (authenticationImpl == null) {
            return originJob;
        }
        // 结果仍然是集群级 kerberos，说明没有发生变化，无须替换
        if (ClusterKerberosAuthentication.CLUSTER.equals(authenticationImpl.name())) {
            return originJob;
        }
        KerberosConfig newKerberosConfig = authentication.getValue();
        if (newKerberosConfig == null) {
            return originJob;
        }
        DtKerberosUtil.replaceSyncJobKerberos(originKerberosConfig, newKerberosConfig);
        DtKerberosUtil.replaceSyncJobKerberos(originHadoopKerberosConfig, newKerberosConfig);
        DtKerberosUtil.replaceSyncJobKerberos(originHadoopConfig, newKerberosConfig);
        return originJobJson.toJSONString();
    }

    private JSONObject buildMetaJdbcInfo(Integer sourceType, String db, Long dtuicTenantId, ScheduleTaskShade taskShade,
                                         com.dtstack.engine.api.domain.Component metadataComponent,
                                         Cluster cluster) {
        String formatVersion = ComponentService.formatVersion(metadataComponent.getComponentTypeCode(), metadataComponent.getHadoopVersion());
        KerberosReq kerberosReq = KerberosReq.builder()
                .clusterId(metadataComponent.getClusterId())
                .componentCode(metadataComponent.getComponentTypeCode())
                .componentVersion(formatVersion)
                .taskType(taskShade.getTaskType())
                .projectId(taskShade.getProjectId())
                .appType(taskShade.getAppType())
                .tenantId(dtuicTenantId)
                .build();
        return pluginInfoService.buildJdbcInfo(metadataComponent, cluster, GlobalConst.YES, sourceType, db,
                dtuicTenantId, kerberosReq);
    }
}