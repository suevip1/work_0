package com.dtstack.engine.master.multiengine;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.annotation.JSONField;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SyncDataSourceReplaceHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncDataSourceReplaceHandler.class);

    private static final String SDK_MAIN_CLASS = "com.dtstack.chunjun.tools.common.Executor";

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    private ConcurrentHashMap<String, Object> cacheObj = new ConcurrentHashMap<>(1);
    private ConcurrentHashMap<String, Method> cacheMethod = new ConcurrentHashMap<>(1);

    public String replaceSync(String syncJob, String jobId) {
        String originJob = syncJob;
        try {
            String flinkXSdkPath = environmentContext.getFlinkXSdkPath();
            if (StringUtils.isBlank(flinkXSdkPath)) {
                return syncJob;
            }
            JSONObject jsonObject = JSONObject.parseObject(syncJob);
            String readerName = (String) JSONPath.eval(jsonObject, "$.job.content[0].reader.name");
            String readerSourceId = Objects.toString(JSONPath.eval(jsonObject, "$.job.content[0].reader.parameter.dtCenterSourceId"), org.apache.commons.lang3.StringUtils.EMPTY);
            String writerName = (String) JSONPath.eval(jsonObject, "$.job.content[0].writer.name");
            String writerSourceId = Objects.toString(JSONPath.eval(jsonObject, "$.job.content[0].writer.parameter.dtCenterSourceId"), org.apache.commons.lang3.StringUtils.EMPTY);
            boolean canReplace = false;
            if (StringUtils.isNotBlank(readerSourceId) && StringUtils.isNotBlank(readerName)) {
                canReplace = true;
                syncJob = replaceExecutor(syncJob, readerName, readerSourceId, jobId);
            }
            if (StringUtils.isNotBlank(writerSourceId) && StringUtils.isNotBlank(writerName)) {
                canReplace = true;
                syncJob = replaceExecutor(syncJob, writerName, writerSourceId, jobId);
            }
            if (StringUtils.isBlank(syncJob)) {
                LOGGER.info("jobId {} replace sync json is empty", jobId);
                return originJob;
            }
            if (canReplace && !originJob.equals(syncJob)) {
                LOGGER.info("jobId {} replace sync json is {}", jobId, syncJob);
            }
            return syncJob;
        } catch (Throwable e) {
            LOGGER.error("replaceSync error", e);
            return originJob;
        }
    }

    private String replaceExecutor(String syncJob, String readerName, String sourceId, String jobId) throws Exception {
        ApiResponse<DsServiceInfoDTO> readSourceDs = dataSourceAPIClient.getDsInfoById(Long.parseLong(sourceId));
        DsServiceInfoDTO data = readSourceDs.getData();
        if (null == data || StringUtils.isBlank(data.getDataJson())) {
            return syncJob;
        }
        ReplaceParameter replaceParameter = new ReplaceParameter();
        replaceParameter.setPluginType(readerName);
        replaceParameter.setContent(syncJob);
        JSONObject dataJsonObj = JSONObject.parseObject(data.getDataJson());
        configKerberos(dataJsonObj, data.getDtuicTenantId());
        List<ReplaceItem> replaceItems = new ArrayList<>();
        for (String key : dataJsonObj.keySet()) {
            ReplaceItem replaceItem = new ReplaceItem();
            replaceItem.setReplaceKey(key);
            replaceItem.setReplaceVal(dataJsonObj.getString(key));
            replaceItems.add(replaceItem);
        }
        replaceParameter.setReplaceItems(replaceItems);
        JSONObject replaceConf = new JSONObject();
        replaceConf.put("replace", replaceParameter);
        try {
            String result = (String) invokeExecutorMethodReplace("replace", replaceConf.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String resultString = (String) JSONPath.eval(resultObj, "$.result");
            Object exception = JSONPath.eval(resultObj, "$.exception");
            if (exception != null) {
                LOGGER.error("flinkx replace {} exception {} jobId {}", replaceConf.toJSONString(), exception, jobId);
                return syncJob;
            }
            //异常返回原值
            return resultString;
        } catch (Exception e) {
            LOGGER.error("flinkx replace exception {} jobId {}", replaceConf.toJSONString(), jobId, e);
            return syncJob;
        }
    }

    private void configKerberos(JSONObject dataJsonObj, Long dtuicTenantId) {
        Boolean OPEN_KERBEROS = dataJsonObj.getBoolean(GlobalConst.OPEN_KERBEROS);
        if (null == OPEN_KERBEROS || BooleanUtils.isFalse(OPEN_KERBEROS)) {
            return;
        }
        JSONObject kerberosConfig = dataJsonObj.getJSONObject(GlobalConst.KERBEROS_CONFIG);
        if (kerberosConfig == null) {
            return;
        }
        JSONObject hadoopConfig = dataJsonObj.getJSONObject(GlobalConst.HADOOP_CONFIG);
        if (hadoopConfig == null) {
            return;
        }
        hadoopConfig.put(GlobalConst.KERBEROS_CONFIG, kerberosConfig);

        hadoopConfig.put(ConfigConstant.PRINCIPAL_FILE, kerberosConfig.getString(ConfigConstant.PRINCIPAL_FILE));
        hadoopConfig.put(ConfigConstant.PRINCIPAL, kerberosConfig.getString(ConfigConstant.PRINCIPAL));
        hadoopConfig.put(ConfigConstant.JAVA_SECURITY_KRB5_CONF, kerberosConfig.getString(ConfigConstant.JAVA_SECURITY_KRB5_CONF));
        dataJsonObj.remove(GlobalConst.KERBEROS_CONFIG);
        //sftp默认取控制台对应集群
        String sftpStr = clusterService.getConfigByKey(dtuicTenantId, EComponentType.SFTP.getConfName(), null, null, false);
        JSONObject sftp = JSONObject.parseObject(sftpStr);
        if (sftp != null) {
            hadoopConfig.put(EComponentType.SFTP.getConfName(), sftp);
            String path = sftp.getString("path");
            hadoopConfig.put(ConfigConstant.REMOTE_DIR, path + File.separator + kerberosConfig.getString("kerberosDir"));
        }
        dataJsonObj.put(GlobalConst.HADOOP_CONFIG, hadoopConfig.toJSONString());
    }

    public synchronized Object invokeExecutorMethodReplace(String... params) throws Exception {
        Object obj = this.cacheObj.computeIfAbsent(SDK_MAIN_CLASS, (k) -> {
            try {
                List<File> jarFile = new ArrayList<>();
                File file = new File(environmentContext.getFlinkXSdkPath());
                if (file.isDirectory() && null != file.listFiles()) {
                    jarFile = Arrays.stream(file.listFiles()).filter(f -> f.getName().endsWith(".jar")).collect(Collectors.toList());
                } else {
                    if (file.getName().endsWith(".jar")) {
                        jarFile.add(file);
                    }
                }
                URL[] urls = jarFile.stream().map(f -> {
                    try {
                        return f.toURI().toURL();
                    } catch (MalformedURLException e) {
                        LOGGER.error("get jar file error", e);
                    }
                    return null;
                }).toArray(URL[]::new);
                URLClassLoader urlClassLoader = new URLClassLoader(urls);
                Class<?> sdkClass = urlClassLoader.loadClass(SDK_MAIN_CLASS);
                if (null == sdkClass) {
                    throw new RdosDefineException(SDK_MAIN_CLASS + "load error");
                }
                Object classInstance = sdkClass.newInstance();
                Method method = sdkClass.getMethod("execute", String.class, String.class);
                cacheMethod.put(SDK_MAIN_CLASS, method);
                return classInstance;
            } catch (Exception e) {
                LOGGER.error("getExecutorClass error", e);
            }
            return null;
        });
        Method method = cacheMethod.get(SDK_MAIN_CLASS);
        if (method != null) {
            return method.invoke(obj, params);
        }
        return null;
    }

}

class ReplaceParameter {
    @JSONField(name = "content")
    private String content;

    @JSONField(name = "plugin-type")
    private String pluginType;

    @JSONField(name = "replace-items")
    private List<ReplaceItem> replaceItems;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public List<ReplaceItem> getReplaceItems() {
        return replaceItems;
    }

    public void setReplaceItems(List<ReplaceItem> replaceItems) {
        this.replaceItems = replaceItems;
    }
}

class ReplaceItem {
    @JSONField(name = "replace-key")
    private String replaceKey;

    @JSONField(name = "replace-val")
    private String replaceVal;

    public String getReplaceKey() {
        return replaceKey;
    }

    public void setReplaceKey(String replaceKey) {
        this.replaceKey = replaceKey;
    }

    public String getReplaceVal() {
        return replaceVal;
    }

    public void setReplaceVal(String replaceVal) {
        this.replaceVal = replaceVal;
    }
}