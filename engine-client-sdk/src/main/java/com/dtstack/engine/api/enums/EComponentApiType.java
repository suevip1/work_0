package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 9:15 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Deprecated
public enum EComponentApiType {

    FLINK(0),
    SPARK(1),
    LEARNING(2),
    DT_SCRIPT(3),
    HDFS(4),
    YARN(5),
    SPARK_THRIFT(6),
    CARBON_DATA(7),
    LIBRA_SQL(8),
    HIVE_SERVER(9),
    SFTP(10),
    IMPALA_SQL(11),
    TIDB_SQL(12),
    ORACLE_SQL(13),
    GREENPLUM_SQL(14),
    KUBERNETES(15),
    PRESTO_SQL(16),
    NFS(17),
    DTSCRIPT_AGENT(18),
    INCEPTOR_SQL(19),
    ANALYTICDB_FOR_PG(21),
    MYSQL(22),
    SQL_SERVER(23),
    DB2(24),
    OCEANBASE(25),
    TRINO(26);

    private int typeCode;

    EComponentApiType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
