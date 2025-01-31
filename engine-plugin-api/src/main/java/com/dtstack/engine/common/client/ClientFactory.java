package com.dtstack.engine.common.client;

import com.dtstack.engine.common.callback.ClassLoaderCallBackMethod;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.loader.DtClassLoader;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/12
 */
public class ClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactory.class);

    private static Map<String, ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    public static IClient createPluginClass(ClassLoader classLoader) throws Exception {
        return ClassLoaderCallBackMethod.callbackAndReset(()-> {
            ServiceLoader<IClient> serviceLoader = ServiceLoader.load(IClient.class);

            List<IClient> matchingClient = new ArrayList<>();
            serviceLoader.iterator().forEachRemaining(matchingClient::add);

            if (matchingClient.size() != 1) {
                throw new RuntimeException("zero or more than one plugin client found" + matchingClient);
            }
            return new ClientProxy(matchingClient.get(0));
        }, classLoader, true);
    }

    public static IClient buildPluginClient(String pluginInfo,String pluginPath) throws Exception {
        Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        String clientTypeStr = MathUtil.getString(params.get(ConfigConstant.TYPE_NAME_KEY));
        if (StringUtils.isBlank(clientTypeStr)) {
            throw new RuntimeException("not support for typeName:" + clientTypeStr + " pluginInfo:" + pluginInfo);
        }
        String clusterName = MathUtil.getString(params.get(ConfigConstant.CLUSTER));
        String classLoaderIsolationKey = String.join(ConfigConstant.SPLIT, clusterName, clientTypeStr);
        ClassLoader classLoader = pluginClassLoader.computeIfAbsent(classLoaderIsolationKey, type -> {
            String plugin = pluginPath + File.separator + clientTypeStr;
            File pluginFile = new File(plugin);
            if (!pluginFile.exists()) {
                LOGGER.error(" {} build plugin client error {} ", classLoaderIsolationKey, plugin);
                throw new RuntimeException(String.format("%s directory not found", plugin));
            }
            return createDtClassLoader(pluginFile);
        });

        return ClientFactory.createPluginClass(classLoader);
    }


    private static URLClassLoader createDtClassLoader(File dir) {
        File[] files = dir.listFiles();
        if (null == files) {
            throw new RuntimeException(String.format("%s directory is empty", dir));
        }
        URL[] urls = Arrays.stream(files).sorted()
                .filter(file -> file.isFile() && file.getName().endsWith(".jar"))
                .map(file -> {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("file to url error ", e);
                    }
                })
                .toArray(URL[]::new);
        LOGGER.info("load jar file {}", Arrays.stream(files).map(File::getName));
        return new DtClassLoader(urls, ClientFactory.class.getClassLoader());
    }
}
