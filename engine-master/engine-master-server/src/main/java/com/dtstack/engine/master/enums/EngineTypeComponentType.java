/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.enums;


import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.constrant.ComponentConstant;
import com.dtstack.engine.common.enums.EComponentType;

import java.util.Objects;

public enum EngineTypeComponentType {

    FLINK(ScheduleEngineType.Flink, EComponentType.FLINK),
    SPARK(ScheduleEngineType.Spark, EComponentType.SPARK),
    LEARNING(ScheduleEngineType.Learning, EComponentType.LEARNING),
    DT_SCRIPT(ScheduleEngineType.DtScript, EComponentType.DT_SCRIPT),
    HDFS(ScheduleEngineType.Hadoop, EComponentType.HDFS),
    YARN(ScheduleEngineType.Hadoop, EComponentType.YARN),
    CARBON_DATA(ScheduleEngineType.Carbon, EComponentType.CARBON_DATA),
    LIBRA_SQL(ScheduleEngineType.Libra, EComponentType.LIBRA_SQL),
    HIVE(ScheduleEngineType.HIVE,EComponentType.HIVE_SERVER),
    IMPALA_SQL(ScheduleEngineType.Hadoop, EComponentType.IMPALA_SQL),
    TIDB_SQL(ScheduleEngineType.TIDB, EComponentType.TIDB_SQL),
    ORACLE_SQL(ScheduleEngineType.ORACLE, EComponentType.ORACLE_SQL),
    KUBERNETES(ScheduleEngineType.KUBERNETES, EComponentType.KUBERNETES),
    GREENPLUM_SQL(ScheduleEngineType.GREENPLUM, EComponentType.GREENPLUM_SQL),
    PRESTO_SQL(ScheduleEngineType.Presto, EComponentType.PRESTO_SQL),
    INCEPTOR_SQL(ScheduleEngineType.INCEPTOR_SQL,EComponentType.INCEPTOR_SQL),
    DTSCRIPT_AGENT(ScheduleEngineType.DTSCRIPT_AGENT,EComponentType.DTSCRIPT_AGENT),
    ANALYTICDB_FOR_PG(ScheduleEngineType.ANALYTICDB_FOR_PG,EComponentType.ANALYTICDB_FOR_PG),
    SQL_SERVER(ScheduleEngineType.SQLSERVER, EComponentType.SQL_SERVER),
    MYSQL(ScheduleEngineType.MYSQL, EComponentType.MYSQL),
    DB2(ScheduleEngineType.DB2, EComponentType.DB2),
    OCEANBASE(ScheduleEngineType.OCEANBASE, EComponentType.OCEANBASE),
    TRINO_SQL(ScheduleEngineType.TRINO, EComponentType.TRINO_SQL),
    TONY(ScheduleEngineType.TONY,EComponentType.TONY),
    S3(ScheduleEngineType.S3,EComponentType.S3),
    VOLCANO(ScheduleEngineType.VOLCANO, EComponentType.VOLCANO),
    RANGER(ScheduleEngineType.RANGER, EComponentType.RANGER),
    LDAP(ScheduleEngineType.LDAP, EComponentType.LDAP),
    HANA(ScheduleEngineType.HANA, EComponentType.HANA),
    STARROCKS(ScheduleEngineType.STARROCKS, EComponentType.STARROCKS),
    HASHDATA(ScheduleEngineType.HASHDATA, EComponentType.HASHDATA),
    OUSHUDB(ScheduleEngineType.OUSHUDB, EComponentType.OUSHUDB),
    ;

    private ScheduleEngineType scheduleEngineType;

    private EComponentType componentType;

    EngineTypeComponentType(ScheduleEngineType scheduleEngineType, EComponentType componentType) {
        this.scheduleEngineType = scheduleEngineType;
        this.componentType = componentType;
    }

    public ScheduleEngineType getScheduleEngineType() {
        return scheduleEngineType;
    }

    public EComponentType getComponentType() {
        return componentType;
    }



    public static EngineTypeComponentType getByEngineName(String engineName){
        switch (engineName.toLowerCase()) {

            case "flink":
                return EngineTypeComponentType.FLINK;

            case "spark":
                return EngineTypeComponentType.SPARK;

            case "learning":
                return EngineTypeComponentType.LEARNING;

            case "dtscript":
                return EngineTypeComponentType.DT_SCRIPT;
            case "hadoop":
            case "hdfs2" :
            case "hdfs3" :
            case "hdfs2tbds" :
            case "hdfs3hw" :
                return EngineTypeComponentType.HDFS;
            case "yarn2" :
            case "yarn3":
            case "yarn2tbds":
            case "yarn3hw":
                return EngineTypeComponentType.YARN;
            case "carbon":
                return EngineTypeComponentType.CARBON_DATA;

            case "librasql":
            case "postgresql":
                return EngineTypeComponentType.LIBRA_SQL;
            case "hive":
            case "hive2":
            case "hive3":
                return EngineTypeComponentType.HIVE;
            case "mysql":
                return EngineTypeComponentType.MYSQL;
            case "sqlserver":
                return EngineTypeComponentType.SQL_SERVER;
            case "db2":
                return EngineTypeComponentType.DB2;
            case "oceanbase":
                return EngineTypeComponentType.OCEANBASE;
            case "maxcompute":
            case "kylin":
            case "kingbase":
            case "dummy":
                return null;
            case "impala":
                return EngineTypeComponentType.IMPALA_SQL;
            case "tidb":
                return EngineTypeComponentType.TIDB_SQL;
            case "oracle":
                return EngineTypeComponentType.ORACLE_SQL;
            case "kubernetes":
                return EngineTypeComponentType.KUBERNETES;
            case "greenplum":
                return EngineTypeComponentType.GREENPLUM_SQL;
            case "presto":
                return EngineTypeComponentType.PRESTO_SQL;
            case "inceptor":
                return EngineTypeComponentType.INCEPTOR_SQL;
            case "dtscript-agent":
                return EngineTypeComponentType.DTSCRIPT_AGENT;
            case ComponentConstant.ANALYTICDB_FOR_PG_PLUGIN:
                return EngineTypeComponentType.ANALYTICDB_FOR_PG;
            case "trino":
                return EngineTypeComponentType.TRINO_SQL;
            case "tony":
                return EngineTypeComponentType.TONY;
            case "s3" :
                return EngineTypeComponentType.S3;
            case "volcano":
                return EngineTypeComponentType.VOLCANO;
            case "ranger":
                return EngineTypeComponentType.RANGER;
            case "ldap":
                return EngineTypeComponentType.LDAP;
            case "hana":
                return EngineTypeComponentType.HANA;
            case "starrocks":
                return EngineTypeComponentType.STARROCKS;
            case "hashdata":
                return EngineTypeComponentType.HASHDATA;
            case "oushudb":
                return EngineTypeComponentType.OUSHUDB;
            default:
                throw new UnsupportedOperationException("未知引擎类型:" + engineName);
        }
    }

    public static EComponentType getComponentByEngineName(String engineName){
        EngineTypeComponentType engineTypeComponentType = getByEngineName(engineName);
        if (Objects.nonNull(engineTypeComponentType)){
            return engineTypeComponentType.componentType;
        }
        return null;
    }

    public static Integer engineName2ComponentType(String engineName){
        EngineTypeComponentType engineTypeComponentType = getByEngineName(engineName);
        if (Objects.nonNull(engineTypeComponentType)){
            return engineTypeComponentType.componentType.getTypeCode();
        }
        return null;
    }
}

