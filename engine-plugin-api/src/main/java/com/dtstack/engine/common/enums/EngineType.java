package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.constrant.ComponentConstant;

/**
 * Company: www.dtstack.com
 * @author toutian
 */

public enum EngineType {
    Flink,
    Spark,
    Datax,
    Learning,
    DtScript,
    Mysql,
    Oracle,
    Sqlserver,
    Maxcompute,
    Hadoop,
    Hive,
    PostgreSQL,
    Kylin,
    Impala,
    TiDB,
    GreenPlum,
    Dummy,
    Presto,
    KingBase,
    InceptorSQL,
    DtScriptAgent,
    FlinkOnStandalone,
    AnalyticdbForPg,
    tony,
    Trino,
    Hana,
    OCEANBASE,
    DB2,
    Volcano,
    StarRocks,
    HashData,
    OushuDB,
    ;

    public static EngineType getEngineType(String type) {

        switch (type.toLowerCase()) {
            case "flink":
                return EngineType.Flink;
            case "spark":
                return EngineType.Spark;
            case "datax":
                return EngineType.Datax;
            case "learning":
                return EngineType.Learning;
            case "dtscript":
                return EngineType.DtScript;
            case "mysql":
                return EngineType.Mysql;
            case "tidb":
                return EngineType.TiDB;
            case "oracle":
                return EngineType.Oracle;
            case "sqlserver":
                return EngineType.Sqlserver;
            case "maxcompute":
                return EngineType.Maxcompute;
            case "hadoop":
                return EngineType.Hadoop;
            case "hive":
                return EngineType.Hive;
            case "postgresql":
                return EngineType.PostgreSQL;
            case "kylin":
                return EngineType.Kylin;
            case "impala":
                return EngineType.Impala;
            case "greenplum":
                return EngineType.GreenPlum;
            case "dummy":
                return EngineType.Dummy;
            case "presto":
                return EngineType.Presto;
            case "trino":
                return EngineType.Trino;
            case "kingbase":
                return EngineType.KingBase;
            case "inceptor":
                return EngineType.InceptorSQL;
            case "dtscript-agent":
                return EngineType.DtScriptAgent;
            case ComponentConstant
                        .ANALYTICDB_FOR_PG_PLUGIN:
                return EngineType.AnalyticdbForPg;
            case "flink-on-standalone":
                return EngineType.FlinkOnStandalone;
            case "tony":
                return EngineType.tony;
            case "hana":
                return EngineType.Hana;
            case "starrocks":
                return EngineType.StarRocks;
            case "oushudb":
                return EngineType.OushuDB;
            case "hashdata":
                return EngineType.HashData;
            case "oceanbase":
                return EngineType.OCEANBASE;
            case "db2":
                return EngineType.DB2;
            case "volcano":
                return EngineType.Volcano;
            default:
                throw new UnsupportedOperationException("unsupported operation exception");
        }
    }

    public static boolean isFlink(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("flink")) {
            return true;
        }

        return false;
    }

}
