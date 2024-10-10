package com.dtstack.schedule.common.enums;


import com.dtstack.engine.common.constrant.ComponentConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 * 值 1000 以上表示未启用，后续标号
 */
public enum DataSourceType {
    MySQL(1),
    MySQL8(1001),
    MySQLPXC(98),
    Polardb_For_MySQL(28),
    Oracle(2),
    SQLServer(3),
    SQLSERVER_2017_LATER(32),
    PostgreSQL(4),
    DB2(19),
    DMDB(35),
    RDBMS(5),
    KINGBASE8(40),
    HIVE(7),
    HIVE1X(27),
    HIVE3(50),
    MAXCOMPUTE(10),
    GREENPLUM6(36),
    LIBRA(21),
    GBase_8a(22),
    DORIS(57),
    HDFS(6),
    HDFS3(63),
    HDFS3_CDP(1003),
    TBDS_HDFS(60),
    HUAWEI_HDFS(78),
    FTP(9),
    S3(41),
    AWS_S3(51),
    SPARKTHRIFT2_1(45),
    IMPALA(29),
    Clickhouse(25),
    TiDB(31),
    CarbonData(20),
    Kudu(24),
    ADS(15),
    ADB_POSTGREPSQL(54),
    Kylin(23),
    Presto(48),
    OceanBase(49),
    INCEPTOR_SQL(52),
    HBASE(8),
    HBASE2(39),
    Phoenix(30),
    PHOENIX5(38),
    ES(11),
    ES6(33),
    ES7(46),
    MONGODB(13),
    REDIS(12),
    SOLR(53),
    HBASE_GATEWAY(99),
    KAFKA_2X(37),
    KAFKA(26),
    KAFKA_11(14),
    KAFKA_10(17),
    KAFKA_09(18),
    EMQ(34),
    WEB_SOCKET(42),
    SOCKET(44),
    RESTFUL(47),
    VERTICA(43),
    INFLUXDB(55),
    OPENTSDB(56),
    BEATS(16),
    TRINO(59),
    HIVE3X_CDP(65),
    HUAWEI_HIVE3(108),
    STARROCKS(91),
    HASHDATA(104),
    OUSHUDB(113),

    /**
     * spark thrift
     */
    Spark(1002),
    KylinRestful(58),

    /**
     * 因为数据资产新增自定义数据集，将该类型与之做对应
     */
    CUSTOM(1000),
    /**
     * 未知数据源，即类型暂时不确定，后续可能会修改为正确类型的数据源
     */
    UNKNOWN(3000),

    RANGER(89),
    LDAP(90),
    SAP_HANA1(76),
    TBDS_HIVE(94),
    ;

    private int val;

