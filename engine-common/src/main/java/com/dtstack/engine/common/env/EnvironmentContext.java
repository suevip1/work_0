package com.dtstack.engine.common.env;

import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author sishu.yss
 */
@Component
@PropertySource(value = "file:${user.dir.conf}/application.properties")
public class EnvironmentContext {

    @Autowired
    private Environment environment;

    /**
     * =========base=======
     */
    public String getSecurity() {
        return environment.getProperty("security");
    }

    public Long getAcquireQueueJobInterval() {
        return Long.parseLong(environment.getProperty("acquireQueueJobInterval", "3000"));
    }

    public Long getBaseJobInterval() {
        // 超时时间最小1分支 需要在一分钟scan到
        return Long.parseLong(environment.getProperty("baseJobInterval", "45"));
    }


    public Integer getCycTimeDayGap() {
        return Math.abs(Integer.parseInt(environment.getProperty("cycTimeDayGap", "1")));
    }

    public Integer getJobBuildImmediatelyDelayMinute() {
        return Math.abs(Integer.parseInt(environment.getProperty("job.build.immediately.delay.minute", "15")));
    }

    /**
     * =========jdbc=======
     */
    public String getJdbcDriverClassName() {
        return environment.getProperty("spring.datasource.driver-class-name");
    }

    public String getJdbcUrl() {
        return environment.getProperty("spring.datasource.url");
    }

    public String getJdbcPassword() {
        return environment.getProperty("spring.datasource.password");
    }

    public String getJdbcUser() {
        return environment.getProperty("spring.datasource.username");
    }

    public int getMaxPoolSize() {
        return Integer.parseInt(environment.getProperty("max.pool.size", "500"));
    }

    public int getMinPoolSize() {
        return Integer.parseInt(environment.getProperty("min.pool.size", "50"));
    }

    public int getInitialPoolSize() {
        return Integer.parseInt(environment.getProperty("initial.pool.size", "50"));
    }

    /**
     * =========mybatis=======
     */

    public String getMybatisMapperLocations() {
        return environment.getProperty("mybatis.mapper-locations", "classpath*:sqlmap/*-mapper.xml");
    }

    public String getMybatisConfigLocation() {
        return environment.getProperty("mybatis.config-location", "classpath:mybatis-config.xml");
    }

    /**
     * =========end=======
     */

    private volatile String httpAddress;

    public String getHttpAddress() {
        if (StringUtils.isNotBlank(httpAddress)) {
            return httpAddress;
        }

        httpAddress = environment.getProperty("http.address");

        if (StringUtils.isBlank(httpAddress)) {
            httpAddress = AddressUtil.getOneIp();
        }

        return httpAddress;
    }

    /**
     * =========redis=======
     */

    public int getRedisDB() {
        return Integer.parseInt(environment.getProperty("spring.redis.database", "1"));
    }

    public String getRedisUrl() {
        return environment.getProperty("spring.redis.host", "127.0.0.1");
    }

    public int getRedisPort() {
        return Integer.parseInt(environment.getProperty("spring.redis.port", "6379"));
    }

    public String getRedisPassword() {
        return environment.getProperty("spring.redis.password");
    }

    public String getRedisSentinel() {
        return environment.getProperty("spring.redis.sentinel.nodes", "");
    }

    public String getRedisMaster() {
        return environment.getProperty("redis.sentinel.master", "mymaster");
    }

    public int getMaxIdle() {
        return Integer.parseInt(environment.getProperty("spring.redis.max.idle","30"));
    }

    public int getMaxTotal() {
        return Integer.parseInt(environment.getProperty("spring.redis.max.total", "80"));
    }

    public int getMaxWaitMills() {
        return Integer.parseInt(environment.getProperty("spring.redis.max.wait..millis", "1200"));
    }

    public int getRedisTimeout() {
        return Integer.parseInt(environment.getProperty("spring.redis.timeout", "3000"));
    }


    public int getRdosSessionExpired() {
        return Integer.parseInt(environment.getProperty("web.session.expired", "1800"));
    }

    public boolean isOpenLdapCache(){
        return Boolean.parseBoolean(environment.getProperty("open.ldap.cache", "true"));
    }

    public Long getRedisTtlInHour() {
        return Long.parseLong(environment.getProperty("spring.redis.ttl.in.hour", "2"));
    }

    /**
     * ===es====
     */
    public String getElasticsearchAddress() {
        return environment.getProperty("es.address");
    }

