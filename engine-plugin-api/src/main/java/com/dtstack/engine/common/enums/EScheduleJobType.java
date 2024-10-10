package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.constrant.ComponentConstant;
import com.dtstack.engine.common.exception.RdosDefineException;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum EScheduleJobType {

    VIRTUAL(-1, "虚节点", -1,14),
    SPARK_SQL(0, "Spark SQL", 0,1),
    SPARK(1, "Spark", 1,2),
    SYNC(2, "数据同步", 2,8),
    SPARK_PYTHON(3, "PySpark", 3,3),
    //R 任务--暂时未支持
    R(4, "R", 3,19),
    DEEP_LEARNING(5,"深度学习", 3,11),
    PYTHON(6,"Python", 3,12),
    SHELL(7,"Shell", 3,13),
    ML_LIb(8, "机器学习", 1,10),
    HADOOP_MR(9, "HadoopMR", 1,5),
    WORK_FLOW(10, "工作流", -1,9),
    CARBON_SQL(12, "CarbonSQL", -1,15),
    NOTEBOOK(13, "Notebook", 3,17),
    ALGORITHM_LAB(14, "算法实验", -1,18),
    KYLIN_CUBE(16,"Kylin",4,16),
    LIBRA_SQL(15, "GaussDB SQL", 0,7),
    HIVE_SQL(17,"Hive SQL",0,4),
    IMPALA_SQL(18, "Impala SQL", 0, 6),
    TIDB_SQL(19,"TiDBSQL",0,7),
    ORACLE_SQL(20,"Oracle SQL",0,8),
    GREENPLUM_SQL(21,"greenplum SQL",0,21),
    TENSORFLOW_1_X(22, "TensorFlow 1.x", 3, 5),
    KERAS(23, "Keras", 3, 6),
    PRESTO_SQL(24, "Presto SQL", 0, 30),
    PYTORCH(25, "Pytorch", 3, 40),
    KINGBASE(26,"kingbase",0,41),
    NOT_DO_TASK(27,"空任务",-1,0),
    INCEPTOR_SQL(28,"Inceptor SQL",0,4),
    SHELL_ON_AGENT(29,"Shell on Agent",3,4),
    ANALYTICDB_FOR_PG(30, ComponentConstant.ANALYTICDB_FOR_PG_ENGINE,0,4),
    FLINK_SQL(31,"Flink SQL",0,4),
    MYSQL(32, "MySQL", 0, 7),
    SQL_SERVER(33, "SQL Server", 0, 8),
    DB2(34, "DB2", 0, 9),
    OCEANBASE(35, "OceanBase", 0, 10),
    TRINO(36, "Trino SQL", 0, 42),
    //实时采集
    DATA_COLLECTION(37, "DATA_COLLECTION", 2, 43),
    TENSORFLOW_2_X(38,"TensorFlow 2.x",3,43),
    FLINK(39, "Flink", 1, 44),
    GROUP(40, "Group", 0, 45),
    EVENT(41, "事件任务", 0, 46),
    CONDITION_BRANCH(42, "条件分支", -1, 47),
    HANA(43, "Hana", 0, 22),
    STARROCKS(44, "StarRocks", 0, 23),
    HASHDATA(45, "HashData", 0, 24),
    OUSHUDB(46, "OushuDB", 0, 25),
    FILE_COPY(99, "FileCopy", 3, 99),

    PYTHON_ON_AGENT(100,"Python on Agent", 3, 100);


    private Integer type;

    private String name;

    /**
     * 引擎能够接受的jobType
     * SQL              0
     * MR               1
     * SYNC             2
     * PYTHON           3
     * 不接受的任务类型    -1
     */
    private Integer engineJobType;

    private Integer sort;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEngineJobType(Integer engineJobType) {
        this.engineJobType = engineJobType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    EScheduleJobType(Integer type, String name, Integer engineJobType, Integer sort){
        this.type = type;
        this.name = name;
        this.engineJobType = engineJobType;
        this.sort = sort;
    }

    public Integer getVal(){
        return this.type;
    }

    public String getName() {
        return name;
    }

    public Integer getEngineJobType() {
        return engineJobType;
    }

    public static EScheduleJobType getEJobType(int type){
        EScheduleJobType[] eJobTypes = EScheduleJobType.values();
        for(EScheduleJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                return eJobType;
            }
        }
        return null;
    }


    public static Integer getEngineJobType(int type){
        EScheduleJobType[] eJobTypes = EScheduleJobType.values();
        for(EScheduleJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                if (eJobType.getVal() != -1){
                    return eJobType.getEngineJobType();
                }
                break;
            }

        }
        throw new RdosDefineException("不支持的任务类型");
    }
}