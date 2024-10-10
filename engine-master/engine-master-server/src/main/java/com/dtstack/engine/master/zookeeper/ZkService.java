package com.dtstack.engine.master.zookeeper;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.KerberosUtils;
import com.dtstack.engine.master.cron.ErrorTopCron;
import com.dtstack.engine.master.cron.ResourceQueueUsedCron;
import com.dtstack.engine.master.failover.FailoverStrategy;
import com.dtstack.engine.master.handler.IMasterHandler;
import com.dtstack.engine.master.listener.HeartBeatCheckListener;
import com.dtstack.engine.master.listener.HeartBeatListener;
import com.dtstack.engine.master.listener.Listener;
import com.dtstack.engine.master.listener.MasterListener;
import com.dtstack.engine.master.zookeeper.data.BrokerHeartNode;
import com.dtstack.engine.master.zookeeper.data.BrokersNode;
import com.dtstack.schedule.common.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class ZkService implements InitializingBean, DisposableBean, ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkService.class);

    private final static Integer LOCK_WAIT_SECONDS = 5;
    private final static String HEART_NODE = "heart";
    private final static String WORKER_NODE = "workers";
    private final static String LOCK_NODE = "locks";

    private ZkConfig zkConfig;
    private String zkAddress;
    private String localAddress;
    private String distributeRootNode;
    private String brokersNode;
    private String localNode;
    private String workersNode;
    private String lockNode;

    private CuratorFramework zkClient;
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * when normal stopped，need trigger Listener close();
     */
    private List<Listener> listeners = Lists.newArrayList();

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private FailoverStrategy failoverStrategy;

    @Autowired
    private List<IMasterHandler> masterHandlers;

    private static class LockServiceImpl implements LockService {

        private final CuratorFramework zkClient;
        private final String lockNode;
        private final Map<String, InterProcessMutex> mutexes = new HashMap<>();

        private LockServiceImpl(CuratorFramework zkClient, String lockNode) {
            this.zkClient = zkClient;
            this.lockNode = lockNode;
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
                LOGGER.info("release lock success, local: {}", lockName);
            } catch (Exception e) {
                LOGGER.warn("Couldn't release lock: {}", lockName, e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing " + this.getClass().getName());

        initConfig();
        checkDistributedConfig();
        initClient();
        zkRegistration();

        ScheduleNode.getInstance().setLockService(new LockServiceImpl(zkClient, lockNode));
        ScheduleNode.getInstance().finishInit();
    }

    private void initConfig() {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setNodeZkAddress(environmentContext.getNodeZkAddress());
        zkConfig.setLocalAddress(environmentContext.getLocalAddress());
        zkConfig.setSecurity(environmentContext.getSecurity());
        this.zkConfig = zkConfig;
    }

    private void initClient() {
        kerberosLogin(environmentContext.getKeytabPath(), environmentContext.getPrincipal(), () -> {
            zkClient = CuratorFrameworkFactory.builder()
                    .connectString(this.zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .connectionTimeoutMs(3000)
                    .sessionTimeoutMs(30000).build();
            zkClient.start();
            return zkClient;
        });
        LOGGER.warn("connector zk success...");
    }


    public CuratorFramework kerberosLogin(String keytabPath, String principal, Supplier<CuratorFramework> initClientFunction) {
        if (StringUtils.isNotBlank(keytabPath)) {
            String zookeeperContestName = environmentContext.getLoginContextName();
            if (StringUtils.isNotEmpty(environmentContext.getKrb5Conf())) {
                System.setProperty(ConfigConstant.JAVA_SECURITY_KRB5_CONF, environmentContext.getKrb5Conf());
            }
            synchronized (objectMapper) {
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

    private void zkRegistration() throws Exception {
        createNodeIfNotExists(this.distributeRootNode, "");
        createNodeIfNotExists(this.brokersNode, BrokersNode.initBrokersNode());
        createNodeIfNotExists(this.localNode, "");
        createNodeIfNotExists(this.workersNode, new HashSet<>());
        createLocalBrokerHeartNode();
        createNodeIfNotExists(this.lockNode, null);
        LOGGER.warn("init zk server success...");
    }

    private void initScheduledExecutorService() throws Exception {
        listeners.add(new HeartBeatListener(this));
        String latchPath = String.format("%s/%s", this.distributeRootNode, "masterLatchLock");
        MasterListener masterListener = new MasterListener(zkClient, latchPath, localAddress);
        if (CollectionUtils.isNotEmpty(masterHandlers)) {
            for (IMasterHandler masterHandler : masterHandlers) {
                masterListener.addMasterHandler(masterHandler);
            }
        }
        listeners.add(masterListener);
        listeners.add(new HeartBeatCheckListener(masterListener, failoverStrategy, this));
    }

    private void createLocalBrokerHeartNode() throws Exception {
        String node = String.format("%s/%s", this.localNode, HEART_NODE);
        if (zkClient.checkExists().forPath(node) == null) {
            zkClient.create().forPath(node,
                    objectMapper.writeValueAsBytes(BrokerHeartNode.initBrokerHeartNode()));
        } else {
            updateSynchronizedLocalBrokerHeartNode(this.localAddress, BrokerHeartNode.initBrokerHeartNode(), true);
        }
    }

    public void updateSynchronizedLocalBrokerHeartNode(String localAddress, BrokerHeartNode source, boolean isCover) {
        String nodePath = String.format("%s/%s/%s", brokersNode, localAddress, HEART_NODE);
        try {
            BrokerHeartNode target = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerHeartNode.class);
            BrokerHeartNode.copy(source, target, isCover);
            zkClient.setData().forPath(nodePath,
                    objectMapper.writeValueAsBytes(target));
        } catch (Exception e) {
            LOGGER.error("{}:updateSynchronizedBrokerHeartNode error:", nodePath, e);
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

    private void checkDistributedConfig() throws Exception {
        if (StringUtils.isBlank(this.zkConfig.getNodeZkAddress())
                || this.zkConfig.getNodeZkAddress().split("/").length < 2) {
            throw new RdosDefineException("zkAddress is error");
        }
        String[] zks = this.zkConfig.getNodeZkAddress().split("/");
        this.zkAddress = zks[0].trim();
        this.distributeRootNode = String.format("/%s", zks[1].trim());
        this.localAddress = zkConfig.getLocalAddress();
        if (StringUtils.isBlank(this.localAddress) || this.localAddress.split(":").length < 2) {
            throw new RdosDefineException("localAddress is error");
        }
        this.brokersNode = String.format("%s/brokers", this.distributeRootNode);
        this.localNode = String.format("%s/%s", this.brokersNode, this.localAddress);
        this.workersNode = String.format("%s/%s", this.localNode, WORKER_NODE);
        this.lockNode = String.format("%s/%s", this.distributeRootNode, LOCK_NODE);
    }

    public BrokerHeartNode getBrokerHeartNode(String node) {
        try {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, node, HEART_NODE);
            return objectMapper.readValue(zkClient.getData()
                    .forPath(nodePath), BrokerHeartNode.class);
        } catch (Exception e) {
            LOGGER.error("{}:getBrokerHeartNode error:", node, e);
        }
        return BrokerHeartNode.initNullBrokerHeartNode();
    }

    public List<String> getBrokersChildren() {
        try {
            return zkClient.getChildren().forPath(this.brokersNode);
        } catch (Exception e) {
            LOGGER.error("getBrokersChildren error:", e);
        }
        return Lists.newArrayList();
    }

    public List<String> getAliveBrokersChildren() {
        List<String> alives = Lists.newArrayList();
        try {
            if (null != zkClient) {
                List<String> brokers = zkClient.getChildren().forPath(this.brokersNode);
                for (String broker : brokers) {
                    BrokerHeartNode brokerHeartNode = getBrokerHeartNode(broker);
                    if (brokerHeartNode.getAlive()) {
                        alives.add(broker);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("getBrokersChildren error:", e);
        }
        //when zk down alive is empty,at least contains self node
        if (alives.isEmpty()) {
            alives.add(localAddress);
        }
        return alives;
    }

    public List<Map<String, Object>> getAllBrokerWorkersNode() {
        List<Map<String, Object>> allWorkers = new ArrayList<>();
        List<String> children = this.getBrokersChildren();
        for (String address : children) {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, address, WORKER_NODE);
            try {
                List<Map<String, Object>> workerNode = objectMapper.readValue(zkClient.getData().forPath(nodePath), ArrayList.class);
                allWorkers.addAll(workerNode);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return allWorkers;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void disableBrokerHeartNode(String localAddress, boolean stopHealthCheck) {
        BrokerHeartNode disableBrokerHeartNode = BrokerHeartNode.initNullBrokerHeartNode();
        if (stopHealthCheck) {
            disableBrokerHeartNode.setSeq(HeartBeatCheckListener.getStopHealthCheckSeq());
        }
        this.updateSynchronizedLocalBrokerHeartNode(localAddress, disableBrokerHeartNode, true);
    }

    @Override
    public void destroy() throws Exception {
        disableBrokerHeartNode(this.localAddress, false);
        for (Listener listener : listeners) {
            try {
                listener.close();
                LOGGER.info("close {}", listener.getClass().getSimpleName());
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    public void setEnvironmentContext(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            initScheduledExecutorService();
            LOGGER.warn("zk service init scheduled executor success...");
        } catch (Exception e) {
            throw new RdosDefineException(e);
        }
    }
}
