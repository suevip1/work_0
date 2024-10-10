package com.dtstack.engine.master.mockcontainer.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.IComponentVO;
import com.dtstack.engine.api.vo.Pair;
import com.dtstack.engine.common.Resource;
import com.dtstack.engine.common.client.bean.DtScriptAgentLabelDTO;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.ZipUtil;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ComponentUserDao;
import com.dtstack.engine.dao.ConsoleSSLDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.CrudEnum;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentAuxiliaryService;
import com.dtstack.engine.master.impl.ComponentConfigService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.DataSourceService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.model.cluster.ClusterFactory;
import com.dtstack.engine.master.model.cluster.ComponentFacade;
import com.dtstack.engine.master.model.cluster.Part;
import com.dtstack.engine.master.model.cluster.PartCluster;
import com.dtstack.engine.master.model.cluster.datasource.ImmediatelyLoadDataSource;
import com.dtstack.engine.master.model.cluster.part.PartImpl;
import com.dtstack.engine.master.model.cluster.part.ResourcePart;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.cache.RdosSubscribe;
import com.dtstack.engine.master.utils.Krb5FileUtil;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.ComponentUser;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleSSL;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-14 18:51
 */
public class ComponentServiceMock extends BaseMock {

    @MockInvoke(targetClass = DataSourceService.class)
    public void syncLdapDir(Integer componentCode, Long clusterId, String clusterName) {

    }

