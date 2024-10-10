package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.constrant.ComponentConstant;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiangbo
 * @date 2019/5/30
 * @see com.dtstack.dtcenter.common.enums.EComponentType
 */
public enum EComponentType {

    FLINK(0, "Flink", "flinkConf"),
    SPARK(1, "Spark", "sparkConf"),
    LEARNING(2, "Learning", "learningConf"),
    DT_SCRIPT(3, "DtScript", "dtScriptConf"),
    HDFS(4, "HDFS", "hadoopConf"),
    YARN(5, "YARN", "yarnConf"),
    SPARK_THRIFT(6, "SparkThrift", "hiveConf"),
    CARBON_DATA(7, "CarbonData ThriftServer", "carbonConf"),
    LIBRA_SQL(8, "GaussDB", "libraConf"),
    HIVE_SERVER(9, "HiveServer", "hiveServerConf"),
    SFTP(10, "SFTP", "sftpConf"),
    IMPALA_SQL(11, "Impala SQL", "impalaSqlConf"),
    TIDB_SQL(12, "TiDB SQL", "tidbConf"),
    ORACLE_SQL(13, "Oracle SQL", "oracleConf"),
    GREENPLUM_SQL(14, "Greenplum SQL", "greenplumConf"),
    KUBERNETES(15, "Kubernetes", "kubernetesConf"),
    PRESTO_SQL(16, "Presto SQL", "prestoConf"),
    NFS(17, "NFS", "nfsConf"),
    DTSCRIPT_AGENT(18,"DtScript Agent","dtScriptAgentConf"),
    INCEPTOR_SQL(19,"InceptorSql","inceptorSqlConf"),
//    FLINK_ON_STANDALONE(20,"FlinkOnStandalone","flinkOnStandaloneConf"),
    ANALYTICDB_FOR_PG(21, ComponentConstant.ANALYTICDB_FOR_PG_NAME,ComponentConstant.ANALYTICDB_FOR_PG_CONFIG_NAME),
    MYSQL(22, "Mysql", "mysqlConf"),
    SQL_SERVER(23, "SqlServer", "sqlServerConf"),
    DB2(24, "DB2", "db2Conf"),
    OCEANBASE(25, "OceanBase", "oceanBaseConf"),
    TRINO_SQL(26, "Trino", "trinoConf"),
    TONY(27,"Tony","tonyConf"),
    S3(28, "S3", "s3Conf"),
    VOLCANO(29, "Volcano", "volcanoConf"),
    RANGER(31, "Ranger", "rangerConf"),
    LDAP(32, "Ldap", "ldapConf"),
    HANA(33, "Hana", "hanaConf"),
    COMMON(34, "Common", "commonConf"),
    STARROCKS(35, "StarRocks", "starRocksConf"),
    HASHDATA(36, "HashData", "hashDataConf"),
    OUSHUDB(37, "OushuDB", "oushuDBConf"),
    ;

    private Integer typeCode;

    private String name;

    private String confName;

