package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ComponentDTO;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.ConsoleParamCheckResult;
import com.dtstack.engine.api.pojo.lineage.ComponentMultiTestResult;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentUserVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.IComponentVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.api.vo.task.TaskGetSupportJobTypesResultVO;
import com.dtstack.engine.common.Resource;
import com.dtstack.engine.common.client.bean.DtScriptAgentLabelDTO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.KerberosUtils;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.Xml2JsonUtil;
import com.dtstack.engine.common.util.ZipUtil;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ComponentUserDao;
import com.dtstack.engine.dao.ConsoleFileSyncDao;
import com.dtstack.engine.dao.ConsoleFileSyncDetailDao;
import com.dtstack.engine.dao.ConsoleSSLDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.dto.AddOrUpdateComponentResult;
import com.dtstack.engine.master.enums.CrudEnum;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.model.cluster.ClusterFactory;
import com.dtstack.engine.master.model.cluster.ComponentFacade;
import com.dtstack.engine.master.model.cluster.Part;
import com.dtstack.engine.master.model.cluster.PartCluster;
import com.dtstack.engine.master.plugininfo.PluginInfoCacheManager;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.cache.RdosSubscribe;
import com.dtstack.engine.master.router.cache.RdosTopic;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.Krb5FileUtil;
import com.dtstack.engine.master.utils.XmlFileUtil;
import com.dtstack.engine.master.vo.JdbcUrlTipVO;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.ComponentUser;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleSSL;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEFAULT_KUBERNETES_PARENT_NODE;
import static com.dtstack.engine.common.constrant.ConfigConstant.KEYTAB_SUFFIX;
import static com.dtstack.engine.common.constrant.ConfigConstant.KRB5_CONF;
import static com.dtstack.engine.common.constrant.ConfigConstant.MD5_ZIP_KEY;
import static com.dtstack.engine.common.constrant.ConfigConstant.TYPE_NAME_KEY;
import static com.dtstack.engine.common.constrant.ConfigConstant.USER_DIR_DOWNLOAD;
import static com.dtstack.engine.common.constrant.ConfigConstant.USER_DIR_UNZIP;
import static com.dtstack.engine.common.constrant.ConfigConstant.XML_SUFFIX;
import static com.dtstack.engine.common.constrant.ConfigConstant.ZIP_SUFFIX;

