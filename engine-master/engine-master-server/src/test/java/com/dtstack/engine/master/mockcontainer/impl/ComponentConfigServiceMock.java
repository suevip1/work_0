package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-11 10:59
 */
public class ComponentConfigServiceMock extends BaseMock {
    /**
     * {clusterId, componentTypeCode, componnetId}
     */
    static Table<Long, Integer, Long> mockClusterId2ComponentId = HashBasedTable.create();

    public static final Long SPARK_COMPONENT_ID = 3825L;

    public static final Long DELETE_SPARK_COMPONENT_ID = 383L;

    static {
        mockClusterId2ComponentId.put(-1L, EComponentType.SPARK.getTypeCode(), SPARK_COMPONENT_ID);
    }

    @MockInvoke(targetClass = Context.class)
    public void populateTip(List<ComponentConfig> componentConfigs,Integer componentType) {

    }


    @MockInvoke(targetClass = Context.class)
    public void populateTip(List<ComponentConfig> componentConfigs) {

    }

        @MockInvoke(targetClass = ComponentDao.class)
    Component getByClusterIdAndComponentType(Long clusterId, Integer type, String componentVersion, Integer deployType) {
        if (clusterId.equals(-1L) && EComponentType.SPARK.getTypeCode().equals(type)) {
            Component sparkComponent = new Component();
            sparkComponent.setId(mockClusterId2ComponentId.get(-1L, EComponentType.SPARK.getTypeCode()));
            return sparkComponent;
        }
        return new Component();
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByComponentId(Long componentId, boolean isFilter) {
        if (componentId.equals(SPARK_COMPONENT_ID)) {
            return mockSparkComponentConfigs(-1L);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    void update(ComponentConfig componentConfig) {
        return;
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByClusterId(Long clusterId, boolean isFilter) {
        if (!clusterId.equals(-1L)) {
            return Collections.emptyList();
        }

        return mockSparkComponentConfigs(clusterId);
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByComponentTypeAndKey(Long clusterId, String key, Integer componentTypeCode) {
        if (clusterId.equals(-1L) && EComponentType.SPARK.getTypeCode().equals(componentTypeCode)) {
            return mockSparkComponentConfigs(clusterId).stream().filter(config -> {
                return config.getKey().equals(key);
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByType(Long componentId, String type) {
        if (componentId.equals(SPARK_COMPONENT_ID)) {
            return mockSparkComponentConfigs(-1L).stream().filter(config -> config.getType().equals(type)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    public ComponentConfig listByKey(Long componentId, String key) {
        if (componentId.equals(SPARK_COMPONENT_ID)) {
            return mockSparkComponentConfigs(-1L).stream().filter(config -> config.getKey().equals(key)).findFirst().get();
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    Integer deleteByComponentId(Long componentId) {
        return 1;
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    Integer insertBatch(List<ComponentConfig> componentConfigs) {
        return componentConfigs.size();
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    Integer deleteByComponentIdAndKeyAndComponentTypeCode(Long componentId,
                                                          String key,
                                                          Integer typeCode) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleDictService.class)
    public Map<String, ScheduleDict> findTipGroup(Integer componentTypeCode) {
        Map<String, ScheduleDict> map = new HashMap<>();
        ScheduleDict s1 = new ScheduleDict();
        s1.setDictName("主要");
        s1.setDictDesc(String.valueOf(EComponentType.SPARK.getTypeCode()));
        map.put(s1.getDictName(), s1);
        return map;
    }

    public static List<ComponentConfig> mockSparkComponentConfigs(Long clusterId) {
        ComponentConfig sparkConfig1 = new ComponentConfig();
        sparkConfig1.setComponentId(mockClusterId2ComponentId.get(clusterId, EComponentType.SPARK.getTypeCode()));
        sparkConfig1.setComponentTypeCode(EComponentType.SPARK.getTypeCode());
        sparkConfig1.setKey("perjob");
        sparkConfig1.setValue("perjob");
        sparkConfig1.setDependencyKey("deploymode");
        sparkConfig1.setDependencyValue("perjob");
        sparkConfig1.setType("GROUP");
        sparkConfig1.setRequired(1);

        ComponentConfig sparkConfig2 = new ComponentConfig();
        BeanUtils.copyProperties(sparkConfig1, sparkConfig2);
        sparkConfig2.setKey("spark.submit.deployMode");
        sparkConfig2.setValue("cluster");
        sparkConfig2.setDependencyKey("deploymode$perjob");
        sparkConfig2.setType("INPUT");

        ComponentConfig sparkConfig3 = new ComponentConfig();
        BeanUtils.copyProperties(sparkConfig1, sparkConfig3);
        sparkConfig3.setKey("deploymode");
        sparkConfig3.setValue("[\"perjob\"]");
        sparkConfig3.setDependencyKey("");
        sparkConfig3.setType("CHECKBOX");

        return Lists.newArrayList(sparkConfig1, sparkConfig2, sparkConfig3);
    }

    public static List<ComponentConfig> mockSftpComponentConfigs(Long clusterId) {
        ComponentConfig sftpConfig = new ComponentConfig();
        sftpConfig.setComponentId(3417L);
        sftpConfig.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        sftpConfig.setKey("auth");
        sftpConfig.setValue("1");
        sftpConfig.setDependencyKey(null);
        sftpConfig.setDependencyValue(null);
        sftpConfig.setType("RADIO_LINKAGE");
        sftpConfig.setRequired(1);

        ComponentConfig sftpConfig2 = new ComponentConfig();
        BeanUtils.copyProperties(sftpConfig, sftpConfig2);
        sftpConfig2.setKey("password");
        sftpConfig2.setValue("1");
        sftpConfig2.setDependencyKey("auth");
        sftpConfig2.setType("");

        ComponentConfig sftpConfig3 = new ComponentConfig();
        BeanUtils.copyProperties(sftpConfig, sftpConfig3);
        sftpConfig3.setKey("password");
        sftpConfig3.setValue("dt@sz.com");
        sftpConfig3.setDependencyKey("auth$password");
        sftpConfig3.setType("PASSWORD");

        ComponentConfig sftpConfig4 = new ComponentConfig();
        BeanUtils.copyProperties(sftpConfig, sftpConfig4);
        sftpConfig4.setKey("host");
        sftpConfig4.setValue("172.16.100.251");
        sftpConfig4.setDependencyKey(null);
        sftpConfig4.setType("INPUT");

        ComponentConfig sftpConfig5 = new ComponentConfig();
        BeanUtils.copyProperties(sftpConfig, sftpConfig5);
        sftpConfig5.setKey("port");
        sftpConfig5.setValue("22");
        sftpConfig5.setDependencyKey(null);
        sftpConfig5.setType("INPUT");


        ComponentConfig sftpConfig6 = new ComponentConfig();
        BeanUtils.copyProperties(sftpConfig, sftpConfig6);
        sftpConfig6.setKey("path");
        sftpConfig6.setValue("/data/sftp");
        sftpConfig6.setDependencyKey(null);
        sftpConfig6.setType("INPUT");

        ComponentConfig sftpConfig7 = new ComponentConfig();
        BeanUtils.copyProperties(sftpConfig, sftpConfig7);
        sftpConfig7.setKey("username");
        sftpConfig7.setValue("root");
        sftpConfig7.setDependencyKey(null);
        sftpConfig7.setType("INPUT");

        return Lists.newArrayList(sftpConfig, sftpConfig2, sftpConfig3, sftpConfig4, sftpConfig5, sftpConfig6, sftpConfig7);
    }

    public static List<ComponentConfig> mockHiveComponentConfigs(Long clusterId) {
        ComponentConfig hiveConfig = new ComponentConfig();
        hiveConfig.setComponentId(495L);
        hiveConfig.setComponentTypeCode(EComponentType.HIVE_SERVER.getTypeCode());
        hiveConfig.setKey("jdbcUrl");
        hiveConfig.setValue("jdbc:hive2://172.16.85.248:10004/%s");
        hiveConfig.setDependencyKey(null);
        hiveConfig.setDependencyValue(null);
        hiveConfig.setType("INPUT");
        hiveConfig.setRequired(1);

        ComponentConfig hiveConfig2 = new ComponentConfig();
        BeanUtils.copyProperties(hiveConfig, hiveConfig2);
        hiveConfig2.setKey("username");
        hiveConfig2.setValue("root");
        hiveConfig2.setDependencyKey("");
        hiveConfig2.setType("INPUT");

        ComponentConfig hiveConfig3 = new ComponentConfig();
        BeanUtils.copyProperties(hiveConfig, hiveConfig3);
        hiveConfig3.setKey("password");
        hiveConfig3.setValue("aaa");
        hiveConfig3.setDependencyKey("");
        hiveConfig3.setType("PASSWORD");

        ComponentConfig hiveConfig4 = new ComponentConfig();
        BeanUtils.copyProperties(hiveConfig, hiveConfig4);
        hiveConfig4.setKey("serviceName");
        hiveConfig4.setValue("serviceName");
        hiveConfig4.setDependencyKey("");
        hiveConfig4.setType("INPUT");


        return Lists.newArrayList(hiveConfig, hiveConfig2, hiveConfig3, hiveConfig4);
    }

    public static List<ClientTemplate> mockSparkComponnetTemplate() {
        String ct = "[{\"dependencyKey\":\"\",\"dependencyValue\":\"\",\"key\":\"deploymode\",\"required\":true,\"type\":\"CHECKBOX\",\"value\":[\"perjob\"],\"values\":[{\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"perjob\",\"key\":\"perjob\",\"required\":true,\"type\":\"GROUP\",\"value\":\"perjob\",\"values\":[{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"主要\",\"key\":\"spark.submit.deployMode\",\"keyDescribe\":\"spark driver的jvm扩展参数\",\"required\":true,\"sort\":1,\"type\":\"INPUT\",\"value\":\"cluster\"},{\"dependName\":\"主要\",\"key\":\"sparkPythonExtLibPath\",\"keyDescribe\":\"远程存储系统上pyspark.zip和py4j-0.10.7-src.zip的路径\\n注：pyspark.zip和py4j-0.10.7-src.zip在$SPARK_HOME/python/lib路径下获取\",\"required\":true,\"sort\":2,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip\"},{\"dependName\":\"主要\",\"key\":\"sparkSqlProxyPath\",\"keyDescribe\":\"远程存储系统上spark-sql-proxy.jar路径\\n注：spark-sql-proxy.jar是用来执行spark sql的jar包\",\"required\":true,\"sort\":3,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/user/spark/client/spark-sql-proxy-feat_5.0.x_4877.jar\"},{\"dependName\":\"主要\",\"key\":\"spark.yarn.maxAppAttempts\",\"keyDescribe\":\"spark driver最大尝试次数, 默认为yarn上yarn.resourcemanager.am.max-attempts配置的值\\n注：如果spark.yarn.maxAppAttempts配置的大于yarn.resourcemanager.am.max-attempts则无效\",\"required\":true,\"sort\":5,\"type\":\"INPUT\",\"value\":\"1\"},{\"dependName\":\"主要\",\"key\":\"sparkYarnArchive\",\"keyDescribe\":\"远程存储系统上spark jars的路径\",\"required\":true,\"sort\":6,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/sparkjars/jars\"},{\"dependName\":\"主要\",\"key\":\"yarnAccepterTaskNumber\",\"keyDescribe\":\"允许yarn上同时存在状态为accepter的任务数量，当达到这个值后会禁止任务提交\",\"required\":false,\"sort\":7,\"type\":\"INPUT\",\"value\":\"3\"},{\"dependName\":\"主要\",\"key\":\"spark.speculation\",\"keyDescribe\":\"spark任务推测行为\",\"required\":true,\"sort\":8,\"type\":\"INPUT\",\"value\":\"true\"}],\"key\":\"主要\",\"required\":false,\"sort\":1,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"资源\",\"key\":\"spark.executor.cores\",\"keyDescribe\":\"每个executor可以使用的cpu核数\",\"required\":true,\"sort\":1,\"type\":\"INPUT\",\"value\":\"1\"},{\"dependName\":\"资源\",\"key\":\"spark.executor.memory\",\"keyDescribe\":\"每个executor可以使用的内存量\",\"required\":true,\"sort\":2,\"type\":\"INPUT\",\"value\":\"512m\"},{\"dependName\":\"资源\",\"key\":\"spark.executor.instances\",\"keyDescribe\":\"executor实例数\",\"required\":true,\"sort\":3,\"type\":\"INPUT\",\"value\":\"1\"},{\"dependName\":\"资源\",\"key\":\"spark.cores.max\",\"keyDescribe\":\" standalone模式下任务最大能申请的cpu核数\",\"required\":true,\"sort\":4,\"type\":\"INPUT\",\"value\":\"1\"}],\"key\":\"资源\",\"required\":false,\"sort\":2,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"网络\",\"key\":\"spark.network.timeout\",\"keyDescribe\":\"spark中所有网络交互的最大超时时间\",\"required\":true,\"sort\":1,\"type\":\"INPUT\",\"value\":\"600s\"},{\"dependName\":\"网络\",\"key\":\"spark.rpc.askTimeout\",\"keyDescribe\":\"RPC 请求操作在超时之前等待的持续时间\",\"required\":true,\"sort\":2,\"type\":\"INPUT\",\"value\":\"600s\"},{\"dependName\":\"网络\",\"key\":\"spark.executor.heartbeatInterval\",\"keyDescribe\":\"driver和executor之间心跳时间间隔\",\"required\":true,\"sort\":3,\"type\":\"INPUT\",\"value\":\"10s\"}],\"key\":\"网络\",\"required\":false,\"sort\":3,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"事件日志\",\"key\":\"spark.eventLog.compress\",\"keyDescribe\":\"是否对spark事件日志进行压缩\",\"required\":false,\"sort\":1,\"type\":\"INPUT\",\"value\":\"true\"},{\"dependName\":\"事件日志\",\"key\":\"spark.eventLog.dir\",\"keyDescribe\":\"spark事件日志存放路径\",\"required\":false,\"sort\":2,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/tmp/spark-yarn-logs\"},{\"dependName\":\"事件日志\",\"key\":\"spark.eventLog.enabled\",\"keyDescribe\":\"是否记录 spark 事件日志\",\"required\":false,\"sort\":3,\"type\":\"INPUT\",\"value\":\"true\"}],\"key\":\"事件日志\",\"required\":false,\"sort\":4,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"JVM\",\"key\":\"spark.driver.extraJavaOptions\",\"keyDescribe\":\"spark driver的jvm扩展参数\",\"required\":false,\"sort\":1,\"type\":\"INPUT\",\"value\":\"-Dfile.encoding=utf-8\"},{\"dependName\":\"JVM\",\"key\":\"spark.executor.extraJavaOptions\",\"keyDescribe\":\"spark executor的jvm扩展参数\",\"required\":false,\"sort\":2,\"type\":\"INPUT\",\"value\":\"-Dfile.encoding=utf-8\"}],\"key\":\"JVM\",\"required\":false,\"sort\":5,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"环境变量\",\"key\":\"spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON\",\"keyDescribe\":\"driver中用于执行pyspark任务的python二进制可执行文件路径\",\"required\":false,\"sort\":2,\"type\":\"INPUT\",\"value\":\"/data/miniconda3/bin/python3\"},{\"dependName\":\"环境变量\",\"key\":\"spark.yarn.appMasterEnv.PYSPARK_PYTHON\",\"keyDescribe\":\"用于执行pyspark任务的python二进制可执行文件路径\",\"required\":false,\"sort\":3,\"type\":\"INPUT\",\"value\":\"/data/miniconda3/bin/python3\"}],\"key\":\"环境变量\",\"required\":false,\"sort\":6,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"安全\",\"key\":\"spark.yarn.security.credentials.hive.enabled\",\"keyDescribe\":\"开启kerberos场景下是否获取hive 票据\",\"required\":false,\"sort\":8,\"type\":\"INPUT\",\"value\":\"true\"}],\"key\":\"安全\",\"required\":false,\"sort\":8,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[{\"dependName\":\"其它\",\"key\":\"addColumnSupport\",\"required\":false,\"type\":\"INPUT\",\"value\":\"true\"}],\"key\":\"其它\",\"required\":false,\"sort\":9,\"type\":\"TIPS\"},{\"dependencyKey\":\"perjob\",\"groupValues\":[],\"key\":\"自定义参数\",\"required\":false,\"sort\":10,\"type\":\"TIPS\"}]}]}]";
        return JSONArray.parseArray(ct, ClientTemplate.class);
    }
}
