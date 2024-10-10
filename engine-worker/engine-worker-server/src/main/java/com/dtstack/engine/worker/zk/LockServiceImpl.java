package com.dtstack.engine.worker.zk;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.KerberosUtils;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.schedule.common.DynamicConfiguration;
import com.dtstack.schedule.common.LockService;
import com.dtstack.schedule.common.LockServiceException;
import com.dtstack.schedule.common.LockTimeoutException;
import com.dtstack.schedule.common.ScheduleNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @Auther: dazhi
 * @Date: 2022/4/15 2:14 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
@ConditionalOnProperty(prefix = "flink.lock",name = "open",havingValue = "true")
public class LockServiceImpl implements LockService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockServiceImpl.class);

    @Autowired
    private EnvironmentContext environmentContext;

    private final static Integer LOCK_WAIT_SECONDS = 5;

    private CuratorFramework zkClient;
    private String lockNode;
    private final Map<String, InterProcessMutex> mutexes = new HashMap<>();
    private final static String LOCK_NODE = "locks";
    private static ObjectMapper objectMapper = new ObjectMapper();

    private LockServiceImpl() {
    }

    @Override
    public void execWithLock(String lockName, Runnable runnable) {
        try {
            boolean locked = tryLock(lockName, LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
            if (locked) {
                runnable.run();
            } else {
                throw new LockTimeoutException("Lock " + lockName + " timeout.");
            }
        } finally {
            release(lockName);
        }
    }

    @Override
    public synchronized boolean tryLock(String lockName, int time, TimeUnit timeUnit) {
        InterProcessMutex mutex = this.mutexes.computeIfAbsent(lockName,
                ln -> new InterProcessMutex(zkClient, String.format("%s/%s", this.lockNode, ln)));
        try {
            return mutex.acquire(time, timeUnit);
        } catch (Exception e) {
            throw new LockServiceException("ZK errors, connection interruptions");
        }
    }

    @Override
    public synchronized void release(String lockName) {
        InterProcessMutex mutex = this.mutexes.get(lockName);
        try {
            if (mutex != null) {
                mutex.release();
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't release lock " + lockName);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            LOGGER.info("ScheduleNode init start");
            SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());
            initClient();
            createNodeIfNotExists(this.lockNode, null);
            ScheduleNode.getInstance().setLockService(this);
            ScheduleNode.getInstance().finishInit();
        } catch (Throwable e) {
            LOGGER.error("ScheduleNode init error:{}", e.getMessage(), e);
        } finally {
            ScheduleNode instance = ScheduleNode.getInstance();
            if (instance != null) {
                LOGGER.info("ScheduleNode init finish,instance");
            }
        }
    }

    public void createNodeIfNotExists(String node, Object obj) throws Exception {
        if (zkClient.checkExists().forPath(node) == null) {
            if (obj != null) {
                zkClient.create().forPath(node,
                        objectMapper.writeValueAsBytes(obj));
            } else {
                zkClient.create().forPath(node);
            }
        }
    }

    private void initClient() {
        String nodeZkAddress = environmentContext.getNodeZkAddress();
        if (StringUtils.isBlank(nodeZkAddress)) {
            return;
        }
        String[] zks = nodeZkAddress.split("/");
        kerberosLogin(environmentContext.getKeytabPath(), environmentContext.getPrincipal(), () -> {
            zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zks[0]).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .connectionTimeoutMs(3000)
                    .sessionTimeoutMs(30000).build();
            zkClient.start();
            return zkClient;
        });
        String distributeRootNode = String.format("/%s", zks[1].trim());
        this.lockNode = String.format("%s/%s", distributeRootNode, LOCK_NODE);
        LOGGER.warn("connector zk success...");
    }

    public CuratorFramework kerberosLogin(String keytabPath, String principal, Supplier<CuratorFramework> initClientFunction) {
        if (StringUtils.isNotBlank(keytabPath)) {
            String zookeeperContestName = environmentContext.getLoginContextName();
            if (StringUtils.isNotEmpty(environmentContext.getKrb5Conf())) {
                System.setProperty(ConfigConstant.JAVA_SECURITY_KRB5_CONF, environmentContext.getKrb5Conf());
            }
            synchronized (LOCK_WAIT_SECONDS) {
                // construct a dynamic JAAS configuration
                // wire up the configured JAAS login contexts to use the krb5 entries
                AppConfigurationEntry krb5Entry = KerberosUtils.keytabEntry(keytabPath, principal);
                DynamicConfiguration currentConfig = new DynamicConfiguration(javax.security.auth.login.Configuration.getConfiguration());
                currentConfig.addAppConfigurationEntry(zookeeperContestName, krb5Entry);
                javax.security.auth.login.Configuration.setConfiguration(currentConfig);

                if (!"Client".equals(zookeeperContestName)) {
                    System.setProperty(ZooKeeperSaslClient.LOGIN_CONTEXT_NAME_KEY, zookeeperContestName);
                }
                CuratorFramework curatorFramework = initClientFunction.get();
                if (!"Client".equals(zookeeperContestName)) {
                    System.clearProperty(ZooKeeperSaslClient.LOGIN_CONTEXT_NAME_KEY);
                }
                return curatorFramework;
            }
        }
        return initClientFunction.get();
    }
}
