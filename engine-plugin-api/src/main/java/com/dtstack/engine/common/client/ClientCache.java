package com.dtstack.engine.common.client;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.ComponentUtil;
import com.dtstack.engine.common.util.PluginInfoUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 插件客户端
 * Date: 2018/2/5
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ClientCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCache.class);

    private String pluginPath;

    private final Map<String, IClient> defaultClientMap = Maps.newConcurrentMap();

    /**
     * {cacheKey:{pluginInfo:client}}
     */
    private final Map<String, Pair<String, IClient>> CLIENT_CACHE = Maps.newConcurrentMap();

    /**
     * {cacheKey:version}
     */
    private final Map<String, Integer> VERSION_CACHE = Maps.newConcurrentMap();

    private static final ClientCache singleton = new ClientCache();

    public static final String CLIENT_BUILD_LOCK_KEY = "client_build_lock::%s";

    private ClientCache() {
    }

    public static ClientCache getInstance(String pluginPath) {
        singleton.pluginPath = pluginPath;
        return singleton;
    }

    /**
     * 获取 client 并处理缓存
     *
     * @param engineType 引擎类型：flink、spark、dtscript
     * @param pluginInfo 集群配置信息
     * @return client
     */
    public IClient getClient(String engineType, String pluginInfo) throws ClientAccessException {
        return getClient(engineType, pluginInfo, true);
    }

    /**
     * 获取 client 并处理缓存
     *
     * @param engineType 引擎类型：flink、spark、dtscript
     * @param pluginInfo 集群配置信息
     * @param isInit 是否执行 init
     * @return client
     */
    public IClient getClient(String engineType, String pluginInfo, boolean isInit) throws ClientAccessException {
        try {
            if (Strings.isNullOrEmpty(pluginInfo)) {
                return getDefaultPlugin(engineType);
            }

            String md5sum = PluginInfoUtil.getMd5SumFromJson(pluginInfo);
            String componentKey = ComponentUtil.getComponentKey(pluginInfo);
            return cacheAndReturn(pluginInfo, componentKey, md5sum, engineType, isInit).getRight();
        } catch (Throwable e) {
            LOGGER.error("------- engineType {}  plugin info {} get client error ", engineType, pluginInfo, e);
            throw new ClientAccessException(e);
        }
    }

    /**
     * 缓存并返回 client
     *
     * @param pluginInfo   pluginInfo 信息
     * @param componentKey 缓存 key
     * @param md5sum       当前插件的 md5sum
     * @param engineType   engine 类型
     * @param isInit       是否执行 init 方法
     * @return client pair 信息
     * @throws Exception 可能的异常信息
     */
    private Pair<String, IClient> cacheAndReturn(String pluginInfo, String componentKey, String md5sum, String engineType, boolean isInit) throws Exception {
        Properties properties = PublicUtil.jsonStrToObjectWithOutNull(pluginInfo, Properties.class);
        String cluster = properties.getProperty(ConfigConstant.CLUSTER, StringUtils.EMPTY);
        String lock = generateClientBuildingLock(cluster, engineType);
        Integer versionId = properties.get(PluginInfoConst.PLUGIN_INFO_VERSION) == null ? 0 : (Integer) properties.get(PluginInfoConst.PLUGIN_INFO_VERSION);
        String cacheKey = String.format("%s_init_%s", StringUtils.isNotBlank(componentKey) ? componentKey : md5sum, isInit);
        boolean isComponentKey = StringUtils.isNotBlank(componentKey);
        Pair<String, IClient> clientPair = CLIENT_CACHE.get(cacheKey);
        if (isComponentKey && Objects.nonNull(clientPair)) {
            if (isInit && VERSION_CACHE.get(cacheKey) != null && VERSION_CACHE.get(cacheKey) > versionId) {
                /*
                   pluginInfo 修改后为 V1 -> V2
                   JobClient 可能还持有 V1的数据
                   V1 数据会和V2 会交叉close 和 init  创建大量client
                   此时 沿用V2 Client V1 不再创建
                 */
                return clientPair;
            }
            synchronized (lock.intern()) {
                clientPair = refreshClient(pluginInfo, md5sum, cacheKey);
            }
        }
        // 当前 engineType 不区分版本, 多版本并行下初始化还会有问题
        if (clientPair == null) {
            synchronized (lock.intern()) {
                clientPair = CLIENT_CACHE.get(cacheKey);
                if (clientPair == null) {
                    IClient client = ClientFactory.buildPluginClient(pluginInfo, pluginPath);
                    if (isInit) {
                        client.init(properties);
                    }
                    clientPair = Pair.of(pluginInfo, client);
                    CLIENT_CACHE.put(cacheKey, clientPair);
                    VERSION_CACHE.put(cacheKey, versionId);
                }
            }
        }
        return clientPair;
    }

    /**
     * @param pluginInfo pluginInfo 信息
     * @param md5sum 当前插件的 md5sum
     * @param cacheKey 缓存 key
     * @return
     */
    private Pair<String, IClient> refreshClient(String pluginInfo, String md5sum, String cacheKey) {
        Pair<String, IClient> clientPair;
        clientPair = CLIENT_CACHE.get(cacheKey);
        if (Objects.nonNull(clientPair)) {
            String cachePluginInfo = clientPair.getLeft();
            String cacheMd5sum = PluginInfoUtil.getMd5SumFromJson(cachePluginInfo);
            // md5 不一致需要清除历史 client
            if (!StringUtils.equals(md5sum, cacheMd5sum)) {
                IClient cacheClient = clientPair.getRight();
                try {
                    JSONObject newPluginInfo = JSONObject.parseObject(pluginInfo);
                    JSONObject originPluginInfo = JSONObject.parseObject(cachePluginInfo);
                    PublicUtil.removeRepeatJSON(newPluginInfo,originPluginInfo);
                    LOGGER.info("stack {} trigger cacheKey {} close", ExceptionUtil.stackTrack(), cacheKey);
                    // 需要调用 close 方法
                    LOGGER.info("pluginInfo changes and close old client, old pluginInfo:\n{} \nnew pluginInfo:\n{}", originPluginInfo, newPluginInfo);
                    cacheClient.close(cachePluginInfo);
                } catch (Throwable e) {
                    LOGGER.error("close engine plugin client error, pluginInfo: {}", cachePluginInfo, e);
                }
                CLIENT_CACHE.remove(cacheKey);
                clientPair = null;
            }
        }
        return clientPair;
    }

    public IClient getDefaultPlugin(String engineType) {
        IClient defaultClient = defaultClientMap.get(engineType);
        try {
            if (defaultClient == null) {
                synchronized (defaultClientMap) {
                    defaultClient = defaultClientMap.get(engineType);
                    if (defaultClient == null) {
                        JSONObject pluginInfo = new JSONObject();
                        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, engineType);
                        defaultClient = ClientFactory.buildPluginClient(pluginInfo.toJSONString(), pluginPath);
                        defaultClientMap.putIfAbsent(engineType, defaultClient);
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("-------job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:{}", engineType, e);
            throw new IllegalArgumentException("job.pluginInfo is empty, either can't find plugin('In console is the typeName') which engineType:" + engineType);
        }
        return defaultClient;
    }

    /**
     * 构建 client 时用到的锁
     * 锁粒度缩小到集群级别，暂不使用 cacheKey 作为锁
     * @param cluster 集群名称（一般不为空）
     * @param engineType
     */
    private static String generateClientBuildingLock(String cluster, String engineType) {
        String lock = StringUtils.isBlank(cluster) ? String.format(CLIENT_BUILD_LOCK_KEY, engineType) : String.format(CLIENT_BUILD_LOCK_KEY, cluster + "::" + engineType);
        return lock.intern();
    }
}
