package com.dtstack.engine.master.security;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.dtcenter.loader.exception.DtLoaderException;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.PreCheckSyncDTO;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.plugininfo.service.PluginInfoService;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-25 20:17
 */
@Component
public class DataSecurityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSecurityChecker.class);

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    private PluginInfoService pluginInfoService;

    @Autowired
    private TenantService tenantService;

    private static final String READER = "reader";
    private static final String WRITER = "writer";

    private static final String JOB_DT_CENTER_SOURCE_ID = "$.job.content[0].%s.parameter.dtCenterSourceId";
    private static final String JOB_SCHEMA = "$.job.content[0].%s.parameter.schema";

    private List<Integer> NEED_CHECK_DATA_SOURCE_TYPE;

    @Value("${need.check.datasource.type:}")
    private void setNeedCheckDataSourceType(List<Integer> needCheckDataSourceType){
        if (CollectionUtils.isNotEmpty(needCheckDataSourceType)) {
            NEED_CHECK_DATA_SOURCE_TYPE = Collections.unmodifiableList(needCheckDataSourceType);
        } else {
            NEED_CHECK_DATA_SOURCE_TYPE = ImmutableList.of(
                    // hive
                    DataSourceType.HIVE.getVal(), DataSourceType.HIVE1X.getVal(),
                    DataSourceType.HIVE3X.getVal(), DataSourceType.HIVE3_CDP.getVal(),
                    // trino
                    DataSourceType.TRINO.getVal());
        }
    }

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;

    /**
     * 针对数据同步任务，进行数据安全预校验
     * @param preCheckSyncDTO 离线传入的预校验 sql
     * @param jobJson 数据同步 job
     * @param taskShade
     * @param scheduleJob
     * @param taskParamsToReplace
     * @param userId
     * @param dtUicTenantId
     */
    public void preCheckSync(PreCheckSyncDTO preCheckSyncDTO, JSONObject jobJson, ScheduleTaskShade taskShade, ScheduleJob scheduleJob,
                             List<ScheduleTaskParamShade> taskParamsToReplace, Long userId, Long dtUicTenantId) {
        if (!AppType.RDOS.getType().equals(taskShade.getAppType())) {
            return;
        }
        if (preCheckSyncDTO == null) {
            return;
        }
        if (jobJson == null) {
            return;
        }
        // 是否开启了数栈的安全管控
        boolean hasRangerAndLdap = tenantService.hasRangerAndLdap(dtUicTenantId);
        if (!hasRangerAndLdap) {
            return;
        }
        String readerPreCheckSql = preCheckSyncDTO.getReader() != null ? preCheckSyncDTO.getReader().getSqlText() : StringUtils.EMPTY;
        String writerPreCheckSql = preCheckSyncDTO.getWriter() != null ? preCheckSyncDTO.getWriter().getSqlText() : StringUtils.EMPTY;
        // 校验顺序: reader --> writer
        Map<String, String> preCheckTypeToSql = new LinkedHashMap<>(2);
        preCheckTypeToSql.put(READER, readerPreCheckSql);
        preCheckTypeToSql.put(WRITER, writerPreCheckSql);

        for (Map.Entry<String, String> aPreCheckTypeToSql : preCheckTypeToSql.entrySet()) {
            // 预校验类型，reader or writer
            String preCheckType = aPreCheckTypeToSql.getKey();
            String dtCenterSourceIdStr = Objects.toString(JSONPath.eval(jobJson, String.format(JOB_DT_CENTER_SOURCE_ID, preCheckType)), StringUtils.EMPTY);
            // 数据源 id
            Long dtCenterSourceId = StringUtils.isNotEmpty(dtCenterSourceIdStr) ? Long.valueOf(dtCenterSourceIdStr) : null;
            // 当前操作的 schema
            String realSchema = (String)JSONPath.eval(jobJson, String.format(JOB_SCHEMA, preCheckType));
            // 预校验 sql
            String preCheckSql = aPreCheckTypeToSql.getValue();
            doPreCheck(preCheckType, dtCenterSourceId, preCheckSql, realSchema, scheduleJob, taskParamsToReplace, userId, taskShade);
        }
    }

    public void doPreCheck(String preCheckType, Long dtCenterSourceId, String preCheckSql, String realSchema,
                           ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace, Long userId, ScheduleTaskShade taskShade) {
        // 若不存在数据源 id，则不予校验
        if (dtCenterSourceId == null) {
            return;
        }
        ApiResponse<DsServiceInfoDTO> ds = dataSourceAPIClient.getDsInfoById(dtCenterSourceId);
        String jobId = scheduleJob.getJobId();
        if (ds == null || ds.getData() == null || StringUtils.isEmpty(ds.getData().getDataJson())) {
            throw new RdosDefineException(String.format("doPreCheck, preCheckType:%s, jobId:%s can't find dataSource's dataJson, dtCenterSourceId:%s",
                    preCheckType, jobId, dtCenterSourceId));
        }
        DsServiceInfoDTO dsInfo = ds.getData();
        Integer dataSourceType = dsInfo.getType();
        Integer isMeta = dsInfo.getIsMeta();
        LOGGER.info("doPreCheck start, preCheckType:{}, jobId:{}, preCheckSql:「{}」, dtCenterSourceId:{}, dataSourceType:{}, isMeta:{}, realSchema:{}",
                preCheckType, jobId, preCheckSql, dtCenterSourceId, dataSourceType, isMeta, realSchema);
        if (!NEED_CHECK_DATA_SOURCE_TYPE.contains(dataSourceType)) {
            return;
        }
        // 控制台的 Meta 数据源，才进行校验
        if (!GlobalConst.YES.equals(isMeta)) {
            return;
        }
        // 需要预校验，但是没传校验 sql
        if (StringUtils.isEmpty(preCheckSql)) {
            LOGGER.warn("doPreCheck warn, preCheckType:{}, preCheckSql is empty, jobId:{}", preCheckType, jobId);
            // throw new RdosDefineException(String.format("doPreCheck, jobId:%s preCheckSql is empty, dtCenterSourceId:%s, dataSourceType:%s", jobId, dtCenterSourceId, dataSourceType));
            return;
        }
        preCheckSql = jobParamReplace.paramReplace(preCheckSql, taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), scheduleJob.getProjectId());
        LOGGER.info("doPreCheck, preCheckType:{}, jobId:{}, after paramReplace, preCheckSql:「{}」", preCheckType, jobId, preCheckSql);

        DtKerberosParam dtKerberosParam = DtKerberosParam.builder()
                .projectId(taskShade.getProjectId())
                .taskType(taskShade.getTaskType())
                .appType(taskShade.getAppType())
                .build();
        JSONObject pluginInfo = pluginInfoService.convertDsPluginInfo(dsInfo, realSchema, dtKerberosParam);
        // 填充 ldapUser 信息
        String engineType = com.dtstack.schedule.common.enums.DataSourceType.getEngineType(com.dtstack.schedule.common.enums.DataSourceType.getSourceType(dataSourceType));
        EngineTypeComponentType type = EngineTypeComponentType.getByEngineName(engineType);
        if (type != null) {
            pluginInfoService.fillProxyConf(pluginInfo, dsInfo, type, userId);
        }

        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dsInfo.getDtuicTenantId());
        IClient client = ClientCache.getClient(dataSourceType);
        try {
            client.executeQuery(sourceDTO,
                    SqlQueryDTO.builder().sql(preCheckSql).schema(realSchema).build());
            LOGGER.info("doPreCheck ok, preCheckType:{}, jobId:{}", preCheckType, jobId);
        } catch (DtLoaderException dtLoaderException) {
            LOGGER.error("doPreCheck error, preCheckType:{}, jobId:{}", preCheckType, jobId, dtLoaderException);
            throw new RdosDefineException("doPreCheck error, preCheckType:" + preCheckType + ", jobId:"
                    + jobId + "," + ExceptionUtil.getErrorMessage(dtLoaderException));
        } catch (Throwable e) {
            LOGGER.error("doPreCheck error, preCheckType:{}, jobId:{}", preCheckType, jobId, e);
            throw new RdosDefineException("doPreCheck error, preCheckType:" + preCheckType + ", jobId:"
                    + jobId + "," + ExceptionUtil.getErrorMessage(e));
        }
    }
}