    public String getElasticsearchUsername() {
        return environment.getProperty("es.username");
    }

    public String getElasticsearchPassword() {
        return environment.getProperty("es.password");
    }

    public String getElasticsearchIndex() {
        return environment.getProperty("es.index");
    }

    public String getElasticsearchFetchSize() {
        return environment.getProperty("es.fetchSize", "500");
    }

    public String getElasticsearchKeepAlive() {
        //  默认10分钟
        return environment.getProperty("es.scroll.keepAlive", "10");
    }


    /**
     * ===============hadoop ===============
     */

    public String getHadoopUserName() {
        return environment.getProperty("hadoop.user.name", "admin");
    }


    public String getJobGraphBuildCron() {
        return environment.getProperty("batch.job.graph.build.cron", "22:00:00");
    }

    public String getHdfsTaskPath() {
        return environment.getProperty("hdfs.task.path", "/dtInsight/task/");
    }

    private volatile String localAddress;

    public String getLocalAddress() {
        if (StringUtils.isNotBlank(localAddress)) {
            return localAddress;
        }

        String address = environment.getProperty("http.address");
        if (StringUtils.isBlank(address)) {
            address = AddressUtil.getOneIp();
        }
        String port = environment.getProperty("http.port", "8090");
        localAddress = String.format("%s:%s", address.trim(), port.trim());
        return localAddress;
    }

    public String getNodeZkAddress() {
        return environment.getProperty("nodeZkAddress");
    }

    public boolean isDebug() {
        return Boolean.parseBoolean(environment.getProperty("isDebug", "false"));
    }

    public int getQueueSize() {
        return Integer.parseInt(environment.getProperty("queueSize", "500"));
    }

    public int getJobStoppedRetry() {
        return Integer.parseInt(environment.getProperty("jobStoppedRetry", "6"));
    }

    public long getJobStoppedDelay() {
        return Integer.parseInt(environment.getProperty("jobStoppedDelay", "3000"));
    }

    public long getJobAcquireStopCycle() {
        return Integer.parseInt(environment.getProperty("jobAcquireStopCycle", "3000"));
    }

    /**
     * plain 1:cluster、2:cluster+queue
     *
     * @return
     */
    public String getComputeResourcePlain() {
        return environment.getProperty("computeResourcePlain", "EngineTypeClusterQueueComputeType");
    }

    public long getJobRestartDelay() {
        return Long.parseLong(environment.getProperty("jobRestartDelay", Integer.toString(2 * 60 * 1000)));
    }

    public long getJobLackingDelay() {
        return Long.parseLong(environment.getProperty("jobLackingDelay", Integer.toString(2 * 60 * 1000)));
    }

    public long getJobPriorityStep() {
        return Long.parseLong(environment.getProperty("jobPriorityStep", "10000"));
    }

    public long getJobLackingInterval() {
        String intervalObj = environment.getProperty("jobLackingInterval");
        if (StringUtils.isBlank(intervalObj)) {
            long interval = getJobLackingDelay() / getQueueSize();
            long defaultInterval = 3000L;
            if (interval < defaultInterval) {
                interval = defaultInterval;
            }
            return interval;
        }
        return Long.parseLong(intervalObj);
    }

    public int getJobLackingCountLimited() {
        return Integer.parseInt(environment.getProperty("jobLackingCountLimited", "3"));
    }

    public long getJobSubmitExpired() {
        return Long.parseLong(environment.getProperty("jobSubmitExpired", "0"));
    }

    public String getLocalKerberosDir() {
        return environment.getProperty("local.kerberos.dir", System.getProperty("user.dir") + "/kerberosUploadTempDir");
    }

    public String getLocalSSLDir() {
        return environment.getProperty("local.ssl.dir", System.getProperty("user.dir") + "/sslUploadTempDir");
    }

    public long getJobLogDelay() {
        return Integer.parseInt(environment.getProperty("jobLogDelay", "30000"));
    }

    public boolean getCheckJobMaxPriorityStrategy() {
        return Boolean.parseBoolean(environment.getProperty("checkJobMaxPriorityStrategy", "false"));
    }

    public int getMinTaskStatusDealerPoolSize() {
        return Integer.parseInt(environment.getProperty("taskMinStatusDealerPoolSize", "5"));
    }

    public int getTaskStatusDealerPoolSize() {
        return Integer.parseInt(environment.getProperty("taskStatusDealerPoolSize", "10"));
    }

