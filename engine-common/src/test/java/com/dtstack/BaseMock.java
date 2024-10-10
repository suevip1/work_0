package com.dtstack;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.logstore.AbstractLogStore;
import com.dtstack.engine.common.logstore.LogStoreFactory;
import com.dtstack.engine.common.logstore.mysql.MysqlLogStore;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseMock {

    @MockInvoke(targetClass = LogStoreFactory.class)
    public static synchronized AbstractLogStore getLogStore(Map<String, String> dbConfig) {
        return null;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getRdbTimeOut() {
        return String.valueOf(1000 * 60 * 10);
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getRedisMaster() {
        return "mymaster";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getConsoleFileSyncRootDirectory() {
        return "/dsdsadsdsdsd";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getPublicServiceNode() {
        return "127.0.0.1";
    }
    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getOpenRealJobGraph() {
        return false;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String dataClearConfig() {
        return "";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxRetryLogSize() {
        return 1;
    }
    @MockInvoke(targetClass = EnvironmentContext.class)
    String getHadoopUserName() {
        return "admin";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    Integer getMaxBatchTask() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getUpdateCacheRetry() {
        return 3;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getJobStoppedRetry() {
        return 6;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    Boolean getOpenErrorTop() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getTaskStatusDealerPoolSize() {
        return 10;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public long getJobStoppedDelay() {
        return 3000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public long getJobAcquireStopCycle() {
        return 3000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getSubmitQueueSize() {
        return 200;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getSubmitConsumerQueueMaxNum() {
        return 20;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getSubmitConsumerQueueMinNum() {
        return 20;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getTaskSyncPoolSize() {
        return 2;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public long getJobLogDelay() {
        return 30000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getNotFoundLimit() {
        return 300;

    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getNotFoundTimeLimit() {
        //  3 * 60 * 1000
        return 180000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getClearCheckpoint() {
        return 1000;
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public long getJobStatusCheckInterVal() {
        return 3500;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getBatchJobInsertSize() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getCheckJobMaxPriorityStrategy() {
        return Boolean.TRUE;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getFillDataThreadPoolCorePoolSize() {
        return 2;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getFillDataQueueCapacity() {
        return 100;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxFillDataThreadPoolSize() {
        return 20;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getComputeResourcePlain() {
        return "EngineTypeClusterQueueComputeType";
    }
    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getBatchJobJobInsertSize() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Long getAcquireQueueJobInterval() {
        return  3000L;
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getJobExecutorPoolCorePoolSize() {
        return 10;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getJobExecutorPoolMaximumPoolSize() {
        return 10;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Long getJobExecutorPoolKeepAliveTime() {
        return 1000L;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getJobExecutorPoolQueueSize() {
        return 1000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getQueueSize() {
        return 10;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getBuildJobErrorRetry() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getJobJobLevel() {
        return 20;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getJobJobSize() {
        return 30;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getRestartOperatorRecordMaxSize() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getListChildTaskLimit() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getFuzzyProjectByProjectAliasLimit() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getMaxBatchTaskSplInsert() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getMaxBatchTaskInsert() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getLocalAddress() {
        return "127.0.0.1:8090";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Long getTaskRuleTimeout() {
        return  600000L;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getOpenAdminKillPermission() {
        return false;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getDatasourceNode() {
        return "172.16.100.116:8077";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSdkToken() {
        return "sdk.token";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getWorkFlowLevel() {
        return 2;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Boolean getUseOptimize() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxExcelSize() {
        return 10;
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxCalenderSize() {
        return 20;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public List<String> getLdapSupportComponentType() {
        return Lists.newArrayList("1", "2");
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getRedisSentinel() {
        return "127.0.0.1:26379,127.0.0.1:26379,127.0.0.1:26379";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxIdle() {
        return 30;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxTotal() {
        return 50;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxWaitMills() {
        return 1000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getRedisUrl() {
        return "127.0.0.1";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getRedisPort() {
        return 6379;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getRedisPassword() {
        return "123";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getRedisDB() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getRedisTimeout() {
        return 3000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getRdosSessionExpired() {
        return 1800;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getJdbcUrl() {
        return "jdbc://";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getJdbcDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getJdbcUser() {
        return "admin";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getJdbcPassword() {
        return "123";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxPoolSize() {
        return 500;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMinPoolSize() {
        return 50;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getInitialPoolSize() {
        return 50;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getKeepAlive() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getMinEvictableIdleTimeMillis() {
        return 300000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getTimeBetweenEvictionRunsMillis() {
        return 60000;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getRemoveAbandonedTimeout() {
        return 120;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getTestWhileIdle() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getTestOnBorrow() {
        return false;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getTestOnReturn() {
        return false;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getPoolPreparedStatements() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return 20;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getMybatisMapperLocations() {
        return "classpath*:sqlmap/*-mapper.xml";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getMybatisConfigLocation() {
        return "classpath:mybatis-config.xml";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getRemoveAbandoned() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean openSlowSqlLog() {
        return false;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getNodeZkAddress() {
        return "172.16.20.195:2181,172.16.20.238:2181/dagschedule";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSecurity() {
        return "{}";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getKeytabPath() {
        return "/sds";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getKrb5Conf() {
        return "/ds";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getLoginContextName() {
        return "Client";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getLocalKerberosDir() {
        return System.getProperty("user.dir") + "/kerberosUploadTempDir";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getPrincipal() {
        return "principal";
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getElasticsearchAddress() {
        return "http://172.16.10.251:9200;http://172.16.10.251:9300";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getElasticsearchUsername() {
        return "admin";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getElasticsearchPassword() {
        return "admin";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getElasticsearchIndex() {
        return "index";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getRetryFrequency() {
        return 1;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getRetryInterval() {
        return 100;
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getElasticsearchFetchSize() {
        return "500";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getElasticsearchKeepAlive() {
        return "10";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getTestConnectTimeout() {
        return 100;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getFlinkXSdkPath() {
        return "/data/sftp/";
    }
    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getHdfsTaskPath() {
        return "/task";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getAdminSize() {
        return 5;
    }
    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSM2PublicKey(){
        return "04D4BDF1A660C728418D685A702C5E16EAA2463471BC23107CBDBBB4AD7AF526F88E89EBC7D3075D826F8323657858A351A709423B18A685CDAD141E671C32E8D4";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSM2PrivateKey(){
        return "0AC55910CF19346F35324577E7F3F0C544A7823B154B756D63160FD1167992B2";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean openDataClear() {
        return true;
    }

    public Integer getComponentRefreshAppType() {
        return AppType.RDOS.getType();
    }



    @MockInvoke(targetClass = PoolHttpClient.class)
    public static String get(String url) throws IOException {
        Map<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("success",true);
        objectObjectHashMap.put("data",true);
        return JSONObject.toJSONString(objectObjectHashMap);
    }


    @MockInvoke(targetClass = ApplicationContext.class)
    <T> T getBean(Class<T> interfaceClass) throws BeansException, InstantiationException, IllegalAccessException {
        if (interfaceClass.isInterface()) {
            return null;
        }
        return interfaceClass.newInstance();
    }
}