        @MockInvoke(targetClass = ClusterService.class)
    public ClusterVO getClusterByName(String clusterName) {
        return new ClusterVO();
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void publishComponent(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {

    }

    @MockInvoke(targetClass = RdosSubscribe.class)
    public void setCallBack(Consumer<Pair<String, String>> consumer) {
        return;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getOne(Long id) {
        if (ComponentConfigServiceMock.SPARK_COMPONENT_ID.equals(id)) {
            return mockSparkComponent();
        }
        if (ComponentConfigServiceMock.DELETE_SPARK_COMPONENT_ID.equals(id)) {
            return mockSparkComponent();
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> getComponentVersionByEngineType(Long uicTenantId, Integer componentTypeCode) {
        return mockDefaultComponents().stream().filter(p -> {
            return p.getComponentTypeCode().equals(componentTypeCode);
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ComponentDao.class)
    void deleteById(@Param("componentId") Long componentId) {
        return;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Long getClusterIdByDtUicTenantId(Long dtUicTenantId) {
        return dtUicTenantId;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Integer update(Component component) {
        return 1;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getNextDefaultComponent(Long clusterId, Integer componentTypeCode, Long currentDeleteId) {
        if (ComponentConfigServiceMock.DELETE_SPARK_COMPONENT_ID.equals(currentDeleteId)) {
            return null;
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listByClusterId(Long clusterId, Integer typeCode, boolean isDefault) {
        if (clusterId.equals(-1L)) {
            return mockDefaultComponents();
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listAllByClusterId(Long clusterId) {
        if (clusterId.equals(-1L)) {
            return mockDefaultComponents();
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public void deleteComponentConfig(Long componentId) {
        return;
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public Map<String, Object> getCacheComponentConfigMap(Long clusterId, Integer componentType, boolean isFilter, Map<Integer, String> componentVersionMap, Long componentId) {
        if (componentId == null) {
            if (EComponentType.SFTP.getTypeCode().equals(componentType)) {
                List<ComponentConfig> sftpComponentConfigs = ComponentConfigServiceMock.mockSftpComponentConfigs(-1L);
                return ComponentConfigUtils.convertComponentConfigToMap(sftpComponentConfigs);
            }
            if (EComponentType.SPARK.getTypeCode().equals(componentType)) {
                List<ComponentConfig> sparkComponentConfigs = ComponentConfigServiceMock.mockSparkComponentConfigs(-1L);
                return ComponentConfigUtils.convertComponentConfigToMap(sparkComponentConfigs);
            }
            return Collections.emptyMap();
        }
        if (componentId.equals(ComponentConfigServiceMock.SPARK_COMPONENT_ID)) {
            List<ComponentConfig> sparkComponentConfigs = ComponentConfigServiceMock.mockSparkComponentConfigs(-1L);
            return ComponentConfigUtils.convertComponentConfigToMap(sparkComponentConfigs);
        }
        return Collections.emptyMap();
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public void clearComponentCache() {
        return;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public void clearStandaloneCache() {
        return;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public Cluster getCluster(Long dtUicTenantId) {
        if (dtUicTenantId.equals(-1L)) {
            return ClusterServiceMock.mockDefaultCluster();
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentFacade.class)
    public List<Component> listAllByClusterIdAndComponentTypes(Long clusterId, List<EComponentType> types) {
        List<Component> components = ClusterServiceMock.mockDefaultComponents();
        List<Integer> eComponentTypes = types.stream().map(EComponentType::getTypeCode).collect(Collectors.toList());
        return components.stream().filter(c -> {
            return eComponentTypes.contains(c.getComponentTypeCode());
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ScheduleDictService.class)
    public Map<String, List<ClientTemplate>> getVersion(Long clusterId) {
        return Collections.emptyMap();
    }

    @MockInvoke(targetClass = ScheduleDictService.class)
    public Map<String, ScheduleDict> findTipGroup(Integer componentTypeCode) {
        return Collections.emptyMap();
    }

    @MockInvoke(targetClass = TenantService.class)
    public Set<Long> findUicTenantIdsByClusterId(Long clusterId) {
        if (clusterId.equals(-1L)) {
            return Sets.newHashSet(-1L);
        }
        return Collections.emptySet();
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void publishSqlComponent(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {
        return;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void syncToRangerIfNeeded(Long clusterId, Component component, Set<Long> dtUicTenantIds, CrudEnum crudEnum) {
        return;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void bindHistoryLdapIfNeeded(Integer componentCode, Long clusterId, CrudEnum crudEnum) {
        return;
    }

    @MockInvoke(targetClass = DataSourceService.class)
    public void syncHistoryInfoToRangerIfNeeded(Long clusterId, Component component, Set<Long> dtUicTenantIds) {
        return;
    }

    @MockInvoke(targetClass = ConsoleCache.class)
    public void publishRemoveMessage(String tenantId) {
        return;
    }

    @MockInvoke(targetClass = KerberosDao.class)
    KerberosConfig getByComponentType(Long clusterId, Integer componentType, String componentVersion) {
        return new KerberosConfig();
    }

    @MockInvoke(targetClass = KerberosDao.class)
    Integer update(KerberosConfig kerberosConfig) {
        return 1;
    }

    @MockInvoke(targetClass = KerberosDao.class)
    Integer insert(KerberosConfig kerberosConfig) {
        return 1;
    }

    @MockInvoke(targetClass = KerberosDao.class, targetMethod = "deleteByComponent")
    void deleteByComponentOfKerberosDao(Long clusterId, Integer componentTypeCode, String componentVersion) {
        return;
    }

    @MockInvoke(targetClass = KerberosDao.class)
    List<KerberosConfig> listAll() {
        KerberosConfig k1 = new KerberosConfig();
        k1.setOpenKerberos(1);
        k1.setClusterId(9L);
        k1.setRemotePath("/data/kerberos_cdh/CONSOLE_kerberos/YARN/kerberos");
        k1.setPrincipal("hdfs/eng-cdh1@DTSTACK.COM");
        k1.setComponentType(EComponentType.YARN.getTypeCode());
        k1.setKrbName("krb5.conf");
        k1.setPrincipals("hdfs/eng-cdh1@DTSTACK.COM");
        k1.setMergeKrbContent("[domain_realm]\n" +
                ".dtstack.com = DTSTACK.COM\n" +
                "dtstack.com = DTSTACK.COM\n" +
                "[realms]\n" +
                "TDH = {\n" +
                "tdh-621-node02 = TDH\n" +
                "tdh-621-node01 = TDH\n" +
                "}\n" +
                "DTSTACK.COM = {\n" +
                "kdc = 172.16.100.208\n" +
                "admin_server = 172.16.100.208\n" +
                "}\n" +
                "\n" +
                "[logging]\n" +
                "default = FILE:/var/log/krb5libs.log\n" +
                "kdc = FILE:/var/log/krb5kdc.log\n" +
                "admin_server = FILE:/var/log/kadmind.log\n" +
                "\n" +
                "[libdefaults]\n" +
                "default_realm = DTSTACK.COM \n" +
                "dns_lookup_realm = false\n" +
                "dns_lookup_kdc = false\n" +
                "ticket_lifetime = 24h\n" +
                "renew_lifetime = 7d\n" +
                "forwardable = true\n" +
                "allow_weak_crypto = true\n" +
                "udp_preference_limit = 1000000\n" +
                "default_ccache_name = FILE:/tmp/krb5cc_%{uid}");
        return Lists.newArrayList(k1);
    }

    @MockInvoke(targetClass = ClusterDao.class, targetMethod = "getOne")
    Cluster getOneCluster(Long clusterId) {
        return ClusterServiceMock.mockDefaultCluster();
    }

    @MockInvoke(targetClass = ClusterDao.class)
    void updateGmtModified(Long clusterId) {
        return;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getByClusterName(String clusterName) {
        if ("default".equals(clusterName)) {
            return ClusterServiceMock.mockDefaultCluster();
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentUserDao.class)
    ComponentUser getComponentUser(Long clusterId, Integer componentTypeCode, String label, String userName) {
        ComponentUser componentUser = new ComponentUser();
        componentUser.setComponentTypeCode(componentTypeCode);
        componentUser.setLabel("default");
        componentUser.setUserName(userName);
        componentUser.setClusterId(clusterId);
        return componentUser;
    }

    @MockInvoke(targetClass = ComponentUserDao.class)
    List<ComponentUser> getComponentUserByCluster(Long clusterId, Integer componentTypeCode) {
        ComponentUser componentUser = new ComponentUser();
        componentUser.setComponentTypeCode(componentTypeCode);
        componentUser.setLabel("default");
        componentUser.setClusterId(clusterId);
        return Lists.newArrayList(componentUser);
    }

    @MockInvoke(targetClass = ComponentUserDao.class)
    void deleteByComponentAndCluster(Long clusterId, Integer componentTypeCode) {
        return;
    }

    @MockInvoke(targetClass = ComponentUserDao.class)
    void batchInsert(List<ComponentUser> addComponentUserList) {
        return;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public ClusterVO addCluster(ClusterDTO clusterDTO) {
        ClusterVO clusterVO = new ClusterVO();
        BeanUtils.copyProperties(clusterDTO, clusterVO);
        return clusterVO;
    }

    @MockInvoke(targetClass = ZipUtil.class)
    public static List<File> upzipFile(String zipPath, String descDir) {
        if ("kerberos_file_name.zip".equalsIgnoreCase(zipPath)) {
            File keyTab = FileUtil.newFile(descDir + File.separator + "keyTab.keytab");
            File krb = FileUtil.newFile(descDir + File.separator + "krb5.conf");
            return Lists.newArrayList(keyTab, krb);
        }
        if ("ssl.zip".equalsIgnoreCase(zipPath)) {
            File sslClient = FileUtil.newFile(descDir + File.separator + "ssl-client.xml");
            File trustStore = FileUtil.newFile(descDir + File.separator + "truststore.jks");
            return Lists.newArrayList(sslClient, trustStore);
        }
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = ComponentService.class)
    private List<PrincipalName> getPrincipal(File file) {
        PrincipalName principalName = new PrincipalName();
        principalName.setNameStrings(Lists.newArrayList("principalName"));
        return Lists.newArrayList(principalName);
    }

    @MockInvoke(targetClass = ComponentService.class)
    private List<File> decompressSSLFile(Resource resource, String localSSLDir) {
        if (localSSLDir.contains("ssl")) {
            File sslClient = FileUtil.newFile("ssl-client.xml");
            File trustStore = FileUtil.newFile("truststore.jks");
            return Lists.newArrayList(sslClient, trustStore);
        }
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = ComponentService.class)
    private void checkSSLClientFile(File xmlFile, File truststoreFile) {
        return;
    }

    @MockInvoke(targetClass = SftpFileManage.class)
    public boolean uploadFile(String remotePath, String localPath) {
        return true;
    }

    @MockInvoke(targetClass = SftpFileManage.class)
    public boolean uploadFile(String remotePath, File file) {
        return true;
    }

    @MockInvoke(targetClass = MD5Util.class)
    public static String getMd5StringFromFiles(File... files) {
        Arrays.sort(files, Comparator.comparing(File::getName));
        StringBuilder result = new StringBuilder();
        for (File file : files) {
            result.append(file.getAbsolutePath());
        }
        return MD5Util.getMd5String(result.toString());
    }

    @MockInvoke(targetClass = SftpFileManage.class)
    public boolean deleteDir(String remotePath) {
        return true;
    }

    @MockInvoke(targetClass = FileUtils.class)
    public static String readFileToString(File file) throws IOException {
        return "{}";
    }

    @MockInvoke(targetClass = FileUtils.class)
    public static void forceDelete(File file) throws IOException {
        return;
    }

    @MockInvoke(targetClass = FileUtils.class)
    public static void write(File file, CharSequence data) throws IOException {
        return;
    }

    @MockInvoke(targetClass = Krb5FileUtil.class)
    public static Map<String, HashMap<String, String>> readKrb5ByPath(String krb5Path) {
        Map<String, HashMap<String, String>> krb5Contents = new HashMap<>();
        Map loggingMap = krb5Contents.computeIfAbsent("logging", k -> new HashMap<String, String>());
        loggingMap.put("kdc", "FILE:/var/log/krb5kdc.log");
        Map libdefaultsMap = krb5Contents.computeIfAbsent("libdefaults", k -> new HashMap<String, String>());
        libdefaultsMap.put("dns_lookup_realm", "false");
        return krb5Contents;
    }

    @MockInvoke(targetClass = ConsoleSSLDao.class)
    List<ConsoleSSL> getByClusterIdAndComponentTypeAndComponentVersion(Long clusterId, Integer componentType, String componentVersion) {
        ConsoleSSL consoleSSL = new ConsoleSSL();
        consoleSSL.setComponentType(componentType);
        consoleSSL.setClusterId(clusterId);
        consoleSSL.setRemotePath("/data/sftp/CONSOLE_default_new/YARN/ssl/client");
        consoleSSL.setTruststore("test.truststore");
        consoleSSL.setSslClient("ssl-client.xml");
        consoleSSL.setMd5("1490bcdc2b9b5a4bb0b95fecc232340d");
        return Lists.newArrayList(consoleSSL);
    }

    @MockInvoke(targetClass = ConsoleSSLDao.class)
    Integer insert(ConsoleSSL engine) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleSSLDao.class)
    Integer update(ConsoleSSL engine) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleSSLDao.class)
    Integer deleteByComponent(Long clusterId,
                              Integer componentType,
                              String componentVersion) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    List<Long> listBoundedTenants(Long clusterId) {
        return Lists.newArrayList(-1L);
    }

    @MockInvoke(targetClass = ScheduleDictService.class)
    public List<ScheduleDict> listByDictType(DictType dictType) {
        if (DictType.SPARK_VERSION.equals(dictType)) {
            ScheduleDict s = new ScheduleDict();
            s.setDictName("2.1");
            s.setDictValue("210");
            return Lists.newArrayList(s);
        }
        if (DictType.JDBC_URL_TIP.equals(dictType)) {
            ScheduleDict s = new ScheduleDict();
            s.setDictName("HiveServer");
            s.setDictValue("jdbc:hive2://host:port/%s");
            return Lists.newArrayList(s);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ScheduleDictService.class)
    public ScheduleDict getByNameAndValue(Integer dictType, String dictName, String dictValue, String dependName) {
        if (DictType.SSL_TEMPLATE.type.equals(dictType) && "ssl_client".equals(dictName)) {
            ScheduleDict dict = new ScheduleDict();
            dict.setDictValue("<configuration>\n" +
                    "\n" +
                    "  <!-- Client certificate Store -->\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.keystore.type</name>\n" +
                    "    <value>jks</value>\n" +
                    "    <desc>keystore 类型，默认为 jks</desc>\n" +
                    "  </property>\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.keystore.location</name>\n" +
                    "    <value>${user.home}/keystores/client-keystore.jks</value>\n" +
                    "    <desc>keystore 储存路径，双向认证时，服务端需要验证客户端需要该参数，单向认证不需要该参数</desc>\n" +
                    "  </property>\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.keystore.password</name>\n" +
                    "    <value>clientfoo</value>\n" +
                    "    <desc>keystore 密码，双向认证时，服务端需要验证客户端需要该参数，单向认证不需要该参数</desc>\n" +
                    "  </property>\n" +
                    "\n" +
                    "  <!-- Client Trust Store -->\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.truststore.type</name>\n" +
                    "    <value>jks</value>\n" +
                    "    <desc></desc>\n" +
                    "  </property>\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.truststore.location</name>\n" +
                    "    <value>truststore.jks</value>\n" +
                    "    <desc>truststore 储存路径,相对路径</desc>\n" +
                    "  </property>\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.truststore.password</name>\n" +
                    "    <value>clientserverbar</value>\n" +
                    "    <desc>truststore 密码</desc>\n" +
                    "  </property>\n" +
                    "  <property>\n" +
                    "    <name>ssl.client.truststore.reload.interval</name>\n" +
                    "    <value>10000</value>\n" +
                    "    <desc>truststore 刷新时间</desc>\n" +
                    "  </property>\n" +
                    "</configuration>");
            return dict;
        }
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    void changeTaskVersion(String targetTaskShadeComponentVersion, List<String> changeTaskShadeComponentVersions,
                           List<Long> dtUicTenantIdByIds, Integer appType) {
        return;
    }

    @MockInvoke(targetClass = ClusterFactory.class)
    public PartCluster newImmediatelyLoadCluster(Long clusterId) {
        return null;
    }

    @MockInvoke(targetClass = ImmediatelyLoadDataSource.class)
    List<Component> listAllByClusterId() {
        return mockDefaultComponents();
    }

    @MockInvoke(targetClass = ComponentFacade.class, targetMethod = "listAllByClusterId")
    public List<Component> listAllByClusterIdOfComponentFacade(Long clusterId) {
        if (clusterId.equals(-1L)) {
            return mockDefaultComponents();
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = PartCluster.class)
    private Map<EComponentScheduleType, List<Component>> initComponentScheduleGroup(List<Component> components) {
        return Collections.emptyMap();
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public ComponentConfig getComponentConfigByKey(Long componentId, String key) {

        if (componentId.equals(ComponentConfigServiceMock.SPARK_COMPONENT_ID)) {
            return ComponentConfigServiceMock.mockSparkComponentConfigs(-1L).stream().filter(config -> config.getKey().equals(key)).findFirst().orElse(null);
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public List<IComponentVO> getComponentVoByComponent(List<Component> components, boolean isFilter, Long clusterId, boolean multiVersion) {
        return Collections.emptyList();
    }


    @MockInvoke(targetClass = ComponentConfigService.class)
    public void addOrUpdateComponentConfig(List<ClientTemplate> clientTemplates, Long componentId, Long clusterId, Integer componentTypeCode) {
        return;
    }

    @MockInvoke(targetClass = PartCluster.class)
    public Part create(EComponentType componentType, String versionName, EComponentType storeType, Integer deployType) {
        if (componentType.equals(EComponentType.YARN)) {
            return new ResourcePart(EComponentType.YARN, versionName, storeType, null, null, null);
        }
        return new PartImpl(componentType, versionName, storeType, null, null, null, null);
    }

    @MockInvoke(targetClass = Part.class)
    public String getPluginName() {
        // todo 如何获取实例变量？
        return "yarn2";
    }

    @MockInvoke(targetClass = Part.class)
    String getVersionValue() {
        return "";
    }

    @MockInvoke(targetClass = Part.class)
    List<ComponentConfig> loadTemplate() {
        return ComponentConfigServiceMock.mockSftpComponentConfigs(-1L);
    }

    @MockInvoke(targetClass = DataSourceXOperator.class)
    public ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId, String versionName) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        if (engineType.startsWith("yarn")) {
            componentTestResult.setComponentTypeCode(EComponentType.YARN.getTypeCode());
            componentTestResult.setVersionName(versionName);
        }
        return componentTestResult;
    }

    @MockInvoke(targetClass = ComponentAuxiliaryService.class)
    public void removeAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {
        return;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getByClusterIdAndComponentType(Long clusterId, Integer type, String componentVersion, Integer deployType) {
        if (type.equals(EComponentType.KUBERNETES.getTypeCode())) {
            return mockK8sComponent();
        }
        return mockDefaultComponents().stream().filter(p -> {
            return p.getComponentTypeCode().equals(type);
        }).findFirst().orElse(null);
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Integer insert(Component component) {
        return 1;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getMetadataComponent(Long clusterId) {
        Component hiveServer = new Component();
        hiveServer.setComponentTypeCode(EComponentType.HIVE_SERVER.getTypeCode());
        hiveServer.setClusterId(-1L);
        hiveServer.setStoreType(EComponentType.HDFS.getTypeCode());
        hiveServer.setIsDefault(true);
        hiveServer.setHadoopVersion("2.x");
        hiveServer.setVersionName("2.x");
        hiveServer.setIsMetadata(1);
        hiveServer.setId(495L);
        return hiveServer;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getByVersionName(Long clusterId, Integer type, String versionName, Integer deployType) {
        return mockDefaultComponents().stream().filter(p -> p.getComponentTypeCode().equals(EComponentType.YARN.getTypeCode()))
                .findFirst().orElse(null);
    }

    @MockInvoke(targetClass = ComponentDao.class, targetMethod = "listAllByClusterIdAndComponentTypes")
    List<Component> listAllByClusterIdAndComponentTypesOfComponentDao(Long clusterId, List<Integer> componentTypes) {
        return mockDefaultComponents().stream().filter(p -> componentTypes.contains(p.getComponentTypeCode()))
                .collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listAllByClusterIdAndComponentType(Long clusterId, Integer componentType) {
        return mockDefaultComponents().stream().filter(p -> p.getComponentTypeCode().equals(componentType))
                .collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listAllByClusterIdAndComponentTypeWithLimit(Long clusterId,
                                                                Integer componentType,
                                                                int max) {
        return mockDefaultComponents().stream().filter(p -> {
            return p.getComponentTypeCode().equals(componentType);
        }).limit(max).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ComponentDao.class)
    List<Component> listAllByClusterIdAndComponentTypeAndVersionName(
            Long clusterId, Integer componentType,
            String versionName) {
        return mockDefaultComponents().stream().filter(p -> {
            return p.getComponentTypeCode().equals(componentType);
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByComponentIds(List<Long> componentIds, boolean isFilter) {
        return ComponentConfigServiceMock.mockSparkComponentConfigs(-1L);
    }


    @MockInvoke(targetClass = ComponentDao.class)
    String getDefaultComponentVersionByClusterAndComponentType(Long clusterId, Integer type) {
        Component component = mockDefaultComponents().stream().filter(p -> {
            return p.getComponentTypeCode().equals(type);
        }).findFirst().orElse(null);
        return component != null ? component.getHadoopVersion() : null;
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String engineType, String pluginInfo) throws Exception {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId) {
        return null;
    }

    @MockInvoke(targetClass = QueueDao.class)
    List<Queue> listByIds(List<Long> ids) {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = QueueDao.class)
    Integer insert(Queue queue) {
        return 1;
    }

    public static List<Component> mockDefaultComponentsK8s() {
        Component sftp = new Component();
        sftp.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        sftp.setClusterId(-1L);
        sftp.setIsDefault(true);
        sftp.setId(3417L);

        Component k8s = mockK8sComponent();

        Component hdfs = new Component();
        hdfs.setComponentTypeCode(EComponentType.HDFS.getTypeCode());
        hdfs.setClusterId(-1L);
        hdfs.setIsDefault(true);
        hdfs.setId(939L);
        hdfs.setVersionName("Apache Hadoop 2.x");
        hdfs.setHadoopVersion("2.7.6");
        hdfs.setStoreType(EComponentType.HDFS.getTypeCode());

        Component flink = new Component();
        flink.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
        flink.setClusterId(-1L);
        flink.setStoreType(EComponentType.HDFS.getTypeCode());
        flink.setHadoopVersion("110");
        flink.setDeployType(EDeployMode.PERJOB.getType());
        flink.setVersionName("1.10");
        flink.setIsDefault(true);
        flink.setId(4205L);

        Component spark = new Component();
        spark.setComponentTypeCode(EComponentType.SPARK.getTypeCode());
        spark.setClusterId(-1L);
        spark.setStoreType(EComponentType.HDFS.getTypeCode());
        spark.setIsDefault(true);
        spark.setHadoopVersion("240");
        spark.setVersionName("2.4");
        spark.setId(ComponentConfigServiceMock.SPARK_COMPONENT_ID);

        Component hiveServer = new Component();
        hiveServer.setComponentTypeCode(EComponentType.HIVE_SERVER.getTypeCode());
        hiveServer.setClusterId(-1L);
        hiveServer.setStoreType(EComponentType.HDFS.getTypeCode());
        hiveServer.setIsDefault(true);
        hiveServer.setHadoopVersion("2.x");
        hiveServer.setVersionName("2.x");
        hiveServer.setIsMetadata(1);
        hiveServer.setId(495L);

        return Lists.newArrayList(sftp, k8s, hdfs, flink, spark, hiveServer);
    }

    public static List<Component> mockDefaultComponents() {
        Component sftp = new Component();
        sftp.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        sftp.setClusterId(-1L);
        sftp.setIsDefault(true);
        sftp.setId(3417L);

        Component yarn = new Component();
        yarn.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        yarn.setClusterId(-1L);
        yarn.setIsDefault(true);
        yarn.setVersionName("Apache Hadoop 2.x");
        yarn.setHadoopVersion("2.7.6");
        yarn.setId(937L);

        Component hdfs = new Component();
        hdfs.setComponentTypeCode(EComponentType.HDFS.getTypeCode());
        hdfs.setClusterId(-1L);
        hdfs.setIsDefault(true);
        hdfs.setId(939L);
        hdfs.setVersionName("Apache Hadoop 2.x");
        hdfs.setHadoopVersion("2.7.6");
        hdfs.setStoreType(EComponentType.HDFS.getTypeCode());

        Component flink = new Component();
        flink.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
        flink.setClusterId(-1L);
        flink.setStoreType(EComponentType.HDFS.getTypeCode());
        flink.setHadoopVersion("110");
        flink.setDeployType(EDeployMode.PERJOB.getType());
        flink.setVersionName("1.10");
        flink.setIsDefault(true);
        flink.setId(4205L);

        Component spark = new Component();
        spark.setComponentTypeCode(EComponentType.SPARK.getTypeCode());
        spark.setClusterId(-1L);
        spark.setStoreType(EComponentType.HDFS.getTypeCode());
        spark.setIsDefault(true);
        spark.setHadoopVersion("240");
        spark.setVersionName("2.4");
        spark.setId(ComponentConfigServiceMock.SPARK_COMPONENT_ID);

        Component hiveServer = new Component();
        hiveServer.setComponentTypeCode(EComponentType.HIVE_SERVER.getTypeCode());
        hiveServer.setClusterId(-1L);
        hiveServer.setStoreType(EComponentType.HDFS.getTypeCode());
        hiveServer.setIsDefault(true);
        hiveServer.setHadoopVersion("2.x");
        hiveServer.setVersionName("2.x");
        hiveServer.setIsMetadata(1);
        hiveServer.setId(495L);

        return Lists.newArrayList(sftp, yarn, hdfs, flink, spark, hiveServer);
    }

    public static Component mockSparkComponent() {
        Component spark = new Component();
        spark.setComponentTypeCode(EComponentType.SPARK.getTypeCode());
        spark.setClusterId(-1L);
        spark.setStoreType(EComponentType.HDFS.getTypeCode());
        spark.setIsDefault(true);
        spark.setHadoopVersion("240");
        spark.setVersionName("2.4");
        return spark;
    }

    public static Component mockK8sComponent() {
        Component k8s = new Component();
        k8s.setComponentTypeCode(EComponentType.KUBERNETES.getTypeCode());
        k8s.setClusterId(-1L);
        k8s.setStoreType(EComponentType.HDFS.getTypeCode());
        k8s.setIsDefault(true);
        k8s.setHadoopVersion("2.7.6");
        k8s.setVersionName("");
        return k8s;
    }
}