@Service
@DependsOn("rdosSubscribe")
public class ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

    private static final String SSL_CLIENT_PATH = "ssl" + File.separator + "client";

    private static final String HADOOP3_SIGNAL = "Hadoop3";

    private static final String GPU_EXEC_SIGNAL = "yarn.nodemanager.resource-plugins.gpu.path-to-discovery-executables";

    private static final String GPU_RESOURCE_PLUGINS_SIGNAL = "yarn.nodemanager.resource-plugins";

    private static final String GPU_ALLOWED_SIGNAL = "yarn.nodemanager.resource-plugins.gpu.allowed-gpu-devices";

    private static final String[] REQUIRED_SSL_CONFIGS = {
            "ssl.client.truststore.type",
            "ssl.client.truststore.location",
            "ssl.client.truststore.password"
    };

    private static final String LOCATION_SSH_CONFIG = "ssl.client.truststore.location";

    private static final String CLUSTER_MODE = "clusterMode";

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ConsoleCache consoleCache;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private ConsoleSSLDao consoleSSLDao;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private RdosSubscribe rdosSubscribe;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private SftpFileManage sftpFileManageBean;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ComponentUserDao componentUserDao;

    @Autowired
    private ClusterFactory clusterFactory;
    @Autowired
    private ConsoleFileSyncDao consoleFileSyncDao;

    @Autowired
    private ConsoleFileSyncDetailDao consoleFileSyncDetailDao;

    @Autowired
    private DataSourceXOperator dataSourceXOperator;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ComponentAuxiliaryService componentAuxiliaryService;

    @Autowired
    private ComponentFacade componentFacade;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private PluginInfoCacheManager pluginInfoCacheManager;

    @Autowired
    private KerberosService kerberosService;

    /**
     * 组件配置文件映射
     */
    public static Map<Integer, List<String>> componentTypeConfigMapping = new HashMap<>(2);

    @javax.annotation.Resource
    private ThreadPoolTaskExecutor commonExecutor;

    @Autowired
    private ConsoleComponentUserService consoleComponentUserService;

    static {
        //hdfs core 需要合并
        componentTypeConfigMapping.put(EComponentType.HDFS.getTypeCode(), Lists.newArrayList("hdfs-site.xml", "core-site.xml"));
        componentTypeConfigMapping.put(EComponentType.YARN.getTypeCode(), Lists.newArrayList("yarn-site.xml","core-site.xml"));
    }

    @PostConstruct
    public void init() {
        rdosSubscribe.setCallBack((pair) -> {
            if (RdosTopic.CONSOLE.equalsIgnoreCase(pair.getKey())) {
                componentConfigService.clearComponentCache();
                clusterService.clearStandaloneCache();
            }
        });
    }

    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long dtUicTenantId, Integer engineType, Map<Integer, String> componentVersionMap) {

        List<ComponentsConfigOfComponentsVO> componentsVOS = Lists.newArrayList();
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);
        if (clusterId == null) {
            return componentsVOS;
        }

        // 目前只取租户下集群组件默认版本，如果需要取出特定版本，需要从componentVersionMap中取出指定版本
        List<Component> componentList = componentDao.listByClusterId(clusterId, null, true);
        for (Component component : componentList) {
            ComponentsConfigOfComponentsVO componentsConfigOfComponentsVO = new ComponentsConfigOfComponentsVO();
            componentsConfigOfComponentsVO.setComponentTypeCode(component.getComponentTypeCode());
            String componentConfig = getComponentByClusterId(clusterId, component.getComponentTypeCode(), false, String.class, componentVersionMap);

            componentsConfigOfComponentsVO.setComponentConfig(componentConfig);
            componentsVOS.add(componentsConfigOfComponentsVO);
        }
        return componentsVOS;
    }

    public Component getOne(Long id) {
        Component component = componentDao.getOne(id);
        if (component == null) {
            throw new RdosDefineException(ErrorCode.COMPONENT_NOT_EXISTS);
        }
        return component;
    }

    /**
     * 更新缓存
     */
    public void updateCache(Long clusterId, Integer componentCode) {
        Set<Long> dtUicTenantIds = tenantService.findUicTenantIdsByClusterId(clusterId);
        this.updateCache(clusterId, componentCode, dtUicTenantIds, true);
    }

    /**
     * 更新缓存
     * @param clusterId
     * @param componentCode
     * @param dtUicTenantIds
     * @param pushToPublicService 是否推送至业务中心
     */
    public void updateCache(Long clusterId, Integer componentCode, Set<Long> dtUicTenantIds, boolean pushToPublicService) {
        componentConfigService.clearComponentCache();
        clusterService.clearStandaloneCache();
        pluginInfoCacheManager.clearPluginInfoClusterCache(clusterId);
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return;
        }
        if (componentCode != null && pushToPublicService) {
            dataSourceService.publishComponent(clusterId, componentCode, dtUicTenantIds);
        }
        //缓存刷新
        for (Long uicTenantId : dtUicTenantIds) {
            consoleCache.publishRemoveMessage(uicTenantId.toString());
        }
    }

    public void asyncUpdateCache(Long clusterId, Integer componentCode, Set<Long> dtUicTenantIds, boolean pushToPublicService) {
        try {
            updateCache(clusterId, componentCode, dtUicTenantIds, pushToPublicService);
        } catch (Exception e) {
            // 入参可能为 null, 日志需防止 NPE
            LOGGER.error("updateCache error, clusterId:{}, componentCode:{}, dtUicTenantIds:{}, pushToPublicService:{}",
                    String.valueOf(clusterId), String.valueOf(componentCode), String.valueOf(dtUicTenantIds), pushToPublicService,
                    e);
        }
    }

    private Map<String, Map<String, Object>> parseUploadFileToMap(List<Resource> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        // 拿到一个 zip 包
        Resource resource = resources.get(0);
        if (!resource.getFileName().endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException(ErrorCode.FILE_NOT_ZIP);
        }
        // 本地 unzip 目录
        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            Map<String, Map<String, Object>> confMap = new HashMap<>();
            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            // zip 包解压到 unzip 目录，得到细分的 file
            List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
            if (CollectionUtils.isEmpty(xmlFiles)) {
                throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
            }
            for (File file : xmlFiles) {
                Map<String, Object> fileMap = null;
                if (file.getName().startsWith(".")) {
                    //.开头过滤
                    continue;
                }
                if (file.getName().endsWith("xml")) {
                    //xml文件
                    fileMap = Xml2JsonUtil.xml2map(file);
                } else if (file.getName().endsWith("json")) {
                    //json文件 --> jsonStr
                    String jsonStr = Xml2JsonUtil.readFile(file);
                    if (StringUtils.isBlank(jsonStr)) {
                        continue;
                    }
                    fileMap = (Map<String, Object>) JSONObject.parseObject(jsonStr, Map.class);
                }
                if (null != fileMap) {
                    // 归集所有文件的内容到一个 map 中
                    confMap.put(file.getName(), fileMap);
                }
            }
            return confMap;
        } catch (Exception e) {
            LOGGER.error("parseAndUploadXmlFile file error ", e);
            throw new RdosDefineException(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (StringUtils.isNotBlank(upzipLocation)) {
                ZipUtil.deletefile(upzipLocation);
            }
        }
    }


    private File getFileWithSuffix(String dir, String suffix) {
        if (StringUtils.isBlank(suffix)) {
            throw new RdosDefineException(ErrorCode.FILE_SUFFIX_EMPTY);
        }
        File file = null;
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (files.length > 0) {
                file = Arrays.stream(files).filter(f -> f.getName().endsWith(suffix)).findFirst().orElse(null);
            }
        }
        return file;
    }


    private List<PrincipalName> getPrincipal(File file) {
        if (null != file) {
            Keytab keytab = null;
            try {
                keytab = Keytab.loadKeytab(file);
            } catch (IOException e) {
                LOGGER.error("Keytab loadKeytab error ", e);
                throw new RdosDefineException(ErrorCode.KEYTAB_FILE_PARSE_ERROR);
            }
            return keytab.getPrincipals();
        }
        throw new RdosDefineException(ErrorCode.KEYTAB_FILE_NOT_CONTAINS_PRINCIPAL);
    }

    private void unzipKeytab(String localKerberosConf, Resource resource) {
        try {
            ZipUtil.upzipFile(resource.getUploadedFileName(), localKerberosConf);
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(new File(localKerberosConf));
            } catch (IOException ioException) {
                LOGGER.error("delete zip directory {} error ", localKerberosConf);
            }
        }
    }


    public KerberosConfig getKerberosConfig(Long clusterId, Integer componentType, String componentVersion) {
        return kerberosDao.getByComponentType(clusterId, componentType, ComponentVersionUtil.formatMultiVersion(componentType, componentVersion));
    }


    @Transactional(rollbackFor = Exception.class)
    public String uploadKerberos(List<Resource> resources, Long clusterId, Integer componentCode, String componentVersion) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException(ErrorCode.FILE_NOT_UPLOAD);
        }

        Resource resource = resources.get(0);
        String kerberosFileName = resource.getFileName();
        if (!kerberosFileName.endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException(ErrorCode.FILE_NOT_ZIP);
        }
        String sftpComponent = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
        SftpConfig sftpConfig = getSFTPConfig(sftpComponent, componentCode, "");
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);

        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, componentCode,ComponentVersionUtil.getComponentVersion(componentVersion));
        Component addComponent = new ComponentDTO();
        addComponent.setComponentTypeCode(componentCode);
        addComponent.setHadoopVersion(ComponentVersionUtil.getComponentVersion(componentVersion));
        updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource, null, null, true);

        String localPath = kerberosService.getLocalKerberosPath(clusterId, addComponent.getComponentTypeCode());
        String localConfPath = localPath + File.separator + KRB5_CONF;
        return Krb5FileUtil.convertMapToString(Krb5FileUtil.readKrb5ByPath(localConfPath));
    }

    public String mergeKrb5() {
        List<KerberosConfig> kerberosConfigs = kerberosDao.listAll();
        return mergeKrb5(kerberosConfigs);
    }

    private synchronized String mergeKrb5(List<KerberosConfig> kerberosConfigs) {
        String mergeKrb5Content = "";
        if (CollectionUtils.isEmpty(kerberosConfigs)) {
            LOGGER.error("KerberosConfigs is null");
            return mergeKrb5Content;
        }

        String mergeDirPath = ConfigConstant.LOCAL_KRB5_MERGE_DIR_PARENT + ConfigConstant.SP + UUID.randomUUID();
        List<Long> clusterDownloadRecords = new ArrayList();
        try {
            String oldMergeKrb5Content = null;
            String mergeKrb5Path = mergeDirPath + ConfigConstant.SP + ConfigConstant.MERGE_KRB5_NAME;
            for (KerberosConfig kerberosConfig : kerberosConfigs) {
                String krb5Name = kerberosConfig.getKrbName();
                String remotePath = kerberosConfig.getRemotePath();
                Long clusterId = kerberosConfig.getClusterId();
                Integer componentCode = kerberosConfig.getComponentType();

                if (StringUtils.isNotEmpty(kerberosConfig.getMergeKrbContent()) && StringUtils.isEmpty(oldMergeKrb5Content)) {
                    oldMergeKrb5Content = kerberosConfig.getMergeKrbContent();
                }

                String remoteKrb5Path = remotePath + ConfigConstant.SP + krb5Name;
                String localKrb5Path = mergeDirPath + remoteKrb5Path;
                try {
                    String sftpComponent = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
                    SftpConfig sftpConfig = getSFTPConfig(sftpComponent, componentCode, "");
                    SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
                    if (clusterDownloadRecords.contains(clusterId)) {
                        continue;
                    }
                    boolean downRes = sftpFileManage.downloadFile(remoteKrb5Path, localKrb5Path);
                    LOGGER.info("download remoteKrb5Path[{}] result {}", remoteKrb5Path, downRes);
                    if (downRes) {
                        clusterDownloadRecords.add(clusterId);
                        if (!new File(mergeKrb5Path).exists()) {
                            FileUtils.copyFile(new File(localKrb5Path), new File(mergeKrb5Path));
                            mergeKrb5Content = Krb5FileUtil.convertMapToString(Krb5FileUtil.readKrb5ByPath(mergeKrb5Path));
                            continue;
                        }
                        mergeKrb5Content = Krb5FileUtil.mergeKrb5ContentByPath(mergeKrb5Path, localKrb5Path);
                    }
                } catch (Exception e) {
                    LOGGER.error("merge krb5.conf[{}] error : {}", localKrb5Path, e.getMessage());
                }
            }
            if (StringUtils.isNotEmpty(oldMergeKrb5Content)) {
                mergeKrb5Content = Krb5FileUtil.resetMergeKrb5Content(oldMergeKrb5Content, mergeKrb5Content);
            }
            LOGGER.info("mergeKrb5Content is {}", mergeKrb5Content);
        } catch (Exception e) {
            LOGGER.error("Merge krb5 error! {}", e.getMessage());
            throw new RdosDefineException(ErrorCode.MERGE_KRB5_ERROR);
        } finally {
            try {
                File mergeDir = new File(mergeDirPath);
                FileUtils.deleteDirectory(mergeDir);
            } catch (Exception e) {
            }
        }
        return mergeKrb5Content;
    }

    @Transactional(rollbackFor = Exception.class)
    public void uploadSSL(Resource resource, Long clusterId, Integer componentCode, String componentVersion) {
        SftpConfig sftpConfig = getSftpConfig(clusterId);
        if (sftpConfig == null) {
            throw new RdosDefineException(ErrorCode.SFTP_SERVER_NOT_CONFIG);
        }
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);

        // CONSOLE_clusetName/componentName
        String componentPath = buildComponentPath(clusterId, componentCode);
        // CONSOLE_clusetName/componentName/ssl/client
        String sslPath = buildSSLPath(componentPath);

        // userDir/sslUploadTempDir/CONSOLE_clusetName/componentName/ssl/client
        String localSSLDir = getLocalSSLDir(sslPath);
        // 将 zip 包解压到 localSSLDir，获取到 ssl-client.xml、truststore.jks 两个文件
        List<File> files = decompressSSLFile(resource, localSSLDir);
        hasTwoFiles(files);

        // 获取远端地址： sftpPath/CONSOLE_clusetName/componentName/ssl/client
        String remoteSSLDir = getRemoteSSLDir(sftpConfig, sslPath);
        LOGGER.info("Remote ssl path:{}", remoteSSLDir);
        File truststoreFile = getTruststoreFile(files);
        File sslClientFile = getSSLClientFile(files);
        checkSSLClientFile(sslClientFile, truststoreFile);
        // 将解压后的两个文件上传到远端：sftpPath/CONSOLE_clusetName/componentName/ssl/client
        transferToSftp(Arrays.asList(truststoreFile, sslClientFile), sftpFileManage, remoteSSLDir);

        String formattedComponentVersion = formatVersion(componentCode, componentVersion);
        updateSSL(clusterId, componentCode,
                formattedComponentVersion, remoteSSLDir, truststoreFile, sslClientFile);
    }

    private SftpConfig getSftpConfig(Long clusterId) {
        String sftpComponent = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
        return JSONObject.parseObject(sftpComponent, SftpConfig.class);
    }

    private String getLocalSSLDir(String sslPath) {
        StringBuilder res = new StringBuilder(env.getLocalSSLDir());
        res.append(File.separator);
        res.append(sslPath);
        return res.toString();
    }

    private List<File> decompressSSLFile(Resource resource, String localSSLDir) {
        File localSSLFile = new File(localSSLDir);
        //删除本地文件夹
        try {
            FileUtils.deleteDirectory(localSSLFile);
        } catch (IOException e) {
            LOGGER.error("delete old ssl directory {} error", localSSLDir, e);
        }

        //解压到本地
        List<File> files = ZipUtil.upzipFile(resource.getUploadedFileName(), localSSLDir);
        if (CollectionUtils.isEmpty(files)) {
            throw new RdosDefineException(ErrorCode.FILE_DECOMPRESSION_ERROR);
        }

        return listFiles(Collections.singletonList(localSSLFile), 0);
    }

    private void hasTwoFiles(List<File> files) {
        if (files.size() != 2) {
            throw new RdosDefineException(ErrorCode.FILE_COUNT_ERROR);
        }
    }

    private List<File> listFiles(List<File> dirs, int level) {

        if (dirs.isEmpty() || level > 2) {
            return Collections.emptyList();
        }

        List<File> res = new ArrayList<>();
        List<File> subDirs = new ArrayList<>();
        for (File d : dirs) {
            File[] subFiles = d.listFiles();
            if (subFiles != null) {
                for (File sub : subFiles) {
                    if (sub.isDirectory()) {
                        subDirs.add(sub);
                    } else {
                        res.add(sub);
                    }
                }
            }
        }

        if (!subDirs.isEmpty()) {
            res.addAll(listFiles(subDirs, level + 1));
        }

        return res;
    }

    private String getRemoteSSLDir(SftpConfig sftpConfig, String sslPath) {
        StringBuilder res = new StringBuilder(sftpConfig.getPath());
        res.append(File.separator);
        res.append(sslPath);
        return res.toString();
    }

    /**
     * @param clusterId
     * @param componentCode
     * @return 形如 CONSOLE_clusetName/componentName
     */
    public String buildComponentPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        StringBuilder res = new StringBuilder();
        res.append(AppType.CONSOLE).append("_").append(one.getClusterName());
        res.append(File.separator);
        res.append(EComponentType.getByCode(componentCode).name());
        return res.toString();
    }

    private String buildSSLPath(String componentPath) {
        StringBuilder res = new StringBuilder(componentPath);
        res.append(File.separator);
        res.append(SSL_CLIENT_PATH);
        return res.toString();
    }

    private File getTruststoreFile(List<File> files) {
        File truststoreFile = files.stream()
                .filter(f -> !f.getName().endsWith(XML_SUFFIX))
                .findFirst().orElse(null);
        if (truststoreFile == null) {
            throw new RdosDefineException(ErrorCode.FILE_MISS);
        }
        LOGGER.info("truststore Unzip fileName:{}", truststoreFile.getAbsolutePath());
        return truststoreFile;
    }

    private File getSSLClientFile(List<File> files) {
        File sslClientFile = files.stream()
                .filter(f -> f.getName().endsWith(XML_SUFFIX))
                .findFirst().orElse(null);
        if (sslClientFile == null) {
            throw new RdosDefineException(ErrorCode.XML_FILE_MISS);
        }
        LOGGER.info("xml Unzip fileName:{}", sslClientFile.getAbsolutePath());
        return sslClientFile;
    }

    /**
     * 校验 ssl 文件和 truststore 文件的匹配关系
     *
     * @param xmlFile
     * @param truststoreFile
     */
    private void checkSSLClientFile(File xmlFile, File truststoreFile) {
        try {
            Map<String, Object> map = Xml2JsonUtil.xml2map(xmlFile);
            for (String config : REQUIRED_SSL_CONFIGS) {
                Object value = map.get(config);
                if (value == null || StringUtils.isBlank(value.toString())) {
                    throw new RdosDefineException(ErrorCode.FILE_MISS.getMsg() + ":(" + config + ")");
                }
            }
            // ssl.client.truststore.location -> truststore.jks
            String location = map.get(LOCATION_SSH_CONFIG).toString().trim();
            if (!location.equals(truststoreFile.getName())) {
                // 优化：文件中 ssl.client.truststore.location 的值可能带路径前缀，形如 /var/lib/truststore.jks，需去除路径
                String[] splitStr = location.split(File.separator);
                boolean containsTrustStoreFileName = Arrays.asList(splitStr).contains(truststoreFile.getName());
                if (containsTrustStoreFileName) {
                    Xml2JsonUtil.operatorXml(xmlFile, (root, xmlKeys) -> {
                        if (xmlKeys.contains(LOCATION_SSH_CONFIG)) {
                            Xml2JsonUtil.removePropertyNode(root, LOCATION_SSH_CONFIG);
                        }
                        Map<String, Object> overrideParam = new HashMap<>();
                        overrideParam.put(LOCATION_SSH_CONFIG, truststoreFile.getName());
                        Xml2JsonUtil.appendPropertyNode(root, overrideParam);
                    });
                } else {
                    throw new RdosDefineException(
                            String.format(
                                    "Property %s is not the name of truststore file.", location));
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.FILE_PARSE_ERROR.getMsg() + ":" + xmlFile.getAbsolutePath(), e);
        }
    }

    private void transferToSftp(List<File> files, SftpFileManage sftpFileManage, String remoteSSLDir) {
        sftpFileManage.deleteDir(remoteSSLDir);
        files.forEach(f -> {
            LOGGER.info("upload sftp file:{}", f.getAbsolutePath());
            sftpFileManage.uploadFile(remoteSSLDir, f);
        });
    }

    public static String formatVersion(Integer componentCode, String componentVersion) {
        String res = ComponentVersionUtil.getComponentVersion(componentVersion);
        return ComponentVersionUtil.formatMultiVersion(componentCode, res);
    }

    /**
     * 新增或修改 console_ssl 配置信息
     *
     * @param clusterId
     * @param componentCode
     * @param formattedVersion
     * @param remoteSSLDir
     * @param truststoreFile
     * @param sslClientFile
     */
    private void updateSSL(Long clusterId, Integer componentCode,
                           String formattedVersion, String remoteSSLDir,
                           File truststoreFile, File sslClientFile) {
        ConsoleSSL sslConfig = getSSLConfig(clusterId, componentCode, formattedVersion);
        boolean isFirstOpenSSL = false;
        if (Objects.isNull(sslConfig)) {
            sslConfig = new ConsoleSSL();
            sslConfig.setComponentVersion(formattedVersion);
            isFirstOpenSSL = true;
        }
        sslConfig.setRemotePath(remoteSSLDir);
        sslConfig.setClusterId(clusterId);
        sslConfig.setComponentType(componentCode);
        sslConfig.setTruststore(truststoreFile.getName());
        sslConfig.setSslClient(sslClientFile.getName());
        sslConfig.setMd5(MD5Util.getMd5StringFromFiles(truststoreFile, sslClientFile));
        if (isFirstOpenSSL) {
            consoleSSLDao.insert(sslConfig);
        } else {
            consoleSSLDao.update(sslConfig);
        }
    }

    public ConsoleSSL getSSLConfig(Long clusterId, Integer componentCode, String componentVersion) {
        List<ConsoleSSL> consoleSSLs = consoleSSLDao.getByClusterIdAndComponentTypeAndComponentVersion(
                clusterId, componentCode, ComponentVersionUtil.formatMultiVersion(componentCode, componentVersion));
        if (CollectionUtils.isEmpty(consoleSSLs)) {
            return null;
        }
        if (consoleSSLs.size() > 1) {
            LOGGER.error(
                    "Too many ssl config for clusterId: " + clusterId +
                            " componentCode: " + componentCode +
                            " componentVersion: " + componentVersion);
            throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_ERROR);
        }
        return consoleSSLs.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateKrb5Conf(String krb5Content) {
        Krb5FileUtil.checkKrb5Content(krb5Content);
        Integer affectedRows = kerberosDao.updateAllKrb5Content(krb5Content);
        LOGGER.info("updateKrb5Conf ok, affectedRows:{}", affectedRows);
    }

    @Transactional(rollbackFor = Exception.class)
    public AddOrUpdateComponentResult addOrUpdateComponent(Long clusterId, String componentConfig,
                                            List<Resource> resources, String versionName,
                                            String kerberosFileName, String componentTemplate,
                                            Integer componentCode, Integer storeType,
                                            String principals, String principal,
                                            boolean isMetadata, Boolean isDefault,
                                            Integer deployType, String sslFileName) {
        if (null == componentCode) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        if (null == clusterId) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        if (CollectionUtils.isNotEmpty(resources) && resources.size() >= 2 && StringUtils.isBlank(kerberosFileName)) {
            //上传二份文件 需要kerberosFileName文件名字段
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        EComponentType type = EComponentType.getByCode(componentCode);
        EComponentType storeComponent = null == storeType ? null : EComponentType.getByCode(storeType);
        PartCluster partCluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        Part part = partCluster.create(type, versionName, storeComponent, deployType);
        String versionValue = part.getVersionValue();
        String pluginName = part.getPluginName();

        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setComponentTypeCode(componentCode);
        Cluster cluster = clusterDao.getOne(clusterId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        //校验引擎是否添加
        EComponentType componentType = EComponentType.getByCode(componentDTO.getComponentTypeCode());
        if (EComponentType.deployTypeComponents.contains(componentType) && null == deployType) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS.getMsg() + ":deployType");
        }
        Component addComponent = new ComponentDTO();
        BeanUtils.copyProperties(componentDTO, addComponent);
        // 判断是否是更新组件, 需要校验组件版本
        Component dbComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), ComponentVersionUtil.isMultiVersionComponent(componentCode) ? versionValue : null, deployType);
        String dbHadoopVersion = "";
        boolean isUpdate = false;
        boolean isOpenKerberos = isOpenKerberos(kerberosFileName, dbComponent);
        ConsoleSSL sslConfig = this.getSSLConfig(clusterId, componentType.getTypeCode(), ComponentVersionUtil.isMultiVersionComponent(componentCode) ? versionValue : null);

        if (null != dbComponent) {
            //更新
            dbHadoopVersion = dbComponent.getHadoopVersion();
            BeanUtils.copyProperties(dbComponent, addComponent);
            isUpdate = true;
        }
        componentConfig = this.checkKubernetesConfig(componentConfig, resources, componentType, dbComponent);

        EComponentType storesComponent = this.checkStoresComponent(clusterId, storeType);
        addComponent.setStoreType(storesComponent.getTypeCode());
        addComponent.setHadoopVersion(versionValue);
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setDeployType(deployType);
        addComponent.setIsMetadata(BooleanUtils.toInteger(isMetadata));
        if (EComponentType.HDFS == componentType) {
            //hdfs的组件和yarn组件的版本保持强一致 如果是k8s-hdfs2-则不作限制
            Component yarnComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.YARN.getTypeCode(), null, null);
            if (null != yarnComponent) {
                versionName = yarnComponent.getVersionName();
            }
        }
        addComponent.setVersionName(versionName);

        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }
        if (StringUtils.isNotBlank(sslFileName)) {
            addComponent.setSslFileName(sslFileName);
        }

        changeDefault(BooleanUtils.isTrue(isDefault), clusterId, componentType, addComponent);

        // md5zip 会作为 config 表的一个属性
        String md5Key = updateResource(clusterId, componentConfig, resources, kerberosFileName, componentCode, principals, principal, addComponent, dbComponent);
        addComponent.setClusterId(clusterId);
        boolean yarnVersionChanged = false;
        if (isUpdate) {
            componentDao.update(addComponent);
            yarnVersionChanged = EComponentType.YARN.equals(componentType) && !noNeedRefreshVersion(addComponent, dbHadoopVersion, dbComponent);
            refreshVersion(componentType, clusterId, addComponent, dbHadoopVersion, dbComponent);
        } else {
            componentDao.insert(addComponent);
        }
        clusterDao.updateGmtModified(clusterId);

        changeMetadata(componentType.getTypeCode(), isMetadata, clusterId, addComponent.getIsMetadata());
        List<ClientTemplate> clientTemplates = this.wrapperConfig(
                componentType, componentConfig, isOpenKerberos, md5Key, componentTemplate, sslConfig, pluginName,versionName);

        componentConfigService.addOrUpdateComponentConfig(clientTemplates, addComponent.getId(), addComponent.getClusterId(), componentCode, (configs -> {
            //保存的时候 sm2 需要反解密
            configs.forEach(c -> c.setValue(scheduleDictService.decryptIfNecessary(c)));
        }));

        return AddOrUpdateComponentResult.AddOrUpdateComponentResultBuilder.builder()
                .cluster(cluster)
                .isUpdate(isUpdate)
                .yarnVersionChanged(yarnVersionChanged)
                .refreshComponent(addComponent)
                .principals(principals)
                .principal(principal)
                .build();
    }

    /**
     * 新增或修改组件后的操作，用于推送业务中心等第三方交互
     */
    public void postAddOrUpdateComponent(AddOrUpdateComponentResult addOrUpdateComponentResult) {
        Cluster cluster = addOrUpdateComponentResult.getCluster();
        Component refreshComponent = addOrUpdateComponentResult.getRefreshComponent();
        if (cluster == null || refreshComponent == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }

        boolean isUpdate = addOrUpdateComponentResult.isUpdate();
        boolean yarnVersionChanged = addOrUpdateComponentResult.isYarnVersionChanged();

        Integer componentTypeCode = refreshComponent.getComponentTypeCode();
        Long clusterId = cluster.getId();
        String clusterName = cluster.getClusterName();

        this.updateCache(clusterId, componentTypeCode);
        CrudEnum crudEnum = isUpdate ? CrudEnum.UPDATE : CrudEnum.ADD;

        dataSourceService.syncLdapDir(componentTypeCode, clusterId, clusterName);
        // 集群已经绑定了租户的情况下，此时租户新增了 ldap/ranger，需要对这些租户进行处理
        dataSourceService.bindHistoryLdapIfNeeded(componentTypeCode, clusterId, crudEnum);
        // ranger 新增，推送历史 hive、trino 组件信息
        this.syncHistoryInfoToRangerIfNeeded(clusterId, componentTypeCode, crudEnum);
        if (yarnVersionChanged) {
            this.syncHdfsToRangerIfUpdateYarn(clusterId);
        }
    }

    /**
     * 更新完毕返回组件信息(抽取原有逻辑，保证兼容性)
     *
     * @param addOrUpdateComponentResult
     * @return
     */
    public ComponentVO returnAfterProcessComponent(AddOrUpdateComponentResult addOrUpdateComponentResult) {
        Component refreshComponent = addOrUpdateComponentResult.getRefreshComponent();
        Cluster cluster = addOrUpdateComponentResult.getCluster();
        // 此时不需要查询默认版本
        List<IComponentVO> componentVos = componentConfigService.getComponentVoByComponent(Lists.newArrayList(refreshComponent), true, cluster.getId(), false, true);
        if (CollectionUtils.isNotEmpty(componentVos)) {
            ComponentVO componentVO = componentVos.get(0).getComponent(refreshComponent.getHadoopVersion());
            componentVO.setClusterName(cluster.getClusterName());
            componentVO.setPrincipal(addOrUpdateComponentResult.getPrincipal());
            componentVO.setPrincipals(addOrUpdateComponentResult.getPrincipals());
            componentVO.setDeployType(refreshComponent.getDeployType());
            componentVO.setIsMetadata(refreshComponent.getIsMetadata());
            return componentVO;
        }
        return null;
    }

    /**
     * yarn 版本变更，需要同步 hdfs 信息到 ranger
     * @param clusterId
     */
    private void syncHdfsToRangerIfUpdateYarn(Long clusterId) {
        boolean openSecurity = clusterService.hasRangerAndLdap(clusterId);
        if (!openSecurity) {
            return;
        }
        Component hdfsComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.HDFS.getTypeCode(), null, null);
        if (hdfsComponent == null) {
            return;
        }
        this.syncToRangerIfNeeded(clusterId, hdfsComponent, CrudEnum.UPDATE);
    }

    /**
     * 同步至数据安全
     *
     * @param componentId
     * @return
     */
    public boolean syncDataSecurity(Long componentId) {
        // 1) 获取组件信息
        Component component = componentDao.getOne(componentId);
        if (component == null) {
            throw new RdosDefineException(ErrorCode.COMPONENT_NOT_EXISTS);
        }

        Integer componentTypeCode = component.getComponentTypeCode();
        if (!DataSourceService.SYNC_TO_RANGER_COMPONENTS.contains(componentTypeCode)) {
            throw new RdosDefineException("该组件无需同步至数据安全");
        }
        Long clusterId = component.getClusterId();
        boolean openSecurity = clusterService.hasRangerAndLdap(clusterId);
        if (!openSecurity) {
            throw new RdosDefineException("未开启数据安全，无需同步");
        }
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(componentId, false);
        if (CollectionUtils.isEmpty(componentConfigs)) {
            throw new RdosDefineException("获取配置信息为空，无需同步，请保存后重试");
        }

        // 2) 校验配置信息
        Map<String, Object> key2ValueMap = ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
        componentConfigService.validateParamForSync2Ranger(key2ValueMap, componentTypeCode);

        // 3) 推送
        this.syncToRangerIfNeeded(clusterId, component, CrudEnum.UPDATE);
        return true;
    }

    /**
     * 是否存在数据安全策略
     *
     * @param componentId
     * @return true 表示存在
     */
    public boolean hasSecurityPolicy(Long componentId) {
        Component component = componentDao.getOne(componentId);
        if (component == null) {
            return false;
        }

        Integer componentTypeCode = component.getComponentTypeCode();
        if (!DataSourceService.SYNC_TO_RANGER_COMPONENTS.contains(componentTypeCode)) {
            // 不属于数据安全组件，视为无安全策略
            return false;
        }
        Long clusterId = component.getClusterId();
        boolean openSecurity = clusterService.hasRangerAndLdap(clusterId);
        if (!openSecurity) {
            // 未开启数据安全，视为无安全策略
            return false;
        }

        Set<Long> dtUicTenantIds = tenantService.findUicTenantIdsByClusterId(clusterId);
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            // 集群未绑定租户，视为无安全策略
            return false;
        }

        // 调用 uic 接口，判断是否存在安全策略
        return dataSourceService.serviceHasPolicy(component, dtUicTenantIds);
    }

    /**
     * @param isDefault
     * @param clusterId
     * @param componentType
     */
    private void changeDefault(boolean isDefault, Long clusterId, EComponentType componentType, Component updateComponent) {
        if (!ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode())) {
            updateComponent.setIsDefault(true);
            return;
        }
        updateComponent.setIsDefault(isDefault);
        List<Component> dbComponents = componentDao.listByClusterId(clusterId, componentType.getTypeCode(), false);
        List<Component> otherComponent = dbComponents.stream()
                .filter(component -> !component.getId().equals(updateComponent.getId()))
                .collect(Collectors.toList());
        if (!isDefault) {
            if (otherComponent.size() == 0) {
                // single component must be default
                updateComponent.setIsDefault(true);
            }
        }
        List<String> reserveComponentVersion = otherComponent.stream()
                .map(Component::getHadoopVersion)
                .collect(Collectors.toList());

        changeTaskVersion(clusterId, componentType, updateComponent, reserveComponentVersion);

        List<Component> otherDefaultComponent =
                componentDao.listByClusterId(clusterId, componentType.getTypeCode(), true)
                        .stream().filter(component -> !component.getId().equals(updateComponent.getId())).collect(Collectors.toList());

        if (isDefault) {
            // 将当前组件的其它版本 is_default 设为 false
            componentDao.updateDefault(clusterId, componentType.getTypeCode(), false, null);
            return;
        }

        // 取消默认版本，且不存在默认版本，设置一个默认版本。
        if (CollectionUtils.isEmpty(otherDefaultComponent)) {
            // 这里产品上没有确定默认版本大逻辑，选择最近添加大作为默认版本 todo
            List<Component> collect = otherComponent.stream().sorted(Comparator.comparing(Component::getId)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                componentDao.updateDefault(clusterId, componentType.getTypeCode(), true, collect.get(0).getId());
            }
        }
    }

    /**
     * 历史任务已经选择了组件版本 该组件删除后 切换成保留组件的版本
     *
     * @param clusterId
     * @param componentType
     * @param updateComponent
     * @param reserveComponentVersion
     */
    public void changeTaskVersion(Long clusterId, EComponentType componentType, Component updateComponent, List<String> reserveComponentVersion) {
        List<Long> dtUicTenantIdByIds = clusterTenantDao.listBoundedTenants(clusterId);
        if (updateComponent.getIsDefault() && CollectionUtils.isNotEmpty(dtUicTenantIdByIds)) {
            //check other component
            DictType dictType = EComponentType.FLINK.equals(componentType) ? DictType.FLINK_VERSION : DictType.SPARK_VERSION;
            //refresh all task to update
            List<ScheduleDict> scheduleDicts = scheduleDictService.listByDictType(dictType);
            List<String> changeTaskShadeComponentVersions = new ArrayList<>(scheduleDicts.size());
            String targetTaskShadeComponentVersion = null;
            for (ScheduleDict scheduleDict : scheduleDicts) {
                if (scheduleDict.getDictValue().equals(updateComponent.getHadoopVersion())) {
                    targetTaskShadeComponentVersion = scheduleDict.getDictName();
                } else if (!reserveComponentVersion.contains(scheduleDict.getDictValue())) {
                    changeTaskShadeComponentVersions.add(scheduleDict.getDictName());
                }
            }
            if (CollectionUtils.isEmpty(changeTaskShadeComponentVersions) || StringUtils.isBlank(targetTaskShadeComponentVersion)) {
                LOGGER.info("change task version {} or target {} is empty", changeTaskShadeComponentVersions, targetTaskShadeComponentVersion);
                return;
            }
            LOGGER.info("change task version {} to [{}] appType {} dtuicTenantId [{}]", changeTaskShadeComponentVersions, targetTaskShadeComponentVersion,
                    env.getComponentRefreshAppType(), dtUicTenantIdByIds);
            scheduleTaskShadeDao.changeTaskVersion(targetTaskShadeComponentVersion, changeTaskShadeComponentVersions, dtUicTenantIdByIds, env.getComponentRefreshAppType());
        }
    }

    /**
     * yarn组件版本变更之后  hdfs组件保存一致
     * 计算组件 如flink的typename也同步变更
     *
     * @param componentType
     * @param clusterId
     * @param addComponent
     * @param dbHadoopVersion 形如 3.1.1
     */
    public void refreshVersion(EComponentType componentType, Long clusterId, Component addComponent, String dbHadoopVersion, Component dbComponent) {
        if (EComponentType.YARN.equals(componentType)) {
            refreshVersionTruly(clusterId, addComponent, dbHadoopVersion, dbComponent);
            refreshHdfsComponentVersion(clusterId, addComponent, dbHadoopVersion, dbComponent);
            return;
        }
        // k8s 搭配 HDFS 存储组件的时候，配合 HDFS 切换版本
        if (EComponentType.HDFS.equals(componentType)) {
            List<Component> components = componentDao.listAllByClusterId(clusterId);
            Component k8sComponent = components.stream().filter(c -> EComponentType.KUBERNETES.getTypeCode().equals(c.getComponentTypeCode())).findFirst().orElse(null);
            if (k8sComponent != null) {
                refreshVersionTruly(clusterId, addComponent, dbHadoopVersion, dbComponent);
                return;
            }
        }
    }

    /**
     * 更新 hdfs 组件版本，保持与 yarn 一致
     * @param clusterId
     * @param addYarnComponent
     * @param dbHadoopVersion
     * @param dbYarnComponent
     */
    private void refreshHdfsComponentVersion(Long clusterId, Component addYarnComponent, String dbHadoopVersion, Component dbYarnComponent) {
        // versionName 和 hadoopVersion 完全一致的时候，就不刷新
        if (noNeedRefreshVersion(addYarnComponent, dbHadoopVersion, dbYarnComponent)) {
            return;
        }
        // 如果存在 hdfs 组件，则刷新版本信息
        Component hdfsComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.HDFS.getTypeCode(), null, null);
        if (hdfsComponent == null) {
            return;
        }

        Component toUpdateHdfsComponent = new Component();
        toUpdateHdfsComponent.setId(hdfsComponent.getId());
        toUpdateHdfsComponent.setVersionName(addYarnComponent.getVersionName());
        toUpdateHdfsComponent.setHadoopVersion(addYarnComponent.getHadoopVersion());
        componentDao.update(toUpdateHdfsComponent);
    }

    private void refreshVersionTruly(Long clusterId, Component addComponent, String dbHadoopVersion, Component dbComponent) {
        // versionName 和 hadoopVersion 完全一致的时候，就不刷新
        if (noNeedRefreshVersion(addComponent, dbHadoopVersion, dbComponent)) {
            return;
        }
        List<Component> components = componentDao.listAllByClusterId(clusterId);
        PartCluster partCluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        for (Component component : components) {
            EComponentType eComponentType = EComponentType.getByCode(component.getComponentTypeCode());
            if (!EComponentType.typeComponentVersion.contains(eComponentType)) {
                continue;
            }
            EComponentType storeComponentType = EComponentType.getByCode(component.getStoreType());
            Part part = partCluster.create(eComponentType, component.getVersionName(), storeComponentType, component.getDeployType());
            String pluginName = part.getPluginName();
            ComponentConfig typeNameComponentConfig = componentConfigService.getComponentConfigByKey(component.getId(), TYPE_NAME_KEY);
            if (null != typeNameComponentConfig && StringUtils.isNotBlank(pluginName)) {
                LOGGER.info("refresh clusterId {} component {} typeName {} to {}", component.getClusterId(), component.getComponentName(), typeNameComponentConfig.getValue(), pluginName);
                typeNameComponentConfig.setValue(pluginName);
                componentConfigService.updateValueComponentConfig(typeNameComponentConfig);
            }
        }
    }

    private boolean noNeedRefreshVersion(Component addComponent, String dbHadoopVersion, Component dbComponent) {
        return addComponent.getHadoopVersion().equals(dbHadoopVersion)
            && addComponent.getVersionName().equals(dbComponent.getVersionName());
    }

    /**
     * 处理hdfs 和yarn的自定义参数
     *
     * @param componentType
     * @param componentTemplate
     * @return
     */
    private List<ClientTemplate> dealXmlCustomControl(EComponentType componentType, String componentTemplate) {
        List<ClientTemplate> extraClient = new ArrayList<>(0);
        if (StringUtils.isBlank(componentTemplate)) {
            return extraClient;
        }
        if (EComponentType.HDFS.getTypeCode().equals(componentType.getTypeCode()) || EComponentType.YARN.getTypeCode().equals(componentType.getTypeCode())) {
            JSONArray keyValues = JSONObject.parseArray(componentTemplate);
            for (int i = 0; i < keyValues.size(); i++) {
                ClientTemplate clientTemplate = ComponentConfigUtils.buildCustom(
                        keyValues.getJSONObject(i).getString("key"),
                        keyValues.getJSONObject(i).getString("value"),
                        EFrontType.CUSTOM_CONTROL.name());
                extraClient.add(clientTemplate);
            }
        }
        return extraClient;
    }


    private String updateResource(Long clusterId, String componentConfig, List<Resource> resources, String kerberosFileName, Integer componentCode, String principals, String principal, Component addComponent, Component dbComponent) {
        //上传资源依赖sftp组件
        String md5zip = "";
        if (CollectionUtils.isNotEmpty(resources)) {
            String sftpConfigStr = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = getSFTPConfig(sftpConfigStr, componentCode, componentConfig);
            md5zip = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent, principals, principal);
        } else if (CollectionUtils.isEmpty(resources) && StringUtils.isNotBlank(principal)) {
            //直接更新认证信息
            String componentVersion = ComponentVersionUtil.isMultiVersionComponent(addComponent.getComponentTypeCode()) ? StringUtils.isNotBlank(addComponent.getHadoopVersion()) ? addComponent.getHadoopVersion() : componentDao.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentCode) : null;
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode(), componentVersion);
            if (null != kerberosConfig) {
                kerberosConfig.setPrincipal(principal);
                kerberosConfig.setPrincipals(principals);
                kerberosService.refreshKerberos(CrudEnum.UPDATE, kerberosConfig, clusterId, componentCode, componentVersion);
            }
        }
        // 若为更新操作，且 HDFS 组件，且此时用户并未上传 resources，则 md5Key 需取旧值，否则后续会被清空掉
        boolean needOldMd5Key = (StringUtils.isEmpty(md5zip)) && (dbComponent != null)
                && EComponentType.HDFS.getTypeCode().equals(dbComponent.getComponentTypeCode())
                && CollectionUtils.isEmpty(resources);
        if (needOldMd5Key) {
            ComponentConfig md5Config = componentConfigDao.listByKey(dbComponent.getId(), MD5_ZIP_KEY);
            if (md5Config != null) {
                md5zip = md5Config.getValue();
            }
        }

        return md5zip;
    }

    private String checkKubernetesConfig(String componentConfig, List<Resource> resources, EComponentType componentType, Component dbComponent) {
        if (EComponentType.KUBERNETES.getTypeCode().equals(componentType.getTypeCode())) {
            if (CollectionUtils.isNotEmpty(resources)) {
                //kubernetes 信息需要自己解析文件
                List<Object> config = this.config(resources, EComponentType.KUBERNETES.getTypeCode(), false, null);
                if (CollectionUtils.isNotEmpty(config)) {
                    componentConfig = (String) config.get(0);
                }
            } else {
                // resources 为空，但此时仍然进入了保存接口，说明通过了前端校验，即此时用的是已经保存的旧配置，仍取原先的配置
                // （备注：由于加密的原因，前端此时传入 componentConfig 为空）
                if (dbComponent != null) {
                    Long dbComponentId = dbComponent.getId();
                    Map<String, Object> config = componentConfigService.convertComponentConfigToMap(dbComponentId, false);
                    componentConfig = (String) config.getOrDefault("kubernetes.context", StringUtils.EMPTY);
                } else {
                    throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
                }
            }
        }
        return componentConfig;
    }

    private boolean isOpenKerberos(String kerberosFileName, Component dbComponent) {
        boolean isOpenKerberos = StringUtils.isNotBlank(kerberosFileName);
        if (!isOpenKerberos) {
            if (null != dbComponent) {
                KerberosConfig componentKerberos = kerberosDao.getByComponentType(dbComponent.getClusterId(), dbComponent.getComponentTypeCode(), ComponentVersionUtil.formatMultiVersion(dbComponent.getComponentTypeCode(), dbComponent.getHadoopVersion()));
                if (componentKerberos != null) {
                    isOpenKerberos = true;
                }
            }
        }
        return isOpenKerberos;
    }

    private EComponentType checkStoresComponent(Long clusterId, Integer storeType) {
        //默认为hdfs
        if (null == storeType) {
            return EComponentType.HDFS;
        }
        EComponentType componentType = EComponentType.getByCode(MathUtil.getIntegerVal(storeType));
        Component storeComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), null, null);
        if (null == storeComponent) {
            throw new RdosDefineException(ErrorCode.PRE_COMPONENT_NOT_EXISTS.getMsg() + ":" + componentType.getName());
        }
        return componentType;
    }

    public static SftpConfig getSFTPConfig(String sftpConfigStr, Integer componentCode, String componentTemplate) {
        if (StringUtils.isBlank(sftpConfigStr)) {
            //  判断componentCode 是否是sftp的配置，如果是上传文件，如果不是 抛异常返回提交配置sftp服务器
            if (EComponentType.SFTP.getTypeCode().equals(componentCode)) {
                // 是sftp的配置
                try {
                    Map<String, Object> configMap = ComponentConfigUtils.convertClientTemplateToMap(JSONArray.parseArray(componentTemplate, ClientTemplate.class));
                    return PublicUtil.mapToObject(configMap, SftpConfig.class);
                } catch (IOException e) {
                    throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_ERROR.getMsg() + ":sftp");
                }
            } else {
                throw new RdosDefineException(ErrorCode.SFTP_SERVER_NOT_CONFIG);
            }
        } else {
            return JSONObject.parseObject(sftpConfigStr, SftpConfig.class);
        }
    }

    private String uploadResourceToSftp(Long clusterId, List<Resource> resources, String kerberosFileName,
                                        SftpConfig sftpConfig, Component addComponent, Component dbComponent, String principals, String principal) {
        //上传配置文件到sftp 供后续下载
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
        String md5zip = "";
        // sftp路径/Console_clusterName/组件名称
        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, addComponent.getComponentTypeCode(),addComponent.getHadoopVersion());
        for (Resource resource : resources) {
            if (!resource.getFileName().equalsIgnoreCase(kerberosFileName) || StringUtils.isBlank(kerberosFileName)) {
                // 根据约定，要么是 kerberos,要么是 zip 配置包，这里一定就是 zip 包
                addComponent.setUploadFileName(resource.getFileName());
            }
            try {
                if (resource.getFileName().equalsIgnoreCase(kerberosFileName)) {
                    // 更新Kerberos信息
                    this.updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource, principals, principal, false);
                } else {
                    LOGGER.info("start upload hadoop config file:{}", resource.getFileName());
                    // qy 上传 zip 包到远端
                    this.updateComponentConfigFile(dbComponent, sftpFileManage, remoteDir, resource);
                    //移除的参数
                    ScheduleDict dict = scheduleDictService.getByNameAndValue(DictType.EXCLUDE_XML_PARAM.type, addComponent.getVersionName(), null, null);
                    List<String> removeKeys = new ArrayList<>();
                    if (null != dict && StringUtils.isNotBlank(dict.getDictValue())) {
                        removeKeys = Arrays.stream(dict.getDictValue().split(","))
                                .collect(Collectors.toList());
                    }

                    if (EComponentType.HDFS.getTypeCode().equals(addComponent.getComponentTypeCode())) {
                        // qy 这里是file_upload的地址
                        String xmlZipLocation = resource.getUploadedFileName();
                        // qy hdfs 对应的 zip 包取了个 md5,md5被塞入到了 config 配置项中
                        md5zip = MD5Util.getFileMd5String(new File(xmlZipLocation));
                        List<String> removeComponentConfig = removeKeys;
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource, file ->
                                removeXmlParam(file, removeComponentConfig, dict == null ? "" : dict.getDependName())
                        );
                    }
                    if (EComponentType.YARN.getTypeCode().equals(addComponent.getComponentTypeCode())) {
                        // qy YARN 可能会添加一些自定义参数到 xml 文件中
                        List<ComponentConfig> extraComponentConfig = scheduleDictService
                                .loadExtraComponentConfig(addComponent.getHadoopVersion(), addComponent.getComponentTypeCode());
                        List<String> removeComponentConfig = removeKeys;
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource, file -> {
                            //yarn-site.xml添加自定义参数
                            String yarnSiteName = componentTypeConfigMapping.get(EComponentType.YARN.getTypeCode()).get(0);
                            if (file.getName().equals(yarnSiteName) && !CollectionUtils.isEmpty(extraComponentConfig)) {
                                Map<String, Object> configMap = ComponentConfigUtils.convertComponentConfigToMap(extraComponentConfig);
                                try {
                                    Xml2JsonUtil.operatorXml(file, (root, xmlKeys) -> Xml2JsonUtil.appendPropertyNode(root, configMap));
                                } catch (Exception e) {
                                    LOGGER.info("file path {} add extra config {} info error ", file.getPath(), configMap, e);
                                }
                            }

                            removeXmlParam(file, removeComponentConfig, dict == null ? "" : dict.getDependName());
                        });
                    }
                }
            } catch (Exception e) {
                LOGGER.error("update component resource {}  error", resource.getUploadedFileName(), e);
                if (e instanceof RdosDefineException) {
                    throw (RdosDefineException) e;
                } else {
                    throw new RdosDefineException(ErrorCode.COMPONENT_UPDATE_ERROR);
                }
            } finally {
                FileUtil.del(resource.getUploadedFileName());
            }
        }
        return md5zip;
    }

    private void removeXmlParam(File file, List<String> removeKeys,String fileName) {
        if (file.getName().equals(fileName) && CollectionUtils.isNotEmpty(removeKeys)) {
            try {
                Xml2JsonUtil.operatorXml(file, (root, xmlKeys) -> {
                    for (String key : removeKeys) {
                        Xml2JsonUtil.removePropertyNode(root, key);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("remove xml key {} error", file.getPath());
            }
        }
    }

    /**
     * 上传四个xml到sftp 作为spark 作为confHdfsPath
     *
     * @param clusterId
     * @param resource
     */
    private void updateConfigToSftpPath(Long clusterId, SftpConfig sftpConfig, SftpFileManage sftpFileManage, Resource resource,Consumer<File> uploadHook) {
        //上传xml到对应路径下 拼接confHdfsPath
        String confRemotePath = sftpConfig.getPath() + File.separator;
        // /confPath/CONSOLE_clusterName
        String buildPath = File.separator + buildConfRemoteDir(clusterId);
        // 本地路径: user.dir/confPath/CONSOLE_clusterName
        String confPath = System.getProperty("user.dir") + buildPath;
        File localFile = new File(confPath);
        try {
            //删除本地目录
            FileUtils.forceDelete(localFile);
        } catch (IOException e) {
            LOGGER.info("delete  local path  {} error ", localFile, e);
        }
        //解压到本地，这里的 resource 其实是一个 zip 包，将 resource --> confPath
        this.unzipKeytab(confPath, resource);
        if (localFile.isDirectory()) {
            File xmlFile = this.getFileWithSuffix(localFile.getPath(), ".xml");
            File dirFiles = null;
            if (null == xmlFile) {
                //包含文件夹目录
                File[] files = localFile.listFiles();
                if (null != files && files.length > 0 && files[0].isDirectory()) {
                    dirFiles = files[0];
                }
            } else {
                //直接是文件
                dirFiles = xmlFile.getParentFile();
            }
            if (null != dirFiles) {
                File[] files = dirFiles.listFiles();
                if (null == files) {
                    return;
                }
                for (File file : files) {
                    if (file.getName().contains(".xml")) {
                        if (uploadHook != null) {
                            uploadHook.accept(file);
                        }
                        // 就是将解压后本地的 xml 文件上传到远端: sftpDir/confPath/CONSOLE_clusterName
                        sftpFileManage.uploadFile(confRemotePath + buildPath, file.getPath());
                    }
                }
            }
        }

    }


    public String buildConfRemoteDir(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        return "confPath" + File.separator + AppType.CONSOLE + "_" + one.getClusterName();
    }

    /**
     * 如果开启Kerberos 则添加一个必加配置项
     * 开启 kerberos hdfs 添加dfs.namenode.kerberos.principal.pattern
     * yarn 添加 yarn.resourcemanager.principal.pattern
     * 必要组件添加typename字段
     *
     * @param componentType
     * @param componentString
     * @return
     */
    private List<ClientTemplate> wrapperConfig(EComponentType componentType, String componentString,
                                               boolean isOpenKerberos, String md5Key,
                                               String clientTemplates, ConsoleSSL sslConfig, String pluginName,String versionName) {
        List<ClientTemplate> templates = new ArrayList<>();
        if (EComponentType.KUBERNETES.equals(componentType)) {
            ClientTemplate kubernetesClientTemplate = ComponentConfigUtils.buildOthers("kubernetes.context", componentString);
            templates.add(kubernetesClientTemplate);
            return templates;
        }
        JSONObject componentConfigJSON = JSONObject.parseObject(componentString);

        if (isOpenKerberos) {
            if (EComponentType.HDFS.equals(componentType)) {
                componentConfigJSON.put("dfs.namenode.kerberos.principal.pattern", "*");
            }

            if (EComponentType.YARN.equals(componentType)) {
                componentConfigJSON.put("yarn.resourcemanager.principal.pattern", "*");
            }
        }
        if (sslConfig != null) {
            if (EComponentType.HDFS.equals(componentType)) {
                componentConfigJSON.put("dfs.client.context", sslConfig.getMd5());
            }
        }
        if (EComponentType.typeComponentVersion.contains(componentType)) {
            //添加typeName
            ClientTemplate typeNameClientTemplate = ComponentConfigUtils.buildOthers(TYPE_NAME_KEY, pluginName);
            templates.add(typeNameClientTemplate);
        }
        if (!StringUtils.isBlank(md5Key)) {
            ClientTemplate md5ClientTemplate = ComponentConfigUtils.buildOthers(MD5_ZIP_KEY, md5Key);
            templates.add(md5ClientTemplate);
        }
        if (EComponentType.noControlComponents.contains(componentType)) {
            ScheduleDict dict = scheduleDictService.getByNameAndValue(DictType.EXCLUDE_XML_PARAM.type, versionName, null, null);
            if (null != dict && StringUtils.isNotBlank(dict.getDictValue())) {
                Arrays.stream(dict.getDictValue().split(",")).forEach(componentConfigJSON::remove);
            }
            //xml配置文件也转换为组件
            List<ClientTemplate> xmlTemplates = ComponentConfigUtils.convertXMLConfigToComponentConfig(componentConfigJSON.toJSONString());
            templates.addAll(xmlTemplates);
            //yarn 和hdfs的自定义参数
            templates.addAll(dealXmlCustomControl(componentType, clientTemplates));
        } else {
            List<ClientTemplate> controlTemplate = JSONObject.parseArray(clientTemplates, ClientTemplate.class);
            templates.addAll(controlTemplate);
        }
        return templates;
    }

    /**
     * 上传配置文件到sftp
     *
     * @param dbComponent
     * @param remoteDir
     * @param resource
     */
    private void updateComponentConfigFile(Component dbComponent, SftpFileManage sftpFileManage, String remoteDir, Resource resource) {
        //原来配置
        String deletePath = remoteDir + File.separator;
        LOGGER.info("upload config file to sftp:{}", deletePath);
        if (Objects.nonNull(dbComponent)) {
            deletePath = deletePath + dbComponent.getUploadFileName();
            //删除原来的文件配置zip 如果dbComponent不为null ,删除文件。
            LOGGER.info("delete file :{}", deletePath);
            sftpFileManage.deleteFile(deletePath);
        }

        //更新为原名。将本地的 zip 包上传到远端
        sftpFileManage.uploadFile(remoteDir, resource.getUploadedFileName());
        // 将远端的包改个名字，跟本地的名字一致 -- 跟上面一步操作多余了
        sftpFileManage.renamePath(remoteDir + File.separator + resource.getUploadedFileName().substring(resource.getUploadedFileName().lastIndexOf(File.separator) + 1), remoteDir + File.separator + resource.getFileName());
    }


    /**
     * 解压kerberos文件到本地 并上传至sftp
     * * @param clusterId
     *
     * @param addComponent
     * @param remoteDir
     * @param resource
     * @return
     */
    private String updateComponentKerberosFile(Long clusterId, Component addComponent, SftpFileManage sftpFileManage, String remoteDir, Resource resource,
                                               String principals, String principal, boolean refreshKerberos) {

        File keyTabFile = null;
        File krb5ConfFile = null;
        String remoteDirKerberos = remoteDir + File.separator + GlobalConst.KERBEROS_PATH;

        // kerberos认证文件 远程删除 kerberos下的文件
        LOGGER.info("updateComponentKerberosFile remote path:{}", remoteDirKerberos);
        //删除本地文件夹
        String kerberosPath = kerberosService.getLocalKerberosPath(clusterId, addComponent.getComponentTypeCode());
        try {
            FileUtils.deleteDirectory(new File(kerberosPath));
        } catch (IOException e) {
            LOGGER.error("delete old kerberos directory {} error", kerberosPath, e);
        }
        //解压到本地
        List<File> files = ZipUtil.upzipFile(resource.getUploadedFileName(), kerberosPath);
        if (CollectionUtils.isEmpty(files)) {
            throw new RdosDefineException(ErrorCode.FILE_DECOMPRESSION_EMPTY);
        }

        keyTabFile = files.stream().filter(f -> f.getName().endsWith(KEYTAB_SUFFIX)).findFirst().orElse(null);
        krb5ConfFile = files.stream().filter(f -> f.getName().equalsIgnoreCase(KRB5_CONF)).findFirst().orElse(null);
        if (keyTabFile == null) {
            throw new RdosDefineException(ErrorCode.KEYTAB_FILE_PARSE_ERROR);
        }
        LOGGER.info("fileKeyTab Unzip fileName:{}", keyTabFile.getAbsolutePath());
        if (krb5ConfFile == null) {
            throw new RdosDefineException(ErrorCode.FILE_MISS.getMsg() + ":krb5.conf");
        }
        LOGGER.info("conf Unzip fileName:{}", krb5ConfFile.getAbsolutePath());

        //获取principal
        List<PrincipalName> principalLists = this.getPrincipal(keyTabFile);
        principal = parsePrincipal(principal, principalLists);
        if (StringUtils.isEmpty(principals)) {
            List<String> principalNames = new ArrayList<>();
            for (PrincipalName principalName : principalLists) {
                principalNames.add(principalName.getName());
            }
            principals = StringUtils.join(principalNames, ",");
        }

        //删除sftp原来kerberos 的文件夹
        sftpFileManage.deleteDir(remoteDirKerberos);
        //上传kerberos解压后的文件
        for (File file : files) {
            LOGGER.info("upload sftp file:{}", file.getAbsolutePath());
            sftpFileManage.uploadFile(remoteDirKerberos, file.getPath());
        }

        String componentVersion = ComponentVersionUtil.getComponentVersion(addComponent.getHadoopVersion());
        String formattedComponentVersion = ComponentVersionUtil.formatMultiVersion(addComponent.getComponentTypeCode(), componentVersion);

        //更新数据库kerberos信息
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode(),
                formattedComponentVersion);
        boolean isFirstOpenKerberos = false;
        if (Objects.isNull(kerberosConfig)) {
            kerberosConfig = new KerberosConfig();
            kerberosConfig.setComponentVersion(formattedComponentVersion);
            isFirstOpenKerberos = true;
        }
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setRemotePath(remoteDirKerberos);
        kerberosConfig.setClusterId(clusterId);
        kerberosConfig.setComponentType(addComponent.getComponentTypeCode());
        if (keyTabFile != null) {
            kerberosConfig.setName(keyTabFile.getName());
        }
        if (krb5ConfFile != null) {
            kerberosConfig.setKrbName(krb5ConfFile.getName());
        }

        if (StringUtils.isNotEmpty(principal)) {
            kerberosConfig.setPrincipal(principal);
        }
        if (StringUtils.isNotEmpty(principals)) {
            kerberosConfig.setPrincipals(principals);
        }

        if (refreshKerberos && krb5ConfFile != null) {
            try {
                kerberosConfig.setMergeKrbContent(FileUtils.readFileToString(krb5ConfFile));
            } catch (IOException e) {
                LOGGER.error("Couldn't read krb5.conf file.", e);
                throw new RdosDefineException(ErrorCode.FILE_PARSE_ERROR.getMsg() + ":krb5.conf");
            }
        }
        updateKerberosConfigWithFile(clusterId,addComponent.getComponentTypeCode(),addComponent.getHadoopVersion(), resource, kerberosConfig, isFirstOpenKerberos);
        return remoteDirKerberos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateKerberosConfigWithFile(Long clusterId, Integer componentType, String hadoopVersion, Resource resource, KerberosConfig kerberosConfig, boolean isFirstOpenKerberos) {
        CrudEnum crudEnum = isFirstOpenKerberos ? CrudEnum.ADD : CrudEnum.UPDATE;
        kerberosService.refreshKerberos(crudEnum, kerberosConfig, clusterId, componentType, hadoopVersion);

        if (null == resource) {
            return;
        }
        Component dbComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType, hadoopVersion, null);
        if (null == dbComponent) {
            return;
        }
        Component updateComponent = new Component();
        updateComponent.setId(dbComponent.getId());
        updateComponent.setKerberosFileName(resource.getFileName());
        componentDao.update(updateComponent);
    }

    private String parsePrincipal(String principal, List<PrincipalName> principalLists) {
        if (CollectionUtils.isEmpty(principalLists)) {
            throw new RdosDefineException(ErrorCode.KEYTAB_FILE_NOT_CONTAINS_PRINCIPAL);
        }
        if (StringUtils.isBlank(principal)) {
            //不传默认取第一个
            principal = principalLists.get(0).getName();
        } else {
            String finalPrincipal = principal;
            boolean isContainsPrincipal = principalLists
                    .stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(finalPrincipal));
            if (!isContainsPrincipal) {
                throw new RdosDefineException(ErrorCode.FILE_MISS.getMsg() + ":" + principal);
            }
        }
        return principal;
    }

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeSSL(Long componentId) {
        try {
            Component component = componentDao.getOne(componentId);
            if (Objects.isNull(component)) {
                return;
            }
            consoleSSLDao.deleteByComponent(component.getClusterId(), component.getComponentTypeCode(),
                    formatVersion(component.getComponentTypeCode(), component.getHadoopVersion()));
            if (component.getComponentTypeCode() != null &&
                    component.getComponentTypeCode().equals(EComponentType.HDFS.getTypeCode())) {
                componentConfigService.deleteByComponentIdAndKeyAndComponentTypeCode(componentId,
                        "dfs.client.context", EComponentType.HDFS.getTypeCode());
            }

            Component updateComponent = new Component();
            updateComponent.setId(componentId);
            updateComponent.setSslFileName("");
            componentDao.update(updateComponent);
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.SSL_REMOVE_ERROR);
        }
    }

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeKerberos(Long componentId) {
        try {
            // 删除kerberos配置需要版本号
            Component component = componentDao.getOne(componentId);
            if (Objects.isNull(component)) {
                return;
            }
            kerberosDao.deleteByComponent(component.getClusterId(), component.getComponentTypeCode(),
                    formatVersion(component.getComponentTypeCode(), component.getHadoopVersion()));
            Component updateComponent = new Component();
            updateComponent.setId(componentId);
            updateComponent.setKerberosFileName("");
            componentDao.update(updateComponent);
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.KERBEROS_REMOVE_ERROR);
        }
    }

    public ComponentsResultVO addOrCheckClusterWithName(String clusterName) {
        if (StringUtils.isNotBlank(clusterName)) {
            if (clusterName.length() > 24) {
                throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
            }
        } else {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        clusterName = clusterName.trim();
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            //创建集群
            ClusterDTO clusterDTO = new ClusterDTO();
            clusterDTO.setClusterName(clusterName);
            ClusterVO clusterVO = clusterService.addCluster(clusterDTO);
            ComponentsResultVO componentsResultVO = new ComponentsResultVO();
            Long clusterId = clusterVO.getClusterId();
            componentsResultVO.setClusterId(clusterId);
            LOGGER.info("add cluster {} ", clusterId);
            return componentsResultVO;
        }
        throw new RdosDefineException(ErrorCode.CLUSTER_ALREADY_EXISTS);
    }


    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    @SuppressWarnings("all")
    public List<Object> config(List<Resource> resources, Integer componentType, Boolean autoDelete, String versionName) {

        try {
            if (componentTypeConfigMapping.keySet().contains(componentType)) {
                //解析xml文件 --多个配置文件合并为一个map
                List<String> xmlName = componentTypeConfigMapping.get(componentType);
                List<String> removeKeys = new ArrayList<>();
                if (StringUtils.isNotBlank(versionName)) {
                    ScheduleDict dict = scheduleDictService.getByNameAndValue(DictType.EXCLUDE_XML_PARAM.type, versionName.trim(), null, null);
                    if (null != dict && StringUtils.isNotBlank(dict.getDictValue())) {
                        removeKeys = Arrays.stream(dict.getDictValue().split(","))
                                .collect(Collectors.toList());
                    }
                }

                return parseXmlFileConfig(resources, xmlName,removeKeys);
            } else if (EComponentType.KUBERNETES.getTypeCode().equals(componentType)) {
                //解析k8s组件
                return parseKubernetesData(resources);
            } else {
                //解析上传的json文件
                return parseJsonFile(resources);
            }
        } finally {
            if (null == autoDelete || autoDelete) {
                for (Resource resource : resources) {
                    FileUtil.del(resource.getUploadedFileName());
                }
            }

        }
    }

    private List<Object> parseJsonFile(List<Resource> resources) {
        List<Object> data = new ArrayList<>();
        // 当作json来解析
        for (Resource resource : resources) {
            try {
                String fileInfo = FileUtils.readFileToString(new File(resource.getUploadedFileName()));
                data.add(PublicUtil.strToMap(fileInfo));
            } catch (Exception e) {
                LOGGER.error("parse json config resource error {} ", resource.getUploadedFileName());
                throw new RdosDefineException(ErrorCode.FILE_PARSE_ERROR);
            }
        }
        return data;
    }

    private List<Object> parseXmlFileConfig(List<Resource> resources, List<String> xmlName,List<String> removeKeys) {
        List<Object> datas = new ArrayList<>();
        // 按照文件名称进行分组
        Map<String, Map<String, Object>> xmlConfigMap = this.parseUploadFileToMap(resources);
        boolean isLostXmlFile = xmlConfigMap.keySet().containsAll(xmlName);
        if (!isLostXmlFile) {
            throw new RdosDefineException(ErrorCode.FILE_MISS);
        }
        //多个配置文件合并为一个map
        if (MapUtils.isNotEmpty(xmlConfigMap)) {
            Map<String, Object> data = new HashMap<>();
            for (String key : xmlConfigMap.keySet()) {
                Map<String, Object> xmlFileConfigMap = xmlConfigMap.get(key);
                if (CollectionUtils.isNotEmpty(removeKeys)) {
                    for (String removeKey : removeKeys) {
                        xmlFileConfigMap.remove(removeKey);
                    }
                }
                scheduleDictService.encryptValue(xmlFileConfigMap);
                data.putAll(xmlFileConfigMap);
            }
            datas.add(data);
        }
        return datas;
    }


    private List<Object> parseKubernetesData(List<Resource> resources) {
        List<Object> datas = new ArrayList<>();
        Resource resource = resources.get(0);
        //解压缩获得配置文件
        String xmlZipLocation = resource.getUploadedFileName();
        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        //解析zip 带换行符号
        List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
        if (CollectionUtils.isNotEmpty(xmlFiles)) {
            try {
                datas.add(FileUtil.getContentFromFile(xmlFiles.get(0).getPath()));
            } catch (FileNotFoundException e) {
                LOGGER.error("parse Kubernetes resource error {} ", resource.getUploadedFileName());
            }
        }
        return datas;
    }


    public String buildSftpPath(Long clusterId, Integer componentCode, String versionValue) {
        String componentPath = this.buildComponentPath(clusterId, componentCode);
        if (StringUtils.isNotBlank(versionValue) && ComponentVersionUtil.isMultiVersionComponent(componentCode)) {
            return componentPath + "_" + versionValue;
        }
        return componentPath;
    }

    private boolean skipNotCheckComponent(Integer componentTypeCode) {
        return EComponentType.NOT_CHECK_COMPONENT.contains(EComponentType.getByCode(componentTypeCode))
                || environmentContext.getSkipTestComponentType().contains(componentTypeCode);
    }

    /**
     * 单个组件连通性测试
     *
     * @param component     组件信息
     * @param clusterName   集群名称
     * @param dtUicTenantId 租户 id
     * @return 连通性校验结果
     */
    public ComponentTestResult testConnect(Component component, String clusterName, Long dtUicTenantId) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            EComponentType eComponentType = EComponentType.getByCode(component.getComponentTypeCode());
            // 跳过不需要校验的组件
            if (skipNotCheckComponent(component.getComponentTypeCode())) {
                componentTestResult.setResult(true);
                return componentTestResult;
            }

            // k8s 调度的 engine-plugins 计算组件不检测
            if (EComponentType.ENGINE_PLUGIN_CHECK_COMPONENT.contains(eComponentType)) {
                boolean isYarn = Objects.nonNull(getComponentByClusterId(component.getClusterId(), EComponentType.YARN.getTypeCode(), null));
                if (!isYarn || EDeployType.KUBERNETES.getType().equals(component.getDeployType())) {
                    componentTestResult.setResult(true);
                    return componentTestResult;
                }
            }

            String pluginType;
            if (EComponentType.HDFS.getTypeCode().equals(component.getComponentTypeCode())) {
                // 单独处理 hdfs pluginType
                pluginType = buildHdfsTypeName(null, component.getClusterId());
            } else {
                pluginType = this.convertComponentTypeToClient(clusterName, component.getComponentTypeCode(), component.getVersionName(), component.getStoreType(), component.getDeployType());
            }

            Function<Integer, JSONObject> pluginInfoFunc = deployMode -> pluginInfoManager.buildConsolePluginInfo(component, null, deployMode, null, null,dtUicTenantId,null);

            // sftp 测试连通性单独处理, 不再走 dummy 插件
            if ("dummy".equals(pluginType) && EComponentType.SFTP.getTypeCode().equals(component.getComponentTypeCode())) {
                componentTestResult = checkNullValueWhenRequiredKey(component, null);
                if (componentTestResult != null && !componentTestResult.getResult()) {
                    return componentTestResult;
                }
                return sftpTest(pluginInfoFunc.apply(null).getJSONObject(EComponentType.SFTP.getConfName()).toJSONString());
            }

            JSONObject componentConfig = getComponentByClusterId(component.getClusterId(), component.getComponentTypeCode(),
                    false, JSONObject.class, Collections.singletonMap(component.getComponentTypeCode(), component.getHadoopVersion()));

            // 测试连通性区分 engine-plugins 和 datasourceX
            if (EComponentType.ENGINE_PLUGIN_CHECK_COMPONENT.contains(eComponentType)) {
                List<EDeployMode> deployModes = getDeployModeByConf(component.getComponentTypeCode(), componentConfig);
                componentTestResult = checkNullValueWhenRequiredKey(component, deployModes);
                if (componentTestResult != null && !componentTestResult.getResult()) {
                    return componentTestResult;
                }
                componentTestResult = buildCheckResultWithDeployMode(
                        deployModes,
                        pluginInfoFunc,
                        pluginType);
            } else {
                componentTestResult = checkNullValueWhenRequiredKey(component, null);
                if (componentTestResult != null && !componentTestResult.getResult()) {
                    return componentTestResult;
                }
                componentTestResult = dataSourceXOperator.testConnect(pluginType, pluginInfoFunc.apply(null).toJSONString(), component.getClusterId(), dtUicTenantId, component.getVersionName());
            }

            if (null == componentTestResult) {
                return ComponentTestResult.createFailResult(ErrorCode.CHECK_CONNECT_FAIL.getMsg());
            }

            // 单组件连通性测试回写yarn的队列信息
            if (EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode()) &&
                    componentTestResult.getResult() &&
                    Objects.nonNull(componentTestResult.getClusterResourceDescription())) {
                queueService.updateQueue(component.getClusterId(), componentTestResult.getClusterResourceDescription());
            }
        } catch (Throwable e) {
            if (Objects.isNull(componentTestResult)) {
                componentTestResult = new ComponentTestResult();
            }
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (null != componentTestResult) {
                componentTestResult.setComponentTypeCode(component.getComponentTypeCode());
                componentTestResult.setVersionName(component.getVersionName());
            }
        }
        return componentTestResult;
    }

    /**
     * 根据 deploy mode 构建组件校验结果
     *
     * @param deployModeList  deploy mode 集合
     * @param pluginInfoBuild pluginInfo 构建回调
     * @param pluginType      插件包目录名称
     * @return 检查结果
     */
    private ComponentTestResult buildCheckResultWithDeployMode(List<EDeployMode> deployModeList,
                                                               Function<Integer, JSONObject> pluginInfoBuild,
                                                               String pluginType) {
        if (CollectionUtils.isEmpty(deployModeList)) {
            ComponentTestResult componentTestResult = enginePluginsOperator.testConnect(pluginType, pluginInfoBuild.apply(null).toJSONString(), null, null, null);
            componentTestResult.setConsoleParamCheckResult(filterParamSuccessResult(componentTestResult.getConsoleParamCheckResultList()));
            componentTestResult.setConsoleParamCheckResultList(null);
            return componentTestResult;
        }
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        Map<String, List<ConsoleParamCheckResult>> paramCheckResult = Maps.newHashMap();
        for (EDeployMode deployMode : deployModeList) {
            JSONObject pluginInfoByMode = pluginInfoBuild.apply(deployMode.getType());
            ComponentTestResult testResult = enginePluginsOperator.testConnect(pluginType, pluginInfoByMode.toJSONString(), null, null, null);
            // 一次失败整体失败
            if (!testResult.getResult()) {
                componentTestResult.setResult(false);
            }
            // 错误日志追加
            if (StringUtils.isNotBlank(testResult.getErrorMsg())) {
                componentTestResult.setErrorMsg(
                        (StringUtils.isNotBlank(componentTestResult.getErrorMsg()) ? componentTestResult.getErrorMsg() : "")
                                + String.format("deploy mode: %s, errorMsg: %s\n", deployMode.getMode(), testResult.getErrorMsg()));
            }
            List<ConsoleParamCheckResult> paramCheckResults = testResult.getConsoleParamCheckResultList();
            if (CollectionUtils.isNotEmpty(paramCheckResults)) {
                paramCheckResult.put(deployMode.getMode(), filterParamSuccessResult(paramCheckResults));
            }
        }
        if (MapUtils.isNotEmpty(paramCheckResult)) {
            componentTestResult.setConsoleParamCheckResult(paramCheckResult);
        }
        return componentTestResult;
    }

    /**
     * 只返回失败的
     *
     * @param consoleParamCheckResultList 校验结果
     * @return 失败结果
     */
    private List<ConsoleParamCheckResult> filterParamSuccessResult(List<ConsoleParamCheckResult> consoleParamCheckResultList) {
        if (CollectionUtils.isEmpty(consoleParamCheckResultList)) {
            return null;
        }
        return consoleParamCheckResultList.stream().filter(Objects::nonNull).filter(result -> !result.isResult()).collect(Collectors.toList());
    }

    /**
     * 从组件配置中获取 deploy mode
     *
     * @param componentType   组件类型枚举
     * @param componentConfig 组件配置信息
     * @return deploy mode list
     */
    public List<EDeployMode> getDeployModeByConf(Integer componentType, JSONObject componentConfig) {
        EComponentType eComponentType = EComponentType.getByCode(componentType);
        Set<EDeployMode> deployModes = Sets.newHashSet();
        if (EComponentType.SPARK.equals(eComponentType)) {
            deployModes.add(EDeployMode.PERJOB);
        } else if (EComponentType.FLINK.equals(eComponentType)) {
            for (EDeployMode deployMode : EDeployMode.values()) {
                // todo 这个地方注意 5.3 要修一下
                if (componentConfig.containsKey(deployMode.getMode())) {
                    deployModes.add(deployMode);
                }
            }
            // 兼容 flink standalone
            if (componentConfig.containsKey(CLUSTER_MODE)) {
                String clusterMode = componentConfig.getString(CLUSTER_MODE);
                Optional.ofNullable(EDeployMode.getByMode(clusterMode)).ifPresent(deployModes::add);
            }
        }
        return Lists.newArrayList(deployModes);
    }

    /**
     * sftp 连通性测试
     *
     * @param pluginInfo sftp 组件信息信息
     * @return 测试结果
     */
    private ComponentTestResult sftpTest(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            SftpConfig sftpConfig = PublicUtil.jsonStrToObject(pluginInfo, SftpConfig.class);
            // check sftpConfig 准确性
            SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
            //测试路径是否存在
            Vector res = sftpFileManage.listFile(sftpConfig.getPath());
            if (null != res) {
                componentTestResult.setResult(true);
            }
        } catch (Exception e) {
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            componentTestResult.setResult(false);
        }
        return componentTestResult;
    }

    /**
     * 获取本地kerberos配置地址
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    public String getLocalKerberosPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        return env.getLocalKerberosDir() + File.separator + AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() + File.separator + GlobalConst.KERBEROS_PATH;
    }

    /**
     * 下载文件
     *
     * @param componentId
     * @param downloadType 0:kerberos配置文件 1:配置文件 2:模板文件 3:ssl上传配置 4:ssl模版
     * @return
     */
    public File downloadFile(Long componentId, Integer downloadType, Integer componentType,
                             String versionName, Long clusterId, Integer deployType) {
        String localDownLoadPath = "";
        String uploadFileName = "";
        if (downloadType != null && DownloadType.SSL_TEMPLATE.getCode() == downloadType) {
            ScheduleDict dict = scheduleDictService.getByNameAndValue(DictType.SSL_TEMPLATE.type, "ssl_client", null, null);
            if (dict == null) {
                throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
            }

            uploadFileName = "ssl-client.xml";
            localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + uploadFileName;
            try {
                FileUtils.write(new File(localDownLoadPath), dict.getDictValue());
            } catch (Exception e) {
                throw new RdosDefineException(ErrorCode.FILE_MISS);
            }
        } else if (null == componentId) {
            EComponentType type = EComponentType.getByCode(componentType);
            EComponentType storeType = null;
            if (EComponentType.ComputeScheduling.contains(type)) {
                List<Component> storeComponents = componentDao.listAllByClusterIdAndComponentTypes(clusterId,
                        EComponentType.StorageScheduling.stream().map(EComponentType::getTypeCode).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(storeComponents)) {
                    storeType = EComponentType.getByCode(storeComponents.get(0).getComponentTypeCode());
                }
            }
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(clusterId, type, versionName, storeType, deployType);
            if (CollectionUtils.isNotEmpty(clientTemplates)) {
                Map<String, Object> fileMap = ComponentConfigUtils.convertClientTemplateToMap(clientTemplates);
                uploadFileName = type.name() + ".json";
                localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + uploadFileName;
                try {
                    FileUtils.write(new File(localDownLoadPath), JSONObject.toJSONString(fileMap));
                } catch (Exception e) {
                    throw new RdosDefineException(ErrorCode.FILE_MISS);
                }
            }
        } else {
            Component component = componentDao.getOne(componentId);
            if (null == component) {
                throw new RdosDefineException(ErrorCode.COMPONENT_NOT_EXISTS);
            }
            SftpConfig sftpConfig = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, SftpConfig.class, null);
            if (null == sftpConfig) {
                throw new RdosDefineException(ErrorCode.COMPONENT_NOT_EXISTS.getMsg() + ":sftp");
            }

            localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + component.getComponentName();
            String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, component.getComponentTypeCode(),component.getHadoopVersion());
            SftpFileManage sftpFileManage = null;
            uploadFileName = component.getUploadFileName();
            if (DownloadType.SSL_UPLOAD.getCode() == downloadType) {
                remoteDir = remoteDir + File.separator + SSL_CLIENT_PATH;
                localDownLoadPath = localDownLoadPath + File.separator + SSL_CLIENT_PATH;
                sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
                sftpFileManage.downloadDir(remoteDir, localDownLoadPath);
                uploadFileName = component.getSslFileName();
            } else if (DownloadType.Kerberos.getCode() == downloadType) {
                remoteDir = remoteDir + File.separator + GlobalConst.KERBEROS_PATH;
                localDownLoadPath = localDownLoadPath + File.separator + GlobalConst.KERBEROS_PATH;
                sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
                sftpFileManage.downloadDir(remoteDir, localDownLoadPath);
                deleteKerberosLockFileIfNeeded(localDownLoadPath);
                uploadFileName = component.getKerberosFileName();
            } else {
                if (StringUtils.isBlank(component.getUploadFileName())) {
                    // 一种是  全部手动填写的 如flink
                    List<Component> components = componentDao.listAllByClusterIdAndComponentTypeAndVersionName(clusterId, componentType, versionName);
                    String componentConfig = "";
                    if (CollectionUtils.isNotEmpty(components)) {
                        componentConfig = getComponentByClusterId(clusterId, componentType, true, String.class, Collections.singletonMap(componentType, components.get(0).getHadoopVersion()));
                    }
                    try {
                        localDownLoadPath = localDownLoadPath + ".json";
                        FileUtils.write(new File(localDownLoadPath), filterConfigMessage(componentConfig));
                    } catch (IOException e) {
                        LOGGER.error("write upload file {} error", componentConfig, e);
                    }
                } else {
                    sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
                    // 一种是 上传配置文件的需要到sftp下载
                    sftpFileManage.downloadDir(remoteDir + File.separator + component.getUploadFileName(), localDownLoadPath);
                    localDownLoadPath = localDownLoadPath + File.separator + component.getUploadFileName();
                }
            }
        }

        File file = new File(localDownLoadPath);
        if (!file.exists()) {
            throw new RdosDefineException(ErrorCode.FILE_NOT_FOUND);
        }
        String zipFilename = StringUtils.isBlank(uploadFileName) ? "download.zip" : uploadFileName;
        if (file.isDirectory()) {
            //将文件夹压缩成zip文件
            return zipFile(componentId, downloadType, componentType, file, zipFilename);
        } else {
            return new File(localDownLoadPath);
        }
    }

    /**
     * kerberos 的 .lock 文件是控制台人为加的，客户无感知，所以不要下载
     *
     * @param localDownLoadPath
     */
    private void deleteKerberosLockFileIfNeeded(String localDownLoadPath) {
        Collection<File> files = FileUtil.listFiles(new File(localDownLoadPath), new String[]{"lock"}, false);
        if (files.isEmpty()) {
            return;
        }
        files.stream().forEach(f -> {
            FileUtil.deleteQuietly(f);
        });
    }

    /**
     * 移除配置信息中的密码信息
     */
    private String filterConfigMessage(String componentConfig) {
        if (StringUtils.isBlank(componentConfig)) {
            return "";
        }
        JSONObject configJsonObject = JSONObject.parseObject(componentConfig);
        String pwdKey = "password";
        configJsonObject.remove(pwdKey);
        return configJsonObject.toJSONString();
    }


    private File zipFile(Long componentId, Integer downloadType, Integer componentType, File file, String zipFilename) {
        File[] files = file.listFiles();
        //压缩成zip包
        if (null != files) {
            if (DownloadType.Kerberos.getCode() == downloadType) {
                Long clusterId = componentDao.getClusterIdByComponentId(componentId);
                KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType, ComponentVersionUtil.isMultiVersionComponent(componentType) ? componentDao.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType) : null);
                if (null != kerberosConfig) {
                    zipFilename = kerberosConfig.getName() + ZIP_SUFFIX;
                }
            }
            ZipUtil.zipFile(USER_DIR_DOWNLOAD + File.separator + zipFilename, Arrays.asList(files));
        }
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            LOGGER.error("delete upload file {} error", file.getName(), e);
        }
        return new File(USER_DIR_DOWNLOAD + File.separator + zipFilename);
    }

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType 组件类型
     * @param clusterId     集群ID
     * @param versionName   组件版本名 如Hadoop 2.x
     * @param storeType     存储组件type 如 HDFS
     * @return
     */
    public List<ClientTemplate> loadTemplate(Long clusterId, EComponentType componentType, String versionName, EComponentType storeType, Integer deployType) {
        PartCluster cluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        Part part = cluster.create(componentType, versionName, storeType, deployType);
        List<ComponentConfig> componentConfigs = part.loadTemplate();
        Map<String, ScheduleDict> tipGroup = scheduleDictService.findTipGroup(componentType.getTypeCode());
        return ComponentConfigUtils.buildDBDataToTipsClientTemplate(componentConfigs, componentType.getTypeCode(), deployType, tipGroup);
    }

    /**
     * 根据组件类型转换对应的插件包目录名称
     * 如果只配 yarn 需要调用插件时候 hdfs 给默认值
     *
     * @param clusterName   集群名称
     * @param componentType 组件类型
     * @param versionName   组件版本名称
     * @return engine-plugin 插件包目录名
     */
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String versionName, Integer storeType, Integer deployType) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        EComponentType type = EComponentType.getByCode(componentType);
        EComponentType storeComponent = null == storeType ? null : EComponentType.getByCode(storeType);
        PartCluster partCluster = clusterFactory.newImmediatelyLoadCluster(cluster.getId());
        Part part = partCluster.create(type, versionName, storeComponent, deployType);
        return part.getPluginName();
    }


    /**
     * 删除组件
     *
     * @param componentIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Integer> componentIds) {
        if (CollectionUtils.isEmpty(componentIds)) {
            return;
        }
        for (Integer componentId : componentIds) {
            Component component = componentDao.getOne(componentId.longValue());
            EngineAssert.assertTrue(component != null, ErrorCode.DATA_NOT_FIND.getDescription());
            List<Long> tenants = clusterTenantDao.listBoundedTenants(component.getClusterId());
            if (!CollectionUtils.isEmpty(tenants)) {

                if (BooleanUtils.toIntegerObject(true).equals(component.getIsMetadata())) {
                    throw new RdosDefineException(ErrorCode.METADATA_COMPONENT_NOT_DELETED);
                }

                EComponentType componentType = EComponentType.getByCode(component.getComponentTypeCode());
                if (EComponentType.ResourceScheduling.contains(componentType) || EComponentType.StorageScheduling.contains(componentType)
                    || EComponentType.LDAP == componentType || EComponentType.RANGER == componentType || EComponentType.COMMON == componentType) {
                    throw new RdosDefineException(ErrorCode.BIND_COMPONENT_NOT_DELETED);
                }

                //多版本组件设置剩余为默认
                if (ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode())) {
                    Component nextDefaultComponent = componentDao.getNextDefaultComponent(component.getClusterId(), component.getComponentTypeCode(), component.getId());
                    if (component.getIsDefault() && null != nextDefaultComponent) {
                        nextDefaultComponent.setIsDefault(true);
                        componentDao.update(nextDefaultComponent);
                    }
                    if (nextDefaultComponent != null) {
                        List<Component> dbComponents = componentDao.listByClusterId(component.getClusterId(), componentType.getTypeCode(), false);
                        List<String> reserveComponentVersion = dbComponents.stream()
                                .filter(c -> !c.getId().equals(componentId.longValue()))
                                .map(Component::getHadoopVersion)
                                .collect(Collectors.toList());
                        changeTaskVersion(component.getClusterId(), componentType, nextDefaultComponent, reserveComponentVersion);
                    }
                }
            }

            //删除
            componentDao.deleteById(componentId.longValue());
            String formattedVersion = formatVersion(component.getComponentTypeCode(), component.getHadoopVersion());
            kerberosDao.deleteByComponent(component.getClusterId(), component.getComponentTypeCode(), formattedVersion);
            consoleSSLDao.deleteByComponent(component.getClusterId(), component.getComponentTypeCode(), formattedVersion);
            componentConfigService.deleteComponentConfig(componentId.longValue());
            consoleComponentUserService.checkAndDeleteByComponentAndCluster(component.getClusterId(), component.getComponentTypeCode());
            try {
                this.updateCache(component.getClusterId(), component.getComponentTypeCode());
            } catch (Exception e) {
                LOGGER.error("clear cache error {} ", componentIds, e);
            }
            this.syncToRangerIfNeeded(component.getClusterId(), component, CrudEnum.DELETE);
            this.removeYarnFileSyncIfNeeded(component);
            this.removeAuxiliaryConfigIfNeeded(component);
        }
    }

    /**
     * 根据用户操作类型，将组件信息同步至 Ranger
     * @param clusterId
     * @param component
     * @param crudEnum
     */
    private void syncToRangerIfNeeded(Long clusterId, Component component, CrudEnum crudEnum) {
        Set<Long> dtUicTenantIds = tenantService.findUicTenantIdsByClusterId(clusterId);
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return;
        }
        dataSourceService.syncToRangerIfNeeded(clusterId, component, dtUicTenantIds, crudEnum);
    }

    /**
     * 用户新增 ranger 组件，需要将历史的组件同步至业务中心
     * 备注：考虑到 hdfs 组件需要新增数据安全参数，此时需要用户手动添加，因为即使后端添加了，前端并不会重新渲染页面
     *
     * @param clusterId
     * @param componentTypeCode
     * @param crudEnum
     */
    private void syncHistoryInfoToRangerIfNeeded(Long clusterId, Integer componentTypeCode, CrudEnum crudEnum) {
        if (!EComponentType.RANGER.getTypeCode().equals(componentTypeCode)) {
            return;
        }
        if (crudEnum != CrudEnum.ADD) {
            return;
        }
        dataSourceService.syncHistoryInfoToRangerIfNeeded(clusterId);
    }

    private void removeYarnFileSyncIfNeeded(Component component) {
        // 只有 yarn 组件存在文件同步配置
        if (!EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
            return;
        }
        Long clusterId = component.getClusterId();
        consoleFileSyncDao.removeByClusterId(clusterId);
        consoleFileSyncDetailDao.deleteByClusterId(clusterId);
    }

    private void removeAuxiliaryConfigIfNeeded(Component component) {
        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(component.getClusterId(), component.getComponentTypeCode(), null);
        componentAuxiliaryService.removeAuxiliaryConfig(auxiliary);
    }

    /***
     * 获取对应的组件版本信息
     /**
     * 获取对应的组件版本信息，根据当前已配置的资源调度组件，隐藏部分计算组件
     * @param clusterId
     * @return
     */
    public Map getComponentVersion(Long clusterId) {
        return scheduleDictService.getVersion(clusterId);
    }

    public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
        return componentDao.getByClusterIdAndComponentType(clusterId, componentType, componentVersion, null);
    }

    /**
     * 获取对应组件的配置信息
     *
     * @param clusterId
     * @param componentType
     * @param isFilter      是否移除typeName 等配置信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap, Long componentId) {
        Map<String, Object> configMap = componentConfigService.getCacheComponentConfigMap(clusterId, componentType, isFilter, componentVersionMap, componentId);
        if (MapUtils.isEmpty(configMap)) {
            return null;
        }
        if (clazz.isInstance(Map.class)) {
            return (T) configMap;
        }
        String configStr = JSONObject.toJSONString(configMap);
        if (clazz.isInstance(String.class)) {
            return (T) configStr;
        }
        return JSONObject.parseObject(configStr, clazz);
    }

    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap) {
        return getComponentByClusterId(clusterId, componentType, isFilter, clazz, componentVersionMap, null);
    }

    public <T> T getComponentByClusterId(Long componentId, boolean isFilter, Class<T> clazz) {
        return getComponentByClusterId(null, null, isFilter, clazz, null, componentId);
    }

    /**
     * 刷新组件信息
     *
     * @param clusterName
     * @return
     */
    public List<ComponentTestResult> refresh(String clusterName) {
        List<ComponentTestResult> refreshResults = new ArrayList<>();
        ComponentTestResult componentTestResult = testConnect(clusterName, EComponentType.YARN.getTypeCode(), null, null);
        refreshResults.add(componentTestResult);
        return refreshResults;
    }

    public ComponentTestResult testConnect(String clusterName, Integer componentType, String versionName, Long dtuicTenantId) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        Component testComponent = componentDao.getByVersionName(cluster.getId(), componentType, ComponentVersionUtil.isMultiVersionComponent(componentType) ? versionName : null, null);
        if (null == testComponent) {
            throw new RdosDefineException(ErrorCode.COMPONENT_NOT_EXISTS);
        }
        if (EComponentType.NOT_CHECK_COMPONENT.contains(EComponentType.getByCode(componentType))) {
            ComponentTestResult componentTestResult = new ComponentTestResult();
            componentTestResult.setComponentTypeCode(componentType);
            componentTestResult.setResult(true);
            componentTestResult.setVersionName(testComponent.getVersionName());
            return componentTestResult;
        }
        return testComponentWithResult(clusterName, cluster, testComponent, dtuicTenantId);
    }

    /**
     * 测试所有组件连通性
     *
     * @param clusterName
     * @return
     */
    public List<ComponentMultiTestResult> testConnects(String clusterName,Long dtUicTenantId) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        List<Component> components = getComponents(cluster);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>();
        }

        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        Table<Integer, String, Future<ComponentTestResult>> completableFutures = HashBasedTable.create();
        for (Component component : components) {
            Future<ComponentTestResult> testResultFuture = commonExecutor.submit(() -> {
                try {
                    return testComponentWithResult(clusterName, cluster, component, dtUicTenantId);
                } finally {
                    countDownLatch.countDown();
                }
            });
            completableFutures.put(component.getComponentTypeCode(), StringUtils.isBlank(component.getVersionName()) ? StringUtils.EMPTY : component.getVersionName(), testResultFuture);
        }
        try {
            countDownLatch.await(env.getTestConnectTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("test connect await {} error ", clusterName, e);
        }

        Map<Integer, ComponentMultiTestResult> multiComponent = new HashMap<>(completableFutures.size());
        for (Table.Cell<Integer, String, Future<ComponentTestResult>> cell : completableFutures.cellSet()) {
            Future<ComponentTestResult> completableFuture = cell.getValue();
            ComponentTestResult testResult = new ComponentTestResult();
            testResult.setResult(false);
            try {
                if (completableFuture.isDone()) {
                    testResult = completableFuture.get();
                } else {
                    testResult.setErrorMsg("test connect time out");
                    completableFuture.cancel(true);
                }
            } catch (Exception e) {
                testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            } finally {
                testResult.setComponentTypeCode(cell.getRowKey());
                ComponentMultiTestResult multiTestResult = multiComponent.computeIfAbsent(cell.getRowKey(), k -> new ComponentMultiTestResult(cell.getRowKey()));
                buildComponentMultiTest(multiTestResult, testResult, cell.getColumnKey());
            }
        }
        return new ArrayList<>(multiComponent.values());
    }

    private ComponentTestResult testComponentWithResult(String clusterName, Cluster cluster, Component component, Long dtUicTenantId) {
        ComponentTestResult testResult = new ComponentTestResult();
        try {
            testResult = this.testConnect(component, clusterName, dtUicTenantId);
            //测试联通性
            if (EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode()) && testResult.getResult()) {
                if (null != testResult.getClusterResourceDescription()) {
                    queueService.updateQueue(cluster.getId(), testResult.getClusterResourceDescription());
                } else {
                    testResult.setResult(false);
                    testResult.setErrorMsg(clusterName + "获取yarn信息为空");
                }
            }
        } catch (Exception e) {
            testResult.setResult(false);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            LOGGER.error("test connect {}  error ", component.getId(), e);
        } finally {
            testResult.setComponentTypeCode(component.getComponentTypeCode());
            testResult.setVersionName(component.getVersionName());
        }
        return testResult;
    }

    /**
     * 校验必填项, SQL 新增必填参数, 界面如果没有手动配置, 直接点全部联通, 前端无法校验
     * @param component
     * @return
     */
    private ComponentTestResult checkNullValueWhenRequiredKey(Component component, List<EDeployMode> deployModeList) {
        List<String> excludeDeployModes = null;
        // flink 只能校验用户选择的 deployMode
        if (EComponentType.FLINK.getTypeCode().equals(component.getComponentTypeCode())
                && CollectionUtils.isNotEmpty(deployModeList)) {
            List<String> deployModes = deployModeList.stream().map(EDeployMode::getMode).collect(Collectors.toList());
            excludeDeployModes = Lists.newArrayList(EDeployMode.allDeployModes());
            excludeDeployModes.removeAll(deployModes);
        } else {
            excludeDeployModes = null;
        }
        List<ComponentConfig> requiredComponentConfigs = componentConfigDao.listRequiredKeyWithNullValue(component.getId(), excludeDeployModes);
        if (CollectionUtils.isEmpty(requiredComponentConfigs)) {
            return null;
        }
        ComponentTestResult testResult = new ComponentTestResult();
        String requireNullValueKey = requiredComponentConfigs.stream()
                .map(ComponentConfig::getKey)
                .collect(Collectors.joining("\r\n"));
        testResult.setResult(false);
        testResult.setErrorMsg("必填参数为空: " + requireNullValueKey);
        return testResult;
    }

    private List<Component> getComponents(Cluster cluster) {

        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }

        List<Component> components = componentDao.listByClusterId(cluster.getId(), null, false);
        if (CollectionUtils.isEmpty(components)) {
            return Collections.emptyList();
        }
        return components;
    }


    public List<Component> getComponentStore(String clusterName, Integer componentType) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        List<Component> components = new ArrayList<>();
        Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(), null, null);
        if (null != hdfs) {
            components.add(hdfs);
        }
        Component nfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode(), null, null);
        if (null != nfs) {
            components.add(nfs);
        }
        Component s3 = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.S3.getTypeCode(), null, null);
        if (null != s3) {
            components.add(s3);
        }
        return components;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateNamespaces(Long clusterId, String namespace, Long queueId, Long dtUicTenantId) {
        if (StringUtils.isBlank(namespace)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.KUBERNETES.getTypeCode(), null, null);
        if (null == component) {
            throw new RdosDefineException(ErrorCode.COMPONENT_NOT_EXISTS);
        }
        String clusterName = cluster.getClusterName();
        String pluginType = this.convertComponentTypeToClient(clusterName, EComponentType.KUBERNETES.getTypeCode(), "", null,null);
        //测试namespace 的权限
        Map<String, Object> extraParam = Maps.newHashMap();
        extraParam.put("namespace", namespace);
        JSONObject pluginInfo = pluginInfoManager.buildConsolePluginInfo(component, cluster, null, extraParam, null,dtUicTenantId,null);
        ComponentTestResult componentTestResult = dataSourceXOperator.testConnect(pluginType, pluginInfo.toJSONString(), clusterId, dtUicTenantId, null);
        if (null != componentTestResult && !componentTestResult.getResult()) {
            throw new RdosDefineException(componentTestResult.getErrorMsg());
        }
        List<Queue> namespaces = queueDao.listByIds(Lists.newArrayList(queueId));
        if (CollectionUtils.isNotEmpty(namespaces)) {
            Queue dbQueue = namespaces.get(0);
            dbQueue.setQueueName(namespace);
            dbQueue.setQueuePath(namespace);
            dbQueue.setIsDeleted(Deleted.NORMAL.getStatus());
            queueDao.update(dbQueue);
            return dbQueue.getId();
        } else {
            Queue queue = new Queue();
            queue.setQueueName(namespace);
            queue.setClusterId(clusterId);
            queue.setMaxCapacity("0");
            queue.setCapacity("0");
            queue.setQueueState("ACTIVE");
            queue.setParentQueueId(DEFAULT_KUBERNETES_PARENT_NODE);
            queue.setQueuePath(namespace);
            Integer insert = queueDao.insert(queue);
            if (insert != 1) {
                throw new RdosDefineException(ErrorCode.INSERT_EXCEPTION);
            }
            if (null == queueId) {
                Long dbQueue = clusterTenantDao.getQueueIdByDtUicTenantId(dtUicTenantId);
                if (null == dbQueue) {
                    //兼容4.0 queueId为空的数据 需要重新绑定
                    tenantService.updateTenantQueue(dtUicTenantId, clusterId, queue.getId());
                }
            }
            return queue.getId();
        }
    }

    public Boolean isYarnSupportGpus(String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        Component yarnComponent = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(), null);

        if (yarnComponent == null) {
            return false;
        }
        if (!HADOOP3_SIGNAL.equals(yarnComponent.getHadoopVersion())) {
            return false;
        }
        JSONObject yarnConf = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(), false, JSONObject.class, null);
        if (null == yarnConf) {
            return false;
        }
        if (!"yarn.io/gpu".equals(yarnConf.getString(GPU_RESOURCE_PLUGINS_SIGNAL))) {
            return false;
        }
        if (StringUtils.isBlank(yarnConf.getString(GPU_EXEC_SIGNAL))) {
            return false;
        }
        if (!"true".equals(yarnConf.getString(GPU_ALLOWED_SIGNAL)) && !"auto".equals(yarnConf.getString(GPU_ALLOWED_SIGNAL))) {
            return false;
        }


        ClusterResource resource = consoleService.clusterResources(cluster.getClusterName(), null, null);
        List<JSONObject> queues = resource.getQueues();
        if (queues != null && queues.size() > 0) {
            for (JSONObject object : queues) {
                try {
                    JSONArray resources = object.getJSONObject("resources").getJSONArray("resourceUsagesByPartition");
                    for (int i = 0; i < resources.size(); ++i) {
                        JSONObject ele = resources.getJSONObject(i);
                        if (ele.containsKey("used")) {
                            JSONArray info = ele.getJSONObject("used").getJSONObject("resourceInformations")
                                    .getJSONArray("resourceInformation");
                            for (int j = 0; j < info.size(); ++j) {
                                JSONObject jsonEle = info.getJSONObject(j);
                                if ("yarn.io/gpu".equals(jsonEle.getString("name"))) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }

            }
        }
        return false;
    }


    /**
     * 解析对应的kerberos的zip中principle
     *
     * @param resourcesFromFiles
     * @return
     */
    public List<String> parseKerberos(List<Resource> resourcesFromFiles) {
        if (CollectionUtils.isEmpty(resourcesFromFiles)) {
            return null;
        }
        Resource resource = resourcesFromFiles.get(0);
        String unzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            //解压到本地
            List<File> files = ZipUtil.upzipFile(resource.getUploadedFileName(), unzipLocation);

            if (CollectionUtils.isEmpty(files)) {
                throw new RdosDefineException(ErrorCode.FILE_DECOMPRESSION_EMPTY);
            }

            File fileKeyTab = files
                    .stream()
                    .filter(f -> f.getName().endsWith(KEYTAB_SUFFIX))
                    .findFirst()
                    .orElse(null);
            if (fileKeyTab == null) {
                throw new RdosDefineException(ErrorCode.KEYTAB_FILE_NOT_FOUND);
            }

            //获取principal
            List<PrincipalName> principal = this.getPrincipal(fileKeyTab);
            return principal
                    .stream()
                    .map(PrincipalName::getName)
                    .collect(Collectors.toList());
        } finally {
            try {
                FileUtils.deleteDirectory(new File(unzipLocation));
            } catch (IOException e) {
                LOGGER.error("delete update file {} error", unzipLocation);
            }
        }
    }

    /**
     * 更新metadata的元数据组件
     * 如果集群只有单个metadata组件 默认勾选
     * 如果集群多个metadata组件 绑定租户之后 无法切换
     *
     * @param clusterId
     * @param componentType
     * @param isMetadata
     * @return
     */
    public boolean changeMetadata(Integer componentType, boolean isMetadata, Long clusterId, Integer oldMetadata) {
        if (!EComponentType.metadataComponents.contains(EComponentType.getByCode(componentType))) {
            return false;
        }
        Integer revertComponentType = EComponentType.HIVE_SERVER.getTypeCode().equals(componentType) ? EComponentType.SPARK_THRIFT.getTypeCode() : EComponentType.HIVE_SERVER.getTypeCode();
        List<Component> components = componentDao.listByClusterId(clusterId, revertComponentType, false);
        Component revertComponent = CollectionUtils.isEmpty(components) ? null : components.get(0);
        if (null == revertComponent) {
            //单个组件默认勾选
            componentDao.updateMetadata(clusterId, componentType, 1);
            return true;
        }
        if (null != oldMetadata && !BooleanUtils.toIntegerObject(isMetadata, 1, 0).equals(oldMetadata)) {
            //如果集群已经绑定过租户 不允许修改
            if (CollectionUtils.isNotEmpty(clusterTenantDao.listBoundedTenants(clusterId))) {
                throw new RdosDefineException(ErrorCode.CHANGE_META_NOT_PERMIT_WHEN_BIND_CLUSTER);
            }
        }
        LOGGER.info("change metadata clusterId {} component {} to {} ", clusterId, componentType, isMetadata);
        componentDao.updateMetadata(clusterId, componentType, isMetadata ? 1 : 0);
        componentDao.updateMetadata(clusterId, revertComponentType, isMetadata ? 0 : 1);
        return true;
    }

    /**
     * 构建组件多版本测试结果
     *
     * @param multiTestResult     多版本
     * @param componentTestResult 单个版本
     * @param componentVersion    版本,可能为 ""
     */
    private void buildComponentMultiTest(ComponentMultiTestResult multiTestResult, ComponentTestResult componentTestResult, String componentVersion) {
        if (!componentTestResult.getResult()) {
            if (multiTestResult.getResult()) {
                multiTestResult.setResult(false);
                multiTestResult.setErrorMsg(new ArrayList<>(2));
            }
            multiTestResult.getErrorMsg().add(new ComponentMultiTestResult.MultiErrorMsg(componentVersion, componentTestResult.getErrorMsg()));
        }
        multiTestResult.getMultiVersion().add(componentTestResult);

    }

    /**
     * @param appType       后面可能会用
     * @param projectId     后面可能会用
     * @param dtuicTenantId 后面可能会用
     * @return
     */
    public List<TaskGetSupportJobTypesResultVO> getSupportJobTypes(Integer appType, Long projectId, Long dtuicTenantId) {
        EScheduleJobType[] eScheduleJobType = EScheduleJobType.values();
        List<TaskGetSupportJobTypesResultVO> vos = Lists.newArrayList();
        for (EScheduleJobType scheduleJobType : eScheduleJobType) {
            TaskGetSupportJobTypesResultVO vo = new TaskGetSupportJobTypesResultVO();
            vo.setKey(scheduleJobType.getType());
            vo.setValue(scheduleJobType.getName());
            vos.add(vo);
        }
        return vos;
    }

    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String agentAddress) {
        try {
            JSONObject pluginInfo = new JSONObject();
            JSONObject dtScriptAgentConf = new JSONObject(1).fluentPut("agentAddress", agentAddress);
            pluginInfo.put(EComponentType.DTSCRIPT_AGENT.getConfName(), dtScriptAgentConf);
            // 不需要集群信息,dtScriptAgent属于普通rdb,直接获取即可
            String engineType = EComponentType.convertPluginNameByComponent(EComponentType.DTSCRIPT_AGENT);
            List<DtScriptAgentLabelDTO> dtScriptAgentLabelDTOList = enginePluginsOperator.getDtScriptAgentLabel(engineType, pluginInfo.toJSONString());
            Map<String, List<DtScriptAgentLabelDTO>> labelGroup = dtScriptAgentLabelDTOList.stream().collect(Collectors.groupingBy(DtScriptAgentLabelDTO::getLabel));
            List<DtScriptAgentLabelDTO> resultList = new ArrayList<>(labelGroup.size());
            for (Map.Entry<String, List<DtScriptAgentLabelDTO>> entry : labelGroup.entrySet()) {
                String ip = entry.getValue().stream().map(localIp -> localIp.getLocalIp() + ":22").collect(Collectors.joining(","));
                DtScriptAgentLabelDTO dtScriptAgentLabelDTO = new DtScriptAgentLabelDTO();
                dtScriptAgentLabelDTO.setLabel(entry.getKey());
                dtScriptAgentLabelDTO.setLocalIp(ip);
                resultList.add(dtScriptAgentLabelDTO);
            }
            return resultList;
        } catch (Exception e) {
            LOGGER.error("find dtScript Agent label error", e);
        }
        return Collections.emptyList();
    }

    public List<Component> getComponentVersionByEngineType(Long uicTenantId, String engineType) {
        EComponentType componentType = EngineTypeComponentType.getByEngineName(engineType).getComponentType();
        return getComponents(uicTenantId, componentType);
    }

    public List<Component> getComponentsByClusterId(Long clusterId, EComponentType componentType) {
        return componentDao.listAllByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
    }
    /**
     * 缺少属性
     * @param uicTenantId
     * @param componentType
     * @return
     */
    @Deprecated
    public List<Component> getComponents(Long uicTenantId, EComponentType componentType) {
        List<Component> componentVersionList = componentDao.getComponentVersionByEngineType(uicTenantId, componentType.getTypeCode());
        if (CollectionUtils.isEmpty(componentVersionList)) {
            return Collections.emptyList();
        }
        Set<String> distinct = new HashSet<>(2);
        List<Component> components = new ArrayList<>(2);
        for (Component component : componentVersionList) {
            if (distinct.add(component.getHadoopVersion())) {
                components.add(component);
            }
        }
        return components;
    }


    public Component getMetadataComponent(Long clusterId) {
        return componentDao.getMetadataComponent(clusterId);
    }

    @Deprecated
    @Transactional
    public void addOrUpdateComponentUser(List<ComponentUserVO> componentUserList) {
        if (CollectionUtils.isEmpty(componentUserList)) {
            return;
        }
        ComponentUserVO componentUserVO = componentUserList.get(0);

        // 删除之前保存的数据
        componentUserDao.deleteByComponentAndCluster(componentUserVO.getClusterId(), componentUserVO.getComponentTypeCode());
        List<ComponentUser> addComponentUserList = new ArrayList<>(componentUserList.size());
        // 构建实例
        for (ComponentUserVO userVO : componentUserList) {
            if (CollectionUtils.isEmpty(userVO.getComponentUserInfoList())
                    && Boolean.TRUE.equals(userVO.getIsDefault())) {
                ComponentUser emptyUser = new ComponentUser();
                emptyUser.setPassword(StringUtils.EMPTY);
                emptyUser.setUserName(StringUtils.EMPTY);
                emptyUser.setLabel(userVO.getLabel());
                emptyUser.setLabelIp(ConsoleComponentUserService.transferIpStr(userVO.getSidecar()));
                emptyUser.setIsDefault(true);
                emptyUser.setClusterId(userVO.getClusterId());
                emptyUser.setComponentTypeCode(userVO.getComponentTypeCode());
                addComponentUserList.add(emptyUser);
            }
            if (CollectionUtils.isEmpty(userVO.getComponentUserInfoList())) {
                continue;
            }
            for (ComponentUserVO.ComponentUserInfo userInfo : userVO.getComponentUserInfoList()) {
                ComponentUser componentUser = new ComponentUser();
                componentUser.setClusterId(userVO.getClusterId());
                componentUser.setComponentTypeCode(userVO.getComponentTypeCode());
                componentUser.setIsDefault(userVO.getIsDefault());
                componentUser.setLabel(userVO.getLabel());
                componentUser.setLabelIp(ConsoleComponentUserService.transferIpStr(userVO.getSidecar()));
                componentUser.setUserName(userInfo.getUserName());
                componentUser.setPassword(Base64Util.baseEncode(userInfo.getPassword()));
                addComponentUserList.add(componentUser);
            }
        }
        if (CollectionUtils.isNotEmpty(addComponentUserList)) {
            componentUserDao.batchInsert(addComponentUserList);
        }

    }

    public List<ComponentUserVO> getClusterComponentUser(Long clusterId, Integer componentTypeCode,
                                                         Boolean needRefresh, String agentAddress, boolean uic) {
        clusterId = uic ? clusterService.getCluster(clusterId).getId() : clusterId;
        List<ComponentUser> componentUserList = componentUserDao.getComponentUserByCluster(clusterId, componentTypeCode);
        // 只取数据库数据
        if (!Boolean.TRUE.equals(needRefresh)) {
            return groupComponentByLabel(componentUserList);
        }
        // 刷新数据必须地址
        if (StringUtils.isBlank(agentAddress)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        // 以最新label数据为主
        List<DtScriptAgentLabelDTO> dtScriptAgentLabelDTO = getDtScriptAgentLabel(agentAddress);
        return showComponentLabel(notDbComponentUser(dtScriptAgentLabelDTO, clusterId, componentTypeCode),componentUserList);
    }

    private List<ComponentUserVO> groupComponentByLabel(List<ComponentUser> componentUserList) {
        Map<String, List<ComponentUser>> labelMap =
                componentUserList.stream().collect(Collectors.groupingBy(ComponentUser::getLabel));
        List<ComponentUserVO> componentUserVOList = new ArrayList<>(labelMap.size());
        for (Map.Entry<String, List<ComponentUser>> entry : labelMap.entrySet()) {
            ComponentUserVO componentUserVO = new ComponentUserVO();
            componentUserVO.setLabel(entry.getKey());
            List<ComponentUser> componentUsers = entry.getValue();
            ComponentUser componentUser = componentUsers.get(0);
            componentUserVO.setLabelIp(componentUser.getLabelIp());
            componentUserVO.setComponentTypeCode(componentUser.getComponentTypeCode());
            componentUserVO.setClusterId(componentUser.getClusterId());
            componentUserVO.setIsDefault(componentUser.getIsDefault());
            List<ComponentUserVO.ComponentUserInfo> componentUserInfoList = new ArrayList<>(componentUsers.size());
            componentUsers.forEach(user -> {
                if (StringUtils.isNoneBlank(user.getUserName(), user.getPassword())) {
                    componentUserInfoList.add(new ComponentUserVO.ComponentUserInfo(user.getUserName(), Base64Util.baseDecode(user.getPassword())));
                }
            });
            componentUserVO.setComponentUserInfoList(CollectionUtils.isEmpty(componentUserInfoList) ? null : componentUserInfoList);
            setSidecars(componentUserVO, componentUser.getLabelIp());
            componentUserVOList.add(componentUserVO);
        }
        return componentUserVOList;
    }

    private void setSidecars(ComponentUserVO componentUserVO, String labelIp) {
        List<Pair<String, Integer>> urls = AddressUtil.parseUrlString(labelIp);
        if (CollectionUtils.isNotEmpty(urls)) {
            List<ComponentUserVO.Sidecar> sidecars = new ArrayList<>(urls.size());
            urls.forEach(url -> {
                ComponentUserVO.Sidecar sidecar = new ComponentUserVO.Sidecar();
                sidecar.setIp(url.getLeft());
                sidecar.setPort(url.getRight());
                sidecars.add(sidecar);
            });

            componentUserVO.setSidecar(sidecars);
        }
    }



    public ComponentUser getComponentUser(Long dtUicId, Integer componentTypeCode, String label, String userName) {
        Cluster cluster = clusterService.getCluster(dtUicId);
        return componentUserDao.getComponentUser(cluster.getId(), componentTypeCode, label, userName);
    }


    private List<ComponentUserVO> notDbComponentUser(List<DtScriptAgentLabelDTO> dtScriptAgentLabelDTO, Long clusterId, Integer componentTypeCode) {
        List<ComponentUserVO> componentUserVOList = new ArrayList<>(dtScriptAgentLabelDTO.size());
        for (DtScriptAgentLabelDTO agentLabel : dtScriptAgentLabelDTO) {
            ComponentUserVO componentUserVO = new ComponentUserVO();
            componentUserVO.setLabel(agentLabel.getLabel());
            componentUserVO.setLabelIp(agentLabel.getLocalIp());
            componentUserVO.setClusterId(clusterId);
            componentUserVO.setComponentTypeCode(componentTypeCode);
            componentUserVOList.add(componentUserVO);
            setSidecars(componentUserVO, agentLabel.getLocalIp());
        }
        return componentUserVOList;
    }

    /**
     * 展示节点标签
     * @param componentUserVOList 刷新后的节点标签
     * @param dbComponentUserVOList db 中的节点标签和服务器用户
     * @return
     */
    private List<ComponentUserVO> showComponentLabel(List<ComponentUserVO> componentUserVOList, List<ComponentUser> dbComponentUserVOList) {
        // 存在默认
        boolean hasDefault = false;
        String existDefaultLabel;
        Map<String, List<ComponentUser>> existDbLabelMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(dbComponentUserVOList)) {
            existDefaultLabel = dbComponentUserVOList.stream().filter(ComponentUser::getIsDefault).map(ComponentUser::getLabel).findFirst().orElse(null);
            existDbLabelMap = dbComponentUserVOList.stream().collect(Collectors.groupingBy(ComponentUser::getLabel));
            if (StringUtils.isNotBlank(existDefaultLabel)) {
                for (ComponentUserVO componentUserVO:componentUserVOList) {
                    if (existDefaultLabel.equals(componentUserVO.getLabel())) {
                        componentUserVO.setIsDefault(true);
                        hasDefault = true;
                    }
                }
            }
        }

        // 遍历节点标签
        for (int i = 0; i < componentUserVOList.size(); i++) {
            // 存在默认，其他设置为非默认
            if (hasDefault && Objects.isNull(componentUserVOList.get(i).getIsDefault())) {
                componentUserVOList.get(i).setIsDefault(false);
            }
            // 不存在默认，第一个设置默认
            // else if (!hasDefault && i == 0) {
            //     componentUserVOList.get(0).setIsDefault(true);
            // }
            else if (Objects.isNull(componentUserVOList.get(i).getIsDefault())) {
                componentUserVOList.get(i).setIsDefault(false);
            }

            String label = componentUserVOList.get(i).getLabel();
            List<ComponentUser> dbComponentUsers = existDbLabelMap.get(label);
            if (CollectionUtils.isNotEmpty(dbComponentUsers)) {
                List<ComponentUserVO.ComponentUserInfo> componentUserInfoList = new ArrayList<>(dbComponentUsers.size());
                dbComponentUsers.forEach(user -> {
                    if (StringUtils.isNoneBlank(user.getUserName(), user.getPassword())) {
                        componentUserInfoList.add(new ComponentUserVO.ComponentUserInfo(user.getUserName(), Base64Util.baseDecode(user.getPassword())));
                    }
                });
                componentUserVOList.get(i).setComponentUserInfoList(CollectionUtils.isEmpty(componentUserInfoList) ? null : componentUserInfoList);
            }
        }
        return componentUserVOList;
    }

    private String findDbAgentAddress(Long clusterId, Integer componentTypeCode) {
        return componentConfigDao.listByComponentTypeAndKey(clusterId, "agentAddress", componentTypeCode)
                .stream()
                .map(ComponentConfig::getValue)
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

    public List<Component> listComponents(Long dtUicTenantId, Integer engineType) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);
        return componentDao.listByClusterId(clusterId, null, true);
    }

    public List<Component> listCommonComponents(Long clusterId) {
        if (clusterId == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        return componentFacade.listAllByClusterIdAndComponentTypes(clusterId, EComponentType.CommonScheduling);
    }

    public List<Component> listByClusterId(Long clusterId, Integer typeCode, boolean isDefault) {
        return componentDao.listByClusterId(clusterId, typeCode, isDefault);
    }

    /**
     * 清理配置信息缓存
     */
    public void clearConfigCache() {
        componentConfigService.clearComponentCache();
        clusterService.clearStandaloneCache();
    }

    public List<JdbcUrlTipVO> listJdbcUrlTips() {
        List<ScheduleDict> dicts = scheduleDictService.listByDictType(DictType.JDBC_URL_TIP);
        return dicts.stream().map(d -> {
            EComponentType componentType = null;
            JdbcUrlTipVO vo = new JdbcUrlTipVO();
            try {
                componentType = EComponentType.getByName(d.getDictName());
                vo.setType(componentType.getTypeCode());
                vo.setTip(d.getDictValue());
                vo.setKey(d.getDependName());
            } catch (Exception e) {
                LOGGER.error("find {} componentType error", d.getDictName(), e);
            }
            return vo;
        }).collect(Collectors.toList());
    }

    public String buildHdfsTypeName(Long dtUicTenantId, Long clusterId) {
        if (null == clusterId) {
            clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtUicTenantId);
        }
        Component component = getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(), null);
        if (null == component || StringUtils.isBlank(component.getVersionName())) {
            return "hdfs2";
        }
        String versionName = component.getVersionName();
        List<ScheduleDict> dicts = scheduleDictService.listByDictType(DictType.HDFS_TYPE_NAME);
        Optional<ScheduleDict> dbTypeNames = dicts.stream().filter(dict -> dict.getDictName().equals(versionName.trim())).findFirst();
        if (dbTypeNames.isPresent()) {
            return dbTypeNames.get().getDictValue();
        }
        String hadoopVersion = component.getHadoopVersion();
        if (StringUtils.isBlank(hadoopVersion)) {
            return "hdfs2";
        }
        return EComponentType.HDFS.name().toLowerCase() + hadoopVersion.charAt(0);
    }

    public String getComponentByTenantId(Long tenantId, Integer componentType) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);

        if (clusterId == null) {
            return "";
        }

        if (componentType == null) {
            return "";
        }
        return getComponentByClusterId(clusterId, componentType, false, String.class, null);
    }

    /**
     * 获取组件版本, 没有指定版本则返回默认版本
     *
     * @param clusterId           集群 id
     * @param componentType       组件类型
     * @param componentVersionMap 指定版本
     * @return 组件版本
     */
    public String getComponentVersion(Long clusterId, Integer componentType, Map<Integer, String> componentVersionMap) {
        String componentVersion;
        if (MapUtils.isNotEmpty(componentVersionMap) && Objects.nonNull(componentVersionMap.get(componentType))) {
            componentVersion = componentVersionMap.get(componentType);
        } else {
            // 返回默认版本
            componentVersion = componentDao.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType);
        }
        return formatVersion(componentType, componentVersion);
    }


    /**
     * 新增方法
     *
     * @param clusterId           集群 id
     * @param componentType       组件类型
     * @param componentVersionMap 多版本信息
     * @return 组件配置
     */
    public Component getComponent(Long clusterId, Integer componentType, Map<Integer, String> componentVersionMap) {
        String componentVersion = getComponentVersion(clusterId, componentType, componentVersionMap);
        return getComponentByVersion(clusterId, componentType, componentVersion);
    }

    public Component getComponentByVersion(Long clusterId, Integer componentType, String componentVersion) {
        String realComponentVersion;
        if (StringUtils.isBlank(componentVersion)) {
            realComponentVersion = getComponentVersion(clusterId, componentType, null);
        } else {
            realComponentVersion = componentVersion;
        }
        return getComponentByClusterId(clusterId, componentType, realComponentVersion);
    }

    public Component getComponentByName(Long clusterId, Integer componentType, String componentName) {
        if (StringUtils.isBlank(componentName)) {
            return getComponentByVersion(clusterId, componentType, null);
        }
        return componentDao.getByVersionName(clusterId, componentType, componentName, null);
    }
}