    DataSourceType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }


    public static DataSourceType getSourceType(int value) {
        for (DataSourceType type : DataSourceType.values()) {
            if (type.val == value) {
                return type;
            }
        }

        throw new RdosDefineException("不支持数据源类型");
    }


    public static String getEngineType(DataSourceType sourceType) {

        switch (sourceType) {
            case MySQL:
                return "mysql";
            case HIVE:
            case SPARKTHRIFT2_1:
                return "hive2";
            case TBDS_HIVE:
                return "tbds_hive";
            case HIVE3:
            case HIVE3X_CDP:
            case HUAWEI_HIVE3:
                return "hive3";
            case HIVE1X:
                return "hive";
            case GREENPLUM6:
                return "greenplum";
            case IMPALA:
                return "impala";
            case Oracle:
                return "oracle";
            case PostgreSQL:
                return "postgresql";
            case Presto:
                return "presto";
            case SQLServer:
                return "sqlserver";
            case TiDB:
                return "tidb";
            case KINGBASE8:
                return "kingbase";
            case ADB_POSTGREPSQL:
                return ComponentConstant.ANALYTICDB_FOR_PG_NAME;
            case INCEPTOR_SQL:
                return "inceptor";
            default:
                throw new RdosDefineException("不支持的数据源类型");
        }
    }

    public static DataSourceType convertEComponentType(EComponentType componentType, String version, String versionName) {
        switch (componentType) {
            case TIDB_SQL:
                return TiDB;
            case IMPALA_SQL:
                return IMPALA;
            case ORACLE_SQL:
                return Oracle;
            case HIVE_SERVER:

                if(StringUtils.isBlank(version) || version.equals("2.x")) {
                    return HIVE;
                } else if(version.contains("3Huawei")){
                     return HUAWEI_HIVE3;
                }else if (version.contains("tbds")) {
                    return TBDS_HIVE;
                } else if (version.startsWith("1")) {
                    return HIVE1X;
                } else if (version.contains("cdp")) {
                    return HIVE3X_CDP;
                } else if (version.startsWith("3")) {
                    return HIVE3;
                }
                return HIVE;
            case GREENPLUM_SQL:
                return GREENPLUM6;
            case PRESTO_SQL:
                return Presto;
            case TRINO_SQL:
                return TRINO;
            case LIBRA_SQL:
                return LIBRA;
            case SPARK_THRIFT:
                return SPARKTHRIFT2_1;
            case SFTP:
                return FTP;
            case ANALYTICDB_FOR_PG:
                return ADB_POSTGREPSQL;
            case MYSQL:
                return MySQL;
            case INCEPTOR_SQL:
                return INCEPTOR_SQL;
            case SQL_SERVER:
                return SQLServer;
            case RANGER:
                return RANGER;
            case LDAP:
                return LDAP;
            case STARROCKS:
                return STARROCKS;
            case HDFS:
                // 优先用 versionName 判断，再用 version 判断
                if (StringUtils.containsIgnoreCase(versionName, "HW")) {
                    return HUAWEI_HDFS;
                } else if (StringUtils.containsIgnoreCase(versionName, "TBDS")) {
                    return TBDS_HDFS;
                } else if (StringUtils.containsIgnoreCase(versionName, "CDP")) {
                    return HDFS3_CDP;
                } else if (StringUtils.startsWith(version, "3")) {
                    return HDFS3;
                } else if (StringUtils.startsWith(version, "2")) {
                    return HDFS;
                } else {
                    return HDFS;
                }
            default:
                return null;
        }
    }


    public static List<Integer> hadoopDirtyDataSource = Lists.newArrayList(
            DataSourceType.HIVE1X.getVal(),
            DataSourceType.HIVE.getVal(),
            DataSourceType.HIVE3.getVal(),
            DataSourceType.TBDS_HIVE.getVal(),
            DataSourceType.HIVE3X_CDP.getVal(),
            DataSourceType.SPARKTHRIFT2_1.getVal());


    public static List<Integer> multiPartitionDatasource = Lists.newArrayList(
            DataSourceType.HIVE1X.getVal(),
            DataSourceType.HIVE.getVal(),
            DataSourceType.HIVE3.getVal(),
            DataSourceType.TBDS_HIVE.getVal(),
            DataSourceType.HIVE3X_CDP.getVal(),
            DataSourceType.INCEPTOR_SQL.getVal(),
            DataSourceType.IMPALA.getVal(),
            DataSourceType.IMPALA.getVal(),
            DataSourceType.SPARKTHRIFT2_1.getVal());

    public static List<Integer> needReplaceKerbersDataSource = ImmutableList.of(
            DataSourceType.HIVE1X.getVal(),
            DataSourceType.HIVE.getVal(),
            DataSourceType.HIVE3.getVal(),
            DataSourceType.HUAWEI_HIVE3.getVal(),
            DataSourceType.TBDS_HIVE.getVal(),
            DataSourceType.HIVE3X_CDP.getVal(),
            DataSourceType.INCEPTOR_SQL.getVal(),
            DataSourceType.IMPALA.getVal(),
            DataSourceType.SPARKTHRIFT2_1.getVal());
}