    EComponentType(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    private static final Map<Integer,EComponentType> COMPONENT_TYPE_CODE_MAP=new ConcurrentHashMap<>(48);
    private static final Map<String ,EComponentType> COMPONENT_TYPE_NAME_MAP=new ConcurrentHashMap<>(48);
    private static final Map<String ,EComponentType> COMPONENT_TYPE_CONF_NAME_MAP=new ConcurrentHashMap<>(48);
    static {
        for (EComponentType componentType : EComponentType.values()) {
            COMPONENT_TYPE_CODE_MAP.put(componentType.getTypeCode(),componentType);
            COMPONENT_TYPE_NAME_MAP.put(componentType.getName(),componentType);
            COMPONENT_TYPE_CONF_NAME_MAP.put(componentType.getConfName(),componentType);
        }
    }

    public static EComponentType getByCode(int code) {
        EComponentType componentType = COMPONENT_TYPE_CODE_MAP.get(code);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static EComponentType getByName(String name) {
        EComponentType componentType = COMPONENT_TYPE_NAME_MAP.get(name);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static EComponentType getByConfName(String confName) {
        EComponentType componentType = COMPONENT_TYPE_CONF_NAME_MAP.get(confName);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with conf name:" + confName);
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getConfName() {
        return confName;
    }


    // 资源调度组件
    public static List<EComponentType> ResourceScheduling = ImmutableList.of(EComponentType.YARN, EComponentType.KUBERNETES);

    // 存储组件
    public static List<EComponentType> StorageScheduling = ImmutableList.of(EComponentType.HDFS, EComponentType.NFS, EComponentType.S3);

    // 计算组件
    public static List<EComponentType> ComputeScheduling = ImmutableList.of(
            EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER,
            EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.TIDB_SQL,
            EComponentType.PRESTO_SQL, EComponentType.LIBRA_SQL,
            EComponentType.ORACLE_SQL, EComponentType.CARBON_DATA,
            EComponentType.GREENPLUM_SQL,EComponentType.INCEPTOR_SQL,
            EComponentType.DTSCRIPT_AGENT,EComponentType.ANALYTICDB_FOR_PG,
            EComponentType.MYSQL,EComponentType.SQL_SERVER,
            EComponentType.DB2,EComponentType.OCEANBASE,
            EComponentType.TRINO_SQL,
            EComponentType.TONY,
            EComponentType.VOLCANO,
            EComponentType.HANA,
            EComponentType.HASHDATA,
            EComponentType.STARROCKS,
            EComponentType.OUSHUDB
    );

    // 需要添加TypeName的组件
    public static List<EComponentType> typeComponentVersion = ImmutableList.of(EComponentType.DT_SCRIPT, EComponentType.FLINK, EComponentType.LEARNING, EComponentType.SPARK,
            EComponentType.HDFS,EComponentType.TONY, EComponentType.S3, EComponentType.VOLCANO);
    public static List<EComponentType> CommonScheduling = ImmutableList.of(EComponentType.SFTP, EComponentType.RANGER, EComponentType.LDAP, EComponentType.COMMON);

    public static EComponentScheduleType getScheduleTypeByComponent(Integer componentCode) {
        EComponentType code = getByCode(componentCode);
        if (ComputeScheduling.contains(code)) {
            return EComponentScheduleType.COMPUTE;
        }
        if (ResourceScheduling.contains(code)) {
            return EComponentScheduleType.RESOURCE;
        }
        if (StorageScheduling.contains(code)) {
            return EComponentScheduleType.STORAGE;
        }
        if (CommonScheduling.contains(code)) {
            return EComponentScheduleType.COMMON;
        }
        throw new RdosDefineException("不支持的组件");
    }


    private static List<EComponentType> EmptyComponents = ImmutableList.of(EComponentType.DTSCRIPT_AGENT);

    /**
     * 直接定位的插件的组件类型
     *
     * @param componentCode
     * @return
     */
    public static String convertPluginNameByComponent(EComponentType componentCode) {
        switch (componentCode) {
            case TIDB_SQL:
                return "tidb";
            case ORACLE_SQL:
                return "oracle";
            case SFTP:
                return "dummy";
            case LIBRA_SQL:
                return "postgresql";
            case IMPALA_SQL:
                return "impala";
            case GREENPLUM_SQL:
                return "greenplum";
            case PRESTO_SQL:
                return "presto";
            case TRINO_SQL:
                return "trino";
            case KUBERNETES:
                return "kubernetes";
            case NFS:
                return "nfs";
            case INCEPTOR_SQL:
                return "inceptor";
            case DTSCRIPT_AGENT:
                return "dtscript-agent";
            case ANALYTICDB_FOR_PG:
                return ComponentConstant.ANALYTICDB_FOR_PG_PLUGIN;
            case SQL_SERVER:
                return "sqlserver";
            case DB2:
                return "db2";
            case OCEANBASE:
                return "oceanbase";
        }
        return "";
    }

    /**
     * 不需要测试连通性的组件
     */
    public static final List<EComponentType> NOT_CHECK_COMPONENT = ImmutableList.of(
            EComponentType.LEARNING, EComponentType.TONY,
            EComponentType.VOLCANO, EComponentType.COMMON
    );

    /**
     * 需要走 engine-plugins 测试连通性的组件
     */
    public static final List<EComponentType> ENGINE_PLUGIN_CHECK_COMPONENT = ImmutableList.of(
            EComponentType.SPARK, EComponentType.FLINK,
            EComponentType.DT_SCRIPT, EComponentType.DTSCRIPT_AGENT
    );

    //允许一个组件 on yarn 或 其他多种模式
    public static List<EComponentType> deployTypeComponents = ImmutableList.of(EComponentType.FLINK);

    //没有控件渲染的组件
    public static List<EComponentType> noControlComponents = ImmutableList.of(EComponentType.YARN, EComponentType.KUBERNETES,EComponentType.HDFS);

    //多hadoop版本选择组件
    public static List<EComponentType> hadoopVersionComponents = ImmutableList.of(EComponentType.YARN,EComponentType.HDFS);

    //metadata组件
    public static List<EComponentType> metadataComponents = ImmutableList.of(EComponentType.HIVE_SERVER,EComponentType.SPARK_THRIFT);

    //hive.proxy.enable组件
    public static List<EComponentType> hiveProxyUserComponents = ImmutableList.of(EComponentType.HIVE_SERVER,EComponentType.INCEPTOR_SQL);


    public static MultiEngineType getEngineTypeByComponent(EComponentType componentType,Integer deployType) {
        //todo 历史遗留问题 k8s归宿hadoop
        if (EComponentType.YARN.equals(componentType) || EComponentType.KUBERNETES.equals(componentType)) {
            return MultiEngineType.HADOOP;
        }
        if (EComponentType.LIBRA_SQL.equals(componentType)) {
            return MultiEngineType.LIBRA;
        }
        if (EComponentType.ORACLE_SQL.equals(componentType)) {
            return MultiEngineType.ORACLE;
        }
        if (EComponentType.TIDB_SQL.equals(componentType)) {
            return MultiEngineType.TIDB;
        }
        if (EComponentType.PRESTO_SQL.equals(componentType)) {
            return MultiEngineType.PRESTO;
        }
        if (EmptyComponents.contains(componentType)){
            return MultiEngineType.COMMON;
        }
        if (EComponentType.ANALYTICDB_FOR_PG.equals(componentType)){
            return MultiEngineType.ANALYTICDB_FOR_PG;
        }
        if (EComponentType.MYSQL == componentType) {
            return MultiEngineType.MYSQL;
        }
        if (EComponentType.HANA == componentType) {
            return MultiEngineType.HANA;
        }
        if (EComponentType.SQL_SERVER == componentType) {
            return MultiEngineType.SQL_SERVER;
        }
        if (EComponentType.DB2 == componentType) {
            return MultiEngineType.DB2;
        }
        if (EComponentType.OCEANBASE == componentType) {
            return MultiEngineType.OCEANBASE;
        }
        if (EComponentType.TRINO_SQL == componentType) {
            return MultiEngineType.TRINO;
        }
        if (EComponentType.GREENPLUM_SQL == componentType) {
            return MultiEngineType.GREENPLUM;
        }
        if (EComponentType.STARROCKS == componentType) {
            return MultiEngineType.STARROCKS;
        }
        if (EComponentType.HASHDATA == componentType) {
            return MultiEngineType.HASHDATA;
        }
        if (EComponentType.OUSHUDB == componentType) {
            return MultiEngineType.OUSHUDB;
        }
        if (EComponentType.TRINO_SQL == componentType) {
            return MultiEngineType.TRINO;
        }
        if (EComponentType.INCEPTOR_SQL == componentType) {
            return MultiEngineType.INCEPTOR;
        }
        if (EComponentType.FLINK == componentType && EDeployType.STANDALONE.type.equals(deployType)) {
            return MultiEngineType.FLINK_ON_STANDALONE;
        }
        return null;
    }


}