    /**T
     * 空闲线程的最大留存时间
     *
     * @return 默认 2h 线程存活时间
     */
    public String getIdleThreadKeepAliveTimeInMilliSeconds() {
        return environment.getProperty("worker.proxy.idle.keep.alive.time", "7200000");
    }

    public int getLogPoolSize() {
        return Integer.parseInt(environment.getProperty("log.pool.size", "10"));
    }

    public int getFsyncPoolSize() {
        return Integer.parseInt(environment.getProperty("log.pool.size", "10"));
    }

    public int getLogTimeout() {
        return Integer.parseInt(environment.getProperty("logTimeout", "10"));
    }

    public int getTestConnectTimeout() {
        return Integer.parseInt(environment.getProperty("testConnectTimeout", "100"));
    }

    public int getBuildJobErrorRetry() {
        return Integer.parseInt(environment.getProperty("build.job.retry", "3"));
    }

    public int getUpdateCacheRetry() {
        return Integer.parseInt(environment.getProperty("update.cache.retry", "3"));
    }

    public int getJobSubmitConcurrent() {
        return Integer.parseInt(environment.getProperty("job.submit.concurrent", "1"));
    }

    public int getJobSubmitMaxThread() {
        return Integer.parseInt(environment.getProperty("job.submit.max.thread", "10"));
    }

    public float getJobSubmitIncrementFactor() {
        return Float.parseFloat(environment.getProperty("job.submit.increment.factor", "0.8"));
    }

    public float getJobSubmitDecrementFactor() {
        return Float.parseFloat(environment.getProperty("job.submit.decrement.factor", "0.6"));
    }

    public boolean getJobGraphBuilderSwitch() {
        return Boolean.parseBoolean(environment.getProperty("jobGraphBuilderSwitch", "false"));
    }

    public boolean getJobGraphWhiteList() {
        return Boolean.parseBoolean(environment.getProperty("jobGraphWhiteList", "false"));
    }

    public boolean openDataClear() {
        return Boolean.parseBoolean(environment.getProperty("data.clear", "true"));
    }

    /**
     * 历史数据保留时间配置，支持参数形如:[{"dictName":"schedule_job","dictValue":"{\\\"deleteDateConfig\\\":45,\\\"clearDateConfig\\\":15}"},{"dictName":"schedule_job_job","dictValue":"{\\\"deleteDateConfig\\\":45,\\\"clearDateConfig\\\":15}"},{"dictName":"schedule_job_expand","dictValue":"{\\\"deleteDateConfig\\\":45,\\\"clearDateConfig\\\":15}"}]
     * @return
     */
    public String dataClearConfig() {
        return environment.getProperty("data.clear.config", "");
    }

    public Integer getJobExecutorPoolCorePoolSize() {
        return Integer.valueOf(environment.getProperty("job.executor.pool.core.size", "10"));
    }

    public Integer getJobExecutorPoolMaximumPoolSize() {
        return Integer.valueOf(environment.getProperty("job.executor.pool.maximum.size", "10"));
    }

    public Long getJobExecutorPoolKeepAliveTime() {
        return Long.valueOf(environment.getProperty("job.executor.pool.keep.alive.time", "1000"));
    }

    public Integer getJobExecutorPoolQueueSize() {
        return Integer.valueOf(environment.getProperty("job.executor.pool.queue.size", "1000"));
    }

    public Boolean getOpenConsoleSftp() {
        return Boolean.parseBoolean(environment.getProperty("console.sftp.open", "true"));
    }

    public Integer getRetryFrequency() {
        return Integer.valueOf(environment.getProperty("retry.frequency", "3"));
    }

    public Integer getRetryInterval() {
        return Integer.valueOf(environment.getProperty("retry.interval", "30000"));
    }

    public long getJobStatusCheckInterVal() {
        return Long.parseLong(environment.getProperty("job.status.check.interval", "3500"));
    }

    public String getComponentJdbcToReplace() {
        return environment.getProperty("component.jdbc.replace", "/default");
    }

    public Integer getMaxBatchTask() {
        return Integer.parseInt(environment.getProperty("max.batch.task", "100"));
    }

    public Integer getMaxBatchTaskInsert() {
        return Integer.parseInt(environment.getProperty("max.batch.task.insert", "50"));
    }

    public Integer getMaxBatchTaskSplInsert() {
        return Integer.parseInt(environment.getProperty("max.batch.task.sql.insert", "10"));
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return Integer.valueOf(environment.getProperty("dataSource.min.evictable.idle.time.millis", "300000"));
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return Integer.valueOf(environment.getProperty("dataSource.time.between.eviction.runs.millis", "60000"));
    }

