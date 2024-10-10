package com.dtstack.engine.master.mockcontainer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.router.cache.SessionCache;
import com.dtstack.engine.master.router.login.LoginSessionStore;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import com.dtstack.engine.master.router.util.CookieUtil;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.master.zookeeper.data.BrokerHeartNode;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BaseMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getComponentJdbcToReplace() {
        return "/default";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getRedisMaster() {
        return "mymaster";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSign() {
        return "engine";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getConsoleFileSyncRootDirectory() {
        return "/dsdsadsdsdsd";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getMaxBaselineJobNum() {
        return 15;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getWorkerNodes() {
        return "";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getPublicServiceNode() {
        return "127.0.0.1";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getFillDataKeepAliveSeconds() {
        return 7200;
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

    @MockInvoke(targetClass = HttpServletRequest.class)
    String getHeader(String name) {
        return "dt_user_id=1; dt_username=admin%40dtstack.com; dt_can_redirect=false; dt_cookie_time=2022-05-20+10%3A04%3A52; dt_is_tenant_admin=true; track_rdos=true; dt_tenant_id=10827; dt_tenant_name=test_datasource; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxMDgyNyIsInVzZXJfaWQiOiIxIiwidXNlcl9uYW1lIjoiYWRtaW5AZHRzdGFjay5jb20iLCJleHAiOjE3MTQ5NjEwOTIsImlhdCI6MTY1Mjc3NTc1Mn0.qMA9Ae9tgkRr3L4dxh0xubhFJKg4ytWLbdcwmw6Jo3U; dt_is_tenant_creator=true; JSESSIONID=938778955C483D781FE096FFB511E749;";
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
        return 3000L;
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
        return 600000L;
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

    @MockInvoke(targetClass = UserService.class)
    public List<User> findUserWithFill(Set<Long> userIds) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = UserService.class)
    public void fillUser(List<ScheduleTaskVO> vos) {
        return;
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public void fillTaskGroupInfo(List<Long> resourceIds, List<ScheduleTaskVO> scheduleTaskVOS) {
        return;
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public AuthProjectVO finProject(Long projectId, Integer appType) {
        AuthProjectVO authProjectVO = new AuthProjectVO();
        authProjectVO.setProjectName("test");
        authProjectVO.setProjectId(1L);
        return authProjectVO;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getLocalSSLDir() {
        return System.getProperty("user.dir") + "/sslUploadTempDir";
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public Table<Integer, Long, AuthProjectVO> getProjectGroupAppType(Map<Integer, List<Long>> appProjectMap) {
        return HashBasedTable.create();
    }

    @MockInvoke(targetClass = TenantService.class)
    public UICTenantVO getTenant(Long tenantId) {
        UICTenantVO uicTenantVO = new UICTenantVO();
        uicTenantVO.setTenantId(tenantId);
        uicTenantVO.setTenantName("tenant");
        return uicTenantVO;
    }

    @MockInvoke(targetClass = TenantService.class)
    public String getTenantName(Long tenantId) {
        return "tenant";
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
    public String getSM2PublicKey() {
        return "04D4BDF1A660C728418D685A702C5E16EAA2463471BC23107CBDBBB4AD7AF526F88E89EBC7D3075D826F8323657858A351A709423B18A685CDAD141E671C32E8D4";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSM2PrivateKey() {
        return "0AC55910CF19346F35324577E7F3F0C544A7823B154B756D63160FD1167992B2";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean openDataClear() {
        return true;
    }

    public Integer getComponentRefreshAppType() {
        return AppType.RDOS.getType();
    }

    @MockInvoke(targetClass = ZkService.class)
    public List<String> getAliveBrokersChildren() {
        return Lists.newArrayList("127.0.0.1:8090", "127.0.0.1:8091", "127.0.0.1:8092");
    }

    @MockInvoke(targetClass = ZkService.class)
    public void updateSynchronizedLocalBrokerHeartNode(String localAddress, BrokerHeartNode source, boolean isCover) {
    }

    @MockInvoke(targetClass = ZkService.class)
    public List<String> getBrokersChildren() {
        return Lists.newArrayList("127.0.0.1:8090", "127.0.0.1:8091", "127.0.0.1:8092");
    }

    @MockInvoke(targetClass = ZkService.class)
    public void disableBrokerHeartNode(String localAddress, boolean stopHealthCheck) {
    }

    @MockInvoke(targetClass = ZkService.class)
    public BrokerHeartNode getBrokerHeartNode(String node) {
        return BrokerHeartNode.initNullBrokerHeartNode();
    }

    @MockInvoke(targetClass = RedisTemplate.class)
    public void convertAndSend(String channel, Object message) {
    }

    @MockInvoke(targetClass = RedisTemplate.class)
    public RedisSerializer<?> getValueSerializer() {
        return new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return new byte[0];
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return new Object();
            }
        };
    }

    @MockInvoke(targetClass = RedisTemplate.class)
    public RedisSerializer<String> getStringSerializer() {
        return new RedisSerializer<String>() {
            @Override
            public byte[] serialize(String s) throws SerializationException {
                return new byte[0];
            }

            @Override
            public String deserialize(byte[] bytes) throws SerializationException {
                return "rdos:session";
            }
        };
    }


    @MockInvoke(targetClass = RedisTemplate.class)
    public ValueOperations opsForValue() {
        return new ValueOperations() {
            @Override
            public void set(Object o, Object o2) {

            }

            @Override
            public void set(Object o, Object o2, long l, TimeUnit timeUnit) {

            }

            @Override
            public Boolean setIfAbsent(Object o, Object o2) {
                return null;
            }

            @Override
            public Boolean setIfAbsent(Object o, Object o2, long l, TimeUnit timeUnit) {
                return null;
            }

            @Override
            public Boolean setIfPresent(Object o, Object o2) {
                return null;
            }

            @Override
            public Boolean setIfPresent(Object o, Object o2, long l, TimeUnit timeUnit) {
                return null;
            }

            @Override
            public void multiSet(Map map) {

            }

            @Override
            public Boolean multiSetIfAbsent(Map map) {
                return null;
            }

            @Override
            public Object get(Object o) {
                return new HashMap<>();
            }

            @Override
            public Object getAndDelete(Object key) {
                return null;
            }

            @Override
            public Object getAndExpire(Object key, long timeout, TimeUnit unit) {
                return null;
            }

            @Override
            public Object getAndExpire(Object key, Duration timeout) {
                return null;
            }

            @Override
            public Object getAndPersist(Object key) {
                return null;
            }

            @Override
            public Object getAndSet(Object o, Object o2) {
                return new Object();
            }

            @Override
            public List multiGet(Collection collection) {
                return null;
            }

            @Override
            public Long increment(Object o) {
                return null;
            }

            @Override
            public Long increment(Object o, long l) {
                return null;
            }

            @Override
            public Double increment(Object o, double v) {
                return null;
            }

            @Override
            public Long decrement(Object o) {
                return null;
            }

            @Override
            public Long decrement(Object o, long l) {
                return null;
            }

            @Override
            public Integer append(Object o, String s) {
                return null;
            }

            @Override
            public String get(Object o, long l, long l1) {
                return null;
            }

            @Override
            public void set(Object o, Object o2, long l) {

            }

            @Override
            public Long size(Object o) {
                return null;
            }

            @Override
            public Boolean setBit(Object o, long l, boolean b) {
                return null;
            }

            @Override
            public Boolean getBit(Object o, long l) {
                return null;
            }

            @Override
            public List<Long> bitField(Object o, BitFieldSubCommands bitFieldSubCommands) {
                return null;
            }

            @Override
            public RedisOperations getOperations() {
                return null;
            }
        };
    }


    @MockInvoke(targetClass = RedisTemplate.class)
    public Boolean hasKey(Object key) {
        return true;
    }

    @MockInvoke(targetClass = RedisTemplate.class)
    public Boolean delete(Object key) {
        return true;
    }

    @MockInvoke(targetClass = SessionCache.class)
    public void remove(String sessionId) {
    }


    @MockInvoke(targetClass = CookieUtil.class)
    public static String getDtUicToken(Cookie[] cookies) {
        return "dsdsdsdsdsd";
    }


    @MockInvoke(targetClass = SessionUtil.class)
    public <T> T getUser(String token, Class<T> clazz) {
        return (T) new UserDTO();
    }


    @MockInvoke(targetClass = PoolHttpClient.class)
    public static String get(String url, Map<String, Object> cookies) throws IOException {
        Map<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("success", true);
        objectObjectHashMap.put("data", new DtUicUser());
        return JSONObject.toJSONString(objectObjectHashMap);
    }

    @MockInvoke(targetClass = PoolHttpClient.class)
    public static String get(String url) throws IOException {
        Map<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("success", true);
        objectObjectHashMap.put("data", true);
        return JSONObject.toJSONString(objectObjectHashMap);
    }

    @MockInvoke(targetClass = LoginSessionStore.class)
    public <T> void createSession(String token, Class<T> clazz, Consumer<DtUicUser> dtUicUserHandler) {

    }

    @MockInvoke(targetClass = ApplicationContext.class)
    <T> T getBean(Class<T> interfaceClass) throws BeansException, InstantiationException, IllegalAccessException {
        if (interfaceClass.isInterface()) {
            return null;
        }
        return interfaceClass.newInstance();
    }
}
