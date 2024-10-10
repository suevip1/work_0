package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.constrant.ComponentConstant;
import com.google.common.collect.ImmutableSet;

/**
 *
 * 引擎类型
 * Date: 2019/5/28
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum MultiEngineType {
    COMMON(-1,"Common"),
    HADOOP(1,"Hadoop"),
    LIBRA(2, "GaussDB"),
    KYLIN(3, "Kylin"),
    TIDB(4,"TiDB"),
    ORACLE(5,"Oracle"),
    GREENPLUM(6, "Greenplum"),
    PRESTO(7, "Presto"),
    FLINK_ON_STANDALONE(8,"FlinkOnStandalone"),
    ANALYTICDB_FOR_PG(9, ComponentConstant.ANALYTICDB_FOR_PG_ENGINE),
    MYSQL(10, "Mysql"),
    SQL_SERVER(11, "SqlServer"),
    DB2(12, "DB2"),
    OCEANBASE(13, "OceanBase"),
    TRINO(14, "Trino"),
    HANA(15, "Hana"),
    STARROCKS(16, "StarRocks"),
    HASHDATA(17, "HashData"),
    OUSHUDB(18, "OushuDB"),
    INCEPTOR(19, "Inceptor"),
    DTSCRIPT_AGENT(20, "DTSCRIPT_AGENT"),
    ;

    private static final ImmutableSet<MultiEngineType> CAN_BIND_ACCOUNT_ENGINE_TYPE = ImmutableSet.of(TIDB, ORACLE, GREENPLUM, ANALYTICDB_FOR_PG, OUSHUDB,
            TRINO, LIBRA, MYSQL, SQL_SERVER, HANA, STARROCKS, HASHDATA, INCEPTOR);

    private int type;

    private String name;

    public String getName() {
        return name;
    }

    MultiEngineType(int type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType(){
        return this.type;
    }

    public static MultiEngineType getByName(String name){
        for (MultiEngineType value : MultiEngineType.values()) {
            if(value.getName().equalsIgnoreCase(name)){
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + name);
    }

    public static MultiEngineType getByType(int type){
        for (MultiEngineType value : MultiEngineType.values()) {
            if(value.getType() == type){
                return value;
            }
        }
        throw new IllegalArgumentException("No enum constant with type code:" + type);
    }

    public static boolean canBindAccount(MultiEngineType type) {
        return CAN_BIND_ACCOUNT_ENGINE_TYPE.contains(type);
    }

    public static EComponentType engineToComponentType(MultiEngineType engineType) {
        if (engineType == null) {
            return null;
        }
        switch (engineType) {
            case TIDB:
                return EComponentType.TIDB_SQL;
            case ORACLE:
                return EComponentType.ORACLE_SQL;
            case GREENPLUM:
                return EComponentType.GREENPLUM_SQL;
            case ANALYTICDB_FOR_PG:
                return EComponentType.ANALYTICDB_FOR_PG;
            case OUSHUDB:
                return EComponentType.OUSHUDB;
            case TRINO:
                return EComponentType.TRINO_SQL;
            case LIBRA:
                return EComponentType.LIBRA_SQL;
            case MYSQL:
                return EComponentType.MYSQL;
            case SQL_SERVER:
                return EComponentType.SQL_SERVER;
            case HANA:
                return EComponentType.HANA;
            case STARROCKS:
                return EComponentType.STARROCKS;
            case HASHDATA:
                return EComponentType.HASHDATA;
            case INCEPTOR:
                return EComponentType.INCEPTOR_SQL;
            case PRESTO:
                return EComponentType.PRESTO_SQL;
        }
        return null;
    }
}