    /**
     * 控制工作流节点展开层数
     **/
    public Integer getWorkFlowLevel() {
        return Integer.valueOf(environment.getProperty("max.workFlow.level", "20"));
    }

    public Boolean getUseOptimize() {

        return Boolean.parseBoolean(environment.getProperty("engine.useOptimize", "true"));
    }

    public int getMaxDeepShow() {
        return Integer.parseInt(environment.getProperty("max.deep.show", "20"));
    }

    /**
     * 是否开启任务调度
     *
     * @return
     */
    public boolean openJobSchedule() {
        return Boolean.parseBoolean(environment.getProperty("job.schedule", "true"));
    }


    public long getForkJoinResultTimeOut() {
        return Long.parseLong(environment.getProperty("fork.join.timeout", Long.toString(60 * 5)));
    }

    public boolean getKeepAlive() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.keep.alive", "true"));
    }

    public boolean getRemoveAbandoned() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.remove.abandoned", "true"));
    }

    public Integer getRemoveAbandonedTimeout() {
        return Integer.valueOf(environment.getProperty("dataSource.remove.abandoned.timeout", "120"));
    }


    public boolean getTestWhileIdle() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.test.while.idle", "true"));
    }

    public boolean getTestOnBorrow() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.test.on.borrow", "false"));
    }

    public boolean getTestOnReturn() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.test.on.return", "false"));
    }

    public boolean getPoolPreparedStatements() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.pool.prepared.statements", "true"));
    }

    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return Integer.valueOf(environment.getProperty("dataSource.max.prepared.statement.per.connection.size", "20"));
    }

    /**控制任务展开层数**/
    public Integer getJobJobLevel(){
        return Integer.valueOf(environment.getProperty("max.jobJob.level","20"));
    }

    /**
     * 控制任务展开大小
     **/
    public Integer getJobJobSize() {
        return Integer.valueOf(environment.getProperty("max.jobJob.size", "3000"));
    }

    /**
     * 是否根据版本加载默认的配置
     *
     * @return
     */
    public boolean isCanAddExtraConfig() {
        return Boolean.parseBoolean(environment.getProperty("console.extra.config", "true"));
    }


    public Integer getFuzzyProjectByProjectAliasLimit() {
        return Integer.parseInt(environment.getProperty("fuzzy.project.alias.limit", "20"));
    }

    public Long getTaskTimeout() {
        return Long.parseLong(environment.getProperty("task.timeout", "600000"));
    }

    public Integer getListChildTaskLimit() {
        return Integer.parseInt(environment.getProperty("list.child.task.limit", "20"));
    }

    public String getPluginPath() {
        return environment.getProperty("enginePlugin.path",  System.getProperty("user.dir") + File.separator +"pluginLibs");
    }

    public int getMaxTenantSize() {
        return Integer.parseInt(environment.getProperty("max.tenant.size", "20"));
    }

    /**
     * 业务中心配置地址
     * @return
     */
    public String getPublicServiceNode() {
        return environment.getProperty("publicservice.node", "");
    }

    /**
     * SDK TOKEN
     *
     * @return
     */
    public String getSdkToken() {
        return environment.getProperty("sdk.token", "");
    }

    public String getSqlParserDir(){
        return environment.getProperty("sqlParserPlugin.path","/opt/dtstack/DTPlugin/SqlParser");
    }

    /**
     * 是否优先走standalone的组件
     *
     * @return
     */
    public boolean checkStandalone() {
        return Boolean.parseBoolean(environment.getProperty("check.standalone", "true"));
    }

    public Boolean getOpenErrorTop() {
        return Boolean.parseBoolean(environment.getProperty("open.error.top", "true"));
    }

    public int getBatchJobInsertSize() {
        return Integer.parseInt(environment.getProperty("batchJob.insert.size", "20"));
    }

    public int getBatchJobJobInsertSize() {
        return Integer.parseInt(environment.getProperty("batchJobJob.insert.size", "1000"));
    }

    public Integer getClearCheckpoint() {
        return Integer.parseInt(environment.getProperty("clear.checkpoint.size", "1000"));
    }

    public Integer getFillDataLimitSize() {
        return Integer.parseInt(environment.getProperty("fill.data.limit.size", "2000"));
    }

    public Integer getFillDataCreateLimitSize() {
        return Integer.parseInt(environment.getProperty("fill.data.create.limit.size", "1000"));
    }

    public int getFillDataThreadPoolCorePoolSize() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.core.pool.size", "2"));
    }

    public int getMaxFillDataThreadPoolSize() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.max.pool.size", "20"));
    }

    public int getFillDataQueueCapacity() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.queue.size", "100"));
    }

    public int getFillDataKeepAliveSeconds() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.keepAliveSeconds", "7200"));
    }

    public int getFillDataRootTaskMaxLevel() {
        return Integer.parseInt(environment.getProperty("fillData.max.level.size", "1000"));
    }

    public Integer getRestartOperatorRecordMaxSize() {
        return Integer.parseInt(environment.getProperty("restart.operator.record.max.size", "200"));
    }

    /**
     * 获取离线临时运行select语句临时表名前缀，如select_sql_temp_table
     **/
    public String getSelectTempKey() {
        return environment.getProperty("batch.temp.select.sql.prefix", "select_sql_temp_table_");
    }

    public int getAdminSize() {
        return Integer.parseInt(environment.getProperty("admin.size", "5"));
    }

    public String getRdbTimeOut() {
        return environment.getProperty("rdb.timeout", String.valueOf(1000 * 60 * 10));
    }

    public Boolean checkRefreshStatusName() {
        return Boolean.parseBoolean(environment.getProperty("refreshStatus.check.name", "true"));
    }

    public boolean openSlowSqlLog() {
        return Boolean.parseBoolean(environment.getProperty("open.slow.sql", "false"));
    }

    public long openSlowSqlTime() {
        return Long.parseLong(environment.getProperty("slow.sql.time", "10000"));
    }

    public Integer getClusterUseSaveInterval() {
        return Integer.parseInt(environment.getProperty("cluster.use.interval", "-3"));
    }

    public int getClusterResourcePoolSize() {
        return Integer.parseInt(environment.getProperty("cluster.resource.pool.size", "10"));
    }

    public Integer getDelayQueueSize() {
        String delayQueueSize = environment.getProperty("delayQueueSize");
        if (StringUtils.isBlank(delayQueueSize)) {
            String queueSize = environment.getProperty("queueSize", "500");
            return Integer.parseInt(queueSize) * 2;
        } else {
            return Integer.parseInt(delayQueueSize);
        }
    }

    public int getMaxRetryLogSize() {
        return Integer.parseInt(environment.getProperty("retry.max.size", "50"));
    }

    public Set<Integer> getLdapSupportComponentType() {
        Set<Integer> all = new HashSet<>();
        all.addAll(getLdapOnYarnSupportComponent());
        all.addAll(getLdapOnRdbSupportComponent());
        return all;
    }

    /**
     * 9 hive; 1 spark; 26 trino; 3 DtScript; 0 flink; 4 HDFS; 19 inceptor
     * @return
     */
    public Set<Integer> getLdapOnYarnSupportComponent() {
        String property = environment.getProperty("ldap.support.yarn.component", "1,3,0,4");
        if (StringUtils.isEmpty(property)) {
            return Collections.emptySet();
        }

        return Arrays.stream(StringUtils.split(property, GlobalConst.COMMA)).map(Integer::parseInt).collect(Collectors.toSet());
    }

    /**
     * 9 hive; 1 spark; 26 trino; 3 DtScript; 0 flink; 4 HDFS; 19 inceptor
     * @return
     */
    public Set<Integer> getLdapOnRdbSupportComponent() {
        String property = environment.getProperty("ldap.support.rdb.component", "9,26,19");
        if (StringUtils.isEmpty(property)) {
            return Collections.emptySet();
        }

        return Arrays.stream(StringUtils.split(property, GlobalConst.COMMA)).map(Integer::parseInt).collect(Collectors.toSet());
    }

    /**
     * 支持文件落盘的数据源枚举，
     * @see
     *
     * @return
     */
    public Set<Integer> getFsyncSupportDataSourceType() {
        String property = environment.getProperty("fsync.support.datasource.type", "36,59,29,52,21,2,31,1,1001,3,32,76,77,54,91,104,28,67,4,113");
        if (StringUtils.isEmpty(property)) {
            return Collections.emptySet();
        }
        return Arrays.stream(StringUtils.split(property, GlobalConst.COMMA)).map(Integer::valueOf).collect(Collectors.toSet());
    }

    /**
     * 支持租户级别组件配置的组件
     * @return componentTypeCode
     */
    public List<String> getSupportTenantConfigComponentType() {
        String property = environment.getProperty("support.tenant.config.component", "9");
        if (StringUtils.isEmpty(property)) {
            return Collections.emptyList();
        }

        return Arrays.stream(StringUtils.split(property, GlobalConst.COMMA)).collect(Collectors.toList());
    }

    public int getMaxBatchSize() {
        return Integer.parseInt(environment.getProperty("max.batch.size", "100"));
    }

    public String getKeytabPath() {
        return environment.getProperty("keytab.path", "");
    }

    public String getPrincipal() {
        return environment.getProperty("principal", "");
    }

    public String getKrb5Conf() {
        return environment.getProperty("krb5.conf", "");
    }

    public String getLoginContextName() {
        return environment.getProperty("zookeeper.login.context", "Client");
    }

    public String getEmailTmpPath() {
        return environment.getProperty("email.tmp.file.path", System.getProperty("user.dir") + "/tmpMailFile");
    }

    public Integer getSubmitQueueSize() {
        return Integer.parseInt(environment.getProperty(GlobalConst.SUBMIT_QUEUE_SIZE, "200"));
    }

    public Integer getSubmitConsumerQueueMaxNum() {
        return Integer.parseInt(environment.getProperty(GlobalConst.SUBMIT_CONSUMER_QUEUE_MAX_NUM, "20"));
    }

    public Integer getSubmitConsumerQueueMinNum() {
        return Integer.parseInt(environment.getProperty(GlobalConst.SUBMIT_CONSUMER_QUEUE_MIN_NUM, "10"));
    }

    public String getWorkerNodes() {
        return environment.getProperty("engine.worker.node", "");
    }

    public int getMaxExcelSize() {
        return Integer.parseInt(environment.getProperty("max.excel.size", "1000"));
    }

    public String getSM2PublicKey() {
        return environment.getProperty("sm2.publicKey");
    }

    public String getSM2PrivateKey() {
        return environment.getProperty("sm2.privateKey");
    }

    public int getMaxCalenderSize() {
        return Integer.parseInt(environment.getProperty("max.calender.size", "20"));
    }

    public int getMaxGlobalParamSize() {
        return Integer.parseInt(environment.getProperty("max.global.param.size", "100"));
    }

    public Integer getComponentRefreshAppType() {
        return Integer.parseInt(environment.getProperty("component.refresh.appType", "1"));
    }

    public boolean getOpenAdminKillPermission() {
        return Boolean.parseBoolean(environment.getProperty("engine.console.admin.kill.close", "false"));
    }

    public Integer getAlertTriggerRecordReceiveLimit() {
        return Integer.parseInt(environment.getProperty("alert.trigger.record.receive.limit", "50"));
    }

    public Integer getMaxBaselineJobNum() {
        return Integer.parseInt(environment.getProperty("max.baseline.line.job.num", "15"));
    }

    public Integer getMaxBaselineTaskNum() {
        return Integer.parseInt(environment.getProperty("max.baseline.line.task.num", "20"));
    }

    public int getNotFoundLimit() {
        return Integer.parseInt(environment.getProperty("not.found.limit", "300"));

    }

    public int getNotFoundTimeLimit() {
        //  3 * 60 * 1000
        return Integer.parseInt(environment.getProperty("not.found.limit.time", "180000"));
    }

    public String getFlinkXSdkPath() {
        if (StringUtils.isNotBlank(environment.getProperty("chunjun.sdk.path"))) {
            return environment.getProperty("chunjun.sdk.path", "");
        }
        return environment.getProperty("flinkx.sdk.path", "");
    }

    public String getSign() {
        return environment.getProperty("sign", "BatchWorks");
    }

    public boolean getBaselineOpen() {
        return Boolean.parseBoolean(environment.getProperty("baseline.open", "true"));
    }

    public String getTimeOutOfClient() {
        return environment.getProperty("akka.worker.timeout", "300000");
    }

    public String getQueueSizeOfClient() {
        return environment.getProperty("worker.proxy.queue.size", "800");
    }

    public String getMinPoolSizeOfClient() {
        return environment.getProperty("worker.proxy.min.pool.size", "1");
    }

    public String getMaxPoolSizeOfClient() {
        return environment.getProperty("worker.proxy.max.pool.size", "10");
    }

    public boolean getAlertOpen() {
        return Boolean.parseBoolean(environment.getProperty("alert.open", "true"));
    }

    public String getConsoleFileSyncRootDirectory() {
        return environment.getProperty("console.file.sync.root.directory",  System.getProperty("user.dir") + File.separator +"consoleFileSyncRootDir");
    }

    public long getSdkTimeout() {
        return Long.parseLong(environment.getProperty("sdk.timeout", "2000"));
    }

    public String getAllSetStrategy() {
        return environment.getProperty("engine.fill.all.set.strategy", "stack");
    }

    public String getRestartKey() {
        return environment.getProperty("engine.restart.job.key", "engine_restart_job_2022");
    }

    public Integer getRestartCount() {
        return Integer.parseInt(environment.getProperty("engine.restart.job.count", "1000"));
    }

    public int getRestartKeyTimeOut() {
        return Integer.parseInt(environment.getProperty("engine.restart.job.key.timeout", "10"));
    }

    public boolean getOpenRealJobGraph() {
        return Boolean.parseBoolean(environment.getProperty("open.real.job.graph", "false"));

    }

    public boolean getOpenCycleParam() {
        return Boolean.parseBoolean(environment.getProperty("open.cycle.param", "false"));
    }

    public List<Integer> getSkipTestComponentType() {
        return Arrays.stream(environment.getProperty("skip.test.component.type", "")
                        .split(",", -1))
                .filter(StringUtils::isNotBlank)
                .map(type -> Integer.parseInt(type.trim()))
                .collect(Collectors.toList());
    }

    public Integer getRunNumSize() {
        return Integer.parseInt(environment.getProperty("run.num.size", "3"));
    }

    public Integer getRdbMaxPreviewNum() {
        return Integer.parseInt(environment.getProperty("engine.rdb.max.preview.num", "1000"));
    }

    /**
     * 任务上下游-输出参数阈值，默认 2 MB(2097152)
     * @return
     */
    public Integer getJobChainOutputParamLimitSize() {
        return Integer.parseInt(environment.getProperty("jobChain.outputParam.limitSize", "2097152"));
    }

    public Integer getReserveDay() {
        return Integer.parseInt(environment.getProperty("engine.reserve.day","2"));
    }

    public Long getMaxReserveNum() {
        return Long.parseLong(environment.getProperty("engine.reserve.num","50"));
    }

    public int getGraphAlertHours() {
        return Integer.parseInt(environment.getProperty("graph.alert.hours", "1"));
    }

    public Integer getKeepOperatorTimeByDay() {
        return Integer.parseInt(environment.getProperty("engine.keep.operator.time.day","2"));
    }

    public Integer getFillJobExecutorJobLimit() {
        return Integer.parseInt(environment.getProperty("engine.fill.job.executor.limit","500"));
    }

    public Integer getRestartJobExecutorJobLimit() {
        return Integer.parseInt(environment.getProperty("engine.restart.job.executor.limit","500"));
    }

    public Integer getJobResourceMonitorPoolCoreSize() {
        return Integer.valueOf(environment.getProperty("job.resource.monitor.pool.core.size", "10"));
    }

    public Integer getJobResourceMonitorPoolMaxSize() {
        return Integer.valueOf(environment.getProperty("job.resource.monitor.pool.max.size", "20"));
    }

    public Integer getJobResourceMonitorTimeout() {
        return Integer.valueOf(environment.getProperty("job.resource.monitor.timeout", "90"));
    }

    public Integer getJobResourceMonitorPeriod() {
        return Integer.valueOf(environment.getProperty("job.resource.monitor.period", "5"));
    }

    public String getTrinoDbJdbc(String name) {
        return environment.getProperty(String.format("engine.trino.%s.jdbc",name));
    }

    public String getTrinoDbUserName(String name) {
        return environment.getProperty(String.format("engine.trino.%s.username",name));
    }

    public String getTrinoDbPassword(String name) {
        return environment.getProperty(String.format("engine.trino.%s.password",name));
    }

    public boolean openNewSummitCheck() {
        return Boolean.parseBoolean(environment.getProperty("engine.new.summit.open", "true"));
    }

    public boolean getOpenFastCheckStatus() {
        return Boolean.parseBoolean(environment.getProperty("engine.open.fast.check.status","false"));
    }

    public String getConfigHttpAddress() {
        return environment.getProperty("http.address");
    }

    public String getConfigHttpPort() {
        return environment.getProperty("http.port", "8090");
    }

    public String getAgentJobArchiveTmpDir() {
        return environment.getProperty("agent.job.archive.tmp.dir",System.getProperty("user.dir") + "/tmp/agentJobArchive");
    }

    public boolean openAgentJobArchiveDebug(){
        return Boolean.parseBoolean(environment.getProperty("open.agent.job.archive.debug", "false"));
    }

    public String getAgentJobPython2Path(){
        String agentJobPython2Path = environment.getProperty("agent.job.python2.path", "/usr/bin/python");
        if (StringUtils.isBlank(agentJobPython2Path)) {
            return "/usr/bin/python";
        }
        return agentJobPython2Path;
    }

    public String getAgentJobPython3Path(){
        String agentJobPython3Path = environment.getProperty("agent.job.python3.path", "/data/anaconda3/bin/python3");
        if (StringUtils.isBlank(agentJobPython3Path)) {
            return "/data/anaconda3/bin/python3";
        }
        return agentJobPython3Path;
    }


    public String getAgentJobArchiveDownloadUrl() {
        return environment.getProperty("agent.job.archive.download.url", "");
    }

    public boolean openStrongPriority() {
        return Boolean.parseBoolean(environment.getProperty("engine.open.strong.priority", "true"));
    }

    public Long flushedAliveNodeAddress() {
        return Long.parseLong(environment.getProperty("engine.flushed.alive.node.address.time", "1"));
    }


    /**
     * 拉取 cache 表的记录到内存 shard 的时间间隔，单位:分钟
     * @return
     */
    public Long getFetchJobCacheIntoShardInterval() {
        return Long.parseLong(environment.getProperty("fetch.jobCache.into.shard.interval", "5"));
    }

    public int getAsyncDealStopJobPoolMaxSize() {
        return Integer.parseInt(environment.getProperty("engine.async.deal.stop.job.pool.max.size","40"));
    }

    public int getAsyncDealStopJobPoolMinSize() {
        return Integer.parseInt(environment.getProperty("engine.async.deal.stop.job.pool.min.size","20"));
    }

    public int getAsyncDealStopJobQueueSize() {
        return Integer.parseInt(environment.getProperty("engine.async.deal.stop.job.queue.size","1"));
    }

    public int getWaitInterval() {
        return Integer.parseInt(environment.getProperty("engine.job.priority.wait.interval","3000"));
    }

    public int getCreatePriorityTime() {
        return Integer.parseInt(environment.getProperty("engine.job.priority.create.interval","3000"));
    }

    public long getTempJobStatusCheckInterVal() {
        return Long.parseLong(environment.getProperty("temp.job.status.check.interval", "1000"));
    }

    public boolean openCheckRelyCheck() {
        return Boolean.parseBoolean(environment.getProperty("open.check.rely.check", "true"));
    }

    public String getElasticJobEngineType() {
        return environment.getProperty("engine.elastic.engine.type", "flink");
    }

    public boolean openAuth() {
        return  Boolean.parseBoolean(environment.getProperty("open.auth", "true"));
    }

    public long stoppedCheckpointCacheExpireTime() {
        return Long.parseLong(environment.getProperty("stopped.checkpoint.cache.expire.time", "60"));
    }

    public long stoppedCheckpointCacheSize() {
        return Long.parseLong(environment.getProperty("stopped.checkpoint.cache.size", "1000"));
    }

    public boolean getOpenSafeMode() {
        return Boolean.parseBoolean(environment.getProperty("engine.open.fastjson.safeMode", "false"));
    }

    public Integer getStopFillPageSize() {
        return Integer.parseInt(environment.getProperty("engine.stop.fill.page.size", "1000"));
    }

    public int getSingleExecutorThreadNum() {
        return Integer.parseInt(environment.getProperty("single.executor.thread.num", "10"));
    }

    public Integer getCandlerTimeInterval() {
        return Integer.parseInt(environment.getProperty("task.candler.time.minute.interval", "5"));
    }

    public boolean getOpenSaveRunSqlText() {
        return Boolean.parseBoolean(environment.getProperty("engine.save.run.sql.text", "true"));
    }

    public boolean getOpenFillLimitation() {
        return Boolean.parseBoolean(environment.getProperty("open.fill.limitation","true"));
    }

    public boolean openDummy() {
        return Boolean.parseBoolean(environment.getProperty("engine.open.dummy","false"));
    }

    public int getFlinkFileSaveDays() {
        return Integer.parseInt(environment.getProperty("flink.file.save.day","90"));
    }

    public Integer getStatusBlackTimeConf() {
        return Integer.parseInt(environment.getProperty("status.black.time","10"));
    }
}

