package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.dto.IdRangeDTO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.cron.constant.DataLifeTable;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamCleaner;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-07-01
 */
@Component
public class DataClearSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataClearSchedule.class);
    public static final String clearDateConfig = "clearDateConfig";
    public static final String deleteDateConfig = "deleteDateConfig";
    public static final String appendWhere = "appendWhere";
    public static final String increment = "increment";
    private static final String directDelete = "directDelete";
    /**
     * 批任务，排除实时任务
     */
    private static final String IS_BATCH_COMPUTE_TYPE = " compute_type = 1 and app_type != 7 ";

    private static final String JOB_ID_NOT_IN = " job_id not in (%s) ";
    public static final String SCHEDULE_JOB = "schedule_job";
    private static final String[] MALICIOUS_SQL = {"select","exec", "insert", "delete", "update",
            "drop", "truncate", "declare", "master", "execute"};
    private static final String UPDATE_CONDITION_TEMPLATE = " where is_deleted != 2 and id >= %s and id <= %s and gmt_modified < %s ";
    private static final String UPDATE_EXTRA_CONDITION_TEMPLATE = UPDATE_CONDITION_TEMPLATE + " and (%s) ";

    private static final String DELETE_CONDITION_TEMPLATE = "  where is_deleted = 2 and id >= %s and id <= %s and gmt_modified < %s ";
    private static final String DELETE_EXTRA_CONDITION_TEMPLATE = DELETE_CONDITION_TEMPLATE + " and (%s) ";

    @Autowired
    private EnvironmentContext environment;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ScheduleDictDao scheduleDictDao;
    @Autowired
    private JobChainParamCleaner jobChainParamCleaner;
    @Resource
    private ThreadPoolTaskExecutor commonExecutor;

    @Autowired
    private ScheduleJobHistoryService scheduleJobHistoryService;

    @EngineCron
    @Scheduled(cron = "${data.clear.cron:0 0 23 * * ? }")
    public void handle() {
        LOGGER.info("{} do handle", this.getClass().getSimpleName());
        this.process();
    }

    public void process() {
        if (!environment.openDataClear()) {
            return;
        }
        List<ScheduleDict> dictsInDb = scheduleDictDao.listDictByType(DictType.DATA_CLEAR_NAME.type);
        String dictStrInProp = environment.dataClearConfig();
        List<ScheduleDict> finalDicts = overrideWithPropConfig(dictsInDb, dictStrInProp);
        LOGGER.info("finalDicts:{}", JSON.toJSONString(finalDicts));
        if (CollectionUtils.isEmpty(finalDicts)) {
            LOGGER.info("data clear table is empty");
            return;
        }
        CompletableFuture.runAsync(() -> dataClear(finalDicts), commonExecutor);
        // 由于内部需加载 hdfs 插件，需指定线程池，否则类加载异常
        ScheduleDict jobOutputParamScheduleDict = finalDicts.stream()
                .filter(p -> JobChainParamCleaner.SCHEDULE_JOB_CHAIN_OUTPUT_PARAM.equals(p.getDictName()))
                .findFirst().orElse(null);
        CompletableFuture.runAsync(() -> jobChainParamCleaner.cleanUpCycAndPatchJobOutputParams(jobOutputParamScheduleDict), commonExecutor);
    }

    private void dataClear(List<ScheduleDict> scheduleDicts) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (ScheduleDict scheduleDict : scheduleDicts) {
                String tableName = StringUtils.trim(scheduleDict.getDictName());
                String tableConfig = StringUtils.trim(scheduleDict.getDictValue());
                if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(tableConfig)) {
                    continue;
                }
                try {
                    JSONObject clearConfig = JSONObject.parseObject(tableConfig);
                    String sqlAppendWhere = (String) clearConfig.getOrDefault(appendWhere, "");
                    sqlAppendWhere = sqlAppendWhere.trim();
                    boolean existsMaliciousSql = existsMaliciousSql(sqlAppendWhere);
                    if (existsMaliciousSql) {
                        LOGGER.warn("appendWhere may contains malicious sql:{}, skip over", sqlAppendWhere);
                        continue;
                    }
                    if (DataLifeTable.SCHEDULE_JOB.equalsIgnoreCase(tableName)) {
                        // schedule_job 需限制为批处理任务
                        sqlAppendWhere += IS_BATCH_COMPUTE_TYPE;
                    }
                    else if (DataLifeTable.SCHEDULE_JOB_EXPAND.equalsIgnoreCase(tableName)){
                        // 上一步未处理stream任务，这里处理schedule_expand时，要避免处理。
                        List<String> streamJobIds = scheduleJobHistoryService.getJobIdsByAppType(AppType.STREAM.getType());
                        if (CollectionUtils.isNotEmpty(streamJobIds)){
                            String jobIds = StringUtils.join(streamJobIds, ",");
                            sqlAppendWhere += String.format(JOB_ID_NOT_IN,jobIds);
                        }
                    }
                    if (JobChainParamCleaner.SCHEDULE_JOB_CHAIN_OUTPUT_PARAM.equalsIgnoreCase(tableName)) {
                        // 在 JobChainParamCleaner 中处理
                        continue;
                    }

                    // 校验表名是否存在，不存在则不予处理
                    boolean existsTable = existsTable(tableName, statement);
                    if (!existsTable) {
                        LOGGER.info("table:{} not exists, skip over", tableName);
                        continue;
                    }
                    IdRangeDTO clearIdRange = getClearIdRange(tableName, statement);
                    if (clearIdRange == null) {
                        continue;
                    }
                    long startClearId = clearIdRange.getStartId();
                    long endClearId = clearIdRange.getEndId();

                    // 单位 day
                    Integer clearDate = (Integer) clearConfig.getOrDefault(clearDateConfig, 366);
                    // 每条sql执行范围
                    Integer idIncrement = (Integer) clearConfig.getOrDefault(increment, 500);
                    boolean directDeleteData = (boolean) clearConfig.getOrDefault(directDelete, false);
                    if (!directDeleteData) {
                        DateTime clearTime = DateTime.now().plusDays(-clearDate);
                        //1. 标记
                        for (long i = startClearId; i <= endClearId; i = i + idIncrement) {
                            String updateWhereSql = StringUtils.isEmpty(sqlAppendWhere) ? UPDATE_CONDITION_TEMPLATE : UPDATE_EXTRA_CONDITION_TEMPLATE;
                            String whereSql = String.format(updateWhereSql,
                                    i, i + idIncrement, clearTime.toString(DateUtil.UN_STANDARD_DATETIME_FORMAT), sqlAppendWhere);
                            String countSql = String.format("select count(*) from %s", tableName) + whereSql;
                            long count = queryCount(countSql, statement);
                            if (count <= 0) {
                                continue;
                            }
                            // 限制 gmt_modified = gmt_modified, 防止因执行本语句造成 gmt_modified 被修改
                            String updateSql = String.format("update %s set is_deleted = 2, gmt_modified = gmt_modified", tableName) + whereSql;
                            int size = statement.executeUpdate(updateSql);
                            LOGGER.info("DataClearSchedule update sql [{}] size [{}]", updateSql, size);
                        }
                    }

                    //2. 清理
                    Integer deleteDate = (Integer) clearConfig.getOrDefault(deleteDateConfig, 30);
                    DateTime deleteTime = DateTime.now().plusDays(-clearDate).plusDays(-deleteDate);
                    for (long i = startClearId; i <= endClearId; i = i + idIncrement) {
                        String deleteWhereSql = StringUtils.isEmpty(sqlAppendWhere) ? DELETE_CONDITION_TEMPLATE : DELETE_EXTRA_CONDITION_TEMPLATE;
                        String whereSql = String.format(deleteWhereSql,
                                i, i + idIncrement, deleteTime.toString(DateUtil.UN_STANDARD_DATETIME_FORMAT), sqlAppendWhere);
                        String countSql = String.format("select count(*) from %s", tableName) + whereSql;
                        long count = queryCount(countSql, statement);
                        if (count <= 0) {
                            continue;
                        }
                        String deleteSql = String.format("delete from %s", tableName) + whereSql;
                        int clearSize = statement.executeUpdate(deleteSql);
                        LOGGER.info("DataClearSchedule delete sql [{}] clear size [{}]", deleteSql, clearSize);
                    }
                } catch (Exception exception) {
                    LOGGER.error("data clear table :{} process error ", tableName, exception);
                }
            }
        } catch (Exception exception) {
            LOGGER.error("data clear process error ", exception);
        }
    }

    /**
     * 是否含有恶意 sql
     * @param sqlAppendWhere
     * @return
     */
    public static boolean existsMaliciousSql(String sqlAppendWhere) {
        if (StringUtils.isEmpty(sqlAppendWhere)) {
            return false;
        }
        sqlAppendWhere = sqlAppendWhere.toLowerCase();
        for (String maliciousSql : MALICIOUS_SQL) {
            if (StringUtils.contains(sqlAppendWhere, maliciousSql)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 表是否存在
     *
     * @param tableName
     * @param statement
     * @return
     * @throws SQLException
     */
    private boolean existsTable(String tableName, Statement statement) throws SQLException {
        String showTableSql = "show tables like '%s'";
        String tableNameFromDb;
        try (ResultSet resultSet = statement.executeQuery(String.format(showTableSql, tableName))) {
            while (resultSet.next()) {
                tableNameFromDb = resultSet.getString(1);
                return StringUtils.isNotEmpty(tableNameFromDb);
            }
        }
        return false;
    }

    private IdRangeDTO getClearIdRange(String tableName, Statement statement) throws SQLException {
        String sql = "select min(id), max(id) from %s";
        try (ResultSet resultSet = statement.executeQuery(String.format(sql, tableName))) {
            while (resultSet.next()) {
                IdRangeDTO clearIdRange = new IdRangeDTO(resultSet.getLong(1), resultSet.getLong(2));
                LOGGER.info("DataClearSchedule table:{}, clearIdRange:{}", tableName, clearIdRange);
                return clearIdRange;
            }
        }
        return null;
    }

    private long queryCount(String sql, Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        }
        return 0L;
    }

    /**
     * 配置文件优先级高于 DB 配置
     * @param dictsInDb DB 配置
     * @param dictStrInProp 配置文件
     * @return
     */
    private List<ScheduleDict> overrideWithPropConfig(List<ScheduleDict> dictsInDb, String dictStrInProp) {
        if (StringUtils.isEmpty(dictStrInProp)) {
            return dictsInDb;
        }

        List<ScheduleDict> dictsInProp = null;
        try {
            // 容错
            dictsInProp = JSONArray.parseArray(dictStrInProp, ScheduleDict.class);
        } catch (Exception e) {
            LOGGER.error("parse dictsInProp error", e);
        }
        if (CollectionUtils.isEmpty(dictsInProp)) {
            return dictsInDb;
        }
        Map<String, ScheduleDict> dictName2ValueInDb = dictsInDb.stream().collect(Collectors.toMap(ScheduleDict::getDictName, Function.identity(), (x, y) -> y));
        // 用配置文件覆盖 DB 配置
        for (ScheduleDict dictInProp : dictsInProp) {
            dictName2ValueInDb.put(dictInProp.getDictName(), dictInProp);
        }
        return new ArrayList<>(dictName2ValueInDb.values());
    }
}
